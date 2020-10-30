/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class FieldHeaderDao extends AbstractDAO<FieldHeader, Long> {

	private final Logger logger = LoggerFactory.getLogger(FieldHeaderDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected FieldHeaderDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public FieldHeader findById(Long id) {
		FieldHeader result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(FieldHeader.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
