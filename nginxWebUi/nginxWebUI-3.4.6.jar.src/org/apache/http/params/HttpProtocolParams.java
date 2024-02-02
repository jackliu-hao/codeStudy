/*     */ package org.apache.http.params;
/*     */ 
/*     */ import java.nio.charset.CodingErrorAction;
/*     */ import org.apache.http.HttpVersion;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.protocol.HTTP;
/*     */ import org.apache.http.util.Args;
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
/*     */ @Deprecated
/*     */ public final class HttpProtocolParams
/*     */   implements CoreProtocolPNames
/*     */ {
/*     */   public static String getHttpElementCharset(HttpParams params) {
/*  60 */     Args.notNull(params, "HTTP parameters");
/*  61 */     String charset = (String)params.getParameter("http.protocol.element-charset");
/*     */     
/*  63 */     if (charset == null) {
/*  64 */       charset = HTTP.DEF_PROTOCOL_CHARSET.name();
/*     */     }
/*  66 */     return charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setHttpElementCharset(HttpParams params, String charset) {
/*  76 */     Args.notNull(params, "HTTP parameters");
/*  77 */     params.setParameter("http.protocol.element-charset", charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getContentCharset(HttpParams params) {
/*  88 */     Args.notNull(params, "HTTP parameters");
/*  89 */     String charset = (String)params.getParameter("http.protocol.content-charset");
/*     */     
/*  91 */     if (charset == null) {
/*  92 */       charset = HTTP.DEF_CONTENT_CHARSET.name();
/*     */     }
/*  94 */     return charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setContentCharset(HttpParams params, String charset) {
/* 104 */     Args.notNull(params, "HTTP parameters");
/* 105 */     params.setParameter("http.protocol.content-charset", charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProtocolVersion getVersion(HttpParams params) {
/* 116 */     Args.notNull(params, "HTTP parameters");
/* 117 */     Object param = params.getParameter("http.protocol.version");
/*     */     
/* 119 */     if (param == null) {
/* 120 */       return (ProtocolVersion)HttpVersion.HTTP_1_1;
/*     */     }
/* 122 */     return (ProtocolVersion)param;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setVersion(HttpParams params, ProtocolVersion version) {
/* 132 */     Args.notNull(params, "HTTP parameters");
/* 133 */     params.setParameter("http.protocol.version", version);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getUserAgent(HttpParams params) {
/* 144 */     Args.notNull(params, "HTTP parameters");
/* 145 */     return (String)params.getParameter("http.useragent");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setUserAgent(HttpParams params, String useragent) {
/* 155 */     Args.notNull(params, "HTTP parameters");
/* 156 */     params.setParameter("http.useragent", useragent);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean useExpectContinue(HttpParams params) {
/* 167 */     Args.notNull(params, "HTTP parameters");
/* 168 */     return params.getBooleanParameter("http.protocol.expect-continue", false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setUseExpectContinue(HttpParams params, boolean b) {
/* 178 */     Args.notNull(params, "HTTP parameters");
/* 179 */     params.setBooleanParameter("http.protocol.expect-continue", b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodingErrorAction getMalformedInputAction(HttpParams params) {
/* 190 */     Args.notNull(params, "HTTP parameters");
/* 191 */     Object param = params.getParameter("http.malformed.input.action");
/* 192 */     if (param == null)
/*     */     {
/* 194 */       return CodingErrorAction.REPORT;
/*     */     }
/* 196 */     return (CodingErrorAction)param;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setMalformedInputAction(HttpParams params, CodingErrorAction action) {
/* 207 */     Args.notNull(params, "HTTP parameters");
/* 208 */     params.setParameter("http.malformed.input.action", action);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CodingErrorAction getUnmappableInputAction(HttpParams params) {
/* 219 */     Args.notNull(params, "HTTP parameters");
/* 220 */     Object param = params.getParameter("http.unmappable.input.action");
/* 221 */     if (param == null)
/*     */     {
/* 223 */       return CodingErrorAction.REPORT;
/*     */     }
/* 225 */     return (CodingErrorAction)param;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setUnmappableInputAction(HttpParams params, CodingErrorAction action) {
/* 236 */     Args.notNull(params, "HTTP parameters");
/* 237 */     params.setParameter("http.unmappable.input.action", action);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpProtocolParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */