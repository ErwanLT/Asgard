package fr.eletutour.asgard.baldr.dao;

import fr.eletutour.asgard.baldr.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
}
