package it.uniroma3.idd;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Searcher {
    private final Path indexPath;
    private final Analyzer analyzer; // Analyzer condiviso con Indexer e SearchController

    public Searcher(Path indexPath) throws Exception {
        this.indexPath = indexPath;
        this.analyzer = AnalyzerFactory.getCustomAnalyzer(); 
    }

    public List<SearchResult> search(String field, String queryText, String expectedTopic) throws Exception {
        List<SearchResult> resultsList = new ArrayList<>();

        int relevantDocumentsFound = 0;
        long totalDocumentsReturned = 0;
        long totalRelevantDocumentsInCollection = 0;

        try (DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexPath))) {
            IndexSearcher searcher = new IndexSearcher(reader);

            // Conta quanti documenti nell'indice hanno l'argomento atteso
            Query relevanceQuery = new TermQuery(new Term("argomento", expectedTopic));
            totalRelevantDocumentsInCollection = searcher.count(relevanceQuery);

            Query query;
            QueryParser parser;
            // Usa lo stesso analyzer della fase di indicizzazione
            if(field.equals("nome")) {
                query = new RegexpQuery(new Term("nome", ".*" + queryText.toLowerCase() + ".*"));
            } else {
                parser = new QueryParser(field, analyzer);
                query = parser.parse(queryText);
            }

            // Esegui la ricerca (top 10 risultati)
            TopDocs results = searcher.search(query, 10);
            ScoreDoc[] hits = results.scoreDocs;
            totalDocumentsReturned = hits.length;

            for (ScoreDoc hit : hits) {
                Document doc = searcher.storedFields().document(hit.doc);
                resultsList.add(new SearchResult(doc.get("nome"), hit.score));

                String docTopic = doc.get("argomento");
                boolean isRelevant = (docTopic != null && docTopic.equalsIgnoreCase(expectedTopic));

                if (isRelevant) relevantDocumentsFound++;
            }

            double precision = (totalDocumentsReturned == 0)
                    ? 0
                    : (double) relevantDocumentsFound / totalDocumentsReturned;
            double recall = (totalRelevantDocumentsInCollection == 0)
                    ? 0
                    : (double) relevantDocumentsFound / totalRelevantDocumentsInCollection;

            System.out.println("\n--- METRICHE DELLA QUERY ---");
            System.out.println("Query: '" + queryText + "' su campo '" + field + "'");
            System.out.println("Argomento Atteso (Ground Truth): " + expectedTopic);
            System.out.println("------------------------------");
            System.out.println("Documenti Rilevanti TOTALE (nell'indice): " + totalRelevantDocumentsInCollection);
            System.out.println("Documenti restituiti (k=10): " + totalDocumentsReturned);
            System.out.println("Documenti Rilevanti Trovati (tra i k): " + relevantDocumentsFound);
            System.out.println("Precision@10: " + String.format("%.4f", precision));
            System.out.println("Recall@10: " + String.format("%.4f", recall));
            System.out.println("------------------------------\n");
        }

        return resultsList;
    }
}
