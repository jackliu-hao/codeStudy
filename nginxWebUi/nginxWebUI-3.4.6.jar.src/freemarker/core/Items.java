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
/*     */ class Items
/*     */   extends TemplateElement
/*     */ {
/*     */   private final String loopVarName;
/*     */   private final String loopVar2Name;
/*     */   
/*     */   Items(String loopVarName, String loopVar2Name, TemplateElements children) {
/*  40 */     this.loopVarName = loopVarName;
/*  41 */     this.loopVar2Name = loopVar2Name;
/*  42 */     setChildren(children);
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  47 */     IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
/*  48 */     if (iterCtx == null)
/*     */     {
/*  50 */       throw new _MiscTemplateException(env, new Object[] {
/*  51 */             getNodeTypeSymbol(), " without iteration in context"
/*     */           });
/*     */     }
/*  54 */     iterCtx.loopForItemsElement(env, getChildBuffer(), this.loopVarName, this.loopVar2Name);
/*  55 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/*  60 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  65 */     StringBuilder sb = new StringBuilder();
/*  66 */     if (canonical) sb.append('<'); 
/*  67 */     sb.append(getNodeTypeSymbol());
/*  68 */     sb.append(" as ");
/*  69 */     sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVarName));
/*  70 */     if (this.loopVar2Name != null) {
/*  71 */       sb.append(", ");
/*  72 */       sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar2Name));
/*     */     } 
/*  74 */     if (canonical) {
/*  75 */       sb.append('>');
/*  76 */       sb.append(getChildrenCanonicalForm());
/*  77 */       sb.append("</");
/*  78 */       sb.append(getNodeTypeSymbol());
/*  79 */       sb.append('>');
/*     */     } 
/*  81 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  86 */     return "#items";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  91 */     return (this.loopVar2Name != null) ? 2 : 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  96 */     switch (idx) {
/*     */       case 0:
/*  98 */         if (this.loopVarName == null) throw new IndexOutOfBoundsException(); 
/*  99 */         return this.loopVarName;
/*     */       case 1:
/* 101 */         if (this.loopVar2Name == null) throw new IndexOutOfBoundsException(); 
/* 102 */         return this.loopVar2Name;
/* 103 */     }  throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 109 */     switch (idx) {
/*     */       case 0:
/* 111 */         if (this.loopVarName == null) throw new IndexOutOfBoundsException(); 
/* 112 */         return ParameterRole.TARGET_LOOP_VARIABLE;
/*     */       case 1:
/* 114 */         if (this.loopVar2Name == null) throw new IndexOutOfBoundsException(); 
/* 115 */         return ParameterRole.TARGET_LOOP_VARIABLE;
/* 116 */     }  throw new IndexOutOfBoundsException();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Items.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */