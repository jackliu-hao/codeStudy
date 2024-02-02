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
/*     */ final class IfBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   IfBlock(ConditionalBlock block) {
/*  34 */     setChildBufferCapacity(1);
/*  35 */     addBlock(block);
/*     */   }
/*     */   
/*     */   void addBlock(ConditionalBlock block) {
/*  39 */     addChild(block);
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  44 */     int ln = getChildCount();
/*  45 */     for (int i = 0; i < ln; i++) {
/*  46 */       ConditionalBlock cblock = (ConditionalBlock)getChild(i);
/*  47 */       Expression condition = cblock.condition;
/*  48 */       env.replaceElementStackTop(cblock);
/*  49 */       if (condition == null || condition.evalToBoolean(env)) {
/*  50 */         return cblock.getChildBuffer();
/*     */       }
/*     */     } 
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
/*  59 */     if (getChildCount() == 1) {
/*  60 */       ConditionalBlock cblock = (ConditionalBlock)getChild(0);
/*  61 */       cblock.setLocation(getTemplate(), cblock, this);
/*  62 */       return cblock.postParseCleanup(stripWhitespace);
/*     */     } 
/*  64 */     return super.postParseCleanup(stripWhitespace);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/*  70 */     if (canonical) {
/*  71 */       StringBuilder buf = new StringBuilder();
/*  72 */       int ln = getChildCount();
/*  73 */       for (int i = 0; i < ln; i++) {
/*  74 */         ConditionalBlock cblock = (ConditionalBlock)getChild(i);
/*  75 */         buf.append(cblock.dump(canonical));
/*     */       } 
/*  77 */       buf.append("</#if>");
/*  78 */       return buf.toString();
/*     */     } 
/*  80 */     return getNodeTypeSymbol();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  86 */     return "#if-#elseif-#else-container";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  91 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  96 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 101 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 106 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\IfBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */