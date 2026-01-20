
package com.strandls.species.es.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.strandls.observation.pojo.DownloadLog;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.taxonomy.pojo.BreadCrumb;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.user.pojo.UserIbp;
import com.strandls.userGroup.pojo.UserGroupIbp;

/**
 * @author Mekala Rishitha Ravi
 *
 */
public class SpeciesUtilityFunctions {

	private final Logger logger = LoggerFactory.getLogger(SpeciesUtilityFunctions.class);

	private final String[] csvCoreHeaders = { "taxonomyDefinition.name", "taxonomyDefinition.nameSourceId",
			"taxonomyDefinition.binomialForm", "taxonomyDefinition.relationship", "taxonomyDefinition.italicisedForm",
			"taxonomyDefinition.normalizedForm", "taxonomyDefinition.authorYear", "taxonomyDefinition.id",
			"taxonomyDefinition.canonicalForm", "taxonomyDefinition.matchDatabaseName",
			"taxonomyDefinition.viaDatasource", "taxonomyDefinition.matchId", "taxonomyDefinition.rank",
			"taxonomyDefinition.position", "taxonomyDefinition.status", "referenceListing", "commonNames", "species.id",
			"species.dataTableId", "species.taxonConceptId", "species.title", "species.hasMedia", "species.habitatId",
			"species.dateCreated", "species.reprImageId", "species.isDeleted", "prefferedCommonName", "userGroups",
			"kingdom", "phylum", "class", "order", "family", "genus", "species", "speciesGroup.groupOrder",
			"speciesGroup.id", "speciesGroup.name", "speciesGroup.parentGroupId" };
	private final String csvFileDownloadPath = "/app/data/biodiv/data-archive/listpagecsv";
	private CSVWriter writer;

	public String getCsvFileNameDownloadPath() {

		Date date = new Date();
		String fileName = "species_" + date.getTime() + ".csv";
		String filePathName;
		filePathName = csvFileDownloadPath + File.separator + fileName;
		File file = new File(filePathName);
		try {
			boolean isFileCreated = file.createNewFile();
			if (isFileCreated)
				return fileName;
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public List<String[]> getCsvHeaders(Set<String> allTraitNames, List<String> fieldNames) {
		List<String[]> headers = new ArrayList<String[]>();

		List<String> header = Stream
				.concat(Stream.concat(Arrays.stream(csvCoreHeaders), allTraitNames.stream()), fieldNames.stream())
				.collect(Collectors.toList());

		headers.add(header.toArray(new String[0]));
		return headers;
	}

	public DownloadLog createDownloadLogEntity(String filePath, Long authorId, String filterURL, String notes,
			Long offSet, String status, String type) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		DownloadLog entity = new DownloadLog();
		entity.setAuthorId(authorId);
		entity.setFilePath(filePath);
		entity.setFilterUrl(filterURL);
		entity.setNotes(notes);
		entity.setOffsetParam(offSet);
		entity.setCreatedOn(timestamp);
		entity.setStatus(status);
		entity.setType(type);
		entity.setVersion(2L);
		return entity;

	}

	private void addCoreHeaderValues(List<String> row, ShowSpeciesPage record, List<String> list, List<Long> ids) {
		try {
			row.add(record.getTaxonomyDefinition().getName());
			row.add(record.getTaxonomyDefinition().getNameSourceId());
			row.add(record.getTaxonomyDefinition().getBinomialForm());
			row.add(record.getTaxonomyDefinition().getRelationship());
			row.add(record.getTaxonomyDefinition().getItalicisedForm());
			row.add(record.getTaxonomyDefinition().getNormalizedForm());
			row.add(record.getTaxonomyDefinition().getAuthorYear());
			row.add(record.getTaxonomyDefinition().getId().toString());
			row.add(record.getTaxonomyDefinition().getCanonicalForm());
			row.add(record.getTaxonomyDefinition().getMatchDatabaseName());
			row.add(record.getTaxonomyDefinition().getViaDatasource());
			row.add(record.getTaxonomyDefinition().getMatchId());
			row.add(record.getTaxonomyDefinition().getRank());
			row.add(record.getTaxonomyDefinition().getPosition());
			row.add(record.getTaxonomyDefinition().getStatus());
			row.add(convertReferencesToStructured(record.getReferencesListing()));
			row.add(fetchCommonName(record.getTaxonomicNames().getCommonNames()));
			row.add(record.getSpecies().getId().toString());
			row.add(record.getSpecies().getDataTableId() != null ? record.getSpecies().getDataTableId().toString()
					: null);
			row.add(record.getSpecies().getTaxonConceptId().toString());
			row.add(record.getSpecies().getTitle());
			row.add(record.getSpecies().getHasMedia().toString());
			row.add(record.getSpecies().getHabitatId() != null ? record.getSpecies().getHabitatId().toString() : null);
			row.add(parseDate(record.getSpecies().getDateCreated()));
			row.add(record.getSpecies().getReprImageId() != null ? record.getSpecies().getReprImageId().toString()
					: null);
			row.add(record.getSpecies().getIsDeleted().toString());
			row.add(record.getPrefferedCommonName() != null ? record.getPrefferedCommonName().getName() : null);

			row.add(convertUserGroupsToStructured(record.getUserGroups()));

			row.addAll(record.getBreadCrumbs() != null ? getOrderedHierarchyFromBreadCrumbs(record.getBreadCrumbs())
					: new ArrayList<>(Collections.nCopies(7, null)));

			if (record.getSpeciesGroup() != null) {
				row.add(record.getSpeciesGroup().getGroupOrder().toString());
				row.add(record.getSpeciesGroup().getId().toString());
				row.add(record.getSpeciesGroup().getName());
				row.add(record.getSpeciesGroup().getParentGroupId().toString());
			}

			Collection<String> values = fetchTraitsForCsv(list, record.getFacts());
			row.addAll(values);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public void insertListToCSV(List<ShowSpeciesPage> records, CSVWriter writer, ObjectMapper objectMapper,
			List<String> list, List<Long> ids, HashMap<Long, String> langaugeMap) {

		List<String[]> rowSets = new ArrayList<String[]>();
		for (ShowSpeciesPage record : records) {
			List<String> row = new ArrayList<String>();

			addCoreHeaderValues(row, record, list, ids);

			Map<Long, LinkedHashMap<String, String>> langContent = fetchFieldDataForCsv(record.getFieldData(), ids);

			int i = 0;
			for (Entry<Long, LinkedHashMap<String, String>> content : langContent.entrySet()) {
				LinkedHashMap<String, String> fieldContent = content.getValue();
				if (i == 0) {
					row.add(langaugeMap.getOrDefault(content.getKey(), content.getKey().toString()));
					for (Long field : ids) {
						if (fieldContent.containsKey(field.toString())) {
							row.add(fieldContent.get(field.toString()));
						} else {
							row.add(null);
						}
					}
					rowSets.add(row.stream().toArray(String[]::new));
				} else {
					String[] emptyRow = new String[csvCoreHeaders.length + list.size() + 1 + ids.size()];
					Arrays.fill(emptyRow, "");
					int j = csvCoreHeaders.length + list.size();
					emptyRow[j] = langaugeMap.getOrDefault(content.getKey(), content.getKey().toString());
					j = j + 1;
					for (Long field : ids) {
						if (fieldContent.containsKey(field.toString())) {
							emptyRow[j] = fieldContent.get(field.toString());
						}
						j = j + 1;
					}
					// emptyRow[csvCoreHeaders.length + list.size()] = "Additional info";
					rowSets.add(emptyRow);
				}
				i = i + 1;
			}
			if (i == 0) {
				rowSets.add(row.stream().toArray(String[]::new));
			}
		}
		writer.writeAll(rowSets);

	}

	public CSVWriter getCsvWriter(String fileName) {
		FileWriter outputfile = null;
		try {
			outputfile = new FileWriter(new File(fileName));
			writer = new CSVWriter(outputfile);
		} catch (IOException e) {
			logger.error("CSVWriter error logging - " + e.getMessage());
		}
		return writer;
	}

	public void writeIntoCSV(CSVWriter writer, List<String[]> data) {
		writer.writeAll(data);
	}

	public void closeWriter() {
		try {
			writer.close();
		} catch (IOException e) {
			logger.error("CSVWriter error logging - " + e.getMessage());
		}
	}

	private Collection<String> fetchTraitsForCsv(List<String> traits, List<FactValuePair> facts) {
		LinkedHashMap<String, String> map = createLinkedHashMap(traits);
		if (facts != null) {
			for (FactValuePair fact : facts) {
				String traitName = fact.getName();
				String value = map.get(traitName);
				if (value == null)
					map.replace(traitName, fact.getValue());
				else
					map.replace(traitName, value + " | " + fact.getValue());
			}
		}
		return map.values();
	}

	private LinkedHashMap<String, String> createLinkedHashMap(List<String> keys) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (String key : keys) {
			map.put(key, null);
		}
		return map;
	}

	private String parseDate(Date date) {
		DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat stringFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

		if (!(date == null)) {
			String date_string = date.toString();
			if (date_string.matches(".*[a-zA-Z]{3}.*")) {
				// Handle string dates like "Tue Aug 27 09:34:02 IST 2024"
				try {
					return originalFormat.format(stringFormat.parse(date_string)).toString();
				} catch (ParseException e) {
					logger.error("String Date Parsing Error - " + e.getMessage());
				}
			} else {
				return originalFormat.format(new Date(Long.parseLong(date_string))).toString();
			}
		}
		return "";
	}

	private String convertReferencesToStructured(List<Reference> references) {
		if (references == null || references.isEmpty()) {
			return null;
		}
		return references.stream()
				.map(ref -> "id:" + ref.getId() + "#speciesId:" + ref.getSpeciesId() + "#url:"
						+ (ref.getUrl() != null ? ref.getUrl() : "") + "#speciesFieldId:" + ref.getSpeciesFieldId()
						+ "#title:" + (ref.getTitle() != null ? ref.getTitle().replace(",", "") : ""))
				.collect(Collectors.joining(" , "));
	}

	private String convertUserGroupsToStructured(List<UserGroupIbp> userGroups) {
		if (userGroups == null || userGroups.isEmpty()) {
			return null;
		}
		return userGroups.stream().map(ug -> "webAddress:" + ug.getWebAddress() + "#id:" + ug.getId()
				+ "#isParticipatory:" + ug.getIsParticipatory() + "#name:" + ug.getName())
				.collect(Collectors.joining(" , "));
	}

	private String fetchCommonName(List<CommonName> names) {
		String value = ""; // Initialize empty string

		// 2. Process if common names exist
		if (names != null && !names.isEmpty()) {
			for (CommonName name : names) {
				if (name != null)
					// 3. Format each common name as "common_name:language_name"
					value += ((name.getName() != null ? name.getName() : "") + ":"
							+ (name.getLanguage() != null ? name.getLanguage().getName() : "") + " | ");
			}

			// 4. Remove the trailing " | " from the last entry
			if (value.length() > 3)
				value = value.substring(0, value.length() - 3);
		}

		return value;
	}

	private List<String> getOrderedHierarchyFromBreadCrumbs(List<BreadCrumb> breadCrumbs) {
		if (breadCrumbs == null)
			return Arrays.asList(null, null, null, null, null, null, null);

		// Sort to standard order: kingdom, phylum, class, order, family, genus, species
		Map<String, String> rankMap = breadCrumbs.stream().collect(
				Collectors.toMap(BreadCrumb::getRankName, BreadCrumb::getName, (existing, replacement) -> replacement));

		return Arrays.asList(rankMap.get("kingdom"), rankMap.get("phylum"), rankMap.get("class"), rankMap.get("order"),
				rankMap.get("family"), rankMap.get("genus"), rankMap.get("species"));
	}

	private Map<Long, LinkedHashMap<String, String>> fetchFieldDataForCsv(List<SpeciesFieldData> fieldValues,
			List<Long> ids) {
		Map<String, Map<Long, List<String>>> fieldGroupedMap = new HashMap<>();

		if (fieldValues != null) {
			for (SpeciesFieldData value : fieldValues) {
				String fieldId = value.getFieldId().toString();
				if (value.getFieldData() != null) {
					Long languageId = value.getFieldData().getLanguageId();
					String contriString = "";
					if (value.getContributor() != null) {
						for (UserIbp contri : value.getContributor()) {
							if (contri != null) {
								contriString = contriString + contri.getName() + ",";
							}
						}
					}

					// Create the content string
					String content = "description:" + value.getFieldData().getDescription().replaceAll("<[^>]*>", "")
							+ "\n\nattributions:" + value.getAttributions() + "\ncontributor:" + contriString
							+ "\nlicense:" + value.getLicense().getName() + "|" + value.getLicense().getUrl() + "|"
							+ value.getLicense().getId();

					// Initialize field map if not exists
					fieldGroupedMap.computeIfAbsent(fieldId, k -> new HashMap<>());

					// Get or create the language map for this field
					Map<Long, List<String>> languageMap = fieldGroupedMap.get(fieldId);

					if (languageMap.containsKey(languageId)) {
						languageMap.get(languageId).add(content);
					} else {
						List<String> descriptions = new ArrayList<>();
						descriptions.add(content);
						languageMap.put(languageId, descriptions);
					}
				}
			}
		}

		Map<Long, LinkedHashMap<String, String>> languageToFieldMap = new HashMap<>();

		for (Long field : ids) {
			String parentFieldId = field.toString();
			if (fieldGroupedMap.containsKey(parentFieldId)) {
				Map<Long, List<String>> languageData = fieldGroupedMap.get(parentFieldId);
				for (Map.Entry<Long, List<String>> langEntry : languageData.entrySet()) {
					if (languageToFieldMap.containsKey(langEntry.getKey())) {
						LinkedHashMap<String, String> languageContent = languageToFieldMap.get(langEntry.getKey());
						String existingContent = "";
						String content = "";
						for (String value : langEntry.getValue()) {
							content = content + value + "\n\n";
						}
						content = content + "\n\n";
						if (languageContent.containsKey(parentFieldId)) {
							existingContent = languageContent.get(parentFieldId);
						}
						languageContent.put(parentFieldId, existingContent + content);
						languageToFieldMap.put(langEntry.getKey(), languageContent);
					} else {
						String content = "";
						for (String value : langEntry.getValue()) {
							content = content + value + "\n\n";
						}
						content = content + "\n\n";
						LinkedHashMap<String, String> languageContent = new LinkedHashMap<>();
						languageContent.put(parentFieldId, content);
						languageToFieldMap.put(langEntry.getKey(), languageContent);
					}
				}
			}
		}

		return languageToFieldMap;
	}
}