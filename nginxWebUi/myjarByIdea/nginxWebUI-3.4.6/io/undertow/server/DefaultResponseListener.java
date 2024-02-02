package io.undertow.server;

import io.undertow.util.AttachmentKey;

public interface DefaultResponseListener {
   AttachmentKey<Throwable> EXCEPTION = AttachmentKey.create(Throwable.class);

   boolean handleDefaultResponse(HttpServerExchange var1);
}
