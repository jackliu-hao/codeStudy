package org.apache.http;

public interface Header extends NameValuePair {
  HeaderElement[] getElements() throws ParseException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\Header.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */