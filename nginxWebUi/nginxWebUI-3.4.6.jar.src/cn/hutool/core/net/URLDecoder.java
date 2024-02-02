/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.util.CharUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.nio.charset.Charset;
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
/*     */ public class URLDecoder
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final byte ESCAPE_CHAR = 37;
/*     */   
/*     */   public static String decodeForPath(String str, Charset charset) {
/*  40 */     return decode(str, charset, false);
/*     */   }
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
/*     */   public static String decode(String str, Charset charset) {
/*  57 */     return decode(str, charset, true);
/*     */   }
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
/*     */   public static String decode(String str, Charset charset, boolean isPlusToSpace) {
/*  74 */     if (null == charset) {
/*  75 */       return str;
/*     */     }
/*  77 */     return StrUtil.str(decode(StrUtil.bytes(str, charset), isPlusToSpace), charset);
/*     */   }
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
/*     */   public static byte[] decode(byte[] bytes) {
/*  92 */     return decode(bytes, true);
/*     */   }
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
/*     */   public static byte[] decode(byte[] bytes, boolean isPlusToSpace) {
/* 109 */     if (bytes == null) {
/* 110 */       return null;
/*     */     }
/* 112 */     ByteArrayOutputStream buffer = new ByteArrayOutputStream(bytes.length);
/*     */     
/* 114 */     for (int i = 0; i < bytes.length; i++) {
/* 115 */       int b = bytes[i];
/* 116 */       if (b == 43) {
/* 117 */         buffer.write(isPlusToSpace ? 32 : b); continue;
/* 118 */       }  if (b == 37) {
/* 119 */         if (i + 1 < bytes.length) {
/* 120 */           int u = CharUtil.digit16(bytes[i + 1]);
/* 121 */           if (u >= 0 && i + 2 < bytes.length) {
/* 122 */             int l = CharUtil.digit16(bytes[i + 2]);
/* 123 */             if (l >= 0) {
/* 124 */               buffer.write((char)((u << 4) + l));
/* 125 */               i += 2;
/*     */               
/*     */               continue;
/*     */             } 
/*     */           } 
/*     */         } 
/* 131 */         buffer.write(b); continue;
/*     */       } 
/* 133 */       buffer.write(b);
/*     */       continue;
/*     */     } 
/* 136 */     return buffer.toByteArray();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\URLDecoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */