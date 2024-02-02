/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Arrays;
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
/*    */ 
/*    */ 
/*    */ final class ExecutableMemberSignature
/*    */ {
/*    */   private final String name;
/*    */   private final Class<?>[] args;
/*    */   
/*    */   ExecutableMemberSignature(String name, Class<?>[] args) {
/* 38 */     this.name = name;
/* 39 */     this.args = args;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ExecutableMemberSignature(Method method) {
/* 46 */     this(method.getName(), method.getParameterTypes());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   ExecutableMemberSignature(Constructor<?> constructor) {
/* 53 */     this("<init>", constructor.getParameterTypes());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 58 */     if (o instanceof ExecutableMemberSignature) {
/* 59 */       ExecutableMemberSignature ms = (ExecutableMemberSignature)o;
/* 60 */       return (ms.name.equals(this.name) && Arrays.equals((Object[])this.args, (Object[])ms.args));
/*    */     } 
/* 62 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 67 */     return this.name.hashCode() + this.args.length * 31;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ExecutableMemberSignature.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */