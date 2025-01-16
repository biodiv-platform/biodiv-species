package com.strandls.species.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.authentication_utility.filter.ValidateUser;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapBoundParams;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.esmodule.pojo.MapSearchParams.SortTypeEnum;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.species.ApiConstants;
import com.strandls.species.Headers;
import com.strandls.species.es.util.ESUpdate;
import com.strandls.species.es.util.ESUtility;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.MapAggregationResponse;
import com.strandls.species.pojo.ReferenceCreateData;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesCreateData;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesListPageData;
import com.strandls.species.pojo.SpeciesPermission;
import com.strandls.species.pojo.SpeciesResourcesPreData;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.species.service.SpeciesListService;
import com.strandls.species.service.SpeciesServices;
import com.strandls.species.util.SpeciesBulkMappingThread;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.CommonNamesData;
import com.strandls.taxonomy.pojo.EncryptedKey;
import com.strandls.taxonomy.pojo.PermissionData;
import com.strandls.taxonomy.pojo.SynonymData;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.taxonomy.pojo.TaxonomySave;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.FactsUpdateData;
import com.strandls.user.pojo.Follow;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
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

	@Inject
	private SpeciesListService listService;

	@Inject
	private ESUtility esUtility;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private EsServicesApi esService;

	@Inject
	private Headers headers;

	@Inject
	private ESUpdate esUpdate;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)

	public Response getPong() {
		return Response.status(Status.OK).entity("PONG").build();
	}

	@GET
	@Path(ApiConstants.SPECIESID + "/{taxonId}")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)

	public Response getSpeciesId(@PathParam("taxonId") Long taxonId) {

		try {
			String speciesId = speciesService.getSpeciesIdFromTaxonId(taxonId);
			if (speciesId != null) {
				return Response.status(Status.OK).entity(speciesId).build();
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.SHOW + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "provide the show page of speices", notes = "Returns the species Show page", response = ShowSpeciesPage.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the show page", response = String.class) })

	public Response getSpeciesShowPage(@PathParam("speciesId") String sId) {
		try {
			Long speciesId = Long.parseLong(sId);
			ShowSpeciesPage result = speciesService.showSpeciesPageFromES(speciesId);
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

	public Response renderFields(@QueryParam("langId") String langId) {
		try {
			Long languageId = null;
			if (langId != null)
				languageId = Long.parseLong(langId);
			List<FieldRender> result = speciesService.getFields(languageId);
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

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.USERGROUP + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update the species usergroup mapping", notes = "Return the associated userGroup", response = UserGroupIbp.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data", response = String.class) })

	public Response updateUserGroupSpecies(@Context HttpServletRequest request,
			@PathParam("speciesId") String speciesId, @ApiParam(name = "userGroupList") List<Long> userGroupList) {
		try {
			UserGroupSpeciesCreateData ugSpeciesCreateData = new UserGroupSpeciesCreateData();
			ugSpeciesCreateData.setUserGroupIds(userGroupList);
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

	@ApiOperation(value = "update and add common Names", notes = "return common Names list", response = CommonName.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the common Names", response = String.class) })

	public Response updateAddCommonName(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "commonNamesData") CommonNamesData commonNamesData) {
		try {
			Long sId = Long.parseLong(speciesId);
			List<CommonName> result = speciesService.updateAddCommonName(request, sId, commonNamesData);

			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_ACCEPTABLE).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path(ApiConstants.UPDATE + ApiConstants.PREFERREDCOMMONNAME + "/{speciesId}/{commonNameId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "update preferrred common Names", notes = "return preferred common Name", response = CommonName.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the preferrred common Names", response = String.class) })

	public Response updatePreferredCommonName(@Context HttpServletRequest request,
			@PathParam("speciesId") String speciesId, @PathParam("commonNameId") String commonNameId) {
		try {

			Long sId = Long.parseLong(speciesId);
			Long pCname = Long.parseLong(commonNameId);
			CommonName result = speciesService.updatePrefferedCommonName(request, sId, pCname);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.COMMONNAME + "/{speciesId}/{commonNameId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "delete common Names", notes = "return common Names list", response = CommonName.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "unable to update the common Names", response = String.class) })

	public Response removeCommonName(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@PathParam("commonNameId") String commonNameId) {
		try {

			Long sId = Long.parseLong(speciesId);
			List<CommonName> result = speciesService.removeCommonName(request, sId, commonNameId);
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

	@POST
	@Path(ApiConstants.DELETE + ApiConstants.COMMENT + "/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Deletes a comment", notes = "Return the current activity", response = Activity.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Unable to log a comment", response = String.class) })

	public Response deleteCommnet(@Context HttpServletRequest request,
			@ApiParam(name = "commentData") CommentLoggingData commentDatas, @PathParam("commentId") String commentId) {
		try {
			Activity result = speciesService.removeSpeciesComment(request, commentDatas, commentId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@GET
	@Path(ApiConstants.CHECK + ApiConstants.SPECIES)
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "check using taxonId if species page is present", notes = "Returns the species Page Id", response = Long.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to fetch the data ", response = String.class) })

	public Response checkSpeciesPageExist(@Context HttpServletRequest request, @QueryParam("taxonId") String taxonId) {
		try {
			Long taxonomyId = Long.parseLong(taxonId);
			Long result = speciesService.checkSpeciesPageExist(request, taxonomyId);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NO_CONTENT).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.SAVE + ApiConstants.TAXONOMY)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "create taxonomy", notes = "Returns the taxonmyDefination", response = TaxonomyDefinition.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to create the taxonomy", response = String.class) })

	public Response createTaxonomy(@Context HttpServletRequest request,
			@ApiParam("taxonomySave") TaxonomySave taxonomySave) {
		try {
			TaxonomyDefinition result = speciesService.createTaxonomy(request, taxonomySave);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_IMPLEMENTED).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.ADD)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "create species", notes = "Returns the speciesId", response = Long.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to create the species", response = String.class) })

	public Response createSpecies(@Context HttpServletRequest request,
			@ApiParam(name = "createData") SpeciesCreateData createData) {
		try {
			Long result = speciesService.createSpeciesPage(request, createData);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.UPDATE + ApiConstants.SYNONYMS + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "add and update synonyms", notes = "Returns the synonyms list", response = TaxonomyDefinition.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to add and update the synonyms", response = String.class) })

	public Response addUpdateSynonyms(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@ApiParam(name = "synonymData") SynonymData synonymData) {
		try {
			List<TaxonomyDefinition> result = speciesService.updateAddSynonyms(request, speciesId, synonymData);
			if (result != null)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_MODIFIED).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@DELETE
	@Path(ApiConstants.REMOVE + ApiConstants.SYNONYMS + "/{speciesId}/{synonymId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "remove synonyms", notes = "Returns the Boolean data", response = TaxonomyDefinition.class, responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to remove the synonyms", response = String.class) })

	public Response removeSynonyms(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
			@PathParam("synonymId") String synonymId) {
		try {
			List<TaxonomyDefinition> result = speciesService.removeSynonyms(request, speciesId, synonymId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.PERMISSION + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Check the permission for species Page", notes = "Returns the Boolean value", response = SpeciesPermission.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "unable to fetch the permission", response = String.class) })

	public Response getSpeciesPagePermission(@Context HttpServletRequest request,
			@PathParam("speciesId") String speciesId) {
		try {
			Long sId = Long.parseLong(speciesId);
			SpeciesPermission result = speciesService.checkPermission(request, sId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.REQUEST)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser
	@ApiOperation(value = "Send request for permission over a taxonomyNode", notes = "sends mail to the permission", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to send the req", response = String.class) })

	public Response requestPermission(@Context HttpServletRequest request,
			@ApiParam(name = "permissionData") PermissionData permissionData) {
		try {
			Boolean result = speciesService.sendPermissionRequest(request, permissionData);
			if (result != null) {
				if (result)
					return Response.status(Status.OK).entity(result).build();
				return Response.status(Status.NOT_MODIFIED).build();
			}
			return Response.status(Status.NOT_FOUND).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.GRANT)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "validate the request for permission over a taxonomyId", notes = "checks the grants the permission", response = Boolean.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "uable to grant the permission", response = String.class) })

	public Response grantPermissionrequest(@Context HttpServletRequest request,
			@ApiParam(name = "encryptedKey") EncryptedKey encryptedKey) {
		try {
			Boolean result = speciesService.sendPermissionGrant(request, encryptedKey);
			if (result)
				return Response.status(Status.OK).entity(result).build();
			return Response.status(Status.NOT_IMPLEMENTED).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.FOLLOW + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Follow the species Page", notes = "Return the follow object", response = Follow.class)
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "uable to grant the permission", response = String.class) })

	public Response followSpecies(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId) {
		try {
			Long sId = Long.parseLong(speciesId);
			Follow result = speciesService.followRequest(request, sId);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.UNFOLLOW + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "unfollow the species Page", notes = "unfollow the species Page", response = Follow.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "uable to unfollow", response = String.class) })

	public Response unFollowSpecies(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId) {
		try {
			Long sId = Long.parseLong(speciesId);
			Follow result = speciesService.unFollowRequest(request, sId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@DELETE
	@Path(ApiConstants.REMOVE + "/{speciesId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "Remove the species page", notes = "return boolean", response = Boolean.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to remove the page", response = String.class) })

	public Response removeSpeciesPage(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId) {
		try {
			Long sId = Long.parseLong(speciesId);
			Boolean result = speciesService.removeSpeciesPage(request, sId);
			if (result)
				return Response.status(Status.OK).build();
			return Response.status(Status.NOT_MODIFIED).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.LIST + "/{index}/{type}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)

	@ApiOperation(value = "search the species for list page", notes = "return speceis list data", response = SpeciesListPageData.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to search", response = String.class) })

	public Response listSearch(@DefaultValue("extended_species") @PathParam("index") String index,
			@DefaultValue("_doc") @PathParam("type") String type,
			@DefaultValue(value = "0") @QueryParam("offset") Integer offset,
			@DefaultValue("10") @QueryParam("max") Integer max,
			@DefaultValue("species.dateCreated") @QueryParam("sort") String sortOn,
			@QueryParam("createdOnMaxDate") String createdOnMaxDate,
			@QueryParam("createdOnMinDate") String createdOnMinDate,
			@QueryParam("revisedOnMaxDate") String revisedOnMaxDate,
			@QueryParam("revisedOnMinDate") String revisedOnMinDate,
			@DefaultValue("") @QueryParam("userGroupList") String userGroupList,
			@DefaultValue("") @QueryParam("user") String user, @QueryParam("taxon") String taxonId,
			@DefaultValue("") @QueryParam("sGroup") String sGroup,
			@DefaultValue("") @QueryParam("scientificName") String scientificName,
			@DefaultValue("") @QueryParam("commonName") String commonName,
			@DefaultValue("") @QueryParam("mediaFilter") String mediaFilter,
			@DefaultValue("") @QueryParam("reference") String reference,
			@DefaultValue("") @QueryParam("featured") String featured,
			@DefaultValue("") @QueryParam("rank") String rank, @DefaultValue("") @QueryParam("path") String path,
			@DefaultValue("") @QueryParam("description") String description,
			@DefaultValue("") @QueryParam("attributes") String attributes,
			@DefaultValue("40") @QueryParam("colorRange") Integer colorRange,
			@DefaultValue("grid") @QueryParam("view") String view, @QueryParam("bulkAction") String bulkAction,
			@QueryParam("selectAll") Boolean selectAll, @QueryParam("bulkUsergroupIds") String bulkUsergroupIds,
			@QueryParam("bulkSpeciesIds") String bulkSpeciesIds, @Context HttpServletRequest request,
			@Context UriInfo uriInfo) {
		try {

			MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
			Map<String, List<String>> traitParams = queryParams.entrySet().stream()
					.filter(entry -> entry.getKey().startsWith("trait"))
					.collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

			MapBoundParams mapBoundsParams = new MapBoundParams();
			mapBoundsParams.setBounds(null);

			MapSearchParams mapSearchParams = new MapSearchParams();
			mapSearchParams.setFrom(offset);
			mapSearchParams.setLimit(max);
			mapSearchParams.setSortOn(sortOn);
			mapSearchParams.setSortType(SortTypeEnum.DESC);
			mapSearchParams.setMapBoundParams(mapBoundsParams);

			MapSearchQuery mapSearchQuery = esUtility.getMapSearchQuery(scientificName, commonName, sGroup,
					userGroupList, taxonId, mediaFilter, createdOnMaxDate, createdOnMinDate, revisedOnMinDate,
					revisedOnMaxDate, rank, path, user, attributes, reference, description, colorRange, traitParams,
					mapSearchParams);

			if (view.equalsIgnoreCase("list") || view.equalsIgnoreCase("grid")) {
				MapAggregationResponse aggregationResult = null;

				aggregationResult = listService.mapAggregate(index, type, scientificName, commonName, sGroup,
						userGroupList, taxonId, mediaFilter, createdOnMaxDate, createdOnMinDate, revisedOnMinDate,
						revisedOnMaxDate, rank, path, user, attributes, reference, description, colorRange, traitParams,
						mapSearchParams);

				SpeciesListPageData result = listService.searchList(index, type, mapSearchQuery, aggregationResult);

				return Response.status(Status.OK).entity(result).build();
			} else if ((Boolean.FALSE.equals(selectAll) && bulkSpeciesIds != null && !bulkAction.isEmpty()
					&& !bulkSpeciesIds.isEmpty() && bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()
					&& view.equalsIgnoreCase("bulkMapping"))
					|| (Boolean.TRUE.equals(selectAll) && bulkUsergroupIds != null && !bulkUsergroupIds.isEmpty()
							&& !bulkAction.isEmpty() && view.equalsIgnoreCase("bulkMapping"))) {
				mapSearchParams.setFrom(0);
				mapSearchParams.setLimit(100000);

				if (request.getHeader(HttpHeaders.AUTHORIZATION) == null) {
					return Response.status(Status.BAD_REQUEST).build();
				}

				SpeciesBulkMappingThread bulkMappingThread = new SpeciesBulkMappingThread(selectAll, bulkAction,
						bulkSpeciesIds, bulkUsergroupIds, mapSearchQuery, ugService, index, type, esService, request,
						headers, objectMapper, esUpdate);

				Thread thread = new Thread(bulkMappingThread);
				thread.start();
				return Response.status(Status.OK).build();
			}

			return Response.status(Status.OK).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path(ApiConstants.ADD + "/reference" + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "add reference to a species Page", notes = "add common reference", response = com.strandls.species.pojo.Reference.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "uable to unfollow", response = String.class) })

	public Response createReference(@Context HttpServletRequest request,
			@ApiParam(name = "referenceCreateData") List<ReferenceCreateData> referenceCreateData,
			@PathParam("speciesId") String speciesId) {
		try {
			List<com.strandls.species.pojo.Reference> result = speciesService.createReference(request,
					Long.parseLong(speciesId), referenceCreateData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@PUT
	@Path(ApiConstants.UPDATE + "/reference" + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "edit references of a species Page", notes = "edit common reference", response = com.strandls.species.pojo.Reference.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "uable to unfollow", response = String.class) })

	public Response updateReference(@Context HttpServletRequest request,
			@ApiParam(name = "reference") com.strandls.species.pojo.Reference reference,
			@PathParam("speciesId") String speciesId) {
		try {
			com.strandls.species.pojo.Reference result = speciesService.editReference(request,
					Long.parseLong(speciesId), reference);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@PUT
	@Path(ApiConstants.DELETE + "/reference" + "/{referenceId}")
	@Produces(MediaType.APPLICATION_JSON)

	@ValidateUser

	@ApiOperation(value = "delete a reference of a species Page", notes = "delete common reference", response = com.strandls.species.pojo.Reference.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "unable to delete", response = String.class) })

	public Response deleteReference(@Context HttpServletRequest request, @PathParam("referenceId") String referenceId) {
		try {
			com.strandls.species.pojo.Reference result = speciesService.deleteReference(request,
					Long.valueOf(referenceId));
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

}
