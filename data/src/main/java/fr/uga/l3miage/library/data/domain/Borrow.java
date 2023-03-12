package fr.uga.l3miage.library.data.domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

@Entity
@Table(name= "Borrow")
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany
    private List<Book> books;
    @Temporal(TemporalType.DATE)
    private Date start;
    @Temporal(TemporalType.DATE)
    private Date requestedReturn;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User borrower;
    @OneToOne
    @JoinColumn(name = "librarian_id")
    private Librarian librarian;
    @Column(name="finished")
    private boolean finished;

    public Long getId() {
        return id;
    }


    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getRequestedReturn() {
        return requestedReturn;
    }

    public void setRequestedReturn(Date end) {
        this.requestedReturn = end;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Librarian getLibrarian() {
        return librarian;
    }

    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Borrow borrow = (Borrow) o;
        return finished == borrow.finished && Objects.equals(id, borrow.id) && Objects.equals(books, borrow.books) && Objects.equals(start, borrow.start) && Objects.equals(requestedReturn, borrow.requestedReturn) && Objects.equals(borrower, borrow.borrower) && Objects.equals(librarian, borrow.librarian);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, books, start, requestedReturn, borrower, librarian, finished);
    }
}
