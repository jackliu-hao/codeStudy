/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
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
/*     */ final class Identifier
/*     */   extends Expression
/*     */ {
/*     */   private final String name;
/*     */   
/*     */   Identifier(String name) {
/*  33 */     this.name = name;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*     */     try {
/*  39 */       return env.getVariable(this.name);
/*  40 */     } catch (NullPointerException e) {
/*  41 */       if (env == null) {
/*  42 */         throw new _MiscTemplateException(new Object[] { "Variables are not available (certainly you are in a parse-time executed directive). The name of the variable you tried to read: ", this.name });
/*     */       }
/*     */ 
/*     */       
/*  46 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  53 */     return _CoreStringUtils.toFTLTopLevelIdentifierReference(this.name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   String getName() {
/*  60 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  65 */     return getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  70 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/*  75 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/*  80 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/*  85 */     throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  91 */     if (this.name.equals(replacedIdentifier)) {
/*  92 */       if (replacementState.replacementAlreadyInUse) {
/*  93 */         Expression clone = replacement.deepCloneWithIdentifierReplaced((String)null, (Expression)null, replacementState);
/*  94 */         clone.copyLocationFrom(replacement);
/*  95 */         return clone;
/*     */       } 
/*  97 */       replacementState.replacementAlreadyInUse = true;
/*  98 */       return replacement;
/*     */     } 
/*     */     
/* 101 */     return new Identifier(this.name);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\Identifier.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */