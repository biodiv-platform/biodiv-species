package com.strandls.species.service.Impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.google.inject.Inject;
import com.strandls.species.pojo.Contributor;
import com.strandls.species.pojo.Reference;
import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.pojo.SpeciesFieldContributor;
import com.strandls.species.pojo.SpeciesFieldLicense;
import com.strandls.species.pojo.SpeciesFieldUpdateData;
import com.strandls.species.pojo.SpeciesFieldUser;

/**
 * Writes the species fields of one batch upload row in a single session and
 * transaction. It stores the same rows as the create path of
 * SpeciesServiceImpl.updateSpeciesField (species_field, its attribution in
 * contributor + species_field_contributor, species_field_suser,
 * species_field_license, reference), without the per-field lookups and calls that
 * path makes.
 */
public class SpeciesBatchWriter {

	private static final int JDBC_BATCH_SIZE = 50;

	private final SessionFactory sessionFactory;

	@Inject
	public SpeciesBatchWriter(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Adds every payload to the species page; all or nothing.
	 *
	 * @return the new species field ids, in payload order
	 * @throws RuntimeException if anything fails (the transaction is rolled back)
	 */
	public List<Long> saveRow(Long speciesId, Long uploaderId, List<SpeciesFieldUpdateData> payloads) {
		List<Long> ids = new ArrayList<>();
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			session.setJdbcBatchSize(JDBC_BATCH_SIZE);
			tx = session.beginTransaction();
			Date now = new Date();

			for (SpeciesFieldUpdateData payload : payloads) {
				SpeciesField field = new SpeciesField(null, payload.getSfDescription(), payload.getFieldId(),
						speciesId, payload.getSfStatus(), "species.SpeciesField", now, now, now, uploaderId,
						payload.getLanguageId(), null, false);
				session.persist(field);
				Long fieldId = field.getId();

				// attribution: a contributor row of its own, linked to the field
				Contributor attribution = new Contributor(null, payload.getAttributions());
				session.persist(attribution);
				session.persist(new SpeciesFieldContributor(fieldId, attribution.getId()));

				for (Long userId : payload.getContributorIds())
					session.persist(new SpeciesFieldUser(fieldId, userId));

				session.persist(new SpeciesFieldLicense(fieldId, payload.getLicenseId()));

				if (payload.getReferences() != null) {
					for (Reference ref : payload.getReferences()) {
						if (ref.getTitle() == null)
							continue;
						Reference reference = new Reference();
						reference.setTitle(ref.getTitle());
						reference.setUrl(ref.getUrl());
						reference.setSpeciesFieldId(fieldId);
						session.persist(reference);
					}
				}
				ids.add(fieldId);
			}

			tx.commit();
			return ids;
		} catch (RuntimeException e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}
}
