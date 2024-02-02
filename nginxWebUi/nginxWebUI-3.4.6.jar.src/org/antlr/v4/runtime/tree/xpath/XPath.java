/*     */ package org.antlr.v4.runtime.tree.xpath;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import org.antlr.v4.runtime.ANTLRInputStream;
/*     */ import org.antlr.v4.runtime.CharStream;
/*     */ import org.antlr.v4.runtime.CommonTokenStream;
/*     */ import org.antlr.v4.runtime.LexerNoViableAltException;
/*     */ import org.antlr.v4.runtime.Parser;
/*     */ import org.antlr.v4.runtime.ParserRuleContext;
/*     */ import org.antlr.v4.runtime.Token;
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
/*     */ public class XPath
/*     */ {
/*     */   public static final String WILDCARD = "*";
/*     */   public static final String NOT = "!";
/*     */   protected String path;
/*     */   protected XPathElement[] elements;
/*     */   protected Parser parser;
/*     */   
/*     */   public XPath(Parser parser, String path) {
/*  71 */     this.parser = parser;
/*  72 */     this.path = path;
/*  73 */     this.elements = split(path);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public XPathElement[] split(String path) {
/*     */     ANTLRInputStream in;
/*     */     try {
/*  82 */       in = new ANTLRInputStream(new StringReader(path));
/*     */     }
/*  84 */     catch (IOException ioe) {
/*  85 */       throw new IllegalArgumentException("Could not read path: " + path, ioe);
/*     */     } 
/*  87 */     XPathLexer lexer = new XPathLexer(in) {
/*  88 */         public void recover(LexerNoViableAltException e) { throw e; }
/*     */       };
/*  90 */     lexer.removeErrorListeners();
/*  91 */     lexer.addErrorListener(new XPathLexerErrorListener());
/*  92 */     CommonTokenStream tokenStream = new CommonTokenStream(lexer);
/*     */     try {
/*  94 */       tokenStream.fill();
/*     */     }
/*  96 */     catch (LexerNoViableAltException e) {
/*  97 */       int pos = lexer.getCharPositionInLine();
/*  98 */       String msg = "Invalid tokens or characters at index " + pos + " in path '" + path + "'";
/*  99 */       throw new IllegalArgumentException(msg, e);
/*     */     } 
/*     */     
/* 102 */     List<Token> tokens = tokenStream.getTokens();
/*     */     
/* 104 */     List<XPathElement> elements = new ArrayList<XPathElement>();
/* 105 */     int n = tokens.size();
/* 106 */     int i = 0;
/*     */     
/* 108 */     while (i < n) {
/* 109 */       boolean anywhere, invert; XPathElement pathElement; Token el = tokens.get(i);
/* 110 */       Token next = null;
/* 111 */       switch (el.getType()) {
/*     */         case 3:
/*     */         case 4:
/* 114 */           anywhere = (el.getType() == 3);
/* 115 */           i++;
/* 116 */           next = tokens.get(i);
/* 117 */           invert = (next.getType() == 6);
/* 118 */           if (invert) {
/* 119 */             i++;
/* 120 */             next = tokens.get(i);
/*     */           } 
/* 122 */           pathElement = getXPathElement(next, anywhere);
/* 123 */           pathElement.invert = invert;
/* 124 */           elements.add(pathElement);
/* 125 */           i++;
/*     */           continue;
/*     */         
/*     */         case 1:
/*     */         case 2:
/*     */         case 5:
/* 131 */           elements.add(getXPathElement(el, false));
/* 132 */           i++;
/*     */           continue;
/*     */         
/*     */         case -1:
/*     */           break;
/*     */       } 
/*     */       
/* 139 */       throw new IllegalArgumentException("Unknowth path element " + el);
/*     */     } 
/*     */     
/* 142 */     return elements.<XPathElement>toArray(new XPathElement[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected XPathElement getXPathElement(Token wordToken, boolean anywhere) {
/* 151 */     if (wordToken.getType() == -1) {
/* 152 */       throw new IllegalArgumentException("Missing path element at end of path");
/*     */     }
/* 154 */     String word = wordToken.getText();
/* 155 */     int ttype = this.parser.getTokenType(word);
/* 156 */     int ruleIndex = this.parser.getRuleIndex(word);
/* 157 */     switch (wordToken.getType()) {
/*     */       case 5:
/* 159 */         return anywhere ? new XPathWildcardAnywhereElement() : new XPathWildcardElement();
/*     */ 
/*     */       
/*     */       case 1:
/*     */       case 8:
/* 164 */         if (ttype == 0) {
/* 165 */           throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid token name");
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 170 */         return anywhere ? new XPathTokenAnywhereElement(word, ttype) : new XPathTokenElement(word, ttype);
/*     */     } 
/*     */ 
/*     */     
/* 174 */     if (ruleIndex == -1) {
/* 175 */       throw new IllegalArgumentException(word + " at index " + wordToken.getStartIndex() + " isn't a valid rule name");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 180 */     return anywhere ? new XPathRuleAnywhereElement(word, ruleIndex) : new XPathRuleElement(word, ruleIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Collection<ParseTree> findAll(ParseTree tree, String xpath, Parser parser) {
/* 188 */     XPath p = new XPath(parser, xpath);
/* 189 */     return p.evaluate(tree);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<ParseTree> evaluate(ParseTree t) {
/* 198 */     ParserRuleContext dummyRoot = new ParserRuleContext();
/* 199 */     dummyRoot.children = Collections.singletonList(t);
/*     */     
/* 201 */     Collection<ParseTree> work = Collections.singleton(dummyRoot);
/*     */     
/* 203 */     int i = 0;
/* 204 */     while (i < this.elements.length) {
/* 205 */       Collection<ParseTree> next = new LinkedHashSet<ParseTree>();
/* 206 */       for (ParseTree node : work) {
/* 207 */         if (node.getChildCount() > 0) {
/*     */ 
/*     */ 
/*     */           
/* 211 */           Collection<? extends ParseTree> matching = this.elements[i].evaluate(node);
/* 212 */           next.addAll(matching);
/*     */         } 
/*     */       } 
/* 215 */       i++;
/* 216 */       work = next;
/*     */     } 
/*     */     
/* 219 */     return work;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\xpath\XPath.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */