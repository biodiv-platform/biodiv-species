/**
 * 
 */
package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.License;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.species.dao.ContributorDao;
import com.strandls.species.dao.FieldDao;
import com.strandls.species.dao.FieldHeaderDao;
import com.strandls.species.dao.FieldNewDao;
import com.strandls.species.dao.ReferenceDao;
import com.strandls.species.dao.SpeciesDao;
import com.strandls.species.dao.SpeciesFieldAudienceTypeDao;
import com.strandls.species.dao.SpeciesFieldContributorDao;
import com.strandls.species.dao.SpeciesFieldDao;
import com.strandls.species.dao.SpeciesFieldLicenseDao;
import com.strandls.species.dao.SpeciesFieldUserDao;
import com.strandls.species.pojo.Contributor;
import com.strandls.species.pojo.Field;
import com.strandls.species.pojo.FieldDisplay;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldNew;
import com.strandls.species.pojo.FieldRender;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.ShowSpeciesPage;
import com.strandls.species.pojo.Species;
import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.pojo.SpeciesFieldAudienceType;
import com.strandls.species.pojo.SpeciesFieldContributor;
import com.strandls.species.pojo.SpeciesFieldData;
import com.strandls.species.pojo.SpeciesFieldLicense;
import com.strandls.species.pojo.SpeciesTrait;
import com.strandls.species.service.SpeciesServices;
import com.strandls.taxonomy.controllers.TaxonomyServicesApi;
import com.strandls.taxonomy.pojo.BreadCrumb;
import com.strandls.taxonomy.pojo.TaxonomyDefinition;
import com.strandls.traits.controller.TraitsServiceApi;
import com.strandls.traits.pojo.FactValuePair;
import com.strandls.traits.pojo.TraitsValuePair;
import com.strandls.user.controller.UserServiceApi;
import com.strandls.user.pojo.UserIbp;
import com.strandls.userGroup.controller.UserGroupSerivceApi;
import com.strandls.userGroup.pojo.Featured;
import com.strandls.userGroup.pojo.UserGroupIbp;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesServiceImpl implements SpeciesServices {

	private final Logger logger = LoggerFactory.getLogger(SpeciesServiceImpl.class);

//	Injection of Dao

	@Inject
	private ContributorDao contributorDao;

	@Inject
	private FieldDao fieldDao;

	@Inject
	private FieldNewDao fieldNewDao;

	@Inject
	private FieldHeaderDao fieldHeaderDao;

	@Inject
	private ReferenceDao referenceDao;

	@Inject
	private SpeciesDao speciesDao;

	@Inject
	private SpeciesFieldDao speciesFieldDao;

	@Inject
	private SpeciesFieldUserDao sfUserDao;

	@Inject
	private SpeciesFieldAudienceTypeDao sfAudienceTypeDao;

	@Inject
	private SpeciesFieldContributorDao sfContributorDao;

	@Inject
	private SpeciesFieldLicenseDao sfLicenseDao;

//	injection of services

//	@Inject
//	private DocumentServiceApi documentService;

	@Inject
	private ResourceServicesApi resourceServices;

	@Inject
	private UserServiceApi userService;

	@Inject
	private UserGroupSerivceApi ugService;

	@Inject
	private TaxonomyServicesApi taxonomyService;

	@Inject
	private TraitsServiceApi traitService;

	@Override
	public ShowSpeciesPage showSpeciesPage(Long speciesId) {
		try {
			List<ResourceData> resourceData = null;

			Species species = speciesDao.findById(speciesId);
			if (!species.getIsDeleted()) {
//				resource data
				resourceData = resourceServices.getImageResource("SPECIES", species.getId().toString());

//				traits
				List<FactValuePair> facts = traitService.getFacts("species.Species", speciesId.toString());

//				species Field
				List<SpeciesField> speciesFields = speciesFieldDao.findBySpeciesId(speciesId);

				List<SpeciesFieldData> fieldData = new ArrayList<SpeciesFieldData>();

//				segregating on the basis of multiple data
				for (SpeciesField speciesField : speciesFields) {

					FieldNew field = fieldNewDao.findById(speciesField.getFieldId());
					FieldHeader fieldHeader = fieldHeaderDao.findById(field.getId());

					SpeciesFieldAudienceType sfAudienceType = sfAudienceTypeDao.findById(speciesField.getId());

					SpeciesFieldLicense sfLicense = sfLicenseDao.findById(speciesField.getId());
					License sfLicenseData = resourceServices.getLicenseResource(sfLicense.getLicenseId().toString());

					List<Reference> references = referenceDao.findBySpeciesFieldId(speciesField.getId());

//					this is actually the attribution of speciesField and a String 

					SpeciesFieldContributor sfAttribution = sfContributorDao.findBySpeciesFieldId(speciesField.getId());
					Contributor attribution = contributorDao.findById(sfAttribution.getContributorId());

//					species field uploader is the contributor of species field

					UserIbp contributor = userService.getUserIbp(speciesField.getUploaderId().toString());

					fieldData.add(new SpeciesFieldData(speciesField.getId(), field.getId(), field.getDisplayOrder(),
							field.getLabel(), fieldHeader.getHeader(), speciesField, references, attribution.getName(),
							contributor, (sfAudienceType != null) ? sfAudienceType.getAudienceType() : null,
							sfLicenseData));

				}

				List<BreadCrumb> breadCrumbs = taxonomyService
						.getTaxonomyBreadCrumb(species.getTaxonConceptId().toString());

				TaxonomyDefinition taxonomyDefinition = taxonomyService
						.getTaxonomyConceptName(species.getTaxonConceptId().toString());

//				List<DocumentMeta> documentMetaList = documentService
//						.getDocumentByTaxonConceptId(species.getTaxonConceptId().toString());

				List<UserGroupIbp> userGroupList = ugService.getSpeciesUserGroup(speciesId.toString());
				List<Featured> featured = ugService.getAllFeatured("species.Species", speciesId.toString());

				
				ShowSpeciesPage showSpeciesPage = new ShowSpeciesPage(species, breadCrumbs, taxonomyDefinition,
						resourceData, fieldData, facts, userGroupList, featured);
				
//				ShowSpeciesPage showSpeciesPage = new ShowSpeciesPage(species, breadCrumbs, taxonomyDefinition,
//						resourceData, fieldData, facts, userGroupList, featured, documentMetaList);

				return showSpeciesPage;

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;

	}

//	migration of field to new structure

	Map<String, FieldNew> conceptMap = new HashMap<String, FieldNew>();
	Map<String, FieldNew> categoryMap = new HashMap<String, FieldNew>();
	Map<String, FieldNew> subCatMap = new HashMap<String, FieldNew>();

	Map<String, Long> count = new HashMap<String, Long>();
	Long fieldListSize = null;

	@Override
	public void migrateField() {

		try {
			List<Field> fieldList = fieldDao.findByLanguageId(205L);
			fieldListSize = Long.parseLong(String.valueOf(fieldList.size()));
			for (Field field : fieldList) {

				String header = field.getConcept();
				String lable = "Concept";

				if (!conceptMap.containsKey(header)) {
					FieldNew fieldNew = new FieldNew((field.getCategory() == null) ? field.getId() : null, null,
							Long.parseLong(String.valueOf((conceptMap.size() + 1))), lable, header);

//					save the object

					if (fieldNew.getId() == null) {
						fieldListSize++;
						fieldNew.setId(fieldListSize);

					}

					fieldNewDao.save(fieldNew);

					FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header,
							(field.getCategory() == null) ? field.getDescription() : null,
							(field.getCategory() == null) ? field.getUrlIdentifier() : null, field.getLanguageId());

					fieldHeaderDao.save(fieldHeader);

					conceptMap.put(header, fieldNew);
					count.put(header, 1L);

					if (field.getCategory() != null) {
						saveCategory(field);
					}

				} else {
					if (field.getCategory() != null) {
						if (count.get(field.getConcept() + ":" + field.getCategory()) == null) {
							Long value = count.get(header);
							count.put(header, value + 1);
						}
						saveCategory(field);
					}

				}

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private void saveCategory(Field field) {

		try {
			String key = field.getConcept() + ":" + field.getCategory();
			String header = field.getCategory();
			String lable = "Category";
			if (!categoryMap.containsKey(key)) {

				Long fieldId = null;

				if (field.getSubCategory() == null) {
					fieldId = field.getId();
				} else {
					fieldListSize++;
					fieldId = fieldListSize;
				}

				FieldNew fieldNew = new FieldNew(fieldId, conceptMap.get(field.getConcept()).getId(),
						count.get(field.getConcept()), lable, header);

				fieldNewDao.save(fieldNew);

				FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save the obj

				fieldHeaderDao.save(fieldHeader);

				categoryMap.put(key, fieldNew);
				count.put(key, 1L);

				if (field.getSubCategory() != null) {
					saveSubCategory(field);
				}

			} else {

				if (field.getSubCategory() != null) {
					saveSubCategory(field);
					if (count.get(field.getCategory() + ":" + field.getSubCategory()) == null) {
						Long value = count.get(key);
						count.put(key, value + 1);
					}
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private void saveSubCategory(Field field) {
		try {
			String key = field.getCategory() + ":" + field.getSubCategory();
			String header = field.getSubCategory();
			String label = "SubCategory";
			if (!subCatMap.containsKey(key)) {
				FieldNew fieldNew = new FieldNew(field.getId(),
						categoryMap.get(field.getConcept() + ":" + field.getCategory()).getId(),
						count.get(field.getConcept() + ":" + field.getCategory()), label, header);

				fieldNewDao.save(fieldNew);
				FieldHeader fieldHeader = new FieldHeader(null, fieldNew.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save obj

				fieldHeaderDao.save(fieldHeader);

				subCatMap.put(key, fieldNew);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	public List<FieldRender> getFields() {

		List<FieldRender> renderList = new ArrayList<FieldRender>();

//		extract all the concept fields in display order
		List<FieldNew> concpetFields = fieldNewDao.findNullParent();

		for (FieldNew concpetField : concpetFields) {

			List<FieldDisplay> categorySubCat = new ArrayList<FieldDisplay>();

//			extract all the category fields in display order
			List<FieldNew> categoryFields = fieldNewDao.findByParentId(concpetField.getId());
			for (FieldNew catField : categoryFields) {
//				extract all the subCategory fields in display order
				List<FieldNew> subCatField = fieldNewDao.findByParentId(catField.getId());
				categorySubCat.add(new FieldDisplay(catField, subCatField));
			}

			renderList.add(new FieldRender(concpetField, categorySubCat));
		}

		return renderList;

	}

	@Override
	public List<SpeciesTrait> getSpeciesTraitsByTaxonomyId(Long taxonomyId) {
		try {
			List<TraitsValuePair> traitValuePairLIst = traitService.getSpeciesTraits(taxonomyId.toString());
			List<SpeciesTrait> arranged = arrangeTraits(traitValuePairLIst);
			return arranged;

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	private List<SpeciesTrait> arrangeTraits(List<TraitsValuePair> traitValuePairList) {

		TreeMap<String, List<TraitsValuePair>> arrangedPair = new TreeMap<String, List<TraitsValuePair>>();

		for (TraitsValuePair traitsValuePair : traitValuePairList) {
			String name = "";
			FieldNew fieldNew = null;
			Long fieldId = traitsValuePair.getTraits().getFieldId();
			do {
				fieldNew = fieldNewDao.findById(fieldId);
				name = fieldHeaderDao.findByFieldId(fieldNew.getId(), 205L).getHeader() + " > " + name;
				fieldId = fieldNew.getParentId();

			} while (fieldNew.getParentId() != null);
			name = name.substring(0, name.length() - 3);
			System.out.println(name);
			if (arrangedPair.containsKey(name)) {
				List<TraitsValuePair> pairList = arrangedPair.get(name);
				pairList.add(traitsValuePair);
				arrangedPair.put(name, pairList);
			} else {
				List<TraitsValuePair> pairList = new ArrayList<TraitsValuePair>();
				pairList.add(traitsValuePair);
				arrangedPair.put(name, pairList);
			}

		}

		List<SpeciesTrait> result = new ArrayList<SpeciesTrait>();
		for (Entry<String, List<TraitsValuePair>> entry : arrangedPair.entrySet()) {
			result.add(new SpeciesTrait(entry.getKey(), entry.getValue()));
		}

		return result;

	}

	@Override
	public List<SpeciesTrait> getAllSpeciesTraits() {
		try {
			List<TraitsValuePair> traitsValuePairList = traitService.getAllSpeciesTraits();
			List<SpeciesTrait> arranged = arrangeTraits(traitsValuePairList);
			return arranged;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return null;
	}

}
