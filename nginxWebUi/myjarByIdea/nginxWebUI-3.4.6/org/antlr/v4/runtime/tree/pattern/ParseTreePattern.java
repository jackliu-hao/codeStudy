package org.antlr.v4.runtime.tree.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.xpath.XPath;

public class ParseTreePattern {
   private final int patternRuleIndex;
   private final String pattern;
   private final ParseTree patternTree;
   private final ParseTreePatternMatcher matcher;

   public ParseTreePattern(ParseTreePatternMatcher matcher, String pattern, int patternRuleIndex, ParseTree patternTree) {
      this.matcher = matcher;
      this.patternRuleIndex = patternRuleIndex;
      this.pattern = pattern;
      this.patternTree = patternTree;
   }

   public ParseTreeMatch match(ParseTree tree) {
      return this.matcher.match(tree, this);
   }

   public boolean matches(ParseTree tree) {
      return this.matcher.match(tree, this).succeeded();
   }

   public List<ParseTreeMatch> findAll(ParseTree tree, String xpath) {
      Collection<ParseTree> subtrees = XPath.findAll(tree, xpath, this.matcher.getParser());
      List<ParseTreeMatch> matches = new ArrayList();
      Iterator i$ = subtrees.iterator();

      while(i$.hasNext()) {
         ParseTree t = (ParseTree)i$.next();
         ParseTreeMatch match = this.match(t);
         if (match.succeeded()) {
            matches.add(match);
         }
      }

      return matches;
   }

   public ParseTreePatternMatcher getMatcher() {
      return this.matcher;
   }

   public String getPattern() {
      return this.pattern;
   }

   public int getPatternRuleIndex() {
      return this.patternRuleIndex;
   }

   public ParseTree getPatternTree() {
      return this.patternTree;
   }
}
