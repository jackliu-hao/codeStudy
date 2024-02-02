package org.apache.http.entity;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

public interface ContentLengthStrategy {
  public static final int IDENTITY = -1;
  
  public static final int CHUNKED = -2;
  
  long determineLength(HttpMessage paramHttpMessage) throws HttpException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\entity\ContentLengthStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */