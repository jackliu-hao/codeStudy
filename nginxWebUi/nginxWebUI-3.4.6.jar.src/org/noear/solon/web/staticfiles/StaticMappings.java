/*    */ package org.noear.solon.web.staticfiles;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticMappings
/*    */ {
/* 14 */   static final Map<StaticRepository, StaticLocation> locationMap = new HashMap<>();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int count() {
/* 20 */     return locationMap.size();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized void add(String pathPrefix, StaticRepository repository) {
/* 30 */     add(pathPrefix, true, repository);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized void add(String pathPrefix, boolean repositoryIncPrefix, StaticRepository repository) {
/* 41 */     if (!pathPrefix.startsWith("/")) {
/* 42 */       pathPrefix = "/" + pathPrefix;
/*    */     }
/*    */     
/* 45 */     if (!pathPrefix.endsWith("/")) {
/* 46 */       pathPrefix = pathPrefix + "/";
/*    */     }
/*    */     
/* 49 */     locationMap.putIfAbsent(repository, new StaticLocation(pathPrefix, repository, repositoryIncPrefix));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static synchronized void remove(StaticRepository repository) {
/* 56 */     locationMap.remove(repository);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static URL find(String path) throws Exception {
/* 63 */     URL rst = null;
/*    */     
/* 65 */     for (StaticLocation m : locationMap.values()) {
/* 66 */       if (path.startsWith(m.pathPrefix)) {
/* 67 */         if (m.repositoryIncPrefix) {
/* 68 */           rst = m.repository.find(path);
/*    */         } else {
/* 70 */           rst = m.repository.find(path.substring(m.pathPrefix.length()));
/*    */         } 
/*    */         
/* 73 */         if (rst != null) {
/* 74 */           return rst;
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 79 */     return rst;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\staticfiles\StaticMappings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */