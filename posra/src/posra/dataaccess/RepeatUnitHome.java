package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class RepeatUnit.
 * @see posra.dataaccess.RepeatUnit
 * @author Hibernate Tools
 */
public class RepeatUnitHome {

	private static final Log log = LogFactory.getLog(RepeatUnitHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(RepeatUnit transientInstance) {
		log.debug("persisting RepeatUnit instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(RepeatUnit instance) {
		log.debug("attaching dirty RepeatUnit instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(RepeatUnit instance) {
		log.debug("attaching clean RepeatUnit instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(RepeatUnit persistentInstance) {
		log.debug("deleting RepeatUnit instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public RepeatUnit merge(RepeatUnit detachedInstance) {
		log.debug("merging RepeatUnit instance");
		try {
			RepeatUnit result = (RepeatUnit) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public RepeatUnit findById(java.lang.Integer id) {
		log.debug("getting RepeatUnit instance with id: " + id);
		try {
			RepeatUnit instance = (RepeatUnit) sessionFactory
					.getCurrentSession().get("posra.dataaccess.RepeatUnit", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(RepeatUnit instance) {
		log.debug("finding RepeatUnit instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("posra.dataaccess.RepeatUnit")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
