/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Field;
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
/*    */ final class AllowAllMemberAccessPolicy
/*    */   implements MemberAccessPolicy
/*    */ {
/* 27 */   public static final AllowAllMemberAccessPolicy INSTANCE = new AllowAllMemberAccessPolicy();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   public static final ClassMemberAccessPolicy CLASS_POLICY_INSTANCE = new ClassMemberAccessPolicy()
/*    */     {
/*    */       public boolean isMethodExposed(Method method) {
/* 35 */         return true;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isConstructorExposed(Constructor<?> constructor) {
/* 40 */         return true;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isFieldExposed(Field field) {
/* 45 */         return true;
/*    */       }
/*    */     };
/*    */ 
/*    */   
/*    */   public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
/* 51 */     return CLASS_POLICY_INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isToStringAlwaysExposed() {
/* 56 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\AllowAllMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */