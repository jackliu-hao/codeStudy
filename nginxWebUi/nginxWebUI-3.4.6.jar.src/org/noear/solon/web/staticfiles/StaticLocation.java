/*    */ package org.noear.solon.web.staticfiles;
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
/*    */ public class StaticLocation
/*    */ {
/*    */   public final String pathPrefix;
/*    */   public final StaticRepository repository;
/*    */   public final boolean repositoryIncPrefix;
/*    */   
/*    */   public StaticLocation(String pathPrefix, StaticRepository repository, boolean repositoryIncPrefix) {
/* 27 */     this.pathPrefix = pathPrefix;
/* 28 */     this.repository = repository;
/* 29 */     this.repositoryIncPrefix = repositoryIncPrefix;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\StaticLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */