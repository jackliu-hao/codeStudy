/*     */ package com.sun.jna.platform.mac;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XAttrUtil
/*     */ {
/*     */   public static List<String> listXAttr(String path) {
/*  38 */     long bufferLength = XAttr.INSTANCE.listxattr(path, null, 0L, 0);
/*     */     
/*  40 */     if (bufferLength < 0L) {
/*  41 */       return null;
/*     */     }
/*  43 */     if (bufferLength == 0L) {
/*  44 */       return new ArrayList<String>(0);
/*     */     }
/*  46 */     Memory valueBuffer = new Memory(bufferLength);
/*  47 */     long valueLength = XAttr.INSTANCE.listxattr(path, (Pointer)valueBuffer, bufferLength, 0);
/*     */     
/*  49 */     if (valueLength < 0L) {
/*  50 */       return null;
/*     */     }
/*  52 */     return decodeStringSequence(valueBuffer.getByteBuffer(0L, valueLength));
/*     */   }
/*     */ 
/*     */   
/*     */   public static String getXAttr(String path, String name) {
/*  57 */     long bufferLength = XAttr.INSTANCE.getxattr(path, name, null, 0L, 0, 0);
/*     */     
/*  59 */     if (bufferLength < 0L) {
/*  60 */       return null;
/*     */     }
/*     */     
/*  63 */     if (bufferLength == 0L) {
/*  64 */       return "";
/*     */     }
/*     */     
/*  67 */     Memory valueBuffer = new Memory(bufferLength);
/*  68 */     valueBuffer.clear();
/*  69 */     long valueLength = XAttr.INSTANCE.getxattr(path, name, (Pointer)valueBuffer, bufferLength, 0, 0);
/*     */     
/*  71 */     if (valueLength < 0L) {
/*  72 */       return null;
/*     */     }
/*     */     
/*  75 */     return Native.toString(valueBuffer.getByteArray(0L, (int)bufferLength), "UTF-8");
/*     */   }
/*     */   
/*     */   public static int setXAttr(String path, String name, String value) {
/*  79 */     Memory valueBuffer = encodeString(value);
/*  80 */     return XAttr.INSTANCE.setxattr(path, name, (Pointer)valueBuffer, valueBuffer.size(), 0, 0);
/*     */   }
/*     */   
/*     */   public static int removeXAttr(String path, String name) {
/*  84 */     return XAttr.INSTANCE.removexattr(path, name, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   protected static Memory encodeString(String s) {
/*  89 */     byte[] bb = s.getBytes(Charset.forName("UTF-8"));
/*  90 */     Memory valueBuffer = new Memory(bb.length);
/*  91 */     valueBuffer.write(0L, bb, 0, bb.length);
/*  92 */     return valueBuffer;
/*     */   }
/*     */   
/*     */   protected static String decodeString(ByteBuffer bb) {
/*  96 */     return Charset.forName("UTF-8").decode(bb).toString();
/*     */   }
/*     */   
/*     */   protected static List<String> decodeStringSequence(ByteBuffer bb) {
/* 100 */     List<String> names = new ArrayList<String>();
/*     */     
/* 102 */     bb.mark();
/* 103 */     while (bb.hasRemaining()) {
/* 104 */       if (bb.get() == 0) {
/* 105 */         ByteBuffer nameBuffer = (ByteBuffer)bb.duplicate().limit(bb.position() - 1).reset();
/* 106 */         if (nameBuffer.hasRemaining()) {
/* 107 */           names.add(decodeString(nameBuffer));
/*     */         }
/* 109 */         bb.mark();
/*     */       } 
/*     */     } 
/*     */     
/* 113 */     return names;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\XAttrUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */