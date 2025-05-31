package fr.eletutour.asgard.mimir.service;

import fr.eletutour.asgard.mimir.model.Documentation;
import fr.eletutour.asgard.mimir.repository.DocumentationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
    private final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final DocumentationRepository documentationRepository;

    public SearchService(DocumentationRepository documentationRepository) {
        this.documentationRepository = documentationRepository;
    }

    public Documentation saveDocumentation(Documentation documentation) {
        logger.info("Saving documentation: {}", documentation.getTitle());
        return documentationRepository.save(documentation);
    }

    public Documentation findDocumentationById(String id) {
        return documentationRepository.findById(id).orElse(null);
    }

    public void deleteDocumentation(String id) {
        documentationRepository.deleteById(id);
    }

    public Documentation findDocumentationByTitle(String title) {
        return documentationRepository.findByTitle(title);
    }
}
