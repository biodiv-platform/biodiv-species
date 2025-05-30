/**
 * 
 */
package com.strandls.species.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.activity.pojo.Activity;
import com.strandls.activity.pojo.CommentLoggingData;
import com.strandls.esmodule.ApiException;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.resource.pojo.SpeciesPull;
import com.strandls.species.pojo.FieldCreateData;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldNew;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.FieldTranslationUpdateData;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.ReferenceCreateData;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesCreateData;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesPermission;
import com.strandls.species.pojo.SpeciesPullData;
import com.strandls.species.pojo.SpeciesResourcesPreData;
import com.strandls.species.pojo.SpeciesTrait;

import com.strandls.taxonomy.pojo.CommonName;
import com.strandls.taxonomy.pojo.CommonNamesData;
import com.strandls.taxonomy.pojo.EncryptedKey;
import com.strandls.taxonomy.pojo.PermissionData;
import com.strandls.taxonomy.pojo.SynonymData;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.taxonomy.pojo.TaxonomySave;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.FactsUpdateData;
import com.strandls.user.pojo.Follow;
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

	public List<FieldRender> getFields(Long langId, String userGroupId);

	public List<SpeciesTrait> getSpeciesTraitsByTaxonomyId(Long taxonomyId, Long language);

	public List<SpeciesTrait> getAllSpeciesTraits(Long language);
	
	public List<SpeciesTrait> getAllTraits(Long language);

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

	public Long checkSpeciesPageExist(HttpServletRequest request, Long taxonId);

	public Long createSpeciesPage(HttpServletRequest request, SpeciesCreateData speciesCreateData);

	public TaxonomyDefinition createTaxonomy(HttpServletRequest request, TaxonomySave taxonomySave);

	public List<TaxonomyDefinition> updateAddSynonyms(HttpServletRequest request, String speciesId,
			SynonymData synonymData);

	public List<TaxonomyDefinition> removeSynonyms(HttpServletRequest request, String speciesId, String synonymId);

	public SpeciesPermission checkPermission(HttpServletRequest request, Long speciesId);

	public Boolean sendPermissionRequest(HttpServletRequest request, PermissionData permissionData);

	public Boolean sendPermissionGrant(HttpServletRequest request, EncryptedKey encryptedKey);

	public Follow followRequest(HttpServletRequest request, Long speciesId);

	public Follow unFollowRequest(HttpServletRequest request, Long speciesId);

	public Boolean removeSpeciesPage(HttpServletRequest request, Long speciesId);

	public String getSpeciesIdFromTaxonId(Long taxonId);

	public void ESSpeciesUpdate(long speciesId) throws ApiException;

	CommonName updatePrefferedCommonName(HttpServletRequest request, Long speciesId, Long commonNameId);

	public Activity removeSpeciesComment(HttpServletRequest request, CommentLoggingData comment, String commentId);

	public ShowSpeciesPage showSpeciesPageFromES(Long speciesId, UserGroupIbp userGroup);

	public List<FieldNew> fetchLeafNodes();

	public List<Reference> createReference(HttpServletRequest request, Long speciesId,
			List<ReferenceCreateData> referenceCreateData);

	public Reference editReference(HttpServletRequest request, Long speciesId, Reference reference);

	public Reference deleteReference(HttpServletRequest request, Long referenceId);

	public FieldNew createField(HttpServletRequest request, FieldCreateData fieldData);

	public List<FieldHeader> getFieldTranslations(Long fieldId);

	public FieldHeader getFieldTranslation(Long fieldId, Long languageId);

	public List<FieldHeader> updateFieldTranslations(HttpServletRequest request,
			List<FieldTranslationUpdateData> translationData) throws Exception;

}