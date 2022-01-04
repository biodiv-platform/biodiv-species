package com.strandls.species.es.util;

public enum SpeciesIndex {

	INDEX("extended_species"),
	TYPE("_doc"),
	SCIENTIFICNAME("taxonomyDefinition.defaultHierarchy.name"),//match phrase
	COMMONNAME("taxonomicNames.commonNames.name"),//match phrase 
	SGROUP("speciesGroup.id"), //terms search comma sep or query
	USERGROUPID("userGroups.id"), //terms search comma sep or query
	TAXONID("taxonomyDefinition.defaultHierarchy.id"), //terms search comma sep or query
	MEDIATYPE("resourceData.resource.type"), //terms or bool query
	TRAITS("facts.value"),//term or bool query
	CREATEDON("species.dateCreated"),
	LASTREVISED("species.lastUpdated"),
	MEDIA_TYPE_KEYWORD("resourceData.resource.type.keyword"),
	FACT_KEYWORD("facts.valueId"),
	RANK_KEYWORD("taxonomyDefinition.rank.keyword")
	;

	private String field;

	private SpeciesIndex(String field) {
		this.field = field;
	}

	public String getValue() {
		return field;
	}
}
