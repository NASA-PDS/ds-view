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

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

//import gov.nasa.pds.dsview.registry.SearchRegistry;
//import gov.nasa.pds.registry.model.ExtrinsicObject;

/**
 * This class is used by the PDS data set view web interface to retrieve values
 * for building the search parameter pull-down lists.
 * 
 * @author Hyun Lee
 */
public class PDS4Search {

	static String solrServerUrl = "http://pdsdev.jpl.nasa.gov:8080/search-service/";
	
	/**
	 * Constructor.
	 */
	public PDS4Search(String url) {
		this.solrServerUrl = url;
	}

	public SolrDocumentList getCollections() throws SolrServerException, IOException {
		HttpSolrClient solr = new HttpSolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_collection\"");
		params.set("fq", "facet_type:\"1,collection\"");
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getBundles() throws SolrServerException, IOException {
		HttpSolrClient solr = new HttpSolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_bundle\"");
		params.set("fq", "facet_type:\"1,bundle\"");
		//params.set("start", start);
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getObservationals(int start) throws SolrServerException, IOException {
		HttpSolrClient solr = new HttpSolrClient.Builder(solrServerUrl).build();
		
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "*");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_observational\"");
		params.set("fq", "facet_type:\"1,observational\"");
		params.set("start", start);

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());

		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}

	public SolrDocumentList getDocuments() throws SolrServerException, IOException {
//		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);
		HttpSolrClient solr = new HttpSolrClient.Builder(solrServerUrl).build();

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "");
		params.set("wt", "xml");
		//params.set("fq", "facet_object_type:\"1,product_document\"");
		params.set("fq", "facet_type:\"1,document\"");
		
		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		if (response==null) 
			return null;
		
		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		int idx = 0;
		while (itr.hasNext()) {
			SolrDocument doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return solrResults;
	}
	
	public SolrDocument getContext(String identifier) throws SolrServerException, IOException {
		HttpSolrClient solr = new HttpSolrClient.Builder(solrServerUrl).build();
		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;
	}
/*	
	public SolrDocument getContext(String facet_type, String identifier) throws MalformedURLException, SolrServerException {
		SolrServer solr = new CommonsHttpSolrServer(solrServerUrl);

		ModifiableSolrParams params = new ModifiableSolrParams();

		params.add("q", "identifier:"+identifier);
		params.set("indent", "on");
		params.set("wt", "xml");
		if (facet_type!=null)
			params.set("fq", "facet_type:\"1," + facet_type + "\"");

		System.out.println("params = " + params.toString());
		QueryResponse response = solr.query(params,
				org.apache.solr.client.solrj.SolrRequest.METHOD.GET);

		SolrDocumentList solrResults = response.getResults();
		System.out.println("numFound = " + solrResults.getNumFound());
		
		Iterator<SolrDocument> itr = solrResults.iterator();
		SolrDocument doc = null;
		int idx = 0;
		while (itr.hasNext()) {
			doc = itr.next();
			System.out.println("*****************  idx = " + (idx++));
			// System.out.println(doc.toString());

			for (Map.Entry<String, Object> entry : doc.entrySet()) {
				System.out.println("Key = " + entry.getKey()
						+ "       Value = " + entry.getValue());
			}
		}
		return doc;
	}
*/	
	public List<String> getValues(SolrDocument doc, String key) {
		Collection<Object> values = doc.getFieldValues(key);
		
		if (values==null || values.size()==0) {
			//System.out.println("key = " + key + "   values = " + values);
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
				System.out.println("date = " + obj.toString() + "  string date = " + dateValue);
			}
			else {
				results.add((String) obj);
				System.out.println("k = " + key + "\tv = " + (String) obj);
			}
		}
		return results;		
	}

	public JSONArray getDoiResponse(URL url) throws IOException, JSONException {
		System.out.println("getDoiResponse(" + url + ")");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
			System.out.println("getDoiResponse=" + jsonResponse.toString(2));
			return jsonResponse;
		}
		else {
			System.out.println("getDoiResponse's responseCode != 200");
			return null;
		}
	}

	public String getDoi(String lid, String vid) throws IOException, JSONException {
		System.out.println("\ngetDOI(" + lid + ", " + vid + ")");
		String identifier = lid + "::";
		Boolean withVid = Boolean.FALSE;

		if (vid != null) {
			identifier += vid;
			withVid = Boolean.TRUE;
		}
		else {
			identifier += "*";
		}
		
		URL url = new URL("http://localhost:8082/PDS_APIs/pds_doi_api/0.2/dois?ids=" + URLEncoder.encode(identifier));
		// for gamma, comment above (localhost) and uncomment below (pds.nasa.gov) so that data engineers can see actual DOIs instead of test data
//		URL url = new URL("https://pds.nasa.gov/api/doi/0.2/dois?ids=" + URLEncoder.encode(identifier));
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
