/*      */ package freemarker.core;
/*      */ 
/*      */ import freemarker.template.Configuration;
/*      */ import freemarker.template._TemplateAPI;
/*      */ import freemarker.template.utility.StringUtil;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.StringTokenizer;
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
/*      */ class FMParserTokenManager
/*      */   implements FMParserConstants
/*      */ {
/*      */   private static final String PLANNED_DIRECTIVE_HINT = "(If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
/*      */   String noparseTag;
/*      */   private FMParser parser;
/*   26 */   private int postInterpolationLexState = -1;
/*      */   
/*      */   private int curlyBracketNesting;
/*      */   
/*      */   private int parenthesisNesting;
/*      */   
/*      */   private int bracketNesting;
/*      */   
/*      */   private boolean inFTLHeader;
/*      */   boolean strictSyntaxMode;
/*      */   boolean squBracTagSyntax;
/*      */   boolean autodetectTagSyntax;
/*      */   boolean tagSyntaxEstablished;
/*      */   boolean inInvocation;
/*      */   int interpolationSyntax;
/*      */   int initialNamingConvention;
/*      */   int namingConvention;
/*      */   Token namingConventionEstabilisher;
/*      */   int incompatibleImprovements;
/*      */   
/*      */   void setParser(FMParser parser) {
/*   47 */     this.parser = parser;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleTagSyntaxAndSwitch(Token tok, int tokenNamingConvention, int newLexState) {
/*   55 */     String image = tok.image;
/*      */ 
/*      */ 
/*      */     
/*   59 */     if (!this.strictSyntaxMode && tokenNamingConvention == 12 && 
/*      */       
/*   61 */       !isStrictTag(image)) {
/*   62 */       tok.kind = 80;
/*      */       
/*      */       return;
/*      */     } 
/*   66 */     char firstChar = image.charAt(0);
/*   67 */     if (this.autodetectTagSyntax && !this.tagSyntaxEstablished) {
/*   68 */       this.squBracTagSyntax = (firstChar == '[');
/*      */     }
/*   70 */     if ((firstChar == '[' && !this.squBracTagSyntax) || (firstChar == '<' && this.squBracTagSyntax)) {
/*   71 */       tok.kind = 80;
/*      */       
/*      */       return;
/*      */     } 
/*   75 */     if (!this.strictSyntaxMode) {
/*      */ 
/*      */       
/*   78 */       checkNamingConvention(tok, tokenNamingConvention);
/*   79 */       SwitchTo(newLexState);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */ 
/*      */     
/*   86 */     if (!this.squBracTagSyntax && !isStrictTag(image)) {
/*   87 */       tok.kind = 80;
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/*   92 */     this.tagSyntaxEstablished = true;
/*      */     
/*   94 */     if (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*   99 */       char lastChar = image.charAt(image.length() - 1);
/*      */       
/*  101 */       if ((lastChar == ']' || lastChar == '>') && ((
/*  102 */         !this.squBracTagSyntax && lastChar != '>') || (this.squBracTagSyntax && lastChar != ']'))) {
/*  103 */         throw new TokenMgrError("The tag shouldn't end with \"" + lastChar + "\".", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  112 */     checkNamingConvention(tok, tokenNamingConvention);
/*      */     
/*  114 */     SwitchTo(newLexState);
/*      */   }
/*      */   
/*      */   void checkNamingConvention(Token tok) {
/*  118 */     checkNamingConvention(tok, _CoreStringUtils.getIdentifierNamingConvention(tok.image));
/*      */   }
/*      */   
/*      */   void checkNamingConvention(Token tok, int tokenNamingConvention) {
/*  122 */     if (tokenNamingConvention != 10) {
/*  123 */       if (this.namingConvention == 10) {
/*  124 */         this.namingConvention = tokenNamingConvention;
/*  125 */         this.namingConventionEstabilisher = tok;
/*  126 */       } else if (this.namingConvention != tokenNamingConvention) {
/*  127 */         throw newNameConventionMismatchException(tok);
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   private TokenMgrError newNameConventionMismatchException(Token tok) {
/*  133 */     return new TokenMgrError("Naming convention mismatch. Identifiers that are part of the template language (not the user specified ones) " + ((this.initialNamingConvention == 10) ? "must consistently use the same naming convention within the same template. This template uses " : "must use the configured naming convention, which is the ") + ((this.namingConvention == 12) ? "camel case naming convention (like: exampleName) " : ((this.namingConvention == 11) ? "legacy naming convention (directive (tag) names are like examplename, everything else is like example_name) " : "??? (internal error)")) + ((this.namingConventionEstabilisher != null) ? ("estabilished by auto-detection at " + 
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
/*  148 */         _MessageUtil.formatPosition(this.namingConventionEstabilisher.beginLine, this.namingConventionEstabilisher.beginColumn) + " by token " + 
/*      */         
/*  150 */         StringUtil.jQuote(this.namingConventionEstabilisher.image.trim())) : "") + ", but the problematic token, " + 
/*      */         
/*  152 */         StringUtil.jQuote(tok.image.trim()) + ", uses a different convention.", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void handleTagSyntaxAndSwitch(Token tok, int newLexState) {
/*  162 */     handleTagSyntaxAndSwitch(tok, 10, newLexState);
/*      */   }
/*      */   
/*      */   private boolean isStrictTag(String image) {
/*  166 */     return (image.length() > 2 && (image.charAt(1) == '#' || image.charAt(2) == '#'));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int getTagNamingConvention(Token tok, int charIdxInName) {
/*  176 */     return _CoreStringUtils.isUpperUSASCII(getTagNameCharAt(tok, charIdxInName)) ? 12 : 11;
/*      */   }
/*      */ 
/*      */   
/*      */   static char getTagNameCharAt(Token tok, int charIdxInName) {
/*  181 */     String image = tok.image;
/*      */ 
/*      */     
/*  184 */     int idx = 0;
/*      */     while (true) {
/*  186 */       char c = image.charAt(idx);
/*  187 */       if (c != '<' && c != '[' && c != '/' && c != '#') {
/*      */         break;
/*      */       }
/*  190 */       idx++;
/*      */     } 
/*      */     
/*  193 */     return image.charAt(idx + charIdxInName);
/*      */   }
/*      */   
/*      */   private void unifiedCall(Token tok) {
/*  197 */     char firstChar = tok.image.charAt(0);
/*  198 */     if (this.autodetectTagSyntax && !this.tagSyntaxEstablished) {
/*  199 */       this.squBracTagSyntax = (firstChar == '[');
/*      */     }
/*  201 */     if (this.squBracTagSyntax && firstChar == '<') {
/*  202 */       tok.kind = 80;
/*      */       return;
/*      */     } 
/*  205 */     if (!this.squBracTagSyntax && firstChar == '[') {
/*  206 */       tok.kind = 80;
/*      */       return;
/*      */     } 
/*  209 */     this.tagSyntaxEstablished = true;
/*  210 */     SwitchTo(6);
/*      */   }
/*      */   
/*      */   private void unifiedCallEnd(Token tok) {
/*  214 */     char firstChar = tok.image.charAt(0);
/*  215 */     if (this.squBracTagSyntax && firstChar == '<') {
/*  216 */       tok.kind = 80;
/*      */       return;
/*      */     } 
/*  219 */     if (!this.squBracTagSyntax && firstChar == '[') {
/*  220 */       tok.kind = 80;
/*      */       return;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void startInterpolation(Token tok) {
/*  226 */     if ((this.interpolationSyntax == 20 && tok.kind == 84) || (this.interpolationSyntax == 21 && tok.kind != 82) || (this.interpolationSyntax == 22 && tok.kind != 84)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  233 */       tok.kind = 80;
/*      */       
/*      */       return;
/*      */     } 
/*  237 */     if (this.postInterpolationLexState != -1) {
/*      */       
/*  239 */       char c = tok.image.charAt(0);
/*  240 */       throw new TokenMgrError("You can't start an interpolation (" + tok.image + "..." + ((this.interpolationSyntax == 22) ? "]" : "}") + ") here as you are inside another interpolation.)", 0, tok.beginLine, tok.beginColumn, tok.endLine, tok.endColumn);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  248 */     this.postInterpolationLexState = this.curLexState;
/*  249 */     SwitchTo(2);
/*      */   }
/*      */   
/*      */   private void endInterpolation(Token closingTk) {
/*  253 */     SwitchTo(this.postInterpolationLexState);
/*  254 */     this.postInterpolationLexState = -1;
/*      */   }
/*      */   
/*      */   private TokenMgrError newUnexpectedClosingTokenException(Token closingTk) {
/*  258 */     return new TokenMgrError("You can't have an \"" + closingTk.image + "\" here, as there's nothing open that it could close.", 0, closingTk.beginLine, closingTk.beginColumn, closingTk.endLine, closingTk.endColumn);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void eatNewline() {
/*  266 */     int charsRead = 0; try {
/*      */       char c;
/*      */       do {
/*  269 */         c = this.input_stream.readChar();
/*  270 */         charsRead++;
/*  271 */         if (!Character.isWhitespace(c)) {
/*  272 */           this.input_stream.backup(charsRead); return;
/*      */         } 
/*  274 */         if (c == '\r') {
/*  275 */           char next = this.input_stream.readChar();
/*  276 */           charsRead++;
/*  277 */           if (next != '\n')
/*  278 */             this.input_stream.backup(1); 
/*      */           return;
/*      */         } 
/*  281 */       } while (c != '\n');
/*      */ 
/*      */       
/*      */       return;
/*  285 */     } catch (IOException ioe) {
/*  286 */       this.input_stream.backup(charsRead);
/*      */       return;
/*      */     } 
/*      */   }
/*      */   private void ftlHeader(Token matchedToken) {
/*  291 */     if (!this.tagSyntaxEstablished) {
/*  292 */       this.squBracTagSyntax = (matchedToken.image.charAt(0) == '[');
/*  293 */       this.tagSyntaxEstablished = true;
/*  294 */       this.autodetectTagSyntax = false;
/*      */     } 
/*  296 */     String img = matchedToken.image;
/*  297 */     char firstChar = img.charAt(0);
/*  298 */     char lastChar = img.charAt(img.length() - 1);
/*  299 */     if ((firstChar == '[' && !this.squBracTagSyntax) || (firstChar == '<' && this.squBracTagSyntax)) {
/*  300 */       matchedToken.kind = 80;
/*      */     }
/*  302 */     if (matchedToken.kind != 80) {
/*  303 */       if (lastChar != '>' && lastChar != ']') {
/*  304 */         SwitchTo(2);
/*  305 */         this.inFTLHeader = true;
/*      */       } else {
/*  307 */         eatNewline();
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*  313 */   public PrintStream debugStream = System.out;
/*      */   public void setDebugStream(PrintStream ds) {
/*  315 */     this.debugStream = ds;
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_7() {
/*  318 */     return jjMoveNfa_7(0, 0);
/*      */   }
/*  320 */   static final long[] jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
/*      */ 
/*      */   
/*  323 */   static final long[] jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
/*      */ 
/*      */ 
/*      */   
/*      */   private int jjMoveNfa_7(int startState, int curPos) {
/*  328 */     int startsAt = 0;
/*  329 */     this.jjnewStateCnt = 13;
/*  330 */     int i = 1;
/*  331 */     this.jjstateSet[0] = startState;
/*  332 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/*  335 */       if (++this.jjround == Integer.MAX_VALUE)
/*  336 */         ReInitRounds(); 
/*  337 */       if (this.curChar < 64) {
/*      */         
/*  339 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/*  342 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 0:
/*  345 */               if ((0xEFFFDFFFFFFFFFFFL & l) != 0L) {
/*      */                 
/*  347 */                 if (kind > 156)
/*  348 */                   kind = 156; 
/*  349 */                 jjCheckNAdd(6);
/*      */               }
/*  351 */               else if ((0x1000200000000000L & l) != 0L) {
/*      */                 
/*  353 */                 if (kind > 157)
/*  354 */                   kind = 157; 
/*      */               } 
/*  356 */               if (this.curChar == 45) {
/*  357 */                 jjAddStates(0, 1); break;
/*  358 */               }  if (this.curChar == 60)
/*  359 */                 this.jjstateSet[this.jjnewStateCnt++] = 1; 
/*      */               break;
/*      */             case 1:
/*  362 */               if (this.curChar == 47)
/*  363 */                 jjCheckNAddTwoStates(2, 3); 
/*      */               break;
/*      */             case 2:
/*  366 */               if (this.curChar == 35)
/*  367 */                 jjCheckNAdd(3); 
/*      */               break;
/*      */             case 4:
/*  370 */               if ((0x100002600L & l) != 0L)
/*  371 */                 jjAddStates(2, 3); 
/*      */               break;
/*      */             case 5:
/*  374 */               if (this.curChar == 62 && kind > 155)
/*  375 */                 kind = 155; 
/*      */               break;
/*      */             case 6:
/*  378 */               if ((0xEFFFDFFFFFFFFFFFL & l) == 0L)
/*      */                 break; 
/*  380 */               if (kind > 156)
/*  381 */                 kind = 156; 
/*  382 */               jjCheckNAdd(6);
/*      */               break;
/*      */             case 7:
/*  385 */               if ((0x1000200000000000L & l) != 0L && kind > 157)
/*  386 */                 kind = 157; 
/*      */               break;
/*      */             case 8:
/*  389 */               if (this.curChar == 45)
/*  390 */                 jjAddStates(0, 1); 
/*      */               break;
/*      */             case 9:
/*  393 */               if (this.curChar == 62 && kind > 154)
/*  394 */                 kind = 154; 
/*      */               break;
/*      */             case 10:
/*  397 */               if (this.curChar == 45)
/*  398 */                 this.jjstateSet[this.jjnewStateCnt++] = 9; 
/*      */               break;
/*      */             case 12:
/*  401 */               if (this.curChar == 45) {
/*  402 */                 this.jjstateSet[this.jjnewStateCnt++] = 11;
/*      */               }
/*      */               break;
/*      */           } 
/*  406 */         } while (i != startsAt);
/*      */       }
/*  408 */       else if (this.curChar < 128) {
/*      */         
/*  410 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/*  413 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 0:
/*  416 */               if ((0xFFFFFFFFF7FFFFFFL & l) != 0L) {
/*      */                 
/*  418 */                 if (kind > 156)
/*  419 */                   kind = 156; 
/*  420 */                 jjCheckNAdd(6);
/*      */               }
/*  422 */               else if (this.curChar == 91) {
/*      */                 
/*  424 */                 if (kind > 157)
/*  425 */                   kind = 157; 
/*      */               } 
/*  427 */               if (this.curChar == 91)
/*  428 */                 this.jjstateSet[this.jjnewStateCnt++] = 1; 
/*      */               break;
/*      */             case 3:
/*  431 */               if ((0x7FFFFFE07FFFFFEL & l) != 0L)
/*  432 */                 jjAddStates(4, 6); 
/*      */               break;
/*      */             case 5:
/*  435 */               if (this.curChar == 93 && kind > 155)
/*  436 */                 kind = 155; 
/*      */               break;
/*      */             case 6:
/*  439 */               if ((0xFFFFFFFFF7FFFFFFL & l) == 0L)
/*      */                 break; 
/*  441 */               if (kind > 156)
/*  442 */                 kind = 156; 
/*  443 */               jjCheckNAdd(6);
/*      */               break;
/*      */             case 7:
/*  446 */               if (this.curChar == 91 && kind > 157)
/*  447 */                 kind = 157; 
/*      */               break;
/*      */             case 11:
/*  450 */               if (this.curChar == 93 && kind > 154) {
/*  451 */                 kind = 154;
/*      */               }
/*      */               break;
/*      */           } 
/*  455 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/*  459 */         int hiByte = this.curChar >> 8;
/*  460 */         int i1 = hiByte >> 6;
/*  461 */         long l1 = 1L << (hiByte & 0x3F);
/*  462 */         int i2 = (this.curChar & 0xFF) >> 6;
/*  463 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/*  466 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 0:
/*      */             case 6:
/*  470 */               if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/*  472 */               if (kind > 156)
/*  473 */                 kind = 156; 
/*  474 */               jjCheckNAdd(6); break;
/*      */             default:
/*  476 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/*  478 */         } while (i != startsAt);
/*      */       } 
/*  480 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/*  482 */         this.jjmatchedKind = kind;
/*  483 */         this.jjmatchedPos = curPos;
/*  484 */         kind = Integer.MAX_VALUE;
/*      */       } 
/*  486 */       curPos++;
/*  487 */       if ((i = this.jjnewStateCnt) == (startsAt = 13 - (this.jjnewStateCnt = startsAt)))
/*  488 */         return curPos;  
/*  489 */       try { this.curChar = this.input_stream.readChar(); }
/*  490 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_0(int pos, long active0, long active1) {
/*  494 */     switch (pos) {
/*      */       
/*      */       case 0:
/*  497 */         if ((active1 & 0x100000L) != 0L) {
/*      */           
/*  499 */           this.jjmatchedKind = 81;
/*  500 */           return 697;
/*      */         } 
/*  502 */         if ((active1 & 0xC0000L) != 0L) {
/*      */           
/*  504 */           this.jjmatchedKind = 81;
/*  505 */           return -1;
/*      */         } 
/*  507 */         return -1;
/*      */     } 
/*  509 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_0(int pos, long active0, long active1) {
/*  513 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
/*      */   }
/*      */   
/*      */   private int jjStopAtPos(int pos, int kind) {
/*  517 */     this.jjmatchedKind = kind;
/*  518 */     this.jjmatchedPos = pos;
/*  519 */     return pos + 1;
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_0() {
/*  522 */     switch (this.curChar) {
/*      */       
/*      */       case 35:
/*  525 */         return jjMoveStringLiteralDfa1_0(524288L);
/*      */       case 36:
/*  527 */         return jjMoveStringLiteralDfa1_0(262144L);
/*      */       case 91:
/*  529 */         return jjMoveStringLiteralDfa1_0(1048576L);
/*      */     } 
/*  531 */     return jjMoveNfa_0(2, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_0(long active1) {
/*      */     try {
/*  535 */       this.curChar = this.input_stream.readChar();
/*  536 */     } catch (IOException e) {
/*  537 */       jjStopStringLiteralDfa_0(0, 0L, active1);
/*  538 */       return 1;
/*      */     } 
/*  540 */     switch (this.curChar) {
/*      */       
/*      */       case 61:
/*  543 */         if ((active1 & 0x100000L) != 0L)
/*  544 */           return jjStopAtPos(1, 84); 
/*      */         break;
/*      */       case 123:
/*  547 */         if ((active1 & 0x40000L) != 0L)
/*  548 */           return jjStopAtPos(1, 82); 
/*  549 */         if ((active1 & 0x80000L) != 0L) {
/*  550 */           return jjStopAtPos(1, 83);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  555 */     return jjStartNfa_0(0, 0L, active1);
/*      */   }
/*  557 */   static final long[] jjbitVec3 = new long[] { -4503595332403202L, -8193L, -17386027614209L, 1585267068842803199L };
/*      */ 
/*      */   
/*  560 */   static final long[] jjbitVec4 = new long[] { 0L, 0L, 297241973452963840L, -36028797027352577L };
/*      */ 
/*      */   
/*  563 */   static final long[] jjbitVec5 = new long[] { 0L, -9222809086901354496L, 536805376L, 0L };
/*      */ 
/*      */   
/*  566 */   static final long[] jjbitVec6 = new long[] { -864764451093480316L, 17376L, 24L, 0L };
/*      */ 
/*      */   
/*  569 */   static final long[] jjbitVec7 = new long[] { -140737488355329L, -2147483649L, -1L, 3509778554814463L };
/*      */ 
/*      */   
/*  572 */   static final long[] jjbitVec8 = new long[] { -245465970900993L, 141836999983103L, 9187201948305063935L, 2139062143L };
/*      */ 
/*      */   
/*  575 */   static final long[] jjbitVec9 = new long[] { 140737488355328L, 0L, 0L, 0L };
/*      */ 
/*      */   
/*  578 */   static final long[] jjbitVec10 = new long[] { 1746833705466331232L, -1L, -1L, -1L };
/*      */ 
/*      */   
/*  581 */   static final long[] jjbitVec11 = new long[] { -1L, -1L, 576460748008521727L, -281474976710656L };
/*      */ 
/*      */   
/*  584 */   static final long[] jjbitVec12 = new long[] { -1L, -1L, 0L, 0L };
/*      */ 
/*      */   
/*  587 */   static final long[] jjbitVec13 = new long[] { -1L, -1L, 18014398509481983L, 0L };
/*      */ 
/*      */   
/*  590 */   static final long[] jjbitVec14 = new long[] { -1L, -1L, 8191L, 4611686018427322368L };
/*      */ 
/*      */   
/*  593 */   static final long[] jjbitVec15 = new long[] { 17592185987071L, -9223231299366420481L, -4278190081L, 274877906943L };
/*      */ 
/*      */   
/*  596 */   static final long[] jjbitVec16 = new long[] { -12893290496L, -1L, 8791799069183L, -72057594037927936L };
/*      */ 
/*      */   
/*  599 */   static final long[] jjbitVec17 = new long[] { 34359736251L, 4503599627370495L, 4503599627370492L, 647392446501552128L };
/*      */ 
/*      */   
/*  602 */   static final long[] jjbitVec18 = new long[] { -281200098803713L, 2305843004918726783L, 2251799813685232L, 67076096L };
/*      */ 
/*      */   
/*  605 */   static final long[] jjbitVec19 = new long[] { 2199023255551L, 324259168942755831L, 4495436853045886975L, 7890092085477381L };
/*      */ 
/*      */   
/*  608 */   static final long[] jjbitVec20 = new long[] { 140183445864062L, 0L, 0L, 287948935534739455L };
/*      */ 
/*      */   
/*  611 */   static final long[] jjbitVec21 = new long[] { -1L, -1L, -281406257233921L, 1152921504606845055L };
/*      */ 
/*      */   
/*  614 */   static final long[] jjbitVec22 = new long[] { 6881498030004502655L, -37L, 1125899906842623L, -524288L };
/*      */ 
/*      */   
/*  617 */   static final long[] jjbitVec23 = new long[] { 4611686018427387903L, -65536L, -196609L, 1152640029630136575L };
/*      */ 
/*      */   
/*  620 */   static final long[] jjbitVec24 = new long[] { 0L, -9288674231451648L, -1L, 2305843009213693951L };
/*      */ 
/*      */   
/*  623 */   static final long[] jjbitVec25 = new long[] { 576460743780532224L, -274743689218L, Long.MAX_VALUE, 486341884L };
/*      */ 
/*      */ 
/*      */   
/*      */   private int jjMoveNfa_0(int startState, int curPos) {
/*  628 */     int startsAt = 0;
/*  629 */     this.jjnewStateCnt = 713;
/*  630 */     int i = 1;
/*  631 */     this.jjstateSet[0] = startState;
/*  632 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/*  635 */       if (++this.jjround == Integer.MAX_VALUE)
/*  636 */         ReInitRounds(); 
/*  637 */       if (this.curChar < 64) {
/*      */         
/*  639 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/*  642 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 697:
/*  645 */               if (this.curChar == 47) {
/*  646 */                 jjCheckNAdd(663);
/*  647 */               } else if (this.curChar == 35) {
/*  648 */                 this.jjstateSet[this.jjnewStateCnt++] = 664;
/*  649 */               }  if (this.curChar == 35) {
/*  650 */                 this.jjstateSet[this.jjnewStateCnt++] = 691;
/*  651 */               } else if (this.curChar == 47) {
/*  652 */                 this.jjstateSet[this.jjnewStateCnt++] = 698;
/*  653 */               }  if (this.curChar == 35) {
/*  654 */                 this.jjstateSet[this.jjnewStateCnt++] = 688;
/*  655 */               } else if (this.curChar == 47) {
/*  656 */                 jjCheckNAdd(649);
/*  657 */               }  if (this.curChar == 35) {
/*  658 */                 jjCheckNAdd(367);
/*  659 */               } else if (this.curChar == 47) {
/*  660 */                 jjCheckNAdd(635);
/*  661 */               }  if (this.curChar == 35) {
/*  662 */                 jjCheckNAdd(357);
/*  663 */               } else if (this.curChar == 47) {
/*  664 */                 jjCheckNAdd(607);
/*  665 */               }  if (this.curChar == 35) {
/*  666 */                 jjCheckNAdd(350);
/*  667 */               } else if (this.curChar == 47) {
/*  668 */                 jjCheckNAdd(596);
/*  669 */               }  if (this.curChar == 35) {
/*  670 */                 jjCheckNAdd(339);
/*  671 */               } else if (this.curChar == 47) {
/*  672 */                 jjCheckNAdd(582);
/*  673 */               }  if (this.curChar == 35) {
/*  674 */                 jjCheckNAdd(331);
/*  675 */               } else if (this.curChar == 47) {
/*  676 */                 jjCheckNAdd(569);
/*  677 */               }  if (this.curChar == 35) {
/*  678 */                 jjCheckNAdd(321);
/*  679 */               } else if (this.curChar == 47) {
/*  680 */                 jjCheckNAdd(550);
/*  681 */               }  if (this.curChar == 35) {
/*  682 */                 jjCheckNAdd(314);
/*  683 */               } else if (this.curChar == 47) {
/*  684 */                 jjCheckNAdd(538);
/*  685 */               }  if (this.curChar == 35) {
/*  686 */                 jjCheckNAdd(305);
/*  687 */               } else if (this.curChar == 47) {
/*  688 */                 jjCheckNAdd(521);
/*  689 */               }  if (this.curChar == 35) {
/*  690 */                 jjCheckNAdd(296);
/*  691 */               } else if (this.curChar == 47) {
/*  692 */                 jjCheckNAdd(511);
/*  693 */               }  if (this.curChar == 35) {
/*  694 */                 jjCheckNAdd(291);
/*  695 */               } else if (this.curChar == 47) {
/*  696 */                 jjCheckNAdd(498);
/*  697 */               }  if (this.curChar == 35) {
/*  698 */                 jjCheckNAdd(286);
/*  699 */               } else if (this.curChar == 47) {
/*  700 */                 jjCheckNAdd(487);
/*  701 */               }  if (this.curChar == 35) {
/*  702 */                 jjCheckNAdd(278);
/*  703 */               } else if (this.curChar == 47) {
/*  704 */                 jjCheckNAdd(476);
/*  705 */               }  if (this.curChar == 35) {
/*  706 */                 jjCheckNAdd(277);
/*  707 */               } else if (this.curChar == 47) {
/*  708 */                 jjCheckNAdd(466);
/*  709 */               }  if (this.curChar == 35) {
/*  710 */                 jjCheckNAdd(269);
/*  711 */               } else if (this.curChar == 47) {
/*  712 */                 jjCheckNAdd(454);
/*  713 */               }  if (this.curChar == 35) {
/*  714 */                 jjCheckNAdd(262);
/*  715 */               } else if (this.curChar == 47) {
/*  716 */                 jjCheckNAdd(442);
/*  717 */               }  if (this.curChar == 35) {
/*  718 */                 jjCheckNAdd(253);
/*  719 */               } else if (this.curChar == 47) {
/*  720 */                 jjCheckNAdd(430);
/*  721 */               }  if (this.curChar == 35) {
/*  722 */                 jjCheckNAdd(242);
/*  723 */               } else if (this.curChar == 47) {
/*  724 */                 jjCheckNAdd(422);
/*  725 */               }  if (this.curChar == 35) {
/*  726 */                 jjCheckNAdd(234);
/*  727 */               } else if (this.curChar == 47) {
/*  728 */                 jjCheckNAdd(412);
/*  729 */               }  if (this.curChar == 47) {
/*  730 */                 jjCheckNAdd(403);
/*  731 */               } else if (this.curChar == 35) {
/*  732 */                 jjCheckNAdd(227);
/*  733 */               }  if (this.curChar == 35)
/*  734 */                 this.jjstateSet[this.jjnewStateCnt++] = 696; 
/*  735 */               if (this.curChar == 35)
/*  736 */                 jjCheckNAdd(218); 
/*  737 */               if (this.curChar == 35)
/*  738 */                 jjCheckNAdd(209); 
/*  739 */               if (this.curChar == 35)
/*  740 */                 jjCheckNAdd(199); 
/*  741 */               if (this.curChar == 35)
/*  742 */                 jjCheckNAdd(183); 
/*  743 */               if (this.curChar == 35)
/*  744 */                 jjCheckNAdd(174); 
/*  745 */               if (this.curChar == 35)
/*  746 */                 jjCheckNAdd(161); 
/*  747 */               if (this.curChar == 35)
/*  748 */                 jjCheckNAdd(153); 
/*  749 */               if (this.curChar == 35)
/*  750 */                 jjCheckNAdd(148); 
/*  751 */               if (this.curChar == 35)
/*  752 */                 jjCheckNAdd(141); 
/*  753 */               if (this.curChar == 35)
/*  754 */                 jjCheckNAdd(136); 
/*  755 */               if (this.curChar == 35)
/*  756 */                 jjCheckNAdd(130); 
/*  757 */               if (this.curChar == 35)
/*  758 */                 jjCheckNAdd(120); 
/*  759 */               if (this.curChar == 35)
/*  760 */                 jjCheckNAdd(114); 
/*  761 */               if (this.curChar == 35)
/*  762 */                 jjCheckNAdd(105); 
/*  763 */               if (this.curChar == 35)
/*  764 */                 jjCheckNAdd(98); 
/*  765 */               if (this.curChar == 35)
/*  766 */                 jjCheckNAdd(90); 
/*  767 */               if (this.curChar == 35)
/*  768 */                 jjCheckNAdd(84); 
/*  769 */               if (this.curChar == 35)
/*  770 */                 jjCheckNAdd(77); 
/*  771 */               if (this.curChar == 35)
/*  772 */                 jjCheckNAdd(70); 
/*  773 */               if (this.curChar == 35)
/*  774 */                 jjCheckNAdd(65); 
/*  775 */               if (this.curChar == 35)
/*  776 */                 jjCheckNAdd(58); 
/*  777 */               if (this.curChar == 35)
/*  778 */                 jjCheckNAdd(50); 
/*  779 */               if (this.curChar == 35)
/*  780 */                 jjCheckNAdd(45); 
/*  781 */               if (this.curChar == 35)
/*  782 */                 jjCheckNAdd(36); 
/*  783 */               if (this.curChar == 35)
/*  784 */                 jjCheckNAdd(31); 
/*  785 */               if (this.curChar == 35)
/*  786 */                 jjCheckNAdd(24); 
/*  787 */               if (this.curChar == 35)
/*  788 */                 jjCheckNAdd(21); 
/*  789 */               if (this.curChar == 35)
/*  790 */                 jjCheckNAdd(12); 
/*      */               break;
/*      */             case 2:
/*  793 */               if ((0xEFFFFFE6FFFFD9FFL & l) != 0L) {
/*      */                 
/*  795 */                 if (kind > 80)
/*  796 */                   kind = 80; 
/*  797 */                 jjCheckNAdd(1);
/*      */               }
/*  799 */               else if ((0x100002600L & l) != 0L) {
/*      */                 
/*  801 */                 if (kind > 79)
/*  802 */                   kind = 79; 
/*  803 */                 jjCheckNAdd(0);
/*      */               }
/*  805 */               else if ((0x1000001800000000L & l) != 0L) {
/*      */                 
/*  807 */                 if (kind > 81)
/*  808 */                   kind = 81; 
/*      */               } 
/*  810 */               if (this.curChar == 60)
/*  811 */                 jjAddStates(7, 8); 
/*  812 */               if (this.curChar == 60)
/*  813 */                 jjCheckNAddStates(9, 100); 
/*  814 */               if (this.curChar == 60)
/*  815 */                 jjCheckNAddStates(101, 147); 
/*      */               break;
/*      */             case 0:
/*  818 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/*  820 */               if (kind > 79)
/*  821 */                 kind = 79; 
/*  822 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 1:
/*  825 */               if ((0xEFFFFFE6FFFFD9FFL & l) == 0L)
/*      */                 break; 
/*  827 */               if (kind > 80)
/*  828 */                 kind = 80; 
/*  829 */               jjCheckNAdd(1);
/*      */               break;
/*      */             case 3:
/*  832 */               if (this.curChar == 60)
/*  833 */                 jjCheckNAddStates(101, 147); 
/*      */               break;
/*      */             case 5:
/*  836 */               if ((0x100002600L & l) != 0L)
/*  837 */                 jjAddStates(148, 149); 
/*      */               break;
/*      */             case 6:
/*  840 */               if (this.curChar == 62 && kind > 6)
/*  841 */                 kind = 6; 
/*      */               break;
/*      */             case 14:
/*  844 */               if ((0x100002600L & l) != 0L)
/*  845 */                 jjAddStates(150, 151); 
/*      */               break;
/*      */             case 15:
/*  848 */               if (this.curChar == 62 && kind > 7)
/*  849 */                 kind = 7; 
/*      */               break;
/*      */             case 23:
/*  852 */               if ((0x100002600L & l) != 0L && kind > 8)
/*  853 */                 kind = 8; 
/*      */               break;
/*      */             case 28:
/*  856 */               if ((0x100002600L & l) != 0L && kind > 9)
/*  857 */                 kind = 9; 
/*      */               break;
/*      */             case 33:
/*  860 */               if ((0x100002600L & l) != 0L && kind > 10)
/*  861 */                 kind = 10; 
/*      */               break;
/*      */             case 38:
/*  864 */               if ((0x100002600L & l) != 0L)
/*  865 */                 jjAddStates(152, 153); 
/*      */               break;
/*      */             case 40:
/*  868 */               if ((0x100002600L & l) != 0L && kind > 11)
/*  869 */                 kind = 11; 
/*      */               break;
/*      */             case 47:
/*  872 */               if ((0x100002600L & l) != 0L)
/*  873 */                 jjAddStates(154, 155); 
/*      */               break;
/*      */             case 48:
/*  876 */               if (this.curChar == 62 && kind > 12)
/*  877 */                 kind = 12; 
/*      */               break;
/*      */             case 54:
/*  880 */               if ((0x100002600L & l) != 0L && kind > 13)
/*  881 */                 kind = 13; 
/*      */               break;
/*      */             case 60:
/*  884 */               if ((0x100002600L & l) != 0L && kind > 14)
/*  885 */                 kind = 14; 
/*      */               break;
/*      */             case 67:
/*  888 */               if ((0x100002600L & l) != 0L && kind > 15)
/*  889 */                 kind = 15; 
/*      */               break;
/*      */             case 72:
/*  892 */               if ((0x100002600L & l) != 0L && kind > 16)
/*  893 */                 kind = 16; 
/*      */               break;
/*      */             case 79:
/*  896 */               if ((0x100002600L & l) != 0L && kind > 17)
/*  897 */                 kind = 17; 
/*      */               break;
/*      */             case 86:
/*  900 */               if ((0x100002600L & l) != 0L && kind > 18)
/*  901 */                 kind = 18; 
/*      */               break;
/*      */             case 92:
/*  904 */               if ((0x100002600L & l) != 0L && kind > 19)
/*  905 */                 kind = 19; 
/*      */               break;
/*      */             case 100:
/*  908 */               if ((0x100002600L & l) != 0L && kind > 20)
/*  909 */                 kind = 20; 
/*      */               break;
/*      */             case 107:
/*  912 */               if ((0x100002600L & l) != 0L && kind > 21)
/*  913 */                 kind = 21; 
/*      */               break;
/*      */             case 116:
/*  916 */               if ((0x100002600L & l) != 0L && kind > 22)
/*  917 */                 kind = 22; 
/*      */               break;
/*      */             case 122:
/*  920 */               if ((0x100002600L & l) != 0L && kind > 23)
/*  921 */                 kind = 23; 
/*      */               break;
/*      */             case 132:
/*  924 */               if ((0x100002600L & l) != 0L && kind > 24)
/*  925 */                 kind = 24; 
/*      */               break;
/*      */             case 138:
/*  928 */               if ((0x100002600L & l) != 0L && kind > 25)
/*  929 */                 kind = 25; 
/*      */               break;
/*      */             case 143:
/*  932 */               if ((0x100002600L & l) != 0L && kind > 26)
/*  933 */                 kind = 26; 
/*      */               break;
/*      */             case 150:
/*  936 */               if ((0x100002600L & l) != 0L && kind > 27)
/*  937 */                 kind = 27; 
/*      */               break;
/*      */             case 155:
/*  940 */               if ((0x100002600L & l) != 0L && kind > 28)
/*  941 */                 kind = 28; 
/*      */               break;
/*      */             case 165:
/*  944 */               if ((0x100002600L & l) != 0L && kind > 29)
/*  945 */                 kind = 29; 
/*      */               break;
/*      */             case 178:
/*  948 */               if ((0x100002600L & l) != 0L)
/*  949 */                 jjAddStates(156, 157); 
/*      */               break;
/*      */             case 179:
/*  952 */               if (this.curChar == 62 && kind > 30)
/*  953 */                 kind = 30; 
/*      */               break;
/*      */             case 187:
/*  956 */               if ((0x100002600L & l) != 0L)
/*  957 */                 jjAddStates(158, 159); 
/*      */               break;
/*      */             case 188:
/*  960 */               if (this.curChar == 62 && kind > 31)
/*  961 */                 kind = 31; 
/*      */               break;
/*      */             case 201:
/*  964 */               if ((0x100002600L & l) != 0L)
/*  965 */                 jjAddStates(160, 161); 
/*      */               break;
/*      */             case 202:
/*  968 */               if (this.curChar == 62 && kind > 32)
/*  969 */                 kind = 32; 
/*      */               break;
/*      */             case 211:
/*  972 */               if ((0x100002600L & l) != 0L)
/*  973 */                 jjAddStates(162, 163); 
/*      */               break;
/*      */             case 212:
/*  976 */               if (this.curChar == 62 && kind > 33)
/*  977 */                 kind = 33; 
/*      */               break;
/*      */             case 222:
/*  980 */               if ((0x100002600L & l) != 0L)
/*  981 */                 jjAddStates(164, 165); 
/*      */               break;
/*      */             case 223:
/*  984 */               if (this.curChar == 62 && kind > 35)
/*  985 */                 kind = 35; 
/*      */               break;
/*      */             case 229:
/*  988 */               if ((0x100002600L & l) != 0L)
/*  989 */                 jjCheckNAddStates(166, 168); 
/*      */               break;
/*      */             case 230:
/*  992 */               if (this.curChar == 47)
/*  993 */                 jjCheckNAdd(231); 
/*      */               break;
/*      */             case 231:
/*  996 */               if (this.curChar == 62 && kind > 54)
/*  997 */                 kind = 54; 
/*      */               break;
/*      */             case 236:
/* 1000 */               if ((0x100002600L & l) != 0L)
/* 1001 */                 jjCheckNAddStates(169, 171); 
/*      */               break;
/*      */             case 237:
/* 1004 */               if (this.curChar == 47)
/* 1005 */                 jjCheckNAdd(238); 
/*      */               break;
/*      */             case 238:
/* 1008 */               if (this.curChar == 62 && kind > 55)
/* 1009 */                 kind = 55; 
/*      */               break;
/*      */             case 244:
/* 1012 */               if ((0x100002600L & l) != 0L)
/* 1013 */                 jjCheckNAddStates(172, 174); 
/*      */               break;
/*      */             case 245:
/* 1016 */               if (this.curChar == 47)
/* 1017 */                 jjCheckNAdd(246); 
/*      */               break;
/*      */             case 246:
/* 1020 */               if (this.curChar == 62 && kind > 56)
/* 1021 */                 kind = 56; 
/*      */               break;
/*      */             case 255:
/* 1024 */               if ((0x100002600L & l) != 0L)
/* 1025 */                 jjCheckNAddStates(175, 177); 
/*      */               break;
/*      */             case 256:
/* 1028 */               if (this.curChar == 47)
/* 1029 */                 jjCheckNAdd(257); 
/*      */               break;
/*      */             case 257:
/* 1032 */               if (this.curChar == 62 && kind > 57)
/* 1033 */                 kind = 57; 
/*      */               break;
/*      */             case 264:
/* 1036 */               if ((0x100002600L & l) != 0L)
/* 1037 */                 jjCheckNAddStates(178, 180); 
/*      */               break;
/*      */             case 265:
/* 1040 */               if (this.curChar == 47)
/* 1041 */                 jjCheckNAdd(266); 
/*      */               break;
/*      */             case 266:
/* 1044 */               if (this.curChar == 62 && kind > 58)
/* 1045 */                 kind = 58; 
/*      */               break;
/*      */             case 271:
/* 1048 */               if ((0x100002600L & l) != 0L)
/* 1049 */                 jjCheckNAddStates(181, 183); 
/*      */               break;
/*      */             case 272:
/* 1052 */               if (this.curChar == 47)
/* 1053 */                 jjCheckNAdd(273); 
/*      */               break;
/*      */             case 273:
/* 1056 */               if (this.curChar == 62 && kind > 59)
/* 1057 */                 kind = 59; 
/*      */               break;
/*      */             case 279:
/* 1060 */               if ((0x100002600L & l) != 0L)
/* 1061 */                 jjCheckNAddStates(184, 186); 
/*      */               break;
/*      */             case 280:
/* 1064 */               if (this.curChar == 47)
/* 1065 */                 jjCheckNAdd(281); 
/*      */               break;
/*      */             case 281:
/* 1068 */               if (this.curChar == 62 && kind > 60)
/* 1069 */                 kind = 60; 
/*      */               break;
/*      */             case 283:
/* 1072 */               if ((0x100002600L & l) != 0L)
/* 1073 */                 jjCheckNAddStates(187, 189); 
/*      */               break;
/*      */             case 284:
/* 1076 */               if (this.curChar == 47)
/* 1077 */                 jjCheckNAdd(285); 
/*      */               break;
/*      */             case 285:
/* 1080 */               if (this.curChar == 62 && kind > 61)
/* 1081 */                 kind = 61; 
/*      */               break;
/*      */             case 288:
/* 1084 */               if ((0x100002600L & l) != 0L)
/* 1085 */                 jjCheckNAddStates(190, 192); 
/*      */               break;
/*      */             case 289:
/* 1088 */               if (this.curChar == 47)
/* 1089 */                 jjCheckNAdd(290); 
/*      */               break;
/*      */             case 290:
/* 1092 */               if (this.curChar == 62 && kind > 62)
/* 1093 */                 kind = 62; 
/*      */               break;
/*      */             case 293:
/* 1096 */               if ((0x100002600L & l) != 0L)
/* 1097 */                 jjCheckNAddStates(193, 195); 
/*      */               break;
/*      */             case 294:
/* 1100 */               if (this.curChar == 47)
/* 1101 */                 jjCheckNAdd(295); 
/*      */               break;
/*      */             case 295:
/* 1104 */               if (this.curChar == 62 && kind > 63)
/* 1105 */                 kind = 63; 
/*      */               break;
/*      */             case 298:
/* 1108 */               if ((0x100002600L & l) != 0L)
/* 1109 */                 jjAddStates(196, 197); 
/*      */               break;
/*      */             case 299:
/* 1112 */               if (this.curChar == 62 && kind > 64)
/* 1113 */                 kind = 64; 
/*      */               break;
/*      */             case 307:
/* 1116 */               if ((0x100002600L & l) != 0L)
/* 1117 */                 jjCheckNAddStates(198, 200); 
/*      */               break;
/*      */             case 308:
/* 1120 */               if (this.curChar == 47)
/* 1121 */                 jjCheckNAdd(309); 
/*      */               break;
/*      */             case 309:
/* 1124 */               if (this.curChar == 62 && kind > 65)
/* 1125 */                 kind = 65; 
/*      */               break;
/*      */             case 316:
/* 1128 */               if ((0x100002600L & l) != 0L && kind > 66)
/* 1129 */                 kind = 66; 
/*      */               break;
/*      */             case 323:
/* 1132 */               if ((0x100002600L & l) != 0L)
/* 1133 */                 jjCheckNAddStates(201, 203); 
/*      */               break;
/*      */             case 324:
/* 1136 */               if (this.curChar == 47)
/* 1137 */                 jjCheckNAdd(325); 
/*      */               break;
/*      */             case 325:
/* 1140 */               if (this.curChar == 62 && kind > 67)
/* 1141 */                 kind = 67; 
/*      */               break;
/*      */             case 333:
/* 1144 */               if ((0x100002600L & l) != 0L && kind > 68)
/* 1145 */                 kind = 68; 
/*      */               break;
/*      */             case 341:
/* 1148 */               if ((0x100002600L & l) != 0L)
/* 1149 */                 jjCheckNAddStates(204, 206); 
/*      */               break;
/*      */             case 342:
/* 1152 */               if (this.curChar == 47)
/* 1153 */                 jjCheckNAdd(343); 
/*      */               break;
/*      */             case 343:
/* 1156 */               if (this.curChar == 62 && kind > 69)
/* 1157 */                 kind = 69; 
/*      */               break;
/*      */             case 352:
/* 1160 */               if ((0x100002600L & l) != 0L && kind > 70)
/* 1161 */                 kind = 70; 
/*      */               break;
/*      */             case 361:
/* 1164 */               if ((0x100002600L & l) != 0L)
/* 1165 */                 jjAddStates(207, 208); 
/*      */               break;
/*      */             case 362:
/* 1168 */               if (this.curChar == 62 && kind > 72)
/* 1169 */                 kind = 72; 
/*      */               break;
/*      */             case 368:
/* 1172 */               if (this.curChar == 60)
/* 1173 */                 jjCheckNAddStates(9, 100); 
/*      */               break;
/*      */             case 369:
/* 1176 */               if (this.curChar == 35)
/* 1177 */                 jjCheckNAdd(12); 
/*      */               break;
/*      */             case 370:
/* 1180 */               if (this.curChar == 35)
/* 1181 */                 jjCheckNAdd(21); 
/*      */               break;
/*      */             case 371:
/* 1184 */               if (this.curChar == 35)
/* 1185 */                 jjCheckNAdd(24); 
/*      */               break;
/*      */             case 372:
/* 1188 */               if (this.curChar == 35)
/* 1189 */                 jjCheckNAdd(31); 
/*      */               break;
/*      */             case 373:
/* 1192 */               if (this.curChar == 35)
/* 1193 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 374:
/* 1196 */               if (this.curChar == 35)
/* 1197 */                 jjCheckNAdd(45); 
/*      */               break;
/*      */             case 375:
/* 1200 */               if (this.curChar == 35)
/* 1201 */                 jjCheckNAdd(50); 
/*      */               break;
/*      */             case 376:
/* 1204 */               if (this.curChar == 35)
/* 1205 */                 jjCheckNAdd(58); 
/*      */               break;
/*      */             case 377:
/* 1208 */               if (this.curChar == 35)
/* 1209 */                 jjCheckNAdd(65); 
/*      */               break;
/*      */             case 378:
/* 1212 */               if (this.curChar == 35)
/* 1213 */                 jjCheckNAdd(70); 
/*      */               break;
/*      */             case 379:
/* 1216 */               if (this.curChar == 35)
/* 1217 */                 jjCheckNAdd(77); 
/*      */               break;
/*      */             case 380:
/* 1220 */               if (this.curChar == 35)
/* 1221 */                 jjCheckNAdd(84); 
/*      */               break;
/*      */             case 381:
/* 1224 */               if (this.curChar == 35)
/* 1225 */                 jjCheckNAdd(90); 
/*      */               break;
/*      */             case 382:
/* 1228 */               if (this.curChar == 35)
/* 1229 */                 jjCheckNAdd(98); 
/*      */               break;
/*      */             case 383:
/* 1232 */               if (this.curChar == 35)
/* 1233 */                 jjCheckNAdd(105); 
/*      */               break;
/*      */             case 384:
/* 1236 */               if (this.curChar == 35)
/* 1237 */                 jjCheckNAdd(114); 
/*      */               break;
/*      */             case 385:
/* 1240 */               if (this.curChar == 35)
/* 1241 */                 jjCheckNAdd(120); 
/*      */               break;
/*      */             case 386:
/* 1244 */               if (this.curChar == 35)
/* 1245 */                 jjCheckNAdd(130); 
/*      */               break;
/*      */             case 387:
/* 1248 */               if (this.curChar == 35)
/* 1249 */                 jjCheckNAdd(136); 
/*      */               break;
/*      */             case 388:
/* 1252 */               if (this.curChar == 35)
/* 1253 */                 jjCheckNAdd(141); 
/*      */               break;
/*      */             case 389:
/* 1256 */               if (this.curChar == 35)
/* 1257 */                 jjCheckNAdd(148); 
/*      */               break;
/*      */             case 390:
/* 1260 */               if (this.curChar == 35)
/* 1261 */                 jjCheckNAdd(153); 
/*      */               break;
/*      */             case 391:
/* 1264 */               if (this.curChar == 35)
/* 1265 */                 jjCheckNAdd(161); 
/*      */               break;
/*      */             case 392:
/* 1268 */               if (this.curChar == 35)
/* 1269 */                 jjCheckNAdd(174); 
/*      */               break;
/*      */             case 393:
/* 1272 */               if (this.curChar == 35)
/* 1273 */                 jjCheckNAdd(183); 
/*      */               break;
/*      */             case 394:
/* 1276 */               if (this.curChar == 35)
/* 1277 */                 jjCheckNAdd(199); 
/*      */               break;
/*      */             case 395:
/* 1280 */               if (this.curChar == 35)
/* 1281 */                 jjCheckNAdd(209); 
/*      */               break;
/*      */             case 396:
/* 1284 */               if (this.curChar == 35)
/* 1285 */                 jjCheckNAdd(218); 
/*      */               break;
/*      */             case 397:
/* 1288 */               if (this.curChar == 35)
/* 1289 */                 jjCheckNAdd(227); 
/*      */               break;
/*      */             case 398:
/* 1292 */               if (this.curChar == 47)
/* 1293 */                 jjCheckNAdd(402); 
/*      */               break;
/*      */             case 400:
/* 1296 */               if ((0x100002600L & l) != 0L)
/* 1297 */                 jjAddStates(209, 210); 
/*      */               break;
/*      */             case 401:
/* 1300 */               if (this.curChar == 62 && kind > 36)
/* 1301 */                 kind = 36; 
/*      */               break;
/*      */             case 403:
/* 1304 */               if (this.curChar == 35)
/* 1305 */                 jjCheckNAdd(402); 
/*      */               break;
/*      */             case 404:
/* 1308 */               if (this.curChar == 47)
/* 1309 */                 jjCheckNAdd(403); 
/*      */               break;
/*      */             case 405:
/* 1312 */               if (this.curChar == 47)
/* 1313 */                 jjCheckNAdd(411); 
/*      */               break;
/*      */             case 407:
/* 1316 */               if ((0x100002600L & l) != 0L)
/* 1317 */                 jjAddStates(211, 212); 
/*      */               break;
/*      */             case 408:
/* 1320 */               if (this.curChar == 62 && kind > 37)
/* 1321 */                 kind = 37; 
/*      */               break;
/*      */             case 412:
/* 1324 */               if (this.curChar == 35)
/* 1325 */                 jjCheckNAdd(411); 
/*      */               break;
/*      */             case 413:
/* 1328 */               if (this.curChar == 47)
/* 1329 */                 jjCheckNAdd(412); 
/*      */               break;
/*      */             case 414:
/* 1332 */               if (this.curChar == 47)
/* 1333 */                 jjCheckNAdd(421); 
/*      */               break;
/*      */             case 416:
/* 1336 */               if ((0x100002600L & l) != 0L)
/* 1337 */                 jjAddStates(213, 214); 
/*      */               break;
/*      */             case 417:
/* 1340 */               if (this.curChar == 62 && kind > 38)
/* 1341 */                 kind = 38; 
/*      */               break;
/*      */             case 422:
/* 1344 */               if (this.curChar == 35)
/* 1345 */                 jjCheckNAdd(421); 
/*      */               break;
/*      */             case 423:
/* 1348 */               if (this.curChar == 47)
/* 1349 */                 jjCheckNAdd(422); 
/*      */               break;
/*      */             case 424:
/* 1352 */               if (this.curChar == 47)
/* 1353 */                 jjCheckNAdd(429); 
/*      */               break;
/*      */             case 426:
/* 1356 */               if ((0x100002600L & l) != 0L)
/* 1357 */                 jjAddStates(215, 216); 
/*      */               break;
/*      */             case 427:
/* 1360 */               if (this.curChar == 62 && kind > 39)
/* 1361 */                 kind = 39; 
/*      */               break;
/*      */             case 430:
/* 1364 */               if (this.curChar == 35)
/* 1365 */                 jjCheckNAdd(429); 
/*      */               break;
/*      */             case 431:
/* 1368 */               if (this.curChar == 47)
/* 1369 */                 jjCheckNAdd(430); 
/*      */               break;
/*      */             case 432:
/* 1372 */               if (this.curChar == 47)
/* 1373 */                 jjCheckNAdd(441); 
/*      */               break;
/*      */             case 434:
/* 1376 */               if ((0x100002600L & l) != 0L)
/* 1377 */                 jjAddStates(217, 218); 
/*      */               break;
/*      */             case 435:
/* 1380 */               if (this.curChar == 62 && kind > 40)
/* 1381 */                 kind = 40; 
/*      */               break;
/*      */             case 442:
/* 1384 */               if (this.curChar == 35)
/* 1385 */                 jjCheckNAdd(441); 
/*      */               break;
/*      */             case 443:
/* 1388 */               if (this.curChar == 47)
/* 1389 */                 jjCheckNAdd(442); 
/*      */               break;
/*      */             case 444:
/* 1392 */               if (this.curChar == 47)
/* 1393 */                 jjCheckNAdd(453); 
/*      */               break;
/*      */             case 446:
/* 1396 */               if ((0x100002600L & l) != 0L)
/* 1397 */                 jjAddStates(219, 220); 
/*      */               break;
/*      */             case 447:
/* 1400 */               if (this.curChar == 62 && kind > 41)
/* 1401 */                 kind = 41; 
/*      */               break;
/*      */             case 454:
/* 1404 */               if (this.curChar == 35)
/* 1405 */                 jjCheckNAdd(453); 
/*      */               break;
/*      */             case 455:
/* 1408 */               if (this.curChar == 47)
/* 1409 */                 jjCheckNAdd(454); 
/*      */               break;
/*      */             case 456:
/* 1412 */               if (this.curChar == 47)
/* 1413 */                 jjCheckNAdd(465); 
/*      */               break;
/*      */             case 460:
/* 1416 */               if ((0x100002600L & l) != 0L)
/* 1417 */                 jjAddStates(221, 222); 
/*      */               break;
/*      */             case 461:
/* 1420 */               if (this.curChar == 62 && kind > 42)
/* 1421 */                 kind = 42; 
/*      */               break;
/*      */             case 466:
/* 1424 */               if (this.curChar == 35)
/* 1425 */                 jjCheckNAdd(465); 
/*      */               break;
/*      */             case 467:
/* 1428 */               if (this.curChar == 47)
/* 1429 */                 jjCheckNAdd(466); 
/*      */               break;
/*      */             case 468:
/* 1432 */               if (this.curChar == 47)
/* 1433 */                 jjCheckNAdd(475); 
/*      */               break;
/*      */             case 470:
/* 1436 */               if ((0x100002600L & l) != 0L)
/* 1437 */                 jjAddStates(223, 224); 
/*      */               break;
/*      */             case 471:
/* 1440 */               if (this.curChar == 62 && kind > 43)
/* 1441 */                 kind = 43; 
/*      */               break;
/*      */             case 476:
/* 1444 */               if (this.curChar == 35)
/* 1445 */                 jjCheckNAdd(475); 
/*      */               break;
/*      */             case 477:
/* 1448 */               if (this.curChar == 47)
/* 1449 */                 jjCheckNAdd(476); 
/*      */               break;
/*      */             case 478:
/* 1452 */               if (this.curChar == 47)
/* 1453 */                 jjCheckNAdd(486); 
/*      */               break;
/*      */             case 480:
/* 1456 */               if ((0x100002600L & l) != 0L)
/* 1457 */                 jjAddStates(225, 226); 
/*      */               break;
/*      */             case 481:
/* 1460 */               if (this.curChar == 62 && kind > 44)
/* 1461 */                 kind = 44; 
/*      */               break;
/*      */             case 487:
/* 1464 */               if (this.curChar == 35)
/* 1465 */                 jjCheckNAdd(486); 
/*      */               break;
/*      */             case 488:
/* 1468 */               if (this.curChar == 47)
/* 1469 */                 jjCheckNAdd(487); 
/*      */               break;
/*      */             case 489:
/* 1472 */               if (this.curChar == 47)
/* 1473 */                 jjCheckNAdd(497); 
/*      */               break;
/*      */             case 491:
/* 1476 */               if ((0x100002600L & l) != 0L)
/* 1477 */                 jjAddStates(227, 228); 
/*      */               break;
/*      */             case 492:
/* 1480 */               if (this.curChar == 62 && kind > 45)
/* 1481 */                 kind = 45; 
/*      */               break;
/*      */             case 498:
/* 1484 */               if (this.curChar == 35)
/* 1485 */                 jjCheckNAdd(497); 
/*      */               break;
/*      */             case 499:
/* 1488 */               if (this.curChar == 47)
/* 1489 */                 jjCheckNAdd(498); 
/*      */               break;
/*      */             case 500:
/* 1492 */               if (this.curChar == 47)
/* 1493 */                 jjCheckNAdd(510); 
/*      */               break;
/*      */             case 502:
/* 1496 */               if ((0x100002600L & l) != 0L)
/* 1497 */                 jjAddStates(229, 230); 
/*      */               break;
/*      */             case 503:
/* 1500 */               if (this.curChar == 62 && kind > 46)
/* 1501 */                 kind = 46; 
/*      */               break;
/*      */             case 511:
/* 1504 */               if (this.curChar == 35)
/* 1505 */                 jjCheckNAdd(510); 
/*      */               break;
/*      */             case 512:
/* 1508 */               if (this.curChar == 47)
/* 1509 */                 jjCheckNAdd(511); 
/*      */               break;
/*      */             case 513:
/* 1512 */               if (this.curChar == 47)
/* 1513 */                 jjCheckNAdd(520); 
/*      */               break;
/*      */             case 515:
/* 1516 */               if ((0x100002600L & l) != 0L)
/* 1517 */                 jjAddStates(231, 232); 
/*      */               break;
/*      */             case 516:
/* 1520 */               if (this.curChar == 62 && kind > 47)
/* 1521 */                 kind = 47; 
/*      */               break;
/*      */             case 521:
/* 1524 */               if (this.curChar == 35)
/* 1525 */                 jjCheckNAdd(520); 
/*      */               break;
/*      */             case 522:
/* 1528 */               if (this.curChar == 47)
/* 1529 */                 jjCheckNAdd(521); 
/*      */               break;
/*      */             case 523:
/* 1532 */               if (this.curChar == 47)
/* 1533 */                 jjCheckNAdd(537); 
/*      */               break;
/*      */             case 527:
/* 1536 */               if ((0x100002600L & l) != 0L)
/* 1537 */                 jjAddStates(233, 234); 
/*      */               break;
/*      */             case 528:
/* 1540 */               if (this.curChar == 62 && kind > 48)
/* 1541 */                 kind = 48; 
/*      */               break;
/*      */             case 538:
/* 1544 */               if (this.curChar == 35)
/* 1545 */                 jjCheckNAdd(537); 
/*      */               break;
/*      */             case 539:
/* 1548 */               if (this.curChar == 47)
/* 1549 */                 jjCheckNAdd(538); 
/*      */               break;
/*      */             case 540:
/* 1552 */               if (this.curChar == 47)
/* 1553 */                 jjCheckNAdd(549); 
/*      */               break;
/*      */             case 544:
/* 1556 */               if ((0x100002600L & l) != 0L)
/* 1557 */                 jjAddStates(235, 236); 
/*      */               break;
/*      */             case 545:
/* 1560 */               if (this.curChar == 62 && kind > 49)
/* 1561 */                 kind = 49; 
/*      */               break;
/*      */             case 550:
/* 1564 */               if (this.curChar == 35)
/* 1565 */                 jjCheckNAdd(549); 
/*      */               break;
/*      */             case 551:
/* 1568 */               if (this.curChar == 47)
/* 1569 */                 jjCheckNAdd(550); 
/*      */               break;
/*      */             case 552:
/* 1572 */               if (this.curChar == 47)
/* 1573 */                 jjCheckNAdd(568); 
/*      */               break;
/*      */             case 556:
/* 1576 */               if ((0x100002600L & l) != 0L)
/* 1577 */                 jjAddStates(237, 238); 
/*      */               break;
/*      */             case 557:
/* 1580 */               if (this.curChar == 62 && kind > 50)
/* 1581 */                 kind = 50; 
/*      */               break;
/*      */             case 569:
/* 1584 */               if (this.curChar == 35)
/* 1585 */                 jjCheckNAdd(568); 
/*      */               break;
/*      */             case 570:
/* 1588 */               if (this.curChar == 47)
/* 1589 */                 jjCheckNAdd(569); 
/*      */               break;
/*      */             case 571:
/* 1592 */               if (this.curChar == 47)
/* 1593 */                 jjCheckNAdd(581); 
/*      */               break;
/*      */             case 573:
/* 1596 */               if ((0x100002600L & l) != 0L)
/* 1597 */                 jjAddStates(239, 240); 
/*      */               break;
/*      */             case 574:
/* 1600 */               if (this.curChar == 62 && kind > 51)
/* 1601 */                 kind = 51; 
/*      */               break;
/*      */             case 582:
/* 1604 */               if (this.curChar == 35)
/* 1605 */                 jjCheckNAdd(581); 
/*      */               break;
/*      */             case 583:
/* 1608 */               if (this.curChar == 47)
/* 1609 */                 jjCheckNAdd(582); 
/*      */               break;
/*      */             case 584:
/* 1612 */               if (this.curChar == 47)
/* 1613 */                 jjCheckNAdd(595); 
/*      */               break;
/*      */             case 586:
/* 1616 */               if ((0x100002600L & l) != 0L)
/* 1617 */                 jjAddStates(241, 242); 
/*      */               break;
/*      */             case 587:
/* 1620 */               if (this.curChar == 62 && kind > 52)
/* 1621 */                 kind = 52; 
/*      */               break;
/*      */             case 596:
/* 1624 */               if (this.curChar == 35)
/* 1625 */                 jjCheckNAdd(595); 
/*      */               break;
/*      */             case 597:
/* 1628 */               if (this.curChar == 47)
/* 1629 */                 jjCheckNAdd(596); 
/*      */               break;
/*      */             case 598:
/* 1632 */               if (this.curChar == 47)
/* 1633 */                 jjCheckNAdd(606); 
/*      */               break;
/*      */             case 600:
/* 1636 */               if ((0x100002600L & l) != 0L)
/* 1637 */                 jjAddStates(243, 244); 
/*      */               break;
/*      */             case 601:
/* 1640 */               if (this.curChar == 62 && kind > 53)
/* 1641 */                 kind = 53; 
/*      */               break;
/*      */             case 607:
/* 1644 */               if (this.curChar == 35)
/* 1645 */                 jjCheckNAdd(606); 
/*      */               break;
/*      */             case 608:
/* 1648 */               if (this.curChar == 47)
/* 1649 */                 jjCheckNAdd(607); 
/*      */               break;
/*      */             case 609:
/* 1652 */               if (this.curChar == 35)
/* 1653 */                 jjCheckNAdd(234); 
/*      */               break;
/*      */             case 610:
/* 1656 */               if (this.curChar == 35)
/* 1657 */                 jjCheckNAdd(242); 
/*      */               break;
/*      */             case 611:
/* 1660 */               if (this.curChar == 35)
/* 1661 */                 jjCheckNAdd(253); 
/*      */               break;
/*      */             case 612:
/* 1664 */               if (this.curChar == 35)
/* 1665 */                 jjCheckNAdd(262); 
/*      */               break;
/*      */             case 613:
/* 1668 */               if (this.curChar == 35)
/* 1669 */                 jjCheckNAdd(269); 
/*      */               break;
/*      */             case 614:
/* 1672 */               if (this.curChar == 35)
/* 1673 */                 jjCheckNAdd(277); 
/*      */               break;
/*      */             case 615:
/* 1676 */               if (this.curChar == 35)
/* 1677 */                 jjCheckNAdd(278); 
/*      */               break;
/*      */             case 616:
/* 1680 */               if (this.curChar == 35)
/* 1681 */                 jjCheckNAdd(286); 
/*      */               break;
/*      */             case 617:
/* 1684 */               if (this.curChar == 35)
/* 1685 */                 jjCheckNAdd(291); 
/*      */               break;
/*      */             case 618:
/* 1688 */               if (this.curChar == 35)
/* 1689 */                 jjCheckNAdd(296); 
/*      */               break;
/*      */             case 619:
/* 1692 */               if (this.curChar == 35)
/* 1693 */                 jjCheckNAdd(305); 
/*      */               break;
/*      */             case 620:
/* 1696 */               if (this.curChar == 35)
/* 1697 */                 jjCheckNAdd(314); 
/*      */               break;
/*      */             case 621:
/* 1700 */               if (this.curChar == 35)
/* 1701 */                 jjCheckNAdd(321); 
/*      */               break;
/*      */             case 622:
/* 1704 */               if (this.curChar == 35)
/* 1705 */                 jjCheckNAdd(331); 
/*      */               break;
/*      */             case 623:
/* 1708 */               if (this.curChar == 35)
/* 1709 */                 jjCheckNAdd(339); 
/*      */               break;
/*      */             case 624:
/* 1712 */               if (this.curChar == 35)
/* 1713 */                 jjCheckNAdd(350); 
/*      */               break;
/*      */             case 625:
/* 1716 */               if (this.curChar == 35)
/* 1717 */                 jjCheckNAdd(357); 
/*      */               break;
/*      */             case 626:
/* 1720 */               if (this.curChar == 47)
/* 1721 */                 jjCheckNAdd(634); 
/*      */               break;
/*      */             case 628:
/* 1724 */               if ((0x100002600L & l) != 0L)
/* 1725 */                 jjAddStates(245, 246); 
/*      */               break;
/*      */             case 629:
/* 1728 */               if (this.curChar == 62 && kind > 71)
/* 1729 */                 kind = 71; 
/*      */               break;
/*      */             case 635:
/* 1732 */               if (this.curChar == 35)
/* 1733 */                 jjCheckNAdd(634); 
/*      */               break;
/*      */             case 636:
/* 1736 */               if (this.curChar == 47)
/* 1737 */                 jjCheckNAdd(635); 
/*      */               break;
/*      */             case 637:
/* 1740 */               if (this.curChar == 35)
/* 1741 */                 jjCheckNAdd(367); 
/*      */               break;
/*      */             case 638:
/* 1744 */               if (this.curChar == 47)
/* 1745 */                 jjCheckNAdd(648); 
/*      */               break;
/*      */             case 642:
/* 1748 */               if ((0x100002600L & l) != 0L)
/* 1749 */                 jjAddStates(247, 248); 
/*      */               break;
/*      */             case 643:
/* 1752 */               if (this.curChar == 62 && kind > 73)
/* 1753 */                 kind = 73; 
/*      */               break;
/*      */             case 649:
/* 1756 */               if (this.curChar == 35)
/* 1757 */                 jjCheckNAdd(648); 
/*      */               break;
/*      */             case 650:
/* 1760 */               if (this.curChar == 47)
/* 1761 */                 jjCheckNAdd(649); 
/*      */               break;
/*      */             case 653:
/* 1764 */               if ((0x100002600L & l) != 0L && kind > 76)
/* 1765 */                 kind = 76; 
/*      */               break;
/*      */             case 656:
/* 1768 */               if (this.curChar == 35)
/* 1769 */                 this.jjstateSet[this.jjnewStateCnt++] = 655; 
/*      */               break;
/*      */             case 658:
/* 1772 */               if (this.curChar == 47)
/* 1773 */                 this.jjstateSet[this.jjnewStateCnt++] = 659; 
/*      */               break;
/*      */             case 659:
/* 1776 */               if (this.curChar == 62 && kind > 77)
/* 1777 */                 kind = 77; 
/*      */               break;
/*      */             case 662:
/* 1780 */               if (this.curChar == 35)
/* 1781 */                 this.jjstateSet[this.jjnewStateCnt++] = 661; 
/*      */               break;
/*      */             case 663:
/* 1784 */               if (this.curChar == 35)
/* 1785 */                 this.jjstateSet[this.jjnewStateCnt++] = 664; 
/*      */               break;
/*      */             case 665:
/* 1788 */               if (this.curChar == 47)
/* 1789 */                 jjCheckNAdd(663); 
/*      */               break;
/*      */             case 667:
/* 1792 */               if (this.curChar == 47)
/* 1793 */                 jjCheckNAdd(403); 
/*      */               break;
/*      */             case 668:
/* 1796 */               if (this.curChar == 47)
/* 1797 */                 jjCheckNAdd(412); 
/*      */               break;
/*      */             case 669:
/* 1800 */               if (this.curChar == 47)
/* 1801 */                 jjCheckNAdd(422); 
/*      */               break;
/*      */             case 670:
/* 1804 */               if (this.curChar == 47)
/* 1805 */                 jjCheckNAdd(430); 
/*      */               break;
/*      */             case 671:
/* 1808 */               if (this.curChar == 47)
/* 1809 */                 jjCheckNAdd(442); 
/*      */               break;
/*      */             case 672:
/* 1812 */               if (this.curChar == 47)
/* 1813 */                 jjCheckNAdd(454); 
/*      */               break;
/*      */             case 673:
/* 1816 */               if (this.curChar == 47)
/* 1817 */                 jjCheckNAdd(466); 
/*      */               break;
/*      */             case 674:
/* 1820 */               if (this.curChar == 47)
/* 1821 */                 jjCheckNAdd(476); 
/*      */               break;
/*      */             case 675:
/* 1824 */               if (this.curChar == 47)
/* 1825 */                 jjCheckNAdd(487); 
/*      */               break;
/*      */             case 676:
/* 1828 */               if (this.curChar == 47)
/* 1829 */                 jjCheckNAdd(498); 
/*      */               break;
/*      */             case 677:
/* 1832 */               if (this.curChar == 47)
/* 1833 */                 jjCheckNAdd(511); 
/*      */               break;
/*      */             case 678:
/* 1836 */               if (this.curChar == 47)
/* 1837 */                 jjCheckNAdd(521); 
/*      */               break;
/*      */             case 679:
/* 1840 */               if (this.curChar == 47)
/* 1841 */                 jjCheckNAdd(538); 
/*      */               break;
/*      */             case 680:
/* 1844 */               if (this.curChar == 47)
/* 1845 */                 jjCheckNAdd(550); 
/*      */               break;
/*      */             case 681:
/* 1848 */               if (this.curChar == 47)
/* 1849 */                 jjCheckNAdd(569); 
/*      */               break;
/*      */             case 682:
/* 1852 */               if (this.curChar == 47)
/* 1853 */                 jjCheckNAdd(582); 
/*      */               break;
/*      */             case 683:
/* 1856 */               if (this.curChar == 47)
/* 1857 */                 jjCheckNAdd(596); 
/*      */               break;
/*      */             case 684:
/* 1860 */               if (this.curChar == 47)
/* 1861 */                 jjCheckNAdd(607); 
/*      */               break;
/*      */             case 685:
/* 1864 */               if (this.curChar == 47)
/* 1865 */                 jjCheckNAdd(635); 
/*      */               break;
/*      */             case 686:
/* 1868 */               if (this.curChar == 47)
/* 1869 */                 jjCheckNAdd(649); 
/*      */               break;
/*      */             case 689:
/* 1872 */               if (this.curChar == 35)
/* 1873 */                 this.jjstateSet[this.jjnewStateCnt++] = 688; 
/*      */               break;
/*      */             case 692:
/* 1876 */               if (this.curChar == 35)
/* 1877 */                 this.jjstateSet[this.jjnewStateCnt++] = 691; 
/*      */               break;
/*      */             case 693:
/* 1880 */               if (this.curChar == 47)
/* 1881 */                 jjCheckNAdd(663); 
/*      */               break;
/*      */             case 694:
/* 1884 */               if (this.curChar == 60)
/* 1885 */                 jjAddStates(7, 8); 
/*      */               break;
/*      */             case 695:
/* 1888 */               if (this.curChar == 45 && kind > 34)
/* 1889 */                 kind = 34; 
/*      */               break;
/*      */             case 696:
/* 1892 */               if (this.curChar == 45)
/* 1893 */                 this.jjstateSet[this.jjnewStateCnt++] = 695; 
/*      */               break;
/*      */             case 699:
/* 1896 */               if (this.curChar == 36)
/* 1897 */                 jjCheckNAddStates(249, 253); 
/*      */               break;
/*      */             case 700:
/* 1900 */               if ((0x3FF001000000000L & l) != 0L)
/* 1901 */                 jjCheckNAddStates(249, 253); 
/*      */               break;
/*      */             case 702:
/* 1904 */               if ((0x400600800000000L & l) != 0L)
/* 1905 */                 jjCheckNAddStates(249, 253); 
/*      */               break;
/*      */             case 703:
/* 1908 */               if (this.curChar == 46)
/* 1909 */                 jjAddStates(254, 255); 
/*      */               break;
/*      */             case 704:
/* 1912 */               if (this.curChar == 36)
/* 1913 */                 jjCheckNAddStates(256, 260); 
/*      */               break;
/*      */             case 705:
/* 1916 */               if ((0x3FF001000000000L & l) != 0L)
/* 1917 */                 jjCheckNAddStates(256, 260); 
/*      */               break;
/*      */             case 707:
/* 1920 */               if ((0x400600800000000L & l) != 0L)
/* 1921 */                 jjCheckNAddStates(256, 260); 
/*      */               break;
/*      */             case 708:
/* 1924 */               if ((0x100002600L & l) != 0L)
/* 1925 */                 jjCheckNAddTwoStates(708, 709); 
/*      */               break;
/*      */             case 709:
/* 1928 */               if (this.curChar == 62 && kind > 75)
/* 1929 */                 kind = 75; 
/*      */               break;
/*      */             case 712:
/* 1932 */               if (this.curChar == 47) {
/* 1933 */                 this.jjstateSet[this.jjnewStateCnt++] = 698;
/*      */               }
/*      */               break;
/*      */           } 
/* 1937 */         } while (i != startsAt);
/*      */       }
/* 1939 */       else if (this.curChar < 128) {
/*      */         
/* 1941 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 1944 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 651:
/*      */             case 697:
/* 1948 */               if (this.curChar == 64 && kind > 74)
/* 1949 */                 kind = 74; 
/*      */               break;
/*      */             case 2:
/* 1952 */               if ((0xF7FFFFFFF7FFFFFFL & l) != 0L) {
/*      */                 
/* 1954 */                 if (kind > 80)
/* 1955 */                   kind = 80; 
/* 1956 */                 jjCheckNAdd(1);
/*      */               }
/* 1958 */               else if ((0x800000008000000L & l) != 0L) {
/*      */                 
/* 1960 */                 if (kind > 81)
/* 1961 */                   kind = 81; 
/*      */               } 
/* 1963 */               if (this.curChar == 91)
/* 1964 */                 jjAddStates(7, 8); 
/* 1965 */               if (this.curChar == 91)
/* 1966 */                 jjAddStates(261, 332); 
/*      */               break;
/*      */             case 1:
/* 1969 */               if ((0xF7FFFFFFF7FFFFFFL & l) == 0L)
/*      */                 break; 
/* 1971 */               if (kind > 80)
/* 1972 */                 kind = 80; 
/* 1973 */               jjCheckNAdd(1);
/*      */               break;
/*      */             case 4:
/* 1976 */               if (this.curChar == 116)
/* 1977 */                 jjAddStates(148, 149); 
/*      */               break;
/*      */             case 6:
/* 1980 */               if (this.curChar == 93 && kind > 6)
/* 1981 */                 kind = 6; 
/*      */               break;
/*      */             case 7:
/* 1984 */               if (this.curChar == 112)
/* 1985 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; 
/*      */               break;
/*      */             case 8:
/* 1988 */               if (this.curChar == 109)
/* 1989 */                 this.jjstateSet[this.jjnewStateCnt++] = 7; 
/*      */               break;
/*      */             case 9:
/* 1992 */               if (this.curChar == 101)
/* 1993 */                 this.jjstateSet[this.jjnewStateCnt++] = 8; 
/*      */               break;
/*      */             case 10:
/* 1996 */               if (this.curChar == 116)
/* 1997 */                 this.jjstateSet[this.jjnewStateCnt++] = 9; 
/*      */               break;
/*      */             case 11:
/* 2000 */               if (this.curChar == 116)
/* 2001 */                 this.jjstateSet[this.jjnewStateCnt++] = 10; 
/*      */               break;
/*      */             case 12:
/* 2004 */               if (this.curChar == 97)
/* 2005 */                 this.jjstateSet[this.jjnewStateCnt++] = 11; 
/*      */               break;
/*      */             case 13:
/* 2008 */               if (this.curChar == 114)
/* 2009 */                 jjAddStates(150, 151); 
/*      */               break;
/*      */             case 15:
/* 2012 */               if (this.curChar == 93 && kind > 7)
/* 2013 */                 kind = 7; 
/*      */               break;
/*      */             case 16:
/* 2016 */               if (this.curChar == 101)
/* 2017 */                 this.jjstateSet[this.jjnewStateCnt++] = 13; 
/*      */               break;
/*      */             case 17:
/* 2020 */               if (this.curChar == 118)
/* 2021 */                 this.jjstateSet[this.jjnewStateCnt++] = 16; 
/*      */               break;
/*      */             case 18:
/* 2024 */               if (this.curChar == 111)
/* 2025 */                 this.jjstateSet[this.jjnewStateCnt++] = 17; 
/*      */               break;
/*      */             case 19:
/* 2028 */               if (this.curChar == 99)
/* 2029 */                 this.jjstateSet[this.jjnewStateCnt++] = 18; 
/*      */               break;
/*      */             case 20:
/* 2032 */               if (this.curChar == 101)
/* 2033 */                 this.jjstateSet[this.jjnewStateCnt++] = 19; 
/*      */               break;
/*      */             case 21:
/* 2036 */               if (this.curChar == 114)
/* 2037 */                 this.jjstateSet[this.jjnewStateCnt++] = 20; 
/*      */               break;
/*      */             case 22:
/* 2040 */               if (this.curChar == 102)
/* 2041 */                 this.jjstateSet[this.jjnewStateCnt++] = 23; 
/*      */               break;
/*      */             case 24:
/* 2044 */               if (this.curChar == 105)
/* 2045 */                 this.jjstateSet[this.jjnewStateCnt++] = 22; 
/*      */               break;
/*      */             case 25:
/* 2048 */               if (this.curChar == 101)
/* 2049 */                 this.jjstateSet[this.jjnewStateCnt++] = 26; 
/*      */               break;
/*      */             case 26:
/* 2052 */               if ((0x20000000200L & l) != 0L)
/* 2053 */                 this.jjstateSet[this.jjnewStateCnt++] = 27; 
/*      */               break;
/*      */             case 27:
/* 2056 */               if (this.curChar == 102)
/* 2057 */                 this.jjstateSet[this.jjnewStateCnt++] = 28; 
/*      */               break;
/*      */             case 29:
/* 2060 */               if (this.curChar == 115)
/* 2061 */                 this.jjstateSet[this.jjnewStateCnt++] = 25; 
/*      */               break;
/*      */             case 30:
/* 2064 */               if (this.curChar == 108)
/* 2065 */                 this.jjstateSet[this.jjnewStateCnt++] = 29; 
/*      */               break;
/*      */             case 31:
/* 2068 */               if (this.curChar == 101)
/* 2069 */                 this.jjstateSet[this.jjnewStateCnt++] = 30; 
/*      */               break;
/*      */             case 32:
/* 2072 */               if (this.curChar == 116)
/* 2073 */                 this.jjstateSet[this.jjnewStateCnt++] = 33; 
/*      */               break;
/*      */             case 34:
/* 2076 */               if (this.curChar == 115)
/* 2077 */                 this.jjstateSet[this.jjnewStateCnt++] = 32; 
/*      */               break;
/*      */             case 35:
/* 2080 */               if (this.curChar == 105)
/* 2081 */                 this.jjstateSet[this.jjnewStateCnt++] = 34; 
/*      */               break;
/*      */             case 36:
/* 2084 */               if (this.curChar == 108)
/* 2085 */                 this.jjstateSet[this.jjnewStateCnt++] = 35; 
/*      */               break;
/*      */             case 37:
/* 2088 */               if (this.curChar == 115)
/* 2089 */                 this.jjstateSet[this.jjnewStateCnt++] = 38; 
/*      */               break;
/*      */             case 39:
/* 2092 */               if (this.curChar == 115)
/* 2093 */                 this.jjstateSet[this.jjnewStateCnt++] = 40; 
/*      */               break;
/*      */             case 41:
/* 2096 */               if (this.curChar == 97)
/* 2097 */                 this.jjstateSet[this.jjnewStateCnt++] = 39; 
/*      */               break;
/*      */             case 42:
/* 2100 */               if (this.curChar == 109)
/* 2101 */                 this.jjstateSet[this.jjnewStateCnt++] = 37; 
/*      */               break;
/*      */             case 43:
/* 2104 */               if (this.curChar == 101)
/* 2105 */                 this.jjstateSet[this.jjnewStateCnt++] = 42; 
/*      */               break;
/*      */             case 44:
/* 2108 */               if (this.curChar == 116)
/* 2109 */                 this.jjstateSet[this.jjnewStateCnt++] = 43; 
/*      */               break;
/*      */             case 45:
/* 2112 */               if (this.curChar == 105)
/* 2113 */                 this.jjstateSet[this.jjnewStateCnt++] = 44; 
/*      */               break;
/*      */             case 46:
/* 2116 */               if (this.curChar == 112)
/* 2117 */                 jjAddStates(154, 155); 
/*      */               break;
/*      */             case 48:
/* 2120 */               if (this.curChar == 93 && kind > 12)
/* 2121 */                 kind = 12; 
/*      */               break;
/*      */             case 49:
/* 2124 */               if (this.curChar == 101)
/* 2125 */                 this.jjstateSet[this.jjnewStateCnt++] = 46; 
/*      */               break;
/*      */             case 50:
/* 2128 */               if (this.curChar == 115)
/* 2129 */                 this.jjstateSet[this.jjnewStateCnt++] = 49; 
/*      */               break;
/*      */             case 51:
/* 2132 */               if (this.curChar == 114)
/* 2133 */                 this.jjstateSet[this.jjnewStateCnt++] = 52; 
/*      */               break;
/*      */             case 52:
/* 2136 */               if ((0x2000000020L & l) != 0L)
/* 2137 */                 this.jjstateSet[this.jjnewStateCnt++] = 56; 
/*      */               break;
/*      */             case 53:
/* 2140 */               if (this.curChar == 104)
/* 2141 */                 this.jjstateSet[this.jjnewStateCnt++] = 54; 
/*      */               break;
/*      */             case 55:
/* 2144 */               if (this.curChar == 99)
/* 2145 */                 this.jjstateSet[this.jjnewStateCnt++] = 53; 
/*      */               break;
/*      */             case 56:
/* 2148 */               if (this.curChar == 97)
/* 2149 */                 this.jjstateSet[this.jjnewStateCnt++] = 55; 
/*      */               break;
/*      */             case 57:
/* 2152 */               if (this.curChar == 111)
/* 2153 */                 this.jjstateSet[this.jjnewStateCnt++] = 51; 
/*      */               break;
/*      */             case 58:
/* 2156 */               if (this.curChar == 102)
/* 2157 */                 this.jjstateSet[this.jjnewStateCnt++] = 57; 
/*      */               break;
/*      */             case 59:
/* 2160 */               if (this.curChar == 104)
/* 2161 */                 this.jjstateSet[this.jjnewStateCnt++] = 60; 
/*      */               break;
/*      */             case 61:
/* 2164 */               if (this.curChar == 99)
/* 2165 */                 this.jjstateSet[this.jjnewStateCnt++] = 59; 
/*      */               break;
/*      */             case 62:
/* 2168 */               if (this.curChar == 116)
/* 2169 */                 this.jjstateSet[this.jjnewStateCnt++] = 61; 
/*      */               break;
/*      */             case 63:
/* 2172 */               if (this.curChar == 105)
/* 2173 */                 this.jjstateSet[this.jjnewStateCnt++] = 62; 
/*      */               break;
/*      */             case 64:
/* 2176 */               if (this.curChar == 119)
/* 2177 */                 this.jjstateSet[this.jjnewStateCnt++] = 63; 
/*      */               break;
/*      */             case 65:
/* 2180 */               if (this.curChar == 115)
/* 2181 */                 this.jjstateSet[this.jjnewStateCnt++] = 64; 
/*      */               break;
/*      */             case 66:
/* 2184 */               if (this.curChar == 101)
/* 2185 */                 this.jjstateSet[this.jjnewStateCnt++] = 67; 
/*      */               break;
/*      */             case 68:
/* 2188 */               if (this.curChar == 115)
/* 2189 */                 this.jjstateSet[this.jjnewStateCnt++] = 66; 
/*      */               break;
/*      */             case 69:
/* 2192 */               if (this.curChar == 97)
/* 2193 */                 this.jjstateSet[this.jjnewStateCnt++] = 68; 
/*      */               break;
/*      */             case 70:
/* 2196 */               if (this.curChar == 99)
/* 2197 */                 this.jjstateSet[this.jjnewStateCnt++] = 69; 
/*      */               break;
/*      */             case 71:
/* 2200 */               if (this.curChar == 110)
/* 2201 */                 this.jjstateSet[this.jjnewStateCnt++] = 72; 
/*      */               break;
/*      */             case 73:
/* 2204 */               if (this.curChar == 103)
/* 2205 */                 this.jjstateSet[this.jjnewStateCnt++] = 71; 
/*      */               break;
/*      */             case 74:
/* 2208 */               if (this.curChar == 105)
/* 2209 */                 this.jjstateSet[this.jjnewStateCnt++] = 73; 
/*      */               break;
/*      */             case 75:
/* 2212 */               if (this.curChar == 115)
/* 2213 */                 this.jjstateSet[this.jjnewStateCnt++] = 74; 
/*      */               break;
/*      */             case 76:
/* 2216 */               if (this.curChar == 115)
/* 2217 */                 this.jjstateSet[this.jjnewStateCnt++] = 75; 
/*      */               break;
/*      */             case 77:
/* 2220 */               if (this.curChar == 97)
/* 2221 */                 this.jjstateSet[this.jjnewStateCnt++] = 76; 
/*      */               break;
/*      */             case 78:
/* 2224 */               if (this.curChar == 108)
/* 2225 */                 this.jjstateSet[this.jjnewStateCnt++] = 79; 
/*      */               break;
/*      */             case 80:
/* 2228 */               if (this.curChar == 97)
/* 2229 */                 this.jjstateSet[this.jjnewStateCnt++] = 78; 
/*      */               break;
/*      */             case 81:
/* 2232 */               if (this.curChar == 98)
/* 2233 */                 this.jjstateSet[this.jjnewStateCnt++] = 80; 
/*      */               break;
/*      */             case 82:
/* 2236 */               if (this.curChar == 111)
/* 2237 */                 this.jjstateSet[this.jjnewStateCnt++] = 81; 
/*      */               break;
/*      */             case 83:
/* 2240 */               if (this.curChar == 108)
/* 2241 */                 this.jjstateSet[this.jjnewStateCnt++] = 82; 
/*      */               break;
/*      */             case 84:
/* 2244 */               if (this.curChar == 103)
/* 2245 */                 this.jjstateSet[this.jjnewStateCnt++] = 83; 
/*      */               break;
/*      */             case 85:
/* 2248 */               if (this.curChar == 108)
/* 2249 */                 this.jjstateSet[this.jjnewStateCnt++] = 86; 
/*      */               break;
/*      */             case 87:
/* 2252 */               if (this.curChar == 97)
/* 2253 */                 this.jjstateSet[this.jjnewStateCnt++] = 85; 
/*      */               break;
/*      */             case 88:
/* 2256 */               if (this.curChar == 99)
/* 2257 */                 this.jjstateSet[this.jjnewStateCnt++] = 87; 
/*      */               break;
/*      */             case 89:
/* 2260 */               if (this.curChar == 111)
/* 2261 */                 this.jjstateSet[this.jjnewStateCnt++] = 88; 
/*      */               break;
/*      */             case 90:
/* 2264 */               if (this.curChar == 108)
/* 2265 */                 this.jjstateSet[this.jjnewStateCnt++] = 89; 
/*      */               break;
/*      */             case 91:
/* 2268 */               if (this.curChar == 101)
/* 2269 */                 this.jjstateSet[this.jjnewStateCnt++] = 92; 
/*      */               break;
/*      */             case 93:
/* 2272 */               if (this.curChar == 100)
/* 2273 */                 this.jjstateSet[this.jjnewStateCnt++] = 91; 
/*      */               break;
/*      */             case 94:
/* 2276 */               if (this.curChar == 117)
/* 2277 */                 this.jjstateSet[this.jjnewStateCnt++] = 93; 
/*      */               break;
/*      */             case 95:
/* 2280 */               if (this.curChar == 108)
/* 2281 */                 this.jjstateSet[this.jjnewStateCnt++] = 94; 
/*      */               break;
/*      */             case 96:
/* 2284 */               if (this.curChar == 99)
/* 2285 */                 this.jjstateSet[this.jjnewStateCnt++] = 95; 
/*      */               break;
/*      */             case 97:
/* 2288 */               if (this.curChar == 110)
/* 2289 */                 this.jjstateSet[this.jjnewStateCnt++] = 96; 
/*      */               break;
/*      */             case 98:
/* 2292 */               if (this.curChar == 105)
/* 2293 */                 this.jjstateSet[this.jjnewStateCnt++] = 97; 
/*      */               break;
/*      */             case 99:
/* 2296 */               if (this.curChar == 116)
/* 2297 */                 this.jjstateSet[this.jjnewStateCnt++] = 100; 
/*      */               break;
/*      */             case 101:
/* 2300 */               if (this.curChar == 114)
/* 2301 */                 this.jjstateSet[this.jjnewStateCnt++] = 99; 
/*      */               break;
/*      */             case 102:
/* 2304 */               if (this.curChar == 111)
/* 2305 */                 this.jjstateSet[this.jjnewStateCnt++] = 101; 
/*      */               break;
/*      */             case 103:
/* 2308 */               if (this.curChar == 112)
/* 2309 */                 this.jjstateSet[this.jjnewStateCnt++] = 102; 
/*      */               break;
/*      */             case 104:
/* 2312 */               if (this.curChar == 109)
/* 2313 */                 this.jjstateSet[this.jjnewStateCnt++] = 103; 
/*      */               break;
/*      */             case 105:
/* 2316 */               if (this.curChar == 105)
/* 2317 */                 this.jjstateSet[this.jjnewStateCnt++] = 104; 
/*      */               break;
/*      */             case 106:
/* 2320 */               if (this.curChar == 110)
/* 2321 */                 this.jjstateSet[this.jjnewStateCnt++] = 107; 
/*      */               break;
/*      */             case 108:
/* 2324 */               if (this.curChar == 111)
/* 2325 */                 this.jjstateSet[this.jjnewStateCnt++] = 106; 
/*      */               break;
/*      */             case 109:
/* 2328 */               if (this.curChar == 105)
/* 2329 */                 this.jjstateSet[this.jjnewStateCnt++] = 108; 
/*      */               break;
/*      */             case 110:
/* 2332 */               if (this.curChar == 116)
/* 2333 */                 this.jjstateSet[this.jjnewStateCnt++] = 109; 
/*      */               break;
/*      */             case 111:
/* 2336 */               if (this.curChar == 99)
/* 2337 */                 this.jjstateSet[this.jjnewStateCnt++] = 110; 
/*      */               break;
/*      */             case 112:
/* 2340 */               if (this.curChar == 110)
/* 2341 */                 this.jjstateSet[this.jjnewStateCnt++] = 111; 
/*      */               break;
/*      */             case 113:
/* 2344 */               if (this.curChar == 117)
/* 2345 */                 this.jjstateSet[this.jjnewStateCnt++] = 112; 
/*      */               break;
/*      */             case 114:
/* 2348 */               if (this.curChar == 102)
/* 2349 */                 this.jjstateSet[this.jjnewStateCnt++] = 113; 
/*      */               break;
/*      */             case 115:
/* 2352 */               if (this.curChar == 111)
/* 2353 */                 this.jjstateSet[this.jjnewStateCnt++] = 116; 
/*      */               break;
/*      */             case 117:
/* 2356 */               if (this.curChar == 114)
/* 2357 */                 this.jjstateSet[this.jjnewStateCnt++] = 115; 
/*      */               break;
/*      */             case 118:
/* 2360 */               if (this.curChar == 99)
/* 2361 */                 this.jjstateSet[this.jjnewStateCnt++] = 117; 
/*      */               break;
/*      */             case 119:
/* 2364 */               if (this.curChar == 97)
/* 2365 */                 this.jjstateSet[this.jjnewStateCnt++] = 118; 
/*      */               break;
/*      */             case 120:
/* 2368 */               if (this.curChar == 109)
/* 2369 */                 this.jjstateSet[this.jjnewStateCnt++] = 119; 
/*      */               break;
/*      */             case 121:
/* 2372 */               if (this.curChar == 109)
/* 2373 */                 this.jjstateSet[this.jjnewStateCnt++] = 122; 
/*      */               break;
/*      */             case 123:
/* 2376 */               if (this.curChar == 114)
/* 2377 */                 this.jjstateSet[this.jjnewStateCnt++] = 121; 
/*      */               break;
/*      */             case 124:
/* 2380 */               if (this.curChar == 111)
/* 2381 */                 this.jjstateSet[this.jjnewStateCnt++] = 123; 
/*      */               break;
/*      */             case 125:
/* 2384 */               if (this.curChar == 102)
/* 2385 */                 this.jjstateSet[this.jjnewStateCnt++] = 124; 
/*      */               break;
/*      */             case 126:
/* 2388 */               if (this.curChar == 115)
/* 2389 */                 this.jjstateSet[this.jjnewStateCnt++] = 125; 
/*      */               break;
/*      */             case 127:
/* 2392 */               if (this.curChar == 110)
/* 2393 */                 this.jjstateSet[this.jjnewStateCnt++] = 126; 
/*      */               break;
/*      */             case 128:
/* 2396 */               if (this.curChar == 97)
/* 2397 */                 this.jjstateSet[this.jjnewStateCnt++] = 127; 
/*      */               break;
/*      */             case 129:
/* 2400 */               if (this.curChar == 114)
/* 2401 */                 this.jjstateSet[this.jjnewStateCnt++] = 128; 
/*      */               break;
/*      */             case 130:
/* 2404 */               if (this.curChar == 116)
/* 2405 */                 this.jjstateSet[this.jjnewStateCnt++] = 129; 
/*      */               break;
/*      */             case 131:
/* 2408 */               if (this.curChar == 116)
/* 2409 */                 this.jjstateSet[this.jjnewStateCnt++] = 132; 
/*      */               break;
/*      */             case 133:
/* 2412 */               if (this.curChar == 105)
/* 2413 */                 this.jjstateSet[this.jjnewStateCnt++] = 131; 
/*      */               break;
/*      */             case 134:
/* 2416 */               if (this.curChar == 115)
/* 2417 */                 this.jjstateSet[this.jjnewStateCnt++] = 133; 
/*      */               break;
/*      */             case 135:
/* 2420 */               if (this.curChar == 105)
/* 2421 */                 this.jjstateSet[this.jjnewStateCnt++] = 134; 
/*      */               break;
/*      */             case 136:
/* 2424 */               if (this.curChar == 118)
/* 2425 */                 this.jjstateSet[this.jjnewStateCnt++] = 135; 
/*      */               break;
/*      */             case 137:
/* 2428 */               if (this.curChar == 112)
/* 2429 */                 this.jjstateSet[this.jjnewStateCnt++] = 138; 
/*      */               break;
/*      */             case 139:
/* 2432 */               if (this.curChar == 111)
/* 2433 */                 this.jjstateSet[this.jjnewStateCnt++] = 137; 
/*      */               break;
/*      */             case 140:
/* 2436 */               if (this.curChar == 116)
/* 2437 */                 this.jjstateSet[this.jjnewStateCnt++] = 139; 
/*      */               break;
/*      */             case 141:
/* 2440 */               if (this.curChar == 115)
/* 2441 */                 this.jjstateSet[this.jjnewStateCnt++] = 140; 
/*      */               break;
/*      */             case 142:
/* 2444 */               if (this.curChar == 110)
/* 2445 */                 this.jjstateSet[this.jjnewStateCnt++] = 143; 
/*      */               break;
/*      */             case 144:
/* 2448 */               if (this.curChar == 114)
/* 2449 */                 this.jjstateSet[this.jjnewStateCnt++] = 142; 
/*      */               break;
/*      */             case 145:
/* 2452 */               if (this.curChar == 117)
/* 2453 */                 this.jjstateSet[this.jjnewStateCnt++] = 144; 
/*      */               break;
/*      */             case 146:
/* 2456 */               if (this.curChar == 116)
/* 2457 */                 this.jjstateSet[this.jjnewStateCnt++] = 145; 
/*      */               break;
/*      */             case 147:
/* 2460 */               if (this.curChar == 101)
/* 2461 */                 this.jjstateSet[this.jjnewStateCnt++] = 146; 
/*      */               break;
/*      */             case 148:
/* 2464 */               if (this.curChar == 114)
/* 2465 */                 this.jjstateSet[this.jjnewStateCnt++] = 147; 
/*      */               break;
/*      */             case 149:
/* 2468 */               if (this.curChar == 108)
/* 2469 */                 this.jjstateSet[this.jjnewStateCnt++] = 150; 
/*      */               break;
/*      */             case 151:
/* 2472 */               if (this.curChar == 108)
/* 2473 */                 this.jjstateSet[this.jjnewStateCnt++] = 149; 
/*      */               break;
/*      */             case 152:
/* 2476 */               if (this.curChar == 97)
/* 2477 */                 this.jjstateSet[this.jjnewStateCnt++] = 151; 
/*      */               break;
/*      */             case 153:
/* 2480 */               if (this.curChar == 99)
/* 2481 */                 this.jjstateSet[this.jjnewStateCnt++] = 152; 
/*      */               break;
/*      */             case 154:
/* 2484 */               if (this.curChar == 103)
/* 2485 */                 this.jjstateSet[this.jjnewStateCnt++] = 155; 
/*      */               break;
/*      */             case 156:
/* 2488 */               if (this.curChar == 110)
/* 2489 */                 this.jjstateSet[this.jjnewStateCnt++] = 154; 
/*      */               break;
/*      */             case 157:
/* 2492 */               if (this.curChar == 105)
/* 2493 */                 this.jjstateSet[this.jjnewStateCnt++] = 156; 
/*      */               break;
/*      */             case 158:
/* 2496 */               if (this.curChar == 116)
/* 2497 */                 this.jjstateSet[this.jjnewStateCnt++] = 157; 
/*      */               break;
/*      */             case 159:
/* 2500 */               if (this.curChar == 116)
/* 2501 */                 this.jjstateSet[this.jjnewStateCnt++] = 158; 
/*      */               break;
/*      */             case 160:
/* 2504 */               if (this.curChar == 101)
/* 2505 */                 this.jjstateSet[this.jjnewStateCnt++] = 159; 
/*      */               break;
/*      */             case 161:
/* 2508 */               if (this.curChar == 115)
/* 2509 */                 this.jjstateSet[this.jjnewStateCnt++] = 160; 
/*      */               break;
/*      */             case 162:
/* 2512 */               if (this.curChar == 116)
/* 2513 */                 this.jjstateSet[this.jjnewStateCnt++] = 163; 
/*      */               break;
/*      */             case 163:
/* 2516 */               if ((0x4000000040L & l) != 0L)
/* 2517 */                 this.jjstateSet[this.jjnewStateCnt++] = 169; 
/*      */               break;
/*      */             case 164:
/* 2520 */               if (this.curChar == 116)
/* 2521 */                 this.jjstateSet[this.jjnewStateCnt++] = 165; 
/*      */               break;
/*      */             case 166:
/* 2524 */               if (this.curChar == 97)
/* 2525 */                 this.jjstateSet[this.jjnewStateCnt++] = 164; 
/*      */               break;
/*      */             case 167:
/* 2528 */               if (this.curChar == 109)
/* 2529 */                 this.jjstateSet[this.jjnewStateCnt++] = 166; 
/*      */               break;
/*      */             case 168:
/* 2532 */               if (this.curChar == 114)
/* 2533 */                 this.jjstateSet[this.jjnewStateCnt++] = 167; 
/*      */               break;
/*      */             case 169:
/* 2536 */               if (this.curChar == 111)
/* 2537 */                 this.jjstateSet[this.jjnewStateCnt++] = 168; 
/*      */               break;
/*      */             case 170:
/* 2540 */               if (this.curChar == 117)
/* 2541 */                 this.jjstateSet[this.jjnewStateCnt++] = 162; 
/*      */               break;
/*      */             case 171:
/* 2544 */               if (this.curChar == 112)
/* 2545 */                 this.jjstateSet[this.jjnewStateCnt++] = 170; 
/*      */               break;
/*      */             case 172:
/* 2548 */               if (this.curChar == 116)
/* 2549 */                 this.jjstateSet[this.jjnewStateCnt++] = 171; 
/*      */               break;
/*      */             case 173:
/* 2552 */               if (this.curChar == 117)
/* 2553 */                 this.jjstateSet[this.jjnewStateCnt++] = 172; 
/*      */               break;
/*      */             case 174:
/* 2556 */               if (this.curChar == 111)
/* 2557 */                 this.jjstateSet[this.jjnewStateCnt++] = 173; 
/*      */               break;
/*      */             case 175:
/* 2560 */               if (this.curChar == 111)
/* 2561 */                 this.jjstateSet[this.jjnewStateCnt++] = 176; 
/*      */               break;
/*      */             case 176:
/* 2564 */               if ((0x2000000020L & l) != 0L)
/* 2565 */                 this.jjstateSet[this.jjnewStateCnt++] = 180; 
/*      */               break;
/*      */             case 177:
/* 2568 */               if (this.curChar == 99)
/* 2569 */                 jjAddStates(156, 157); 
/*      */               break;
/*      */             case 179:
/* 2572 */               if (this.curChar == 93 && kind > 30)
/* 2573 */                 kind = 30; 
/*      */               break;
/*      */             case 180:
/* 2576 */               if (this.curChar == 115)
/* 2577 */                 this.jjstateSet[this.jjnewStateCnt++] = 177; 
/*      */               break;
/*      */             case 181:
/* 2580 */               if (this.curChar == 116)
/* 2581 */                 this.jjstateSet[this.jjnewStateCnt++] = 175; 
/*      */               break;
/*      */             case 182:
/* 2584 */               if (this.curChar == 117)
/* 2585 */                 this.jjstateSet[this.jjnewStateCnt++] = 181; 
/*      */               break;
/*      */             case 183:
/* 2588 */               if (this.curChar == 97)
/* 2589 */                 this.jjstateSet[this.jjnewStateCnt++] = 182; 
/*      */               break;
/*      */             case 184:
/* 2592 */               if (this.curChar == 111)
/* 2593 */                 jjAddStates(333, 334); 
/*      */               break;
/*      */             case 185:
/* 2596 */               if (this.curChar == 101)
/* 2597 */                 jjCheckNAdd(189); 
/*      */               break;
/*      */             case 186:
/* 2600 */               if (this.curChar == 99)
/* 2601 */                 jjAddStates(158, 159); 
/*      */               break;
/*      */             case 188:
/* 2604 */               if (this.curChar == 93 && kind > 31)
/* 2605 */                 kind = 31; 
/*      */               break;
/*      */             case 189:
/* 2608 */               if (this.curChar == 115)
/* 2609 */                 this.jjstateSet[this.jjnewStateCnt++] = 186; 
/*      */               break;
/*      */             case 190:
/* 2612 */               if (this.curChar == 111)
/* 2613 */                 this.jjstateSet[this.jjnewStateCnt++] = 185; 
/*      */               break;
/*      */             case 191:
/* 2616 */               if (this.curChar == 116)
/* 2617 */                 this.jjstateSet[this.jjnewStateCnt++] = 190; 
/*      */               break;
/*      */             case 192:
/* 2620 */               if (this.curChar == 117)
/* 2621 */                 this.jjstateSet[this.jjnewStateCnt++] = 191; 
/*      */               break;
/*      */             case 193:
/* 2624 */               if (this.curChar == 97)
/* 2625 */                 this.jjstateSet[this.jjnewStateCnt++] = 192; 
/*      */               break;
/*      */             case 194:
/* 2628 */               if (this.curChar == 69)
/* 2629 */                 jjCheckNAdd(189); 
/*      */               break;
/*      */             case 195:
/* 2632 */               if (this.curChar == 111)
/* 2633 */                 this.jjstateSet[this.jjnewStateCnt++] = 194; 
/*      */               break;
/*      */             case 196:
/* 2636 */               if (this.curChar == 116)
/* 2637 */                 this.jjstateSet[this.jjnewStateCnt++] = 195; 
/*      */               break;
/*      */             case 197:
/* 2640 */               if (this.curChar == 117)
/* 2641 */                 this.jjstateSet[this.jjnewStateCnt++] = 196; 
/*      */               break;
/*      */             case 198:
/* 2644 */               if (this.curChar == 65)
/* 2645 */                 this.jjstateSet[this.jjnewStateCnt++] = 197; 
/*      */               break;
/*      */             case 199:
/* 2648 */               if (this.curChar == 110)
/* 2649 */                 this.jjstateSet[this.jjnewStateCnt++] = 184; 
/*      */               break;
/*      */             case 200:
/* 2652 */               if (this.curChar == 115)
/* 2653 */                 jjAddStates(160, 161); 
/*      */               break;
/*      */             case 202:
/* 2656 */               if (this.curChar == 93 && kind > 32)
/* 2657 */                 kind = 32; 
/*      */               break;
/*      */             case 203:
/* 2660 */               if (this.curChar == 115)
/* 2661 */                 this.jjstateSet[this.jjnewStateCnt++] = 200; 
/*      */               break;
/*      */             case 204:
/* 2664 */               if (this.curChar == 101)
/* 2665 */                 this.jjstateSet[this.jjnewStateCnt++] = 203; 
/*      */               break;
/*      */             case 205:
/* 2668 */               if (this.curChar == 114)
/* 2669 */                 this.jjstateSet[this.jjnewStateCnt++] = 204; 
/*      */               break;
/*      */             case 206:
/* 2672 */               if (this.curChar == 112)
/* 2673 */                 this.jjstateSet[this.jjnewStateCnt++] = 205; 
/*      */               break;
/*      */             case 207:
/* 2676 */               if (this.curChar == 109)
/* 2677 */                 this.jjstateSet[this.jjnewStateCnt++] = 206; 
/*      */               break;
/*      */             case 208:
/* 2680 */               if (this.curChar == 111)
/* 2681 */                 this.jjstateSet[this.jjnewStateCnt++] = 207; 
/*      */               break;
/*      */             case 209:
/* 2684 */               if (this.curChar == 99)
/* 2685 */                 this.jjstateSet[this.jjnewStateCnt++] = 208; 
/*      */               break;
/*      */             case 210:
/* 2688 */               if (this.curChar == 116)
/* 2689 */                 jjAddStates(162, 163); 
/*      */               break;
/*      */             case 212:
/* 2692 */               if (this.curChar == 93 && kind > 33)
/* 2693 */                 kind = 33; 
/*      */               break;
/*      */             case 213:
/* 2696 */               if (this.curChar == 110)
/* 2697 */                 this.jjstateSet[this.jjnewStateCnt++] = 210; 
/*      */               break;
/*      */             case 214:
/* 2700 */               if (this.curChar == 101)
/* 2701 */                 this.jjstateSet[this.jjnewStateCnt++] = 213; 
/*      */               break;
/*      */             case 215:
/* 2704 */               if (this.curChar == 109)
/* 2705 */                 this.jjstateSet[this.jjnewStateCnt++] = 214; 
/*      */               break;
/*      */             case 216:
/* 2708 */               if (this.curChar == 109)
/* 2709 */                 this.jjstateSet[this.jjnewStateCnt++] = 215; 
/*      */               break;
/*      */             case 217:
/* 2712 */               if (this.curChar == 111)
/* 2713 */                 this.jjstateSet[this.jjnewStateCnt++] = 216; 
/*      */               break;
/*      */             case 218:
/* 2716 */               if (this.curChar == 99)
/* 2717 */                 this.jjstateSet[this.jjnewStateCnt++] = 217; 
/*      */               break;
/*      */             case 219:
/* 2720 */               if (this.curChar == 111)
/* 2721 */                 this.jjstateSet[this.jjnewStateCnt++] = 220; 
/*      */               break;
/*      */             case 220:
/* 2724 */               if ((0x1000000010000L & l) != 0L)
/* 2725 */                 this.jjstateSet[this.jjnewStateCnt++] = 226; 
/*      */               break;
/*      */             case 221:
/* 2728 */               if (this.curChar == 101)
/* 2729 */                 jjAddStates(164, 165); 
/*      */               break;
/*      */             case 223:
/* 2732 */               if (this.curChar == 93 && kind > 35)
/* 2733 */                 kind = 35; 
/*      */               break;
/*      */             case 224:
/* 2736 */               if (this.curChar == 115)
/* 2737 */                 this.jjstateSet[this.jjnewStateCnt++] = 221; 
/*      */               break;
/*      */             case 225:
/* 2740 */               if (this.curChar == 114)
/* 2741 */                 this.jjstateSet[this.jjnewStateCnt++] = 224; 
/*      */               break;
/*      */             case 226:
/* 2744 */               if (this.curChar == 97)
/* 2745 */                 this.jjstateSet[this.jjnewStateCnt++] = 225; 
/*      */               break;
/*      */             case 227:
/* 2748 */               if (this.curChar == 110)
/* 2749 */                 this.jjstateSet[this.jjnewStateCnt++] = 219; 
/*      */               break;
/*      */             case 228:
/* 2752 */               if (this.curChar == 101)
/* 2753 */                 jjAddStates(166, 168); 
/*      */               break;
/*      */             case 231:
/* 2756 */               if (this.curChar == 93 && kind > 54)
/* 2757 */                 kind = 54; 
/*      */               break;
/*      */             case 232:
/* 2760 */               if (this.curChar == 115)
/* 2761 */                 this.jjstateSet[this.jjnewStateCnt++] = 228; 
/*      */               break;
/*      */             case 233:
/* 2764 */               if (this.curChar == 108)
/* 2765 */                 this.jjstateSet[this.jjnewStateCnt++] = 232; 
/*      */               break;
/*      */             case 234:
/* 2768 */               if (this.curChar == 101)
/* 2769 */                 this.jjstateSet[this.jjnewStateCnt++] = 233; 
/*      */               break;
/*      */             case 235:
/* 2772 */               if (this.curChar == 107)
/* 2773 */                 jjAddStates(169, 171); 
/*      */               break;
/*      */             case 238:
/* 2776 */               if (this.curChar == 93 && kind > 55)
/* 2777 */                 kind = 55; 
/*      */               break;
/*      */             case 239:
/* 2780 */               if (this.curChar == 97)
/* 2781 */                 this.jjstateSet[this.jjnewStateCnt++] = 235; 
/*      */               break;
/*      */             case 240:
/* 2784 */               if (this.curChar == 101)
/* 2785 */                 this.jjstateSet[this.jjnewStateCnt++] = 239; 
/*      */               break;
/*      */             case 241:
/* 2788 */               if (this.curChar == 114)
/* 2789 */                 this.jjstateSet[this.jjnewStateCnt++] = 240; 
/*      */               break;
/*      */             case 242:
/* 2792 */               if (this.curChar == 98)
/* 2793 */                 this.jjstateSet[this.jjnewStateCnt++] = 241; 
/*      */               break;
/*      */             case 243:
/* 2796 */               if (this.curChar == 101)
/* 2797 */                 jjAddStates(172, 174); 
/*      */               break;
/*      */             case 246:
/* 2800 */               if (this.curChar == 93 && kind > 56)
/* 2801 */                 kind = 56; 
/*      */               break;
/*      */             case 247:
/* 2804 */               if (this.curChar == 117)
/* 2805 */                 this.jjstateSet[this.jjnewStateCnt++] = 243; 
/*      */               break;
/*      */             case 248:
/* 2808 */               if (this.curChar == 110)
/* 2809 */                 this.jjstateSet[this.jjnewStateCnt++] = 247; 
/*      */               break;
/*      */             case 249:
/* 2812 */               if (this.curChar == 105)
/* 2813 */                 this.jjstateSet[this.jjnewStateCnt++] = 248; 
/*      */               break;
/*      */             case 250:
/* 2816 */               if (this.curChar == 116)
/* 2817 */                 this.jjstateSet[this.jjnewStateCnt++] = 249; 
/*      */               break;
/*      */             case 251:
/* 2820 */               if (this.curChar == 110)
/* 2821 */                 this.jjstateSet[this.jjnewStateCnt++] = 250; 
/*      */               break;
/*      */             case 252:
/* 2824 */               if (this.curChar == 111)
/* 2825 */                 this.jjstateSet[this.jjnewStateCnt++] = 251; 
/*      */               break;
/*      */             case 253:
/* 2828 */               if (this.curChar == 99)
/* 2829 */                 this.jjstateSet[this.jjnewStateCnt++] = 252; 
/*      */               break;
/*      */             case 254:
/* 2832 */               if (this.curChar == 110)
/* 2833 */                 jjAddStates(175, 177); 
/*      */               break;
/*      */             case 257:
/* 2836 */               if (this.curChar == 93 && kind > 57)
/* 2837 */                 kind = 57; 
/*      */               break;
/*      */             case 258:
/* 2840 */               if (this.curChar == 114)
/* 2841 */                 this.jjstateSet[this.jjnewStateCnt++] = 254; 
/*      */               break;
/*      */             case 259:
/* 2844 */               if (this.curChar == 117)
/* 2845 */                 this.jjstateSet[this.jjnewStateCnt++] = 258; 
/*      */               break;
/*      */             case 260:
/* 2848 */               if (this.curChar == 116)
/* 2849 */                 this.jjstateSet[this.jjnewStateCnt++] = 259; 
/*      */               break;
/*      */             case 261:
/* 2852 */               if (this.curChar == 101)
/* 2853 */                 this.jjstateSet[this.jjnewStateCnt++] = 260; 
/*      */               break;
/*      */             case 262:
/* 2856 */               if (this.curChar == 114)
/* 2857 */                 this.jjstateSet[this.jjnewStateCnt++] = 261; 
/*      */               break;
/*      */             case 263:
/* 2860 */               if (this.curChar == 112)
/* 2861 */                 jjAddStates(178, 180); 
/*      */               break;
/*      */             case 266:
/* 2864 */               if (this.curChar == 93 && kind > 58)
/* 2865 */                 kind = 58; 
/*      */               break;
/*      */             case 267:
/* 2868 */               if (this.curChar == 111)
/* 2869 */                 this.jjstateSet[this.jjnewStateCnt++] = 263; 
/*      */               break;
/*      */             case 268:
/* 2872 */               if (this.curChar == 116)
/* 2873 */                 this.jjstateSet[this.jjnewStateCnt++] = 267; 
/*      */               break;
/*      */             case 269:
/* 2876 */               if (this.curChar == 115)
/* 2877 */                 this.jjstateSet[this.jjnewStateCnt++] = 268; 
/*      */               break;
/*      */             case 270:
/* 2880 */               if (this.curChar == 104)
/* 2881 */                 jjAddStates(181, 183); 
/*      */               break;
/*      */             case 273:
/* 2884 */               if (this.curChar == 93 && kind > 59)
/* 2885 */                 kind = 59; 
/*      */               break;
/*      */             case 274:
/* 2888 */               if (this.curChar == 115)
/* 2889 */                 this.jjstateSet[this.jjnewStateCnt++] = 270; 
/*      */               break;
/*      */             case 275:
/* 2892 */               if (this.curChar == 117)
/* 2893 */                 this.jjstateSet[this.jjnewStateCnt++] = 274; 
/*      */               break;
/*      */             case 276:
/* 2896 */               if (this.curChar == 108)
/* 2897 */                 this.jjstateSet[this.jjnewStateCnt++] = 275; 
/*      */               break;
/*      */             case 277:
/* 2900 */               if (this.curChar == 102)
/* 2901 */                 this.jjstateSet[this.jjnewStateCnt++] = 276; 
/*      */               break;
/*      */             case 278:
/* 2904 */               if (this.curChar == 116)
/* 2905 */                 jjAddStates(184, 186); 
/*      */               break;
/*      */             case 281:
/* 2908 */               if (this.curChar == 93 && kind > 60)
/* 2909 */                 kind = 60; 
/*      */               break;
/*      */             case 282:
/* 2912 */               if (this.curChar == 116)
/* 2913 */                 jjAddStates(187, 189); 
/*      */               break;
/*      */             case 285:
/* 2916 */               if (this.curChar == 93 && kind > 61)
/* 2917 */                 kind = 61; 
/*      */               break;
/*      */             case 286:
/* 2920 */               if (this.curChar == 108)
/* 2921 */                 this.jjstateSet[this.jjnewStateCnt++] = 282; 
/*      */               break;
/*      */             case 287:
/* 2924 */               if (this.curChar == 116)
/* 2925 */                 jjAddStates(190, 192); 
/*      */               break;
/*      */             case 290:
/* 2928 */               if (this.curChar == 93 && kind > 62)
/* 2929 */                 kind = 62; 
/*      */               break;
/*      */             case 291:
/* 2932 */               if (this.curChar == 114)
/* 2933 */                 this.jjstateSet[this.jjnewStateCnt++] = 287; 
/*      */               break;
/*      */             case 292:
/* 2936 */               if (this.curChar == 116)
/* 2937 */                 jjAddStates(193, 195); 
/*      */               break;
/*      */             case 295:
/* 2940 */               if (this.curChar == 93 && kind > 63)
/* 2941 */                 kind = 63; 
/*      */               break;
/*      */             case 296:
/* 2944 */               if (this.curChar == 110)
/* 2945 */                 this.jjstateSet[this.jjnewStateCnt++] = 292; 
/*      */               break;
/*      */             case 297:
/* 2948 */               if (this.curChar == 116)
/* 2949 */                 jjAddStates(196, 197); 
/*      */               break;
/*      */             case 299:
/* 2952 */               if (this.curChar == 93 && kind > 64)
/* 2953 */                 kind = 64; 
/*      */               break;
/*      */             case 300:
/* 2956 */               if (this.curChar == 108)
/* 2957 */                 this.jjstateSet[this.jjnewStateCnt++] = 297; 
/*      */               break;
/*      */             case 301:
/* 2960 */               if (this.curChar == 117)
/* 2961 */                 this.jjstateSet[this.jjnewStateCnt++] = 300; 
/*      */               break;
/*      */             case 302:
/* 2964 */               if (this.curChar == 97)
/* 2965 */                 this.jjstateSet[this.jjnewStateCnt++] = 301; 
/*      */               break;
/*      */             case 303:
/* 2968 */               if (this.curChar == 102)
/* 2969 */                 this.jjstateSet[this.jjnewStateCnt++] = 302; 
/*      */               break;
/*      */             case 304:
/* 2972 */               if (this.curChar == 101)
/* 2973 */                 this.jjstateSet[this.jjnewStateCnt++] = 303; 
/*      */               break;
/*      */             case 305:
/* 2976 */               if (this.curChar == 100)
/* 2977 */                 this.jjstateSet[this.jjnewStateCnt++] = 304; 
/*      */               break;
/*      */             case 306:
/* 2980 */               if (this.curChar == 100)
/* 2981 */                 jjAddStates(198, 200); 
/*      */               break;
/*      */             case 309:
/* 2984 */               if (this.curChar == 93 && kind > 65)
/* 2985 */                 kind = 65; 
/*      */               break;
/*      */             case 310:
/* 2988 */               if (this.curChar == 101)
/* 2989 */                 this.jjstateSet[this.jjnewStateCnt++] = 306; 
/*      */               break;
/*      */             case 311:
/* 2992 */               if (this.curChar == 116)
/* 2993 */                 this.jjstateSet[this.jjnewStateCnt++] = 310; 
/*      */               break;
/*      */             case 312:
/* 2996 */               if (this.curChar == 115)
/* 2997 */                 this.jjstateSet[this.jjnewStateCnt++] = 311; 
/*      */               break;
/*      */             case 313:
/* 3000 */               if (this.curChar == 101)
/* 3001 */                 this.jjstateSet[this.jjnewStateCnt++] = 312; 
/*      */               break;
/*      */             case 314:
/* 3004 */               if (this.curChar == 110)
/* 3005 */                 this.jjstateSet[this.jjnewStateCnt++] = 313; 
/*      */               break;
/*      */             case 315:
/* 3008 */               if (this.curChar == 100)
/* 3009 */                 this.jjstateSet[this.jjnewStateCnt++] = 316; 
/*      */               break;
/*      */             case 317:
/* 3012 */               if (this.curChar == 101)
/* 3013 */                 this.jjstateSet[this.jjnewStateCnt++] = 315; 
/*      */               break;
/*      */             case 318:
/* 3016 */               if (this.curChar == 116)
/* 3017 */                 this.jjstateSet[this.jjnewStateCnt++] = 317; 
/*      */               break;
/*      */             case 319:
/* 3020 */               if (this.curChar == 115)
/* 3021 */                 this.jjstateSet[this.jjnewStateCnt++] = 318; 
/*      */               break;
/*      */             case 320:
/* 3024 */               if (this.curChar == 101)
/* 3025 */                 this.jjstateSet[this.jjnewStateCnt++] = 319; 
/*      */               break;
/*      */             case 321:
/* 3028 */               if (this.curChar == 110)
/* 3029 */                 this.jjstateSet[this.jjnewStateCnt++] = 320; 
/*      */               break;
/*      */             case 322:
/* 3032 */               if (this.curChar == 101)
/* 3033 */                 jjAddStates(201, 203); 
/*      */               break;
/*      */             case 325:
/* 3036 */               if (this.curChar == 93 && kind > 67)
/* 3037 */                 kind = 67; 
/*      */               break;
/*      */             case 326:
/* 3040 */               if (this.curChar == 115)
/* 3041 */                 this.jjstateSet[this.jjnewStateCnt++] = 322; 
/*      */               break;
/*      */             case 327:
/* 3044 */               if (this.curChar == 114)
/* 3045 */                 this.jjstateSet[this.jjnewStateCnt++] = 326; 
/*      */               break;
/*      */             case 328:
/* 3048 */               if (this.curChar == 117)
/* 3049 */                 this.jjstateSet[this.jjnewStateCnt++] = 327; 
/*      */               break;
/*      */             case 329:
/* 3052 */               if (this.curChar == 99)
/* 3053 */                 this.jjstateSet[this.jjnewStateCnt++] = 328; 
/*      */               break;
/*      */             case 330:
/* 3056 */               if (this.curChar == 101)
/* 3057 */                 this.jjstateSet[this.jjnewStateCnt++] = 329; 
/*      */               break;
/*      */             case 331:
/* 3060 */               if (this.curChar == 114)
/* 3061 */                 this.jjstateSet[this.jjnewStateCnt++] = 330; 
/*      */               break;
/*      */             case 332:
/* 3064 */               if (this.curChar == 101)
/* 3065 */                 this.jjstateSet[this.jjnewStateCnt++] = 333; 
/*      */               break;
/*      */             case 334:
/* 3068 */               if (this.curChar == 115)
/* 3069 */                 this.jjstateSet[this.jjnewStateCnt++] = 332; 
/*      */               break;
/*      */             case 335:
/* 3072 */               if (this.curChar == 114)
/* 3073 */                 this.jjstateSet[this.jjnewStateCnt++] = 334; 
/*      */               break;
/*      */             case 336:
/* 3076 */               if (this.curChar == 117)
/* 3077 */                 this.jjstateSet[this.jjnewStateCnt++] = 335; 
/*      */               break;
/*      */             case 337:
/* 3080 */               if (this.curChar == 99)
/* 3081 */                 this.jjstateSet[this.jjnewStateCnt++] = 336; 
/*      */               break;
/*      */             case 338:
/* 3084 */               if (this.curChar == 101)
/* 3085 */                 this.jjstateSet[this.jjnewStateCnt++] = 337; 
/*      */               break;
/*      */             case 339:
/* 3088 */               if (this.curChar == 114)
/* 3089 */                 this.jjstateSet[this.jjnewStateCnt++] = 338; 
/*      */               break;
/*      */             case 340:
/* 3092 */               if (this.curChar == 107)
/* 3093 */                 jjAddStates(204, 206); 
/*      */               break;
/*      */             case 343:
/* 3096 */               if (this.curChar == 93 && kind > 69)
/* 3097 */                 kind = 69; 
/*      */               break;
/*      */             case 344:
/* 3100 */               if (this.curChar == 99)
/* 3101 */                 this.jjstateSet[this.jjnewStateCnt++] = 340; 
/*      */               break;
/*      */             case 345:
/* 3104 */               if (this.curChar == 97)
/* 3105 */                 this.jjstateSet[this.jjnewStateCnt++] = 344; 
/*      */               break;
/*      */             case 346:
/* 3108 */               if (this.curChar == 98)
/* 3109 */                 this.jjstateSet[this.jjnewStateCnt++] = 345; 
/*      */               break;
/*      */             case 347:
/* 3112 */               if (this.curChar == 108)
/* 3113 */                 this.jjstateSet[this.jjnewStateCnt++] = 346; 
/*      */               break;
/*      */             case 348:
/* 3116 */               if (this.curChar == 108)
/* 3117 */                 this.jjstateSet[this.jjnewStateCnt++] = 347; 
/*      */               break;
/*      */             case 349:
/* 3120 */               if (this.curChar == 97)
/* 3121 */                 this.jjstateSet[this.jjnewStateCnt++] = 348; 
/*      */               break;
/*      */             case 350:
/* 3124 */               if (this.curChar == 102)
/* 3125 */                 this.jjstateSet[this.jjnewStateCnt++] = 349; 
/*      */               break;
/*      */             case 351:
/* 3128 */               if (this.curChar == 101)
/* 3129 */                 this.jjstateSet[this.jjnewStateCnt++] = 352; 
/*      */               break;
/*      */             case 353:
/* 3132 */               if (this.curChar == 112)
/* 3133 */                 this.jjstateSet[this.jjnewStateCnt++] = 351; 
/*      */               break;
/*      */             case 354:
/* 3136 */               if (this.curChar == 97)
/* 3137 */                 this.jjstateSet[this.jjnewStateCnt++] = 353; 
/*      */               break;
/*      */             case 355:
/* 3140 */               if (this.curChar == 99)
/* 3141 */                 this.jjstateSet[this.jjnewStateCnt++] = 354; 
/*      */               break;
/*      */             case 356:
/* 3144 */               if (this.curChar == 115)
/* 3145 */                 this.jjstateSet[this.jjnewStateCnt++] = 355; 
/*      */               break;
/*      */             case 357:
/* 3148 */               if (this.curChar == 101)
/* 3149 */                 this.jjstateSet[this.jjnewStateCnt++] = 356; 
/*      */               break;
/*      */             case 358:
/* 3152 */               if (this.curChar == 111)
/* 3153 */                 this.jjstateSet[this.jjnewStateCnt++] = 359; 
/*      */               break;
/*      */             case 359:
/* 3156 */               if ((0x2000000020L & l) != 0L)
/* 3157 */                 this.jjstateSet[this.jjnewStateCnt++] = 366; 
/*      */               break;
/*      */             case 360:
/* 3160 */               if (this.curChar == 101)
/* 3161 */                 jjAddStates(207, 208); 
/*      */               break;
/*      */             case 362:
/* 3164 */               if (this.curChar == 93 && kind > 72)
/* 3165 */                 kind = 72; 
/*      */               break;
/*      */             case 363:
/* 3168 */               if (this.curChar == 112)
/* 3169 */                 this.jjstateSet[this.jjnewStateCnt++] = 360; 
/*      */               break;
/*      */             case 364:
/* 3172 */               if (this.curChar == 97)
/* 3173 */                 this.jjstateSet[this.jjnewStateCnt++] = 363; 
/*      */               break;
/*      */             case 365:
/* 3176 */               if (this.curChar == 99)
/* 3177 */                 this.jjstateSet[this.jjnewStateCnt++] = 364; 
/*      */               break;
/*      */             case 366:
/* 3180 */               if (this.curChar == 115)
/* 3181 */                 this.jjstateSet[this.jjnewStateCnt++] = 365; 
/*      */               break;
/*      */             case 367:
/* 3184 */               if (this.curChar == 110)
/* 3185 */                 this.jjstateSet[this.jjnewStateCnt++] = 358; 
/*      */               break;
/*      */             case 399:
/* 3188 */               if (this.curChar == 102)
/* 3189 */                 jjAddStates(209, 210); 
/*      */               break;
/*      */             case 401:
/* 3192 */               if (this.curChar == 93 && kind > 36)
/* 3193 */                 kind = 36; 
/*      */               break;
/*      */             case 402:
/* 3196 */               if (this.curChar == 105)
/* 3197 */                 this.jjstateSet[this.jjnewStateCnt++] = 399; 
/*      */               break;
/*      */             case 406:
/* 3200 */               if (this.curChar == 116)
/* 3201 */                 jjAddStates(211, 212); 
/*      */               break;
/*      */             case 408:
/* 3204 */               if (this.curChar == 93 && kind > 37)
/* 3205 */                 kind = 37; 
/*      */               break;
/*      */             case 409:
/* 3208 */               if (this.curChar == 115)
/* 3209 */                 this.jjstateSet[this.jjnewStateCnt++] = 406; 
/*      */               break;
/*      */             case 410:
/* 3212 */               if (this.curChar == 105)
/* 3213 */                 this.jjstateSet[this.jjnewStateCnt++] = 409; 
/*      */               break;
/*      */             case 411:
/* 3216 */               if (this.curChar == 108)
/* 3217 */                 this.jjstateSet[this.jjnewStateCnt++] = 410; 
/*      */               break;
/*      */             case 415:
/* 3220 */               if (this.curChar == 115)
/* 3221 */                 jjAddStates(213, 214); 
/*      */               break;
/*      */             case 417:
/* 3224 */               if (this.curChar == 93 && kind > 38)
/* 3225 */                 kind = 38; 
/*      */               break;
/*      */             case 418:
/* 3228 */               if (this.curChar == 109)
/* 3229 */                 this.jjstateSet[this.jjnewStateCnt++] = 415; 
/*      */               break;
/*      */             case 419:
/* 3232 */               if (this.curChar == 101)
/* 3233 */                 this.jjstateSet[this.jjnewStateCnt++] = 418; 
/*      */               break;
/*      */             case 420:
/* 3236 */               if (this.curChar == 116)
/* 3237 */                 this.jjstateSet[this.jjnewStateCnt++] = 419; 
/*      */               break;
/*      */             case 421:
/* 3240 */               if (this.curChar == 105)
/* 3241 */                 this.jjstateSet[this.jjnewStateCnt++] = 420; 
/*      */               break;
/*      */             case 425:
/* 3244 */               if (this.curChar == 112)
/* 3245 */                 jjAddStates(215, 216); 
/*      */               break;
/*      */             case 427:
/* 3248 */               if (this.curChar == 93 && kind > 39)
/* 3249 */                 kind = 39; 
/*      */               break;
/*      */             case 428:
/* 3252 */               if (this.curChar == 101)
/* 3253 */                 this.jjstateSet[this.jjnewStateCnt++] = 425; 
/*      */               break;
/*      */             case 429:
/* 3256 */               if (this.curChar == 115)
/* 3257 */                 this.jjstateSet[this.jjnewStateCnt++] = 428; 
/*      */               break;
/*      */             case 433:
/* 3260 */               if (this.curChar == 114)
/* 3261 */                 jjAddStates(217, 218); 
/*      */               break;
/*      */             case 435:
/* 3264 */               if (this.curChar == 93 && kind > 40)
/* 3265 */                 kind = 40; 
/*      */               break;
/*      */             case 436:
/* 3268 */               if (this.curChar == 101)
/* 3269 */                 this.jjstateSet[this.jjnewStateCnt++] = 433; 
/*      */               break;
/*      */             case 437:
/* 3272 */               if (this.curChar == 118)
/* 3273 */                 this.jjstateSet[this.jjnewStateCnt++] = 436; 
/*      */               break;
/*      */             case 438:
/* 3276 */               if (this.curChar == 111)
/* 3277 */                 this.jjstateSet[this.jjnewStateCnt++] = 437; 
/*      */               break;
/*      */             case 439:
/* 3280 */               if (this.curChar == 99)
/* 3281 */                 this.jjstateSet[this.jjnewStateCnt++] = 438; 
/*      */               break;
/*      */             case 440:
/* 3284 */               if (this.curChar == 101)
/* 3285 */                 this.jjstateSet[this.jjnewStateCnt++] = 439; 
/*      */               break;
/*      */             case 441:
/* 3288 */               if (this.curChar == 114)
/* 3289 */                 this.jjstateSet[this.jjnewStateCnt++] = 440; 
/*      */               break;
/*      */             case 445:
/* 3292 */               if (this.curChar == 116)
/* 3293 */                 jjAddStates(219, 220); 
/*      */               break;
/*      */             case 447:
/* 3296 */               if (this.curChar == 93 && kind > 41)
/* 3297 */                 kind = 41; 
/*      */               break;
/*      */             case 448:
/* 3300 */               if (this.curChar == 112)
/* 3301 */                 this.jjstateSet[this.jjnewStateCnt++] = 445; 
/*      */               break;
/*      */             case 449:
/* 3304 */               if (this.curChar == 109)
/* 3305 */                 this.jjstateSet[this.jjnewStateCnt++] = 448; 
/*      */               break;
/*      */             case 450:
/* 3308 */               if (this.curChar == 101)
/* 3309 */                 this.jjstateSet[this.jjnewStateCnt++] = 449; 
/*      */               break;
/*      */             case 451:
/* 3312 */               if (this.curChar == 116)
/* 3313 */                 this.jjstateSet[this.jjnewStateCnt++] = 450; 
/*      */               break;
/*      */             case 452:
/* 3316 */               if (this.curChar == 116)
/* 3317 */                 this.jjstateSet[this.jjnewStateCnt++] = 451; 
/*      */               break;
/*      */             case 453:
/* 3320 */               if (this.curChar == 97)
/* 3321 */                 this.jjstateSet[this.jjnewStateCnt++] = 452; 
/*      */               break;
/*      */             case 457:
/* 3324 */               if (this.curChar == 114)
/* 3325 */                 this.jjstateSet[this.jjnewStateCnt++] = 458; 
/*      */               break;
/*      */             case 458:
/* 3328 */               if ((0x2000000020L & l) != 0L)
/* 3329 */                 this.jjstateSet[this.jjnewStateCnt++] = 463; 
/*      */               break;
/*      */             case 459:
/* 3332 */               if (this.curChar == 104)
/* 3333 */                 jjAddStates(221, 222); 
/*      */               break;
/*      */             case 461:
/* 3336 */               if (this.curChar == 93 && kind > 42)
/* 3337 */                 kind = 42; 
/*      */               break;
/*      */             case 462:
/* 3340 */               if (this.curChar == 99)
/* 3341 */                 this.jjstateSet[this.jjnewStateCnt++] = 459; 
/*      */               break;
/*      */             case 463:
/* 3344 */               if (this.curChar == 97)
/* 3345 */                 this.jjstateSet[this.jjnewStateCnt++] = 462; 
/*      */               break;
/*      */             case 464:
/* 3348 */               if (this.curChar == 111)
/* 3349 */                 this.jjstateSet[this.jjnewStateCnt++] = 457; 
/*      */               break;
/*      */             case 465:
/* 3352 */               if (this.curChar == 102)
/* 3353 */                 this.jjstateSet[this.jjnewStateCnt++] = 464; 
/*      */               break;
/*      */             case 469:
/* 3356 */               if (this.curChar == 108)
/* 3357 */                 jjAddStates(223, 224); 
/*      */               break;
/*      */             case 471:
/* 3360 */               if (this.curChar == 93 && kind > 43)
/* 3361 */                 kind = 43; 
/*      */               break;
/*      */             case 472:
/* 3364 */               if (this.curChar == 97)
/* 3365 */                 this.jjstateSet[this.jjnewStateCnt++] = 469; 
/*      */               break;
/*      */             case 473:
/* 3368 */               if (this.curChar == 99)
/* 3369 */                 this.jjstateSet[this.jjnewStateCnt++] = 472; 
/*      */               break;
/*      */             case 474:
/* 3372 */               if (this.curChar == 111)
/* 3373 */                 this.jjstateSet[this.jjnewStateCnt++] = 473; 
/*      */               break;
/*      */             case 475:
/* 3376 */               if (this.curChar == 108)
/* 3377 */                 this.jjstateSet[this.jjnewStateCnt++] = 474; 
/*      */               break;
/*      */             case 479:
/* 3380 */               if (this.curChar == 108)
/* 3381 */                 jjAddStates(225, 226); 
/*      */               break;
/*      */             case 481:
/* 3384 */               if (this.curChar == 93 && kind > 44)
/* 3385 */                 kind = 44; 
/*      */               break;
/*      */             case 482:
/* 3388 */               if (this.curChar == 97)
/* 3389 */                 this.jjstateSet[this.jjnewStateCnt++] = 479; 
/*      */               break;
/*      */             case 483:
/* 3392 */               if (this.curChar == 98)
/* 3393 */                 this.jjstateSet[this.jjnewStateCnt++] = 482; 
/*      */               break;
/*      */             case 484:
/* 3396 */               if (this.curChar == 111)
/* 3397 */                 this.jjstateSet[this.jjnewStateCnt++] = 483; 
/*      */               break;
/*      */             case 485:
/* 3400 */               if (this.curChar == 108)
/* 3401 */                 this.jjstateSet[this.jjnewStateCnt++] = 484; 
/*      */               break;
/*      */             case 486:
/* 3404 */               if (this.curChar == 103)
/* 3405 */                 this.jjstateSet[this.jjnewStateCnt++] = 485; 
/*      */               break;
/*      */             case 490:
/* 3408 */               if (this.curChar == 110)
/* 3409 */                 jjAddStates(227, 228); 
/*      */               break;
/*      */             case 492:
/* 3412 */               if (this.curChar == 93 && kind > 45)
/* 3413 */                 kind = 45; 
/*      */               break;
/*      */             case 493:
/* 3416 */               if (this.curChar == 103)
/* 3417 */                 this.jjstateSet[this.jjnewStateCnt++] = 490; 
/*      */               break;
/*      */             case 494:
/* 3420 */               if (this.curChar == 105)
/* 3421 */                 this.jjstateSet[this.jjnewStateCnt++] = 493; 
/*      */               break;
/*      */             case 495:
/* 3424 */               if (this.curChar == 115)
/* 3425 */                 this.jjstateSet[this.jjnewStateCnt++] = 494; 
/*      */               break;
/*      */             case 496:
/* 3428 */               if (this.curChar == 115)
/* 3429 */                 this.jjstateSet[this.jjnewStateCnt++] = 495; 
/*      */               break;
/*      */             case 497:
/* 3432 */               if (this.curChar == 97)
/* 3433 */                 this.jjstateSet[this.jjnewStateCnt++] = 496; 
/*      */               break;
/*      */             case 501:
/* 3436 */               if (this.curChar == 110)
/* 3437 */                 jjAddStates(229, 230); 
/*      */               break;
/*      */             case 503:
/* 3440 */               if (this.curChar == 93 && kind > 46)
/* 3441 */                 kind = 46; 
/*      */               break;
/*      */             case 504:
/* 3444 */               if (this.curChar == 111)
/* 3445 */                 this.jjstateSet[this.jjnewStateCnt++] = 501; 
/*      */               break;
/*      */             case 505:
/* 3448 */               if (this.curChar == 105)
/* 3449 */                 this.jjstateSet[this.jjnewStateCnt++] = 504; 
/*      */               break;
/*      */             case 506:
/* 3452 */               if (this.curChar == 116)
/* 3453 */                 this.jjstateSet[this.jjnewStateCnt++] = 505; 
/*      */               break;
/*      */             case 507:
/* 3456 */               if (this.curChar == 99)
/* 3457 */                 this.jjstateSet[this.jjnewStateCnt++] = 506; 
/*      */               break;
/*      */             case 508:
/* 3460 */               if (this.curChar == 110)
/* 3461 */                 this.jjstateSet[this.jjnewStateCnt++] = 507; 
/*      */               break;
/*      */             case 509:
/* 3464 */               if (this.curChar == 117)
/* 3465 */                 this.jjstateSet[this.jjnewStateCnt++] = 508; 
/*      */               break;
/*      */             case 510:
/* 3468 */               if (this.curChar == 102)
/* 3469 */                 this.jjstateSet[this.jjnewStateCnt++] = 509; 
/*      */               break;
/*      */             case 514:
/* 3472 */               if (this.curChar == 111)
/* 3473 */                 jjAddStates(231, 232); 
/*      */               break;
/*      */             case 516:
/* 3476 */               if (this.curChar == 93 && kind > 47)
/* 3477 */                 kind = 47; 
/*      */               break;
/*      */             case 517:
/* 3480 */               if (this.curChar == 114)
/* 3481 */                 this.jjstateSet[this.jjnewStateCnt++] = 514; 
/*      */               break;
/*      */             case 518:
/* 3484 */               if (this.curChar == 99)
/* 3485 */                 this.jjstateSet[this.jjnewStateCnt++] = 517; 
/*      */               break;
/*      */             case 519:
/* 3488 */               if (this.curChar == 97)
/* 3489 */                 this.jjstateSet[this.jjnewStateCnt++] = 518; 
/*      */               break;
/*      */             case 520:
/* 3492 */               if (this.curChar == 109)
/* 3493 */                 this.jjstateSet[this.jjnewStateCnt++] = 519; 
/*      */               break;
/*      */             case 524:
/* 3496 */               if (this.curChar == 116)
/* 3497 */                 this.jjstateSet[this.jjnewStateCnt++] = 525; 
/*      */               break;
/*      */             case 525:
/* 3500 */               if ((0x4000000040L & l) != 0L)
/* 3501 */                 this.jjstateSet[this.jjnewStateCnt++] = 532; 
/*      */               break;
/*      */             case 526:
/* 3504 */               if (this.curChar == 116)
/* 3505 */                 jjAddStates(233, 234); 
/*      */               break;
/*      */             case 528:
/* 3508 */               if (this.curChar == 93 && kind > 48)
/* 3509 */                 kind = 48; 
/*      */               break;
/*      */             case 529:
/* 3512 */               if (this.curChar == 97)
/* 3513 */                 this.jjstateSet[this.jjnewStateCnt++] = 526; 
/*      */               break;
/*      */             case 530:
/* 3516 */               if (this.curChar == 109)
/* 3517 */                 this.jjstateSet[this.jjnewStateCnt++] = 529; 
/*      */               break;
/*      */             case 531:
/* 3520 */               if (this.curChar == 114)
/* 3521 */                 this.jjstateSet[this.jjnewStateCnt++] = 530; 
/*      */               break;
/*      */             case 532:
/* 3524 */               if (this.curChar == 111)
/* 3525 */                 this.jjstateSet[this.jjnewStateCnt++] = 531; 
/*      */               break;
/*      */             case 533:
/* 3528 */               if (this.curChar == 117)
/* 3529 */                 this.jjstateSet[this.jjnewStateCnt++] = 524; 
/*      */               break;
/*      */             case 534:
/* 3532 */               if (this.curChar == 112)
/* 3533 */                 this.jjstateSet[this.jjnewStateCnt++] = 533; 
/*      */               break;
/*      */             case 535:
/* 3536 */               if (this.curChar == 116)
/* 3537 */                 this.jjstateSet[this.jjnewStateCnt++] = 534; 
/*      */               break;
/*      */             case 536:
/* 3540 */               if (this.curChar == 117)
/* 3541 */                 this.jjstateSet[this.jjnewStateCnt++] = 535; 
/*      */               break;
/*      */             case 537:
/* 3544 */               if (this.curChar == 111)
/* 3545 */                 this.jjstateSet[this.jjnewStateCnt++] = 536; 
/*      */               break;
/*      */             case 541:
/* 3548 */               if (this.curChar == 111)
/* 3549 */                 this.jjstateSet[this.jjnewStateCnt++] = 542; 
/*      */               break;
/*      */             case 542:
/* 3552 */               if ((0x2000000020L & l) != 0L)
/* 3553 */                 this.jjstateSet[this.jjnewStateCnt++] = 546; 
/*      */               break;
/*      */             case 543:
/* 3556 */               if (this.curChar == 99)
/* 3557 */                 jjAddStates(235, 236); 
/*      */               break;
/*      */             case 545:
/* 3560 */               if (this.curChar == 93 && kind > 49)
/* 3561 */                 kind = 49; 
/*      */               break;
/*      */             case 546:
/* 3564 */               if (this.curChar == 115)
/* 3565 */                 this.jjstateSet[this.jjnewStateCnt++] = 543; 
/*      */               break;
/*      */             case 547:
/* 3568 */               if (this.curChar == 116)
/* 3569 */                 this.jjstateSet[this.jjnewStateCnt++] = 541; 
/*      */               break;
/*      */             case 548:
/* 3572 */               if (this.curChar == 117)
/* 3573 */                 this.jjstateSet[this.jjnewStateCnt++] = 547; 
/*      */               break;
/*      */             case 549:
/* 3576 */               if (this.curChar == 97)
/* 3577 */                 this.jjstateSet[this.jjnewStateCnt++] = 548; 
/*      */               break;
/*      */             case 553:
/* 3580 */               if (this.curChar == 111)
/* 3581 */                 jjAddStates(335, 336); 
/*      */               break;
/*      */             case 554:
/* 3584 */               if (this.curChar == 101)
/* 3585 */                 jjCheckNAdd(558); 
/*      */               break;
/*      */             case 555:
/* 3588 */               if (this.curChar == 99)
/* 3589 */                 jjAddStates(237, 238); 
/*      */               break;
/*      */             case 557:
/* 3592 */               if (this.curChar == 93 && kind > 50)
/* 3593 */                 kind = 50; 
/*      */               break;
/*      */             case 558:
/* 3596 */               if (this.curChar == 115)
/* 3597 */                 this.jjstateSet[this.jjnewStateCnt++] = 555; 
/*      */               break;
/*      */             case 559:
/* 3600 */               if (this.curChar == 111)
/* 3601 */                 this.jjstateSet[this.jjnewStateCnt++] = 554; 
/*      */               break;
/*      */             case 560:
/* 3604 */               if (this.curChar == 116)
/* 3605 */                 this.jjstateSet[this.jjnewStateCnt++] = 559; 
/*      */               break;
/*      */             case 561:
/* 3608 */               if (this.curChar == 117)
/* 3609 */                 this.jjstateSet[this.jjnewStateCnt++] = 560; 
/*      */               break;
/*      */             case 562:
/* 3612 */               if (this.curChar == 97)
/* 3613 */                 this.jjstateSet[this.jjnewStateCnt++] = 561; 
/*      */               break;
/*      */             case 563:
/* 3616 */               if (this.curChar == 69)
/* 3617 */                 jjCheckNAdd(558); 
/*      */               break;
/*      */             case 564:
/* 3620 */               if (this.curChar == 111)
/* 3621 */                 this.jjstateSet[this.jjnewStateCnt++] = 563; 
/*      */               break;
/*      */             case 565:
/* 3624 */               if (this.curChar == 116)
/* 3625 */                 this.jjstateSet[this.jjnewStateCnt++] = 564; 
/*      */               break;
/*      */             case 566:
/* 3628 */               if (this.curChar == 117)
/* 3629 */                 this.jjstateSet[this.jjnewStateCnt++] = 565; 
/*      */               break;
/*      */             case 567:
/* 3632 */               if (this.curChar == 65)
/* 3633 */                 this.jjstateSet[this.jjnewStateCnt++] = 566; 
/*      */               break;
/*      */             case 568:
/* 3636 */               if (this.curChar == 110)
/* 3637 */                 this.jjstateSet[this.jjnewStateCnt++] = 553; 
/*      */               break;
/*      */             case 572:
/* 3640 */               if (this.curChar == 115)
/* 3641 */                 jjAddStates(239, 240); 
/*      */               break;
/*      */             case 574:
/* 3644 */               if (this.curChar == 93 && kind > 51)
/* 3645 */                 kind = 51; 
/*      */               break;
/*      */             case 575:
/* 3648 */               if (this.curChar == 115)
/* 3649 */                 this.jjstateSet[this.jjnewStateCnt++] = 572; 
/*      */               break;
/*      */             case 576:
/* 3652 */               if (this.curChar == 101)
/* 3653 */                 this.jjstateSet[this.jjnewStateCnt++] = 575; 
/*      */               break;
/*      */             case 577:
/* 3656 */               if (this.curChar == 114)
/* 3657 */                 this.jjstateSet[this.jjnewStateCnt++] = 576; 
/*      */               break;
/*      */             case 578:
/* 3660 */               if (this.curChar == 112)
/* 3661 */                 this.jjstateSet[this.jjnewStateCnt++] = 577; 
/*      */               break;
/*      */             case 579:
/* 3664 */               if (this.curChar == 109)
/* 3665 */                 this.jjstateSet[this.jjnewStateCnt++] = 578; 
/*      */               break;
/*      */             case 580:
/* 3668 */               if (this.curChar == 111)
/* 3669 */                 this.jjstateSet[this.jjnewStateCnt++] = 579; 
/*      */               break;
/*      */             case 581:
/* 3672 */               if (this.curChar == 99)
/* 3673 */                 this.jjstateSet[this.jjnewStateCnt++] = 580; 
/*      */               break;
/*      */             case 585:
/* 3676 */               if (this.curChar == 109)
/* 3677 */                 jjAddStates(241, 242); 
/*      */               break;
/*      */             case 587:
/* 3680 */               if (this.curChar == 93 && kind > 52)
/* 3681 */                 kind = 52; 
/*      */               break;
/*      */             case 588:
/* 3684 */               if (this.curChar == 114)
/* 3685 */                 this.jjstateSet[this.jjnewStateCnt++] = 585; 
/*      */               break;
/*      */             case 589:
/* 3688 */               if (this.curChar == 111)
/* 3689 */                 this.jjstateSet[this.jjnewStateCnt++] = 588; 
/*      */               break;
/*      */             case 590:
/* 3692 */               if (this.curChar == 102)
/* 3693 */                 this.jjstateSet[this.jjnewStateCnt++] = 589; 
/*      */               break;
/*      */             case 591:
/* 3696 */               if (this.curChar == 115)
/* 3697 */                 this.jjstateSet[this.jjnewStateCnt++] = 590; 
/*      */               break;
/*      */             case 592:
/* 3700 */               if (this.curChar == 110)
/* 3701 */                 this.jjstateSet[this.jjnewStateCnt++] = 591; 
/*      */               break;
/*      */             case 593:
/* 3704 */               if (this.curChar == 97)
/* 3705 */                 this.jjstateSet[this.jjnewStateCnt++] = 592; 
/*      */               break;
/*      */             case 594:
/* 3708 */               if (this.curChar == 114)
/* 3709 */                 this.jjstateSet[this.jjnewStateCnt++] = 593; 
/*      */               break;
/*      */             case 595:
/* 3712 */               if (this.curChar == 116)
/* 3713 */                 this.jjstateSet[this.jjnewStateCnt++] = 594; 
/*      */               break;
/*      */             case 599:
/* 3716 */               if (this.curChar == 104)
/* 3717 */                 jjAddStates(243, 244); 
/*      */               break;
/*      */             case 601:
/* 3720 */               if (this.curChar == 93 && kind > 53)
/* 3721 */                 kind = 53; 
/*      */               break;
/*      */             case 602:
/* 3724 */               if (this.curChar == 99)
/* 3725 */                 this.jjstateSet[this.jjnewStateCnt++] = 599; 
/*      */               break;
/*      */             case 603:
/* 3728 */               if (this.curChar == 116)
/* 3729 */                 this.jjstateSet[this.jjnewStateCnt++] = 602; 
/*      */               break;
/*      */             case 604:
/* 3732 */               if (this.curChar == 105)
/* 3733 */                 this.jjstateSet[this.jjnewStateCnt++] = 603; 
/*      */               break;
/*      */             case 605:
/* 3736 */               if (this.curChar == 119)
/* 3737 */                 this.jjstateSet[this.jjnewStateCnt++] = 604; 
/*      */               break;
/*      */             case 606:
/* 3740 */               if (this.curChar == 115)
/* 3741 */                 this.jjstateSet[this.jjnewStateCnt++] = 605; 
/*      */               break;
/*      */             case 627:
/* 3744 */               if (this.curChar == 101)
/* 3745 */                 jjAddStates(245, 246); 
/*      */               break;
/*      */             case 629:
/* 3748 */               if (this.curChar == 93 && kind > 71)
/* 3749 */                 kind = 71; 
/*      */               break;
/*      */             case 630:
/* 3752 */               if (this.curChar == 112)
/* 3753 */                 this.jjstateSet[this.jjnewStateCnt++] = 627; 
/*      */               break;
/*      */             case 631:
/* 3756 */               if (this.curChar == 97)
/* 3757 */                 this.jjstateSet[this.jjnewStateCnt++] = 630; 
/*      */               break;
/*      */             case 632:
/* 3760 */               if (this.curChar == 99)
/* 3761 */                 this.jjstateSet[this.jjnewStateCnt++] = 631; 
/*      */               break;
/*      */             case 633:
/* 3764 */               if (this.curChar == 115)
/* 3765 */                 this.jjstateSet[this.jjnewStateCnt++] = 632; 
/*      */               break;
/*      */             case 634:
/* 3768 */               if (this.curChar == 101)
/* 3769 */                 this.jjstateSet[this.jjnewStateCnt++] = 633; 
/*      */               break;
/*      */             case 639:
/* 3772 */               if (this.curChar == 111)
/* 3773 */                 this.jjstateSet[this.jjnewStateCnt++] = 640; 
/*      */               break;
/*      */             case 640:
/* 3776 */               if ((0x2000000020L & l) != 0L)
/* 3777 */                 this.jjstateSet[this.jjnewStateCnt++] = 647; 
/*      */               break;
/*      */             case 641:
/* 3780 */               if (this.curChar == 101)
/* 3781 */                 jjAddStates(247, 248); 
/*      */               break;
/*      */             case 643:
/* 3784 */               if (this.curChar == 93 && kind > 73)
/* 3785 */                 kind = 73; 
/*      */               break;
/*      */             case 644:
/* 3788 */               if (this.curChar == 112)
/* 3789 */                 this.jjstateSet[this.jjnewStateCnt++] = 641; 
/*      */               break;
/*      */             case 645:
/* 3792 */               if (this.curChar == 97)
/* 3793 */                 this.jjstateSet[this.jjnewStateCnt++] = 644; 
/*      */               break;
/*      */             case 646:
/* 3796 */               if (this.curChar == 99)
/* 3797 */                 this.jjstateSet[this.jjnewStateCnt++] = 645; 
/*      */               break;
/*      */             case 647:
/* 3800 */               if (this.curChar == 115)
/* 3801 */                 this.jjstateSet[this.jjnewStateCnt++] = 646; 
/*      */               break;
/*      */             case 648:
/* 3804 */               if (this.curChar == 110)
/* 3805 */                 this.jjstateSet[this.jjnewStateCnt++] = 639; 
/*      */               break;
/*      */             case 652:
/* 3808 */               if (this.curChar == 108)
/* 3809 */                 this.jjstateSet[this.jjnewStateCnt++] = 653; 
/*      */               break;
/*      */             case 654:
/*      */             case 687:
/* 3813 */               if (this.curChar == 116)
/* 3814 */                 jjCheckNAdd(652); 
/*      */               break;
/*      */             case 655:
/* 3817 */               if (this.curChar == 102)
/* 3818 */                 this.jjstateSet[this.jjnewStateCnt++] = 654; 
/*      */               break;
/*      */             case 657:
/* 3821 */               if (this.curChar == 108)
/* 3822 */                 jjAddStates(337, 338); 
/*      */               break;
/*      */             case 659:
/* 3825 */               if (this.curChar == 93 && kind > 77)
/* 3826 */                 kind = 77; 
/*      */               break;
/*      */             case 660:
/*      */             case 690:
/* 3830 */               if (this.curChar == 116)
/* 3831 */                 jjCheckNAdd(657); 
/*      */               break;
/*      */             case 661:
/* 3834 */               if (this.curChar == 102)
/* 3835 */                 this.jjstateSet[this.jjnewStateCnt++] = 660; 
/*      */               break;
/*      */             case 664:
/* 3838 */               if ((0x7FFFFFE87FFFFFEL & l) == 0L)
/*      */                 break; 
/* 3840 */               if (kind > 78)
/* 3841 */                 kind = 78; 
/* 3842 */               this.jjstateSet[this.jjnewStateCnt++] = 664;
/*      */               break;
/*      */             case 666:
/* 3845 */               if (this.curChar == 91)
/* 3846 */                 jjAddStates(261, 332); 
/*      */               break;
/*      */             case 688:
/* 3849 */               if (this.curChar == 102)
/* 3850 */                 this.jjstateSet[this.jjnewStateCnt++] = 687; 
/*      */               break;
/*      */             case 691:
/* 3853 */               if (this.curChar == 102)
/* 3854 */                 this.jjstateSet[this.jjnewStateCnt++] = 690; 
/*      */               break;
/*      */             case 694:
/* 3857 */               if (this.curChar == 91)
/* 3858 */                 jjAddStates(7, 8); 
/*      */               break;
/*      */             case 698:
/* 3861 */               if (this.curChar == 64)
/* 3862 */                 jjCheckNAddStates(339, 342); 
/*      */               break;
/*      */             case 699:
/*      */             case 700:
/* 3866 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L)
/* 3867 */                 jjCheckNAddStates(249, 253); 
/*      */               break;
/*      */             case 701:
/*      */             case 711:
/* 3871 */               if (this.curChar == 92)
/* 3872 */                 jjCheckNAdd(702); 
/*      */               break;
/*      */             case 704:
/*      */             case 705:
/* 3876 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L)
/* 3877 */                 jjCheckNAddStates(256, 260); 
/*      */               break;
/*      */             case 706:
/*      */             case 710:
/* 3881 */               if (this.curChar == 92)
/* 3882 */                 jjCheckNAdd(707); 
/*      */               break;
/*      */             case 709:
/* 3885 */               if (this.curChar == 93 && kind > 75) {
/* 3886 */                 kind = 75;
/*      */               }
/*      */               break;
/*      */           } 
/* 3890 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 3894 */         int hiByte = this.curChar >> 8;
/* 3895 */         int i1 = hiByte >> 6;
/* 3896 */         long l1 = 1L << (hiByte & 0x3F);
/* 3897 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 3898 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 3901 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/*      */             case 2:
/* 3905 */               if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 3907 */               if (kind > 80)
/* 3908 */                 kind = 80; 
/* 3909 */               jjCheckNAdd(1);
/*      */               break;
/*      */             case 699:
/*      */             case 700:
/* 3913 */               if (jjCanMove_1(hiByte, i1, i2, l1, l2))
/* 3914 */                 jjCheckNAddStates(249, 253); 
/*      */               break;
/*      */             case 704:
/*      */             case 705:
/* 3918 */               if (jjCanMove_1(hiByte, i1, i2, l1, l2))
/* 3919 */                 jjCheckNAddStates(256, 260);  break;
/*      */             default:
/* 3921 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 3923 */         } while (i != startsAt);
/*      */       } 
/* 3925 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 3927 */         this.jjmatchedKind = kind;
/* 3928 */         this.jjmatchedPos = curPos;
/* 3929 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 3931 */       curPos++;
/* 3932 */       if ((i = this.jjnewStateCnt) == (startsAt = 713 - (this.jjnewStateCnt = startsAt)))
/* 3933 */         return curPos;  
/* 3934 */       try { this.curChar = this.input_stream.readChar(); }
/* 3935 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_2(int pos, long active0, long active1, long active2) {
/* 3939 */     switch (pos) {
/*      */       
/*      */       case 0:
/* 3942 */         if ((active2 & 0x20L) != 0L)
/* 3943 */           return 2; 
/* 3944 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x3800L) != 0L) {
/*      */           
/* 3946 */           this.jjmatchedKind = 142;
/* 3947 */           return 104;
/*      */         } 
/* 3949 */         if ((active1 & 0x2000800000000000L) != 0L)
/* 3950 */           return 44; 
/* 3951 */         if ((active1 & 0x1000005800000000L) != 0L)
/* 3952 */           return 54; 
/* 3953 */         if ((active1 & 0x204200000000000L) != 0L)
/* 3954 */           return 47; 
/* 3955 */         return -1;
/*      */       case 1:
/* 3957 */         if ((active2 & 0x1800L) != 0L)
/* 3958 */           return 104; 
/* 3959 */         if ((active1 & 0x1000005000000000L) != 0L)
/* 3960 */           return 53; 
/* 3961 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 3963 */           if (this.jjmatchedPos != 1) {
/*      */             
/* 3965 */             this.jjmatchedKind = 142;
/* 3966 */             this.jjmatchedPos = 1;
/*      */           } 
/* 3968 */           return 104;
/*      */         } 
/* 3970 */         return -1;
/*      */       case 2:
/* 3972 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 3974 */           this.jjmatchedKind = 142;
/* 3975 */           this.jjmatchedPos = 2;
/* 3976 */           return 104;
/*      */         } 
/* 3978 */         return -1;
/*      */       case 3:
/* 3980 */         if ((active1 & 0x100000000L) != 0L)
/* 3981 */           return 104; 
/* 3982 */         if ((active1 & 0x80000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 3984 */           this.jjmatchedKind = 142;
/* 3985 */           this.jjmatchedPos = 3;
/* 3986 */           return 104;
/*      */         } 
/* 3988 */         return -1;
/*      */     } 
/* 3990 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_2(int pos, long active0, long active1, long active2) {
/* 3994 */     return jjMoveNfa_2(jjStopStringLiteralDfa_2(pos, active0, active1, active2), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_2() {
/* 3997 */     switch (this.curChar) {
/*      */       
/*      */       case 33:
/* 4000 */         this.jjmatchedKind = 129;
/* 4001 */         return jjMoveStringLiteralDfa1_2(8796093022208L, 0L);
/*      */       case 37:
/* 4003 */         this.jjmatchedKind = 126;
/* 4004 */         return jjMoveStringLiteralDfa1_2(281474976710656L, 0L);
/*      */       case 40:
/* 4006 */         return jjStopAtPos(0, 135);
/*      */       case 41:
/* 4008 */         return jjStopAtPos(0, 136);
/*      */       case 42:
/* 4010 */         this.jjmatchedKind = 122;
/* 4011 */         return jjMoveStringLiteralDfa1_2(576531121047601152L, 0L);
/*      */       case 43:
/* 4013 */         this.jjmatchedKind = 120;
/* 4014 */         return jjMoveStringLiteralDfa1_2(580542139465728L, 0L);
/*      */       case 44:
/* 4016 */         return jjStopAtPos(0, 130);
/*      */       case 45:
/* 4018 */         this.jjmatchedKind = 121;
/* 4019 */         return jjMoveStringLiteralDfa1_2(1161084278931456L, 0L);
/*      */       case 46:
/* 4021 */         this.jjmatchedKind = 99;
/* 4022 */         return jjMoveStringLiteralDfa1_2(1152921848204230656L, 0L);
/*      */       case 47:
/* 4024 */         this.jjmatchedKind = 125;
/* 4025 */         return jjMoveStringLiteralDfa1_2(140737488355328L, 0L);
/*      */       case 58:
/* 4027 */         return jjStopAtPos(0, 132);
/*      */       case 59:
/* 4029 */         return jjStopAtPos(0, 131);
/*      */       case 61:
/* 4031 */         this.jjmatchedKind = 105;
/* 4032 */         return jjMoveStringLiteralDfa1_2(4398046511104L, 0L);
/*      */       case 62:
/* 4034 */         return jjStopAtPos(0, 148);
/*      */       case 63:
/* 4036 */         this.jjmatchedKind = 103;
/* 4037 */         return jjMoveStringLiteralDfa1_2(1099511627776L, 0L);
/*      */       case 91:
/* 4039 */         return jjStartNfaWithStates_2(0, 133, 2);
/*      */       case 93:
/* 4041 */         return jjStopAtPos(0, 134);
/*      */       case 97:
/* 4043 */         return jjMoveStringLiteralDfa1_2(0L, 4096L);
/*      */       case 102:
/* 4045 */         return jjMoveStringLiteralDfa1_2(2147483648L, 0L);
/*      */       case 105:
/* 4047 */         return jjMoveStringLiteralDfa1_2(0L, 2048L);
/*      */       case 116:
/* 4049 */         return jjMoveStringLiteralDfa1_2(4294967296L, 0L);
/*      */       case 117:
/* 4051 */         return jjMoveStringLiteralDfa1_2(0L, 8192L);
/*      */       case 123:
/* 4053 */         return jjStopAtPos(0, 137);
/*      */       case 125:
/* 4055 */         return jjStopAtPos(0, 138);
/*      */     } 
/* 4057 */     return jjMoveNfa_2(1, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_2(long active1, long active2) {
/*      */     try {
/* 4061 */       this.curChar = this.input_stream.readChar();
/* 4062 */     } catch (IOException e) {
/* 4063 */       jjStopStringLiteralDfa_2(0, 0L, active1, active2);
/* 4064 */       return 1;
/*      */     } 
/* 4066 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 4069 */         if ((active1 & 0x800000000000000L) != 0L)
/* 4070 */           return jjStopAtPos(1, 123); 
/*      */         break;
/*      */       case 43:
/* 4073 */         if ((active1 & 0x2000000000000L) != 0L)
/* 4074 */           return jjStopAtPos(1, 113); 
/*      */         break;
/*      */       case 45:
/* 4077 */         if ((active1 & 0x4000000000000L) != 0L)
/* 4078 */           return jjStopAtPos(1, 114); 
/*      */         break;
/*      */       case 46:
/* 4081 */         if ((active1 & 0x1000000000L) != 0L) {
/*      */           
/* 4083 */           this.jjmatchedKind = 100;
/* 4084 */           this.jjmatchedPos = 1;
/*      */         } 
/* 4086 */         return jjMoveStringLiteralDfa2_2(active1, 1152921779484753920L, active2, 0L);
/*      */       case 61:
/* 4088 */         if ((active1 & 0x40000000000L) != 0L)
/* 4089 */           return jjStopAtPos(1, 106); 
/* 4090 */         if ((active1 & 0x80000000000L) != 0L)
/* 4091 */           return jjStopAtPos(1, 107); 
/* 4092 */         if ((active1 & 0x100000000000L) != 0L)
/* 4093 */           return jjStopAtPos(1, 108); 
/* 4094 */         if ((active1 & 0x200000000000L) != 0L)
/* 4095 */           return jjStopAtPos(1, 109); 
/* 4096 */         if ((active1 & 0x400000000000L) != 0L)
/* 4097 */           return jjStopAtPos(1, 110); 
/* 4098 */         if ((active1 & 0x800000000000L) != 0L)
/* 4099 */           return jjStopAtPos(1, 111); 
/* 4100 */         if ((active1 & 0x1000000000000L) != 0L)
/* 4101 */           return jjStopAtPos(1, 112); 
/*      */         break;
/*      */       case 63:
/* 4104 */         if ((active1 & 0x10000000000L) != 0L)
/* 4105 */           return jjStopAtPos(1, 104); 
/*      */         break;
/*      */       case 97:
/* 4108 */         return jjMoveStringLiteralDfa2_2(active1, 2147483648L, active2, 0L);
/*      */       case 110:
/* 4110 */         if ((active2 & 0x800L) != 0L)
/* 4111 */           return jjStartNfaWithStates_2(1, 139, 104); 
/*      */         break;
/*      */       case 114:
/* 4114 */         return jjMoveStringLiteralDfa2_2(active1, 4294967296L, active2, 0L);
/*      */       case 115:
/* 4116 */         if ((active2 & 0x1000L) != 0L)
/* 4117 */           return jjStartNfaWithStates_2(1, 140, 104); 
/* 4118 */         return jjMoveStringLiteralDfa2_2(active1, 0L, active2, 8192L);
/*      */     } 
/*      */ 
/*      */     
/* 4122 */     return jjStartNfa_2(0, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa2_2(long old1, long active1, long old2, long active2) {
/* 4125 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 4126 */       return jjStartNfa_2(0, 0L, old1, old2);  try {
/* 4127 */       this.curChar = this.input_stream.readChar();
/* 4128 */     } catch (IOException e) {
/* 4129 */       jjStopStringLiteralDfa_2(1, 0L, active1, active2);
/* 4130 */       return 2;
/*      */     } 
/* 4132 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 4135 */         if ((active1 & 0x4000000000L) != 0L)
/* 4136 */           return jjStopAtPos(2, 102); 
/*      */         break;
/*      */       case 46:
/* 4139 */         if ((active1 & 0x1000000000000000L) != 0L)
/* 4140 */           return jjStopAtPos(2, 124); 
/*      */         break;
/*      */       case 105:
/* 4143 */         return jjMoveStringLiteralDfa3_2(active1, 0L, active2, 8192L);
/*      */       case 108:
/* 4145 */         return jjMoveStringLiteralDfa3_2(active1, 2147483648L, active2, 0L);
/*      */       case 117:
/* 4147 */         return jjMoveStringLiteralDfa3_2(active1, 4294967296L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 4151 */     return jjStartNfa_2(1, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa3_2(long old1, long active1, long old2, long active2) {
/* 4154 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 4155 */       return jjStartNfa_2(1, 0L, old1, old2);  try {
/* 4156 */       this.curChar = this.input_stream.readChar();
/* 4157 */     } catch (IOException e) {
/* 4158 */       jjStopStringLiteralDfa_2(2, 0L, active1, active2);
/* 4159 */       return 3;
/*      */     } 
/* 4161 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 4164 */         if ((active1 & 0x100000000L) != 0L)
/* 4165 */           return jjStartNfaWithStates_2(3, 96, 104); 
/*      */         break;
/*      */       case 110:
/* 4168 */         return jjMoveStringLiteralDfa4_2(active1, 0L, active2, 8192L);
/*      */       case 115:
/* 4170 */         return jjMoveStringLiteralDfa4_2(active1, 2147483648L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 4174 */     return jjStartNfa_2(2, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa4_2(long old1, long active1, long old2, long active2) {
/* 4177 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 4178 */       return jjStartNfa_2(2, 0L, old1, old2);  try {
/* 4179 */       this.curChar = this.input_stream.readChar();
/* 4180 */     } catch (IOException e) {
/* 4181 */       jjStopStringLiteralDfa_2(3, 0L, active1, active2);
/* 4182 */       return 4;
/*      */     } 
/* 4184 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 4187 */         if ((active1 & 0x80000000L) != 0L)
/* 4188 */           return jjStartNfaWithStates_2(4, 95, 104); 
/*      */         break;
/*      */       case 103:
/* 4191 */         if ((active2 & 0x2000L) != 0L) {
/* 4192 */           return jjStartNfaWithStates_2(4, 141, 104);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 4197 */     return jjStartNfa_2(3, 0L, active1, active2);
/*      */   }
/*      */   
/*      */   private int jjStartNfaWithStates_2(int pos, int kind, int state) {
/* 4201 */     this.jjmatchedKind = kind;
/* 4202 */     this.jjmatchedPos = pos; 
/* 4203 */     try { this.curChar = this.input_stream.readChar(); }
/* 4204 */     catch (IOException e) { return pos + 1; }
/* 4205 */      return jjMoveNfa_2(state, pos + 1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_2(int startState, int curPos) {
/* 4209 */     int startsAt = 0;
/* 4210 */     this.jjnewStateCnt = 104;
/* 4211 */     int i = 1;
/* 4212 */     this.jjstateSet[0] = startState;
/* 4213 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 4216 */       if (++this.jjround == Integer.MAX_VALUE)
/* 4217 */         ReInitRounds(); 
/* 4218 */       if (this.curChar < 64) {
/*      */         
/* 4220 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 4223 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 53:
/* 4226 */               if (this.curChar == 33) {
/*      */                 
/* 4228 */                 if (kind > 101)
/* 4229 */                   kind = 101;  break;
/*      */               } 
/* 4231 */               if (this.curChar == 60)
/*      */               {
/* 4233 */                 if (kind > 101)
/* 4234 */                   kind = 101; 
/*      */               }
/*      */               break;
/*      */             case 1:
/* 4238 */               if ((0x3FF000000000000L & l) != 0L) {
/*      */                 
/* 4240 */                 if (kind > 97)
/* 4241 */                   kind = 97; 
/* 4242 */                 jjCheckNAddStates(343, 345);
/*      */               }
/* 4244 */               else if ((0x100002600L & l) != 0L) {
/*      */                 
/* 4246 */                 if (kind > 85)
/* 4247 */                   kind = 85; 
/* 4248 */                 jjCheckNAdd(0);
/*      */               }
/* 4250 */               else if (this.curChar == 38) {
/* 4251 */                 jjAddStates(346, 351);
/* 4252 */               } else if (this.curChar == 46) {
/* 4253 */                 jjAddStates(352, 353);
/* 4254 */               } else if (this.curChar == 45) {
/* 4255 */                 jjAddStates(354, 355);
/* 4256 */               } else if (this.curChar == 47) {
/* 4257 */                 jjAddStates(356, 357);
/* 4258 */               } else if (this.curChar == 35) {
/* 4259 */                 jjCheckNAdd(38);
/* 4260 */               } else if (this.curChar == 36) {
/* 4261 */                 jjCheckNAdd(38);
/* 4262 */               } else if (this.curChar == 60) {
/* 4263 */                 jjCheckNAdd(27);
/* 4264 */               } else if (this.curChar == 39) {
/* 4265 */                 jjCheckNAddStates(358, 360);
/* 4266 */               } else if (this.curChar == 34) {
/* 4267 */                 jjCheckNAddStates(361, 363);
/* 4268 */               }  if (this.curChar == 36) {
/*      */                 
/* 4270 */                 if (kind > 142)
/* 4271 */                   kind = 142; 
/* 4272 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 4274 */               else if (this.curChar == 38) {
/*      */                 
/* 4276 */                 if (kind > 127) {
/* 4277 */                   kind = 127;
/*      */                 }
/* 4279 */               } else if (this.curChar == 60) {
/*      */                 
/* 4281 */                 if (kind > 115)
/* 4282 */                   kind = 115; 
/*      */               } 
/* 4284 */               if (this.curChar == 60)
/* 4285 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 54:
/* 4288 */               if (this.curChar == 46)
/* 4289 */                 this.jjstateSet[this.jjnewStateCnt++] = 55; 
/* 4290 */               if (this.curChar == 46)
/* 4291 */                 this.jjstateSet[this.jjnewStateCnt++] = 53; 
/*      */               break;
/*      */             case 47:
/* 4294 */               if (this.curChar == 38) {
/* 4295 */                 this.jjstateSet[this.jjnewStateCnt++] = 50; break;
/* 4296 */               }  if (this.curChar == 62)
/*      */               {
/* 4298 */                 if (kind > 119)
/* 4299 */                   kind = 119; 
/*      */               }
/*      */               break;
/*      */             case 2:
/* 4303 */               if ((0xA00000000L & l) != 0L) {
/* 4304 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; break;
/* 4305 */               }  if (this.curChar == 61)
/*      */               {
/* 4307 */                 if (kind > 143)
/* 4308 */                   kind = 143; 
/*      */               }
/*      */               break;
/*      */             case 44:
/* 4312 */               if (this.curChar == 62 && kind > 149)
/* 4313 */                 kind = 149; 
/*      */               break;
/*      */             case 34:
/*      */             case 104:
/* 4317 */               if ((0x3FF001000000000L & l) == 0L)
/*      */                 break; 
/* 4319 */               if (kind > 142)
/* 4320 */                 kind = 142; 
/* 4321 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 0:
/* 4324 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 4326 */               if (kind > 85)
/* 4327 */                 kind = 85; 
/* 4328 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 3:
/* 4331 */               if (this.curChar == 45 && kind > 86)
/* 4332 */                 kind = 86; 
/*      */               break;
/*      */             case 4:
/* 4335 */               if (this.curChar == 45)
/* 4336 */                 this.jjstateSet[this.jjnewStateCnt++] = 3; 
/*      */               break;
/*      */             case 5:
/* 4339 */               if (this.curChar == 34)
/* 4340 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 6:
/* 4343 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 4344 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 9:
/* 4347 */               if ((0x3FF000000000000L & l) != 0L)
/* 4348 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 10:
/* 4351 */               if (this.curChar == 34 && kind > 93)
/* 4352 */                 kind = 93; 
/*      */               break;
/*      */             case 11:
/* 4355 */               if ((0x2000008400000000L & l) != 0L)
/* 4356 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 12:
/* 4359 */               if (this.curChar == 39)
/* 4360 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 13:
/* 4363 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 4364 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 16:
/* 4367 */               if ((0x3FF000000000000L & l) != 0L)
/* 4368 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 17:
/* 4371 */               if (this.curChar == 39 && kind > 93)
/* 4372 */                 kind = 93; 
/*      */               break;
/*      */             case 18:
/* 4375 */               if ((0x2000008400000000L & l) != 0L)
/* 4376 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 20:
/* 4379 */               if (this.curChar == 34)
/* 4380 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 21:
/* 4383 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 4384 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 22:
/* 4387 */               if (this.curChar == 34 && kind > 94)
/* 4388 */                 kind = 94; 
/*      */               break;
/*      */             case 23:
/* 4391 */               if (this.curChar == 39)
/* 4392 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 24:
/* 4395 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 4396 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 25:
/* 4399 */               if (this.curChar == 39 && kind > 94)
/* 4400 */                 kind = 94; 
/*      */               break;
/*      */             case 26:
/* 4403 */               if (this.curChar == 60 && kind > 115)
/* 4404 */                 kind = 115; 
/*      */               break;
/*      */             case 27:
/* 4407 */               if (this.curChar == 61 && kind > 116)
/* 4408 */                 kind = 116; 
/*      */               break;
/*      */             case 28:
/* 4411 */               if (this.curChar == 60)
/* 4412 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 29:
/*      */             case 92:
/* 4416 */               if (this.curChar == 38 && kind > 127)
/* 4417 */                 kind = 127; 
/*      */               break;
/*      */             case 33:
/* 4420 */               if (this.curChar != 36)
/*      */                 break; 
/* 4422 */               if (kind > 142)
/* 4423 */                 kind = 142; 
/* 4424 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 36:
/* 4427 */               if ((0x400600800000000L & l) == 0L)
/*      */                 break; 
/* 4429 */               if (kind > 142)
/* 4430 */                 kind = 142; 
/* 4431 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 39:
/* 4434 */               if (this.curChar == 36)
/* 4435 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 40:
/* 4438 */               if (this.curChar == 35)
/* 4439 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 41:
/* 4442 */               if (this.curChar == 61 && kind > 143)
/* 4443 */                 kind = 143; 
/*      */               break;
/*      */             case 43:
/* 4446 */               if (this.curChar == 47)
/* 4447 */                 jjAddStates(356, 357); 
/*      */               break;
/*      */             case 46:
/* 4450 */               if (this.curChar == 45)
/* 4451 */                 jjAddStates(354, 355); 
/*      */               break;
/*      */             case 48:
/* 4454 */               if (this.curChar == 59 && kind > 119)
/* 4455 */                 kind = 119; 
/*      */               break;
/*      */             case 51:
/* 4458 */               if (this.curChar == 38)
/* 4459 */                 this.jjstateSet[this.jjnewStateCnt++] = 50; 
/*      */               break;
/*      */             case 52:
/* 4462 */               if (this.curChar == 46)
/* 4463 */                 jjAddStates(352, 353); 
/*      */               break;
/*      */             case 55:
/* 4466 */               if (this.curChar == 33 && kind > 101)
/* 4467 */                 kind = 101; 
/*      */               break;
/*      */             case 56:
/* 4470 */               if (this.curChar == 46)
/* 4471 */                 this.jjstateSet[this.jjnewStateCnt++] = 55; 
/*      */               break;
/*      */             case 57:
/* 4474 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 4476 */               if (kind > 97)
/* 4477 */                 kind = 97; 
/* 4478 */               jjCheckNAddStates(343, 345);
/*      */               break;
/*      */             case 58:
/* 4481 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 4483 */               if (kind > 97)
/* 4484 */                 kind = 97; 
/* 4485 */               jjCheckNAdd(58);
/*      */               break;
/*      */             case 59:
/* 4488 */               if ((0x3FF000000000000L & l) != 0L)
/* 4489 */                 jjCheckNAddTwoStates(59, 60); 
/*      */               break;
/*      */             case 60:
/* 4492 */               if (this.curChar == 46)
/* 4493 */                 jjCheckNAdd(61); 
/*      */               break;
/*      */             case 61:
/* 4496 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 4498 */               if (kind > 98)
/* 4499 */                 kind = 98; 
/* 4500 */               jjCheckNAdd(61);
/*      */               break;
/*      */             case 78:
/* 4503 */               if (this.curChar == 38)
/* 4504 */                 jjAddStates(346, 351); 
/*      */               break;
/*      */             case 79:
/* 4507 */               if (this.curChar == 59 && kind > 115)
/* 4508 */                 kind = 115; 
/*      */               break;
/*      */             case 82:
/* 4511 */               if (this.curChar == 59)
/* 4512 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 85:
/* 4515 */               if (this.curChar == 59 && kind > 117)
/* 4516 */                 kind = 117; 
/*      */               break;
/*      */             case 88:
/* 4519 */               if (this.curChar == 61 && kind > 118)
/* 4520 */                 kind = 118; 
/*      */               break;
/*      */             case 89:
/* 4523 */               if (this.curChar == 59)
/* 4524 */                 this.jjstateSet[this.jjnewStateCnt++] = 88; 
/*      */               break;
/*      */             case 93:
/* 4527 */               if (this.curChar == 59 && kind > 127)
/* 4528 */                 kind = 127; 
/*      */               break;
/*      */             case 97:
/* 4531 */               if (this.curChar == 38)
/* 4532 */                 this.jjstateSet[this.jjnewStateCnt++] = 96; 
/*      */               break;
/*      */             case 98:
/* 4535 */               if (this.curChar == 59) {
/* 4536 */                 this.jjstateSet[this.jjnewStateCnt++] = 97;
/*      */               }
/*      */               break;
/*      */           } 
/* 4540 */         } while (i != startsAt);
/*      */       }
/* 4542 */       else if (this.curChar < 128) {
/*      */         
/* 4544 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 4547 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 4550 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 4552 */                 if (kind > 142)
/* 4553 */                   kind = 142; 
/* 4554 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 4556 */               else if (this.curChar == 92) {
/* 4557 */                 jjAddStates(364, 368);
/* 4558 */               } else if (this.curChar == 91) {
/* 4559 */                 this.jjstateSet[this.jjnewStateCnt++] = 41;
/* 4560 */               } else if (this.curChar == 124) {
/* 4561 */                 this.jjstateSet[this.jjnewStateCnt++] = 31;
/* 4562 */               }  if (this.curChar == 103) {
/* 4563 */                 jjCheckNAddTwoStates(70, 103); break;
/* 4564 */               }  if (this.curChar == 108) {
/* 4565 */                 jjCheckNAddTwoStates(63, 65); break;
/* 4566 */               }  if (this.curChar == 92) {
/* 4567 */                 jjCheckNAdd(36); break;
/* 4568 */               }  if (this.curChar == 124) {
/*      */                 
/* 4570 */                 if (kind > 128)
/* 4571 */                   kind = 128;  break;
/*      */               } 
/* 4573 */               if (this.curChar == 114) {
/* 4574 */                 jjAddStates(369, 370); break;
/* 4575 */               }  if (this.curChar == 91)
/* 4576 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 44:
/* 4579 */               if (this.curChar == 93 && kind > 149)
/* 4580 */                 kind = 149; 
/*      */               break;
/*      */             case 104:
/* 4583 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 4585 */                 if (kind > 142)
/* 4586 */                   kind = 142; 
/* 4587 */                 jjCheckNAddTwoStates(34, 35); break;
/*      */               } 
/* 4589 */               if (this.curChar == 92)
/* 4590 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 6:
/* 4593 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 4594 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 7:
/* 4597 */               if (this.curChar == 92)
/* 4598 */                 jjAddStates(371, 372); 
/*      */               break;
/*      */             case 8:
/* 4601 */               if (this.curChar == 120)
/* 4602 */                 this.jjstateSet[this.jjnewStateCnt++] = 9; 
/*      */               break;
/*      */             case 9:
/* 4605 */               if ((0x7E0000007EL & l) != 0L)
/* 4606 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 11:
/* 4609 */               if ((0x81450C610000000L & l) != 0L)
/* 4610 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 4613 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 4614 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 14:
/* 4617 */               if (this.curChar == 92)
/* 4618 */                 jjAddStates(373, 374); 
/*      */               break;
/*      */             case 15:
/* 4621 */               if (this.curChar == 120)
/* 4622 */                 this.jjstateSet[this.jjnewStateCnt++] = 16; 
/*      */               break;
/*      */             case 16:
/* 4625 */               if ((0x7E0000007EL & l) != 0L)
/* 4626 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 18:
/* 4629 */               if ((0x81450C610000000L & l) != 0L)
/* 4630 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 19:
/* 4633 */               if (this.curChar == 114)
/* 4634 */                 jjAddStates(369, 370); 
/*      */               break;
/*      */             case 21:
/* 4637 */               jjAddStates(375, 376);
/*      */               break;
/*      */             case 24:
/* 4640 */               jjAddStates(377, 378);
/*      */               break;
/*      */             case 30:
/*      */             case 31:
/* 4644 */               if (this.curChar == 124 && kind > 128)
/* 4645 */                 kind = 128; 
/*      */               break;
/*      */             case 32:
/* 4648 */               if (this.curChar == 124)
/* 4649 */                 this.jjstateSet[this.jjnewStateCnt++] = 31; 
/*      */               break;
/*      */             case 33:
/* 4652 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 4654 */               if (kind > 142)
/* 4655 */                 kind = 142; 
/* 4656 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/* 4659 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 4661 */               if (kind > 142)
/* 4662 */                 kind = 142; 
/* 4663 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 35:
/* 4666 */               if (this.curChar == 92)
/* 4667 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 37:
/* 4670 */               if (this.curChar == 92)
/* 4671 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 38:
/* 4674 */               if (this.curChar == 123 && kind > 143)
/* 4675 */                 kind = 143; 
/*      */               break;
/*      */             case 42:
/* 4678 */               if (this.curChar == 91)
/* 4679 */                 this.jjstateSet[this.jjnewStateCnt++] = 41; 
/*      */               break;
/*      */             case 49:
/* 4682 */               if (this.curChar == 116)
/* 4683 */                 this.jjstateSet[this.jjnewStateCnt++] = 48; 
/*      */               break;
/*      */             case 50:
/* 4686 */               if (this.curChar == 103)
/* 4687 */                 this.jjstateSet[this.jjnewStateCnt++] = 49; 
/*      */               break;
/*      */             case 62:
/* 4690 */               if (this.curChar == 108)
/* 4691 */                 jjCheckNAddTwoStates(63, 65); 
/*      */               break;
/*      */             case 63:
/* 4694 */               if (this.curChar == 116 && kind > 115)
/* 4695 */                 kind = 115; 
/*      */               break;
/*      */             case 64:
/* 4698 */               if (this.curChar == 101 && kind > 116)
/* 4699 */                 kind = 116; 
/*      */               break;
/*      */             case 65:
/*      */             case 68:
/* 4703 */               if (this.curChar == 116)
/* 4704 */                 jjCheckNAdd(64); 
/*      */               break;
/*      */             case 66:
/* 4707 */               if (this.curChar == 92)
/* 4708 */                 jjAddStates(364, 368); 
/*      */               break;
/*      */             case 67:
/* 4711 */               if (this.curChar == 108)
/* 4712 */                 jjCheckNAdd(63); 
/*      */               break;
/*      */             case 69:
/* 4715 */               if (this.curChar == 108)
/* 4716 */                 this.jjstateSet[this.jjnewStateCnt++] = 68; 
/*      */               break;
/*      */             case 70:
/* 4719 */               if (this.curChar == 116 && kind > 117)
/* 4720 */                 kind = 117; 
/*      */               break;
/*      */             case 71:
/* 4723 */               if (this.curChar == 103)
/* 4724 */                 jjCheckNAdd(70); 
/*      */               break;
/*      */             case 72:
/* 4727 */               if (this.curChar == 101 && kind > 118)
/* 4728 */                 kind = 118; 
/*      */               break;
/*      */             case 73:
/*      */             case 103:
/* 4732 */               if (this.curChar == 116)
/* 4733 */                 jjCheckNAdd(72); 
/*      */               break;
/*      */             case 74:
/* 4736 */               if (this.curChar == 103)
/* 4737 */                 this.jjstateSet[this.jjnewStateCnt++] = 73; 
/*      */               break;
/*      */             case 75:
/* 4740 */               if (this.curChar == 100 && kind > 127)
/* 4741 */                 kind = 127; 
/*      */               break;
/*      */             case 76:
/* 4744 */               if (this.curChar == 110)
/* 4745 */                 this.jjstateSet[this.jjnewStateCnt++] = 75; 
/*      */               break;
/*      */             case 77:
/* 4748 */               if (this.curChar == 97)
/* 4749 */                 this.jjstateSet[this.jjnewStateCnt++] = 76; 
/*      */               break;
/*      */             case 80:
/* 4752 */               if (this.curChar == 116)
/* 4753 */                 this.jjstateSet[this.jjnewStateCnt++] = 79; 
/*      */               break;
/*      */             case 81:
/* 4756 */               if (this.curChar == 108)
/* 4757 */                 this.jjstateSet[this.jjnewStateCnt++] = 80; 
/*      */               break;
/*      */             case 83:
/* 4760 */               if (this.curChar == 116)
/* 4761 */                 this.jjstateSet[this.jjnewStateCnt++] = 82; 
/*      */               break;
/*      */             case 84:
/* 4764 */               if (this.curChar == 108)
/* 4765 */                 this.jjstateSet[this.jjnewStateCnt++] = 83; 
/*      */               break;
/*      */             case 86:
/* 4768 */               if (this.curChar == 116)
/* 4769 */                 this.jjstateSet[this.jjnewStateCnt++] = 85; 
/*      */               break;
/*      */             case 87:
/* 4772 */               if (this.curChar == 103)
/* 4773 */                 this.jjstateSet[this.jjnewStateCnt++] = 86; 
/*      */               break;
/*      */             case 90:
/* 4776 */               if (this.curChar == 116)
/* 4777 */                 this.jjstateSet[this.jjnewStateCnt++] = 89; 
/*      */               break;
/*      */             case 91:
/* 4780 */               if (this.curChar == 103)
/* 4781 */                 this.jjstateSet[this.jjnewStateCnt++] = 90; 
/*      */               break;
/*      */             case 94:
/* 4784 */               if (this.curChar == 112)
/* 4785 */                 this.jjstateSet[this.jjnewStateCnt++] = 93; 
/*      */               break;
/*      */             case 95:
/* 4788 */               if (this.curChar == 109)
/* 4789 */                 this.jjstateSet[this.jjnewStateCnt++] = 94; 
/*      */               break;
/*      */             case 96:
/* 4792 */               if (this.curChar == 97)
/* 4793 */                 this.jjstateSet[this.jjnewStateCnt++] = 95; 
/*      */               break;
/*      */             case 99:
/* 4796 */               if (this.curChar == 112)
/* 4797 */                 this.jjstateSet[this.jjnewStateCnt++] = 98; 
/*      */               break;
/*      */             case 100:
/* 4800 */               if (this.curChar == 109)
/* 4801 */                 this.jjstateSet[this.jjnewStateCnt++] = 99; 
/*      */               break;
/*      */             case 101:
/* 4804 */               if (this.curChar == 97)
/* 4805 */                 this.jjstateSet[this.jjnewStateCnt++] = 100; 
/*      */               break;
/*      */             case 102:
/* 4808 */               if (this.curChar == 103) {
/* 4809 */                 jjCheckNAddTwoStates(70, 103);
/*      */               }
/*      */               break;
/*      */           } 
/* 4813 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 4817 */         int hiByte = this.curChar >> 8;
/* 4818 */         int i1 = hiByte >> 6;
/* 4819 */         long l1 = 1L << (hiByte & 0x3F);
/* 4820 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 4821 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 4824 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 4827 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 4829 */               if (kind > 142)
/* 4830 */                 kind = 142; 
/* 4831 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/*      */             case 104:
/* 4835 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 4837 */               if (kind > 142)
/* 4838 */                 kind = 142; 
/* 4839 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 6:
/* 4842 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 4843 */                 jjAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 4846 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 4847 */                 jjAddStates(358, 360); 
/*      */               break;
/*      */             case 21:
/* 4850 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 4851 */                 jjAddStates(375, 376); 
/*      */               break;
/*      */             case 24:
/* 4854 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 4855 */                 jjAddStates(377, 378);  break;
/*      */             default:
/* 4857 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 4859 */         } while (i != startsAt);
/*      */       } 
/* 4861 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 4863 */         this.jjmatchedKind = kind;
/* 4864 */         this.jjmatchedPos = curPos;
/* 4865 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 4867 */       curPos++;
/* 4868 */       if ((i = this.jjnewStateCnt) == (startsAt = 104 - (this.jjnewStateCnt = startsAt)))
/* 4869 */         return curPos;  
/* 4870 */       try { this.curChar = this.input_stream.readChar(); }
/* 4871 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_3(int pos, long active0, long active1, long active2) {
/* 4875 */     switch (pos) {
/*      */       
/*      */       case 0:
/* 4878 */         if ((active2 & 0x20L) != 0L)
/* 4879 */           return 2; 
/* 4880 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x3800L) != 0L) {
/*      */           
/* 4882 */           this.jjmatchedKind = 142;
/* 4883 */           return 101;
/*      */         } 
/* 4885 */         if ((active1 & 0x1000005800000000L) != 0L)
/* 4886 */           return 51; 
/* 4887 */         if ((active1 & 0x204200000000000L) != 0L)
/* 4888 */           return 44; 
/* 4889 */         return -1;
/*      */       case 1:
/* 4891 */         if ((active2 & 0x1800L) != 0L)
/* 4892 */           return 101; 
/* 4893 */         if ((active1 & 0x1000005000000000L) != 0L)
/* 4894 */           return 50; 
/* 4895 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 4897 */           if (this.jjmatchedPos != 1) {
/*      */             
/* 4899 */             this.jjmatchedKind = 142;
/* 4900 */             this.jjmatchedPos = 1;
/*      */           } 
/* 4902 */           return 101;
/*      */         } 
/* 4904 */         return -1;
/*      */       case 2:
/* 4906 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 4908 */           this.jjmatchedKind = 142;
/* 4909 */           this.jjmatchedPos = 2;
/* 4910 */           return 101;
/*      */         } 
/* 4912 */         return -1;
/*      */       case 3:
/* 4914 */         if ((active1 & 0x100000000L) != 0L)
/* 4915 */           return 101; 
/* 4916 */         if ((active1 & 0x80000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 4918 */           this.jjmatchedKind = 142;
/* 4919 */           this.jjmatchedPos = 3;
/* 4920 */           return 101;
/*      */         } 
/* 4922 */         return -1;
/*      */     } 
/* 4924 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_3(int pos, long active0, long active1, long active2) {
/* 4928 */     return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0, active1, active2), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_3() {
/* 4931 */     switch (this.curChar) {
/*      */       
/*      */       case 33:
/* 4934 */         this.jjmatchedKind = 129;
/* 4935 */         return jjMoveStringLiteralDfa1_3(8796093022208L, 0L);
/*      */       case 37:
/* 4937 */         this.jjmatchedKind = 126;
/* 4938 */         return jjMoveStringLiteralDfa1_3(281474976710656L, 0L);
/*      */       case 40:
/* 4940 */         return jjStopAtPos(0, 135);
/*      */       case 41:
/* 4942 */         return jjStopAtPos(0, 136);
/*      */       case 42:
/* 4944 */         this.jjmatchedKind = 122;
/* 4945 */         return jjMoveStringLiteralDfa1_3(576531121047601152L, 0L);
/*      */       case 43:
/* 4947 */         this.jjmatchedKind = 120;
/* 4948 */         return jjMoveStringLiteralDfa1_3(580542139465728L, 0L);
/*      */       case 44:
/* 4950 */         return jjStopAtPos(0, 130);
/*      */       case 45:
/* 4952 */         this.jjmatchedKind = 121;
/* 4953 */         return jjMoveStringLiteralDfa1_3(1161084278931456L, 0L);
/*      */       case 46:
/* 4955 */         this.jjmatchedKind = 99;
/* 4956 */         return jjMoveStringLiteralDfa1_3(1152921848204230656L, 0L);
/*      */       case 47:
/* 4958 */         this.jjmatchedKind = 125;
/* 4959 */         return jjMoveStringLiteralDfa1_3(140737488355328L, 0L);
/*      */       case 58:
/* 4961 */         return jjStopAtPos(0, 132);
/*      */       case 59:
/* 4963 */         return jjStopAtPos(0, 131);
/*      */       case 61:
/* 4965 */         this.jjmatchedKind = 105;
/* 4966 */         return jjMoveStringLiteralDfa1_3(4398046511104L, 0L);
/*      */       case 62:
/* 4968 */         this.jjmatchedKind = 150;
/* 4969 */         return jjMoveStringLiteralDfa1_3(0L, 8388608L);
/*      */       case 63:
/* 4971 */         this.jjmatchedKind = 103;
/* 4972 */         return jjMoveStringLiteralDfa1_3(1099511627776L, 0L);
/*      */       case 91:
/* 4974 */         return jjStartNfaWithStates_3(0, 133, 2);
/*      */       case 93:
/* 4976 */         return jjStopAtPos(0, 134);
/*      */       case 97:
/* 4978 */         return jjMoveStringLiteralDfa1_3(0L, 4096L);
/*      */       case 102:
/* 4980 */         return jjMoveStringLiteralDfa1_3(2147483648L, 0L);
/*      */       case 105:
/* 4982 */         return jjMoveStringLiteralDfa1_3(0L, 2048L);
/*      */       case 116:
/* 4984 */         return jjMoveStringLiteralDfa1_3(4294967296L, 0L);
/*      */       case 117:
/* 4986 */         return jjMoveStringLiteralDfa1_3(0L, 8192L);
/*      */       case 123:
/* 4988 */         return jjStopAtPos(0, 137);
/*      */       case 125:
/* 4990 */         return jjStopAtPos(0, 138);
/*      */     } 
/* 4992 */     return jjMoveNfa_3(1, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_3(long active1, long active2) {
/*      */     try {
/* 4996 */       this.curChar = this.input_stream.readChar();
/* 4997 */     } catch (IOException e) {
/* 4998 */       jjStopStringLiteralDfa_3(0, 0L, active1, active2);
/* 4999 */       return 1;
/*      */     } 
/* 5001 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 5004 */         if ((active1 & 0x800000000000000L) != 0L)
/* 5005 */           return jjStopAtPos(1, 123); 
/*      */         break;
/*      */       case 43:
/* 5008 */         if ((active1 & 0x2000000000000L) != 0L)
/* 5009 */           return jjStopAtPos(1, 113); 
/*      */         break;
/*      */       case 45:
/* 5012 */         if ((active1 & 0x4000000000000L) != 0L)
/* 5013 */           return jjStopAtPos(1, 114); 
/*      */         break;
/*      */       case 46:
/* 5016 */         if ((active1 & 0x1000000000L) != 0L) {
/*      */           
/* 5018 */           this.jjmatchedKind = 100;
/* 5019 */           this.jjmatchedPos = 1;
/*      */         } 
/* 5021 */         return jjMoveStringLiteralDfa2_3(active1, 1152921779484753920L, active2, 0L);
/*      */       case 61:
/* 5023 */         if ((active1 & 0x40000000000L) != 0L)
/* 5024 */           return jjStopAtPos(1, 106); 
/* 5025 */         if ((active1 & 0x80000000000L) != 0L)
/* 5026 */           return jjStopAtPos(1, 107); 
/* 5027 */         if ((active1 & 0x100000000000L) != 0L)
/* 5028 */           return jjStopAtPos(1, 108); 
/* 5029 */         if ((active1 & 0x200000000000L) != 0L)
/* 5030 */           return jjStopAtPos(1, 109); 
/* 5031 */         if ((active1 & 0x400000000000L) != 0L)
/* 5032 */           return jjStopAtPos(1, 110); 
/* 5033 */         if ((active1 & 0x800000000000L) != 0L)
/* 5034 */           return jjStopAtPos(1, 111); 
/* 5035 */         if ((active1 & 0x1000000000000L) != 0L)
/* 5036 */           return jjStopAtPos(1, 112); 
/* 5037 */         if ((active2 & 0x800000L) != 0L)
/* 5038 */           return jjStopAtPos(1, 151); 
/*      */         break;
/*      */       case 63:
/* 5041 */         if ((active1 & 0x10000000000L) != 0L)
/* 5042 */           return jjStopAtPos(1, 104); 
/*      */         break;
/*      */       case 97:
/* 5045 */         return jjMoveStringLiteralDfa2_3(active1, 2147483648L, active2, 0L);
/*      */       case 110:
/* 5047 */         if ((active2 & 0x800L) != 0L)
/* 5048 */           return jjStartNfaWithStates_3(1, 139, 101); 
/*      */         break;
/*      */       case 114:
/* 5051 */         return jjMoveStringLiteralDfa2_3(active1, 4294967296L, active2, 0L);
/*      */       case 115:
/* 5053 */         if ((active2 & 0x1000L) != 0L)
/* 5054 */           return jjStartNfaWithStates_3(1, 140, 101); 
/* 5055 */         return jjMoveStringLiteralDfa2_3(active1, 0L, active2, 8192L);
/*      */     } 
/*      */ 
/*      */     
/* 5059 */     return jjStartNfa_3(0, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa2_3(long old1, long active1, long old2, long active2) {
/* 5062 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 5063 */       return jjStartNfa_3(0, 0L, old1, old2);  try {
/* 5064 */       this.curChar = this.input_stream.readChar();
/* 5065 */     } catch (IOException e) {
/* 5066 */       jjStopStringLiteralDfa_3(1, 0L, active1, active2);
/* 5067 */       return 2;
/*      */     } 
/* 5069 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 5072 */         if ((active1 & 0x4000000000L) != 0L)
/* 5073 */           return jjStopAtPos(2, 102); 
/*      */         break;
/*      */       case 46:
/* 5076 */         if ((active1 & 0x1000000000000000L) != 0L)
/* 5077 */           return jjStopAtPos(2, 124); 
/*      */         break;
/*      */       case 105:
/* 5080 */         return jjMoveStringLiteralDfa3_3(active1, 0L, active2, 8192L);
/*      */       case 108:
/* 5082 */         return jjMoveStringLiteralDfa3_3(active1, 2147483648L, active2, 0L);
/*      */       case 117:
/* 5084 */         return jjMoveStringLiteralDfa3_3(active1, 4294967296L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 5088 */     return jjStartNfa_3(1, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa3_3(long old1, long active1, long old2, long active2) {
/* 5091 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 5092 */       return jjStartNfa_3(1, 0L, old1, old2);  try {
/* 5093 */       this.curChar = this.input_stream.readChar();
/* 5094 */     } catch (IOException e) {
/* 5095 */       jjStopStringLiteralDfa_3(2, 0L, active1, active2);
/* 5096 */       return 3;
/*      */     } 
/* 5098 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 5101 */         if ((active1 & 0x100000000L) != 0L)
/* 5102 */           return jjStartNfaWithStates_3(3, 96, 101); 
/*      */         break;
/*      */       case 110:
/* 5105 */         return jjMoveStringLiteralDfa4_3(active1, 0L, active2, 8192L);
/*      */       case 115:
/* 5107 */         return jjMoveStringLiteralDfa4_3(active1, 2147483648L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 5111 */     return jjStartNfa_3(2, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa4_3(long old1, long active1, long old2, long active2) {
/* 5114 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 5115 */       return jjStartNfa_3(2, 0L, old1, old2);  try {
/* 5116 */       this.curChar = this.input_stream.readChar();
/* 5117 */     } catch (IOException e) {
/* 5118 */       jjStopStringLiteralDfa_3(3, 0L, active1, active2);
/* 5119 */       return 4;
/*      */     } 
/* 5121 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 5124 */         if ((active1 & 0x80000000L) != 0L)
/* 5125 */           return jjStartNfaWithStates_3(4, 95, 101); 
/*      */         break;
/*      */       case 103:
/* 5128 */         if ((active2 & 0x2000L) != 0L) {
/* 5129 */           return jjStartNfaWithStates_3(4, 141, 101);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 5134 */     return jjStartNfa_3(3, 0L, active1, active2);
/*      */   }
/*      */   
/*      */   private int jjStartNfaWithStates_3(int pos, int kind, int state) {
/* 5138 */     this.jjmatchedKind = kind;
/* 5139 */     this.jjmatchedPos = pos; 
/* 5140 */     try { this.curChar = this.input_stream.readChar(); }
/* 5141 */     catch (IOException e) { return pos + 1; }
/* 5142 */      return jjMoveNfa_3(state, pos + 1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_3(int startState, int curPos) {
/* 5146 */     int startsAt = 0;
/* 5147 */     this.jjnewStateCnt = 101;
/* 5148 */     int i = 1;
/* 5149 */     this.jjstateSet[0] = startState;
/* 5150 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 5153 */       if (++this.jjround == Integer.MAX_VALUE)
/* 5154 */         ReInitRounds(); 
/* 5155 */       if (this.curChar < 64) {
/*      */         
/* 5157 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 5160 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 5163 */               if ((0x3FF000000000000L & l) != 0L) {
/*      */                 
/* 5165 */                 if (kind > 97)
/* 5166 */                   kind = 97; 
/* 5167 */                 jjCheckNAddStates(379, 381);
/*      */               }
/* 5169 */               else if ((0x100002600L & l) != 0L) {
/*      */                 
/* 5171 */                 if (kind > 85)
/* 5172 */                   kind = 85; 
/* 5173 */                 jjCheckNAdd(0);
/*      */               }
/* 5175 */               else if (this.curChar == 38) {
/* 5176 */                 jjAddStates(382, 387);
/* 5177 */               } else if (this.curChar == 46) {
/* 5178 */                 jjAddStates(388, 389);
/* 5179 */               } else if (this.curChar == 45) {
/* 5180 */                 jjAddStates(390, 391);
/* 5181 */               } else if (this.curChar == 35) {
/* 5182 */                 jjCheckNAdd(38);
/* 5183 */               } else if (this.curChar == 36) {
/* 5184 */                 jjCheckNAdd(38);
/* 5185 */               } else if (this.curChar == 60) {
/* 5186 */                 jjCheckNAdd(27);
/* 5187 */               } else if (this.curChar == 39) {
/* 5188 */                 jjCheckNAddStates(358, 360);
/* 5189 */               } else if (this.curChar == 34) {
/* 5190 */                 jjCheckNAddStates(361, 363);
/* 5191 */               }  if (this.curChar == 36) {
/*      */                 
/* 5193 */                 if (kind > 142)
/* 5194 */                   kind = 142; 
/* 5195 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 5197 */               else if (this.curChar == 38) {
/*      */                 
/* 5199 */                 if (kind > 127) {
/* 5200 */                   kind = 127;
/*      */                 }
/* 5202 */               } else if (this.curChar == 60) {
/*      */                 
/* 5204 */                 if (kind > 115)
/* 5205 */                   kind = 115; 
/*      */               } 
/* 5207 */               if (this.curChar == 60)
/* 5208 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 51:
/* 5211 */               if (this.curChar == 46)
/* 5212 */                 this.jjstateSet[this.jjnewStateCnt++] = 52; 
/* 5213 */               if (this.curChar == 46)
/* 5214 */                 this.jjstateSet[this.jjnewStateCnt++] = 50; 
/*      */               break;
/*      */             case 44:
/* 5217 */               if (this.curChar == 38) {
/* 5218 */                 this.jjstateSet[this.jjnewStateCnt++] = 47; break;
/* 5219 */               }  if (this.curChar == 62)
/*      */               {
/* 5221 */                 if (kind > 119)
/* 5222 */                   kind = 119; 
/*      */               }
/*      */               break;
/*      */             case 50:
/* 5226 */               if (this.curChar == 33) {
/*      */                 
/* 5228 */                 if (kind > 101)
/* 5229 */                   kind = 101;  break;
/*      */               } 
/* 5231 */               if (this.curChar == 60)
/*      */               {
/* 5233 */                 if (kind > 101)
/* 5234 */                   kind = 101; 
/*      */               }
/*      */               break;
/*      */             case 2:
/* 5238 */               if ((0xA00000000L & l) != 0L) {
/* 5239 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; break;
/* 5240 */               }  if (this.curChar == 61)
/*      */               {
/* 5242 */                 if (kind > 143)
/* 5243 */                   kind = 143; 
/*      */               }
/*      */               break;
/*      */             case 34:
/*      */             case 101:
/* 5248 */               if ((0x3FF001000000000L & l) == 0L)
/*      */                 break; 
/* 5250 */               if (kind > 142)
/* 5251 */                 kind = 142; 
/* 5252 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 0:
/* 5255 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 5257 */               if (kind > 85)
/* 5258 */                 kind = 85; 
/* 5259 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 3:
/* 5262 */               if (this.curChar == 45 && kind > 86)
/* 5263 */                 kind = 86; 
/*      */               break;
/*      */             case 4:
/* 5266 */               if (this.curChar == 45)
/* 5267 */                 this.jjstateSet[this.jjnewStateCnt++] = 3; 
/*      */               break;
/*      */             case 5:
/* 5270 */               if (this.curChar == 34)
/* 5271 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 6:
/* 5274 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 5275 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 9:
/* 5278 */               if ((0x3FF000000000000L & l) != 0L)
/* 5279 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 10:
/* 5282 */               if (this.curChar == 34 && kind > 93)
/* 5283 */                 kind = 93; 
/*      */               break;
/*      */             case 11:
/* 5286 */               if ((0x2000008400000000L & l) != 0L)
/* 5287 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 12:
/* 5290 */               if (this.curChar == 39)
/* 5291 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 13:
/* 5294 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 5295 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 16:
/* 5298 */               if ((0x3FF000000000000L & l) != 0L)
/* 5299 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 17:
/* 5302 */               if (this.curChar == 39 && kind > 93)
/* 5303 */                 kind = 93; 
/*      */               break;
/*      */             case 18:
/* 5306 */               if ((0x2000008400000000L & l) != 0L)
/* 5307 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 20:
/* 5310 */               if (this.curChar == 34)
/* 5311 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 21:
/* 5314 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 5315 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 22:
/* 5318 */               if (this.curChar == 34 && kind > 94)
/* 5319 */                 kind = 94; 
/*      */               break;
/*      */             case 23:
/* 5322 */               if (this.curChar == 39)
/* 5323 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 24:
/* 5326 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 5327 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 25:
/* 5330 */               if (this.curChar == 39 && kind > 94)
/* 5331 */                 kind = 94; 
/*      */               break;
/*      */             case 26:
/* 5334 */               if (this.curChar == 60 && kind > 115)
/* 5335 */                 kind = 115; 
/*      */               break;
/*      */             case 27:
/* 5338 */               if (this.curChar == 61 && kind > 116)
/* 5339 */                 kind = 116; 
/*      */               break;
/*      */             case 28:
/* 5342 */               if (this.curChar == 60)
/* 5343 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 29:
/*      */             case 89:
/* 5347 */               if (this.curChar == 38 && kind > 127)
/* 5348 */                 kind = 127; 
/*      */               break;
/*      */             case 33:
/* 5351 */               if (this.curChar != 36)
/*      */                 break; 
/* 5353 */               if (kind > 142)
/* 5354 */                 kind = 142; 
/* 5355 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 36:
/* 5358 */               if ((0x400600800000000L & l) == 0L)
/*      */                 break; 
/* 5360 */               if (kind > 142)
/* 5361 */                 kind = 142; 
/* 5362 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 39:
/* 5365 */               if (this.curChar == 36)
/* 5366 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 40:
/* 5369 */               if (this.curChar == 35)
/* 5370 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 41:
/* 5373 */               if (this.curChar == 61 && kind > 143)
/* 5374 */                 kind = 143; 
/*      */               break;
/*      */             case 43:
/* 5377 */               if (this.curChar == 45)
/* 5378 */                 jjAddStates(390, 391); 
/*      */               break;
/*      */             case 45:
/* 5381 */               if (this.curChar == 59 && kind > 119)
/* 5382 */                 kind = 119; 
/*      */               break;
/*      */             case 48:
/* 5385 */               if (this.curChar == 38)
/* 5386 */                 this.jjstateSet[this.jjnewStateCnt++] = 47; 
/*      */               break;
/*      */             case 49:
/* 5389 */               if (this.curChar == 46)
/* 5390 */                 jjAddStates(388, 389); 
/*      */               break;
/*      */             case 52:
/* 5393 */               if (this.curChar == 33 && kind > 101)
/* 5394 */                 kind = 101; 
/*      */               break;
/*      */             case 53:
/* 5397 */               if (this.curChar == 46)
/* 5398 */                 this.jjstateSet[this.jjnewStateCnt++] = 52; 
/*      */               break;
/*      */             case 54:
/* 5401 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 5403 */               if (kind > 97)
/* 5404 */                 kind = 97; 
/* 5405 */               jjCheckNAddStates(379, 381);
/*      */               break;
/*      */             case 55:
/* 5408 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 5410 */               if (kind > 97)
/* 5411 */                 kind = 97; 
/* 5412 */               jjCheckNAdd(55);
/*      */               break;
/*      */             case 56:
/* 5415 */               if ((0x3FF000000000000L & l) != 0L)
/* 5416 */                 jjCheckNAddTwoStates(56, 57); 
/*      */               break;
/*      */             case 57:
/* 5419 */               if (this.curChar == 46)
/* 5420 */                 jjCheckNAdd(58); 
/*      */               break;
/*      */             case 58:
/* 5423 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 5425 */               if (kind > 98)
/* 5426 */                 kind = 98; 
/* 5427 */               jjCheckNAdd(58);
/*      */               break;
/*      */             case 75:
/* 5430 */               if (this.curChar == 38)
/* 5431 */                 jjAddStates(382, 387); 
/*      */               break;
/*      */             case 76:
/* 5434 */               if (this.curChar == 59 && kind > 115)
/* 5435 */                 kind = 115; 
/*      */               break;
/*      */             case 79:
/* 5438 */               if (this.curChar == 59)
/* 5439 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 82:
/* 5442 */               if (this.curChar == 59 && kind > 117)
/* 5443 */                 kind = 117; 
/*      */               break;
/*      */             case 85:
/* 5446 */               if (this.curChar == 61 && kind > 118)
/* 5447 */                 kind = 118; 
/*      */               break;
/*      */             case 86:
/* 5450 */               if (this.curChar == 59)
/* 5451 */                 this.jjstateSet[this.jjnewStateCnt++] = 85; 
/*      */               break;
/*      */             case 90:
/* 5454 */               if (this.curChar == 59 && kind > 127)
/* 5455 */                 kind = 127; 
/*      */               break;
/*      */             case 94:
/* 5458 */               if (this.curChar == 38)
/* 5459 */                 this.jjstateSet[this.jjnewStateCnt++] = 93; 
/*      */               break;
/*      */             case 95:
/* 5462 */               if (this.curChar == 59) {
/* 5463 */                 this.jjstateSet[this.jjnewStateCnt++] = 94;
/*      */               }
/*      */               break;
/*      */           } 
/* 5467 */         } while (i != startsAt);
/*      */       }
/* 5469 */       else if (this.curChar < 128) {
/*      */         
/* 5471 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 5474 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 5477 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 5479 */                 if (kind > 142)
/* 5480 */                   kind = 142; 
/* 5481 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 5483 */               else if (this.curChar == 92) {
/* 5484 */                 jjAddStates(392, 396);
/* 5485 */               } else if (this.curChar == 91) {
/* 5486 */                 this.jjstateSet[this.jjnewStateCnt++] = 41;
/* 5487 */               } else if (this.curChar == 124) {
/* 5488 */                 this.jjstateSet[this.jjnewStateCnt++] = 31;
/* 5489 */               }  if (this.curChar == 103) {
/* 5490 */                 jjCheckNAddTwoStates(67, 100); break;
/* 5491 */               }  if (this.curChar == 108) {
/* 5492 */                 jjCheckNAddTwoStates(60, 62); break;
/* 5493 */               }  if (this.curChar == 92) {
/* 5494 */                 jjCheckNAdd(36); break;
/* 5495 */               }  if (this.curChar == 124) {
/*      */                 
/* 5497 */                 if (kind > 128)
/* 5498 */                   kind = 128;  break;
/*      */               } 
/* 5500 */               if (this.curChar == 114) {
/* 5501 */                 jjAddStates(369, 370); break;
/* 5502 */               }  if (this.curChar == 91)
/* 5503 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 101:
/* 5506 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 5508 */                 if (kind > 142)
/* 5509 */                   kind = 142; 
/* 5510 */                 jjCheckNAddTwoStates(34, 35); break;
/*      */               } 
/* 5512 */               if (this.curChar == 92)
/* 5513 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 6:
/* 5516 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 5517 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 7:
/* 5520 */               if (this.curChar == 92)
/* 5521 */                 jjAddStates(371, 372); 
/*      */               break;
/*      */             case 8:
/* 5524 */               if (this.curChar == 120)
/* 5525 */                 this.jjstateSet[this.jjnewStateCnt++] = 9; 
/*      */               break;
/*      */             case 9:
/* 5528 */               if ((0x7E0000007EL & l) != 0L)
/* 5529 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 11:
/* 5532 */               if ((0x81450C610000000L & l) != 0L)
/* 5533 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 5536 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 5537 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 14:
/* 5540 */               if (this.curChar == 92)
/* 5541 */                 jjAddStates(373, 374); 
/*      */               break;
/*      */             case 15:
/* 5544 */               if (this.curChar == 120)
/* 5545 */                 this.jjstateSet[this.jjnewStateCnt++] = 16; 
/*      */               break;
/*      */             case 16:
/* 5548 */               if ((0x7E0000007EL & l) != 0L)
/* 5549 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 18:
/* 5552 */               if ((0x81450C610000000L & l) != 0L)
/* 5553 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 19:
/* 5556 */               if (this.curChar == 114)
/* 5557 */                 jjAddStates(369, 370); 
/*      */               break;
/*      */             case 21:
/* 5560 */               jjAddStates(375, 376);
/*      */               break;
/*      */             case 24:
/* 5563 */               jjAddStates(377, 378);
/*      */               break;
/*      */             case 30:
/*      */             case 31:
/* 5567 */               if (this.curChar == 124 && kind > 128)
/* 5568 */                 kind = 128; 
/*      */               break;
/*      */             case 32:
/* 5571 */               if (this.curChar == 124)
/* 5572 */                 this.jjstateSet[this.jjnewStateCnt++] = 31; 
/*      */               break;
/*      */             case 33:
/* 5575 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 5577 */               if (kind > 142)
/* 5578 */                 kind = 142; 
/* 5579 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/* 5582 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 5584 */               if (kind > 142)
/* 5585 */                 kind = 142; 
/* 5586 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 35:
/* 5589 */               if (this.curChar == 92)
/* 5590 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 37:
/* 5593 */               if (this.curChar == 92)
/* 5594 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 38:
/* 5597 */               if (this.curChar == 123 && kind > 143)
/* 5598 */                 kind = 143; 
/*      */               break;
/*      */             case 42:
/* 5601 */               if (this.curChar == 91)
/* 5602 */                 this.jjstateSet[this.jjnewStateCnt++] = 41; 
/*      */               break;
/*      */             case 46:
/* 5605 */               if (this.curChar == 116)
/* 5606 */                 this.jjstateSet[this.jjnewStateCnt++] = 45; 
/*      */               break;
/*      */             case 47:
/* 5609 */               if (this.curChar == 103)
/* 5610 */                 this.jjstateSet[this.jjnewStateCnt++] = 46; 
/*      */               break;
/*      */             case 59:
/* 5613 */               if (this.curChar == 108)
/* 5614 */                 jjCheckNAddTwoStates(60, 62); 
/*      */               break;
/*      */             case 60:
/* 5617 */               if (this.curChar == 116 && kind > 115)
/* 5618 */                 kind = 115; 
/*      */               break;
/*      */             case 61:
/* 5621 */               if (this.curChar == 101 && kind > 116)
/* 5622 */                 kind = 116; 
/*      */               break;
/*      */             case 62:
/*      */             case 65:
/* 5626 */               if (this.curChar == 116)
/* 5627 */                 jjCheckNAdd(61); 
/*      */               break;
/*      */             case 63:
/* 5630 */               if (this.curChar == 92)
/* 5631 */                 jjAddStates(392, 396); 
/*      */               break;
/*      */             case 64:
/* 5634 */               if (this.curChar == 108)
/* 5635 */                 jjCheckNAdd(60); 
/*      */               break;
/*      */             case 66:
/* 5638 */               if (this.curChar == 108)
/* 5639 */                 this.jjstateSet[this.jjnewStateCnt++] = 65; 
/*      */               break;
/*      */             case 67:
/* 5642 */               if (this.curChar == 116 && kind > 117)
/* 5643 */                 kind = 117; 
/*      */               break;
/*      */             case 68:
/* 5646 */               if (this.curChar == 103)
/* 5647 */                 jjCheckNAdd(67); 
/*      */               break;
/*      */             case 69:
/* 5650 */               if (this.curChar == 101 && kind > 118)
/* 5651 */                 kind = 118; 
/*      */               break;
/*      */             case 70:
/*      */             case 100:
/* 5655 */               if (this.curChar == 116)
/* 5656 */                 jjCheckNAdd(69); 
/*      */               break;
/*      */             case 71:
/* 5659 */               if (this.curChar == 103)
/* 5660 */                 this.jjstateSet[this.jjnewStateCnt++] = 70; 
/*      */               break;
/*      */             case 72:
/* 5663 */               if (this.curChar == 100 && kind > 127)
/* 5664 */                 kind = 127; 
/*      */               break;
/*      */             case 73:
/* 5667 */               if (this.curChar == 110)
/* 5668 */                 this.jjstateSet[this.jjnewStateCnt++] = 72; 
/*      */               break;
/*      */             case 74:
/* 5671 */               if (this.curChar == 97)
/* 5672 */                 this.jjstateSet[this.jjnewStateCnt++] = 73; 
/*      */               break;
/*      */             case 77:
/* 5675 */               if (this.curChar == 116)
/* 5676 */                 this.jjstateSet[this.jjnewStateCnt++] = 76; 
/*      */               break;
/*      */             case 78:
/* 5679 */               if (this.curChar == 108)
/* 5680 */                 this.jjstateSet[this.jjnewStateCnt++] = 77; 
/*      */               break;
/*      */             case 80:
/* 5683 */               if (this.curChar == 116)
/* 5684 */                 this.jjstateSet[this.jjnewStateCnt++] = 79; 
/*      */               break;
/*      */             case 81:
/* 5687 */               if (this.curChar == 108)
/* 5688 */                 this.jjstateSet[this.jjnewStateCnt++] = 80; 
/*      */               break;
/*      */             case 83:
/* 5691 */               if (this.curChar == 116)
/* 5692 */                 this.jjstateSet[this.jjnewStateCnt++] = 82; 
/*      */               break;
/*      */             case 84:
/* 5695 */               if (this.curChar == 103)
/* 5696 */                 this.jjstateSet[this.jjnewStateCnt++] = 83; 
/*      */               break;
/*      */             case 87:
/* 5699 */               if (this.curChar == 116)
/* 5700 */                 this.jjstateSet[this.jjnewStateCnt++] = 86; 
/*      */               break;
/*      */             case 88:
/* 5703 */               if (this.curChar == 103)
/* 5704 */                 this.jjstateSet[this.jjnewStateCnt++] = 87; 
/*      */               break;
/*      */             case 91:
/* 5707 */               if (this.curChar == 112)
/* 5708 */                 this.jjstateSet[this.jjnewStateCnt++] = 90; 
/*      */               break;
/*      */             case 92:
/* 5711 */               if (this.curChar == 109)
/* 5712 */                 this.jjstateSet[this.jjnewStateCnt++] = 91; 
/*      */               break;
/*      */             case 93:
/* 5715 */               if (this.curChar == 97)
/* 5716 */                 this.jjstateSet[this.jjnewStateCnt++] = 92; 
/*      */               break;
/*      */             case 96:
/* 5719 */               if (this.curChar == 112)
/* 5720 */                 this.jjstateSet[this.jjnewStateCnt++] = 95; 
/*      */               break;
/*      */             case 97:
/* 5723 */               if (this.curChar == 109)
/* 5724 */                 this.jjstateSet[this.jjnewStateCnt++] = 96; 
/*      */               break;
/*      */             case 98:
/* 5727 */               if (this.curChar == 97)
/* 5728 */                 this.jjstateSet[this.jjnewStateCnt++] = 97; 
/*      */               break;
/*      */             case 99:
/* 5731 */               if (this.curChar == 103) {
/* 5732 */                 jjCheckNAddTwoStates(67, 100);
/*      */               }
/*      */               break;
/*      */           } 
/* 5736 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 5740 */         int hiByte = this.curChar >> 8;
/* 5741 */         int i1 = hiByte >> 6;
/* 5742 */         long l1 = 1L << (hiByte & 0x3F);
/* 5743 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 5744 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 5747 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 5750 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 5752 */               if (kind > 142)
/* 5753 */                 kind = 142; 
/* 5754 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/*      */             case 101:
/* 5758 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 5760 */               if (kind > 142)
/* 5761 */                 kind = 142; 
/* 5762 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 6:
/* 5765 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 5766 */                 jjAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 5769 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 5770 */                 jjAddStates(358, 360); 
/*      */               break;
/*      */             case 21:
/* 5773 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 5774 */                 jjAddStates(375, 376); 
/*      */               break;
/*      */             case 24:
/* 5777 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 5778 */                 jjAddStates(377, 378);  break;
/*      */             default:
/* 5780 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 5782 */         } while (i != startsAt);
/*      */       } 
/* 5784 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 5786 */         this.jjmatchedKind = kind;
/* 5787 */         this.jjmatchedPos = curPos;
/* 5788 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 5790 */       curPos++;
/* 5791 */       if ((i = this.jjnewStateCnt) == (startsAt = 101 - (this.jjnewStateCnt = startsAt)))
/* 5792 */         return curPos;  
/* 5793 */       try { this.curChar = this.input_stream.readChar(); }
/* 5794 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_5(int pos, long active0, long active1) {
/* 5798 */     switch (pos) {
/*      */     
/*      */     } 
/* 5801 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_5(int pos, long active0, long active1) {
/* 5805 */     return jjMoveNfa_5(jjStopStringLiteralDfa_5(pos, active0, active1), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_5() {
/* 5808 */     switch (this.curChar) {
/*      */       
/*      */       case 45:
/* 5811 */         return jjStartNfaWithStates_5(0, 90, 3);
/*      */     } 
/* 5813 */     return jjMoveNfa_5(1, 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private int jjStartNfaWithStates_5(int pos, int kind, int state) {
/* 5818 */     this.jjmatchedKind = kind;
/* 5819 */     this.jjmatchedPos = pos; 
/* 5820 */     try { this.curChar = this.input_stream.readChar(); }
/* 5821 */     catch (IOException e) { return pos + 1; }
/* 5822 */      return jjMoveNfa_5(state, pos + 1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_5(int startState, int curPos) {
/* 5826 */     int startsAt = 0;
/* 5827 */     this.jjnewStateCnt = 6;
/* 5828 */     int i = 1;
/* 5829 */     this.jjstateSet[0] = startState;
/* 5830 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 5833 */       if (++this.jjround == Integer.MAX_VALUE)
/* 5834 */         ReInitRounds(); 
/* 5835 */       if (this.curChar < 64) {
/*      */         
/* 5837 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 5840 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 3:
/* 5843 */               if (this.curChar == 45)
/* 5844 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; 
/* 5845 */               if (this.curChar == 45)
/* 5846 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 1:
/* 5849 */               if ((0xBFFFDFFFFFFFFFFFL & l) != 0L) {
/*      */                 
/* 5851 */                 if (kind > 87)
/* 5852 */                   kind = 87; 
/* 5853 */                 jjCheckNAdd(0); break;
/*      */               } 
/* 5855 */               if (this.curChar == 45)
/* 5856 */                 jjAddStates(397, 398); 
/*      */               break;
/*      */             case 0:
/* 5859 */               if ((0xBFFFDFFFFFFFFFFFL & l) == 0L)
/*      */                 break; 
/* 5861 */               kind = 87;
/* 5862 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 2:
/* 5865 */               if (this.curChar == 62)
/* 5866 */                 kind = 91; 
/*      */               break;
/*      */             case 5:
/* 5869 */               if (this.curChar == 45) {
/* 5870 */                 this.jjstateSet[this.jjnewStateCnt++] = 4;
/*      */               }
/*      */               break;
/*      */           } 
/* 5874 */         } while (i != startsAt);
/*      */       }
/* 5876 */       else if (this.curChar < 128) {
/*      */         
/* 5878 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 5881 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 0:
/*      */             case 1:
/* 5885 */               if ((0xFFFFFFFFDFFFFFFFL & l) == 0L)
/*      */                 break; 
/* 5887 */               kind = 87;
/* 5888 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 4:
/* 5891 */               if (this.curChar == 93) {
/* 5892 */                 kind = 91;
/*      */               }
/*      */               break;
/*      */           } 
/* 5896 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 5900 */         int hiByte = this.curChar >> 8;
/* 5901 */         int i1 = hiByte >> 6;
/* 5902 */         long l1 = 1L << (hiByte & 0x3F);
/* 5903 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 5904 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 5907 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 0:
/*      */             case 1:
/* 5911 */               if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 5913 */               if (kind > 87)
/* 5914 */                 kind = 87; 
/* 5915 */               jjCheckNAdd(0); break;
/*      */             default:
/* 5917 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 5919 */         } while (i != startsAt);
/*      */       } 
/* 5921 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 5923 */         this.jjmatchedKind = kind;
/* 5924 */         this.jjmatchedPos = curPos;
/* 5925 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 5927 */       curPos++;
/* 5928 */       if ((i = this.jjnewStateCnt) == (startsAt = 6 - (this.jjnewStateCnt = startsAt)))
/* 5929 */         return curPos;  
/* 5930 */       try { this.curChar = this.input_stream.readChar(); }
/* 5931 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_1(int pos, long active0, long active1) {
/* 5935 */     switch (pos) {
/*      */       
/*      */       case 0:
/* 5938 */         if ((active1 & 0x1C0000L) != 0L) {
/*      */           
/* 5940 */           this.jjmatchedKind = 81;
/* 5941 */           return -1;
/*      */         } 
/* 5943 */         return -1;
/*      */     } 
/* 5945 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_1(int pos, long active0, long active1) {
/* 5949 */     return jjMoveNfa_1(jjStopStringLiteralDfa_1(pos, active0, active1), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_1() {
/* 5952 */     switch (this.curChar) {
/*      */       
/*      */       case 35:
/* 5955 */         return jjMoveStringLiteralDfa1_1(524288L);
/*      */       case 36:
/* 5957 */         return jjMoveStringLiteralDfa1_1(262144L);
/*      */       case 91:
/* 5959 */         return jjMoveStringLiteralDfa1_1(1048576L);
/*      */     } 
/* 5961 */     return jjMoveNfa_1(2, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_1(long active1) {
/*      */     try {
/* 5965 */       this.curChar = this.input_stream.readChar();
/* 5966 */     } catch (IOException e) {
/* 5967 */       jjStopStringLiteralDfa_1(0, 0L, active1);
/* 5968 */       return 1;
/*      */     } 
/* 5970 */     switch (this.curChar) {
/*      */       
/*      */       case 61:
/* 5973 */         if ((active1 & 0x100000L) != 0L)
/* 5974 */           return jjStopAtPos(1, 84); 
/*      */         break;
/*      */       case 123:
/* 5977 */         if ((active1 & 0x40000L) != 0L)
/* 5978 */           return jjStopAtPos(1, 82); 
/* 5979 */         if ((active1 & 0x80000L) != 0L) {
/* 5980 */           return jjStopAtPos(1, 83);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 5985 */     return jjStartNfa_1(0, 0L, active1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_1(int startState, int curPos) {
/* 5989 */     int startsAt = 0;
/* 5990 */     this.jjnewStateCnt = 3;
/* 5991 */     int i = 1;
/* 5992 */     this.jjstateSet[0] = startState;
/* 5993 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 5996 */       if (++this.jjround == Integer.MAX_VALUE)
/* 5997 */         ReInitRounds(); 
/* 5998 */       if (this.curChar < 64) {
/*      */         
/* 6000 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 6003 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 2:
/* 6006 */               if ((0xEFFFFFE6FFFFD9FFL & l) != 0L) {
/*      */                 
/* 6008 */                 if (kind > 80)
/* 6009 */                   kind = 80; 
/* 6010 */                 jjCheckNAdd(1); break;
/*      */               } 
/* 6012 */               if ((0x100002600L & l) != 0L) {
/*      */                 
/* 6014 */                 if (kind > 79)
/* 6015 */                   kind = 79; 
/* 6016 */                 jjCheckNAdd(0); break;
/*      */               } 
/* 6018 */               if ((0x1000001800000000L & l) != 0L)
/*      */               {
/* 6020 */                 if (kind > 81)
/* 6021 */                   kind = 81; 
/*      */               }
/*      */               break;
/*      */             case 0:
/* 6025 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 6027 */               kind = 79;
/* 6028 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 1:
/* 6031 */               if ((0xEFFFFFE6FFFFD9FFL & l) == 0L)
/*      */                 break; 
/* 6033 */               kind = 80;
/* 6034 */               jjCheckNAdd(1);
/*      */               break;
/*      */           } 
/*      */         
/* 6038 */         } while (i != startsAt);
/*      */       }
/* 6040 */       else if (this.curChar < 128) {
/*      */         
/* 6042 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 6045 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 2:
/* 6048 */               if ((0xF7FFFFFFF7FFFFFFL & l) != 0L) {
/*      */                 
/* 6050 */                 if (kind > 80)
/* 6051 */                   kind = 80; 
/* 6052 */                 jjCheckNAdd(1); break;
/*      */               } 
/* 6054 */               if ((0x800000008000000L & l) != 0L)
/*      */               {
/* 6056 */                 if (kind > 81)
/* 6057 */                   kind = 81; 
/*      */               }
/*      */               break;
/*      */             case 1:
/* 6061 */               if ((0xF7FFFFFFF7FFFFFFL & l) == 0L)
/*      */                 break; 
/* 6063 */               kind = 80;
/* 6064 */               jjCheckNAdd(1);
/*      */               break;
/*      */           } 
/*      */         
/* 6068 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 6072 */         int hiByte = this.curChar >> 8;
/* 6073 */         int i1 = hiByte >> 6;
/* 6074 */         long l1 = 1L << (hiByte & 0x3F);
/* 6075 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 6076 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 6079 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/*      */             case 2:
/* 6083 */               if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 6085 */               if (kind > 80)
/* 6086 */                 kind = 80; 
/* 6087 */               jjCheckNAdd(1); break;
/*      */             default:
/* 6089 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 6091 */         } while (i != startsAt);
/*      */       } 
/* 6093 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 6095 */         this.jjmatchedKind = kind;
/* 6096 */         this.jjmatchedPos = curPos;
/* 6097 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 6099 */       curPos++;
/* 6100 */       if ((i = this.jjnewStateCnt) == (startsAt = 3 - (this.jjnewStateCnt = startsAt)))
/* 6101 */         return curPos;  
/* 6102 */       try { this.curChar = this.input_stream.readChar(); }
/* 6103 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_6(int pos, long active0, long active1, long active2) {
/* 6107 */     switch (pos) {
/*      */       
/*      */       case 0:
/* 6110 */         if ((active2 & 0x20L) != 0L)
/* 6111 */           return 36; 
/* 6112 */         if ((active1 & 0x2000800000000000L) != 0L)
/* 6113 */           return 40; 
/* 6114 */         if ((active1 & 0x204200000000000L) != 0L)
/* 6115 */           return 43; 
/* 6116 */         if ((active1 & 0x1000005800000000L) != 0L)
/* 6117 */           return 50; 
/* 6118 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x3800L) != 0L) {
/*      */           
/* 6120 */           this.jjmatchedKind = 142;
/* 6121 */           return 100;
/*      */         } 
/* 6123 */         return -1;
/*      */       case 1:
/* 6125 */         if ((active2 & 0x1800L) != 0L)
/* 6126 */           return 100; 
/* 6127 */         if ((active1 & 0x1000005000000000L) != 0L)
/* 6128 */           return 49; 
/* 6129 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 6131 */           if (this.jjmatchedPos != 1) {
/*      */             
/* 6133 */             this.jjmatchedKind = 142;
/* 6134 */             this.jjmatchedPos = 1;
/*      */           } 
/* 6136 */           return 100;
/*      */         } 
/* 6138 */         return -1;
/*      */       case 2:
/* 6140 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 6142 */           this.jjmatchedKind = 142;
/* 6143 */           this.jjmatchedPos = 2;
/* 6144 */           return 100;
/*      */         } 
/* 6146 */         return -1;
/*      */       case 3:
/* 6148 */         if ((active1 & 0x100000000L) != 0L)
/* 6149 */           return 100; 
/* 6150 */         if ((active1 & 0x80000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 6152 */           this.jjmatchedKind = 142;
/* 6153 */           this.jjmatchedPos = 3;
/* 6154 */           return 100;
/*      */         } 
/* 6156 */         return -1;
/*      */     } 
/* 6158 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_6(int pos, long active0, long active1, long active2) {
/* 6162 */     return jjMoveNfa_6(jjStopStringLiteralDfa_6(pos, active0, active1, active2), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_6() {
/* 6165 */     switch (this.curChar) {
/*      */       
/*      */       case 33:
/* 6168 */         this.jjmatchedKind = 129;
/* 6169 */         return jjMoveStringLiteralDfa1_6(8796093022208L, 0L);
/*      */       case 37:
/* 6171 */         this.jjmatchedKind = 126;
/* 6172 */         return jjMoveStringLiteralDfa1_6(281474976710656L, 0L);
/*      */       case 40:
/* 6174 */         return jjStopAtPos(0, 135);
/*      */       case 41:
/* 6176 */         return jjStopAtPos(0, 136);
/*      */       case 42:
/* 6178 */         this.jjmatchedKind = 122;
/* 6179 */         return jjMoveStringLiteralDfa1_6(576531121047601152L, 0L);
/*      */       case 43:
/* 6181 */         this.jjmatchedKind = 120;
/* 6182 */         return jjMoveStringLiteralDfa1_6(580542139465728L, 0L);
/*      */       case 44:
/* 6184 */         return jjStopAtPos(0, 130);
/*      */       case 45:
/* 6186 */         this.jjmatchedKind = 121;
/* 6187 */         return jjMoveStringLiteralDfa1_6(1161084278931456L, 0L);
/*      */       case 46:
/* 6189 */         this.jjmatchedKind = 99;
/* 6190 */         return jjMoveStringLiteralDfa1_6(1152921848204230656L, 0L);
/*      */       case 47:
/* 6192 */         this.jjmatchedKind = 125;
/* 6193 */         return jjMoveStringLiteralDfa1_6(140737488355328L, 0L);
/*      */       case 58:
/* 6195 */         return jjStopAtPos(0, 132);
/*      */       case 59:
/* 6197 */         return jjStopAtPos(0, 131);
/*      */       case 61:
/* 6199 */         this.jjmatchedKind = 105;
/* 6200 */         return jjMoveStringLiteralDfa1_6(4398046511104L, 0L);
/*      */       case 62:
/* 6202 */         return jjStopAtPos(0, 148);
/*      */       case 63:
/* 6204 */         this.jjmatchedKind = 103;
/* 6205 */         return jjMoveStringLiteralDfa1_6(1099511627776L, 0L);
/*      */       case 91:
/* 6207 */         return jjStartNfaWithStates_6(0, 133, 36);
/*      */       case 93:
/* 6209 */         return jjStopAtPos(0, 134);
/*      */       case 97:
/* 6211 */         return jjMoveStringLiteralDfa1_6(0L, 4096L);
/*      */       case 102:
/* 6213 */         return jjMoveStringLiteralDfa1_6(2147483648L, 0L);
/*      */       case 105:
/* 6215 */         return jjMoveStringLiteralDfa1_6(0L, 2048L);
/*      */       case 116:
/* 6217 */         return jjMoveStringLiteralDfa1_6(4294967296L, 0L);
/*      */       case 117:
/* 6219 */         return jjMoveStringLiteralDfa1_6(0L, 8192L);
/*      */       case 123:
/* 6221 */         return jjStopAtPos(0, 137);
/*      */       case 125:
/* 6223 */         return jjStopAtPos(0, 138);
/*      */     } 
/* 6225 */     return jjMoveNfa_6(0, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_6(long active1, long active2) {
/*      */     try {
/* 6229 */       this.curChar = this.input_stream.readChar();
/* 6230 */     } catch (IOException e) {
/* 6231 */       jjStopStringLiteralDfa_6(0, 0L, active1, active2);
/* 6232 */       return 1;
/*      */     } 
/* 6234 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 6237 */         if ((active1 & 0x800000000000000L) != 0L)
/* 6238 */           return jjStopAtPos(1, 123); 
/*      */         break;
/*      */       case 43:
/* 6241 */         if ((active1 & 0x2000000000000L) != 0L)
/* 6242 */           return jjStopAtPos(1, 113); 
/*      */         break;
/*      */       case 45:
/* 6245 */         if ((active1 & 0x4000000000000L) != 0L)
/* 6246 */           return jjStopAtPos(1, 114); 
/*      */         break;
/*      */       case 46:
/* 6249 */         if ((active1 & 0x1000000000L) != 0L) {
/*      */           
/* 6251 */           this.jjmatchedKind = 100;
/* 6252 */           this.jjmatchedPos = 1;
/*      */         } 
/* 6254 */         return jjMoveStringLiteralDfa2_6(active1, 1152921779484753920L, active2, 0L);
/*      */       case 61:
/* 6256 */         if ((active1 & 0x40000000000L) != 0L)
/* 6257 */           return jjStopAtPos(1, 106); 
/* 6258 */         if ((active1 & 0x80000000000L) != 0L)
/* 6259 */           return jjStopAtPos(1, 107); 
/* 6260 */         if ((active1 & 0x100000000000L) != 0L)
/* 6261 */           return jjStopAtPos(1, 108); 
/* 6262 */         if ((active1 & 0x200000000000L) != 0L)
/* 6263 */           return jjStopAtPos(1, 109); 
/* 6264 */         if ((active1 & 0x400000000000L) != 0L)
/* 6265 */           return jjStopAtPos(1, 110); 
/* 6266 */         if ((active1 & 0x800000000000L) != 0L)
/* 6267 */           return jjStopAtPos(1, 111); 
/* 6268 */         if ((active1 & 0x1000000000000L) != 0L)
/* 6269 */           return jjStopAtPos(1, 112); 
/*      */         break;
/*      */       case 63:
/* 6272 */         if ((active1 & 0x10000000000L) != 0L)
/* 6273 */           return jjStopAtPos(1, 104); 
/*      */         break;
/*      */       case 97:
/* 6276 */         return jjMoveStringLiteralDfa2_6(active1, 2147483648L, active2, 0L);
/*      */       case 110:
/* 6278 */         if ((active2 & 0x800L) != 0L)
/* 6279 */           return jjStartNfaWithStates_6(1, 139, 100); 
/*      */         break;
/*      */       case 114:
/* 6282 */         return jjMoveStringLiteralDfa2_6(active1, 4294967296L, active2, 0L);
/*      */       case 115:
/* 6284 */         if ((active2 & 0x1000L) != 0L)
/* 6285 */           return jjStartNfaWithStates_6(1, 140, 100); 
/* 6286 */         return jjMoveStringLiteralDfa2_6(active1, 0L, active2, 8192L);
/*      */     } 
/*      */ 
/*      */     
/* 6290 */     return jjStartNfa_6(0, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa2_6(long old1, long active1, long old2, long active2) {
/* 6293 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 6294 */       return jjStartNfa_6(0, 0L, old1, old2);  try {
/* 6295 */       this.curChar = this.input_stream.readChar();
/* 6296 */     } catch (IOException e) {
/* 6297 */       jjStopStringLiteralDfa_6(1, 0L, active1, active2);
/* 6298 */       return 2;
/*      */     } 
/* 6300 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 6303 */         if ((active1 & 0x4000000000L) != 0L)
/* 6304 */           return jjStopAtPos(2, 102); 
/*      */         break;
/*      */       case 46:
/* 6307 */         if ((active1 & 0x1000000000000000L) != 0L)
/* 6308 */           return jjStopAtPos(2, 124); 
/*      */         break;
/*      */       case 105:
/* 6311 */         return jjMoveStringLiteralDfa3_6(active1, 0L, active2, 8192L);
/*      */       case 108:
/* 6313 */         return jjMoveStringLiteralDfa3_6(active1, 2147483648L, active2, 0L);
/*      */       case 117:
/* 6315 */         return jjMoveStringLiteralDfa3_6(active1, 4294967296L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 6319 */     return jjStartNfa_6(1, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa3_6(long old1, long active1, long old2, long active2) {
/* 6322 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 6323 */       return jjStartNfa_6(1, 0L, old1, old2);  try {
/* 6324 */       this.curChar = this.input_stream.readChar();
/* 6325 */     } catch (IOException e) {
/* 6326 */       jjStopStringLiteralDfa_6(2, 0L, active1, active2);
/* 6327 */       return 3;
/*      */     } 
/* 6329 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 6332 */         if ((active1 & 0x100000000L) != 0L)
/* 6333 */           return jjStartNfaWithStates_6(3, 96, 100); 
/*      */         break;
/*      */       case 110:
/* 6336 */         return jjMoveStringLiteralDfa4_6(active1, 0L, active2, 8192L);
/*      */       case 115:
/* 6338 */         return jjMoveStringLiteralDfa4_6(active1, 2147483648L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 6342 */     return jjStartNfa_6(2, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa4_6(long old1, long active1, long old2, long active2) {
/* 6345 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 6346 */       return jjStartNfa_6(2, 0L, old1, old2);  try {
/* 6347 */       this.curChar = this.input_stream.readChar();
/* 6348 */     } catch (IOException e) {
/* 6349 */       jjStopStringLiteralDfa_6(3, 0L, active1, active2);
/* 6350 */       return 4;
/*      */     } 
/* 6352 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 6355 */         if ((active1 & 0x80000000L) != 0L)
/* 6356 */           return jjStartNfaWithStates_6(4, 95, 100); 
/*      */         break;
/*      */       case 103:
/* 6359 */         if ((active2 & 0x2000L) != 0L) {
/* 6360 */           return jjStartNfaWithStates_6(4, 141, 100);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 6365 */     return jjStartNfa_6(3, 0L, active1, active2);
/*      */   }
/*      */   
/*      */   private int jjStartNfaWithStates_6(int pos, int kind, int state) {
/* 6369 */     this.jjmatchedKind = kind;
/* 6370 */     this.jjmatchedPos = pos; 
/* 6371 */     try { this.curChar = this.input_stream.readChar(); }
/* 6372 */     catch (IOException e) { return pos + 1; }
/* 6373 */      return jjMoveNfa_6(state, pos + 1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_6(int startState, int curPos) {
/* 6377 */     int startsAt = 0;
/* 6378 */     this.jjnewStateCnt = 100;
/* 6379 */     int i = 1;
/* 6380 */     this.jjstateSet[0] = startState;
/* 6381 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 6384 */       if (++this.jjround == Integer.MAX_VALUE)
/* 6385 */         ReInitRounds(); 
/* 6386 */       if (this.curChar < 64) {
/*      */         
/* 6388 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 6391 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 43:
/* 6394 */               if (this.curChar == 38) {
/* 6395 */                 this.jjstateSet[this.jjnewStateCnt++] = 46; break;
/* 6396 */               }  if (this.curChar == 62)
/*      */               {
/* 6398 */                 if (kind > 119)
/* 6399 */                   kind = 119; 
/*      */               }
/*      */               break;
/*      */             case 40:
/* 6403 */               if (this.curChar == 62 && kind > 149)
/* 6404 */                 kind = 149; 
/*      */               break;
/*      */             case 49:
/* 6407 */               if (this.curChar == 33) {
/*      */                 
/* 6409 */                 if (kind > 101)
/* 6410 */                   kind = 101;  break;
/*      */               } 
/* 6412 */               if (this.curChar == 60)
/*      */               {
/* 6414 */                 if (kind > 101)
/* 6415 */                   kind = 101; 
/*      */               }
/*      */               break;
/*      */             case 50:
/* 6419 */               if (this.curChar == 46)
/* 6420 */                 this.jjstateSet[this.jjnewStateCnt++] = 51; 
/* 6421 */               if (this.curChar == 46)
/* 6422 */                 this.jjstateSet[this.jjnewStateCnt++] = 49; 
/*      */               break;
/*      */             case 29:
/*      */             case 100:
/* 6426 */               if ((0x3FF001000000000L & l) == 0L)
/*      */                 break; 
/* 6428 */               if (kind > 142)
/* 6429 */                 kind = 142; 
/* 6430 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 0:
/* 6433 */               if ((0x3FF000000000000L & l) != 0L) {
/*      */                 
/* 6435 */                 if (kind > 97)
/* 6436 */                   kind = 97; 
/* 6437 */                 jjCheckNAddStates(399, 401);
/*      */               }
/* 6439 */               else if ((0x100002600L & l) != 0L) {
/*      */                 
/* 6441 */                 if (kind > 152)
/* 6442 */                   kind = 152; 
/* 6443 */                 jjCheckNAdd(38);
/*      */               }
/* 6445 */               else if (this.curChar == 38) {
/* 6446 */                 jjAddStates(402, 407);
/* 6447 */               } else if (this.curChar == 46) {
/* 6448 */                 jjAddStates(408, 409);
/* 6449 */               } else if (this.curChar == 45) {
/* 6450 */                 jjAddStates(410, 411);
/* 6451 */               } else if (this.curChar == 47) {
/* 6452 */                 jjAddStates(412, 413);
/* 6453 */               } else if (this.curChar == 35) {
/* 6454 */                 jjCheckNAdd(33);
/* 6455 */               } else if (this.curChar == 36) {
/* 6456 */                 jjCheckNAdd(33);
/* 6457 */               } else if (this.curChar == 60) {
/* 6458 */                 jjCheckNAdd(22);
/* 6459 */               } else if (this.curChar == 39) {
/* 6460 */                 jjCheckNAddStates(414, 416);
/* 6461 */               } else if (this.curChar == 34) {
/* 6462 */                 jjCheckNAddStates(417, 419);
/* 6463 */               }  if (this.curChar == 36) {
/*      */                 
/* 6465 */                 if (kind > 142)
/* 6466 */                   kind = 142; 
/* 6467 */                 jjCheckNAddTwoStates(29, 30); break;
/*      */               } 
/* 6469 */               if (this.curChar == 38) {
/*      */                 
/* 6471 */                 if (kind > 127)
/* 6472 */                   kind = 127;  break;
/*      */               } 
/* 6474 */               if (this.curChar == 60)
/*      */               {
/* 6476 */                 if (kind > 115)
/* 6477 */                   kind = 115; 
/*      */               }
/*      */               break;
/*      */             case 1:
/* 6481 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 6482 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 4:
/* 6485 */               if ((0x3FF000000000000L & l) != 0L)
/* 6486 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 5:
/* 6489 */               if (this.curChar == 34 && kind > 93)
/* 6490 */                 kind = 93; 
/*      */               break;
/*      */             case 6:
/* 6493 */               if ((0x2000008400000000L & l) != 0L)
/* 6494 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 7:
/* 6497 */               if (this.curChar == 39)
/* 6498 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 8:
/* 6501 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 6502 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 11:
/* 6505 */               if ((0x3FF000000000000L & l) != 0L)
/* 6506 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 12:
/* 6509 */               if (this.curChar == 39 && kind > 93)
/* 6510 */                 kind = 93; 
/*      */               break;
/*      */             case 13:
/* 6513 */               if ((0x2000008400000000L & l) != 0L)
/* 6514 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 15:
/* 6517 */               if (this.curChar == 34)
/* 6518 */                 jjCheckNAddTwoStates(16, 17); 
/*      */               break;
/*      */             case 16:
/* 6521 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 6522 */                 jjCheckNAddTwoStates(16, 17); 
/*      */               break;
/*      */             case 17:
/* 6525 */               if (this.curChar == 34 && kind > 94)
/* 6526 */                 kind = 94; 
/*      */               break;
/*      */             case 18:
/* 6529 */               if (this.curChar == 39)
/* 6530 */                 jjCheckNAddTwoStates(19, 20); 
/*      */               break;
/*      */             case 19:
/* 6533 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 6534 */                 jjCheckNAddTwoStates(19, 20); 
/*      */               break;
/*      */             case 20:
/* 6537 */               if (this.curChar == 39 && kind > 94)
/* 6538 */                 kind = 94; 
/*      */               break;
/*      */             case 21:
/* 6541 */               if (this.curChar == 60 && kind > 115)
/* 6542 */                 kind = 115; 
/*      */               break;
/*      */             case 22:
/* 6545 */               if (this.curChar == 61 && kind > 116)
/* 6546 */                 kind = 116; 
/*      */               break;
/*      */             case 23:
/* 6549 */               if (this.curChar == 60)
/* 6550 */                 jjCheckNAdd(22); 
/*      */               break;
/*      */             case 24:
/*      */             case 88:
/* 6554 */               if (this.curChar == 38 && kind > 127)
/* 6555 */                 kind = 127; 
/*      */               break;
/*      */             case 28:
/* 6558 */               if (this.curChar != 36)
/*      */                 break; 
/* 6560 */               if (kind > 142)
/* 6561 */                 kind = 142; 
/* 6562 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 31:
/* 6565 */               if ((0x400600800000000L & l) == 0L)
/*      */                 break; 
/* 6567 */               if (kind > 142)
/* 6568 */                 kind = 142; 
/* 6569 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 34:
/* 6572 */               if (this.curChar == 36)
/* 6573 */                 jjCheckNAdd(33); 
/*      */               break;
/*      */             case 35:
/* 6576 */               if (this.curChar == 35)
/* 6577 */                 jjCheckNAdd(33); 
/*      */               break;
/*      */             case 36:
/* 6580 */               if (this.curChar == 61 && kind > 143)
/* 6581 */                 kind = 143; 
/*      */               break;
/*      */             case 38:
/* 6584 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 6586 */               if (kind > 152)
/* 6587 */                 kind = 152; 
/* 6588 */               jjCheckNAdd(38);
/*      */               break;
/*      */             case 39:
/* 6591 */               if (this.curChar == 47)
/* 6592 */                 jjAddStates(412, 413); 
/*      */               break;
/*      */             case 42:
/* 6595 */               if (this.curChar == 45)
/* 6596 */                 jjAddStates(410, 411); 
/*      */               break;
/*      */             case 44:
/* 6599 */               if (this.curChar == 59 && kind > 119)
/* 6600 */                 kind = 119; 
/*      */               break;
/*      */             case 47:
/* 6603 */               if (this.curChar == 38)
/* 6604 */                 this.jjstateSet[this.jjnewStateCnt++] = 46; 
/*      */               break;
/*      */             case 48:
/* 6607 */               if (this.curChar == 46)
/* 6608 */                 jjAddStates(408, 409); 
/*      */               break;
/*      */             case 51:
/* 6611 */               if (this.curChar == 33 && kind > 101)
/* 6612 */                 kind = 101; 
/*      */               break;
/*      */             case 52:
/* 6615 */               if (this.curChar == 46)
/* 6616 */                 this.jjstateSet[this.jjnewStateCnt++] = 51; 
/*      */               break;
/*      */             case 53:
/* 6619 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 6621 */               if (kind > 97)
/* 6622 */                 kind = 97; 
/* 6623 */               jjCheckNAddStates(399, 401);
/*      */               break;
/*      */             case 54:
/* 6626 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 6628 */               if (kind > 97)
/* 6629 */                 kind = 97; 
/* 6630 */               jjCheckNAdd(54);
/*      */               break;
/*      */             case 55:
/* 6633 */               if ((0x3FF000000000000L & l) != 0L)
/* 6634 */                 jjCheckNAddTwoStates(55, 56); 
/*      */               break;
/*      */             case 56:
/* 6637 */               if (this.curChar == 46)
/* 6638 */                 jjCheckNAdd(57); 
/*      */               break;
/*      */             case 57:
/* 6641 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 6643 */               if (kind > 98)
/* 6644 */                 kind = 98; 
/* 6645 */               jjCheckNAdd(57);
/*      */               break;
/*      */             case 74:
/* 6648 */               if (this.curChar == 38)
/* 6649 */                 jjAddStates(402, 407); 
/*      */               break;
/*      */             case 75:
/* 6652 */               if (this.curChar == 59 && kind > 115)
/* 6653 */                 kind = 115; 
/*      */               break;
/*      */             case 78:
/* 6656 */               if (this.curChar == 59)
/* 6657 */                 jjCheckNAdd(22); 
/*      */               break;
/*      */             case 81:
/* 6660 */               if (this.curChar == 59 && kind > 117)
/* 6661 */                 kind = 117; 
/*      */               break;
/*      */             case 84:
/* 6664 */               if (this.curChar == 61 && kind > 118)
/* 6665 */                 kind = 118; 
/*      */               break;
/*      */             case 85:
/* 6668 */               if (this.curChar == 59)
/* 6669 */                 this.jjstateSet[this.jjnewStateCnt++] = 84; 
/*      */               break;
/*      */             case 89:
/* 6672 */               if (this.curChar == 59 && kind > 127)
/* 6673 */                 kind = 127; 
/*      */               break;
/*      */             case 93:
/* 6676 */               if (this.curChar == 38)
/* 6677 */                 this.jjstateSet[this.jjnewStateCnt++] = 92; 
/*      */               break;
/*      */             case 94:
/* 6680 */               if (this.curChar == 59) {
/* 6681 */                 this.jjstateSet[this.jjnewStateCnt++] = 93;
/*      */               }
/*      */               break;
/*      */           } 
/* 6685 */         } while (i != startsAt);
/*      */       }
/* 6687 */       else if (this.curChar < 128) {
/*      */         
/* 6689 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 6692 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 40:
/* 6695 */               if (this.curChar == 93 && kind > 149)
/* 6696 */                 kind = 149; 
/*      */               break;
/*      */             case 100:
/* 6699 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 6701 */                 if (kind > 142)
/* 6702 */                   kind = 142; 
/* 6703 */                 jjCheckNAddTwoStates(29, 30); break;
/*      */               } 
/* 6705 */               if (this.curChar == 92)
/* 6706 */                 jjCheckNAdd(31); 
/*      */               break;
/*      */             case 0:
/* 6709 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 6711 */                 if (kind > 142)
/* 6712 */                   kind = 142; 
/* 6713 */                 jjCheckNAddTwoStates(29, 30);
/*      */               }
/* 6715 */               else if (this.curChar == 92) {
/* 6716 */                 jjAddStates(420, 424);
/* 6717 */               } else if (this.curChar == 91) {
/* 6718 */                 this.jjstateSet[this.jjnewStateCnt++] = 36;
/* 6719 */               } else if (this.curChar == 124) {
/* 6720 */                 this.jjstateSet[this.jjnewStateCnt++] = 26;
/* 6721 */               }  if (this.curChar == 103) {
/* 6722 */                 jjCheckNAddTwoStates(66, 99); break;
/* 6723 */               }  if (this.curChar == 108) {
/* 6724 */                 jjCheckNAddTwoStates(59, 61); break;
/* 6725 */               }  if (this.curChar == 92) {
/* 6726 */                 jjCheckNAdd(31); break;
/* 6727 */               }  if (this.curChar == 124) {
/*      */                 
/* 6729 */                 if (kind > 128)
/* 6730 */                   kind = 128;  break;
/*      */               } 
/* 6732 */               if (this.curChar == 114)
/* 6733 */                 jjAddStates(373, 374); 
/*      */               break;
/*      */             case 1:
/* 6736 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 6737 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 2:
/* 6740 */               if (this.curChar == 92)
/* 6741 */                 jjAddStates(425, 426); 
/*      */               break;
/*      */             case 3:
/* 6744 */               if (this.curChar == 120)
/* 6745 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; 
/*      */               break;
/*      */             case 4:
/* 6748 */               if ((0x7E0000007EL & l) != 0L)
/* 6749 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 6:
/* 6752 */               if ((0x81450C610000000L & l) != 0L)
/* 6753 */                 jjCheckNAddStates(417, 419); 
/*      */               break;
/*      */             case 8:
/* 6756 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 6757 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 9:
/* 6760 */               if (this.curChar == 92)
/* 6761 */                 jjAddStates(427, 428); 
/*      */               break;
/*      */             case 10:
/* 6764 */               if (this.curChar == 120)
/* 6765 */                 this.jjstateSet[this.jjnewStateCnt++] = 11; 
/*      */               break;
/*      */             case 11:
/* 6768 */               if ((0x7E0000007EL & l) != 0L)
/* 6769 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 13:
/* 6772 */               if ((0x81450C610000000L & l) != 0L)
/* 6773 */                 jjCheckNAddStates(414, 416); 
/*      */               break;
/*      */             case 14:
/* 6776 */               if (this.curChar == 114)
/* 6777 */                 jjAddStates(373, 374); 
/*      */               break;
/*      */             case 16:
/* 6780 */               jjAddStates(429, 430);
/*      */               break;
/*      */             case 19:
/* 6783 */               jjAddStates(431, 432);
/*      */               break;
/*      */             case 25:
/*      */             case 26:
/* 6787 */               if (this.curChar == 124 && kind > 128)
/* 6788 */                 kind = 128; 
/*      */               break;
/*      */             case 27:
/* 6791 */               if (this.curChar == 124)
/* 6792 */                 this.jjstateSet[this.jjnewStateCnt++] = 26; 
/*      */               break;
/*      */             case 28:
/* 6795 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 6797 */               if (kind > 142)
/* 6798 */                 kind = 142; 
/* 6799 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 29:
/* 6802 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 6804 */               if (kind > 142)
/* 6805 */                 kind = 142; 
/* 6806 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 30:
/* 6809 */               if (this.curChar == 92)
/* 6810 */                 jjCheckNAdd(31); 
/*      */               break;
/*      */             case 32:
/* 6813 */               if (this.curChar == 92)
/* 6814 */                 jjCheckNAdd(31); 
/*      */               break;
/*      */             case 33:
/* 6817 */               if (this.curChar == 123 && kind > 143)
/* 6818 */                 kind = 143; 
/*      */               break;
/*      */             case 37:
/* 6821 */               if (this.curChar == 91)
/* 6822 */                 this.jjstateSet[this.jjnewStateCnt++] = 36; 
/*      */               break;
/*      */             case 45:
/* 6825 */               if (this.curChar == 116)
/* 6826 */                 this.jjstateSet[this.jjnewStateCnt++] = 44; 
/*      */               break;
/*      */             case 46:
/* 6829 */               if (this.curChar == 103)
/* 6830 */                 this.jjstateSet[this.jjnewStateCnt++] = 45; 
/*      */               break;
/*      */             case 58:
/* 6833 */               if (this.curChar == 108)
/* 6834 */                 jjCheckNAddTwoStates(59, 61); 
/*      */               break;
/*      */             case 59:
/* 6837 */               if (this.curChar == 116 && kind > 115)
/* 6838 */                 kind = 115; 
/*      */               break;
/*      */             case 60:
/* 6841 */               if (this.curChar == 101 && kind > 116)
/* 6842 */                 kind = 116; 
/*      */               break;
/*      */             case 61:
/*      */             case 64:
/* 6846 */               if (this.curChar == 116)
/* 6847 */                 jjCheckNAdd(60); 
/*      */               break;
/*      */             case 62:
/* 6850 */               if (this.curChar == 92)
/* 6851 */                 jjAddStates(420, 424); 
/*      */               break;
/*      */             case 63:
/* 6854 */               if (this.curChar == 108)
/* 6855 */                 jjCheckNAdd(59); 
/*      */               break;
/*      */             case 65:
/* 6858 */               if (this.curChar == 108)
/* 6859 */                 this.jjstateSet[this.jjnewStateCnt++] = 64; 
/*      */               break;
/*      */             case 66:
/* 6862 */               if (this.curChar == 116 && kind > 117)
/* 6863 */                 kind = 117; 
/*      */               break;
/*      */             case 67:
/* 6866 */               if (this.curChar == 103)
/* 6867 */                 jjCheckNAdd(66); 
/*      */               break;
/*      */             case 68:
/* 6870 */               if (this.curChar == 101 && kind > 118)
/* 6871 */                 kind = 118; 
/*      */               break;
/*      */             case 69:
/*      */             case 99:
/* 6875 */               if (this.curChar == 116)
/* 6876 */                 jjCheckNAdd(68); 
/*      */               break;
/*      */             case 70:
/* 6879 */               if (this.curChar == 103)
/* 6880 */                 this.jjstateSet[this.jjnewStateCnt++] = 69; 
/*      */               break;
/*      */             case 71:
/* 6883 */               if (this.curChar == 100 && kind > 127)
/* 6884 */                 kind = 127; 
/*      */               break;
/*      */             case 72:
/* 6887 */               if (this.curChar == 110)
/* 6888 */                 this.jjstateSet[this.jjnewStateCnt++] = 71; 
/*      */               break;
/*      */             case 73:
/* 6891 */               if (this.curChar == 97)
/* 6892 */                 this.jjstateSet[this.jjnewStateCnt++] = 72; 
/*      */               break;
/*      */             case 76:
/* 6895 */               if (this.curChar == 116)
/* 6896 */                 this.jjstateSet[this.jjnewStateCnt++] = 75; 
/*      */               break;
/*      */             case 77:
/* 6899 */               if (this.curChar == 108)
/* 6900 */                 this.jjstateSet[this.jjnewStateCnt++] = 76; 
/*      */               break;
/*      */             case 79:
/* 6903 */               if (this.curChar == 116)
/* 6904 */                 this.jjstateSet[this.jjnewStateCnt++] = 78; 
/*      */               break;
/*      */             case 80:
/* 6907 */               if (this.curChar == 108)
/* 6908 */                 this.jjstateSet[this.jjnewStateCnt++] = 79; 
/*      */               break;
/*      */             case 82:
/* 6911 */               if (this.curChar == 116)
/* 6912 */                 this.jjstateSet[this.jjnewStateCnt++] = 81; 
/*      */               break;
/*      */             case 83:
/* 6915 */               if (this.curChar == 103)
/* 6916 */                 this.jjstateSet[this.jjnewStateCnt++] = 82; 
/*      */               break;
/*      */             case 86:
/* 6919 */               if (this.curChar == 116)
/* 6920 */                 this.jjstateSet[this.jjnewStateCnt++] = 85; 
/*      */               break;
/*      */             case 87:
/* 6923 */               if (this.curChar == 103)
/* 6924 */                 this.jjstateSet[this.jjnewStateCnt++] = 86; 
/*      */               break;
/*      */             case 90:
/* 6927 */               if (this.curChar == 112)
/* 6928 */                 this.jjstateSet[this.jjnewStateCnt++] = 89; 
/*      */               break;
/*      */             case 91:
/* 6931 */               if (this.curChar == 109)
/* 6932 */                 this.jjstateSet[this.jjnewStateCnt++] = 90; 
/*      */               break;
/*      */             case 92:
/* 6935 */               if (this.curChar == 97)
/* 6936 */                 this.jjstateSet[this.jjnewStateCnt++] = 91; 
/*      */               break;
/*      */             case 95:
/* 6939 */               if (this.curChar == 112)
/* 6940 */                 this.jjstateSet[this.jjnewStateCnt++] = 94; 
/*      */               break;
/*      */             case 96:
/* 6943 */               if (this.curChar == 109)
/* 6944 */                 this.jjstateSet[this.jjnewStateCnt++] = 95; 
/*      */               break;
/*      */             case 97:
/* 6947 */               if (this.curChar == 97)
/* 6948 */                 this.jjstateSet[this.jjnewStateCnt++] = 96; 
/*      */               break;
/*      */             case 98:
/* 6951 */               if (this.curChar == 103) {
/* 6952 */                 jjCheckNAddTwoStates(66, 99);
/*      */               }
/*      */               break;
/*      */           } 
/* 6956 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 6960 */         int hiByte = this.curChar >> 8;
/* 6961 */         int i1 = hiByte >> 6;
/* 6962 */         long l1 = 1L << (hiByte & 0x3F);
/* 6963 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 6964 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 6967 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 29:
/*      */             case 100:
/* 6971 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 6973 */               if (kind > 142)
/* 6974 */                 kind = 142; 
/* 6975 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 0:
/* 6978 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 6980 */               if (kind > 142)
/* 6981 */                 kind = 142; 
/* 6982 */               jjCheckNAddTwoStates(29, 30);
/*      */               break;
/*      */             case 1:
/* 6985 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 6986 */                 jjAddStates(417, 419); 
/*      */               break;
/*      */             case 8:
/* 6989 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 6990 */                 jjAddStates(414, 416); 
/*      */               break;
/*      */             case 16:
/* 6993 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 6994 */                 jjAddStates(429, 430); 
/*      */               break;
/*      */             case 19:
/* 6997 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 6998 */                 jjAddStates(431, 432);  break;
/*      */             default:
/* 7000 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 7002 */         } while (i != startsAt);
/*      */       } 
/* 7004 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 7006 */         this.jjmatchedKind = kind;
/* 7007 */         this.jjmatchedPos = curPos;
/* 7008 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 7010 */       curPos++;
/* 7011 */       if ((i = this.jjnewStateCnt) == (startsAt = 100 - (this.jjnewStateCnt = startsAt)))
/* 7012 */         return curPos;  
/* 7013 */       try { this.curChar = this.input_stream.readChar(); }
/* 7014 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/*      */   } private final int jjStopStringLiteralDfa_4(int pos, long active0, long active1, long active2) {
/* 7018 */     switch (pos) {
/*      */       
/*      */       case 0:
/* 7021 */         if ((active2 & 0x20L) != 0L)
/* 7022 */           return 2; 
/* 7023 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x3800L) != 0L) {
/*      */           
/* 7025 */           this.jjmatchedKind = 142;
/* 7026 */           return 106;
/*      */         } 
/* 7028 */         if ((active1 & 0x2000800000000000L) != 0L)
/* 7029 */           return 46; 
/* 7030 */         if ((active1 & 0x1000005800000000L) != 0L)
/* 7031 */           return 56; 
/* 7032 */         if ((active1 & 0x204200000000000L) != 0L)
/* 7033 */           return 49; 
/* 7034 */         if ((active1 & 0x80000000000L) != 0L || (active2 & 0x2L) != 0L)
/* 7035 */           return 44; 
/* 7036 */         return -1;
/*      */       case 1:
/* 7038 */         if ((active2 & 0x1800L) != 0L)
/* 7039 */           return 106; 
/* 7040 */         if ((active1 & 0x1000005000000000L) != 0L)
/* 7041 */           return 55; 
/* 7042 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 7044 */           if (this.jjmatchedPos != 1) {
/*      */             
/* 7046 */             this.jjmatchedKind = 142;
/* 7047 */             this.jjmatchedPos = 1;
/*      */           } 
/* 7049 */           return 106;
/*      */         } 
/* 7051 */         return -1;
/*      */       case 2:
/* 7053 */         if ((active1 & 0x180000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 7055 */           this.jjmatchedKind = 142;
/* 7056 */           this.jjmatchedPos = 2;
/* 7057 */           return 106;
/*      */         } 
/* 7059 */         return -1;
/*      */       case 3:
/* 7061 */         if ((active1 & 0x100000000L) != 0L)
/* 7062 */           return 106; 
/* 7063 */         if ((active1 & 0x80000000L) != 0L || (active2 & 0x2000L) != 0L) {
/*      */           
/* 7065 */           this.jjmatchedKind = 142;
/* 7066 */           this.jjmatchedPos = 3;
/* 7067 */           return 106;
/*      */         } 
/* 7069 */         return -1;
/*      */     } 
/* 7071 */     return -1;
/*      */   }
/*      */   
/*      */   private final int jjStartNfa_4(int pos, long active0, long active1, long active2) {
/* 7075 */     return jjMoveNfa_4(jjStopStringLiteralDfa_4(pos, active0, active1, active2), pos + 1);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa0_4() {
/* 7078 */     switch (this.curChar) {
/*      */       
/*      */       case 33:
/* 7081 */         this.jjmatchedKind = 129;
/* 7082 */         return jjMoveStringLiteralDfa1_4(8796093022208L, 0L);
/*      */       case 37:
/* 7084 */         this.jjmatchedKind = 126;
/* 7085 */         return jjMoveStringLiteralDfa1_4(281474976710656L, 0L);
/*      */       case 40:
/* 7087 */         return jjStopAtPos(0, 135);
/*      */       case 41:
/* 7089 */         return jjStopAtPos(0, 136);
/*      */       case 42:
/* 7091 */         this.jjmatchedKind = 122;
/* 7092 */         return jjMoveStringLiteralDfa1_4(576531121047601152L, 0L);
/*      */       case 43:
/* 7094 */         this.jjmatchedKind = 120;
/* 7095 */         return jjMoveStringLiteralDfa1_4(580542139465728L, 0L);
/*      */       case 44:
/* 7097 */         return jjStopAtPos(0, 130);
/*      */       case 45:
/* 7099 */         this.jjmatchedKind = 121;
/* 7100 */         return jjMoveStringLiteralDfa1_4(1161084278931456L, 0L);
/*      */       case 46:
/* 7102 */         this.jjmatchedKind = 99;
/* 7103 */         return jjMoveStringLiteralDfa1_4(1152921848204230656L, 0L);
/*      */       case 47:
/* 7105 */         this.jjmatchedKind = 125;
/* 7106 */         return jjMoveStringLiteralDfa1_4(140737488355328L, 0L);
/*      */       case 58:
/* 7108 */         return jjStopAtPos(0, 132);
/*      */       case 59:
/* 7110 */         return jjStopAtPos(0, 131);
/*      */       case 61:
/* 7112 */         this.jjmatchedKind = 105;
/* 7113 */         return jjMoveStringLiteralDfa1_4(4398046511104L, 0L);
/*      */       case 62:
/* 7115 */         return jjStopAtPos(0, 148);
/*      */       case 63:
/* 7117 */         this.jjmatchedKind = 103;
/* 7118 */         return jjMoveStringLiteralDfa1_4(1099511627776L, 0L);
/*      */       case 91:
/* 7120 */         return jjStartNfaWithStates_4(0, 133, 2);
/*      */       case 93:
/* 7122 */         return jjStopAtPos(0, 134);
/*      */       case 97:
/* 7124 */         return jjMoveStringLiteralDfa1_4(0L, 4096L);
/*      */       case 102:
/* 7126 */         return jjMoveStringLiteralDfa1_4(2147483648L, 0L);
/*      */       case 105:
/* 7128 */         return jjMoveStringLiteralDfa1_4(0L, 2048L);
/*      */       case 116:
/* 7130 */         return jjMoveStringLiteralDfa1_4(4294967296L, 0L);
/*      */       case 117:
/* 7132 */         return jjMoveStringLiteralDfa1_4(0L, 8192L);
/*      */       case 123:
/* 7134 */         return jjStopAtPos(0, 137);
/*      */       case 125:
/* 7136 */         return jjStopAtPos(0, 138);
/*      */     } 
/* 7138 */     return jjMoveNfa_4(1, 0);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa1_4(long active1, long active2) {
/*      */     try {
/* 7142 */       this.curChar = this.input_stream.readChar();
/* 7143 */     } catch (IOException e) {
/* 7144 */       jjStopStringLiteralDfa_4(0, 0L, active1, active2);
/* 7145 */       return 1;
/*      */     } 
/* 7147 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 7150 */         if ((active1 & 0x800000000000000L) != 0L)
/* 7151 */           return jjStopAtPos(1, 123); 
/*      */         break;
/*      */       case 43:
/* 7154 */         if ((active1 & 0x2000000000000L) != 0L)
/* 7155 */           return jjStopAtPos(1, 113); 
/*      */         break;
/*      */       case 45:
/* 7158 */         if ((active1 & 0x4000000000000L) != 0L)
/* 7159 */           return jjStopAtPos(1, 114); 
/*      */         break;
/*      */       case 46:
/* 7162 */         if ((active1 & 0x1000000000L) != 0L) {
/*      */           
/* 7164 */           this.jjmatchedKind = 100;
/* 7165 */           this.jjmatchedPos = 1;
/*      */         } 
/* 7167 */         return jjMoveStringLiteralDfa2_4(active1, 1152921779484753920L, active2, 0L);
/*      */       case 61:
/* 7169 */         if ((active1 & 0x40000000000L) != 0L)
/* 7170 */           return jjStopAtPos(1, 106); 
/* 7171 */         if ((active1 & 0x80000000000L) != 0L)
/* 7172 */           return jjStopAtPos(1, 107); 
/* 7173 */         if ((active1 & 0x100000000000L) != 0L)
/* 7174 */           return jjStopAtPos(1, 108); 
/* 7175 */         if ((active1 & 0x200000000000L) != 0L)
/* 7176 */           return jjStopAtPos(1, 109); 
/* 7177 */         if ((active1 & 0x400000000000L) != 0L)
/* 7178 */           return jjStopAtPos(1, 110); 
/* 7179 */         if ((active1 & 0x800000000000L) != 0L)
/* 7180 */           return jjStopAtPos(1, 111); 
/* 7181 */         if ((active1 & 0x1000000000000L) != 0L)
/* 7182 */           return jjStopAtPos(1, 112); 
/*      */         break;
/*      */       case 63:
/* 7185 */         if ((active1 & 0x10000000000L) != 0L)
/* 7186 */           return jjStopAtPos(1, 104); 
/*      */         break;
/*      */       case 97:
/* 7189 */         return jjMoveStringLiteralDfa2_4(active1, 2147483648L, active2, 0L);
/*      */       case 110:
/* 7191 */         if ((active2 & 0x800L) != 0L)
/* 7192 */           return jjStartNfaWithStates_4(1, 139, 106); 
/*      */         break;
/*      */       case 114:
/* 7195 */         return jjMoveStringLiteralDfa2_4(active1, 4294967296L, active2, 0L);
/*      */       case 115:
/* 7197 */         if ((active2 & 0x1000L) != 0L)
/* 7198 */           return jjStartNfaWithStates_4(1, 140, 106); 
/* 7199 */         return jjMoveStringLiteralDfa2_4(active1, 0L, active2, 8192L);
/*      */     } 
/*      */ 
/*      */     
/* 7203 */     return jjStartNfa_4(0, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa2_4(long old1, long active1, long old2, long active2) {
/* 7206 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 7207 */       return jjStartNfa_4(0, 0L, old1, old2);  try {
/* 7208 */       this.curChar = this.input_stream.readChar();
/* 7209 */     } catch (IOException e) {
/* 7210 */       jjStopStringLiteralDfa_4(1, 0L, active1, active2);
/* 7211 */       return 2;
/*      */     } 
/* 7213 */     switch (this.curChar) {
/*      */       
/*      */       case 42:
/* 7216 */         if ((active1 & 0x4000000000L) != 0L)
/* 7217 */           return jjStopAtPos(2, 102); 
/*      */         break;
/*      */       case 46:
/* 7220 */         if ((active1 & 0x1000000000000000L) != 0L)
/* 7221 */           return jjStopAtPos(2, 124); 
/*      */         break;
/*      */       case 105:
/* 7224 */         return jjMoveStringLiteralDfa3_4(active1, 0L, active2, 8192L);
/*      */       case 108:
/* 7226 */         return jjMoveStringLiteralDfa3_4(active1, 2147483648L, active2, 0L);
/*      */       case 117:
/* 7228 */         return jjMoveStringLiteralDfa3_4(active1, 4294967296L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 7232 */     return jjStartNfa_4(1, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa3_4(long old1, long active1, long old2, long active2) {
/* 7235 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 7236 */       return jjStartNfa_4(1, 0L, old1, old2);  try {
/* 7237 */       this.curChar = this.input_stream.readChar();
/* 7238 */     } catch (IOException e) {
/* 7239 */       jjStopStringLiteralDfa_4(2, 0L, active1, active2);
/* 7240 */       return 3;
/*      */     } 
/* 7242 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 7245 */         if ((active1 & 0x100000000L) != 0L)
/* 7246 */           return jjStartNfaWithStates_4(3, 96, 106); 
/*      */         break;
/*      */       case 110:
/* 7249 */         return jjMoveStringLiteralDfa4_4(active1, 0L, active2, 8192L);
/*      */       case 115:
/* 7251 */         return jjMoveStringLiteralDfa4_4(active1, 2147483648L, active2, 0L);
/*      */     } 
/*      */ 
/*      */     
/* 7255 */     return jjStartNfa_4(2, 0L, active1, active2);
/*      */   }
/*      */   private int jjMoveStringLiteralDfa4_4(long old1, long active1, long old2, long active2) {
/* 7258 */     if (((active1 &= old1) | (active2 &= old2)) == 0L)
/* 7259 */       return jjStartNfa_4(2, 0L, old1, old2);  try {
/* 7260 */       this.curChar = this.input_stream.readChar();
/* 7261 */     } catch (IOException e) {
/* 7262 */       jjStopStringLiteralDfa_4(3, 0L, active1, active2);
/* 7263 */       return 4;
/*      */     } 
/* 7265 */     switch (this.curChar) {
/*      */       
/*      */       case 101:
/* 7268 */         if ((active1 & 0x80000000L) != 0L)
/* 7269 */           return jjStartNfaWithStates_4(4, 95, 106); 
/*      */         break;
/*      */       case 103:
/* 7272 */         if ((active2 & 0x2000L) != 0L) {
/* 7273 */           return jjStartNfaWithStates_4(4, 141, 106);
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 7278 */     return jjStartNfa_4(3, 0L, active1, active2);
/*      */   }
/*      */   
/*      */   private int jjStartNfaWithStates_4(int pos, int kind, int state) {
/* 7282 */     this.jjmatchedKind = kind;
/* 7283 */     this.jjmatchedPos = pos; 
/* 7284 */     try { this.curChar = this.input_stream.readChar(); }
/* 7285 */     catch (IOException e) { return pos + 1; }
/* 7286 */      return jjMoveNfa_4(state, pos + 1);
/*      */   }
/*      */   
/*      */   private int jjMoveNfa_4(int startState, int curPos) {
/* 7290 */     int startsAt = 0;
/* 7291 */     this.jjnewStateCnt = 106;
/* 7292 */     int i = 1;
/* 7293 */     this.jjstateSet[0] = startState;
/* 7294 */     int kind = Integer.MAX_VALUE;
/*      */     
/*      */     while (true) {
/* 7297 */       if (++this.jjround == Integer.MAX_VALUE)
/* 7298 */         ReInitRounds(); 
/* 7299 */       if (this.curChar < 64) {
/*      */         
/* 7301 */         long l = 1L << this.curChar;
/*      */         
/*      */         do {
/* 7304 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 46:
/* 7307 */               if (this.curChar == 62 && kind > 149)
/* 7308 */                 kind = 149; 
/*      */               break;
/*      */             case 56:
/* 7311 */               if (this.curChar == 46)
/* 7312 */                 this.jjstateSet[this.jjnewStateCnt++] = 57; 
/* 7313 */               if (this.curChar == 46)
/* 7314 */                 this.jjstateSet[this.jjnewStateCnt++] = 55; 
/*      */               break;
/*      */             case 49:
/* 7317 */               if (this.curChar == 38) {
/* 7318 */                 this.jjstateSet[this.jjnewStateCnt++] = 52; break;
/* 7319 */               }  if (this.curChar == 62)
/*      */               {
/* 7321 */                 if (kind > 119)
/* 7322 */                   kind = 119; 
/*      */               }
/*      */               break;
/*      */             case 1:
/* 7326 */               if ((0x3FF000000000000L & l) != 0L) {
/*      */                 
/* 7328 */                 if (kind > 97)
/* 7329 */                   kind = 97; 
/* 7330 */                 jjCheckNAddStates(433, 435);
/*      */               }
/* 7332 */               else if ((0x100002600L & l) != 0L) {
/*      */                 
/* 7334 */                 if (kind > 85)
/* 7335 */                   kind = 85; 
/* 7336 */                 jjCheckNAdd(0);
/*      */               }
/* 7338 */               else if (this.curChar == 38) {
/* 7339 */                 jjAddStates(436, 441);
/* 7340 */               } else if (this.curChar == 46) {
/* 7341 */                 jjAddStates(442, 443);
/* 7342 */               } else if (this.curChar == 45) {
/* 7343 */                 jjAddStates(444, 445);
/* 7344 */               } else if (this.curChar == 47) {
/* 7345 */                 jjAddStates(446, 447);
/* 7346 */               } else if (this.curChar == 33) {
/* 7347 */                 jjCheckNAdd(44);
/* 7348 */               } else if (this.curChar == 35) {
/* 7349 */                 jjCheckNAdd(38);
/* 7350 */               } else if (this.curChar == 36) {
/* 7351 */                 jjCheckNAdd(38);
/* 7352 */               } else if (this.curChar == 60) {
/* 7353 */                 jjCheckNAdd(27);
/* 7354 */               } else if (this.curChar == 39) {
/* 7355 */                 jjCheckNAddStates(358, 360);
/* 7356 */               } else if (this.curChar == 34) {
/* 7357 */                 jjCheckNAddStates(361, 363);
/* 7358 */               }  if (this.curChar == 36) {
/*      */                 
/* 7360 */                 if (kind > 142)
/* 7361 */                   kind = 142; 
/* 7362 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 7364 */               else if (this.curChar == 38) {
/*      */                 
/* 7366 */                 if (kind > 127) {
/* 7367 */                   kind = 127;
/*      */                 }
/* 7369 */               } else if (this.curChar == 60) {
/*      */                 
/* 7371 */                 if (kind > 115)
/* 7372 */                   kind = 115; 
/*      */               } 
/* 7374 */               if (this.curChar == 60)
/* 7375 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 2:
/* 7378 */               if ((0xA00000000L & l) != 0L) {
/* 7379 */                 this.jjstateSet[this.jjnewStateCnt++] = 4; break;
/* 7380 */               }  if (this.curChar == 61)
/*      */               {
/* 7382 */                 if (kind > 143)
/* 7383 */                   kind = 143; 
/*      */               }
/*      */               break;
/*      */             case 55:
/* 7387 */               if (this.curChar == 33) {
/*      */                 
/* 7389 */                 if (kind > 101)
/* 7390 */                   kind = 101;  break;
/*      */               } 
/* 7392 */               if (this.curChar == 60)
/*      */               {
/* 7394 */                 if (kind > 101)
/* 7395 */                   kind = 101; 
/*      */               }
/*      */               break;
/*      */             case 34:
/*      */             case 106:
/* 7400 */               if ((0x3FF001000000000L & l) == 0L)
/*      */                 break; 
/* 7402 */               if (kind > 142)
/* 7403 */                 kind = 142; 
/* 7404 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 0:
/* 7407 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 7409 */               if (kind > 85)
/* 7410 */                 kind = 85; 
/* 7411 */               jjCheckNAdd(0);
/*      */               break;
/*      */             case 3:
/* 7414 */               if (this.curChar == 45 && kind > 86)
/* 7415 */                 kind = 86; 
/*      */               break;
/*      */             case 4:
/* 7418 */               if (this.curChar == 45)
/* 7419 */                 this.jjstateSet[this.jjnewStateCnt++] = 3; 
/*      */               break;
/*      */             case 5:
/* 7422 */               if (this.curChar == 34)
/* 7423 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 6:
/* 7426 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 7427 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 9:
/* 7430 */               if ((0x3FF000000000000L & l) != 0L)
/* 7431 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 10:
/* 7434 */               if (this.curChar == 34 && kind > 93)
/* 7435 */                 kind = 93; 
/*      */               break;
/*      */             case 11:
/* 7438 */               if ((0x2000008400000000L & l) != 0L)
/* 7439 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 12:
/* 7442 */               if (this.curChar == 39)
/* 7443 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 13:
/* 7446 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 7447 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 16:
/* 7450 */               if ((0x3FF000000000000L & l) != 0L)
/* 7451 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 17:
/* 7454 */               if (this.curChar == 39 && kind > 93)
/* 7455 */                 kind = 93; 
/*      */               break;
/*      */             case 18:
/* 7458 */               if ((0x2000008400000000L & l) != 0L)
/* 7459 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 20:
/* 7462 */               if (this.curChar == 34)
/* 7463 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 21:
/* 7466 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 7467 */                 jjCheckNAddTwoStates(21, 22); 
/*      */               break;
/*      */             case 22:
/* 7470 */               if (this.curChar == 34 && kind > 94)
/* 7471 */                 kind = 94; 
/*      */               break;
/*      */             case 23:
/* 7474 */               if (this.curChar == 39)
/* 7475 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 24:
/* 7478 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 7479 */                 jjCheckNAddTwoStates(24, 25); 
/*      */               break;
/*      */             case 25:
/* 7482 */               if (this.curChar == 39 && kind > 94)
/* 7483 */                 kind = 94; 
/*      */               break;
/*      */             case 26:
/* 7486 */               if (this.curChar == 60 && kind > 115)
/* 7487 */                 kind = 115; 
/*      */               break;
/*      */             case 27:
/* 7490 */               if (this.curChar == 61 && kind > 116)
/* 7491 */                 kind = 116; 
/*      */               break;
/*      */             case 28:
/* 7494 */               if (this.curChar == 60)
/* 7495 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 29:
/*      */             case 94:
/* 7499 */               if (this.curChar == 38 && kind > 127)
/* 7500 */                 kind = 127; 
/*      */               break;
/*      */             case 33:
/* 7503 */               if (this.curChar != 36)
/*      */                 break; 
/* 7505 */               if (kind > 142)
/* 7506 */                 kind = 142; 
/* 7507 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 36:
/* 7510 */               if ((0x400600800000000L & l) == 0L)
/*      */                 break; 
/* 7512 */               if (kind > 142)
/* 7513 */                 kind = 142; 
/* 7514 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 39:
/* 7517 */               if (this.curChar == 36)
/* 7518 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 40:
/* 7521 */               if (this.curChar == 35)
/* 7522 */                 jjCheckNAdd(38); 
/*      */               break;
/*      */             case 41:
/* 7525 */               if (this.curChar == 61 && kind > 143)
/* 7526 */                 kind = 143; 
/*      */               break;
/*      */             case 43:
/* 7529 */               if (this.curChar == 33)
/* 7530 */                 jjCheckNAdd(44); 
/*      */               break;
/*      */             case 44:
/* 7533 */               if ((0x100002600L & l) == 0L)
/*      */                 break; 
/* 7535 */               if (kind > 153)
/* 7536 */                 kind = 153; 
/* 7537 */               jjCheckNAdd(44);
/*      */               break;
/*      */             case 45:
/* 7540 */               if (this.curChar == 47)
/* 7541 */                 jjAddStates(446, 447); 
/*      */               break;
/*      */             case 48:
/* 7544 */               if (this.curChar == 45)
/* 7545 */                 jjAddStates(444, 445); 
/*      */               break;
/*      */             case 50:
/* 7548 */               if (this.curChar == 59 && kind > 119)
/* 7549 */                 kind = 119; 
/*      */               break;
/*      */             case 53:
/* 7552 */               if (this.curChar == 38)
/* 7553 */                 this.jjstateSet[this.jjnewStateCnt++] = 52; 
/*      */               break;
/*      */             case 54:
/* 7556 */               if (this.curChar == 46)
/* 7557 */                 jjAddStates(442, 443); 
/*      */               break;
/*      */             case 57:
/* 7560 */               if (this.curChar == 33 && kind > 101)
/* 7561 */                 kind = 101; 
/*      */               break;
/*      */             case 58:
/* 7564 */               if (this.curChar == 46)
/* 7565 */                 this.jjstateSet[this.jjnewStateCnt++] = 57; 
/*      */               break;
/*      */             case 59:
/* 7568 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 7570 */               if (kind > 97)
/* 7571 */                 kind = 97; 
/* 7572 */               jjCheckNAddStates(433, 435);
/*      */               break;
/*      */             case 60:
/* 7575 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 7577 */               if (kind > 97)
/* 7578 */                 kind = 97; 
/* 7579 */               jjCheckNAdd(60);
/*      */               break;
/*      */             case 61:
/* 7582 */               if ((0x3FF000000000000L & l) != 0L)
/* 7583 */                 jjCheckNAddTwoStates(61, 62); 
/*      */               break;
/*      */             case 62:
/* 7586 */               if (this.curChar == 46)
/* 7587 */                 jjCheckNAdd(63); 
/*      */               break;
/*      */             case 63:
/* 7590 */               if ((0x3FF000000000000L & l) == 0L)
/*      */                 break; 
/* 7592 */               if (kind > 98)
/* 7593 */                 kind = 98; 
/* 7594 */               jjCheckNAdd(63);
/*      */               break;
/*      */             case 80:
/* 7597 */               if (this.curChar == 38)
/* 7598 */                 jjAddStates(436, 441); 
/*      */               break;
/*      */             case 81:
/* 7601 */               if (this.curChar == 59 && kind > 115)
/* 7602 */                 kind = 115; 
/*      */               break;
/*      */             case 84:
/* 7605 */               if (this.curChar == 59)
/* 7606 */                 jjCheckNAdd(27); 
/*      */               break;
/*      */             case 87:
/* 7609 */               if (this.curChar == 59 && kind > 117)
/* 7610 */                 kind = 117; 
/*      */               break;
/*      */             case 90:
/* 7613 */               if (this.curChar == 61 && kind > 118)
/* 7614 */                 kind = 118; 
/*      */               break;
/*      */             case 91:
/* 7617 */               if (this.curChar == 59)
/* 7618 */                 this.jjstateSet[this.jjnewStateCnt++] = 90; 
/*      */               break;
/*      */             case 95:
/* 7621 */               if (this.curChar == 59 && kind > 127)
/* 7622 */                 kind = 127; 
/*      */               break;
/*      */             case 99:
/* 7625 */               if (this.curChar == 38)
/* 7626 */                 this.jjstateSet[this.jjnewStateCnt++] = 98; 
/*      */               break;
/*      */             case 100:
/* 7629 */               if (this.curChar == 59) {
/* 7630 */                 this.jjstateSet[this.jjnewStateCnt++] = 99;
/*      */               }
/*      */               break;
/*      */           } 
/* 7634 */         } while (i != startsAt);
/*      */       }
/* 7636 */       else if (this.curChar < 128) {
/*      */         
/* 7638 */         long l = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 7641 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 46:
/* 7644 */               if (this.curChar == 93 && kind > 149)
/* 7645 */                 kind = 149; 
/*      */               break;
/*      */             case 1:
/* 7648 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 7650 */                 if (kind > 142)
/* 7651 */                   kind = 142; 
/* 7652 */                 jjCheckNAddTwoStates(34, 35);
/*      */               }
/* 7654 */               else if (this.curChar == 92) {
/* 7655 */                 jjAddStates(448, 452);
/* 7656 */               } else if (this.curChar == 91) {
/* 7657 */                 this.jjstateSet[this.jjnewStateCnt++] = 41;
/* 7658 */               } else if (this.curChar == 124) {
/* 7659 */                 this.jjstateSet[this.jjnewStateCnt++] = 31;
/* 7660 */               }  if (this.curChar == 103) {
/* 7661 */                 jjCheckNAddTwoStates(72, 105); break;
/* 7662 */               }  if (this.curChar == 108) {
/* 7663 */                 jjCheckNAddTwoStates(65, 67); break;
/* 7664 */               }  if (this.curChar == 92) {
/* 7665 */                 jjCheckNAdd(36); break;
/* 7666 */               }  if (this.curChar == 124) {
/*      */                 
/* 7668 */                 if (kind > 128)
/* 7669 */                   kind = 128;  break;
/*      */               } 
/* 7671 */               if (this.curChar == 114) {
/* 7672 */                 jjAddStates(369, 370); break;
/* 7673 */               }  if (this.curChar == 91)
/* 7674 */                 this.jjstateSet[this.jjnewStateCnt++] = 2; 
/*      */               break;
/*      */             case 106:
/* 7677 */               if ((0x7FFFFFE87FFFFFFL & l) != 0L) {
/*      */                 
/* 7679 */                 if (kind > 142)
/* 7680 */                   kind = 142; 
/* 7681 */                 jjCheckNAddTwoStates(34, 35); break;
/*      */               } 
/* 7683 */               if (this.curChar == 92)
/* 7684 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 6:
/* 7687 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 7688 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 7:
/* 7691 */               if (this.curChar == 92)
/* 7692 */                 jjAddStates(371, 372); 
/*      */               break;
/*      */             case 8:
/* 7695 */               if (this.curChar == 120)
/* 7696 */                 this.jjstateSet[this.jjnewStateCnt++] = 9; 
/*      */               break;
/*      */             case 9:
/* 7699 */               if ((0x7E0000007EL & l) != 0L)
/* 7700 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 11:
/* 7703 */               if ((0x81450C610000000L & l) != 0L)
/* 7704 */                 jjCheckNAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 7707 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 7708 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 14:
/* 7711 */               if (this.curChar == 92)
/* 7712 */                 jjAddStates(373, 374); 
/*      */               break;
/*      */             case 15:
/* 7715 */               if (this.curChar == 120)
/* 7716 */                 this.jjstateSet[this.jjnewStateCnt++] = 16; 
/*      */               break;
/*      */             case 16:
/* 7719 */               if ((0x7E0000007EL & l) != 0L)
/* 7720 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 18:
/* 7723 */               if ((0x81450C610000000L & l) != 0L)
/* 7724 */                 jjCheckNAddStates(358, 360); 
/*      */               break;
/*      */             case 19:
/* 7727 */               if (this.curChar == 114)
/* 7728 */                 jjAddStates(369, 370); 
/*      */               break;
/*      */             case 21:
/* 7731 */               jjAddStates(375, 376);
/*      */               break;
/*      */             case 24:
/* 7734 */               jjAddStates(377, 378);
/*      */               break;
/*      */             case 30:
/*      */             case 31:
/* 7738 */               if (this.curChar == 124 && kind > 128)
/* 7739 */                 kind = 128; 
/*      */               break;
/*      */             case 32:
/* 7742 */               if (this.curChar == 124)
/* 7743 */                 this.jjstateSet[this.jjnewStateCnt++] = 31; 
/*      */               break;
/*      */             case 33:
/* 7746 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 7748 */               if (kind > 142)
/* 7749 */                 kind = 142; 
/* 7750 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/* 7753 */               if ((0x7FFFFFE87FFFFFFL & l) == 0L)
/*      */                 break; 
/* 7755 */               if (kind > 142)
/* 7756 */                 kind = 142; 
/* 7757 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 35:
/* 7760 */               if (this.curChar == 92)
/* 7761 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 37:
/* 7764 */               if (this.curChar == 92)
/* 7765 */                 jjCheckNAdd(36); 
/*      */               break;
/*      */             case 38:
/* 7768 */               if (this.curChar == 123 && kind > 143)
/* 7769 */                 kind = 143; 
/*      */               break;
/*      */             case 42:
/* 7772 */               if (this.curChar == 91)
/* 7773 */                 this.jjstateSet[this.jjnewStateCnt++] = 41; 
/*      */               break;
/*      */             case 51:
/* 7776 */               if (this.curChar == 116)
/* 7777 */                 this.jjstateSet[this.jjnewStateCnt++] = 50; 
/*      */               break;
/*      */             case 52:
/* 7780 */               if (this.curChar == 103)
/* 7781 */                 this.jjstateSet[this.jjnewStateCnt++] = 51; 
/*      */               break;
/*      */             case 64:
/* 7784 */               if (this.curChar == 108)
/* 7785 */                 jjCheckNAddTwoStates(65, 67); 
/*      */               break;
/*      */             case 65:
/* 7788 */               if (this.curChar == 116 && kind > 115)
/* 7789 */                 kind = 115; 
/*      */               break;
/*      */             case 66:
/* 7792 */               if (this.curChar == 101 && kind > 116)
/* 7793 */                 kind = 116; 
/*      */               break;
/*      */             case 67:
/*      */             case 70:
/* 7797 */               if (this.curChar == 116)
/* 7798 */                 jjCheckNAdd(66); 
/*      */               break;
/*      */             case 68:
/* 7801 */               if (this.curChar == 92)
/* 7802 */                 jjAddStates(448, 452); 
/*      */               break;
/*      */             case 69:
/* 7805 */               if (this.curChar == 108)
/* 7806 */                 jjCheckNAdd(65); 
/*      */               break;
/*      */             case 71:
/* 7809 */               if (this.curChar == 108)
/* 7810 */                 this.jjstateSet[this.jjnewStateCnt++] = 70; 
/*      */               break;
/*      */             case 72:
/* 7813 */               if (this.curChar == 116 && kind > 117)
/* 7814 */                 kind = 117; 
/*      */               break;
/*      */             case 73:
/* 7817 */               if (this.curChar == 103)
/* 7818 */                 jjCheckNAdd(72); 
/*      */               break;
/*      */             case 74:
/* 7821 */               if (this.curChar == 101 && kind > 118)
/* 7822 */                 kind = 118; 
/*      */               break;
/*      */             case 75:
/*      */             case 105:
/* 7826 */               if (this.curChar == 116)
/* 7827 */                 jjCheckNAdd(74); 
/*      */               break;
/*      */             case 76:
/* 7830 */               if (this.curChar == 103)
/* 7831 */                 this.jjstateSet[this.jjnewStateCnt++] = 75; 
/*      */               break;
/*      */             case 77:
/* 7834 */               if (this.curChar == 100 && kind > 127)
/* 7835 */                 kind = 127; 
/*      */               break;
/*      */             case 78:
/* 7838 */               if (this.curChar == 110)
/* 7839 */                 this.jjstateSet[this.jjnewStateCnt++] = 77; 
/*      */               break;
/*      */             case 79:
/* 7842 */               if (this.curChar == 97)
/* 7843 */                 this.jjstateSet[this.jjnewStateCnt++] = 78; 
/*      */               break;
/*      */             case 82:
/* 7846 */               if (this.curChar == 116)
/* 7847 */                 this.jjstateSet[this.jjnewStateCnt++] = 81; 
/*      */               break;
/*      */             case 83:
/* 7850 */               if (this.curChar == 108)
/* 7851 */                 this.jjstateSet[this.jjnewStateCnt++] = 82; 
/*      */               break;
/*      */             case 85:
/* 7854 */               if (this.curChar == 116)
/* 7855 */                 this.jjstateSet[this.jjnewStateCnt++] = 84; 
/*      */               break;
/*      */             case 86:
/* 7858 */               if (this.curChar == 108)
/* 7859 */                 this.jjstateSet[this.jjnewStateCnt++] = 85; 
/*      */               break;
/*      */             case 88:
/* 7862 */               if (this.curChar == 116)
/* 7863 */                 this.jjstateSet[this.jjnewStateCnt++] = 87; 
/*      */               break;
/*      */             case 89:
/* 7866 */               if (this.curChar == 103)
/* 7867 */                 this.jjstateSet[this.jjnewStateCnt++] = 88; 
/*      */               break;
/*      */             case 92:
/* 7870 */               if (this.curChar == 116)
/* 7871 */                 this.jjstateSet[this.jjnewStateCnt++] = 91; 
/*      */               break;
/*      */             case 93:
/* 7874 */               if (this.curChar == 103)
/* 7875 */                 this.jjstateSet[this.jjnewStateCnt++] = 92; 
/*      */               break;
/*      */             case 96:
/* 7878 */               if (this.curChar == 112)
/* 7879 */                 this.jjstateSet[this.jjnewStateCnt++] = 95; 
/*      */               break;
/*      */             case 97:
/* 7882 */               if (this.curChar == 109)
/* 7883 */                 this.jjstateSet[this.jjnewStateCnt++] = 96; 
/*      */               break;
/*      */             case 98:
/* 7886 */               if (this.curChar == 97)
/* 7887 */                 this.jjstateSet[this.jjnewStateCnt++] = 97; 
/*      */               break;
/*      */             case 101:
/* 7890 */               if (this.curChar == 112)
/* 7891 */                 this.jjstateSet[this.jjnewStateCnt++] = 100; 
/*      */               break;
/*      */             case 102:
/* 7894 */               if (this.curChar == 109)
/* 7895 */                 this.jjstateSet[this.jjnewStateCnt++] = 101; 
/*      */               break;
/*      */             case 103:
/* 7898 */               if (this.curChar == 97)
/* 7899 */                 this.jjstateSet[this.jjnewStateCnt++] = 102; 
/*      */               break;
/*      */             case 104:
/* 7902 */               if (this.curChar == 103) {
/* 7903 */                 jjCheckNAddTwoStates(72, 105);
/*      */               }
/*      */               break;
/*      */           } 
/* 7907 */         } while (i != startsAt);
/*      */       }
/*      */       else {
/*      */         
/* 7911 */         int hiByte = this.curChar >> 8;
/* 7912 */         int i1 = hiByte >> 6;
/* 7913 */         long l1 = 1L << (hiByte & 0x3F);
/* 7914 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 7915 */         long l2 = 1L << (this.curChar & 0x3F);
/*      */         
/*      */         do {
/* 7918 */           switch (this.jjstateSet[--i]) {
/*      */             
/*      */             case 1:
/* 7921 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 7923 */               if (kind > 142)
/* 7924 */                 kind = 142; 
/* 7925 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 34:
/*      */             case 106:
/* 7929 */               if (!jjCanMove_1(hiByte, i1, i2, l1, l2))
/*      */                 break; 
/* 7931 */               if (kind > 142)
/* 7932 */                 kind = 142; 
/* 7933 */               jjCheckNAddTwoStates(34, 35);
/*      */               break;
/*      */             case 6:
/* 7936 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 7937 */                 jjAddStates(361, 363); 
/*      */               break;
/*      */             case 13:
/* 7940 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 7941 */                 jjAddStates(358, 360); 
/*      */               break;
/*      */             case 21:
/* 7944 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 7945 */                 jjAddStates(375, 376); 
/*      */               break;
/*      */             case 24:
/* 7948 */               if (jjCanMove_0(hiByte, i1, i2, l1, l2))
/* 7949 */                 jjAddStates(377, 378);  break;
/*      */             default:
/* 7951 */               if (i1 == 0 || l1 == 0L || i2 == 0 || l2 == 0L); break;
/*      */           } 
/* 7953 */         } while (i != startsAt);
/*      */       } 
/* 7955 */       if (kind != Integer.MAX_VALUE) {
/*      */         
/* 7957 */         this.jjmatchedKind = kind;
/* 7958 */         this.jjmatchedPos = curPos;
/* 7959 */         kind = Integer.MAX_VALUE;
/*      */       } 
/* 7961 */       curPos++;
/* 7962 */       if ((i = this.jjnewStateCnt) == (startsAt = 106 - (this.jjnewStateCnt = startsAt)))
/* 7963 */         return curPos;  
/* 7964 */       try { this.curChar = this.input_stream.readChar(); }
/* 7965 */       catch (IOException e) { return curPos; }
/*      */     
/*      */     } 
/* 7968 */   } static final int[] jjnextStates = new int[] { 10, 12, 4, 5, 3, 4, 5, 697, 712, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 398, 404, 405, 413, 414, 423, 424, 431, 432, 443, 444, 455, 456, 467, 468, 477, 478, 488, 489, 499, 500, 512, 513, 522, 523, 539, 540, 551, 552, 570, 571, 583, 584, 597, 598, 608, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 626, 636, 637, 638, 650, 651, 656, 662, 663, 665, 12, 21, 24, 31, 36, 45, 50, 58, 65, 70, 77, 84, 90, 98, 105, 114, 120, 130, 136, 141, 148, 153, 161, 174, 183, 199, 209, 218, 227, 234, 242, 253, 262, 269, 277, 278, 286, 291, 296, 305, 314, 321, 331, 339, 350, 357, 367, 5, 6, 14, 15, 38, 41, 47, 48, 178, 179, 187, 188, 201, 202, 211, 212, 222, 223, 229, 230, 231, 236, 237, 238, 244, 245, 246, 255, 256, 257, 264, 265, 266, 271, 272, 273, 279, 280, 281, 283, 284, 285, 288, 289, 290, 293, 294, 295, 298, 299, 307, 308, 309, 323, 324, 325, 341, 342, 343, 361, 362, 400, 401, 407, 408, 416, 417, 426, 427, 434, 435, 446, 447, 460, 461, 470, 471, 480, 481, 491, 492, 502, 503, 515, 516, 527, 528, 544, 545, 556, 557, 573, 574, 586, 587, 600, 601, 628, 629, 642, 643, 700, 701, 703, 708, 709, 704, 710, 703, 705, 706, 708, 709, 369, 370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390, 391, 392, 393, 394, 395, 396, 397, 667, 668, 669, 670, 671, 672, 673, 674, 675, 676, 677, 678, 679, 680, 681, 682, 683, 684, 609, 610, 611, 612, 613, 614, 615, 616, 617, 618, 619, 620, 621, 622, 623, 624, 625, 685, 637, 686, 651, 689, 692, 663, 693, 193, 198, 562, 567, 658, 659, 699, 711, 708, 709, 58, 59, 60, 81, 84, 87, 91, 92, 101, 54, 56, 47, 51, 44, 45, 13, 14, 17, 6, 7, 10, 67, 69, 71, 74, 77, 20, 23, 8, 11, 15, 18, 21, 22, 24, 25, 55, 56, 57, 78, 81, 84, 88, 89, 98, 51, 53, 44, 48, 64, 66, 68, 71, 74, 3, 5, 54, 55, 56, 77, 80, 83, 87, 88, 97, 50, 52, 43, 47, 40, 41, 8, 9, 12, 1, 2, 5, 63, 65, 67, 70, 73, 3, 6, 10, 13, 16, 17, 19, 20, 60, 61, 62, 83, 86, 89, 93, 94, 103, 56, 58, 49, 53, 46, 47, 69, 71, 73, 76, 79 };
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
/*      */   private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
/* 8001 */     switch (hiByte) {
/*      */       
/*      */       case 0:
/* 8004 */         return ((jjbitVec2[i2] & l2) != 0L);
/*      */     } 
/* 8006 */     if ((jjbitVec0[i1] & l1) != 0L)
/* 8007 */       return true; 
/* 8008 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2) {
/* 8013 */     switch (hiByte) {
/*      */       
/*      */       case 0:
/* 8016 */         return ((jjbitVec4[i2] & l2) != 0L);
/*      */       case 32:
/* 8018 */         return ((jjbitVec5[i2] & l2) != 0L);
/*      */       case 33:
/* 8020 */         return ((jjbitVec6[i2] & l2) != 0L);
/*      */       case 44:
/* 8022 */         return ((jjbitVec7[i2] & l2) != 0L);
/*      */       case 45:
/* 8024 */         return ((jjbitVec8[i2] & l2) != 0L);
/*      */       case 46:
/* 8026 */         return ((jjbitVec9[i2] & l2) != 0L);
/*      */       case 48:
/* 8028 */         return ((jjbitVec10[i2] & l2) != 0L);
/*      */       case 49:
/* 8030 */         return ((jjbitVec11[i2] & l2) != 0L);
/*      */       case 51:
/* 8032 */         return ((jjbitVec12[i2] & l2) != 0L);
/*      */       case 77:
/* 8034 */         return ((jjbitVec13[i2] & l2) != 0L);
/*      */       case 164:
/* 8036 */         return ((jjbitVec14[i2] & l2) != 0L);
/*      */       case 166:
/* 8038 */         return ((jjbitVec15[i2] & l2) != 0L);
/*      */       case 167:
/* 8040 */         return ((jjbitVec16[i2] & l2) != 0L);
/*      */       case 168:
/* 8042 */         return ((jjbitVec17[i2] & l2) != 0L);
/*      */       case 169:
/* 8044 */         return ((jjbitVec18[i2] & l2) != 0L);
/*      */       case 170:
/* 8046 */         return ((jjbitVec19[i2] & l2) != 0L);
/*      */       case 171:
/* 8048 */         return ((jjbitVec20[i2] & l2) != 0L);
/*      */       case 215:
/* 8050 */         return ((jjbitVec21[i2] & l2) != 0L);
/*      */       case 251:
/* 8052 */         return ((jjbitVec22[i2] & l2) != 0L);
/*      */       case 253:
/* 8054 */         return ((jjbitVec23[i2] & l2) != 0L);
/*      */       case 254:
/* 8056 */         return ((jjbitVec24[i2] & l2) != 0L);
/*      */       case 255:
/* 8058 */         return ((jjbitVec25[i2] & l2) != 0L);
/*      */     } 
/* 8060 */     if ((jjbitVec3[i1] & l1) != 0L)
/* 8061 */       return true; 
/* 8062 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/* 8067 */   public static final String[] jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "${", "#{", "[=", null, null, null, null, null, null, null, null, null, null, "false", "true", null, null, ".", "..", null, "..*", "?", "??", "=", "==", "!=", "+=", "-=", "*=", "/=", "%=", "++", "--", null, null, null, null, null, "+", "-", "*", "**", "...", "/", "%", null, null, "!", ",", ";", ":", "[", "]", "(", ")", "{", "}", "in", "as", "using", null, null, null, null, null, null, ">", null, ">", ">=", null, null, null, null, null, null };
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
/*      */   protected Token jjFillToken() {
/* 8090 */     String im = jjstrLiteralImages[this.jjmatchedKind];
/* 8091 */     String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
/* 8092 */     int beginLine = this.input_stream.getBeginLine();
/* 8093 */     int beginColumn = this.input_stream.getBeginColumn();
/* 8094 */     int endLine = this.input_stream.getEndLine();
/* 8095 */     int endColumn = this.input_stream.getEndColumn();
/* 8096 */     Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
/*      */     
/* 8098 */     t.beginLine = beginLine;
/* 8099 */     t.endLine = endLine;
/* 8100 */     t.beginColumn = beginColumn;
/* 8101 */     t.endColumn = endColumn;
/*      */     
/* 8103 */     return t;
/*      */   }
/*      */   
/* 8106 */   int curLexState = 0;
/* 8107 */   int defaultLexState = 0;
/*      */   
/*      */   int jjnewStateCnt;
/*      */   
/*      */   int jjround;
/*      */   
/*      */   int jjmatchedPos;
/*      */   int jjmatchedKind;
/*      */   
/*      */   public Token getNextToken() {
/* 8117 */     int curPos = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/*      */       try {
/* 8124 */         this.curChar = this.input_stream.BeginToken();
/*      */       }
/* 8126 */       catch (Exception e) {
/*      */         
/* 8128 */         this.jjmatchedKind = 0;
/* 8129 */         this.jjmatchedPos = -1;
/* 8130 */         Token matchedToken = jjFillToken();
/* 8131 */         return matchedToken;
/*      */       } 
/* 8133 */       this.image = this.jjimage;
/* 8134 */       this.image.setLength(0);
/* 8135 */       this.jjimageLen = 0;
/*      */       
/* 8137 */       switch (this.curLexState) {
/*      */         
/*      */         case 0:
/* 8140 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8141 */           this.jjmatchedPos = 0;
/* 8142 */           curPos = jjMoveStringLiteralDfa0_0();
/*      */           break;
/*      */         case 1:
/* 8145 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8146 */           this.jjmatchedPos = 0;
/* 8147 */           curPos = jjMoveStringLiteralDfa0_1();
/*      */           break;
/*      */         case 2:
/* 8150 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8151 */           this.jjmatchedPos = 0;
/* 8152 */           curPos = jjMoveStringLiteralDfa0_2();
/*      */           break;
/*      */         case 3:
/* 8155 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8156 */           this.jjmatchedPos = 0;
/* 8157 */           curPos = jjMoveStringLiteralDfa0_3();
/*      */           break;
/*      */         case 4:
/* 8160 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8161 */           this.jjmatchedPos = 0;
/* 8162 */           curPos = jjMoveStringLiteralDfa0_4(); break;
/*      */         case 5:
/*      */           
/* 8165 */           try { this.input_stream.backup(0);
/* 8166 */             while ((this.curChar < 64 && (0x4000000000000000L & 1L << this.curChar) != 0L) || (this.curChar >> 6 == 1 && (0x20000000L & 1L << (this.curChar & 0x3F)) != 0L))
/*      */             {
/* 8168 */               this.curChar = this.input_stream.BeginToken();
/*      */             } }
/* 8170 */           catch (IOException e1) { continue; }
/* 8171 */            this.jjmatchedKind = Integer.MAX_VALUE;
/* 8172 */           this.jjmatchedPos = 0;
/* 8173 */           curPos = jjMoveStringLiteralDfa0_5();
/*      */           break;
/*      */         case 6:
/* 8176 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8177 */           this.jjmatchedPos = 0;
/* 8178 */           curPos = jjMoveStringLiteralDfa0_6();
/*      */           break;
/*      */         case 7:
/* 8181 */           this.jjmatchedKind = Integer.MAX_VALUE;
/* 8182 */           this.jjmatchedPos = 0;
/* 8183 */           curPos = jjMoveStringLiteralDfa0_7();
/*      */           break;
/*      */       } 
/* 8186 */       if (this.jjmatchedKind != Integer.MAX_VALUE) {
/*      */         
/* 8188 */         if (this.jjmatchedPos + 1 < curPos)
/* 8189 */           this.input_stream.backup(curPos - this.jjmatchedPos - 1); 
/* 8190 */         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
/*      */           
/* 8192 */           Token matchedToken = jjFillToken();
/* 8193 */           TokenLexicalActions(matchedToken);
/* 8194 */           if (jjnewLexState[this.jjmatchedKind] != -1)
/* 8195 */             this.curLexState = jjnewLexState[this.jjmatchedKind]; 
/* 8196 */           return matchedToken;
/*      */         } 
/*      */ 
/*      */         
/* 8200 */         SkipLexicalActions(null);
/* 8201 */         if (jjnewLexState[this.jjmatchedKind] != -1)
/* 8202 */           this.curLexState = jjnewLexState[this.jjmatchedKind];  continue;
/*      */       } 
/*      */       break;
/*      */     } 
/* 8206 */     int error_line = this.input_stream.getEndLine();
/* 8207 */     int error_column = this.input_stream.getEndColumn();
/* 8208 */     String error_after = null;
/* 8209 */     boolean EOFSeen = false; try {
/* 8210 */       this.input_stream.readChar(); this.input_stream.backup(1);
/* 8211 */     } catch (IOException e1) {
/* 8212 */       EOFSeen = true;
/* 8213 */       error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/* 8214 */       if (this.curChar == 10 || this.curChar == 13) {
/* 8215 */         error_line++;
/* 8216 */         error_column = 0;
/*      */       } else {
/*      */         
/* 8219 */         error_column++;
/*      */       } 
/* 8221 */     }  if (!EOFSeen) {
/* 8222 */       this.input_stream.backup(1);
/* 8223 */       error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/*      */     } 
/* 8225 */     throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   void SkipLexicalActions(Token matchedToken) {
/* 8231 */     switch (this.jjmatchedKind) {
/*      */       
/*      */       case 91:
/* 8234 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8235 */         if (this.parenthesisNesting > 0) { SwitchTo(3); break; }
/* 8236 */          if (this.inInvocation) { SwitchTo(4); break; }
/* 8237 */          SwitchTo(2);
/*      */         break;
/*      */     } 
/*      */   } void TokenLexicalActions(Token matchedToken) {
/*      */     int tagNamingConvention;
/*      */     char firstChar;
/*      */     String s;
/*      */     StringTokenizer st;
/* 8245 */     switch (this.jjmatchedKind) {
/*      */       
/*      */       case 6:
/* 8248 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8249 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 7:
/* 8252 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8253 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 8:
/* 8256 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8257 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 9:
/* 8260 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8261 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 2);
/*      */         break;
/*      */       case 10:
/* 8264 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8265 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 11:
/* 8268 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8269 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 13:
/* 8272 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8273 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 3), 2);
/*      */         break;
/*      */       case 14:
/* 8276 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8277 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 15:
/* 8280 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8281 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 16:
/* 8284 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8285 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 17:
/* 8288 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8289 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 18:
/* 8292 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8293 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 19:
/* 8296 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8297 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 20:
/* 8300 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8301 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 21:
/* 8304 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8305 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 22:
/* 8308 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8309 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 23:
/* 8312 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8313 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 24:
/* 8316 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8317 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 25:
/* 8320 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8321 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 26:
/* 8324 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8325 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 27:
/* 8328 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8329 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 28:
/* 8332 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8333 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 29:
/* 8336 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8337 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 6), 2);
/*      */         break;
/*      */       case 30:
/* 8340 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8341 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 0);
/*      */         break;
/*      */       case 31:
/* 8344 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8345 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
/*      */         break;
/*      */       case 32:
/* 8348 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8349 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 33:
/* 8352 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8353 */         handleTagSyntaxAndSwitch(matchedToken, 7); this.noparseTag = "comment";
/*      */         break;
/*      */       case 34:
/* 8356 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8357 */         this.noparseTag = "-->"; handleTagSyntaxAndSwitch(matchedToken, 7);
/*      */         break;
/*      */       case 35:
/* 8360 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8361 */         tagNamingConvention = getTagNamingConvention(matchedToken, 2);
/* 8362 */         handleTagSyntaxAndSwitch(matchedToken, tagNamingConvention, 7);
/* 8363 */         this.noparseTag = (tagNamingConvention == 12) ? "noParse" : "noparse";
/*      */         break;
/*      */       case 36:
/* 8366 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8367 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 37:
/* 8370 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8371 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 38:
/* 8374 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8375 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 39:
/* 8378 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8379 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 40:
/* 8382 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8383 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 41:
/* 8386 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8387 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 42:
/* 8390 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8391 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 3), 0);
/*      */         break;
/*      */       case 43:
/* 8394 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8395 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 44:
/* 8398 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8399 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 45:
/* 8402 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8403 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 46:
/* 8406 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8407 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 47:
/* 8410 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8411 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 48:
/* 8414 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8415 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 6), 0);
/*      */         break;
/*      */       case 49:
/* 8418 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8419 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 4), 0);
/*      */         break;
/*      */       case 50:
/* 8422 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8423 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
/*      */         break;
/*      */       case 51:
/* 8426 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8427 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 52:
/* 8430 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8431 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 53:
/* 8434 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8435 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 54:
/* 8438 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8439 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 55:
/* 8442 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8443 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 56:
/* 8446 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8447 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 57:
/* 8450 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8451 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 58:
/* 8454 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8455 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 59:
/* 8458 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8459 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 60:
/* 8462 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8463 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 61:
/* 8466 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8467 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 62:
/* 8470 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8471 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 63:
/* 8474 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8475 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 64:
/* 8478 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8479 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 65:
/* 8482 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8483 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 66:
/* 8486 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8487 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 67:
/* 8490 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8491 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 68:
/* 8494 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8495 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 69:
/* 8498 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8499 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 70:
/* 8502 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8503 */         handleTagSyntaxAndSwitch(matchedToken, 2);
/*      */         break;
/*      */       case 71:
/* 8506 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8507 */         handleTagSyntaxAndSwitch(matchedToken, 0);
/*      */         break;
/*      */       case 72:
/* 8510 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8511 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
/*      */         break;
/*      */       case 73:
/* 8514 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8515 */         handleTagSyntaxAndSwitch(matchedToken, getTagNamingConvention(matchedToken, 2), 0);
/*      */         break;
/*      */       case 74:
/* 8518 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8519 */         unifiedCall(matchedToken);
/*      */         break;
/*      */       case 75:
/* 8522 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8523 */         unifiedCallEnd(matchedToken);
/*      */         break;
/*      */       case 76:
/* 8526 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8527 */         ftlHeader(matchedToken);
/*      */         break;
/*      */       case 77:
/* 8530 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8531 */         ftlHeader(matchedToken);
/*      */         break;
/*      */       case 78:
/* 8534 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8535 */         if (!this.tagSyntaxEstablished && this.incompatibleImprovements < _TemplateAPI.VERSION_INT_2_3_19) {
/* 8536 */           matchedToken.kind = 80; break;
/*      */         } 
/* 8538 */         firstChar = matchedToken.image.charAt(0);
/*      */         
/* 8540 */         if (!this.tagSyntaxEstablished && this.autodetectTagSyntax) {
/* 8541 */           this.squBracTagSyntax = (firstChar == '[');
/* 8542 */           this.tagSyntaxEstablished = true;
/*      */         } 
/*      */         
/* 8545 */         if (firstChar == '<' && this.squBracTagSyntax) {
/* 8546 */           matchedToken.kind = 80; break;
/* 8547 */         }  if (firstChar == '[' && !this.squBracTagSyntax) {
/* 8548 */           matchedToken.kind = 80; break;
/* 8549 */         }  if (this.strictSyntaxMode) {
/* 8550 */           String dn = matchedToken.image;
/* 8551 */           int index = dn.indexOf('#');
/* 8552 */           dn = dn.substring(index + 1);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 8558 */           if (_CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES.contains(dn)) {
/* 8559 */             throw new TokenMgrError("#" + dn + " is an existing directive, but the tag is malformed.  (See FreeMarker Manual / Directive Reference.)", 0, matchedToken.beginLine, matchedToken.beginColumn + 1, matchedToken.endLine, matchedToken.endColumn);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 8567 */           String tip = null;
/* 8568 */           if (dn.equals("set") || dn.equals("var")) {
/* 8569 */             tip = "Use #assign or #local or #global, depending on the intented scope (#assign is template-scope). (If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
/*      */           }
/* 8571 */           else if (dn.equals("else_if") || dn.equals("elif")) {
/* 8572 */             tip = "Use #elseif.";
/* 8573 */           } else if (dn.equals("no_escape")) {
/* 8574 */             tip = "Use #noescape instead.";
/* 8575 */           } else if (dn.equals("method")) {
/* 8576 */             tip = "Use #function instead.";
/* 8577 */           } else if (dn.equals("head") || dn.equals("template") || dn.equals("fm")) {
/* 8578 */             tip = "You may meant #ftl.";
/* 8579 */           } else if (dn.equals("try") || dn.equals("atempt")) {
/* 8580 */             tip = "You may meant #attempt.";
/* 8581 */           } else if (dn.equals("for") || dn.equals("each") || dn.equals("iterate") || dn.equals("iterator")) {
/* 8582 */             tip = "You may meant #list (http://freemarker.org/docs/ref_directive_list.html).";
/* 8583 */           } else if (dn.equals("prefix")) {
/* 8584 */             tip = "You may meant #import. (If you have seen this directive in use elsewhere, this was a planned directive, so maybe you need to upgrade FreeMarker.)";
/* 8585 */           } else if (dn.equals("item") || dn.equals("row") || dn.equals("rows")) {
/* 8586 */             tip = "You may meant #items.";
/* 8587 */           } else if (dn.equals("separator") || dn.equals("separate") || dn.equals("separ")) {
/* 8588 */             tip = "You may meant #sep.";
/*      */           } else {
/*      */             
/* 8591 */             tip = "Help (latest version): http://freemarker.org/docs/ref_directive_alphaidx.html; you're using FreeMarker " + Configuration.getVersion() + ".";
/*      */           } 
/* 8593 */           throw new TokenMgrError("Unknown directive: #" + dn + ((tip != null) ? (". " + tip) : ""), 0, matchedToken.beginLine, matchedToken.beginColumn + 1, matchedToken.endLine, matchedToken.endColumn);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 82:
/* 8602 */         this.image.append(jjstrLiteralImages[82]);
/* 8603 */         this.lengthOfMatch = jjstrLiteralImages[82].length();
/* 8604 */         startInterpolation(matchedToken);
/*      */         break;
/*      */       case 83:
/* 8607 */         this.image.append(jjstrLiteralImages[83]);
/* 8608 */         this.lengthOfMatch = jjstrLiteralImages[83].length();
/* 8609 */         startInterpolation(matchedToken);
/*      */         break;
/*      */       case 84:
/* 8612 */         this.image.append(jjstrLiteralImages[84]);
/* 8613 */         this.lengthOfMatch = jjstrLiteralImages[84].length();
/* 8614 */         startInterpolation(matchedToken);
/*      */         break;
/*      */       case 133:
/* 8617 */         this.image.append(jjstrLiteralImages[133]);
/* 8618 */         this.lengthOfMatch = jjstrLiteralImages[133].length();
/* 8619 */         this.bracketNesting++;
/*      */         break;
/*      */       case 134:
/* 8622 */         this.image.append(jjstrLiteralImages[134]);
/* 8623 */         this.lengthOfMatch = jjstrLiteralImages[134].length();
/* 8624 */         if (this.bracketNesting > 0) {
/* 8625 */           this.bracketNesting--; break;
/* 8626 */         }  if (this.interpolationSyntax == 22 && this.postInterpolationLexState != -1) {
/* 8627 */           endInterpolation(matchedToken);
/*      */           
/*      */           break;
/*      */         } 
/* 8631 */         if ((!this.squBracTagSyntax && (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22)) || this.postInterpolationLexState != -1)
/*      */         {
/*      */ 
/*      */           
/* 8635 */           throw newUnexpectedClosingTokenException(matchedToken);
/*      */         }
/*      */ 
/*      */         
/* 8639 */         matchedToken.kind = 148;
/* 8640 */         if (this.inFTLHeader) {
/* 8641 */           eatNewline();
/* 8642 */           this.inFTLHeader = false;
/*      */         } 
/* 8644 */         SwitchTo(0);
/*      */         break;
/*      */       
/*      */       case 135:
/* 8648 */         this.image.append(jjstrLiteralImages[135]);
/* 8649 */         this.lengthOfMatch = jjstrLiteralImages[135].length();
/* 8650 */         this.parenthesisNesting++;
/* 8651 */         if (this.parenthesisNesting == 1) SwitchTo(3); 
/*      */         break;
/*      */       case 136:
/* 8654 */         this.image.append(jjstrLiteralImages[136]);
/* 8655 */         this.lengthOfMatch = jjstrLiteralImages[136].length();
/* 8656 */         this.parenthesisNesting--;
/* 8657 */         if (this.parenthesisNesting == 0) {
/* 8658 */           if (this.inInvocation) { SwitchTo(4); break; }
/* 8659 */            SwitchTo(2);
/*      */         } 
/*      */         break;
/*      */       case 137:
/* 8663 */         this.image.append(jjstrLiteralImages[137]);
/* 8664 */         this.lengthOfMatch = jjstrLiteralImages[137].length();
/* 8665 */         this.curlyBracketNesting++;
/*      */         break;
/*      */       case 138:
/* 8668 */         this.image.append(jjstrLiteralImages[138]);
/* 8669 */         this.lengthOfMatch = jjstrLiteralImages[138].length();
/* 8670 */         if (this.curlyBracketNesting > 0) {
/* 8671 */           this.curlyBracketNesting--; break;
/* 8672 */         }  if (this.interpolationSyntax != 22 && this.postInterpolationLexState != -1) {
/* 8673 */           endInterpolation(matchedToken); break;
/*      */         } 
/* 8675 */         throw newUnexpectedClosingTokenException(matchedToken);
/*      */ 
/*      */       
/*      */       case 142:
/* 8679 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/*      */         
/* 8681 */         s = matchedToken.image;
/* 8682 */         if (s.indexOf('\\') != -1) {
/* 8683 */           int srcLn = s.length();
/* 8684 */           char[] newS = new char[srcLn - 1];
/* 8685 */           int dstIdx = 0;
/* 8686 */           for (int srcIdx = 0; srcIdx < srcLn; srcIdx++) {
/* 8687 */             char c = s.charAt(srcIdx);
/* 8688 */             if (c != '\\') {
/* 8689 */               newS[dstIdx++] = c;
/*      */             }
/*      */           } 
/* 8692 */           matchedToken.image = new String(newS, 0, dstIdx);
/*      */         } 
/*      */         break;
/*      */       case 143:
/* 8696 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8697 */         if ("".length() == 0) {
/* 8698 */           char closerC = (matchedToken.image.charAt(0) != '[') ? '}' : ']';
/* 8699 */           throw new TokenMgrError("You can't use " + matchedToken.image + "..." + closerC + " (an interpolation) here as you are already in FreeMarker-expression-mode. Thus, instead of " + matchedToken.image + "myExpression" + closerC + ", just write myExpression. (" + matchedToken.image + "..." + closerC + " is only used where otherwise static text is expected, i.e., outside FreeMarker tags and interpolations, or inside string literals.)", 0, matchedToken.beginLine, matchedToken.beginColumn, matchedToken.endLine, matchedToken.endColumn);
/*      */         } 
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case 148:
/* 8711 */         this.image.append(jjstrLiteralImages[148]);
/* 8712 */         this.lengthOfMatch = jjstrLiteralImages[148].length();
/* 8713 */         if (this.inFTLHeader) {
/* 8714 */           eatNewline();
/* 8715 */           this.inFTLHeader = false;
/*      */         } 
/* 8717 */         if (this.squBracTagSyntax || this.postInterpolationLexState != -1) {
/* 8718 */           matchedToken.kind = 150; break;
/*      */         } 
/* 8720 */         SwitchTo(0);
/*      */         break;
/*      */       
/*      */       case 149:
/* 8724 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8725 */         if (this.tagSyntaxEstablished && (this.incompatibleImprovements >= _TemplateAPI.VERSION_INT_2_3_28 || this.interpolationSyntax == 22)) {
/*      */           
/* 8727 */           String image = matchedToken.image;
/* 8728 */           char lastChar = image.charAt(image.length() - 1);
/* 8729 */           if ((!this.squBracTagSyntax && lastChar != '>') || (this.squBracTagSyntax && lastChar != ']')) {
/* 8730 */             throw new TokenMgrError("The tag shouldn't end with \"" + lastChar + "\".", 0, matchedToken.beginLine, matchedToken.beginColumn, matchedToken.endLine, matchedToken.endColumn);
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 8738 */         if (this.inFTLHeader) {
/* 8739 */           eatNewline();
/* 8740 */           this.inFTLHeader = false;
/*      */         } 
/* 8742 */         SwitchTo(0);
/*      */         break;
/*      */       case 154:
/* 8745 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8746 */         if (this.noparseTag.equals("-->")) {
/* 8747 */           boolean squareBracket = matchedToken.image.endsWith("]");
/* 8748 */           if ((this.squBracTagSyntax && squareBracket) || (!this.squBracTagSyntax && !squareBracket)) {
/* 8749 */             matchedToken.image += ";";
/* 8750 */             SwitchTo(0);
/*      */           } 
/*      */         } 
/*      */         break;
/*      */       case 155:
/* 8755 */         this.image.append(this.input_stream.GetSuffix(this.jjimageLen + (this.lengthOfMatch = this.jjmatchedPos + 1)));
/* 8756 */         st = new StringTokenizer(this.image.toString(), " \t\n\r<>[]/#", false);
/* 8757 */         if (st.nextToken().equals(this.noparseTag)) {
/* 8758 */           matchedToken.image += ";";
/* 8759 */           SwitchTo(0);
/*      */         } 
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void jjCheckNAdd(int state) {
/* 8768 */     if (this.jjrounds[state] != this.jjround) {
/*      */       
/* 8770 */       this.jjstateSet[this.jjnewStateCnt++] = state;
/* 8771 */       this.jjrounds[state] = this.jjround;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void jjAddStates(int start, int end) {
/*      */     do {
/* 8777 */       this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
/* 8778 */     } while (start++ != end);
/*      */   }
/*      */   
/*      */   private void jjCheckNAddTwoStates(int state1, int state2) {
/* 8782 */     jjCheckNAdd(state1);
/* 8783 */     jjCheckNAdd(state2);
/*      */   }
/*      */ 
/*      */   
/*      */   private void jjCheckNAddStates(int start, int end) {
/*      */     do {
/* 8789 */       jjCheckNAdd(jjnextStates[start]);
/* 8790 */     } while (start++ != end);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void ReInit(SimpleCharStream stream) {
/* 8812 */     this.jjmatchedPos = this.jjnewStateCnt = 0;
/* 8813 */     this.curLexState = this.defaultLexState;
/* 8814 */     this.input_stream = stream;
/* 8815 */     ReInitRounds();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void ReInitRounds() {
/* 8821 */     this.jjround = -2147483647;
/* 8822 */     for (int i = 713; i-- > 0;) {
/* 8823 */       this.jjrounds[i] = Integer.MIN_VALUE;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void ReInit(SimpleCharStream stream, int lexState) {
/* 8830 */     ReInit(stream);
/* 8831 */     SwitchTo(lexState);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void SwitchTo(int lexState) {
/* 8837 */     if (lexState >= 8 || lexState < 0) {
/* 8838 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
/*      */     }
/* 8840 */     this.curLexState = lexState;
/*      */   }
/*      */ 
/*      */   
/* 8844 */   public static final String[] lexStateNames = new String[] { "DEFAULT", "NO_DIRECTIVE", "FM_EXPRESSION", "IN_PAREN", "NAMED_PARAMETER_EXPRESSION", "EXPRESSION_COMMENT", "NO_SPACE_EXPRESSION", "NO_PARSE" };
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
/* 8856 */   public static final int[] jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 2, -1, -1, -1, -1 };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 8865 */   static final long[] jjtoToken = new long[] { -63L, -534773761L, 1072758783L };
/*      */ 
/*      */   
/* 8868 */   static final long[] jjtoSkip = new long[] { 0L, 266338304L, 0L };
/*      */   protected SimpleCharStream input_stream; private final int[] jjrounds; private final int[] jjstateSet; private final StringBuilder jjimage; private StringBuilder image; private int jjimageLen;
/*      */   private int lengthOfMatch;
/*      */   protected int curChar;
/*      */   
/* 8873 */   public FMParserTokenManager(SimpleCharStream stream) { this.jjrounds = new int[713];
/* 8874 */     this.jjstateSet = new int[1426];
/*      */     
/* 8876 */     this.jjimage = new StringBuilder();
/* 8877 */     this.image = this.jjimage; this.input_stream = stream; } public FMParserTokenManager(SimpleCharStream stream, int lexState) { this.jjrounds = new int[713]; this.jjstateSet = new int[1426]; this.jjimage = new StringBuilder(); this.image = this.jjimage;
/*      */     ReInit(stream);
/*      */     SwitchTo(lexState); }
/*      */ 
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\FMParserTokenManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */