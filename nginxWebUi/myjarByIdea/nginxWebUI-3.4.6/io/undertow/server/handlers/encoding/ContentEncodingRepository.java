package io.undertow.server.handlers.encoding;

import io.undertow.predicate.Predicate;
import io.undertow.predicate.Predicates;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.CopyOnWriteMap;
import io.undertow.util.Headers;
import io.undertow.util.QValueParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContentEncodingRepository {
   public static final String IDENTITY = "identity";
   public static final EncodingMapping IDENTITY_ENCODING;
   private final Map<String, EncodingMapping> encodingMap = new CopyOnWriteMap();

   public AllowedContentEncodings getContentEncodings(HttpServerExchange exchange) {
      List<String> res = exchange.getRequestHeaders().get(Headers.ACCEPT_ENCODING);
      if (res != null && !res.isEmpty()) {
         List<EncodingMapping> resultingMappings = new ArrayList();
         List<List<QValueParser.QValueResult>> found = QValueParser.parse(res);
         Iterator var5 = found.iterator();

         while(var5.hasNext()) {
            List<QValueParser.QValueResult> result = (List)var5.next();
            List<EncodingMapping> available = new ArrayList();
            boolean includesIdentity = false;
            boolean isQValue0 = false;
            Iterator var10 = result.iterator();

            while(var10.hasNext()) {
               QValueParser.QValueResult value = (QValueParser.QValueResult)var10.next();
               EncodingMapping encoding;
               if (value.getValue().equals("*")) {
                  includesIdentity = true;
                  encoding = IDENTITY_ENCODING;
               } else {
                  encoding = (EncodingMapping)this.encodingMap.get(value.getValue());
                  if (encoding == null && "identity".equals(value.getValue())) {
                     encoding = IDENTITY_ENCODING;
                  }
               }

               if (value.isQValueZero()) {
                  isQValue0 = true;
               }

               if (encoding != null) {
                  available.add(encoding);
               }
            }

            if (isQValue0) {
               if (resultingMappings.isEmpty()) {
                  if (includesIdentity) {
                     return new AllowedContentEncodings(exchange, Collections.emptyList());
                  }

                  return null;
               }
            } else if (!available.isEmpty()) {
               Collections.sort(available, Collections.reverseOrder());
               resultingMappings.addAll(available);
            }
         }

         if (!resultingMappings.isEmpty()) {
            return new AllowedContentEncodings(exchange, resultingMappings);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public synchronized ContentEncodingRepository addEncodingHandler(String encoding, ContentEncodingProvider encoder, int priority) {
      this.addEncodingHandler(encoding, encoder, priority, Predicates.truePredicate());
      return this;
   }

   public synchronized ContentEncodingRepository addEncodingHandler(String encoding, ContentEncodingProvider encoder, int priority, Predicate enabledPredicate) {
      this.encodingMap.put(encoding, new EncodingMapping(encoding, encoder, priority, enabledPredicate));
      return this;
   }

   public synchronized ContentEncodingRepository removeEncodingHandler(String encoding) {
      this.encodingMap.remove(encoding);
      return this;
   }

   static {
      IDENTITY_ENCODING = new EncodingMapping("identity", ContentEncodingProvider.IDENTITY, 0, Predicates.truePredicate());
   }
}
