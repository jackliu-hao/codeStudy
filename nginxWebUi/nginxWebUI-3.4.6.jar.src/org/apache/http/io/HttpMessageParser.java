package org.apache.http.io;

import java.io.IOException;
import org.apache.http.HttpException;

public interface HttpMessageParser<T extends org.apache.http.HttpMessage> {
  T parse() throws IOException, HttpException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\io\HttpMessageParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */