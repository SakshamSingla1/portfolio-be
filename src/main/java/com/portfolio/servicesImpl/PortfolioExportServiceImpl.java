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
import com.portfolio.entities.Profile;
import com.portfolio.enums.ExceptionCodeEnum;
import com.portfolio.enums.SkillCategoryEnum;
import com.portfolio.exceptions.GenericException;
import com.portfolio.services.PortfolioExportService;
import com.portfolio.services.ProfileMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
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

        sb.append(buildHeader(profile, theme));

        if (notBlank(profile.getAboutMe())) {
            sb.append("<div class=\"summary\">\n");
            sb.append("<div class=\"summary-label\">Professional Summary</div>\n");
            sb.append("<div class=\"summary-text\">").append(richText(profile.getAboutMe())).append("</div>\n");
            sb.append("</div>\n");
        }

        sb.append("<table class=\"layout\" cellspacing=\"0\" cellpadding=\"0\">\n<tr>\n");
        sb.append("<td class=\"col-main\">\n").append(buildMainColumn(data)).append("</td>\n");
        sb.append("<td class=\"col-side\">\n").append(buildSideColumn(data)).append("</td>\n");
        sb.append("</tr>\n</table>\n");

        sb.append("</body>\n</html>");
        return sb.toString();
    }

    private String buildCss(Theme t) {
        StringBuilder css = new StringBuilder();
        css.append("@page { size: A4; margin: 0mm 14mm 14mm 14mm; }\n");
        css.append("* { box-sizing: border-box; }\n");
        css.append("body { font-family: Arial, Helvetica, sans-serif; font-size: 9.5pt; line-height: 1.45; color: #262626; background: #ffffff; margin: 0; padding: 0; }\n");

        // Header band (page top margin is zero so this can bleed edge-to-edge without clipping)
        css.append(".header { background-color: ").append(t.headerBg).append("; color: #ffffff; padding: 16px 14mm; margin: 0 -14mm 0 -14mm; }\n");
        css.append(".name { font-size: 22pt; font-weight: bold; color: #ffffff; letter-spacing: 0.3px; }\n");
        css.append(".role-title { font-size: 11.5pt; color: ").append(t.headerAccentText).append("; margin-top: 3px; }\n");
        css.append(".contact-line { font-size: 9pt; color: #ffffff; margin-top: 8px; }\n");
        css.append(".contact-line span.sep { color: ").append(t.headerAccentText).append("; padding: 0 6px; }\n");

        // Summary band (full width, under header)
        css.append(".summary { padding: 10px 0 4px 0; border-bottom: 1px solid ").append(t.neutral200).append("; margin-bottom: 4px; }\n");
        css.append(".summary-label { font-size: 8.5pt; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; color: ").append(t.primary600).append("; margin-bottom: 3px; }\n");
        css.append(".summary-text { font-size: 9.5pt; color: #333333; }\n");
        css.append(".summary-text p { margin: 0 0 4px 0; }\n");

        // Two-column layout
        css.append(".layout { width: 100%; table-layout: fixed; border-collapse: collapse; margin-top: 10px; }\n");
        css.append(".col-main { width: 63%; vertical-align: top; padding-right: 16px; }\n");
        css.append(".col-side { width: 37%; vertical-align: top; background-color: ").append(t.neutral50).append("; padding: 10px 12px; border-left: 1px solid ").append(t.neutral200).append("; }\n");

        // Section headings
        css.append(".section-heading { font-size: 10.5pt; font-weight: bold; text-transform: uppercase; letter-spacing: 0.6px; color: ").append(t.primary700).append("; border-left: 3px solid ").append(t.primary500).append("; padding-left: 7px; margin-top: 12px; margin-bottom: 7px; }\n");
        css.append(".col-main .section-heading:first-child, .col-side .section-heading:first-child { margin-top: 0; }\n");

        // Items
        css.append(".item { margin-bottom: 9px; }\n");
        css.append(".item-title { font-weight: bold; font-size: 10pt; color: #1a1a1a; }\n");
        css.append(".item-subtitle { font-size: 8.7pt; color: #666666; margin-top: 1px; }\n");
        css.append(".item-desc { font-size: 9pt; margin-top: 3px; color: #3a3a3a; }\n");
        css.append(".item-desc p { margin: 0 0 4px 0; }\n");
        css.append(".item-desc ul, .item-desc ol { margin: 2px 0 2px 15px; padding: 0; }\n");
        css.append(".item-desc li { margin-bottom: 3px; }\n");
        css.append(".link { color: ").append(t.primary600).append("; font-size: 8.7pt; }\n");

        // Sidebar-specific: skill pills, compact list items
        css.append(".skill-cat { font-weight: bold; font-size: 8.7pt; color: ").append(t.primary700).append("; text-transform: uppercase; letter-spacing: 0.4px; margin-bottom: 4px; margin-top: 8px; }\n");
        css.append(".col-side .section-heading + .skill-cat { margin-top: 0; }\n");
        css.append(".pill { display: inline; background-color: ").append(t.primary50).append("; color: ").append(t.primary700).append("; border: 1px solid ").append(t.primary200).append("; border-radius: 3px; padding: 2px 6px; margin: 0 4px 4px 0; font-size: 8.3pt; }\n");
        css.append(".side-item { margin-bottom: 8px; }\n");
        css.append(".side-item-title { font-weight: bold; font-size: 9pt; color: #1a1a1a; }\n");
        css.append(".side-item-sub { font-size: 8.3pt; color: #666666; margin-top: 1px; }\n");
        css.append(".lang-row { font-size: 8.7pt; color: #3a3a3a; margin-bottom: 3px; }\n");
        css.append(".lang-name { font-weight: bold; color: #1a1a1a; }\n");
        return css.toString();
    }

    private String buildHeader(ProfileResponse profile, Theme t) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"header\">\n");
        sb.append("<div class=\"name\">").append(esc(s(profile.getFullName()))).append("</div>\n");
        if (notBlank(profile.getTitle())) {
            sb.append("<div class=\"role-title\">").append(esc(profile.getTitle())).append("</div>\n");
        }
        StringBuilder contact = new StringBuilder();
        if (notBlank(profile.getLocation())) contact.append(esc(profile.getLocation()));
        if (notBlank(profile.getEmail())) {
            if (contact.length() > 0) contact.append("<span class=\"sep\">|</span>");
            contact.append(esc(profile.getEmail()));
        }
        if (notBlank(profile.getPhone())) {
            if (contact.length() > 0) contact.append("<span class=\"sep\">|</span>");
            contact.append(esc(profile.getPhone()));
        }
        if (contact.length() > 0) {
            sb.append("<div class=\"contact-line\">").append(contact).append("</div>\n");
        }
        sb.append("</div>\n");
        return sb.toString();
    }

    // ── Main column: Experience, Education, Projects, Publications ─────────────
    private String buildMainColumn(ProfileMasterResponse data) {
        StringBuilder sb = new StringBuilder();

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

        List<EducationResponse> educations = data.getEducations();
        if (nonEmpty(educations)) {
            sb.append("<div class=\"section-heading\">Education</div>\n");
            for (EducationResponse edu : educations) {
                sb.append("<div class=\"item\">\n");
                sb.append("<div class=\"item-title\">").append(esc(s(edu.getInstitution()))).append("</div>\n");
                String degreeField = (edu.getDegree() != null ? edu.getDegree().getDisplayName() : "") +
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

        return sb.toString();
    }

    // ── Side column: Skills, Certifications, Achievements, Languages, Services ─
    private String buildSideColumn(ProfileMasterResponse data) {
        StringBuilder sb = new StringBuilder();

        List<SkillResponse> skills = data.getSkills();
        if (nonEmpty(skills)) {
            sb.append("<div class=\"section-heading\">Skills</div>\n");
            Map<SkillCategoryEnum, List<SkillResponse>> grouped = skills.stream()
                    .collect(Collectors.groupingBy(
                            sk -> sk.getCategory() != null ? sk.getCategory() : SkillCategoryEnum.OTHER,
                            LinkedHashMap::new, Collectors.toList()));
            for (Map.Entry<SkillCategoryEnum, List<SkillResponse>> entry : grouped.entrySet()) {
                List<String> names = entry.getValue().stream()
                        .map(sk -> s(sk.getLogoName()))
                        .filter(n -> !n.isEmpty())
                        .collect(Collectors.toList());
                if (!names.isEmpty()) {
                    sb.append("<div class=\"skill-cat\">").append(esc(entry.getKey().getDisplayName())).append("</div>\n");
                    sb.append("<div class=\"item\">\n");
                    for (String name : names) {
                        sb.append("<span class=\"pill\">").append(esc(name)).append("</span>");
                    }
                    sb.append("\n</div>\n");
                }
            }
        }

        List<CertificationResponseDTO> certs = data.getCertifications();
        if (nonEmpty(certs)) {
            sb.append("<div class=\"section-heading\">Certifications</div>\n");
            for (CertificationResponseDTO cert : certs) {
                sb.append("<div class=\"side-item\">\n");
                sb.append("<div class=\"side-item-title\">").append(esc(s(cert.getTitle()))).append("</div>\n");
                String certSub = s(cert.getIssuer()) +
                        (cert.getIssueDate() != null ? " | " + cert.getIssueDate() : "");
                if (notBlank(certSub)) {
                    sb.append("<div class=\"side-item-sub\">").append(esc(certSub)).append("</div>\n");
                }
                if (notBlank(cert.getCredentialUrl())) {
                    sb.append("<div class=\"side-item-sub\"><span class=\"link\">").append(esc(cert.getCredentialUrl())).append("</span></div>\n");
                }
                sb.append("</div>\n");
            }
        }

        List<AchievementResponseDTO> achievements = data.getAchievements();
        if (nonEmpty(achievements)) {
            sb.append("<div class=\"section-heading\">Achievements</div>\n");
            for (AchievementResponseDTO ach : achievements) {
                sb.append("<div class=\"side-item\">\n");
                sb.append("<div class=\"side-item-title\">").append(esc(s(ach.getTitle()))).append("</div>\n");
                if (ach.getAchievedAt() != null) {
                    sb.append("<div class=\"side-item-sub\">").append(esc(ach.getAchievedAt().toString())).append("</div>\n");
                }
                if (notBlank(ach.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(ach.getDescription())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        List<ProfileLanguageResponse> languages = data.getLanguages();
        if (nonEmpty(languages)) {
            sb.append("<div class=\"section-heading\">Languages</div>\n");
            for (ProfileLanguageResponse lang : languages) {
                sb.append("<div class=\"lang-row\"><span class=\"lang-name\">").append(esc(s(lang.getLanguageName()))).append("</span>");
                if (lang.getProficiency() != null) {
                    sb.append(" — ").append(esc(lang.getProficiency().getDisplayName()));
                }
                sb.append("</div>\n");
            }
        }

        List<ServiceResponse> services = data.getServices();
        if (nonEmpty(services)) {
            sb.append("<div class=\"section-heading\">Services</div>\n");
            for (ServiceResponse svc : services) {
                sb.append("<div class=\"side-item\">\n");
                sb.append("<div class=\"side-item-title\">").append(esc(s(svc.getTitle()))).append("</div>\n");
                if (notBlank(svc.getDescription())) {
                    sb.append("<div class=\"item-desc\">").append(richText(svc.getDescription())).append("</div>\n");
                }
                if (notBlank(svc.getPriceRange())) {
                    sb.append("<div class=\"side-item-sub\">Price: ").append(esc(svc.getPriceRange())).append("</div>\n");
                }
                sb.append("</div>\n");
            }
        }

        return sb.toString();
    }

    // ── Theme ────────────────────────────────────────────────────────────────

    /**
     * Resolves accent colors from the profile's own selected portfolio color theme
     * (falls back to a neutral navy palette if no theme is mapped) so the exported
     * resume visually matches the profile's live portfolio site.
     */
    private static final class Theme {
        final String headerBg;
        final String headerAccentText;
        final String primary500;
        final String primary600;
        final String primary700;
        final String primary200;
        final String primary50;
        final String neutral50;
        final String neutral200;

        private Theme(String headerBg, String headerAccentText, String primary500, String primary600,
                      String primary700, String primary200, String primary50, String neutral50, String neutral200) {
            this.headerBg = headerBg;
            this.headerAccentText = headerAccentText;
            this.primary500 = primary500;
            this.primary600 = primary600;
            this.primary700 = primary700;
            this.primary200 = primary200;
            this.primary50 = primary50;
            this.neutral50 = neutral50;
            this.neutral200 = neutral200;
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
            String secondary900 = shades.getOrDefault("secondary900", "#1e3a5f");
            String primary500 = shades.getOrDefault("primary500", "#2f6fb0");
            String primary600 = shades.getOrDefault("primary600", "#265a8d");
            String primary700 = shades.getOrDefault("primary700", "#1e3a5f");
            String primary200 = shades.getOrDefault("primary200", "#bcd6ee");
            String primary50 = shades.getOrDefault("primary50", "#eaf3fb");
            String neutral50 = shades.getOrDefault("neutral50", "#f7f8fa");
            String neutral200 = shades.getOrDefault("neutral200", "#e2e2e2");
            String accent500 = shades.getOrDefault("accent500", primary500);
            return new Theme(secondary900, accent500, primary500, primary600, primary700, primary200, primary50, neutral50, neutral200);
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
