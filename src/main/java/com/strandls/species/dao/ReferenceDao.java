/**
 * 
 */
package com.strandls.species.dao;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.Reference;
import com.strandls.species.util.AbstractDAO;

import jakarta.inject.Inject;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class ReferenceDao extends AbstractDAO<Reference, Long> {

	private final Logger logger = LoggerFactory.getLogger(ReferenceDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected ReferenceDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Reference findById(Long id) {
		Reference result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Reference.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Reference> findBySpeciesFieldId(Long speciesFieldId) {
		String qry = "from Reference where speciesFieldId  = :speciesFieldId";
		Session session = sessionFactory.openSession();
		List<Reference> result = null;
		try {
			Query<Reference> query = session.createQuery(qry);
			query.setParameter("speciesFieldId", speciesFieldId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Reference> findBySpeciesId(Long speciesId) {
		String qry = "from Reference where speciesId  = :speciesId";
		Session session = sessionFactory.openSession();
		List<Reference> result = null;
		try {
			Query<Reference> query = session.createQuery(qry);
			query.setParameter("speciesId", speciesId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Reference updateIsDeleted(Long referenceId) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Reference result = null;
		try {
			tx = session.beginTransaction();
			String qry = "update Reference set isDeleted = true where id = :referenceId";
			Query<Reference> query = session.createQuery(qry);
			query.setParameter("referenceId", referenceId);
			query.executeUpdate();

			// Get the updated reference
			String fetchQry = "from Reference where id = :referenceId";
			Query<Reference> fetchQuery = session.createQuery(fetchQry);
			fetchQuery.setParameter("referenceId", referenceId);
			result = fetchQuery.getSingleResult();

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	public void mergeReferencesBySpeciesIds(List<Long> sourceSpeciesIds, Long targetSpeciesId) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();

			// Fetch existing titles on the target species to detect duplicates
			List<Reference> targetReferences = findBySpeciesId(targetSpeciesId);
			Set<String> existingTitles = targetReferences.stream().map(Reference::getTitle).filter(Objects::nonNull)
					.map(String::toLowerCase).collect(Collectors.toSet());

			for (Long sourceSpeciesId : sourceSpeciesIds) {
				if (sourceSpeciesId.equals(targetSpeciesId))
					continue;

				List<Reference> sourceReferences = findBySpeciesId(sourceSpeciesId);

				for (Reference ref : sourceReferences) {
					String title = ref.getTitle();
					String titleKey = title != null ? title.toLowerCase() : null;

					if (titleKey != null && existingTitles.contains(titleKey)) {
						// Duplicate title — skip
						logger.info("Skipping duplicate reference title '{}' for targetSpeciesId {}", title,
								targetSpeciesId);
						continue;
					}

					// Move reference to target species
					ref.setSpeciesId(targetSpeciesId);
					session.merge(ref);

					// Track this title so subsequent sources don't duplicate it either
					if (titleKey != null) {
						existingTitles.add(titleKey);
					}
				}
			}

			tx.commit();
			logger.info("Merge complete into speciesId {}", targetSpeciesId);

		} catch (Exception e) {
			if (tx != null)
				tx.rollback();
			logger.error("Error merging references: {}", e.getMessage(), e);
		} finally {
			session.close();
		}
	}

}
