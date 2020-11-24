/**
 * 
 */
package com.strandls.species.service;

import java.util.List;

import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.SpeciesTrait;

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

}
