/*     */ package org.antlr.v4.runtime.tree;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.CommonToken;
/*     */ import org.antlr.v4.runtime.Parser;
/*     */ import org.antlr.v4.runtime.ParserRuleContext;
/*     */ import org.antlr.v4.runtime.RuleContext;
/*     */ import org.antlr.v4.runtime.Token;
/*     */ import org.antlr.v4.runtime.misc.Interval;
/*     */ import org.antlr.v4.runtime.misc.Predicate;
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
/*     */ public class Trees
/*     */ {
/*     */   public static String toStringTree(Tree t) {
/*  56 */     return toStringTree(t, (List<String>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringTree(Tree t, Parser recog) {
/*  64 */     String[] ruleNames = (recog != null) ? recog.getRuleNames() : null;
/*  65 */     List<String> ruleNamesList = (ruleNames != null) ? Arrays.<String>asList(ruleNames) : null;
/*  66 */     return toStringTree(t, ruleNamesList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toStringTree(Tree t, List<String> ruleNames) {
/*  73 */     String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
/*  74 */     if (t.getChildCount() == 0) return s; 
/*  75 */     StringBuilder buf = new StringBuilder();
/*  76 */     buf.append("(");
/*  77 */     s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
/*  78 */     buf.append(s);
/*  79 */     buf.append(' ');
/*  80 */     for (int i = 0; i < t.getChildCount(); i++) {
/*  81 */       if (i > 0) buf.append(' '); 
/*  82 */       buf.append(toStringTree(t.getChild(i), ruleNames));
/*     */     } 
/*  84 */     buf.append(")");
/*  85 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public static String getNodeText(Tree t, Parser recog) {
/*  89 */     String[] ruleNames = (recog != null) ? recog.getRuleNames() : null;
/*  90 */     List<String> ruleNamesList = (ruleNames != null) ? Arrays.<String>asList(ruleNames) : null;
/*  91 */     return getNodeText(t, ruleNamesList);
/*     */   }
/*     */   
/*     */   public static String getNodeText(Tree t, List<String> ruleNames) {
/*  95 */     if (ruleNames != null) {
/*  96 */       if (t instanceof RuleContext) {
/*  97 */         int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
/*  98 */         String ruleName = ruleNames.get(ruleIndex);
/*  99 */         int altNumber = ((RuleContext)t).getAltNumber();
/* 100 */         if (altNumber != 0) {
/* 101 */           return ruleName + ":" + altNumber;
/*     */         }
/* 103 */         return ruleName;
/*     */       } 
/* 105 */       if (t instanceof ErrorNode) {
/* 106 */         return t.toString();
/*     */       }
/* 108 */       if (t instanceof TerminalNode) {
/* 109 */         Token symbol = ((TerminalNode)t).getSymbol();
/* 110 */         if (symbol != null) {
/* 111 */           String s = symbol.getText();
/* 112 */           return s;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 117 */     Object payload = t.getPayload();
/* 118 */     if (payload instanceof Token) {
/* 119 */       return ((Token)payload).getText();
/*     */     }
/* 121 */     return t.getPayload().toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<Tree> getChildren(Tree t) {
/* 126 */     List<Tree> kids = new ArrayList<Tree>();
/* 127 */     for (int i = 0; i < t.getChildCount(); i++) {
/* 128 */       kids.add(t.getChild(i));
/*     */     }
/* 130 */     return kids;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<? extends Tree> getAncestors(Tree t) {
/* 139 */     if (t.getParent() == null) return Collections.emptyList(); 
/* 140 */     List<Tree> ancestors = new ArrayList<Tree>();
/* 141 */     t = t.getParent();
/* 142 */     while (t != null) {
/* 143 */       ancestors.add(0, t);
/* 144 */       t = t.getParent();
/*     */     } 
/* 146 */     return ancestors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAncestorOf(Tree t, Tree u) {
/* 155 */     if (t == null || u == null || t.getParent() == null) return false; 
/* 156 */     Tree p = u.getParent();
/* 157 */     while (p != null) {
/* 158 */       if (t == p) return true; 
/* 159 */       p = p.getParent();
/*     */     } 
/* 161 */     return false;
/*     */   }
/*     */   
/*     */   public static Collection<ParseTree> findAllTokenNodes(ParseTree t, int ttype) {
/* 165 */     return findAllNodes(t, ttype, true);
/*     */   }
/*     */   
/*     */   public static Collection<ParseTree> findAllRuleNodes(ParseTree t, int ruleIndex) {
/* 169 */     return findAllNodes(t, ruleIndex, false);
/*     */   }
/*     */   
/*     */   public static List<ParseTree> findAllNodes(ParseTree t, int index, boolean findTokens) {
/* 173 */     List<ParseTree> nodes = new ArrayList<ParseTree>();
/* 174 */     _findAllNodes(t, index, findTokens, nodes);
/* 175 */     return nodes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void _findAllNodes(ParseTree t, int index, boolean findTokens, List<? super ParseTree> nodes) {
/* 182 */     if (findTokens && t instanceof TerminalNode) {
/* 183 */       TerminalNode tnode = (TerminalNode)t;
/* 184 */       if (tnode.getSymbol().getType() == index) nodes.add(t);
/*     */     
/* 186 */     } else if (!findTokens && t instanceof ParserRuleContext) {
/* 187 */       ParserRuleContext ctx = (ParserRuleContext)t;
/* 188 */       if (ctx.getRuleIndex() == index) nodes.add(t);
/*     */     
/*     */     } 
/* 191 */     for (int i = 0; i < t.getChildCount(); i++) {
/* 192 */       _findAllNodes(t.getChild(i), index, findTokens, nodes);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<ParseTree> getDescendants(ParseTree t) {
/* 201 */     List<ParseTree> nodes = new ArrayList<ParseTree>();
/* 202 */     nodes.add(t);
/*     */     
/* 204 */     int n = t.getChildCount();
/* 205 */     for (int i = 0; i < n; i++) {
/* 206 */       nodes.addAll(getDescendants(t.getChild(i)));
/*     */     }
/* 208 */     return nodes;
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<ParseTree> descendants(ParseTree t) {
/* 213 */     return getDescendants(t);
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
/*     */   public static ParserRuleContext getRootOfSubtreeEnclosingRegion(ParseTree t, int startTokenIndex, int stopTokenIndex) {
/* 225 */     int n = t.getChildCount();
/* 226 */     for (int i = 0; i < n; i++) {
/* 227 */       ParseTree child = t.getChild(i);
/* 228 */       ParserRuleContext r = getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
/* 229 */       if (r != null) return r; 
/*     */     } 
/* 231 */     if (t instanceof ParserRuleContext) {
/* 232 */       ParserRuleContext r = (ParserRuleContext)t;
/* 233 */       if (startTokenIndex >= r.getStart().getTokenIndex() && (r.getStop() == null || stopTokenIndex <= r.getStop().getTokenIndex()))
/*     */       {
/*     */ 
/*     */         
/* 237 */         return r;
/*     */       }
/*     */     } 
/* 240 */     return null;
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
/*     */   public static void stripChildrenOutOfRange(ParserRuleContext t, ParserRuleContext root, int startIndex, int stopIndex) {
/* 256 */     if (t == null)
/* 257 */       return;  for (int i = 0; i < t.getChildCount(); i++) {
/* 258 */       ParseTree child = t.getChild(i);
/* 259 */       Interval range = child.getSourceInterval();
/* 260 */       if (child instanceof ParserRuleContext && (range.b < startIndex || range.a > stopIndex) && 
/* 261 */         isAncestorOf(child, root)) {
/* 262 */         CommonToken abbrev = new CommonToken(0, "...");
/* 263 */         t.children.set(i, new TerminalNodeImpl(abbrev));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Tree findNodeSuchThat(Tree t, Predicate<Tree> pred) {
/* 274 */     if (pred.test(t)) return t;
/*     */     
/* 276 */     int n = t.getChildCount();
/* 277 */     for (int i = 0; i < n; i++) {
/* 278 */       Tree u = findNodeSuchThat(t.getChild(i), pred);
/* 279 */       if (u != null) return u; 
/*     */     } 
/* 281 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\Trees.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */