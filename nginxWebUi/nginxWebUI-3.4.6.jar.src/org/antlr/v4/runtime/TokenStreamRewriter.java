/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.antlr.v4.runtime.misc.Interval;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TokenStreamRewriter
/*     */ {
/*     */   public static final String DEFAULT_PROGRAM_NAME = "default";
/*     */   public static final int PROGRAM_INIT_SIZE = 100;
/*     */   public static final int MIN_TOKEN_INDEX = 0;
/*     */   protected final TokenStream tokens;
/*     */   protected final Map<String, List<RewriteOperation>> programs;
/*     */   protected final Map<String, Integer> lastRewriteTokenIndexes;
/*     */   
/*     */   public class RewriteOperation
/*     */   {
/*     */     protected int instructionIndex;
/*     */     protected int index;
/*     */     protected Object text;
/*     */     
/*     */     protected RewriteOperation(int index) {
/* 130 */       this.index = index;
/*     */     }
/*     */     
/*     */     protected RewriteOperation(int index, Object text) {
/* 134 */       this.index = index;
/* 135 */       this.text = text;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int execute(StringBuilder buf) {
/* 141 */       return this.index;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 146 */       String opName = getClass().getName();
/* 147 */       int $index = opName.indexOf('$');
/* 148 */       opName = opName.substring($index + 1, opName.length());
/* 149 */       return "<" + opName + "@" + TokenStreamRewriter.this.tokens.get(this.index) + ":\"" + this.text + "\">";
/*     */     }
/*     */   }
/*     */   
/*     */   class InsertBeforeOp
/*     */     extends RewriteOperation {
/*     */     public InsertBeforeOp(int index, Object text) {
/* 156 */       super(index, text);
/*     */     }
/*     */ 
/*     */     
/*     */     public int execute(StringBuilder buf) {
/* 161 */       buf.append(this.text);
/* 162 */       if (TokenStreamRewriter.this.tokens.get(this.index).getType() != -1) {
/* 163 */         buf.append(TokenStreamRewriter.this.tokens.get(this.index).getText());
/*     */       }
/* 165 */       return this.index + 1;
/*     */     }
/*     */   }
/*     */   
/*     */   class ReplaceOp
/*     */     extends RewriteOperation
/*     */   {
/*     */     protected int lastIndex;
/*     */     
/*     */     public ReplaceOp(int from, int to, Object text) {
/* 175 */       super(from, text);
/* 176 */       this.lastIndex = to;
/*     */     }
/*     */     
/*     */     public int execute(StringBuilder buf) {
/* 180 */       if (this.text != null) {
/* 181 */         buf.append(this.text);
/*     */       }
/* 183 */       return this.lastIndex + 1;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 187 */       if (this.text == null) {
/* 188 */         return "<DeleteOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ">";
/*     */       }
/*     */       
/* 191 */       return "<ReplaceOp@" + TokenStreamRewriter.this.tokens.get(this.index) + ".." + TokenStreamRewriter.this.tokens.get(this.lastIndex) + ":\"" + this.text + "\">";
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
/*     */ 
/*     */ 
/*     */   
/*     */   public TokenStreamRewriter(TokenStream tokens) {
/* 209 */     this.tokens = tokens;
/* 210 */     this.programs = new HashMap<String, List<RewriteOperation>>();
/* 211 */     this.programs.put("default", new ArrayList<RewriteOperation>(100));
/*     */     
/* 213 */     this.lastRewriteTokenIndexes = new HashMap<String, Integer>();
/*     */   }
/*     */   
/*     */   public final TokenStream getTokenStream() {
/* 217 */     return this.tokens;
/*     */   }
/*     */   
/*     */   public void rollback(int instructionIndex) {
/* 221 */     rollback("default", instructionIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void rollback(String programName, int instructionIndex) {
/* 229 */     List<RewriteOperation> is = this.programs.get(programName);
/* 230 */     if (is != null) {
/* 231 */       this.programs.put(programName, is.subList(0, instructionIndex));
/*     */     }
/*     */   }
/*     */   
/*     */   public void deleteProgram() {
/* 236 */     deleteProgram("default");
/*     */   }
/*     */ 
/*     */   
/*     */   public void deleteProgram(String programName) {
/* 241 */     rollback(programName, 0);
/*     */   }
/*     */   
/*     */   public void insertAfter(Token t, Object text) {
/* 245 */     insertAfter("default", t, text);
/*     */   }
/*     */   
/*     */   public void insertAfter(int index, Object text) {
/* 249 */     insertAfter("default", index, text);
/*     */   }
/*     */   
/*     */   public void insertAfter(String programName, Token t, Object text) {
/* 253 */     insertAfter(programName, t.getTokenIndex(), text);
/*     */   }
/*     */ 
/*     */   
/*     */   public void insertAfter(String programName, int index, Object text) {
/* 258 */     insertBefore(programName, index + 1, text);
/*     */   }
/*     */   
/*     */   public void insertBefore(Token t, Object text) {
/* 262 */     insertBefore("default", t, text);
/*     */   }
/*     */   
/*     */   public void insertBefore(int index, Object text) {
/* 266 */     insertBefore("default", index, text);
/*     */   }
/*     */   
/*     */   public void insertBefore(String programName, Token t, Object text) {
/* 270 */     insertBefore(programName, t.getTokenIndex(), text);
/*     */   }
/*     */   
/*     */   public void insertBefore(String programName, int index, Object text) {
/* 274 */     RewriteOperation op = new InsertBeforeOp(index, text);
/* 275 */     List<RewriteOperation> rewrites = getProgram(programName);
/* 276 */     op.instructionIndex = rewrites.size();
/* 277 */     rewrites.add(op);
/*     */   }
/*     */   
/*     */   public void replace(int index, Object text) {
/* 281 */     replace("default", index, index, text);
/*     */   }
/*     */   
/*     */   public void replace(int from, int to, Object text) {
/* 285 */     replace("default", from, to, text);
/*     */   }
/*     */   
/*     */   public void replace(Token indexT, Object text) {
/* 289 */     replace("default", indexT, indexT, text);
/*     */   }
/*     */   
/*     */   public void replace(Token from, Token to, Object text) {
/* 293 */     replace("default", from, to, text);
/*     */   }
/*     */   
/*     */   public void replace(String programName, int from, int to, Object text) {
/* 297 */     if (from > to || from < 0 || to < 0 || to >= this.tokens.size()) {
/* 298 */       throw new IllegalArgumentException("replace: range invalid: " + from + ".." + to + "(size=" + this.tokens.size() + ")");
/*     */     }
/* 300 */     RewriteOperation op = new ReplaceOp(from, to, text);
/* 301 */     List<RewriteOperation> rewrites = getProgram(programName);
/* 302 */     op.instructionIndex = rewrites.size();
/* 303 */     rewrites.add(op);
/*     */   }
/*     */   
/*     */   public void replace(String programName, Token from, Token to, Object text) {
/* 307 */     replace(programName, from.getTokenIndex(), to.getTokenIndex(), text);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void delete(int index) {
/* 314 */     delete("default", index, index);
/*     */   }
/*     */   
/*     */   public void delete(int from, int to) {
/* 318 */     delete("default", from, to);
/*     */   }
/*     */   
/*     */   public void delete(Token indexT) {
/* 322 */     delete("default", indexT, indexT);
/*     */   }
/*     */   
/*     */   public void delete(Token from, Token to) {
/* 326 */     delete("default", from, to);
/*     */   }
/*     */   
/*     */   public void delete(String programName, int from, int to) {
/* 330 */     replace(programName, from, to, (Object)null);
/*     */   }
/*     */   
/*     */   public void delete(String programName, Token from, Token to) {
/* 334 */     replace(programName, from, to, (Object)null);
/*     */   }
/*     */   
/*     */   public int getLastRewriteTokenIndex() {
/* 338 */     return getLastRewriteTokenIndex("default");
/*     */   }
/*     */   
/*     */   protected int getLastRewriteTokenIndex(String programName) {
/* 342 */     Integer I = this.lastRewriteTokenIndexes.get(programName);
/* 343 */     if (I == null) {
/* 344 */       return -1;
/*     */     }
/* 346 */     return I.intValue();
/*     */   }
/*     */   
/*     */   protected void setLastRewriteTokenIndex(String programName, int i) {
/* 350 */     this.lastRewriteTokenIndexes.put(programName, Integer.valueOf(i));
/*     */   }
/*     */   
/*     */   protected List<RewriteOperation> getProgram(String name) {
/* 354 */     List<RewriteOperation> is = this.programs.get(name);
/* 355 */     if (is == null) {
/* 356 */       is = initializeProgram(name);
/*     */     }
/* 358 */     return is;
/*     */   }
/*     */   
/*     */   private List<RewriteOperation> initializeProgram(String name) {
/* 362 */     List<RewriteOperation> is = new ArrayList<RewriteOperation>(100);
/* 363 */     this.programs.put(name, is);
/* 364 */     return is;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 371 */     return getText("default", Interval.of(0, this.tokens.size() - 1));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText(String programName) {
/* 378 */     return getText(programName, Interval.of(0, this.tokens.size() - 1));
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
/*     */   public String getText(Interval interval) {
/* 391 */     return getText("default", interval);
/*     */   }
/*     */   
/*     */   public String getText(String programName, Interval interval) {
/* 395 */     List<RewriteOperation> rewrites = this.programs.get(programName);
/* 396 */     int start = interval.a;
/* 397 */     int stop = interval.b;
/*     */ 
/*     */     
/* 400 */     if (stop > this.tokens.size() - 1) stop = this.tokens.size() - 1; 
/* 401 */     if (start < 0) start = 0;
/*     */     
/* 403 */     if (rewrites == null || rewrites.isEmpty()) {
/* 404 */       return this.tokens.getText(interval);
/*     */     }
/* 406 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*     */     
/* 409 */     Map<Integer, RewriteOperation> indexToOp = reduceToSingleOperationPerIndex(rewrites);
/*     */ 
/*     */     
/* 412 */     int i = start;
/* 413 */     while (i <= stop && i < this.tokens.size()) {
/* 414 */       RewriteOperation op = indexToOp.get(Integer.valueOf(i));
/* 415 */       indexToOp.remove(Integer.valueOf(i));
/* 416 */       Token t = this.tokens.get(i);
/* 417 */       if (op == null) {
/*     */         
/* 419 */         if (t.getType() != -1) buf.append(t.getText()); 
/* 420 */         i++;
/*     */         continue;
/*     */       } 
/* 423 */       i = op.execute(buf);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 430 */     if (stop == this.tokens.size() - 1)
/*     */     {
/*     */       
/* 433 */       for (RewriteOperation op : indexToOp.values()) {
/* 434 */         if (op.index >= this.tokens.size() - 1) buf.append(op.text); 
/*     */       } 
/*     */     }
/* 437 */     return buf.toString();
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
/*     */   protected Map<Integer, RewriteOperation> reduceToSingleOperationPerIndex(List<RewriteOperation> rewrites) {
/*     */     int i;
/* 493 */     for (i = 0; i < rewrites.size(); i++) {
/* 494 */       RewriteOperation op = rewrites.get(i);
/* 495 */       if (op != null && 
/* 496 */         op instanceof ReplaceOp) {
/* 497 */         ReplaceOp rop = (ReplaceOp)rewrites.get(i);
/*     */         
/* 499 */         List<? extends InsertBeforeOp> inserts = getKindOfOps(rewrites, InsertBeforeOp.class, i);
/* 500 */         for (InsertBeforeOp iop : inserts) {
/* 501 */           if (iop.index == rop.index) {
/*     */ 
/*     */             
/* 504 */             rewrites.set(iop.instructionIndex, null);
/* 505 */             rop.text = iop.text.toString() + ((rop.text != null) ? rop.text.toString() : ""); continue;
/*     */           } 
/* 507 */           if (iop.index > rop.index && iop.index <= rop.lastIndex)
/*     */           {
/* 509 */             rewrites.set(iop.instructionIndex, null);
/*     */           }
/*     */         } 
/*     */         
/* 513 */         List<? extends ReplaceOp> prevReplaces = getKindOfOps(rewrites, ReplaceOp.class, i);
/* 514 */         for (ReplaceOp prevRop : prevReplaces) {
/* 515 */           if (prevRop.index >= rop.index && prevRop.lastIndex <= rop.lastIndex) {
/*     */             
/* 517 */             rewrites.set(prevRop.instructionIndex, null);
/*     */             
/*     */             continue;
/*     */           } 
/* 521 */           boolean disjoint = (prevRop.lastIndex < rop.index || prevRop.index > rop.lastIndex);
/*     */           
/* 523 */           boolean same = (prevRop.index == rop.index && prevRop.lastIndex == rop.lastIndex);
/*     */ 
/*     */ 
/*     */           
/* 527 */           if (prevRop.text == null && rop.text == null && !disjoint) {
/*     */             
/* 529 */             rewrites.set(prevRop.instructionIndex, null);
/* 530 */             rop.index = Math.min(prevRop.index, rop.index);
/* 531 */             rop.lastIndex = Math.max(prevRop.lastIndex, rop.lastIndex);
/* 532 */             System.out.println("new rop " + rop); continue;
/*     */           } 
/* 534 */           if (!disjoint && !same) {
/* 535 */             throw new IllegalArgumentException("replace op boundaries of " + rop + " overlap with previous " + prevRop);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 541 */     for (i = 0; i < rewrites.size(); i++) {
/* 542 */       RewriteOperation op = rewrites.get(i);
/* 543 */       if (op != null && 
/* 544 */         op instanceof InsertBeforeOp) {
/* 545 */         InsertBeforeOp iop = (InsertBeforeOp)rewrites.get(i);
/*     */         
/* 547 */         List<? extends InsertBeforeOp> prevInserts = getKindOfOps(rewrites, InsertBeforeOp.class, i);
/* 548 */         for (InsertBeforeOp prevIop : prevInserts) {
/* 549 */           if (prevIop.index == iop.index) {
/*     */ 
/*     */             
/* 552 */             iop.text = catOpText(iop.text, prevIop.text);
/*     */             
/* 554 */             rewrites.set(prevIop.instructionIndex, null);
/*     */           } 
/*     */         } 
/*     */         
/* 558 */         List<? extends ReplaceOp> prevReplaces = getKindOfOps(rewrites, ReplaceOp.class, i);
/* 559 */         for (ReplaceOp rop : prevReplaces) {
/* 560 */           if (iop.index == rop.index) {
/* 561 */             rop.text = catOpText(iop.text, rop.text);
/* 562 */             rewrites.set(i, null);
/*     */             continue;
/*     */           } 
/* 565 */           if (iop.index >= rop.index && iop.index <= rop.lastIndex) {
/* 566 */             throw new IllegalArgumentException("insert op " + iop + " within boundaries of previous " + rop);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 571 */     Map<Integer, RewriteOperation> m = new HashMap<Integer, RewriteOperation>();
/* 572 */     for (int j = 0; j < rewrites.size(); j++) {
/* 573 */       RewriteOperation op = rewrites.get(j);
/* 574 */       if (op != null) {
/* 575 */         if (m.get(Integer.valueOf(op.index)) != null) {
/* 576 */           throw new Error("should only be one op per index");
/*     */         }
/* 578 */         m.put(Integer.valueOf(op.index), op);
/*     */       } 
/*     */     } 
/* 581 */     return m;
/*     */   }
/*     */   
/*     */   protected String catOpText(Object a, Object b) {
/* 585 */     String x = "";
/* 586 */     String y = "";
/* 587 */     if (a != null) x = a.toString(); 
/* 588 */     if (b != null) y = b.toString(); 
/* 589 */     return x + y;
/*     */   }
/*     */ 
/*     */   
/*     */   protected <T extends RewriteOperation> List<? extends T> getKindOfOps(List<? extends RewriteOperation> rewrites, Class<T> kind, int before) {
/* 594 */     List<T> ops = new ArrayList<T>();
/* 595 */     for (int i = 0; i < before && i < rewrites.size(); i++) {
/* 596 */       RewriteOperation op = rewrites.get(i);
/* 597 */       if (op != null && 
/* 598 */         kind.isInstance(op)) {
/* 599 */         ops.add(kind.cast(op));
/*     */       }
/*     */     } 
/* 602 */     return ops;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\TokenStreamRewriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */