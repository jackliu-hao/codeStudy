/*     */ package org.antlr.v4.runtime.tree.pattern;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.ANTLRInputStream;
/*     */ import org.antlr.v4.runtime.BailErrorStrategy;
/*     */ import org.antlr.v4.runtime.CommonTokenStream;
/*     */ import org.antlr.v4.runtime.Lexer;
/*     */ import org.antlr.v4.runtime.ListTokenSource;
/*     */ import org.antlr.v4.runtime.Parser;
/*     */ import org.antlr.v4.runtime.ParserInterpreter;
/*     */ import org.antlr.v4.runtime.ParserRuleContext;
/*     */ import org.antlr.v4.runtime.RecognitionException;
/*     */ import org.antlr.v4.runtime.Token;
/*     */ import org.antlr.v4.runtime.misc.MultiMap;
/*     */ import org.antlr.v4.runtime.misc.ParseCancellationException;
/*     */ import org.antlr.v4.runtime.tree.ParseTree;
/*     */ import org.antlr.v4.runtime.tree.RuleNode;
/*     */ import org.antlr.v4.runtime.tree.TerminalNode;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParseTreePatternMatcher
/*     */ {
/*     */   private final Lexer lexer;
/*     */   private final Parser parser;
/*     */   
/*     */   public static class CannotInvokeStartRule
/*     */     extends RuntimeException
/*     */   {
/*     */     public CannotInvokeStartRule(Throwable e) {
/* 114 */       super(e);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class StartRuleDoesNotConsumeFullPattern
/*     */     extends RuntimeException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 133 */   protected String start = "<";
/* 134 */   protected String stop = ">";
/* 135 */   protected String escape = "\\";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreePatternMatcher(Lexer lexer, Parser parser) {
/* 144 */     this.lexer = lexer;
/* 145 */     this.parser = parser;
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
/*     */   public void setDelimiters(String start, String stop, String escapeLeft) {
/* 160 */     if (start == null || start.isEmpty()) {
/* 161 */       throw new IllegalArgumentException("start cannot be null or empty");
/*     */     }
/*     */     
/* 164 */     if (stop == null || stop.isEmpty()) {
/* 165 */       throw new IllegalArgumentException("stop cannot be null or empty");
/*     */     }
/*     */     
/* 168 */     this.start = start;
/* 169 */     this.stop = stop;
/* 170 */     this.escape = escapeLeft;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean matches(ParseTree tree, String pattern, int patternRuleIndex) {
/* 175 */     ParseTreePattern p = compile(pattern, patternRuleIndex);
/* 176 */     return matches(tree, p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean matches(ParseTree tree, ParseTreePattern pattern) {
/* 183 */     MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
/* 184 */     ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
/* 185 */     return (mismatchedNode == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreeMatch match(ParseTree tree, String pattern, int patternRuleIndex) {
/* 194 */     ParseTreePattern p = compile(pattern, patternRuleIndex);
/* 195 */     return match(tree, p);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreeMatch match(ParseTree tree, ParseTreePattern pattern) {
/* 206 */     MultiMap<String, ParseTree> labels = new MultiMap<String, ParseTree>();
/* 207 */     ParseTree mismatchedNode = matchImpl(tree, pattern.getPatternTree(), labels);
/* 208 */     return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParseTreePattern compile(String pattern, int patternRuleIndex) {
/* 216 */     List<? extends Token> tokenList = tokenize(pattern);
/* 217 */     ListTokenSource tokenSrc = new ListTokenSource(tokenList);
/* 218 */     CommonTokenStream tokens = new CommonTokenStream(tokenSrc);
/*     */     
/* 220 */     ParserInterpreter parserInterp = new ParserInterpreter(this.parser.getGrammarFileName(), this.parser.getVocabulary(), Arrays.asList(this.parser.getRuleNames()), this.parser.getATNWithBypassAlts(), tokens);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 226 */     ParseTree tree = null;
/*     */     try {
/* 228 */       parserInterp.setErrorHandler(new BailErrorStrategy());
/* 229 */       tree = parserInterp.parse(patternRuleIndex);
/*     */     
/*     */     }
/* 232 */     catch (ParseCancellationException e) {
/* 233 */       throw (RecognitionException)e.getCause();
/*     */     }
/* 235 */     catch (RecognitionException re) {
/* 236 */       throw re;
/*     */     }
/* 238 */     catch (Exception e) {
/* 239 */       throw new CannotInvokeStartRule(e);
/*     */     } 
/*     */ 
/*     */     
/* 243 */     if (tokens.LA(1) != -1) {
/* 244 */       throw new StartRuleDoesNotConsumeFullPattern();
/*     */     }
/*     */     
/* 247 */     return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lexer getLexer() {
/* 256 */     return this.lexer;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Parser getParser() {
/* 265 */     return this.parser;
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
/*     */   protected ParseTree matchImpl(ParseTree tree, ParseTree patternTree, MultiMap<String, ParseTree> labels) {
/* 284 */     if (tree == null) {
/* 285 */       throw new IllegalArgumentException("tree cannot be null");
/*     */     }
/*     */     
/* 288 */     if (patternTree == null) {
/* 289 */       throw new IllegalArgumentException("patternTree cannot be null");
/*     */     }
/*     */ 
/*     */     
/* 293 */     if (tree instanceof TerminalNode && patternTree instanceof TerminalNode) {
/* 294 */       TerminalNode t1 = (TerminalNode)tree;
/* 295 */       TerminalNode t2 = (TerminalNode)patternTree;
/* 296 */       ParseTree mismatchedNode = null;
/*     */       
/* 298 */       if (t1.getSymbol().getType() == t2.getSymbol().getType()) {
/* 299 */         if (t2.getSymbol() instanceof TokenTagToken) {
/* 300 */           TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
/*     */           
/* 302 */           labels.map(tokenTagToken.getTokenName(), tree);
/* 303 */           if (tokenTagToken.getLabel() != null) {
/* 304 */             labels.map(tokenTagToken.getLabel(), tree);
/*     */           }
/*     */         }
/* 307 */         else if (!t1.getText().equals(t2.getText())) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 312 */           if (mismatchedNode == null) {
/* 313 */             mismatchedNode = t1;
/*     */           }
/*     */         }
/*     */       
/*     */       }
/* 318 */       else if (mismatchedNode == null) {
/* 319 */         mismatchedNode = t1;
/*     */       } 
/*     */ 
/*     */       
/* 323 */       return mismatchedNode;
/*     */     } 
/*     */     
/* 326 */     if (tree instanceof ParserRuleContext && patternTree instanceof ParserRuleContext) {
/* 327 */       ParserRuleContext r1 = (ParserRuleContext)tree;
/* 328 */       ParserRuleContext r2 = (ParserRuleContext)patternTree;
/* 329 */       ParseTree mismatchedNode = null;
/*     */       
/* 331 */       RuleTagToken ruleTagToken = getRuleTagToken(r2);
/* 332 */       if (ruleTagToken != null) {
/* 333 */         ParseTreeMatch m = null;
/* 334 */         if (r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex()) {
/*     */           
/* 336 */           labels.map(ruleTagToken.getRuleName(), tree);
/* 337 */           if (ruleTagToken.getLabel() != null) {
/* 338 */             labels.map(ruleTagToken.getLabel(), tree);
/*     */           
/*     */           }
/*     */         }
/* 342 */         else if (mismatchedNode == null) {
/* 343 */           mismatchedNode = r1;
/*     */         } 
/*     */ 
/*     */         
/* 347 */         return mismatchedNode;
/*     */       } 
/*     */ 
/*     */       
/* 351 */       if (r1.getChildCount() != r2.getChildCount()) {
/* 352 */         if (mismatchedNode == null) {
/* 353 */           mismatchedNode = r1;
/*     */         }
/*     */         
/* 356 */         return mismatchedNode;
/*     */       } 
/*     */       
/* 359 */       int n = r1.getChildCount();
/* 360 */       for (int i = 0; i < n; i++) {
/* 361 */         ParseTree childMatch = matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
/* 362 */         if (childMatch != null) {
/* 363 */           return childMatch;
/*     */         }
/*     */       } 
/*     */       
/* 367 */       return mismatchedNode;
/*     */     } 
/*     */ 
/*     */     
/* 371 */     return tree;
/*     */   }
/*     */ 
/*     */   
/*     */   protected RuleTagToken getRuleTagToken(ParseTree t) {
/* 376 */     if (t instanceof RuleNode) {
/* 377 */       RuleNode r = (RuleNode)t;
/* 378 */       if (r.getChildCount() == 1 && r.getChild(0) instanceof TerminalNode) {
/* 379 */         TerminalNode c = (TerminalNode)r.getChild(0);
/* 380 */         if (c.getSymbol() instanceof RuleTagToken)
/*     */         {
/* 382 */           return (RuleTagToken)c.getSymbol();
/*     */         }
/*     */       } 
/*     */     } 
/* 386 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<? extends Token> tokenize(String pattern) {
/* 391 */     List<Chunk> chunks = split(pattern);
/*     */ 
/*     */     
/* 394 */     List<Token> tokens = new ArrayList<Token>();
/* 395 */     for (Chunk chunk : chunks) {
/* 396 */       if (chunk instanceof TagChunk) {
/* 397 */         TagChunk tagChunk = (TagChunk)chunk;
/*     */         
/* 399 */         if (Character.isUpperCase(tagChunk.getTag().charAt(0))) {
/* 400 */           Integer ttype = Integer.valueOf(this.parser.getTokenType(tagChunk.getTag()));
/* 401 */           if (ttype.intValue() == 0) {
/* 402 */             throw new IllegalArgumentException("Unknown token " + tagChunk.getTag() + " in pattern: " + pattern);
/*     */           }
/* 404 */           TokenTagToken tokenTagToken = new TokenTagToken(tagChunk.getTag(), ttype.intValue(), tagChunk.getLabel());
/* 405 */           tokens.add(tokenTagToken); continue;
/*     */         } 
/* 407 */         if (Character.isLowerCase(tagChunk.getTag().charAt(0))) {
/* 408 */           int ruleIndex = this.parser.getRuleIndex(tagChunk.getTag());
/* 409 */           if (ruleIndex == -1) {
/* 410 */             throw new IllegalArgumentException("Unknown rule " + tagChunk.getTag() + " in pattern: " + pattern);
/*     */           }
/* 412 */           int ruleImaginaryTokenType = (this.parser.getATNWithBypassAlts()).ruleToTokenType[ruleIndex];
/* 413 */           tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
/*     */           continue;
/*     */         } 
/* 416 */         throw new IllegalArgumentException("invalid tag: " + tagChunk.getTag() + " in pattern: " + pattern);
/*     */       } 
/*     */ 
/*     */       
/* 420 */       TextChunk textChunk = (TextChunk)chunk;
/* 421 */       ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
/* 422 */       this.lexer.setInputStream(in);
/* 423 */       Token t = this.lexer.nextToken();
/* 424 */       while (t.getType() != -1) {
/* 425 */         tokens.add(t);
/* 426 */         t = this.lexer.nextToken();
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 432 */     return tokens;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Chunk> split(String pattern) {
/* 437 */     int p = 0;
/* 438 */     int n = pattern.length();
/* 439 */     List<Chunk> chunks = new ArrayList<Chunk>();
/* 440 */     StringBuilder buf = new StringBuilder();
/*     */     
/* 442 */     List<Integer> starts = new ArrayList<Integer>();
/* 443 */     List<Integer> stops = new ArrayList<Integer>();
/* 444 */     while (p < n) {
/* 445 */       if (p == pattern.indexOf(this.escape + this.start, p)) {
/* 446 */         p += this.escape.length() + this.start.length(); continue;
/*     */       } 
/* 448 */       if (p == pattern.indexOf(this.escape + this.stop, p)) {
/* 449 */         p += this.escape.length() + this.stop.length(); continue;
/*     */       } 
/* 451 */       if (p == pattern.indexOf(this.start, p)) {
/* 452 */         starts.add(Integer.valueOf(p));
/* 453 */         p += this.start.length(); continue;
/*     */       } 
/* 455 */       if (p == pattern.indexOf(this.stop, p)) {
/* 456 */         stops.add(Integer.valueOf(p));
/* 457 */         p += this.stop.length();
/*     */         continue;
/*     */       } 
/* 460 */       p++;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 467 */     if (starts.size() > stops.size()) {
/* 468 */       throw new IllegalArgumentException("unterminated tag in pattern: " + pattern);
/*     */     }
/*     */     
/* 471 */     if (starts.size() < stops.size()) {
/* 472 */       throw new IllegalArgumentException("missing start tag in pattern: " + pattern);
/*     */     }
/*     */     
/* 475 */     int ntags = starts.size(); int i;
/* 476 */     for (i = 0; i < ntags; i++) {
/* 477 */       if (((Integer)starts.get(i)).intValue() >= ((Integer)stops.get(i)).intValue()) {
/* 478 */         throw new IllegalArgumentException("tag delimiters out of order in pattern: " + pattern);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 483 */     if (ntags == 0) {
/* 484 */       String text = pattern.substring(0, n);
/* 485 */       chunks.add(new TextChunk(text));
/*     */     } 
/*     */     
/* 488 */     if (ntags > 0 && ((Integer)starts.get(0)).intValue() > 0) {
/* 489 */       String text = pattern.substring(0, ((Integer)starts.get(0)).intValue());
/* 490 */       chunks.add(new TextChunk(text));
/*     */     } 
/* 492 */     for (i = 0; i < ntags; i++) {
/*     */       
/* 494 */       String tag = pattern.substring(((Integer)starts.get(i)).intValue() + this.start.length(), ((Integer)stops.get(i)).intValue());
/* 495 */       String ruleOrToken = tag;
/* 496 */       String label = null;
/* 497 */       int colon = tag.indexOf(':');
/* 498 */       if (colon >= 0) {
/* 499 */         label = tag.substring(0, colon);
/* 500 */         ruleOrToken = tag.substring(colon + 1, tag.length());
/*     */       } 
/* 502 */       chunks.add(new TagChunk(label, ruleOrToken));
/* 503 */       if (i + 1 < ntags) {
/*     */         
/* 505 */         String text = pattern.substring(((Integer)stops.get(i)).intValue() + this.stop.length(), ((Integer)starts.get(i + 1)).intValue());
/* 506 */         chunks.add(new TextChunk(text));
/*     */       } 
/*     */     } 
/* 509 */     if (ntags > 0) {
/* 510 */       int afterLastTag = ((Integer)stops.get(ntags - 1)).intValue() + this.stop.length();
/* 511 */       if (afterLastTag < n) {
/* 512 */         String text = pattern.substring(afterLastTag, n);
/* 513 */         chunks.add(new TextChunk(text));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 518 */     for (i = 0; i < chunks.size(); i++) {
/* 519 */       Chunk c = chunks.get(i);
/* 520 */       if (c instanceof TextChunk) {
/* 521 */         TextChunk tc = (TextChunk)c;
/* 522 */         String unescaped = tc.getText().replace(this.escape, "");
/* 523 */         if (unescaped.length() < tc.getText().length()) {
/* 524 */           chunks.set(i, new TextChunk(unescaped));
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 529 */     return chunks;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\ParseTreePatternMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */