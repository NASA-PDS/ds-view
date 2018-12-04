/**
 * Copyright 2010-2017, by the California Institute of Technology.
 */
package gov.nasa.pds.tracking.tracking.jsoninterfaces;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import gov.nasa.pds.tracking.tracking.db.Reference;
import gov.nasa.pds.tracking.tracking.db.ReferenceDao;

/**
 * @author danyu dan.yu@jpl.nasa.gov
 *
 */
@Path("json/references")
public class JSONBasedReferences {

	public static Logger logger = Logger.getLogger(JSONBasedReferences.class);
	
	private ReferenceDao rD;
	private static final String FAILURE_RESULT="Failure";
	
	@GET
    @Produces("application/json")
    public Response defaultReferences() throws JSONException {
 
        JSONObject jsonReferences = new JSONObject();
        JSONObject jsonInstReference = new JSONObject();
        JSONObject jsonInveReference = new JSONObject();
        JSONObject jsonNodeReference = new JSONObject();
        
        JSONObject jsonReference = new JSONObject();
        
        ReferenceDao refD;
		try {
			//Instrument Reference
			refD = new ReferenceDao();
			List<Reference> refInsts = refD.getProductAllReferences(ReferenceDao.INST_TABLENAME);
						
			logger.info("number of Instrument Reference: "  + refInsts.size());
			Iterator<Reference> itr = refInsts.iterator();
			int countInst = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("instrument Reference " + countInst + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();
		        jsonReference.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(ReferenceDao.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(ReferenceDao.TITLECOLUMN, r.getTitle());
		        jsonInstReference.append("instrument", jsonReference);
		        countInst++;
		    }
			
			
	         //investigation_reference
			refD = new ReferenceDao();
			List<Reference> refInves = refD.getProductAllReferences(ReferenceDao.INVES_TABLENAME);
						
			logger.info("number of Investigation Reference: "  + refInves.size());
			itr = refInves.iterator();
			int countInve = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("investigation Reference " + countInve + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();		         
		        jsonReference.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(ReferenceDao.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(ReferenceDao.TITLECOLUMN, r.getTitle());
		        jsonInveReference.append("investigation", jsonReference);
		        countInve++;
		    }
	         
	         //node_reference
			refD = new ReferenceDao();
			List<Reference> refNodes = refD.getProductAllReferences(ReferenceDao.NODE_TABLENAME);
						
			logger.info("number of Node Reference: "  + refNodes.size());
			itr = refNodes.iterator();
			int countNode = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
		        logger.debug("node Reference " + countNode + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
		        jsonReference = new JSONObject();		         
		        jsonReference.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
		        jsonReference.put(ReferenceDao.REFERENCECOLUMN, r.getReference());
		        jsonReference.put(ReferenceDao.TITLECOLUMN, r.getTitle());
		        jsonNodeReference.append("Node", jsonReference);
		        countNode++;
		    }

	         jsonReferences.append("References", jsonInstReference);
	         jsonReferences.append("References", jsonInveReference);
	         jsonReferences.append("References", jsonNodeReference);
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonReferences.toString(4);
        return Response.status(200).entity(result).build();
    }

	@Path("{id : (.+)?}/{refType : (.+)?}")
    @GET
    @Produces("application/json")
    public Response getReferences(@PathParam("id") String id, @PathParam("refType") String refTableName)  throws JSONException {
		
		JSONObject jsonRefs = new JSONObject();
        
        JSONObject jsonRef = new JSONObject();
        
        ReferenceDao refD;
		try {
			refD = new ReferenceDao();
			List<Reference> refs = refD.getProductReferences(id, refTableName);
			logger.info("number of refTableName: "  + refs.size());
			Iterator<Reference> itr = refs.iterator();
			int count = 1;
			
			while(itr.hasNext()) {
				Reference r = itr.next();
				logger.debug(refTableName + " " + count + ":\n " + r.getLog_identifier() + " : " + r.getReference());
		         
				jsonRef = new JSONObject();		         
				jsonRef.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, r.getLog_identifier());
				jsonRef.put(ReferenceDao.REFERENCECOLUMN, r.getReference());
				jsonRef.put(ReferenceDao.TITLECOLUMN, r.getTitle());

				jsonRefs.append(refTableName, jsonRef);
		        count++;
		    }
			
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			logger.error(e);
		}
        String result = "" + jsonRefs.toString(4);
        return Response.status(200).entity(result).build();
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response createReference(@FormParam("LogicalIdentifier") String log_id, 
							@FormParam("Reference") String ref, 
							@FormParam("Title") String title, 
							@FormParam("ReferenceType") String refType) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			rD = new ReferenceDao();

			Reference refObj = new Reference(log_id, ref, title, refType);
			int result = rD.insertReference(refObj);
			
			if(result == 1){
				message.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, log_id);
				message.put(ReferenceDao.REFERENCECOLUMN, ref);
				message.put(ReferenceDao.TITLECOLUMN, title);
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		//logger.debug("Result: " + message);
		relt.append("Reference", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
	
	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)	
	public Response updateReference(@FormParam("LogicalIdentifier") String log_id, 
				@FormParam("Reference") String ref, 
				@FormParam("Title") String title, 
				@FormParam("ReferenceType") String refType) throws IOException{
		
		JSONObject relt = new JSONObject();
		JSONObject message = new JSONObject();
		try {
			rD = new ReferenceDao();
			
			Reference refObj = new Reference(log_id, ref, title, refType);
			Reference updatedRef = rD.updateReference(refObj);
			
			if(updatedRef != null){
				message.put(ReferenceDao.LOG_IDENTIFIERCOLUMN, updatedRef.getLog_identifier());
				message.put(ReferenceDao.REFERENCECOLUMN, updatedRef.getReference());
				message.put(ReferenceDao.TITLECOLUMN, updatedRef.getTitle());
			}else{
				message.put("Message", FAILURE_RESULT);
			}
		} catch (ClassNotFoundException | SQLException e) {			
			e.printStackTrace();
			message.put("Message", FAILURE_RESULT);
		}
		relt.append("Reference", message);
		String jsonOutput = "" + relt.toString(4);
		return Response.status(200).entity(jsonOutput).build();
	}
}
