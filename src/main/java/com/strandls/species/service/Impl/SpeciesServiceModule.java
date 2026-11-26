/**
 * 
 */
package com.strandls.species.service.Impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.strandls.resource.controllers.LicenseControllerApi;
import com.strandls.species.config.CacheConfig;
import com.strandls.species.service.SpeciesBatchService;
import com.strandls.species.service.SpeciesListService;
import com.strandls.species.service.SpeciesServices;

/**
 * @author Abhishek Rudra
 *
 *
 */
public class SpeciesServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SpeciesServices.class).to(SpeciesServiceImpl.class).in(Scopes.SINGLETON);
		bind(SpeciesHelper.class).in(Scopes.SINGLETON);
		bind(LogActivities.class).in(Scopes.SINGLETON);
		bind(SpeciesListService.class).to(SpeciesListServiceImpl.class).in(Scopes.SINGLETON);
		bind(SpeciesBatchService.class).to(SpeciesBatchServiceImpl.class).in(Scopes.SINGLETON);
		bind(SpeciesBatchWriter.class).in(Scopes.SINGLETON);
		// clients used only by the batch upload preview
		bind(LicenseControllerApi.class).in(Scopes.SINGLETON);
		bind(CacheConfig.class).in(Scopes.SINGLETON);
	}
}
