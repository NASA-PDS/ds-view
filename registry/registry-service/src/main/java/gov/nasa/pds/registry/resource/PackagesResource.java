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

import java.net.URI;
import java.util.List;

import gov.nasa.pds.registry.exception.RegistryServiceException;
import gov.nasa.pds.registry.model.Link;
import gov.nasa.pds.registry.model.ObjectAction;
import gov.nasa.pds.registry.model.ObjectStatus;
import gov.nasa.pds.registry.model.PagedResponse;
import gov.nasa.pds.registry.model.RegistryPackage;
import gov.nasa.pds.registry.query.PackageFilter;
import gov.nasa.pds.registry.query.RegistryQuery;
import gov.nasa.pds.registry.query.QueryOperator;
import gov.nasa.pds.registry.service.RegistryService;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

/**
 * This resource is responsible for managing Packages with the 
 * registry service.
 *
 * @author pramirez
 */
public class PackagesResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @Context
  RegistryService registryService;

  public PackagesResource(UriInfo uriInfo, Request request,
      RegistryService registryService) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.registryService = registryService;
  }

  /**
   * Publishes a package to the registry. Publishing includes validation,
   * assigning an internal version, validating the submission, and notification.
   * 
   * @request.representation.qname {http://registry.pds.nasa.gov}registryPackage
   * @request.representation.mediaType application/xml
   * @response.param {@name Location} {@style header} {@type
   *                 {http://www.w3.org/2001/XMLSchema}anyURI} {@doc The URI
   *                 where the created item is accessible.}
   * 
   * @param registryPackage
   *          to publish
   * @return returns an HTTP response that indicates an error or the location of
   *         the created package and its guid
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response publishPackage(RegistryPackage registryPackage) {
    // TODO: Change to add user
    try {
      String guid = registryService.publishObject("Unknown", registryPackage);
      return Response.created(
          PackagesResource.getPackageUri((RegistryPackage) registryService
              .getObject(guid, RegistryPackage.class), uriInfo)).entity(guid)
          .build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  protected static URI getPackageUri(RegistryPackage registryPackage,
      UriInfo uriInfo) {
    return uriInfo.getBaseUriBuilder().clone().path(RegistryResource.class)
        .path(RegistryResource.class, "getPackagesResource").path(
            registryPackage.getGuid()).build();
  }

  /**
   * Retrieves the package with the given global identifier.
   * 
   * @response.representation.200.qname 
   *                                    {http://registry.pds.nasa.gov}registryPackage
   * @response.representation.200.mediaType application/xml
   * @return the package
   */
  @GET
  @Path("{packageGuid}")
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public RegistryPackage getPackage(@PathParam("packageGuid") String packageGuid) {
    try {
      RegistryPackage registryPackage = (RegistryPackage) registryService
          .getObject(packageGuid, RegistryPackage.class);
      return registryPackage;
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }
  
  /**
   * Deletes the package with the given global identifier.
   * 
   * @param packageGuid
   *          of package
   * @return Response indicating whether the operation succeeded or had an error
   */
  @DELETE
  @Path("{packageGuid}")
  public Response deletePackage(@PathParam("packageGuid") String packageGuid) {
    registryService.deleteObject("Unknown", packageGuid, RegistryPackage.class);
    return Response.ok().build();
  }
  
  /**
   * Updates the package with the given global identifier.
   * 
   * @param guid
   *          globally unique identifier of the service
   * @param service
   *          updates to the service
   * @return returns an HTTP response that indicates an error or ok
   */
  @PUT
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{packageGuid}")
  public Response updatePackage(@PathParam("packageGuid") String guid, RegistryPackage registryPackage) {
    try {
      registryService.updateObject("Unknown", registryPackage);
      return Response.ok().build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Updates the package with the given global identifier. This method supports 
   * clients that can not do a PUT operation.
   */
  @POST
  @Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("{packageGuid}")
  public Response updatePackageWithPost(@PathParam("packageGuid") String guid,
      RegistryPackage registryPackage) {
    return this.updatePackage(guid, registryPackage);
  }
  
  
  /**
   * Updates the status of the package with the given 
   * global identifier.
   * 
   * @param packageGuid
   *          unique identifier of package to look up 
   * @param action
   *          to take on package which will result in an update of status
   *          {@link ObjectAction}
   */
  @POST
  @Path("{packageGuid}/{action}")
  public Response changeStatusOfPackage(@PathParam("packageGuid") String packageGuid, @PathParam("action") ObjectAction action) {
      registryService.changeObjectStatus("Unknown", packageGuid, action, RegistryPackage.class);
      return Response.ok().build();
  }
  
  /**
   * Deletes all the members of a package with the given global identifier.
   * 
   * @param packageGuid
   *          unique identifier of package to look up members of 
   */
  @DELETE
  @Path("{packageGuid}/members")
  public Response deletePackageMembers(@PathParam("packageGuid") String packageGuid) {
    try {
      registryService.deletePackageMembersWithQuery("Unknown", packageGuid);
      return Response.ok().build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }
  
  /**
   * Updates the status of all the members of the package with the given 
   * global identifier.
   * 
   * @param packageGuid
   *          unique identifier of package to look up members of 
   * @param action
   *          to take on all members which will result in an update of status
   *          {@link ObjectAction}
   */
  @POST
  @Path("{packageGuid}/members/{action}")
  public Response changeStatusOfPackageMembers(@PathParam("packageGuid") String packageGuid, @PathParam("action") ObjectAction action) {
    try {
      registryService.changeStatusOfPackageMembersWithQuery("Unknown", packageGuid, action);
      return Response.ok().build();
    } catch (RegistryServiceException ex) {
      throw new WebApplicationException(Response.status(
          ex.getExceptionType().getStatus()).entity(ex.getMessage()).build());
    }
  }

  /**
   * Retrieves all packages managed by the registry given a set of filters.
   */ 
  @SuppressWarnings("unchecked")
  @GET
  @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response getPackages(
      @QueryParam("start") @DefaultValue("1") Integer start,
      @QueryParam("rows") @DefaultValue("20") Integer rows,
      @QueryParam("guid") String guid, 
      @QueryParam("name") String name,
      @QueryParam("lid") String lid,
      @QueryParam("status") ObjectStatus status,
      @QueryParam("queryOp") @DefaultValue("AND") QueryOperator operator,
      @QueryParam("sort") List<String> sort) {
	PackageFilter filter = new PackageFilter.Builder().guid(guid).name(name).lid(lid)
			.status(status).build();
    RegistryQuery.Builder<PackageFilter> queryBuilder = new RegistryQuery.Builder<PackageFilter>()
		        .filter(filter).operator(operator);  
    PagedResponse<RegistryPackage> rr =  registryService.getPackages(queryBuilder.build(), start, rows);
    
    Response.ResponseBuilder builder = Response.ok(rr);
    UriBuilder absolute = uriInfo.getAbsolutePathBuilder();
    absolute.queryParam("start", "{start}");
    absolute.queryParam("rows", "{rows}");
    // Add in next link
    if (start - 1 + rows < rr.getNumFound()) {
      int next = start + rows;
      String nextUri = absolute.clone().build(next, rows).toString();
      builder.header("Link", new Link(nextUri, "next", null));
    }
    // Add in previous link
    if (start > 1) {
      int previous = start - rows;
      if (previous < 1)
        previous = 1;
      String previousUri = absolute.clone().build(previous, rows).toString();
      builder.header("Link", new Link(previousUri, "previous", null));
    }
    return builder.build();
  }
}
