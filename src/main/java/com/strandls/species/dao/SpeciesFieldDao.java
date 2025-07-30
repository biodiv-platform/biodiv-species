/**
 * 
 */
package com.strandls.species.dao;

import java.util.List;

import jakarta.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.SpeciesField;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldDao extends AbstractDAO<SpeciesField, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesFieldDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesFieldDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesField findById(Long id) {
		SpeciesField result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesField.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<SpeciesField> findBySpeciesId(Long speciesId) {
		String qry = "from SpeciesField where speciesId = :speciesId and isDeleted = false";
		Session session = sessionFactory.openSession();
		List<SpeciesField> result = null;
		try {
			Query<SpeciesField> query = session.createQuery(qry);
			query.setParameter("speciesId", speciesId);
			result = query.getResultList();

		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
