/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.ArrayList;
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
/*    */ class Http2SettingsParser
/*    */   extends Http2PushBackParser
/*    */ {
/* 30 */   private int count = 0;
/*    */   
/* 32 */   private final List<Http2Setting> settings = new ArrayList<>();
/*    */   
/*    */   Http2SettingsParser(int frameLength) {
/* 35 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser parser) {
/* 40 */     while (this.count < parser.length) {
/* 41 */       if (resource.remaining() < 6) {
/*    */         return;
/*    */       }
/* 44 */       int id = (resource.get() & 0xFF) << 8;
/* 45 */       id += resource.get() & 0xFF;
/* 46 */       long value = (resource.get() & 0xFFL) << 24L;
/* 47 */       value += (resource.get() & 0xFFL) << 16L;
/* 48 */       value += (resource.get() & 0xFFL) << 8L;
/* 49 */       value += resource.get() & 0xFFL;
/* 50 */       this.settings.add(new Http2Setting(id, value));
/* 51 */       this.count += 6;
/*    */     } 
/*    */   }
/*    */   
/*    */   public List<Http2Setting> getSettings() {
/* 56 */     return this.settings;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2SettingsParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */