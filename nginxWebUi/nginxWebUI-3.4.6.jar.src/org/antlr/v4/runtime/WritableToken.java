package org.antlr.v4.runtime;

public interface WritableToken extends Token {
  void setText(String paramString);
  
  void setType(int paramInt);
  
  void setLine(int paramInt);
  
  void setCharPositionInLine(int paramInt);
  
  void setChannel(int paramInt);
  
  void setTokenIndex(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\WritableToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */