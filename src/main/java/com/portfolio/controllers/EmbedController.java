package com.portfolio.controllers;

import com.portfolio.dao.profile.ProfileDao;
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

    private final ProfileDao profileDao;
    private final ProfileMasterService profileMasterService;

    @GetMapping("/embed/{username}")
    public ResponseEntity<String> getEmbedWidget(@PathVariable String username) {
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

            String html = buildEmbedHtml(data, username);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("text/html;charset=UTF-8"));
            headers.add("X-Frame-Options", "ALLOWALL");
            headers.add("Content-Security-Policy", "frame-ancestors *");
            return ResponseEntity.ok().headers(headers).body(html);

        } catch (Exception e) {
            log.warn("Embed widget error for {}: {}", username, e.getMessage());
            return ResponseEntity.notFound().<String>build();
        }
    }

    private String buildEmbedHtml(ProfileMasterResponse data, String username) {
        ProfileResponse p = data.getProfile();

        String fullName = p.getFullName() != null ? htmlEscape(p.getFullName()) : "";
        String title    = p.getTitle()    != null ? htmlEscape(p.getTitle())    : "";
        String location = p.getLocation() != null ? htmlEscape(p.getLocation()) : "";
        String imageUrl = p.getProfileImageUrl();
        boolean openToWork = p.isAvailableForWork();

        // --- Avatar: image or coloured initial circle ---
        String imageHtml;
        if (imageUrl != null && !imageUrl.isBlank()) {
            imageHtml = String.format(
                "<img src=\"%s\" alt=\"%s\" "
                + "style=\"width:60px;height:60px;border-radius:50%%;object-fit:cover;\" />",
                imageUrl, fullName);
        } else {
            String initial = fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
            imageHtml = String.format(
                "<div style=\"width:60px;height:60px;border-radius:50%%;background:#3b82f6;"
                + "display:flex;align-items:center;justify-content:center;"
                + "font-size:24px;font-weight:700;color:#fff;\">%s</div>",
                initial);
        }

        // --- Availability badge ---
        String badgeHtml = openToWork
            ? "<span style=\"display:inline-block;background:#d1fae5;color:#065f46;"
              + "font-size:11px;font-weight:600;padding:3px 10px;border-radius:50px;"
              + "margin-top:6px;\">&#x25cf;&nbsp;Open to Work</span>"
            : "";

        // --- Location row ---
        String locationHtml = location.isEmpty() ? "" : String.format(
            "<div style=\"color:#6b7280;font-size:12px;margin-top:4px;\">&#128205;&nbsp;%s</div>",
            location);

        // --- Skills chips (first 6 with a name) ---
        List<SkillResponse> skills = data.getSkills();
        StringBuilder skillsHtml = new StringBuilder();
        if (skills != null && !skills.isEmpty()) {
            List<SkillResponse> named = skills.stream()
                .filter(s -> s.getLogoName() != null && !s.getLogoName().isBlank())
                .limit(6)
                .toList();
            if (!named.isEmpty()) {
                skillsHtml.append("<div style=\"margin-top:14px;\">");
                skillsHtml.append("<div style=\"font-size:11px;font-weight:600;color:#6b7280;"
                    + "margin-bottom:6px;text-transform:uppercase;letter-spacing:0.5px;\">Skills</div>");
                skillsHtml.append("<div style=\"display:flex;flex-wrap:wrap;gap:6px;\">");
                for (SkillResponse skill : named) {
                    skillsHtml.append(String.format(
                        "<span style=\"background:#eff6ff;color:#3b82f6;font-size:11px;"
                        + "font-weight:500;padding:3px 10px;border-radius:50px;"
                        + "white-space:nowrap;\">%s</span>",
                        htmlEscape(skill.getLogoName())));
                }
                skillsHtml.append("</div></div>");
            }
        }

        // --- Social icon links ---
        List<SocialLinkResponseDTO> links = data.getSocialLinks();
        String githubUrl   = findActiveSocialUrl(links, PlatformEnum.GITHUB);
        String linkedinUrl = findActiveSocialUrl(links, PlatformEnum.LINKEDIN);
        String websiteUrl  = findActiveSocialUrl(links, PlatformEnum.WEBSITE);
        if (websiteUrl == null) {
            websiteUrl = findActiveSocialUrl(links, PlatformEnum.PORTFOLIO);
        }

        StringBuilder socialHtml = new StringBuilder();
        if (githubUrl != null) {
            socialHtml.append(String.format(
                "<a href=\"%s\" target=\"_blank\" rel=\"noopener\""
                + " style=\"color:#6b7280;text-decoration:none;font-size:12px;\""
                + " title=\"GitHub\">&#128296;&nbsp;GitHub</a>",
                htmlEscape(githubUrl)));
        }
        if (linkedinUrl != null) {
            if (socialHtml.length() > 0) socialHtml.append(
                "<span style=\"color:#e5e7eb;margin:0 4px;\">|</span>");
            socialHtml.append(String.format(
                "<a href=\"%s\" target=\"_blank\" rel=\"noopener\""
                + " style=\"color:#6b7280;text-decoration:none;font-size:12px;\""
                + " title=\"LinkedIn\">&#128101;&nbsp;LinkedIn</a>",
                htmlEscape(linkedinUrl)));
        }
        if (websiteUrl != null) {
            if (socialHtml.length() > 0) socialHtml.append(
                "<span style=\"color:#e5e7eb;margin:0 4px;\">|</span>");
            socialHtml.append(String.format(
                "<a href=\"%s\" target=\"_blank\" rel=\"noopener\""
                + " style=\"color:#6b7280;text-decoration:none;font-size:12px;\""
                + " title=\"Website\">&#127760;&nbsp;Website</a>",
                htmlEscape(websiteUrl)));
        }

        String portfolioUrl = "https://portfoliosbuilder.com/" + username;

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
                      width: 420px; height: 460px; overflow: hidden;
                      font-family: "Segoe UI", Arial, sans-serif;
                      background: #ffffff; color: #1a1a2e;
                    }
                    .card {
                      width: 420px; height: 460px;
                      background: #ffffff;
                      box-shadow: 0 4px 24px rgba(0,0,0,0.10);
                      border-radius: 16px;
                      padding: 24px 24px 16px 24px;
                      display: flex; flex-direction: column;
                      overflow: hidden;
                    }
                    .header { display: flex; align-items: flex-start; gap: 16px; }
                    .info { flex: 1; min-width: 0; }
                    .name {
                      font-size: 18px; font-weight: 700; color: #1a1a2e;
                      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
                    }
                    .headline {
                      font-size: 13px; color: #6b7280; margin-top: 2px;
                      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
                    }
                    .footer-row {
                      margin-top: auto; padding-top: 10px;
                      border-top: 1px solid #f3f4f6;
                      display: flex; justify-content: space-between;
                      align-items: center; flex-wrap: wrap; gap: 6px;
                    }
                    .view-btn {
                      background: #3b82f6; color: #fff; font-size: 12px;
                      font-weight: 600; padding: 6px 14px; border-radius: 50px;
                      text-decoration: none; white-space: nowrap;
                    }
                    .powered { font-size: 10px; color: #d1d5db; text-align: center; margin-top: 8px; }
                  </style>
                </head>
                <body>
                  <div class="card">
                    <div class="header">
                      <div class="avatar">%s</div>
                      <div class="info">
                        <div class="name">%s</div>
                        <div class="headline">%s</div>
                        %s
                        %s
                      </div>
                    </div>
                    %s
                    <div class="footer-row">
                      <div style="display:flex;align-items:center;gap:6px;flex-wrap:wrap;">%s</div>
                      <a href="%s" target="_blank" rel="noopener" class="view-btn">View Portfolio &#8594;</a>
                    </div>
                    <div class="powered">Powered by PortfoliosBuilder</div>
                  </div>
                </body>
                </html>
                """;

        return String.format(template,
            fullName,        // <title>
            imageHtml,       // avatar
            fullName,        // .name
            title,           // .headline
            badgeHtml,       // open-to-work badge
            locationHtml,    // location row
            skillsHtml,      // skills section
            socialHtml,      // social links
            portfolioUrl     // view portfolio href
        );
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
