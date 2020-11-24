/**
 * 
 */
package com.strandls.species.dao;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesDaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SpeciesDao.class).in(Scopes.SINGLETON);
		bind(FieldDao.class).in(Scopes.SINGLETON);
		bind(SpeciesFieldDao.class).in(Scopes.SINGLETON);
		bind(FieldNewDao.class).in(Scopes.SINGLETON);
		bind(FieldHeaderDao.class).in(Scopes.SINGLETON);
		bind(ReferenceDao.class).in(Scopes.SINGLETON);
		bind(ContributorDao.class).in(Scopes.SINGLETON);
		bind(SpeciesFieldAudienceTypeDao.class).in(Scopes.SINGLETON);
		bind(SpeciesFieldLicenseDao.class).in(Scopes.SINGLETON);
		bind(SpeciesFieldUserDao.class).in(Scopes.SINGLETON);
	}
}
