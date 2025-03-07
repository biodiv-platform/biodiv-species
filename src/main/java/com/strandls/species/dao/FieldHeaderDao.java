/**
 * 
 */
package com.strandls.species.dao;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.species.pojo.FieldHeader;
import com.strandls.species.util.AbstractDAO;

import java.util.List;

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

	@SuppressWarnings("unchecked")
	public FieldHeader findByFieldId(Long fieldId, Long languageId) {
		String qry = "from FieldHeader where fieldId = :fieldId and languageId = :languageId ";
		Session session = sessionFactory.openSession();
		FieldHeader result = null;
		try {
			Query<FieldHeader> query = session.createQuery(qry);
			query.setParameter("fieldId", fieldId);
			query.setParameter("languageId", languageId);
			result = query.getSingleResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public FieldHeader findByFieldIdAndLanguageId(Long fieldId, Long languageId) {
		String qry = "from FieldHeader where fieldId = :fieldId and languageId = :languageId";
		Session session = sessionFactory.openSession();
		try {
			Query<FieldHeader> query = session.createQuery(qry);
			query.setParameter("fieldId", fieldId);
			query.setParameter("languageId", languageId);
			return query.uniqueResult();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<FieldHeader> findAllByFieldId(Long fieldId) {
		String qry = "from FieldHeader where fieldId = :fieldId";
		Session session = sessionFactory.openSession();
		try {
			Query<FieldHeader> query = session.createQuery(qry);
			query.setParameter("fieldId", fieldId);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return null;
	}

}
