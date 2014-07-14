package posra.dataaccess;

// Generated Jun 22, 2014 10:16:50 PM by Hibernate Tools 4.0.0

import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Example;
import org.hibernate.service.ServiceRegistry;

/**
 * Home object for domain model class Polymer.
 * 
 * @see posra.dataaccess.Polymer
 * @author Hibernate Tools
 */
public class PolymerHome {

	private static final Log log = LogFactory.getLog(PolymerHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			
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

	public void persist(Polymer transientInstance) {
		log.debug("persisting Polymer instance");
		try {
			/* Bryn added some experimental code
			 * used to be
			 * sessionFactory.getCurrentSession()
			 * .persist(transientInstance);
			 * 
			 * The current code causes an issue with
			 * trying to add things in an umgrammatical
			 * way to our database.
			 * 
			 * "You have an error in your SQL syntax;
			 * check the manual"
			 * 
			 * Issues with the line:
			 * '.Polymer (name, ExternalID) 
			 * values ('Polystyrene', null)'
			 * 
			 */
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

	public void attachDirty(Polymer instance) {
		log.debug("attaching dirty Polymer instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Polymer instance) {
		log.debug("attaching clean Polymer instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Polymer persistentInstance) {
		log.debug("deleting Polymer instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Polymer merge(Polymer detachedInstance) {
		log.debug("merging Polymer instance");
		try {
			Polymer result = (Polymer) sessionFactory.getCurrentSession()
					.merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Polymer findById(java.lang.Integer id) {
		log.debug("getting Polymer instance with id: " + id);
		try {
			Polymer instance = (Polymer) sessionFactory.getCurrentSession()
					.get("posra.dataaccess.Polymer", id);
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

	public List findByExample(Polymer instance) {
		log.debug("finding Polymer instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("posra.dataaccess.Polymer")
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
