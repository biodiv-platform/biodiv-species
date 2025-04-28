package com.strandls.species.util;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.spatial.dialect.postgis.PostgisDialect;

/**
 * Custom PostgreSQL dialect that registers the ltree type
 */
public class CustomPostgisDialect extends PostgisDialect {

	public CustomPostgisDialect() {
		super();
		// Register the ltree type with our custom handler
		registerColumnType(Types.OTHER, "ltree");
		// Register the custom type
		registerHibernateType(Types.OTHER, "com.strandls.species.util.LTreeType");
	}
}