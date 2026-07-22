package com.portfolio.servicesImpl;

import com.portfolio.dtos.Search.SearchResultDTO;
import com.portfolio.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final int LIMIT_PER_MODULE = 5;
    private static final int LIMIT_TOTAL = 30;

    private record ModuleConfig(String module, String table, String titleExpr, String snippetExpr, String[] searchCols, String path) {
    }

    private static final String STRIP_TAGS = "regexp_replace(COALESCE(%s, ''), '<[^>]+>', '', 'g')";

    private static final List<ModuleConfig> MODULES = List.of(
            new ModuleConfig("skill", "skills", "logo_name",
                    "COALESCE(category, '')", new String[]{"logo_name", "category"}, "/skills"),
            new ModuleConfig("project", "projects", "project_name",
                    "LEFT(" + STRIP_TAGS.formatted("project_description") + ", 160)", new String[]{"project_name", "project_description"}, "/projects"),
            new ModuleConfig("experience", "experiences",
                    "COALESCE(job_title, '') || CASE WHEN company_name IS NOT NULL THEN ' @ ' || company_name ELSE '' END",
                    "LEFT(" + STRIP_TAGS.formatted("description") + ", 160)", new String[]{"job_title", "company_name", "description"}, "/experience"),
            new ModuleConfig("education", "educations", "institution",
                    "COALESCE(degree, '')", new String[]{"institution", "degree", "field_of_study"}, "/education"),
            new ModuleConfig("certification", "certifications", "title",
                    "COALESCE(issuer, '')", new String[]{"title", "issuer"}, "/certifications"),
            new ModuleConfig("publication", "publications", "title",
                    "COALESCE(publisher, '')", new String[]{"title", "publisher", "description"}, "/publications"),
            new ModuleConfig("achievement", "achievements", "title",
                    "COALESCE(issuer, '')", new String[]{"title", "issuer", "description"}, "/achievements"),
            new ModuleConfig("service", "services", "title",
                    "LEFT(" + STRIP_TAGS.formatted("description") + ", 160)", new String[]{"title", "description"}, "/services"),
            new ModuleConfig("testimonial", "testimonials", "name",
                    "LEFT(" + STRIP_TAGS.formatted("message") + ", 160)", new String[]{"name", "company", "message"}, "/testimonials"),
            new ModuleConfig("blogPost", "blog_posts", "title",
                    "COALESCE(excerpt, '')", new String[]{"title", "excerpt", "content"}, "/blogs")
    );

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<SearchResultDTO> search(Long profileId, String query) {
        if (query == null || query.isBlank()) return List.of();

        String like = "%" + query.trim() + "%";
        List<String> unionParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        for (ModuleConfig m : MODULES) {
            String searchClause = String.join(" OR ",
                    java.util.Arrays.stream(m.searchCols()).map(c -> c + " ILIKE ?").toList());
            unionParts.add("""
                    (SELECT '%s' AS module, id, %s AS title, %s AS snippet
                     FROM %s
                     WHERE profile_id = ? AND (%s)
                     ORDER BY created_at DESC
                     LIMIT %d)
                    """.formatted(m.module(), m.titleExpr(), m.snippetExpr(), m.table(), searchClause, LIMIT_PER_MODULE));
            params.add(profileId);
            for (int i = 0; i < m.searchCols().length; i++) params.add(like);
        }

        String sql = "SELECT * FROM (" + String.join(" UNION ALL ", unionParts) + ") t ORDER BY module LIMIT " + LIMIT_TOTAL;

        java.util.Map<String, String> pathByModule = MODULES.stream()
                .collect(java.util.stream.Collectors.toMap(ModuleConfig::module, ModuleConfig::path));

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String module = rs.getString("module");
            String title = rs.getString("title");
            return SearchResultDTO.builder()
                    .module(module)
                    .id(rs.getLong("id"))
                    .title(title)
                    .snippet(rs.getString("snippet"))
                    .path(pathByModule.get(module) + "?search=" + java.net.URLEncoder.encode(title == null ? "" : title, java.nio.charset.StandardCharsets.UTF_8))
                    .build();
        }, params.toArray());
    }
}
