package org.apache.http.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

public interface HttpMessageWriter<T extends HttpMessage> {
   void write(T var1) throws IOException, HttpException;
}
