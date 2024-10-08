/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.strandls.document.pojo.DocumentMeta;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.taxonomy.pojo.BreadCrumb;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.SpeciesGroup;
import com.strandls.taxonomy.pojo.TaxonomicNames;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.UserGroupIbp;

/**
 * @author Abhishek Rudra
 *
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShowSpeciesPage {

	private Species species;
	private CommonName prefferedCommonName;
	private SpeciesGroup speciesGroup;
	private List<BreadCrumb> breadCrumbs;
	private TaxonomyDefinition taxonomyDefinition;
	private List<ResourceData> resourceData;
	private List<SpeciesFieldData> fieldData;
	private List<FactValuePair> facts;
	private List<UserGroupIbp> userGroups;
	private List<Featured> featured;
	private TaxonomicNames taxonomicNames;
	private Map<String, Long> temporalData;
	private List<DocumentMeta> documentMetaList;
	private List<Reference> referencesListing;

	/**
	 * 
	 */
	public ShowSpeciesPage() {
		super();
	}

	/**
	 * @param species
	 * @param prefferedCommonName
	 * @param speciesGroup
	 * @param breadCrumbs
	 * @param taxonomyDefinition
	 * @param resourceData
	 * @param fieldData
	 * @param facts
	 * @param userGroups
	 * @param featured
	 * @param taxonomicNames
	 * @param temporalData
	 * @param documentMetaList
	 * @param referencesListing
	 */
	public ShowSpeciesPage(Species species, CommonName prefferedCommonName, SpeciesGroup speciesGroup,
			List<BreadCrumb> breadCrumbs, TaxonomyDefinition taxonomyDefinition, List<ResourceData> resourceData,
			List<SpeciesFieldData> fieldData, List<FactValuePair> facts, List<UserGroupIbp> userGroups,
			List<Featured> featured, TaxonomicNames taxonomicNames, Map<String, Long> temporalData,
			List<DocumentMeta> documentMetaList, List<Reference> referencesListing) {
		super();
		this.species = species;
		this.prefferedCommonName = prefferedCommonName;
		this.speciesGroup = speciesGroup;
		this.breadCrumbs = breadCrumbs;
		this.taxonomyDefinition = taxonomyDefinition;
		this.resourceData = resourceData;
		this.fieldData = fieldData;
		this.facts = facts;
		this.userGroups = userGroups;
		this.featured = featured;
		this.taxonomicNames = taxonomicNames;
		this.temporalData = temporalData;
		this.documentMetaList = documentMetaList;
		this.referencesListing = referencesListing;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public CommonName getPrefferedCommonName() {
		return prefferedCommonName;
	}

	public void setPrefferedCommonName(CommonName prefferedCommonName) {
		this.prefferedCommonName = prefferedCommonName;
	}

	public SpeciesGroup getSpeciesGroup() {
		return speciesGroup;
	}

	public void setSpeciesGroup(SpeciesGroup speciesGroup) {
		this.speciesGroup = speciesGroup;
	}

	public List<BreadCrumb> getBreadCrumbs() {
		return breadCrumbs;
	}

	public void setBreadCrumbs(List<BreadCrumb> breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public TaxonomyDefinition getTaxonomyDefinition() {
		return taxonomyDefinition;
	}

	public void setTaxonomyDefinition(TaxonomyDefinition taxonomyDefinition) {
		this.taxonomyDefinition = taxonomyDefinition;
	}

	public List<ResourceData> getResourceData() {
		return resourceData;
	}

	public void setResourceData(List<ResourceData> resourceData) {
		this.resourceData = resourceData;
	}

	public List<SpeciesFieldData> getFieldData() {
		return fieldData;
	}

	public void setFieldData(List<SpeciesFieldData> fieldData) {
		this.fieldData = fieldData;
	}

	public List<FactValuePair> getFacts() {
		return facts;
	}

	public void setFacts(List<FactValuePair> facts) {
		this.facts = facts;
	}

	public List<UserGroupIbp> getUserGroups() {
		return userGroups;
	}

	public void setUserGroups(List<UserGroupIbp> userGroups) {
		this.userGroups = userGroups;
	}

	public List<Featured> getFeatured() {
		return featured;
	}

	public void setFeatured(List<Featured> featured) {
		this.featured = featured;
	}

	public TaxonomicNames getTaxonomicNames() {
		return taxonomicNames;
	}

	public void setTaxonomicNames(TaxonomicNames taxonomicNames) {
		this.taxonomicNames = taxonomicNames;
	}

	public Map<String, Long> getTemporalData() {
		return temporalData;
	}

	public void setTemporalData(Map<String, Long> temporalData) {
		this.temporalData = temporalData;
	}

	public List<DocumentMeta> getDocumentMetaList() {
		return documentMetaList;
	}

	public void setDocumentMetaList(List<DocumentMeta> documentMetaList) {
		this.documentMetaList = documentMetaList;
	}

	public List<Reference> getReferencesListing() {
		return referencesListing;
	}

	public void setReferencesListing(List<Reference> referencesListing) {
		this.referencesListing = referencesListing;
	}

}
