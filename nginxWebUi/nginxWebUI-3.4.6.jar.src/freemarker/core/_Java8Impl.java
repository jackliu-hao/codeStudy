/*    */ package freemarker.core;
/*    */ 
/*    */ import java.lang.reflect.Method;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class _Java8Impl
/*    */   implements _Java8
/*    */ {
/* 31 */   public static final _Java8 INSTANCE = new _Java8Impl();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isDefaultMethod(Method method) {
/* 39 */     return method.isDefault();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_Java8Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */