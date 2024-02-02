/*    */ package org.noear.solon.core.handle;
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
/*    */ public interface Render
/*    */ {
/*    */   default String getName() {
/* 15 */     return getClass().getSimpleName();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default String renderAndReturn(Object data, Context ctx) throws Throwable {
/* 22 */     return null;
/*    */   }
/*    */   
/*    */   void render(Object paramObject, Context paramContext) throws Throwable;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\Render.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */