/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.utility.StringUtil;
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
/*    */ @Deprecated
/*    */ public final class Comment
/*    */   extends TemplateElement
/*    */ {
/*    */   private final String text;
/*    */   
/*    */   Comment(String text) {
/* 36 */     this.text = text;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) {
/* 42 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 47 */     if (canonical) {
/* 48 */       return "<#--" + this.text + "-->";
/*    */     }
/* 50 */     return "comment " + StringUtil.jQuote(this.text.trim());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 56 */     return "#--...--";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 62 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 67 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 68 */     return this.text;
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 73 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 74 */     return ParameterRole.CONTENT;
/*    */   }
/*    */   
/*    */   public String getText() {
/* 78 */     return this.text;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isOutputCacheable() {
/* 83 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 88 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Comment.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */