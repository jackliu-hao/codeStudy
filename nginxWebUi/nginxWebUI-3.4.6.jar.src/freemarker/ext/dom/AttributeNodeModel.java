/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ import freemarker.template.TemplateScalarModel;
/*    */ import org.w3c.dom.Attr;
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
/*    */ class AttributeNodeModel
/*    */   extends NodeModel
/*    */   implements TemplateScalarModel
/*    */ {
/*    */   public AttributeNodeModel(Attr att) {
/* 30 */     super(att);
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 35 */     return ((Attr)this.node).getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getNodeName() {
/* 40 */     String result = this.node.getLocalName();
/* 41 */     if (result == null || result.equals("")) {
/* 42 */       result = this.node.getNodeName();
/*    */     }
/* 44 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEmpty() {
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   String getQualifiedName() {
/* 54 */     String nsURI = this.node.getNamespaceURI();
/* 55 */     if (nsURI == null || nsURI.equals(""))
/* 56 */       return this.node.getNodeName(); 
/* 57 */     Environment env = Environment.getCurrentEnvironment();
/* 58 */     String defaultNS = env.getDefaultNS();
/* 59 */     String prefix = null;
/* 60 */     if (nsURI.equals(defaultNS)) {
/* 61 */       prefix = "D";
/*    */     } else {
/* 63 */       prefix = env.getPrefixForNamespace(nsURI);
/*    */     } 
/* 65 */     if (prefix == null) {
/* 66 */       return null;
/*    */     }
/* 68 */     return prefix + ":" + this.node.getLocalName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\AttributeNodeModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */