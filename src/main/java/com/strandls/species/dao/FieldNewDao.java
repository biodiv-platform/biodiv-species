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

	@SuppressWarnings("unchecked")
	public List<FieldNew> findNullParent() {
		List<FieldNew> result = null;
		String qry = "from FieldNew where parentId is NULL order by displayOrder";
		Session session = sessionFactory.openSession();
		try {
			Query<FieldNew> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FieldNew> findByParentId(Long parentId) {
		List<FieldNew> result = null;
		String qry = "from FieldNew where parentId = :parentId order by displayOrder";
		Session session = sessionFactory.openSession();
		try {
			Query<FieldNew> query = session.createQuery(qry);
			query.setParameter("parentId", parentId);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FieldNew> getLeafNodes() {
		List<FieldNew> result = null;
		String qry = "from FieldNew f " + "where f.id not in ( " + "    select distinct fn.parentId "
				+ "    from FieldNew fn " + "    where fn.parentId is not null " + ") " + "order by f.id";

		Session session = sessionFactory.openSession();
		try {
			Query<FieldNew> query = session.createQuery(qry);
			result = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			session.close();
		}
		return result;
	}

}
