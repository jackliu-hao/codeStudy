/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.Recognizer;
/*     */ import org.antlr.v4.runtime.RuleContext;
/*     */ import org.antlr.v4.runtime.misc.MurmurHash;
/*     */ import org.antlr.v4.runtime.misc.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class SemanticContext
/*     */ {
/*  59 */   public static final SemanticContext NONE = new Predicate();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/*  95 */     return this;
/*     */   }
/*     */   
/*     */   public static class Predicate extends SemanticContext {
/*     */     public final int ruleIndex;
/*     */     public final int predIndex;
/*     */     public final boolean isCtxDependent;
/*     */     
/*     */     protected Predicate() {
/* 104 */       this.ruleIndex = -1;
/* 105 */       this.predIndex = -1;
/* 106 */       this.isCtxDependent = false;
/*     */     }
/*     */     
/*     */     public Predicate(int ruleIndex, int predIndex, boolean isCtxDependent) {
/* 110 */       this.ruleIndex = ruleIndex;
/* 111 */       this.predIndex = predIndex;
/* 112 */       this.isCtxDependent = isCtxDependent;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/* 117 */       RuleContext localctx = this.isCtxDependent ? parserCallStack : null;
/* 118 */       return parser.sempred(localctx, this.ruleIndex, this.predIndex);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 123 */       int hashCode = MurmurHash.initialize();
/* 124 */       hashCode = MurmurHash.update(hashCode, this.ruleIndex);
/* 125 */       hashCode = MurmurHash.update(hashCode, this.predIndex);
/* 126 */       hashCode = MurmurHash.update(hashCode, this.isCtxDependent ? 1 : 0);
/* 127 */       hashCode = MurmurHash.finish(hashCode, 3);
/* 128 */       return hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 133 */       if (!(obj instanceof Predicate)) return false; 
/* 134 */       if (this == obj) return true; 
/* 135 */       Predicate p = (Predicate)obj;
/* 136 */       return (this.ruleIndex == p.ruleIndex && this.predIndex == p.predIndex && this.isCtxDependent == p.isCtxDependent);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 143 */       return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
/*     */     }
/*     */   }
/*     */   
/*     */   public static class PrecedencePredicate extends SemanticContext implements Comparable<PrecedencePredicate> {
/*     */     public final int precedence;
/*     */     
/*     */     protected PrecedencePredicate() {
/* 151 */       this.precedence = 0;
/*     */     }
/*     */     
/*     */     public PrecedencePredicate(int precedence) {
/* 155 */       this.precedence = precedence;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/* 160 */       return parser.precpred(parserCallStack, this.precedence);
/*     */     }
/*     */ 
/*     */     
/*     */     public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/* 165 */       if (parser.precpred(parserCallStack, this.precedence)) {
/* 166 */         return SemanticContext.NONE;
/*     */       }
/*     */       
/* 169 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int compareTo(PrecedencePredicate o) {
/* 175 */       return this.precedence - o.precedence;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 180 */       int hashCode = 1;
/* 181 */       hashCode = 31 * hashCode + this.precedence;
/* 182 */       return hashCode;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 187 */       if (!(obj instanceof PrecedencePredicate)) {
/* 188 */         return false;
/*     */       }
/*     */       
/* 191 */       if (this == obj) {
/* 192 */         return true;
/*     */       }
/*     */       
/* 195 */       PrecedencePredicate other = (PrecedencePredicate)obj;
/* 196 */       return (this.precedence == other.precedence);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 202 */       return "{" + this.precedence + ">=prec}?";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class Operator
/*     */     extends SemanticContext
/*     */   {
/*     */     public abstract Collection<SemanticContext> getOperands();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class AND
/*     */     extends Operator
/*     */   {
/*     */     public final SemanticContext[] opnds;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public AND(SemanticContext a, SemanticContext b) {
/* 233 */       Set<SemanticContext> operands = new HashSet<SemanticContext>();
/* 234 */       if (a instanceof AND) { operands.addAll(Arrays.asList(((AND)a).opnds)); }
/* 235 */       else { operands.add(a); }
/* 236 */        if (b instanceof AND) { operands.addAll(Arrays.asList(((AND)b).opnds)); }
/* 237 */       else { operands.add(b); }
/*     */       
/* 239 */       List<SemanticContext.PrecedencePredicate> precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
/* 240 */       if (!precedencePredicates.isEmpty()) {
/*     */         
/* 242 */         SemanticContext.PrecedencePredicate reduced = Collections.<SemanticContext.PrecedencePredicate>min(precedencePredicates);
/* 243 */         operands.add(reduced);
/*     */       } 
/*     */       
/* 246 */       this.opnds = operands.<SemanticContext>toArray(new SemanticContext[operands.size()]);
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<SemanticContext> getOperands() {
/* 251 */       return Arrays.asList(this.opnds);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 256 */       if (this == obj) return true; 
/* 257 */       if (!(obj instanceof AND)) return false; 
/* 258 */       AND other = (AND)obj;
/* 259 */       return Arrays.equals((Object[])this.opnds, (Object[])other.opnds);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 264 */       return MurmurHash.hashCode(this.opnds, AND.class.hashCode());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/* 276 */       for (SemanticContext opnd : this.opnds) {
/* 277 */         if (!opnd.eval(parser, parserCallStack)) return false; 
/*     */       } 
/* 279 */       return true;
/*     */     }
/*     */     
/*     */     public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/*     */       int j;
/* 284 */       boolean differs = false;
/* 285 */       List<SemanticContext> operands = new ArrayList<SemanticContext>();
/* 286 */       for (SemanticContext context : this.opnds) {
/* 287 */         SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
/* 288 */         j = differs | ((evaluated != context) ? 1 : 0);
/* 289 */         if (evaluated == null)
/*     */         {
/* 291 */           return null;
/*     */         }
/* 293 */         if (evaluated != NONE)
/*     */         {
/* 295 */           operands.add(evaluated);
/*     */         }
/*     */       } 
/*     */       
/* 299 */       if (j == 0) {
/* 300 */         return this;
/*     */       }
/*     */       
/* 303 */       if (operands.isEmpty())
/*     */       {
/* 305 */         return NONE;
/*     */       }
/*     */       
/* 308 */       SemanticContext result = operands.get(0);
/* 309 */       for (int i = 1; i < operands.size(); i++) {
/* 310 */         result = SemanticContext.and(result, operands.get(i));
/*     */       }
/*     */       
/* 313 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 318 */       return Utils.join(Arrays.asList((Object[])this.opnds).iterator(), "&&");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class OR
/*     */     extends Operator
/*     */   {
/*     */     public final SemanticContext[] opnds;
/*     */ 
/*     */     
/*     */     public OR(SemanticContext a, SemanticContext b) {
/* 330 */       Set<SemanticContext> operands = new HashSet<SemanticContext>();
/* 331 */       if (a instanceof OR) { operands.addAll(Arrays.asList(((OR)a).opnds)); }
/* 332 */       else { operands.add(a); }
/* 333 */        if (b instanceof OR) { operands.addAll(Arrays.asList(((OR)b).opnds)); }
/* 334 */       else { operands.add(b); }
/*     */       
/* 336 */       List<SemanticContext.PrecedencePredicate> precedencePredicates = SemanticContext.filterPrecedencePredicates(operands);
/* 337 */       if (!precedencePredicates.isEmpty()) {
/*     */         
/* 339 */         SemanticContext.PrecedencePredicate reduced = Collections.<SemanticContext.PrecedencePredicate>max(precedencePredicates);
/* 340 */         operands.add(reduced);
/*     */       } 
/*     */       
/* 343 */       this.opnds = operands.<SemanticContext>toArray(new SemanticContext[operands.size()]);
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<SemanticContext> getOperands() {
/* 348 */       return Arrays.asList(this.opnds);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object obj) {
/* 353 */       if (this == obj) return true; 
/* 354 */       if (!(obj instanceof OR)) return false; 
/* 355 */       OR other = (OR)obj;
/* 356 */       return Arrays.equals((Object[])this.opnds, (Object[])other.opnds);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 361 */       return MurmurHash.hashCode(this.opnds, OR.class.hashCode());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean eval(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/* 373 */       for (SemanticContext opnd : this.opnds) {
/* 374 */         if (opnd.eval(parser, parserCallStack)) return true; 
/*     */       } 
/* 376 */       return false;
/*     */     }
/*     */     
/*     */     public SemanticContext evalPrecedence(Recognizer<?, ?> parser, RuleContext parserCallStack) {
/*     */       int j;
/* 381 */       boolean differs = false;
/* 382 */       List<SemanticContext> operands = new ArrayList<SemanticContext>();
/* 383 */       for (SemanticContext context : this.opnds) {
/* 384 */         SemanticContext evaluated = context.evalPrecedence(parser, parserCallStack);
/* 385 */         j = differs | ((evaluated != context) ? 1 : 0);
/* 386 */         if (evaluated == NONE)
/*     */         {
/* 388 */           return NONE;
/*     */         }
/* 390 */         if (evaluated != null)
/*     */         {
/* 392 */           operands.add(evaluated);
/*     */         }
/*     */       } 
/*     */       
/* 396 */       if (j == 0) {
/* 397 */         return this;
/*     */       }
/*     */       
/* 400 */       if (operands.isEmpty())
/*     */       {
/* 402 */         return null;
/*     */       }
/*     */       
/* 405 */       SemanticContext result = operands.get(0);
/* 406 */       for (int i = 1; i < operands.size(); i++) {
/* 407 */         result = SemanticContext.or(result, operands.get(i));
/*     */       }
/*     */       
/* 410 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 415 */       return Utils.join(Arrays.asList((Object[])this.opnds).iterator(), "||");
/*     */     }
/*     */   }
/*     */   
/*     */   public static SemanticContext and(SemanticContext a, SemanticContext b) {
/* 420 */     if (a == null || a == NONE) return b; 
/* 421 */     if (b == null || b == NONE) return a; 
/* 422 */     AND result = new AND(a, b);
/* 423 */     if (result.opnds.length == 1) {
/* 424 */       return result.opnds[0];
/*     */     }
/*     */     
/* 427 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SemanticContext or(SemanticContext a, SemanticContext b) {
/* 435 */     if (a == null) return b; 
/* 436 */     if (b == null) return a; 
/* 437 */     if (a == NONE || b == NONE) return NONE; 
/* 438 */     OR result = new OR(a, b);
/* 439 */     if (result.opnds.length == 1) {
/* 440 */       return result.opnds[0];
/*     */     }
/*     */     
/* 443 */     return result;
/*     */   }
/*     */   
/*     */   private static List<PrecedencePredicate> filterPrecedencePredicates(Collection<? extends SemanticContext> collection) {
/* 447 */     ArrayList<PrecedencePredicate> result = null;
/* 448 */     for (Iterator<? extends SemanticContext> iterator = collection.iterator(); iterator.hasNext(); ) {
/* 449 */       SemanticContext context = iterator.next();
/* 450 */       if (context instanceof PrecedencePredicate) {
/* 451 */         if (result == null) {
/* 452 */           result = new ArrayList<PrecedencePredicate>();
/*     */         }
/*     */         
/* 455 */         result.add((PrecedencePredicate)context);
/* 456 */         iterator.remove();
/*     */       } 
/*     */     } 
/*     */     
/* 460 */     if (result == null) {
/* 461 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 464 */     return result;
/*     */   }
/*     */   
/*     */   public abstract boolean eval(Recognizer<?, ?> paramRecognizer, RuleContext paramRuleContext);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\SemanticContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */