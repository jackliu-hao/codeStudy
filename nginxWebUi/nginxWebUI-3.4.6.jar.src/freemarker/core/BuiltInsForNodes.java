/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.ext.dom._ExtDomApi;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateMethodModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template.TemplateNodeModelEx;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class BuiltInsForNodes
/*     */ {
/*     */   static class ancestorsBI
/*     */     extends BuiltInForNode
/*     */   {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  43 */       BuiltInsForNodes.AncestorSequence result = new BuiltInsForNodes.AncestorSequence(env);
/*  44 */       TemplateNodeModel parent = nodeModel.getParentNode();
/*  45 */       while (parent != null) {
/*  46 */         result.add(parent);
/*  47 */         parent = parent.getParentNode();
/*     */       } 
/*  49 */       return (TemplateModel)result;
/*     */     }
/*     */   }
/*     */   
/*     */   static class childrenBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  56 */       return (TemplateModel)nodeModel.getChildNodes();
/*     */     }
/*     */   }
/*     */   
/*     */   static class node_nameBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  63 */       return (TemplateModel)new SimpleScalar(nodeModel.getNodeName());
/*     */     }
/*     */   }
/*     */   
/*     */   static class node_namespaceBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  70 */       String nsURI = nodeModel.getNodeNamespace();
/*  71 */       return (nsURI == null) ? null : (TemplateModel)new SimpleScalar(nsURI);
/*     */     }
/*     */   }
/*     */   
/*     */   static class node_typeBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  78 */       return (TemplateModel)new SimpleScalar(nodeModel.getNodeType());
/*     */     }
/*     */   }
/*     */   
/*     */   static class parentBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  85 */       return (TemplateModel)nodeModel.getParentNode();
/*     */     }
/*     */   }
/*     */   
/*     */   static class rootBI
/*     */     extends BuiltInForNode {
/*     */     TemplateModel calculateResult(TemplateNodeModel nodeModel, Environment env) throws TemplateModelException {
/*  92 */       TemplateNodeModel result = nodeModel;
/*  93 */       TemplateNodeModel parent = nodeModel.getParentNode();
/*  94 */       while (parent != null) {
/*  95 */         result = parent;
/*  96 */         parent = result.getParentNode();
/*     */       } 
/*  98 */       return (TemplateModel)result;
/*     */     }
/*     */   }
/*     */   
/*     */   static class previousSiblingBI
/*     */     extends BuiltInForNodeEx {
/*     */     TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
/* 105 */       return (TemplateModel)nodeModel.getPreviousSibling();
/*     */     }
/*     */   }
/*     */   
/*     */   static class nextSiblingBI
/*     */     extends BuiltInForNodeEx {
/*     */     TemplateModel calculateResult(TemplateNodeModelEx nodeModel, Environment env) throws TemplateModelException {
/* 112 */       return (TemplateModel)nodeModel.getNextSibling();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class AncestorSequence
/*     */     extends SimpleSequence
/*     */     implements TemplateMethodModel
/*     */   {
/*     */     private Environment env;
/*     */ 
/*     */     
/*     */     AncestorSequence(Environment env) {
/* 126 */       super((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 127 */       this.env = env;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object exec(List<String> names) throws TemplateModelException {
/* 132 */       if (names == null || names.isEmpty()) {
/* 133 */         return this;
/*     */       }
/* 135 */       AncestorSequence result = new AncestorSequence(this.env);
/* 136 */       for (int i = 0; i < size(); i++) {
/* 137 */         TemplateNodeModel tnm = (TemplateNodeModel)get(i);
/* 138 */         String nodeName = tnm.getNodeName();
/* 139 */         String nsURI = tnm.getNodeNamespace();
/* 140 */         if (nsURI == null) {
/* 141 */           if (names.contains(nodeName)) {
/* 142 */             result.add(tnm);
/*     */           }
/*     */         } else {
/* 145 */           for (int j = 0; j < names.size(); j++) {
/* 146 */             if (_ExtDomApi.matchesName(names.get(j), nodeName, nsURI, this.env)) {
/* 147 */               result.add(tnm);
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 153 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForNodes.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */