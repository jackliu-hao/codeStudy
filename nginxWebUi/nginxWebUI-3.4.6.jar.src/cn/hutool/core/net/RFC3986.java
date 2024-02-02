/*     */ package cn.hutool.core.net;
/*     */ 
/*     */ import cn.hutool.core.codec.PercentCodec;
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
/*     */ public class RFC3986
/*     */ {
/*  17 */   public static final PercentCodec GEN_DELIMS = PercentCodec.of(":/?#[]@");
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  22 */   public static final PercentCodec SUB_DELIMS = PercentCodec.of("!$&'()*+,;=");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  28 */   public static final PercentCodec RESERVED = GEN_DELIMS.orNew(SUB_DELIMS);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final PercentCodec UNRESERVED = PercentCodec.of(unreservedChars());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   public static final PercentCodec PCHAR = UNRESERVED.orNew(SUB_DELIMS).or(PercentCodec.of(":@"));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  45 */   public static final PercentCodec SEGMENT = PCHAR;
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final PercentCodec SEGMENT_NZ_NC = PercentCodec.of(SEGMENT).removeSafe(':');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public static final PercentCodec PATH = SEGMENT.orNew(PercentCodec.of("/"));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final PercentCodec QUERY = PCHAR.orNew(PercentCodec.of("/?"));
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  64 */   public static final PercentCodec FRAGMENT = QUERY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public static final PercentCodec QUERY_PARAM_VALUE = PercentCodec.of(QUERY).removeSafe('&');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   public static final PercentCodec QUERY_PARAM_NAME = PercentCodec.of(QUERY_PARAM_VALUE).removeSafe('=');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static StringBuilder unreservedChars() {
/*  84 */     StringBuilder sb = new StringBuilder();
/*     */     
/*     */     char c;
/*  87 */     for (c = 'A'; c <= 'Z'; c = (char)(c + 1)) {
/*  88 */       sb.append(c);
/*     */     }
/*  90 */     for (c = 'a'; c <= 'z'; c = (char)(c + 1)) {
/*  91 */       sb.append(c);
/*     */     }
/*     */ 
/*     */     
/*  95 */     for (c = '0'; c <= '9'; c = (char)(c + 1)) {
/*  96 */       sb.append(c);
/*     */     }
/*     */ 
/*     */     
/* 100 */     sb.append("_.-~");
/*     */     
/* 102 */     return sb;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\RFC3986.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */