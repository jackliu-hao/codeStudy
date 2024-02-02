/*    */ package org.noear.solon.data.cache;
/*    */ 
/*    */ import java.util.Properties;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LocalCacheFactoryImpl
/*    */   implements CacheFactory
/*    */ {
/*    */   public CacheService create(Properties props) {
/* 12 */     return new LocalCacheService(props);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\LocalCacheFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */