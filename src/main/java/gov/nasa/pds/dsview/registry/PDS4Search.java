// Copyright 2012-2017, by the California Institute of Technology.
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
//
// $Id$

package gov.nasa.pds.dsview.registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class PDS4Search {

	private static Logger log = Logger.getLogger(PDS4Search.class.getName());
	private static String solrServerUrl;

	public static final String DOI_SERVER_URL = "https://pds.nasa.gov/api/doi/0.2/dois";
	
	/**
	 * Constructor.
	 */
	public PDS4Search(String url) {
		solrServerUrl = url;
	}

	public SolrDocumentList getCollections() throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	  try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,collection\"");
		
        log.fine("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}

        return solrResults;
      } finally {
        solr.close();
      }
	}

	public SolrDocumentList getBundles() throws SolrServerException, IOException {
      Http2SolrClient solr = null;
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,bundle\"");
		
        log.fine("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
        return solrResults;
      } finally {
        solr.close();
      }
	}

	public SolrDocumentList getObservationals(int start) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();
		
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,observational\"");
		params.set("start", start);

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());

		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
            log.fine("*****************  idx = " + (idx++));
			// log.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
        return solrResults;
      } finally {
        solr.close();
      }
	}

	public SolrDocumentList getDocuments() throws SolrServerException, IOException {
      Http2SolrClient solr = null;
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,document\"");
		
        log.fine("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
            log.fine("*****************  idx = " + (idx++));
			// log.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
        return solrResults;
      } finally {
        solr.close();
      }
	}
	
	public SolrDocument getContext(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();
        
        ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:" + cleanIdentifier(identifier));
		params.set("wt", "xml");

        log.fine("params = " + params.toString());
        QueryResponse response =
            solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + (idx++));
			// log.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;
      } finally {
        solr.close();
      }
	}

	public List<String> getValues(SolrDocument doc, String key) {
		Collection<Object> values = doc.getFieldValues(key);
		
		if (values==null || values.size()==0) {
			//log.info("key = " + key + "   values = " + values);
			return null;
		}
		
		List<String> results = new ArrayList<String>();
		for (Object obj: values) {
			if (obj instanceof java.util.Date) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				String dateValue = df.format(obj);
				if (dateValue.equals("3000-01-01T12:00:00.000Z")) {
					results.add("N/A (ongoing)");
				} else {
					results.add(dateValue);
				}
                log.fine("date = " + obj.toString() + "  string date = " + dateValue);
			}
			else {
              results.add(obj.toString());
              log.fine("k = " + key + "\tv = " + obj.toString());
			}
		}
		return results;		
	}

    public Map<String, String> getResourceLinks(List<String> resourceRefList)
        throws SolrServerException, IOException {
      Http2SolrClient solr = null;
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();
        ModifiableSolrParams params = null;

        Map<String, String> resourceMap = new LinkedHashMap<String, String>();

        if (resourceRefList == null) {
          return resourceMap;
        }

        for (String resourceRef : resourceRefList) {
          params = new ModifiableSolrParams();
          params.add("q", "identifier:" + cleanIdentifier(resourceRef));
          params.set("wt", "xml");

          log.info("params = " + params.toString());
          QueryResponse response =
              solr.query(params, org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

          SolrDocumentList solrResults = response.getResults();
          log.fine("numFound = " + solrResults.getNumFound());

          Iterator<SolrDocument> itr = solrResults.iterator();
          SolrDocument doc = null;
          int idx = 0;
          while (itr.hasNext()) {
            doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

            String resourceName = "";
            String resourceURL = "";
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
              if (entry.getKey().equals("resource_name")) {
                resourceName = getValue(entry);
              } else if (entry.getKey().equals("resLocation")) {
                resourceURL = getValue(entry);
              }
            }
            log.fine("resname = " + resourceName + "       reslink = " + resourceURL);
            resourceMap.put(resourceName, resourceURL);
          }
        }
        return resourceMap;
      } finally {
        solr.close();
      }
    }

    private String getValue(Map.Entry<String, Object> entry) {
      if (entry.getValue() instanceof List<?>) {
        return ((List<?>) entry.getValue()).get(0).toString();
      }

      return entry.getValue().toString();
    }

	public JSONArray getDoiResponse(URL url) throws IOException, JSONException {
      log.fine("getDoiResponse(" + url + ")");
      HttpURLConnection conn = null;
      try {
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);

		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = br.readLine()) != null) {
				response.append(line);
			}
			br.close();

			JSONArray jsonResponse = new JSONArray(response.toString());
            log.fine("getDoiResponse=" + jsonResponse.toString(2));
			return jsonResponse;
		}
		else {
          log.warning("getDoiResponse's responseCode != 200");
          return null;
		}
      } finally {
        conn.disconnect();
      }

	}

	public String getDoi(String lid, String vid) throws IOException, JSONException {
		log.fine("\ngetDOI(" + lid + ", " + vid + ")");
		String identifier = lid + "::";
		Boolean withVid = Boolean.FALSE;

		if (vid != null) {
			identifier += vid;
			withVid = Boolean.TRUE;
		}
		else {
			identifier += "*";
		}
		
		// for gamma, comment above (localhost) and uncomment below (pds.nasa.gov) so that data engineers can see actual DOIs instead of test data
		URL url = new URL(DOI_SERVER_URL + "?ids=" + URLEncoder.encode(identifier, "UTF-8"));
        log.info("DOI query: " + url);
		JSONArray doiResponse = getDoiResponse(url);

		if (doiResponse == null) {
			return null;
		} else {
			if (doiResponse.length() == 0) {
				if (withVid) return getDoi(lid, null);
				else return "No DOI found.";
			}
			else if (doiResponse.length() == 1) {
				JSONObject jsonResponse = doiResponse.getJSONObject(0);
				String doi = jsonResponse.getString("doi");
				return "<a href=\"https://doi.org/" + doi + "\">" + doi + "</a>";
			}
			else {
				return "Multiple DOIs found. Use <a href=\"/tools/doi/#/search/" + identifier + "\">DOI Search</a> to select the most appropriate.";
			}
		}
	}
	
	private String cleanIdentifier(String identifier) {
		return identifier.replace(":", "\\:").replace("\\\\", "\\");
	}

	/**
	 * Command line invocation.
	 * 
	 * @param argv
	 *            Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			PDS4Search pds4Search;

			if (argv.length == 1)
				pds4Search = new PDS4Search(argv[0]);
			else
				pds4Search = new PDS4Search(
						"http://localhost:8983/solr/data");

			pds4Search.getCollections();
			pds4Search.getBundles();
//			pds4Search.getContext("urn:nasa:pds:context:investigation:investigation.PHOENIX");

			// sparms.getSearchResult("mission:cassini-huygens and target:Callisto");
		} catch (Exception ex) {
			System.err.println("Exception " + ex.getClass().getName() + ": "
					+ ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
