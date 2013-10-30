//  Copyright 2009-2011, by the California Institute of Technology.
//  ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//  Any commercial use must be negotiated with the Office of Technology 
//  Transfer at the California Institute of Technology.
//  
//  This software is subject to U. S. export control laws and regulations 
//  (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software 
//  is subject to U.S. export control laws and regulations, the recipient has 
//  the responsibility to obtain export licenses or other export authority as 
//  may be required before exporting such information to foreign countries or 
//  providing access to foreign nationals.
//  
//  $Id$
//

package gov.nasa.pds.registry.resource;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.model.ReplicationReport;
import gov.nasa.pds.registry.service.RegistryService;
import gov.nasa.pds.registry.util.DateParam;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

/**
 * This resource is responsible for managing replication requests.
 * 
 * @author pramirez
 */
@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ReplicationResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public ReplicationResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Requests a replication of a remote registry contents to be initiated.
   * 
   * @param registryUrl
   *          to replicate contents from
   * @param lastModified
   *          time to constrain which registry objects are relevant to
   *          replicate. This time is inclusive. If set to null all will be
   *          pulled.
   * @param objectType 
   *          to replicate, all others will be ignored.
   * @param packageGuid
   *          identifier for the package which all replicated items will be
   *          associated with.
   * @param packageName
   *          used for the replication package
   * @return an HTTP response that indicates an error or location of the report
   *         for this replication request.
   */
  @POST
  public Response performReplication(
      @QueryParam("registryUrl") String registryUrl,
      @QueryParam("lastModified") DateParam lastModified,
      @QueryParam("objectType") String objectType,
      @QueryParam("packageGuid") String packageGuid,
      @QueryParam("packageName") String packageName) {
    if (registryUrl == null) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
          .entity("registryUrl was not set").build());
    }
    try {
      RegistryPackage replicationPackage = new RegistryPackage();
      replicationPackage.setGuid(packageGuid);
      replicationPackage.setName(packageName);
      replicationPackage.setDescription("Replication of " + registryUrl + 
      		" with last modified [" + lastModified + "] and object type [" + 
      		objectType + "].");
      registryService.performReplication("Unknown", registryUrl,
          (lastModified == null) ? null : lastModified.getDate(), objectType, 
          		replicationPackage);
      return Response.created(
          uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
              .path(RegistryResource.class, "getReplicationResource").path(
                  "report").build()).build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves information about an ongoing replication request.
   * 
   * @return the report of the ongoing or last finished replication event if no
   *         replication event has occurred since the server was restarted a 404
   *         will be returned.
   */
  @GET
  @Path("report")
  @Produces( { MediaType.APPLICATION_XML, MediaType.TEXT_XML,
      MediaType.APPLICATION_JSON })
  public ReplicationReport getReplicationReport() {
    ReplicationReport report = registryService.getReplicationReport();
    if (report == null) {
      throw new WebApplicationException(Response.status(
          Response.Status.NOT_FOUND).build());
    }
    return registryService.getReplicationReport();
  }

}
