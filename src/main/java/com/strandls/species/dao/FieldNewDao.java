/**
 * 
 */
package com.strandls.species.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

	public Long getMaxDisplayOrderForParent(Long parentId) {
		Session session = sessionFactory.openSession();
		try {
			String query;
			if (parentId == null) {
				query = "SELECT MAX(displayOrder) FROM FieldNew WHERE parentId IS NULL";
				Query<Long> q = session.createQuery(query, Long.class);
				Long result = q.uniqueResult();
				return result != null ? result : 0L;
			} else {
				query = "SELECT MAX(displayOrder) FROM FieldNew WHERE parentId = :parentId";
				Query<Long> q = session.createQuery(query, Long.class);
				q.setParameter("parentId", parentId);
				Long result = q.uniqueResult();
				return result != null ? result : 0L;
			}
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("rawtypes")
	public void updatePath(FieldNew field) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			if (field.getParentId() != null) {
				// Get parent path
				String parentPathQuery = "SELECT path FROM field_new WHERE id = :parentId";
				Query pathQuery = session.createNativeQuery(parentPathQuery);
				pathQuery.setParameter("parentId", field.getParentId());
				String parentPath = (String) pathQuery.uniqueResult();

				// Update path using parent path
				String updatePathQuery = "UPDATE field_new SET path = text2ltree(:newPath) WHERE id = :fieldId";
				String newPath = parentPath + "." + field.getId();

				Query updateQuery = session.createNativeQuery(updatePathQuery);
				updateQuery.setParameter("newPath", newPath);
				updateQuery.setParameter("fieldId", field.getId());
				updateQuery.executeUpdate();
			} else {
				// Top level - just use ID
				String updatePathQuery = "UPDATE field_new SET path = text2ltree(:newPath) WHERE id = :fieldId";

				Query updateQuery = session.createNativeQuery(updatePathQuery);
				updateQuery.setParameter("newPath", field.getId().toString());
				updateQuery.setParameter("fieldId", field.getId());
				updateQuery.executeUpdate();
			}

			tx.commit();
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error(e.getMessage());
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public FieldNew save(FieldNew entity) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// Save the entity normally
			session.save(entity);

			// Update the path field
			if (entity.getParentId() != null) {
				// Get parent path
				String parentPathQuery = "SELECT path FROM field_new WHERE id = :parentId";
				Query pathQuery = session.createNativeQuery(parentPathQuery);
				pathQuery.setParameter("parentId", entity.getParentId());
				String parentPath = (String) pathQuery.uniqueResult();

				// Create new path
				String newPath = parentPath != null ? parentPath + "." + entity.getParentId()
						: entity.getParentId().toString();
				entity.setPath(newPath);

				// Update in the database - use concatenation instead of inline cast
				String updateQuery = "UPDATE field_new SET path = text2ltree(:path) WHERE id = :id";
				Query query = session.createNativeQuery(updateQuery);
				query.setParameter("path", newPath);
				query.setParameter("id", entity.getId());
				query.executeUpdate();
			} else {
				// Top level node - just use the ID
				// String newPath = entity.getId().toString();
				// String newPath = null;
				// entity.setPath(newPath);

				// Update in the database - handle null path
				String updateQuery = "UPDATE field_new SET path = NULL WHERE id = :id";
				Query query = session.createNativeQuery(updateQuery);
				// query.setParameter("path", newPath);
				query.setParameter("id", entity.getId());
				query.executeUpdate();
			}

			tx.commit();
			return entity;
		} catch (Exception e) {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
			logger.error(e.getMessage());
			throw e;
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<FieldNew> findAll() {
		List<FieldNew> result = null;
		String qry = "from FieldNew order by displayOrder";
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
