/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.strandls.species.pojo.FieldNew;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class FieldNewDao extends AbstractDAO<FieldNew, Long> {

	private final Logger logger = LoggerFactory.getLogger(FieldNewDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected FieldNewDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public FieldNew findById(Long id) {
		FieldNew result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(FieldNew.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
