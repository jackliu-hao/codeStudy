package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class RequestAcceptEncoding implements HttpRequestInterceptor {
   private final String acceptEncoding;

   public RequestAcceptEncoding(List<String> encodings) {
      if (encodings != null && !encodings.isEmpty()) {
         StringBuilder buf = new StringBuilder();

         for(int i = 0; i < encodings.size(); ++i) {
            if (i > 0) {
               buf.append(",");
            }

            buf.append((String)encodings.get(i));
         }

         this.acceptEncoding = buf.toString();
      } else {
         this.acceptEncoding = "gzip,deflate";
      }

   }

   public RequestAcceptEncoding() {
      this((List)null);
   }

   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      RequestConfig requestConfig = clientContext.getRequestConfig();
      if (!request.containsHeader("Accept-Encoding") && requestConfig.isContentCompressionEnabled()) {
         request.addHeader("Accept-Encoding", this.acceptEncoding);
      }

   }
}
