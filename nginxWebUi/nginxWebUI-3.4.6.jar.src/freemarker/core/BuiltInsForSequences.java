/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.ext.beans.CollectionModel;
/*      */ import freemarker.template.ObjectWrapper;
/*      */ import freemarker.template.SimpleNumber;
/*      */ import freemarker.template.SimpleScalar;
/*      */ import freemarker.template.SimpleSequence;
/*      */ import freemarker.template.TemplateBooleanModel;
/*      */ import freemarker.template.TemplateCollectionModel;
/*      */ import freemarker.template.TemplateCollectionModelEx;
/*      */ import freemarker.template.TemplateDateModel;
/*      */ import freemarker.template.TemplateException;
/*      */ import freemarker.template.TemplateHashModel;
/*      */ import freemarker.template.TemplateMethodModelEx;
/*      */ import freemarker.template.TemplateModel;
/*      */ import freemarker.template.TemplateModelException;
/*      */ import freemarker.template.TemplateModelIterator;
/*      */ import freemarker.template.TemplateModelListSequence;
/*      */ import freemarker.template.TemplateNumberModel;
/*      */ import freemarker.template.TemplateScalarModel;
/*      */ import freemarker.template.TemplateSequenceModel;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.Constants;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.io.Serializable;
/*      */ import java.text.Collator;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class BuiltInsForSequences
/*      */ {
/*      */   static class chunkBI
/*      */     extends BuiltInForSequence
/*      */   {
/*      */     private class BIMethod
/*      */       implements TemplateMethodModelEx
/*      */     {
/*      */       private final TemplateSequenceModel tsm;
/*      */       
/*      */       private BIMethod(TemplateSequenceModel tsm) {
/*   64 */         this.tsm = tsm;
/*      */       }
/*      */ 
/*      */       
/*      */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*   69 */         BuiltInsForSequences.chunkBI.this.checkMethodArgCount(args, 1, 2);
/*   70 */         int chunkSize = BuiltInsForSequences.chunkBI.this.getNumberMethodArg(args, 0).intValue();
/*   71 */         if (chunkSize < 1) {
/*   72 */           throw new _TemplateModelException(new Object[] { "The 1st argument to ?", this.this$0.key, " (...) must be at least 1." });
/*      */         }
/*      */         
/*   75 */         return new BuiltInsForSequences.chunkBI.ChunkedSequence(this.tsm, chunkSize, 
/*      */ 
/*      */             
/*   78 */             (args.size() > 1) ? args.get(1) : null);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private static class ChunkedSequence
/*      */       implements TemplateSequenceModel
/*      */     {
/*      */       private final TemplateSequenceModel wrappedTsm;
/*      */       
/*      */       private final int chunkSize;
/*      */       
/*      */       private final TemplateModel fillerItem;
/*      */       
/*      */       private final int numberOfChunks;
/*      */       
/*      */       private ChunkedSequence(TemplateSequenceModel wrappedTsm, int chunkSize, TemplateModel fillerItem) throws TemplateModelException {
/*   95 */         this.wrappedTsm = wrappedTsm;
/*   96 */         this.chunkSize = chunkSize;
/*   97 */         this.fillerItem = fillerItem;
/*   98 */         this.numberOfChunks = (wrappedTsm.size() + chunkSize - 1) / chunkSize;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public TemplateModel get(final int chunkIndex) throws TemplateModelException {
/*  104 */         if (chunkIndex >= this.numberOfChunks) {
/*  105 */           return null;
/*      */         }
/*      */         
/*  108 */         return (TemplateModel)new TemplateSequenceModel()
/*      */           {
/*  110 */             private final int baseIndex = chunkIndex * BuiltInsForSequences.chunkBI.ChunkedSequence.this.chunkSize;
/*      */ 
/*      */ 
/*      */             
/*      */             public TemplateModel get(int relIndex) throws TemplateModelException {
/*  115 */               int absIndex = this.baseIndex + relIndex;
/*  116 */               if (absIndex < BuiltInsForSequences.chunkBI.ChunkedSequence.this.wrappedTsm.size()) {
/*  117 */                 return BuiltInsForSequences.chunkBI.ChunkedSequence.this.wrappedTsm.get(absIndex);
/*      */               }
/*  119 */               return (absIndex < BuiltInsForSequences.chunkBI.ChunkedSequence.this.numberOfChunks * BuiltInsForSequences.chunkBI.ChunkedSequence.this.chunkSize) ? BuiltInsForSequences.chunkBI.ChunkedSequence.this
/*  120 */                 .fillerItem : null;
/*      */             }
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             public int size() throws TemplateModelException {
/*  127 */               return (BuiltInsForSequences.chunkBI.ChunkedSequence.this.fillerItem != null || chunkIndex + 1 < BuiltInsForSequences.chunkBI.ChunkedSequence.this.numberOfChunks) ? BuiltInsForSequences.chunkBI.ChunkedSequence.this
/*  128 */                 .chunkSize : (BuiltInsForSequences.chunkBI.ChunkedSequence.this
/*  129 */                 .wrappedTsm.size() - this.baseIndex);
/*      */             }
/*      */           };
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public int size() throws TemplateModelException {
/*  137 */         return this.numberOfChunks;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
/*  144 */       return (TemplateModel)new BIMethod(tsm);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class firstBI
/*      */     extends BuiltIn
/*      */   {
/*      */     protected void setTarget(Expression target) {
/*  153 */       super.setTarget(target);
/*  154 */       target.enableLazilyGeneratedResult();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  160 */       TemplateModel model = this.target.eval(env);
/*      */ 
/*      */       
/*  163 */       if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model))
/*  164 */         return calculateResultForSequence((TemplateSequenceModel)model); 
/*  165 */       if (model instanceof TemplateCollectionModel) {
/*  166 */         return calculateResultForColletion((TemplateCollectionModel)model);
/*      */       }
/*  168 */       throw new NonSequenceOrCollectionException(this.target, model, env);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private TemplateModel calculateResultForSequence(TemplateSequenceModel seq) throws TemplateModelException {
/*  174 */       if (seq.size() == 0) {
/*  175 */         return null;
/*      */       }
/*  177 */       return seq.get(0);
/*      */     }
/*      */ 
/*      */     
/*      */     private TemplateModel calculateResultForColletion(TemplateCollectionModel coll) throws TemplateModelException {
/*  182 */       TemplateModelIterator iter = coll.iterator();
/*  183 */       if (!iter.hasNext()) {
/*  184 */         return null;
/*      */       }
/*  186 */       return iter.next();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class joinBI
/*      */     extends BuiltInWithDirectCallOptimization
/*      */   {
/*      */     protected void setDirectlyCalled() {
/*  195 */       this.target.enableLazilyGeneratedResult();
/*      */     }
/*      */     
/*      */     private class BIMethodForCollection
/*      */       implements TemplateMethodModelEx {
/*      */       private final Environment env;
/*      */       private final TemplateCollectionModel coll;
/*      */       
/*      */       private BIMethodForCollection(Environment env, TemplateCollectionModel coll) {
/*  204 */         this.env = env;
/*  205 */         this.coll = coll;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object exec(List args) throws TemplateModelException {
/*  211 */         BuiltInsForSequences.joinBI.this.checkMethodArgCount(args, 1, 3);
/*  212 */         String separator = BuiltInsForSequences.joinBI.this.getStringMethodArg(args, 0);
/*  213 */         String whenEmpty = BuiltInsForSequences.joinBI.this.getOptStringMethodArg(args, 1);
/*  214 */         String afterLast = BuiltInsForSequences.joinBI.this.getOptStringMethodArg(args, 2);
/*      */         
/*  216 */         StringBuilder sb = new StringBuilder();
/*      */         
/*  218 */         TemplateModelIterator it = this.coll.iterator();
/*      */         
/*  220 */         int idx = 0;
/*  221 */         boolean hadItem = false;
/*  222 */         while (it.hasNext()) {
/*  223 */           TemplateModel item = it.next();
/*  224 */           if (item != null) {
/*  225 */             if (hadItem) {
/*  226 */               sb.append(separator);
/*      */             } else {
/*  228 */               hadItem = true;
/*      */             } 
/*      */             try {
/*  231 */               sb.append(EvalUtil.coerceModelToStringOrUnsupportedMarkup(item, null, null, this.env));
/*  232 */             } catch (TemplateException e) {
/*  233 */               throw new _TemplateModelException(e, new Object[] { "\"?", this.this$0.key, "\" failed at index ", 
/*  234 */                     Integer.valueOf(idx), " with this error:\n\n", "---begin-message---\n", new _DelayedGetMessageWithoutStackTop(e), "\n---end-message---" });
/*      */             } 
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/*  240 */           idx++;
/*      */         } 
/*  242 */         if (hadItem)
/*  243 */         { if (afterLast != null) sb.append(afterLast);
/*      */            }
/*  245 */         else if (whenEmpty != null) { sb.append(whenEmpty); }
/*      */         
/*  247 */         return new SimpleScalar(sb.toString());
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  254 */       TemplateModel model = this.target.eval(env);
/*  255 */       if (model instanceof TemplateCollectionModel) {
/*  256 */         BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
/*  257 */         return (TemplateModel)new BIMethodForCollection(env, (TemplateCollectionModel)model);
/*  258 */       }  if (model instanceof TemplateSequenceModel) {
/*  259 */         return (TemplateModel)new BIMethodForCollection(env, new CollectionAndSequence((TemplateSequenceModel)model));
/*      */       }
/*  261 */       throw new NonSequenceOrCollectionException(this.target, model, env);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   static class lastBI
/*      */     extends BuiltInForSequence
/*      */   {
/*      */     TemplateModel calculateResult(TemplateSequenceModel tsm) throws TemplateModelException {
/*  271 */       int size = tsm.size();
/*  272 */       if (size == 0) {
/*  273 */         return null;
/*      */       }
/*  275 */       return tsm.get(size - 1);
/*      */     }
/*      */   }
/*      */   
/*      */   static class reverseBI extends BuiltInForSequence {
/*      */     private static class ReverseSequence implements TemplateSequenceModel {
/*      */       private final TemplateSequenceModel seq;
/*      */       
/*      */       ReverseSequence(TemplateSequenceModel seq) {
/*  284 */         this.seq = seq;
/*      */       }
/*      */ 
/*      */       
/*      */       public TemplateModel get(int index) throws TemplateModelException {
/*  289 */         return this.seq.get(this.seq.size() - 1 - index);
/*      */       }
/*      */ 
/*      */       
/*      */       public int size() throws TemplateModelException {
/*  294 */         return this.seq.size();
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     TemplateModel calculateResult(TemplateSequenceModel tsm) {
/*  300 */       if (tsm instanceof ReverseSequence) {
/*  301 */         return (TemplateModel)((ReverseSequence)tsm).seq;
/*      */       }
/*  303 */       return (TemplateModel)new ReverseSequence(tsm);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   static class seq_containsBI
/*      */     extends BuiltInWithDirectCallOptimization
/*      */   {
/*      */     protected void setDirectlyCalled() {
/*  312 */       this.target.enableLazilyGeneratedResult();
/*      */     }
/*      */     
/*      */     private class BIMethodForCollection implements TemplateMethodModelEx {
/*      */       private TemplateCollectionModel m_coll;
/*      */       private Environment m_env;
/*      */       
/*      */       private BIMethodForCollection(TemplateCollectionModel coll, Environment env) {
/*  320 */         this.m_coll = coll;
/*  321 */         this.m_env = env;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*  327 */         BuiltInsForSequences.seq_containsBI.this.checkMethodArgCount(args, 1);
/*  328 */         TemplateModel arg = args.get(0);
/*  329 */         TemplateModelIterator it = this.m_coll.iterator();
/*  330 */         int idx = 0;
/*  331 */         while (it.hasNext()) {
/*  332 */           if (BuiltInsForSequences.modelsEqual(idx, it.next(), arg, this.m_env))
/*  333 */             return TemplateBooleanModel.TRUE; 
/*  334 */           idx++;
/*      */         } 
/*  336 */         return TemplateBooleanModel.FALSE;
/*      */       }
/*      */     }
/*      */     
/*      */     private class BIMethodForSequence
/*      */       implements TemplateMethodModelEx {
/*      */       private TemplateSequenceModel m_seq;
/*      */       private Environment m_env;
/*      */       
/*      */       private BIMethodForSequence(TemplateSequenceModel seq, Environment env) {
/*  346 */         this.m_seq = seq;
/*  347 */         this.m_env = env;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public Object exec(List<TemplateModel> args) throws TemplateModelException {
/*  353 */         BuiltInsForSequences.seq_containsBI.this.checkMethodArgCount(args, 1);
/*  354 */         TemplateModel arg = args.get(0);
/*  355 */         int size = this.m_seq.size();
/*  356 */         for (int i = 0; i < size; i++) {
/*  357 */           if (BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), arg, this.m_env))
/*  358 */             return TemplateBooleanModel.TRUE; 
/*      */         } 
/*  360 */         return TemplateBooleanModel.FALSE;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  368 */       TemplateModel model = this.target.eval(env);
/*      */ 
/*      */       
/*  371 */       if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model))
/*  372 */         return (TemplateModel)new BIMethodForSequence((TemplateSequenceModel)model, env); 
/*  373 */       if (model instanceof TemplateCollectionModel) {
/*  374 */         return (TemplateModel)new BIMethodForCollection((TemplateCollectionModel)model, env);
/*      */       }
/*  376 */       throw new NonSequenceOrCollectionException(this.target, model, env);
/*      */     }
/*      */   }
/*      */   
/*      */   static class seq_index_ofBI
/*      */     extends BuiltInWithDirectCallOptimization
/*      */   {
/*      */     private boolean findFirst;
/*      */     
/*      */     protected void setDirectlyCalled() {
/*  386 */       this.target.enableLazilyGeneratedResult();
/*      */     }
/*      */     
/*      */     private class BIMethod
/*      */       implements TemplateMethodModelEx
/*      */     {
/*      */       protected final TemplateSequenceModel m_seq;
/*      */       protected final TemplateCollectionModel m_col;
/*      */       protected final Environment m_env;
/*      */       
/*      */       private BIMethod(Environment env) throws TemplateException {
/*  397 */         TemplateModel model = BuiltInsForSequences.seq_index_ofBI.this.target.eval(env);
/*  398 */         this
/*  399 */           .m_seq = (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) ? (TemplateSequenceModel)model : null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  409 */         this.m_col = (this.m_seq == null && model instanceof TemplateCollectionModel) ? (TemplateCollectionModel)model : null;
/*      */ 
/*      */         
/*  412 */         if (this.m_seq == null && this.m_col == null) {
/*  413 */           throw new NonSequenceOrCollectionException(BuiltInsForSequences.seq_index_ofBI.this.target, model, env);
/*      */         }
/*      */         
/*  416 */         this.m_env = env;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       public final Object exec(List<TemplateModel> args) throws TemplateModelException {
/*  422 */         int foundAtIdx, argCnt = args.size();
/*  423 */         BuiltInsForSequences.seq_index_ofBI.this.checkMethodArgCount(argCnt, 1, 2);
/*      */         
/*  425 */         TemplateModel searched = args.get(0);
/*      */         
/*  427 */         if (argCnt > 1) {
/*  428 */           int startIndex = BuiltInsForSequences.seq_index_ofBI.this.getNumberMethodArg(args, 1).intValue();
/*      */ 
/*      */ 
/*      */           
/*  432 */           foundAtIdx = (this.m_seq != null) ? findInSeq(searched, startIndex) : findInCol(searched, startIndex);
/*      */         }
/*      */         else {
/*      */           
/*  436 */           foundAtIdx = (this.m_seq != null) ? findInSeq(searched) : findInCol(searched);
/*      */         } 
/*  438 */         return (foundAtIdx == -1) ? Constants.MINUS_ONE : new SimpleNumber(foundAtIdx);
/*      */       }
/*      */       
/*      */       int findInCol(TemplateModel searched) throws TemplateModelException {
/*  442 */         return findInCol(searched, 0, 2147483647);
/*      */       }
/*      */ 
/*      */       
/*      */       protected int findInCol(TemplateModel searched, int startIndex) throws TemplateModelException {
/*  447 */         if (BuiltInsForSequences.seq_index_ofBI.this.findFirst) {
/*  448 */           return findInCol(searched, startIndex, 2147483647);
/*      */         }
/*  450 */         return findInCol(searched, 0, startIndex);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       protected int findInCol(TemplateModel searched, int allowedRangeStart, int allowedRangeEnd) throws TemplateModelException {
/*  457 */         if (allowedRangeEnd < 0) return -1;
/*      */         
/*  459 */         TemplateModelIterator it = this.m_col.iterator();
/*      */         
/*  461 */         int foundAtIdx = -1;
/*  462 */         int idx = 0;
/*  463 */         while (it.hasNext() && 
/*  464 */           idx <= allowedRangeEnd) {
/*      */           
/*  466 */           TemplateModel current = it.next();
/*  467 */           if (idx >= allowedRangeStart && BuiltInsForSequences
/*  468 */             .modelsEqual(idx, current, searched, this.m_env)) {
/*  469 */             foundAtIdx = idx;
/*      */             
/*  471 */             if (BuiltInsForSequences.seq_index_ofBI.this.findFirst) {
/*      */               break;
/*      */             }
/*      */           } 
/*      */           
/*  476 */           idx++;
/*      */         } 
/*  478 */         return foundAtIdx;
/*      */       }
/*      */ 
/*      */       
/*      */       int findInSeq(TemplateModel searched) throws TemplateModelException {
/*  483 */         int actualStartIndex, seqSize = this.m_seq.size();
/*      */ 
/*      */         
/*  486 */         if (BuiltInsForSequences.seq_index_ofBI.this.findFirst) {
/*  487 */           actualStartIndex = 0;
/*      */         } else {
/*  489 */           actualStartIndex = seqSize - 1;
/*      */         } 
/*      */         
/*  492 */         return findInSeq(searched, actualStartIndex, seqSize);
/*      */       }
/*      */ 
/*      */       
/*      */       private int findInSeq(TemplateModel searched, int startIndex) throws TemplateModelException {
/*  497 */         int seqSize = this.m_seq.size();
/*      */         
/*  499 */         if (BuiltInsForSequences.seq_index_ofBI.this.findFirst) {
/*  500 */           if (startIndex >= seqSize) {
/*  501 */             return -1;
/*      */           }
/*  503 */           if (startIndex < 0) {
/*  504 */             startIndex = 0;
/*      */           }
/*      */         } else {
/*  507 */           if (startIndex >= seqSize) {
/*  508 */             startIndex = seqSize - 1;
/*      */           }
/*  510 */           if (startIndex < 0) {
/*  511 */             return -1;
/*      */           }
/*      */         } 
/*      */         
/*  515 */         return findInSeq(searched, startIndex, seqSize);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*      */       private int findInSeq(TemplateModel target, int scanStartIndex, int seqSize) throws TemplateModelException {
/*  521 */         if (BuiltInsForSequences.seq_index_ofBI.this.findFirst) {
/*  522 */           for (int i = scanStartIndex; i < seqSize; i++) {
/*  523 */             if (BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), target, this.m_env)) return i; 
/*      */           } 
/*      */         } else {
/*  526 */           for (int i = scanStartIndex; i >= 0; i--) {
/*  527 */             if (BuiltInsForSequences.modelsEqual(i, this.m_seq.get(i), target, this.m_env)) return i; 
/*      */           } 
/*      */         } 
/*  530 */         return -1;
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     seq_index_ofBI(boolean findFirst) {
/*  538 */       this.findFirst = findFirst;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  544 */       return (TemplateModel)new BIMethod(env);
/*      */     }
/*      */   }
/*      */   
/*      */   static class sort_byBI extends sortBI {
/*      */     class BIMethod implements TemplateMethodModelEx {
/*      */       TemplateSequenceModel seq;
/*      */       
/*      */       BIMethod(TemplateSequenceModel seq) {
/*  553 */         this.seq = seq;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       public Object exec(List args) throws TemplateModelException {
/*      */         String[] subvars;
/*  562 */         if (args.size() < 1) throw _MessageUtil.newArgCntError("?" + BuiltInsForSequences.sort_byBI.this.key, args.size(), 1);
/*      */ 
/*      */         
/*  565 */         Object obj = args.get(0);
/*  566 */         if (obj instanceof TemplateScalarModel) {
/*  567 */           subvars = new String[] { ((TemplateScalarModel)obj).getAsString() };
/*  568 */         } else if (obj instanceof TemplateSequenceModel) {
/*  569 */           TemplateSequenceModel seq = (TemplateSequenceModel)obj;
/*  570 */           int ln = seq.size();
/*  571 */           subvars = new String[ln];
/*  572 */           for (int i = 0; i < ln; i++) {
/*  573 */             Object item = seq.get(i);
/*      */             try {
/*  575 */               subvars[i] = ((TemplateScalarModel)item)
/*  576 */                 .getAsString();
/*  577 */             } catch (ClassCastException e) {
/*  578 */               if (!(item instanceof TemplateScalarModel)) {
/*  579 */                 throw new _TemplateModelException(new Object[] { "The argument to ?", this.this$0.key, "(key), when it's a sequence, must be a sequence of strings, but the item at index ", 
/*      */                       
/*  581 */                       Integer.valueOf(i), " is not a string." });
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } else {
/*      */           
/*  587 */           throw new _TemplateModelException(new Object[] { "The argument to ?", this.this$0.key, "(key) must be a string (the name of the subvariable), or a sequence of strings (the \"path\" to the subvariable)." });
/*      */         } 
/*      */ 
/*      */         
/*  591 */         return BuiltInsForSequences.sortBI.sort(this.seq, subvars);
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     TemplateModel calculateResult(TemplateSequenceModel seq) {
/*  597 */       return (TemplateModel)new BIMethod(seq);
/*      */     }
/*      */   }
/*      */   
/*      */   static class sortBI extends BuiltInForSequence { static final int KEY_TYPE_NOT_YET_DETECTED = 0;
/*      */     static final int KEY_TYPE_STRING = 1;
/*      */     static final int KEY_TYPE_NUMBER = 2;
/*      */     static final int KEY_TYPE_DATE = 3;
/*      */     static final int KEY_TYPE_BOOLEAN = 4;
/*      */     
/*      */     private static class BooleanKVPComparator implements Comparator, Serializable { public int compare(Object arg0, Object arg1) {
/*  608 */         boolean b0 = ((Boolean)((BuiltInsForSequences.sortBI.KVP)arg0).key).booleanValue();
/*  609 */         boolean b1 = ((Boolean)((BuiltInsForSequences.sortBI.KVP)arg1).key).booleanValue();
/*  610 */         if (b0) {
/*  611 */           return b1 ? 0 : 1;
/*      */         }
/*  613 */         return b1 ? -1 : 0;
/*      */       }
/*      */       
/*      */       private BooleanKVPComparator() {} }
/*      */     
/*      */     private static class DateKVPComparator implements Comparator, Serializable { private DateKVPComparator() {}
/*      */       
/*      */       public int compare(Object arg0, Object arg1) {
/*  621 */         return ((Date)((BuiltInsForSequences.sortBI.KVP)arg0).key).compareTo(
/*  622 */             (Date)((BuiltInsForSequences.sortBI.KVP)arg1).key);
/*      */       } }
/*      */ 
/*      */ 
/*      */     
/*      */     private static class KVP
/*      */     {
/*      */       private Object key;
/*      */       private Object value;
/*      */       
/*      */       private KVP(Object key, Object value) {
/*  633 */         this.key = key;
/*  634 */         this.value = value;
/*      */       }
/*      */     }
/*      */     
/*      */     private static class LexicalKVPComparator implements Comparator { private Collator collator;
/*      */       
/*      */       LexicalKVPComparator(Collator collator) {
/*  641 */         this.collator = collator;
/*      */       }
/*      */ 
/*      */       
/*      */       public int compare(Object arg0, Object arg1) {
/*  646 */         return this.collator.compare(((BuiltInsForSequences.sortBI.KVP)arg0)
/*  647 */             .key, ((BuiltInsForSequences.sortBI.KVP)arg1).key);
/*      */       } }
/*      */     
/*      */     private static class NumericalKVPComparator implements Comparator {
/*      */       private ArithmeticEngine ae;
/*      */       
/*      */       private NumericalKVPComparator(ArithmeticEngine ae) {
/*  654 */         this.ae = ae;
/*      */       }
/*      */ 
/*      */       
/*      */       public int compare(Object arg0, Object arg1) {
/*      */         try {
/*  660 */           return this.ae.compareNumbers(
/*  661 */               (Number)((BuiltInsForSequences.sortBI.KVP)arg0).key, 
/*  662 */               (Number)((BuiltInsForSequences.sortBI.KVP)arg1).key);
/*  663 */         } catch (TemplateException e) {
/*  664 */           throw new ClassCastException("Failed to compare numbers: " + e);
/*      */         } 
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     static TemplateModelException newInconsistentSortKeyTypeException(int keyNamesLn, String firstType, String firstTypePlural, int index, TemplateModel key) {
/*      */       String valueInMsg;
/*      */       String valuesInMsg;
/*  674 */       if (keyNamesLn == 0) {
/*  675 */         valueInMsg = "value";
/*  676 */         valuesInMsg = "values";
/*      */       } else {
/*  678 */         valueInMsg = "key value";
/*  679 */         valuesInMsg = "key values";
/*      */       } 
/*  681 */       return new _TemplateModelException(new Object[] { 
/*  682 */             startErrorMessage(keyNamesLn, index), "All ", valuesInMsg, " in the sequence must be ", firstTypePlural, ", because the first ", valueInMsg, " was that. However, the ", valueInMsg, " of the current item isn't a ", firstType, " but a ", new _DelayedFTLTypeDescription(key), "." });
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static TemplateSequenceModel sort(TemplateSequenceModel seq, String[] keyNames) throws TemplateModelException {
/*  705 */       int ln = seq.size();
/*  706 */       if (ln == 0) return seq;
/*      */       
/*  708 */       ArrayList<KVP> res = new ArrayList(ln);
/*      */       
/*  710 */       int keyNamesLn = (keyNames == null) ? 0 : keyNames.length;
/*      */ 
/*      */       
/*  713 */       int keyType = 0;
/*  714 */       Comparator<? super KVP> keyComparator = null; int i;
/*  715 */       for (i = 0; i < ln; i++) {
/*  716 */         TemplateModel item = seq.get(i);
/*  717 */         TemplateModel key = item;
/*  718 */         for (int keyNameI = 0; keyNameI < keyNamesLn; keyNameI++) {
/*      */           try {
/*  720 */             key = ((TemplateHashModel)key).get(keyNames[keyNameI]);
/*  721 */           } catch (ClassCastException e) {
/*  722 */             if (!(key instanceof TemplateHashModel)) {
/*  723 */               throw new _TemplateModelException(new Object[] {
/*  724 */                     startErrorMessage(keyNamesLn, i), (keyNameI == 0) ? "Sequence items must be hashes when using ?sort_by. " : ("The " + 
/*      */ 
/*      */                     
/*  727 */                     StringUtil.jQuote(keyNames[keyNameI - 1])), " subvariable is not a hash, so ?sort_by ", "can't proceed with getting the ", new _DelayedJQuote(keyNames[keyNameI]), " subvariable."
/*      */                   });
/*      */             }
/*      */ 
/*      */ 
/*      */             
/*  733 */             throw e;
/*      */           } 
/*      */           
/*  736 */           if (key == null) {
/*  737 */             throw new _TemplateModelException(new Object[] {
/*  738 */                   startErrorMessage(keyNamesLn, i), "The " + 
/*  739 */                   StringUtil.jQuote(keyNames[keyNameI]), " subvariable was null or missing."
/*      */                 });
/*      */           }
/*      */         } 
/*  743 */         if (keyType == 0) {
/*  744 */           if (key instanceof TemplateScalarModel) {
/*  745 */             keyType = 1;
/*      */             
/*  747 */             keyComparator = new LexicalKVPComparator(Environment.getCurrentEnvironment().getCollator());
/*  748 */           } else if (key instanceof TemplateNumberModel) {
/*  749 */             keyType = 2;
/*      */ 
/*      */             
/*  752 */             keyComparator = new NumericalKVPComparator(Environment.getCurrentEnvironment().getArithmeticEngine());
/*  753 */           } else if (key instanceof TemplateDateModel) {
/*  754 */             keyType = 3;
/*  755 */             keyComparator = new DateKVPComparator();
/*  756 */           } else if (key instanceof TemplateBooleanModel) {
/*  757 */             keyType = 4;
/*  758 */             keyComparator = new BooleanKVPComparator();
/*      */           } else {
/*  760 */             throw new _TemplateModelException(new Object[] {
/*  761 */                   startErrorMessage(keyNamesLn, i), "Values used for sorting must be numbers, strings, date/times or booleans."
/*      */                 });
/*      */           } 
/*      */         }
/*  765 */         switch (keyType) {
/*      */           case 1:
/*      */             try {
/*  768 */               res.add(new KVP(((TemplateScalarModel)key)
/*  769 */                     .getAsString(), item));
/*      */             }
/*  771 */             catch (ClassCastException e) {
/*  772 */               if (!(key instanceof TemplateScalarModel)) {
/*  773 */                 throw newInconsistentSortKeyTypeException(keyNamesLn, "string", "strings", i, key);
/*      */               }
/*      */               
/*  776 */               throw e;
/*      */             } 
/*      */             break;
/*      */ 
/*      */           
/*      */           case 2:
/*      */             try {
/*  783 */               res.add(new KVP(((TemplateNumberModel)key)
/*  784 */                     .getAsNumber(), item));
/*      */             }
/*  786 */             catch (ClassCastException e) {
/*  787 */               if (!(key instanceof TemplateNumberModel)) {
/*  788 */                 throw newInconsistentSortKeyTypeException(keyNamesLn, "number", "numbers", i, key);
/*      */               }
/*      */             } 
/*      */             break;
/*      */ 
/*      */           
/*      */           case 3:
/*      */             try {
/*  796 */               res.add(new KVP(((TemplateDateModel)key)
/*  797 */                     .getAsDate(), item));
/*      */             }
/*  799 */             catch (ClassCastException e) {
/*  800 */               if (!(key instanceof TemplateDateModel)) {
/*  801 */                 throw newInconsistentSortKeyTypeException(keyNamesLn, "date/time", "date/times", i, key);
/*      */               }
/*      */             } 
/*      */             break;
/*      */ 
/*      */           
/*      */           case 4:
/*      */             try {
/*  809 */               res.add(new KVP(
/*  810 */                     Boolean.valueOf(((TemplateBooleanModel)key).getAsBoolean()), item));
/*      */             }
/*  812 */             catch (ClassCastException e) {
/*  813 */               if (!(key instanceof TemplateBooleanModel)) {
/*  814 */                 throw newInconsistentSortKeyTypeException(keyNamesLn, "boolean", "booleans", i, key);
/*      */               }
/*      */             } 
/*      */             break;
/*      */ 
/*      */           
/*      */           default:
/*  821 */             throw new BugException("Unexpected key type");
/*      */         } 
/*      */ 
/*      */       
/*      */       } 
/*      */       try {
/*  827 */         Collections.sort(res, keyComparator);
/*  828 */       } catch (Exception exc) {
/*  829 */         throw new _TemplateModelException(exc, new Object[] {
/*  830 */               startErrorMessage(keyNamesLn), "Unexpected error while sorting:" + exc
/*      */             });
/*      */       } 
/*      */       
/*  834 */       for (i = 0; i < ln; i++) {
/*  835 */         res.set(i, (res.get(i)).value);
/*      */       }
/*      */       
/*  838 */       return (TemplateSequenceModel)new TemplateModelListSequence(res);
/*      */     }
/*      */     
/*      */     static Object[] startErrorMessage(int keyNamesLn) {
/*  842 */       return new Object[] { (keyNamesLn == 0) ? "?sort" : "?sort_by(...)", " failed: " };
/*      */     }
/*      */     
/*      */     static Object[] startErrorMessage(int keyNamesLn, int index) {
/*  846 */       return new Object[] { (keyNamesLn == 0) ? "?sort" : "?sort_by(...)", " failed at sequence index ", 
/*      */           
/*  848 */           Integer.valueOf(index), (index == 0) ? ": " : " (0-based): " };
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel calculateResult(TemplateSequenceModel seq) throws TemplateModelException {
/*  865 */       return (TemplateModel)sort(seq, null);
/*      */     } }
/*      */ 
/*      */ 
/*      */   
/*      */   static class sequenceBI
/*      */     extends BuiltIn
/*      */   {
/*      */     private boolean lazilyGeneratedResultEnabled;
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  876 */       TemplateModel model = this.target.eval(env);
/*      */       
/*  878 */       if (model instanceof TemplateSequenceModel && !BuiltInsForSequences.isBuggySeqButGoodCollection(model)) {
/*  879 */         return model;
/*      */       }
/*      */       
/*  882 */       if (!(model instanceof TemplateCollectionModel)) {
/*  883 */         throw new NonSequenceOrCollectionException(this.target, model, env);
/*      */       }
/*  885 */       TemplateCollectionModel coll = (TemplateCollectionModel)model;
/*      */       
/*  887 */       if (!this.lazilyGeneratedResultEnabled) {
/*      */ 
/*      */ 
/*      */         
/*  891 */         SimpleSequence seq = (coll instanceof TemplateCollectionModelEx) ? new SimpleSequence(((TemplateCollectionModelEx)coll).size(), (ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER) : new SimpleSequence((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/*      */ 
/*      */         
/*  894 */         for (TemplateModelIterator iter = coll.iterator(); iter.hasNext();) {
/*  895 */           seq.add(iter.next());
/*      */         }
/*  897 */         return (TemplateModel)seq;
/*      */       } 
/*  899 */       return (coll instanceof LazilyGeneratedCollectionModel) ? (TemplateModel)((LazilyGeneratedCollectionModel)coll)
/*  900 */         .withIsSequenceTrue() : ((coll instanceof TemplateCollectionModelEx) ? (TemplateModel)new LazilyGeneratedCollectionModelWithSameSizeCollEx(new LazyCollectionTemplateModelIterator(coll), (TemplateCollectionModelEx)coll, true) : (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(new LazyCollectionTemplateModelIterator(coll), true));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void enableLazilyGeneratedResult() {
/*  912 */       this.lazilyGeneratedResultEnabled = true;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void setTarget(Expression target) {
/*  917 */       super.setTarget(target);
/*  918 */       target.enableLazilyGeneratedResult();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isBuggySeqButGoodCollection(TemplateModel model) {
/*  924 */     return (model instanceof CollectionModel) ? (
/*  925 */       !((CollectionModel)model).getSupportsIndexedAccess()) : false;
/*      */   }
/*      */ 
/*      */   
/*      */   private static void checkNotRightUnboundedNumericalRange(TemplateModel model) throws TemplateModelException {
/*  930 */     if (model instanceof RightUnboundedRangeModel) {
/*  931 */       throw new _TemplateModelException("The input sequence is a right-unbounded numerical range, thus, it's infinitely long, and can't processed with this built-in.");
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean modelsEqual(int seqItemIndex, TemplateModel seqItem, TemplateModel searchedItem, Environment env) throws TemplateModelException {
/*      */     try {
/*  942 */       return EvalUtil.compare(seqItem, null, 1, null, searchedItem, null, null, false, true, true, true, env);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  949 */     catch (TemplateException ex) {
/*  950 */       throw new _TemplateModelException(ex, new Object[] { "This error has occurred when comparing sequence item at 0-based index ", 
/*  951 */             Integer.valueOf(seqItemIndex), " to the searched item:\n", new _DelayedGetMessage(ex) });
/*      */     } 
/*      */   }
/*      */   
/*      */   private static abstract class MinOrMaxBI
/*      */     extends BuiltIn
/*      */   {
/*      */     private final int comparatorOperator;
/*      */     
/*      */     protected MinOrMaxBI(int comparatorOperator) {
/*  961 */       this.comparatorOperator = comparatorOperator;
/*      */     }
/*      */ 
/*      */     
/*      */     protected void setTarget(Expression target) {
/*  966 */       super.setTarget(target);
/*  967 */       target.enableLazilyGeneratedResult();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     TemplateModel _eval(Environment env) throws TemplateException {
/*  973 */       TemplateModel model = this.target.eval(env);
/*  974 */       if (model instanceof TemplateCollectionModel) {
/*  975 */         BuiltInsForSequences.checkNotRightUnboundedNumericalRange(model);
/*  976 */         return calculateResultForCollection((TemplateCollectionModel)model, env);
/*  977 */       }  if (model instanceof TemplateSequenceModel) {
/*  978 */         return calculateResultForSequence((TemplateSequenceModel)model, env);
/*      */       }
/*  980 */       throw new NonSequenceOrCollectionException(this.target, model, env);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     private TemplateModel calculateResultForCollection(TemplateCollectionModel coll, Environment env) throws TemplateException {
/*  986 */       TemplateModel best = null;
/*  987 */       TemplateModelIterator iter = coll.iterator();
/*  988 */       while (iter.hasNext()) {
/*  989 */         TemplateModel cur = iter.next();
/*  990 */         if (cur != null && (best == null || 
/*  991 */           EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env)))
/*      */         {
/*  993 */           best = cur;
/*      */         }
/*      */       } 
/*  996 */       return best;
/*      */     }
/*      */ 
/*      */     
/*      */     private TemplateModel calculateResultForSequence(TemplateSequenceModel seq, Environment env) throws TemplateException {
/* 1001 */       TemplateModel best = null;
/* 1002 */       for (int i = 0; i < seq.size(); i++) {
/* 1003 */         TemplateModel cur = seq.get(i);
/* 1004 */         if (cur != null && (best == null || 
/* 1005 */           EvalUtil.compare(cur, null, this.comparatorOperator, null, best, null, this, true, false, false, false, env)))
/*      */         {
/* 1007 */           best = cur;
/*      */         }
/*      */       } 
/* 1010 */       return best;
/*      */     }
/*      */   }
/*      */   
/*      */   static class maxBI
/*      */     extends MinOrMaxBI
/*      */   {
/*      */     public maxBI() {
/* 1018 */       super(4);
/*      */     }
/*      */   }
/*      */   
/*      */   static class minBI
/*      */     extends MinOrMaxBI
/*      */   {
/*      */     public minBI() {
/* 1026 */       super(3);
/*      */     }
/*      */   }
/*      */   
/*      */   private static abstract class FilterLikeBI
/*      */     extends IntermediateStreamOperationLikeBuiltIn {
/*      */     private FilterLikeBI() {}
/*      */     
/*      */     protected final boolean elementMatches(TemplateModel element, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
/* 1035 */       TemplateModel transformedElement = elementTransformer.transformElement(element, env);
/* 1036 */       if (!(transformedElement instanceof TemplateBooleanModel)) {
/* 1037 */         if (transformedElement == null) {
/* 1038 */           throw new _TemplateModelException(getElementTransformerExp(), env, "The filter expression has returned no value (has returned null), rather than a boolean.");
/*      */         }
/*      */ 
/*      */         
/* 1042 */         throw new _TemplateModelException(getElementTransformerExp(), env, new Object[] { "The filter expression had to return a boolean value, but it returned ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(transformedElement)), " instead." });
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1047 */       return ((TemplateBooleanModel)transformedElement).getAsBoolean();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class filterBI
/*      */     extends FilterLikeBI
/*      */   {
/*      */     protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
/* 1058 */       if (!isLazilyGeneratedResultEnabled()) {
/* 1059 */         if (!lhoIsSequence) {
/* 1060 */           throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
/*      */         }
/*      */         
/* 1063 */         List<TemplateModel> resultList = new ArrayList<>();
/* 1064 */         while (lhoIterator.hasNext()) {
/* 1065 */           TemplateModel element = lhoIterator.next();
/* 1066 */           if (elementMatches(element, elementTransformer, env)) {
/* 1067 */             resultList.add(element);
/*      */           }
/*      */         } 
/* 1070 */         return (TemplateModel)new TemplateModelListSequence(resultList);
/*      */       } 
/* 1072 */       return (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator()
/*      */           {
/*      */             boolean prefetchDone;
/*      */             
/*      */             TemplateModel prefetchedElement;
/*      */             boolean prefetchedEndOfIterator;
/*      */             
/*      */             public TemplateModel next() throws TemplateModelException {
/* 1080 */               ensurePrefetchDone();
/* 1081 */               if (this.prefetchedEndOfIterator) {
/* 1082 */                 throw new IllegalStateException("next() was called when hasNext() is false");
/*      */               }
/* 1084 */               this.prefetchDone = false;
/* 1085 */               return this.prefetchedElement;
/*      */             }
/*      */ 
/*      */             
/*      */             public boolean hasNext() throws TemplateModelException {
/* 1090 */               ensurePrefetchDone();
/* 1091 */               return !this.prefetchedEndOfIterator;
/*      */             }
/*      */             
/*      */             private void ensurePrefetchDone() throws TemplateModelException {
/* 1095 */               if (this.prefetchDone) {
/*      */                 return;
/*      */               }
/*      */               
/* 1099 */               boolean conclusionReached = false;
/*      */               while (true) {
/* 1101 */                 if (lhoIterator.hasNext()) {
/* 1102 */                   boolean elementMatched; TemplateModel element = lhoIterator.next();
/*      */                   
/*      */                   try {
/* 1105 */                     elementMatched = BuiltInsForSequences.filterBI.this.elementMatches(element, elementTransformer, env);
/* 1106 */                   } catch (TemplateException e) {
/* 1107 */                     throw new _TemplateModelException(e, env, "Failed to transform element");
/*      */                   } 
/* 1109 */                   if (elementMatched) {
/* 1110 */                     this.prefetchedElement = element;
/* 1111 */                     conclusionReached = true;
/*      */                   } 
/*      */                 } else {
/* 1114 */                   this.prefetchedEndOfIterator = true;
/* 1115 */                   this.prefetchedElement = null;
/* 1116 */                   conclusionReached = true;
/*      */                 } 
/* 1118 */                 if (conclusionReached) {
/* 1119 */                   this.prefetchDone = true;
/*      */                   return;
/*      */                 } 
/*      */               } 
/*      */             }
/*      */           }lhoIsSequence);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class take_whileBI
/*      */     extends FilterLikeBI
/*      */   {
/*      */     protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
/* 1136 */       if (!isLazilyGeneratedResultEnabled()) {
/* 1137 */         if (!lhoIsSequence) {
/* 1138 */           throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
/*      */         }
/*      */         
/* 1141 */         List<TemplateModel> resultList = new ArrayList<>();
/* 1142 */         while (lhoIterator.hasNext()) {
/* 1143 */           TemplateModel element = lhoIterator.next();
/* 1144 */           if (elementMatches(element, elementTransformer, env)) {
/* 1145 */             resultList.add(element);
/*      */           }
/*      */         } 
/*      */ 
/*      */         
/* 1150 */         return (TemplateModel)new TemplateModelListSequence(resultList);
/*      */       } 
/* 1152 */       return (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator()
/*      */           {
/*      */             boolean prefetchDone;
/*      */             
/*      */             TemplateModel prefetchedElement;
/*      */             boolean prefetchedEndOfIterator;
/*      */             
/*      */             public TemplateModel next() throws TemplateModelException {
/* 1160 */               ensurePrefetchDone();
/* 1161 */               if (this.prefetchedEndOfIterator) {
/* 1162 */                 throw new IllegalStateException("next() was called when hasNext() is false");
/*      */               }
/* 1164 */               this.prefetchDone = false;
/* 1165 */               return this.prefetchedElement;
/*      */             }
/*      */ 
/*      */             
/*      */             public boolean hasNext() throws TemplateModelException {
/* 1170 */               ensurePrefetchDone();
/* 1171 */               return !this.prefetchedEndOfIterator;
/*      */             }
/*      */             
/*      */             private void ensurePrefetchDone() throws TemplateModelException {
/* 1175 */               if (this.prefetchDone) {
/*      */                 return;
/*      */               }
/*      */               
/* 1179 */               if (lhoIterator.hasNext()) {
/* 1180 */                 boolean elementMatched; TemplateModel element = lhoIterator.next();
/*      */                 
/*      */                 try {
/* 1183 */                   elementMatched = BuiltInsForSequences.take_whileBI.this.elementMatches(element, elementTransformer, env);
/* 1184 */                 } catch (TemplateException e) {
/* 1185 */                   throw new _TemplateModelException(e, env, "Failed to transform element");
/*      */                 } 
/* 1187 */                 if (elementMatched) {
/* 1188 */                   this.prefetchedElement = element;
/*      */                 } else {
/* 1190 */                   this.prefetchedEndOfIterator = true;
/* 1191 */                   this.prefetchedElement = null;
/*      */                 } 
/*      */               } else {
/* 1194 */                 this.prefetchedEndOfIterator = true;
/* 1195 */                 this.prefetchedElement = null;
/*      */               } 
/* 1197 */               this.prefetchDone = true;
/*      */             }
/*      */           }lhoIsSequence);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class mapBI
/*      */     extends IntermediateStreamOperationLikeBuiltIn
/*      */   {
/*      */     protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
/* 1213 */       if (!isLazilyGeneratedResultEnabled()) {
/* 1214 */         if (!lhoIsSequence) {
/* 1215 */           throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
/*      */         }
/*      */         
/* 1218 */         List<TemplateModel> resultList = new ArrayList<>();
/* 1219 */         while (lhoIterator.hasNext()) {
/* 1220 */           resultList.add(fetchAndMapNextElement(lhoIterator, elementTransformer, env));
/*      */         }
/* 1222 */         return (TemplateModel)new TemplateModelListSequence(resultList);
/*      */       } 
/* 1224 */       TemplateModelIterator mappedLhoIterator = new TemplateModelIterator()
/*      */         {
/*      */           public TemplateModel next() throws TemplateModelException {
/*      */             try {
/* 1228 */               return BuiltInsForSequences.mapBI.this.fetchAndMapNextElement(lhoIterator, elementTransformer, env);
/* 1229 */             } catch (TemplateException e) {
/* 1230 */               throw new _TemplateModelException(e, env, "Failed to transform element");
/*      */             } 
/*      */           }
/*      */ 
/*      */           
/*      */           public boolean hasNext() throws TemplateModelException {
/* 1236 */             return lhoIterator.hasNext();
/*      */           }
/*      */         };
/* 1239 */       if (lho instanceof TemplateCollectionModelEx) {
/* 1240 */         return (TemplateModel)new LazilyGeneratedCollectionModelWithSameSizeCollEx(mappedLhoIterator, (TemplateCollectionModelEx)lho, lhoIsSequence);
/*      */       }
/* 1242 */       if (lho instanceof TemplateSequenceModel) {
/* 1243 */         return (TemplateModel)new LazilyGeneratedCollectionModelWithSameSizeSeq(mappedLhoIterator, (TemplateSequenceModel)lho);
/*      */       }
/*      */       
/* 1246 */       return (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(mappedLhoIterator, lhoIsSequence);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private TemplateModel fetchAndMapNextElement(TemplateModelIterator lhoIterator, IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, Environment env) throws TemplateException {
/* 1255 */       TemplateModel transformedElement = elementTransformer.transformElement(lhoIterator.next(), env);
/* 1256 */       if (transformedElement == null) {
/* 1257 */         throw new _TemplateModelException(getElementTransformerExp(), env, "The element mapper function has returned no return value (has returned null).");
/*      */       }
/*      */       
/* 1260 */       return transformedElement;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class drop_whileBI
/*      */     extends FilterLikeBI
/*      */   {
/*      */     protected TemplateModel calculateResult(final TemplateModelIterator lhoIterator, TemplateModel lho, boolean lhoIsSequence, final IntermediateStreamOperationLikeBuiltIn.ElementTransformer elementTransformer, final Environment env) throws TemplateException {
/* 1272 */       if (!isLazilyGeneratedResultEnabled()) {
/* 1273 */         if (!lhoIsSequence) {
/* 1274 */           throw _MessageUtil.newLazilyGeneratedCollectionMustBeSequenceException(this);
/*      */         }
/*      */         
/* 1277 */         List<TemplateModel> resultList = new ArrayList<>();
/* 1278 */         while (lhoIterator.hasNext()) {
/* 1279 */           TemplateModel element = lhoIterator.next();
/* 1280 */           if (!elementMatches(element, elementTransformer, env)) {
/* 1281 */             resultList.add(element);
/* 1282 */             while (lhoIterator.hasNext()) {
/* 1283 */               resultList.add(lhoIterator.next());
/*      */             }
/*      */             break;
/*      */           } 
/*      */         } 
/* 1288 */         return (TemplateModel)new TemplateModelListSequence(resultList);
/*      */       } 
/* 1290 */       return (TemplateModel)new LazilyGeneratedCollectionModelWithUnknownSize(new TemplateModelIterator()
/*      */           {
/*      */             boolean dropMode = true;
/*      */             
/*      */             boolean prefetchDone;
/*      */             TemplateModel prefetchedElement;
/*      */             boolean prefetchedEndOfIterator;
/*      */             
/*      */             public TemplateModel next() throws TemplateModelException {
/* 1299 */               ensurePrefetchDone();
/* 1300 */               if (this.prefetchedEndOfIterator) {
/* 1301 */                 throw new IllegalStateException("next() was called when hasNext() is false");
/*      */               }
/* 1303 */               this.prefetchDone = false;
/* 1304 */               return this.prefetchedElement;
/*      */             }
/*      */ 
/*      */             
/*      */             public boolean hasNext() throws TemplateModelException {
/* 1309 */               ensurePrefetchDone();
/* 1310 */               return !this.prefetchedEndOfIterator;
/*      */             }
/*      */             
/*      */             private void ensurePrefetchDone() throws TemplateModelException {
/* 1314 */               if (this.prefetchDone) {
/*      */                 return;
/*      */               }
/*      */               
/* 1318 */               if (this.dropMode) {
/* 1319 */                 boolean foundElement = false;
/* 1320 */                 while (lhoIterator.hasNext()) {
/* 1321 */                   TemplateModel element = lhoIterator.next();
/*      */                   try {
/* 1323 */                     if (!BuiltInsForSequences.drop_whileBI.this.elementMatches(element, elementTransformer, env)) {
/* 1324 */                       this.prefetchedElement = element;
/* 1325 */                       foundElement = true;
/*      */                       break;
/*      */                     } 
/* 1328 */                   } catch (TemplateException e) {
/* 1329 */                     throw new _TemplateModelException(e, env, "Failed to transform element");
/*      */                   } 
/*      */                 } 
/*      */                 
/* 1333 */                 this.dropMode = false;
/* 1334 */                 if (!foundElement) {
/* 1335 */                   this.prefetchedEndOfIterator = true;
/* 1336 */                   this.prefetchedElement = null;
/*      */                 }
/*      */               
/* 1339 */               } else if (lhoIterator.hasNext()) {
/* 1340 */                 TemplateModel element = lhoIterator.next();
/* 1341 */                 this.prefetchedElement = element;
/*      */               } else {
/* 1343 */                 this.prefetchedEndOfIterator = true;
/* 1344 */                 this.prefetchedElement = null;
/*      */               } 
/*      */               
/* 1347 */               this.prefetchDone = true;
/*      */             }
/*      */           }lhoIsSequence);
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\BuiltInsForSequences.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */