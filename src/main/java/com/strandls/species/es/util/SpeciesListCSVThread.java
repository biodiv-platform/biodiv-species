package com.strandls.species.es.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import com.strandls.activity.controller.ActivityServiceApi;
import com.strandls.esmodule.controllers.EsServicesApi;
import com.strandls.esmodule.pojo.MapDocument;
import com.strandls.esmodule.pojo.MapResponse;
import com.strandls.esmodule.pojo.MapSearchParams;
import com.strandls.esmodule.pojo.MapSearchQuery;
import com.strandls.species.Headers;
import com.strandls.species.pojo.FieldDisplay;
import com.strandls.species.pojo.FieldNewExtended;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.service.SpeciesServices;
import com.strandls.species.util.PropertyFileUtil;
import com.strandls.observation.pojo.DownloadLog;
import com.strandls.user.ApiException;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.DownloadLogData;
import com.strandls.utility.controller.UtilityServiceApi;
import com.strandls.utility.pojo.Language;

public class SpeciesListCSVThread implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(SpeciesListCSVThread.class);
	private final String modulePath = "/data-archive/listpagecsv";
	private final String basePath = "/app/data/biodiv";

	private Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	private MapSearchQuery mapSearchQuery;
	private String index;
	private String type;
	private EsServicesApi esService;
	private ObjectMapper objectMapper;
	private SpeciesServices speciesService;
	private UtilityServiceApi utilityServices;
	private Headers headers;
	private String requestAuthHeader;
	private MapSearchParams mapSearchParams;
	private String url;
	private final String authorId;
	private UserServiceApi userService;
	private ActivityServiceApi activityService;

	public SpeciesListCSVThread(MapSearchQuery mapSearchQuery, String index, String type, EsServicesApi esService,
			ObjectMapper objectMapper, SpeciesServices speciesService, UtilityServiceApi utilityServices,
			HttpServletRequest request, Headers headers, MapSearchParams mapSearchParams, String url, String authorId,
			UserServiceApi userService, ActivityServiceApi activityService) {
		super();
		this.mapSearchQuery = mapSearchQuery;
		this.index = index;
		this.type = type;
		this.esService = esService;
		this.objectMapper = objectMapper;
		this.speciesService = speciesService;
		this.utilityServices = utilityServices;
		this.headers = headers;
		this.requestAuthHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		this.mapSearchParams = mapSearchParams;
		this.url = url;
		this.authorId = authorId;
		System.out.println("\n\n***** Author Id: " + authorId + " *****\n\n");
		this.userService = userService;
		this.activityService = activityService;
	}

	@Override
	public void run() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		logger.info("Species List Download Request Received : RequestId = " + authorId + dtf.format(now));
		SpeciesUtilityFunctions obUtil = new SpeciesUtilityFunctions();
		String fileName = obUtil.getCsvFileNameDownloadPath();
		String modulePathForDownloads = modulePath;
		String filePath = basePath + modulePathForDownloads + File.separator + fileName;
		CSVWriter writer = obUtil.getCsvWriter(filePath);
		Integer max = 2000;
		Integer offset = 0;
		Integer epochSize = 0;
		String fileGenerationStatus = "Pending";
		String fileType = "CSV";
		DownloadLog entity = obUtil.createDownloadLogEntity(null, Long.parseLong(authorId), url, "Species List", 0L,
				fileGenerationStatus, fileType);

		try {
			fileGenerationStatus = "SUCCESS";

			// For getting languageNames
			utilityServices = headers.addUtilityHeaders(utilityServices, requestAuthHeader);
			List<Language> langauges = utilityServices.getAllLanguages(false);
			HashMap<Long, String> langaugeMap = new HashMap<>();
			for (Language lang : langauges) {
				langaugeMap.put(lang.getId(), lang.getName());
			}

			// Getting species fields
			List<FieldRender> fields = speciesService.getFields(defaultLanguageId, null);
			List<String> fieldNames = new ArrayList<>();
			List<Long> ids = new ArrayList<>();

			// Getting leaf nodes
			for (FieldRender field : fields) {
				List<FieldDisplay> childFields = field.getChildField();
				if (childFields != null && !childFields.isEmpty()) {
					for (FieldDisplay childField : childFields) {
						List<FieldNewExtended> grandChildFields = childField.getChildFields();
						if (grandChildFields != null && !grandChildFields.isEmpty()) {
							for (FieldNewExtended grandchildField : grandChildFields) {
								fieldNames.add(
										childField.getParentField().getHeader() + " < " + grandchildField.getHeader());
								ids.add(grandchildField.getId());
							}
						} else {
							fieldNames.add(childField.getParentField().getHeader());
							ids.add(childField.getParentField().getId());
						}
					}
				} else {
					fieldNames.add(field.getParentField().getHeader());
					ids.add(field.getParentField().getId());
				}
			}

			Set<String> allTraitNames = new LinkedHashSet<String>();

			obUtil.writeIntoCSV(writer, obUtil.getCsvHeaders(allTraitNames, fieldNames));

			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			do {
				MapResponse result;
				mapSearchParams.setFrom(offset);
				mapSearchParams.setLimit(max);
				result = esService.search(index, type, null, null, false, null, null, mapSearchQuery);
				List<MapDocument> documents = result.getDocuments();
				List<ShowSpeciesPage> specieList = new ArrayList<ShowSpeciesPage>();
				for (MapDocument document : documents) {
					try {
						ShowSpeciesPage species = objectMapper.readValue(String.valueOf(document.getDocument()),
								ShowSpeciesPage.class);
						specieList.add(species);
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
				}
				epochSize = specieList.size();
				offset = offset + max;
				obUtil.insertListToCSV(specieList, writer, objectMapper,
						allTraitNames.stream().collect(Collectors.toList()), ids, langaugeMap);

				logger.info("Species List Download RequestId = " + authorId + dtf.format(now) + "@ offset = " + offset);

			} while (epochSize >= max);
			entity.setFilePath(filePath);
			entity.setStatus(fileGenerationStatus);
			activityService.speciesDownloadMail(fileName, "species");
			logger.info("File Generated successfully");
		} catch (Exception e) {
			logger.error("file generation failed @ " + filePath + " due to - " + e);
			fileGenerationStatus = "FAILED";
			entity.setStatus(fileGenerationStatus);
		} finally {
			obUtil.closeWriter();
			entity.setStatus(fileGenerationStatus);
			if (fileGenerationStatus.equalsIgnoreCase("SUCCESS")) {
				DownloadLogData data = new DownloadLogData();
				data.setFilePath(modulePathForDownloads + File.separator + fileName);
				data.setFileType(fileType);
				data.setFilterUrl(entity.getFilterUrl());
				data.setStatus(fileGenerationStatus);
				data.setNotes("Species List");
				data.setSourcetype("Species");
				try {
					userService.logDocumentDownload(data);
				} catch (ApiException e) {
					logger.error(e.getMessage());
				}
			}
		}
		if (fileGenerationStatus.equalsIgnoreCase("failed")) {
			try {
				Files.deleteIfExists(Paths.get(filePath));
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

}
