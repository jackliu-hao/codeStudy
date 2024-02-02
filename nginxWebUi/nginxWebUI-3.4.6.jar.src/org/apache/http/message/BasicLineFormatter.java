/*     */ package org.apache.http.message;
/*     */ 
/*     */ import org.apache.http.FormattedHeader;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.ProtocolVersion;
/*     */ import org.apache.http.RequestLine;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.CharArrayBuffer;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public class BasicLineFormatter
/*     */   implements LineFormatter
/*     */ {
/*     */   @Deprecated
/*  64 */   public static final BasicLineFormatter DEFAULT = new BasicLineFormatter();
/*     */   
/*  66 */   public static final BasicLineFormatter INSTANCE = new BasicLineFormatter();
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
/*     */   protected CharArrayBuffer initBuffer(CharArrayBuffer charBuffer) {
/*  81 */     CharArrayBuffer buffer = charBuffer;
/*  82 */     if (buffer != null) {
/*  83 */       buffer.clear();
/*     */     } else {
/*  85 */       buffer = new CharArrayBuffer(64);
/*     */     } 
/*  87 */     return buffer;
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
/*     */   public static String formatProtocolVersion(ProtocolVersion version, LineFormatter formatter) {
/* 104 */     return ((formatter != null) ? formatter : INSTANCE).appendProtocolVersion(null, version).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer appendProtocolVersion(CharArrayBuffer buffer, ProtocolVersion version) {
/* 113 */     Args.notNull(version, "Protocol version");
/*     */     
/* 115 */     CharArrayBuffer result = buffer;
/* 116 */     int len = estimateProtocolVersionLen(version);
/* 117 */     if (result == null) {
/* 118 */       result = new CharArrayBuffer(len);
/*     */     } else {
/* 120 */       result.ensureCapacity(len);
/*     */     } 
/*     */     
/* 123 */     result.append(version.getProtocol());
/* 124 */     result.append('/');
/* 125 */     result.append(Integer.toString(version.getMajor()));
/* 126 */     result.append('.');
/* 127 */     result.append(Integer.toString(version.getMinor()));
/*     */     
/* 129 */     return result;
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
/*     */   protected int estimateProtocolVersionLen(ProtocolVersion version) {
/* 143 */     return version.getProtocol().length() + 4;
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
/*     */   public static String formatRequestLine(RequestLine reqline, LineFormatter formatter) {
/* 159 */     return ((formatter != null) ? formatter : INSTANCE).formatRequestLine(null, reqline).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
/* 168 */     Args.notNull(reqline, "Request line");
/* 169 */     CharArrayBuffer result = initBuffer(buffer);
/* 170 */     doFormatRequestLine(result, reqline);
/*     */     
/* 172 */     return result;
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
/*     */   protected void doFormatRequestLine(CharArrayBuffer buffer, RequestLine reqline) {
/* 186 */     String method = reqline.getMethod();
/* 187 */     String uri = reqline.getUri();
/*     */ 
/*     */     
/* 190 */     int len = method.length() + 1 + uri.length() + 1 + estimateProtocolVersionLen(reqline.getProtocolVersion());
/*     */     
/* 192 */     buffer.ensureCapacity(len);
/*     */     
/* 194 */     buffer.append(method);
/* 195 */     buffer.append(' ');
/* 196 */     buffer.append(uri);
/* 197 */     buffer.append(' ');
/* 198 */     appendProtocolVersion(buffer, reqline.getProtocolVersion());
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
/*     */   public static String formatStatusLine(StatusLine statline, LineFormatter formatter) {
/* 215 */     return ((formatter != null) ? formatter : INSTANCE).formatStatusLine(null, statline).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
/* 224 */     Args.notNull(statline, "Status line");
/* 225 */     CharArrayBuffer result = initBuffer(buffer);
/* 226 */     doFormatStatusLine(result, statline);
/*     */     
/* 228 */     return result;
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
/*     */   protected void doFormatStatusLine(CharArrayBuffer buffer, StatusLine statline) {
/* 243 */     int len = estimateProtocolVersionLen(statline.getProtocolVersion()) + 1 + 3 + 1;
/*     */     
/* 245 */     String reason = statline.getReasonPhrase();
/* 246 */     if (reason != null) {
/* 247 */       len += reason.length();
/*     */     }
/* 249 */     buffer.ensureCapacity(len);
/*     */     
/* 251 */     appendProtocolVersion(buffer, statline.getProtocolVersion());
/* 252 */     buffer.append(' ');
/* 253 */     buffer.append(Integer.toString(statline.getStatusCode()));
/* 254 */     buffer.append(' ');
/* 255 */     if (reason != null) {
/* 256 */       buffer.append(reason);
/*     */     }
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
/*     */   public static String formatHeader(Header header, LineFormatter formatter) {
/* 273 */     return ((formatter != null) ? formatter : INSTANCE).formatHeader(null, header).toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharArrayBuffer formatHeader(CharArrayBuffer buffer, Header header) {
/*     */     CharArrayBuffer result;
/* 282 */     Args.notNull(header, "Header");
/*     */ 
/*     */     
/* 285 */     if (header instanceof FormattedHeader) {
/*     */       
/* 287 */       result = ((FormattedHeader)header).getBuffer();
/*     */     } else {
/* 289 */       result = initBuffer(buffer);
/* 290 */       doFormatHeader(result, header);
/*     */     } 
/* 292 */     return result;
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
/*     */   protected void doFormatHeader(CharArrayBuffer buffer, Header header) {
/* 307 */     String name = header.getName();
/* 308 */     String value = header.getValue();
/*     */     
/* 310 */     int len = name.length() + 2;
/* 311 */     if (value != null) {
/* 312 */       len += value.length();
/*     */     }
/* 314 */     buffer.ensureCapacity(len);
/*     */     
/* 316 */     buffer.append(name);
/* 317 */     buffer.append(": ");
/* 318 */     if (value != null) {
/* 319 */       buffer.ensureCapacity(buffer.length() + value.length());
/* 320 */       for (int valueIndex = 0; valueIndex < value.length(); valueIndex++) {
/* 321 */         char valueChar = value.charAt(valueIndex);
/* 322 */         if (valueChar == '\r' || valueChar == '\n' || valueChar == '\f' || valueChar == '\013')
/*     */         {
/*     */ 
/*     */           
/* 326 */           valueChar = ' ';
/*     */         }
/* 328 */         buffer.append(valueChar);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\message\BasicLineFormatter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */