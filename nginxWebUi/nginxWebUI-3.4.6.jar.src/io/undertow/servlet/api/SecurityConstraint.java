/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
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
/*    */ public class SecurityConstraint
/*    */   extends SecurityInfo<SecurityConstraint>
/*    */ {
/* 32 */   private final Set<WebResourceCollection> webResourceCollections = new HashSet<>();
/*    */   
/*    */   public Set<WebResourceCollection> getWebResourceCollections() {
/* 35 */     return Collections.unmodifiableSet(this.webResourceCollections);
/*    */   }
/*    */   
/*    */   public SecurityConstraint addWebResourceCollection(WebResourceCollection webResourceCollection) {
/* 39 */     this.webResourceCollections.add(webResourceCollection);
/* 40 */     return this;
/*    */   }
/*    */   
/*    */   public SecurityConstraint addWebResourceCollections(WebResourceCollection... webResourceCollection) {
/* 44 */     this.webResourceCollections.addAll(Arrays.asList(webResourceCollection));
/* 45 */     return this;
/*    */   }
/*    */   
/*    */   public SecurityConstraint addWebResourceCollections(List<WebResourceCollection> webResourceCollections) {
/* 49 */     this.webResourceCollections.addAll(webResourceCollections);
/* 50 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SecurityConstraint createInstance() {
/* 55 */     return new SecurityConstraint();
/*    */   }
/*    */ 
/*    */   
/*    */   public SecurityConstraint clone() {
/* 60 */     SecurityConstraint info = super.clone();
/* 61 */     for (WebResourceCollection wr : this.webResourceCollections) {
/* 62 */       info.addWebResourceCollection(wr.clone());
/*    */     }
/* 64 */     return info;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\SecurityConstraint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */