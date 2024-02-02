/*      */ package org.h2.command;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.BitSet;
/*      */ import java.util.ListIterator;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueDecfloat;
/*      */ import org.h2.value.ValueNumeric;
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
/*      */ public final class Tokenizer
/*      */ {
/*      */   private final CastDataProvider provider;
/*      */   private final boolean identifiersToUpper;
/*      */   private final boolean identifiersToLower;
/*      */   private final BitSet nonKeywords;
/*      */   
/*      */   Tokenizer(CastDataProvider paramCastDataProvider, boolean paramBoolean1, boolean paramBoolean2, BitSet paramBitSet) {
/*  156 */     this.provider = paramCastDataProvider;
/*  157 */     this.identifiersToUpper = paramBoolean1;
/*  158 */     this.identifiersToLower = paramBoolean2;
/*  159 */     this.nonKeywords = paramBitSet;
/*      */   }
/*      */   
/*      */   ArrayList<Token> tokenize(String paramString, boolean paramBoolean) {
/*  163 */     ArrayList<Token> arrayList = new ArrayList();
/*  164 */     int i = paramString.length() - 1;
/*  165 */     boolean bool = false;
/*  166 */     int j = 0;
/*  167 */     for (int k = 0; k <= i; ) {
/*  168 */       Token.ParameterToken parameterToken; Token.KeywordToken keywordToken; boolean bool1; int m = k;
/*  169 */       char c = paramString.charAt(k);
/*      */       
/*  171 */       switch (c) {
/*      */         case '!':
/*  173 */           if (k < i) {
/*  174 */             char c1 = paramString.charAt(++k);
/*  175 */             if (c1 == '=') {
/*  176 */               Token.KeywordToken keywordToken1 = new Token.KeywordToken(m, 100);
/*      */               break;
/*      */             } 
/*  179 */             if (c1 == '~') {
/*  180 */               Token.KeywordToken keywordToken1 = new Token.KeywordToken(m, 122);
/*      */               break;
/*      */             } 
/*      */           } 
/*  184 */           throw DbException.getSyntaxError(paramString, m);
/*      */         case '"':
/*      */         case '`':
/*  187 */           k = readQuotedIdentifier(paramString, i, m, k, c, false, arrayList);
/*      */           continue;
/*      */         case '#':
/*  190 */           if ((this.provider.getMode()).supportPoundSymbolForColumnNames) {
/*  191 */             k = readIdentifier(paramString, i, m, k, c, arrayList);
/*      */             continue;
/*      */           } 
/*  194 */           throw DbException.getSyntaxError(paramString, m);
/*      */         case '$':
/*  196 */           if (k < i) {
/*  197 */             char c1 = paramString.charAt(k + 1);
/*  198 */             if (c1 == '$') {
/*  199 */               k += 2;
/*  200 */               int n = paramString.indexOf("$$", k);
/*  201 */               if (n < 0) {
/*  202 */                 throw DbException.getSyntaxError(paramString, m);
/*      */               }
/*  204 */               Token.CharacterStringToken characterStringToken = new Token.CharacterStringToken(m, paramString.substring(k, n), false);
/*  205 */               k = n + 1; break;
/*      */             } 
/*  207 */             k = parseParameterIndex(paramString, i, k, arrayList);
/*  208 */             j = assignParameterIndex(arrayList, j);
/*      */             
/*      */             continue;
/*      */           } 
/*  212 */           parameterToken = new Token.ParameterToken(m, 0);
/*      */           break;
/*      */         
/*      */         case '%':
/*  216 */           keywordToken = new Token.KeywordToken(m, 114);
/*      */           break;
/*      */         case '&':
/*  219 */           if (k < i && paramString.charAt(k + 1) == '&') {
/*  220 */             k++;
/*  221 */             keywordToken = new Token.KeywordToken(m, 107);
/*      */             break;
/*      */           } 
/*  224 */           throw DbException.getSyntaxError(paramString, m);
/*      */         case '\'':
/*  226 */           k = readCharacterString(paramString, m, i, k, false, arrayList);
/*      */           continue;
/*      */         case '(':
/*  229 */           keywordToken = new Token.KeywordToken(m, 105);
/*      */           break;
/*      */         case ')':
/*  232 */           keywordToken = new Token.KeywordToken(m, 106);
/*  233 */           if (paramBoolean) {
/*  234 */             arrayList.add(keywordToken);
/*  235 */             i = skipWhitespace(paramString, i, k + 1) - 1;
/*      */             break;
/*      */           } 
/*      */           break;
/*      */         case '*':
/*  240 */           keywordToken = new Token.KeywordToken(m, 108);
/*      */           break;
/*      */         case '+':
/*  243 */           keywordToken = new Token.KeywordToken(m, 103);
/*      */           break;
/*      */         case ',':
/*  246 */           keywordToken = new Token.KeywordToken(m, 109);
/*      */           break;
/*      */         case '-':
/*  249 */           if (k < i && paramString.charAt(k + 1) == '-') {
/*  250 */             k = skipSimpleComment(paramString, i, k);
/*      */             continue;
/*      */           } 
/*  253 */           keywordToken = new Token.KeywordToken(m, 102);
/*      */           break;
/*      */         
/*      */         case '.':
/*  257 */           if (k < i) {
/*  258 */             char c1 = paramString.charAt(k + 1);
/*  259 */             if (c1 >= '0' && c1 <= '9') {
/*  260 */               k = readNumeric(paramString, m, i, k + 1, c1, false, false, arrayList);
/*      */               continue;
/*      */             } 
/*      */           } 
/*  264 */           keywordToken = new Token.KeywordToken(m, 110);
/*      */           break;
/*      */         case '/':
/*  267 */           if (k < i) {
/*  268 */             char c1 = paramString.charAt(k + 1);
/*  269 */             if (c1 == '*') {
/*  270 */               k = skipBracketedComment(paramString, m, i, k); continue;
/*      */             } 
/*  272 */             if (c1 == '/') {
/*  273 */               k = skipSimpleComment(paramString, i, k);
/*      */               continue;
/*      */             } 
/*      */           } 
/*  277 */           keywordToken = new Token.KeywordToken(m, 113);
/*      */           break;
/*      */         case '0':
/*  280 */           if (k < i) {
/*  281 */             char c1 = paramString.charAt(k + 1);
/*  282 */             if (c1 == 'X' || c1 == 'x') {
/*  283 */               k = readHexNumber(paramString, this.provider, m, i, k + 2, arrayList);
/*      */               continue;
/*      */             } 
/*      */           } 
/*      */         
/*      */         case '1':
/*      */         case '2':
/*      */         case '3':
/*      */         case '4':
/*      */         case '5':
/*      */         case '6':
/*      */         case '7':
/*      */         case '8':
/*      */         case '9':
/*  297 */           k = readNumeric(paramString, m, i, k + 1, c, arrayList);
/*      */           continue;
/*      */         case ':':
/*  300 */           if (k < i) {
/*  301 */             char c1 = paramString.charAt(k + 1);
/*  302 */             if (c1 == ':') {
/*  303 */               k++;
/*  304 */               keywordToken = new Token.KeywordToken(m, 120); break;
/*      */             } 
/*  306 */             if (c1 == '=') {
/*  307 */               k++;
/*  308 */               keywordToken = new Token.KeywordToken(m, 121);
/*      */               break;
/*      */             } 
/*      */           } 
/*  312 */           keywordToken = new Token.KeywordToken(m, 116);
/*      */           break;
/*      */         case ';':
/*  315 */           keywordToken = new Token.KeywordToken(m, 115);
/*      */           break;
/*      */         case '<':
/*  318 */           if (k < i) {
/*  319 */             char c1 = paramString.charAt(k + 1);
/*  320 */             if (c1 == '=') {
/*  321 */               k++;
/*  322 */               keywordToken = new Token.KeywordToken(m, 99);
/*      */               break;
/*      */             } 
/*  325 */             if (c1 == '>') {
/*  326 */               k++;
/*  327 */               keywordToken = new Token.KeywordToken(m, 100);
/*      */               break;
/*      */             } 
/*      */           } 
/*  331 */           keywordToken = new Token.KeywordToken(m, 98);
/*      */           break;
/*      */         case '=':
/*  334 */           keywordToken = new Token.KeywordToken(m, 95);
/*      */           break;
/*      */         case '>':
/*  337 */           if (k < i && paramString.charAt(k + 1) == '=') {
/*  338 */             k++;
/*  339 */             keywordToken = new Token.KeywordToken(m, 96);
/*      */             break;
/*      */           } 
/*  342 */           keywordToken = new Token.KeywordToken(m, 97);
/*      */           break;
/*      */         case '?':
/*  345 */           if (k + 1 < i && paramString.charAt(k + 1) == '?') {
/*  346 */             char c1 = paramString.charAt(k + 2);
/*  347 */             if (c1 == '(') {
/*  348 */               k += 2;
/*  349 */               keywordToken = new Token.KeywordToken(m, 117);
/*      */               break;
/*      */             } 
/*  352 */             if (c1 == ')') {
/*  353 */               k += 2;
/*  354 */               keywordToken = new Token.KeywordToken(m, 118);
/*      */               break;
/*      */             } 
/*      */           } 
/*  358 */           k = parseParameterIndex(paramString, i, k, arrayList);
/*  359 */           j = assignParameterIndex(arrayList, j);
/*      */           continue;
/*      */         
/*      */         case '@':
/*  363 */           keywordToken = new Token.KeywordToken(m, 101);
/*      */           break;
/*      */         case 'A':
/*      */         case 'a':
/*  367 */           k = readA(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'B':
/*      */         case 'b':
/*  371 */           k = readB(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'C':
/*      */         case 'c':
/*  375 */           k = readC(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'D':
/*      */         case 'd':
/*  379 */           k = readD(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'E':
/*      */         case 'e':
/*  383 */           k = readE(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'F':
/*      */         case 'f':
/*  387 */           k = readF(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'G':
/*      */         case 'g':
/*  391 */           k = readG(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'H':
/*      */         case 'h':
/*  395 */           k = readH(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'I':
/*      */         case 'i':
/*  399 */           k = readI(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'J':
/*      */         case 'j':
/*  403 */           k = readJ(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'K':
/*      */         case 'k':
/*  407 */           k = readK(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'L':
/*      */         case 'l':
/*  411 */           k = readL(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'M':
/*      */         case 'm':
/*  415 */           k = readM(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'N':
/*      */         case 'n':
/*  419 */           if (k < i && paramString.charAt(k + 1) == '\'') {
/*  420 */             k = readCharacterString(paramString, m, i, k + 1, false, arrayList); continue;
/*      */           } 
/*  422 */           k = readN(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         
/*      */         case 'O':
/*      */         case 'o':
/*  427 */           k = readO(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'P':
/*      */         case 'p':
/*  431 */           k = readP(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'Q':
/*      */         case 'q':
/*  435 */           k = readQ(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'R':
/*      */         case 'r':
/*  439 */           k = readR(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'S':
/*      */         case 's':
/*  443 */           k = readS(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'T':
/*      */         case 't':
/*  447 */           k = readT(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'U':
/*      */         case 'u':
/*  451 */           if (k + 1 < i && paramString.charAt(k + 1) == '&') {
/*  452 */             char c1 = paramString.charAt(k + 2);
/*  453 */             if (c1 == '"') {
/*  454 */               k = readQuotedIdentifier(paramString, i, m, k + 2, '"', true, arrayList);
/*  455 */               bool = true; continue;
/*      */             } 
/*  457 */             if (c1 == '\'') {
/*  458 */               k = readCharacterString(paramString, m, i, k + 2, true, arrayList);
/*  459 */               bool = true;
/*      */               continue;
/*      */             } 
/*      */           } 
/*  463 */           k = readU(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'V':
/*      */         case 'v':
/*  467 */           k = readV(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'W':
/*      */         case 'w':
/*  471 */           k = readW(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'X':
/*      */         case 'x':
/*  475 */           if (k < i && paramString.charAt(k + 1) == '\'') {
/*  476 */             k = readBinaryString(paramString, m, i, k + 1, arrayList); continue;
/*      */           } 
/*  478 */           k = readIdentifier(paramString, i, m, k, c, arrayList);
/*      */           continue;
/*      */         
/*      */         case 'Y':
/*      */         case 'y':
/*  483 */           k = readY(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case 'Z':
/*      */         case 'z':
/*  487 */           k = readIdentifier(paramString, i, m, k, c, arrayList);
/*      */           continue;
/*      */         case '[':
/*  490 */           if ((this.provider.getMode()).squareBracketQuotedNames) {
/*  491 */             int n = paramString.indexOf(']', ++k);
/*  492 */             if (n < 0) {
/*  493 */               throw DbException.getSyntaxError(paramString, m);
/*      */             }
/*  495 */             Token.IdentifierToken identifierToken = new Token.IdentifierToken(m, paramString.substring(k, n), true, false);
/*  496 */             k = n; break;
/*      */           } 
/*  498 */           keywordToken = new Token.KeywordToken(m, 117);
/*      */           break;
/*      */         
/*      */         case ']':
/*  502 */           keywordToken = new Token.KeywordToken(m, 118);
/*      */           break;
/*      */         case '_':
/*  505 */           k = read_(paramString, i, m, k, arrayList);
/*      */           continue;
/*      */         case '{':
/*  508 */           keywordToken = new Token.KeywordToken(m, 111);
/*      */           break;
/*      */         case '|':
/*  511 */           if (k < i && paramString.charAt(++k) == '|') {
/*  512 */             keywordToken = new Token.KeywordToken(m, 104);
/*      */             break;
/*      */           } 
/*  515 */           throw DbException.getSyntaxError(paramString, m);
/*      */         case '}':
/*  517 */           keywordToken = new Token.KeywordToken(m, 112);
/*      */           break;
/*      */         case '~':
/*  520 */           keywordToken = new Token.KeywordToken(m, 119);
/*      */           break;
/*      */         default:
/*  523 */           if (c <= ' ') {
/*  524 */             k++;
/*      */             continue;
/*      */           } 
/*  527 */           bool1 = Character.isHighSurrogate(c) ? paramString.codePointAt(k++) : c;
/*  528 */           if (Character.isSpaceChar(bool1)) {
/*      */             continue;
/*      */           }
/*  531 */           if (Character.isJavaIdentifierStart(bool1)) {
/*  532 */             k = readIdentifier(paramString, i, m, k, bool1, arrayList);
/*      */             continue;
/*      */           } 
/*  535 */           throw DbException.getSyntaxError(paramString, m);
/*      */       } 
/*      */       
/*  538 */       arrayList.add(keywordToken);
/*  539 */       k++;
/*      */     } 
/*  541 */     if (bool) {
/*  542 */       processUescape(paramString, arrayList);
/*      */     }
/*  544 */     arrayList.add(new Token.EndOfInputToken(i + 1));
/*  545 */     return arrayList;
/*      */   }
/*      */   
/*      */   private int readIdentifier(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, ArrayList<Token> paramArrayList) {
/*  549 */     if (paramInt4 >= 65536) {
/*  550 */       paramInt3++;
/*      */     }
/*  552 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3 + Character.charCount(paramInt4) - 1);
/*  553 */     paramArrayList.add(new Token.IdentifierToken(paramInt2, extractIdentifier(paramString, paramInt2, i), false, false));
/*  554 */     return i;
/*      */   }
/*      */   private int readA(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  558 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  559 */     int j = i - paramInt2;
/*      */     
/*  561 */     if (j == 2) {
/*  562 */       b = ((paramString.charAt(paramInt2 + 1) & 0xFFDF) == 83) ? 7 : 2;
/*      */     }
/*  564 */     else if (eq("ALL", paramString, paramInt2, j)) {
/*  565 */       b = 3;
/*  566 */     } else if (eq("AND", paramString, paramInt2, j)) {
/*  567 */       b = 4;
/*  568 */     } else if (eq("ANY", paramString, paramInt2, j)) {
/*  569 */       b = 5;
/*  570 */     } else if (eq("ARRAY", paramString, paramInt2, j)) {
/*  571 */       b = 6;
/*  572 */     } else if (eq("ASYMMETRIC", paramString, paramInt2, j)) {
/*  573 */       b = 8;
/*  574 */     } else if (eq("AUTHORIZATION", paramString, paramInt2, j)) {
/*  575 */       b = 9;
/*      */     } else {
/*  577 */       b = 2;
/*      */     } 
/*      */     
/*  580 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readB(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  584 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  585 */     int j = i - paramInt2;
/*  586 */     byte b = eq("BETWEEN", paramString, paramInt2, j) ? 10 : 2;
/*  587 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readC(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  591 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  592 */     int j = i - paramInt2;
/*      */     
/*  594 */     if (eq("CASE", paramString, paramInt2, j)) {
/*  595 */       b = 11;
/*  596 */     } else if (eq("CAST", paramString, paramInt2, j)) {
/*  597 */       b = 12;
/*  598 */     } else if (eq("CHECK", paramString, paramInt2, j)) {
/*  599 */       b = 13;
/*  600 */     } else if (eq("CONSTRAINT", paramString, paramInt2, j)) {
/*  601 */       b = 14;
/*  602 */     } else if (eq("CROSS", paramString, paramInt2, j)) {
/*  603 */       b = 15;
/*  604 */     } else if (j >= 12 && eq("CURRENT_", paramString, paramInt2, 8)) {
/*  605 */       b = getTokenTypeCurrent(paramString, paramInt2, j);
/*      */     } else {
/*  607 */       b = 2;
/*      */     } 
/*  609 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private static int getTokenTypeCurrent(String paramString, int paramInt1, int paramInt2) {
/*  613 */     paramInt1 += 8;
/*  614 */     switch (paramInt2) {
/*      */       case 12:
/*  616 */         if (eqCurrent("CURRENT_DATE", paramString, paramInt1, paramInt2))
/*  617 */           return 17; 
/*  618 */         if (eqCurrent("CURRENT_PATH", paramString, paramInt1, paramInt2))
/*  619 */           return 18; 
/*  620 */         if (eqCurrent("CURRENT_ROLE", paramString, paramInt1, paramInt2))
/*  621 */           return 19; 
/*  622 */         if (eqCurrent("CURRENT_TIME", paramString, paramInt1, paramInt2))
/*  623 */           return 21; 
/*  624 */         if (eqCurrent("CURRENT_USER", paramString, paramInt1, paramInt2)) {
/*  625 */           return 23;
/*      */         }
/*      */         break;
/*      */       case 14:
/*  629 */         if (eqCurrent("CURRENT_SCHEMA", paramString, paramInt1, paramInt2)) {
/*  630 */           return 20;
/*      */         }
/*      */         break;
/*      */       case 15:
/*  634 */         if (eqCurrent("CURRENT_CATALOG", paramString, paramInt1, paramInt2)) {
/*  635 */           return 16;
/*      */         }
/*      */         break;
/*      */       case 17:
/*  639 */         if (eqCurrent("CURRENT_TIMESTAMP", paramString, paramInt1, paramInt2))
/*  640 */           return 22; 
/*      */         break;
/*      */     } 
/*  643 */     return 2;
/*      */   }
/*      */   
/*      */   private static boolean eqCurrent(String paramString1, String paramString2, int paramInt1, int paramInt2) {
/*  647 */     for (byte b = 8; b < paramInt2; b++) {
/*  648 */       if (paramString1.charAt(b) != (paramString2.charAt(paramInt1++) & 0xFFDF)) {
/*  649 */         return false;
/*      */       }
/*      */     } 
/*  652 */     return true;
/*      */   }
/*      */   private int readD(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  656 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  657 */     int j = i - paramInt2;
/*      */     
/*  659 */     if (eq("DAY", paramString, paramInt2, j)) {
/*  660 */       b = 24;
/*  661 */     } else if (eq("DEFAULT", paramString, paramInt2, j)) {
/*  662 */       b = 25;
/*  663 */     } else if (eq("DISTINCT", paramString, paramInt2, j)) {
/*  664 */       b = 26;
/*      */     } else {
/*  666 */       b = 2;
/*      */     } 
/*  668 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readE(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  672 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  673 */     int j = i - paramInt2;
/*      */     
/*  675 */     if (eq("ELSE", paramString, paramInt2, j)) {
/*  676 */       b = 27;
/*  677 */     } else if (eq("END", paramString, paramInt2, j)) {
/*  678 */       b = 28;
/*  679 */     } else if (eq("EXCEPT", paramString, paramInt2, j)) {
/*  680 */       b = 29;
/*  681 */     } else if (eq("EXISTS", paramString, paramInt2, j)) {
/*  682 */       b = 30;
/*      */     } else {
/*  684 */       b = 2;
/*      */     } 
/*  686 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readF(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  690 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  691 */     int j = i - paramInt2;
/*      */     
/*  693 */     if (eq("FETCH", paramString, paramInt2, j)) {
/*  694 */       b = 32;
/*  695 */     } else if (eq("FROM", paramString, paramInt2, j)) {
/*  696 */       b = 35;
/*  697 */     } else if (eq("FOR", paramString, paramInt2, j)) {
/*  698 */       b = 33;
/*  699 */     } else if (eq("FOREIGN", paramString, paramInt2, j)) {
/*  700 */       b = 34;
/*  701 */     } else if (eq("FULL", paramString, paramInt2, j)) {
/*  702 */       b = 36;
/*  703 */     } else if (eq("FALSE", paramString, paramInt2, j)) {
/*  704 */       b = 31;
/*      */     } else {
/*  706 */       b = 2;
/*      */     } 
/*  708 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readG(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  712 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  713 */     int j = i - paramInt2;
/*  714 */     byte b = eq("GROUP", paramString, paramInt2, j) ? 37 : 2;
/*  715 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readH(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  719 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  720 */     int j = i - paramInt2;
/*      */     
/*  722 */     if (eq("HAVING", paramString, paramInt2, j)) {
/*  723 */       b = 38;
/*  724 */     } else if (eq("HOUR", paramString, paramInt2, j)) {
/*  725 */       b = 39;
/*      */     } else {
/*  727 */       b = 2;
/*      */     } 
/*  729 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readI(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  733 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  734 */     int j = i - paramInt2;
/*      */     
/*  736 */     if (j == 2)
/*  737 */     { switch (paramString.charAt(paramInt2 + 1) & 0xFFDF)
/*      */       { case 70:
/*  739 */           b = 40;
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
/*  761 */           return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);case 78: b = 41; return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);case 83: b = 45; return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b); }  b = 2; } else if (eq("INNER", paramString, paramInt2, j)) { b = 42; } else if (eq("INTERSECT", paramString, paramInt2, j)) { b = 43; } else if (eq("INTERVAL", paramString, paramInt2, j)) { b = 44; } else { b = 2; }  return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readJ(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  765 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  766 */     int j = i - paramInt2;
/*  767 */     byte b = eq("JOIN", paramString, paramInt2, j) ? 46 : 2;
/*  768 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readK(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  772 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  773 */     int j = i - paramInt2;
/*  774 */     byte b = eq("KEY", paramString, paramInt2, j) ? 47 : 2;
/*  775 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readL(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  779 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  780 */     int j = i - paramInt2;
/*      */     
/*  782 */     if (eq("LEFT", paramString, paramInt2, j)) {
/*  783 */       b = 48;
/*  784 */     } else if (eq("LIMIT", paramString, paramInt2, j)) {
/*  785 */       b = (this.provider.getMode()).limit ? 50 : 2;
/*  786 */     } else if (eq("LIKE", paramString, paramInt2, j)) {
/*  787 */       b = 49;
/*  788 */     } else if (eq("LOCALTIME", paramString, paramInt2, j)) {
/*  789 */       b = 51;
/*  790 */     } else if (eq("LOCALTIMESTAMP", paramString, paramInt2, j)) {
/*  791 */       b = 52;
/*      */     } else {
/*  793 */       b = 2;
/*      */     } 
/*  795 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readM(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  799 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  800 */     int j = i - paramInt2;
/*      */     
/*  802 */     if (eq("MINUS", paramString, paramInt2, j)) {
/*  803 */       b = (this.provider.getMode()).minusIsExcept ? 53 : 2;
/*  804 */     } else if (eq("MINUTE", paramString, paramInt2, j)) {
/*  805 */       b = 54;
/*  806 */     } else if (eq("MONTH", paramString, paramInt2, j)) {
/*  807 */       b = 55;
/*      */     } else {
/*  809 */       b = 2;
/*      */     } 
/*  811 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readN(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  815 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  816 */     int j = i - paramInt2;
/*      */     
/*  818 */     if (eq("NOT", paramString, paramInt2, j)) {
/*  819 */       b = 57;
/*  820 */     } else if (eq("NATURAL", paramString, paramInt2, j)) {
/*  821 */       b = 56;
/*  822 */     } else if (eq("NULL", paramString, paramInt2, j)) {
/*  823 */       b = 58;
/*      */     } else {
/*  825 */       b = 2;
/*      */     } 
/*  827 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readO(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  831 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  832 */     int j = i - paramInt2;
/*      */     
/*  834 */     if (j == 2)
/*  835 */     { switch (paramString.charAt(paramInt2 + 1) & 0xFFDF)
/*      */       { case 78:
/*  837 */           b = 60;
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
/*  854 */           return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);case 82: b = 61; return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b); }  b = 2; } else if (eq("OFFSET", paramString, paramInt2, j)) { b = 59; } else if (eq("ORDER", paramString, paramInt2, j)) { b = 62; } else { b = 2; }  return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readP(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  858 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  859 */     int j = i - paramInt2;
/*  860 */     byte b = eq("PRIMARY", paramString, paramInt2, j) ? 63 : 2;
/*  861 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readQ(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  865 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  866 */     int j = i - paramInt2;
/*  867 */     byte b = eq("QUALIFY", paramString, paramInt2, j) ? 64 : 2;
/*  868 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readR(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  872 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  873 */     int j = i - paramInt2;
/*      */     
/*  875 */     if (eq("RIGHT", paramString, paramInt2, j)) {
/*  876 */       b = 65;
/*  877 */     } else if (eq("ROW", paramString, paramInt2, j)) {
/*  878 */       b = 66;
/*  879 */     } else if (eq("ROWNUM", paramString, paramInt2, j)) {
/*  880 */       b = 67;
/*      */     } else {
/*  882 */       b = 2;
/*      */     } 
/*  884 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readS(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  888 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  889 */     int j = i - paramInt2;
/*      */     
/*  891 */     if (eq("SECOND", paramString, paramInt2, j)) {
/*  892 */       b = 68;
/*  893 */     } else if (eq("SELECT", paramString, paramInt2, j)) {
/*  894 */       b = 69;
/*  895 */     } else if (eq("SESSION_USER", paramString, paramInt2, j)) {
/*  896 */       b = 70;
/*  897 */     } else if (eq("SET", paramString, paramInt2, j)) {
/*  898 */       b = 71;
/*  899 */     } else if (eq("SOME", paramString, paramInt2, j)) {
/*  900 */       b = 72;
/*  901 */     } else if (eq("SYMMETRIC", paramString, paramInt2, j)) {
/*  902 */       b = 73;
/*  903 */     } else if (eq("SYSTEM_USER", paramString, paramInt2, j)) {
/*  904 */       b = 74;
/*      */     } else {
/*  906 */       b = 2;
/*      */     } 
/*  908 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readT(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  912 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  913 */     int j = i - paramInt2;
/*      */     
/*  915 */     if (j == 2) {
/*  916 */       b = ((paramString.charAt(paramInt2 + 1) & 0xFFDF) == 79) ? 76 : 2;
/*      */     }
/*  918 */     else if (eq("TABLE", paramString, paramInt2, j)) {
/*  919 */       b = 75;
/*  920 */     } else if (eq("TRUE", paramString, paramInt2, j)) {
/*  921 */       b = 77;
/*      */     } else {
/*  923 */       b = 2;
/*      */     } 
/*      */     
/*  926 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readU(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  930 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  931 */     int j = i - paramInt2;
/*      */     
/*  933 */     if (eq("UESCAPE", paramString, paramInt2, j)) {
/*  934 */       b = 78;
/*  935 */     } else if (eq("UNION", paramString, paramInt2, j)) {
/*  936 */       b = 79;
/*  937 */     } else if (eq("UNIQUE", paramString, paramInt2, j)) {
/*  938 */       b = 80;
/*  939 */     } else if (eq("UNKNOWN", paramString, paramInt2, j)) {
/*  940 */       b = 81;
/*  941 */     } else if (eq("USER", paramString, paramInt2, j)) {
/*  942 */       b = 82;
/*  943 */     } else if (eq("USING", paramString, paramInt2, j)) {
/*  944 */       b = 83;
/*      */     } else {
/*  946 */       b = 2;
/*      */     } 
/*  948 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readV(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  952 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  953 */     int j = i - paramInt2;
/*      */     
/*  955 */     if (eq("VALUE", paramString, paramInt2, j)) {
/*  956 */       b = 84;
/*  957 */     } else if (eq("VALUES", paramString, paramInt2, j)) {
/*  958 */       b = 85;
/*      */     } else {
/*  960 */       b = 2;
/*      */     } 
/*  962 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   private int readW(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     byte b;
/*  966 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  967 */     int j = i - paramInt2;
/*      */     
/*  969 */     if (eq("WHEN", paramString, paramInt2, j)) {
/*  970 */       b = 86;
/*  971 */     } else if (eq("WHERE", paramString, paramInt2, j)) {
/*  972 */       b = 87;
/*  973 */     } else if (eq("WINDOW", paramString, paramInt2, j)) {
/*  974 */       b = 88;
/*  975 */     } else if (eq("WITH", paramString, paramInt2, j)) {
/*  976 */       b = 89;
/*      */     } else {
/*  978 */       b = 2;
/*      */     } 
/*  980 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readY(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  984 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  985 */     int j = i - paramInt2;
/*  986 */     byte b = eq("YEAR", paramString, paramInt2, j) ? 90 : 2;
/*  987 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int read_(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*  991 */     int i = findIdentifierEnd(paramString, paramInt1, paramInt3);
/*  992 */     byte b = (i - paramInt2 == 7 && "_ROWID_".regionMatches(true, 1, paramString, paramInt2 + 1, 6)) ? 91 : 2;
/*      */     
/*  994 */     return readIdentifierOrKeyword(paramString, paramInt2, paramArrayList, i, b);
/*      */   }
/*      */   
/*      */   private int readIdentifierOrKeyword(String paramString, int paramInt1, ArrayList<Token> paramArrayList, int paramInt2, int paramInt3) {
/*      */     Token.KeywordToken keywordToken;
/*  999 */     if (paramInt3 == 2) {
/* 1000 */       Token.IdentifierToken identifierToken = new Token.IdentifierToken(paramInt1, extractIdentifier(paramString, paramInt1, paramInt2), false, false);
/* 1001 */     } else if (this.nonKeywords != null && this.nonKeywords.get(paramInt3)) {
/* 1002 */       Token.KeywordOrIdentifierToken keywordOrIdentifierToken = new Token.KeywordOrIdentifierToken(paramInt1, paramInt3, extractIdentifier(paramString, paramInt1, paramInt2));
/*      */     } else {
/* 1004 */       keywordToken = new Token.KeywordToken(paramInt1, paramInt3);
/*      */     } 
/* 1006 */     paramArrayList.add(keywordToken);
/* 1007 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private static boolean eq(String paramString1, String paramString2, int paramInt1, int paramInt2) {
/* 1011 */     if (paramInt2 != paramString1.length()) {
/* 1012 */       return false;
/*      */     }
/* 1014 */     for (byte b = 1; b < paramInt2; b++) {
/* 1015 */       if (paramString1.charAt(b) != (paramString2.charAt(++paramInt1) & 0xFFDF)) {
/* 1016 */         return false;
/*      */       }
/*      */     } 
/* 1019 */     return true;
/*      */   }
/*      */   
/*      */   private int findIdentifierEnd(String paramString, int paramInt1, int paramInt2) {
/* 1023 */     paramInt2++;
/*      */     
/*      */     int i;
/* 1026 */     while (paramInt2 <= paramInt1 && (Character.isJavaIdentifierPart(i = paramString.codePointAt(paramInt2)) || (i == 35 && 
/* 1027 */       (this.provider.getMode()).supportPoundSymbolForColumnNames)))
/*      */     {
/*      */       
/* 1030 */       paramInt2 += Character.charCount(i);
/*      */     }
/* 1032 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private String extractIdentifier(String paramString, int paramInt1, int paramInt2) {
/* 1036 */     return convertCase(paramString.substring(paramInt1, paramInt2));
/*      */   }
/*      */ 
/*      */   
/*      */   private int readQuotedIdentifier(String paramString, int paramInt1, int paramInt2, int paramInt3, char paramChar, boolean paramBoolean, ArrayList<Token> paramArrayList) {
/* 1041 */     int i = paramString.indexOf(paramChar, ++paramInt3);
/* 1042 */     if (i < 0) {
/* 1043 */       throw DbException.getSyntaxError(paramString, paramInt2);
/*      */     }
/* 1045 */     String str = paramString.substring(paramInt3, i);
/* 1046 */     paramInt3 = i + 1;
/* 1047 */     if (paramInt3 <= paramInt1 && paramString.charAt(paramInt3) == paramChar) {
/* 1048 */       StringBuilder stringBuilder = new StringBuilder(str);
/*      */       do {
/* 1050 */         i = paramString.indexOf(paramChar, paramInt3 + 1);
/* 1051 */         if (i < 0) {
/* 1052 */           throw DbException.getSyntaxError(paramString, paramInt2);
/*      */         }
/* 1054 */         stringBuilder.append(paramString, paramInt3, i);
/* 1055 */         paramInt3 = i + 1;
/* 1056 */       } while (paramInt3 <= paramInt1 && paramString.charAt(paramInt3) == paramChar);
/* 1057 */       str = stringBuilder.toString();
/*      */     } 
/* 1059 */     if (paramChar == '`') {
/* 1060 */       str = convertCase(str);
/*      */     }
/* 1062 */     paramArrayList.add(new Token.IdentifierToken(paramInt2, str, true, paramBoolean));
/* 1063 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private String convertCase(String paramString) {
/* 1067 */     if (this.identifiersToUpper) {
/* 1068 */       paramString = StringUtils.toUpperEnglish(paramString);
/* 1069 */     } else if (this.identifiersToLower) {
/* 1070 */       paramString = StringUtils.toLowerEnglish(paramString);
/*      */     } 
/* 1072 */     return paramString;
/*      */   }
/*      */   
/*      */   private static int readBinaryString(String paramString, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/* 1076 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*      */     
/*      */     do {
/* 1079 */       int i = paramString.indexOf('\'', ++paramInt3);
/* 1080 */       if (i < 0 || (i < paramInt2 && paramString.charAt(i + 1) == '\'')) {
/* 1081 */         throw DbException.getSyntaxError(paramString, paramInt1);
/*      */       }
/* 1083 */       StringUtils.convertHexWithSpacesToBytes(byteArrayOutputStream, paramString, paramInt3, i);
/* 1084 */       paramInt3 = skipWhitespace(paramString, paramInt2, i + 1);
/* 1085 */     } while (paramInt3 <= paramInt2 && paramString.charAt(paramInt3) == '\'');
/* 1086 */     paramArrayList.add(new Token.BinaryStringToken(paramInt1, byteArrayOutputStream.toByteArray()));
/* 1087 */     return paramInt3;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int readCharacterString(String paramString, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, ArrayList<Token> paramArrayList) {
/* 1092 */     String str = null;
/* 1093 */     StringBuilder stringBuilder = null;
/*      */     
/*      */     do {
/* 1096 */       int i = paramString.indexOf('\'', ++paramInt3);
/* 1097 */       if (i < 0) {
/* 1098 */         throw DbException.getSyntaxError(paramString, paramInt1);
/*      */       }
/* 1100 */       if (str == null) {
/* 1101 */         str = paramString.substring(paramInt3, i);
/*      */       } else {
/* 1103 */         if (stringBuilder == null) {
/* 1104 */           stringBuilder = new StringBuilder(str);
/*      */         }
/* 1106 */         stringBuilder.append(paramString, paramInt3, i);
/*      */       } 
/* 1108 */       paramInt3 = i + 1;
/* 1109 */       if (paramInt3 <= paramInt2 && paramString.charAt(paramInt3) == '\'') {
/* 1110 */         if (stringBuilder == null) {
/* 1111 */           stringBuilder = new StringBuilder(str);
/*      */         }
/*      */         do {
/* 1114 */           i = paramString.indexOf('\'', paramInt3 + 1);
/* 1115 */           if (i < 0) {
/* 1116 */             throw DbException.getSyntaxError(paramString, paramInt1);
/*      */           }
/* 1118 */           stringBuilder.append(paramString, paramInt3, i);
/* 1119 */           paramInt3 = i + 1;
/* 1120 */         } while (paramInt3 <= paramInt2 && paramString.charAt(paramInt3) == '\'');
/*      */       } 
/* 1122 */       paramInt3 = skipWhitespace(paramString, paramInt2, paramInt3);
/* 1123 */     } while (paramInt3 <= paramInt2 && paramString.charAt(paramInt3) == '\'');
/* 1124 */     if (stringBuilder != null) {
/* 1125 */       str = stringBuilder.toString();
/*      */     }
/* 1127 */     paramArrayList.add(new Token.CharacterStringToken(paramInt1, str, paramBoolean));
/* 1128 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private static int skipWhitespace(String paramString, int paramInt1, int paramInt2) {
/* 1132 */     while (paramInt2 <= paramInt1) {
/* 1133 */       int i = paramString.codePointAt(paramInt2);
/* 1134 */       if (!Character.isWhitespace(i)) {
/* 1135 */         if (i == 47 && paramInt2 < paramInt1) {
/* 1136 */           char c = paramString.charAt(paramInt2 + 1);
/* 1137 */           if (c == '*') {
/* 1138 */             paramInt2 = skipBracketedComment(paramString, paramInt2, paramInt1, paramInt2); continue;
/*      */           } 
/* 1140 */           if (c == '/') {
/* 1141 */             paramInt2 = skipSimpleComment(paramString, paramInt1, paramInt2);
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         break;
/*      */       } 
/* 1147 */       paramInt2 += Character.charCount(i);
/*      */     } 
/* 1149 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private static int readHexNumber(String paramString, CastDataProvider paramCastDataProvider, int paramInt1, int paramInt2, int paramInt3, ArrayList<Token> paramArrayList) {
/*      */     char c;
/* 1154 */     if ((paramCastDataProvider.getMode()).zeroExLiteralsAreBinaryStrings) {
/* 1155 */       int j = paramInt3;
/*      */       char c1;
/* 1157 */       while (paramInt3 <= paramInt2 && (((c1 = paramString.charAt(paramInt3)) >= '0' && c1 <= '9') || ((c1 = (char)(c1 & 0xFFDF)) >= 'A' && c1 <= 'F'))) {
/* 1158 */         paramInt3++;
/*      */       }
/* 1160 */       if (paramInt3 <= paramInt2 && Character.isJavaIdentifierPart(paramString.codePointAt(paramInt3))) {
/* 1161 */         throw DbException.get(90004, paramString.substring(j, paramInt3 + 1));
/*      */       }
/* 1163 */       paramArrayList.add(new Token.BinaryStringToken(j, StringUtils.convertHexToBytes(paramString.substring(j, paramInt3))));
/* 1164 */       return paramInt3;
/*      */     } 
/* 1166 */     if (paramInt3 > paramInt2) {
/* 1167 */       throw DbException.getSyntaxError(paramString, paramInt1, "Hex number");
/*      */     }
/* 1169 */     int i = paramInt3;
/* 1170 */     long l = 0L;
/*      */     
/*      */     do {
/* 1173 */       c = paramString.charAt(paramInt3);
/* 1174 */       if (c >= '0' && c <= '9')
/* 1175 */       { l = (l << 4L) + c - 48L; }
/*      */       
/* 1177 */       else if ((c = (char)(c & 0xFFDF)) >= 'A' && c <= 'F')
/* 1178 */       { l = (l << 4L) + c - 55L; }
/* 1179 */       else { if (paramInt3 == i) {
/* 1180 */           throw DbException.getSyntaxError(paramString, paramInt1, "Hex number");
/*      */         }
/*      */         break; }
/*      */       
/* 1184 */       if (l > 2147483647L) {
/* 1185 */         while (++paramInt3 <= paramInt2 && (((
/* 1186 */           c = paramString.charAt(paramInt3)) >= '0' && c <= '9') || ((c = (char)(c & 0xFFDF)) >= 'A' && c <= 'F')));
/*      */         
/* 1188 */         return finishBigInteger(paramString, paramInt1, paramInt2, paramInt3, i, (paramInt3 <= paramInt2 && c == 'L'), 16, paramArrayList);
/*      */       } 
/* 1190 */     } while (++paramInt3 <= paramInt2);
/*      */     
/* 1192 */     boolean bool = (paramInt3 <= paramInt2 && c == 'L') ? true : false;
/* 1193 */     if (bool) {
/* 1194 */       paramInt3++;
/*      */     }
/* 1196 */     if (paramInt3 <= paramInt2 && Character.isJavaIdentifierPart(paramString.codePointAt(paramInt3))) {
/* 1197 */       throw DbException.getSyntaxError(paramString, paramInt1, "Hex number");
/*      */     }
/* 1199 */     paramArrayList.add(bool ? new Token.BigintToken(i, l) : new Token.IntegerToken(i, (int)l));
/* 1200 */     return paramInt3;
/*      */   }
/*      */ 
/*      */   
/*      */   private static int readNumeric(String paramString, int paramInt1, int paramInt2, int paramInt3, char paramChar, ArrayList<Token> paramArrayList) {
/* 1205 */     long l = (paramChar - 48);
/* 1206 */     for (; paramInt3 <= paramInt2; paramInt3++) {
/* 1207 */       paramChar = paramString.charAt(paramInt3);
/* 1208 */       if (paramChar < '0' || paramChar > '9') {
/* 1209 */         switch (paramChar) {
/*      */           case '.':
/* 1211 */             return readNumeric(paramString, paramInt1, paramInt2, paramInt3, paramChar, false, false, paramArrayList);
/*      */           case 'E':
/*      */           case 'e':
/* 1214 */             return readNumeric(paramString, paramInt1, paramInt2, paramInt3, paramChar, false, true, paramArrayList);
/*      */           case 'L':
/*      */           case 'l':
/* 1217 */             return finishBigInteger(paramString, paramInt1, paramInt2, paramInt3, paramInt1, true, 10, paramArrayList);
/*      */         } 
/*      */         break;
/*      */       } 
/* 1221 */       l = l * 10L + (paramChar - 48);
/* 1222 */       if (l > 2147483647L) {
/* 1223 */         return readNumeric(paramString, paramInt1, paramInt2, paramInt3, paramChar, true, false, paramArrayList);
/*      */       }
/*      */     } 
/* 1226 */     paramArrayList.add(new Token.IntegerToken(paramInt1, (int)l));
/* 1227 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private static int readNumeric(String paramString, int paramInt1, int paramInt2, int paramInt3, char paramChar, boolean paramBoolean1, boolean paramBoolean2, ArrayList<Token> paramArrayList) {
/*      */     BigDecimal bigDecimal;
/* 1232 */     if (!paramBoolean2) {
/* 1233 */       while (++paramInt3 <= paramInt2) {
/* 1234 */         paramChar = paramString.charAt(paramInt3);
/* 1235 */         if (paramChar == '.') {
/* 1236 */           paramBoolean1 = false; continue;
/* 1237 */         }  if (paramChar < '0' || paramChar > '9') {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     }
/* 1242 */     if (paramInt3 <= paramInt2 && (paramChar == 'E' || paramChar == 'e')) {
/* 1243 */       paramBoolean1 = false;
/* 1244 */       paramBoolean2 = true;
/* 1245 */       if (paramInt3 == paramInt2) {
/* 1246 */         throw DbException.getSyntaxError(paramString, paramInt1);
/*      */       }
/* 1248 */       paramChar = paramString.charAt(++paramInt3);
/* 1249 */       if (paramChar == '+' || paramChar == '-') {
/* 1250 */         if (paramInt3 == paramInt2) {
/* 1251 */           throw DbException.getSyntaxError(paramString, paramInt1);
/*      */         }
/* 1253 */         paramChar = paramString.charAt(++paramInt3);
/*      */       } 
/* 1255 */       if (paramChar < '0' || paramChar > '9') {
/* 1256 */         throw DbException.getSyntaxError(paramString, paramInt1);
/*      */       }
/* 1258 */       while (++paramInt3 <= paramInt2 && (paramChar = paramString.charAt(paramInt3)) >= '0' && paramChar <= '9');
/*      */     } 
/*      */ 
/*      */     
/* 1262 */     if (paramBoolean1) {
/* 1263 */       return finishBigInteger(paramString, paramInt1, paramInt2, paramInt3, paramInt1, ((paramInt3 < paramInt2 && paramChar == 'L') || paramChar == 'l'), 10, paramArrayList);
/*      */     }
/*      */     
/* 1266 */     String str = paramString.substring(paramInt1, paramInt3);
/*      */     try {
/* 1268 */       bigDecimal = new BigDecimal(str);
/* 1269 */     } catch (NumberFormatException numberFormatException) {
/* 1270 */       throw DbException.get(22018, numberFormatException, new String[] { str });
/*      */     } 
/* 1272 */     paramArrayList.add(new Token.ValueToken(paramInt1, paramBoolean2 ? (Value)ValueDecfloat.get(bigDecimal) : (Value)ValueNumeric.get(bigDecimal)));
/* 1273 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private static int finishBigInteger(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5, ArrayList<Token> paramArrayList) {
/*      */     Token.BigintToken bigintToken;
/* 1278 */     int i = paramInt3;
/* 1279 */     if (paramBoolean) {
/* 1280 */       paramInt3++;
/*      */     }
/* 1282 */     if (paramInt5 == 16 && paramInt3 <= paramInt2 && Character.isJavaIdentifierPart(paramString.codePointAt(paramInt3))) {
/* 1283 */       throw DbException.getSyntaxError(paramString, paramInt1, "Hex number");
/*      */     }
/* 1285 */     BigInteger bigInteger = new BigInteger(paramString.substring(paramInt4, i), paramInt5);
/*      */     
/* 1287 */     if (bigInteger.compareTo(ValueBigint.MAX_BI) > 0) {
/* 1288 */       if (paramBoolean) {
/* 1289 */         throw DbException.getSyntaxError(paramString, paramInt1);
/*      */       }
/* 1291 */       Token.ValueToken valueToken = new Token.ValueToken(paramInt1, (Value)ValueNumeric.get(bigInteger));
/*      */     } else {
/* 1293 */       bigintToken = new Token.BigintToken(paramInt4, bigInteger.longValue());
/*      */     } 
/* 1295 */     paramArrayList.add(bigintToken);
/* 1296 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private static int skipBracketedComment(String paramString, int paramInt1, int paramInt2, int paramInt3) {
/* 1300 */     paramInt3 += 2; byte b;
/* 1301 */     label17: for (b = 1; b;) {
/*      */       while (true) {
/* 1303 */         if (paramInt3 >= paramInt2) {
/* 1304 */           throw DbException.getSyntaxError(paramString, paramInt1);
/*      */         }
/* 1306 */         char c = paramString.charAt(paramInt3++);
/* 1307 */         if (c == '*') {
/* 1308 */           if (paramString.charAt(paramInt3) == '/') {
/* 1309 */             b--;
/* 1310 */             paramInt3++; continue label17;
/*      */           }  continue;
/*      */         } 
/* 1313 */         if (c == '/' && paramString.charAt(paramInt3) == '*') {
/* 1314 */           b++;
/* 1315 */           paramInt3++;
/*      */         } 
/*      */       } 
/*      */     } 
/* 1319 */     return paramInt3;
/*      */   }
/*      */   
/*      */   private static int skipSimpleComment(String paramString, int paramInt1, int paramInt2) {
/* 1323 */     paramInt2 += 2; char c;
/* 1324 */     for (; paramInt2 <= paramInt1 && (c = paramString.charAt(paramInt2)) != '\n' && c != '\r'; paramInt2++);
/*      */ 
/*      */     
/* 1327 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private static int parseParameterIndex(String paramString, int paramInt1, int paramInt2, ArrayList<Token> paramArrayList) {
/* 1331 */     int i = paramInt2;
/* 1332 */     long l = 0L; char c;
/* 1333 */     while (++paramInt2 <= paramInt1 && (c = paramString.charAt(paramInt2)) >= '0' && c <= '9') {
/* 1334 */       l = l * 10L + (c - 48);
/* 1335 */       if (l > 2147483647L) {
/* 1336 */         throw DbException.getInvalidValueException("parameter index", Long.valueOf(l));
/*      */       }
/*      */     } 
/* 1339 */     if (paramInt2 > i + 1 && l == 0L) {
/* 1340 */       throw DbException.getInvalidValueException("parameter index", Long.valueOf(l));
/*      */     }
/* 1342 */     paramArrayList.add(new Token.ParameterToken(i, (int)l));
/* 1343 */     return paramInt2;
/*      */   }
/*      */   
/*      */   private static int assignParameterIndex(ArrayList<Token> paramArrayList, int paramInt) {
/* 1347 */     Token.ParameterToken parameterToken = (Token.ParameterToken)paramArrayList.get(paramArrayList.size() - 1);
/* 1348 */     if (parameterToken.index == 0)
/* 1349 */     { if (paramInt < 0) {
/* 1350 */         throw DbException.get(90123);
/*      */       }
/* 1352 */       parameterToken.index = ++paramInt; }
/* 1353 */     else { if (paramInt > 0) {
/* 1354 */         throw DbException.get(90123);
/*      */       }
/* 1356 */       paramInt = -1; }
/*      */     
/* 1358 */     return paramInt;
/*      */   }
/*      */   
/*      */   private static void processUescape(String paramString, ArrayList<Token> paramArrayList) {
/* 1362 */     ListIterator<Token> listIterator = paramArrayList.listIterator();
/* 1363 */     while (listIterator.hasNext()) {
/* 1364 */       Token token = listIterator.next();
/* 1365 */       if (token.needsUnicodeConversion()) {
/* 1366 */         int i = 92;
/* 1367 */         if (listIterator.hasNext()) {
/* 1368 */           Token token1 = listIterator.next();
/* 1369 */           if (token1.tokenType() == 78) {
/* 1370 */             listIterator.remove();
/* 1371 */             if (listIterator.hasNext())
/* 1372 */             { Token token2 = listIterator.next();
/* 1373 */               listIterator.remove();
/* 1374 */               if (token2 instanceof Token.CharacterStringToken)
/* 1375 */               { String str = ((Token.CharacterStringToken)token2).string;
/* 1376 */                 if (str.codePointCount(0, str.length()) == 1)
/* 1377 */                 { int j = str.codePointAt(0);
/* 1378 */                   if (!Character.isWhitespace(j) && (j < 48 || j > 57) && (j < 65 || j > 70) && (j < 97 || j > 102))
/*      */                   
/* 1380 */                   { switch (j)
/*      */                     { default:
/* 1382 */                         i = j;
/*      */                         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                       
/*      */                       case 34:
/*      */                       case 39:
/*      */                       case 43:
/* 1392 */                         throw DbException.getSyntaxError(paramString, token1.start() + 7, "'<Unicode escape character>'"); }  } else {  }  } else {  }  } else {  }  } else {  }
/*      */           
/*      */           } 
/* 1395 */         }  token.convertUnicode(i);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\Tokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */