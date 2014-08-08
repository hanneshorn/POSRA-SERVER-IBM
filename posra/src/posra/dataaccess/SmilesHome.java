package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.service.ServiceRegistry;

/**
 * Home object for domain model class Smiles.
 * @see posra.dataaccess.Smiles
 * @author Hibernate Tools
 */
public class SmilesHome {

	private static final Log log = LogFactory.getLog(SmilesHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			/* return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");*/
			
			// return (SessionFactory) new InitialContext()
			//	.lookup("SessionFactory");
		Configuration configuration = new Configuration()
				.configure("posra/dataaccess/hibernate.cfg.xml");
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		// StandardServiceRegistryBuilder builder = new
		// StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
		SessionFactory factory = configuration
				.buildSessionFactory(serviceRegistry);
		// servletContextEvent.getServletContext().setAttribute("SessionFactory",
		// sessionFactory);

		// SessionFactory sessionFactory = new
		// Configuration().configure("/posra/dataaccess/hibernate.cfg.xml").buildSessionFactory();
		return factory; // sessionFactory; // (SessionFactory) new
						// InitialContext()
		// .lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Smiles transientInstance) {
		log.debug("persisting Smiles instance");
		try {
			/** Session sess = sessionFactory.getCurrentSession();
			sess.beginTransaction();
			sess.persist(transientInstance);
			sess.getTransaction().commit();
			log.debug("persist successful"); 
			**/
			Session sess = sessionFactory.getCurrentSession();
			sess.beginTransaction();
			sess.persist(transientInstance);
			sess.getTransaction().commit();
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Smiles instance) {
		log.debug("attaching dirty Smiles instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Smiles instance) {
		log.debug("attaching clean Smiles instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Smiles persistentInstance) {
		log.debug("deleting Smiles instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Smiles merge(Smiles detachedInstance) {
		log.debug("merging Smiles instance");
		try {
			Smiles result = (Smiles) sessionFactory.getCurrentSession().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Smiles findById(java.lang.Integer id) {
		log.debug("getting Smiles instance with id: " + id);
		try {
			Smiles instance = (Smiles) sessionFactory.getCurrentSession().get(
					"posra.dataaccess.Smiles", id);
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

	public List findByExample(Smiles instance) {
		log.debug("finding Smiles instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("posra.dataaccess.Smiles")
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
