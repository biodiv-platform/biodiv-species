/**
 * 
 */
package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.resource.controllers.ResourceServicesApi;
import com.strandls.resource.pojo.ResourceData;
import com.strandls.species.dao.FieldDao;
import com.strandls.species.dao.FieldHeaderDao;
import com.strandls.species.dao.FieldNewDao;
import com.strandls.species.dao.SpeciesDao;
import com.strandls.species.dao.SpeciesFieldDao;
import com.strandls.species.pojo.Field;
import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.pojo.FieldNew;
import com.strandls.species.pojo.Species;
import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.service.SpeciesServices;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesServiceImpl implements SpeciesServices {

	private final Logger logger = LoggerFactory.getLogger(SpeciesServiceImpl.class);

//	Injection of Dao
	@Inject
	private SpeciesDao speciesDao;

	@Inject
	private FieldDao fieldDao;

	@Inject
	private SpeciesFieldDao speciesFieldDao;

	@Inject
	private FieldNewDao fieldNewDao;

	@Inject
	private FieldHeaderDao fieldHeaderDao;

//	injection of services

	@Inject
	private ResourceServicesApi resourceServices;

	@Override
	public void showSpeciesPage(Long speciesId) {
		try {
			List<ResourceData> resoruceData = null;

			Species species = speciesDao.findById(speciesId);
			if (!species.getIsDeleted()) {
//				resource data
				resoruceData = resourceServices.getImageResource("SPECIES", species.getId().toString());

//				species Field
				List<SpeciesField> speciesFields = speciesFieldDao.findBySpeciesId(speciesId);

//				segregating on the basis of multiple data
				for (SpeciesField speciesField : speciesFields) {
					FieldNew field = fieldNewDao.findById(speciesField.getFieldId());
//					fieldHeaderDao.
					

				}

//				segregating on the basis of category

			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

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

					FieldHeader fieldHeader = new FieldHeader(null, field.getId(), header,
							(field.getCategory() == null) ? field.getDescription() : null,
							(field.getCategory() == null) ? field.getUrlIdentifier() : null, field.getLanguageId());

//					save the object

					if (fieldNew.getId() == null) {
						fieldListSize++;
						fieldNew.setId(fieldListSize);

					}

					fieldNewDao.save(fieldNew);
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

				FieldHeader fieldHeader = new FieldHeader(null, field.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save the obj
				fieldNewDao.save(fieldNew);
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

				FieldHeader fieldHeader = new FieldHeader(null, field.getId(), header, field.getDescription(),
						field.getUrlIdentifier(), field.getLanguageId());

//				save obj

				fieldNewDao.save(fieldNew);
				fieldHeaderDao.save(fieldHeader);

				subCatMap.put(key, fieldNew);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

}
