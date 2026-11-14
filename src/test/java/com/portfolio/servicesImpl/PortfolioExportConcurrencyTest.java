package com.portfolio.servicesImpl;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.ProfileMasterService;
import org.junit.jupiter.api.Test;
import org.thymeleaf.TemplateEngine;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.Semaphore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Proves the fix for the production OOM: PortfolioExportServiceImpl must refuse to start a second,
 * concurrent PDF render (the heaviest allocation in this app on a 320MB-heap instance) rather than
 * letting requests stack up and exhaust the heap. Simulates "a render is already in flight" by
 * grabbing the service's own export permit directly, since the real contention (two real renders
 * racing) isn't reliably reproducible in a unit test.
 */
class PortfolioExportConcurrencyTest {

    @Test
    void rejectsExportAsBusyWhenAnotherRenderHoldsTheOnlyPermit() throws Exception {
        ProfileMasterService profileMasterService = mock(ProfileMasterService.class);
        ProfileDao profileDao = mock(ProfileDao.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);

        when(profileDao.findByUserName("janedoe"))
                .thenReturn(Optional.of(Profile.builder().id(1L).userName("janedoe").build()));
        // Never reached if the busy check fires before rendering, as asserted below.
        when(profileMasterService.getForResumeExport(1L)).thenReturn(null);

        PortfolioExportServiceImpl service = new PortfolioExportServiceImpl(
                profileMasterService, profileDao, templateEngine);

        Semaphore permit = exportSemaphore();
        assertThat(permit.tryAcquire()).isTrue(); // hold the only permit, simulating an in-flight render
        try {
            assertThatThrownByServiceBusy(() -> service.exportPdf("janedoe"));
        } finally {
            permit.release();
        }
    }

    private void assertThatThrownByServiceBusy(ThrowingCall call) {
        try {
            call.call();
        } catch (GenericException e) {
            assertThat(e.getExceptionCode()).isEqualTo(ExceptionCodeEnum.SERVICE_BUSY);
            return;
        } catch (Exception e) {
            throw new AssertionError("Expected GenericException(SERVICE_BUSY), got " + e, e);
        }
        throw new AssertionError("Expected GenericException(SERVICE_BUSY) but nothing was thrown");
    }

    @SuppressWarnings("unchecked")
    private Semaphore exportSemaphore() throws Exception {
        Field field = PortfolioExportServiceImpl.class.getDeclaredField("EXPORT_SEMAPHORE");
        field.setAccessible(true);
        return (Semaphore) field.get(null);
    }

    @FunctionalInterface
    private interface ThrowingCall {
        void call() throws Exception;
    }
}
