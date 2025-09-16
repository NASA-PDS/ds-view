package gov.nasa.pds.dsview.registry;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.lang.reflect.Method;

/**
 * Unit tests for PDS4Search DOI selection logic.
 */
@RunWith(MockitoJUnitRunner.class)
public class PDS4SearchTest {

    private PDS4Search pds4Search;
    
    @Mock
    private Logger mockLogger;

    @Before
    public void setUp() {
        pds4Search = new PDS4Search("http://test-solr:8983/solr/data");
    }

    @Test
    public void testSelectBestMatchingDoi_ExactMatch() throws Exception {
        // Arrange
        JSONArray doiResponse = createDoiResponse(
            "lid::4.8", "doi-4.8",
            "lid::4.9", "doi-4.9", 
            "lid::4.10", "doi-4.10"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.9");
        
        // Assert
        assertEquals("doi-4.9", result);
    }

    @Test
    public void testSelectBestMatchingDoi_HighestVersionBelowTarget() throws Exception {
        // Arrange
        JSONArray doiResponse = createDoiResponse(
            "lid::4.8", "doi-4.8",
            "lid::4.9", "doi-4.9", 
            "lid::4.11", "doi-4.11"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.10");
        
        // Assert
        assertEquals("doi-4.9", result);
    }

    @Test
    public void testSelectBestMatchingDoi_NoSuitableVersion() throws Exception {
        // Arrange
        JSONArray doiResponse = createDoiResponse(
            "lid::4.11", "doi-4.11",
            "lid::4.12", "doi-4.12"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.10");
        
        // Assert
        assertNull(result);
    }

    @Test
    public void testSelectBestMatchingDoi_VersionComparison() throws Exception {
        // Arrange - Test that 4.10 > 4.9
        JSONArray doiResponse = createDoiResponse(
            "lid::4.8", "doi-4.8",
            "lid::4.9", "doi-4.9",
            "lid::4.10", "doi-4.10"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.10");
        
        // Assert
        assertEquals("doi-4.10", result);
    }

    @Test
    public void testSelectBestMatchingDoi_ComplexVersions() throws Exception {
        // Arrange - Test complex version numbers
        JSONArray doiResponse = createDoiResponse(
            "lid::4.1.2", "doi-4.1.2",
            "lid::4.1.10", "doi-4.1.10",
            "lid::4.2.0", "doi-4.2.0"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.1.15");
        
        // Assert - Should pick 4.1.10 (highest <= target)
        assertEquals("doi-4.1.10", result);
    }

    @Test
    public void testSelectBestMatchingDoi_EmptyResponse() throws Exception {
        // Arrange
        JSONArray doiResponse = new JSONArray();
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.10");
        
        // Assert
        assertNull(result);
    }

    @Test
    public void testSelectBestMatchingDoi_NullTargetVersion() throws Exception {
        // Arrange
        JSONArray doiResponse = createDoiResponse(
            "lid::4.8", "doi-4.8",
            "lid::4.9", "doi-4.9"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, null);
        
        // Assert - Should return first DOI when no target version specified
        assertEquals("doi-4.8", result);
    }

    @Test
    public void testSelectBestMatchingDoi_IdentifierWithoutVersion() throws Exception {
        // Arrange - Test case where identifier has no :: separator (just "lid")
        JSONArray doiResponse = createDoiResponse(
            "lid", "doi-no-version",
            "lid::4.8", "doi-4.8"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.9");
        
        // Assert - Should handle gracefully and return the versioned one
        assertEquals("doi-4.8", result);
    }

    @Test
    public void testSelectBestMatchingDoi_IdentifierWithEmptyVersion() throws Exception {
        // Arrange - Test case where identifier ends with :: (empty version)
        JSONArray doiResponse = createDoiResponse(
            "lid::", "doi-empty-version",
            "lid::4.8", "doi-4.8"
        );
        
        // Act
        String result = invokeSelectBestMatchingDoi(doiResponse, "4.9");
        
        // Assert - Should handle gracefully and return the versioned one
        assertEquals("doi-4.8", result);
    }

    @Test
    public void testCompareVersions_BasicComparison() throws Exception {
        // Test basic version comparison
        assertEquals(-1, invokeCompareVersions("4.8", "4.9"));
        assertEquals(0, invokeCompareVersions("4.9", "4.9"));
        assertEquals(1, invokeCompareVersions("4.10", "4.9"));
    }

    @Test
    public void testCompareVersions_ComplexVersions() throws Exception {
        // Test complex version comparison
        assertEquals(-1, invokeCompareVersions("4.9", "4.10"));
        assertEquals(1, invokeCompareVersions("4.10", "4.9"));
        assertEquals(-1, invokeCompareVersions("4.1.2", "4.1.10"));
        assertEquals(1, invokeCompareVersions("4.1.10", "4.1.2"));
    }

    @Test
    public void testCompareVersions_DifferentLengths() throws Exception {
        // Test versions with different number of parts
        assertEquals(-1, invokeCompareVersions("4.9", "4.9.1"));
        assertEquals(1, invokeCompareVersions("4.9.1", "4.9"));
        assertEquals(0, invokeCompareVersions("4.9.0", "4.9"));
    }

    // Helper methods

    private JSONArray createDoiResponse(String... identifierDoiPairs) throws JSONException {
        JSONArray response = new JSONArray();
        for (int i = 0; i < identifierDoiPairs.length; i += 2) {
            JSONObject doiObj = new JSONObject();
            doiObj.put("identifier", identifierDoiPairs[i]);
            doiObj.put("doi", identifierDoiPairs[i + 1]);
            response.put(doiObj);
        }
        return response;
    }

    private String invokeSelectBestMatchingDoi(JSONArray doiResponse, String targetVersion) throws Exception {
        Method method = PDS4Search.class.getDeclaredMethod("selectBestMatchingDoi", JSONArray.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(pds4Search, doiResponse, targetVersion);
    }

    private int invokeCompareVersions(String v1, String v2) throws Exception {
        Method method = PDS4Search.class.getDeclaredMethod("compareVersions", String.class, String.class);
        method.setAccessible(true);
        return (Integer) method.invoke(pds4Search, v1, v2);
    }
}
