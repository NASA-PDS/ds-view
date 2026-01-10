// Copyright 2012-2025, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.

package gov.nasa.pds.dsview.util;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for HtmlUtils XSS protection utilities.
 *
 * @author PDS Engineering
 */
public class HtmlUtilsTest {

    // ========== escapeHtml() Tests ==========

    @Test
    public void testEscapeHtml_ScriptTag() {
        String input = "<script>alert('xss')</script>";
        String result = HtmlUtils.escapeHtml(input);
        assertFalse("Script tag should be escaped", result.contains("<script>"));
        assertTrue("Should contain escaped opening tag", result.contains("&lt;script&gt;"));
    }

    @Test
    public void testEscapeHtml_ImgTag() {
        String input = "<img src=x onerror=alert(1)>";
        String result = HtmlUtils.escapeHtml(input);
        assertFalse("Img tag should be escaped", result.contains("<img"));
        assertTrue("Should contain escaped opening bracket", result.contains("&lt;img"));
    }

    @Test
    public void testEscapeHtml_Quotes() {
        String input = "John\" onclick=\"alert(1)";
        String result = HtmlUtils.escapeHtml(input);
        assertTrue("Quotes should be escaped", result.contains("&quot;"));
        assertFalse("Should not contain raw quotes", result.contains("\" onclick=\""));
    }

    @Test
    public void testEscapeHtml_SingleQuote() {
        String input = "O'Brien";
        String result = HtmlUtils.escapeHtml(input);
        // Note: escapeHtml4() doesn't escape single quotes in text content, only double quotes
        // This is safe because single quotes don't break HTML text, and we use double quotes for attributes
        assertEquals("Single quote in text content doesn't need escaping", "O'Brien", result);
    }

    @Test
    public void testEscapeHtml_Ampersand() {
        String input = "Smith & Jones";
        String result = HtmlUtils.escapeHtml(input);
        assertTrue("Ampersand should be escaped", result.contains("&amp;"));
    }

    @Test
    public void testEscapeHtml_Null() {
        assertEquals("Null should return empty string", "", HtmlUtils.escapeHtml(null));
    }

    @Test
    public void testEscapeHtml_EmptyString() {
        assertEquals("Empty string should remain empty", "", HtmlUtils.escapeHtml(""));
    }

    @Test
    public void testEscapeHtml_NormalText() {
        String input = "John Doe";
        String result = HtmlUtils.escapeHtml(input);
        assertEquals("Normal text should not be modified", input, result);
    }

    // ========== sanitizeUrl() Tests ==========

    @Test
    public void testSanitizeUrl_ValidHttpsDoi() {
        String url = "https://doi.org/10.1234/test";
        assertEquals("Valid HTTPS DOI URL should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_ValidHttpOrcid() {
        String url = "http://orcid.org/0000-0001-2345-6789";
        assertEquals("Valid HTTP ORCID URL should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_ValidHttpsRor() {
        String url = "https://ror.org/12345";
        assertEquals("Valid ROR URL should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_ValidPdsNasa() {
        String url = "https://pds.nasa.gov/datasearch";
        assertEquals("Valid PDS NASA URL should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_JavascriptProtocol() {
        assertEquals("JavaScript protocol should be rejected", "", HtmlUtils.sanitizeUrl("javascript:alert(1)"));
    }

    @Test
    public void testSanitizeUrl_DataProtocol() {
        assertEquals("Data protocol should be rejected", "",
            HtmlUtils.sanitizeUrl("data:text/html,<script>alert(1)</script>"));
    }

    @Test
    public void testSanitizeUrl_VbscriptProtocol() {
        assertEquals("VBScript protocol should be rejected", "", HtmlUtils.sanitizeUrl("vbscript:alert(1)"));
    }

    @Test
    public void testSanitizeUrl_FileProtocol() {
        assertEquals("File protocol should be rejected", "", HtmlUtils.sanitizeUrl("file:///etc/passwd"));
    }

    @Test
    public void testSanitizeUrl_DisallowedDomain() {
        assertEquals("Non-whitelisted domain should be rejected", "",
            HtmlUtils.sanitizeUrl("https://evil.com/malware"));
    }

    @Test
    public void testSanitizeUrl_Subdomain() {
        String url = "https://www.orcid.org/test";
        assertEquals("Subdomain of whitelisted domain should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_SubdomainDoi() {
        String url = "https://api.doi.org/endpoint";
        assertEquals("API subdomain should pass", url, HtmlUtils.sanitizeUrl(url));
    }

    @Test
    public void testSanitizeUrl_Null() {
        assertEquals("Null URL should return empty string", "", HtmlUtils.sanitizeUrl(null));
    }

    @Test
    public void testSanitizeUrl_Empty() {
        assertEquals("Empty URL should return empty string", "", HtmlUtils.sanitizeUrl(""));
    }

    @Test
    public void testSanitizeUrl_Whitespace() {
        assertEquals("Whitespace-only URL should return empty string", "", HtmlUtils.sanitizeUrl("   "));
    }

    @Test
    public void testSanitizeUrl_MalformedUrl() {
        assertEquals("Malformed URL should return empty string", "", HtmlUtils.sanitizeUrl("not a url"));
    }

    // ========== buildLink() Tests ==========

    @Test
    public void testBuildLink_ValidUrl() {
        String result = HtmlUtils.buildLink("https://orcid.org/0000-0001-2345-6789", "John Doe", "_blank");
        assertTrue("Should contain opening a tag", result.contains("<a href=\""));
        assertTrue("Should contain ORCID URL", result.contains("https://orcid.org"));
        assertTrue("Should contain target attribute", result.contains("target=\"_blank\""));
        assertTrue("Should contain link text", result.contains(">John Doe</a>"));
    }

    @Test
    public void testBuildLink_InvalidUrl_ReturnsTextOnly() {
        String result = HtmlUtils.buildLink("javascript:alert(1)", "Click me", null);
        assertEquals("Invalid URL should return just escaped text", "Click me", result);
        assertFalse("Should not contain anchor tag", result.contains("<a"));
    }

    @Test
    public void testBuildLink_EscapesText() {
        String result = HtmlUtils.buildLink("https://doi.org/test", "<script>alert(1)</script>", null);
        assertFalse("Script tag should be escaped", result.contains("<script>"));
        assertTrue("Should contain escaped script tag", result.contains("&lt;script&gt;"));
    }

    @Test
    public void testBuildLink_EscapesUrlInAttribute() {
        String result = HtmlUtils.buildLink("https://doi.org/test<script>", "Text", null);
        // URL with script tag should be escaped in href attribute
        if (result.contains("<a")) {
            assertTrue("URL should be escaped in href", result.contains("&lt;script&gt;"));
        }
    }

    @Test
    public void testBuildLink_NoTarget() {
        String result = HtmlUtils.buildLink("https://doi.org/test", "Link", null);
        assertTrue("Should contain anchor tag", result.contains("<a href="));
        assertFalse("Should not contain target attribute", result.contains("target="));
    }

    @Test
    public void testBuildLink_EmptyTarget() {
        String result = HtmlUtils.buildLink("https://doi.org/test", "Link", "");
        assertTrue("Should contain anchor tag", result.contains("<a href="));
        assertFalse("Should not contain target attribute", result.contains("target="));
    }

    @Test
    public void testBuildLink_NullUrl() {
        String result = HtmlUtils.buildLink(null, "Text", null);
        assertEquals("Null URL should return just text", "Text", result);
    }

    @Test
    public void testBuildLink_NullText() {
        String result = HtmlUtils.buildLink("https://doi.org/test", null, null);
        assertTrue("Should still create link", result.contains("<a href="));
        assertTrue("Should contain empty text", result.contains("></a>"));
    }

    @Test
    public void testBuildLink_QuotesInText() {
        String result = HtmlUtils.buildLink("https://doi.org/test", "Author \"Nickname\"", null);
        assertTrue("Quotes should be escaped", result.contains("&quot;"));
        assertFalse("Should not break out of tag", result.contains("\"Nickname\""));
    }

    @Test
    public void testBuildLink_AmpersandInText() {
        String result = HtmlUtils.buildLink("https://doi.org/test", "Smith & Jones", null);
        assertTrue("Ampersand should be escaped", result.contains("&amp;"));
    }
}
