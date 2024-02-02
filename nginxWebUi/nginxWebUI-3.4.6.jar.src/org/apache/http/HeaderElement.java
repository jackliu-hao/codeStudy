package org.apache.http;

public interface HeaderElement {
  String getName();
  
  String getValue();
  
  NameValuePair[] getParameters();
  
  NameValuePair getParameterByName(String paramString);
  
  int getParameterCount();
  
  NameValuePair getParameter(int paramInt);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\HeaderElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */