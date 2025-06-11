package fr.eletutour.asgard.baldr.service;

import fr.eletutour.asgard.baldr.dao.AuthorRepository;
import fr.eletutour.asgard.baldr.exception.AuthorNotFoundException;
import fr.eletutour.asgard.baldr.model.Author;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service gérant les opérations liées aux auteurs.
 */
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    /**
     * Constructeur du service AuthorService.
     *
     * @param authorRepository Le repository pour accéder aux données des auteurs.
     */
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Récupère la liste de tous les auteurs.
     *
     * @return La liste de tous les auteurs.
     */
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    /**
     * Récupère un auteur par son identifiant.
     *
     * @param id L'identifiant de l'auteur.
     * @return L'auteur correspondant à l'identifiant.
     * @throws AuthorNotFoundException Si aucun auteur n'est trouvé avec l'identifiant spécifié.
     */
    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) throws AuthorNotFoundException {
        return authorRepository.findById(id).orElseThrow( () -> new AuthorNotFoundException("Author non trouvé pour l'id : " + id, id));
    }

    /**
     * Crée un nouvel auteur.
     *
     * @param name Le nom de l'auteur.
     * @param bio La biographie de l'auteur.
     * @return L'auteur créé.
     */
    @Transactional
    public Author createAuthor(String name, String bio) {
        // Clone un auteur avec les nouvelles valeurs
        Author author = new Author();
        author.setName(name);
        author.setBio(bio);
        return authorRepository.save(author);
    }

    /**
     * Initialise la base de données avec des auteurs par défaut.
     * Cette méthode est exécutée après la construction de l'objet.
     */
    @PostConstruct
    private void initAuthors(){
        createAuthor("J.K. Rowling", "J.K. Rowling is the author of the much-loved series of seven Harry Potter novels.");
        createAuthor("Stephen King", "Stephen King is the author of more than sixty books, all of them worldwide bestsellers.");
        createAuthor("Agatha Christie", "Agatha Christie is known throughout the world as the Queen of Crime.");
    }

    @Transactional
    public Author updateAuthor(Long id, String name, String bio) throws AuthorNotFoundException {
        return authorRepository.findById(id)
            .orElseThrow(() -> new AuthorNotFoundException("Auteur non trouvé"));
    }

    @Transactional
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

}