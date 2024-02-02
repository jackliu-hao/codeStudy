/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.template.TemplateScalarModel;
/*    */ import org.w3c.dom.ProcessingInstruction;
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
/*    */ class PINodeModel
/*    */   extends NodeModel
/*    */   implements TemplateScalarModel
/*    */ {
/*    */   public PINodeModel(ProcessingInstruction pi) {
/* 29 */     super(pi);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 34 */     return ((ProcessingInstruction)this.node).getData();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeName() {
/* 39 */     return "@pi$" + ((ProcessingInstruction)this.node).getTarget();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 44 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\PINodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */