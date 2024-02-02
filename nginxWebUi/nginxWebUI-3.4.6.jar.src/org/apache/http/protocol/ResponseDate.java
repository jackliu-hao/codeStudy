/*    */ package org.apache.http.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.HttpResponseInterceptor;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.util.Args;
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
/*    */ @Contract(threading = ThreadingBehavior.SAFE)
/*    */ public class ResponseDate
/*    */   implements HttpResponseInterceptor
/*    */ {
/* 50 */   private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
/* 59 */     Args.notNull(response, "HTTP response");
/* 60 */     int status = response.getStatusLine().getStatusCode();
/* 61 */     if (status >= 200 && !response.containsHeader("Date")) {
/*    */       
/* 63 */       String httpdate = DATE_GENERATOR.getCurrentDate();
/* 64 */       response.setHeader("Date", httpdate);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\protocol\ResponseDate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */