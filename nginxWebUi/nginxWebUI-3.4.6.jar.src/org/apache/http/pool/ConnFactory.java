package org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C> {
  C create(T paramT) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\pool\ConnFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */