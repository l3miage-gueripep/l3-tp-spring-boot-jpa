package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Borrow;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BorrowRepository implements CRUDRepository<String, Borrow> {

    private final EntityManager entityManager;
    private CriteriaBuilder cb;

    @Autowired
    public BorrowRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = this.entityManager.getCriteriaBuilder();

    }

    @Override
    public Borrow save(Borrow entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Borrow get(String id) {
        return entityManager.find(Borrow.class, id);
    }

    @Override
    public void delete(Borrow entity) {
        entityManager.remove(entity);
    }

    @Override
    public List<Borrow> all() {
        return entityManager.createQuery("from Borrow", Borrow.class).getResultList();
    }

    /**
     * Trouver des emprunts en cours pour un emprunteur donné
     *
     * @param userId l'id de l'emprunteur
     * @return la liste des emprunts en cours
     */
    public List<Borrow> findInProgressByUser(Long userId) {
        CriteriaQuery<Borrow> query = this.cb.createQuery(Borrow.class);
        Root<Borrow> root = query.from(Borrow.class);
    
        Predicate borrowerPredicate = this.cb.equal(root.get("borrower"), userId);
        Predicate inProgressPredicate = this.cb.equal(root.get("finished"), false);
    
        query.where(this.cb.and(borrowerPredicate, inProgressPredicate));
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Compte le nombre total de livres emprunté par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countBorrowedBooksByUser(String userId) {
        // TODO
        return 0;
    }

    /**
     * Compte le nombre total de livres non rendu par un utilisateur.
     *
     * @param userId l'id de l'emprunteur
     * @return le nombre de livre
     */
    public int countCurrentBorrowedBooksByUser(String userId) {
        // TODO
        return 0;
    }

    /**
     * Recherche tous les emprunt en retard trié
     *
     * @return la liste des emprunt en retard
     */
    public List<Borrow> foundAllLateBorrow() {
        // TODO
        return null;
    }

    /**
     * Calcul les emprunts qui seront en retard entre maintenant et x jours.
     *
     * @param days le nombre de jour avant que l'emprunt soit en retard
     * @return les emprunt qui sont bientôt en retard
     */
    public List<Borrow> findAllBorrowThatWillLateWithin(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = now.plusDays(days);
    
        CriteriaQuery<Borrow> query = this.cb.createQuery(Borrow.class);
        Root<Borrow> root = query.from(Borrow.class);
    
        Predicate notReturnedPredicate = this.cb.equal(root.get("finished"), false);
        Predicate dueDatePredicate = this.cb.between(root.get("requestedReturn"), now, dueDate);
    
        query.where(this.cb.and(notReturnedPredicate, dueDatePredicate));
    
        return this.entityManager.createQuery(query).getResultList();
    }

}
