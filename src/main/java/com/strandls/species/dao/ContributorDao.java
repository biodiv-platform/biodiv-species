/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.Contributor;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class ContributorDao extends AbstractDAO<Contributor, Long> {

	private final Logger logger = LoggerFactory.getLogger(ContributorDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected ContributorDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public Contributor findById(Long id) {
		Contributor result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(Contributor.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
