package com.strandls.species.es.util;

public enum SpeciesIndex {

	INDEX("extended_species"), TYPE("_doc"), SCIENTIFICNAME("taxonomyDefinition.name"), // match phrase
	SYNONYM("taxonomicNames.synonyms.name"), // match phrase
	COMMONNAME("taxonomicNames.commonNames.name"), // match phrase
	SGROUP("speciesGroup.id"), // terms search comma sep or query
	USERGROUPID("userGroups.id"), // terms search comma sep or query
	TAXONID("breadCrumbs.id"), // terms search comma sep or query
	MEDIATYPE("resourceData.resource.type"), // terms or bool query
	TRAITS("facts.value"), // term or bool query
	CREATEDON("species.dateCreated"), LASTREVISED("species.lastUpdated"),
	MEDIA_TYPE_KEYWORD("resourceData.resource.type.keyword"), FACT_KEYWORD("facts.valueId"),
	RANK_KEYWORD("taxonomyDefinition.rank.keyword"), FIELD_PATH("fieldData.path"),
	FIELD_DESCRIPTION("fieldData.fieldData.description"), FIELD_REFERENCES("fieldData.references.title"),
	FIELD_CONTRIBUTOR("fieldData.contributor.id"), FIELD_ATTRIBUTION("fieldData.attributions"),
	TRAITS_NAME_KEYWORD("facts.nameId"), TRAITS_COLOR_HUE("facts.color.h"), TRAITS_COLOR_SATURATION("facts.color.s"),
	TRAITS_COLOR_VALUE("facts.color.v"), TRAITS_RANGE_MAX("facts.range.max"), TRAITS_RANGE_MIN("facts.range.min"),
	TRAITS_COLOR_GROUP("facts.color.1"), TRAITS_RANGE_GROUP("facts.range.1"), TRAITS_COLOR_NAMEID("facts.color.nameId"),
	TRAITS_RANGE_NAMEID("facts.range.nameId"), FACTS_GROUP("facts.1"), TRAITS_TO_DATE("facts.toDate"),
	TRAITS_FROM_DATE("facts.fromDate"), TRAITS_NAME_ID("facts.nameId"), FIELD_DATA("fieldData");

	private String field;

	private SpeciesIndex(String field) {
		this.field = field;
	}

	public String getValue() {
		return field;
	}
}
