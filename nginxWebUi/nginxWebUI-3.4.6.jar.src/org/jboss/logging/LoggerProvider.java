package org.jboss.logging;

import java.util.Map;

public interface LoggerProvider {
  Logger getLogger(String paramString);
  
  void clearMdc();
  
  Object putMdc(String paramString, Object paramObject);
  
  Object getMdc(String paramString);
  
  void removeMdc(String paramString);
  
  Map<String, Object> getMdcMap();
  
  void clearNdc();
  
  String getNdc();
  
  int getNdcDepth();
  
  String popNdc();
  
  String peekNdc();
  
  void pushNdc(String paramString);
  
  void setNdcMaxDepth(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\LoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */