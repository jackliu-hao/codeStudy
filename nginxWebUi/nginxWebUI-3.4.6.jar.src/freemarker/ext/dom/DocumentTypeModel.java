/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateSequenceModel;
/*    */ import org.w3c.dom.DocumentType;
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
/*    */ 
/*    */ class DocumentTypeModel
/*    */   extends NodeModel
/*    */ {
/*    */   public DocumentTypeModel(DocumentType docType) {
/* 32 */     super(docType);
/*    */   }
/*    */   
/*    */   public String getAsString() {
/* 36 */     return ((ProcessingInstruction)this.node).getData();
/*    */   }
/*    */   
/*    */   public TemplateSequenceModel getChildren() throws TemplateModelException {
/* 40 */     throw new TemplateModelException("entering the child nodes of a DTD node is not currently supported");
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(String key) throws TemplateModelException {
/* 45 */     throw new TemplateModelException("accessing properties of a DTD is not currently supported");
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeName() {
/* 50 */     return "@document_type$" + this.node.getNodeName();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 55 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\DocumentTypeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */