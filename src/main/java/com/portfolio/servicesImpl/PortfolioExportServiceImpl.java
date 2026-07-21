package com.portfolio.servicesImpl;

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
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.ProfileMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioExportServiceImpl implements PortfolioExportService {

    private static final int EXPERIENCE_MAX_BULLETS = 8;
    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMM yyyy");

    private final ProfileMasterService profileMasterService;
    private final ProfileDao profileDao;

    @Override
    public byte[] exportPdf(String username) throws GenericException {
        Profile profile = profileDao.findByUserName(username)
                .orElseThrow(() -> new GenericException(ExceptionCodeEnum.PROFILE_NOT_FOUND, "Profile not found: " + username));

        ProfileMasterResponse data = profileMasterService.getForResumeExport(profile.getId());

        try {
            String html = buildHtml(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/fa-solid-900.ttf"), "FASolid");
            builder.useFont(() -> getClass().getResourceAsStream("/fonts/fa-brands-400.ttf"), "FABrands");
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
        Theme theme = Theme.from(data.getColorTheme());

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        sb.append("<head>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>\n");
        sb.append("<title>").append(esc(s(profile.getFullName()))).append(" — Resume</title>\n");
        sb.append("<style type=\"text/css\">\n").append(buildCss(theme)).append("</style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");

        sb.append(buildHeader(profile, data.getSocialLinks(), theme));

        sb.append("<div class=\"content\">\n");
        if (notBlank(profile.getAboutMe())) {
            sb.append("<div class=\"section-heading\">Professional Summary</div>\n");
            sb.append("<div class=\"summary-text\">").append(richText(profile.getAboutMe())).append("</div>\n");
        }
        sb.append(buildSkills(data));
        sb.append(buildExperience(data));
        sb.append(buildProjects(data));
        sb.append(buildEducation(data));
        sb.append(buildCertifications(data));
        sb.append(buildPublications(data));
        sb.append(buildAchievements(data));
        sb.append(buildLanguages(data));
        sb.append(buildServices(data));
        sb.append("</div>\n");

        sb.append("</body>\n</html>");
        return sb.toString();
    }

    private String buildCss(Theme t) {
        StringBuilder css = new StringBuilder();
        css.append("@page { size: A4; margin: 14mm 14mm 12mm 14mm; }\n");
        css.append("* { box-sizing: border-box; }\n");
        css.append("body { font-family: 'Times New Roman', Times, serif; font-size: 10pt; line-height: 1.35; color: #111111; background: #ffffff; margin: 0; padding: 0; }\n");

        // Header — centered, plain white, classic ATS-friendly style
        css.append(".header { text-align: center; }\n");
        css.append(".name { font-size: 20pt; font-weight: bold; letter-spacing: 0.3px; }\n");
        css.append(".contact-line { font-size: 9.3pt; margin-top: 5px; }\n");
        css.append(".contact-item, .social-item { display: inline-block; margin: 0 8px; }\n");
        css.append(".contact-item .icon, .social-item .icon { margin-right: 3px; color: ").append(t.accent).append("; }\n");
        css.append(".social-item a { color: ").append(t.accent).append("; text-decoration: none; }\n");

        // Icon fonts (Font Awesome Free, bundled — see resources/fonts/FONT-AWESOME-LICENSE.txt).
        // Registered programmatically via builder.useFont(...) in exportPdf(), not @font-face,
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
        css.append(".section-heading { font-size: 11.5pt; font-weight: bold; text-transform: uppercase; letter-spacing: 0.5px; border-bottom: 0.75pt solid #000000; padding-bottom: 2px; margin-top: 11px; margin-bottom: 6px; }\n");
        css.append(".content > .section-heading:first-child { margin-top: 0; }\n");

        css.append(".summary-text { font-size: 9.8pt; color: #1a1a1a; margin-bottom: 4px; }\n");
        css.append(".summary-text p { margin: 0 0 3px 0; }\n");

        // Title-left / date-right row, reused for Experience, Projects, Education, Certifications, Publications
        css.append(".row { display: table; width: 100%; }\n");
        css.append(".row .left { display: table-cell; text-align: left; font-weight: bold; font-size: 10pt; color: #111111; }\n");
        css.append(".row .right { display: table-cell; text-align: right; font-size: 9.5pt; color: #111111; white-space: nowrap; padding-left: 8px; }\n");
        css.append(".subtitle-italic { font-style: italic; font-size: 9.5pt; color: #1a1a1a; margin-top: 1px; }\n");

        css.append(".item { margin-bottom: 7px; }\n");
        css.append(".item-title { font-weight: bold; font-size: 10pt; color: #111111; }\n");
        css.append(".item-desc { font-size: 9.5pt; margin-top: 2px; color: #1a1a1a; }\n");
        css.append(".item-desc p { margin: 0 0 3px 0; }\n");
        css.append(".item-desc ul, .item-desc ol { margin: 2px 0 2px 15px; padding: 0; }\n");
        css.append(".item-desc li { margin-bottom: 2px; }\n");
        css.append(".link { color: ").append(t.accent).append("; font-size: 9.3pt; }\n");

        // Skills — plain label:value lines, no pills (matches classic ATS-optimized format)
        css.append(".skill-row { font-size: 9.7pt; margin-bottom: 2px; }\n");
        css.append(".skill-cat { font-weight: bold; }\n");

        css.append(".lang-line { font-size: 9.7pt; }\n");
        return css.toString();
    }

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

    private String buildHeader(ProfileResponse profile, List<SocialLinkResponseDTO> socialLinks, Theme t) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"header\">\n");
        sb.append("<div class=\"name\">").append(esc(s(profile.getFullName()))).append("</div>\n");

        StringBuilder contact = new StringBuilder();
        if (notBlank(profile.getPhone())) {
            contact.append("<span class=\"contact-item\"><i class=\"icon icon-phone\"></i>").append(esc(profile.getPhone())).append("</span>");
        }
        if (notBlank(profile.getEmail())) {
            contact.append("<span class=\"contact-item\"><i class=\"icon icon-envelope\"></i>").append(esc(profile.getEmail())).append("</span>");
        }
        if (nonEmpty(socialLinks)) {
            for (PlatformEnum platform : IMPORTANT_PLATFORMS) {
                socialLinks.stream()
                        .filter(l -> l.getPlatform() == platform && l.getStatus() == StatusEnum.ACTIVE && notBlank(l.getUrl()))
                        .findFirst()
                        .ifPresent(l -> {
                            String icon = PLATFORM_ICON.getOrDefault(platform, "icon-globe");
                            String label = PLATFORM_LABEL.getOrDefault(platform, platform.name());
                            contact.append("<span class=\"social-item\"><i class=\"icon ").append(icon).append("\"></i>")
                                    .append("<a href=\"").append(esc(l.getUrl())).append("\">")
                                    .append(esc(label))
                                    .append("</a></span>");
                        });
            }
        }
        if (notBlank(profile.getLocation())) {
            contact.append("<span class=\"contact-item\"><i class=\"icon icon-location\"></i>").append(esc(profile.getLocation())).append("</span>");
        }
        if (contact.length() > 0) {
            sb.append("<div class=\"contact-line\">").append(contact).append("</div>\n");
        }

        sb.append("</div>\n");
        return sb.toString();
    }

    private String buildSkills(ProfileMasterResponse data) {
        List<SkillResponse> skills = data.getSkills();
        if (!nonEmpty(skills)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Skills</div>\n");
        Map<SkillCategoryEnum, List<SkillResponse>> grouped = skills.stream()
                .collect(Collectors.groupingBy(
                        sk -> sk.getCategory() != null ? sk.getCategory() : SkillCategoryEnum.OTHER,
                        LinkedHashMap::new, Collectors.toList()));
        for (Map.Entry<SkillCategoryEnum, List<SkillResponse>> entry : grouped.entrySet()) {
            String names = entry.getValue().stream()
                    .map(sk -> s(sk.getLogoName()))
                    .filter(n -> !n.isEmpty())
                    .collect(Collectors.joining(", "));
            if (!names.isEmpty()) {
                sb.append("<div class=\"skill-row\"><span class=\"skill-cat\">").append(esc(entry.getKey().getDisplayName())).append(":</span> ")
                        .append(esc(names)).append("</div>\n");
            }
        }
        return sb.toString();
    }

    private String buildExperience(ProfileMasterResponse data) {
        List<ExperienceResponse> experiences = data.getExperiences();
        if (!nonEmpty(experiences)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Experience</div>\n");
        for (ExperienceResponse exp : sortByDateDesc(experiences,
                e -> nullSafe(parseDateSafe(e.getEndDate()), LocalDate.MAX),
                e -> nullSafe(parseDateSafe(e.getStartDate()), LocalDate.MIN))) {
            sb.append("<div class=\"item\">\n");
            String dates = formatMonthYear(exp.getStartDate()) + " – " + (notBlank(exp.getEndDate()) ? formatMonthYear(exp.getEndDate()) : "Present");
            sb.append("<div class=\"row\"><span class=\"left\">").append(esc(s(exp.getJobTitle()))).append("</span>")
                    .append("<span class=\"right\">").append(esc(dates)).append("</span></div>\n");
            String companyLine = s(exp.getCompanyName()) + (notBlank(exp.getLocation()) ? " — " + exp.getLocation() : "");
            if (notBlank(companyLine)) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(companyLine)).append("</div>\n");
            }
            if (notBlank(exp.getDescription())) {
                sb.append("<div class=\"item-desc\">").append(richTextLimited(exp.getDescription(), EXPERIENCE_MAX_BULLETS)).append("</div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    private String buildProjects(ProfileMasterResponse data) {
        List<ProjectResponse> projects = data.getProjects();
        if (!nonEmpty(projects)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Projects</div>\n");
        for (ProjectResponse proj : projects) {
            sb.append("<div class=\"item\">\n");
            sb.append("<div class=\"item-title\">").append(esc(s(proj.getProjectName()))).append("</div>\n");
            if (proj.getSkills() != null && !proj.getSkills().isEmpty()) {
                String techStack = proj.getSkills().stream()
                        .map(sk -> s(sk.getLogoName()))
                        .filter(n -> !n.isEmpty())
                        .collect(Collectors.joining(", "));
                if (!techStack.isEmpty()) {
                    sb.append("<div class=\"subtitle-italic\">").append(esc(techStack)).append("</div>\n");
                }
            }
            if (notBlank(proj.getProjectDescription())) {
                sb.append("<div class=\"item-desc\">").append(richText(proj.getProjectDescription())).append("</div>\n");
            }
            if (notBlank(proj.getProjectLink())) {
                sb.append("<div class=\"subtitle-italic\"><span class=\"link\">").append(esc(proj.getProjectLink())).append("</span></div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    // Education intentionally shows no description/bullets — institution, degree, and dates only.
    private String buildEducation(ProfileMasterResponse data) {
        List<EducationResponse> educations = data.getEducations();
        if (!nonEmpty(educations)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Education</div>\n");
        for (EducationResponse edu : sortByDateDesc(educations,
                e -> e.getEndYear() != null ? e.getEndYear() : Integer.MAX_VALUE,
                e -> e.getStartYear() != null ? e.getStartYear() : Integer.MIN_VALUE)) {
            sb.append("<div class=\"item\">\n");
            String degreeField = (edu.getDegree() != null ? edu.getDegree().getDisplayName() : "") +
                    (notBlank(edu.getFieldOfStudy()) ? " in " + edu.getFieldOfStudy() : "");
            String yearRange = edu.getStartYear() != null
                    ? String.valueOf(edu.getStartYear()) + (edu.getEndYear() != null ? " – " + edu.getEndYear() : "")
                    : "";
            sb.append("<div class=\"row\"><span class=\"left\">").append(esc(s(edu.getInstitution()))).append("</span>")
                    .append("<span class=\"right\">").append(esc(yearRange)).append("</span></div>\n");
            if (notBlank(degreeField)) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(degreeField)).append("</div>\n");
            }
            if (notBlank(edu.getGrade())) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(edu.getGrade())).append("</div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    private String buildCertifications(ProfileMasterResponse data) {
        List<CertificationResponseDTO> certs = data.getCertifications();
        if (!nonEmpty(certs)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Certifications</div>\n");
        for (CertificationResponseDTO cert : sortByDateDesc(certs,
                c -> nullSafe(c.getIssueDate(), LocalDate.MIN))) {
            sb.append("<div class=\"item\">\n");
            sb.append("<div class=\"row\"><span class=\"left\">").append(esc(s(cert.getTitle()))).append("</span>")
                    .append("<span class=\"right\">").append(esc(formatMonthYear(cert.getIssueDate()))).append("</span></div>\n");
            if (notBlank(cert.getIssuer())) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(cert.getIssuer())).append("</div>\n");
            }
            if (notBlank(cert.getCredentialUrl())) {
                sb.append("<div class=\"subtitle-italic\"><span class=\"link\">").append(esc(cert.getCredentialUrl())).append("</span></div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    private String buildPublications(ProfileMasterResponse data) {
        List<PublicationResponseDTO> publications = data.getPublications();
        if (!nonEmpty(publications)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Publications</div>\n");
        for (PublicationResponseDTO pub : sortByDateDesc(publications,
                p -> nullSafe(p.getPublishedDate(), LocalDate.MIN))) {
            sb.append("<div class=\"item\">\n");
            String typeLabel = notBlank(pub.getType()) ? " (" + pub.getType() + ")" : "";
            sb.append("<div class=\"row\"><span class=\"left\">").append(esc(s(pub.getTitle()) + typeLabel)).append("</span>")
                    .append("<span class=\"right\">").append(esc(formatMonthYear(pub.getPublishedDate()))).append("</span></div>\n");
            if (notBlank(pub.getPublisher())) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(pub.getPublisher())).append("</div>\n");
            }
            if (notBlank(pub.getDescription())) {
                sb.append("<div class=\"item-desc\">").append(richText(pub.getDescription())).append("</div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    private String buildAchievements(ProfileMasterResponse data) {
        List<AchievementResponseDTO> achievements = data.getAchievements();
        if (!nonEmpty(achievements)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Achievements</div>\n");
        for (AchievementResponseDTO ach : sortByDateDesc(achievements,
                a -> nullSafe(a.getAchievedAt(), LocalDate.MIN))) {
            sb.append("<div class=\"item\">\n");
            sb.append("<div class=\"row\"><span class=\"left\">").append(esc(s(ach.getTitle()))).append("</span>")
                    .append("<span class=\"right\">").append(esc(formatMonthYear(ach.getAchievedAt()))).append("</span></div>\n");
            if (notBlank(ach.getDescription())) {
                sb.append("<div class=\"item-desc\">").append(richText(ach.getDescription())).append("</div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    private String buildLanguages(ProfileMasterResponse data) {
        List<ProfileLanguageResponse> languages = data.getLanguages();
        if (!nonEmpty(languages)) return "";
        String line = languages.stream()
                .map(l -> s(l.getLanguageName()) + (l.getProficiency() != null ? " (" + l.getProficiency().getDisplayName() + ")" : ""))
                .collect(Collectors.joining(", "));
        return "<div class=\"section-heading\">Languages</div>\n<div class=\"lang-line\">" + esc(line) + "</div>\n";
    }

    private String buildServices(ProfileMasterResponse data) {
        List<ServiceResponse> services = data.getServices();
        if (!nonEmpty(services)) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"section-heading\">Services</div>\n");
        for (ServiceResponse svc : services) {
            sb.append("<div class=\"item\">\n");
            sb.append("<div class=\"item-title\">").append(esc(s(svc.getTitle()))).append("</div>\n");
            if (notBlank(svc.getPriceRange())) {
                sb.append("<div class=\"subtitle-italic\">").append(esc(svc.getPriceRange())).append("</div>\n");
            }
            if (notBlank(svc.getDescription())) {
                sb.append("<div class=\"item-desc\">").append(richText(svc.getDescription())).append("</div>\n");
            }
            sb.append("</div>\n");
        }
        return sb.toString();
    }

    // ── Theme ────────────────────────────────────────────────────────────────

    /**
     * Resolves a single accent color (used only for header icons/links) from the profile's own
     * selected portfolio color theme, falling back to a neutral blue if none is mapped. Everything
     * else in this classic ATS-style layout stays plain black for maximum parser compatibility.
     */
    private static final class Theme {
        final String accent;

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

    private String esc(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

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
