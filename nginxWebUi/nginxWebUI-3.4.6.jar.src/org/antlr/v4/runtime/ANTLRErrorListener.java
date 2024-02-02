package org.antlr.v4.runtime;

import java.util.BitSet;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public interface ANTLRErrorListener {
  void syntaxError(Recognizer<?, ?> paramRecognizer, Object paramObject, int paramInt1, int paramInt2, String paramString, RecognitionException paramRecognitionException);
  
  void reportAmbiguity(Parser paramParser, DFA paramDFA, int paramInt1, int paramInt2, boolean paramBoolean, BitSet paramBitSet, ATNConfigSet paramATNConfigSet);
  
  void reportAttemptingFullContext(Parser paramParser, DFA paramDFA, int paramInt1, int paramInt2, BitSet paramBitSet, ATNConfigSet paramATNConfigSet);
  
  void reportContextSensitivity(Parser paramParser, DFA paramDFA, int paramInt1, int paramInt2, int paramInt3, ATNConfigSet paramATNConfigSet);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ANTLRErrorListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */