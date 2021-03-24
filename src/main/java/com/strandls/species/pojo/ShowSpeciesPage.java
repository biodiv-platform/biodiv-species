/**
 * 
 */
package com.strandls.species.pojo;

import java.util.List;
import java.util.Map;

import com.strandls.resource.pojo.ResourceData;
import com.strandls.taxonomy.pojo.BreadCrumb;
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
public class ShowSpeciesPage {

	private Species species;
	private List<BreadCrumb> breadCrumbs;
	private TaxonomyDefinition taxonomyDefinition;
	private List<ResourceData> resourceData;
	private List<SpeciesFieldData> fieldData;
	private List<FactValuePair> facts;
	private List<UserGroupIbp> userGroups;
	private List<Featured> featured;
	private TaxonomicNames taxonomicNames;
	private Map<String, Long> temporalData;
//	private List<DocumentMeta> documentMetaList;

	/**
	 * 
	 */
	public ShowSpeciesPage() {
		super();
	}

	/**
	 * @param species
	 * @param breadCrumbs
	 * @param taxonomyDefinition
	 * @param resourceData
	 * @param fieldData
	 * @param facts
	 * @param userGroups
	 * @param featured
	 * @param taxonomicNames
	 * @param temporalData
	 */
	public ShowSpeciesPage(Species species, List<BreadCrumb> breadCrumbs, TaxonomyDefinition taxonomyDefinition,
			List<ResourceData> resourceData, List<SpeciesFieldData> fieldData, List<FactValuePair> facts,
			List<UserGroupIbp> userGroups, List<Featured> featured, TaxonomicNames taxonomicNames,
			Map<String, Long> temporalData) {
		super();
		this.species = species;
		this.breadCrumbs = breadCrumbs;
		this.taxonomyDefinition = taxonomyDefinition;
		this.resourceData = resourceData;
		this.fieldData = fieldData;
		this.facts = facts;
		this.userGroups = userGroups;
		this.featured = featured;
		this.taxonomicNames = taxonomicNames;
		this.temporalData = temporalData;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
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

//	public List<DocumentMeta> getDocumentMetaList() {
//		return documentMetaList;
//	}
//
//	public void setDocumentMetaList(List<DocumentMeta> documentMetaList) {
//		this.documentMetaList = documentMetaList;
//	}

}
