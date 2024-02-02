/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateCollectionModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateHashModelEx;
/*     */ import freemarker.template.TemplateHashModelEx2;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelIterator;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.TemplateSequenceModel;
/*     */ import freemarker.template.utility.Constants;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
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
/*     */ final class IteratorBlock
/*     */   extends TemplateElement
/*     */ {
/*     */   private final Expression listedExp;
/*     */   private final String loopVar1Name;
/*     */   private final String loopVar2Name;
/*     */   private final boolean hashListing;
/*     */   private final boolean forEach;
/*     */   
/*     */   IteratorBlock(Expression listedExp, String loopVar1Name, String loopVar2Name, TemplateElements childrenBeforeElse, boolean hashListing, boolean forEach) {
/*  78 */     this.listedExp = listedExp;
/*  79 */     this.loopVar1Name = loopVar1Name;
/*  80 */     this.loopVar2Name = loopVar2Name;
/*  81 */     setChildren(childrenBeforeElse);
/*  82 */     this.hashListing = hashListing;
/*  83 */     this.forEach = forEach;
/*     */     
/*  85 */     listedExp.enableLazilyGeneratedResult();
/*     */   }
/*     */   
/*     */   boolean isHashListing() {
/*  89 */     return this.hashListing;
/*     */   }
/*     */ 
/*     */   
/*     */   TemplateElement[] accept(Environment env) throws TemplateException, IOException {
/*  94 */     acceptWithResult(env);
/*  95 */     return null;
/*     */   }
/*     */   boolean acceptWithResult(Environment env) throws TemplateException, IOException {
/*     */     TemplateSequenceModel templateSequenceModel;
/*  99 */     TemplateModel listedValue = this.listedExp.eval(env);
/* 100 */     if (listedValue == null) {
/* 101 */       if (env.isClassicCompatible()) {
/* 102 */         templateSequenceModel = Constants.EMPTY_SEQUENCE;
/*     */       } else {
/* 104 */         this.listedExp.assertNonNull((TemplateModel)null, env);
/*     */       } 
/*     */     }
/*     */     
/* 108 */     return env.visitIteratorBlock(new IterationContext((TemplateModel)templateSequenceModel, this.loopVar1Name, this.loopVar2Name));
/*     */   }
/*     */ 
/*     */   
/*     */   protected String dump(boolean canonical) {
/* 113 */     StringBuilder buf = new StringBuilder();
/* 114 */     if (canonical) buf.append('<'); 
/* 115 */     buf.append(getNodeTypeSymbol());
/* 116 */     buf.append(' ');
/* 117 */     if (this.forEach) {
/* 118 */       buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar1Name));
/* 119 */       buf.append(" in ");
/* 120 */       buf.append(this.listedExp.getCanonicalForm());
/*     */     } else {
/* 122 */       buf.append(this.listedExp.getCanonicalForm());
/* 123 */       if (this.loopVar1Name != null) {
/* 124 */         buf.append(" as ");
/* 125 */         buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar1Name));
/* 126 */         if (this.loopVar2Name != null) {
/* 127 */           buf.append(", ");
/* 128 */           buf.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar2Name));
/*     */         } 
/*     */       } 
/*     */     } 
/* 132 */     if (canonical) {
/* 133 */       buf.append(">");
/* 134 */       buf.append(getChildrenCanonicalForm());
/* 135 */       if (!(getParentElement() instanceof ListElseContainer)) {
/* 136 */         buf.append("</");
/* 137 */         buf.append(getNodeTypeSymbol());
/* 138 */         buf.append('>');
/*     */       } 
/*     */     } 
/* 141 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   int getParameterCount() {
/* 146 */     return 1 + ((this.loopVar1Name != null) ? 1 : 0) + ((this.loopVar2Name != null) ? 1 : 0);
/*     */   }
/*     */ 
/*     */   
/*     */   Object getParameterValue(int idx) {
/* 151 */     switch (idx) {
/*     */       case 0:
/* 153 */         return this.listedExp;
/*     */       case 1:
/* 155 */         if (this.loopVar1Name == null) throw new IndexOutOfBoundsException(); 
/* 156 */         return this.loopVar1Name;
/*     */       case 2:
/* 158 */         if (this.loopVar2Name == null) throw new IndexOutOfBoundsException(); 
/* 159 */         return this.loopVar2Name;
/* 160 */     }  throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   ParameterRole getParameterRole(int idx) {
/* 166 */     switch (idx) {
/*     */       case 0:
/* 168 */         return ParameterRole.LIST_SOURCE;
/*     */       case 1:
/* 170 */         if (this.loopVar1Name == null) throw new IndexOutOfBoundsException(); 
/* 171 */         return ParameterRole.TARGET_LOOP_VARIABLE;
/*     */       case 2:
/* 173 */         if (this.loopVar2Name == null) throw new IndexOutOfBoundsException(); 
/* 174 */         return ParameterRole.TARGET_LOOP_VARIABLE;
/* 175 */     }  throw new IndexOutOfBoundsException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   String getNodeTypeSymbol() {
/* 181 */     return this.forEach ? "#foreach" : "#list";
/*     */   }
/*     */ 
/*     */   
/*     */   boolean isNestedBlockRepeater() {
/* 186 */     return (this.loopVar1Name != null);
/*     */   }
/*     */ 
/*     */   
/*     */   class IterationContext
/*     */     implements LocalContext
/*     */   {
/*     */     private static final String LOOP_STATE_HAS_NEXT = "_has_next";
/*     */     
/*     */     private static final String LOOP_STATE_INDEX = "_index";
/*     */     
/*     */     private Object openedIterator;
/*     */     private boolean hasNext;
/*     */     private TemplateModel loopVar1Value;
/*     */     private TemplateModel loopVar2Value;
/*     */     private int index;
/*     */     private boolean alreadyEntered;
/* 203 */     private Collection<String> localVarNames = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String loopVar1Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String visibleLoopVar1Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private String loopVar2Name;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private final TemplateModel listedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public IterationContext(TemplateModel listedValue, String loopVar1Name, String loopVar2Name) {
/* 238 */       this.listedValue = listedValue;
/* 239 */       this.loopVar1Name = loopVar1Name;
/* 240 */       this.loopVar2Name = loopVar2Name;
/*     */     }
/*     */     
/*     */     boolean accept(Environment env) throws TemplateException, IOException {
/* 244 */       return executeNestedContent(env, IteratorBlock.this.getChildBuffer());
/*     */     }
/*     */ 
/*     */     
/*     */     void loopForItemsElement(Environment env, TemplateElement[] childBuffer, String loopVarName, String loopVar2Name) throws TemplateException, IOException {
/*     */       try {
/* 250 */         if (this.alreadyEntered) {
/* 251 */           throw new _MiscTemplateException(env, "The #items directive was already entered earlier for this listing.");
/*     */         }
/*     */         
/* 254 */         this.alreadyEntered = true;
/* 255 */         this.loopVar1Name = loopVarName;
/* 256 */         this.loopVar2Name = loopVar2Name;
/* 257 */         executeNestedContent(env, childBuffer);
/*     */       } finally {
/* 259 */         this.loopVar1Name = null;
/* 260 */         this.loopVar2Name = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean executeNestedContent(Environment env, TemplateElement[] childBuffer) throws TemplateException, IOException {
/* 270 */       return !IteratorBlock.this.hashListing ? 
/* 271 */         executedNestedContentForCollOrSeqListing(env, childBuffer) : 
/* 272 */         executedNestedContentForHashListing(env, childBuffer);
/*     */     }
/*     */ 
/*     */     
/*     */     private boolean executedNestedContentForCollOrSeqListing(Environment env, TemplateElement[] childBuffer) throws IOException, TemplateException {
/*     */       boolean listNotEmpty;
/* 278 */       if (this.listedValue instanceof TemplateCollectionModel)
/* 279 */       { TemplateCollectionModel collModel = (TemplateCollectionModel)this.listedValue;
/*     */         
/* 281 */         TemplateModelIterator iterModel = (this.openedIterator == null) ? collModel.iterator() : (TemplateModelIterator)this.openedIterator;
/*     */         
/* 283 */         listNotEmpty = iterModel.hasNext();
/* 284 */         if (listNotEmpty) {
/* 285 */           if (this.loopVar1Name != null) {
/*     */             do {
/* 287 */               this.loopVar1Value = iterModel.next();
/* 288 */               this.hasNext = iterModel.hasNext();
/*     */               
/* 290 */               try { this.visibleLoopVar1Name = this.loopVar1Name;
/* 291 */                 env.visit(childBuffer); }
/* 292 */               catch (BreakOrContinueException br)
/* 293 */               { if (br == BreakOrContinueException.BREAK_INSTANCE)
/*     */                 
/*     */                 { 
/*     */                   
/* 297 */                   this.visibleLoopVar1Name = null; break; }  } finally { this.visibleLoopVar1Name = null; }
/*     */               
/* 299 */               this.index++;
/* 300 */             } while (this.hasNext);
/* 301 */             this.openedIterator = null;
/*     */           }
/*     */           else {
/*     */             
/* 305 */             this.openedIterator = iterModel;
/*     */             
/* 307 */             env.visit(childBuffer);
/*     */           } 
/*     */         } }
/* 310 */       else if (this.listedValue instanceof TemplateSequenceModel)
/* 311 */       { TemplateSequenceModel seqModel = (TemplateSequenceModel)this.listedValue;
/* 312 */         int size = seqModel.size();
/* 313 */         listNotEmpty = (size != 0);
/* 314 */         if (listNotEmpty) {
/* 315 */           if (this.loopVar1Name != null) {
/* 316 */             for (this.index = 0; this.index < size; this.index++) {
/* 317 */               this.loopVar1Value = seqModel.get(this.index);
/* 318 */               this.hasNext = (size > this.index + 1);
/*     */ 
/*     */ 
/*     */             
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */ 
/*     */             
/* 332 */             env.visit(childBuffer);
/*     */           } 
/*     */         } }
/* 335 */       else if (env.isClassicCompatible())
/* 336 */       { listNotEmpty = true;
/* 337 */         if (this.loopVar1Name != null) {
/* 338 */           this.loopVar1Value = this.listedValue;
/* 339 */           this.hasNext = false;
/*     */         } 
/*     */         try {
/* 342 */           this.visibleLoopVar1Name = this.loopVar1Name;
/* 343 */           env.visit(childBuffer);
/* 344 */         } catch (BreakOrContinueException breakOrContinueException) {
/*     */         
/*     */         } finally {
/* 347 */           this.visibleLoopVar1Name = null;
/*     */         }  }
/* 349 */       else { if (this.listedValue instanceof TemplateHashModelEx && 
/* 350 */           !NonSequenceOrCollectionException.isWrappedIterable(this.listedValue)) {
/* 351 */           throw new NonSequenceOrCollectionException(env, new _ErrorDescriptionBuilder(new Object[] { "The value you try to list is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(this.listedValue)), ", thus you must specify two loop variables after the \"as\"; one for the key, and another for the value, like ", "<#... as k, v>", ")." }));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 358 */         throw new NonSequenceOrCollectionException(IteratorBlock.this
/* 359 */             .listedExp, this.listedValue, env); }
/*     */       
/* 361 */       return listNotEmpty;
/*     */     }
/*     */ 
/*     */     
/*     */     private boolean executedNestedContentForHashListing(Environment env, TemplateElement[] childBuffer) throws IOException, TemplateException {
/*     */       boolean hashNotEmpty;
/* 367 */       if (this.listedValue instanceof TemplateHashModelEx) {
/* 368 */         TemplateHashModelEx listedHash = (TemplateHashModelEx)this.listedValue;
/* 369 */         if (listedHash instanceof TemplateHashModelEx2) {
/*     */           
/* 371 */           TemplateHashModelEx2.KeyValuePairIterator kvpIter = (this.openedIterator == null) ? ((TemplateHashModelEx2)listedHash).keyValuePairIterator() : (TemplateHashModelEx2.KeyValuePairIterator)this.openedIterator;
/*     */           
/* 373 */           hashNotEmpty = kvpIter.hasNext();
/* 374 */           if (hashNotEmpty) {
/* 375 */             if (this.loopVar1Name != null) {
/*     */               do {
/* 377 */                 TemplateHashModelEx2.KeyValuePair kvp = kvpIter.next();
/* 378 */                 this.loopVar1Value = kvp.getKey();
/* 379 */                 this.loopVar2Value = kvp.getValue();
/* 380 */                 this.hasNext = kvpIter.hasNext();
/*     */                 
/* 382 */                 try { this.visibleLoopVar1Name = this.loopVar1Name;
/* 383 */                   env.visit(childBuffer); }
/* 384 */                 catch (BreakOrContinueException br)
/* 385 */                 { if (br == BreakOrContinueException.BREAK_INSTANCE)
/*     */                   
/*     */                   { 
/*     */                     
/* 389 */                     this.visibleLoopVar1Name = null; break; }  } finally { this.visibleLoopVar1Name = null; }
/*     */                 
/* 391 */                 this.index++;
/* 392 */               } while (this.hasNext);
/* 393 */               this.openedIterator = null;
/*     */             } else {
/*     */               
/* 396 */               this.openedIterator = kvpIter;
/*     */               
/* 398 */               env.visit(childBuffer);
/*     */             } 
/*     */           }
/*     */         } else {
/* 402 */           TemplateModelIterator keysIter = listedHash.keys().iterator();
/* 403 */           hashNotEmpty = keysIter.hasNext();
/* 404 */           if (hashNotEmpty)
/* 405 */             if (this.loopVar1Name != null) {
/*     */               do {
/* 407 */                 this.loopVar1Value = keysIter.next();
/* 408 */                 if (!(this.loopVar1Value instanceof TemplateScalarModel)) {
/* 409 */                   throw _MessageUtil.newKeyValuePairListingNonStringKeyExceptionMessage(this.loopVar1Value, (TemplateHashModelEx)this.listedValue);
/*     */                 }
/*     */                 
/* 412 */                 this.loopVar2Value = listedHash.get(((TemplateScalarModel)this.loopVar1Value).getAsString());
/* 413 */                 this.hasNext = keysIter.hasNext();
/*     */                 
/* 415 */                 try { this.visibleLoopVar1Name = this.loopVar1Name;
/* 416 */                   env.visit(childBuffer); }
/* 417 */                 catch (BreakOrContinueException br)
/* 418 */                 { if (br == BreakOrContinueException.BREAK_INSTANCE)
/*     */                   
/*     */                   { 
/*     */                     
/* 422 */                     this.visibleLoopVar1Name = null; break; }  } finally { this.visibleLoopVar1Name = null; }
/*     */                 
/* 424 */                 this.index++;
/* 425 */               } while (this.hasNext);
/*     */             } else {
/*     */               
/* 428 */               env.visit(childBuffer);
/*     */             }  
/*     */         } 
/*     */       } else {
/* 432 */         if (this.listedValue instanceof TemplateCollectionModel || this.listedValue instanceof TemplateSequenceModel)
/*     */         {
/* 434 */           throw new NonSequenceOrCollectionException(env, new _ErrorDescriptionBuilder(new Object[] { "The value you try to list is ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(this.listedValue)), ", thus you must specify only one loop variable after the \"as\" (there's no separate key and value)." }));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 441 */         throw new NonExtendedHashException(IteratorBlock.this
/* 442 */             .listedExp, this.listedValue, env);
/*     */       } 
/* 444 */       return hashNotEmpty;
/*     */     }
/*     */     
/*     */     boolean hasVisibleLoopVar(String visibleLoopVarName) {
/* 448 */       String visibleLoopVar1Name = this.visibleLoopVar1Name;
/* 449 */       if (visibleLoopVar1Name == null) {
/* 450 */         return false;
/*     */       }
/* 452 */       return (visibleLoopVarName.equals(visibleLoopVar1Name) || visibleLoopVarName.equals(this.loopVar2Name));
/*     */     }
/*     */ 
/*     */     
/*     */     public TemplateModel getLocalVariable(String name) {
/* 457 */       String visibleLoopVar1Name = this.visibleLoopVar1Name;
/* 458 */       if (visibleLoopVar1Name == null)
/*     */       {
/* 460 */         return null;
/*     */       }
/*     */       
/* 463 */       if (name.startsWith(visibleLoopVar1Name)) {
/* 464 */         switch (name.length() - visibleLoopVar1Name.length()) {
/*     */           case 0:
/* 466 */             return (this.loopVar1Value != null) ? this.loopVar1Value : (
/* 467 */               IteratorBlock.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE);
/*     */           
/*     */           case 6:
/* 470 */             if (name.endsWith("_index")) {
/* 471 */               return (TemplateModel)new SimpleNumber(this.index);
/*     */             }
/*     */             break;
/*     */           case 9:
/* 475 */             if (name.endsWith("_has_next")) {
/* 476 */               return this.hasNext ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */             }
/*     */             break;
/*     */         } 
/*     */       
/*     */       }
/* 482 */       if (name.equals(this.loopVar2Name)) {
/* 483 */         return (this.loopVar2Value != null) ? this.loopVar2Value : (
/* 484 */           IteratorBlock.this.getTemplate().getConfiguration().getFallbackOnNullLoopVariable() ? null : TemplateNullModel.INSTANCE);
/*     */       }
/*     */ 
/*     */       
/* 488 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<String> getLocalVariableNames() {
/* 493 */       String visibleLoopVar1Name = this.visibleLoopVar1Name;
/* 494 */       if (visibleLoopVar1Name != null) {
/* 495 */         if (this.localVarNames == null) {
/* 496 */           this.localVarNames = new ArrayList<>(3);
/* 497 */           this.localVarNames.add(visibleLoopVar1Name);
/* 498 */           this.localVarNames.add(visibleLoopVar1Name + "_index");
/* 499 */           this.localVarNames.add(visibleLoopVar1Name + "_has_next");
/*     */         } 
/* 501 */         return this.localVarNames;
/*     */       } 
/* 503 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */     
/*     */     boolean hasNext() {
/* 508 */       return this.hasNext;
/*     */     }
/*     */     
/*     */     int getIndex() {
/* 512 */       return this.index;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\IteratorBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */