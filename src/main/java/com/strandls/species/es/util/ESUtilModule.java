package com.strandls.species.es.util;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;


public class ESUtilModule  extends AbstractModule {

	@Override
	protected void configure() {
		bind(ESUtility.class).in(Scopes.SINGLETON);	
	}
}

