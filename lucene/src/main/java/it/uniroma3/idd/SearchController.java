package it.uniroma3.idd;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class SearchController {

    private final Path indexPath = Paths.get("index");
    private final Searcher searcher = new Searcher(indexPath);

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
            
            Analyzer analyzer;
            String luceneField;

            switch (field) {
                case "nome" -> {
                    luceneField = "nome";
                    analyzer = new SimpleAnalyzer();
                }
                case "contenuto" -> {
                    luceneField = "contenuto";
                    analyzer = new StandardAnalyzer();
                }
                default -> {
                    model.addAttribute("error", "Campo non valido: usa 'nome' o 'contenuto'");
                    return "index";
                }
            }

            String searchText = queryText;

            String expectedTopic = queryText.replace("\"", "").replace(" ", "_").toLowerCase();
           
            List<SearchResult> results = searcher.search(luceneField, searchText, analyzer, expectedTopic);
            
            model.addAttribute("results", results);
            model.addAttribute("query", query); 
            model.addAttribute("expectedTopic", expectedTopic); 

        } catch (Exception e) {
            model.addAttribute("error", "Errore: " + e.getMessage());
            e.printStackTrace(); 
        }
        
        return "index";
    }
}