package org.antlr.v4.runtime.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Predicate;
import org.antlr.v4.runtime.misc.Utils;

public class Trees {
   public static String toStringTree(Tree t) {
      return toStringTree(t, (List)null);
   }

   public static String toStringTree(Tree t, Parser recog) {
      String[] ruleNames = recog != null ? recog.getRuleNames() : null;
      List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
      return toStringTree(t, ruleNamesList);
   }

   public static String toStringTree(Tree t, List<String> ruleNames) {
      String s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
      if (t.getChildCount() == 0) {
         return s;
      } else {
         StringBuilder buf = new StringBuilder();
         buf.append("(");
         s = Utils.escapeWhitespace(getNodeText(t, ruleNames), false);
         buf.append(s);
         buf.append(' ');

         for(int i = 0; i < t.getChildCount(); ++i) {
            if (i > 0) {
               buf.append(' ');
            }

            buf.append(toStringTree(t.getChild(i), ruleNames));
         }

         buf.append(")");
         return buf.toString();
      }
   }

   public static String getNodeText(Tree t, Parser recog) {
      String[] ruleNames = recog != null ? recog.getRuleNames() : null;
      List<String> ruleNamesList = ruleNames != null ? Arrays.asList(ruleNames) : null;
      return getNodeText(t, ruleNamesList);
   }

   public static String getNodeText(Tree t, List<String> ruleNames) {
      if (ruleNames != null) {
         String s;
         if (t instanceof RuleContext) {
            int ruleIndex = ((RuleContext)t).getRuleContext().getRuleIndex();
            s = (String)ruleNames.get(ruleIndex);
            int altNumber = ((RuleContext)t).getAltNumber();
            if (altNumber != 0) {
               return s + ":" + altNumber;
            }

            return s;
         }

         if (t instanceof ErrorNode) {
            return t.toString();
         }

         if (t instanceof TerminalNode) {
            Token symbol = ((TerminalNode)t).getSymbol();
            if (symbol != null) {
               s = symbol.getText();
               return s;
            }
         }
      }

      Object payload = t.getPayload();
      return payload instanceof Token ? ((Token)payload).getText() : t.getPayload().toString();
   }

   public static List<Tree> getChildren(Tree t) {
      List<Tree> kids = new ArrayList();

      for(int i = 0; i < t.getChildCount(); ++i) {
         kids.add(t.getChild(i));
      }

      return kids;
   }

   public static List<? extends Tree> getAncestors(Tree t) {
      if (t.getParent() == null) {
         return Collections.emptyList();
      } else {
         List<Tree> ancestors = new ArrayList();

         for(t = t.getParent(); t != null; t = t.getParent()) {
            ancestors.add(0, t);
         }

         return ancestors;
      }
   }

   public static boolean isAncestorOf(Tree t, Tree u) {
      if (t != null && u != null && t.getParent() != null) {
         for(Tree p = u.getParent(); p != null; p = p.getParent()) {
            if (t == p) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static Collection<ParseTree> findAllTokenNodes(ParseTree t, int ttype) {
      return findAllNodes(t, ttype, true);
   }

   public static Collection<ParseTree> findAllRuleNodes(ParseTree t, int ruleIndex) {
      return findAllNodes(t, ruleIndex, false);
   }

   public static List<ParseTree> findAllNodes(ParseTree t, int index, boolean findTokens) {
      List<ParseTree> nodes = new ArrayList();
      _findAllNodes(t, index, findTokens, nodes);
      return nodes;
   }

   public static void _findAllNodes(ParseTree t, int index, boolean findTokens, List<? super ParseTree> nodes) {
      if (findTokens && t instanceof TerminalNode) {
         TerminalNode tnode = (TerminalNode)t;
         if (tnode.getSymbol().getType() == index) {
            nodes.add(t);
         }
      } else if (!findTokens && t instanceof ParserRuleContext) {
         ParserRuleContext ctx = (ParserRuleContext)t;
         if (ctx.getRuleIndex() == index) {
            nodes.add(t);
         }
      }

      for(int i = 0; i < t.getChildCount(); ++i) {
         _findAllNodes(t.getChild(i), index, findTokens, nodes);
      }

   }

   public static List<ParseTree> getDescendants(ParseTree t) {
      List<ParseTree> nodes = new ArrayList();
      nodes.add(t);
      int n = t.getChildCount();

      for(int i = 0; i < n; ++i) {
         nodes.addAll(getDescendants(t.getChild(i)));
      }

      return nodes;
   }

   /** @deprecated */
   public static List<ParseTree> descendants(ParseTree t) {
      return getDescendants(t);
   }

   public static ParserRuleContext getRootOfSubtreeEnclosingRegion(ParseTree t, int startTokenIndex, int stopTokenIndex) {
      int n = t.getChildCount();

      for(int i = 0; i < n; ++i) {
         ParseTree child = t.getChild(i);
         ParserRuleContext r = getRootOfSubtreeEnclosingRegion(child, startTokenIndex, stopTokenIndex);
         if (r != null) {
            return r;
         }
      }

      if (t instanceof ParserRuleContext) {
         ParserRuleContext r = (ParserRuleContext)t;
         if (startTokenIndex >= r.getStart().getTokenIndex() && (r.getStop() == null || stopTokenIndex <= r.getStop().getTokenIndex())) {
            return r;
         }
      }

      return null;
   }

   public static void stripChildrenOutOfRange(ParserRuleContext t, ParserRuleContext root, int startIndex, int stopIndex) {
      if (t != null) {
         for(int i = 0; i < t.getChildCount(); ++i) {
            ParseTree child = t.getChild(i);
            Interval range = child.getSourceInterval();
            if (child instanceof ParserRuleContext && (range.b < startIndex || range.a > stopIndex) && isAncestorOf(child, root)) {
               CommonToken abbrev = new CommonToken(0, "...");
               t.children.set(i, new TerminalNodeImpl(abbrev));
            }
         }

      }
   }

   public static Tree findNodeSuchThat(Tree t, Predicate<Tree> pred) {
      if (pred.test(t)) {
         return t;
      } else {
         int n = t.getChildCount();

         for(int i = 0; i < n; ++i) {
            Tree u = findNodeSuchThat(t.getChild(i), pred);
            if (u != null) {
               return u;
            }
         }

         return null;
      }
   }

   private Trees() {
   }
}
