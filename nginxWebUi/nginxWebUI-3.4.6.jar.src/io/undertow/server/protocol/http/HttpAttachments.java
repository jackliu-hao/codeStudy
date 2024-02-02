/*    */ package io.undertow.server.protocol.http;
/*    */ 
/*    */ import io.undertow.util.AttachmentKey;
/*    */ import io.undertow.util.HeaderMap;
/*    */ import java.util.function.Supplier;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpAttachments
/*    */ {
/* 37 */   public static final AttachmentKey<HeaderMap> REQUEST_TRAILERS = AttachmentKey.create(HeaderMap.class);
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
/* 49 */   public static final AttachmentKey<HeaderMap> RESPONSE_TRAILERS = AttachmentKey.create(HeaderMap.class);
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
/* 62 */   public static final AttachmentKey<Supplier<HeaderMap>> RESPONSE_TRAILER_SUPPLIER = AttachmentKey.create(Supplier.class);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 71 */   public static final AttachmentKey<Boolean> PRE_CHUNKED_RESPONSE = AttachmentKey.create(Boolean.class);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\http\HttpAttachments.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */