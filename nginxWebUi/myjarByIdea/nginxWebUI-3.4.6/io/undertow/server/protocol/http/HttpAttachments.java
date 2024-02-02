package io.undertow.server.protocol.http;

import io.undertow.util.AttachmentKey;
import io.undertow.util.HeaderMap;
import java.util.function.Supplier;

public class HttpAttachments {
   public static final AttachmentKey<HeaderMap> REQUEST_TRAILERS = AttachmentKey.create(HeaderMap.class);
   public static final AttachmentKey<HeaderMap> RESPONSE_TRAILERS = AttachmentKey.create(HeaderMap.class);
   public static final AttachmentKey<Supplier<HeaderMap>> RESPONSE_TRAILER_SUPPLIER = AttachmentKey.create(Supplier.class);
   public static final AttachmentKey<Boolean> PRE_CHUNKED_RESPONSE = AttachmentKey.create(Boolean.class);
}
