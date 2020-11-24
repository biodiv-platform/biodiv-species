/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.SpeciesFieldLicense;
import com.strandls.species.util.AbstractDAO;

/**
 * @author Abhishek Rudra
 *
 * 
 */
public class SpeciesFieldLicenseDao extends AbstractDAO<SpeciesFieldLicense, Long> {

	private final Logger logger = LoggerFactory.getLogger(SpeciesFieldLicenseDao.class);

	/**
	 * @param sessionFactory
	 */
	@Inject
	protected SpeciesFieldLicenseDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public SpeciesFieldLicense findById(Long id) {
		SpeciesFieldLicense result = null;
		Session session = sessionFactory.openSession();
		try {
			result = session.get(SpeciesFieldLicense.class, id);
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
