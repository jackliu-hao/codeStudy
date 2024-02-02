/*    */ package io.undertow.servlet.spec;
/*    */ 
/*    */ import javax.servlet.http.HttpServletMapping;
/*    */ import javax.servlet.http.MappingMatch;
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
/*    */ public class MappingImpl
/*    */   implements HttpServletMapping
/*    */ {
/*    */   private final String matchValue;
/*    */   private final String pattern;
/*    */   private final MappingMatch matchType;
/*    */   private final String servletName;
/*    */   
/*    */   public MappingImpl(String matchValue, String pattern, MappingMatch matchType, String servletName) {
/* 35 */     this.matchValue = matchValue;
/* 36 */     this.pattern = pattern;
/* 37 */     this.matchType = matchType;
/* 38 */     this.servletName = servletName;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMatchValue() {
/* 43 */     return this.matchValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getPattern() {
/* 48 */     return this.pattern;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getServletName() {
/* 53 */     return this.servletName;
/*    */   }
/*    */ 
/*    */   
/*    */   public MappingMatch getMappingMatch() {
/* 58 */     return this.matchType;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\MappingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */