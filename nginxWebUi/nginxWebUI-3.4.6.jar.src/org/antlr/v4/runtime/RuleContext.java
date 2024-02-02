/*     */ package org.antlr.v4.runtime;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.tree.ParseTree;
/*     */ import org.antlr.v4.runtime.tree.ParseTreeVisitor;
/*     */ import org.antlr.v4.runtime.tree.RuleNode;
/*     */ import org.antlr.v4.runtime.tree.Tree;
/*     */ import org.antlr.v4.runtime.tree.Trees;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RuleContext
/*     */   implements RuleNode
/*     */ {
/*  93 */   public static final ParserRuleContext EMPTY = new ParserRuleContext();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleContext parent;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   public int invokingState = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleContext(RuleContext parent, int invokingState) {
/* 108 */     this.parent = parent;
/*     */     
/* 110 */     this.invokingState = invokingState;
/*     */   }
/*     */   
/*     */   public int depth() {
/* 114 */     int n = 0;
/* 115 */     RuleContext p = this;
/* 116 */     while (p != null) {
/* 117 */       p = p.parent;
/* 118 */       n++;
/*     */     } 
/* 120 */     return n;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 127 */     return (this.invokingState == -1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Interval getSourceInterval() {
/* 134 */     return Interval.INVALID;
/*     */   }
/*     */   
/*     */   public RuleContext getRuleContext() {
/* 138 */     return this;
/*     */   }
/*     */   public RuleContext getParent() {
/* 141 */     return this.parent;
/*     */   }
/*     */   public RuleContext getPayload() {
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getText() {
/* 155 */     if (getChildCount() == 0) {
/* 156 */       return "";
/*     */     }
/*     */     
/* 159 */     StringBuilder builder = new StringBuilder();
/* 160 */     for (int i = 0; i < getChildCount(); i++) {
/* 161 */       builder.append(getChild(i).getText());
/*     */     }
/*     */     
/* 164 */     return builder.toString();
/*     */   }
/*     */   public int getRuleIndex() {
/* 167 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAltNumber() {
/* 178 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAltNumber(int altNumber) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTree getChild(int i) {
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount() {
/* 197 */     return 0;
/*     */   }
/*     */   
/*     */   public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
/* 201 */     return visitor.visitChildren(this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringTree(Parser recog) {
/* 209 */     return Trees.toStringTree(this, recog);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringTree(List<String> ruleNames) {
/* 216 */     return Trees.toStringTree(this, ruleNames);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toStringTree() {
/* 221 */     return toStringTree((List<String>)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 226 */     return toString((List<String>)null, (RuleContext)null);
/*     */   }
/*     */   
/*     */   public final String toString(Recognizer<?, ?> recog) {
/* 230 */     return toString(recog, ParserRuleContext.EMPTY);
/*     */   }
/*     */   
/*     */   public final String toString(List<String> ruleNames) {
/* 234 */     return toString(ruleNames, (RuleContext)null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString(Recognizer<?, ?> recog, RuleContext stop) {
/* 239 */     String[] ruleNames = (recog != null) ? recog.getRuleNames() : null;
/* 240 */     List<String> ruleNamesList = (ruleNames != null) ? Arrays.<String>asList(ruleNames) : null;
/* 241 */     return toString(ruleNamesList, stop);
/*     */   }
/*     */   
/*     */   public String toString(List<String> ruleNames, RuleContext stop) {
/* 245 */     StringBuilder buf = new StringBuilder();
/* 246 */     RuleContext p = this;
/* 247 */     buf.append("[");
/* 248 */     while (p != null && p != stop) {
/* 249 */       if (ruleNames == null) {
/* 250 */         if (!p.isEmpty()) {
/* 251 */           buf.append(p.invokingState);
/*     */         }
/*     */       } else {
/*     */         
/* 255 */         int ruleIndex = p.getRuleIndex();
/* 256 */         String ruleName = (ruleIndex >= 0 && ruleIndex < ruleNames.size()) ? ruleNames.get(ruleIndex) : Integer.toString(ruleIndex);
/* 257 */         buf.append(ruleName);
/*     */       } 
/*     */       
/* 260 */       if (p.parent != null && (ruleNames != null || !p.parent.isEmpty())) {
/* 261 */         buf.append(" ");
/*     */       }
/*     */       
/* 264 */       p = p.parent;
/*     */     } 
/*     */     
/* 267 */     buf.append("]");
/* 268 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public RuleContext() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\RuleContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */