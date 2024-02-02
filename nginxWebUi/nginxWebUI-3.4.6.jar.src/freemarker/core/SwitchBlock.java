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
/*     */ final class SwitchBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   private Case defaultCase;
/*     */   private final Expression searched;
/*     */   private int firstCaseIndex;
/*     */   
/*     */   SwitchBlock(Expression searched, MixedContent ignoredSectionBeforeFirstCase) {
/*  39 */     this.searched = searched;
/*     */     
/*  41 */     int ignoredCnt = (ignoredSectionBeforeFirstCase != null) ? ignoredSectionBeforeFirstCase.getChildCount() : 0;
/*  42 */     setChildBufferCapacity(ignoredCnt + 4);
/*  43 */     for (int i = 0; i < ignoredCnt; i++) {
/*  44 */       addChild(ignoredSectionBeforeFirstCase.getChild(i));
/*     */     }
/*  46 */     this.firstCaseIndex = ignoredCnt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void addCase(Case cas) {
/*  53 */     if (cas.condition == null) {
/*  54 */       this.defaultCase = cas;
/*     */     }
/*  56 */     addChild(cas);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  62 */     boolean processedCase = false;
/*  63 */     int ln = getChildCount();
/*     */     try {
/*  65 */       for (int i = this.firstCaseIndex; i < ln; i++) {
/*  66 */         Case cas = (Case)getChild(i);
/*  67 */         boolean processCase = false;
/*     */ 
/*     */         
/*  70 */         if (processedCase) {
/*  71 */           processCase = true;
/*  72 */         } else if (cas.condition != null) {
/*     */           
/*  74 */           processCase = EvalUtil.compare(this.searched, 1, "case==", cas.condition, cas.condition, env);
/*     */         } 
/*     */ 
/*     */         
/*  78 */         if (processCase) {
/*  79 */           env.visit(cas);
/*  80 */           processedCase = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/*  86 */       if (!processedCase && this.defaultCase != null) {
/*  87 */         env.visit(this.defaultCase);
/*     */       }
/*  89 */     } catch (BreakOrContinueException breakOrContinueException) {}
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  95 */     StringBuilder buf = new StringBuilder();
/*  96 */     if (canonical) buf.append('<'); 
/*  97 */     buf.append(getNodeTypeSymbol());
/*  98 */     buf.append(' ');
/*  99 */     buf.append(this.searched.getCanonicalForm());
/* 100 */     if (canonical) {
/* 101 */       buf.append('>');
/* 102 */       int ln = getChildCount();
/* 103 */       for (int i = 0; i < ln; i++) {
/* 104 */         buf.append(getChild(i).getCanonicalForm());
/*     */       }
/* 106 */       buf.append("</").append(getNodeTypeSymbol()).append('>');
/*     */     } 
/* 108 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 113 */     return "#switch";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 118 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 123 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 124 */     return this.searched;
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 129 */     if (idx != 0) throw new IndexOutOfBoundsException(); 
/* 130 */     return ParameterRole.VALUE;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 135 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
/* 140 */     TemplateElement result = super.postParseCleanup(stripWhitespace);
/*     */ 
/*     */     
/* 143 */     int ln = getChildCount();
/* 144 */     int i = 0;
/* 145 */     while (i < ln && !(getChild(i) instanceof Case)) {
/* 146 */       i++;
/*     */     }
/* 148 */     this.firstCaseIndex = i;
/*     */     
/* 150 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\SwitchBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */