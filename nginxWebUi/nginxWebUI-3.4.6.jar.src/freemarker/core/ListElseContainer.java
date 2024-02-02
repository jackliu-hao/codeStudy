/*    */ package freemarker.core;
/*    */ 
/*    */ import freemarker.template.TemplateException;
/*    */ import java.io.IOException;
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
/*    */ class ListElseContainer
/*    */   extends TemplateElement
/*    */ {
/*    */   private final IteratorBlock listPart;
/*    */   private final ElseOfList elsePart;
/*    */   
/*    */   public ListElseContainer(IteratorBlock listPart, ElseOfList elsePart) {
/* 31 */     setChildBufferCapacity(2);
/* 32 */     addChild(listPart);
/* 33 */     addChild(elsePart);
/* 34 */     this.listPart = listPart;
/* 35 */     this.elsePart = elsePart;
/*    */   }
/*    */ 
/*    */   
/*    */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/* 40 */     if (!this.listPart.acceptWithResult(env)) {
/* 41 */       return this.elsePart.accept(env);
/*    */     }
/* 43 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   boolean isNestedBlockRepeater() {
/* 48 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String dump(boolean canonical) {
/* 53 */     if (canonical) {
/* 54 */       StringBuilder buf = new StringBuilder();
/* 55 */       int ln = getChildCount();
/* 56 */       for (int i = 0; i < ln; i++) {
/* 57 */         TemplateElement element = getChild(i);
/* 58 */         buf.append(element.dump(canonical));
/*    */       } 
/* 60 */       buf.append("</#list>");
/* 61 */       return buf.toString();
/*    */     } 
/* 63 */     return getNodeTypeSymbol();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   String getNodeTypeSymbol() {
/* 69 */     return "#list-#else-container";
/*    */   }
/*    */ 
/*    */   
/*    */   int getParameterCount() {
/* 74 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   Object getParameterValue(int idx) {
/* 79 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ 
/*    */   
/*    */   ParameterRole getParameterRole(int idx) {
/* 84 */     throw new IndexOutOfBoundsException();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\ListElseContainer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */