package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.service.ServiceRegistry;

/**
 * Home object for domain model class SegmentAssociation.
 * 
 * @see posra.dataaccess.SegmentAssociation
 * @author Hibernate Tools
 */
public class SegmentAssociationHome {

	private static final Log log = LogFactory
			.getLog(SegmentAssociationHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			Configuration configuration = new Configuration()
					.configure("posra/dataaccess/hibernate.cfg.xml");
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties()).build();
			SessionFactory factory = configuration
					.buildSessionFactory(serviceRegistry);
			return factory;

		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(SegmentAssociation transientInstance) {
		log.debug("persisting SegmentAssociation instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(SegmentAssociation instance) {
		log.debug("attaching dirty SegmentAssociation instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(SegmentAssociation instance) {
		log.debug("attaching clean SegmentAssociation instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(SegmentAssociation persistentInstance) {
		log.debug("deleting SegmentAssociation instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SegmentAssociation merge(SegmentAssociation detachedInstance) {
		log.debug("merging SegmentAssociation instance");
		try {
			SegmentAssociation result = (SegmentAssociation) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SegmentAssociation findById(posra.dataaccess.SegmentAssociationId id) {
		log.debug("getting SegmentAssociation instance with id: " + id);
		try {
			SegmentAssociation instance = (SegmentAssociation) sessionFactory
					.getCurrentSession().get(
							"posra.dataaccess.SegmentAssociation", id);
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

	public List findByExample(SegmentAssociation instance) {
		log.debug("finding SegmentAssociation instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("posra.dataaccess.SegmentAssociation")
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
