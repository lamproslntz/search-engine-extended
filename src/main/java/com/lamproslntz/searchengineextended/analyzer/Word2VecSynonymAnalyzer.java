package com.lamproslntz.searchengineextended.analyzer;

import com.lamproslntz.searchengineextended.filter.Word2VecSynonymFilter;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lampros Lountzis
 */
public final class Word2VecSynonymAnalyzer extends StopwordAnalyzerBase  {

    /**
     * An unmodifiable set containing some common English words that are not usually useful
     * for searching.
     */
    public static final CharArraySet ENGLISH_STOP_WORDS_SET;
    static {
        final List<String> stopWords = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by",
                "for", "if", "in", "into", "is", "it",
                "no", "not", "of", "on", "or", "such",
                "that", "the", "their", "then", "there", "these",
                "they", "this", "to", "was", "will", "with"
        );
        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }

    private final CharArraySet stemExclusionSet;

    private final Word2Vec model;
    private final double minAccuracy;

    /**
     * Builds an analyzer with the default stop words: {@link #getDefaultStopSet}, and the given Word2Vec model
     * and minimum model accuracy.
     *
     * @param model
     * @param minAccuracy
     */
    public Word2VecSynonymAnalyzer(Word2Vec model, double minAccuracy) {
        this(ENGLISH_STOP_WORDS_SET, model, minAccuracy);
    }

    /**
     * Builds an analyzer with the given stop words, Word2Vec model and minimum model accuracy.
     *
     * @param stopwords a stopword set
     * @param model
     * @param minAccuracy
     */
    public Word2VecSynonymAnalyzer(CharArraySet stopwords, Word2Vec model, double minAccuracy) {
        this(stopwords, CharArraySet.EMPTY_SET, model, minAccuracy);
    }

    /**
     * Builds an analyzer with the given stop words, Word2Vec model and minimum model accuracy.
     * If a non-empty stem exclusion set is provided this analyzer will add a {@link SetKeywordMarkerFilter} before
     * stemming.
     *
     * @param stopwords a stopword set
     * @param stemExclusionSet a set of terms not to be stemmed
     * @param model
     * @param minAccuracy
     */
    public Word2VecSynonymAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet, Word2Vec model, double minAccuracy) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
        this.model = model;
        this.minAccuracy = minAccuracy;
    }

    /**
     * Returns an unmodifiable instance of the default stop words set.
     *
     * @return default stop words set.
     */
    public static CharArraySet getDefaultStopSet(){
        return ENGLISH_STOP_WORDS_SET;
    }

    /**
     * Creates a
     * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
     * which tokenizes all the text in the provided {@link Reader}.
     *
     * @return A
     *         {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}
     *         built from an {@link StandardTokenizer} filtered with
     *         {@link EnglishPossessiveFilter},
     *         {@link LowerCaseFilter}, {@link StopFilter}
     *         , {@link SetKeywordMarkerFilter} if a stem exclusion set is
     *         provided and {@link PorterStemFilter}.
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new EnglishPossessiveFilter(source);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopwords);
        result = new Word2VecSynonymFilter(result, model, minAccuracy);
        if(!stemExclusionSet.isEmpty()) {
            result = new SetKeywordMarkerFilter(result, stemExclusionSet);
        }
        result = new PorterStemFilter(result);
        return new TokenStreamComponents(source, result);
    }

    @Override
    protected TokenStream normalize(String fieldName, TokenStream in) {
        return new LowerCaseFilter(in);
    }

}
