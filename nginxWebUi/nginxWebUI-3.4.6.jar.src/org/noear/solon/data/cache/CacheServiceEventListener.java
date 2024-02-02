/*    */ package org.noear.solon.data.cache;
/*    */ 
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.BeanWrap;
/*    */ import org.noear.solon.core.event.EventListener;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CacheServiceEventListener
/*    */   implements EventListener<BeanWrap>
/*    */ {
/*    */   public void onEvent(BeanWrap bw) {
/* 16 */     if (bw.raw() instanceof CacheService)
/* 17 */       if (Utils.isEmpty(bw.name())) {
/* 18 */         CacheLib.cacheServiceAdd("", (CacheService)bw.raw());
/*    */       } else {
/* 20 */         CacheLib.cacheServiceAddIfAbsent(bw.name(), (CacheService)bw.raw());
/*    */         
/* 22 */         if (bw.typed())
/* 23 */           CacheLib.cacheServiceAdd("", (CacheService)bw.raw()); 
/*    */       }  
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\data\cache\CacheServiceEventListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */