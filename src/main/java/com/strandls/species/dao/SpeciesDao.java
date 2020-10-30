/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
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

}
