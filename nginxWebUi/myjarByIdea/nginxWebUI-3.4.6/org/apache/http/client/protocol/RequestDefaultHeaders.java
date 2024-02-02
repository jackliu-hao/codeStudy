package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class RequestDefaultHeaders implements HttpRequestInterceptor {
   private final Collection<? extends Header> defaultHeaders;

   public RequestDefaultHeaders(Collection<? extends Header> defaultHeaders) {
      this.defaultHeaders = defaultHeaders;
   }

   public RequestDefaultHeaders() {
      this((Collection)null);
   }

   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
      Args.notNull(request, "HTTP request");
      String method = request.getRequestLine().getMethod();
      if (!method.equalsIgnoreCase("CONNECT")) {
         Collection<? extends Header> defHeaders = (Collection)request.getParams().getParameter("http.default-headers");
         if (defHeaders == null) {
            defHeaders = this.defaultHeaders;
         }

         if (defHeaders != null) {
            Iterator i$ = defHeaders.iterator();

            while(i$.hasNext()) {
               Header defHeader = (Header)i$.next();
               request.addHeader(defHeader);
            }
         }

      }
   }
}
