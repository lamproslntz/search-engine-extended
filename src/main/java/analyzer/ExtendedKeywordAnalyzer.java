package analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;

/**
 * Extends Apache Lucene KeywordAnalyzer.
 * Tokenizes the entire stream as a single token, and transforms it to lowercase.
 * This is useful for data like zip codes, ids, and some product names.
 *
 * @author Lampros Lountzis
 */
public final class ExtendedKeywordAnalyzer extends Analyzer {

    public ExtendedKeywordAnalyzer() {
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName) {
        final Tokenizer source = new KeywordTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        return new TokenStreamComponents(source, result);
    }
}
