package org.antlr.v4.runtime;

import java.util.Collection;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNType;
import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;

public class LexerInterpreter extends Lexer {
   protected final String grammarFileName;
   protected final ATN atn;
   /** @deprecated */
   @Deprecated
   protected final String[] tokenNames;
   protected final String[] ruleNames;
   protected final String[] modeNames;
   private final Vocabulary vocabulary;
   protected final DFA[] _decisionToDFA;
   protected final PredictionContextCache _sharedContextCache;

   /** @deprecated */
   @Deprecated
   public LexerInterpreter(String grammarFileName, Collection<String> tokenNames, Collection<String> ruleNames, Collection<String> modeNames, ATN atn, CharStream input) {
      this(grammarFileName, VocabularyImpl.fromTokenNames((String[])tokenNames.toArray(new String[tokenNames.size()])), ruleNames, modeNames, atn, input);
   }

   public LexerInterpreter(String grammarFileName, Vocabulary vocabulary, Collection<String> ruleNames, Collection<String> modeNames, ATN atn, CharStream input) {
      super(input);
      this._sharedContextCache = new PredictionContextCache();
      if (atn.grammarType != ATNType.LEXER) {
         throw new IllegalArgumentException("The ATN must be a lexer ATN.");
      } else {
         this.grammarFileName = grammarFileName;
         this.atn = atn;
         this.tokenNames = new String[atn.maxTokenType];

         int i;
         for(i = 0; i < this.tokenNames.length; ++i) {
            this.tokenNames[i] = vocabulary.getDisplayName(i);
         }

         this.ruleNames = (String[])ruleNames.toArray(new String[ruleNames.size()]);
         this.modeNames = (String[])modeNames.toArray(new String[modeNames.size()]);
         this.vocabulary = vocabulary;
         this._decisionToDFA = new DFA[atn.getNumberOfDecisions()];

         for(i = 0; i < this._decisionToDFA.length; ++i) {
            this._decisionToDFA[i] = new DFA(atn.getDecisionState(i), i);
         }

         this._interp = new LexerATNSimulator(this, atn, this._decisionToDFA, this._sharedContextCache);
      }
   }

   public ATN getATN() {
      return this.atn;
   }

   public String getGrammarFileName() {
      return this.grammarFileName;
   }

   /** @deprecated */
   @Deprecated
   public String[] getTokenNames() {
      return this.tokenNames;
   }

   public String[] getRuleNames() {
      return this.ruleNames;
   }

   public String[] getModeNames() {
      return this.modeNames;
   }

   public Vocabulary getVocabulary() {
      return this.vocabulary != null ? this.vocabulary : super.getVocabulary();
   }
}
