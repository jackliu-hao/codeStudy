package io.undertow.client;

import io.undertow.util.AttachmentKey;

public class ProxiedRequestAttachments {
   public static final AttachmentKey<String> REMOTE_ADDRESS = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> REMOTE_HOST = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> SERVER_NAME = AttachmentKey.create(String.class);
   public static final AttachmentKey<Integer> SERVER_PORT = AttachmentKey.create(Integer.class);
   public static final AttachmentKey<Boolean> IS_SSL = AttachmentKey.create(Boolean.class);
   public static final AttachmentKey<String> REMOTE_USER = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> AUTH_TYPE = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> ROUTE = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> SSL_CERT = AttachmentKey.create(String.class);
   public static final AttachmentKey<String> SSL_CYPHER = AttachmentKey.create(String.class);
   public static final AttachmentKey<byte[]> SSL_SESSION_ID = AttachmentKey.create(byte[].class);
   public static final AttachmentKey<Integer> SSL_KEY_SIZE = AttachmentKey.create(Integer.class);
   public static final AttachmentKey<String> SECRET = AttachmentKey.create(String.class);
}
