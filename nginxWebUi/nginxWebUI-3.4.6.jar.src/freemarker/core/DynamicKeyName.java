/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.SimpleSequence;
/*     */ import freemarker.template.TemplateCollectionModelEx;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.Constants;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ 
/*     */ final class DynamicKeyName
/*     */   extends Expression
/*     */ {
/*     */   private static final int UNKNOWN_RESULT_SIZE = -1;
/*     */   private final Expression keyExpression;
/*     */   private final Expression target;
/*     */   private boolean lazilyGeneratedResultEnabled;
/*     */   
/*     */   DynamicKeyName(Expression target, Expression keyExpression) {
/*  53 */     this.target = target;
/*  54 */     this.keyExpression = keyExpression;
/*     */     
/*  56 */     target.enableLazilyGeneratedResult();
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateModel _eval(Environment env) throws TemplateException {
/*  61 */     TemplateModel targetModel = this.target.eval(env);
/*  62 */     if (targetModel == null) {
/*  63 */       if (env.isClassicCompatible()) {
/*  64 */         return null;
/*     */       }
/*  66 */       throw InvalidReferenceException.getInstance(this.target, env);
/*     */     } 
/*     */ 
/*     */     
/*  70 */     TemplateModel keyModel = this.keyExpression.eval(env);
/*  71 */     if (keyModel == null) {
/*  72 */       if (env.isClassicCompatible()) {
/*  73 */         keyModel = TemplateScalarModel.EMPTY_STRING;
/*     */       } else {
/*  75 */         this.keyExpression.assertNonNull((TemplateModel)null, env);
/*     */       } 
/*     */     }
/*  78 */     if (keyModel instanceof TemplateNumberModel) {
/*  79 */       int index = this.keyExpression.modelToNumber(keyModel, env).intValue();
/*  80 */       return dealWithNumericalKey(targetModel, index, env);
/*     */     } 
/*  82 */     if (keyModel instanceof TemplateScalarModel) {
/*  83 */       String key = EvalUtil.modelToString((TemplateScalarModel)keyModel, this.keyExpression, env);
/*  84 */       return dealWithStringKey(targetModel, key, env);
/*     */     } 
/*  86 */     if (keyModel instanceof RangeModel) {
/*  87 */       return dealWithRangeKey(targetModel, (RangeModel)keyModel, env);
/*     */     }
/*  89 */     throw new UnexpectedTypeException(this.keyExpression, keyModel, "number, range, or string", new Class[] { TemplateNumberModel.class, TemplateScalarModel.class, Range.class }, env);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   private static Class[] NUMERICAL_KEY_LHO_EXPECTED_TYPES = new Class[1 + NonStringException.STRING_COERCABLE_TYPES.length]; static {
/*  96 */     NUMERICAL_KEY_LHO_EXPECTED_TYPES[0] = TemplateSequenceModel.class;
/*  97 */     for (int i = 0; i < NonStringException.STRING_COERCABLE_TYPES.length; i++) {
/*  98 */       NUMERICAL_KEY_LHO_EXPECTED_TYPES[i + 1] = NonStringException.STRING_COERCABLE_TYPES[i];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateModel dealWithNumericalKey(TemplateModel targetModel, int index, Environment env) throws TemplateException {
/* 106 */     if (targetModel instanceof TemplateSequenceModel) {
/* 107 */       int size; TemplateSequenceModel tsm = (TemplateSequenceModel)targetModel;
/*     */       
/*     */       try {
/* 110 */         size = tsm.size();
/* 111 */       } catch (Exception e) {
/* 112 */         size = Integer.MAX_VALUE;
/*     */       } 
/* 114 */       return (index < size) ? tsm.get(index) : null;
/*     */     } 
/* 116 */     if (targetModel instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)targetModel)
/* 117 */       .isSequence()) {
/* 118 */       if (index < 0) {
/* 119 */         return null;
/*     */       }
/* 121 */       TemplateModelIterator iter = ((LazilyGeneratedCollectionModel)targetModel).iterator();
/* 122 */       for (int curIndex = 0; iter.hasNext(); curIndex++) {
/* 123 */         TemplateModel next = iter.next();
/* 124 */         if (index == curIndex) {
/* 125 */           return next;
/*     */         }
/*     */       } 
/* 128 */       return null;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 133 */       String s = this.target.evalAndCoerceToPlainText(env);
/*     */       try {
/* 135 */         return (TemplateModel)new SimpleScalar(s.substring(index, index + 1));
/* 136 */       } catch (IndexOutOfBoundsException e) {
/* 137 */         if (index < 0) {
/* 138 */           throw new _MiscTemplateException(new Object[] { "Negative index not allowed: ", Integer.valueOf(index) });
/*     */         }
/* 140 */         if (index >= s.length()) {
/* 141 */           throw new _MiscTemplateException(new Object[] { "String index out of range: The index was ", 
/* 142 */                 Integer.valueOf(index), " (0-based), but the length of the string is only ", 
/* 143 */                 Integer.valueOf(s.length()), "." });
/*     */         }
/* 145 */         throw new RuntimeException("Can't explain exception", e);
/*     */       } 
/* 147 */     } catch (NonStringException e) {
/* 148 */       throw new UnexpectedTypeException(this.target, targetModel, "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, (targetModel instanceof TemplateHashModel) ? "You had a numerical value inside the []. Currently that's only supported for sequences (lists) and strings. To get a Map item with a non-string key, use myMap?api.get(myKey)." : null, env);
/*     */     } 
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
/*     */   private TemplateModel dealWithStringKey(TemplateModel targetModel, String key, Environment env) throws TemplateException {
/* 163 */     if (targetModel instanceof TemplateHashModel) {
/* 164 */       return ((TemplateHashModel)targetModel).get(key);
/*     */     }
/* 166 */     throw new NonHashException(this.target, targetModel, env);
/*     */   }
/*     */   private TemplateModel dealWithRangeKey(TemplateModel targetModel, RangeModel range, Environment env) throws TemplateException {
/*     */     TemplateSequenceModel targetSeq;
/*     */     LazilyGeneratedCollectionModel targetLazySeq;
/*     */     String targetStr;
/*     */     int targetSize;
/*     */     boolean targetSizeKnown;
/*     */     int resultSize, exclEndIdx;
/* 175 */     if (targetModel instanceof TemplateSequenceModel) {
/* 176 */       targetSeq = (TemplateSequenceModel)targetModel;
/* 177 */       targetLazySeq = null;
/* 178 */       targetStr = null;
/* 179 */     } else if (targetModel instanceof LazilyGeneratedCollectionModel && ((LazilyGeneratedCollectionModel)targetModel)
/* 180 */       .isSequence()) {
/* 181 */       targetSeq = null;
/* 182 */       targetLazySeq = (LazilyGeneratedCollectionModel)targetModel;
/* 183 */       targetStr = null;
/*     */     } else {
/* 185 */       targetSeq = null;
/* 186 */       targetLazySeq = null;
/*     */       try {
/* 188 */         targetStr = this.target.evalAndCoerceToPlainText(env);
/* 189 */       } catch (NonStringException e) {
/* 190 */         throw new UnexpectedTypeException(this.target, this.target
/* 191 */             .eval(env), "sequence or string or something automatically convertible to string (number, date or boolean)", NUMERICAL_KEY_LHO_EXPECTED_TYPES, env);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 197 */     int rangeSize = range.size();
/* 198 */     boolean rightUnbounded = range.isRightUnbounded();
/* 199 */     boolean rightAdaptive = range.isRightAdaptive();
/*     */ 
/*     */ 
/*     */     
/* 203 */     if (!rightUnbounded && rangeSize == 0) {
/* 204 */       return emptyResult((targetSeq != null));
/*     */     }
/*     */     
/* 207 */     int firstIdx = range.getBegining();
/* 208 */     if (firstIdx < 0) {
/* 209 */       throw new _MiscTemplateException(this.keyExpression, new Object[] { "Negative range start index (", 
/* 210 */             Integer.valueOf(firstIdx), ") isn't allowed for a range used for slicing." });
/*     */     }
/*     */ 
/*     */     
/* 214 */     int step = range.getStep();
/*     */ 
/*     */ 
/*     */     
/* 218 */     if (targetStr != null) {
/* 219 */       targetSize = targetStr.length();
/* 220 */       targetSizeKnown = true;
/* 221 */     } else if (targetSeq != null) {
/* 222 */       targetSize = targetSeq.size();
/* 223 */       targetSizeKnown = true;
/* 224 */     } else if (targetLazySeq instanceof TemplateCollectionModelEx) {
/*     */       
/* 226 */       targetSize = ((TemplateCollectionModelEx)targetLazySeq).size();
/* 227 */       targetSizeKnown = true;
/*     */     } else {
/* 229 */       targetSize = Integer.MAX_VALUE;
/* 230 */       targetSizeKnown = false;
/*     */     } 
/*     */     
/* 233 */     if (targetSizeKnown)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 239 */       if (rightAdaptive ? ((step == 1) ? (firstIdx > targetSize) : (firstIdx >= targetSize)) : (firstIdx >= targetSize)) {
/* 240 */         throw new _MiscTemplateException(this.keyExpression, new Object[] { "Range start index ", 
/* 241 */               Integer.valueOf(firstIdx), " is out of bounds, because the sliced ", (targetStr != null) ? "string" : "sequence", " has only ", 
/*     */               
/* 243 */               Integer.valueOf(targetSize), " ", (targetStr != null) ? "character(s)" : "element(s)", ". ", "(Note that indices are 0-based)." });
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 253 */     if (!rightUnbounded) {
/* 254 */       int lastIdx = firstIdx + (rangeSize - 1) * step;
/* 255 */       if (lastIdx < 0) {
/* 256 */         if (!rightAdaptive) {
/* 257 */           throw new _MiscTemplateException(this.keyExpression, new Object[] { "Negative range end index (", 
/* 258 */                 Integer.valueOf(lastIdx), ") isn't allowed for a range used for slicing." });
/*     */         }
/*     */         
/* 261 */         resultSize = firstIdx + 1;
/*     */       }
/* 263 */       else if (targetSizeKnown && lastIdx >= targetSize) {
/* 264 */         if (!rightAdaptive) {
/* 265 */           throw new _MiscTemplateException(this.keyExpression, new Object[] { "Range end index ", 
/* 266 */                 Integer.valueOf(lastIdx), " is out of bounds, because the sliced ", (targetStr != null) ? "string" : "sequence", " has only ", 
/*     */                 
/* 268 */                 Integer.valueOf(targetSize), " ", (targetStr != null) ? "character(s)" : "element(s)", ". (Note that indices are 0-based)." });
/*     */         }
/*     */         
/* 271 */         resultSize = Math.abs(targetSize - firstIdx);
/*     */       } else {
/*     */         
/* 274 */         resultSize = rangeSize;
/*     */       } 
/*     */     } else {
/* 277 */       resultSize = targetSizeKnown ? (targetSize - firstIdx) : -1;
/*     */     } 
/*     */     
/* 280 */     if (resultSize == 0) {
/* 281 */       return emptyResult((targetSeq != null));
/*     */     }
/* 283 */     if (targetSeq != null) {
/*     */ 
/*     */ 
/*     */       
/* 287 */       ArrayList<TemplateModel> resultList = new ArrayList<>(resultSize);
/* 288 */       int srcIdx = firstIdx;
/* 289 */       for (int i = 0; i < resultSize; i++) {
/* 290 */         resultList.add(targetSeq.get(srcIdx));
/* 291 */         srcIdx += step;
/*     */       } 
/*     */       
/* 294 */       return (TemplateModel)new SimpleSequence(resultList, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 295 */     }  if (targetLazySeq != null) {
/*     */ 
/*     */       
/* 298 */       if (step == 1)
/* 299 */         return getStep1RangeFromIterator(targetLazySeq.iterator(), range, resultSize, targetSizeKnown); 
/* 300 */       if (step == -1) {
/* 301 */         return getStepMinus1RangeFromIterator(targetLazySeq.iterator(), range, resultSize);
/*     */       }
/* 303 */       throw new AssertionError();
/*     */     } 
/*     */ 
/*     */     
/* 307 */     if (step < 0 && resultSize > 1) {
/* 308 */       if (!range.isAffectedByStringSlicingBug() || resultSize != 2) {
/* 309 */         throw new _MiscTemplateException(this.keyExpression, new Object[] { "Decreasing ranges aren't allowed for slicing strings (as it would give reversed text). The index range was: first = ", 
/*     */               
/* 311 */               Integer.valueOf(firstIdx), ", last = ", 
/* 312 */               Integer.valueOf(firstIdx + (resultSize - 1) * step) });
/*     */       }
/*     */ 
/*     */       
/* 316 */       exclEndIdx = firstIdx;
/*     */     } else {
/*     */       
/* 319 */       exclEndIdx = firstIdx + resultSize;
/*     */     } 
/*     */     
/* 322 */     return (TemplateModel)new SimpleScalar(targetStr.substring(firstIdx, exclEndIdx));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateModel getStep1RangeFromIterator(final TemplateModelIterator targetIter, RangeModel range, int resultSize, boolean targetSizeKnown) throws TemplateModelException {
/* 329 */     final int firstIdx = range.getBegining();
/* 330 */     final int lastIdx = firstIdx + range.size() - 1;
/* 331 */     final boolean rightAdaptive = range.isRightAdaptive();
/* 332 */     final boolean rightUnbounded = range.isRightUnbounded();
/* 333 */     if (this.lazilyGeneratedResultEnabled) {
/* 334 */       TemplateModelIterator iterator = new TemplateModelIterator()
/*     */         {
/*     */           private boolean elementsBeforeFirsIndexWereSkipped;
/*     */           private int nextIdx;
/*     */           
/*     */           public TemplateModel next() throws TemplateModelException {
/* 340 */             ensureElementsBeforeFirstIndexWereSkipped();
/* 341 */             if (!rightUnbounded && this.nextIdx > lastIdx)
/*     */             {
/* 343 */               throw new _TemplateModelException(new Object[] { "Iterator has no more elements (at index ", 
/* 344 */                     Integer.valueOf(this.nextIdx), ")" });
/*     */             }
/* 346 */             if (!rightAdaptive && !targetIter.hasNext())
/*     */             {
/* 348 */               throw DynamicKeyName.this.newRangeEndOutOfBoundsException(this.nextIdx, lastIdx);
/*     */             }
/* 350 */             TemplateModel result = targetIter.next();
/* 351 */             this.nextIdx++;
/* 352 */             return result;
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean hasNext() throws TemplateModelException {
/* 357 */             ensureElementsBeforeFirstIndexWereSkipped();
/* 358 */             return ((rightUnbounded || this.nextIdx <= lastIdx) && (!rightAdaptive || targetIter.hasNext()));
/*     */           }
/*     */           
/*     */           public void ensureElementsBeforeFirstIndexWereSkipped() throws TemplateModelException {
/* 362 */             if (this.elementsBeforeFirsIndexWereSkipped) {
/*     */               return;
/*     */             }
/*     */             
/* 366 */             DynamicKeyName.this.skipElementsBeforeFirstIndex(targetIter, firstIdx);
/* 367 */             this.nextIdx = firstIdx;
/* 368 */             this.elementsBeforeFirsIndexWereSkipped = true;
/*     */           }
/*     */         };
/* 371 */       return (resultSize != -1 && targetSizeKnown) ? (TemplateModel)new LazilyGeneratedCollectionModelWithAlreadyKnownSize(iterator, resultSize, true) : (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(iterator, true);
/*     */     } 
/*     */ 
/*     */     
/* 375 */     List<TemplateModel> resultList = (resultSize != -1) ? new ArrayList<>(resultSize) : new ArrayList<>();
/*     */ 
/*     */     
/* 378 */     skipElementsBeforeFirstIndex(targetIter, firstIdx);
/* 379 */     for (int nextIdx = firstIdx; rightUnbounded || nextIdx <= lastIdx; nextIdx++) {
/* 380 */       if (!targetIter.hasNext()) {
/* 381 */         if (!rightAdaptive) {
/* 382 */           throw newRangeEndOutOfBoundsException(nextIdx, lastIdx);
/*     */         }
/*     */         break;
/*     */       } 
/* 386 */       resultList.add(targetIter.next());
/*     */     } 
/*     */     
/* 389 */     return (TemplateModel)new SimpleSequence(resultList, (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */   }
/*     */ 
/*     */   
/*     */   private void skipElementsBeforeFirstIndex(TemplateModelIterator targetIter, int firstIdx) throws TemplateModelException {
/* 394 */     int nextIdx = 0;
/* 395 */     while (nextIdx < firstIdx) {
/* 396 */       if (!targetIter.hasNext()) {
/* 397 */         throw new _TemplateModelException(this.keyExpression, new Object[] { "Range start index ", 
/* 398 */               Integer.valueOf(firstIdx), " is out of bounds, as the sliced sequence only has ", 
/* 399 */               Integer.valueOf(nextIdx), " elements." });
/*     */       }
/* 401 */       targetIter.next();
/* 402 */       nextIdx++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private _TemplateModelException newRangeEndOutOfBoundsException(int nextIdx, int lastIdx) {
/* 407 */     return new _TemplateModelException(this.keyExpression, new Object[] { "Range end index ", 
/* 408 */           Integer.valueOf(lastIdx), " is out of bounds, as sliced sequence only has ", 
/* 409 */           Integer.valueOf(nextIdx), " elements." });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TemplateModel getStepMinus1RangeFromIterator(TemplateModelIterator targetIter, RangeModel range, int resultSize) throws TemplateException {
/* 416 */     int highIndex = range.getBegining();
/*     */     
/* 418 */     int lowIndex = Math.max(highIndex - range.size() - 1, 0);
/*     */     
/* 420 */     TemplateModel[] resultElements = new TemplateModel[highIndex - lowIndex + 1];
/*     */     
/* 422 */     int srcIdx = 0;
/* 423 */     int dstIdx = resultElements.length - 1;
/* 424 */     while (srcIdx <= highIndex && targetIter.hasNext()) {
/* 425 */       TemplateModel element = targetIter.next();
/* 426 */       if (srcIdx >= lowIndex) {
/* 427 */         resultElements[dstIdx--] = element;
/*     */       }
/* 429 */       srcIdx++;
/*     */     } 
/* 431 */     if (dstIdx != -1) {
/* 432 */       throw new _MiscTemplateException(this, "Range top index " + highIndex + " (0-based) is outside the sliced sequence of length " + srcIdx + ".");
/*     */     }
/*     */ 
/*     */     
/* 436 */     return (TemplateModel)new SimpleSequence(Arrays.asList(resultElements), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*     */   }
/*     */   
/*     */   private TemplateModel emptyResult(boolean seq) {
/* 440 */     return seq ? (
/* 441 */       (_TemplateAPI.getTemplateLanguageVersionAsInt(this) < _TemplateAPI.VERSION_INT_2_3_21) ? (TemplateModel)new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER) : (TemplateModel)Constants.EMPTY_SEQUENCE) : TemplateScalarModel.EMPTY_STRING;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void enableLazilyGeneratedResult() {
/* 449 */     this.lazilyGeneratedResultEnabled = true;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCanonicalForm() {
/* 454 */     return this.target.getCanonicalForm() + "[" + this.keyExpression
/*     */       
/* 456 */       .getCanonicalForm() + "]";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 462 */     return "...[...]";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isLiteral() {
/* 467 */     return (this.constantValue != null || (this.target.isLiteral() && this.keyExpression.isLiteral()));
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 472 */     return 2;
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 477 */     return (idx == 0) ? this.target : this.keyExpression;
/*     */   }
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 482 */     return (idx == 0) ? ParameterRole.LEFT_HAND_OPERAND : ParameterRole.ENCLOSED_OPERAND;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Expression deepCloneWithIdentifierReplaced_inner(String replacedIdentifier, Expression replacement, Expression.ReplacemenetState replacementState) {
/* 488 */     return new DynamicKeyName(this.target
/* 489 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState), this.keyExpression
/* 490 */         .deepCloneWithIdentifierReplaced(replacedIdentifier, replacement, replacementState));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\DynamicKeyName.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */