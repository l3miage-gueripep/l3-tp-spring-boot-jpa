package fr.uga.l3miage.library.data.repo;

import fr.uga.l3miage.library.data.domain.Author;
import fr.uga.l3miage.library.data.domain.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AuthorRepository implements CRUDRepository<Long, Author> {

    private final EntityManager entityManager;
    CriteriaBuilder cb;

    @Autowired
    public AuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.cb = this.entityManager.getCriteriaBuilder();
    }

    @Override
    public Author save(Author author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Author get(Long id) {
        return entityManager.find(Author.class, id);
    }


    @Override
    public void delete(Author author) {
        entityManager.remove(author);
    }

    /**
     * Renvoie tous les auteurs
     *
     * @return une liste d'auteurs trié par nom
     */
    @Override
    public List<Author> all() {
        CriteriaQuery<Author> query = this.cb.createQuery(Author.class);
        Root<Author> root = query.from(Author.class);
        
        //predicate
        query.orderBy(cb.asc(root.get("fullName")));

        //return
        return entityManager.createQuery(query).getResultList();
    }

    /**
     * Recherche un auteur par nom (ou partie du nom) de façon insensible  à la casse.
     *
     * @param namePart tout ou partie du nomde l'auteur
     * @return une liste d'auteurs trié par nom
     */
    public List<Author> searchByName(String namePart) {
        CriteriaQuery<Author> query = this.cb.createQuery(Author.class);
        Root<Author> root = query.from(Author.class);

        //predicate
        Predicate predicate = cb.like(cb.lower(root.get("fullName")), "%" + namePart.toLowerCase() + "%");
        query.where(predicate);

        //order
        query.orderBy(cb.asc(root.get("fullName")));

        //return
        return this.entityManager.createQuery(query).getResultList();
    }

    /**
     * Recherche si l'auteur a au moins un livre co-écrit avec un autre auteur
     *
     * @return true si l'auteur partage
     */
    public boolean checkAuthorByIdHavingCoAuthoredBooks(long authorId) {
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> root = query.from(Book.class);

        //join
        Join<Author, Book> authorJoin = root.join("authors");

        //get author entity to easily select id having same name as book id
        // EntityType<Author> author = entityManager.getMetamodel().entity(Author.class);
        // EntityType<Book> book = entityManager.getMetamodel().entity(Book.class);

        //predicate
        // query.where(cb.equal(root.get("id"), authorId));
        // query.groupBy(join.get(book.getSingularAttribute("id")));
        // query.having(cb.gt(cb.count(join), 1));

        //selectionne les livres où l'auteur a fait le livre
        query.where(cb.equal(authorJoin.get("id"), authorId));
        //groupe par livre
        query.groupBy(root.get("id"));
        
        query.having(cb.gt(cb.count(authorJoin), 1));



        //get result
        List<Book> authorBooks =  this.entityManager.createQuery(query).getResultList();

        // //debug
        // System.out.println("Author " + authorId + ", " + authorBooks.size() + " livre(s) :");

        // for(Book book : authorBooks){
        //     System.out.println(book.getAuthors().size());
        // }
        
        //return books
        System.out.println("Taille : " + authorBooks.size());
        return !authorBooks.isEmpty();
    }

}
