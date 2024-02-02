/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleCollection;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.Constants;
/*     */ import java.util.ArrayList;
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
/*     */ class DefaultToExpression
/*     */   extends Expression
/*     */ {
/*  36 */   private static final TemplateCollectionModel EMPTY_COLLECTION = (TemplateCollectionModel)new SimpleCollection(new ArrayList(0));
/*     */   
/*     */   private static class EmptyStringAndSequenceAndHash implements TemplateScalarModel, TemplateSequenceModel, TemplateHashModelEx2 {
/*     */     private EmptyStringAndSequenceAndHash() {}
/*     */     
/*     */     public String getAsString() {
/*  42 */       return "";
/*     */     }
/*     */     
/*     */     public TemplateModel get(int i) {
/*  46 */       return null;
/*     */     }
/*     */     
/*     */     public TemplateModel get(String s) {
/*  50 */       return null;
/*     */     }
/*     */     
/*     */     public int size() {
/*  54 */       return 0;
/*     */     }
/*     */     
/*     */     public boolean isEmpty() {
/*  58 */       return true;
/*     */     }
/*     */     
/*     */     public TemplateCollectionModel keys() {
/*  62 */       return DefaultToExpression.EMPTY_COLLECTION;
/*     */     }
/*     */     
/*     */     public TemplateCollectionModel values() {
/*  66 */       return DefaultToExpression.EMPTY_COLLECTION;
/*     */     }
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
/*  70 */       return Constants.EMPTY_KEY_VALUE_PAIR_ITERATOR;
/*     */     }
/*     */   }
/*     */   
/*  74 */   static final TemplateModel EMPTY_STRING_AND_SEQUENCE_AND_HASH = (TemplateModel)new EmptyStringAndSequenceAndHash();
/*     */   private final Expression lho;
/*     */   private final Expression rho;
/*     */   
/*     */   DefaultToExpression(Expression lho, Expression rho) {
/*  79 */     this.lho = lho;
/*  80 */     this.rho = rho;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*     */     TemplateModel left;
/*  86 */     if (this.lho instanceof ParentheticalExpression) {
/*  87 */       boolean lastFIRE = env.setFastInvalidReferenceExceptions(true);
/*     */       try {
/*  89 */         left = this.lho.eval(env);
/*  90 */       } catch (InvalidReferenceException ire) {
/*  91 */         left = null;
/*     */       } finally {
/*  93 */         env.setFastInvalidReferenceExceptions(lastFIRE);
/*     */       } 
/*     */     } else {
/*  96 */       left = this.lho.eval(env);
/*     */     } 
/*     */     
/*  99 */     if (left != null) return left; 
/* 100 */     if (this.rho == null) return EMPTY_STRING_AND_SEQUENCE_AND_HASH; 
/* 101 */     return this.rho.eval(env);
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 111 */     return new DefaultToExpression(this.lho
/* 112 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), (this.rho != null) ? this.rho
/*     */         
/* 114 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState) : null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 120 */     if (this.rho == null) {
/* 121 */       return this.lho.getCanonicalForm() + '!';
/*     */     }
/* 123 */     return this.lho.getCanonicalForm() + '!' + this.rho.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 128 */     return "...!...";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 133 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 138 */     switch (idx) { case 0:
/* 139 */         return this.lho;
/* 140 */       case 1: return this.rho; }
/* 141 */      throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 147 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DefaultToExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */