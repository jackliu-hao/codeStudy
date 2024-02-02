/*    */ package io.undertow.protocols.ajp;
/*    */ 
/*    */ import io.undertow.util.HttpString;
/*    */ import java.nio.ByteBuffer;
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
/*    */ class AjpUtils
/*    */ {
/*    */   static boolean notNull(Boolean attachment) {
/* 32 */     return (attachment == null) ? false : attachment.booleanValue();
/*    */   }
/*    */   
/*    */   static int notNull(Integer attachment) {
/* 36 */     return (attachment == null) ? 0 : attachment.intValue();
/*    */   }
/*    */   
/*    */   static String notNull(String attachment) {
/* 40 */     return (attachment == null) ? "" : attachment;
/*    */   }
/*    */   
/*    */   static void putInt(ByteBuffer buf, int value) {
/* 44 */     buf.put((byte)(value >> 8 & 0xFF));
/* 45 */     buf.put((byte)(value & 0xFF));
/*    */   }
/*    */   
/*    */   static void putString(ByteBuffer buf, String value) {
/* 49 */     int length = value.length();
/* 50 */     putInt(buf, length);
/* 51 */     for (int i = 0; i < length; i++) {
/* 52 */       buf.put((byte)value.charAt(i));
/*    */     }
/* 54 */     buf.put((byte)0);
/*    */   }
/*    */   
/*    */   static void putHttpString(ByteBuffer buf, HttpString value) {
/* 58 */     int length = value.length();
/* 59 */     putInt(buf, length);
/* 60 */     value.appendTo(buf);
/* 61 */     buf.put((byte)0);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */