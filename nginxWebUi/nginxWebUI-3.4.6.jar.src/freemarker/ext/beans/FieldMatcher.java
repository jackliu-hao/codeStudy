/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Field;
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
/*    */ final class FieldMatcher
/*    */   extends MemberMatcher<Field, String>
/*    */ {
/*    */   protected String toMemberSignature(Field member) {
/* 32 */     return member.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean matchInUpperBoundTypeSubtypes() {
/* 37 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\FieldMatcher.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */