package org.apache.http.io;

import org.apache.http.HttpMessage;
import org.apache.http.config.MessageConstraints;

public interface HttpMessageParserFactory<T extends HttpMessage> {
   HttpMessageParser<T> create(SessionInputBuffer var1, MessageConstraints var2);
}
