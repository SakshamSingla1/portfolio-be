package com.portfolio.servicesImpl;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.Achievements.AchievementResponseDTO;
import com.portfolio.dtos.Certifications.CertificationResponseDTO;
import com.portfolio.dtos.ColorTheme.ColorGroupDTO;
import com.portfolio.dtos.ColorTheme.ColorShadeDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.Education.EducationResponse;
import com.portfolio.dtos.Experience.ExperienceResponse;
import com.portfolio.dtos.Language.ProfileLanguageResponse;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.Project.ProjectResponse;
import com.portfolio.dtos.Publication.PublicationResponseDTO;
import com.portfolio.dtos.Services.ServiceResponse;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.CloudinaryService;
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.ProfileMasterService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Renders a profile's data as an ATS-friendly resume-style PDF ("export portfolio as resume") and
 * uploads it to Cloudinary, returning a URL -- same shape as SteelBazaar's PdfGenerator (Thymeleaf
 * template -> HTML -> openhtmltopdf -> cloud upload -> URL) and the resume-download flow already
 * in this app: the caller redirects the client straight to the returned URL instead of this server
 * proxying PDF bytes through itself.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioExportServiceImpl implements PortfolioExportService {

    private static final int EXPERIENCE_MAX_BULLETS = 6;
    private static final int PROJECT_MAX_BULLETS = 3;
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final String EXPORT_FOLDER = "portfolio/exports";
    private static final String TEMPLATE_NAME = "portfolio-export";

    // Registered under this family name (see exportPdf) instead of relying on the CSS 'Times New
    // Roman'/serif name resolving to whatever font a given host has installed. Without an explicitly
    // embedded TTF, openhtmltopdf falls back to its built-in base-14 AFM "Times-Roman" substitute,
    // which has broken kerning for several glyph pairs (e.g. "Fr", "Ja") that shows up as a stray
    // gap mid-word. Liberation Serif is metrically compatible with Times New Roman, so line breaks
    // and page-fit are unaffected.
    private static final String BODY_FONT_FAMILY = "PortfolioSerif";

    private final ProfileMasterService profileMasterService;
    private final ProfileDao profileDao;
    private final CloudinaryService cloudinaryService;
    private final TemplateEngine templateEngine;

    @Override
    public String exportPdf(String username) throws GenericException {
        Profile profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + username));

        ProfileMasterResponse data = profileMasterService.getForResumeExport(profile.getId());

        try {
            String html = renderHtml(data);
            byte[] pdf = renderPdf(html);
            Map<String, Object> uploaded = cloudinaryService.uploadBytes(pdf, EXPORT_FOLDER);
            return (String) uploaded.get("secure_url");
        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            log.error("PDF generation failed for username={}", username, e);
            throw new GenericException(ExceptionCodeEnum.INTERNAL_SERVER_ERROR, "Failed to generate PDF");
        }
    }

    // ── HTML rendering (Thymeleaf) ──────────────────────────────────────────

    private String renderHtml(ProfileMasterResponse data) {
        ProfileResponse profile = data.getProfile();
        Theme theme = Theme.from(data.getColorTheme());

        Context context = new Context();
        context.setVariable("fullName", s(profile.getFullName()));
        context.setVariable("css", buildCss(theme));
        context.setVariable("contactItems", buildContactItems(profile, data.getSocialLinks()));
        context.setVariable("summaryHtml", notBlank(profile.getAboutMe()) ? richText(profile.getAboutMe()) : null);
        context.setVariable("skillGroups", buildSkillGroups(data.getSkills()));
        context.setVariable("experiences", buildExperiences(data.getExperiences()));
        context.setVariable("projects", buildProjects(data.getProjects()));
        context.setVariable("educations", buildEducations(data.getEducations()));
        context.setVariable("certifications", buildCertifications(data.getCertifications()));
        context.setVariable("publications", buildPublications(data.getPublications()));
        context.setVariable("achievements", buildAchievements(data.getAchievements()));
        context.setVariable("languagesLine", buildLanguagesLine(data.getLanguages()));
        context.setVariable("services", buildServices(data.getServices()));

        return templateEngine.process(TEMPLATE_NAME, context);
    }

    private byte[] renderPdf(String html) throws Exception {
        // Thymeleaf's default HTML template mode is lenient (not strict XML), so -- same as
        // SteelBazaar's PdfGenerator -- the rendered markup goes through jsoup's tolerant parser
        // first and is normalized into a strict W3C DOM, rather than handing raw HTML straight to
        // openhtmltopdf (which requires well-formed XML and would otherwise be one stray
        // unescaped/unclosed tag away from failing).
        Document jsoupDoc = Jsoup.parse(html);
        jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        org.w3c.dom.Document w3cDoc = new org.jsoup.helper.W3CDom().fromJsoup(jsoupDoc);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/fa-solid-900.ttf"), "FASolid");
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/fa-brands-400.ttf"), "FABrands");
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/LiberationSerif-Regular.ttf"), BODY_FONT_FAMILY, 400, FontStyle.NORMAL, true);
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/LiberationSerif-Bold.ttf"), BODY_FONT_FAMILY, 700, FontStyle.NORMAL, true);
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/LiberationSerif-Italic.ttf"), BODY_FONT_FAMILY, 400, FontStyle.ITALIC, true);
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/LiberationSerif-BoldItalic.ttf"), BODY_FONT_FAMILY, 700, FontStyle.ITALIC, true);
        builder.withW3cDocument(w3cDoc, "");
        builder.toStream(baos);
        builder.run();
        return baos.toByteArray();
    }

    private String buildCss(Theme t) {
        StringBuilder css = new StringBuilder();
        css.append("@page { size: A4; margin: 12mm 14mm 10mm 14mm; }\n");
        css.append("* { box-sizing: border-box; }\n");
        css.append("body { font-family: '").append(BODY_FONT_FAMILY).append("', 'Times New Roman', Times, serif; font-size: 10pt; line-height: 1.3; color: #111111; background: #ffffff; margin: 0; padding: 0; }\n");

        // Header — centered, plain white, classic ATS-friendly style
        css.append(".header { text-align: center; }\n");
        css.append(".name { font-size: 20pt; font-weight: bold; letter-spacing: 0.3px; }\n");
        css.append(".contact-line { font-size: 9.3pt; margin-top: 5px; }\n");
        css.append(".contact-item, .social-item { display: inline-block; margin: 0 8px; }\n");
        css.append(".contact-item .icon, .social-item .icon { margin-right: 3px; color: ").append(t.getAccent()).append("; }\n");
        css.append(".social-item a { color: ").append(t.getAccent()).append("; text-decoration: none; }\n");

        // Icon fonts (Font Awesome Free, bundled — see resources/fonts/FONT-AWESOME-LICENSE.txt).
        // Registered programmatically via builder.useFont(...) in renderPdf(), not @font-face,
        // since there's no base URL configured here for resolving a relative font src.
        css.append(".icon-phone::before { font-family: 'FASolid'; content: '\\f095'; }\n");
        css.append(".icon-envelope::before { font-family: 'FASolid'; content: '\\f0e0'; }\n");
        css.append(".icon-location::before { font-family: 'FASolid'; content: '\\f3c5'; }\n");
        css.append(".icon-globe::before { font-family: 'FASolid'; content: '\\f0ac'; }\n");
        css.append(".icon-github::before { font-family: 'FABrands'; content: '\\f09b'; }\n");
        css.append(".icon-linkedin::before { font-family: 'FABrands'; content: '\\f0e1'; }\n");
        css.append(".icon-gitlab::before { font-family: 'FABrands'; content: '\\f296'; }\n");
        css.append(".icon-bitbucket::before { font-family: 'FABrands'; content: '\\f171'; }\n");

        css.append(".content { margin-top: 10px; }\n");

        // Section headings — bold, uppercase, horizontal rule underneath (classic LaTeX-resume look)
        css.append(".section-heading { font-size: 11.5pt; font-weight: bold; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 0.75pt solid #000000; padding-bottom: 2px; margin-top: 9px; margin-bottom: 5px; }\n");
        css.append(".content > .section-heading:first-child { margin-top: 0; }\n");

        css.append(".summary-text { font-size: 9.8pt; color: #1a1a1a; margin-bottom: 4px; }\n");
        css.append(".summary-text p { margin: 0 0 3px 0; }\n");

        // Title-left / date-right row, reused for Experience, Projects, Education, Certifications, Publications
        css.append(".row { display: table; width: 100%; }\n");
        css.append(".row .left { display: table-cell; text-align: left; font-weight: bold; font-size: 10pt; color: #111111; }\n");
        css.append(".row .right { display: table-cell; text-align: right; font-size: 9.5pt; color: #111111; white-space: nowrap; padding-left: 8px; }\n");
        css.append(".subtitle-italic { font-style: italic; font-size: 9.5pt; color: #1a1a1a; margin-top: 1px; }\n");

        css.append(".item { margin-bottom: 5px; }\n");
        css.append(".item-title { font-weight: bold; font-size: 10pt; color: #111111; }\n");
        css.append(".item-desc { font-size: 9.5pt; margin-top: 2px; color: #1a1a1a; }\n");
        css.append(".item-desc p { margin: 0 0 3px 0; }\n");
        css.append(".item-desc ul, .item-desc ol { margin: 2px 0 2px 15px; padding: 0; }\n");
        css.append(".item-desc li { margin-bottom: 2px; }\n");
        css.append(".link { color: ").append(t.getAccent()).append("; font-size: 9.3pt; }\n");

        // Skills — plain label:value lines, no pills (matches classic ATS-optimized format)
        css.append(".skill-row { font-size: 9.7pt; margin-bottom: 2px; }\n");
        css.append(".skill-cat { font-weight: bold; }\n");

        css.append(".lang-line { font-size: 9.7pt; }\n");
        return css.toString();
    }

    // ── Section builders (DTO -> view model) ────────────────────────────────

    // Platforms worth showing on a resume header — everything else (LeetCode, Twitter, Instagram,
    // YouTube, etc.) is left off intentionally, per "only the important ones".
    private static final List<PlatformEnum> IMPORTANT_PLATFORMS = Arrays.asList(
            PlatformEnum.LINKEDIN, PlatformEnum.GITHUB, PlatformEnum.PORTFOLIO, PlatformEnum.WEBSITE,
            PlatformEnum.GITLAB, PlatformEnum.BITBUCKET
    );

    private static final Map<PlatformEnum, String> PLATFORM_ICON = new EnumMap<>(PlatformEnum.class);
    private static final Map<PlatformEnum, String> PLATFORM_LABEL = new EnumMap<>(PlatformEnum.class);
    static {
        PLATFORM_ICON.put(PlatformEnum.LINKEDIN, "icon-linkedin");
        PLATFORM_ICON.put(PlatformEnum.GITHUB, "icon-github");
        PLATFORM_ICON.put(PlatformEnum.GITLAB, "icon-gitlab");
        PLATFORM_ICON.put(PlatformEnum.BITBUCKET, "icon-bitbucket");
        PLATFORM_ICON.put(PlatformEnum.PORTFOLIO, "icon-globe");
        PLATFORM_ICON.put(PlatformEnum.WEBSITE, "icon-globe");

        PLATFORM_LABEL.put(PlatformEnum.LINKEDIN, "LinkedIn");
        PLATFORM_LABEL.put(PlatformEnum.GITHUB, "GitHub");
        PLATFORM_LABEL.put(PlatformEnum.GITLAB, "GitLab");
        PLATFORM_LABEL.put(PlatformEnum.BITBUCKET, "Bitbucket");
        PLATFORM_LABEL.put(PlatformEnum.PORTFOLIO, "Portfolio");
        PLATFORM_LABEL.put(PlatformEnum.WEBSITE, "Website");
    }

    // Ordered exactly as the original header rendered: phone, email, social links
    // (platform-priority order), then location.
    private List<ContactItemVM> buildContactItems(ProfileResponse profile, List<SocialLinkResponseDTO> socialLinks) {
        List<ContactItemVM> items = new ArrayList<>();
        if (notBlank(profile.getPhone())) {
            items.add(new ContactItemVM("icon-phone", profile.getPhone(), null));
        }
        if (notBlank(profile.getEmail())) {
            items.add(new ContactItemVM("icon-envelope", profile.getEmail(), null));
        }
        if (nonEmpty(socialLinks)) {
            for (PlatformEnum platform : IMPORTANT_PLATFORMS) {
                socialLinks.stream()
                        .filter(l -> l.getPlatform() == platform && l.getStatus() == StatusEnum.ACTIVE && notBlank(l.getUrl()))
                        .findFirst()
                        .ifPresent(l -> items.add(new ContactItemVM(
                                PLATFORM_ICON.getOrDefault(platform, "icon-globe"),
                                PLATFORM_LABEL.getOrDefault(platform, platform.name()),
                                l.getUrl()
                        )));
            }
        }
        if (notBlank(profile.getLocation())) {
            items.add(new ContactItemVM("icon-location", profile.getLocation(), null));
        }
        return items;
    }

    private List<SkillGroupVM> buildSkillGroups(List<SkillResponse> skills) {
        if (!nonEmpty(skills)) return List.of();
        Map<SkillCategoryEnum, List<SkillResponse>> grouped = skills.stream()
                .collect(Collectors.groupingBy(
                        sk -> sk.getCategory() != null ? sk.getCategory() : SkillCategoryEnum.OTHER,
                        LinkedHashMap::new, Collectors.toList()));
        List<SkillGroupVM> groups = new ArrayList<>();
        for (Map.Entry<SkillCategoryEnum, List<SkillResponse>> entry : grouped.entrySet()) {
            String names = entry.getValue().stream()
                    .map(sk -> s(sk.getLogoName()))
                    .filter(n -> !n.isEmpty())
                    .collect(Collectors.joining(", "));
            if (!names.isEmpty()) {
                groups.add(new SkillGroupVM(entry.getKey().getDisplayName(), names));
            }
        }
        return groups;
    }

    private List<ExperienceVM> buildExperiences(List<ExperienceResponse> experiences) {
        if (!nonEmpty(experiences)) return List.of();
        List<ExperienceVM> out = new ArrayList<>();
        for (ExperienceResponse exp : sortByDateDesc(experiences,
                e -> nullSafe(parseDateSafe(e.getEndDate()), LocalDate.MAX),
                e -> nullSafe(parseDateSafe(e.getStartDate()), LocalDate.MIN))) {
            String dates = formatMonthYear(exp.getStartDate()) + " – " + (notBlank(exp.getEndDate()) ? formatMonthYear(exp.getEndDate()) : "Present");
            String companyLine = s(exp.getCompanyName()) + (notBlank(exp.getLocation()) ? " — " + exp.getLocation() : "");
            String descriptionHtml = notBlank(exp.getDescription()) ? richTextLimited(exp.getDescription(), EXPERIENCE_MAX_BULLETS) : null;
            out.add(new ExperienceVM(s(exp.getJobTitle()), dates, notBlank(companyLine) ? companyLine : null, descriptionHtml));
        }
        return out;
    }

    private List<ProjectVM> buildProjects(List<ProjectResponse> projects) {
        if (!nonEmpty(projects)) return List.of();
        List<ProjectVM> out = new ArrayList<>();
        for (ProjectResponse proj : projects) {
            String techStack = null;
            if (proj.getSkills() != null && !proj.getSkills().isEmpty()) {
                String joined = proj.getSkills().stream()
                        .map(sk -> s(sk.getLogoName()))
                        .filter(n -> !n.isEmpty())
                        .collect(Collectors.joining(", "));
                techStack = joined.isEmpty() ? null : joined;
            }
            String descriptionHtml = notBlank(proj.getProjectDescription()) ? richTextLimited(proj.getProjectDescription(), PROJECT_MAX_BULLETS) : null;
            out.add(new ProjectVM(s(proj.getProjectName()), techStack, descriptionHtml,
                    notBlank(proj.getProjectLink()) ? proj.getProjectLink() : null));
        }
        return out;
    }

    // Education intentionally shows no description/bullets — institution, degree, and dates only.
    private List<EducationVM> buildEducations(List<EducationResponse> educations) {
        if (!nonEmpty(educations)) return List.of();
        List<EducationVM> out = new ArrayList<>();
        for (EducationResponse edu : sortByDateDesc(educations,
                e -> e.getEndYear() != null ? e.getEndYear() : Integer.MAX_VALUE,
                e -> e.getStartYear() != null ? e.getStartYear() : Integer.MIN_VALUE)) {
            String degreeField = (edu.getDegree() != null ? edu.getDegree().getDisplayName() : "") +
                    (notBlank(edu.getFieldOfStudy()) ? " in " + edu.getFieldOfStudy() : "");
            String yearRange = edu.getStartYear() != null
                    ? String.valueOf(edu.getStartYear()) + (edu.getEndYear() != null ? " – " + edu.getEndYear() : "")
                    : "";
            out.add(new EducationVM(s(edu.getInstitution()), yearRange,
                    notBlank(degreeField) ? degreeField : null, notBlank(edu.getGrade()) ? edu.getGrade() : null));
        }
        return out;
    }

    private List<CertificationVM> buildCertifications(List<CertificationResponseDTO> certs) {
        if (!nonEmpty(certs)) return List.of();
        List<CertificationVM> out = new ArrayList<>();
        for (CertificationResponseDTO cert : sortByDateDesc(certs, c -> nullSafe(c.getIssueDate(), LocalDate.MIN))) {
            out.add(new CertificationVM(s(cert.getTitle()), formatMonthYear(cert.getIssueDate()),
                    notBlank(cert.getIssuer()) ? cert.getIssuer() : null,
                    notBlank(cert.getCredentialUrl()) ? cert.getCredentialUrl() : null));
        }
        return out;
    }

    private List<PublicationVM> buildPublications(List<PublicationResponseDTO> publications) {
        if (!nonEmpty(publications)) return List.of();
        List<PublicationVM> out = new ArrayList<>();
        for (PublicationResponseDTO pub : sortByDateDesc(publications, p -> nullSafe(p.getPublishedDate(), LocalDate.MIN))) {
            String typeLabel = notBlank(pub.getType()) ? " (" + pub.getType() + ")" : "";
            String descriptionHtml = notBlank(pub.getDescription()) ? richText(pub.getDescription()) : null;
            out.add(new PublicationVM(s(pub.getTitle()) + typeLabel, formatMonthYear(pub.getPublishedDate()),
                    notBlank(pub.getPublisher()) ? pub.getPublisher() : null, descriptionHtml));
        }
        return out;
    }

    private List<AchievementVM> buildAchievements(List<AchievementResponseDTO> achievements) {
        if (!nonEmpty(achievements)) return List.of();
        List<AchievementVM> out = new ArrayList<>();
        for (AchievementResponseDTO ach : sortByDateDesc(achievements, a -> nullSafe(a.getAchievedAt(), LocalDate.MIN))) {
            String descriptionHtml = notBlank(ach.getDescription()) ? richText(ach.getDescription()) : null;
            out.add(new AchievementVM(s(ach.getTitle()), formatMonthYear(ach.getAchievedAt()), descriptionHtml));
        }
        return out;
    }

    private String buildLanguagesLine(List<ProfileLanguageResponse> languages) {
        if (!nonEmpty(languages)) return null;
        return languages.stream()
                .map(l -> s(l.getLanguageName()) + (l.getProficiency() != null ? " (" + l.getProficiency().getDisplayName() + ")" : ""))
                .collect(Collectors.joining(", "));
    }

    private List<ServiceVM> buildServices(List<ServiceResponse> services) {
        if (!nonEmpty(services)) return List.of();
        List<ServiceVM> out = new ArrayList<>();
        for (ServiceResponse svc : services) {
            String descriptionHtml = notBlank(svc.getDescription()) ? richText(svc.getDescription()) : null;
            out.add(new ServiceVM(s(svc.getTitle()), notBlank(svc.getPriceRange()) ? svc.getPriceRange() : null, descriptionHtml));
        }
        return out;
    }

    // ── View models (plain JavaBean-style getters -- guaranteed Thymeleaf-compatible) ───────────

    @Getter
    @AllArgsConstructor
    public static final class ContactItemVM {
        private final String iconClass;
        private final String text;
        private final String url;
    }

    @Getter
    @AllArgsConstructor
    public static final class SkillGroupVM {
        private final String categoryLabel;
        private final String names;
    }

    @Getter
    @AllArgsConstructor
    public static final class ExperienceVM {
        private final String jobTitle;
        private final String dates;
        private final String companyLine;
        private final String descriptionHtml;
    }

    @Getter
    @AllArgsConstructor
    public static final class ProjectVM {
        private final String projectName;
        private final String techStack;
        private final String descriptionHtml;
        private final String projectLink;
    }

    @Getter
    @AllArgsConstructor
    public static final class EducationVM {
        private final String institution;
        private final String yearRange;
        private final String degreeField;
        private final String grade;
    }

    @Getter
    @AllArgsConstructor
    public static final class CertificationVM {
        private final String title;
        private final String dateLabel;
        private final String issuer;
        private final String credentialUrl;
    }

    @Getter
    @AllArgsConstructor
    public static final class PublicationVM {
        private final String titleWithType;
        private final String dateLabel;
        private final String publisher;
        private final String descriptionHtml;
    }

    @Getter
    @AllArgsConstructor
    public static final class AchievementVM {
        private final String title;
        private final String dateLabel;
        private final String descriptionHtml;
    }

    @Getter
    @AllArgsConstructor
    public static final class ServiceVM {
        private final String title;
        private final String priceRange;
        private final String descriptionHtml;
    }

    // ── Theme ────────────────────────────────────────────────────────────────

    /**
     * Resolves a single accent color (used only for header icons/links) from the profile's own
     * selected portfolio color theme, falling back to a neutral blue if none is mapped. Everything
     * else in this classic ATS-style layout stays plain black for maximum parser compatibility.
     */
    @Getter
    private static final class Theme {
        private final String accent;

        private Theme(String accent) {
            this.accent = accent;
        }

        static Theme from(ColorThemeResponseDTO colorTheme) {
            Map<String, String> shades = new LinkedHashMap<>();
            if (colorTheme != null && colorTheme.getPalette() != null && colorTheme.getPalette().getColorGroups() != null) {
                for (ColorGroupDTO group : colorTheme.getPalette().getColorGroups()) {
                    if (group.getColorShades() == null) continue;
                    for (ColorShadeDTO shade : group.getColorShades()) {
                        if (shade.getColorName() != null && shade.getColorCode() != null) {
                            shades.put(shade.getColorName(), shade.getColorCode());
                        }
                    }
                }
            }
            String accent = shades.getOrDefault("primary600", "#265a8d");
            return new Theme(accent);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    // Formats an ISO "yyyy-MM-dd" string (as produced by Experience's LocalDate.toString()) as
    // "MMM yyyy" (e.g. "Apr 2025"). Falls back to the raw string if it can't be parsed.
    private String formatMonthYear(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) return "";
        try {
            return LocalDate.parse(isoDate).format(MONTH_YEAR);
        } catch (DateTimeParseException e) {
            return isoDate;
        }
    }

    private String formatMonthYear(LocalDate date) {
        return date != null ? date.format(MONTH_YEAR) : "";
    }

    private LocalDate parseDateSafe(String isoDate) {
        if (isoDate == null || isoDate.isBlank()) return null;
        try {
            return LocalDate.parse(isoDate);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private <T> T nullSafe(T value, T fallback) {
        return value != null ? value : fallback;
    }

    // Sorts a section's entries latest-first: by primary date/year descending, ties broken by
    // secondary date/year descending (e.g. Experience end date, then start date).
    private <T, K extends Comparable<K>> List<T> sortByDateDesc(
            List<T> list, java.util.function.Function<T, K> primaryKey, java.util.function.Function<T, K> secondaryKey) {
        return list.stream()
                .sorted(Comparator.comparing(primaryKey).thenComparing(secondaryKey).reversed())
                .collect(Collectors.toList());
    }

    // Single-key variant, for sections with only one meaningful date (Certifications, Publications, Achievements).
    private <T, K extends Comparable<K>> List<T> sortByDateDesc(List<T> list, java.util.function.Function<T, K> key) {
        return list.stream()
                .sorted(Comparator.comparing(key).reversed())
                .collect(Collectors.toList());
    }

    // Inline-level formatting tags that openhtmltopdf 1.0.10 is confirmed to mis-order on text
    // extraction (see stripInlineFormatting for why). Anything that can sit mid-line next to
    // plain text and carry its own styling belongs here.
    private static final String[] INLINE_FORMATTING_TAGS = {
            "b", "strong", "i", "em", "u", "s", "strike", "span", "font", "mark", "small", "sub", "sup"
    };

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
        stripInlineFormatting(doc);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).prettyPrint(false);
        return doc.body().html();
    }

    /**
     * Same as {@link #richText(String)}, but caps the number of bullet points (list items)
     * kept in the output, dropping the rest — used to keep long descriptions (e.g. Experience)
     * to a readable, resume-appropriate length.
     */
    private String richTextLimited(String value, int maxBullets) {
        if (value == null || value.isBlank()) return "";
        Safelist safelist = Safelist.relaxed()
                .addTags("u", "s", "strike")
                .addAttributes("span", "style")
                .addAttributes("p", "style")
                .addAttributes("li", "style");
        String cleaned = Jsoup.clean(value, safelist);
        Document doc = Jsoup.parse(cleaned);
        Elements listItems = doc.body().select("li");
        for (int i = listItems.size() - 1; i >= maxBullets; i--) {
            listItems.get(i).remove();
        }
        stripInlineFormatting(doc);
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).prettyPrint(false);
        return doc.body().html();
    }

    /**
     * Unwraps inline formatting elements (bold/italic/underline/color spans, etc.) in place,
     * keeping their text content but dropping the tag.
     * <p>
     * Confirmed by isolated reproduction against openhtmltopdf-pdfbox 1.0.10: when a line of text
     * contains ANY inline element next to plain text — {@code <b>}, {@code <strong>}, or even a
     * bare {@code <span style="color:red">} with no font change at all — the library's PDF
     * content-stream writer emits that inline run's text-showing operator out of visual order
     * relative to its plain-text siblings on the same line. The rendered PDF looks correct
     * (each glyph still carries its own explicit position), but the invisible text layer that
     * ATS parsers and copy-paste read comes out scrambled, e.g. "Developed and maintained
     * scalable frontend applications using React.js" extracts as "Developed and maintained
     * using React.js ... scalable frontend applications" — the styled run gets flushed after the
     * plain text of that line instead of interleaved in place. A plain paragraph with zero inline
     * elements was the only case that extracted in correct order.
     * <p>
     * Block-level structure (paragraphs, list items) is unaffected by this, since each block is
     * already its own line with a single run — only text-level emphasis WITHIN a line is at risk.
     * Since this export explicitly bills itself as "ATS-optimized", correct text order beats
     * keeping inline bold/italic/color emphasis, so we drop the tags rather than the content.
     */
    private void stripInlineFormatting(Document doc) {
        for (String tag : INLINE_FORMATTING_TAGS) {
            for (Element el : doc.body().select(tag)) {
                el.unwrap();
            }
        }
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
