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
/*    */ public class WhitelistMemberAccessPolicy
/*    */   extends MemberSelectorListMemberAccessPolicy
/*    */ {
/*    */   private static final Method TO_STRING_METHOD;
/*    */   private final boolean toStringAlwaysExposed;
/*    */   
/*    */   static {
/*    */     try {
/* 49 */       TO_STRING_METHOD = Object.class.getMethod("toString", new Class[0]);
/* 50 */     } catch (NoSuchMethodException e) {
/* 51 */       throw new IllegalStateException(e);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WhitelistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
/* 63 */     super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.WHITELIST, (Class)TemplateAccessible.class);
/* 64 */     this.toStringAlwaysExposed = forClass(Object.class).isMethodExposed(TO_STRING_METHOD);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isToStringAlwaysExposed() {
/* 69 */     return this.toStringAlwaysExposed;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\WhitelistMemberAccessPolicy.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */