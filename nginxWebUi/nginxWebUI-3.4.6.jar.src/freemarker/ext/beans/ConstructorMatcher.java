/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Member;
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
/*    */ final class ConstructorMatcher
/*    */   extends MemberMatcher<Constructor<?>, ExecutableMemberSignature>
/*    */ {
/*    */   protected ExecutableMemberSignature toMemberSignature(Constructor<?> member) {
/* 32 */     return new ExecutableMemberSignature(member);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean matchInUpperBoundTypeSubtypes() {
/* 37 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\ConstructorMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */