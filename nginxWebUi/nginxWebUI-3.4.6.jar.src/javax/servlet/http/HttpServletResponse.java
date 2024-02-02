/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.servlet.ServletResponse;
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
/*     */ public interface HttpServletResponse
/*     */   extends ServletResponse
/*     */ {
/*     */   public static final int SC_CONTINUE = 100;
/*     */   public static final int SC_SWITCHING_PROTOCOLS = 101;
/*     */   public static final int SC_OK = 200;
/*     */   public static final int SC_CREATED = 201;
/*     */   public static final int SC_ACCEPTED = 202;
/*     */   public static final int SC_NON_AUTHORITATIVE_INFORMATION = 203;
/*     */   public static final int SC_NO_CONTENT = 204;
/*     */   public static final int SC_RESET_CONTENT = 205;
/*     */   public static final int SC_PARTIAL_CONTENT = 206;
/*     */   public static final int SC_MULTIPLE_CHOICES = 300;
/*     */   public static final int SC_MOVED_PERMANENTLY = 301;
/*     */   public static final int SC_MOVED_TEMPORARILY = 302;
/*     */   public static final int SC_FOUND = 302;
/*     */   public static final int SC_SEE_OTHER = 303;
/*     */   public static final int SC_NOT_MODIFIED = 304;
/*     */   public static final int SC_USE_PROXY = 305;
/*     */   public static final int SC_TEMPORARY_REDIRECT = 307;
/*     */   public static final int SC_BAD_REQUEST = 400;
/*     */   public static final int SC_UNAUTHORIZED = 401;
/*     */   public static final int SC_PAYMENT_REQUIRED = 402;
/*     */   public static final int SC_FORBIDDEN = 403;
/*     */   public static final int SC_NOT_FOUND = 404;
/*     */   public static final int SC_METHOD_NOT_ALLOWED = 405;
/*     */   public static final int SC_NOT_ACCEPTABLE = 406;
/*     */   public static final int SC_PROXY_AUTHENTICATION_REQUIRED = 407;
/*     */   public static final int SC_REQUEST_TIMEOUT = 408;
/*     */   public static final int SC_CONFLICT = 409;
/*     */   public static final int SC_GONE = 410;
/*     */   public static final int SC_LENGTH_REQUIRED = 411;
/*     */   public static final int SC_PRECONDITION_FAILED = 412;
/*     */   public static final int SC_REQUEST_ENTITY_TOO_LARGE = 413;
/*     */   public static final int SC_REQUEST_URI_TOO_LONG = 414;
/*     */   public static final int SC_UNSUPPORTED_MEDIA_TYPE = 415;
/*     */   public static final int SC_REQUESTED_RANGE_NOT_SATISFIABLE = 416;
/*     */   public static final int SC_EXPECTATION_FAILED = 417;
/*     */   public static final int SC_INTERNAL_SERVER_ERROR = 500;
/*     */   public static final int SC_NOT_IMPLEMENTED = 501;
/*     */   public static final int SC_BAD_GATEWAY = 502;
/*     */   public static final int SC_SERVICE_UNAVAILABLE = 503;
/*     */   public static final int SC_GATEWAY_TIMEOUT = 504;
/*     */   public static final int SC_HTTP_VERSION_NOT_SUPPORTED = 505;
/*     */   
/*     */   void addCookie(Cookie paramCookie);
/*     */   
/*     */   boolean containsHeader(String paramString);
/*     */   
/*     */   String encodeURL(String paramString);
/*     */   
/*     */   String encodeRedirectURL(String paramString);
/*     */   
/*     */   @Deprecated
/*     */   String encodeUrl(String paramString);
/*     */   
/*     */   @Deprecated
/*     */   String encodeRedirectUrl(String paramString);
/*     */   
/*     */   void sendError(int paramInt, String paramString) throws IOException;
/*     */   
/*     */   void sendError(int paramInt) throws IOException;
/*     */   
/*     */   void sendRedirect(String paramString) throws IOException;
/*     */   
/*     */   void setDateHeader(String paramString, long paramLong);
/*     */   
/*     */   void addDateHeader(String paramString, long paramLong);
/*     */   
/*     */   void setHeader(String paramString1, String paramString2);
/*     */   
/*     */   void addHeader(String paramString1, String paramString2);
/*     */   
/*     */   void setIntHeader(String paramString, int paramInt);
/*     */   
/*     */   void addIntHeader(String paramString, int paramInt);
/*     */   
/*     */   void setStatus(int paramInt);
/*     */   
/*     */   @Deprecated
/*     */   void setStatus(int paramInt, String paramString);
/*     */   
/*     */   int getStatus();
/*     */   
/*     */   String getHeader(String paramString);
/*     */   
/*     */   Collection<String> getHeaders(String paramString);
/*     */   
/*     */   Collection<String> getHeaderNames();
/*     */   
/*     */   default void setTrailerFields(Supplier<Map<String, String>> supplier) {}
/*     */   
/*     */   default Supplier<Map<String, String>> getTrailerFields() {
/* 464 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpServletResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */