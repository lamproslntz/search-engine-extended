package com.lamproslntz.searchengineextended.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.CharsRef;
import org.apache.lucene.util.CharsRefBuilder;
import org.deeplearning4j.models.word2vec.Word2Vec;

/**
 * Word2Vec based synonym filter.
 *
 * @author Lampros Lountzis
 */
public final class Word2VecSynonymFilter extends TokenFilter {

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
  private final PositionIncrementAttribute positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);

  private final Word2Vec word2Vec;
  private final double minAccuracy;
  private final List<PendingOutput> outputs = new LinkedList<>();
  private int positions = 0;

  public Word2VecSynonymFilter(TokenStream input, Word2Vec word2Vec, double minAccuracy) {
    super(input);
    this.word2Vec = word2Vec;
    this.minAccuracy = minAccuracy;
  }

  @Override
  public boolean incrementToken() throws IOException {
    if (!outputs.isEmpty()) {
      PendingOutput output = outputs.remove(0);

      restoreState(output.state);

      termAtt.copyBuffer(output.charsRef.chars, output.charsRef.offset, output.charsRef.length);

      typeAtt.setType(SynonymGraphFilter.TYPE_SYNONYM);
      positionIncrementAttribute.setPositionIncrement(output.posIncr);

      return true;
    }

    if (!SynonymGraphFilter.TYPE_SYNONYM.equals(typeAtt.type())) {
      positions++;
      positionIncrementAttribute.setPositionIncrement(positions);
      String word = new String(termAtt.buffer()).trim();
      Collection<String> list = word2Vec.similarWordsInVocabTo(word, minAccuracy);

      for (String syn : list) {
        if (!syn.equals(word)) {
          CharsRefBuilder charsRefBuilder = new CharsRefBuilder();
          CharsRef cr = charsRefBuilder.append(syn).get();

          State state = captureState();
          outputs.add(new PendingOutput(state, cr, positions));
        }
      }
    }

    return !outputs.isEmpty() || input.incrementToken();
  }

  @Override
  public void end() throws IOException {
    super.end();
    outputs.clear();
    positions = 0;
  }

  private class PendingOutput {

    private final State state;
    private final CharsRef charsRef;
    private final int posIncr;

    private PendingOutput(State state, CharsRef charsRef, int posIncr) {
      this.state = state;
      this.charsRef = charsRef;
      this.posIncr = posIncr;
    }

  }

}
