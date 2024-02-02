/*     */ package org.noear.solon.socketd;
/*     */ 
/*     */ import org.noear.solon.Solon;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SocketProps
/*     */ {
/*  12 */   private static int readBufferSize = 0;
/*  13 */   private static int writeBufferSize = 0;
/*     */   
/*  15 */   private static int connectTimeout = 0;
/*  16 */   private static int socketTimeout = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int readBufferSize() {
/*  23 */     return readBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int writeBufferSize() {
/*  30 */     return writeBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int connectTimeout() {
/*  38 */     return connectTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int socketTimeout() {
/*  45 */     return socketTimeout;
/*     */   }
/*     */ 
/*     */   
/*     */   static {
/*  50 */     readBufferSize = loadBufferSize("solon.socketd.readBufferSize");
/*  51 */     writeBufferSize = loadBufferSize("solon.socketd.writeBufferSize");
/*     */     
/*  53 */     connectTimeout = loadTimeout("solon.socketd.connectTimeout");
/*  54 */     socketTimeout = loadTimeout("solon.socketd.socketTimeout");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int loadBufferSize(String cfgKey) {
/*  62 */     String tmp = Solon.cfg().get(cfgKey, "").toLowerCase();
/*     */     
/*  64 */     if (tmp.length() > 2) {
/*  65 */       if (tmp.endsWith("kb")) {
/*  66 */         return Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024;
/*     */       }
/*     */       
/*  69 */       if (tmp.endsWith("mb")) {
/*  70 */         return Integer.parseInt(tmp.substring(0, tmp.length() - 2)) * 1024 * 1024;
/*     */       }
/*     */       
/*  73 */       if (tmp.indexOf("b") < 0) {
/*  74 */         return Integer.parseInt(tmp);
/*     */       }
/*     */     } 
/*     */     
/*  78 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int loadTimeout(String cfgKey) {
/*  85 */     String tmp = Solon.cfg().get(cfgKey, "").toLowerCase();
/*     */     
/*  87 */     if (tmp.length() > 2) {
/*  88 */       if (tmp.endsWith("ms")) {
/*  89 */         return Integer.parseInt(tmp.substring(0, tmp.length() - 2));
/*     */       }
/*     */       
/*  92 */       if (tmp.endsWith("s")) {
/*  93 */         return Integer.parseInt(tmp.substring(0, tmp.length() - 1)) * 1000;
/*     */       }
/*     */       
/*  96 */       if (tmp.indexOf("s") < 0)
/*     */       {
/*  98 */         return Integer.parseInt(tmp);
/*     */       }
/*     */     } 
/*     */     
/* 102 */     return 0;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SocketProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */