package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

public class LexerDFASerializer extends DFASerializer {
   public LexerDFASerializer(DFA dfa) {
      super(dfa, (Vocabulary)VocabularyImpl.EMPTY_VOCABULARY);
   }

   protected String getEdgeLabel(int i) {
      return "'" + (char)i + "'";
   }
}
