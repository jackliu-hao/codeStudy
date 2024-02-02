package org.antlr.v4.runtime;

public interface IntStream {
  public static final int EOF = -1;
  
  public static final String UNKNOWN_SOURCE_NAME = "<unknown>";
  
  void consume();
  
  int LA(int paramInt);
  
  int mark();
  
  void release(int paramInt);
  
  int index();
  
  void seek(int paramInt);
  
  int size();
  
  String getSourceName();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\IntStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */