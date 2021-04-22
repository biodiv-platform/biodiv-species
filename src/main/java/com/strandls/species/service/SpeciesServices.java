/**
 * 
 */
package com.strandls.species.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesCreateData;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesPullData;
import com.strandls.species.pojo.SpeciesResourcesPreData;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.CommonNamesData;
import com.strandls.taxonomy.pojo.PermissionData;
import com.strandls.taxonomy.pojo.SynonymData;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.taxonomy.pojo.TaxonomySave;
import com.strandls.taxonomy.pojo.TaxonomySearch;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.FactsUpdateData;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.FeaturedCreate;
import com.strandls.userGroup.pojo.UserGroupIbp;
import com.strandls.userGroup.pojo.UserGroupSpeciesCreateData;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public interface SpeciesServices {

	public ShowSpeciesPage showSpeciesPage(Long speciesId);

	public List<FieldRender> getFields();

	public List<SpeciesTrait> getSpeciesTraitsByTaxonomyId(Long taxonomyId);

	public List<SpeciesTrait> getAllSpeciesTraits();

	public void migrateField();

//	edits in speceis show page

	public List<UserGroupIbp> updateUserGroup(HttpServletRequest request, String speciesId,
			UserGroupSpeciesCreateData ugSpeciesCreateData);

	public List<Featured> createFeatured(HttpServletRequest request, FeaturedCreate featuredCreate);

	public List<Featured> unFeatured(HttpServletRequest request, String speciesId, List<Long> userGroupList);

	public List<FactValuePair> updateTraits(HttpServletRequest request, String speciesId, String traitId,
			FactsUpdateData factsUpdateData);

	public SpeciesFieldData updateSpeciesField(HttpServletRequest request, Long speciesId,
			SpeciesFieldUpdateData sfUpdatedata);

	public Boolean removeSpeciesField(HttpServletRequest request, Long speciesfield);

	public List<CommonName> updateAddCommonName(HttpServletRequest request, Long speciesId,
			CommonNamesData commonNamesData);

	public List<CommonName> removeCommonName(HttpServletRequest request, Long speciesId, String commonNameId);

	public List<SpeciesPull> getObservationResource(Long speciesId, Long offset);

	public List<ResourceData> pullResource(HttpServletRequest request, Long speciesId,
			List<SpeciesPullData> speciesPullData);

	public List<ResourceData> getSpeciesResources(HttpServletRequest request, Long speciesId);

	public List<ResourceData> updateSpciesResources(HttpServletRequest request, Long speciesId,
			List<SpeciesResourcesPreData> preDataList);

	public Activity addSpeciesComment(HttpServletRequest request, CommentLoggingData loggingData);

	public TaxonomySearch checkTaxonomyExist(HttpServletRequest request, String speciesName, String rank);

	public Long checkSpeciesPageExist(HttpServletRequest request, Long taxonId);

	public Long createSpeciesPage(HttpServletRequest request, SpeciesCreateData speciesCreateData);

	public TaxonomyDefinition createTaxonomy(HttpServletRequest request, TaxonomySave taxonomySave);

	public List<TaxonomyDefinition> updateAddSynonyms(HttpServletRequest request, String speciesId,
			SynonymData synonymData);

	public List<TaxonomyDefinition> removeSynonyms(HttpServletRequest request, String speciesId, String synonymId);

	public Boolean checkPermission(HttpServletRequest request, Long speciesId);

	public Boolean sendPermissionRequest(HttpServletRequest request, PermissionData permissionData);

	public Boolean sendPermissionGrant(HttpServletRequest request, String encryptedKey);

}
