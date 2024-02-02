package org.apache.http.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class UriHttpRequestHandlerMapper implements HttpRequestHandlerMapper {
   private final UriPatternMatcher<HttpRequestHandler> matcher;

   protected UriHttpRequestHandlerMapper(UriPatternMatcher<HttpRequestHandler> matcher) {
      this.matcher = (UriPatternMatcher)Args.notNull(matcher, "Pattern matcher");
   }

   public UriHttpRequestHandlerMapper() {
      this(new UriPatternMatcher());
   }

   public void register(String pattern, HttpRequestHandler handler) {
      Args.notNull(pattern, "Pattern");
      Args.notNull(handler, "Handler");
      this.matcher.register(pattern, handler);
   }

   public void unregister(String pattern) {
      this.matcher.unregister(pattern);
   }

   protected String getRequestPath(HttpRequest request) {
      String uriPath = request.getRequestLine().getUri();
      int index = uriPath.indexOf(63);
      if (index != -1) {
         uriPath = uriPath.substring(0, index);
      } else {
         index = uriPath.indexOf(35);
         if (index != -1) {
            uriPath = uriPath.substring(0, index);
         }
      }

      return uriPath;
   }

   public HttpRequestHandler lookup(HttpRequest request) {
      Args.notNull(request, "HTTP request");
      return (HttpRequestHandler)this.matcher.lookup(this.getRequestPath(request));
   }
}
