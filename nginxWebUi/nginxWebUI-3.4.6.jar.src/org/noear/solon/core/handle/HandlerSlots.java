/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.util.Set;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.annotation.Mapping;
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
/*    */ public interface HandlerSlots
/*    */ {
/*    */   default void before(String expr, MethodType method, int index, Handler handler) {}
/*    */   
/*    */   default void after(String expr, MethodType method, int index, Handler handler) {}
/*    */   
/*    */   void add(String paramString, MethodType paramMethodType, Handler paramHandler);
/*    */   
/*    */   default void add(Mapping mapping, Set<MethodType> methodTypes, Handler handler) {
/* 30 */     String path = Utils.annoAlias(mapping.value(), mapping.path());
/*    */     
/* 32 */     for (MethodType m1 : methodTypes) {
/* 33 */       if (mapping.after() || mapping.before()) {
/* 34 */         if (mapping.after()) {
/* 35 */           after(path, m1, mapping.index(), handler); continue;
/*    */         } 
/* 37 */         before(path, m1, mapping.index(), handler);
/*    */         continue;
/*    */       } 
/* 40 */       add(path, m1, handler);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\HandlerSlots.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */