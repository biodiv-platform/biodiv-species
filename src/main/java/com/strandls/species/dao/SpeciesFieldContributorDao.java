/**
 * 
 */
package com.strandls.species.dao;

import jakarta.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.SpeciesFieldContributor;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldContributorDao extends AbstractDAO<SpeciesFieldContributor, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesFieldContributorDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesFieldContributorDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesFieldContributor findById(Long id) {
		SpeciesFieldContributor result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesFieldContributor.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public SpeciesFieldContributor findBySpeciesFieldId(Long speciesFieldId) {
		String qry = "from SpeciesFieldContributor where  speciesFieldId = :speciesFieldId";
		Session session = sessionFactory.openSession();
		SpeciesFieldContributor result = null;
		try {
			Query<SpeciesFieldContributor> query = session.createQuery(qry);
			query.setParameter("speciesFieldId", speciesFieldId);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
