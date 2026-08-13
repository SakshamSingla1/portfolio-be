package com.portfolio.controllers;

import com.portfolio.dao.profile.ProfileDao;
import com.portfolio.dtos.ColorTheme.ColorGroupDTO;
import com.portfolio.dtos.ColorTheme.ColorShadeDTO;
import com.portfolio.dtos.ColorTheme.ColorThemeResponseDTO;
import com.portfolio.dtos.Profile.ProfileMasterResponse;
import com.portfolio.dtos.Profile.ProfileResponse;
import com.portfolio.dtos.Skill.SkillResponse;
import com.portfolio.dtos.SocialLinks.SocialLinkResponseDTO;
import com.portfolio.enums.PlatformEnum;
import com.portfolio.enums.StatusEnum;
import com.portfolio.services.ProfileMasterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmbedController {

    private static final String GITHUB_ICON_PATH =
        "M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z";

    private static final String LINKEDIN_ICON_PATH =
        "M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667h-3.554v-11.452h3.414v1.561h.049c.476-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zm-15.115-13.019c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019h-3.564v-11.452h3.564v11.452zm16.545-20.452h-20.452c-.977 0-1.771.774-1.771 1.729v20.542c0 .955.794 1.729 1.771 1.729h20.451c.978 0 1.778-.774 1.778-1.729v-20.542c0-.955-.8-1.729-1.778-1.729z";

    private final ProfileDao profileDao;
    private final ProfileMasterService profileMasterService;

    @GetMapping("/embed/{username}")
    public ResponseEntity<String> getEmbedWidget(@PathVariable String username) {
        return respond(username, this::buildEmbedHtml);
    }

    @GetMapping("/embed/{username}/email")
    public ResponseEntity<String> getEmbedWidgetForEmail(@PathVariable String username) {
        return respond(username, this::buildEmailSafeHtml);
    }

    private ResponseEntity<String> respond(String username, java.util.function.BiFunction<CardData, String, String> renderer) {
        try {
            Long profileId = profileDao.findByUserName(username)
                    .map(p -> p.getId())
                    .orElse(null);

            if (profileId == null) {
                return ResponseEntity.notFound().<String>build();
            }

            ProfileMasterResponse data = profileMasterService.getByProfileId(profileId);
            if (data == null) {
                return ResponseEntity.notFound().<String>build();
            }

            CardData card = computeCardData(data, username);
            String html = renderer.apply(card, username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/html;charset=UTF-8"));
            headers.add("Content-Security-Policy", "frame-ancestors *");
            return ResponseEntity.ok().headers(headers).body(html);

        } catch (Exception e) {
            log.warn("Embed widget error for {}: {}", username, e.getMessage());
            return ResponseEntity.notFound().<String>build();
        }
    }

    /** Everything both renderers need, computed once. */
    private record CardData(
        String fullName, String title, String location, boolean openToWork,
        String imageUrl, String bioPlain, List<String> skills,
        String githubUrl, String linkedinUrl, String websiteUrl, String portfolioUrl,
        String primary400, String primary500, String primary600, String primary700
    ) {}

    private CardData computeCardData(ProfileMasterResponse data, String username) {
        ProfileResponse p = data.getProfile();

        String fullName = p.getFullName() != null ? p.getFullName() : "";
        String title    = p.getTitle()    != null ? p.getTitle()    : "";
        String location = p.getLocation() != null ? p.getLocation() : "";
        boolean openToWork = p.isAvailableForWork();

        String bioPlain = "";
        String aboutMe = p.getAboutMe();
        if (aboutMe != null && !aboutMe.isBlank()) {
            String plain = aboutMe.replaceAll("<[^>]+>", " ").replaceAll("&nbsp;", " ")
                .replaceAll("\\s+", " ").trim();
            if (!plain.isEmpty()) {
                bioPlain = plain.length() > 150 ? plain.substring(0, 150).trim() + "…" : plain;
            }
        }

        List<String> skills = data.getSkills() != null
            ? data.getSkills().stream()
                .map(SkillResponse::getLogoName)
                .filter(n -> n != null && !n.isBlank())
                .limit(10)
                .toList()
            : List.of();

        List<SocialLinkResponseDTO> links = data.getSocialLinks();
        String githubUrl   = findActiveSocialUrl(links, PlatformEnum.GITHUB);
        String linkedinUrl = findActiveSocialUrl(links, PlatformEnum.LINKEDIN);
        String websiteUrl  = findActiveSocialUrl(links, PlatformEnum.WEBSITE);
        if (websiteUrl == null) {
            websiteUrl = findActiveSocialUrl(links, PlatformEnum.PORTFOLIO);
        }
        String portfolioUrl = findActiveSocialUrl(links, PlatformEnum.PORTFOLIO);
        if (portfolioUrl == null) {
            portfolioUrl = "https://portfoliosbuilder.com/" + username;
        }

        return new CardData(
            fullName, title, location, openToWork,
            p.getProfileImageUrl(), bioPlain, skills,
            githubUrl, linkedinUrl, websiteUrl, portfolioUrl,
            themeColor(data.getColorTheme(), "primary400", "#34d399"),
            themeColor(data.getColorTheme(), "primary500", "#059669"),
            themeColor(data.getColorTheme(), "primary600", "#047857"),
            themeColor(data.getColorTheme(), "primary700", "#065f46")
        );
    }

    // ── Rich, interactive version — for iframe embeds on websites you control ──

    private String buildEmbedHtml(CardData c, String username) {
        String fullName = htmlEscape(c.fullName());
        String title = htmlEscape(c.title());
        String location = htmlEscape(c.location());

        String themeVars = String.format(
            "--primary400:%s;--primary500:%s;--primary600:%s;--primary700:%s;",
            c.primary400(), c.primary500(), c.primary600(), c.primary700());

        String imageHtml;
        if (c.imageUrl() != null && !c.imageUrl().isBlank()) {
            imageHtml = String.format("<img src=\"%s\" alt=\"%s\" />", c.imageUrl(), fullName);
        } else {
            String initial = fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
            imageHtml = String.format("<div class=\"avatar-fallback\">%s</div>", initial);
        }

        StringBuilder metaHtml = new StringBuilder();
        if (!location.isEmpty()) {
            metaHtml.append(String.format(
                "<span class=\"location\"><svg width=\"11\" height=\"11\" viewBox=\"0 0 24 24\" fill=\"none\" "
                + "stroke=\"currentColor\" stroke-width=\"2.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\">"
                + "<path d=\"M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z\"></path><circle cx=\"12\" cy=\"10\" r=\"3\">"
                + "</circle></svg>%s</span>", location));
        }
        if (c.openToWork()) {
            metaHtml.append(
                "<span class=\"badge\"><span class=\"pulse-wrap\"><span class=\"pulse-ring\"></span>"
                + "<span class=\"pulse-dot\"></span></span>Open to Work</span>");
        }

        String bioHtml = c.bioPlain().isEmpty() ? "" : String.format("<div class=\"bio\">%s</div>", htmlEscape(c.bioPlain()));

        StringBuilder skillsHtml = new StringBuilder();
        if (!c.skills().isEmpty()) {
            skillsHtml.append("<div><div class=\"section-label\">Skills</div><div class=\"skills\">");
            for (String skill : c.skills()) {
                skillsHtml.append(String.format("<span class=\"skill-chip\">%s</span>", htmlEscape(skill)));
            }
            skillsHtml.append("</div></div>");
        }

        StringBuilder socialHtml = new StringBuilder();
        if (c.githubUrl() != null) {
            socialHtml.append(String.format(
                "<a class=\"social-btn\" href=\"%s\" target=\"_blank\" rel=\"noopener\" title=\"GitHub\">"
                + "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"currentColor\"><path d=\"%s\"/></svg></a>",
                htmlEscape(c.githubUrl()), GITHUB_ICON_PATH));
        }
        if (c.linkedinUrl() != null) {
            socialHtml.append(String.format(
                "<a class=\"social-btn\" href=\"%s\" target=\"_blank\" rel=\"noopener\" title=\"LinkedIn\">"
                + "<svg width=\"14\" height=\"14\" viewBox=\"0 0 24 24\" fill=\"currentColor\"><path d=\"%s\"/></svg></a>",
                htmlEscape(c.linkedinUrl()), LINKEDIN_ICON_PATH));
        }
        if (c.websiteUrl() != null) {
            socialHtml.append(String.format(
                "<a class=\"social-btn\" href=\"%s\" target=\"_blank\" rel=\"noopener\" title=\"Website\">"
                + "<svg width=\"15\" height=\"15\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" "
                + "stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\"><circle cx=\"12\" cy=\"12\" r=\"10\">"
                + "</circle><line x1=\"2\" y1=\"12\" x2=\"22\" y2=\"12\"></line><path d=\"M12 2a15.3 15.3 0 0 1 4 10 "
                + "15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z\"></path></svg></a>",
                htmlEscape(c.websiteUrl())));
        }

        String template = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>%s &#8212; Portfolio Card</title>
                  <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body {
                      width: 420px; height: 400px; overflow: hidden;
                      font-family: -apple-system, "Segoe UI", Roboto, Arial, sans-serif;
                      background: transparent;
                    }
                    .card {
                      position: relative;
                      width: 420px; height: 400px;
                      background: #ffffff;
                      box-shadow: 0 4px 28px rgba(15,23,42,0.12);
                      border-radius: 18px;
                      border: 1px solid rgba(15,23,42,0.06);
                      display: flex; flex-direction: column;
                      overflow: hidden;
                    }
                    .glow {
                      position: absolute; top: -60px; right: -60px;
                      width: 220px; height: 220px; border-radius: 50%%;
                      background: radial-gradient(circle, var(--primary500), transparent 70%%);
                      opacity: 0.14; pointer-events: none;
                    }
                    .accent-bar {
                      height: 4px; flex-shrink: 0;
                      background: linear-gradient(90deg, var(--primary500), var(--primary700));
                    }
                    .card-body {
                      position: relative;
                      flex: 1; min-height: 0;
                      padding: 22px 24px 18px 24px;
                      display: flex; flex-direction: column;
                    }
                    .content-stack {
                      flex: 1; min-height: 0;
                      display: flex; flex-direction: column;
                      justify-content: center;
                      gap: 16px;
                    }
                    .header { display: flex; align-items: flex-start; gap: 14px; }
                    .avatar-wrap {
                      width: 68px; height: 68px; border-radius: 50%%; flex-shrink: 0;
                      padding: 2.5px;
                      background: linear-gradient(135deg, var(--primary400), var(--primary600));
                    }
                    .avatar-wrap img, .avatar-fallback {
                      width: 100%%; height: 100%%; border-radius: 50%%;
                      display: flex; object-fit: cover;
                      border: 2.5px solid #fff;
                    }
                    .avatar-fallback {
                      align-items: center; justify-content: center;
                      font-size: 26px; font-weight: 700; color: #fff;
                      background: linear-gradient(135deg, var(--primary400), var(--primary600));
                    }
                    .info { flex: 1; min-width: 0; padding-top: 2px; }
                    .name {
                      font-size: 18px; font-weight: 800; color: #0f172a; letter-spacing: -0.01em;
                      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
                    }
                    .headline {
                      font-size: 13px; color: #64748b; margin-top: 1px; font-weight: 500;
                      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
                    }
                    .meta-row { display: flex; align-items: center; gap: 10px; margin-top: 7px; flex-wrap: wrap; }
                    .location {
                      display: inline-flex; align-items: center; gap: 4px;
                      color: #94a3b8; font-size: 11.5px; font-weight: 500;
                    }
                    .badge {
                      display: inline-flex; align-items: center; gap: 5px;
                      background: #ecfdf5; color: #047857;
                      font-size: 11px; font-weight: 700; padding: 3px 10px 3px 8px; border-radius: 50px;
                    }
                    .pulse-wrap { position: relative; display: inline-flex; width: 7px; height: 7px; }
                    .pulse-dot { position: absolute; inset: 0; border-radius: 50%%; background: #10b981; }
                    .pulse-ring {
                      position: absolute; inset: 0; border-radius: 50%%; background: #10b981;
                      animation: embedPulse 1.8s ease-out infinite;
                    }
                    @keyframes embedPulse {
                      0%% { transform: scale(1); opacity: 0.6; }
                      100%% { transform: scale(2.6); opacity: 0; }
                    }
                    .bio { font-size: 12.5px; line-height: 1.55; color: #475569; }
                    .section-label {
                      font-size: 10.5px; font-weight: 700; color: #94a3b8;
                      text-transform: uppercase; letter-spacing: 0.06em;
                      margin-bottom: 8px;
                    }
                    .skills { display: flex; flex-wrap: wrap; gap: 6px; }
                    .skill-chip {
                      display: inline-flex; align-items: center;
                      background: #f8fafc; border: 1px solid #eef2f7;
                      color: #334155; font-size: 11.5px; font-weight: 600;
                      padding: 4px 11px; border-radius: 50px; white-space: nowrap;
                    }
                    .footer-row {
                      margin-top: auto; padding-top: 14px;
                      border-top: 1px solid #f1f5f9;
                      display: flex; justify-content: space-between; align-items: center;
                    }
                    .social-icons { display: flex; align-items: center; gap: 6px; }
                    .social-btn {
                      width: 30px; height: 30px; border-radius: 50%%;
                      background: #f8fafc; border: 1px solid #eef2f7;
                      display: flex; align-items: center; justify-content: center;
                      color: #64748b; text-decoration: none;
                    }
                    .view-btn {
                      display: inline-flex; align-items: center; gap: 6px;
                      background: linear-gradient(135deg, var(--primary500), var(--primary700));
                      color: #fff; font-size: 12.5px; font-weight: 700;
                      padding: 8px 16px; border-radius: 50px;
                      text-decoration: none; white-space: nowrap;
                    }
                    .powered {
                      font-size: 9.5px; color: #cbd5e1; text-align: center;
                      margin-top: 10px; font-weight: 500;
                    }
                  </style>
                </head>
                <body>
                  <div class="card" style="%s">
                    <div class="glow"></div>
                    <div class="accent-bar"></div>
                    <div class="card-body">
                      <div class="content-stack">
                        <div class="header">
                          <div class="avatar-wrap">%s</div>
                          <div class="info">
                            <div class="name">%s</div>
                            <div class="headline">%s</div>
                            <div class="meta-row">%s</div>
                          </div>
                        </div>
                        %s
                        %s
                      </div>
                      <div class="footer-row">
                        <div class="social-icons">%s</div>
                        <a href="%s" target="_blank" rel="noopener" class="view-btn">View Portfolio
                          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
                        </a>
                      </div>
                      <div class="powered">Powered by PortfoliosBuilder</div>
                    </div>
                  </div>
                </body>
                </html>
                """;

        return String.format(template,
            fullName, themeVars, imageHtml, fullName, title, metaHtml, bioHtml, skillsHtml, socialHtml, c.portfolioUrl()
        );
    }

    // ── Email-safe version — table layout, inline styles only, no <style>/SVG/CSS ──
    // vars, so pasting this into Gmail/Outlook compose (or any rich-text editor)
    // keeps the layout and every link clickable instead of being stripped/broken.

    private String buildEmailSafeHtml(CardData c, String username) {
        String fullName = htmlEscape(c.fullName());
        String title = htmlEscape(c.title());
        String location = htmlEscape(c.location());
        String primary600 = c.primary600();
        String primary700 = c.primary700();

        String avatarHtml;
        if (c.imageUrl() != null && !c.imageUrl().isBlank()) {
            avatarHtml = String.format(
                "<img src=\"%s\" width=\"60\" height=\"60\" alt=\"%s\" "
                + "style=\"border-radius:50%%;display:block;object-fit:cover;border:2px solid #ffffff;\" />",
                c.imageUrl(), fullName);
        } else {
            String initial = fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
            avatarHtml = String.format(
                "<table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" width=\"60\" height=\"60\" "
                + "style=\"background:%s;border-radius:50%%;\"><tr><td align=\"center\" valign=\"middle\" "
                + "style=\"font-size:24px;font-weight:700;color:#ffffff;font-family:Arial,sans-serif;\">%s</td></tr></table>",
                primary600, initial);
        }

        String badgeHtml = c.openToWork()
            ? "&nbsp;&nbsp;<span style=\"background:#ecfdf5;color:#047857;font-size:11px;font-weight:700;"
              + "padding:3px 9px;border-radius:20px;\">&#9679;&nbsp;Open to Work</span>"
            : "";
        String locationHtml = location.isEmpty() ? "" : "&#128205;&nbsp;" + location;

        String bioHtml = c.bioPlain().isEmpty() ? "" : String.format(
            "<tr><td colspan=\"2\" style=\"padding-top:14px;font-size:12.5px;line-height:1.55;color:#475569;font-family:Arial,sans-serif;\">%s</td></tr>",
            htmlEscape(c.bioPlain()));

        StringBuilder skillsHtml = new StringBuilder();
        if (!c.skills().isEmpty()) {
            StringBuilder chips = new StringBuilder();
            for (String skill : c.skills()) {
                chips.append(String.format(
                    "<span style=\"display:inline-block;background:#f8fafc;border:1px solid #eef2f7;color:#334155;"
                    + "font-size:11.5px;font-weight:600;padding:4px 11px;border-radius:20px;margin:0 6px 6px 0;"
                    + "font-family:Arial,sans-serif;\">%s</span>",
                    htmlEscape(skill)));
            }
            skillsHtml.append(String.format(
                "<tr><td colspan=\"2\" style=\"padding-top:16px;\">"
                + "<div style=\"font-size:10.5px;font-weight:700;color:#94a3b8;text-transform:uppercase;"
                + "letter-spacing:1px;margin-bottom:8px;font-family:Arial,sans-serif;\">Skills</div>"
                + "<div>%s</div></td></tr>", chips));
        }

        StringBuilder linksHtml = new StringBuilder();
        if (c.githubUrl() != null) {
            linksHtml.append(String.format(
                "<a href=\"%s\" style=\"color:#64748b;text-decoration:none;font-size:12px;font-family:Arial,sans-serif;\">GitHub</a>",
                htmlEscape(c.githubUrl())));
        }
        if (c.linkedinUrl() != null) {
            if (linksHtml.length() > 0) linksHtml.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            linksHtml.append(String.format(
                "<a href=\"%s\" style=\"color:#64748b;text-decoration:none;font-size:12px;font-family:Arial,sans-serif;\">LinkedIn</a>",
                htmlEscape(c.linkedinUrl())));
        }
        if (c.websiteUrl() != null) {
            if (linksHtml.length() > 0) linksHtml.append("&nbsp;&nbsp;|&nbsp;&nbsp;");
            linksHtml.append(String.format(
                "<a href=\"%s\" style=\"color:#64748b;text-decoration:none;font-size:12px;font-family:Arial,sans-serif;\">Website</a>",
                htmlEscape(c.websiteUrl())));
        }

        String templ = """
                <table role="presentation" cellpadding="0" cellspacing="0" width="440" style="max-width:440px;background:#ffffff;border:1px solid #e2e8f0;border-radius:16px;font-family:Arial,sans-serif;">
                  <tr><td style="height:4px;background:%s;font-size:0;line-height:0;border-radius:16px 16px 0 0;">&nbsp;</td></tr>
                  <tr><td style="padding:22px 24px 18px 24px;">
                    <table role="presentation" cellpadding="0" cellspacing="0" width="100%%">
                      <tr>
                        <td width="60" valign="top">%s</td>
                        <td style="padding-left:14px;" valign="top">
                          <div style="font-size:18px;font-weight:800;color:#0f172a;font-family:Arial,sans-serif;">%s</div>
                          <div style="font-size:13px;color:#64748b;margin-top:2px;font-family:Arial,sans-serif;">%s</div>
                          <div style="font-size:11.5px;color:#94a3b8;margin-top:6px;font-family:Arial,sans-serif;">%s%s</div>
                        </td>
                      </tr>
                      %s
                      %s
                      <tr><td colspan="2" style="padding-top:16px;border-top:1px solid #f1f5f9;margin-top:16px;">&nbsp;</td></tr>
                      <tr><td colspan="2">
                        <table role="presentation" cellpadding="0" cellspacing="0" width="100%%"><tr>
                          <td valign="middle">%s</td>
                          <td align="right" valign="middle">
                            <a href="%s" style="display:inline-block;background:%s;color:#ffffff;text-decoration:none;font-weight:700;font-size:12.5px;padding:9px 18px;border-radius:20px;font-family:Arial,sans-serif;">View Portfolio &#8594;</a>
                          </td>
                        </tr></table>
                      </td></tr>
                      <tr><td colspan="2" style="padding-top:10px;text-align:center;font-size:9.5px;color:#cbd5e1;font-family:Arial,sans-serif;">Powered by PortfoliosBuilder</td></tr>
                    </table>
                  </td></tr>
                </table>
                """;

        return String.format(templ,
            "linear-gradient(90deg," + primary600 + "," + primary700 + ")",
            avatarHtml, fullName, title, locationHtml, badgeHtml,
            bioHtml, skillsHtml, linksHtml, c.portfolioUrl(), primary600
        );
    }

    private String themeColor(ColorThemeResponseDTO colorTheme, String colorName, String fallback) {
        if (colorTheme == null || colorTheme.getPalette() == null || colorTheme.getPalette().getColorGroups() == null) {
            return fallback;
        }
        for (ColorGroupDTO group : colorTheme.getPalette().getColorGroups()) {
            if (!"primary".equals(group.getGroupName()) || group.getColorShades() == null) continue;
            for (ColorShadeDTO shade : group.getColorShades()) {
                if (colorName.equals(shade.getColorName()) && shade.getColorCode() != null && !shade.getColorCode().isBlank()) {
                    return shade.getColorCode();
                }
            }
        }
        return fallback;
    }

    private String findActiveSocialUrl(List<SocialLinkResponseDTO> links, PlatformEnum platform) {
        if (links == null) return null;
        return links.stream()
            .filter(l -> platform.equals(l.getPlatform()) && StatusEnum.ACTIVE.equals(l.getStatus()))
            .map(SocialLinkResponseDTO::getUrl)
            .findFirst()
            .orElse(null);
    }

    private String htmlEscape(String input) {
        if (input == null) return "";
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
}
