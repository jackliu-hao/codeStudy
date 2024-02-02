/*     */ package com.github.odiszapc.nginxparser.javacc;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class NginxConfigParserTokenManager
/*     */   implements NginxConfigParserConstants
/*     */ {
/*   9 */   public PrintStream debugStream = System.out;
/*     */   public void setDebugStream(PrintStream ds) {
/*  11 */     this.debugStream = ds;
/*     */   } private final int jjStopStringLiteralDfa_0(int pos, long active0) {
/*  13 */     switch (pos) {
/*     */       
/*     */       case 0:
/*  16 */         if ((active0 & 0x400L) != 0L) {
/*     */           
/*  18 */           this.jjmatchedKind = 13;
/*  19 */           return 5;
/*     */         } 
/*  21 */         if ((active0 & 0x20L) != 0L)
/*  22 */           return 20; 
/*  23 */         if ((active0 & 0x40L) != 0L)
/*  24 */           return 5; 
/*  25 */         return -1;
/*     */     } 
/*  27 */     return -1;
/*     */   }
/*     */   
/*     */   private final int jjStartNfa_0(int pos, long active0) {
/*  31 */     return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
/*     */   }
/*     */   
/*     */   private int jjStopAtPos(int pos, int kind) {
/*  35 */     this.jjmatchedKind = kind;
/*  36 */     this.jjmatchedPos = pos;
/*  37 */     return pos + 1;
/*     */   }
/*     */   private int jjMoveStringLiteralDfa0_0() {
/*  40 */     switch (this.curChar) {
/*     */       
/*     */       case '(':
/*  43 */         return jjStartNfaWithStates_0(0, 5, 20);
/*     */       case ')':
/*  45 */         return jjStartNfaWithStates_0(0, 6, 5);
/*     */       case ';':
/*  47 */         return jjStopAtPos(0, 9);
/*     */       case 'i':
/*  49 */         return jjMoveStringLiteralDfa1_0(1024L);
/*     */       case '{':
/*  51 */         return jjStopAtPos(0, 7);
/*     */       case '}':
/*  53 */         return jjStopAtPos(0, 8);
/*     */     } 
/*  55 */     return jjMoveNfa_0(0, 0);
/*     */   }
/*     */   private int jjMoveStringLiteralDfa1_0(long active0) {
/*     */     try {
/*  59 */       this.curChar = this.input_stream.readChar();
/*  60 */     } catch (IOException e) {
/*  61 */       jjStopStringLiteralDfa_0(0, active0);
/*  62 */       return 1;
/*     */     } 
/*  64 */     switch (this.curChar) {
/*     */       
/*     */       case 'f':
/*  67 */         if ((active0 & 0x400L) != 0L) {
/*  68 */           return jjStartNfaWithStates_0(1, 10, 5);
/*     */         }
/*     */         break;
/*     */     } 
/*     */     
/*  73 */     return jjStartNfa_0(0, active0);
/*     */   }
/*     */   
/*     */   private int jjStartNfaWithStates_0(int pos, int kind, int state) {
/*  77 */     this.jjmatchedKind = kind;
/*  78 */     this.jjmatchedPos = pos; 
/*  79 */     try { this.curChar = this.input_stream.readChar(); }
/*  80 */     catch (IOException e) { return pos + 1; }
/*  81 */      return jjMoveNfa_0(state, pos + 1);
/*     */   }
/*  83 */   static final long[] jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
/*     */ 
/*     */ 
/*     */   
/*     */   private int jjMoveNfa_0(int startState, int curPos) {
/*  88 */     int startsAt = 0;
/*  89 */     this.jjnewStateCnt = 20;
/*  90 */     int i = 1;
/*  91 */     this.jjstateSet[0] = startState;
/*  92 */     int kind = Integer.MAX_VALUE;
/*     */     
/*     */     while (true) {
/*  95 */       if (++this.jjround == Integer.MAX_VALUE)
/*  96 */         ReInitRounds(); 
/*  97 */       if (this.curChar < '@') {
/*     */         
/*  99 */         long l = 1L << this.curChar;
/*     */         
/*     */         do {
/* 102 */           switch (this.jjstateSet[--i]) {
/*     */             
/*     */             case 20:
/* 105 */               if ((0xFFFFFDFFFFFFFFFFL & l) != 0L)
/* 106 */                 jjCheckNAddTwoStates(1, 2); 
/* 107 */               if ((0xA7FFEF5000000000L & l) != 0L) {
/*     */                 
/* 109 */                 if (kind > 13)
/* 110 */                   kind = 13; 
/* 111 */                 jjCheckNAdd(5);
/*     */               } 
/*     */               break;
/*     */             case 0:
/* 115 */               if ((0xA7FFEF5000000000L & l) != 0L) {
/*     */                 
/* 117 */                 if (kind > 13)
/* 118 */                   kind = 13; 
/* 119 */                 jjCheckNAdd(5);
/*     */               }
/* 121 */               else if (this.curChar == '#') {
/*     */                 
/* 123 */                 if (kind > 16)
/* 124 */                   kind = 16; 
/* 125 */                 jjCheckNAdd(19);
/*     */               }
/* 127 */               else if (this.curChar == '\'') {
/* 128 */                 jjCheckNAddTwoStates(16, 17);
/* 129 */               } else if (this.curChar == '"') {
/* 130 */                 jjCheckNAddStates(0, 2);
/* 131 */               }  if ((0x3FE000000000000L & l) != 0L) {
/*     */                 
/* 133 */                 if (kind > 12)
/* 134 */                   kind = 12; 
/* 135 */                 jjCheckNAdd(4); break;
/*     */               } 
/* 137 */               if (this.curChar == '(')
/* 138 */                 jjCheckNAdd(1); 
/*     */               break;
/*     */             case 1:
/* 141 */               if ((0xFFFFFDFFFFFFFFFFL & l) != 0L)
/* 142 */                 jjCheckNAddTwoStates(1, 2); 
/*     */               break;
/*     */             case 2:
/* 145 */               if (this.curChar == ')' && kind > 11)
/* 146 */                 kind = 11; 
/*     */               break;
/*     */             case 3:
/* 149 */               if ((0x3FE000000000000L & l) == 0L)
/*     */                 break; 
/* 151 */               if (kind > 12)
/* 152 */                 kind = 12; 
/* 153 */               jjCheckNAdd(4);
/*     */               break;
/*     */             case 4:
/* 156 */               if ((0x3FF000000000000L & l) == 0L)
/*     */                 break; 
/* 158 */               if (kind > 12)
/* 159 */                 kind = 12; 
/* 160 */               jjCheckNAdd(4);
/*     */               break;
/*     */             case 5:
/* 163 */               if ((0xA7FFEF5000000000L & l) == 0L)
/*     */                 break; 
/* 165 */               if (kind > 13)
/* 166 */                 kind = 13; 
/* 167 */               jjCheckNAdd(5);
/*     */               break;
/*     */             case 6:
/* 170 */               if (this.curChar == '"')
/* 171 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 7:
/* 174 */               if ((0xFFFFFFFBFFFFFFFFL & l) != 0L)
/* 175 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 9:
/* 178 */               if ((0x8400000000L & l) != 0L)
/* 179 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 10:
/* 182 */               if (this.curChar == '"' && kind > 14)
/* 183 */                 kind = 14; 
/*     */               break;
/*     */             case 11:
/* 186 */               if ((0xFF000000000000L & l) != 0L)
/* 187 */                 jjCheckNAddStates(3, 6); 
/*     */               break;
/*     */             case 12:
/* 190 */               if ((0xFF000000000000L & l) != 0L)
/* 191 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 13:
/* 194 */               if ((0xF000000000000L & l) != 0L)
/* 195 */                 this.jjstateSet[this.jjnewStateCnt++] = 14; 
/*     */               break;
/*     */             case 14:
/* 198 */               if ((0xFF000000000000L & l) != 0L)
/* 199 */                 jjCheckNAdd(12); 
/*     */               break;
/*     */             case 15:
/* 202 */               if (this.curChar == '\'')
/* 203 */                 jjCheckNAddTwoStates(16, 17); 
/*     */               break;
/*     */             case 16:
/* 206 */               if ((0xFFFFFF7FFFFFFFFFL & l) != 0L)
/* 207 */                 jjCheckNAddTwoStates(16, 17); 
/*     */               break;
/*     */             case 17:
/* 210 */               if (this.curChar == '\'' && kind > 15)
/* 211 */                 kind = 15; 
/*     */               break;
/*     */             case 18:
/* 214 */               if (this.curChar != '#')
/*     */                 break; 
/* 216 */               if (kind > 16)
/* 217 */                 kind = 16; 
/* 218 */               jjCheckNAdd(19);
/*     */               break;
/*     */             case 19:
/* 221 */               if ((0xFFFFFFFFFFFFDBFFL & l) == 0L)
/*     */                 break; 
/* 223 */               if (kind > 16)
/* 224 */                 kind = 16; 
/* 225 */               jjCheckNAdd(19);
/*     */               break;
/*     */           } 
/*     */         
/* 229 */         } while (i != startsAt);
/*     */       }
/* 231 */       else if (this.curChar < '') {
/*     */         
/* 233 */         long l = 1L << (this.curChar & 0x3F);
/*     */         
/*     */         do {
/* 236 */           switch (this.jjstateSet[--i]) {
/*     */             
/*     */             case 20:
/* 239 */               jjCheckNAddTwoStates(1, 2);
/* 240 */               if ((0x57FFFFFED7FFFFFFL & l) != 0L) {
/*     */                 
/* 242 */                 if (kind > 13)
/* 243 */                   kind = 13; 
/* 244 */                 jjCheckNAdd(5);
/*     */               } 
/*     */               break;
/*     */             case 0:
/*     */             case 5:
/* 249 */               if ((0x57FFFFFED7FFFFFFL & l) == 0L)
/*     */                 break; 
/* 251 */               if (kind > 13)
/* 252 */                 kind = 13; 
/* 253 */               jjCheckNAdd(5);
/*     */               break;
/*     */             case 1:
/* 256 */               jjCheckNAddTwoStates(1, 2);
/*     */               break;
/*     */             case 7:
/* 259 */               if ((0xFFFFFFFFEFFFFFFFL & l) != 0L)
/* 260 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 8:
/* 263 */               if (this.curChar == '\\')
/* 264 */                 jjAddStates(7, 9); 
/*     */               break;
/*     */             case 9:
/* 267 */               if ((0x14404410000000L & l) != 0L)
/* 268 */                 jjCheckNAddStates(0, 2); 
/*     */               break;
/*     */             case 16:
/* 271 */               jjAddStates(10, 11);
/*     */               break;
/*     */             case 19:
/* 274 */               if (kind > 16)
/* 275 */                 kind = 16; 
/* 276 */               this.jjstateSet[this.jjnewStateCnt++] = 19;
/*     */               break;
/*     */           } 
/*     */         
/* 280 */         } while (i != startsAt);
/*     */       }
/*     */       else {
/*     */         
/* 284 */         int i2 = (this.curChar & 0xFF) >> 6;
/* 285 */         long l2 = 1L << (this.curChar & 0x3F);
/*     */         
/*     */         do {
/* 288 */           switch (this.jjstateSet[--i]) {
/*     */             
/*     */             case 1:
/*     */             case 20:
/* 292 */               if ((jjbitVec0[i2] & l2) != 0L)
/* 293 */                 jjCheckNAddTwoStates(1, 2); 
/*     */               break;
/*     */             case 7:
/* 296 */               if ((jjbitVec0[i2] & l2) != 0L)
/* 297 */                 jjAddStates(0, 2); 
/*     */               break;
/*     */             case 16:
/* 300 */               if ((jjbitVec0[i2] & l2) != 0L)
/* 301 */                 jjAddStates(10, 11); 
/*     */               break;
/*     */             case 19:
/* 304 */               if ((jjbitVec0[i2] & l2) == 0L)
/*     */                 break; 
/* 306 */               if (kind > 16)
/* 307 */                 kind = 16; 
/* 308 */               this.jjstateSet[this.jjnewStateCnt++] = 19;
/*     */               break;
/*     */           } 
/*     */         
/* 312 */         } while (i != startsAt);
/*     */       } 
/* 314 */       if (kind != Integer.MAX_VALUE) {
/*     */         
/* 316 */         this.jjmatchedKind = kind;
/* 317 */         this.jjmatchedPos = curPos;
/* 318 */         kind = Integer.MAX_VALUE;
/*     */       } 
/* 320 */       curPos++;
/* 321 */       if ((i = this.jjnewStateCnt) == (startsAt = 20 - (this.jjnewStateCnt = startsAt)))
/* 322 */         return curPos;  
/* 323 */       try { this.curChar = this.input_stream.readChar(); }
/* 324 */       catch (IOException e) { return curPos; }
/*     */     
/*     */     } 
/* 327 */   } static final int[] jjnextStates = new int[] { 7, 8, 10, 7, 8, 12, 10, 9, 11, 13, 16, 17 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 332 */   public static final String[] jjstrLiteralImages = new String[] { "", null, null, null, null, "(", ")", "{", "}", ";", "if", null, null, null, null, null, null, null, null };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Token jjFillToken() {
/* 343 */     String im = jjstrLiteralImages[this.jjmatchedKind];
/* 344 */     String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
/* 345 */     int beginLine = this.input_stream.getBeginLine();
/* 346 */     int beginColumn = this.input_stream.getBeginColumn();
/* 347 */     int endLine = this.input_stream.getEndLine();
/* 348 */     int endColumn = this.input_stream.getEndColumn();
/* 349 */     Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
/*     */     
/* 351 */     t.beginLine = beginLine;
/* 352 */     t.endLine = endLine;
/* 353 */     t.beginColumn = beginColumn;
/* 354 */     t.endColumn = endColumn;
/*     */     
/* 356 */     return t;
/*     */   }
/*     */   
/* 359 */   int curLexState = 0;
/* 360 */   int defaultLexState = 0;
/*     */   
/*     */   int jjnewStateCnt;
/*     */   
/*     */   int jjround;
/*     */   
/*     */   int jjmatchedPos;
/*     */   int jjmatchedKind;
/*     */   
/*     */   public Token getNextToken() {
/* 370 */     int curPos = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/*     */       try {
/* 377 */         this.curChar = this.input_stream.BeginToken();
/*     */       }
/* 379 */       catch (IOException e) {
/*     */         
/* 381 */         this.jjmatchedKind = 0;
/* 382 */         this.jjmatchedPos = -1;
/* 383 */         Token matchedToken = jjFillToken();
/* 384 */         return matchedToken;
/*     */       } 
/*     */       
/* 387 */       try { this.input_stream.backup(0);
/* 388 */         while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0L) {
/* 389 */           this.curChar = this.input_stream.BeginToken();
/*     */         } }
/* 391 */       catch (IOException e1) { continue; }
/* 392 */        this.jjmatchedKind = Integer.MAX_VALUE;
/* 393 */       this.jjmatchedPos = 0;
/* 394 */       curPos = jjMoveStringLiteralDfa0_0();
/* 395 */       if (this.jjmatchedKind != Integer.MAX_VALUE) {
/*     */         
/* 397 */         if (this.jjmatchedPos + 1 < curPos)
/* 398 */           this.input_stream.backup(curPos - this.jjmatchedPos - 1); 
/* 399 */         if ((jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0L) {
/*     */           
/* 401 */           Token matchedToken = jjFillToken();
/* 402 */           return matchedToken;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 409 */     int error_line = this.input_stream.getEndLine();
/* 410 */     int error_column = this.input_stream.getEndColumn();
/* 411 */     String error_after = null;
/* 412 */     boolean EOFSeen = false; try {
/* 413 */       this.input_stream.readChar(); this.input_stream.backup(1);
/* 414 */     } catch (IOException e1) {
/* 415 */       EOFSeen = true;
/* 416 */       error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/* 417 */       if (this.curChar == '\n' || this.curChar == '\r') {
/* 418 */         error_line++;
/* 419 */         error_column = 0;
/*     */       } else {
/*     */         
/* 422 */         error_column++;
/*     */       } 
/* 424 */     }  if (!EOFSeen) {
/* 425 */       this.input_stream.backup(1);
/* 426 */       error_after = (curPos <= 1) ? "" : this.input_stream.GetImage();
/*     */     } 
/* 428 */     throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void jjCheckNAdd(int state) {
/* 434 */     if (this.jjrounds[state] != this.jjround) {
/*     */       
/* 436 */       this.jjstateSet[this.jjnewStateCnt++] = state;
/* 437 */       this.jjrounds[state] = this.jjround;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void jjAddStates(int start, int end) {
/*     */     do {
/* 443 */       this.jjstateSet[this.jjnewStateCnt++] = jjnextStates[start];
/* 444 */     } while (start++ != end);
/*     */   }
/*     */   
/*     */   private void jjCheckNAddTwoStates(int state1, int state2) {
/* 448 */     jjCheckNAdd(state1);
/* 449 */     jjCheckNAdd(state2);
/*     */   }
/*     */ 
/*     */   
/*     */   private void jjCheckNAddStates(int start, int end) {
/*     */     do {
/* 455 */       jjCheckNAdd(jjnextStates[start]);
/* 456 */     } while (start++ != end);
/*     */   }
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
/*     */   public void ReInit(SimpleCharStream stream) {
/* 477 */     this.jjmatchedPos = this.jjnewStateCnt = 0;
/* 478 */     this.curLexState = this.defaultLexState;
/* 479 */     this.input_stream = stream;
/* 480 */     ReInitRounds();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void ReInitRounds() {
/* 486 */     this.jjround = -2147483647;
/* 487 */     for (int i = 20; i-- > 0;) {
/* 488 */       this.jjrounds[i] = Integer.MIN_VALUE;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void ReInit(SimpleCharStream stream, int lexState) {
/* 494 */     ReInit(stream);
/* 495 */     SwitchTo(lexState);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void SwitchTo(int lexState) {
/* 501 */     if (lexState >= 1 || lexState < 0) {
/* 502 */       throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
/*     */     }
/* 504 */     this.curLexState = lexState;
/*     */   }
/*     */ 
/*     */   
/* 508 */   public static final String[] lexStateNames = new String[] { "DEFAULT" };
/*     */ 
/*     */   
/* 511 */   static final long[] jjtoToken = new long[] { 131041L };
/*     */ 
/*     */   
/* 514 */   static final long[] jjtoSkip = new long[] { 30L };
/*     */   protected SimpleCharStream input_stream; private final int[] jjrounds;
/*     */   private final int[] jjstateSet;
/*     */   protected char curChar;
/*     */   
/* 519 */   public NginxConfigParserTokenManager(SimpleCharStream stream) { this.jjrounds = new int[20];
/* 520 */     this.jjstateSet = new int[40]; this.input_stream = stream; } public NginxConfigParserTokenManager(SimpleCharStream stream, int lexState) { this.jjrounds = new int[20]; this.jjstateSet = new int[40];
/*     */     ReInit(stream);
/*     */     SwitchTo(lexState); }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\NginxConfigParserTokenManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */