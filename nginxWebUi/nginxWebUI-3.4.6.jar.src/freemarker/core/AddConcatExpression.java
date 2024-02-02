/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ final class AddConcatExpression
/*     */   extends Expression
/*     */ {
/*     */   private final Expression left;
/*     */   private final Expression right;
/*     */   
/*     */   AddConcatExpression(Expression left, Expression right) {
/*  51 */     this.left = left;
/*  52 */     this.right = right;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  57 */     return _eval(env, this, this.left, this.left.eval(env), this.right, this.right.eval(env));
/*     */   }
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
/*     */   static TemplateModel _eval(Environment env, TemplateObject parent, Expression leftExp, TemplateModel leftModel, Expression rightExp, TemplateModel rightModel) throws TemplateModelException, TemplateException, NonStringException {
/*  71 */     if (leftModel instanceof TemplateNumberModel && rightModel instanceof TemplateNumberModel) {
/*  72 */       Number first = EvalUtil.modelToNumber((TemplateNumberModel)leftModel, leftExp);
/*  73 */       Number second = EvalUtil.modelToNumber((TemplateNumberModel)rightModel, rightExp);
/*  74 */       return _evalOnNumbers(env, parent, first, second);
/*  75 */     }  if (leftModel instanceof TemplateSequenceModel && rightModel instanceof TemplateSequenceModel) {
/*  76 */       return (TemplateModel)new ConcatenatedSequence((TemplateSequenceModel)leftModel, (TemplateSequenceModel)rightModel);
/*     */     }
/*  78 */     boolean hashConcatPossible = (leftModel instanceof TemplateHashModel && rightModel instanceof TemplateHashModel);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  84 */       Object leftOMOrStr = EvalUtil.coerceModelToStringOrMarkup(leftModel, leftExp, hashConcatPossible, null, env);
/*     */ 
/*     */       
/*  87 */       if (leftOMOrStr == null) {
/*  88 */         return _eval_concatenateHashes(leftModel, rightModel);
/*     */       }
/*     */ 
/*     */       
/*  92 */       Object rightOMOrStr = EvalUtil.coerceModelToStringOrMarkup(rightModel, rightExp, hashConcatPossible, null, env);
/*     */ 
/*     */       
/*  95 */       if (rightOMOrStr == null) {
/*  96 */         return _eval_concatenateHashes(leftModel, rightModel);
/*     */       }
/*     */       
/*  99 */       if (leftOMOrStr instanceof String) {
/* 100 */         if (rightOMOrStr instanceof String) {
/* 101 */           return (TemplateModel)new SimpleScalar(((String)leftOMOrStr).concat((String)rightOMOrStr));
/*     */         }
/* 103 */         TemplateMarkupOutputModel<?> rightMO = (TemplateMarkupOutputModel)rightOMOrStr;
/* 104 */         return EvalUtil.concatMarkupOutputs(parent, rightMO
/* 105 */             .getOutputFormat().fromPlainTextByEscaping((String)leftOMOrStr), rightMO);
/*     */       } 
/*     */ 
/*     */       
/* 109 */       TemplateMarkupOutputModel<?> leftMO = (TemplateMarkupOutputModel)leftOMOrStr;
/* 110 */       if (rightOMOrStr instanceof String) {
/* 111 */         return EvalUtil.concatMarkupOutputs(parent, leftMO, leftMO
/*     */             
/* 113 */             .getOutputFormat().fromPlainTextByEscaping((String)rightOMOrStr));
/*     */       }
/* 115 */       return EvalUtil.concatMarkupOutputs(parent, leftMO, (TemplateMarkupOutputModel)rightOMOrStr);
/*     */ 
/*     */ 
/*     */     
/*     */     }
/* 120 */     catch (NonStringOrTemplateOutputException e) {
/*     */ 
/*     */       
/* 123 */       if (hashConcatPossible) {
/* 124 */         return _eval_concatenateHashes(leftModel, rightModel);
/*     */       }
/* 126 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static TemplateModel _eval_concatenateHashes(TemplateModel leftModel, TemplateModel rightModel) throws TemplateModelException {
/* 134 */     if (leftModel instanceof TemplateHashModelEx && rightModel instanceof TemplateHashModelEx) {
/* 135 */       TemplateHashModelEx leftModelEx = (TemplateHashModelEx)leftModel;
/* 136 */       TemplateHashModelEx rightModelEx = (TemplateHashModelEx)rightModel;
/* 137 */       if (leftModelEx.size() == 0)
/* 138 */         return (TemplateModel)rightModelEx; 
/* 139 */       if (rightModelEx.size() == 0) {
/* 140 */         return (TemplateModel)leftModelEx;
/*     */       }
/* 142 */       return (TemplateModel)new ConcatenatedHashEx(leftModelEx, rightModelEx);
/*     */     } 
/*     */     
/* 145 */     return (TemplateModel)new ConcatenatedHash((TemplateHashModel)leftModel, (TemplateHashModel)rightModel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static TemplateModel _evalOnNumbers(Environment env, TemplateObject parent, Number first, Number second) throws TemplateException {
/* 152 */     ArithmeticEngine ae = EvalUtil.getArithmeticEngine(env, parent);
/* 153 */     return (TemplateModel)new SimpleNumber(ae.add(first, second));
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 158 */     return (this.constantValue != null || (this.left.isLiteral() && this.right.isLiteral()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 164 */     return new AddConcatExpression(this.left
/* 165 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.right
/* 166 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 171 */     return this.left.getCanonicalForm() + " + " + this.right.getCanonicalForm();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 176 */     return "+";
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 181 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 186 */     return (idx == 0) ? this.left : this.right;
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 191 */     return ParameterRole.forBinaryOperatorOperand(idx);
/*     */   }
/*     */   
/*     */   private static final class ConcatenatedSequence
/*     */     implements TemplateSequenceModel
/*     */   {
/*     */     private final TemplateSequenceModel left;
/*     */     private final TemplateSequenceModel right;
/*     */     
/*     */     ConcatenatedSequence(TemplateSequenceModel left, TemplateSequenceModel right) {
/* 201 */       this.left = left;
/* 202 */       this.right = right;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 208 */       return this.left.size() + this.right.size();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel get(int i) throws TemplateModelException {
/* 214 */       int ls = this.left.size();
/* 215 */       return (i < ls) ? this.left.get(i) : this.right.get(i - ls);
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ConcatenatedHash
/*     */     implements TemplateHashModel {
/*     */     protected final TemplateHashModel left;
/*     */     protected final TemplateHashModel right;
/*     */     
/*     */     ConcatenatedHash(TemplateHashModel left, TemplateHashModel right) {
/* 225 */       this.left = left;
/* 226 */       this.right = right;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) throws TemplateModelException {
/* 232 */       TemplateModel model = this.right.get(key);
/* 233 */       return (model != null) ? model : this.left.get(key);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isEmpty() throws TemplateModelException {
/* 239 */       return (this.left.isEmpty() && this.right.isEmpty());
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ConcatenatedHashEx
/*     */     extends ConcatenatedHash
/*     */     implements TemplateHashModelEx {
/*     */     private CollectionAndSequence keys;
/*     */     private CollectionAndSequence values;
/*     */     
/*     */     ConcatenatedHashEx(TemplateHashModelEx left, TemplateHashModelEx right) {
/* 250 */       super((TemplateHashModel)left, (TemplateHashModel)right);
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() throws TemplateModelException {
/* 255 */       initKeys();
/* 256 */       return this.keys.size();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel keys() throws TemplateModelException {
/* 262 */       initKeys();
/* 263 */       return this.keys;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() throws TemplateModelException {
/* 269 */       initValues();
/* 270 */       return this.values;
/*     */     }
/*     */ 
/*     */     
/*     */     private void initKeys() throws TemplateModelException {
/* 275 */       if (this.keys == null) {
/* 276 */         HashSet keySet = new HashSet();
/* 277 */         SimpleSequence keySeq = new SimpleSequence(32, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 278 */         addKeys(keySet, keySeq, (TemplateHashModelEx)this.left);
/* 279 */         addKeys(keySet, keySeq, (TemplateHashModelEx)this.right);
/* 280 */         this.keys = new CollectionAndSequence((TemplateSequenceModel)keySeq);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private static void addKeys(Set<String> keySet, SimpleSequence keySeq, TemplateHashModelEx hash) throws TemplateModelException {
/* 286 */       TemplateModelIterator it = hash.keys().iterator();
/* 287 */       while (it.hasNext()) {
/* 288 */         TemplateScalarModel tsm = (TemplateScalarModel)it.next();
/* 289 */         if (keySet.add(tsm.getAsString()))
/*     */         {
/*     */           
/* 292 */           keySeq.add(tsm);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     private void initValues() throws TemplateModelException {
/* 299 */       if (this.values == null) {
/* 300 */         SimpleSequence seq = new SimpleSequence(size(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */ 
/*     */         
/* 303 */         int ln = this.keys.size();
/* 304 */         for (int i = 0; i < ln; i++) {
/* 305 */           seq.add(get(((TemplateScalarModel)this.keys.get(i)).getAsString()));
/*     */         }
/* 307 */         this.values = new CollectionAndSequence((TemplateSequenceModel)seq);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\AddConcatExpression.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */