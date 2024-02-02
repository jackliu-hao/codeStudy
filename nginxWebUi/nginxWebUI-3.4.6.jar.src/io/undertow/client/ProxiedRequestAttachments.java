/*    */ package io.undertow.client;
/*    */ 
/*    */ import io.undertow.util.AttachmentKey;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProxiedRequestAttachments
/*    */ {
/* 30 */   public static final AttachmentKey<String> REMOTE_ADDRESS = AttachmentKey.create(String.class);
/* 31 */   public static final AttachmentKey<String> REMOTE_HOST = AttachmentKey.create(String.class);
/* 32 */   public static final AttachmentKey<String> SERVER_NAME = AttachmentKey.create(String.class);
/* 33 */   public static final AttachmentKey<Integer> SERVER_PORT = AttachmentKey.create(Integer.class);
/* 34 */   public static final AttachmentKey<Boolean> IS_SSL = AttachmentKey.create(Boolean.class);
/*    */   
/* 36 */   public static final AttachmentKey<String> REMOTE_USER = AttachmentKey.create(String.class);
/* 37 */   public static final AttachmentKey<String> AUTH_TYPE = AttachmentKey.create(String.class);
/* 38 */   public static final AttachmentKey<String> ROUTE = AttachmentKey.create(String.class);
/* 39 */   public static final AttachmentKey<String> SSL_CERT = AttachmentKey.create(String.class);
/* 40 */   public static final AttachmentKey<String> SSL_CYPHER = AttachmentKey.create(String.class);
/* 41 */   public static final AttachmentKey<byte[]> SSL_SESSION_ID = AttachmentKey.create(byte[].class);
/* 42 */   public static final AttachmentKey<Integer> SSL_KEY_SIZE = AttachmentKey.create(Integer.class);
/* 43 */   public static final AttachmentKey<String> SECRET = AttachmentKey.create(String.class);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\client\ProxiedRequestAttachments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */