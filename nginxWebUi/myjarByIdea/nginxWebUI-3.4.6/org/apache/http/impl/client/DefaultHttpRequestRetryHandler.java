package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {
   public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();
   private final int retryCount;
   private final boolean requestSentRetryEnabled;
   private final Set<Class<? extends IOException>> nonRetriableClasses;

   protected DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled, Collection<Class<? extends IOException>> clazzes) {
      this.retryCount = retryCount;
      this.requestSentRetryEnabled = requestSentRetryEnabled;
      this.nonRetriableClasses = new HashSet();
      Iterator i$ = clazzes.iterator();

      while(i$.hasNext()) {
         Class<? extends IOException> clazz = (Class)i$.next();
         this.nonRetriableClasses.add(clazz);
      }

   }

   public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
      this(retryCount, requestSentRetryEnabled, Arrays.asList(InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class));
   }

   public DefaultHttpRequestRetryHandler() {
      this(3, false);
   }

   public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
      Args.notNull(exception, "Exception parameter");
      Args.notNull(context, "HTTP context");
      if (executionCount > this.retryCount) {
         return false;
      } else if (this.nonRetriableClasses.contains(exception.getClass())) {
         return false;
      } else {
         Iterator i$ = this.nonRetriableClasses.iterator();

         Class rejectException;
         do {
            if (!i$.hasNext()) {
               HttpClientContext clientContext = HttpClientContext.adapt(context);
               HttpRequest request = clientContext.getRequest();
               if (this.requestIsAborted(request)) {
                  return false;
               }

               if (this.handleAsIdempotent(request)) {
                  return true;
               }

               if (clientContext.isRequestSent() && !this.requestSentRetryEnabled) {
                  return false;
               }

               return true;
            }

            rejectException = (Class)i$.next();
         } while(!rejectException.isInstance(exception));

         return false;
      }
   }

   public boolean isRequestSentRetryEnabled() {
      return this.requestSentRetryEnabled;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   protected boolean handleAsIdempotent(HttpRequest request) {
      return !(request instanceof HttpEntityEnclosingRequest);
   }

   /** @deprecated */
   @Deprecated
   protected boolean requestIsAborted(HttpRequest request) {
      HttpRequest req = request;
      if (request instanceof RequestWrapper) {
         req = ((RequestWrapper)request).getOriginal();
      }

      return req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted();
   }
}
