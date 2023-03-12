package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import fr.uga.l3miage.library.data.domain.Librarian;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LibrarianRepository implements CRUDRepository<String, Librarian> {

    private final EntityManager entityManager;
    private CriteriaBuilder cb;

    @Autowired
    public LibrarianRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder();
    }

    @Override
    public Librarian save(Librarian entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Librarian get(String id) {
        return entityManager.find(Librarian.class, id);
    }

    @Override
    public void delete(Librarian entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Librarian> all() {
        return entityManager.createQuery("from Librarian", Librarian.class).getResultList();
    }

    /**
     * Récupere les bibliothéquaires ayant enregistré le plus de prêts
     * @return les bibliothéquaires les plus actif
     */
    public List<Librarian> top3WorkingLibrarians() {
        CriteriaQuery<Librarian> query = this.cb.createQuery(Librarian.class);
    
        Root<Borrow> borrowRoot = query.from(Borrow.class);
        Join<Borrow, Librarian> librarianJoin = borrowRoot.join("librarian");
    
        query.multiselect(borrowRoot.get("librarian"), cb.count(borrowRoot))
            .groupBy(borrowRoot.get("librarian"))
            .orderBy(cb.desc(cb.count(borrowRoot)));
        
        return entityManager.createQuery(query).setMaxResults(3).getResultList();
    }

}
