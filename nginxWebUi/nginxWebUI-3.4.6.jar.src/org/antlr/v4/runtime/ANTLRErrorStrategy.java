package org.antlr.v4.runtime;

public interface ANTLRErrorStrategy {
  void reset(Parser paramParser);
  
  Token recoverInline(Parser paramParser) throws RecognitionException;
  
  void recover(Parser paramParser, RecognitionException paramRecognitionException) throws RecognitionException;
  
  void sync(Parser paramParser) throws RecognitionException;
  
  boolean inErrorRecoveryMode(Parser paramParser);
  
  void reportMatch(Parser paramParser);
  
  void reportError(Parser paramParser, RecognitionException paramRecognitionException);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ANTLRErrorStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */