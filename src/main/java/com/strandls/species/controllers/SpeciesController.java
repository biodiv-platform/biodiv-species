package com.strandls.species.controllers;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.species.ApiConstants;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesResourcesPreData;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.species.service.SpeciesServices;
import com.strandls.taxonomy.pojo.CommonNames;
import com.strandls.taxonomy.pojo.CommonNamesData;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.FactsUpdateData;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserGroupSpeciesCreateData;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
	@Path(ApiConstants.SHOW + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "provide the show page of speices", notes = "Returns the species Show page")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the show page", response = String.class) })

	public Response getSpeciesShowPage(@PathParam("speciesId") String sId) {
		try {
			Long speciesId = Long.parseLong(sId);
			ShowSpeciesPage result = speciesService.showSpeciesPage(speciesId);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_FOUND).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.FIELDS + ApiConstants.RENDER)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "forms the field stucture", notes = "returns the fields structure", response = FieldRender.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to get the fields framework", response = String.class) })

	public Response renderFields() {
		try {
			List<FieldRender> result = speciesService.getFields();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TRAITS + ApiConstants.ALL)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Get all the species traits field wise", notes = "returns all the traits CategoryWise", response = SpeciesTrait.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to fetch the traits", response = String.class) })

	public Response getAllSpeciesTraits() {
		try {
			List<SpeciesTrait> result = speciesService.getAllSpeciesTraits();
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TRAITS + ApiConstants.TAXONOMY + "/{taxonomyId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "Get all the species traits field wise by taxonomyId", notes = "returns all the traits CategoryWise", response = SpeciesTrait.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to fetch the traits", response = String.class) })

	public Response getSpeciesTraitsByTaxonomy(@PathParam("taxonomyId") String taxonomyId) {
		try {
			Long taxon = Long.parseLong(taxonomyId);
			List<SpeciesTrait> result = speciesService.getSpeciesTraitsByTaxonomyId(taxon);
			return Response.status(Status.OK).entity(result).build();
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

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.USERGROUP + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the species usergroup mapping", notes = "Return the associated userGroup", response = UserGroupIbp.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response updateUserGroupSpecies(@Context HttpServletRequest request,
			@PathParam("speciesId") String speciesId,
			@ApiParam(name = "ugSpeciesCreateData") UserGroupSpeciesCreateData ugSpeciesCreateData) {
		try {
			List<UserGroupIbp> result = speciesService.updateUserGroup(request, speciesId, ugSpeciesCreateData);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.FEATURED)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Feature a species", notes = "Returns all the featuring", response = Featured.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Unable to feature the species", response = String.class) })

	public Response createFeatured(@Context HttpServletRequest request,
			@ApiParam(name = "featuredCreate") FeaturedCreate featuredCreate) {
		try {
			List<Featured> result = speciesService.createFeatured(request, featuredCreate);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UNFEATURED + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Unfeatured a species", notes = "Returns all the featuring", response = Featured.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Unable to feature the species", response = String.class) })

	public Response unFeatured(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "userGroupList") List<Long> userGroupList) {
		try {
			List<Featured> result = speciesService.unFeatured(request, speciesId, userGroupList);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.TRAITS + "/{speciesId}/{traitId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update species Traits", notes = "Return all the traits", response = FactValuePair.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the traits", response = String.class) })

	public Response updateTraits(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@PathParam("traitId") String traitId, @ApiParam(name = "factsUpdateData") FactsUpdateData factsUpdateData) {
		try {
			List<FactValuePair> result = speciesService.updateTraits(request, speciesId, traitId, factsUpdateData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {

			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.SPECIESFIELD + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update species field", notes = "return species field data", response = SpeciesFieldData.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the species Field", response = String.class) })

	public Response updateSpeciesField(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "sfUpdateData") SpeciesFieldUpdateData sfUpdateData) {
		try {
			Long sId = Long.parseLong(speciesId);
			SpeciesFieldData result = speciesService.updateSpeciesField(request, sId, sfUpdateData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.SPECIESFIELD + "/{speciesFieldId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Delete species field", notes = "return Boolean value", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to delete the species Field", response = String.class) })

	public Response removeSpeciesField(@Context HttpServletRequest request,
			@PathParam("speciesFieldId") String speciesFieldId) {
		try {
			Long sfId = Long.parseLong(speciesFieldId);
			Boolean result = speciesService.removeSpeciesField(request, sfId);
			if (result)
				return Response.status(Status.OK).entity("DELETED").build();
			return Response.status(Status.NOT_MODIFIED).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.COMMONNAME + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update and add common Names", notes = "return common Names list", response = CommonNames.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the common Names", response = String.class) })

	public Response updateAddCommonName(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "commonNamesData") CommonNamesData commonNamesData) {
		try {
			Long sId = Long.parseLong(speciesId);
			List<CommonNames> result = speciesService.updateAddCommonName(request, sId, commonNamesData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.COMMONNAME + "/{speciesId}/{commonNameId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "delete common Names", notes = "return common Names list", response = CommonNames.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the common Names", response = String.class) })

	public Response removeCommonName(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@PathParam("commonNameId") String commonNameId) {
		try {
			Long sId = Long.parseLong(speciesId);
			List<CommonNames> result = speciesService.removeCommonName(request, sId, commonNameId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.PULL + ApiConstants.OBSERVATION + ApiConstants.RESOURCE + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "get all the observation resources", notes = "Returns the observation resources", response = SpeciesPull.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to get the resources", response = String.class) })

	public Response getObservationResources(@PathParam("speciesId") String speciesId,
			@DefaultValue("0") @QueryParam("offset") String offset) {
		try {

			Long sId = Long.parseLong(speciesId);
			Long offSet = Long.parseLong(offset);
			List<SpeciesPull> result = speciesService.getObservationResource(sId, offSet);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.EDIT + ApiConstants.RESOURCE + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "get all the species resources", notes = "Returns the spcies resources", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to get the resources", response = String.class) })

	public Response getEditSpeciesResource(@Context HttpServletRequest request,
			@PathParam("speciesId") String speciesId) {
		try {
			Long sId = Long.parseLong(speciesId);
			List<ResourceData> result = speciesService.getSpeciesResources(request, sId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.RESOURCE + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the species resources", notes = "Returns the species resources", response = ResourceData.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to update the resources", response = String.class) })

	public Response updateSpeciesResource(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "") List<SpeciesResourcesPreData> preDataList) {
		try {
			Long sId = Long.parseLong(speciesId);
			List<ResourceData> result = speciesService.updateSpciesResources(request, sId, preDataList);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.ADD + ApiConstants.COMMENT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Add species Comment", notes = "Return the comment activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to log the comment", response = String.class) })

	public Response addSpeciesComment(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData loggingData) {
		try {
			Activity result = speciesService.addSpeciesComment(request, loggingData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

}
