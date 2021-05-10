/**
 * 
 */
package com.strandls.species.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.Species;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesDao extends AbstractDAO<Species, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Species findById(Long id) {
		Species result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Species.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Species findByTaxonId(Long taxonId) {
		String qry = "from Species where taxonConceptId = :taxonId ";
		Session session = sessionFactory.openSession();
		Species result = null;
		try {
			Query<Species> query = session.createQuery(qry);
			query.setParameter("taxonId", taxonId);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Species> fetchInBatches(String orderBy, String offset) {
		String qry = "from Species";
		if (orderBy.equalsIgnoreCase("lastUpdated"))
			qry = qry + " order by lastUpdated DESC";
		else
			qry = qry + " order by dateCreated ASC";
		List<Species> result = null;
		Session session = sessionFactory.openSession();
		try {
			Query<Species> query = session.createQuery(qry);
			query.setFirstResult(Integer.parseInt(offset));
			query.setMaxResults(10);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public Long fetchCountOfSpeices() {
		String qry = "select count(id) from Species";
		Session session = sessionFactory.openSession();
		Long result = null;
		try {
			Query<Long> query = session.createNativeQuery(qry);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;

	}

}
