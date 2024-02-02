/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.TemplateHashModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.NodeList;
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
/*    */ class DocumentModel
/*    */   extends NodeModel
/*    */   implements TemplateHashModel
/*    */ {
/*    */   private ElementModel rootElement;
/*    */   
/*    */   DocumentModel(Document doc) {
/* 40 */     super(doc);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeName() {
/* 45 */     return "@document";
/*    */   }
/*    */ 
/*    */   
/*    */   public TemplateModel get(String key) throws TemplateModelException {
/* 50 */     if (key.equals("*"))
/* 51 */       return (TemplateModel)getRootElement(); 
/* 52 */     if (key.equals("**")) {
/* 53 */       NodeList nl = ((Document)this.node).getElementsByTagName("*");
/* 54 */       return (TemplateModel)new NodeListModel(nl, this);
/* 55 */     }  if (DomStringUtil.isXMLNameLike(key)) {
/* 56 */       ElementModel em = (ElementModel)NodeModel.wrap(((Document)this.node).getDocumentElement());
/* 57 */       if (em.matchesName(key, Environment.getCurrentEnvironment())) {
/* 58 */         return (TemplateModel)em;
/*    */       }
/* 60 */       return (TemplateModel)new NodeListModel(this);
/*    */     } 
/*    */     
/* 63 */     return super.get(key);
/*    */   }
/*    */   
/*    */   ElementModel getRootElement() {
/* 67 */     if (this.rootElement == null) {
/* 68 */       this.rootElement = (ElementModel)wrap(((Document)this.node).getDocumentElement());
/*    */     }
/* 70 */     return this.rootElement;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 75 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\DocumentModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */