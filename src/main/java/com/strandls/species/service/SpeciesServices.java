/**
 * 
 */
package com.strandls.species.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.taxonomy.pojo.CommonNames;
import com.strandls.taxonomy.pojo.CommonNamesData;
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

	public List<CommonNames> updateAddCommonName(HttpServletRequest request, CommonNamesData commonNamesData);

	public Boolean removeCommonName(HttpServletRequest request, String commonNameId);

}
