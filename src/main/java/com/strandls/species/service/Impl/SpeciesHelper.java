/**
 * 
 */
package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;

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

	@SuppressWarnings("unchecked")
	public List<Resource> createResourceMapping(HttpServletRequest request,
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
				filesDTO.setFolder("species");
				fileMap = fileUploadService.moveFiles(filesDTO);
			}

			for (SpeciesResourceData resourceData : resourceDataList) {
				Resource resource = new Resource();
				resource.setVersion(0L);
				if (resourceData.getCaption() != null)
					resource.setDescription(
							(resourceData.getCaption().trim().length() != 0) ? resourceData.getCaption().trim() : null);

				if (resourceData.getPath() != null) {
					if (fileMap != null && !fileMap.isEmpty() && fileMap.containsKey(resourceData.getPath())) {
						// new path getting extracted from the map
						System.out.println(fileMap);
						Map<String, String> files = (Map<String, String>) fileMap.get(resourceData.getPath());
						System.out.println(files);
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
				resource.setUploadTime(new Date());
				resource.setUploaderId(userId);
				resource.setContext("OBSERVATION");
				resource.setLanguageId(205L);
				resource.setAccessRights(null);
				resource.setAnnotations(null);
				resource.setGbifId(null);
				resource.setLicenseId(resourceData.getLicenceId());

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
