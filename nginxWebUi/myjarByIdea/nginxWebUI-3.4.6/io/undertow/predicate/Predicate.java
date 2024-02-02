package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.util.Map;

public interface Predicate {
   AttachmentKey<Map<String, Object>> PREDICATE_CONTEXT = AttachmentKey.create(Map.class);

   boolean resolve(HttpServerExchange var1);
}
