/**
 * 
 */
package com.strandls.species.service.Impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.activity.pojo.MailData;
import com.strandls.activity.pojo.SpeciesMailData;
import com.strandls.activity.pojo.UserGroupActivity;
import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.document.controllers.DocumentServiceApi;
import com.strandls.document.pojo.DocumentMeta;
import com.strandls.esmodule.ApiException;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.ObservationInfo;
import com.strandls.esmodule.pojo.ObservationMapInfo;
import com.strandls.observation.controller.ObservationServiceApi;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.Resource;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.resource.pojo.SpeciesResourcePulling;
import com.strandls.species.Headers;
import com.strandls.species.dao.ContributorDao;
import com.strandls.species.dao.FieldHeaderDao;
import com.strandls.species.dao.FieldNewDao;
import com.strandls.species.dao.ReferenceDao;
import com.strandls.species.dao.SpeciesDao;
import com.strandls.species.dao.SpeciesFieldAudienceTypeDao;
import com.strandls.species.dao.SpeciesFieldContributorDao;
import com.strandls.species.dao.SpeciesFieldDao;
import com.strandls.species.dao.SpeciesFieldLicenseDao;
import com.strandls.species.dao.SpeciesFieldUserDao;
import com.strandls.species.es.util.SpeciesIndex;
import com.strandls.species.pojo.Contributor;
import com.strandls.species.pojo.FieldCreateData;
import com.strandls.species.pojo.FieldDisplay;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldHeaderData;
import com.strandls.species.pojo.FieldNew;
import com.strandls.species.pojo.FieldNewExtended;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.FieldTranslation;
import com.strandls.species.pojo.FieldTranslationUpdateData;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.ReferenceCreateData;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.Species;
import com.strandls.species.pojo.SpeciesCreateData;
import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.pojo.SpeciesFieldAudienceType;
import com.strandls.species.pojo.SpeciesFieldContributor;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldLicense;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesFieldUser;
import com.strandls.species.pojo.SpeciesPermission;
import com.strandls.species.pojo.SpeciesPullData;
import com.strandls.species.pojo.SpeciesResourceData;
import com.strandls.species.pojo.SpeciesResourcesPreData;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.species.service.SpeciesServices;
import com.strandls.species.util.AppUtil;
import com.strandls.species.util.PropertyFileUtil;
import com.strandls.taxonomy.controllers.CommonNameServicesApi;
import com.strandls.taxonomy.controllers.SpeciesServicesApi;
import com.strandls.taxonomy.controllers.TaxonomyPermissionServiceApi;
import com.strandls.taxonomy.controllers.TaxonomyServicesApi;
import com.strandls.taxonomy.controllers.TaxonomyTreeServicesApi;
import com.strandls.taxonomy.pojo.BreadCrumb;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.CommonNamesData;
import com.strandls.taxonomy.pojo.EncryptedKey;
import com.strandls.taxonomy.pojo.PermissionData;
import com.strandls.taxonomy.pojo.SpeciesGroup;
import com.strandls.taxonomy.pojo.SynonymData;
import com.strandls.taxonomy.pojo.TaxonomicNames;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.taxonomy.pojo.TaxonomySave;
import com.strandls.traits.controller.TraitsServiceApi;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.FactsUpdateData;
import com.strandls.traits.pojo.TraitsValuePair;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.Follow;
import com.strandls.user.pojo.UserIbp;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.FeaturedCreateData;
import com.strandls.userGroup.pojo.SpeciesFieldValuesDTO;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserGroupMappingCreateData;
import com.strandls.userGroup.pojo.UserGroupSpeciesCreateData;
import com.strandls.userGroup.pojo.UserGroupSpeciesFieldMeta;

import net.minidev.json.JSONArray;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesServiceImpl implements SpeciesServices {

	private final Logger logger = LoggerFactory.getLogger(SpeciesServiceImpl.class);

	@Inject
	private Headers headers;

//	Injection of Dao

	@Inject
	private ContributorDao contributorDao;

	@Inject
	private FieldNewDao fieldNewDao;

	@Inject
	private FieldHeaderDao fieldHeaderDao;

	@Inject
	private ReferenceDao referenceDao;

	@Inject
	private SpeciesDao speciesDao;

	@Inject
	private SpeciesFieldDao speciesFieldDao;

	@Inject
	private SpeciesFieldUserDao sfUserDao;

	@Inject
	private SpeciesFieldAudienceTypeDao sfAudienceTypeDao;

	@Inject
	private SpeciesFieldContributorDao sfContributorDao;

	@Inject
	private SpeciesFieldLicenseDao sfLicenseDao;

//	injection of services

	@Inject
	private ActivitySerivceApi activityService;

	@Inject
	private EsServicesApi esService;

	@Inject
	private DocumentServiceApi documentService;

	@Inject
	private CommonNameServicesApi commonNameService;

	@Inject
	private LogActivities logActivity;

	@Inject
	private ObservationServiceApi observationService;

	@Inject
	private ResourceServicesApi resourceServices;

	@Inject
	private SpeciesHelper speciesHelper;

	@Inject
	private SpeciesServicesApi sgroupServices;

	@Inject
	private UserServiceApi userService;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private TaxonomyPermissionServiceApi taxPermissionService;

	@Inject
	private TaxonomyServicesApi taxonomyService;

	@Inject
	private TaxonomyTreeServicesApi taxonomyTreeServices;

	@Inject
	private TraitsServiceApi traitService;

	@Inject
	private ObjectMapper om;

	private Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	private String blackList = PropertyFileUtil.fetchProperty("config.properties", "blackListSFId");

	private List<Long> blackListSFId = Arrays.asList(blackList.split(",")).stream().map(s -> Long.parseLong(s.trim()))
			.collect(Collectors.toList());

	public enum ReferenceOperation {
		ADD, UPDATE
	}

	@Override
	public ShowSpeciesPage showSpeciesPage(Long speciesId) {
		try {
			List<ResourceData> resourceData = null;

			Species species = speciesDao.findById(speciesId);
			if (!species.getIsDeleted()) {

				// preffered Common name
				CommonName prefferedCommonName = commonNameService.getPrefferedCommanName(species.getTaxonConceptId());

				// species group
				SpeciesGroup speciesGroup = sgroupServices.getGroupId(species.getTaxonConceptId());

				// resource data
				resourceData = resourceServices.getImageResource("SPECIES", species.getId().toString());

				// traits
				List<FactValuePair> facts = traitService.getFacts("species.Species", speciesId.toString());

				// species Field
				List<SpeciesField> speciesFields = speciesFieldDao.findBySpeciesId(speciesId);

				List<SpeciesFieldData> fieldData = new ArrayList<SpeciesFieldData>();

				List<Reference> referencesList = referenceDao.findBySpeciesId(speciesId);

				// segregating on the basis of multiple data
				for (SpeciesField speciesField : speciesFields) {

					if (!blackListSFId.contains(speciesField.getFieldId())) {
						SpeciesFieldData speciesFieldData = getSpeciesFieldData(speciesField);
						if (speciesFieldData != null)
							fieldData.add(speciesFieldData);

					}

				}

				List<BreadCrumb> breadCrumbs = taxonomyTreeServices
						.getTaxonomyBreadCrumb(species.getTaxonConceptId().toString());

				TaxonomyDefinition taxonomyDefinition = taxonomyService
						.getTaxonomyConceptName(species.getTaxonConceptId().toString());

				List<DocumentMeta> documentMetaList = documentService
						.getDocumentByTaxonConceptId(species.getTaxonConceptId().toString());

				List<UserGroupIbp> userGroupList = ugService.getSpeciesUserGroup(speciesId.toString());
				List<Featured> featured = ugService.getAllFeatured("species.Species", speciesId.toString());

				// common name and synonyms
				TaxonomicNames names = taxonomyService.getNames(species.getTaxonConceptId().toString());

				// temporal data
				ObservationInfo observationInfo = esService.getObservationInfo("extended_observation", "_doc",
						species.getTaxonConceptId().toString(), false);

				Map<String, Long> temporalData = observationInfo.getMonthAggregation();

				ShowSpeciesPage showSpeciesPage = new ShowSpeciesPage(species, prefferedCommonName, speciesGroup,
						breadCrumbs, taxonomyDefinition, resourceData, fieldData, facts, userGroupList, featured, names,
						temporalData, documentMetaList, referencesList);

				return showSpeciesPage;

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Removes objects from the list where all fields are null recursively
	 * 
	 * @param list The list to process
	 * @param <T>  The type of objects in the list
	 */
	public <T> void removeNullObjects(List<T> list) {
		int initialSize = list.size();
		list.removeIf(this::areAllFieldsNullRecursive);

		int removedCount = initialSize - list.size();
		logger.info("Removed " + removedCount + " objects from the list");
	}

	public void speciesEsUpdate(ShowSpeciesPage showData, String speciesId) {
		MapDocument document = new MapDocument();
		try {
			String payload = om.writeValueAsString(showData);
			JsonNode rootNode = om.readTree(payload);
			if (showData.getTaxonomyDefinition().getDefaultHierarchy() != null
					&& !showData.getTaxonomyDefinition().getDefaultHierarchy().isEmpty()) {
				JsonNode child = ((ObjectNode) rootNode).get("taxonomyDefinition");
				((ObjectNode) child).replace("defaultHierarchy", null);
			}
			document.setDocument(om.writeValueAsString(rootNode));
			esService.create(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(),
					showData.getSpecies().getId().toString(), document);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private boolean areAllFieldsNullRecursive(Object obj) {
		if (obj == null) {
			return true;
		}

		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object value = field.get(obj);
				if (value != null) {
					if (value instanceof Collection) {
						// Check if the collection is empty
						if (!((Collection<?>) value).isEmpty()) {
							logger.info("Field " + field.getName() + " is a non-empty collection");
							return false;
						}
					} else if (value.getClass().getPackage() != null
							&& value.getClass().getPackage().getName().startsWith("java")) {
						// For Java standard classes, just check if they're non-null
						logger.info("Field " + field.getName() + " is not null: " + value);
						return false;
					} else {
						// For custom classes, recursively check their fields
						if (!areAllFieldsNullRecursive(value)) {
							return false;
						}
					}
				}
			} catch (IllegalAccessException e) {
				logger.warn("Cannot access field " + field.getName() + ": " + e.getMessage());
			}
		}

		return true;
	}

	@Override
	public ShowSpeciesPage showSpeciesPageFromES(Long speciesId, UserGroupIbp userGroup) {
		try {
			MapDocument document = esService.fetch("extended_species", "_doc", speciesId.toString());
			om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ShowSpeciesPage showPagePayload = om.readValue(String.valueOf(document.getDocument()),
					ShowSpeciesPage.class);

			List<DocumentMeta> documentMetaList = documentService
					.getDocumentByTaxonConceptId(showPagePayload.getSpecies().getTaxonConceptId().toString());

			showPagePayload.setDocumentMetaList(documentMetaList);

			if (showPagePayload.getFacts() == null) {
				showPagePayload.setFacts(new ArrayList<FactValuePair>());
			}

			if (showPagePayload.getTaxonomicNames().getSynonyms() != null) {
				removeNullObjects(showPagePayload.getTaxonomicNames().getSynonyms());
			}

			if (showPagePayload.getTemporalData() == null) {
				showPagePayload.setTemporalData(new HashMap<String, Long>());
			}

			if (showPagePayload.getDocumentMetaList() == null) {
				showPagePayload.setDocumentMetaList(new ArrayList<DocumentMeta>());
			}

			if (showPagePayload.getReferencesListing() == null) {
				showPagePayload.setReferencesListing(new ArrayList<Reference>());
			}

			if (showPagePayload.getTaxonomicNames().getCommonNames().stream().allMatch(Objects::isNull)) {
				showPagePayload.getTaxonomicNames().setCommonNames(new ArrayList<CommonName>());
			}

			if (showPagePayload.getFieldData() == null) {
				showPagePayload.setFieldData(new ArrayList<SpeciesFieldData>());
			}

			// Add newly created fields from database that might not be in ElasticSearch yet

			List<SpeciesFieldValuesDTO> ugSpeciesFields = new ArrayList<>();
			if (userGroup != null) {
				ugSpeciesFields = ugService.getSpeciesFieldsByUserGroupId(userGroup.getId().toString());
			}

			List<Long> ugFieldIds = new ArrayList<>();

			for (SpeciesFieldValuesDTO ugMapping : ugSpeciesFields) {
				ugFieldIds.add(ugMapping.getSpeciesFieldId());
			}

			List<SpeciesFieldData> filteredFields = new ArrayList<>();
			for (SpeciesFieldData fieldData : showPagePayload.getFieldData()) {

				if (fieldData.getReferences().stream().allMatch(Objects::isNull)) {
					fieldData.setReferences(new ArrayList<Reference>());
				}
				if (fieldData.getSpeciesFieldResource() != null) {
					removeNullObjects(fieldData.getSpeciesFieldResource());
					if (fieldData.getSpeciesFieldResource().size() == 0) {
						fieldData.setSpeciesFieldResource(null);
					}
				}

				if (fieldData.getContributor() != null) {
					removeNullObjects(fieldData.getContributor());
				}

				List<UserGroupSpeciesFieldMeta> sfMetaData = new ArrayList<>();
				if (userGroup != null) {
					sfMetaData = ugService.getSpeciesFieldMetadata(userGroup.getId());
				}

				List<Long> sfContributors = fieldData.getContributor().stream().map(c -> c.getId())
						.collect(Collectors.toList());

				// UserGroup specific logic
				if (userGroup == null || (ugFieldIds.isEmpty() && sfMetaData.isEmpty())) {
					filteredFields.add(fieldData);
				} else if (ugFieldIds.isEmpty() && !sfMetaData.isEmpty()) {
					if (isMetaDataFilterTrue(sfMetaData, sfContributors)) {
						filteredFields.add(fieldData);
					}
				} else if (!ugFieldIds.isEmpty() && !sfMetaData.isEmpty()) {
					if (ugFieldIds.contains(fieldData.getFieldId())
							&& isMetaDataFilterTrue(sfMetaData, sfContributors)) {
						filteredFields.add(fieldData);
					}
				}

			}

			showPagePayload.setFieldData(filteredFields);
			enrichSpeciesPageWithNewFields(showPagePayload, speciesId);
			return showPagePayload;
		}

		catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	/**
	 * Enriches the species page with fields from the database that might not be in
	 * ElasticSearch yet
	 * 
	 * @param showPagePayload The species page payload from ElasticSearch
	 * @param speciesId       The ID of the species
	 */
	private void enrichSpeciesPageWithNewFields(ShowSpeciesPage showPagePayload, Long speciesId) {
		try {
			// Get existing field IDs from the ES response
			Set<Long> existingFieldIds = showPagePayload.getFieldData().stream().map(SpeciesFieldData::getFieldId)
					.collect(Collectors.toSet());

			// Get all fields from the database, not just fields for this species
			List<FieldNew> allFields = fieldNewDao.findAll();

			// For each field definition in the database
			for (FieldNew fieldNew : allFields) {
				// Skip fields already in the ES response
				if (existingFieldIds.contains(fieldNew.getId())) {
					continue;
				}

				// Skip blacklisted fields
				if (blackListSFId.contains(fieldNew.getId())) {
					continue;
				}

				// Create a minimal SpeciesFieldData object for this field definition
				SpeciesFieldData fieldData = createMinimalFieldData(fieldNew, speciesId);

				// Add it to the payload
				if (fieldData != null) {
					showPagePayload.getFieldData().add(fieldData);
				}
			}
		} catch (Exception e) {
			logger.error("Error enriching species page with new fields: " + e.getMessage());
		}
	}

	/**
	 * Creates a minimal SpeciesFieldData object with just enough information to
	 * display an empty field
	 * 
	 * @param fieldNew  The FieldNew definition from the database
	 * @param speciesId The ID of the species
	 * @return A minimal SpeciesFieldData object
	 */
	private SpeciesFieldData createMinimalFieldData(FieldNew fieldNew, Long speciesId) {
		try {
			// Get basic field header info (just for the name)
			FieldHeader fieldHeader = null;
			List<FieldHeader> headers = fieldHeaderDao.findAllByFieldId(fieldNew.getId());
			if (headers != null && !headers.isEmpty()) {
				// Get the default language header or first available
				fieldHeader = headers.stream().filter(h -> h.getLanguageId().equals(defaultLanguageId)).findFirst()
						.orElse(headers.get(0));
			}

			// Create the minimal SpeciesFieldData
			SpeciesFieldData fieldData = new SpeciesFieldData();
			fieldData.setFieldId(fieldNew.getId());
			fieldData.setDisplayOrder(fieldNew.getDisplayOrder());
			fieldData.setLabel(fieldNew.getLabel());
			fieldData.setHeader(fieldHeader != null ? fieldHeader.getHeader() : fieldNew.getHeader());
			fieldData.setPath(fieldNew.getPath());

			// Since this is an empty field, we don't have species field data
			// but we need to initialize the collections to prevent NPEs
			fieldData.setReferences(new ArrayList<>());
			fieldData.setContributor(new ArrayList<>());
			fieldData.setSpeciesFieldResource(new ArrayList<>());

			return fieldData;
		} catch (Exception e) {
			logger.error("Error creating minimal field data: " + e.getMessage());
			return null;
		}
	}

	private boolean isMetaDataFilterTrue(List<UserGroupSpeciesFieldMeta> sfMetaData, List<Long> sfContributors) {
		List<Long> ugContributors = new ArrayList<>();
		ugContributors = sfMetaData.stream().filter(m -> m.getValueType().equalsIgnoreCase("contributor"))
				.map(m -> m.getValueId()).collect(Collectors.toList());

		if (sfContributors.stream().anyMatch(ugContributors::contains)) {
			return true;
		}

		return false;
	}

	private SpeciesFieldData getSpeciesFieldData(SpeciesField speciesField) {

		try {
			FieldNew field = fieldNewDao.findById(speciesField.getFieldId());
			FieldHeader fieldHeader = fieldHeaderDao.findByFieldId(field.getId(), defaultLanguageId);

			SpeciesFieldAudienceType sfAudienceType = sfAudienceTypeDao.findById(speciesField.getId());

			SpeciesFieldLicense sfLicense = sfLicenseDao.findById(speciesField.getId());
			License sfLicenseData = resourceServices.getLicenseResource(sfLicense.getLicenseId().toString());

			List<Reference> references = referenceDao.findBySpeciesFieldId(speciesField.getId());

//			this is actually the attribution of speciesField and a String 

			SpeciesFieldContributor sfAttribution = sfContributorDao.findBySpeciesFieldId(speciesField.getId());
			Contributor attribution = null;
			if (sfAttribution != null)
				attribution = contributorDao.findById(sfAttribution.getContributorId());

//			species field user is the contributor of species field

			List<Long> userList = sfUserDao.findBySpeciesFieldId(speciesField.getId());
			List<UserIbp> contributors = new ArrayList<UserIbp>();
			for (Long userId : userList) {
				UserIbp contributor = userService.getUserIbp(userId.toString());
				contributors.add(contributor);
			}

//			resources of speciesField
			List<ResourceData> sfResources = resourceServices.getImageResource("SPECIES_FIELD",
					speciesField.getId().toString());

			SpeciesFieldData speciesFieldData = new SpeciesFieldData(speciesField.getId(), field.getId(),
					field.getDisplayOrder(), field.getLabel(), fieldHeader.getHeader(), fieldHeader.getDescription(),
					speciesField, references, attribution != null ? attribution.getName() : null, contributors,
					(sfAudienceType != null) ? sfAudienceType.getAudienceType() : null, sfLicenseData, sfResources,
					field.getPath());

			return speciesFieldData;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;

	}

	@Override
	public List<FieldNew> fetchLeafNodes() {
		List<FieldNew> result = new ArrayList<>();
		try {
			result = fieldNewDao.getLeafNodes();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<FieldRender> getFields(Long langId, String userGroupId) {

		if (langId == null)
			langId = defaultLanguageId;

		FieldHeader fieldHeader = null;

		List<FieldRender> renderList = new ArrayList<FieldRender>();

//		extract all the concept fields in display order
		List<FieldNew> concpetFields = fieldNewDao.findNullParent();

		List<SpeciesFieldValuesDTO> ugSpeciesFields = new ArrayList<>();

		if (userGroupId != null) {
			try {
				ugSpeciesFields = ugService.getSpeciesFieldsByUserGroupId(userGroupId);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		List<Long> ugFieldIds = new ArrayList<>();

		for (SpeciesFieldValuesDTO ugMapping : ugSpeciesFields) {
			ugFieldIds.add(ugMapping.getSpeciesFieldId());
		}

		for (FieldNew concpetField : concpetFields) {

//			check if the concept is itslef blacklisted
			if (!blackListSFId.contains(concpetField.getId())
					&& (ugFieldIds.contains(concpetField.getId()) || ugFieldIds.isEmpty())) {

				List<FieldDisplay> categorySubCat = new ArrayList<FieldDisplay>();
				fieldHeader = fieldHeaderDao.findByFieldId(concpetField.getId(), langId);
				concpetField.setHeader(fieldHeader.getHeader());

				FieldNewExtended conceptFieldExtended = new FieldNewExtended();
				conceptFieldExtended.setDescription(fieldHeader.getDescription());
				conceptFieldExtended.setUrlIdentifier(fieldHeader.getUrlIdentifier());
				conceptFieldExtended.setHeader(fieldHeader.getHeader());
				conceptFieldExtended.setDisplayOrder(concpetField.getDisplayOrder());
				conceptFieldExtended.setId(concpetField.getId());
				conceptFieldExtended.setLabel(concpetField.getLabel());
				conceptFieldExtended.setParentId(concpetField.getParentId());
				conceptFieldExtended.setPath(concpetField.getPath());

//				extract all the category fields in display order
				List<FieldNew> categoryFields = fieldNewDao.findByParentId(concpetField.getId());

				for (FieldNew catField : categoryFields) {

//					check if category is blacklisted
					if (!blackListSFId.contains(catField.getId())
							&& (ugFieldIds.contains(catField.getId()) || ugFieldIds.isEmpty())) {

						fieldHeader = fieldHeaderDao.findByFieldId(catField.getId(), langId);
						FieldNewExtended catFieldExtended = new FieldNewExtended();
						catField.setHeader(fieldHeader.getHeader());

						catFieldExtended.setDescription(fieldHeader.getDescription());
						catFieldExtended.setUrlIdentifier(fieldHeader.getUrlIdentifier());

						catFieldExtended.setHeader(fieldHeader.getHeader());
						catFieldExtended.setDisplayOrder(catField.getDisplayOrder());
						catFieldExtended.setId(catField.getId());
						catFieldExtended.setLabel(catField.getLabel());
						catFieldExtended.setParentId(catField.getParentId());
						catFieldExtended.setPath(catField.getPath());

//						extract all the subCategory fields in display order
						List<FieldNew> subCatField = fieldNewDao.findByParentId(catField.getId());
						List<FieldNewExtended> qualifiedsubCatField = new ArrayList<FieldNewExtended>();

						if (subCatField != null) {
							for (FieldNew subCat : subCatField) {
//								checking for blacklisted sub category
								if (!blackListSFId.contains(subCat.getId())
										&& (ugFieldIds.contains(subCat.getId()) || ugFieldIds.isEmpty())) {
									fieldHeader = fieldHeaderDao.findByFieldId(subCat.getId(), langId);
									subCat.setHeader(fieldHeader.getHeader());

									FieldNewExtended subCatExtended = new FieldNewExtended();
									subCatExtended.setHeader(fieldHeader.getHeader());
									subCatExtended.setDescription(fieldHeader.getDescription());
									subCatExtended.setUrlIdentifier(fieldHeader.getUrlIdentifier());
									subCatExtended.setDisplayOrder(subCat.getDisplayOrder());
									subCatExtended.setId(subCat.getId());
									subCatExtended.setLabel(subCat.getLabel());
									subCatExtended.setParentId(subCat.getParentId());
									subCatExtended.setPath(subCat.getPath());

									qualifiedsubCatField.add(subCatExtended);

								}
							}
						}
						categorySubCat.add(new FieldDisplay(catFieldExtended, qualifiedsubCatField));
					}

				}
				renderList.add(new FieldRender(conceptFieldExtended, categorySubCat));

			}

		}

		return renderList;

	}

	@Override
	public List<SpeciesTrait> getSpeciesTraitsByTaxonomyId(Long taxonomyId, Long language) {
		try {
			List<TraitsValuePair> traitValuePairLIst = traitService.getSpeciesTraits(taxonomyId.toString(),
					language.toString());
			List<SpeciesTrait> arranged = arrangeTraits(traitValuePairLIst, language);
			return arranged;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private List<SpeciesTrait> arrangeTraits(List<TraitsValuePair> traitValuePairList, Long languageId) {

		TreeMap<String, List<TraitsValuePair>> arrangedPair = new TreeMap<String, List<TraitsValuePair>>();

		for (TraitsValuePair traitsValuePair : traitValuePairList) {
			String name = "";
			Long fieldId = traitsValuePair.getTraits().getFieldId();
			name = fieldHierarchyString(fieldId, languageId);
			if (arrangedPair.containsKey(name)) {
				List<TraitsValuePair> pairList = arrangedPair.get(name);
				pairList.add(traitsValuePair);
				arrangedPair.put(name, pairList);
			} else {
				List<TraitsValuePair> pairList = new ArrayList<TraitsValuePair>();
				pairList.add(traitsValuePair);
				arrangedPair.put(name, pairList);
			}

		}

		List<SpeciesTrait> result = new ArrayList<SpeciesTrait>();
		for (Entry<String, List<TraitsValuePair>> entry : arrangedPair.entrySet()) {
			result.add(new SpeciesTrait(entry.getKey(), entry.getValue()));
		}

		return result;

	}

	private List<SpeciesTrait> arrangeHierarchyTraits(List<TraitsValuePair> traitValuePairList) {

		LinkedHashMap<String, List<TraitsValuePair>> arrangedPair = new LinkedHashMap<>();
		Map<Long, String> fields = new LinkedHashMap<>();
		List<FieldNew> conceptFields = fieldNewDao.findNullParent();
		for (FieldNew concept : conceptFields) {
			String conceptName = fieldHeaderDao.findByFieldId(concept.getId(), defaultLanguageId).getHeader();
			List<FieldNew> categoryFields = fieldNewDao.findByParentId(concept.getId());
			for (FieldNew cat : categoryFields) {
				String catName = fieldHeaderDao.findByFieldId(cat.getId(), defaultLanguageId).getHeader();
				List<FieldNew> subCategoryFields = fieldNewDao.findByParentId(cat.getId());
				if (subCategoryFields.size() == 0) {
					fields.put(cat.getId(), conceptName + " > " + catName);
				} else {
					for (FieldNew subCat : subCategoryFields) {
						String subCatName = fieldHeaderDao.findByFieldId(subCat.getId(), defaultLanguageId).getHeader();
						fields.put(cat.getId(), conceptName + " > " + catName + " > " + subCatName);
					}
				}
			}
		}

		for (Entry<Long, String> field : fields.entrySet()) {
			List<TraitsValuePair> matchingTraits = traitValuePairList.stream()
					.filter(trait -> trait.getTraits().getFieldId().equals(field.getKey()))
					.collect(Collectors.toList());

			if (!matchingTraits.isEmpty()) {
				arrangedPair.put(field.getValue(), matchingTraits);
			}
		}

		List<SpeciesTrait> result = new ArrayList<SpeciesTrait>();
		for (Entry<String, List<TraitsValuePair>> entry : arrangedPair.entrySet()) {
			result.add(new SpeciesTrait(entry.getKey(), entry.getValue()));
		}
		return result;

	}

	private String fieldHierarchyString(Long fieldId, Long language) {
		FieldNew fieldNew = null;
		String name = "";
		do {
			fieldNew = fieldNewDao.findById(fieldId);
			FieldHeader header = fieldHeaderDao.findByFieldId(fieldNew.getId(), language);
			if (header == null) {
				header = fieldHeaderDao.findByFieldId(fieldNew.getId(), defaultLanguageId);
			}
			name = header.getHeader() + " > " + name;
			fieldId = fieldNew.getParentId();

		} while (fieldNew.getParentId() != null);
		name = name.substring(0, name.length() - 3);
		return name;
	}

	@Override
	public List<SpeciesTrait> getAllSpeciesTraits(Long language) {
		try {
			List<TraitsValuePair> traitsValuePairList = traitService.getAllSpeciesTraits(language.toString());
			List<SpeciesTrait> arranged = arrangeTraits(traitsValuePairList, language);
			return arranged;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public List<SpeciesTrait> getAllTraits(Long language) {
		try {
			List<TraitsValuePair> traitsValuePairList = traitService.getAllTraitsList(language.toString());
			List<SpeciesTrait> arranged = arrangeHierarchyTraits(traitsValuePairList);
			return arranged;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public List<UserGroupIbp> updateUserGroup(HttpServletRequest request, String speciesId,
			UserGroupSpeciesCreateData ugSpeciesCreateData) {
		try {
			Long sId = Long.parseLong(speciesId);
			Species species = speciesDao.findById(sId);
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			List<UserGroupIbp> result = ugService.updateUserGroupSpeciesMapping(speciesId, ugSpeciesCreateData);
			updateLastRevised(Long.parseLong(speciesId));
			for (Long ugId : ugSpeciesCreateData.getUserGroupIds()) {

				UserGroupActivity ugActivity = new UserGroupActivity();
				UserGroupIbp ugIbp = ugService.getIbpData(ugId.toString());
				String description = "";
				ugActivity.setFeatured(null);
				ugActivity.setUserGroupId(ugIbp.getId());
				ugActivity.setUserGroupName(ugIbp.getName());
				ugActivity.setWebAddress(ugIbp.getWebAddress());
				try {
					description = om.writeValueAsString(ugActivity);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}

				logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), description, sId, sId, "species",
						ugId, "Posted resource", getSpeciesMailData(request, species));

			}

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Featured> createFeatured(HttpServletRequest request, FeaturedCreate featuredCreate) {
		try {
			FeaturedCreateData featuredCreateData = new FeaturedCreateData();
			featuredCreateData.setFeaturedCreate(featuredCreate);
			featuredCreateData.setMailData(null);
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			List<Featured> result = ugService.createFeatured(featuredCreateData);
			updateLastRevised(featuredCreate.getObjectId());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<Featured> unFeatured(HttpServletRequest request, String speciesId, List<Long> userGroupList) {
		try {

			UserGroupMappingCreateData userGroupData = new UserGroupMappingCreateData();
			userGroupData.setUserGroups(userGroupList);
			userGroupData.setUgFilterData(null);
			userGroupData.setMailData(null);
			ugService = headers.addUserGroupHeader(ugService, request.getHeader(HttpHeaders.AUTHORIZATION));
			List<Featured> result = ugService.unFeatured("species", speciesId, userGroupData);
			updateLastRevised(Long.parseLong(speciesId));
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private void updateLastRevised(Long speciesId) {

		try {
			Species species = speciesDao.findById(speciesId);
			species.setLastUpdated(new Date());
			speciesDao.update(species);
			ESSpeciesUpdate(speciesId);
		} catch (ApiException e) {
			logger.error(e.getMessage());

		}
	}

	@Override
	public List<FactValuePair> updateTraits(HttpServletRequest request, String speciesId, String traitId,
			FactsUpdateData factsUpdateData) {
		try {
			Boolean isContributor = checkIsContributor(request, Long.parseLong(speciesId));
			if (isContributor) {
				traitService = headers.addTraitsHeader(traitService, request.getHeader(HttpHeaders.AUTHORIZATION));
				List<FactValuePair> result = traitService.updateTraits("species.Species", speciesId, traitId,
						factsUpdateData);
				ShowSpeciesPage showData = showSpeciesPageFromES(Long.parseLong(speciesId), null);
				List<FactValuePair> existingFacts = showData.getFacts();
				Iterator<FactValuePair> iterator = existingFacts.iterator();
				while (iterator.hasNext()) {
					FactValuePair f = iterator.next();
					if (f.getNameId().equals(Long.parseLong(traitId))) {
						iterator.remove();
					}
				}
				existingFacts.addAll(result);
				showData.setFacts(existingFacts);
				;
				speciesEsUpdate(showData, speciesId);
				return existingFacts;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public SpeciesFieldData updateSpeciesField(HttpServletRequest request, Long speciesId,
			SpeciesFieldUpdateData sfdata) {

		try {

			Boolean isValid = speciesHelper.validateSpeciesFieldData(sfdata);
			if (!isValid)
				return null;
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			Long userId = Long.parseLong(profile.getId());
			Boolean isContributor = checkIsContributor(request, speciesId);
			Species species = speciesDao.findById(speciesId);

//			check if edit is done by the same contributor
			List<Long> sfUserList = new ArrayList<Long>();
			if (sfdata.getIsEdit()) {
				sfUserList = sfUserDao.findBySpeciesFieldId(sfdata.getSpeciesFieldId());
				JSONArray userRole = (JSONArray) profile.getAttribute("roles");
				if (!(sfUserList.contains(userId) || userRole.contains("ROLE_ADMIN")))
					return null;
			}

			if (isContributor) {

//				speciesField core update
				SpeciesField speciesField = updateCreateSpeciesField(speciesId, userId, sfdata);
				if (speciesField == null)
					return null;

//				attribution update
//				this is actually the attribution of speciesField and a String
				SpeciesFieldContributor sfAttribution = sfContributorDao.findBySpeciesFieldId(speciesField.getId());
				Contributor attribution = null;
				if (sfAttribution != null) {
					attribution = contributorDao.findById(sfAttribution.getContributorId());
				}
				if (attribution != null && sfdata.getIsEdit()) {
//					update attributions
					attribution.setName(sfdata.getAttributions());
					contributorDao.update(attribution);

				} else {
//						create new attributions
					Contributor contributor = new Contributor(null, sfdata.getAttributions());
					contributor = contributorDao.save(contributor);

					sfAttribution = new SpeciesFieldContributor(speciesField.getId(), contributor.getId());
					sfContributorDao.save(sfAttribution);
				}

//				species field resource
				if (sfdata.getSpeciesFieldResource() == null || sfdata.getSpeciesFieldResource().isEmpty()) {
					resourceServices = headers.addResourceHeaders(resourceServices,
							request.getHeader(HttpHeaders.AUTHORIZATION));
					resourceServices.removeSFMapping(speciesField.getId().toString());
				} else {
					updateCreateSpeciesResource(request, "SPECIES_FIELD", speciesField.getId().toString(),
							sfdata.getIsEdit(), sfdata.getSpeciesFieldResource());
				}

//				sf user contributor
				if (sfdata.getIsEdit()) {
//					deleting existing contributors
					for (Long existingUserId : sfUserList) {
						if (!sfdata.getContributorIds().contains(existingUserId)) {
							SpeciesFieldUser sfUser = sfUserDao.findBySpeciesFieldIdUserId(speciesField.getId(),
									existingUserId);
							sfUserDao.delete(sfUser);
						}
					}
//					adding new user contributors
					for (Long newUserId : sfdata.getContributorIds()) {
						if (!sfUserList.contains(newUserId)) {
							SpeciesFieldUser sfUser = new SpeciesFieldUser(speciesField.getId(), newUserId);
							sfUserDao.save(sfUser);
						}
					}
				} else {
					for (Long newUserId : sfdata.getContributorIds()) {
						SpeciesFieldUser sfUser = new SpeciesFieldUser(speciesField.getId(), newUserId);
						sfUserDao.save(sfUser);
					}
				}

//				sf license 
				if (sfdata.getIsEdit()) {
					SpeciesFieldLicense sfLicense = sfLicenseDao.findById(speciesField.getId());
					if (!sfLicense.getLicenseId().equals(sfdata.getLicenseId())) {
						sfLicense.setLicenseId(sfdata.getLicenseId());
						sfLicenseDao.update(sfLicense);
					}
				} else {
					SpeciesFieldLicense sfLicense = new SpeciesFieldLicense(speciesField.getId(),
							sfdata.getLicenseId());
					sfLicenseDao.save(sfLicense);
				}

//				add / update references
				updateCreateReferences(speciesField.getId(), sfdata.getReferences());

				String fieldHierarchy = fieldHierarchyString(sfdata.getFieldId(), defaultLanguageId);

				if (sfdata.getIsEdit()) {
					String desc = "Updated species field : " + fieldHierarchy;
					logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), desc, speciesId, speciesId,
							"species", speciesField.getId(), "Updated species field",
							getSpeciesMailData(request, species));
				} else {
					String desc = "Added species field : " + fieldHierarchy;
					logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), desc, speciesId, speciesId,
							"species", speciesField.getId(), "Added species field",
							getSpeciesMailData(request, species));
				}

				updateLastRevised(speciesId);

				return getSpeciesFieldData(speciesField);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private List<Reference> updateCreateReferences(Long speciesFieldId, List<Reference> referencesList) {
		List<Long> newRefId = new ArrayList<Long>();
		List<Reference> alredyExistingList = referenceDao.findBySpeciesFieldId(speciesFieldId);
		for (Reference reference : referencesList) {
			if (reference.getId() == null) {
//				add new references
				if (reference.getTitle() != null) {
					reference.setSpeciesFieldId(speciesFieldId);
					referenceDao.save(reference);
				}

			} else {

//				update existing references
				Reference ref = referenceDao.findById(reference.getId());
				ref.setTitle(reference.getTitle());
				ref.setUrl(reference.getUrl());
				referenceDao.update(ref);
				newRefId.add(ref.getId());
			}
		}
//		delete older references
		for (Reference alreadyExist : alredyExistingList) {
			if (!newRefId.contains(alreadyExist.getId()))
				referenceDao.delete(alreadyExist);
		}

		return referenceDao.findBySpeciesFieldId(speciesFieldId);

	}

	private List<Resource> updateCreateSpeciesResource(HttpServletRequest request, String objectType, String objectId,
			Boolean isEdit, List<SpeciesResourceData> speciesResourceData) {

		try {
			if (speciesResourceData != null) {
				List<Resource> resources = speciesHelper.createResourceMapping(request, objectType,
						speciesResourceData);

				if (resources != null) {
					resourceServices = headers.addResourceHeaders(resourceServices,
							request.getHeader(HttpHeaders.AUTHORIZATION));

					if (isEdit) {
						resources = resourceServices.updateResources(objectType, objectId, resources);
					} else {
						resources = resourceServices.createResource(objectType, objectId, resources);
					}
					return resources;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	private SpeciesField updateCreateSpeciesField(Long speciesId, Long uploaderId, SpeciesFieldUpdateData sfData) {

		SpeciesField field = null;
		if (sfData.getIsEdit()) {
//			update the species field
			field = speciesFieldDao.findById(sfData.getSpeciesFieldId());
			if (field.getIsDeleted())
				return null;
			field.setDescription(sfData.getSfDescription());
			field.setStatus(sfData.getSfStatus());
			field.setLanguageId(sfData.getLanguageId() != null ? sfData.getLanguageId() : defaultLanguageId);
			field.setLastUpdated(new Date());

			speciesFieldDao.update(field);

		} else {
//			create the species field
			field = new SpeciesField(null, sfData.getSfDescription(), sfData.getFieldId(), speciesId,
					sfData.getSfStatus(), "species.SpeciesField", new Date(), new Date(), new Date(), uploaderId,
					sfData.getLanguageId() != null ? sfData.getLanguageId() : defaultLanguageId, null, false);
			field = speciesFieldDao.save(field);
		}

		return field;
	}

	@Override
	public Boolean removeSpeciesField(HttpServletRequest request, Long speciesfieldId) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		JSONArray userRoles = (JSONArray) profile.getAttribute("roles");
		Long userId = Long.parseLong(profile.getId());
		List<Long> sfUserList = sfUserDao.findBySpeciesFieldId(speciesfieldId);
		SpeciesField speciesfield = speciesFieldDao.findById(speciesfieldId);
		Species species = speciesDao.findById(speciesfield.getSpeciesId());
		Boolean isContributor = checkIsContributor(request, speciesfield.getSpeciesId());

		if (userRoles.contains("ROLE_ADMIN") || (isContributor && sfUserList.contains(userId))) {
			speciesfield.setIsDeleted(true);
			speciesFieldDao.update(speciesfield);

			String fieldHierarchy = fieldHierarchyString(speciesfield.getFieldId(), defaultLanguageId);

			String desc = "Deleted species field : " + fieldHierarchy;
			logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), desc, speciesfield.getSpeciesId(),
					speciesfield.getSpeciesId(), "species", speciesfield.getId(), "Deleted species field",
					getSpeciesMailData(request, species));

			updateLastRevised(speciesfield.getSpeciesId());

			return true;
		}

		return false;
	}

	@Override

	public List<CommonName> updateAddCommonName(HttpServletRequest request, Long speciesId,
			CommonNamesData commonNamesData) {
		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			if (!isContributor)
				isContributor = checkIsObservationCurator(request, speciesId);
			if (isContributor) {
				commonNameService = headers.addCommonNameHeader(commonNameService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				List<CommonName> result = commonNameService.updateAddCommonNames(speciesId.toString(), commonNamesData);
				updateLastRevised(speciesId);

				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<CommonName> removeCommonName(HttpServletRequest request, Long speciesId, String commonNameId) {
		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			if (!isContributor)
				isContributor = checkIsObservationCurator(request, speciesId);
			if (isContributor) {
				commonNameService = headers.addCommonNameHeader(commonNameService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				List<CommonName> result = commonNameService.removeCommonName(commonNameId, speciesId.toString());
				updateLastRevised(speciesId);
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<SpeciesPull> getObservationResource(Long speciesId, Long offset) {
		try {
			Species species = speciesDao.findById(speciesId);
			ObservationInfo observationInfo = esService.getObservationInfo("extended_observation", "_doc",
					species.getTaxonConceptId().toString(), false);
			List<ObservationMapInfo> observations = observationInfo.getLatlon();
			List<Long> objectIds = new ArrayList<Long>();
			for (ObservationMapInfo obs : observations) {
				objectIds.add(obs.getId());
			}

			List<SpeciesPull> resources = resourceServices.getBulkResources("observation", offset.toString(),
					objectIds);

			return resources;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<ResourceData> pullResource(HttpServletRequest request, Long speciesId,
			List<SpeciesPullData> speciesPullData) {
		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			if (!isContributor)
				isContributor = checkIsObservationCurator(request, speciesId);
			if (isContributor) {

				Species species = speciesDao.findById(speciesId);
				Set<Long> observationIds = new HashSet<Long>();
				List<Long> resourcesIds = new ArrayList<Long>();

				for (SpeciesPullData speciesPull : speciesPullData) {
					resourcesIds.add(speciesPull.getResourceId());
					observationIds.add(speciesPull.getObservationId());
				}
				SpeciesResourcePulling resourcePulling = new SpeciesResourcePulling();
				resourcePulling.setSpeciesId(speciesId);
				resourcePulling.setResourcesIds(resourcesIds);

				resourceServices = headers.addResourceHeaders(resourceServices,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				List<ResourceData> resourceResult = resourceServices.pullResource(resourcePulling);

//				validate the observation

				List<Long> observationIdsList = new ArrayList<Long>(observationIds);
				observationService = headers.addObservationHeader(observationService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				observationService.speciesPullObservationValidation(species.getTaxonConceptId().toString(),
						observationIdsList);
				updateLastRevised(speciesId);
				return resourceResult;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

	@Override
	public List<ResourceData> getSpeciesResources(HttpServletRequest request, Long speciesId) {
		List<ResourceData> noResourceDataList = new ArrayList<>();
		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			if (!isContributor)
				isContributor = checkIsObservationCurator(request, speciesId);
			if (isContributor) {
				List<ResourceData> resourceData = resourceServices.getImageResource("SPECIES", speciesId.toString());
				return resourceData != null ? resourceData : noResourceDataList;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return noResourceDataList;
	}

	@Override
	public List<ResourceData> updateSpciesResources(HttpServletRequest request, Long speciesId,
			List<SpeciesResourcesPreData> preDataList) {

		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			Species species = speciesDao.findById(speciesId);
			if (!isContributor)
				isContributor = checkIsObservationCurator(request, speciesId);
			if (isContributor) {
				List<SpeciesPullData> speciesPullDatas = new ArrayList<SpeciesPullData>();
				List<SpeciesResourceData> speciesResourceData = new ArrayList<SpeciesResourceData>();
				for (SpeciesResourcesPreData preData : preDataList) {
					if (preData.getObservationId() != null) {
						speciesPullDatas.add(new SpeciesPullData(preData.getObservationId(), preData.getResourcesId()));
					} else {
						speciesResourceData.add(new SpeciesResourceData(preData.getPath(), preData.getUrl(),
								preData.getType(), preData.getCaption(), preData.getRating(), preData.getLicenseId(),
								preData.getContributor()));
					}
				}

				List<ResourceData> newResourceList = null;
				if (!speciesPullDatas.isEmpty())
					newResourceList = pullResource(request, speciesId, speciesPullDatas);
				if (!speciesResourceData.isEmpty() || preDataList.isEmpty()) {
//					if pull resource worked, we got extra resources attached which we dont want to remove so add them as well
					if (newResourceList != null) {
						for (ResourceData data : newResourceList) {
							Resource resource = data.getResource();

							SpeciesResourceData sd = new SpeciesResourceData(resource.getFileName(), resource.getUrl(),
									resource.getType(), resource.getDescription(), resource.getRating(),
									resource.getLicenseId(), resource.getContributor());

							if (!speciesResourceData.contains(sd))
								speciesResourceData.add(sd);
						}
					}

					updateCreateSpeciesResource(request, "SPECIES", speciesId.toString(), true, speciesResourceData);
				}

				List<ResourceData> resource = getSpeciesResources(request, speciesId);
				species = updateReprImage(speciesId, resource);

				logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), "Updated species gallery",
						speciesId, speciesId, "species", speciesId, "Updated species gallery",
						getSpeciesMailData(request, species));

				updateLastRevised(speciesId);
				return resource;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private Species updateReprImage(Long speciesId, List<ResourceData> resourcesData) {
		Long reprImage = null;
		int rating = 0;
		Boolean hasMedia = false;
		Resource res;
		if (resourcesData != null && !resourcesData.isEmpty()) {
			for (ResourceData resData : resourcesData) {
				res = resData.getResource();
				if (res.getType().equals("IMAGE")) {
					if (reprImage == null)
						reprImage = res.getId();
					if (res.getRating() != null && res.getRating() > rating) {
						reprImage = res.getId();
						rating = res.getRating();
					}
				}
			}
		}

		Species species = speciesDao.findById(speciesId);
		species.setHasMedia(hasMedia);
		species.setReprImageId(reprImage);
		return speciesDao.update(species);

	}

	@Override
	public Activity addSpeciesComment(HttpServletRequest request, CommentLoggingData loggingData) {
		try {
			Species species = speciesDao.findById(loggingData.getRootHolderId());
			activityService = headers.addActivityHeader(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));
			loggingData.setMailData(getSpeciesMailData(request, species));
			Activity result = activityService.addComment("species", loggingData);
			updateLastRevised(loggingData.getRootHolderId());
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Activity removeSpeciesComment(HttpServletRequest request, CommentLoggingData comment, String commentId) {
		try {
			Species species = speciesDao.findById(comment.getRootHolderId());
			comment.setMailData(getSpeciesMailData(request, species));
			activityService = headers.addActivityHeader(activityService, request.getHeader(HttpHeaders.AUTHORIZATION));

			return activityService.deleteComment("species", commentId, comment);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Long checkSpeciesPageExist(HttpServletRequest request, Long taxonId) {
		Species species = speciesDao.findByTaxonId(taxonId);
		if (species != null) {
			return species.getId();
		}
		return null;

	}

	@Override
	public Long createSpeciesPage(HttpServletRequest request, SpeciesCreateData speciesCreateData) {

		Boolean isContributor = checkIsContributor(request, speciesCreateData.getTaxonConceptId());
		if (isContributor) {
			Species species = speciesDao.findByTaxonId(speciesCreateData.getTaxonConceptId());
			if (species == null) {
				species = new Species(null, speciesCreateData.getTaxonConceptId(), speciesCreateData.getTitle(),
						new Date(), new Date(), 0, speciesCreateData.getHabitatId(), false, null, false, null);
				species = speciesDao.save(species);

				try {
					ESSpeciesUpdate(species.getId());
				} catch (ApiException e) {
					logger.error(e.getMessage());
				}

				logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), "Created species",
						species.getId(), species.getId(), "species", null, "Created species",
						getSpeciesMailData(request, species));

				return species.getId();
			}

		}
		return null;

	}

	private MailData getSpeciesMailData(HttpServletRequest request, Species species) {

		SpeciesGroup speciesGroup = null;

		try {
			speciesGroup = sgroupServices.getGroupId(species.getTaxonConceptId());
			Resource resourceData = species.getReprImageId() != null
					? resourceServices.getResourceDataById(species.getReprImageId().toString())
					: null;
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			String authorId = profile.getId();
			SpeciesMailData speciesData = new SpeciesMailData();
			speciesData.setAuthorId(Long.parseLong(authorId));
			speciesData.setGroup(speciesGroup != null ? speciesGroup.getName().toLowerCase() : null);
			speciesData.setIconUrl(resourceData != null && AppUtil.getResourceContext(resourceData.getContext()) != null
					? AppUtil.getResourceContext(resourceData.getContext()) + "/" + resourceData.getFileName()
					: null);
			speciesData.setSpeciesId(species.getId());
			speciesData.setSpeciesName(species.getTitle());

			List<UserGroupIbp> userGroupIbp = ugService.getSpeciesUserGroup(species.getId().toString());
			List<UserGroupMailData> userGroupData = new ArrayList<UserGroupMailData>();
			MailData payload = new MailData();
			if (userGroupIbp != null && !userGroupIbp.isEmpty()) {
				for (UserGroupIbp ugIbp : userGroupIbp) {
					UserGroupMailData ugMailData = new UserGroupMailData();
					ugMailData.setId(ugIbp.getId());
					ugMailData.setIcon(ugIbp.getIcon());
					ugMailData.setName(ugIbp.getName());
					ugMailData.setWebAddress(ugIbp.getWebAddress());
					userGroupData.add(ugMailData);
				}

				payload.setUserGroupData(userGroupData);
			}

			payload.setSpeciesData(speciesData);
			return payload;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;

	}

	@Override
	public TaxonomyDefinition createTaxonomy(HttpServletRequest request, TaxonomySave taxonomySave) {
		try {
			taxonomyService = headers.addTaxonomyHeader(taxonomyService, request.getHeader(HttpHeaders.AUTHORIZATION));
			TaxonomyDefinition result = taxonomyService.saveTaxonomy(taxonomySave);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<TaxonomyDefinition> updateAddSynonyms(HttpServletRequest request, String speciesId,
			SynonymData synonymData) {
		try {

			Boolean isContributor = checkIsContributor(request, Long.parseLong(speciesId));
			if (isContributor) {
				Species species = speciesDao.findById(Long.parseLong(speciesId));
				taxonomyService = headers.addTaxonomyHeader(taxonomyService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				List<TaxonomyDefinition> result = taxonomyService
						.updateAddSynonym(species.getTaxonConceptId().toString(), speciesId, synonymData);
				updateLastRevised(Long.parseLong(speciesId));
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public List<TaxonomyDefinition> removeSynonyms(HttpServletRequest request, String speciesId, String synonymId) {
		try {
			Boolean isContributor = checkIsContributor(request, Long.parseLong(speciesId));
			if (isContributor) {
				Species species = speciesDao.findById(Long.parseLong(speciesId));
				taxonomyService = headers.addTaxonomyHeader(taxonomyService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				List<TaxonomyDefinition> result = taxonomyService.removeSynonyms(species.getTaxonConceptId().toString(),
						synonymId, speciesId);
				updateLastRevised(Long.parseLong(speciesId));
				return result;
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public SpeciesPermission checkPermission(HttpServletRequest request, Long speciesId) {
		try {
			Boolean isContributor = checkIsContributor(request, speciesId);
			userService = headers.addUserHeader(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow follow = userService.getFollowByObject("species", speciesId.toString());
			Boolean isFollower = false;
			if (follow != null)
				isFollower = true;
			SpeciesPermission permisison = new SpeciesPermission(isContributor, isFollower);
			return permisison;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	private Boolean checkIsObservationCurator(HttpServletRequest request, Long speciesId) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			Boolean isContributor = false;
			if (userRole.contains("ROLE_ADMIN")) {
				isContributor = true;
			} else {
				Species species = speciesDao.findById(speciesId);
				taxPermissionService = headers.addTaxonomyPermissionHeader(taxPermissionService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				isContributor = taxPermissionService.isObservationCurator(species.getTaxonConceptId().toString());

			}
			return isContributor;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	private Boolean checkIsContributor(HttpServletRequest request, Long speciesId) {

		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			Boolean isContributor = false;
			if (userRole.contains("ROLE_ADMIN")) {
				isContributor = true;
			} else {
				Species species = speciesDao.findById(speciesId);
				taxPermissionService = headers.addTaxonomyPermissionHeader(taxPermissionService,
						request.getHeader(HttpHeaders.AUTHORIZATION));
				isContributor = taxPermissionService.getPermissionSpeciesTree(species.getTaxonConceptId().toString());

			}
			return isContributor;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public Boolean sendPermissionRequest(HttpServletRequest request, PermissionData permissionData) {
		try {
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray userRole = (JSONArray) profile.getAttribute("roles");
			taxPermissionService = headers.addTaxonomyPermissionHeader(taxPermissionService,
					request.getHeader(HttpHeaders.AUTHORIZATION));
			Boolean result = null;
			if (userRole.contains("ROLE_ADMIN")) {
				result = taxPermissionService.assignDirectPermission(permissionData);
			} else {
				result = taxPermissionService.requestPermission(permissionData);
			}
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Boolean sendPermissionGrant(HttpServletRequest request, EncryptedKey encryptedKey) {
		try {
			taxPermissionService = headers.addTaxonomyPermissionHeader(taxPermissionService,
					request.getHeader(HttpHeaders.AUTHORIZATION));
			Boolean result = taxPermissionService.grantPermissionrequest(encryptedKey);
			return result;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	@Override
	public Follow followRequest(HttpServletRequest request, Long speciesId) {
		try {
			userService = headers.addUserHeader(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow follow = userService.updateFollow("species", speciesId.toString());
			return follow;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Follow unFollowRequest(HttpServletRequest request, Long speciesId) {
		try {
			userService = headers.addUserHeader(userService, request.getHeader(HttpHeaders.AUTHORIZATION));
			Follow unfollow = userService.unfollow("species", speciesId.toString());
			return unfollow;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Boolean removeSpeciesPage(HttpServletRequest request, Long speciesId) {
		Boolean isEligible = checkIsContributor(request, speciesId);
		if (isEligible) {

			try {
				Species species = speciesDao.findById(speciesId);
				species.setIsDeleted(true);
				speciesDao.update(species);
				esService.delete(SpeciesIndex.INDEX.getValue(), SpeciesIndex.TYPE.getValue(), speciesId.toString());

				logActivity.LogActivity(request.getHeader(HttpHeaders.AUTHORIZATION), "Remove species", speciesId,
						speciesId, "species", speciesId, "Remove species", getSpeciesMailData(request, species));

			} catch (ApiException e) {
				logger.error(e.getMessage());
			}

			return true;
		}
		return false;
	}

	public String getSpeciesIdFromTaxonId(Long taxonId) {

		Species speciesDetails = speciesDao.findByTaxonId(taxonId);

		if (speciesDetails != null) {
			return speciesDetails.getId().toString();
		} else {
			return null;
		}

	}

	@Override
	public void ESSpeciesUpdate(long speciesId) throws ApiException {
		ShowSpeciesPage showData = showSpeciesPage(speciesId);
		speciesEsUpdate(showData, String.valueOf(speciesId));
	}

	@Override
	public CommonName updatePrefferedCommonName(HttpServletRequest request, Long speciesId, Long commonNameId) {
		CommonName result = null;
		Boolean isContributor = checkIsContributor(request, speciesId);
		commonNameService = headers.addCommonNameHeader(commonNameService,
				request.getHeader(HttpHeaders.AUTHORIZATION));
		try {
			if (Boolean.TRUE.equals(isContributor)) {
				result = commonNameService.updateIsPreffered(commonNameId);
				ESSpeciesUpdate(speciesId);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<Reference> createReference(HttpServletRequest request, Long speciesId,
			List<ReferenceCreateData> referenceCreateData) {
		try {

			Boolean isContributor = checkIsContributor(request, speciesId);

			if (isContributor) {
				List<Reference> newReferences = new ArrayList<Reference>();
				for (ReferenceCreateData rfCreateData : referenceCreateData) {
					Reference reference = new Reference();
					reference.setSpeciesId(rfCreateData.getSpeciesId());
					reference.setTitle(rfCreateData.getTitle());
					reference.setUrl(rfCreateData.getUrl());
					reference.setIsDeleted(false);

					Reference response = referenceDao.save(reference);
					newReferences.add(response);
				}
				handleSpeciesReferences(speciesId, newReferences, ReferenceOperation.ADD);

				return newReferences;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private void handleSpeciesReferences(Long speciesId, List<Reference> references, ReferenceOperation operation)
			throws ApiException {
		ShowSpeciesPage showData = showSpeciesPageFromES(speciesId, null);
		List<Reference> referencesListing = showData.getReferencesListing();

		switch (operation) {
		case ADD:
			referencesListing.addAll(references);
			break;
		case UPDATE:
			for (Reference newRef : references) {
				referencesListing.stream().filter(existingRef -> existingRef.getId().equals(newRef.getId())).findFirst()
						.ifPresent(existingRef -> {
							existingRef.setTitle(newRef.getTitle());
							existingRef.setUrl(newRef.getUrl());
							existingRef.setIsDeleted(newRef.getIsDeleted());
						});
			}
			break;
		}

		showData.setReferencesListing(referencesListing);
		speciesEsUpdate(showData, speciesId.toString());
	}

	@Override
	public Reference editReference(HttpServletRequest request, Long speciesId, Reference reference) {

		Boolean isContributor = checkIsContributor(request, speciesId);
		try {
			if (isContributor) {
				Reference response = referenceDao.update(reference);
				handleSpeciesReferences(speciesId, Collections.singletonList(reference), ReferenceOperation.UPDATE);
				return response;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public Reference deleteReference(HttpServletRequest request, Long referenceId) {

		Reference reference = referenceDao.findById(referenceId);
		Boolean isContributor = checkIsContributor(request, reference.getSpeciesId());
		try {
			if (isContributor) {
				reference.setIsDeleted(true);
				referenceDao.update(reference);
				handleSpeciesReferences(reference.getSpeciesId(), Collections.singletonList(reference),
						ReferenceOperation.UPDATE);
				return reference;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	@Override
	public FieldNew createField(HttpServletRequest request, FieldCreateData fieldData) {
		try {
			// Validate user permissions
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			JSONArray userRoles = (JSONArray) profile.getAttribute("roles");
			if (!userRoles.contains("ROLE_ADMIN")) {
				throw new Exception("User not authorized to create fields");
			}

			// Validate that we have at least one translation
			if (fieldData.getTranslations() == null || fieldData.getTranslations().isEmpty()) {
				throw new Exception("At least one language translation is required");
			}

			// Use the first translation as the default for the field header
			FieldHeaderData defaultTranslation = fieldData.getTranslations().get(0);

			// Create new field
			FieldNew field = new FieldNew();
			field.setHeader(defaultTranslation.getHeader()); // Use first translation as default
			field.setParentId(fieldData.getParentId());

			// Determine label based on hierarchy level
			if (fieldData.getParentId() == null) {
				field.setLabel("Concept");
			} else {
				FieldNew parentField = fieldNewDao.findById(fieldData.getParentId());
				if (parentField == null) {
					throw new Exception("Parent field not found");
				}

				if ("Concept".equals(parentField.getLabel())) {
					field.setLabel("Category");
				} else if ("Category".equals(parentField.getLabel())) {
					field.setLabel("SubCategory");
				} else {
					throw new Exception("Invalid parent field type. Cannot create subcategory under a subcategory.");
				}
			}

			// Set display order
			if (fieldData.getDisplayOrder() != null) {
				field.setDisplayOrder(fieldData.getDisplayOrder());
			} else {
				Long maxDisplayOrder = fieldNewDao.getMaxDisplayOrderForParent(fieldData.getParentId());
				field.setDisplayOrder(maxDisplayOrder + 1);
			}

			// Save the field
			field = fieldNewDao.save(field);

			// Create field headers for all translations
			for (FieldHeaderData translation : fieldData.getTranslations()) {
				FieldHeader header = new FieldHeader();
				header.setFieldId(field.getId());
				header.setHeader(translation.getHeader());
				header.setDescription(translation.getDescription());
				header.setUrlIdentifier(translation.getUrlIdentifier());
				header.setLanguageId(translation.getLanguageId());

				fieldHeaderDao.save(header);
			}

			return field;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public List<FieldHeader> getFieldTranslations(Long fieldId) {
		return fieldHeaderDao.findAllByFieldId(fieldId);
	}

	@Override
	public FieldHeader getFieldTranslation(Long fieldId, Long languageId) {
		return fieldHeaderDao.findByFieldIdAndLanguageId(fieldId, languageId);
	}

	@Override
	public List<FieldHeader> updateFieldTranslations(HttpServletRequest request,
			List<FieldTranslationUpdateData> translationData) throws Exception {
		// Validate user permissions
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		if (profile == null) {
			throw new Exception("User not authorized to update translations");
		}

		List<FieldHeader> updatedHeaders = new ArrayList<>();

		try {
			for (FieldTranslationUpdateData fieldData : translationData) {
				// Validate field exists
				FieldNew field = fieldNewDao.findById(fieldData.getFieldId());
				if (field == null) {
					logger.error("Field not found with id: " + fieldData.getFieldId());
					continue;
				}

				// Process each translation
				for (FieldTranslation translation : fieldData.getTranslations()) {
					FieldHeader header = new FieldHeader();
					header.setFieldId(fieldData.getFieldId());
					header.setLanguageId(translation.getLangId());
					header.setHeader(translation.getHeader());
					header.setDescription(translation.getDescription());
					header.setUrlIdentifier(translation.getUrlIdentifier());

					// Update or create translation
					FieldHeader updated = fieldHeaderDao.updateOrCreate(header);
					updatedHeaders.add(updated);
				}
			}

			return updatedHeaders;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new RuntimeException("Error updating field translations: " + e.getMessage());
		}
	}

}
