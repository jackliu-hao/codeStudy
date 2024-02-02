/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Member;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ final class MethodMatcher
/*    */   extends MemberMatcher<Method, ExecutableMemberSignature>
/*    */ {
/*    */   protected ExecutableMemberSignature toMemberSignature(Method member) {
/* 36 */     return new ExecutableMemberSignature(member);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean matchInUpperBoundTypeSubtypes() {
/* 41 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\MethodMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */