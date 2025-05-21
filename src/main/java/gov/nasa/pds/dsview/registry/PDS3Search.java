// Copyright 2012-2013, by the California Institute of Technology.
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
// $Id: PDS4Search.java 12759 2014-02-27 21:02:33Z hyunlee $

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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class PDS3Search {

	private static final Logger logger = LoggerFactory.getLogger(PDS3Search.class);
    private static String solrServerUrl;
	public static final String DOI_SERVER_URL = "https://pds.nasa.gov/api/doi/0.2/dois";
	/**
	 * Constructor.
	 */
	public PDS3Search(String url) {
      solrServerUrl = url;
	}

	// Add a singleton Http2SolrClient
	private static final AtomicReference<Http2SolrClient> solrClient = new AtomicReference<>();

	public void cleanup() {
		Http2SolrClient client = solrClient.getAndSet(null);
		if (client != null) {
			client.close();
		}
	}

	public SolrDocumentList getDataSetList() throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	  
	  try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,data_set\"");
		
		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		logger.info("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public SolrDocument getDataSet(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
	      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();
		
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3 AND data_set_id:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,data_set\"");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound() + "     maxScores = "
            + solrResults.getMaxScore());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0, returnIdx = 0;
		List<SolrDocument> dsDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + idx);

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				String key = entry.getKey();
                logger.debug("Key = " + key
						+ "       Value = " + entry.getValue());
				String value = entry.getValue().toString();

				if (key.equalsIgnoreCase("identifier")) {
					if (value.startsWith("urn:nasa:pds")) {
						returnIdx = idx;
					}
				}
			}
			dsDocs.add(doc);
			idx++;
		}
		// if there are more than 1 data sets, return "urn:nasa:pds" product as default
		if (idx>1) {
			doc = dsDocs.get(returnIdx);
		}
		return doc;
      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public SolrDocument getMission(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
	      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3 AND investigation_name:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,investigation\"");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}		
		return doc;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}

	public SolrDocument getInstHost(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3 AND instrument_host_id:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument_host\"");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
        return doc;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public List<SolrDocument> getInst(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3 AND instrument_id:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument\"");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		logger.info("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));
			// logger.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
		return instDocs;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public SolrDocument getInst(String instId, String instHostId) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "pds_model_version:pds3 AND instrument_id:\""+instId+
				"\" AND instrument_host_id:\""+instHostId+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,instrument\"");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));
			// logger.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public SolrDocument getTarget(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "target_name:\""+identifier+"\" AND pds_model_version:pds3");
		params.set("indent", "on");
		params.set("wt", "xml");
		params.set("fq", "facet_type:\"1,target\"");

        logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
		if (idx>1)
			doc = instDocs.get(0);
		return doc;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public SolrDocument getResource(String identifier) throws SolrServerException, IOException {
      Http2SolrClient solr = null;
	      
      try {
        solr = new Http2SolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:\""+identifier+"\"");
		params.set("indent", "on");
		params.set("wt", "xml");

		logger.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        logger.debug("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
            logger.debug("*****************  idx = " + (idx++));
			// logger.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              logger.debug("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;

      } catch (Exception ex) {
        logger.error("Error during PDS3 search", ex);
        return null;
      } finally {
        if (solr != null)
          solr.close();
      }
	}
	
	public List<String> getValues(SolrDocument doc, String key) {
		Collection<Object> values = doc.getFieldValues(key);
		
		if (values==null || values.size()==0) {
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
                logger.debug("key = " + key + "   date = " + obj.toString() + "  string date = "
                    + dateValue);
			}
			else {
				results.add((String)obj);
                logger.debug("key = " + key + "   obj = " + (String) obj);
			}
		}
		return results;		
	}

	public String getDoi(String identifier) throws IOException, JSONException {
		logger.info("getDOI(" + identifier + ")");
		URL url = new URL(DOI_SERVER_URL + "?ids=" + URLEncoder.encode(identifier, "UTF-8"));
	
		HttpURLConnection conn = null;
		BufferedReader br = null;
		InputStreamReader isr = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				isr = new InputStreamReader(conn.getInputStream());
				br = new BufferedReader(isr);
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = br.readLine()) != null) {
					response.append(line);
				}

				JSONArray jsonArray = new JSONArray(response.toString());
				logger.info("DOI Service response = " + jsonArray.toString(2));
				if (jsonArray.length() == 0) {
					return null;
				} else if (jsonArray.length() == 1) {
					JSONObject jsonResponse = jsonArray.getJSONObject(0);
					String doi = jsonResponse.getString("doi");
					return "<a href=\"https://doi.org/" + doi + "\">" + doi + "</a>";
				} else {
					return "Multiple DOIs found. Use <a href=\"/tools/doi/#/search/" + identifier
						+ "\">DOI Search</a> to select the most appropriate.";
				}
			} else {
				return null;
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					logger.warn("Error closing BufferedReader: " + e.getMessage());
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					logger.warn("Error closing InputStreamReader: " + e.getMessage());
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Command line invocation.
	 * 
	 * @param argv
	 *            Command-line arguments.
	 */
	public static void main(String[] argv) {
		try {
			PDS3Search pds3Search;

			if (argv.length == 1)
				pds3Search = new PDS3Search(argv[0]);
			else
				pds3Search = new PDS3Search(
						"http://pdsbeta.jpl.nasa.gov:8080/search-service");

			pds3Search.getDataSetList();

		} catch (Exception ex) {
			logger.error("Exception in main: " + ex.getMessage(), ex);
			System.exit(1);
		}
		System.exit(0);
	}
}
