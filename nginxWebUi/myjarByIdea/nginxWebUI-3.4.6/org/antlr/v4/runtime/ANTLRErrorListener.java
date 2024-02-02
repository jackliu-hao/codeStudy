package org.antlr.v4.runtime;

import java.util.BitSet;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public interface ANTLRErrorListener {
   void syntaxError(Recognizer<?, ?> var1, Object var2, int var3, int var4, String var5, RecognitionException var6);

   void reportAmbiguity(Parser var1, DFA var2, int var3, int var4, boolean var5, BitSet var6, ATNConfigSet var7);

   void reportAttemptingFullContext(Parser var1, DFA var2, int var3, int var4, BitSet var5, ATNConfigSet var6);

   void reportContextSensitivity(Parser var1, DFA var2, int var3, int var4, int var5, ATNConfigSet var6);
}
