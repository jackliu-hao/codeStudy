/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.tree.ParseTree;
/*     */ import org.antlr.v4.runtime.tree.xpath.XPath;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParseTreePattern
/*     */ {
/*     */   private final int patternRuleIndex;
/*     */   private final String pattern;
/*     */   private final ParseTree patternTree;
/*     */   private final ParseTreePatternMatcher matcher;
/*     */   
/*     */   public ParseTreePattern(ParseTreePatternMatcher matcher, String pattern, int patternRuleIndex, ParseTree patternTree) {
/*  81 */     this.matcher = matcher;
/*  82 */     this.patternRuleIndex = patternRuleIndex;
/*  83 */     this.pattern = pattern;
/*  84 */     this.patternTree = patternTree;
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
/*     */   public ParseTreeMatch match(ParseTree tree) {
/*  97 */     return this.matcher.match(tree, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(ParseTree tree) {
/* 108 */     return this.matcher.match(tree, this).succeeded();
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
/*     */   public List<ParseTreeMatch> findAll(ParseTree tree, String xpath) {
/* 124 */     Collection<ParseTree> subtrees = XPath.findAll(tree, xpath, this.matcher.getParser());
/* 125 */     List<ParseTreeMatch> matches = new ArrayList<ParseTreeMatch>();
/* 126 */     for (ParseTree t : subtrees) {
/* 127 */       ParseTreeMatch match = match(t);
/* 128 */       if (match.succeeded()) {
/* 129 */         matches.add(match);
/*     */       }
/*     */     } 
/* 132 */     return matches;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreePatternMatcher getMatcher() {
/* 143 */     return this.matcher;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/* 153 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getPatternRuleIndex() {
/* 164 */     return this.patternRuleIndex;
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
/*     */   public ParseTree getPatternTree() {
/* 176 */     return this.patternTree;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\ParseTreePattern.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */