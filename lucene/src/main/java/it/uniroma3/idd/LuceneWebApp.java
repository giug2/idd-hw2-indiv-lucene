package it.uniroma3.idd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.lucene.analysis.Analyzer;
import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
public class LuceneWebApp {

    public static void main(String[] args) throws Exception {
        Path docsPath = Paths.get("docs");
        Path indexPath = Paths.get("index");

        // Analyzer personalizzato ottenuto dalla factory
        Analyzer analyzerCustom = AnalyzerFactory.getCustomAnalyzer();

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
