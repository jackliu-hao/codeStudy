package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class LexerATNConfig extends ATNConfig {
   private final LexerActionExecutor lexerActionExecutor;
   private final boolean passedThroughNonGreedyDecision;

   public LexerATNConfig(ATNState state, int alt, PredictionContext context) {
      super(state, alt, context, SemanticContext.NONE);
      this.passedThroughNonGreedyDecision = false;
      this.lexerActionExecutor = null;
   }

   public LexerATNConfig(ATNState state, int alt, PredictionContext context, LexerActionExecutor lexerActionExecutor) {
      super(state, alt, context, SemanticContext.NONE);
      this.lexerActionExecutor = lexerActionExecutor;
      this.passedThroughNonGreedyDecision = false;
   }

   public LexerATNConfig(LexerATNConfig c, ATNState state) {
      super(c, state, c.context, c.semanticContext);
      this.lexerActionExecutor = c.lexerActionExecutor;
      this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
   }

   public LexerATNConfig(LexerATNConfig c, ATNState state, LexerActionExecutor lexerActionExecutor) {
      super(c, state, c.context, c.semanticContext);
      this.lexerActionExecutor = lexerActionExecutor;
      this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
   }

   public LexerATNConfig(LexerATNConfig c, ATNState state, PredictionContext context) {
      super(c, state, context, c.semanticContext);
      this.lexerActionExecutor = c.lexerActionExecutor;
      this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
   }

   public final LexerActionExecutor getLexerActionExecutor() {
      return this.lexerActionExecutor;
   }

   public final boolean hasPassedThroughNonGreedyDecision() {
      return this.passedThroughNonGreedyDecision;
   }

   public int hashCode() {
      int hashCode = MurmurHash.initialize(7);
      hashCode = MurmurHash.update(hashCode, this.state.stateNumber);
      hashCode = MurmurHash.update(hashCode, this.alt);
      hashCode = MurmurHash.update(hashCode, this.context);
      hashCode = MurmurHash.update(hashCode, this.semanticContext);
      hashCode = MurmurHash.update(hashCode, this.passedThroughNonGreedyDecision ? 1 : 0);
      hashCode = MurmurHash.update(hashCode, this.lexerActionExecutor);
      hashCode = MurmurHash.finish(hashCode, 6);
      return hashCode;
   }

   public boolean equals(ATNConfig other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof LexerATNConfig)) {
         return false;
      } else {
         LexerATNConfig lexerOther = (LexerATNConfig)other;
         if (this.passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision) {
            return false;
         } else {
            return !ObjectEqualityComparator.INSTANCE.equals(this.lexerActionExecutor, lexerOther.lexerActionExecutor) ? false : super.equals(other);
         }
      }
   }

   private static boolean checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
      return source.passedThroughNonGreedyDecision || target instanceof DecisionState && ((DecisionState)target).nonGreedy;
   }
}
