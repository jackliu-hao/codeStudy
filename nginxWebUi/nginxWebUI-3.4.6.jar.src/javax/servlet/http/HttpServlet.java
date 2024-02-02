/*     */ package javax.servlet.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Enumeration;
/*     */ import java.util.ResourceBundle;
/*     */ import javax.servlet.GenericServlet;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.ServletRequest;
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
/*     */ public abstract class HttpServlet
/*     */   extends GenericServlet
/*     */ {
/*     */   private static final String METHOD_DELETE = "DELETE";
/*     */   private static final String METHOD_HEAD = "HEAD";
/*     */   private static final String METHOD_GET = "GET";
/*     */   private static final String METHOD_OPTIONS = "OPTIONS";
/*     */   private static final String METHOD_POST = "POST";
/*     */   private static final String METHOD_PUT = "PUT";
/*     */   private static final String METHOD_TRACE = "TRACE";
/*     */   private static final String HEADER_IFMODSINCE = "If-Modified-Since";
/*     */   private static final String HEADER_LASTMOD = "Last-Modified";
/*     */   private static final String LSTRING_FILE = "javax.servlet.http.LocalStrings";
/*  90 */   private static ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 170 */     String protocol = req.getProtocol();
/* 171 */     String msg = lStrings.getString("http.method_get_not_supported");
/* 172 */     if (protocol.endsWith("1.1")) {
/* 173 */       resp.sendError(405, msg);
/*     */     } else {
/* 175 */       resp.sendError(400, msg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected long getLastModified(HttpServletRequest req) {
/* 204 */     return -1L;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 244 */     NoBodyResponse response = new NoBodyResponse(resp);
/*     */     
/* 246 */     doGet(req, response);
/* 247 */     response.setContentLength();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 312 */     String protocol = req.getProtocol();
/* 313 */     String msg = lStrings.getString("http.method_post_not_supported");
/* 314 */     if (protocol.endsWith("1.1")) {
/* 315 */       resp.sendError(405, msg);
/*     */     } else {
/* 317 */       resp.sendError(400, msg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 367 */     String protocol = req.getProtocol();
/* 368 */     String msg = lStrings.getString("http.method_put_not_supported");
/* 369 */     if (protocol.endsWith("1.1")) {
/* 370 */       resp.sendError(405, msg);
/*     */     } else {
/* 372 */       resp.sendError(400, msg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 414 */     String protocol = req.getProtocol();
/* 415 */     String msg = lStrings.getString("http.method_delete_not_supported");
/* 416 */     if (protocol.endsWith("1.1")) {
/* 417 */       resp.sendError(405, msg);
/*     */     } else {
/* 419 */       resp.sendError(400, msg);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Method[] getAllDeclaredMethods(Class<? extends HttpServlet> c) {
/* 426 */     Class<?> clazz = c;
/* 427 */     Method[] allMethods = null;
/*     */     
/* 429 */     while (!clazz.equals(HttpServlet.class)) {
/* 430 */       Method[] thisMethods = clazz.getDeclaredMethods();
/* 431 */       if (allMethods != null && allMethods.length > 0) {
/* 432 */         Method[] subClassMethods = allMethods;
/* 433 */         allMethods = new Method[thisMethods.length + subClassMethods.length];
/*     */         
/* 435 */         System.arraycopy(thisMethods, 0, allMethods, 0, thisMethods.length);
/*     */         
/* 437 */         System.arraycopy(subClassMethods, 0, allMethods, thisMethods.length, subClassMethods.length);
/*     */       } else {
/*     */         
/* 440 */         allMethods = thisMethods;
/*     */       } 
/*     */       
/* 443 */       clazz = clazz.getSuperclass();
/*     */     } 
/*     */     
/* 446 */     return (allMethods != null) ? allMethods : new Method[0];
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 484 */     Method[] methods = getAllDeclaredMethods((Class)getClass());
/*     */     
/* 486 */     boolean ALLOW_GET = false;
/* 487 */     boolean ALLOW_HEAD = false;
/* 488 */     boolean ALLOW_POST = false;
/* 489 */     boolean ALLOW_PUT = false;
/* 490 */     boolean ALLOW_DELETE = false;
/* 491 */     boolean ALLOW_TRACE = true;
/* 492 */     boolean ALLOW_OPTIONS = true;
/*     */     
/* 494 */     for (int i = 0; i < methods.length; i++) {
/* 495 */       String methodName = methods[i].getName();
/*     */       
/* 497 */       if (methodName.equals("doGet")) {
/* 498 */         ALLOW_GET = true;
/* 499 */         ALLOW_HEAD = true;
/* 500 */       } else if (methodName.equals("doPost")) {
/* 501 */         ALLOW_POST = true;
/* 502 */       } else if (methodName.equals("doPut")) {
/* 503 */         ALLOW_PUT = true;
/* 504 */       } else if (methodName.equals("doDelete")) {
/* 505 */         ALLOW_DELETE = true;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 512 */     StringBuilder allow = new StringBuilder();
/* 513 */     if (ALLOW_GET) {
/* 514 */       allow.append("GET");
/*     */     }
/* 516 */     if (ALLOW_HEAD) {
/* 517 */       if (allow.length() > 0) {
/* 518 */         allow.append(", ");
/*     */       }
/* 520 */       allow.append("HEAD");
/*     */     } 
/* 522 */     if (ALLOW_POST) {
/* 523 */       if (allow.length() > 0) {
/* 524 */         allow.append(", ");
/*     */       }
/* 526 */       allow.append("POST");
/*     */     } 
/* 528 */     if (ALLOW_PUT) {
/* 529 */       if (allow.length() > 0) {
/* 530 */         allow.append(", ");
/*     */       }
/* 532 */       allow.append("PUT");
/*     */     } 
/* 534 */     if (ALLOW_DELETE) {
/* 535 */       if (allow.length() > 0) {
/* 536 */         allow.append(", ");
/*     */       }
/* 538 */       allow.append("DELETE");
/*     */     } 
/* 540 */     if (ALLOW_TRACE) {
/* 541 */       if (allow.length() > 0) {
/* 542 */         allow.append(", ");
/*     */       }
/* 544 */       allow.append("TRACE");
/*     */     } 
/* 546 */     if (ALLOW_OPTIONS) {
/* 547 */       if (allow.length() > 0) {
/* 548 */         allow.append(", ");
/*     */       }
/* 550 */       allow.append("OPTIONS");
/*     */     } 
/*     */     
/* 553 */     resp.setHeader("Allow", allow.toString());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 587 */     String CRLF = "\r\n";
/*     */     
/* 589 */     StringBuilder buffer = (new StringBuilder("TRACE ")).append(req.getRequestURI()).append(" ").append(req.getProtocol());
/*     */     
/* 591 */     Enumeration<String> reqHeaderEnum = req.getHeaderNames();
/*     */     
/* 593 */     while (reqHeaderEnum.hasMoreElements()) {
/* 594 */       String headerName = reqHeaderEnum.nextElement();
/* 595 */       buffer.append(CRLF).append(headerName).append(": ")
/* 596 */         .append(req.getHeader(headerName));
/*     */     } 
/*     */     
/* 599 */     buffer.append(CRLF);
/*     */     
/* 601 */     int responseLength = buffer.length();
/*     */     
/* 603 */     resp.setContentType("message/http");
/* 604 */     resp.setContentLength(responseLength);
/* 605 */     ServletOutputStream out = resp.getOutputStream();
/* 606 */     out.print(buffer.toString());
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/* 638 */     String method = req.getMethod();
/*     */     
/* 640 */     if (method.equals("GET")) {
/* 641 */       long lastModified = getLastModified(req);
/* 642 */       if (lastModified == -1L) {
/*     */ 
/*     */         
/* 645 */         doGet(req, resp);
/*     */       } else {
/* 647 */         long ifModifiedSince = req.getDateHeader("If-Modified-Since");
/* 648 */         if (ifModifiedSince < lastModified) {
/*     */ 
/*     */ 
/*     */           
/* 652 */           maybeSetLastModified(resp, lastModified);
/* 653 */           doGet(req, resp);
/*     */         } else {
/* 655 */           resp.setStatus(304);
/*     */         }
/*     */       
/*     */       } 
/* 659 */     } else if (method.equals("HEAD")) {
/* 660 */       long lastModified = getLastModified(req);
/* 661 */       maybeSetLastModified(resp, lastModified);
/* 662 */       doHead(req, resp);
/*     */     }
/* 664 */     else if (method.equals("POST")) {
/* 665 */       doPost(req, resp);
/*     */     }
/* 667 */     else if (method.equals("PUT")) {
/* 668 */       doPut(req, resp);
/*     */     }
/* 670 */     else if (method.equals("DELETE")) {
/* 671 */       doDelete(req, resp);
/*     */     }
/* 673 */     else if (method.equals("OPTIONS")) {
/* 674 */       doOptions(req, resp);
/*     */     }
/* 676 */     else if (method.equals("TRACE")) {
/* 677 */       doTrace(req, resp);
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */ 
/*     */       
/* 685 */       String errMsg = lStrings.getString("http.method_not_implemented");
/* 686 */       Object[] errArgs = new Object[1];
/* 687 */       errArgs[0] = method;
/* 688 */       errMsg = MessageFormat.format(errMsg, errArgs);
/*     */       
/* 690 */       resp.sendError(501, errMsg);
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
/*     */   private void maybeSetLastModified(HttpServletResponse resp, long lastModified) {
/* 704 */     if (resp.containsHeader("Last-Modified"))
/*     */       return; 
/* 706 */     if (lastModified >= 0L) {
/* 707 */       resp.setDateHeader("Last-Modified", lastModified);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
/* 742 */     if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse))
/*     */     {
/* 744 */       throw new ServletException("non-HTTP request or response");
/*     */     }
/*     */     
/* 747 */     HttpServletRequest request = (HttpServletRequest)req;
/* 748 */     HttpServletResponse response = (HttpServletResponse)res;
/*     */     
/* 750 */     service(request, response);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpServlet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */