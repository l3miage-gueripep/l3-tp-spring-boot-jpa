package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }

    //get an author with a specified id
    @GetMapping("/authors/{id}")
    public AuthorDTO author(@PathVariable Long id) throws EntityNotFoundException {
        try{
            Author author = this.authorService.get(id);
            return authorMapper.entityToDTO(author);
        }
        catch(EntityNotFoundException e){
            throw e;
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException ex) {
        return ex.getMessage();
    }

    //creating a new author
    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody AuthorDTO authorDTO) {
        //check if author name is valid
        if(authorDTO.fullName() == null || authorDTO.fullName().trim().isEmpty()){
            throw new IllegalArgumentException("Full name cannot be null.");
        }
        //create the author
        Author author = authorMapper.dtoToEntity(authorDTO);
        author = authorService.save(author);
        return authorMapper.entityToDTO(author); //doesnt return the given DTO because it has no id
    }

    //gives 400 error instead of 500 error
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(ex.getMessage());
    }

    @PutMapping("/authors/{id}")
    public AuthorDTO updateAuthor(@RequestBody AuthorDTO authorDTO, @PathVariable Long id) {
        System.out.println(id);
        // attention AuthorDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        Author author = authorMapper.dtoToEntity(authorDTO); //get the author corresponding to the dto given
        author.setFullName(null);
        author = authorService.save(author);
        return authorMapper.entityToDTO(author);
    }

    public void deleteAuthor(Long id) {
        // unimplemented... yet!
    }

    public Collection<BookDTO> books(Long authorId) {
        return Collections.emptyList();
    }

}
