/*     */ package org.apache.http.impl;
/*     */ 
/*     */ import java.util.Locale;
/*     */ import org.apache.http.ReasonPhraseCatalog;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class EnglishReasonPhraseCatalog
/*     */   implements ReasonPhraseCatalog
/*     */ {
/*  55 */   public static final EnglishReasonPhraseCatalog INSTANCE = new EnglishReasonPhraseCatalog();
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
/*     */   public String getReason(int status, Locale loc) {
/*  77 */     Args.check((status >= 100 && status < 600), "Unknown category for status code " + status);
/*  78 */     int category = status / 100;
/*  79 */     int subcode = status - 100 * category;
/*     */     
/*  81 */     String reason = null;
/*  82 */     if ((REASON_PHRASES[category]).length > subcode) {
/*  83 */       reason = REASON_PHRASES[category][subcode];
/*     */     }
/*     */     
/*  86 */     return reason;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  91 */   private static final String[][] REASON_PHRASES = new String[][] { null, new String[3], new String[8], new String[8], new String[25], new String[8] };
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
/*     */   private static void setReason(int status, String reason) {
/* 110 */     int category = status / 100;
/* 111 */     int subcode = status - 100 * category;
/* 112 */     REASON_PHRASES[category][subcode] = reason;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 121 */     setReason(200, "OK");
/*     */     
/* 123 */     setReason(201, "Created");
/*     */     
/* 125 */     setReason(202, "Accepted");
/*     */     
/* 127 */     setReason(204, "No Content");
/*     */     
/* 129 */     setReason(301, "Moved Permanently");
/*     */     
/* 131 */     setReason(302, "Moved Temporarily");
/*     */     
/* 133 */     setReason(304, "Not Modified");
/*     */     
/* 135 */     setReason(400, "Bad Request");
/*     */     
/* 137 */     setReason(401, "Unauthorized");
/*     */     
/* 139 */     setReason(403, "Forbidden");
/*     */     
/* 141 */     setReason(404, "Not Found");
/*     */     
/* 143 */     setReason(500, "Internal Server Error");
/*     */     
/* 145 */     setReason(501, "Not Implemented");
/*     */     
/* 147 */     setReason(502, "Bad Gateway");
/*     */     
/* 149 */     setReason(503, "Service Unavailable");
/*     */ 
/*     */ 
/*     */     
/* 153 */     setReason(100, "Continue");
/*     */     
/* 155 */     setReason(307, "Temporary Redirect");
/*     */     
/* 157 */     setReason(405, "Method Not Allowed");
/*     */     
/* 159 */     setReason(409, "Conflict");
/*     */     
/* 161 */     setReason(412, "Precondition Failed");
/*     */     
/* 163 */     setReason(413, "Request Too Long");
/*     */     
/* 165 */     setReason(414, "Request-URI Too Long");
/*     */     
/* 167 */     setReason(415, "Unsupported Media Type");
/*     */     
/* 169 */     setReason(300, "Multiple Choices");
/*     */     
/* 171 */     setReason(303, "See Other");
/*     */     
/* 173 */     setReason(305, "Use Proxy");
/*     */     
/* 175 */     setReason(402, "Payment Required");
/*     */     
/* 177 */     setReason(406, "Not Acceptable");
/*     */     
/* 179 */     setReason(407, "Proxy Authentication Required");
/*     */     
/* 181 */     setReason(408, "Request Timeout");
/*     */ 
/*     */     
/* 184 */     setReason(101, "Switching Protocols");
/*     */     
/* 186 */     setReason(203, "Non Authoritative Information");
/*     */     
/* 188 */     setReason(205, "Reset Content");
/*     */     
/* 190 */     setReason(206, "Partial Content");
/*     */     
/* 192 */     setReason(504, "Gateway Timeout");
/*     */     
/* 194 */     setReason(505, "Http Version Not Supported");
/*     */     
/* 196 */     setReason(410, "Gone");
/*     */     
/* 198 */     setReason(411, "Length Required");
/*     */     
/* 200 */     setReason(416, "Requested Range Not Satisfiable");
/*     */     
/* 202 */     setReason(417, "Expectation Failed");
/*     */ 
/*     */ 
/*     */     
/* 206 */     setReason(102, "Processing");
/*     */     
/* 208 */     setReason(207, "Multi-Status");
/*     */     
/* 210 */     setReason(422, "Unprocessable Entity");
/*     */     
/* 212 */     setReason(419, "Insufficient Space On Resource");
/*     */     
/* 214 */     setReason(420, "Method Failure");
/*     */     
/* 216 */     setReason(423, "Locked");
/*     */     
/* 218 */     setReason(507, "Insufficient Storage");
/*     */     
/* 220 */     setReason(424, "Failed Dependency");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\EnglishReasonPhraseCatalog.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */