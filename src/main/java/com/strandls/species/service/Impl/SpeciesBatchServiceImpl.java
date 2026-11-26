package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.strandls.activity.pojo.MailData;
import com.strandls.activity.pojo.SpeciesMailData;
import com.strandls.activity.pojo.UserGroupMailData;
import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.observation.controller.ObservationServiceApi;
import com.strandls.observation.pojo.Language;
import com.strandls.resource.controllers.LicenseControllerApi;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.Resource;
import com.strandls.species.Headers;
import com.strandls.species.config.CacheConfig;
import com.strandls.species.dao.FieldHeaderDao;
import com.strandls.species.dao.FieldNewDao;
import com.strandls.species.dao.SpeciesDao;
import com.strandls.species.dao.SpeciesFieldDao;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldNew;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.Species;
import com.strandls.species.pojo.SpeciesBatchContributor;
import com.strandls.species.pojo.SpeciesBatchFieldValue;
import com.strandls.species.pojo.SpeciesBatchPreviewRequest;
import com.strandls.species.pojo.SpeciesBatchPreviewResult;
import com.strandls.species.pojo.SpeciesBatchPreviewRow;
import com.strandls.species.pojo.SpeciesBatchUploadRequest;
import com.strandls.species.pojo.SpeciesBatchUploadResult;
import com.strandls.species.pojo.SpeciesBatchUploadRow;
import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.service.SpeciesBatchService;
import com.strandls.species.service.SpeciesServices;
import com.strandls.species.util.AppUtil;
import com.strandls.species.util.PropertyFileUtil;
import com.strandls.species.util.SpeciesBatchParser;
import com.strandls.taxonomy.controllers.SpeciesServicesApi;
import com.strandls.taxonomy.pojo.SpeciesGroup;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.EsLocationListParams;
import com.strandls.user.pojo.User;
import com.strandls.user.pojo.UserListData;
import com.strandls.user.pojo.UserMappingList;
import com.strandls.userGroup.controller.UserGroupServiceApi;
import com.strandls.userGroup.pojo.UserGroupIbp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import net.minidev.json.JSONArray;

/**
 * Read-only preview of the species batch upload. Every lookup is cached for the
 * duration of one call, since many rows share contributors, taxa and fields.
 */
public class SpeciesBatchServiceImpl implements SpeciesBatchService {

	private final Logger logger = LoggerFactory.getLogger(SpeciesBatchServiceImpl.class);

	/** Status of every species field the batch upload adds; not taken from the sheet */
	static final String DEFAULT_STATUS = "UNDER_CREATION";

	private static final String USER_INDEX = "extended_user";
	private static final String USER_INDEX_TYPE = "_doc";

	@Inject
	private FieldNewDao fieldNewDao;

	@Inject
	private FieldHeaderDao fieldHeaderDao;

	@Inject
	private SpeciesFieldDao speciesFieldDao;

	@Inject
	private UserServiceApi userService;

	@Inject
	private LicenseControllerApi licenseService;

	@Inject
	private ObservationServiceApi observationService;

	@Inject
	private Headers headers;

	// ---- used by upload() -----------------------------------------------------

	@Inject
	private SpeciesBatchWriter writer;

	@Inject
	private SpeciesDao speciesDao;

	@Inject
	private SpeciesServices speciesServices;

	@Inject
	private CacheConfig cacheConfig;

	@Inject
	private LogActivities logActivity;

	@Inject
	private SpeciesServicesApi sgroupServices;

	@Inject
	private ResourceServicesApi resourceServices;

	@Inject
	private UserGroupServiceApi ugService;

	private final Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	private final Set<Long> blackListSFId = parseIds(
			PropertyFileUtil.fetchProperty("config.properties", "blackListSFId"));

	@Override
	public List<SpeciesBatchPreviewResult> preview(HttpServletRequest request,
			SpeciesBatchPreviewRequest previewRequest) {

		requireAdmin(request);

		if (previewRequest == null || previewRequest.getRows() == null || previewRequest.getRows().isEmpty())
			throw new IllegalArgumentException("No rows to preview");
		if (previewRequest.getRows().size() > MAX_PREVIEW_ROWS)
			throw new IllegalArgumentException(
					"Too many rows: " + previewRequest.getRows().size() + " (max " + MAX_PREVIEW_ROWS + ")");

		Lookups lookups = new Lookups(request.getHeader(HttpHeaders.AUTHORIZATION));

		List<SpeciesBatchPreviewResult> results = new ArrayList<>();
		for (SpeciesBatchPreviewRow row : previewRequest.getRows()) {
			try {
				results.add(previewRow(row, lookups));
			} catch (Exception e) {
				logger.error("Batch preview failed for row {}: {}", row.getRowNumber(), e.getMessage());
				SpeciesBatchPreviewResult failed = new SpeciesBatchPreviewResult();
				failed.setRowNumber(row.getRowNumber());
				failed.setScientificName(row.getScientificName());
				failed.getErrors().add("Couldn't check this row: " + e.getMessage());
				results.add(failed);
			}
		}
		return results;
	}

	// ---- upload ----------------------------------------------------------------

	@Override
	public List<SpeciesBatchUploadResult> upload(HttpServletRequest request,
			SpeciesBatchUploadRequest uploadRequest) {

		// The rows are the preview's checked payloads; no preview lookup is repeated here
		CommonProfile profile = requireAdmin(request);
		if (uploadRequest == null || uploadRequest.getRows() == null || uploadRequest.getRows().isEmpty())
			throw new IllegalArgumentException("No rows to upload");
		if (uploadRequest.getRows().size() > MAX_PREVIEW_ROWS)
			throw new IllegalArgumentException(
					"Too many rows: " + uploadRequest.getRows().size() + " (max " + MAX_PREVIEW_ROWS + ")");

		Long uploaderId = Long.parseLong(profile.getId());
		String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		Map<Long, Species> pages = new HashMap<>();
		Map<Long, MailData> mailData = new HashMap<>();
		Set<Long> changedPages = new LinkedHashSet<>();
		List<SpeciesBatchUploadResult> results = new ArrayList<>();

		for (SpeciesBatchUploadRow row : uploadRequest.getRows()) {
			SpeciesBatchUploadResult result = new SpeciesBatchUploadResult(row.getRowNumber(),
					row.getScientificName(), row.getSpeciesId(), null, null);
			results.add(result);

			String problem = payloadProblem(row);
			if (problem != null) {
				result.setStatus(SpeciesBatchUploadResult.SKIPPED);
				result.setMessage(problem);
				continue;
			}

			// One lookup per page, so content is never added to a missing or deleted page
			if (!pages.containsKey(row.getSpeciesId()))
				pages.put(row.getSpeciesId(), speciesDao.findById(row.getSpeciesId()));
			Species page = pages.get(row.getSpeciesId());
			if (page == null || Boolean.TRUE.equals(page.getIsDeleted())) {
				result.setStatus(SpeciesBatchUploadResult.FAILED);
				result.setMessage("Species page " + row.getSpeciesId() + " doesn't exist");
				continue;
			}

			// New entries always start as UNDER_CREATION, whatever was sent
			row.getFieldPayloads().forEach(p -> p.setSfStatus(DEFAULT_STATUS));

			try {
				List<Long> ids = writer.saveRow(page.getId(), uploaderId, row.getFieldPayloads());
				result.setSpeciesFieldIds(ids);
				result.setStatus(SpeciesBatchUploadResult.SAVED);
				changedPages.add(page.getId());
				//logRowActivity(request, authHeader, page, row, ids, mailData);
			} catch (Exception e) {
				logger.error("Batch upload failed for row {}: {}", row.getRowNumber(), e.getMessage());
				result.setStatus(SpeciesBatchUploadResult.FAILED);
				result.setMessage("Couldn't save this row: " + e.getMessage());
			}
		}

		// Once per page, not once per field: last revised date, page cache, search index
		for (Long speciesId : changedPages)
			refreshPage(pages.get(speciesId));

		return results;
	}

	/** Checks that need no lookup: everything a species field entry must have is present */
	private static String payloadProblem(SpeciesBatchUploadRow row) {
		if (row.getSpeciesId() == null)
			return "SpeciesId missing";
		if (row.getFieldPayloads() == null || row.getFieldPayloads().isEmpty())
			return "No species field values";
		for (SpeciesFieldUpdateData p : row.getFieldPayloads()) {
			if (p == null || p.getFieldId() == null || SpeciesBatchParser.isBlank(p.getSfDescription()))
				return "A species field or its content is missing";
			if (SpeciesBatchParser.isBlank(p.getAttributions()))
				return "Attribution missing";
			if (p.getLicenseId() == null)
				return "License missing";
			if (p.getLanguageId() == null)
				return "Language missing";
			if (p.getContributorIds() == null || p.getContributorIds().isEmpty()
					|| p.getContributorIds().stream().anyMatch(id -> id == null))
				return "Contributor missing";
		}
		return null;
	}

	/** The caller's profile, if they are an admin */
	private static CommonProfile requireAdmin(HttpServletRequest request) {
		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		JSONArray roles = profile == null ? null : (JSONArray) profile.getAttribute("roles");
		if (roles == null || !roles.contains("ROLE_ADMIN"))
			throw new SecurityException("Only admins can batch upload species pages");
		return profile;
	}

	/** One activity entry per row, listing its fields (the form logs one per field) */
	private void logRowActivity(HttpServletRequest request, String authHeader, Species page,
			SpeciesBatchUploadRow row, List<Long> ids, Map<Long, MailData> mailData) {
		List<String> names = new ArrayList<>();
		for (int i = 0; i < row.getFieldPayloads().size(); i++) {
			SpeciesBatchFieldValue field = row.getFields() != null && i < row.getFields().size()
					? row.getFields().get(i)
					: null;
			names.add(field != null && field.getLabel() != null ? field.getLabel().replace(" \u203A ", " > ")
					: "field " + row.getFieldPayloads().get(i).getFieldId());
		}
		String description = (names.size() == 1 ? "Added species field : " : "Added species fields : ")
				+ String.join(", ", names);
		MailData mail = mailData.computeIfAbsent(page.getId(), id -> speciesMailData(request, page));
		logActivity.LogActivity(authHeader, description, page.getId(), page.getId(), "species", ids.get(0),
				"Added species field", mail);
	}

	private void refreshPage(Species page) {
		try {
			page.setLastUpdated(new Date());
			speciesDao.update(page);
		} catch (Exception e) {
			logger.error("Couldn't update last revised of species {}: {}", page.getId(), e.getMessage());
		}
		cacheConfig.invalidateSpeciesCache(page.getId());
		try {
			speciesServices.ESSpeciesUpdate(page.getId());
		} catch (Exception e) {
			logger.error("Couldn't reindex species {}: {}", page.getId(), e.getMessage());
		}
	}

	/** Same mail data SpeciesServiceImpl.getSpeciesMailData builds, made once per page */
	private MailData speciesMailData(HttpServletRequest request, Species species) {
		try {
			SpeciesGroup speciesGroup = sgroupServices.getGroupId(species.getTaxonConceptId());
			Resource resourceData = species.getReprImageId() != null
					? resourceServices.getResourceDataById(species.getReprImageId().toString())
					: null;
			CommonProfile profile = AuthUtil.getProfileFromRequest(request);
			SpeciesMailData speciesData = new SpeciesMailData();
			speciesData.setAuthorId(Long.parseLong(profile.getId()));
			speciesData.setGroup(speciesGroup != null ? speciesGroup.getName().toLowerCase() : null);
			speciesData.setIconUrl(resourceData != null && AppUtil.getResourceContext(resourceData.getContext()) != null
					? AppUtil.getResourceContext(resourceData.getContext()) + "/" + resourceData.getFileName()
					: null);
			speciesData.setSpeciesId(species.getId());
			speciesData.setSpeciesName(species.getTitle());

			List<UserGroupMailData> userGroupData = new ArrayList<>();
			List<UserGroupIbp> userGroups = ugService.getSpeciesUserGroup(species.getId().toString());
			if (userGroups != null) {
				for (UserGroupIbp ug : userGroups) {
					UserGroupMailData ugMailData = new UserGroupMailData();
					ugMailData.setId(ug.getId());
					ugMailData.setIcon(ug.getIcon());
					ugMailData.setName(ug.getName());
					ugMailData.setWebAddress(ug.getWebAddress());
					userGroupData.add(ugMailData);
				}
			}

			MailData payload = new MailData();
			payload.setUserGroupData(userGroupData);
			payload.setSpeciesData(speciesData);
			return payload;
		} catch (Exception e) {
			logger.error("Couldn't build mail data for species {}: {}", species.getId(), e.getMessage());
		}
		return null;
	}

	private SpeciesBatchPreviewResult previewRow(SpeciesBatchPreviewRow row, Lookups lookups) {
		SpeciesBatchPreviewResult result = new SpeciesBatchPreviewResult();
		List<String> errors = result.getErrors();
		List<String> warnings = result.getWarnings();
		result.setRowNumber(row.getRowNumber());
		result.setScientificName(SpeciesBatchParser.trim(row.getScientificName()));

		resolveSpeciesPage(row, result);
		resolveContributors(row, result, lookups);

		// Attribution, License and Language are required columns
		String attribution = SpeciesBatchParser.trim(row.getAttribution());
		if (attribution.isEmpty())
			errors.add("Attribution missing");
		result.setAttributions(attribution);

		resolveLicense(row, result, lookups);
		resolveLanguage(row, result, lookups);

		result.setStatus(DEFAULT_STATUS);

		result.setReferences(SpeciesBatchParser.parseReferences(row.getReferences()));
		for (Reference reference : result.getReferences()) {
			if (reference.getUrl() != null && !SpeciesBatchParser.isUrl(reference.getUrl()))
				warnings.add("Reference link \"" + reference.getUrl() + "\" isn't an http(s) URL");
		}

		resolveFields(row, result, lookups);

		if (errors.isEmpty())
			result.setFieldPayloads(buildPayloads(result));
		return result;
	}

	// ---- species page ------------------------------------------------------

	/** SpeciesId is required and used as given; the page itself isn't looked up */
	private void resolveSpeciesPage(SpeciesBatchPreviewRow row, SpeciesBatchPreviewResult result) {
		Long speciesId = SpeciesBatchParser.toLong(row.getSpeciesId());
		if (speciesId == null) {
			result.getErrors().add(SpeciesBatchParser.isBlank(row.getSpeciesId()) ? "SpeciesId missing"
					: "SpeciesId \"" + row.getSpeciesId().trim() + "\" isn't a number");
			return;
		}
		result.setSpeciesId(speciesId);
	}

	// ---- contributors ------------------------------------------------------

	private void resolveContributors(SpeciesBatchPreviewRow row, SpeciesBatchPreviewResult result, Lookups lookups) {
		List<String> emails = SpeciesBatchParser.splitList(row.getContributor());
		if (emails.isEmpty()) {
			result.getErrors().add("Contributor missing");
			return;
		}
		Set<Long> seen = new HashSet<>();
		for (String email : emails) {
			if (!SpeciesBatchParser.isEmail(email)) {
				result.getErrors().add("Contributor \"" + email + "\" isn't an email address");
				continue;
			}
			UserLookup lookup = lookups.user(email);
			if (lookup.failed) {
				result.getErrors().add("Couldn't look up contributor " + email + ", try again");
			} else if (lookup.contributor == null) {
				result.getErrors().add("No user with email " + email);
			} else if (seen.add(lookup.contributor.getUserId())) {
				result.getContributors().add(lookup.contributor);
			}
		}
	}

	// ---- license / language ------------------------------------------------

	private void resolveLicense(SpeciesBatchPreviewRow row, SpeciesBatchPreviewResult result, Lookups lookups) {
		String cell = SpeciesBatchParser.trim(row.getLicense());
		List<License> licenses = lookups.licenses();
		if (licenses == null) {
			result.getErrors().add("Couldn't load the license list, try again");
			return;
		}

		if (cell.isEmpty()) {
			result.getErrors().add("License missing");
			return;
		}
		Long id = SpeciesBatchParser.toLong(cell);
		String normalized = SpeciesBatchParser.normalize(cell);
		License license = licenses.stream()
				.filter(l -> (id != null && id.equals(l.getId()))
						|| SpeciesBatchParser.normalize(l.getName()).equals(normalized))
				.findFirst().orElse(null);
		if (license == null) {
			result.getErrors().add("License \"" + cell + "\" not found");
			return;
		}
		result.setLicenseId(license.getId());
		result.setLicenseName(license.getName());
	}

	/** Language by name (case-insensitive) among all languages not marked dirty */
	private void resolveLanguage(SpeciesBatchPreviewRow row, SpeciesBatchPreviewResult result, Lookups lookups) {
		String cell = SpeciesBatchParser.trim(row.getLanguage());
		List<Language> languages = lookups.languages();

		if (cell.isEmpty()) {
			result.getErrors().add("Language missing");
			return;
		}
		if (languages == null) {
			result.getErrors().add("Couldn't load the language list, try again");
			return;
		}

		Language language = languages.stream().filter(l -> cell.equalsIgnoreCase(SpeciesBatchParser.trim(l.getName())))
				.findFirst().orElse(null);
		if (language == null) {
			result.getErrors().add("Language \"" + cell + "\" not found");
			return;
		}
		result.setLanguageId(language.getId());
		result.setLanguageName(language.getName());
	}

	// ---- fields ------------------------------------------------------------

	private void resolveFields(SpeciesBatchPreviewRow row, SpeciesBatchPreviewResult result, Lookups lookups) {
		List<SpeciesBatchFieldValue> values = row.getFields() == null ? new ArrayList<>() : row.getFields();
		Long speciesId = result.getSpeciesId();
		Long languageId = result.getLanguageId();
		// Content already on the page, as "fieldId:languageId": another language isn't a duplicate
		Set<String> existing = speciesId == null ? new HashSet<>() : lookups.filledFields(speciesId);
		String languageName = result.getLanguageName() != null ? result.getLanguageName() : "this language";
		Set<Long> seen = new HashSet<>();

		for (SpeciesBatchFieldValue value : values) {
			if (value == null || SpeciesBatchParser.isBlank(value.getValue()))
				continue;
			Long fieldId = value.getFieldId();
			FieldInfo field = fieldId == null ? null : lookups.field(fieldId);
			if (field == null) {
				result.getErrors().add("Species field " + fieldId + " doesn't exist");
				continue;
			}
			if (!field.isLeaf) {
				result.getErrors().add(field.label + " has sub-fields; map a sub-field instead");
				continue;
			}
			if (blackListSFId.contains(fieldId)) {
				result.getErrors().add(field.label + " isn't shown on species pages");
				continue;
			}
			if (!seen.add(fieldId)) {
				result.getWarnings().add(field.label + " is mapped more than once; each value is added");
			} else if (speciesId != null && languageId != null) {
				String fieldLanguage = fieldId + ":" + languageId;
				if (existing.contains(fieldLanguage))
					result.getWarnings().add(field.label + " already has " + languageName
							+ " content; this adds another entry");
				// Another row of this sheet for the same page, language and field
				Integer firstRow = lookups.sheetEntries.putIfAbsent(speciesId + ":" + fieldLanguage,
						result.getRowNumber());
				if (firstRow != null && !firstRow.equals(result.getRowNumber()))
					result.getWarnings().add(field.label + " is also filled in " + languageName + " in row "
							+ firstRow + " for this page; both are added");
			}

			result.getFields().add(new SpeciesBatchFieldValue(fieldId, field.label, value.getValue().trim()));
		}
		if (result.getFields().isEmpty())
			result.getWarnings().add("No species field values in this row");
	}

	private List<SpeciesFieldUpdateData> buildPayloads(SpeciesBatchPreviewResult result) {
		List<Long> contributorIds = result.getContributors().stream().map(SpeciesBatchContributor::getUserId)
				.collect(Collectors.toList());

		List<SpeciesFieldUpdateData> payloads = new ArrayList<>();
		for (SpeciesBatchFieldValue field : result.getFields()) {
			payloads.add(new SpeciesFieldUpdateData(false, field.getFieldId(), null, result.getLicenseId(),
					field.getValue(), result.getStatus(), result.getLanguageId(), result.getAttributions(),
					new ArrayList<>(), contributorIds, result.getReferences()));
		}
		return payloads;
	}

	private static Set<Long> parseIds(String value) {
		if (SpeciesBatchParser.isBlank(value))
			return new HashSet<>();
		return Arrays.stream(value.split(",")).map(SpeciesBatchParser::toLong).filter(id -> id != null)
				.collect(Collectors.toSet());
	}

	// ---- per-request lookups -------------------------------------------------

	private static class FieldInfo {
		private final String label;
		private final boolean isLeaf;

		FieldInfo(String label, boolean isLeaf) {
			this.label = label;
			this.isLeaf = isLeaf;
		}
	}

	private static class UserLookup {
		private final SpeciesBatchContributor contributor;
		private final boolean failed;

		UserLookup(SpeciesBatchContributor contributor, boolean failed) {
			this.contributor = contributor;
			this.failed = failed;
		}
	}

	/** Caches for one preview call */
	private class Lookups {
		private final String authHeader;

		private final Map<String, UserLookup> users = new HashMap<>();
		private final Map<Long, Set<String>> filledFields = new HashMap<>();
		/** "speciesId:fieldId:languageId" -> first sheet row that fills it, across this request */
		private final Map<String, Integer> sheetEntries = new HashMap<>();
		private final Map<Long, FieldInfo> fields = new HashMap<>();
		private List<License> licenses;
		private boolean licensesLoaded;
		private List<Language> languages;
		private boolean languagesLoaded;

		Lookups(String authHeader) {
			this.authHeader = authHeader;
		}

		UserLookup user(String email) {
			return users.computeIfAbsent(email.toLowerCase(Locale.ROOT), this::findUser);
		}

		private UserLookup findUser(String email) {
			try {
				userService = headers.addUserHeader(userService, authHeader);
				UserListData data = userService.userList(USER_INDEX, USER_INDEX_TYPE, 10, 0, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, email, null,
						null, null, null, null, null, null, null, new EsLocationListParams());
				List<User> hits = new ArrayList<>();
				if (data != null && data.getUserList() != null) {
					for (UserMappingList mapping : data.getUserList()) {
						User user = mapping.getUser();
						if (user != null && user.getId() != null && !Boolean.TRUE.equals(user.getIsDeleted()))
							hits.add(user);
					}
				}
				// the ES search isn't an exact match, so insist on the same address
				User match = hits.stream().filter(u -> email.equalsIgnoreCase(u.getEmail())).findFirst()
						.orElse(null);
				if (match == null && hits.size() == 1 && hits.get(0).getEmail() == null)
					match = hits.get(0);
				if (match == null)
					return new UserLookup(null, false);
				String name = SpeciesBatchParser.isBlank(match.getName()) ? email : match.getName().trim();
				return new UserLookup(new SpeciesBatchContributor(email, match.getId(), name), false);
			} catch (Exception e) {
				logger.error("User lookup failed for {}: {}", email, e.getMessage());
				return new UserLookup(null, true);
			}
		}

		/** Existing (not deleted) species field content of a page, as "fieldId:languageId" */
		Set<String> filledFields(Long speciesId) {
			return filledFields.computeIfAbsent(speciesId, id -> {
				List<SpeciesField> existing = speciesFieldDao.findBySpeciesId(id);
				if (existing == null)
					return new HashSet<>();
				return existing.stream().filter(sf -> !Boolean.TRUE.equals(sf.getIsDeleted()))
						.map(sf -> sf.getFieldId() + ":"
								+ (sf.getLanguageId() != null ? sf.getLanguageId() : defaultLanguageId))
						.collect(Collectors.toSet());
			});
		}

		FieldInfo field(Long fieldId) {
			if (!fields.containsKey(fieldId)) {
				FieldNew field = fieldNewDao.findById(fieldId);
				if (field == null) {
					fields.put(fieldId, null);
				} else {
					List<FieldNew> children = fieldNewDao.findByParentId(fieldId);
					fields.put(fieldId, new FieldInfo(label(field), children == null || children.isEmpty()));
				}
			}
			return fields.get(fieldId);
		}

		/** "Concept > Category > Sub-category" (joined with a single right angle quote) in the default language */
		private String label(FieldNew field) {
			List<String> reversed = new ArrayList<>();
			FieldNew current = field;
			Set<Long> visited = new HashSet<>();
			while (current != null && visited.add(current.getId())) {
				FieldHeader header = fieldHeaderDao.findByFieldIdAndLanguageId(current.getId(), defaultLanguageId);
				String name = header != null && !SpeciesBatchParser.isBlank(header.getHeader()) ? header.getHeader()
						: current.getHeader();
				reversed.add(0, SpeciesBatchParser.isBlank(name) ? "Field " + current.getId() : name.trim());
				current = current.getParentId() == null ? null : fieldNewDao.findById(current.getParentId());
			}
			return String.join(" \u203A ", reversed);
		}

		List<License> licenses() {
			if (!licensesLoaded) {
				licensesLoaded = true;
				try {
					licenseService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
					licenses = licenseService.getAllLicenses();
				} catch (Exception e) {
					logger.error("License list failed: {}", e.getMessage());
				}
			}
			return licenses;
		}

		List<Language> languages() {
			if (!languagesLoaded) {
				languagesLoaded = true;
				try {
					// GET /v1/observation/language?isDirty=false: the list the UI's axGetLangList uses.
					// The generated getLanguaes() declares a single Language, so read the list directly.
					com.strandls.observation.ApiClient client = observationService.getApiClient();
					languages = client.getHttpClient().target(client.getBasePath())
							.path("/v1/observation/language").queryParam("isDirty", false)
							.request(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION, authHeader)
							.get(new GenericType<List<Language>>() {
							});
				} catch (Exception e) {
					logger.error("Language list failed: {}", e.getMessage());
				}
			}
			return languages;
		}
	}
}
