package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class UserRepository implements CRUDRepository<String, User> {

    private final EntityManager entityManager;
    private CriteriaBuilder cb;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = this.entityManager.getCriteriaBuilder();
    }

    @Override
    public User save(User entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public User get(String id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public void delete(User entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<User> all() {
        return entityManager.createQuery("from User", User.class).getResultList();
    }

    /**
     * Trouve tous les utilisateurs ayant plus de l'age passé
     * @param age l'age minimum de l'utilisateur
     * @return
     */
    public List<User> findAllOlderThan(int age) {
        //get current date
        Date currentDate = new Date();
        //get the max date to be born at to be older than age
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.YEAR, -age);
        //create a new corresponding date
        Date maxDate = calendar.getTime();

        //query
        CriteriaQuery<User> query = this.cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        
        query.where(cb.lessThan(root.get("birth"), maxDate));

        return this.entityManager.createQuery(query).getResultList();
    }

}
