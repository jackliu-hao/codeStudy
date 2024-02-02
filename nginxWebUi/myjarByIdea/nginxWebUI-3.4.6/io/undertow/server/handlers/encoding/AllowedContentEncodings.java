package io.undertow.server.handlers.encoding;

import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import java.util.Iterator;
import java.util.List;
import org.xnio.conduits.StreamSinkConduit;

public class AllowedContentEncodings implements ConduitWrapper<StreamSinkConduit> {
   public static final AttachmentKey<AllowedContentEncodings> ATTACHMENT_KEY = AttachmentKey.create(AllowedContentEncodings.class);
   private final HttpServerExchange exchange;
   private final List<EncodingMapping> encodings;

   public AllowedContentEncodings(HttpServerExchange exchange, List<EncodingMapping> encodings) {
      this.exchange = exchange;
      this.encodings = encodings;
   }

   public String getCurrentContentEncoding() {
      Iterator var1 = this.encodings.iterator();

      EncodingMapping encoding;
      do {
         if (!var1.hasNext()) {
            return Headers.IDENTITY.toString();
         }

         encoding = (EncodingMapping)var1.next();
      } while(encoding.getAllowed() != null && !encoding.getAllowed().resolve(this.exchange));

      return encoding.getName();
   }

   public EncodingMapping getEncoding() {
      Iterator var1 = this.encodings.iterator();

      EncodingMapping encoding;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         encoding = (EncodingMapping)var1.next();
      } while(encoding.getAllowed() != null && !encoding.getAllowed().resolve(this.exchange));

      return encoding;
   }

   public boolean isIdentity() {
      return this.getCurrentContentEncoding().equals(Headers.IDENTITY.toString());
   }

   public boolean isNoEncodingsAllowed() {
      return this.encodings.isEmpty();
   }

   public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
      if (exchange.getResponseHeaders().contains(Headers.CONTENT_ENCODING)) {
         return (StreamSinkConduit)factory.create();
      } else {
         if (exchange.getResponseContentLength() != 0L && exchange.getStatusCode() != 204 && exchange.getStatusCode() != 304) {
            EncodingMapping encoding = this.getEncoding();
            if (encoding != null) {
               exchange.getResponseHeaders().put(Headers.CONTENT_ENCODING, encoding.getName());
               if (exchange.getRequestMethod().equals(Methods.HEAD)) {
                  return (StreamSinkConduit)factory.create();
               }

               return (StreamSinkConduit)encoding.getEncoding().getResponseWrapper().wrap(factory, exchange);
            }
         }

         return (StreamSinkConduit)factory.create();
      }
   }
}
