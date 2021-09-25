package com.lamproslntz.searchengineextended.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Lampros Lountzis
 */
public class AnalyzerUtils {

    public static void displayTokens(Analyzer analyzer, String text) {
        TokenStream stream = analyzer.tokenStream("field", new StringReader(text));

        // get the CharTermAttribute from the TokenStream
        CharTermAttribute term = stream.addAttribute(CharTermAttribute .class);

        try {
            stream.reset();

            // print all tokens until stream is exhausted
            while (stream.incrementToken()) {
                System.out.println("[" + term.toString() + "] ");
            }

            stream.end();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
