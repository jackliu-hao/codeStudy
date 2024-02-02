/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
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
/*    */ public class Http2SettingsStreamSourceChannel
/*    */   extends AbstractHttp2StreamSourceChannel
/*    */ {
/*    */   private final List<Http2Setting> settings;
/*    */   
/*    */   Http2SettingsStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, List<Http2Setting> settings) {
/* 36 */     super(framedChannel, data, frameDataRemaining);
/* 37 */     this.settings = settings;
/* 38 */     lastFrame();
/*    */   }
/*    */   
/*    */   public List<Http2Setting> getSettings() {
/* 42 */     return Collections.unmodifiableList(this.settings);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2SettingsStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */