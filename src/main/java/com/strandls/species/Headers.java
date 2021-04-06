/**
 * 
 */
package com.strandls.species;

import javax.ws.rs.core.HttpHeaders;

import com.strandls.activity.controller.ActivitySerivceApi;
import com.strandls.file.api.UploadApi;
import com.strandls.observation.controller.ObservationServiceApi;
import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.taxonomy.controllers.TaxonomyServicesApi;
import com.strandls.traits.controller.TraitsServiceApi;
import com.strandls.userGroup.controller.UserGroupSerivceApi;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class Headers {

	public UserGroupSerivceApi addUserGroupHeader(UserGroupSerivceApi ugService, String authHeader) {
		ugService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return ugService;
	}

	public TraitsServiceApi addTraitsHeader(TraitsServiceApi traitsService, String authHeader) {
		traitsService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return traitsService;
	}

	public UploadApi addFileUploadHeader(UploadApi uploadService, String authHeader) {
		uploadService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return uploadService;
	}

	public ResourceServicesApi addResourceHeaders(ResourceServicesApi resourceService, String authHeader) {
		resourceService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return resourceService;
	}

	public TaxonomyServicesApi addTaxonomyHeader(TaxonomyServicesApi taxonomyServices, String authHeader) {
		taxonomyServices.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return taxonomyServices;
	}

	public ObservationServiceApi addObservationHeader(ObservationServiceApi observationService, String authHeader) {
		observationService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeader);
		return observationService;
	}

	public ActivitySerivceApi addActivityHeader(ActivitySerivceApi activityService, String authHeaders) {
		activityService.getApiClient().addDefaultHeader(HttpHeaders.AUTHORIZATION, authHeaders);
		return activityService;
	}

}
