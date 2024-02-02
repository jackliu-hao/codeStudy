/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.tree.ErrorNode;
/*     */ import org.antlr.v4.runtime.tree.ErrorNodeImpl;
/*     */ import org.antlr.v4.runtime.tree.ParseTree;
/*     */ import org.antlr.v4.runtime.tree.ParseTreeListener;
/*     */ import org.antlr.v4.runtime.tree.TerminalNode;
/*     */ import org.antlr.v4.runtime.tree.TerminalNodeImpl;
/*     */ import org.antlr.v4.runtime.tree.Tree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParserRuleContext
/*     */   extends RuleContext
/*     */ {
/*     */   public List<ParseTree> children;
/*     */   public Token start;
/*     */   public Token stop;
/*     */   public RecognitionException exception;
/*     */   
/*     */   public ParserRuleContext() {}
/*     */   
/*     */   public void copyFrom(ParserRuleContext ctx) {
/* 109 */     this.parent = ctx.parent;
/* 110 */     this.invokingState = ctx.invokingState;
/*     */     
/* 112 */     this.start = ctx.start;
/* 113 */     this.stop = ctx.stop;
/*     */   }
/*     */   
/*     */   public ParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
/* 117 */     super(parent, invokingStateNumber);
/*     */   }
/*     */ 
/*     */   
/*     */   public void enterRule(ParseTreeListener listener) {}
/*     */ 
/*     */   
/*     */   public void exitRule(ParseTreeListener listener) {}
/*     */   
/*     */   public TerminalNode addChild(TerminalNode t) {
/* 127 */     if (this.children == null) this.children = new ArrayList<ParseTree>(); 
/* 128 */     this.children.add(t);
/* 129 */     return t;
/*     */   }
/*     */   
/*     */   public RuleContext addChild(RuleContext ruleInvocation) {
/* 133 */     if (this.children == null) this.children = new ArrayList<ParseTree>(); 
/* 134 */     this.children.add(ruleInvocation);
/* 135 */     return ruleInvocation;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeLastChild() {
/* 143 */     if (this.children != null) {
/* 144 */       this.children.remove(this.children.size() - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TerminalNode addChild(Token matchedToken) {
/* 154 */     TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
/* 155 */     addChild(t);
/* 156 */     t.parent = this;
/* 157 */     return t;
/*     */   }
/*     */   
/*     */   public ErrorNode addErrorNode(Token badToken) {
/* 161 */     ErrorNodeImpl t = new ErrorNodeImpl(badToken);
/* 162 */     addChild(t);
/* 163 */     t.parent = this;
/* 164 */     return t;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserRuleContext getParent() {
/* 170 */     return (ParserRuleContext)super.getParent();
/*     */   }
/*     */ 
/*     */   
/*     */   public ParseTree getChild(int i) {
/* 175 */     return (this.children != null && i >= 0 && i < this.children.size()) ? this.children.get(i) : null;
/*     */   }
/*     */   
/*     */   public <T extends ParseTree> T getChild(Class<? extends T> ctxType, int i) {
/* 179 */     if (this.children == null || i < 0 || i >= this.children.size()) {
/* 180 */       return null;
/*     */     }
/*     */     
/* 183 */     int j = -1;
/* 184 */     for (ParseTree o : this.children) {
/*     */       
/* 186 */       j++;
/* 187 */       if (ctxType.isInstance(o) && j == i) {
/* 188 */         return ctxType.cast(o);
/*     */       }
/*     */     } 
/*     */     
/* 192 */     return null;
/*     */   }
/*     */   
/*     */   public TerminalNode getToken(int ttype, int i) {
/* 196 */     if (this.children == null || i < 0 || i >= this.children.size()) {
/* 197 */       return null;
/*     */     }
/*     */     
/* 200 */     int j = -1;
/* 201 */     for (ParseTree o : this.children) {
/* 202 */       if (o instanceof TerminalNode) {
/* 203 */         TerminalNode tnode = (TerminalNode)o;
/* 204 */         Token symbol = tnode.getSymbol();
/*     */         
/* 206 */         j++;
/* 207 */         if (symbol.getType() == ttype && j == i) {
/* 208 */           return tnode;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 214 */     return null;
/*     */   }
/*     */   
/*     */   public List<TerminalNode> getTokens(int ttype) {
/* 218 */     if (this.children == null) {
/* 219 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 222 */     List<TerminalNode> tokens = null;
/* 223 */     for (ParseTree o : this.children) {
/* 224 */       if (o instanceof TerminalNode) {
/* 225 */         TerminalNode tnode = (TerminalNode)o;
/* 226 */         Token symbol = tnode.getSymbol();
/* 227 */         if (symbol.getType() == ttype) {
/* 228 */           if (tokens == null) {
/* 229 */             tokens = new ArrayList<TerminalNode>();
/*     */           }
/* 231 */           tokens.add(tnode);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 236 */     if (tokens == null) {
/* 237 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 240 */     return tokens;
/*     */   }
/*     */   
/*     */   public <T extends ParserRuleContext> T getRuleContext(Class<? extends T> ctxType, int i) {
/* 244 */     return (T)getChild(ctxType, i);
/*     */   }
/*     */   
/*     */   public <T extends ParserRuleContext> List<T> getRuleContexts(Class<? extends T> ctxType) {
/* 248 */     if (this.children == null) {
/* 249 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 252 */     List<T> contexts = null;
/* 253 */     for (ParseTree o : this.children) {
/* 254 */       if (ctxType.isInstance(o)) {
/* 255 */         if (contexts == null) {
/* 256 */           contexts = new ArrayList<T>();
/*     */         }
/*     */         
/* 259 */         contexts.add(ctxType.cast(o));
/*     */       } 
/*     */     } 
/*     */     
/* 263 */     if (contexts == null) {
/* 264 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 267 */     return contexts;
/*     */   }
/*     */   
/*     */   public int getChildCount() {
/* 271 */     return (this.children != null) ? this.children.size() : 0;
/*     */   }
/*     */   
/*     */   public Interval getSourceInterval() {
/* 275 */     if (this.start == null) {
/* 276 */       return Interval.INVALID;
/*     */     }
/* 278 */     if (this.stop == null || this.stop.getTokenIndex() < this.start.getTokenIndex()) {
/* 279 */       return Interval.of(this.start.getTokenIndex(), this.start.getTokenIndex() - 1);
/*     */     }
/* 281 */     return Interval.of(this.start.getTokenIndex(), this.stop.getTokenIndex());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Token getStart() {
/* 289 */     return this.start;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Token getStop() {
/* 295 */     return this.stop;
/*     */   }
/*     */   
/*     */   public String toInfoString(Parser recognizer) {
/* 299 */     List<String> rules = recognizer.getRuleInvocationStack(this);
/* 300 */     Collections.reverse(rules);
/* 301 */     return "ParserRuleContext" + rules + "{" + "start=" + this.start + ", stop=" + this.stop + '}';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\ParserRuleContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */