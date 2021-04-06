/**
 * 
 */
package com.strandls.species.service.Impl;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
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
	}
}
