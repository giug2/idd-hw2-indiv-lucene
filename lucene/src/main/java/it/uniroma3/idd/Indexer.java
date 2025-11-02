package it.uniroma3.idd;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class Indexer {

    private final Path docsPath;
    private final Path indexPath;
    private final Analyzer analyzer;
    private final long start = System.currentTimeMillis();

    public Indexer(Path docsPath, Path indexPath, Analyzer analyzer) {
        this.docsPath = docsPath;
        this.indexPath = indexPath;
        this.analyzer = analyzer;
    }

    private static final java.util.Set<String> STOP_WORDS = java.util.Set.of(
            "il", "lo", "la", "i", "gli", "le", "un", "uno", "una",
            "di", "a", "da", "in", "con", "su", "per", "tra", "fra", "e", "o",
            "del", "dello", "della", "dei", "degli", "delle",
            "al", "allo", "alla", "ai", "agli", "alle",
            "dal", "dallo", "dalla", "dai", "dagli", "dalle",
            "nel", "nello", "nella", "nei", "negli", "nelle"
    );

    public void createIndex() throws IOException {
        Directory dir = FSDirectory.open(indexPath);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try (IndexWriter writer = new IndexWriter(dir, iwc)) {
            indexDocs(writer, docsPath);
        }

        System.out.println("Indicizzazione completata.");
    }

    private void indexDocs(IndexWriter writer, Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            stream.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".txt"))
                    .forEach(file -> {
                        try {
                            indexDoc(writer, file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
        long end = System.currentTimeMillis();
        long elapsed = end - start;

        System.out.println("Tempo impiegato: " + elapsed + " ms");
    }

    private void indexDoc(IndexWriter writer, Path file) throws IOException {
        String content = Files.readString(file);
        String nomeFile = file.getFileName().toString(); 

        String argomento = "N/D";
        try {
            // Rimuovi ".txt", poi dividi per "_"
            String[] parti = nomeFile.replace(".txt", "").split("_");
            
            if (parti.length > 1) {
                StringBuilder sb = new StringBuilder();
                
                for (int i = 1; i < parti.length; i++) {
                    String parte = parti[i];

                    // Controlla se la parte è puramente numerica (es. "4741")
                    if (parte.matches("\\d+")) {
                        break; 
                    }

                    if (sb.length() > 0) {
                        sb.append("_");
                    }
                    sb.append(parte);
                }

                if (sb.length() > 0) {
                    argomento = sb.toString().toLowerCase(); 
                }
            }
        } catch (Exception e) {
            System.err.println("Impossibile estrarre argomento da: " + nomeFile);
        }


        Document doc = new Document();
        
        doc.add(new TextField("nome", nomeFile, Field.Store.YES));
        doc.add(new TextField("contenuto", content, Field.Store.YES));
        doc.add(new StringField("argomento", argomento, Field.Store.YES));

        writer.addDocument(doc);

        System.out.println("Indicizzato: " + nomeFile + " (Argomento: " + argomento + ")");
    }
}