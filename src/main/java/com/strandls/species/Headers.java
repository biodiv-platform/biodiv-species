/**
 * 
 */
package com.strandls.species;

import javax.ws.rs.core.HttpHeaders;

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

}
