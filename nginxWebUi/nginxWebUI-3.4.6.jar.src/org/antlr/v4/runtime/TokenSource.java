package org.antlr.v4.runtime;

public interface TokenSource {
  Token nextToken();
  
  int getLine();
  
  int getCharPositionInLine();
  
  CharStream getInputStream();
  
  String getSourceName();
  
  void setTokenFactory(TokenFactory<?> paramTokenFactory);
  
  TokenFactory<?> getTokenFactory();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\TokenSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */