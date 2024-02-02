package org.apache.http.impl.io;

import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.LineFormatter;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory<HttpResponse> {
   public static final DefaultHttpResponseWriterFactory INSTANCE = new DefaultHttpResponseWriterFactory();
   private final LineFormatter lineFormatter;

   public DefaultHttpResponseWriterFactory(LineFormatter lineFormatter) {
      this.lineFormatter = (LineFormatter)(lineFormatter != null ? lineFormatter : BasicLineFormatter.INSTANCE);
   }

   public DefaultHttpResponseWriterFactory() {
      this((LineFormatter)null);
   }

   public HttpMessageWriter<HttpResponse> create(SessionOutputBuffer buffer) {
      return new DefaultHttpResponseWriter(buffer, this.lineFormatter);
   }
}
