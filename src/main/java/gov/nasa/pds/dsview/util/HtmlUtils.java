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

import org.apache.commons.text.StringEscapeUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * HTML output escaping utilities to prevent XSS vulnerabilities.
 *
 * This class provides methods for safely embedding untrusted data (e.g., from Solr,
 * user input, or external APIs) into HTML output. All text and URLs are validated
 * and escaped to prevent cross-site scripting (XSS) attacks.
 *
 * @author PDS Engineering
 */
public class HtmlUtils {

    private HtmlUtils() {
        // Utility class, no instantiation
    }

    // Whitelist of allowed URL domains for href attributes
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
        "doi.org",
        "orcid.org",
        "ror.org",
        "pds.nasa.gov"
    );

    /**
     * Escape text for safe inclusion in HTML content.
     *
     * Converts special characters to HTML entities:
     * <ul>
     * <li>&lt; becomes &amp;lt;</li>
     * <li>&gt; becomes &amp;gt;</li>
     * <li>&amp; becomes &amp;amp;</li>
     * <li>&quot; becomes &amp;quot;</li>
     * <li>&#39; becomes &amp;#39;</li>
     * </ul>
     *
     * @param text The text to escape (may be null)
     * @return HTML-escaped text, or empty string if input is null
     */
    public static String escapeHtml(String text) {
        return (text == null) ? "" : StringEscapeUtils.escapeHtml4(text);
    }

    /**
     * Validate and sanitize URL for use in href attributes.
     *
     * This method performs strict URL validation:
     * <ul>
     * <li>Only allows http and https protocols</li>
     * <li>Only allows whitelisted domains (doi.org, orcid.org, ror.org, pds.nasa.gov)</li>
     * <li>Rejects javascript:, data:, and other dangerous protocols</li>
     * <li>Rejects malformed URLs</li>
     * </ul>
     *
     * @param url The URL to validate (may be null)
     * @return The validated URL if safe, or empty string if invalid/unsafe
     */
    public static String sanitizeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return "";
        }

        try {
            URL parsed = new URL(url);
            String protocol = parsed.getProtocol().toLowerCase();
            String host = parsed.getHost().toLowerCase();

            // Only allow https/http protocols
            if (!protocol.equals("https") && !protocol.equals("http")) {
                return "";
            }

            // Check domain whitelist (allow subdomains)
            for (String allowedDomain : ALLOWED_DOMAINS) {
                if (host.equals(allowedDomain) || host.endsWith("." + allowedDomain)) {
                    return url; // URL is safe
                }
            }

            return ""; // Domain not in whitelist
        } catch (MalformedURLException e) {
            return ""; // Invalid URL
        }
    }

    /**
     * Build a safe HTML anchor tag with escaped text and validated URL.
     *
     * This method creates an HTML &lt;a&gt; tag with:
     * <ul>
     * <li>URL validated against whitelist (sanitizeUrl)</li>
     * <li>Link text HTML-escaped (escapeHtml)</li>
     * <li>Optional target attribute (e.g., "_blank")</li>
     * </ul>
     *
     * If the URL fails validation, only the escaped text is returned (no link).
     *
     * @param href The URL for the href attribute
     * @param text The link text to display
     * @param target Optional target attribute (e.g., "_blank"), may be null
     * @return Safe HTML anchor tag, or just escaped text if URL is invalid
     */
    public static String buildLink(String href, String text, String target) {
        String safeUrl = sanitizeUrl(href);
        if (safeUrl.isEmpty()) {
            // If URL is invalid, return just the escaped text (no link)
            return escapeHtml(text);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"").append(escapeHtml(safeUrl)).append("\"");

        if (target != null && !target.isEmpty()) {
            sb.append(" target=\"").append(escapeHtml(target)).append("\"");
        }

        sb.append(">").append(escapeHtml(text)).append("</a>");
        return sb.toString();
    }
}
