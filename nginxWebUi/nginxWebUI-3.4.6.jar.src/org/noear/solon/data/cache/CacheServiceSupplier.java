/*    */ package org.noear.solon.data.cache;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheServiceSupplier
/*    */   implements Supplier<CacheService>
/*    */ {
/*    */   private CacheService real;
/*    */   private String driverType;
/*    */   
/*    */   public CacheServiceSupplier(Properties props) {
/* 17 */     this.driverType = props.getProperty("driverType");
/*    */     
/* 19 */     CacheFactory factory = CacheLib.cacheFactoryGet(this.driverType);
/*    */     
/* 21 */     if (factory != null) {
/* 22 */       this.real = factory.create(props);
/*    */     } else {
/* 24 */       throw new IllegalArgumentException("There is no supported driverType");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public CacheService get() {
/* 30 */     return this.real;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheServiceSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */