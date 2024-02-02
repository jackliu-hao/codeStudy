package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

public interface TokenStream extends IntStream {
  Token LT(int paramInt);
  
  Token get(int paramInt);
  
  TokenSource getTokenSource();
  
  String getText(Interval paramInterval);
  
  String getText();
  
  String getText(RuleContext paramRuleContext);
  
  String getText(Token paramToken1, Token paramToken2);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\TokenStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */