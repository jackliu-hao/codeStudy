/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
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
/*    */ public class WebResourceCollection
/*    */   implements Cloneable
/*    */ {
/* 31 */   private final Set<String> httpMethods = new HashSet<>();
/* 32 */   private final Set<String> httpMethodOmissions = new HashSet<>();
/* 33 */   private final Set<String> urlPatterns = new HashSet<>();
/*    */   
/*    */   public WebResourceCollection addHttpMethod(String s) {
/* 36 */     this.httpMethods.add(s);
/* 37 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addHttpMethods(String... s) {
/* 41 */     this.httpMethods.addAll(Arrays.asList(s));
/* 42 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addHttpMethods(Collection<String> s) {
/* 46 */     this.httpMethods.addAll(s);
/* 47 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addUrlPattern(String s) {
/* 51 */     this.urlPatterns.add(s);
/* 52 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addUrlPatterns(String... s) {
/* 56 */     this.urlPatterns.addAll(Arrays.asList(s));
/* 57 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addUrlPatterns(Collection<String> s) {
/* 61 */     this.urlPatterns.addAll(s);
/* 62 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addHttpMethodOmission(String s) {
/* 66 */     this.httpMethodOmissions.add(s);
/* 67 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addHttpMethodOmissions(String... s) {
/* 71 */     this.httpMethodOmissions.addAll(Arrays.asList(s));
/* 72 */     return this;
/*    */   }
/*    */   
/*    */   public WebResourceCollection addHttpMethodOmissions(Collection<String> s) {
/* 76 */     this.httpMethodOmissions.addAll(s);
/* 77 */     return this;
/*    */   }
/*    */   
/*    */   public Set<String> getHttpMethodOmissions() {
/* 81 */     return this.httpMethodOmissions;
/*    */   }
/*    */   
/*    */   public Set<String> getUrlPatterns() {
/* 85 */     return this.urlPatterns;
/*    */   }
/*    */   
/*    */   public Set<String> getHttpMethods() {
/* 89 */     return this.httpMethods;
/*    */   }
/*    */ 
/*    */   
/*    */   protected WebResourceCollection clone() {
/* 94 */     return (new WebResourceCollection())
/* 95 */       .addHttpMethodOmissions(this.httpMethodOmissions)
/* 96 */       .addHttpMethods(this.httpMethods)
/* 97 */       .addUrlPatterns(this.urlPatterns);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\WebResourceCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */