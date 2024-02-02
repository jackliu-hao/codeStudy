/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.misc.MultiMap;
/*     */ import org.antlr.v4.runtime.tree.ParseTree;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParseTreeMatch
/*     */ {
/*     */   private final ParseTree tree;
/*     */   private final ParseTreePattern pattern;
/*     */   private final MultiMap<String, ParseTree> labels;
/*     */   private final ParseTree mismatchedNode;
/*     */   
/*     */   public ParseTreeMatch(ParseTree tree, ParseTreePattern pattern, MultiMap<String, ParseTree> labels, ParseTree mismatchedNode) {
/*  79 */     if (tree == null) {
/*  80 */       throw new IllegalArgumentException("tree cannot be null");
/*     */     }
/*     */     
/*  83 */     if (pattern == null) {
/*  84 */       throw new IllegalArgumentException("pattern cannot be null");
/*     */     }
/*     */     
/*  87 */     if (labels == null) {
/*  88 */       throw new IllegalArgumentException("labels cannot be null");
/*     */     }
/*     */     
/*  91 */     this.tree = tree;
/*  92 */     this.pattern = pattern;
/*  93 */     this.labels = labels;
/*  94 */     this.mismatchedNode = mismatchedNode;
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
/*     */   public ParseTree get(String label) {
/* 115 */     List<ParseTree> parseTrees = this.labels.get(label);
/* 116 */     if (parseTrees == null || parseTrees.size() == 0) {
/* 117 */       return null;
/*     */     }
/*     */     
/* 120 */     return parseTrees.get(parseTrees.size() - 1);
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
/*     */   public List<ParseTree> getAll(String label) {
/* 148 */     List<ParseTree> nodes = this.labels.get(label);
/* 149 */     if (nodes == null) {
/* 150 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 153 */     return nodes;
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
/*     */   public MultiMap<String, ParseTree> getLabels() {
/* 168 */     return this.labels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTree getMismatchedNode() {
/* 179 */     return this.mismatchedNode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean succeeded() {
/* 189 */     return (this.mismatchedNode == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreePattern getPattern() {
/* 199 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTree getTree() {
/* 209 */     return this.tree;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 217 */     return String.format("Match %s; found %d labels", new Object[] { succeeded() ? "succeeded" : "failed", Integer.valueOf(getLabels().size()) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\ParseTreeMatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */