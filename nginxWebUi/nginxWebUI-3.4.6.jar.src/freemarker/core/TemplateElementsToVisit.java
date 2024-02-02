/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
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
/*    */ class TemplateElementsToVisit
/*    */ {
/*    */   private final Collection<TemplateElement> templateElements;
/*    */   
/*    */   TemplateElementsToVisit(Collection<TemplateElement> templateElements) {
/* 37 */     this.templateElements = (null != templateElements) ? templateElements : Collections.<TemplateElement>emptyList();
/*    */   }
/*    */   
/*    */   TemplateElementsToVisit(TemplateElement nestedBlock) {
/* 41 */     this(Collections.singleton(nestedBlock));
/*    */   }
/*    */   
/*    */   Collection<TemplateElement> getTemplateElements() {
/* 45 */     return this.templateElements;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\TemplateElementsToVisit.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */