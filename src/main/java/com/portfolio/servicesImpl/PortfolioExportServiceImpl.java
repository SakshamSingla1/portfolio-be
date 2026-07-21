package com.portfolio.servicesImpl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.ProfileMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioExportServiceImpl implements PortfolioExportService {

    private final ProfileMasterService profileMasterService;
    private final ProfileDao profileDao;

    @Override
    public byte[] exportPdf(String username) throws GenericException {
        Profile profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + username));

        ProfileMasterResponse data = profileMasterService.getByProfileId(profile.getId());

        try {
            String html = buildHtml(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(baos);
            builder.run();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("PDF generation failed for username={}", username, e);
            throw new GenericException(ExceptionCodeEnum.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }

    private String buildHtml(ProfileMasterResponse data) {
        ProfileResponse profile = data.getProfile();
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        sb.append("<head>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
        sb.append("<title>").append(esc(s(profile.getFullName()))).append(" — Portfolio</title>\n");
        sb.append("<style type=\"text/css\">\n");
        sb.append("@page { size: A4; margin: 20mm 15mm 20mm 15mm; }\n");
        sb.append("body { font-family: Arial, Helvetica, sans-serif; font-size: 10pt; color: #1a1a1a; background: #ffffff; margin: 0; padding: 0; }\n");
        sb.append(".name { font-size: 14pt; font-weight: bold; color: #1e3a5f; }\n");
        sb.append(".role-title { font-size: 11pt; color: #555555; margin-top: 2px; }\n");
        sb.append(".contact-line { font-size: 9pt; color: #555555; margin-top: 5px; }\n");
        sb.append(".section-heading { font-size: 11pt; font-weight: bold; color: #1e3a5f; border-bottom: 1px solid #1e3a5f; margin-top: 14px; margin-bottom: 8px; padding-bottom: 3px; }\n");
        sb.append(".item { margin-bottom: 8px; }\n");
        sb.append(".item-title { font-weight: bold; font-size: 10pt; color: #1a1a1a; }\n");
        sb.append(".item-subtitle { font-size: 9pt; color: #555555; }\n");
        sb.append(".item-desc { font-size: 9pt; margin-top: 2px; color: #333333; }\n");
        sb.append(".item-desc p { margin: 0 0 4px 0; }\n");
        sb.append(".item-desc ul, .item-desc ol { margin: 2px 0 2px 16px; padding: 0; }\n");
        sb.append(".item-desc li { margin-bottom: 2px; }\n");
        sb.append(".skill-cat { font-weight: bold; font-size: 9pt; color: #1e3a5f; }\n");
        sb.append(".skill-list { font-size: 9pt; color: #333333; }\n");
        sb.append(".link { color: #1e3a5f; font-size: 9pt; }\n");
        sb.append("</style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        // ── Header ──────────────────────────────────────────────────────────
        sb.append("<div>\n");
        sb.append("<span class=\"name\">").append(esc(s(profile.getFullName()))).append("</span><br/>\n");
        if (notBlank(profile.getTitle())) {
            sb.append("<span class=\"role-title\">").append(esc(profile.getTitle())).append("</span><br/>\n");
        }
        StringBuilder contact = new StringBuilder();
        if (notBlank(profile.getLocation())) contact.append(esc(profile.getLocation()));
        if (notBlank(profile.getEmail())) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append(esc(profile.getEmail()));
        }
        if (notBlank(profile.getPhone())) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append(esc(profile.getPhone()));
        }
        if (contact.length() > 0) {
            sb.append("<div class=\"contact-line\">").append(contact).append("</div>\n");
        }
        sb.append("</div>\n");

        // ── About ────────────────────────────────────────────────────────────
        if (notBlank(profile.getAboutMe())) {
            sb.append("<div class=\"section-heading\">About Me</div>\n");
            sb.append("<div class=\"item-desc\">").append(richText(profile.getAboutMe())).append("</div>\n");
        }

        // ── Experience ───────────────────────────────────────────────────────
        List<ExperienceResponse> experiences = data.getExperiences();
        if (nonEmpty(experiences)) {
            sb.append("<div class=\"section-heading\">Experience</div>\n");
            for (ExperienceResponse exp : experiences) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(exp.getJobTitle()))).append(" — ").append(esc(s(exp.getCompanyName()))).append("</div>\n");
                String dates = s(exp.getStartDate()) + (notBlank(exp.getEndDate()) ? " – " + exp.getEndDate() : " – Present");
                sb.append("<div class=\"item-subtitle\">").append(esc(dates));
                if (notBlank(exp.getLocation())) sb.append(" | ").append(esc(exp.getLocation()));
                sb.append("</div>\n");
                if (notBlank(exp.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(exp.getDescription())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Education ────────────────────────────────────────────────────────
        List<EducationResponse> educations = data.getEducations();
        if (nonEmpty(educations)) {
            sb.append("<div class=\"section-heading\">Education</div>\n");
            for (EducationResponse edu : educations) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(edu.getInstitution()))).append("</div>\n");
                String degreeField = (edu.getDegree() != null ? edu.getDegree().name() : "") +
                        (notBlank(edu.getFieldOfStudy()) ? " in " + edu.getFieldOfStudy() : "");
                String yearRange = edu.getStartYear() != null
                        ? String.valueOf(edu.getStartYear()) + (edu.getEndYear() != null ? " – " + edu.getEndYear() : "")
                        : "";
                String eduSub = degreeField + (!yearRange.isEmpty() ? " | " + yearRange : "");
                if (notBlank(eduSub)) {
                    sb.append("<div class=\"item-subtitle\">").append(esc(eduSub)).append("</div>\n");
                }
                if (notBlank(edu.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(edu.getDescription())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Skills ───────────────────────────────────────────────────────────
        List<SkillResponse> skills = data.getSkills();
        if (nonEmpty(skills)) {
            sb.append("<div class=\"section-heading\">Skills</div>\n");
            Map<SkillCategoryEnum, List<SkillResponse>> grouped = skills.stream()
                    .collect(Collectors.groupingBy(
                            sk -> sk.getCategory() != null ? sk.getCategory() : SkillCategoryEnum.OTHER,
                            LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<SkillCategoryEnum, List<SkillResponse>> entry : grouped.entrySet()) {
                String catLabel = entry.getKey().name().replace('_', ' ');
                String skillNames = entry.getValue().stream()
                        .map(sk -> s(sk.getLogoName()))
                        .filter(n -> !n.isEmpty())
                        .collect(Collectors.joining(", "));
                if (!skillNames.isEmpty()) {
                    sb.append("<div class=\"item\">\n");
                    sb.append("<span class=\"skill-cat\">").append(esc(catLabel)).append(": </span>");
                    sb.append("<span class=\"skill-list\">").append(esc(skillNames)).append("</span>\n");
                    sb.append("</div>\n");
                }
            }
        }

        // ── Projects ─────────────────────────────────────────────────────────
        List<ProjectResponse> projects = data.getProjects();
        if (nonEmpty(projects)) {
            sb.append("<div class=\"section-heading\">Projects</div>\n");
            for (ProjectResponse proj : projects) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(proj.getProjectName()))).append("</div>\n");
                if (notBlank(proj.getProjectDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(proj.getProjectDescription())).append("</div>\n");
                }
                if (proj.getSkills() != null && !proj.getSkills().isEmpty()) {
                    String techStack = proj.getSkills().stream()
                            .map(sk -> s(sk.getLogoName()))
                            .filter(n -> !n.isEmpty())
                            .collect(Collectors.joining(", "));
                    if (!techStack.isEmpty()) {
                        sb.append("<div class=\"item-subtitle\">Tech: ").append(esc(techStack)).append("</div>\n");
                    }
                }
                if (notBlank(proj.getProjectLink())) {
                    sb.append("<div class=\"item-subtitle\">URL: <span class=\"link\">").append(esc(proj.getProjectLink())).append("</span></div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Certifications ───────────────────────────────────────────────────
        List<CertificationResponseDTO> certs = data.getCertifications();
        if (nonEmpty(certs)) {
            sb.append("<div class=\"section-heading\">Certifications</div>\n");
            for (CertificationResponseDTO cert : certs) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(cert.getTitle()))).append("</div>\n");
                String certSub = s(cert.getIssuer()) +
                        (cert.getIssueDate() != null ? " | " + cert.getIssueDate() : "");
                if (notBlank(certSub)) {
                    sb.append("<div class=\"item-subtitle\">").append(esc(certSub)).append("</div>\n");
                }
                if (notBlank(cert.getCredentialUrl())) {
                    sb.append("<div class=\"item-subtitle\">URL: <span class=\"link\">").append(esc(cert.getCredentialUrl())).append("</span></div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Publications ─────────────────────────────────────────────────────
        List<PublicationResponseDTO> publications = data.getPublications();
        if (nonEmpty(publications)) {
            sb.append("<div class=\"section-heading\">Publications</div>\n");
            for (PublicationResponseDTO pub : publications) {
                sb.append("<div class=\"item\">\n");
                String typeLabel = notBlank(pub.getType()) ? " (" + pub.getType() + ")" : "";
                sb.append("<div class=\"item-title\">").append(esc(s(pub.getTitle()) + typeLabel)).append("</div>\n");
                String pubSub = s(pub.getPublisher()) +
                        (pub.getPublishedDate() != null ? " | " + pub.getPublishedDate() : "");
                if (notBlank(pubSub)) {
                    sb.append("<div class=\"item-subtitle\">").append(esc(pubSub)).append("</div>\n");
                }
                if (notBlank(pub.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(pub.getDescription())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Achievements ─────────────────────────────────────────────────────
        List<AchievementResponseDTO> achievements = data.getAchievements();
        if (nonEmpty(achievements)) {
            sb.append("<div class=\"section-heading\">Achievements</div>\n");
            for (AchievementResponseDTO ach : achievements) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(ach.getTitle()))).append("</div>\n");
                if (ach.getAchievedAt() != null) {
                    sb.append("<div class=\"item-subtitle\">").append(esc(ach.getAchievedAt().toString())).append("</div>\n");
                }
                if (notBlank(ach.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(ach.getDescription())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        // ── Languages ────────────────────────────────────────────────────────
        List<ProfileLanguageResponse> languages = data.getLanguages();
        if (nonEmpty(languages)) {
            sb.append("<div class=\"section-heading\">Languages</div>\n");
            sb.append("<div class=\"item\">\n");
            String langList = languages.stream()
                    .map(l -> s(l.getLanguageName()) + (l.getProficiency() != null ? " — " + l.getProficiency().name() : ""))
                    .collect(Collectors.joining(", "));
            sb.append("<span class=\"skill-list\">").append(esc(langList)).append("</span>\n");
            sb.append("</div>\n");
        }

        // ── Services ─────────────────────────────────────────────────────────
        List<ServiceResponse> services = data.getServices();
        if (nonEmpty(services)) {
            sb.append("<div class=\"section-heading\">Services</div>\n");
            for (ServiceResponse svc : services) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(svc.getTitle()))).append("</div>\n");
                if (notBlank(svc.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(svc.getDescription())).append("</div>\n");
                }
                if (notBlank(svc.getPriceRange())) {
                    sb.append("<div class=\"item-subtitle\">Price: ").append(esc(svc.getPriceRange())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        sb.append("</body>\n</html>");
        return sb.toString();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String esc(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    /**
     * Renders a rich-text field (authored via the Jodit WYSIWYG editor and stored as HTML)
     * for embedding in the XHTML export. Sanitizes to a safe subset of formatting tags and
     * re-serializes as well-formed XHTML, since openhtmltopdf requires strict XML syntax.
     */
    private String richText(String value) {
        if (value == null || value.isBlank()) return "";
        Safelist safelist = Safelist.relaxed()
                .addTags("u", "s", "strike")
                .addAttributes("span", "style")
                .addAttributes("p", "style")
                .addAttributes("li", "style");
        String cleaned = Jsoup.clean(value, safelist);
        Document doc = Jsoup.parse(cleaned);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).prettyPrint(false);
        return doc.body().html();
    }

    private String s(String value) {
        return value != null ? value : "";
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private <T> boolean nonEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}
