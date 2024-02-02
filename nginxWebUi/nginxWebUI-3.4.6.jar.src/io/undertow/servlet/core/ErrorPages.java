/*    */ package io.undertow.servlet.core;
/*    */ 
/*    */ import java.util.Map;
/*    */ import javax.servlet.ServletException;
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
/*    */ public class ErrorPages
/*    */ {
/*    */   private final Map<Integer, String> errorCodeLocations;
/*    */   private final Map<Class<? extends Throwable>, String> exceptionMappings;
/*    */   private final String defaultErrorPage;
/*    */   
/*    */   public ErrorPages(Map<Integer, String> errorCodeLocations, Map<Class<? extends Throwable>, String> exceptionMappings, String defaultErrorPage) {
/* 39 */     this.errorCodeLocations = errorCodeLocations;
/* 40 */     this.exceptionMappings = exceptionMappings;
/* 41 */     this.defaultErrorPage = defaultErrorPage;
/*    */   }
/*    */   
/*    */   public String getErrorLocation(int code) {
/* 45 */     String location = this.errorCodeLocations.get(Integer.valueOf(code));
/* 46 */     if (location == null) {
/* 47 */       return this.defaultErrorPage;
/*    */     }
/* 49 */     return location;
/*    */   }
/*    */   
/*    */   public String getErrorLocation(Throwable exception) {
/* 53 */     if (exception == null) {
/* 54 */       return null;
/*    */     }
/*    */     
/* 57 */     String location = null;
/* 58 */     for (Class<?> c = exception.getClass(); c != null && location == null; c = c.getSuperclass()) {
/* 59 */       location = this.exceptionMappings.get(c);
/*    */     }
/* 61 */     if (location == null && exception instanceof ServletException) {
/* 62 */       Throwable rootCause = ((ServletException)exception).getRootCause();
/*    */       
/* 64 */       while (rootCause != null && rootCause instanceof ServletException && location == null) {
/* 65 */         for (Class<?> clazz = rootCause.getClass(); clazz != null && location == null; clazz = clazz.getSuperclass()) {
/* 66 */           location = this.exceptionMappings.get(clazz);
/*    */         }
/* 68 */         rootCause = ((ServletException)rootCause).getRootCause();
/*    */       } 
/* 70 */       if (rootCause != null && location == null) {
/* 71 */         for (Class<?> clazz = rootCause.getClass(); clazz != null && location == null; clazz = clazz.getSuperclass()) {
/* 72 */           location = this.exceptionMappings.get(clazz);
/*    */         }
/*    */       }
/*    */     } 
/* 76 */     if (location == null) {
/* 77 */       location = getErrorLocation(500);
/*    */     }
/* 79 */     return location;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ErrorPages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */