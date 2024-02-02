package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.util.CharArrayBuffer;

public interface HeaderValueParser {
  HeaderElement[] parseElements(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  HeaderElement parseHeaderElement(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  NameValuePair[] parseParameters(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
  
  NameValuePair parseNameValuePair(CharArrayBuffer paramCharArrayBuffer, ParserCursor paramParserCursor) throws ParseException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\HeaderValueParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */