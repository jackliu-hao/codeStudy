/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Collection;
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
/*    */ public class BlacklistMemberAccessPolicy
/*    */   extends MemberSelectorListMemberAccessPolicy
/*    */ {
/*    */   private final boolean toStringAlwaysExposed;
/*    */   
/*    */   public BlacklistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
/* 50 */     super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.BLACKLIST, null);
/*    */     
/* 52 */     boolean toStringBlacklistedAnywhere = false;
/* 53 */     for (MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector : memberSelectors) {
/* 54 */       Method method = memberSelector.getMethod();
/* 55 */       if (method != null && method.getName().equals("toString") && (method.getParameterTypes()).length == 0) {
/* 56 */         toStringBlacklistedAnywhere = true;
/*    */         break;
/*    */       } 
/*    */     } 
/* 60 */     this.toStringAlwaysExposed = !toStringBlacklistedAnywhere;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isToStringAlwaysExposed() {
/* 65 */     return this.toStringAlwaysExposed;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\BlacklistMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */