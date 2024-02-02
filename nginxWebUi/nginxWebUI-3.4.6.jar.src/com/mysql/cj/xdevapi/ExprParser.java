/*      */ package com.mysql.cj.xdevapi;
/*      */ 
/*      */ import com.mysql.cj.exceptions.WrongArgumentException;
/*      */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*      */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.function.Supplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ExprParser
/*      */ {
/*      */   String string;
/*   86 */   List<Token> tokens = new ArrayList<>();
/*      */   
/*   88 */   int tokenPos = 0;
/*      */ 
/*      */ 
/*      */   
/*   92 */   Map<String, Integer> placeholderNameToPosition = new HashMap<>();
/*      */   
/*   94 */   int positionalPlaceholderCount = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean allowRelationalColumns;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExprParser(String s) {
/*  106 */     this(s, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExprParser(String s, boolean allowRelationalColumns) {
/*  118 */     this.string = s;
/*  119 */     lex();
/*      */     
/*  121 */     this.allowRelationalColumns = allowRelationalColumns;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private enum TokenType
/*      */   {
/*  128 */     NOT, AND, ANDAND, OR, OROR, XOR, IS, LPAREN, RPAREN, LSQBRACKET, RSQBRACKET, BETWEEN, TRUE, NULL, FALSE, IN, LIKE, INTERVAL, REGEXP, ESCAPE, IDENT,
/*  129 */     LSTRING, LNUM_INT, LNUM_DOUBLE, DOT, DOLLAR, COMMA, EQ, NE, GT, GE, LT, LE, BITAND, BITOR, BITXOR, LSHIFT, RSHIFT, PLUS, MINUS, STAR, SLASH, HEX, BIN,
/*  130 */     NEG, BANG, EROTEME, MICROSECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, QUARTER, YEAR, SECOND_MICROSECOND, MINUTE_MICROSECOND, MINUTE_SECOND,
/*  131 */     HOUR_MICROSECOND, HOUR_SECOND, HOUR_MINUTE, DAY_MICROSECOND, DAY_SECOND, DAY_MINUTE, DAY_HOUR, YEAR_MONTH, DOUBLESTAR, MOD, COLON, ORDERBY_ASC,
/*  132 */     ORDERBY_DESC, AS, LCURLY, RCURLY, DOTSTAR, CAST, DECIMAL, UNSIGNED, SIGNED, INTEGER, DATE, TIME, DATETIME, CHAR, BINARY, JSON, COLDOCPATH, OVERLAPS;
/*      */   }
/*      */ 
/*      */   
/*      */   static class Token
/*      */   {
/*      */     ExprParser.TokenType type;
/*      */     
/*      */     String value;
/*      */     
/*      */     public Token(ExprParser.TokenType x, char c) {
/*  143 */       this.type = x;
/*  144 */       this.value = new String(new char[] { c });
/*      */     }
/*      */     
/*      */     public Token(ExprParser.TokenType t, String v) {
/*  148 */       this.type = t;
/*  149 */       this.value = v;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  154 */       if (this.type == ExprParser.TokenType.IDENT || this.type == ExprParser.TokenType.LNUM_INT || this.type == ExprParser.TokenType.LNUM_DOUBLE || this.type == ExprParser.TokenType.LSTRING) {
/*  155 */         return this.type.toString() + "(" + this.value + ")";
/*      */       }
/*  157 */       return this.type.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*  162 */   static Map<String, TokenType> reservedWords = new HashMap<>();
/*      */   
/*      */   static {
/*  165 */     reservedWords.put("and", TokenType.AND);
/*  166 */     reservedWords.put("or", TokenType.OR);
/*  167 */     reservedWords.put("xor", TokenType.XOR);
/*  168 */     reservedWords.put("is", TokenType.IS);
/*  169 */     reservedWords.put("not", TokenType.NOT);
/*  170 */     reservedWords.put("like", TokenType.LIKE);
/*  171 */     reservedWords.put("in", TokenType.IN);
/*  172 */     reservedWords.put("regexp", TokenType.REGEXP);
/*  173 */     reservedWords.put("between", TokenType.BETWEEN);
/*  174 */     reservedWords.put("interval", TokenType.INTERVAL);
/*  175 */     reservedWords.put("escape", TokenType.ESCAPE);
/*  176 */     reservedWords.put("div", TokenType.SLASH);
/*  177 */     reservedWords.put("hex", TokenType.HEX);
/*  178 */     reservedWords.put("bin", TokenType.BIN);
/*  179 */     reservedWords.put("true", TokenType.TRUE);
/*  180 */     reservedWords.put("false", TokenType.FALSE);
/*  181 */     reservedWords.put("null", TokenType.NULL);
/*  182 */     reservedWords.put("microsecond", TokenType.MICROSECOND);
/*  183 */     reservedWords.put("second", TokenType.SECOND);
/*  184 */     reservedWords.put("minute", TokenType.MINUTE);
/*  185 */     reservedWords.put("hour", TokenType.HOUR);
/*  186 */     reservedWords.put("day", TokenType.DAY);
/*  187 */     reservedWords.put("week", TokenType.WEEK);
/*  188 */     reservedWords.put("month", TokenType.MONTH);
/*  189 */     reservedWords.put("quarter", TokenType.QUARTER);
/*  190 */     reservedWords.put("year", TokenType.YEAR);
/*  191 */     reservedWords.put("second_microsecond", TokenType.SECOND_MICROSECOND);
/*  192 */     reservedWords.put("minute_microsecond", TokenType.MINUTE_MICROSECOND);
/*  193 */     reservedWords.put("minute_second", TokenType.MINUTE_SECOND);
/*  194 */     reservedWords.put("hour_microsecond", TokenType.HOUR_MICROSECOND);
/*  195 */     reservedWords.put("hour_second", TokenType.HOUR_SECOND);
/*  196 */     reservedWords.put("hour_minute", TokenType.HOUR_MINUTE);
/*  197 */     reservedWords.put("day_microsecond", TokenType.DAY_MICROSECOND);
/*  198 */     reservedWords.put("day_second", TokenType.DAY_SECOND);
/*  199 */     reservedWords.put("day_minute", TokenType.DAY_MINUTE);
/*  200 */     reservedWords.put("day_hour", TokenType.DAY_HOUR);
/*  201 */     reservedWords.put("year_month", TokenType.YEAR_MONTH);
/*  202 */     reservedWords.put("asc", TokenType.ORDERBY_ASC);
/*  203 */     reservedWords.put("desc", TokenType.ORDERBY_DESC);
/*  204 */     reservedWords.put("as", TokenType.AS);
/*  205 */     reservedWords.put("cast", TokenType.CAST);
/*  206 */     reservedWords.put("decimal", TokenType.DECIMAL);
/*  207 */     reservedWords.put("unsigned", TokenType.UNSIGNED);
/*  208 */     reservedWords.put("signed", TokenType.SIGNED);
/*  209 */     reservedWords.put("integer", TokenType.INTEGER);
/*  210 */     reservedWords.put("date", TokenType.DATE);
/*  211 */     reservedWords.put("time", TokenType.TIME);
/*  212 */     reservedWords.put("datetime", TokenType.DATETIME);
/*  213 */     reservedWords.put("char", TokenType.CHAR);
/*  214 */     reservedWords.put("binary", TokenType.BINARY);
/*  215 */     reservedWords.put("json", TokenType.BINARY);
/*  216 */     reservedWords.put("overlaps", TokenType.OVERLAPS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean nextCharEquals(int i, char c) {
/*  229 */     return (i + 1 < this.string.length() && this.string.charAt(i + 1) == c);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int lexNumber(int i) {
/*  241 */     boolean isInt = true;
/*      */     
/*  243 */     int start = i;
/*  244 */     for (; i < this.string.length(); i++) {
/*  245 */       char c = this.string.charAt(i);
/*  246 */       if (c == '.') {
/*  247 */         isInt = false;
/*  248 */       } else if (c == 'e' || c == 'E') {
/*  249 */         isInt = false;
/*  250 */         if (nextCharEquals(i, '-') || nextCharEquals(i, '+')) {
/*  251 */           i++;
/*      */         }
/*  253 */       } else if (!Character.isDigit(c)) {
/*      */         break;
/*      */       } 
/*      */     } 
/*  257 */     if (isInt) {
/*  258 */       this.tokens.add(new Token(TokenType.LNUM_INT, this.string.substring(start, i)));
/*      */     } else {
/*  260 */       this.tokens.add(new Token(TokenType.LNUM_DOUBLE, this.string.substring(start, i)));
/*      */     } 
/*  262 */     i--;
/*  263 */     return i;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void lex() {
/*  270 */     for (int i = 0; i < this.string.length(); i++) {
/*  271 */       int start = i;
/*  272 */       char c = this.string.charAt(i);
/*  273 */       if (!Character.isWhitespace(c))
/*      */       {
/*  275 */         if (Character.isDigit(c)) {
/*  276 */           i = lexNumber(i);
/*  277 */         } else if (c != '_' && !Character.isUnicodeIdentifierStart(c)) {
/*      */           char quoteChar; StringBuilder val;
/*  279 */           switch (c) {
/*      */             case ':':
/*  281 */               this.tokens.add(new Token(TokenType.COLON, c));
/*      */               break;
/*      */             case '+':
/*  284 */               this.tokens.add(new Token(TokenType.PLUS, c));
/*      */               break;
/*      */             case '-':
/*  287 */               if (nextCharEquals(i, '>')) {
/*  288 */                 i++;
/*  289 */                 this.tokens.add(new Token(TokenType.COLDOCPATH, "->")); break;
/*      */               } 
/*  291 */               this.tokens.add(new Token(TokenType.MINUS, c));
/*      */               break;
/*      */             
/*      */             case '*':
/*  295 */               if (nextCharEquals(i, '*')) {
/*  296 */                 i++;
/*  297 */                 this.tokens.add(new Token(TokenType.DOUBLESTAR, "**")); break;
/*      */               } 
/*  299 */               this.tokens.add(new Token(TokenType.STAR, c));
/*      */               break;
/*      */             
/*      */             case '/':
/*  303 */               this.tokens.add(new Token(TokenType.SLASH, c));
/*      */               break;
/*      */             case '$':
/*  306 */               this.tokens.add(new Token(TokenType.DOLLAR, c));
/*      */               break;
/*      */             case '%':
/*  309 */               this.tokens.add(new Token(TokenType.MOD, c));
/*      */               break;
/*      */             case '=':
/*  312 */               if (nextCharEquals(i, '=')) {
/*  313 */                 i++;
/*      */               }
/*  315 */               this.tokens.add(new Token(TokenType.EQ, "=="));
/*      */               break;
/*      */             case '&':
/*  318 */               if (nextCharEquals(i, '&')) {
/*  319 */                 i++;
/*  320 */                 this.tokens.add(new Token(TokenType.ANDAND, "&&")); break;
/*      */               } 
/*  322 */               this.tokens.add(new Token(TokenType.BITAND, c));
/*      */               break;
/*      */             
/*      */             case '|':
/*  326 */               if (nextCharEquals(i, '|')) {
/*  327 */                 i++;
/*  328 */                 this.tokens.add(new Token(TokenType.OROR, "||")); break;
/*      */               } 
/*  330 */               this.tokens.add(new Token(TokenType.BITOR, c));
/*      */               break;
/*      */             
/*      */             case '^':
/*  334 */               this.tokens.add(new Token(TokenType.BITXOR, c));
/*      */               break;
/*      */             case '(':
/*  337 */               this.tokens.add(new Token(TokenType.LPAREN, c));
/*      */               break;
/*      */             case ')':
/*  340 */               this.tokens.add(new Token(TokenType.RPAREN, c));
/*      */               break;
/*      */             case '[':
/*  343 */               this.tokens.add(new Token(TokenType.LSQBRACKET, c));
/*      */               break;
/*      */             case ']':
/*  346 */               this.tokens.add(new Token(TokenType.RSQBRACKET, c));
/*      */               break;
/*      */             case '{':
/*  349 */               this.tokens.add(new Token(TokenType.LCURLY, c));
/*      */               break;
/*      */             case '}':
/*  352 */               this.tokens.add(new Token(TokenType.RCURLY, c));
/*      */               break;
/*      */             case '~':
/*  355 */               this.tokens.add(new Token(TokenType.NEG, c));
/*      */               break;
/*      */             case ',':
/*  358 */               this.tokens.add(new Token(TokenType.COMMA, c));
/*      */               break;
/*      */             case '!':
/*  361 */               if (nextCharEquals(i, '=')) {
/*  362 */                 i++;
/*  363 */                 this.tokens.add(new Token(TokenType.NE, "!=")); break;
/*      */               } 
/*  365 */               this.tokens.add(new Token(TokenType.BANG, c));
/*      */               break;
/*      */             
/*      */             case '?':
/*  369 */               this.tokens.add(new Token(TokenType.EROTEME, c));
/*      */               break;
/*      */             case '<':
/*  372 */               if (nextCharEquals(i, '<')) {
/*  373 */                 i++;
/*  374 */                 this.tokens.add(new Token(TokenType.LSHIFT, "<<")); break;
/*  375 */               }  if (nextCharEquals(i, '=')) {
/*  376 */                 i++;
/*  377 */                 this.tokens.add(new Token(TokenType.LE, "<=")); break;
/*      */               } 
/*  379 */               this.tokens.add(new Token(TokenType.LT, c));
/*      */               break;
/*      */             
/*      */             case '>':
/*  383 */               if (nextCharEquals(i, '>')) {
/*  384 */                 i++;
/*  385 */                 this.tokens.add(new Token(TokenType.RSHIFT, ">>")); break;
/*  386 */               }  if (nextCharEquals(i, '=')) {
/*  387 */                 i++;
/*  388 */                 this.tokens.add(new Token(TokenType.GE, ">=")); break;
/*      */               } 
/*  390 */               this.tokens.add(new Token(TokenType.GT, c));
/*      */               break;
/*      */             
/*      */             case '.':
/*  394 */               if (nextCharEquals(i, '*')) {
/*  395 */                 i++;
/*  396 */                 this.tokens.add(new Token(TokenType.DOTSTAR, ".*")); break;
/*  397 */               }  if (i + 1 < this.string.length() && Character.isDigit(this.string.charAt(i + 1))) {
/*  398 */                 i = lexNumber(i); break;
/*      */               } 
/*  400 */               this.tokens.add(new Token(TokenType.DOT, c));
/*      */               break;
/*      */             
/*      */             case '"':
/*      */             case '\'':
/*      */             case '`':
/*  406 */               quoteChar = c;
/*  407 */               val = new StringBuilder();
/*      */               try {
/*  409 */                 c = this.string.charAt(++i);
/*  410 */                 for (; c != quoteChar || (i + 1 < this.string.length() && this.string.charAt(i + 1) == quoteChar); c = this.string.charAt(++i)) {
/*  411 */                   if (c == '\\' || c == quoteChar) {
/*  412 */                     i++;
/*      */                   }
/*  414 */                   val.append(this.string.charAt(i));
/*      */                 } 
/*  416 */               } catch (StringIndexOutOfBoundsException ex) {
/*  417 */                 throw new WrongArgumentException("Unterminated string starting at " + start);
/*      */               } 
/*  419 */               this.tokens.add(new Token((quoteChar == '`') ? TokenType.IDENT : TokenType.LSTRING, val.toString()));
/*      */               break;
/*      */             default:
/*  422 */               throw new WrongArgumentException("Can't parse at pos: " + i);
/*      */           } 
/*      */         
/*      */         } else {
/*  426 */           for (; i < this.string.length() && Character.isUnicodeIdentifierPart(this.string.charAt(i)); i++);
/*      */           
/*  428 */           String val = this.string.substring(start, i);
/*  429 */           String valLower = val.toLowerCase();
/*  430 */           if (i < this.string.length())
/*      */           {
/*  432 */             i--;
/*      */           }
/*  434 */           if (reservedWords.containsKey(valLower)) {
/*      */             
/*  436 */             if ("and".equals(valLower)) {
/*  437 */               this.tokens.add(new Token(reservedWords.get(valLower), "&&"));
/*  438 */             } else if ("or".equals(valLower)) {
/*  439 */               this.tokens.add(new Token(reservedWords.get(valLower), "||"));
/*      */             } else {
/*      */               
/*  442 */               this.tokens.add(new Token(reservedWords.get(valLower), valLower));
/*      */             } 
/*      */           } else {
/*  445 */             this.tokens.add(new Token(TokenType.IDENT, val));
/*      */           } 
/*      */         } 
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void assertTokenAt(int pos, TokenType type) {
/*  460 */     if (this.tokens.size() <= pos) {
/*  461 */       throw new WrongArgumentException("No more tokens when expecting " + type + " at token pos " + pos);
/*      */     }
/*  463 */     if (((Token)this.tokens.get(pos)).type != type) {
/*  464 */       throw new WrongArgumentException("Expected token type " + type + " at token pos " + pos);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean currentTokenTypeEquals(TokenType t) {
/*  476 */     return posTokenTypeEquals(this.tokenPos, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean nextTokenTypeEquals(TokenType t) {
/*  487 */     return posTokenTypeEquals(this.tokenPos + 1, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   boolean posTokenTypeEquals(int pos, TokenType t) {
/*  500 */     return (this.tokens.size() > pos && ((Token)this.tokens.get(pos)).type == t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   String consumeToken(TokenType t) {
/*  511 */     assertTokenAt(this.tokenPos, t);
/*  512 */     String value = ((Token)this.tokens.get(this.tokenPos)).value;
/*  513 */     this.tokenPos++;
/*  514 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   List<MysqlxExpr.Expr> parenExprList() {
/*  523 */     List<MysqlxExpr.Expr> exprs = new ArrayList<>();
/*  524 */     consumeToken(TokenType.LPAREN);
/*  525 */     if (!currentTokenTypeEquals(TokenType.RPAREN)) {
/*  526 */       exprs.add(expr());
/*  527 */       while (currentTokenTypeEquals(TokenType.COMMA)) {
/*  528 */         consumeToken(TokenType.COMMA);
/*  529 */         exprs.add(expr());
/*      */       } 
/*      */     } 
/*  532 */     consumeToken(TokenType.RPAREN);
/*  533 */     return exprs;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.Expr functionCall() {
/*  542 */     MysqlxExpr.Identifier id = identifier();
/*  543 */     MysqlxExpr.FunctionCall.Builder b = MysqlxExpr.FunctionCall.newBuilder();
/*  544 */     b.setName(id);
/*  545 */     b.addAllParam(parenExprList());
/*  546 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.FUNC_CALL).setFunctionCall(b.build()).build();
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr starOperator() {
/*  550 */     MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName("*").build();
/*  551 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.Identifier identifier() {
/*  560 */     MysqlxExpr.Identifier.Builder builder = MysqlxExpr.Identifier.newBuilder();
/*  561 */     assertTokenAt(this.tokenPos, TokenType.IDENT);
/*  562 */     if (nextTokenTypeEquals(TokenType.DOT)) {
/*  563 */       builder.setSchemaName(((Token)this.tokens.get(this.tokenPos)).value);
/*  564 */       consumeToken(TokenType.IDENT);
/*  565 */       consumeToken(TokenType.DOT);
/*  566 */       assertTokenAt(this.tokenPos, TokenType.IDENT);
/*      */     } 
/*  568 */     builder.setName(((Token)this.tokens.get(this.tokenPos)).value);
/*  569 */     consumeToken(TokenType.IDENT);
/*  570 */     return builder.build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.DocumentPathItem docPathMember() {
/*      */     String memberName;
/*  579 */     consumeToken(TokenType.DOT);
/*  580 */     Token t = this.tokens.get(this.tokenPos);
/*      */     
/*  582 */     if (currentTokenTypeEquals(TokenType.IDENT)) {
/*      */       
/*  584 */       if (!t.value.equals(ExprUnparser.quoteIdentifier(t.value))) {
/*  585 */         throw new WrongArgumentException("'" + t.value + "' is not a valid JSON/ECMAScript identifier");
/*      */       }
/*  587 */       consumeToken(TokenType.IDENT);
/*  588 */       memberName = t.value;
/*  589 */     } else if (currentTokenTypeEquals(TokenType.LSTRING)) {
/*  590 */       consumeToken(TokenType.LSTRING);
/*  591 */       memberName = t.value;
/*      */     } else {
/*  593 */       throw new WrongArgumentException("Expected token type IDENT or LSTRING in JSON path at token pos " + this.tokenPos);
/*      */     } 
/*  595 */     MysqlxExpr.DocumentPathItem.Builder item = MysqlxExpr.DocumentPathItem.newBuilder();
/*  596 */     item.setType(MysqlxExpr.DocumentPathItem.Type.MEMBER);
/*  597 */     item.setValue(memberName);
/*  598 */     return item.build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.DocumentPathItem docPathArrayLoc() {
/*  607 */     MysqlxExpr.DocumentPathItem.Builder builder = MysqlxExpr.DocumentPathItem.newBuilder();
/*  608 */     consumeToken(TokenType.LSQBRACKET);
/*  609 */     if (currentTokenTypeEquals(TokenType.STAR)) {
/*  610 */       consumeToken(TokenType.STAR);
/*  611 */       consumeToken(TokenType.RSQBRACKET);
/*  612 */       return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX_ASTERISK).build();
/*  613 */     }  if (currentTokenTypeEquals(TokenType.LNUM_INT)) {
/*  614 */       Integer v = Integer.valueOf(((Token)this.tokens.get(this.tokenPos)).value);
/*  615 */       if (v.intValue() < 0) {
/*  616 */         throw new WrongArgumentException("Array index cannot be negative at " + this.tokenPos);
/*      */       }
/*  618 */       consumeToken(TokenType.LNUM_INT);
/*  619 */       consumeToken(TokenType.RSQBRACKET);
/*  620 */       return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX).setIndex(v.intValue()).build();
/*      */     } 
/*  622 */     throw new WrongArgumentException("Expected token type STAR or LNUM_INT in JSON path array index at token pos " + this.tokenPos);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MysqlxExpr.DocumentPathItem> documentPath() {
/*  632 */     List<MysqlxExpr.DocumentPathItem> items = new ArrayList<>();
/*      */     while (true) {
/*  634 */       while (currentTokenTypeEquals(TokenType.DOT))
/*  635 */         items.add(docPathMember()); 
/*  636 */       if (currentTokenTypeEquals(TokenType.DOTSTAR)) {
/*  637 */         consumeToken(TokenType.DOTSTAR);
/*  638 */         items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER_ASTERISK).build()); continue;
/*  639 */       }  if (currentTokenTypeEquals(TokenType.LSQBRACKET)) {
/*  640 */         items.add(docPathArrayLoc()); continue;
/*  641 */       }  if (currentTokenTypeEquals(TokenType.DOUBLESTAR)) {
/*  642 */         consumeToken(TokenType.DOUBLESTAR);
/*  643 */         items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK).build());
/*      */         continue;
/*      */       } 
/*      */       break;
/*      */     } 
/*  648 */     if (items.size() > 0 && ((MysqlxExpr.DocumentPathItem)items.get(items.size() - 1)).getType() == MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK) {
/*  649 */       throw new WrongArgumentException("JSON path may not end in '**' at " + this.tokenPos);
/*      */     }
/*  651 */     return items;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MysqlxExpr.Expr documentField() {
/*  660 */     MysqlxExpr.ColumnIdentifier.Builder builder = MysqlxExpr.ColumnIdentifier.newBuilder();
/*  661 */     if (currentTokenTypeEquals(TokenType.IDENT)) {
/*  662 */       builder.addDocumentPath(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER).setValue(consumeToken(TokenType.IDENT)).build());
/*      */     }
/*  664 */     builder.addAllDocumentPath(documentPath());
/*  665 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(builder.build()).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.Expr columnIdentifier() {
/*  674 */     List<String> parts = new LinkedList<>();
/*  675 */     parts.add(consumeToken(TokenType.IDENT));
/*  676 */     while (currentTokenTypeEquals(TokenType.DOT)) {
/*  677 */       consumeToken(TokenType.DOT);
/*  678 */       parts.add(consumeToken(TokenType.IDENT));
/*      */       
/*  680 */       if (parts.size() == 3) {
/*      */         break;
/*      */       }
/*      */     } 
/*  684 */     Collections.reverse(parts);
/*  685 */     MysqlxExpr.ColumnIdentifier.Builder id = MysqlxExpr.ColumnIdentifier.newBuilder();
/*  686 */     for (int i = 0; i < parts.size(); i++) {
/*  687 */       switch (i) {
/*      */         case 0:
/*  689 */           id.setName(parts.get(0));
/*      */           break;
/*      */         case 1:
/*  692 */           id.setTableName(parts.get(1));
/*      */           break;
/*      */         case 2:
/*  695 */           id.setSchemaName(parts.get(2));
/*      */           break;
/*      */       } 
/*      */     } 
/*  699 */     if (currentTokenTypeEquals(TokenType.COLDOCPATH)) {
/*  700 */       consumeToken(TokenType.COLDOCPATH);
/*  701 */       if (currentTokenTypeEquals(TokenType.DOLLAR)) {
/*  702 */         consumeToken(TokenType.DOLLAR);
/*  703 */         id.addAllDocumentPath(documentPath());
/*  704 */       } else if (currentTokenTypeEquals(TokenType.LSTRING)) {
/*  705 */         String path = consumeToken(TokenType.LSTRING);
/*  706 */         if (path.charAt(0) != '$') {
/*  707 */           throw new WrongArgumentException("Invalid document path at " + this.tokenPos);
/*      */         }
/*  709 */         id.addAllDocumentPath((new ExprParser(path.substring(1, path.length()))).documentPath());
/*      */       } 
/*  711 */       if (id.getDocumentPathCount() == 0) {
/*  712 */         throw new WrongArgumentException("Invalid document path at " + this.tokenPos);
/*      */       }
/*      */     } 
/*  715 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(id.build()).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.Expr buildUnaryOp(String name, MysqlxExpr.Expr param) {
/*  728 */     String opName = "-".equals(name) ? "sign_minus" : ("+".equals(name) ? "sign_plus" : name);
/*  729 */     MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName(opName).addParam(param).build();
/*  730 */     return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
/*      */   } MysqlxExpr.Expr atomicExpr() {
/*      */     String placeholderName;
/*      */     MysqlxExpr.Expr e;
/*      */     MysqlxExpr.Object.Builder builder2;
/*      */     MysqlxExpr.Array.Builder builder1;
/*      */     MysqlxExpr.Operator.Builder builder;
/*      */     MysqlxExpr.Expr.Builder placeholder;
/*      */     StringBuilder typeStr;
/*  739 */     if (this.tokenPos >= this.tokens.size()) {
/*  740 */       throw new WrongArgumentException("No more tokens when expecting one at token pos " + this.tokenPos);
/*      */     }
/*  742 */     Token t = this.tokens.get(this.tokenPos);
/*  743 */     this.tokenPos++;
/*  744 */     switch (t.type) {
/*      */       
/*      */       case EROTEME:
/*      */       case COLON:
/*  748 */         if (currentTokenTypeEquals(TokenType.LNUM_INT)) {
/*      */ 
/*      */           
/*  751 */           placeholderName = consumeToken(TokenType.LNUM_INT);
/*  752 */         } else if (currentTokenTypeEquals(TokenType.IDENT)) {
/*  753 */           placeholderName = consumeToken(TokenType.IDENT);
/*  754 */         } else if (t.type == TokenType.EROTEME) {
/*  755 */           placeholderName = String.valueOf(this.positionalPlaceholderCount);
/*      */         } else {
/*  757 */           throw new WrongArgumentException("Invalid placeholder name at token pos " + this.tokenPos);
/*      */         } 
/*  759 */         placeholder = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.PLACEHOLDER);
/*  760 */         if (this.placeholderNameToPosition.containsKey(placeholderName)) {
/*  761 */           placeholder.setPosition(((Integer)this.placeholderNameToPosition.get(placeholderName)).intValue());
/*      */         } else {
/*  763 */           placeholder.setPosition(this.positionalPlaceholderCount);
/*  764 */           this.placeholderNameToPosition.put(placeholderName, Integer.valueOf(this.positionalPlaceholderCount));
/*  765 */           this.positionalPlaceholderCount++;
/*      */         } 
/*  767 */         return placeholder.build();
/*      */       
/*      */       case LPAREN:
/*  770 */         e = expr();
/*  771 */         consumeToken(TokenType.RPAREN);
/*  772 */         return e;
/*      */       
/*      */       case LCURLY:
/*  775 */         builder2 = MysqlxExpr.Object.newBuilder();
/*  776 */         if (currentTokenTypeEquals(TokenType.LSTRING)) {
/*  777 */           parseCommaSeparatedList(() -> {
/*      */                 String key = consumeToken(TokenType.LSTRING);
/*      */                 consumeToken(TokenType.COLON);
/*      */                 MysqlxExpr.Expr value = expr();
/*      */                 return Collections.singletonMap(key, value);
/*  782 */               }).stream().map(pair -> (Map.Entry)pair.entrySet().iterator().next()).map(e -> MysqlxExpr.Object.ObjectField.newBuilder().setKey((String)e.getKey()).setValue((MysqlxExpr.Expr)e.getValue()))
/*  783 */             .forEach(builder2::addFld);
/*      */         }
/*  785 */         consumeToken(TokenType.RCURLY);
/*  786 */         return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OBJECT).setObject(builder2.build()).build();
/*      */       
/*      */       case LSQBRACKET:
/*  789 */         builder1 = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).getArrayBuilder();
/*  790 */         if (!currentTokenTypeEquals(TokenType.RSQBRACKET)) {
/*  791 */           parseCommaSeparatedList(() -> expr())
/*      */             
/*  793 */             .stream().forEach(builder1::addValue);
/*      */         }
/*  795 */         consumeToken(TokenType.RSQBRACKET);
/*  796 */         return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).setArray(builder1).build();
/*      */       
/*      */       case CAST:
/*  799 */         consumeToken(TokenType.LPAREN);
/*  800 */         builder = MysqlxExpr.Operator.newBuilder().setName(TokenType.CAST.toString().toLowerCase());
/*  801 */         builder.addParam(expr());
/*  802 */         consumeToken(TokenType.AS);
/*  803 */         typeStr = new StringBuilder(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase());
/*      */         
/*  805 */         if (currentTokenTypeEquals(TokenType.DECIMAL)) {
/*  806 */           this.tokenPos++;
/*  807 */           if (currentTokenTypeEquals(TokenType.LPAREN)) {
/*  808 */             typeStr.append(consumeToken(TokenType.LPAREN));
/*  809 */             typeStr.append(consumeToken(TokenType.LNUM_INT));
/*  810 */             if (currentTokenTypeEquals(TokenType.COMMA)) {
/*  811 */               typeStr.append(consumeToken(TokenType.COMMA));
/*  812 */               typeStr.append(consumeToken(TokenType.LNUM_INT));
/*      */             } 
/*  814 */             typeStr.append(consumeToken(TokenType.RPAREN));
/*      */           } 
/*  816 */         } else if (currentTokenTypeEquals(TokenType.CHAR) || currentTokenTypeEquals(TokenType.BINARY)) {
/*  817 */           this.tokenPos++;
/*  818 */           if (currentTokenTypeEquals(TokenType.LPAREN)) {
/*  819 */             typeStr.append(consumeToken(TokenType.LPAREN));
/*  820 */             typeStr.append(consumeToken(TokenType.LNUM_INT));
/*  821 */             typeStr.append(consumeToken(TokenType.RPAREN));
/*      */           } 
/*  823 */         } else if (currentTokenTypeEquals(TokenType.UNSIGNED) || currentTokenTypeEquals(TokenType.SIGNED)) {
/*  824 */           this.tokenPos++;
/*  825 */           if (currentTokenTypeEquals(TokenType.INTEGER))
/*      */           {
/*  827 */             consumeToken(TokenType.INTEGER);
/*      */           }
/*  829 */         } else if (currentTokenTypeEquals(TokenType.JSON) || currentTokenTypeEquals(TokenType.DATE) || currentTokenTypeEquals(TokenType.DATETIME) || 
/*  830 */           currentTokenTypeEquals(TokenType.TIME)) {
/*  831 */           this.tokenPos++;
/*      */         } else {
/*  833 */           throw new WrongArgumentException("Expected valid CAST type argument at " + this.tokenPos);
/*      */         } 
/*  835 */         consumeToken(TokenType.RPAREN);
/*      */         
/*  837 */         builder.addParam(ExprUtil.buildLiteralScalar(typeStr.toString().getBytes()));
/*  838 */         return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
/*      */       
/*      */       case PLUS:
/*      */       case MINUS:
/*  842 */         if (currentTokenTypeEquals(TokenType.LNUM_INT) || currentTokenTypeEquals(TokenType.LNUM_DOUBLE)) {
/*      */           
/*  844 */           t.value += ((Token)this.tokens.get(this.tokenPos)).value;
/*  845 */           return atomicExpr();
/*      */         } 
/*  847 */         return buildUnaryOp(t.value, atomicExpr());
/*      */       
/*      */       case NOT:
/*      */       case NEG:
/*      */       case BANG:
/*  852 */         return buildUnaryOp(t.value, atomicExpr());
/*      */       case LSTRING:
/*  854 */         return ExprUtil.buildLiteralScalar(t.value);
/*      */       case NULL:
/*  856 */         return ExprUtil.buildLiteralNullScalar();
/*      */       case LNUM_INT:
/*  858 */         return ExprUtil.buildLiteralScalar(Long.valueOf(t.value).longValue());
/*      */       case LNUM_DOUBLE:
/*  860 */         return ExprUtil.buildLiteralScalar(Double.valueOf(t.value).doubleValue());
/*      */       case TRUE:
/*      */       case FALSE:
/*  863 */         return ExprUtil.buildLiteralScalar((t.type == TokenType.TRUE));
/*      */       case DOLLAR:
/*  865 */         return documentField();
/*      */       
/*      */       case STAR:
/*  868 */         return starOperator();
/*      */       case IDENT:
/*  870 */         this.tokenPos--;
/*      */         
/*  872 */         if (nextTokenTypeEquals(TokenType.LPAREN) || (posTokenTypeEquals(this.tokenPos + 1, TokenType.DOT) && 
/*  873 */           posTokenTypeEquals(this.tokenPos + 2, TokenType.IDENT) && posTokenTypeEquals(this.tokenPos + 3, TokenType.LPAREN))) {
/*  874 */           return functionCall();
/*      */         }
/*  876 */         if (this.allowRelationalColumns) {
/*  877 */           return columnIdentifier();
/*      */         }
/*  879 */         return documentField();
/*      */     } 
/*      */ 
/*      */     
/*  883 */     throw new WrongArgumentException("Cannot find atomic expression at token pos: " + (this.tokenPos - 1));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @FunctionalInterface
/*      */   static interface ParseExpr
/*      */   {
/*      */     MysqlxExpr.Expr parseExpr();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   MysqlxExpr.Expr parseLeftAssocBinaryOpExpr(TokenType[] types, ParseExpr innerParser) {
/*  904 */     MysqlxExpr.Expr lhs = innerParser.parseExpr();
/*  905 */     while (this.tokenPos < this.tokens.size() && Arrays.<TokenType>asList(types).contains(((Token)this.tokens.get(this.tokenPos)).type)) {
/*  906 */       MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(((Token)this.tokens.get(this.tokenPos)).value).addParam(lhs);
/*  907 */       this.tokenPos++;
/*  908 */       builder.addParam(innerParser.parseExpr());
/*  909 */       lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
/*      */     } 
/*  911 */     return lhs;
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr addSubIntervalExpr() {
/*  915 */     MysqlxExpr.Expr lhs = atomicExpr();
/*  916 */     while ((currentTokenTypeEquals(TokenType.PLUS) || currentTokenTypeEquals(TokenType.MINUS)) && nextTokenTypeEquals(TokenType.INTERVAL)) {
/*  917 */       Token op = this.tokens.get(this.tokenPos);
/*  918 */       this.tokenPos++;
/*  919 */       MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().addParam(lhs);
/*      */ 
/*      */       
/*  922 */       consumeToken(TokenType.INTERVAL);
/*      */       
/*  924 */       if (op.type == TokenType.PLUS) {
/*  925 */         builder.setName("date_add");
/*      */       } else {
/*  927 */         builder.setName("date_sub");
/*      */       } 
/*      */       
/*  930 */       builder.addParam(bitExpr());
/*      */ 
/*      */       
/*  933 */       if (currentTokenTypeEquals(TokenType.MICROSECOND) || currentTokenTypeEquals(TokenType.SECOND) || currentTokenTypeEquals(TokenType.MINUTE) || 
/*  934 */         currentTokenTypeEquals(TokenType.HOUR) || currentTokenTypeEquals(TokenType.DAY) || currentTokenTypeEquals(TokenType.WEEK) || 
/*  935 */         currentTokenTypeEquals(TokenType.MONTH) || currentTokenTypeEquals(TokenType.QUARTER) || currentTokenTypeEquals(TokenType.YEAR) || 
/*  936 */         currentTokenTypeEquals(TokenType.SECOND_MICROSECOND) || currentTokenTypeEquals(TokenType.MINUTE_MICROSECOND) || 
/*  937 */         currentTokenTypeEquals(TokenType.MINUTE_SECOND) || currentTokenTypeEquals(TokenType.HOUR_MICROSECOND) || 
/*  938 */         currentTokenTypeEquals(TokenType.HOUR_SECOND) || currentTokenTypeEquals(TokenType.HOUR_MINUTE) || 
/*  939 */         currentTokenTypeEquals(TokenType.DAY_MICROSECOND) || currentTokenTypeEquals(TokenType.DAY_SECOND) || 
/*  940 */         currentTokenTypeEquals(TokenType.DAY_MINUTE) || currentTokenTypeEquals(TokenType.DAY_HOUR) || 
/*  941 */         currentTokenTypeEquals(TokenType.YEAR_MONTH)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  947 */         builder.addParam(ExprUtil.buildLiteralScalar(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase().getBytes()));
/*  948 */         this.tokenPos++;
/*      */         
/*  950 */         lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build(); continue;
/*      */       }  throw new WrongArgumentException("Expected interval units at " + this.tokenPos);
/*  952 */     }  return lhs;
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr mulDivExpr() {
/*  956 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.STAR, TokenType.SLASH, TokenType.MOD }, this::addSubIntervalExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr addSubExpr() {
/*  960 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.PLUS, TokenType.MINUS }, this::mulDivExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr shiftExpr() {
/*  964 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.LSHIFT, TokenType.RSHIFT }, this::addSubExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr bitExpr() {
/*  968 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.BITAND, TokenType.BITOR, TokenType.BITXOR }, this::shiftExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr compExpr() {
/*  972 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.GE, TokenType.GT, TokenType.LE, TokenType.LT, TokenType.EQ, TokenType.NE }, this::bitExpr);
/*      */   }
/*      */ 
/*      */   
/*      */   MysqlxExpr.Expr ilriExpr() {
/*  977 */     MysqlxExpr.Expr lhs = compExpr();
/*      */     
/*  979 */     List<TokenType> expected = Arrays.asList(new TokenType[] { TokenType.IS, TokenType.IN, TokenType.LIKE, TokenType.BETWEEN, TokenType.REGEXP, TokenType.NOT, TokenType.OVERLAPS });
/*  980 */     while (this.tokenPos < this.tokens.size() && expected.contains(((Token)this.tokens.get(this.tokenPos)).type)) {
/*  981 */       boolean isNot = false;
/*  982 */       if (currentTokenTypeEquals(TokenType.NOT)) {
/*  983 */         consumeToken(TokenType.NOT);
/*  984 */         isNot = true;
/*      */       } 
/*  986 */       if (this.tokenPos < this.tokens.size()) {
/*  987 */         List<MysqlxExpr.Expr> params = new ArrayList<>();
/*  988 */         params.add(lhs);
/*  989 */         String opName = ((Token)this.tokens.get(this.tokenPos)).value.toLowerCase();
/*  990 */         switch (((Token)this.tokens.get(this.tokenPos)).type) {
/*      */           case IS:
/*  992 */             consumeToken(TokenType.IS);
/*  993 */             if (currentTokenTypeEquals(TokenType.NOT)) {
/*  994 */               consumeToken(TokenType.NOT);
/*  995 */               opName = "is_not";
/*      */             } 
/*  997 */             params.add(compExpr());
/*      */             break;
/*      */           case IN:
/* 1000 */             consumeToken(TokenType.IN);
/* 1001 */             if (currentTokenTypeEquals(TokenType.LPAREN)) {
/* 1002 */               params.addAll(parenExprList()); break;
/*      */             } 
/* 1004 */             opName = "cont_in";
/* 1005 */             params.add(compExpr());
/*      */             break;
/*      */           
/*      */           case LIKE:
/* 1009 */             consumeToken(TokenType.LIKE);
/* 1010 */             params.add(compExpr());
/* 1011 */             if (currentTokenTypeEquals(TokenType.ESCAPE)) {
/* 1012 */               consumeToken(TokenType.ESCAPE);
/*      */               
/* 1014 */               params.add(compExpr());
/*      */             } 
/*      */             break;
/*      */           case BETWEEN:
/* 1018 */             consumeToken(TokenType.BETWEEN);
/* 1019 */             params.add(compExpr());
/* 1020 */             assertTokenAt(this.tokenPos, TokenType.AND);
/* 1021 */             consumeToken(TokenType.AND);
/* 1022 */             params.add(compExpr());
/*      */             break;
/*      */           case REGEXP:
/* 1025 */             consumeToken(TokenType.REGEXP);
/* 1026 */             params.add(compExpr());
/*      */             break;
/*      */           case OVERLAPS:
/* 1029 */             consumeToken(TokenType.OVERLAPS);
/* 1030 */             params.add(compExpr());
/*      */             break;
/*      */           default:
/* 1033 */             throw new WrongArgumentException("Unknown token after NOT at pos: " + this.tokenPos);
/*      */         } 
/* 1035 */         if (isNot) {
/* 1036 */           opName = "not_" + opName;
/*      */         }
/* 1038 */         MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(opName).addAllParam(params);
/* 1039 */         lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
/*      */       } 
/*      */     } 
/* 1042 */     return lhs;
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr andExpr() {
/* 1046 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.AND, TokenType.ANDAND }, this::ilriExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr orExpr() {
/* 1050 */     return parseLeftAssocBinaryOpExpr(new TokenType[] { TokenType.OR, TokenType.OROR }, this::andExpr);
/*      */   }
/*      */   
/*      */   MysqlxExpr.Expr expr() {
/* 1054 */     MysqlxExpr.Expr e = orExpr();
/* 1055 */     return e;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MysqlxExpr.Expr parse() {
/*      */     try {
/* 1065 */       MysqlxExpr.Expr e = expr();
/* 1066 */       if (this.tokenPos != this.tokens.size()) {
/* 1067 */         throw new WrongArgumentException("Only " + this.tokenPos + " tokens consumed, out of " + this.tokens.size());
/*      */       }
/* 1069 */       return e;
/* 1070 */     } catch (IllegalArgumentException ex) {
/* 1071 */       throw new WrongArgumentException("Unable to parse query '" + this.string + "'", ex);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private <T> List<T> parseCommaSeparatedList(Supplier<T> elementParser) {
/* 1085 */     List<T> elements = new ArrayList<>();
/* 1086 */     boolean first = true;
/* 1087 */     while (first || currentTokenTypeEquals(TokenType.COMMA)) {
/* 1088 */       if (!first) {
/* 1089 */         consumeToken(TokenType.COMMA);
/*      */       } else {
/* 1091 */         first = false;
/*      */       } 
/* 1093 */       elements.add(elementParser.get());
/*      */     } 
/* 1095 */     return elements;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MysqlxCrud.Order> parseOrderSpec() {
/* 1104 */     return parseCommaSeparatedList(() -> {
/*      */           MysqlxCrud.Order.Builder builder = MysqlxCrud.Order.newBuilder();
/*      */           builder.setExpr(expr());
/*      */           if (currentTokenTypeEquals(TokenType.ORDERBY_ASC)) {
/*      */             consumeToken(TokenType.ORDERBY_ASC);
/*      */             builder.setDirection(MysqlxCrud.Order.Direction.ASC);
/*      */           } else if (currentTokenTypeEquals(TokenType.ORDERBY_DESC)) {
/*      */             consumeToken(TokenType.ORDERBY_DESC);
/*      */             builder.setDirection(MysqlxCrud.Order.Direction.DESC);
/*      */           } 
/*      */           return builder.build();
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MysqlxCrud.Projection> parseTableSelectProjection() {
/* 1124 */     return parseCommaSeparatedList(() -> {
/*      */           MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
/*      */           builder.setSource(expr());
/*      */           if (currentTokenTypeEquals(TokenType.AS)) {
/*      */             consumeToken(TokenType.AS);
/*      */             builder.setAlias(consumeToken(TokenType.IDENT));
/*      */           } 
/*      */           return builder.build();
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MysqlxCrud.Column parseTableInsertField() {
/* 1142 */     return MysqlxCrud.Column.newBuilder().setName(consumeToken(TokenType.IDENT)).build();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public MysqlxExpr.ColumnIdentifier parseTableUpdateField() {
/* 1151 */     return columnIdentifier().getIdentifier();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MysqlxCrud.Projection> parseDocumentProjection() {
/* 1160 */     this.allowRelationalColumns = false;
/* 1161 */     return parseCommaSeparatedList(() -> {
/*      */           MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
/*      */           builder.setSource(expr());
/*      */           consumeToken(TokenType.AS);
/*      */           builder.setAlias(consumeToken(TokenType.IDENT));
/*      */           return builder.build();
/*      */         });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public List<MysqlxExpr.Expr> parseExprList() {
/* 1177 */     return parseCommaSeparatedList(this::expr);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getPositionalPlaceholderCount() {
/* 1186 */     return this.positionalPlaceholderCount;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Map<String, Integer> getPlaceholderNameToPositionMap() {
/* 1195 */     return Collections.unmodifiableMap(this.placeholderNameToPosition);
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ExprParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */