/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class MixedContent
/*     */   extends TemplateElement
/*     */ {
/*     */   @Deprecated
/*     */   void addElement(TemplateElement element) {
/*  38 */     addChild(element);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   void addElement(int index, TemplateElement element) {
/*  46 */     addChild(index, element);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
/*  52 */     super.postParseCleanup(stripWhitespace);
/*  53 */     return (getChildCount() == 1) ? getChild(0) : this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  63 */     return getChildBuffer();
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  68 */     if (canonical) {
/*  69 */       return getChildrenCanonicalForm();
/*     */     }
/*  71 */     if (getParentElement() == null) {
/*  72 */       return "root";
/*     */     }
/*  74 */     return getNodeTypeSymbol();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isOutputCacheable() {
/*  80 */     int ln = getChildCount();
/*  81 */     for (int i = 0; i < ln; i++) {
/*  82 */       if (!getChild(i).isOutputCacheable()) {
/*  83 */         return false;
/*     */       }
/*     */     } 
/*  86 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  91 */     return "#mixed_content";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  96 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 101 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 106 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isIgnorable(boolean stripWhitespace) {
/* 111 */     return (getChildCount() == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 116 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\MixedContent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */