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
public class PDS3Search {

	private static Logger log = Logger.getLogger(PDS3Search.class.getName());
    static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/";
	public static final String DOI_SERVER_URL = "https://pds.nasa.gov/api/doi/0.2/dois";
	/**
	 * Constructor.
	 */
	public PDS3Search(String url) {
      solrServerUrl = url;
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
		
		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		log.info("numFound = " + solrResults.getNumFound());
		
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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound() + "     maxScores = "
            + solrResults.getMaxScore());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0, returnIdx = 0;
		List<SolrDocument> dsDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + idx);

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				String key = entry.getKey();
                log.fine("Key = " + key
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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}		
		return doc;

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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
        return doc;

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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		log.info("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + (idx++));
			// log.info(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
		return instDocs;

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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

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

        log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
        log.fine("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		List<SolrDocument> instDocs = new ArrayList<SolrDocument>();
		while (itr.hasNext()) {
			doc = itr.next();
            log.fine("*****************  idx = " + (idx++));

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
              log.fine("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
			instDocs.add(doc);
		}
		if (idx>1)
			doc = instDocs.get(0);
		return doc;

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

		log.info("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

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
                log.fine("key = " + key + "   date = " + obj.toString() + "  string date = "
                    + dateValue);
			}
			else {
				results.add((String)obj);
                log.fine("key = " + key + "   obj = " + (String) obj);
			}
		}
		return results;		
	}

	public String getDoi(String identifier) throws IOException, JSONException {
		log.info("getDOI(" + identifier + ")");
		URL url = new URL(DOI_SERVER_URL + "?ids=" + URLEncoder.encode(identifier, "UTF-8"));
	
        HttpURLConnection conn = null;
        try {
          conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");
          conn.setConnectTimeout(5000);
          conn.setReadTimeout(5000);

          int responseCode = conn.getResponseCode();
          if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = br.readLine()) != null) {
              response.append(line);
            }
            br.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            log.info("DOI Service response = " + jsonArray.toString(2));
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
          if (conn != null)
            conn.disconnect();
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
			//pds3Search.getContext("urn:nasa:pds:context:investigation:investigation.PHOENIX");

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
