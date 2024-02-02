/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
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
/*     */ final class HashLiteral
/*     */   extends Expression
/*     */ {
/*     */   private final List<? extends Expression> keys;
/*     */   private final List<? extends Expression> values;
/*     */   private final int size;
/*     */   
/*     */   HashLiteral(List<? extends Expression> keys, List<? extends Expression> values) {
/*  43 */     this.keys = keys;
/*  44 */     this.values = values;
/*  45 */     this.size = keys.size();
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  50 */     return (TemplateModel)new SequenceHash(env);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/*  55 */     StringBuilder buf = new StringBuilder("{");
/*  56 */     for (int i = 0; i < this.size; i++) {
/*  57 */       Expression key = this.keys.get(i);
/*  58 */       Expression value = this.values.get(i);
/*  59 */       buf.append(key.getCanonicalForm());
/*  60 */       buf.append(": ");
/*  61 */       buf.append(value.getCanonicalForm());
/*  62 */       if (i != this.size - 1) {
/*  63 */         buf.append(", ");
/*     */       }
/*     */     } 
/*  66 */     buf.append("}");
/*  67 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/*  72 */     return "{...}";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/*  77 */     if (this.constantValue != null) {
/*  78 */       return true;
/*     */     }
/*  80 */     for (int i = 0; i < this.size; i++) {
/*  81 */       Expression key = this.keys.get(i);
/*  82 */       Expression value = this.values.get(i);
/*  83 */       if (!key.isLiteral() || !value.isLiteral()) {
/*  84 */         return false;
/*     */       }
/*     */     } 
/*  87 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/*  94 */     List<Expression> clonedKeys = new ArrayList<>(this.keys.size());
/*  95 */     for (Expression key : this.keys) {
/*  96 */       clonedKeys.add(key.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */     }
/*     */     
/*  99 */     List<Expression> clonedValues = new ArrayList<>(this.values.size());
/* 100 */     for (Expression value : this.values) {
/* 101 */       clonedValues.add(value.deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */     }
/*     */     
/* 104 */     return new HashLiteral(clonedKeys, clonedValues);
/*     */   }
/*     */   
/*     */   private class SequenceHash
/*     */     implements TemplateHashModelEx2 {
/*     */     private HashMap<String, TemplateModel> map;
/*     */     private TemplateCollectionModel keyCollection;
/*     */     private TemplateCollectionModel valueCollection;
/*     */     
/*     */     SequenceHash(Environment env) throws TemplateException {
/* 114 */       if (_TemplateAPI.getTemplateLanguageVersionAsInt(HashLiteral.this) >= _TemplateAPI.VERSION_INT_2_3_21) {
/* 115 */         this.map = new LinkedHashMap<>();
/* 116 */         for (int i = 0; i < HashLiteral.this.size; i++) {
/* 117 */           Expression keyExp = HashLiteral.this.keys.get(i);
/* 118 */           Expression valExp = HashLiteral.this.values.get(i);
/* 119 */           String key = keyExp.evalAndCoerceToPlainText(env);
/* 120 */           TemplateModel value = valExp.eval(env);
/* 121 */           if (env == null || !env.isClassicCompatible()) {
/* 122 */             valExp.assertNonNull(value, env);
/*     */           }
/* 124 */           this.map.put(key, value);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 129 */         this.map = new HashMap<>();
/* 130 */         SimpleSequence keyList = new SimpleSequence(HashLiteral.this.size, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 131 */         SimpleSequence valueList = new SimpleSequence(HashLiteral.this.size, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 132 */         for (int i = 0; i < HashLiteral.this.size; i++) {
/* 133 */           Expression keyExp = HashLiteral.this.keys.get(i);
/* 134 */           Expression valExp = HashLiteral.this.values.get(i);
/* 135 */           String key = keyExp.evalAndCoerceToPlainText(env);
/* 136 */           TemplateModel value = valExp.eval(env);
/* 137 */           if (env == null || !env.isClassicCompatible()) {
/* 138 */             valExp.assertNonNull(value, env);
/*     */           }
/* 140 */           this.map.put(key, value);
/* 141 */           keyList.add(key);
/* 142 */           valueList.add(value);
/*     */         } 
/* 144 */         this.keyCollection = new CollectionAndSequence((TemplateSequenceModel)keyList);
/* 145 */         this.valueCollection = new CollectionAndSequence((TemplateSequenceModel)valueList);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int size() {
/* 151 */       return HashLiteral.this.size;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel keys() {
/* 156 */       if (this.keyCollection == null)
/*     */       {
/* 158 */         this
/* 159 */           .keyCollection = new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(this.map.keySet(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER));
/*     */       }
/* 161 */       return this.keyCollection;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateCollectionModel values() {
/* 166 */       if (this.valueCollection == null)
/*     */       {
/* 168 */         this
/* 169 */           .valueCollection = new CollectionAndSequence((TemplateSequenceModel)new SimpleSequence(this.map.values(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER));
/*     */       }
/* 171 */       return this.valueCollection;
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel get(String key) {
/* 176 */       return this.map.get(key);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 181 */       return (HashLiteral.this.size == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 186 */       return HashLiteral.this.getCanonicalForm();
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateHashModelEx2.KeyValuePairIterator keyValuePairIterator() throws TemplateModelException {
/* 191 */       return new TemplateHashModelEx2.KeyValuePairIterator() {
/* 192 */           private final TemplateModelIterator keyIterator = HashLiteral.SequenceHash.this.keys().iterator();
/* 193 */           private final TemplateModelIterator valueIterator = HashLiteral.SequenceHash.this.values().iterator();
/*     */ 
/*     */           
/*     */           public boolean hasNext() throws TemplateModelException {
/* 197 */             return this.keyIterator.hasNext();
/*     */           }
/*     */ 
/*     */           
/*     */           public TemplateHashModelEx2.KeyValuePair next() throws TemplateModelException {
/* 202 */             return new TemplateHashModelEx2.KeyValuePair() {
/* 203 */                 private final TemplateModel key = HashLiteral.SequenceHash.null.this.keyIterator.next();
/* 204 */                 private final TemplateModel value = HashLiteral.SequenceHash.null.this.valueIterator.next();
/*     */ 
/*     */                 
/*     */                 public TemplateModel getKey() {
/* 208 */                   return this.key;
/*     */                 }
/*     */ 
/*     */                 
/*     */                 public TemplateModel getValue() {
/* 213 */                   return this.value;
/*     */                 }
/*     */               };
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 226 */     return this.size * 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 231 */     checkIndex(idx);
/* 232 */     return (idx % 2 == 0) ? this.keys.get(idx / 2) : this.values.get(idx / 2);
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 237 */     checkIndex(idx);
/* 238 */     return (idx % 2 == 0) ? ParameterRole.ITEM_KEY : ParameterRole.ITEM_VALUE;
/*     */   }
/*     */   
/*     */   private void checkIndex(int idx) {
/* 242 */     if (idx >= this.size * 2)
/* 243 */       throw new IndexOutOfBoundsException(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\HashLiteral.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */