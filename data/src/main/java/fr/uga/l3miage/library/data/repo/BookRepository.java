package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import fr.uga.l3miage.library.data.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepository implements CRUDRepository<Long, Book> {

    private final EntityManager entityManager;
    private final CriteriaBuilder cb;

    @Autowired
    public BookRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = entityManager.getCriteriaBuilder(); //create a criteria builder
    }

    @Override
    public Book save(Book author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Book get(Long id) {
        return entityManager.find(Book.class, id);
    }


    @Override
    public void delete(Book author) {
        entityManager.remove(author);
    }

    /**
     * Renvoie tous les auteurs par ordre alphabétique
     * @return une liste de livres
     */
    public List<Book> all() {
        CriteriaQuery<Book> query = this.cb.createQuery(Book.class); //create a query using the criteria builder
        Root<Book> root = query.from(Book.class); //create a new root instance representing books

        //predicate
        query.orderBy(cb.asc(root.get("title"))); //sort the results by title

        //return
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Trouve les livres dont le titre contient la chaine passée (non sensible à la casse)
     * @param titlePart tout ou partie du titre
     * @return une liste de livres
     */
    public List<Book> findByContainingTitle(String titlePart) {
        CriteriaQuery<Book> query = this.cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);

        //predicate
        query.where(cb.like(cb.lower(root.get("title")), "%" + titlePart.toLowerCase() + "%")); //we use cb.lower() to make it case insensitive
        
        //return result
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Trouve les livres d'un auteur donnée dont le titre contient la chaine passée (non sensible à la casse)
     * @param authorId id de l'auteur
     * @param titlePart tout ou partie d'un titre de livré
     * @return une liste de livres
     */
    public List<Book> findByAuthorIdAndContainingTitle(Long authorId, String titlePart) {
        CriteriaQuery<Book> query = this.cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);

        //Join the 2 tables needed
        Join<Book, Author> authorJoin = root.join("authors");

        //creating predicates
        Predicate containsTitlePredicate = cb.like(cb.lower(root.get("title")), "%" + titlePart.toLowerCase() + "%");
        Predicate authorIdPredicate = cb.equal(authorJoin.get("id"), authorId);

        //combine the predicates
        query.where(cb.and(authorIdPredicate, containsTitlePredicate));

        //return result
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Recherche des livres dont le nom de l'auteur contient la chaine passée (non sensible à la casse)
     * @param namePart tout ou partie du nom
     * @return une liste de livres
     */
    public List<Book> findBooksByAuthorContainingName(String namePart) {
        CriteriaQuery<Book> query = this.cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);

        //join
        Join<Book, Author> authorJoin = root.join("authors");

        //predicate
        query.where(cb.like(cb.lower(authorJoin.get("fullName")), "%" + namePart.toLowerCase() + "%"));
        query.select(root);

        //return
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Trouve des livres avec un nombre d'auteurs supérieur au compte donné
     * @param count le compte minimum d'auteurs
     * @return une liste de livres
     */
    public List<Book> findBooksHavingAuthorCountGreaterThan(int count) {
        CriteriaQuery<Book> query = this.cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);

        //join
        Join<Author, Book> authorJoin = root.join("authors");

        //predicate
        query.groupBy(root).having(cb.gt(cb.count(authorJoin), count));

        return entityManager.createQuery(query).getResultList();
    }

}
