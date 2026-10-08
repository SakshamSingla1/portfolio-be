package com.portfolio.servicesImpl;

import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the "portfolio-export" Thymeleaf template and the full render pipeline
 * (Thymeleaf -> jsoup/W3C DOM -> openhtmltopdf) standalone, without Spring context, a
 * database, or Cloudinary -- this is the highest-risk surface of the PDF export rewrite
 * (a typo'd th:text/th:each property path only fails at render time, not at compile time).
 */
class PortfolioExportTemplateTest {

    // Matches what Spring Boot's Thymeleaf auto-configuration actually injects in production
    // (SpringTemplateEngine + SpringEL) -- a bare org.thymeleaf.TemplateEngine defaults to OGNL
    // for expression evaluation, which isn't a dependency of this project at all (never needed,
    // since the real app never uses it), so testing against it would both fail to run AND not
    // reflect how this template is actually evaluated at runtime.
    private SpringTemplateEngine newEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        return engine;
    }

    private Context fullContext() {
        Context context = new Context();
        context.setVariable("fullName", "Jane Doe");
        context.setVariable("css", "body { color: #111; }");
        context.setVariable("contactItems", List.of(
                new PortfolioExportServiceImpl.ContactItemVM("icon-phone", "+1 555-0100", null),
                new PortfolioExportServiceImpl.ContactItemVM("icon-envelope", "jane@example.com", null),
                new PortfolioExportServiceImpl.ContactItemVM("icon-linkedin", "LinkedIn", "https://linkedin.com/in/janedoe"),
                new PortfolioExportServiceImpl.ContactItemVM("icon-location", "Remote", null)
        ));
        context.setVariable("summaryHtml", "<p>Experienced engineer who ships things.</p>");
        context.setVariable("skillGroups", List.of(
                new PortfolioExportServiceImpl.SkillGroupVM("Languages", "Java, Python, TypeScript")
        ));
        context.setVariable("experiences", List.of(
                new PortfolioExportServiceImpl.ExperienceVM("Senior Engineer", "Jan 2022 – Present",
                        "Acme Corp — Remote", "<ul><li>Shipped the thing</li></ul>")
        ));
        context.setVariable("projects", List.of(
                new PortfolioExportServiceImpl.ProjectVM("Cool Project", "React, Node.js",
                        "<p>Built a cool project.</p>", "https://github.com/janedoe/cool-project")
        ));
        context.setVariable("educations", List.of(
                new PortfolioExportServiceImpl.EducationVM("State University", "2016 – 2020", "B.Sc in Computer Science", "3.8 GPA")
        ));
        context.setVariable("certifications", List.of(
                new PortfolioExportServiceImpl.CertificationVM("Certified Something", "Jun 2023", "Some Body", "https://cred.example/abc")
        ));
        context.setVariable("publications", List.of(
                new PortfolioExportServiceImpl.PublicationVM("A Paper (Conference)", "Mar 2021", "Some Journal", "<p>Abstract text.</p>")
        ));
        context.setVariable("achievements", List.of(
                new PortfolioExportServiceImpl.AchievementVM("Won a Hackathon", "Aug 2020", "<p>First place.</p>")
        ));
        context.setVariable("languagesLine", "English (Native), Spanish (Conversational)");
        context.setVariable("services", List.of(
                new PortfolioExportServiceImpl.ServiceVM("Consulting", "$100-150/hr", "<p>Available for consulting.</p>")
        ));
        return context;
    }

    private Context emptyContext() {
        Context context = new Context();
        context.setVariable("fullName", "John Smith");
        context.setVariable("css", "body { color: #111; }");
        context.setVariable("contactItems", List.of());
        context.setVariable("summaryHtml", null);
        context.setVariable("skillGroups", List.of());
        context.setVariable("experiences", List.of());
        context.setVariable("projects", List.of());
        context.setVariable("educations", List.of());
        context.setVariable("certifications", List.of());
        context.setVariable("publications", List.of());
        context.setVariable("achievements", List.of());
        context.setVariable("languagesLine", null);
        context.setVariable("services", List.of());
        return context;
    }

    @Test
    void rendersAllSectionsWithContent() {
        String html = newEngine().process("portfolio-export", fullContext());

        assertThat(html).contains("Jane Doe");
        assertThat(html).contains("jane@example.com");
        assertThat(html).contains("https://linkedin.com/in/janedoe");
        assertThat(html).contains("Experienced engineer who ships things.");
        assertThat(html).contains("Languages");
        assertThat(html).contains("Java, Python, TypeScript");
        assertThat(html).contains("Senior Engineer");
        assertThat(html).contains("Shipped the thing");
        assertThat(html).contains("Cool Project");
        assertThat(html).contains("State University");
        assertThat(html).contains("Certified Something");
        assertThat(html).contains("A Paper (Conference)");
        assertThat(html).contains("Won a Hackathon");
        assertThat(html).contains("English (Native), Spanish (Conversational)");
        assertThat(html).contains("Consulting");
    }

    @Test
    void rendersWithoutErrorWhenEverySectionIsEmpty() {
        String html = newEngine().process("portfolio-export", emptyContext());

        assertThat(html).contains("John Smith");
        // None of the optional section headings should appear.
        assertThat(html).doesNotContain("Professional Summary");
        assertThat(html).doesNotContain("section-heading\">Skills");
        assertThat(html).doesNotContain("Languages</div>");
    }

    @Test
    void renderedHtmlProducesAValidNonEmptyPdf() throws Exception {
        String html = newEngine().process("portfolio-export", fullContext());

        // Same pipeline as PortfolioExportServiceImpl.renderPdf: lenient jsoup parse -> strict
        // W3C DOM -> openhtmltopdf. If the Thymeleaf output isn't well-formed enough for this,
        // it throws here rather than silently producing a broken/empty PDF.
        Document jsoupDoc = Jsoup.parse(html);
        jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFont(() -> getClass().getResourceAsStream("/fonts/LiberationSerif-Regular.ttf"), "PortfolioSerif", 400, FontStyle.NORMAL, true);
        builder.withW3cDocument(w3cDoc, "");
        builder.toStream(baos);
        builder.run();

        byte[] pdf = baos.toByteArray();
        assertThat(pdf.length).isGreaterThan(500);
        // PDF file signature.
        assertThat(new String(pdf, 0, 5, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }
}
