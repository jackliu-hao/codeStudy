package org.antlr.v4.runtime.tree.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserInterpreter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class ParseTreePatternMatcher {
   private final Lexer lexer;
   private final Parser parser;
   protected String start = "<";
   protected String stop = ">";
   protected String escape = "\\";

   public ParseTreePatternMatcher(Lexer lexer, Parser parser) {
      this.lexer = lexer;
      this.parser = parser;
   }

   public void setDelimiters(String start, String stop, String escapeLeft) {
      if (start != null && !start.isEmpty()) {
         if (stop != null && !stop.isEmpty()) {
            this.start = start;
            this.stop = stop;
            this.escape = escapeLeft;
         } else {
            throw new IllegalArgumentException("stop cannot be null or empty");
         }
      } else {
         throw new IllegalArgumentException("start cannot be null or empty");
      }
   }

   public boolean matches(ParseTree tree, String pattern, int patternRuleIndex) {
      ParseTreePattern p = this.compile(pattern, patternRuleIndex);
      return this.matches(tree, p);
   }

   public boolean matches(ParseTree tree, ParseTreePattern pattern) {
      MultiMap<String, ParseTree> labels = new MultiMap();
      ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
      return mismatchedNode == null;
   }

   public ParseTreeMatch match(ParseTree tree, String pattern, int patternRuleIndex) {
      ParseTreePattern p = this.compile(pattern, patternRuleIndex);
      return this.match(tree, p);
   }

   public ParseTreeMatch match(ParseTree tree, ParseTreePattern pattern) {
      MultiMap<String, ParseTree> labels = new MultiMap();
      ParseTree mismatchedNode = this.matchImpl(tree, pattern.getPatternTree(), labels);
      return new ParseTreeMatch(tree, pattern, labels, mismatchedNode);
   }

   public ParseTreePattern compile(String pattern, int patternRuleIndex) {
      List<? extends Token> tokenList = this.tokenize(pattern);
      ListTokenSource tokenSrc = new ListTokenSource(tokenList);
      CommonTokenStream tokens = new CommonTokenStream(tokenSrc);
      ParserInterpreter parserInterp = new ParserInterpreter(this.parser.getGrammarFileName(), this.parser.getVocabulary(), Arrays.asList(this.parser.getRuleNames()), this.parser.getATNWithBypassAlts(), tokens);
      ParseTree tree = null;

      try {
         parserInterp.setErrorHandler(new BailErrorStrategy());
         tree = parserInterp.parse(patternRuleIndex);
      } catch (ParseCancellationException var9) {
         throw (RecognitionException)var9.getCause();
      } catch (RecognitionException var10) {
         throw var10;
      } catch (Exception var11) {
         throw new CannotInvokeStartRule(var11);
      }

      if (tokens.LA(1) != -1) {
         throw new StartRuleDoesNotConsumeFullPattern();
      } else {
         return new ParseTreePattern(this, pattern, patternRuleIndex, tree);
      }
   }

   public Lexer getLexer() {
      return this.lexer;
   }

   public Parser getParser() {
      return this.parser;
   }

   protected ParseTree matchImpl(ParseTree tree, ParseTree patternTree, MultiMap<String, ParseTree> labels) {
      if (tree == null) {
         throw new IllegalArgumentException("tree cannot be null");
      } else if (patternTree == null) {
         throw new IllegalArgumentException("patternTree cannot be null");
      } else if (tree instanceof TerminalNode && patternTree instanceof TerminalNode) {
         TerminalNode t1 = (TerminalNode)tree;
         TerminalNode t2 = (TerminalNode)patternTree;
         ParseTree mismatchedNode = null;
         if (t1.getSymbol().getType() == t2.getSymbol().getType()) {
            if (t2.getSymbol() instanceof TokenTagToken) {
               TokenTagToken tokenTagToken = (TokenTagToken)t2.getSymbol();
               labels.map(tokenTagToken.getTokenName(), tree);
               if (tokenTagToken.getLabel() != null) {
                  labels.map(tokenTagToken.getLabel(), tree);
               }
            } else if (!t1.getText().equals(t2.getText()) && mismatchedNode == null) {
               mismatchedNode = t1;
            }
         } else if (mismatchedNode == null) {
            mismatchedNode = t1;
         }

         return mismatchedNode;
      } else if (tree instanceof ParserRuleContext && patternTree instanceof ParserRuleContext) {
         ParserRuleContext r1 = (ParserRuleContext)tree;
         ParserRuleContext r2 = (ParserRuleContext)patternTree;
         ParseTree mismatchedNode = null;
         RuleTagToken ruleTagToken = this.getRuleTagToken(r2);
         if (ruleTagToken != null) {
            ParseTreeMatch m = null;
            if (r1.getRuleContext().getRuleIndex() == r2.getRuleContext().getRuleIndex()) {
               labels.map(ruleTagToken.getRuleName(), tree);
               if (ruleTagToken.getLabel() != null) {
                  labels.map(ruleTagToken.getLabel(), tree);
               }
            } else if (mismatchedNode == null) {
               mismatchedNode = r1;
            }

            return mismatchedNode;
         } else if (r1.getChildCount() != r2.getChildCount()) {
            if (mismatchedNode == null) {
               mismatchedNode = r1;
            }

            return mismatchedNode;
         } else {
            int n = r1.getChildCount();

            for(int i = 0; i < n; ++i) {
               ParseTree childMatch = this.matchImpl(r1.getChild(i), patternTree.getChild(i), labels);
               if (childMatch != null) {
                  return childMatch;
               }
            }

            return mismatchedNode;
         }
      } else {
         return tree;
      }
   }

   protected RuleTagToken getRuleTagToken(ParseTree t) {
      if (t instanceof RuleNode) {
         RuleNode r = (RuleNode)t;
         if (r.getChildCount() == 1 && r.getChild(0) instanceof TerminalNode) {
            TerminalNode c = (TerminalNode)r.getChild(0);
            if (c.getSymbol() instanceof RuleTagToken) {
               return (RuleTagToken)c.getSymbol();
            }
         }
      }

      return null;
   }

   public List<? extends Token> tokenize(String pattern) {
      List<Chunk> chunks = this.split(pattern);
      List<Token> tokens = new ArrayList();
      Iterator i$ = chunks.iterator();

      while(true) {
         while(i$.hasNext()) {
            Chunk chunk = (Chunk)i$.next();
            if (chunk instanceof TagChunk) {
               TagChunk tagChunk = (TagChunk)chunk;
               if (Character.isUpperCase(tagChunk.getTag().charAt(0))) {
                  Integer ttype = this.parser.getTokenType(tagChunk.getTag());
                  if (ttype == 0) {
                     throw new IllegalArgumentException("Unknown token " + tagChunk.getTag() + " in pattern: " + pattern);
                  }

                  TokenTagToken t = new TokenTagToken(tagChunk.getTag(), ttype, tagChunk.getLabel());
                  tokens.add(t);
               } else {
                  if (!Character.isLowerCase(tagChunk.getTag().charAt(0))) {
                     throw new IllegalArgumentException("invalid tag: " + tagChunk.getTag() + " in pattern: " + pattern);
                  }

                  int ruleIndex = this.parser.getRuleIndex(tagChunk.getTag());
                  if (ruleIndex == -1) {
                     throw new IllegalArgumentException("Unknown rule " + tagChunk.getTag() + " in pattern: " + pattern);
                  }

                  int ruleImaginaryTokenType = this.parser.getATNWithBypassAlts().ruleToTokenType[ruleIndex];
                  tokens.add(new RuleTagToken(tagChunk.getTag(), ruleImaginaryTokenType, tagChunk.getLabel()));
               }
            } else {
               TextChunk textChunk = (TextChunk)chunk;
               ANTLRInputStream in = new ANTLRInputStream(textChunk.getText());
               this.lexer.setInputStream(in);

               for(Token t = this.lexer.nextToken(); t.getType() != -1; t = this.lexer.nextToken()) {
                  tokens.add(t);
               }
            }
         }

         return tokens;
      }
   }

   public List<Chunk> split(String pattern) {
      int p = 0;
      int n = pattern.length();
      List<Chunk> chunks = new ArrayList();
      new StringBuilder();
      List<Integer> starts = new ArrayList();
      List<Integer> stops = new ArrayList();

      while(p < n) {
         if (p == pattern.indexOf(this.escape + this.start, p)) {
            p += this.escape.length() + this.start.length();
         } else if (p == pattern.indexOf(this.escape + this.stop, p)) {
            p += this.escape.length() + this.stop.length();
         } else if (p == pattern.indexOf(this.start, p)) {
            starts.add(p);
            p += this.start.length();
         } else if (p == pattern.indexOf(this.stop, p)) {
            stops.add(p);
            p += this.stop.length();
         } else {
            ++p;
         }
      }

      if (starts.size() > stops.size()) {
         throw new IllegalArgumentException("unterminated tag in pattern: " + pattern);
      } else if (starts.size() < stops.size()) {
         throw new IllegalArgumentException("missing start tag in pattern: " + pattern);
      } else {
         int ntags = starts.size();

         int i;
         for(i = 0; i < ntags; ++i) {
            if ((Integer)starts.get(i) >= (Integer)stops.get(i)) {
               throw new IllegalArgumentException("tag delimiters out of order in pattern: " + pattern);
            }
         }

         String text;
         if (ntags == 0) {
            text = pattern.substring(0, n);
            chunks.add(new TextChunk(text));
         }

         if (ntags > 0 && (Integer)starts.get(0) > 0) {
            text = pattern.substring(0, (Integer)starts.get(0));
            chunks.add(new TextChunk(text));
         }

         String text;
         String unescaped;
         for(i = 0; i < ntags; ++i) {
            text = pattern.substring((Integer)starts.get(i) + this.start.length(), (Integer)stops.get(i));
            String ruleOrToken = text;
            unescaped = null;
            int colon = text.indexOf(58);
            if (colon >= 0) {
               unescaped = text.substring(0, colon);
               ruleOrToken = text.substring(colon + 1, text.length());
            }

            chunks.add(new TagChunk(unescaped, ruleOrToken));
            if (i + 1 < ntags) {
               String text = pattern.substring((Integer)stops.get(i) + this.stop.length(), (Integer)starts.get(i + 1));
               chunks.add(new TextChunk(text));
            }
         }

         if (ntags > 0) {
            i = (Integer)stops.get(ntags - 1) + this.stop.length();
            if (i < n) {
               text = pattern.substring(i, n);
               chunks.add(new TextChunk(text));
            }
         }

         for(i = 0; i < chunks.size(); ++i) {
            Chunk c = (Chunk)chunks.get(i);
            if (c instanceof TextChunk) {
               TextChunk tc = (TextChunk)c;
               unescaped = tc.getText().replace(this.escape, "");
               if (unescaped.length() < tc.getText().length()) {
                  chunks.set(i, new TextChunk(unescaped));
               }
            }
         }

         return chunks;
      }
   }

   public static class StartRuleDoesNotConsumeFullPattern extends RuntimeException {
   }

   public static class CannotInvokeStartRule extends RuntimeException {
      public CannotInvokeStartRule(Throwable e) {
         super(e);
      }
   }
}
