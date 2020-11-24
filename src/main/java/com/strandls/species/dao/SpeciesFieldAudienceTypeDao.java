/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.SpeciesFieldAudienceType;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldAudienceTypeDao extends AbstractDAO<SpeciesFieldAudienceType, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesFieldAudienceTypeDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesFieldAudienceTypeDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesFieldAudienceType findById(Long id) {
		SpeciesFieldAudienceType result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesFieldAudienceType.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
