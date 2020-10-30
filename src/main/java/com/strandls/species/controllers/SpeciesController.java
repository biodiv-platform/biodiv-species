package com.strandls.species.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.species.ApiConstants;
import com.strandls.species.service.SpeciesServices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api("Species Services")
@Path(ApiConstants.V1 + ApiConstants.SPECIES)
public class SpeciesController {

	@Inject
	private SpeciesServices speciesService;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)

	public Response getPong() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@GET
	@Path(ApiConstants.SHOW + "/{spciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "provide the show page of speices", notes = "Returns the species Show page")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the show page", response = String.class) })

	public Response getSpeciesShowPage(@PathParam("speicesId") String sId) {
		try {
			Long speciesId = Long.parseLong(sId);

			return null;
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.MIGRATEFIELD)
	@Produces(MediaType.TEXT_PLAIN)

	public Response migratefield() {
		try {
			speciesService.migrateField();
			return Response.status(Status.OK).entity("done").build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}
