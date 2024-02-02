package org.antlr.v4.runtime.tree.xpath;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPath {
   public static final String WILDCARD = "*";
   public static final String NOT = "!";
   protected String path;
   protected XPathElement[] elements;
   protected Parser parser;

   public XPath(Parser parser, String path) {
      this.parser = parser;
      this.path = path;
      this.elements = this.split(path);
   }

   public XPathElement[] split(String path) {
      ANTLRInputStream in;
      try {
         in = new ANTLRInputStream(new StringReader(path));
      } catch (IOException var15) {
         throw new IllegalArgumentException("Could not read path: " + path, var15);
      }

      XPathLexer lexer = new XPathLexer(in) {
         public void recover(LexerNoViableAltException e) {
            throw e;
         }
      };
      lexer.removeErrorListeners();
      lexer.addErrorListener(new XPathLexerErrorListener());
      CommonTokenStream tokenStream = new CommonTokenStream(lexer);

      try {
         tokenStream.fill();
      } catch (LexerNoViableAltException var14) {
         int pos = lexer.getCharPositionInLine();
         String msg = "Invalid tokens or characters at index " + pos + " in path '" + path + "'";
         throw new IllegalArgumentException(msg, var14);
      }

      List<Token> tokens = tokenStream.getTokens();
      List<XPathElement> elements = new ArrayList();
      int n = tokens.size();
      int i = 0;

      while(true) {
         if (i < n) {
            Token el = (Token)tokens.get(i);
            Token next = null;
            switch (el.getType()) {
               case -1:
                  break;
               case 0:
               default:
                  throw new IllegalArgumentException("Unknowth path element " + el);
               case 1:
               case 2:
               case 5:
                  elements.add(this.getXPathElement(el, false));
                  ++i;
                  continue;
               case 3:
               case 4:
                  boolean anywhere = el.getType() == 3;
                  ++i;
                  next = (Token)tokens.get(i);
                  boolean invert = next.getType() == 6;
                  if (invert) {
                     ++i;
                     next = (Token)tokens.get(i);
                  }

                  XPathElement pathElement = this.getXPathElement(next, anywhere);
                  pathElement.invert = invert;
                  elements.add(pathElement);
                  ++i;
                  continue;
            }
         }

         return (XPathElement[])elements.toArray(new XPathElement[0]);
      }
   }

   protected XPathElement getXPathElement(Token wordToken, boolean anywhere) {
      if (wordToken.getType() == -1) {
         throw new IllegalArgumentException("Missing path element at end of path");
      } else {
         String word = wordToken.getText();
         int ttype = this.parser.getTokenType(word);
         int ruleIndex = this.parser.getRuleIndex(word);
         switch (wordToken.getType()) {
            case 1:
            case 8:
               if (ttype == 0) {
                  throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid token name");
               }

               return (XPathElement)(anywhere ? new XPathTokenAnywhereElement(word, ttype) : new XPathTokenElement(word, ttype));
            case 5:
               return (XPathElement)(anywhere ? new XPathWildcardAnywhereElement() : new XPathWildcardElement());
            default:
               if (ruleIndex == -1) {
                  throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid rule name");
               } else {
                  return (XPathElement)(anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : new XPathRuleElement(word, ruleIndex));
               }
         }
      }
   }

   public static Collection<ParseTree> findAll(ParseTree tree, String xpath, Parser parser) {
      XPath p = new XPath(parser, xpath);
      return p.evaluate(tree);
   }

   public Collection<ParseTree> evaluate(ParseTree t) {
      ParserRuleContext dummyRoot = new ParserRuleContext();
      dummyRoot.children = Collections.singletonList(t);
      Collection<ParseTree> work = Collections.singleton(dummyRoot);

      LinkedHashSet next;
      for(int i = 0; i < this.elements.length; work = next) {
         next = new LinkedHashSet();
         Iterator i$ = ((Collection)work).iterator();

         while(i$.hasNext()) {
            ParseTree node = (ParseTree)i$.next();
            if (node.getChildCount() > 0) {
               Collection<? extends ParseTree> matching = this.elements[i].evaluate(node);
               next.addAll(matching);
            }
         }

         ++i;
      }

      return (Collection)work;
   }
}
