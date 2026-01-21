package it.uniroma3.idd;

import org.apache.lucene.analysis.Analyzer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
public class SearchController {
    private final Path indexPath = Paths.get("index");
    private final Searcher searcher;

    public SearchController() {
        try {
            this.searcher = new Searcher(indexPath);
        } catch (Exception e) {
            throw new RuntimeException("Errore nell'inizializzazione del Searcher", e);
        }
    }


    @GetMapping("/")
    public String home() {
        return "index";
    }


    @PostMapping("/")
    public String search(@RequestParam("query") String query, Model model) {
        if (query == null || query.trim().isEmpty()) {
            model.addAttribute("error", "Inserisci una query valida.");
            return "index";
        }

        try {
            String[] parts = query.trim().split("\\s+", 2);
            if (parts.length < 2) {
                model.addAttribute("error", "Sintassi: <campo> <termine_query>");
                model.addAttribute("info", "Esempio: contenuto \"energia solare\"");
                return "index";
            }

            String field = parts[0].toLowerCase();
            String queryText = parts[1];

            String luceneField;
            switch (field) {
                case "nome" -> luceneField = "nome";
                case "contenuto" -> luceneField = "contenuto";
                default -> {
                    model.addAttribute("error", "Campo non valido: usa 'nome' o 'contenuto'");
                    return "index";
                }
            }

            String searchText = queryText;

            // Conversione coerente con l'argomento estratto in Indexer
            String expectedTopic = queryText.replace("\"", "").replace(" ", "_").toLowerCase();

            List<SearchResult> results = searcher.search(luceneField, searchText, expectedTopic);

            model.addAttribute("results", results);
            model.addAttribute("query", query);
            model.addAttribute("expectedTopic", expectedTopic);

        } catch (Exception e) {
            model.addAttribute("error", "Errore: " + e.getMessage());
            e.printStackTrace();
        }

        return "index";
    }
    

    @GetMapping("/file/{fileName:.+}")
    @ResponseBody
    public org.springframework.core.io.Resource serveFile(@PathVariable String fileName) {
        try {
            Path file = Paths.get("docs").resolve(fileName).normalize();
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(file.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File non trovato: " + fileName);
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Errore nel recupero del file: " + fileName, e);
        }
    }
}
