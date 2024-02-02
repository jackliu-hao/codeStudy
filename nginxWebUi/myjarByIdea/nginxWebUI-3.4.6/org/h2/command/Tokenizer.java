package org.h2.command;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.ListIterator;
import org.h2.engine.CastDataProvider;
import org.h2.message.DbException;
import org.h2.util.StringUtils;
import org.h2.value.Value;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDecfloat;
import org.h2.value.ValueNumeric;

public final class Tokenizer {
   private final CastDataProvider provider;
   private final boolean identifiersToUpper;
   private final boolean identifiersToLower;
   private final BitSet nonKeywords;

   Tokenizer(CastDataProvider var1, boolean var2, boolean var3, BitSet var4) {
      this.provider = var1;
      this.identifiersToUpper = var2;
      this.identifiersToLower = var3;
      this.nonKeywords = var4;
   }

   ArrayList<Token> tokenize(String var1, boolean var2) {
      ArrayList var3 = new ArrayList();
      int var4 = var1.length() - 1;
      boolean var5 = false;
      int var6 = 0;
      int var7 = 0;

      while(true) {
         while(true) {
            Object var10;
            label332:
            while(true) {
               while(true) {
                  while(true) {
                     while(true) {
                        while(true) {
                           while(true) {
                              label319:
                              while(var7 <= var4) {
                                 int var8 = var7;
                                 char var9 = var1.charAt(var7);
                                 int var11;
                                 char var13;
                                 switch (var9) {
                                    case '!':
                                       if (var7 >= var4) {
                                          throw DbException.getSyntaxError(var1, var7);
                                       }

                                       ++var7;
                                       var13 = var1.charAt(var7);
                                       if (var13 == '=') {
                                          var10 = new Token.KeywordToken(var8, 100);
                                       } else {
                                          if (var13 != '~') {
                                             throw DbException.getSyntaxError(var1, var7);
                                          }

                                          var10 = new Token.KeywordToken(var8, 122);
                                       }
                                       break label332;
                                    case '"':
                                    case '`':
                                       var7 = this.readQuotedIdentifier(var1, var4, var7, var7, var9, false, var3);
                                       break;
                                    case '#':
                                       if (!this.provider.getMode().supportPoundSymbolForColumnNames) {
                                          throw DbException.getSyntaxError(var1, var7);
                                       }

                                       var7 = this.readIdentifier(var1, var4, var7, var7, var9, var3);
                                       break;
                                    case '$':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 != '$') {
                                             var7 = parseParameterIndex(var1, var4, var7, var3);
                                             var6 = assignParameterIndex(var3, var6);
                                             break;
                                          }

                                          var7 += 2;
                                          int var12 = var1.indexOf("$$", var7);
                                          if (var12 < 0) {
                                             throw DbException.getSyntaxError(var1, var8);
                                          }

                                          var10 = new Token.CharacterStringToken(var8, var1.substring(var7, var12), false);
                                          var7 = var12 + 1;
                                       } else {
                                          var10 = new Token.ParameterToken(var7, 0);
                                       }
                                       break label332;
                                    case '%':
                                       var10 = new Token.KeywordToken(var7, 114);
                                       break label332;
                                    case '&':
                                       if (var7 < var4 && var1.charAt(var7 + 1) == '&') {
                                          ++var7;
                                          var10 = new Token.KeywordToken(var8, 107);
                                          break label332;
                                       }

                                       throw DbException.getSyntaxError(var1, var7);
                                    case '\'':
                                       var7 = readCharacterString(var1, var7, var4, var7, false, var3);
                                       break;
                                    case '(':
                                       var10 = new Token.KeywordToken(var7, 105);
                                       break label332;
                                    case ')':
                                       var10 = new Token.KeywordToken(var7, 106);
                                       if (!var2) {
                                          break label332;
                                       }

                                       var3.add(var10);
                                       var4 = skipWhitespace(var1, var4, var7 + 1) - 1;
                                       break label319;
                                    case '*':
                                       var10 = new Token.KeywordToken(var7, 108);
                                       break label332;
                                    case '+':
                                       var10 = new Token.KeywordToken(var7, 103);
                                       break label332;
                                    case ',':
                                       var10 = new Token.KeywordToken(var7, 109);
                                       break label332;
                                    case '-':
                                       if (var7 < var4 && var1.charAt(var7 + 1) == '-') {
                                          var7 = skipSimpleComment(var1, var4, var7);
                                          break;
                                       }

                                       var10 = new Token.KeywordToken(var7, 102);
                                       break label332;
                                    case '.':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 >= '0' && var13 <= '9') {
                                             var7 = readNumeric(var1, var7, var4, var7 + 1, var13, false, false, var3);
                                             break;
                                          }
                                       }

                                       var10 = new Token.KeywordToken(var7, 110);
                                       break label332;
                                    case '/':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 == '*') {
                                             var7 = skipBracketedComment(var1, var7, var4, var7);
                                             break;
                                          }

                                          if (var13 == '/') {
                                             var7 = skipSimpleComment(var1, var4, var7);
                                             break;
                                          }
                                       }

                                       var10 = new Token.KeywordToken(var7, 113);
                                       break label332;
                                    case '0':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 == 'X' || var13 == 'x') {
                                             var7 = readHexNumber(var1, this.provider, var7, var4, var7 + 2, var3);
                                             break;
                                          }
                                       }
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                       var7 = readNumeric(var1, var7, var4, var7 + 1, var9, var3);
                                       break;
                                    case ':':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 == ':') {
                                             ++var7;
                                             var10 = new Token.KeywordToken(var8, 120);
                                             break label332;
                                          }

                                          if (var13 == '=') {
                                             ++var7;
                                             var10 = new Token.KeywordToken(var8, 121);
                                             break label332;
                                          }
                                       }

                                       var10 = new Token.KeywordToken(var7, 116);
                                       break label332;
                                    case ';':
                                       var10 = new Token.KeywordToken(var7, 115);
                                       break label332;
                                    case '<':
                                       if (var7 < var4) {
                                          var13 = var1.charAt(var7 + 1);
                                          if (var13 == '=') {
                                             ++var7;
                                             var10 = new Token.KeywordToken(var8, 99);
                                             break label332;
                                          }

                                          if (var13 == '>') {
                                             ++var7;
                                             var10 = new Token.KeywordToken(var8, 100);
                                             break label332;
                                          }
                                       }

                                       var10 = new Token.KeywordToken(var7, 98);
                                       break label332;
                                    case '=':
                                       var10 = new Token.KeywordToken(var7, 95);
                                       break label332;
                                    case '>':
                                       if (var7 < var4 && var1.charAt(var7 + 1) == '=') {
                                          ++var7;
                                          var10 = new Token.KeywordToken(var8, 96);
                                       } else {
                                          var10 = new Token.KeywordToken(var7, 97);
                                       }
                                       break label332;
                                    case '?':
                                       if (var7 + 1 < var4 && var1.charAt(var7 + 1) == '?') {
                                          var13 = var1.charAt(var7 + 2);
                                          if (var13 == '(') {
                                             var7 += 2;
                                             var10 = new Token.KeywordToken(var8, 117);
                                             break label332;
                                          }

                                          if (var13 == ')') {
                                             var7 += 2;
                                             var10 = new Token.KeywordToken(var8, 118);
                                             break label332;
                                          }
                                       }

                                       var7 = parseParameterIndex(var1, var4, var7, var3);
                                       var6 = assignParameterIndex(var3, var6);
                                       break;
                                    case '@':
                                       var10 = new Token.KeywordToken(var7, 101);
                                       break label332;
                                    case 'A':
                                    case 'a':
                                       var7 = this.readA(var1, var4, var7, var7, var3);
                                       break;
                                    case 'B':
                                    case 'b':
                                       var7 = this.readB(var1, var4, var7, var7, var3);
                                       break;
                                    case 'C':
                                    case 'c':
                                       var7 = this.readC(var1, var4, var7, var7, var3);
                                       break;
                                    case 'D':
                                    case 'd':
                                       var7 = this.readD(var1, var4, var7, var7, var3);
                                       break;
                                    case 'E':
                                    case 'e':
                                       var7 = this.readE(var1, var4, var7, var7, var3);
                                       break;
                                    case 'F':
                                    case 'f':
                                       var7 = this.readF(var1, var4, var7, var7, var3);
                                       break;
                                    case 'G':
                                    case 'g':
                                       var7 = this.readG(var1, var4, var7, var7, var3);
                                       break;
                                    case 'H':
                                    case 'h':
                                       var7 = this.readH(var1, var4, var7, var7, var3);
                                       break;
                                    case 'I':
                                    case 'i':
                                       var7 = this.readI(var1, var4, var7, var7, var3);
                                       break;
                                    case 'J':
                                    case 'j':
                                       var7 = this.readJ(var1, var4, var7, var7, var3);
                                       break;
                                    case 'K':
                                    case 'k':
                                       var7 = this.readK(var1, var4, var7, var7, var3);
                                       break;
                                    case 'L':
                                    case 'l':
                                       var7 = this.readL(var1, var4, var7, var7, var3);
                                       break;
                                    case 'M':
                                    case 'm':
                                       var7 = this.readM(var1, var4, var7, var7, var3);
                                       break;
                                    case 'N':
                                    case 'n':
                                       if (var7 < var4 && var1.charAt(var7 + 1) == '\'') {
                                          var7 = readCharacterString(var1, var7, var4, var7 + 1, false, var3);
                                          break;
                                       }

                                       var7 = this.readN(var1, var4, var7, var7, var3);
                                       break;
                                    case 'O':
                                    case 'o':
                                       var7 = this.readO(var1, var4, var7, var7, var3);
                                       break;
                                    case 'P':
                                    case 'p':
                                       var7 = this.readP(var1, var4, var7, var7, var3);
                                       break;
                                    case 'Q':
                                    case 'q':
                                       var7 = this.readQ(var1, var4, var7, var7, var3);
                                       break;
                                    case 'R':
                                    case 'r':
                                       var7 = this.readR(var1, var4, var7, var7, var3);
                                       break;
                                    case 'S':
                                    case 's':
                                       var7 = this.readS(var1, var4, var7, var7, var3);
                                       break;
                                    case 'T':
                                    case 't':
                                       var7 = this.readT(var1, var4, var7, var7, var3);
                                       break;
                                    case 'U':
                                    case 'u':
                                       if (var7 + 1 < var4 && var1.charAt(var7 + 1) == '&') {
                                          var13 = var1.charAt(var7 + 2);
                                          if (var13 == '"') {
                                             var7 = this.readQuotedIdentifier(var1, var4, var7, var7 + 2, '"', true, var3);
                                             var5 = true;
                                             break;
                                          }

                                          if (var13 == '\'') {
                                             var7 = readCharacterString(var1, var7, var4, var7 + 2, true, var3);
                                             var5 = true;
                                             break;
                                          }
                                       }

                                       var7 = this.readU(var1, var4, var7, var7, var3);
                                       break;
                                    case 'V':
                                    case 'v':
                                       var7 = this.readV(var1, var4, var7, var7, var3);
                                       break;
                                    case 'W':
                                    case 'w':
                                       var7 = this.readW(var1, var4, var7, var7, var3);
                                       break;
                                    case 'X':
                                    case 'x':
                                       if (var7 < var4 && var1.charAt(var7 + 1) == '\'') {
                                          var7 = readBinaryString(var1, var7, var4, var7 + 1, var3);
                                          break;
                                       }

                                       var7 = this.readIdentifier(var1, var4, var7, var7, var9, var3);
                                       break;
                                    case 'Y':
                                    case 'y':
                                       var7 = this.readY(var1, var4, var7, var7, var3);
                                       break;
                                    case 'Z':
                                    case 'z':
                                       var7 = this.readIdentifier(var1, var4, var7, var7, var9, var3);
                                       break;
                                    case '[':
                                       if (this.provider.getMode().squareBracketQuotedNames) {
                                          ++var7;
                                          var11 = var1.indexOf(93, var7);
                                          if (var11 < 0) {
                                             throw DbException.getSyntaxError(var1, var8);
                                          }

                                          var10 = new Token.IdentifierToken(var8, var1.substring(var7, var11), true, false);
                                          var7 = var11;
                                       } else {
                                          var10 = new Token.KeywordToken(var7, 117);
                                       }
                                       break label332;
                                    case '\\':
                                    case '^':
                                    default:
                                       if (var9 <= ' ') {
                                          ++var7;
                                       } else {
                                          var11 = Character.isHighSurrogate(var9) ? var1.codePointAt(var7++) : var9;
                                          if (!Character.isSpaceChar(var11)) {
                                             if (!Character.isJavaIdentifierStart(var11)) {
                                                throw DbException.getSyntaxError(var1, var7);
                                             }

                                             var7 = this.readIdentifier(var1, var4, var7, var7, var11, var3);
                                          }
                                       }
                                       break;
                                    case ']':
                                       var10 = new Token.KeywordToken(var7, 118);
                                       break label332;
                                    case '_':
                                       var7 = this.read_(var1, var4, var7, var7, var3);
                                       break;
                                    case '{':
                                       var10 = new Token.KeywordToken(var7, 111);
                                       break label332;
                                    case '|':
                                       if (var7 >= var4) {
                                          throw DbException.getSyntaxError(var1, var7);
                                       }

                                       ++var7;
                                       if (var1.charAt(var7) != '|') {
                                          throw DbException.getSyntaxError(var1, var7);
                                       }

                                       var10 = new Token.KeywordToken(var8, 104);
                                       break label332;
                                    case '}':
                                       var10 = new Token.KeywordToken(var7, 112);
                                       break label332;
                                    case '~':
                                       var10 = new Token.KeywordToken(var7, 119);
                                       break label332;
                                 }
                              }

                              if (var5) {
                                 processUescape(var1, var3);
                              }

                              var3.add(new Token.EndOfInputToken(var4 + 1));
                              return var3;
                           }
                        }
                     }
                  }
               }
            }

            var3.add(var10);
            ++var7;
         }
      }
   }

   private int readIdentifier(String var1, int var2, int var3, int var4, int var5, ArrayList<Token> var6) {
      if (var5 >= 65536) {
         ++var4;
      }

      int var7 = this.findIdentifierEnd(var1, var2, var4 + Character.charCount(var5) - 1);
      var6.add(new Token.IdentifierToken(var3, this.extractIdentifier(var1, var3, var7), false, false));
      return var7;
   }

   private int readA(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8;
      if (var7 == 2) {
         var8 = (var1.charAt(var3 + 1) & '\uffdf') == 83 ? 7 : 2;
      } else if (eq("ALL", var1, var3, var7)) {
         var8 = 3;
      } else if (eq("AND", var1, var3, var7)) {
         var8 = 4;
      } else if (eq("ANY", var1, var3, var7)) {
         var8 = 5;
      } else if (eq("ARRAY", var1, var3, var7)) {
         var8 = 6;
      } else if (eq("ASYMMETRIC", var1, var3, var7)) {
         var8 = 8;
      } else if (eq("AUTHORIZATION", var1, var3, var7)) {
         var8 = 9;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readB(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("BETWEEN", var1, var3, var7) ? 10 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readC(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8;
      if (eq("CASE", var1, var3, var7)) {
         var8 = 11;
      } else if (eq("CAST", var1, var3, var7)) {
         var8 = 12;
      } else if (eq("CHECK", var1, var3, var7)) {
         var8 = 13;
      } else if (eq("CONSTRAINT", var1, var3, var7)) {
         var8 = 14;
      } else if (eq("CROSS", var1, var3, var7)) {
         var8 = 15;
      } else if (var7 >= 12 && eq("CURRENT_", var1, var3, 8)) {
         var8 = getTokenTypeCurrent(var1, var3, var7);
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private static int getTokenTypeCurrent(String var0, int var1, int var2) {
      var1 += 8;
      switch (var2) {
         case 12:
            if (eqCurrent("CURRENT_DATE", var0, var1, var2)) {
               return 17;
            }

            if (eqCurrent("CURRENT_PATH", var0, var1, var2)) {
               return 18;
            }

            if (eqCurrent("CURRENT_ROLE", var0, var1, var2)) {
               return 19;
            }

            if (eqCurrent("CURRENT_TIME", var0, var1, var2)) {
               return 21;
            }

            if (eqCurrent("CURRENT_USER", var0, var1, var2)) {
               return 23;
            }
         case 13:
         case 16:
         default:
            break;
         case 14:
            if (eqCurrent("CURRENT_SCHEMA", var0, var1, var2)) {
               return 20;
            }
            break;
         case 15:
            if (eqCurrent("CURRENT_CATALOG", var0, var1, var2)) {
               return 16;
            }
            break;
         case 17:
            if (eqCurrent("CURRENT_TIMESTAMP", var0, var1, var2)) {
               return 22;
            }
      }

      return 2;
   }

   private static boolean eqCurrent(String var0, String var1, int var2, int var3) {
      for(int var4 = 8; var4 < var3; ++var4) {
         if (var0.charAt(var4) != (var1.charAt(var2++) & '\uffdf')) {
            return false;
         }
      }

      return true;
   }

   private int readD(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("DAY", var1, var3, var7)) {
         var8 = 24;
      } else if (eq("DEFAULT", var1, var3, var7)) {
         var8 = 25;
      } else if (eq("DISTINCT", var1, var3, var7)) {
         var8 = 26;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readE(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("ELSE", var1, var3, var7)) {
         var8 = 27;
      } else if (eq("END", var1, var3, var7)) {
         var8 = 28;
      } else if (eq("EXCEPT", var1, var3, var7)) {
         var8 = 29;
      } else if (eq("EXISTS", var1, var3, var7)) {
         var8 = 30;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readF(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("FETCH", var1, var3, var7)) {
         var8 = 32;
      } else if (eq("FROM", var1, var3, var7)) {
         var8 = 35;
      } else if (eq("FOR", var1, var3, var7)) {
         var8 = 33;
      } else if (eq("FOREIGN", var1, var3, var7)) {
         var8 = 34;
      } else if (eq("FULL", var1, var3, var7)) {
         var8 = 36;
      } else if (eq("FALSE", var1, var3, var7)) {
         var8 = 31;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readG(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("GROUP", var1, var3, var7) ? 37 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readH(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("HAVING", var1, var3, var7)) {
         var8 = 38;
      } else if (eq("HOUR", var1, var3, var7)) {
         var8 = 39;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readI(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (var7 == 2) {
         switch (var1.charAt(var3 + 1) & 65503) {
            case 70:
               var8 = 40;
               break;
            case 78:
               var8 = 41;
               break;
            case 83:
               var8 = 45;
               break;
            default:
               var8 = 2;
         }
      } else if (eq("INNER", var1, var3, var7)) {
         var8 = 42;
      } else if (eq("INTERSECT", var1, var3, var7)) {
         var8 = 43;
      } else if (eq("INTERVAL", var1, var3, var7)) {
         var8 = 44;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readJ(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("JOIN", var1, var3, var7) ? 46 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readK(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("KEY", var1, var3, var7) ? 47 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readL(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8;
      if (eq("LEFT", var1, var3, var7)) {
         var8 = 48;
      } else if (eq("LIMIT", var1, var3, var7)) {
         var8 = this.provider.getMode().limit ? 50 : 2;
      } else if (eq("LIKE", var1, var3, var7)) {
         var8 = 49;
      } else if (eq("LOCALTIME", var1, var3, var7)) {
         var8 = 51;
      } else if (eq("LOCALTIMESTAMP", var1, var3, var7)) {
         var8 = 52;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readM(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8;
      if (eq("MINUS", var1, var3, var7)) {
         var8 = this.provider.getMode().minusIsExcept ? 53 : 2;
      } else if (eq("MINUTE", var1, var3, var7)) {
         var8 = 54;
      } else if (eq("MONTH", var1, var3, var7)) {
         var8 = 55;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readN(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("NOT", var1, var3, var7)) {
         var8 = 57;
      } else if (eq("NATURAL", var1, var3, var7)) {
         var8 = 56;
      } else if (eq("NULL", var1, var3, var7)) {
         var8 = 58;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readO(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (var7 == 2) {
         switch (var1.charAt(var3 + 1) & 65503) {
            case 78:
               var8 = 60;
               break;
            case 82:
               var8 = 61;
               break;
            default:
               var8 = 2;
         }
      } else if (eq("OFFSET", var1, var3, var7)) {
         var8 = 59;
      } else if (eq("ORDER", var1, var3, var7)) {
         var8 = 62;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readP(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("PRIMARY", var1, var3, var7) ? 63 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readQ(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("QUALIFY", var1, var3, var7) ? 64 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readR(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("RIGHT", var1, var3, var7)) {
         var8 = 65;
      } else if (eq("ROW", var1, var3, var7)) {
         var8 = 66;
      } else if (eq("ROWNUM", var1, var3, var7)) {
         var8 = 67;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readS(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("SECOND", var1, var3, var7)) {
         var8 = 68;
      } else if (eq("SELECT", var1, var3, var7)) {
         var8 = 69;
      } else if (eq("SESSION_USER", var1, var3, var7)) {
         var8 = 70;
      } else if (eq("SET", var1, var3, var7)) {
         var8 = 71;
      } else if (eq("SOME", var1, var3, var7)) {
         var8 = 72;
      } else if (eq("SYMMETRIC", var1, var3, var7)) {
         var8 = 73;
      } else if (eq("SYSTEM_USER", var1, var3, var7)) {
         var8 = 74;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readT(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8;
      if (var7 == 2) {
         var8 = (var1.charAt(var3 + 1) & '\uffdf') == 79 ? 76 : 2;
      } else if (eq("TABLE", var1, var3, var7)) {
         var8 = 75;
      } else if (eq("TRUE", var1, var3, var7)) {
         var8 = 77;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readU(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("UESCAPE", var1, var3, var7)) {
         var8 = 78;
      } else if (eq("UNION", var1, var3, var7)) {
         var8 = 79;
      } else if (eq("UNIQUE", var1, var3, var7)) {
         var8 = 80;
      } else if (eq("UNKNOWN", var1, var3, var7)) {
         var8 = 81;
      } else if (eq("USER", var1, var3, var7)) {
         var8 = 82;
      } else if (eq("USING", var1, var3, var7)) {
         var8 = 83;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readV(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("VALUE", var1, var3, var7)) {
         var8 = 84;
      } else if (eq("VALUES", var1, var3, var7)) {
         var8 = 85;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readW(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      byte var8;
      if (eq("WHEN", var1, var3, var7)) {
         var8 = 86;
      } else if (eq("WHERE", var1, var3, var7)) {
         var8 = 87;
      } else if (eq("WINDOW", var1, var3, var7)) {
         var8 = 88;
      } else if (eq("WITH", var1, var3, var7)) {
         var8 = 89;
      } else {
         var8 = 2;
      }

      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int readY(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3;
      int var8 = eq("YEAR", var1, var3, var7) ? 90 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var8);
   }

   private int read_(String var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6 = this.findIdentifierEnd(var1, var2, var4);
      int var7 = var6 - var3 == 7 && "_ROWID_".regionMatches(true, 1, var1, var3 + 1, 6) ? 91 : 2;
      return this.readIdentifierOrKeyword(var1, var3, var5, var6, var7);
   }

   private int readIdentifierOrKeyword(String var1, int var2, ArrayList<Token> var3, int var4, int var5) {
      Object var6;
      if (var5 == 2) {
         var6 = new Token.IdentifierToken(var2, this.extractIdentifier(var1, var2, var4), false, false);
      } else if (this.nonKeywords != null && this.nonKeywords.get(var5)) {
         var6 = new Token.KeywordOrIdentifierToken(var2, var5, this.extractIdentifier(var1, var2, var4));
      } else {
         var6 = new Token.KeywordToken(var2, var5);
      }

      var3.add(var6);
      return var4;
   }

   private static boolean eq(String var0, String var1, int var2, int var3) {
      if (var3 != var0.length()) {
         return false;
      } else {
         for(int var4 = 1; var4 < var3; ++var4) {
            char var10000 = var0.charAt(var4);
            ++var2;
            if (var10000 != (var1.charAt(var2) & '\uffdf')) {
               return false;
            }
         }

         return true;
      }
   }

   private int findIdentifierEnd(String var1, int var2, int var3) {
      ++var3;

      int var4;
      while(var3 <= var2 && (Character.isJavaIdentifierPart(var4 = var1.codePointAt(var3)) || var4 == 35 && this.provider.getMode().supportPoundSymbolForColumnNames)) {
         var3 += Character.charCount(var4);
      }

      return var3;
   }

   private String extractIdentifier(String var1, int var2, int var3) {
      return this.convertCase(var1.substring(var2, var3));
   }

   private int readQuotedIdentifier(String var1, int var2, int var3, int var4, char var5, boolean var6, ArrayList<Token> var7) {
      ++var4;
      int var8 = var1.indexOf(var5, var4);
      if (var8 < 0) {
         throw DbException.getSyntaxError(var1, var3);
      } else {
         String var9 = var1.substring(var4, var8);
         var4 = var8 + 1;
         if (var4 <= var2 && var1.charAt(var4) == var5) {
            StringBuilder var10 = new StringBuilder(var9);

            do {
               var8 = var1.indexOf(var5, var4 + 1);
               if (var8 < 0) {
                  throw DbException.getSyntaxError(var1, var3);
               }

               var10.append(var1, var4, var8);
               var4 = var8 + 1;
            } while(var4 <= var2 && var1.charAt(var4) == var5);

            var9 = var10.toString();
         }

         if (var5 == '`') {
            var9 = this.convertCase(var9);
         }

         var7.add(new Token.IdentifierToken(var3, var9, true, var6));
         return var4;
      }
   }

   private String convertCase(String var1) {
      if (this.identifiersToUpper) {
         var1 = StringUtils.toUpperEnglish(var1);
      } else if (this.identifiersToLower) {
         var1 = StringUtils.toLowerEnglish(var1);
      }

      return var1;
   }

   private static int readBinaryString(String var0, int var1, int var2, int var3, ArrayList<Token> var4) {
      ByteArrayOutputStream var5 = new ByteArrayOutputStream();

      do {
         ++var3;
         int var6 = var0.indexOf(39, var3);
         if (var6 < 0 || var6 < var2 && var0.charAt(var6 + 1) == '\'') {
            throw DbException.getSyntaxError(var0, var1);
         }

         StringUtils.convertHexWithSpacesToBytes(var5, var0, var3, var6);
         var3 = skipWhitespace(var0, var2, var6 + 1);
      } while(var3 <= var2 && var0.charAt(var3) == '\'');

      var4.add(new Token.BinaryStringToken(var1, var5.toByteArray()));
      return var3;
   }

   private static int readCharacterString(String var0, int var1, int var2, int var3, boolean var4, ArrayList<Token> var5) {
      String var6 = null;
      StringBuilder var7 = null;

      do {
         ++var3;
         int var8 = var0.indexOf(39, var3);
         if (var8 < 0) {
            throw DbException.getSyntaxError(var0, var1);
         }

         if (var6 == null) {
            var6 = var0.substring(var3, var8);
         } else {
            if (var7 == null) {
               var7 = new StringBuilder(var6);
            }

            var7.append(var0, var3, var8);
         }

         var3 = var8 + 1;
         if (var3 <= var2 && var0.charAt(var3) == '\'') {
            if (var7 == null) {
               var7 = new StringBuilder(var6);
            }

            do {
               var8 = var0.indexOf(39, var3 + 1);
               if (var8 < 0) {
                  throw DbException.getSyntaxError(var0, var1);
               }

               var7.append(var0, var3, var8);
               var3 = var8 + 1;
            } while(var3 <= var2 && var0.charAt(var3) == '\'');
         }

         var3 = skipWhitespace(var0, var2, var3);
      } while(var3 <= var2 && var0.charAt(var3) == '\'');

      if (var7 != null) {
         var6 = var7.toString();
      }

      var5.add(new Token.CharacterStringToken(var1, var6, var4));
      return var3;
   }

   private static int skipWhitespace(String var0, int var1, int var2) {
      while(true) {
         if (var2 <= var1) {
            int var3 = var0.codePointAt(var2);
            if (Character.isWhitespace(var3)) {
               var2 += Character.charCount(var3);
               continue;
            }

            if (var3 == 47 && var2 < var1) {
               char var4 = var0.charAt(var2 + 1);
               if (var4 == '*') {
                  var2 = skipBracketedComment(var0, var2, var1, var2);
                  continue;
               }

               if (var4 == '/') {
                  var2 = skipSimpleComment(var0, var1, var2);
                  continue;
               }
            }
         }

         return var2;
      }
   }

   private static int readHexNumber(String var0, CastDataProvider var1, int var2, int var3, int var4, ArrayList<Token> var5) {
      int var6;
      if (var1.getMode().zeroExLiteralsAreBinaryStrings) {
         char var11;
         for(var6 = var4; var4 <= var3 && ((var11 = var0.charAt(var4)) >= '0' && var11 <= '9' || (var11 &= '\uffdf') >= 'A' && var11 <= 'F'); ++var4) {
         }

         if (var4 <= var3 && Character.isJavaIdentifierPart(var0.codePointAt(var4))) {
            throw DbException.get(90004, var0.substring(var6, var4 + 1));
         } else {
            var5.add(new Token.BinaryStringToken(var6, StringUtils.convertHexToBytes(var0.substring(var6, var4))));
            return var4;
         }
      } else if (var4 > var3) {
         throw DbException.getSyntaxError(var0, var2, "Hex number");
      } else {
         var6 = var4;
         long var7 = 0L;

         char var9;
         do {
            var9 = var0.charAt(var4);
            if (var9 >= '0' && var9 <= '9') {
               var7 = (var7 << 4) + (long)var9 - 48L;
            } else {
               if ((var9 &= '\uffdf') < 'A' || var9 > 'F') {
                  if (var4 == var6) {
                     throw DbException.getSyntaxError(var0, var2, "Hex number");
                  }
                  break;
               }

               var7 = (var7 << 4) + (long)var9 - 55L;
            }

            if (var7 > 2147483647L) {
               do {
                  ++var4;
               } while(var4 <= var3 && ((var9 = var0.charAt(var4)) >= '0' && var9 <= '9' || (var9 &= '\uffdf') >= 'A' && var9 <= 'F'));

               return finishBigInteger(var0, var2, var3, var4, var6, var4 <= var3 && var9 == 'L', 16, var5);
            }

            ++var4;
         } while(var4 <= var3);

         boolean var10 = var4 <= var3 && var9 == 'L';
         if (var10) {
            ++var4;
         }

         if (var4 <= var3 && Character.isJavaIdentifierPart(var0.codePointAt(var4))) {
            throw DbException.getSyntaxError(var0, var2, "Hex number");
         } else {
            var5.add(var10 ? new Token.BigintToken(var6, var7) : new Token.IntegerToken(var6, (int)var7));
            return var4;
         }
      }
   }

   private static int readNumeric(String var0, int var1, int var2, int var3, char var4, ArrayList<Token> var5) {
      long var6;
      label28:
      for(var6 = (long)(var4 - 48); var3 <= var2; ++var3) {
         var4 = var0.charAt(var3);
         if (var4 < '0' || var4 > '9') {
            switch (var4) {
               case '.':
                  return readNumeric(var0, var1, var2, var3, var4, false, false, var5);
               case 'E':
               case 'e':
                  return readNumeric(var0, var1, var2, var3, var4, false, true, var5);
               case 'L':
               case 'l':
                  return finishBigInteger(var0, var1, var2, var3, var1, true, 10, var5);
               default:
                  break label28;
            }
         }

         var6 = var6 * 10L + (long)(var4 - 48);
         if (var6 > 2147483647L) {
            return readNumeric(var0, var1, var2, var3, var4, true, false, var5);
         }
      }

      var5.add(new Token.IntegerToken(var1, (int)var6));
      return var3;
   }

   private static int readNumeric(String var0, int var1, int var2, int var3, char var4, boolean var5, boolean var6, ArrayList<Token> var7) {
      if (!var6) {
         while(true) {
            ++var3;
            if (var3 > var2) {
               break;
            }

            var4 = var0.charAt(var3);
            if (var4 == '.') {
               var5 = false;
            } else if (var4 < '0' || var4 > '9') {
               break;
            }
         }
      }

      if (var3 <= var2 && (var4 == 'E' || var4 == 'e')) {
         var5 = false;
         var6 = true;
         if (var3 == var2) {
            throw DbException.getSyntaxError(var0, var1);
         }

         ++var3;
         var4 = var0.charAt(var3);
         if (var4 == '+' || var4 == '-') {
            if (var3 == var2) {
               throw DbException.getSyntaxError(var0, var1);
            }

            ++var3;
            var4 = var0.charAt(var3);
         }

         if (var4 < '0' || var4 > '9') {
            throw DbException.getSyntaxError(var0, var1);
         }

         do {
            ++var3;
         } while(var3 <= var2 && (var4 = var0.charAt(var3)) >= '0' && var4 <= '9');
      }

      if (!var5) {
         String var9 = var0.substring(var1, var3);

         BigDecimal var8;
         try {
            var8 = new BigDecimal(var9);
         } catch (NumberFormatException var11) {
            throw DbException.get(22018, var11, var9);
         }

         var7.add(new Token.ValueToken(var1, (Value)(var6 ? ValueDecfloat.get(var8) : ValueNumeric.get(var8))));
         return var3;
      } else {
         return finishBigInteger(var0, var1, var2, var3, var1, var3 < var2 && var4 == 'L' || var4 == 'l', 10, var7);
      }
   }

   private static int finishBigInteger(String var0, int var1, int var2, int var3, int var4, boolean var5, int var6, ArrayList<Token> var7) {
      if (var5) {
         ++var3;
      }

      if (var6 == 16 && var3 <= var2 && Character.isJavaIdentifierPart(var0.codePointAt(var3))) {
         throw DbException.getSyntaxError(var0, var1, "Hex number");
      } else {
         BigInteger var9 = new BigInteger(var0.substring(var4, var3), var6);
         Object var10;
         if (var9.compareTo(ValueBigint.MAX_BI) > 0) {
            if (var5) {
               throw DbException.getSyntaxError(var0, var1);
            }

            var10 = new Token.ValueToken(var1, ValueNumeric.get(var9));
         } else {
            var10 = new Token.BigintToken(var4, var9.longValue());
         }

         var7.add(var10);
         return var3;
      }
   }

   private static int skipBracketedComment(String var0, int var1, int var2, int var3) {
      var3 += 2;

      label27:
      for(int var4 = 1; var4 > 0; ++var3) {
         while(var3 < var2) {
            char var5 = var0.charAt(var3++);
            if (var5 == '*') {
               if (var0.charAt(var3) == '/') {
                  --var4;
                  continue label27;
               }
            } else if (var5 == '/' && var0.charAt(var3) == '*') {
               ++var4;
               ++var3;
            }
         }

         throw DbException.getSyntaxError(var0, var1);
      }

      return var3;
   }

   private static int skipSimpleComment(String var0, int var1, int var2) {
      char var3;
      for(var2 += 2; var2 <= var1 && (var3 = var0.charAt(var2)) != '\n' && var3 != '\r'; ++var2) {
      }

      return var2;
   }

   private static int parseParameterIndex(String var0, int var1, int var2, ArrayList<Token> var3) {
      int var4 = var2;
      long var5 = 0L;

      do {
         ++var2;
         char var7;
         if (var2 > var1 || (var7 = var0.charAt(var2)) < '0' || var7 > '9') {
            if (var2 > var4 + 1 && var5 == 0L) {
               throw DbException.getInvalidValueException("parameter index", var5);
            } else {
               var3.add(new Token.ParameterToken(var4, (int)var5));
               return var2;
            }
         }

         var5 = var5 * 10L + (long)(var7 - 48);
      } while(var5 <= 2147483647L);

      throw DbException.getInvalidValueException("parameter index", var5);
   }

   private static int assignParameterIndex(ArrayList<Token> var0, int var1) {
      Token.ParameterToken var2 = (Token.ParameterToken)var0.get(var0.size() - 1);
      if (var2.index == 0) {
         if (var1 < 0) {
            throw DbException.get(90123);
         }

         ++var1;
         var2.index = var1;
      } else {
         if (var1 > 0) {
            throw DbException.get(90123);
         }

         var1 = -1;
      }

      return var1;
   }

   private static void processUescape(String var0, ArrayList<Token> var1) {
      ListIterator var2 = var1.listIterator();

      Token var5;
      while(true) {
         Token var3;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (Token)var2.next();
         } while(!var3.needsUnicodeConversion());

         int var4 = 92;
         if (var2.hasNext()) {
            var5 = (Token)var2.next();
            if (var5.tokenType() == 78) {
               var2.remove();
               if (!var2.hasNext()) {
                  break;
               }

               Token var6 = (Token)var2.next();
               var2.remove();
               if (!(var6 instanceof Token.CharacterStringToken)) {
                  break;
               }

               String var7 = ((Token.CharacterStringToken)var6).string;
               if (var7.codePointCount(0, var7.length()) != 1) {
                  break;
               }

               int var8 = var7.codePointAt(0);
               if (Character.isWhitespace(var8) || var8 >= 48 && var8 <= 57 || var8 >= 65 && var8 <= 70 || var8 >= 97 && var8 <= 102) {
                  break;
               }

               switch (var8) {
                  case 34:
                  case 39:
                  case 43:
                     throw DbException.getSyntaxError(var0, var5.start() + 7, "'<Unicode escape character>'");
                  default:
                     var4 = var8;
               }
            }
         }

         var3.convertUnicode(var4);
      }

      throw DbException.getSyntaxError(var0, var5.start() + 7, "'<Unicode escape character>'");
   }
}
