package com.strandls.species.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.core.Response.Status;

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
import com.strandls.species.pojo.FieldCreateData;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldNew;
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
import com.strandls.userGroup.controller.UserGroupServiceApi;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserGroupSpeciesCreateData;
import com.strandls.species.pojo.FieldTranslation;
import com.strandls.species.pojo.FieldTranslationUpdateData;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Tag(name = "Species Services", description = "APIs for managing species information")
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
	private UserGroupServiceApi ugService;

	@Inject
	private EsServicesApi esService;

	@Inject
	private Headers headers;

	@Inject
	private ESUpdate esUpdate;

	@GET
	@Path(ApiConstants.PING)
	@Produces(MediaType.TEXT_PLAIN)
	@Operation(
		summary = "Ping endpoint",
		description = "Returns PONG to check if the service is alive"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Success",
			content = @Content(schema = @Schema(type = "string")))
	})
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

	@POST
	@Path(ApiConstants.SHOW + "/{speciesId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "provide the show page of speices",
		description = "Returns the species Show page",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "User group information", required = true,
			content = @Content(schema = @Schema(implementation = UserGroupIbp.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(schema = @Schema(implementation = ShowSpeciesPage.class))),
			@ApiResponse(responseCode = "400", description = "unable to fetch the show page")
		}
	)
	public Response getSpeciesShowPage(@PathParam("speciesId") String sId, UserGroupIbp userGroupIbp) {

		try {
			Long speciesId = Long.parseLong(sId);
			ShowSpeciesPage result = speciesService.showSpeciesPageFromES(speciesId, userGroupIbp);
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
	@Operation(
		summary = "Forms the field structure",
		description = "Returns the field structure",
		responses = {
			@ApiResponse(responseCode = "200", description = "List of field structure",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldRender.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to get the fields framework",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response renderFields(
		@QueryParam("langId") String langId,
		@QueryParam("userGroupId") String userGroupId) {

		try {
			Long languageId = null;
			if (langId != null)
				languageId = Long.parseLong(langId);
			List<FieldRender> result = speciesService.getFields(languageId, userGroupId);
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.FIELDS + ApiConstants.LEAFNODES)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
    summary = "Get all the fields with no children",
    description = "Returns the leaf node fields",
    responses = {
        @ApiResponse(
            responseCode = "200",
            description = "List of leaf fields",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldNew.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Unable to get the leaf node fields",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    }
)
	public Response getLeafNodeFields() {
		try {
			List<FieldNew> result = speciesService.fetchLeafNodes();
			return Response.status(Status.OK).entity(result).build();

		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TRAITS + ApiConstants.ALL + "/{languageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all species traits field-wise",
		description = "Returns all the traits CategoryWise",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "List of species traits",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesTrait.class)))
			),
			@ApiResponse(
				responseCode = "400",
				description = "Unable to fetch the traits",
				content = @Content(schema = @Schema(implementation = String.class))
			)
		}
	)
	public Response getAllSpeciesTraits(
		@PathParam("languageId") String languageId) {
		Long language = Long.parseLong(languageId);
		try {
			List<SpeciesTrait> result = speciesService.getAllSpeciesTraits(language);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TRAITS + "/{languageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all the species traits field wise",
		description = "Returns all the traits CategoryWise",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "List of traits",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesTrait.class)))
			),
			@ApiResponse(
				responseCode = "400",
				description = "Unable to fetch the traits",
				content = @Content(schema = @Schema(implementation = String.class))
			)
		}
	)
	public Response getAllTraits(
		@PathParam("languageId") String languageId) {
		Long language = Long.parseLong(languageId);
		try {
			List<SpeciesTrait> result = speciesService.getAllTraits(language);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path(ApiConstants.TRAITS + ApiConstants.TAXONOMY + "/{taxonomyId}/{languageId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all the species traits field wise by taxonomyId",
		description = "Returns all the traits CategoryWise",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Traits for taxonomy",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesTrait.class)))
			),
			@ApiResponse(
				responseCode = "400",
				description = "Unable to fetch the traits",
				content = @Content(schema = @Schema(implementation = String.class))
			)
		}
	)
	public Response getSpeciesTraitsByTaxonomy(
		@PathParam("taxonomyId") String taxonomyId,
		@PathParam("languageId") String languageId) {
		try {
			Long language = Long.parseLong(languageId);
			Long taxon = Long.parseLong(taxonomyId);
			List<SpeciesTrait> result = speciesService.getSpeciesTraitsByTaxonomyId(taxon, language);
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
	@Operation(
		summary = "update the species usergroup mapping",
		description = "Return the associated userGroup",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "List of user group IDs", required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserGroupIbp.class)))),
			@ApiResponse(responseCode = "400", description = "unable to fetch the data")
		}
	)
	public Response updateUserGroupSpecies(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId, List<Long> userGroupList) {

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
	@Operation(
		summary = "Feature a species",
		description = "Returns all the featuring",
		requestBody = @RequestBody(description = "Featured creation data", required = true,
			content = @Content(schema = @Schema(implementation = FeaturedCreate.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = Featured.class)))),
			@ApiResponse(responseCode = "404", description = "Unable to feature the species")
		}
	)
	public Response createFeatured(@Context HttpServletRequest request, FeaturedCreate featuredCreate) {

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
	@Operation(
		summary = "Unfeatured a species",
		description = "Returns all the featuring",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "List of user group IDs", required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = Featured.class)))),
			@ApiResponse(responseCode = "404", description = "Unable to feature the species")
		}
	)
	public Response unFeatured(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId, List<Long> userGroupList) {

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
	@Operation(
		summary = "update species Traits",
		description = "Return all the traits",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true),
			@Parameter(name = "traitId", description = "Trait ID", required = true)
		},
		requestBody = @RequestBody(description = "Traits update data", required = true,
			content = @Content(schema = @Schema(implementation = FactsUpdateData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = FactValuePair.class)))),
			@ApiResponse(responseCode = "404", description = "unable to update the traits")
		}
	)
	public Response updateTraits(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId,
                             @PathParam("traitId") String traitId, FactsUpdateData factsUpdateData) {

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
	@Operation(
		summary = "update species field",
		description = "return species field data",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "Species field update data", required = true,
			content = @Content(schema = @Schema(implementation = SpeciesFieldUpdateData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(schema = @Schema(implementation = SpeciesFieldData.class))),
			@ApiResponse(responseCode = "404", description = "unable to update the species Field")
		}
	)
	public Response updateSpeciesField(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId, SpeciesFieldUpdateData sfUpdateData) {

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
	@Operation(
		summary = "Delete species field",
		description = "Deletes a species field and returns success/failure",
		responses = {
			@ApiResponse(responseCode = "200", description = "Species field deleted"),
			@ApiResponse(responseCode = "404", description = "Unable to delete the species field",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response removeSpeciesField(
		@Context HttpServletRequest request,
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
	@Operation(
		summary = "update and add common Names",
		description = "return common Names list",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "Common names update data", required = true,
			content = @Content(schema = @Schema(implementation = CommonNamesData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommonName.class)))),
			@ApiResponse(responseCode = "404", description = "unable to update the common Names")
		}
	)
	public Response updateAddCommonName(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId, CommonNamesData commonNamesData) {

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
	@Operation(
		summary = "Update preferred common names",
		description = "Return preferred common Name",
		responses = {
			@ApiResponse(responseCode = "200", description = "Preferred common name updated",
				content = @Content(schema = @Schema(implementation = CommonName.class))),
			@ApiResponse(responseCode = "404", description = "Unable to update the preferred common names",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response updatePreferredCommonName(
		@Context HttpServletRequest request,
		@PathParam("speciesId") String speciesId,
		@PathParam("commonNameId") String commonNameId) {

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
	@Operation(
		summary = "Delete common names",
		description = "Return common names list",
		responses = {
			@ApiResponse(responseCode = "200", description = "Updated common names list",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommonName.class)))),
			@ApiResponse(responseCode = "404", description = "Unable to update the common names",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response removeCommonName(
		@Context HttpServletRequest request,
		@PathParam("speciesId") String speciesId,
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
	@Operation(
		summary = "Get all observation resources",
		description = "Returns all observation-linked resources",
		responses = {
			@ApiResponse(responseCode = "200", description = "Observation resources list",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesPull.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to get the resources",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response getObservationResources(
		@PathParam("speciesId") @Parameter(description = "Species ID") String speciesId,
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
	@Operation(
		summary = "Get all the species resources",
		description = "Returns the species resources",
		responses = {
			@ApiResponse(responseCode = "200", description = "Species resource list",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceData.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to get the resources",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response getEditSpeciesResource(
		@Context HttpServletRequest request,
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
	@Operation(
		summary = "update the species resources",
		description = "Returns the species resources",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "List of species resource pre-data", required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpeciesResourcesPreData.class)))),
		responses = {
			@ApiResponse(responseCode = "200", description = "OK",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = ResourceData.class)))),
			@ApiResponse(responseCode = "400", description = "unable to update the resources")
		}
	)
	public Response updateSpeciesResource(@Context HttpServletRequest request, @PathParam("speciesId") String speciesId, List<SpeciesResourcesPreData> preDataList) {

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
	@Operation(
		summary = "Add species Comment",
		description = "Return the comment activity",
		requestBody = @RequestBody(description = "Comment data", required = true,
			content = @Content(schema = @Schema(implementation = CommentLoggingData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Comment added",
				content = @Content(schema = @Schema(implementation = Activity.class))),
			@ApiResponse(responseCode = "400", description = "unable to log the comment")
		}
	)
	public Response addSpeciesComment(@Context HttpServletRequest request, CommentLoggingData loggingData) {
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
	@Operation(
		summary = "Deletes a comment",
		description = "Return the current activity",
		parameters = {
			@Parameter(name = "commentId", description = "Comment ID", required = true)
		},
		requestBody = @RequestBody(description = "Comment data", required = true,
			content = @Content(schema = @Schema(implementation = CommentLoggingData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Comment deleted",
				content = @Content(schema = @Schema(implementation = Activity.class))),
			@ApiResponse(responseCode = "400", description = "Unable to log a comment")
		}
	)
	public Response deleteComment(@Context HttpServletRequest request,
                              CommentLoggingData commentDatas,
                              @PathParam("commentId") String commentId) {
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
	@Operation(
		summary = "Check using taxonId if species page is present",
		description = "Returns the species Page Id",
		responses = {
			@ApiResponse(responseCode = "200", description = "Species page ID found",
				content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(responseCode = "400", description = "Bad request",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response checkSpeciesPageExist(
		@Context HttpServletRequest request,
		@QueryParam("taxonId") String taxonId) {

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
	@Operation(
		summary = "create taxonomy",
		description = "Returns the taxonmyDefination",
		requestBody = @RequestBody(description = "Taxonomy save object", required = true,
			content = @Content(schema = @Schema(implementation = TaxonomySave.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Taxonomy created",
				content = @Content(schema = @Schema(implementation = TaxonomyDefinition.class))),
			@ApiResponse(responseCode = "400", description = "unable to create the taxonomy")
		}
	)
	public Response createTaxonomy(@Context HttpServletRequest request, TaxonomySave taxonomySave) {
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
	@Operation(
		summary = "create species",
		description = "Returns the speciesId",
		requestBody = @RequestBody(description = "Species create data", required = true,
			content = @Content(schema = @Schema(implementation = SpeciesCreateData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Species created",
				content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(responseCode = "400", description = "unable to create the species")
		}
	)
	public Response createSpecies(@Context HttpServletRequest request, SpeciesCreateData createData) {
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
	@Operation(
		summary = "add and update synonyms",
		description = "Returns the synonyms list",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "Synonym data", required = true,
			content = @Content(schema = @Schema(implementation = SynonymData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Synonyms updated",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaxonomyDefinition.class)))),
			@ApiResponse(responseCode = "400", description = "unable to add and update the synonyms")
		}
	)
	public Response addUpdateSynonyms(@Context HttpServletRequest request,
									@PathParam("speciesId") String speciesId,
									SynonymData synonymData) {
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
@Operation(
    summary = "Remove synonyms",
    description = "Returns the boolean data",
    responses = {
        @ApiResponse(responseCode = "200", description = "Synonym removed",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaxonomyDefinition.class)))),
        @ApiResponse(responseCode = "400", description = "Unable to remove synonyms",
            content = @Content(schema = @Schema(implementation = String.class)))
    }
)
public Response removeSynonyms(
    @Context HttpServletRequest request,
    @PathParam("speciesId") String speciesId,
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
	@Operation(
		summary = "Check the permission for species Page",
		description = "Returns the Boolean value",
		responses = {
			@ApiResponse(responseCode = "200", description = "Permission info",
				content = @Content(schema = @Schema(implementation = SpeciesPermission.class))),
			@ApiResponse(responseCode = "400", description = "Unable to fetch the permission",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response getSpeciesPagePermission(
		@Context HttpServletRequest request,
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
	@Operation(
		summary = "Send request for permission over a taxonomyNode",
		description = "sends mail to the permission",
		requestBody = @RequestBody(description = "Permission request data", required = true,
			content = @Content(schema = @Schema(implementation = PermissionData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Request sent",
				content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "unable to send the req"),
			@ApiResponse(responseCode = "404", description = "not found")
		}
	)
	public Response requestPermission(@Context HttpServletRequest request, PermissionData permissionData) {

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
	@Operation(
		summary = "validate the request for permission over a taxonomyId",
		description = "checks the grants the permission",
		requestBody = @RequestBody(description = "Encrypted permission key", required = true,
			content = @Content(schema = @Schema(implementation = EncryptedKey.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Permission granted",
				content = @Content(schema = @Schema(implementation = Boolean.class))),
			@ApiResponse(responseCode = "400", description = "uable to grant the permission"),
			@ApiResponse(responseCode = "501", description = "Not implemented")
		}
	)
	public Response grantPermissionrequest(@Context HttpServletRequest request, EncryptedKey encryptedKey) {


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
	@Operation(
		summary = "Follow the species page",
		description = "Return the follow object",
		responses = {
			@ApiResponse(responseCode = "200", description = "Followed species",
				content = @Content(schema = @Schema(implementation = Follow.class))),
			@ApiResponse(responseCode = "400", description = "Unable to follow",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response followSpecies(
		@Context HttpServletRequest request,
		@PathParam("speciesId") String speciesId) {

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
@Operation(
    summary = "Unfollow the species page",
    description = "Unfollowed the species page",
    responses = {
        @ApiResponse(responseCode = "200", description = "Unfollowed species",
            content = @Content(schema = @Schema(implementation = Follow.class))),
        @ApiResponse(responseCode = "400", description = "Unable to unfollow",
            content = @Content(schema = @Schema(implementation = String.class)))
    }
)
public Response unFollowSpecies(
    @Context HttpServletRequest request,
    @PathParam("speciesId") String speciesId) {

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
@Operation(
    summary = "Remove a species page",
    description = "Return boolean",
    responses = {
        @ApiResponse(responseCode = "200", description = "Species page deleted"),
        @ApiResponse(responseCode = "400", description = "Unable to remove the page",
            content = @Content(schema = @Schema(implementation = String.class)))
    }
)
public Response removeSpeciesPage(
    @Context HttpServletRequest request,
    @PathParam("speciesId") String speciesId) {

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
	@Operation(
		summary = "Search the species for list page",
		description = "Return species list data",
		responses = {
			@ApiResponse(responseCode = "200", description = "Species list search results",
				content = @Content(schema = @Schema(implementation = SpeciesListPageData.class))),
			@ApiResponse(responseCode = "400", description = "Unable to search",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response listSearch(
		@DefaultValue("extended_species") @PathParam("index") String index,
		@DefaultValue("_doc") @PathParam("type") String type,
		@DefaultValue("0") @QueryParam("offset") Integer offset,
		@DefaultValue("10") @QueryParam("max") Integer max,
		@DefaultValue("species.dateCreated") @QueryParam("sort") String sortOn,
		@QueryParam("createdOnMaxDate") String createdOnMaxDate,
		@QueryParam("createdOnMinDate") String createdOnMinDate,
		@QueryParam("revisedOnMaxDate") String revisedOnMaxDate,
		@QueryParam("revisedOnMinDate") String revisedOnMinDate,
		@DefaultValue("") @QueryParam("userGroupList") String userGroupList,
		@DefaultValue("") @QueryParam("user") String user,
		@QueryParam("taxon") String taxonId,
		@DefaultValue("") @QueryParam("sGroup") String sGroup,
		@DefaultValue("") @QueryParam("scientificName") String scientificName,
		@DefaultValue("") @QueryParam("commonName") String commonName,
		@DefaultValue("") @QueryParam("mediaFilter") String mediaFilter,
		@DefaultValue("") @QueryParam("reference") String reference,
		@DefaultValue("") @QueryParam("featured") String featured,
		@DefaultValue("") @QueryParam("rank") String rank,
		@DefaultValue("") @QueryParam("path") String path,
		@DefaultValue("") @QueryParam("description") String description,
		@DefaultValue("") @QueryParam("attributes") String attributes,
		@DefaultValue("40") @QueryParam("colorRange") Integer colorRange,
		@DefaultValue("grid") @QueryParam("view") String view,
		@QueryParam("bulkAction") String bulkAction,
		@QueryParam("selectAll") Boolean selectAll,
		@QueryParam("bulkUsergroupIds") String bulkUsergroupIds,
		@QueryParam("bulkSpeciesIds") String bulkSpeciesIds,
		@Context HttpServletRequest request,
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
	@Operation(
		summary = "add reference to a species Page",
		description = "add common reference",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "List of reference create data", required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReferenceCreateData.class)))),
		responses = {
			@ApiResponse(responseCode = "200", description = "References created",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = com.strandls.species.pojo.Reference.class)))),
			@ApiResponse(responseCode = "400", description = "uable to unfollow")
		}
	)
	public Response createReference(@Context HttpServletRequest request,
									List<ReferenceCreateData> referenceCreateData,
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
	@Operation(
		summary = "edit references of a species Page",
		description = "edit common reference",
		parameters = {
			@Parameter(name = "speciesId", description = "Species ID", required = true)
		},
		requestBody = @RequestBody(description = "Reference to be updated", required = true,
			content = @Content(schema = @Schema(implementation = com.strandls.species.pojo.Reference.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Reference updated",
				content = @Content(schema = @Schema(implementation = com.strandls.species.pojo.Reference.class))),
			@ApiResponse(responseCode = "400", description = "uable to unfollow")
		}
	)
	public Response updateReference(@Context HttpServletRequest request,
									com.strandls.species.pojo.Reference reference,
									@PathParam("speciesId") String speciesId) {
		try {
			com.strandls.species.pojo.Reference result = speciesService.editReference(
				request, Long.parseLong(speciesId), reference);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@PUT
	@Path(ApiConstants.DELETE + "/reference" + "/{referenceId}")
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(
		summary = "Delete a reference of a species Page",
		description = "Delete common reference",
		responses = {
			@ApiResponse(responseCode = "200", description = "Reference deleted",
				content = @Content(schema = @Schema(implementation = com.strandls.species.pojo.Reference.class))),
			@ApiResponse(responseCode = "400", description = "Unable to delete reference",
				content = @Content(schema = @Schema(implementation = String.class)))
		}
	)
	public Response deleteReference(
		@Context HttpServletRequest request,
		@PathParam("referenceId") String referenceId) {

		try {
			com.strandls.species.pojo.Reference result = speciesService.deleteReference(
				request, Long.valueOf(referenceId));
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}

	}

	@POST
	@Path("/create/field")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(
		summary = "Create a new species field",
		description = "Returns the created field",
		requestBody = @RequestBody(description = "Field Data to create", required = true,
			content = @Content(schema = @Schema(implementation = FieldCreateData.class))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Field created",
				content = @Content(schema = @Schema(implementation = FieldNew.class))),
			@ApiResponse(responseCode = "400", description = "Unable to create the field"),
			@ApiResponse(responseCode = "401", description = "User not authorized to create field")
		}
	)
	public Response createField(@Context HttpServletRequest request, FieldCreateData fieldData) {
		try {
			FieldNew result = speciesService.createField(request, fieldData);
			return Response.status(Status.OK).entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/field/{fieldId}/translations")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get all translations for a field",
		description = "Returns list of field headers for all available languages",
		parameters = {
			@Parameter(name = "fieldId", description = "Field ID", required = true)
		},
		responses = {
			@ApiResponse(responseCode = "200", description = "Translations found",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldHeader.class))))
		}
	)
	public Response getFieldTranslations(@PathParam("fieldId") Long fieldId) {
		try {
			List<FieldHeader> translations = speciesService.getFieldTranslations(fieldId);
			return Response.ok().entity(translations).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}


	@GET
	@Path("/field/{fieldId}/translation/{languageId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
		summary = "Get specific translation for a field",
		description = "Returns field header for the specified language",
		parameters = {
			@Parameter(name = "fieldId", description = "Field ID", required = true),
			@Parameter(name = "languageId", description = "Language ID", required = true)
		},
		responses = {
			@ApiResponse(responseCode = "200", description = "Translation found",
				content = @Content(schema = @Schema(implementation = FieldHeader.class))),
			@ApiResponse(responseCode = "404", description = "Translation not found")
		}
	)
	public Response getFieldTranslation(@PathParam("fieldId") Long fieldId,
										@PathParam("languageId") Long languageId) {
		try {
			FieldHeader translation = speciesService.getFieldTranslation(fieldId, languageId);
			if (translation == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok().entity(translation).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@PUT
	@Path("/field/translations")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ValidateUser
	@Operation(
		summary = "Update translations for multiple fields",
		description = "Returns updated field headers",
		requestBody = @RequestBody(description = "List of fields with their translations", required = true,
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldTranslationUpdateData.class)))),
		responses = {
			@ApiResponse(responseCode = "200", description = "Translations updated",
				content = @Content(array = @ArraySchema(schema = @Schema(implementation = FieldHeader.class)))),
			@ApiResponse(responseCode = "400", description = "Unable to update translations"),
			@ApiResponse(responseCode = "401", description = "User not authorized to update translations")
		}
	)
	public Response updateFieldTranslations(@Context HttpServletRequest request,
											List<FieldTranslationUpdateData> translationData) {
		try {
			List<FieldHeader> result = speciesService.updateFieldTranslations(request, translationData);
			return Response.ok().entity(result).build();
		} catch (Exception e) {
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

}
