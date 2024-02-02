package org.antlr.v4.runtime;

public interface ANTLRErrorStrategy {
   void reset(Parser var1);

   Token recoverInline(Parser var1) throws RecognitionException;

   void recover(Parser var1, RecognitionException var2) throws RecognitionException;

   void sync(Parser var1) throws RecognitionException;

   boolean inErrorRecoveryMode(Parser var1);

   void reportMatch(Parser var1);

   void reportError(Parser var1, RecognitionException var2);
}
