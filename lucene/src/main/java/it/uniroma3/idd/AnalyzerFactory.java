package it.uniroma3.idd;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.it.ItalianAnalyzer;


public class AnalyzerFactory {
    public static Analyzer getCustomAnalyzer() throws IOException {
        return new ItalianAnalyzer();
    }
}

    /*
        CustomAnalyzer.builder()
                    .withTokenizer("standard")
                    .addTokenFilter("lowercase")
                    .addTokenFilter("stop", "words", "stopword.txt")
                    .addTokenFilter("italianlightstem")
                    .build();

        CustomAnalyzer.builder()
                    .withTokenizer(WhitespaceTokenizerFactory.class)
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                    .build();

        new ItalianAnalyzer();
    */
   