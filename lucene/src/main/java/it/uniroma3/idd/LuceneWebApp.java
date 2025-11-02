package it.uniroma3.idd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class LuceneWebApp {

    public static void main(String[] args) throws Exception {
        Path docsPath = Paths.get("docs");
        Path indexPath = Paths.get("index");

        // Analyzer personalizzato per gestire tokenizzazione e minuscole
        Analyzer analyzerCustom = CustomAnalyzer.builder()
                    .withTokenizer(WhitespaceTokenizerFactory.class)
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                    .build();

        // Indicizza all'avvio
        System.err.println("----- AVVIO INDICIZZAZIONE -----");
        Indexer indexer = new Indexer(docsPath, indexPath, analyzerCustom);
        indexer.createIndex();
        System.err.println("--------------------------------");

        System.err.println("------- AVVIO STATISTICHE ------");
        Stats statistiche = new Stats();
        statistiche.statsIndex(indexPath);
        System.err.println("--------------------------------");
        
        SpringApplication.run(LuceneWebApp.class, args);
    }
}
