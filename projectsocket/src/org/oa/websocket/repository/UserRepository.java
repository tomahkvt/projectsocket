/*
 * This class for save and load to database entity User
 */
package org.oa.websocket.repository;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.oa.websocket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
	private static Logger log = Logger.getLogger(UserRepository.class);
	private SessionFactory sessionFactory;

	@Autowired
	public UserRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void create(User item) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.save(item);
		session.getTransaction().commit();
		session.close();
	}

	public void update(User item) {
		User user;
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		user = (User) session.get(User.class, item.getLogin());
		if (item.getPassword() != null) {
			user.setPassword(item.getPassword());
		}
		session.update(user);
		session.getTransaction().commit();
		session.close();

	}

	public void delete(User item) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(item);
		session.getTransaction().commit();
		log.info("delete");
		session.close();
	}

	public List<User> findAll() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.createQuery("FROM User");
		List<User> result = query.list();
		session.getTransaction().commit();
		session.close();
		return result;
	}

	public User findByLogin(String login) {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		User result = (User) session.get(User.class, login);
		session.getTransaction().commit();
		session.close();
		return result;
	}

	public boolean checkAuthentication(User user){
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		User userFromDB = (User) session.get(User.class, user.getLogin());
		if (userFromDB == null){
			return false;
		}
		session.getTransaction().commit();
		session.close();
		if (user.getPassword().equals(userFromDB.getPassword())){
			return true;	
		}else{
			return false;
		}
	}
}
