/**
 * 
 */
package com.strandls.species.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesTrait;
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

}
