/**
 *
 */
package com.strandls.species.service.Impl;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.HttpHeaders;

import org.pac4j.core.profile.CommonProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.authentication_utility.util.AuthUtil;
import com.strandls.file.api.UploadApi;
import com.strandls.file.model.FilesDTO;
import com.strandls.resource.pojo.Resource;
import com.strandls.species.Headers;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesResourceData;
import com.strandls.species.util.PropertyFileUtil;

/**
 * @author Abhishek Rudra
 *
 *
 */
public class SpeciesHelper {

	private final Logger logger = LoggerFactory.getLogger(SpeciesHelper.class);

	@Inject
	private UploadApi fileUploadService;

	@Inject
	private Headers headers;

	private Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	@SuppressWarnings("unchecked")
	public List<Resource> createResourceMapping(HttpServletRequest request, String context,
			List<SpeciesResourceData> resourceDataList) {

		CommonProfile profile = AuthUtil.getProfileFromRequest(request);
		Long userId = Long.parseLong(profile.getId());

		List<Resource> resources = new ArrayList<Resource>();
		try {
			List<String> fileList = new ArrayList<String>();
			for (SpeciesResourceData rd : resourceDataList) {
				if (rd.getPath() != null && rd.getPath().trim().length() > 0)
					fileList.add(rd.getPath());
			}
			Map<String, Object> fileMap = new HashMap<String, Object>();
			if (!fileList.isEmpty()) {
				fileUploadService = headers.addFileUploadHeader(fileUploadService,
						request.getHeader(HttpHeaders.AUTHORIZATION));

				FilesDTO filesDTO = new FilesDTO();
				filesDTO.setFiles(fileList);
				filesDTO.setFolder("img");
				filesDTO.setModule("SPECIES");
				fileMap = fileUploadService.moveFiles(filesDTO).getData();
			}

			for (SpeciesResourceData resourceData : resourceDataList) {
				Resource resource = new Resource();
				if (resourceData.getCaption() != null)
					resource.setDescription(
							(resourceData.getCaption().trim().length() != 0) ? resourceData.getCaption().trim() : null);

				if (resourceData.getPath() != null) {
					if (fileMap != null && !fileMap.isEmpty() && fileMap.containsKey(resourceData.getPath())) {
						// new path getting extracted from the map
						Map<String, String> files = (Map<String, String>) fileMap.get(resourceData.getPath());
						String relativePath = files.get("name").toString();
						resource.setFileName(relativePath);

					} else if (resourceData.getPath().startsWith("/ibpmu")) {
						continue;
					} else {
						resource.setFileName(resourceData.getPath()); // skip the resource as no new path has been
					} // returned
				}
				resource.setMimeType(null);
				if (resourceData.getType().startsWith("image") || resourceData.getType().equalsIgnoreCase("image"))
					resource.setType("IMAGE");
				else if (resourceData.getType().startsWith("audio") || resourceData.getType().equalsIgnoreCase("audio"))
					resource.setType("AUDIO");
				else if (resourceData.getType().startsWith("video") || resourceData.getType().equalsIgnoreCase("video"))
					resource.setType("VIDEO");
				if (resourceData.getPath() == null) {
					resource.setFileName(resource.getType().substring(0, 1).toLowerCase());
				}
				resource.setUrl(resourceData.getUrl());
				resource.setRating(resourceData.getRating());
				resource.setUploadTime(new java.util.Date());
				resource.setUploaderId(userId);
				resource.setContext(context.toUpperCase());
				resource.setLanguageId(defaultLanguageId);
				resource.setLicenseId(resourceData.getLicenseId());
				resource.setContributor(resourceData.getContributor());

				resources.add(resource);
			}
			return resources;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	public Boolean validateSpeciesFieldData(SpeciesFieldUpdateData sfData) {
		if (sfData.getAttributions() == null || sfData.getAttributions().isEmpty())
			return false;
		if (sfData.getLicenseId() == null)
			return false;
		if (sfData.getContributorIds() == null || sfData.getContributorIds().isEmpty())
			return false;
		if (sfData.getSfDescription() == null || sfData.getSfDescription().isEmpty())
			return false;
		return true;
	}

}
