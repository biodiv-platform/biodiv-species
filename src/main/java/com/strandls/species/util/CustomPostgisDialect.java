package com.strandls.species.util;

import org.hibernate.dialect.PostgreSQLDialect;

public class CustomPostgisDialect extends PostgreSQLDialect {
    public CustomPostgisDialect() {
        super();
        // Optionally register ltree as a recognized type for metadata
        // This helps Hibernate know about the `ltree` type
        // You may add mapping if needed, but usually columnDefinition="ltree" in @Column is enough
        // this.registerColumnType(Types.OTHER, "ltree");
    }
}
