package com.mysql.cj.xdevapi;

import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ExprParser {
   String string;
   List<Token> tokens;
   int tokenPos;
   Map<String, Integer> placeholderNameToPosition;
   int positionalPlaceholderCount;
   private boolean allowRelationalColumns;
   static Map<String, TokenType> reservedWords = new HashMap();

   public ExprParser(String s) {
      this(s, true);
   }

   public ExprParser(String s, boolean allowRelationalColumns) {
      this.tokens = new ArrayList();
      this.tokenPos = 0;
      this.placeholderNameToPosition = new HashMap();
      this.positionalPlaceholderCount = 0;
      this.string = s;
      this.lex();
      this.allowRelationalColumns = allowRelationalColumns;
   }

   boolean nextCharEquals(int i, char c) {
      return i + 1 < this.string.length() && this.string.charAt(i + 1) == c;
   }

   private int lexNumber(int i) {
      boolean isInt = true;

      int start;
      for(start = i; i < this.string.length(); ++i) {
         char c = this.string.charAt(i);
         if (c == '.') {
            isInt = false;
         } else if (c != 'e' && c != 'E') {
            if (!Character.isDigit(c)) {
               break;
            }
         } else {
            isInt = false;
            if (this.nextCharEquals(i, '-') || this.nextCharEquals(i, '+')) {
               ++i;
            }
         }
      }

      if (isInt) {
         this.tokens.add(new Token(ExprParser.TokenType.LNUM_INT, this.string.substring(start, i)));
      } else {
         this.tokens.add(new Token(ExprParser.TokenType.LNUM_DOUBLE, this.string.substring(start, i)));
      }

      --i;
      return i;
   }

   void lex() {
      for(int i = 0; i < this.string.length(); ++i) {
         int start = i;
         char c = this.string.charAt(i);
         if (!Character.isWhitespace(c)) {
            if (Character.isDigit(c)) {
               i = this.lexNumber(i);
            } else if (c != '_' && !Character.isUnicodeIdentifierStart(c)) {
               switch (c) {
                  case '!':
                     if (this.nextCharEquals(i, '=')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.NE, "!="));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.BANG, c));
                     }
                     break;
                  case '"':
                  case '\'':
                  case '`':
                     char quoteChar = c;
                     StringBuilder val = new StringBuilder();

                     try {
                        ++i;

                        for(c = this.string.charAt(i); c != quoteChar || i + 1 < this.string.length() && this.string.charAt(i + 1) == quoteChar; c = this.string.charAt(i)) {
                           if (c == '\\' || c == quoteChar) {
                              ++i;
                           }

                           val.append(this.string.charAt(i));
                           ++i;
                        }
                     } catch (StringIndexOutOfBoundsException var7) {
                        throw new WrongArgumentException("Unterminated string starting at " + i);
                     }

                     this.tokens.add(new Token(quoteChar == '`' ? ExprParser.TokenType.IDENT : ExprParser.TokenType.LSTRING, val.toString()));
                     break;
                  case '#':
                  case '0':
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                  case ';':
                  case '@':
                  case 'A':
                  case 'B':
                  case 'C':
                  case 'D':
                  case 'E':
                  case 'F':
                  case 'G':
                  case 'H':
                  case 'I':
                  case 'J':
                  case 'K':
                  case 'L':
                  case 'M':
                  case 'N':
                  case 'O':
                  case 'P':
                  case 'Q':
                  case 'R':
                  case 'S':
                  case 'T':
                  case 'U':
                  case 'V':
                  case 'W':
                  case 'X':
                  case 'Y':
                  case 'Z':
                  case '\\':
                  case '_':
                  case 'a':
                  case 'b':
                  case 'c':
                  case 'd':
                  case 'e':
                  case 'f':
                  case 'g':
                  case 'h':
                  case 'i':
                  case 'j':
                  case 'k':
                  case 'l':
                  case 'm':
                  case 'n':
                  case 'o':
                  case 'p':
                  case 'q':
                  case 'r':
                  case 's':
                  case 't':
                  case 'u':
                  case 'v':
                  case 'w':
                  case 'x':
                  case 'y':
                  case 'z':
                  default:
                     throw new WrongArgumentException("Can't parse at pos: " + i);
                  case '$':
                     this.tokens.add(new Token(ExprParser.TokenType.DOLLAR, c));
                     break;
                  case '%':
                     this.tokens.add(new Token(ExprParser.TokenType.MOD, c));
                     break;
                  case '&':
                     if (this.nextCharEquals(i, '&')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.ANDAND, "&&"));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.BITAND, c));
                     }
                     break;
                  case '(':
                     this.tokens.add(new Token(ExprParser.TokenType.LPAREN, c));
                     break;
                  case ')':
                     this.tokens.add(new Token(ExprParser.TokenType.RPAREN, c));
                     break;
                  case '*':
                     if (this.nextCharEquals(i, '*')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.DOUBLESTAR, "**"));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.STAR, c));
                     }
                     break;
                  case '+':
                     this.tokens.add(new Token(ExprParser.TokenType.PLUS, c));
                     break;
                  case ',':
                     this.tokens.add(new Token(ExprParser.TokenType.COMMA, c));
                     break;
                  case '-':
                     if (this.nextCharEquals(i, '>')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.COLDOCPATH, "->"));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.MINUS, c));
                     }
                     break;
                  case '.':
                     if (this.nextCharEquals(i, '*')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.DOTSTAR, ".*"));
                     } else if (i + 1 < this.string.length() && Character.isDigit(this.string.charAt(i + 1))) {
                        i = this.lexNumber(i);
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.DOT, c));
                     }
                     break;
                  case '/':
                     this.tokens.add(new Token(ExprParser.TokenType.SLASH, c));
                     break;
                  case ':':
                     this.tokens.add(new Token(ExprParser.TokenType.COLON, c));
                     break;
                  case '<':
                     if (this.nextCharEquals(i, '<')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.LSHIFT, "<<"));
                     } else if (this.nextCharEquals(i, '=')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.LE, "<="));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.LT, c));
                     }
                     break;
                  case '=':
                     if (this.nextCharEquals(i, '=')) {
                        ++i;
                     }

                     this.tokens.add(new Token(ExprParser.TokenType.EQ, "=="));
                     break;
                  case '>':
                     if (this.nextCharEquals(i, '>')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.RSHIFT, ">>"));
                     } else if (this.nextCharEquals(i, '=')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.GE, ">="));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.GT, c));
                     }
                     break;
                  case '?':
                     this.tokens.add(new Token(ExprParser.TokenType.EROTEME, c));
                     break;
                  case '[':
                     this.tokens.add(new Token(ExprParser.TokenType.LSQBRACKET, c));
                     break;
                  case ']':
                     this.tokens.add(new Token(ExprParser.TokenType.RSQBRACKET, c));
                     break;
                  case '^':
                     this.tokens.add(new Token(ExprParser.TokenType.BITXOR, c));
                     break;
                  case '{':
                     this.tokens.add(new Token(ExprParser.TokenType.LCURLY, c));
                     break;
                  case '|':
                     if (this.nextCharEquals(i, '|')) {
                        ++i;
                        this.tokens.add(new Token(ExprParser.TokenType.OROR, "||"));
                     } else {
                        this.tokens.add(new Token(ExprParser.TokenType.BITOR, c));
                     }
                     break;
                  case '}':
                     this.tokens.add(new Token(ExprParser.TokenType.RCURLY, c));
                     break;
                  case '~':
                     this.tokens.add(new Token(ExprParser.TokenType.NEG, c));
               }
            } else {
               while(i < this.string.length() && Character.isUnicodeIdentifierPart(this.string.charAt(i))) {
                  ++i;
               }

               String val = this.string.substring(start, i);
               String valLower = val.toLowerCase();
               if (i < this.string.length()) {
                  --i;
               }

               if (reservedWords.containsKey(valLower)) {
                  if ("and".equals(valLower)) {
                     this.tokens.add(new Token((TokenType)reservedWords.get(valLower), "&&"));
                  } else if ("or".equals(valLower)) {
                     this.tokens.add(new Token((TokenType)reservedWords.get(valLower), "||"));
                  } else {
                     this.tokens.add(new Token((TokenType)reservedWords.get(valLower), valLower));
                  }
               } else {
                  this.tokens.add(new Token(ExprParser.TokenType.IDENT, val));
               }
            }
         }
      }

   }

   void assertTokenAt(int pos, TokenType type) {
      if (this.tokens.size() <= pos) {
         throw new WrongArgumentException("No more tokens when expecting " + type + " at token pos " + pos);
      } else if (((Token)this.tokens.get(pos)).type != type) {
         throw new WrongArgumentException("Expected token type " + type + " at token pos " + pos);
      }
   }

   boolean currentTokenTypeEquals(TokenType t) {
      return this.posTokenTypeEquals(this.tokenPos, t);
   }

   boolean nextTokenTypeEquals(TokenType t) {
      return this.posTokenTypeEquals(this.tokenPos + 1, t);
   }

   boolean posTokenTypeEquals(int pos, TokenType t) {
      return this.tokens.size() > pos && ((Token)this.tokens.get(pos)).type == t;
   }

   String consumeToken(TokenType t) {
      this.assertTokenAt(this.tokenPos, t);
      String value = ((Token)this.tokens.get(this.tokenPos)).value;
      ++this.tokenPos;
      return value;
   }

   List<MysqlxExpr.Expr> parenExprList() {
      List<MysqlxExpr.Expr> exprs = new ArrayList();
      this.consumeToken(ExprParser.TokenType.LPAREN);
      if (!this.currentTokenTypeEquals(ExprParser.TokenType.RPAREN)) {
         exprs.add(this.expr());

         while(this.currentTokenTypeEquals(ExprParser.TokenType.COMMA)) {
            this.consumeToken(ExprParser.TokenType.COMMA);
            exprs.add(this.expr());
         }
      }

      this.consumeToken(ExprParser.TokenType.RPAREN);
      return exprs;
   }

   MysqlxExpr.Expr functionCall() {
      MysqlxExpr.Identifier id = this.identifier();
      MysqlxExpr.FunctionCall.Builder b = MysqlxExpr.FunctionCall.newBuilder();
      b.setName(id);
      b.addAllParam(this.parenExprList());
      return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.FUNC_CALL).setFunctionCall(b.build()).build();
   }

   MysqlxExpr.Expr starOperator() {
      MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName("*").build();
      return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
   }

   MysqlxExpr.Identifier identifier() {
      MysqlxExpr.Identifier.Builder builder = MysqlxExpr.Identifier.newBuilder();
      this.assertTokenAt(this.tokenPos, ExprParser.TokenType.IDENT);
      if (this.nextTokenTypeEquals(ExprParser.TokenType.DOT)) {
         builder.setSchemaName(((Token)this.tokens.get(this.tokenPos)).value);
         this.consumeToken(ExprParser.TokenType.IDENT);
         this.consumeToken(ExprParser.TokenType.DOT);
         this.assertTokenAt(this.tokenPos, ExprParser.TokenType.IDENT);
      }

      builder.setName(((Token)this.tokens.get(this.tokenPos)).value);
      this.consumeToken(ExprParser.TokenType.IDENT);
      return builder.build();
   }

   MysqlxExpr.DocumentPathItem docPathMember() {
      this.consumeToken(ExprParser.TokenType.DOT);
      Token t = (Token)this.tokens.get(this.tokenPos);
      String memberName;
      if (this.currentTokenTypeEquals(ExprParser.TokenType.IDENT)) {
         if (!t.value.equals(ExprUnparser.quoteIdentifier(t.value))) {
            throw new WrongArgumentException("'" + t.value + "' is not a valid JSON/ECMAScript identifier");
         }

         this.consumeToken(ExprParser.TokenType.IDENT);
         memberName = t.value;
      } else {
         if (!this.currentTokenTypeEquals(ExprParser.TokenType.LSTRING)) {
            throw new WrongArgumentException("Expected token type IDENT or LSTRING in JSON path at token pos " + this.tokenPos);
         }

         this.consumeToken(ExprParser.TokenType.LSTRING);
         memberName = t.value;
      }

      MysqlxExpr.DocumentPathItem.Builder item = MysqlxExpr.DocumentPathItem.newBuilder();
      item.setType(MysqlxExpr.DocumentPathItem.Type.MEMBER);
      item.setValue(memberName);
      return item.build();
   }

   MysqlxExpr.DocumentPathItem docPathArrayLoc() {
      MysqlxExpr.DocumentPathItem.Builder builder = MysqlxExpr.DocumentPathItem.newBuilder();
      this.consumeToken(ExprParser.TokenType.LSQBRACKET);
      if (this.currentTokenTypeEquals(ExprParser.TokenType.STAR)) {
         this.consumeToken(ExprParser.TokenType.STAR);
         this.consumeToken(ExprParser.TokenType.RSQBRACKET);
         return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX_ASTERISK).build();
      } else if (this.currentTokenTypeEquals(ExprParser.TokenType.LNUM_INT)) {
         Integer v = Integer.valueOf(((Token)this.tokens.get(this.tokenPos)).value);
         if (v < 0) {
            throw new WrongArgumentException("Array index cannot be negative at " + this.tokenPos);
         } else {
            this.consumeToken(ExprParser.TokenType.LNUM_INT);
            this.consumeToken(ExprParser.TokenType.RSQBRACKET);
            return builder.setType(MysqlxExpr.DocumentPathItem.Type.ARRAY_INDEX).setIndex(v).build();
         }
      } else {
         throw new WrongArgumentException("Expected token type STAR or LNUM_INT in JSON path array index at token pos " + this.tokenPos);
      }
   }

   public List<MysqlxExpr.DocumentPathItem> documentPath() {
      List<MysqlxExpr.DocumentPathItem> items = new ArrayList();

      while(true) {
         while(!this.currentTokenTypeEquals(ExprParser.TokenType.DOT)) {
            if (this.currentTokenTypeEquals(ExprParser.TokenType.DOTSTAR)) {
               this.consumeToken(ExprParser.TokenType.DOTSTAR);
               items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER_ASTERISK).build());
            } else if (this.currentTokenTypeEquals(ExprParser.TokenType.LSQBRACKET)) {
               items.add(this.docPathArrayLoc());
            } else {
               if (!this.currentTokenTypeEquals(ExprParser.TokenType.DOUBLESTAR)) {
                  if (items.size() > 0 && ((MysqlxExpr.DocumentPathItem)items.get(items.size() - 1)).getType() == MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK) {
                     throw new WrongArgumentException("JSON path may not end in '**' at " + this.tokenPos);
                  }

                  return items;
               }

               this.consumeToken(ExprParser.TokenType.DOUBLESTAR);
               items.add(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.DOUBLE_ASTERISK).build());
            }
         }

         items.add(this.docPathMember());
      }
   }

   public MysqlxExpr.Expr documentField() {
      MysqlxExpr.ColumnIdentifier.Builder builder = MysqlxExpr.ColumnIdentifier.newBuilder();
      if (this.currentTokenTypeEquals(ExprParser.TokenType.IDENT)) {
         builder.addDocumentPath(MysqlxExpr.DocumentPathItem.newBuilder().setType(MysqlxExpr.DocumentPathItem.Type.MEMBER).setValue(this.consumeToken(ExprParser.TokenType.IDENT)).build());
      }

      builder.addAllDocumentPath(this.documentPath());
      return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(builder.build()).build();
   }

   MysqlxExpr.Expr columnIdentifier() {
      List<String> parts = new LinkedList();
      parts.add(this.consumeToken(ExprParser.TokenType.IDENT));

      while(this.currentTokenTypeEquals(ExprParser.TokenType.DOT)) {
         this.consumeToken(ExprParser.TokenType.DOT);
         parts.add(this.consumeToken(ExprParser.TokenType.IDENT));
         if (parts.size() == 3) {
            break;
         }
      }

      Collections.reverse(parts);
      MysqlxExpr.ColumnIdentifier.Builder id = MysqlxExpr.ColumnIdentifier.newBuilder();

      for(int i = 0; i < parts.size(); ++i) {
         switch (i) {
            case 0:
               id.setName((String)parts.get(0));
               break;
            case 1:
               id.setTableName((String)parts.get(1));
               break;
            case 2:
               id.setSchemaName((String)parts.get(2));
         }
      }

      if (this.currentTokenTypeEquals(ExprParser.TokenType.COLDOCPATH)) {
         this.consumeToken(ExprParser.TokenType.COLDOCPATH);
         if (this.currentTokenTypeEquals(ExprParser.TokenType.DOLLAR)) {
            this.consumeToken(ExprParser.TokenType.DOLLAR);
            id.addAllDocumentPath(this.documentPath());
         } else if (this.currentTokenTypeEquals(ExprParser.TokenType.LSTRING)) {
            String path = this.consumeToken(ExprParser.TokenType.LSTRING);
            if (path.charAt(0) != '$') {
               throw new WrongArgumentException("Invalid document path at " + this.tokenPos);
            }

            id.addAllDocumentPath((new ExprParser(path.substring(1, path.length()))).documentPath());
         }

         if (id.getDocumentPathCount() == 0) {
            throw new WrongArgumentException("Invalid document path at " + this.tokenPos);
         }
      }

      return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.IDENT).setIdentifier(id.build()).build();
   }

   MysqlxExpr.Expr buildUnaryOp(String name, MysqlxExpr.Expr param) {
      String opName = "-".equals(name) ? "sign_minus" : ("+".equals(name) ? "sign_plus" : name);
      MysqlxExpr.Operator op = MysqlxExpr.Operator.newBuilder().setName(opName).addParam(param).build();
      return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(op).build();
   }

   MysqlxExpr.Expr atomicExpr() {
      if (this.tokenPos >= this.tokens.size()) {
         throw new WrongArgumentException("No more tokens when expecting one at token pos " + this.tokenPos);
      } else {
         Token t = (Token)this.tokens.get(this.tokenPos);
         ++this.tokenPos;
         switch (t.type) {
            case EROTEME:
            case COLON:
               String placeholderName;
               if (this.currentTokenTypeEquals(ExprParser.TokenType.LNUM_INT)) {
                  placeholderName = this.consumeToken(ExprParser.TokenType.LNUM_INT);
               } else if (this.currentTokenTypeEquals(ExprParser.TokenType.IDENT)) {
                  placeholderName = this.consumeToken(ExprParser.TokenType.IDENT);
               } else {
                  if (t.type != ExprParser.TokenType.EROTEME) {
                     throw new WrongArgumentException("Invalid placeholder name at token pos " + this.tokenPos);
                  }

                  placeholderName = String.valueOf(this.positionalPlaceholderCount);
               }

               MysqlxExpr.Expr.Builder placeholder = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.PLACEHOLDER);
               if (this.placeholderNameToPosition.containsKey(placeholderName)) {
                  placeholder.setPosition((Integer)this.placeholderNameToPosition.get(placeholderName));
               } else {
                  placeholder.setPosition(this.positionalPlaceholderCount);
                  this.placeholderNameToPosition.put(placeholderName, this.positionalPlaceholderCount);
                  ++this.positionalPlaceholderCount;
               }

               return placeholder.build();
            case LPAREN:
               MysqlxExpr.Expr e = this.expr();
               this.consumeToken(ExprParser.TokenType.RPAREN);
               return e;
            case LCURLY:
               MysqlxExpr.Object.Builder builder = MysqlxExpr.Object.newBuilder();
               if (this.currentTokenTypeEquals(ExprParser.TokenType.LSTRING)) {
                  this.parseCommaSeparatedList(() -> {
                     String key = this.consumeToken(ExprParser.TokenType.LSTRING);
                     this.consumeToken(ExprParser.TokenType.COLON);
                     MysqlxExpr.Expr value = this.expr();
                     return Collections.singletonMap(key, value);
                  }).stream().map((pair) -> {
                     return (Map.Entry)pair.entrySet().iterator().next();
                  }).map((ex) -> {
                     return MysqlxExpr.Object.ObjectField.newBuilder().setKey((String)ex.getKey()).setValue((MysqlxExpr.Expr)ex.getValue());
                  }).forEach(builder::addFld);
               }

               this.consumeToken(ExprParser.TokenType.RCURLY);
               return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OBJECT).setObject(builder.build()).build();
            case LSQBRACKET:
               MysqlxExpr.Array.Builder builder = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).getArrayBuilder();
               if (!this.currentTokenTypeEquals(ExprParser.TokenType.RSQBRACKET)) {
                  this.parseCommaSeparatedList(() -> {
                     return this.expr();
                  }).stream().forEach(builder::addValue);
               }

               this.consumeToken(ExprParser.TokenType.RSQBRACKET);
               return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.ARRAY).setArray(builder).build();
            case CAST:
               this.consumeToken(ExprParser.TokenType.LPAREN);
               MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(ExprParser.TokenType.CAST.toString().toLowerCase());
               builder.addParam(this.expr());
               this.consumeToken(ExprParser.TokenType.AS);
               StringBuilder typeStr = new StringBuilder(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase());
               if (this.currentTokenTypeEquals(ExprParser.TokenType.DECIMAL)) {
                  ++this.tokenPos;
                  if (this.currentTokenTypeEquals(ExprParser.TokenType.LPAREN)) {
                     typeStr.append(this.consumeToken(ExprParser.TokenType.LPAREN));
                     typeStr.append(this.consumeToken(ExprParser.TokenType.LNUM_INT));
                     if (this.currentTokenTypeEquals(ExprParser.TokenType.COMMA)) {
                        typeStr.append(this.consumeToken(ExprParser.TokenType.COMMA));
                        typeStr.append(this.consumeToken(ExprParser.TokenType.LNUM_INT));
                     }

                     typeStr.append(this.consumeToken(ExprParser.TokenType.RPAREN));
                  }
               } else if (!this.currentTokenTypeEquals(ExprParser.TokenType.CHAR) && !this.currentTokenTypeEquals(ExprParser.TokenType.BINARY)) {
                  if (!this.currentTokenTypeEquals(ExprParser.TokenType.UNSIGNED) && !this.currentTokenTypeEquals(ExprParser.TokenType.SIGNED)) {
                     if (!this.currentTokenTypeEquals(ExprParser.TokenType.JSON) && !this.currentTokenTypeEquals(ExprParser.TokenType.DATE) && !this.currentTokenTypeEquals(ExprParser.TokenType.DATETIME) && !this.currentTokenTypeEquals(ExprParser.TokenType.TIME)) {
                        throw new WrongArgumentException("Expected valid CAST type argument at " + this.tokenPos);
                     }

                     ++this.tokenPos;
                  } else {
                     ++this.tokenPos;
                     if (this.currentTokenTypeEquals(ExprParser.TokenType.INTEGER)) {
                        this.consumeToken(ExprParser.TokenType.INTEGER);
                     }
                  }
               } else {
                  ++this.tokenPos;
                  if (this.currentTokenTypeEquals(ExprParser.TokenType.LPAREN)) {
                     typeStr.append(this.consumeToken(ExprParser.TokenType.LPAREN));
                     typeStr.append(this.consumeToken(ExprParser.TokenType.LNUM_INT));
                     typeStr.append(this.consumeToken(ExprParser.TokenType.RPAREN));
                  }
               }

               this.consumeToken(ExprParser.TokenType.RPAREN);
               builder.addParam(ExprUtil.buildLiteralScalar(typeStr.toString().getBytes()));
               return MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
            case PLUS:
            case MINUS:
               if (!this.currentTokenTypeEquals(ExprParser.TokenType.LNUM_INT) && !this.currentTokenTypeEquals(ExprParser.TokenType.LNUM_DOUBLE)) {
                  return this.buildUnaryOp(t.value, this.atomicExpr());
               }

               ((Token)this.tokens.get(this.tokenPos)).value = t.value + ((Token)this.tokens.get(this.tokenPos)).value;
               return this.atomicExpr();
            case NOT:
            case NEG:
            case BANG:
               return this.buildUnaryOp(t.value, this.atomicExpr());
            case LSTRING:
               return ExprUtil.buildLiteralScalar(t.value);
            case NULL:
               return ExprUtil.buildLiteralNullScalar();
            case LNUM_INT:
               return ExprUtil.buildLiteralScalar(Long.valueOf(t.value));
            case LNUM_DOUBLE:
               return ExprUtil.buildLiteralScalar(Double.valueOf(t.value));
            case TRUE:
            case FALSE:
               return ExprUtil.buildLiteralScalar(t.type == ExprParser.TokenType.TRUE);
            case DOLLAR:
               return this.documentField();
            case STAR:
               return this.starOperator();
            case IDENT:
               --this.tokenPos;
               if (this.nextTokenTypeEquals(ExprParser.TokenType.LPAREN) || this.posTokenTypeEquals(this.tokenPos + 1, ExprParser.TokenType.DOT) && this.posTokenTypeEquals(this.tokenPos + 2, ExprParser.TokenType.IDENT) && this.posTokenTypeEquals(this.tokenPos + 3, ExprParser.TokenType.LPAREN)) {
                  return this.functionCall();
               } else {
                  if (this.allowRelationalColumns) {
                     return this.columnIdentifier();
                  }

                  return this.documentField();
               }
            default:
               throw new WrongArgumentException("Cannot find atomic expression at token pos: " + (this.tokenPos - 1));
         }
      }
   }

   MysqlxExpr.Expr parseLeftAssocBinaryOpExpr(TokenType[] types, ParseExpr innerParser) {
      MysqlxExpr.Expr lhs;
      MysqlxExpr.Operator.Builder builder;
      for(lhs = innerParser.parseExpr(); this.tokenPos < this.tokens.size() && Arrays.asList(types).contains(((Token)this.tokens.get(this.tokenPos)).type); lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build()) {
         builder = MysqlxExpr.Operator.newBuilder().setName(((Token)this.tokens.get(this.tokenPos)).value).addParam(lhs);
         ++this.tokenPos;
         builder.addParam(innerParser.parseExpr());
      }

      return lhs;
   }

   MysqlxExpr.Expr addSubIntervalExpr() {
      MysqlxExpr.Expr lhs;
      MysqlxExpr.Operator.Builder builder;
      for(lhs = this.atomicExpr(); (this.currentTokenTypeEquals(ExprParser.TokenType.PLUS) || this.currentTokenTypeEquals(ExprParser.TokenType.MINUS)) && this.nextTokenTypeEquals(ExprParser.TokenType.INTERVAL); lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build()) {
         Token op = (Token)this.tokens.get(this.tokenPos);
         ++this.tokenPos;
         builder = MysqlxExpr.Operator.newBuilder().addParam(lhs);
         this.consumeToken(ExprParser.TokenType.INTERVAL);
         if (op.type == ExprParser.TokenType.PLUS) {
            builder.setName("date_add");
         } else {
            builder.setName("date_sub");
         }

         builder.addParam(this.bitExpr());
         if (!this.currentTokenTypeEquals(ExprParser.TokenType.MICROSECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.SECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.MINUTE) && !this.currentTokenTypeEquals(ExprParser.TokenType.HOUR) && !this.currentTokenTypeEquals(ExprParser.TokenType.DAY) && !this.currentTokenTypeEquals(ExprParser.TokenType.WEEK) && !this.currentTokenTypeEquals(ExprParser.TokenType.MONTH) && !this.currentTokenTypeEquals(ExprParser.TokenType.QUARTER) && !this.currentTokenTypeEquals(ExprParser.TokenType.YEAR) && !this.currentTokenTypeEquals(ExprParser.TokenType.SECOND_MICROSECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.MINUTE_MICROSECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.MINUTE_SECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.HOUR_MICROSECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.HOUR_SECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.HOUR_MINUTE) && !this.currentTokenTypeEquals(ExprParser.TokenType.DAY_MICROSECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.DAY_SECOND) && !this.currentTokenTypeEquals(ExprParser.TokenType.DAY_MINUTE) && !this.currentTokenTypeEquals(ExprParser.TokenType.DAY_HOUR) && !this.currentTokenTypeEquals(ExprParser.TokenType.YEAR_MONTH)) {
            throw new WrongArgumentException("Expected interval units at " + this.tokenPos);
         }

         builder.addParam(ExprUtil.buildLiteralScalar(((Token)this.tokens.get(this.tokenPos)).value.toUpperCase().getBytes()));
         ++this.tokenPos;
      }

      return lhs;
   }

   MysqlxExpr.Expr mulDivExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.STAR, ExprParser.TokenType.SLASH, ExprParser.TokenType.MOD}, this::addSubIntervalExpr);
   }

   MysqlxExpr.Expr addSubExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.PLUS, ExprParser.TokenType.MINUS}, this::mulDivExpr);
   }

   MysqlxExpr.Expr shiftExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.LSHIFT, ExprParser.TokenType.RSHIFT}, this::addSubExpr);
   }

   MysqlxExpr.Expr bitExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.BITAND, ExprParser.TokenType.BITOR, ExprParser.TokenType.BITXOR}, this::shiftExpr);
   }

   MysqlxExpr.Expr compExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.GE, ExprParser.TokenType.GT, ExprParser.TokenType.LE, ExprParser.TokenType.LT, ExprParser.TokenType.EQ, ExprParser.TokenType.NE}, this::bitExpr);
   }

   MysqlxExpr.Expr ilriExpr() {
      MysqlxExpr.Expr lhs = this.compExpr();
      List<TokenType> expected = Arrays.asList(ExprParser.TokenType.IS, ExprParser.TokenType.IN, ExprParser.TokenType.LIKE, ExprParser.TokenType.BETWEEN, ExprParser.TokenType.REGEXP, ExprParser.TokenType.NOT, ExprParser.TokenType.OVERLAPS);

      while(this.tokenPos < this.tokens.size() && expected.contains(((Token)this.tokens.get(this.tokenPos)).type)) {
         boolean isNot = false;
         if (this.currentTokenTypeEquals(ExprParser.TokenType.NOT)) {
            this.consumeToken(ExprParser.TokenType.NOT);
            isNot = true;
         }

         if (this.tokenPos < this.tokens.size()) {
            List<MysqlxExpr.Expr> params = new ArrayList();
            params.add(lhs);
            String opName = ((Token)this.tokens.get(this.tokenPos)).value.toLowerCase();
            switch (((Token)this.tokens.get(this.tokenPos)).type) {
               case IS:
                  this.consumeToken(ExprParser.TokenType.IS);
                  if (this.currentTokenTypeEquals(ExprParser.TokenType.NOT)) {
                     this.consumeToken(ExprParser.TokenType.NOT);
                     opName = "is_not";
                  }

                  params.add(this.compExpr());
                  break;
               case IN:
                  this.consumeToken(ExprParser.TokenType.IN);
                  if (this.currentTokenTypeEquals(ExprParser.TokenType.LPAREN)) {
                     params.addAll(this.parenExprList());
                  } else {
                     opName = "cont_in";
                     params.add(this.compExpr());
                  }
                  break;
               case LIKE:
                  this.consumeToken(ExprParser.TokenType.LIKE);
                  params.add(this.compExpr());
                  if (this.currentTokenTypeEquals(ExprParser.TokenType.ESCAPE)) {
                     this.consumeToken(ExprParser.TokenType.ESCAPE);
                     params.add(this.compExpr());
                  }
                  break;
               case BETWEEN:
                  this.consumeToken(ExprParser.TokenType.BETWEEN);
                  params.add(this.compExpr());
                  this.assertTokenAt(this.tokenPos, ExprParser.TokenType.AND);
                  this.consumeToken(ExprParser.TokenType.AND);
                  params.add(this.compExpr());
                  break;
               case REGEXP:
                  this.consumeToken(ExprParser.TokenType.REGEXP);
                  params.add(this.compExpr());
                  break;
               case OVERLAPS:
                  this.consumeToken(ExprParser.TokenType.OVERLAPS);
                  params.add(this.compExpr());
                  break;
               default:
                  throw new WrongArgumentException("Unknown token after NOT at pos: " + this.tokenPos);
            }

            if (isNot) {
               opName = "not_" + opName;
            }

            MysqlxExpr.Operator.Builder builder = MysqlxExpr.Operator.newBuilder().setName(opName).addAllParam(params);
            lhs = MysqlxExpr.Expr.newBuilder().setType(MysqlxExpr.Expr.Type.OPERATOR).setOperator(builder.build()).build();
         }
      }

      return lhs;
   }

   MysqlxExpr.Expr andExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.AND, ExprParser.TokenType.ANDAND}, this::ilriExpr);
   }

   MysqlxExpr.Expr orExpr() {
      return this.parseLeftAssocBinaryOpExpr(new TokenType[]{ExprParser.TokenType.OR, ExprParser.TokenType.OROR}, this::andExpr);
   }

   MysqlxExpr.Expr expr() {
      MysqlxExpr.Expr e = this.orExpr();
      return e;
   }

   public MysqlxExpr.Expr parse() {
      try {
         MysqlxExpr.Expr e = this.expr();
         if (this.tokenPos != this.tokens.size()) {
            throw new WrongArgumentException("Only " + this.tokenPos + " tokens consumed, out of " + this.tokens.size());
         } else {
            return e;
         }
      } catch (IllegalArgumentException var2) {
         throw new WrongArgumentException("Unable to parse query '" + this.string + "'", var2);
      }
   }

   private <T> List<T> parseCommaSeparatedList(Supplier<T> elementParser) {
      List<T> elements = new ArrayList();

      for(boolean first = true; first || this.currentTokenTypeEquals(ExprParser.TokenType.COMMA); elements.add(elementParser.get())) {
         if (!first) {
            this.consumeToken(ExprParser.TokenType.COMMA);
         } else {
            first = false;
         }
      }

      return elements;
   }

   public List<MysqlxCrud.Order> parseOrderSpec() {
      return this.parseCommaSeparatedList(() -> {
         MysqlxCrud.Order.Builder builder = MysqlxCrud.Order.newBuilder();
         builder.setExpr(this.expr());
         if (this.currentTokenTypeEquals(ExprParser.TokenType.ORDERBY_ASC)) {
            this.consumeToken(ExprParser.TokenType.ORDERBY_ASC);
            builder.setDirection(MysqlxCrud.Order.Direction.ASC);
         } else if (this.currentTokenTypeEquals(ExprParser.TokenType.ORDERBY_DESC)) {
            this.consumeToken(ExprParser.TokenType.ORDERBY_DESC);
            builder.setDirection(MysqlxCrud.Order.Direction.DESC);
         }

         return builder.build();
      });
   }

   public List<MysqlxCrud.Projection> parseTableSelectProjection() {
      return this.parseCommaSeparatedList(() -> {
         MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
         builder.setSource(this.expr());
         if (this.currentTokenTypeEquals(ExprParser.TokenType.AS)) {
            this.consumeToken(ExprParser.TokenType.AS);
            builder.setAlias(this.consumeToken(ExprParser.TokenType.IDENT));
         }

         return builder.build();
      });
   }

   public MysqlxCrud.Column parseTableInsertField() {
      return MysqlxCrud.Column.newBuilder().setName(this.consumeToken(ExprParser.TokenType.IDENT)).build();
   }

   public MysqlxExpr.ColumnIdentifier parseTableUpdateField() {
      return this.columnIdentifier().getIdentifier();
   }

   public List<MysqlxCrud.Projection> parseDocumentProjection() {
      this.allowRelationalColumns = false;
      return this.parseCommaSeparatedList(() -> {
         MysqlxCrud.Projection.Builder builder = MysqlxCrud.Projection.newBuilder();
         builder.setSource(this.expr());
         this.consumeToken(ExprParser.TokenType.AS);
         builder.setAlias(this.consumeToken(ExprParser.TokenType.IDENT));
         return builder.build();
      });
   }

   public List<MysqlxExpr.Expr> parseExprList() {
      return this.parseCommaSeparatedList(this::expr);
   }

   public int getPositionalPlaceholderCount() {
      return this.positionalPlaceholderCount;
   }

   public Map<String, Integer> getPlaceholderNameToPositionMap() {
      return Collections.unmodifiableMap(this.placeholderNameToPosition);
   }

   static {
      reservedWords.put("and", ExprParser.TokenType.AND);
      reservedWords.put("or", ExprParser.TokenType.OR);
      reservedWords.put("xor", ExprParser.TokenType.XOR);
      reservedWords.put("is", ExprParser.TokenType.IS);
      reservedWords.put("not", ExprParser.TokenType.NOT);
      reservedWords.put("like", ExprParser.TokenType.LIKE);
      reservedWords.put("in", ExprParser.TokenType.IN);
      reservedWords.put("regexp", ExprParser.TokenType.REGEXP);
      reservedWords.put("between", ExprParser.TokenType.BETWEEN);
      reservedWords.put("interval", ExprParser.TokenType.INTERVAL);
      reservedWords.put("escape", ExprParser.TokenType.ESCAPE);
      reservedWords.put("div", ExprParser.TokenType.SLASH);
      reservedWords.put("hex", ExprParser.TokenType.HEX);
      reservedWords.put("bin", ExprParser.TokenType.BIN);
      reservedWords.put("true", ExprParser.TokenType.TRUE);
      reservedWords.put("false", ExprParser.TokenType.FALSE);
      reservedWords.put("null", ExprParser.TokenType.NULL);
      reservedWords.put("microsecond", ExprParser.TokenType.MICROSECOND);
      reservedWords.put("second", ExprParser.TokenType.SECOND);
      reservedWords.put("minute", ExprParser.TokenType.MINUTE);
      reservedWords.put("hour", ExprParser.TokenType.HOUR);
      reservedWords.put("day", ExprParser.TokenType.DAY);
      reservedWords.put("week", ExprParser.TokenType.WEEK);
      reservedWords.put("month", ExprParser.TokenType.MONTH);
      reservedWords.put("quarter", ExprParser.TokenType.QUARTER);
      reservedWords.put("year", ExprParser.TokenType.YEAR);
      reservedWords.put("second_microsecond", ExprParser.TokenType.SECOND_MICROSECOND);
      reservedWords.put("minute_microsecond", ExprParser.TokenType.MINUTE_MICROSECOND);
      reservedWords.put("minute_second", ExprParser.TokenType.MINUTE_SECOND);
      reservedWords.put("hour_microsecond", ExprParser.TokenType.HOUR_MICROSECOND);
      reservedWords.put("hour_second", ExprParser.TokenType.HOUR_SECOND);
      reservedWords.put("hour_minute", ExprParser.TokenType.HOUR_MINUTE);
      reservedWords.put("day_microsecond", ExprParser.TokenType.DAY_MICROSECOND);
      reservedWords.put("day_second", ExprParser.TokenType.DAY_SECOND);
      reservedWords.put("day_minute", ExprParser.TokenType.DAY_MINUTE);
      reservedWords.put("day_hour", ExprParser.TokenType.DAY_HOUR);
      reservedWords.put("year_month", ExprParser.TokenType.YEAR_MONTH);
      reservedWords.put("asc", ExprParser.TokenType.ORDERBY_ASC);
      reservedWords.put("desc", ExprParser.TokenType.ORDERBY_DESC);
      reservedWords.put("as", ExprParser.TokenType.AS);
      reservedWords.put("cast", ExprParser.TokenType.CAST);
      reservedWords.put("decimal", ExprParser.TokenType.DECIMAL);
      reservedWords.put("unsigned", ExprParser.TokenType.UNSIGNED);
      reservedWords.put("signed", ExprParser.TokenType.SIGNED);
      reservedWords.put("integer", ExprParser.TokenType.INTEGER);
      reservedWords.put("date", ExprParser.TokenType.DATE);
      reservedWords.put("time", ExprParser.TokenType.TIME);
      reservedWords.put("datetime", ExprParser.TokenType.DATETIME);
      reservedWords.put("char", ExprParser.TokenType.CHAR);
      reservedWords.put("binary", ExprParser.TokenType.BINARY);
      reservedWords.put("json", ExprParser.TokenType.BINARY);
      reservedWords.put("overlaps", ExprParser.TokenType.OVERLAPS);
   }

   @FunctionalInterface
   interface ParseExpr {
      MysqlxExpr.Expr parseExpr();
   }

   static class Token {
      TokenType type;
      String value;

      public Token(TokenType x, char c) {
         this.type = x;
         this.value = new String(new char[]{c});
      }

      public Token(TokenType t, String v) {
         this.type = t;
         this.value = v;
      }

      public String toString() {
         return this.type != ExprParser.TokenType.IDENT && this.type != ExprParser.TokenType.LNUM_INT && this.type != ExprParser.TokenType.LNUM_DOUBLE && this.type != ExprParser.TokenType.LSTRING ? this.type.toString() : this.type.toString() + "(" + this.value + ")";
      }
   }

   private static enum TokenType {
      NOT,
      AND,
      ANDAND,
      OR,
      OROR,
      XOR,
      IS,
      LPAREN,
      RPAREN,
      LSQBRACKET,
      RSQBRACKET,
      BETWEEN,
      TRUE,
      NULL,
      FALSE,
      IN,
      LIKE,
      INTERVAL,
      REGEXP,
      ESCAPE,
      IDENT,
      LSTRING,
      LNUM_INT,
      LNUM_DOUBLE,
      DOT,
      DOLLAR,
      COMMA,
      EQ,
      NE,
      GT,
      GE,
      LT,
      LE,
      BITAND,
      BITOR,
      BITXOR,
      LSHIFT,
      RSHIFT,
      PLUS,
      MINUS,
      STAR,
      SLASH,
      HEX,
      BIN,
      NEG,
      BANG,
      EROTEME,
      MICROSECOND,
      SECOND,
      MINUTE,
      HOUR,
      DAY,
      WEEK,
      MONTH,
      QUARTER,
      YEAR,
      SECOND_MICROSECOND,
      MINUTE_MICROSECOND,
      MINUTE_SECOND,
      HOUR_MICROSECOND,
      HOUR_SECOND,
      HOUR_MINUTE,
      DAY_MICROSECOND,
      DAY_SECOND,
      DAY_MINUTE,
      DAY_HOUR,
      YEAR_MONTH,
      DOUBLESTAR,
      MOD,
      COLON,
      ORDERBY_ASC,
      ORDERBY_DESC,
      AS,
      LCURLY,
      RCURLY,
      DOTSTAR,
      CAST,
      DECIMAL,
      UNSIGNED,
      SIGNED,
      INTEGER,
      DATE,
      TIME,
      DATETIME,
      CHAR,
      BINARY,
      JSON,
      COLDOCPATH,
      OVERLAPS;
   }
}
