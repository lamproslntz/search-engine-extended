package com.lamproslntz.searchengineextended.analyzers;

import com.lamproslntz.searchengineextended.filters.Word2VecSynonymFilter;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
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

    // Word2Vec model
    public Word2Vec vec = WordVectorSerializer.readWord2VecModel(".\\src\\main\\resources\\fasttext-en\\wiki-news-300d-1M.vec");

    public double minAccuracy = 0.98;

    /**
     * Builds an analyzer with the default stop words: {@link #getDefaultStopSet}.
     */
    public Word2VecSynonymAnalyzer() {
        this(ENGLISH_STOP_WORDS_SET);
    }

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopwords a stopword set
     */
    public Word2VecSynonymAnalyzer(CharArraySet stopwords) {
        this(stopwords, CharArraySet.EMPTY_SET);
    }

    /**
     * Builds an analyzer with the given stop words. If a non-empty stem exclusion set is
     * provided this analyzer will add a {@link SetKeywordMarkerFilter} before
     * stemming.
     *
     * @param stopwords a stopword set
     * @param stemExclusionSet a set of terms not to be stemmed
     */
    public Word2VecSynonymAnalyzer(CharArraySet stopwords, CharArraySet stemExclusionSet) {
        super(stopwords);
        this.stemExclusionSet = CharArraySet.unmodifiableSet(CharArraySet.copy(stemExclusionSet));
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
        result = new Word2VecSynonymFilter(result, vec, minAccuracy);
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
