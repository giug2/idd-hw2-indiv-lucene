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
    private final long start = System.currentTimeMillis();

    // Analyzer gestito tramite factory
    private final Analyzer analyzer;

    public Indexer(Path docsPath, Path indexPath, Analyzer analyzer) {
        this.docsPath = docsPath;
        this.indexPath = indexPath;
        this.analyzer = analyzer;
    }

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
            String[] parti = nomeFile.replace(".txt", "").split("_");
            if (parti.length > 1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < parti.length; i++) {
                    if (sb.length() > 0) sb.append("_");
                    sb.append(parti[i]);
                }
                if (sb.length() > 0)
                    argomento = sb.toString().toLowerCase();
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
