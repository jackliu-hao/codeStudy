package org.antlr.v4.runtime;

public interface Token {
  public static final int INVALID_TYPE = 0;
  
  public static final int EPSILON = -2;
  
  public static final int MIN_USER_TOKEN_TYPE = 1;
  
  public static final int EOF = -1;
  
  public static final int DEFAULT_CHANNEL = 0;
  
  public static final int HIDDEN_CHANNEL = 1;
  
  public static final int MIN_USER_CHANNEL_VALUE = 2;
  
  String getText();
  
  int getType();
  
  int getLine();
  
  int getCharPositionInLine();
  
  int getChannel();
  
  int getTokenIndex();
  
  int getStartIndex();
  
  int getStopIndex();
  
  TokenSource getTokenSource();
  
  CharStream getInputStream();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\Token.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */