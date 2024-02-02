/*    */ package freemarker.ext.xml;
/*    */ 
/*    */ import freemarker.template.TemplateMethodModel;
/*    */ import freemarker.template.TemplateModelException;
/*    */ import freemarker.template.TemplateScalarModel;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
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
/*    */ class Namespaces
/*    */   implements TemplateMethodModel, Cloneable
/*    */ {
/*    */   private HashMap namespaces;
/*    */   private boolean shared;
/*    */   
/*    */   Namespaces() {
/* 38 */     this.namespaces = new HashMap<>();
/* 39 */     this.namespaces.put("", "");
/* 40 */     this.namespaces.put("xml", "http://www.w3.org/XML/1998/namespace");
/* 41 */     this.shared = false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object clone() {
/*    */     try {
/* 47 */       Namespaces clone = (Namespaces)super.clone();
/* 48 */       clone.namespaces = (HashMap)this.namespaces.clone();
/* 49 */       clone.shared = false;
/* 50 */       return clone;
/* 51 */     } catch (CloneNotSupportedException e) {
/* 52 */       throw new Error();
/*    */     } 
/*    */   }
/*    */   
/*    */   public String translateNamespacePrefixToUri(String prefix) {
/* 57 */     synchronized (this.namespaces) {
/* 58 */       return (String)this.namespaces.get(prefix);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Object exec(List<String> arguments) throws TemplateModelException {
/* 64 */     if (arguments.size() != 2) {
/* 65 */       throw new TemplateModelException("_registerNamespace(prefix, uri) requires two arguments");
/*    */     }
/* 67 */     registerNamespace(arguments.get(0), arguments.get(1));
/* 68 */     return TemplateScalarModel.EMPTY_STRING;
/*    */   }
/*    */   
/*    */   void registerNamespace(String prefix, String uri) {
/* 72 */     synchronized (this.namespaces) {
/* 73 */       this.namespaces.put(prefix, uri);
/*    */     } 
/*    */   }
/*    */   
/*    */   void markShared() {
/* 78 */     if (!this.shared) {
/* 79 */       this.shared = true;
/*    */     }
/*    */   }
/*    */   
/*    */   boolean isShared() {
/* 84 */     return this.shared;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\xml\Namespaces.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */