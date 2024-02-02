/*     */ package com.github.odiszapc.nginxparser.javacc;public class NginxConfigParser implements NginxConfigParserConstants { private boolean isDebug = false; public NginxConfigParserTokenManager token_source; SimpleCharStream jj_input_stream; public Token token;
/*     */   public Token jj_nt;
/*     */   private int jj_ntk;
/*     */   private Token jj_scanpos;
/*     */   private Token jj_lastpos;
/*     */   private int jj_la;
/*     */   private int jj_gen;
/*     */   
/*     */   public void setDebug(boolean val) {
/*  10 */     this.isDebug = val;
/*     */   }
/*     */   
/*     */   private void debug(String message) {
/*  14 */     debug(message, false);
/*     */   }
/*     */   
/*     */   private void debugLn(String message) {
/*  18 */     debug(message, true);
/*     */   }
/*     */   
/*     */   private void debug(String message, boolean appendLineEnding) {
/*  22 */     if (this.isDebug) {
/*  23 */       System.out.print(message);
/*  24 */       if (appendLineEnding) System.out.println();
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final NgxConfig parse() throws ParseException {
/*  32 */     NgxConfig config = statements();
/*  33 */     if ("" != null) return config; 
/*  34 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */   public final NgxConfig statements() throws ParseException {
/*  37 */     NgxConfig config = new NgxConfig();
/*     */     
/*     */     while (true) {
/*     */       NgxComment ngxComment;
/*  41 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */         case 16:
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/*  50 */           this.jj_la1[0] = this.jj_gen;
/*     */           break;
/*     */       } 
/*  53 */       if (jj_2_1(4)) {
/*  54 */         NgxParam ngxParam = param();
/*     */       } else {
/*  56 */         NgxBlock ngxBlock; switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */           case 13:
/*     */           case 14:
/*     */           case 15:
/*  60 */             ngxBlock = block();
/*     */             break;
/*     */           
/*     */           case 16:
/*  64 */             ngxComment = comment();
/*     */             break;
/*     */           
/*     */           default:
/*  68 */             this.jj_la1[1] = this.jj_gen;
/*  69 */             jj_consume_token(-1);
/*  70 */             throw new ParseException();
/*     */         } 
/*     */       } 
/*  73 */       config.addEntry((NgxEntry)ngxComment);
/*     */     } 
/*  75 */     if ("" != null) return config; 
/*  76 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final NgxBlock block() throws ParseException {
/*  87 */     String blockName = "";
/*  88 */     NgxBlock block = new NgxBlock();
/*     */ 
/*     */     
/*  91 */     NgxToken curToken = id();
/*  92 */     blockName = blockName + this.token.image + " ";
/*  93 */     block.getTokens().add(curToken);
/*     */     
/*     */     while (true) {
/*  96 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 104 */           this.jj_la1[2] = this.jj_gen;
/*     */           break;
/*     */       } 
/* 107 */       curToken = id();
/* 108 */       blockName = blockName + this.token.image + " ";
/* 109 */       block.getTokens().add(curToken);
/*     */     } 
/* 111 */     debugLn("BLOCK=" + blockName + "{");
/* 112 */     jj_consume_token(7);
/*     */     while (true) {
/*     */       NgxBlock ngxBlock;
/* 115 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */         case 10:
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */         case 16:
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 125 */           this.jj_la1[3] = this.jj_gen;
/*     */           break;
/*     */       } 
/* 128 */       if (jj_2_2(4)) {
/* 129 */         NgxParam ngxParam = param();
/*     */       } else {
/* 131 */         NgxBlock ngxBlock1; NgxComment ngxComment; switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */           case 13:
/*     */           case 14:
/*     */           case 15:
/* 135 */             ngxBlock1 = block();
/*     */             break;
/*     */           
/*     */           case 16:
/* 139 */             ngxComment = comment();
/*     */             break;
/*     */           
/*     */           case 10:
/* 143 */             ngxBlock = if_block();
/*     */             break;
/*     */           
/*     */           default:
/* 147 */             this.jj_la1[4] = this.jj_gen;
/* 148 */             jj_consume_token(-1);
/* 149 */             throw new ParseException();
/*     */         } 
/*     */       } 
/* 152 */       block.addEntry((NgxEntry)ngxBlock);
/*     */     } 
/* 154 */     jj_consume_token(8);
/* 155 */     debugLn("}");
/* 156 */     if ("" != null) return block; 
/* 157 */     throw new Error("Missing return statement in function");
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
/*     */   public final NgxBlock if_block() throws ParseException {
/* 169 */     NgxIfBlock block = new NgxIfBlock();
/* 170 */     NgxEntry curEntry = null;
/* 171 */     jj_consume_token(10);
/* 172 */     debug(this.token.image + " ");
/* 173 */     block.addValue(new NgxToken(this.token.image));
/* 174 */     jj_consume_token(11);
/* 175 */     debug(this.token.image + " ");
/* 176 */     block.addValue(new NgxToken(this.token.image));
/* 177 */     jj_consume_token(7);
/* 178 */     debugLn(this.token.image);
/*     */     while (true) {
/*     */       NgxComment ngxComment;
/* 181 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */         case 16:
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 190 */           this.jj_la1[5] = this.jj_gen;
/*     */           break;
/*     */       } 
/* 193 */       if (jj_2_3(4)) {
/* 194 */         NgxParam ngxParam = param();
/*     */       } else {
/* 196 */         NgxBlock ngxBlock; switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */           case 13:
/*     */           case 14:
/*     */           case 15:
/* 200 */             ngxBlock = block();
/*     */             break;
/*     */           
/*     */           case 16:
/* 204 */             ngxComment = comment();
/*     */             break;
/*     */           
/*     */           default:
/* 208 */             this.jj_la1[6] = this.jj_gen;
/* 209 */             jj_consume_token(-1);
/* 210 */             throw new ParseException();
/*     */         } 
/*     */       } 
/* 213 */       block.addEntry((NgxEntry)ngxComment);
/*     */     } 
/* 215 */     jj_consume_token(8);
/* 216 */     debugLn(this.token.image);
/* 217 */     if ("" != null) return (NgxBlock)block; 
/* 218 */     throw new Error("Missing return statement in function");
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
/*     */   public final NgxParam param() throws ParseException {
/* 232 */     NgxParam param = new NgxParam();
/* 233 */     NgxToken curToken = null;
/* 234 */     curToken = id();
/* 235 */     debug("KEY=" + this.token.image + ", VALUE=");
/* 236 */     param.getTokens().add(curToken);
/*     */     
/*     */     while (true) {
/* 239 */       switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */         case 12:
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/* 248 */           this.jj_la1[7] = this.jj_gen;
/*     */           break;
/*     */       } 
/* 251 */       curToken = value();
/* 252 */       debug(this.token.image + " | ");
/* 253 */       param.getTokens().add(curToken);
/*     */     } 
/* 255 */     debugLn("");
/* 256 */     jj_consume_token(9);
/* 257 */     if ("" != null) return param; 
/* 258 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final NgxComment comment() throws ParseException {
/* 267 */     jj_consume_token(16);
/* 268 */     debugLn("COMMENT=" + this.token.image);
/* 269 */     if ("" != null) return new NgxComment(this.token.image); 
/* 270 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final NgxToken id() throws ParseException {
/* 280 */     NgxToken val = null;
/* 281 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */       case 13:
/* 283 */         jj_consume_token(13);
/*     */         break;
/*     */       
/*     */       case 14:
/* 287 */         jj_consume_token(14);
/*     */         break;
/*     */       
/*     */       case 15:
/* 291 */         jj_consume_token(15);
/*     */         break;
/*     */       
/*     */       default:
/* 295 */         this.jj_la1[8] = this.jj_gen;
/* 296 */         jj_consume_token(-1);
/* 297 */         throw new ParseException();
/*     */     } 
/* 299 */     if ("" != null) return new NgxToken(this.token.image); 
/* 300 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final NgxToken value() throws ParseException {
/* 306 */     NgxToken val = null;
/* 307 */     switch ((this.jj_ntk == -1) ? jj_ntk_f() : this.jj_ntk) {
/*     */       case 12:
/* 309 */         jj_consume_token(12);
/*     */         break;
/*     */       
/*     */       case 13:
/* 313 */         jj_consume_token(13);
/*     */         break;
/*     */       
/*     */       case 14:
/* 317 */         jj_consume_token(14);
/*     */         break;
/*     */       
/*     */       case 15:
/* 321 */         jj_consume_token(15);
/*     */         break;
/*     */       
/*     */       default:
/* 325 */         this.jj_la1[9] = this.jj_gen;
/* 326 */         jj_consume_token(-1);
/* 327 */         throw new ParseException();
/*     */     } 
/* 329 */     if ("" != null) return new NgxToken(this.token.image); 
/* 330 */     throw new Error("Missing return statement in function");
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean jj_2_1(int xla) {
/* 335 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 336 */     try { return !jj_3_1(); }
/* 337 */     catch (LookaheadSuccess ls) { return true; }
/* 338 */     finally { jj_save(0, xla); }
/*     */   
/*     */   }
/*     */   
/*     */   private boolean jj_2_2(int xla) {
/* 343 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 344 */     try { return !jj_3_2(); }
/* 345 */     catch (LookaheadSuccess ls) { return true; }
/* 346 */     finally { jj_save(1, xla); }
/*     */   
/*     */   }
/*     */   
/*     */   private boolean jj_2_3(int xla) {
/* 351 */     this.jj_la = xla; this.jj_lastpos = this.jj_scanpos = this.token; 
/* 352 */     try { return !jj_3_3(); }
/* 353 */     catch (LookaheadSuccess ls) { return true; }
/* 354 */     finally { jj_save(2, xla); }
/*     */   
/*     */   }
/*     */   
/*     */   private boolean jj_3R_8() {
/* 359 */     if (jj_3R_9()) return true; 
/* 360 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean jj_3R_7() {
/* 366 */     Token xsp = this.jj_scanpos;
/* 367 */     if (jj_scan_token(13)) {
/* 368 */       this.jj_scanpos = xsp;
/* 369 */       if (jj_scan_token(14)) {
/* 370 */         this.jj_scanpos = xsp;
/* 371 */         if (jj_scan_token(15)) return true; 
/*     */       } 
/*     */     } 
/* 374 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean jj_3_2() {
/* 379 */     if (jj_3R_6()) return true; 
/* 380 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean jj_3R_9() {
/* 386 */     Token xsp = this.jj_scanpos;
/* 387 */     if (jj_scan_token(12)) {
/* 388 */       this.jj_scanpos = xsp;
/* 389 */       if (jj_scan_token(13)) {
/* 390 */         this.jj_scanpos = xsp;
/* 391 */         if (jj_scan_token(14)) {
/* 392 */           this.jj_scanpos = xsp;
/* 393 */           if (jj_scan_token(15)) return true; 
/*     */         } 
/*     */       } 
/*     */     } 
/* 397 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean jj_3_3() {
/* 402 */     if (jj_3R_6()) return true; 
/* 403 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean jj_3_1() {
/* 408 */     if (jj_3R_6()) return true; 
/* 409 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean jj_3R_6() {
/* 414 */     if (jj_3R_7()) return true;
/*     */     
/*     */     while (true) {
/* 417 */       Token xsp = this.jj_scanpos;
/* 418 */       if (jj_3R_8()) { this.jj_scanpos = xsp;
/*     */         
/* 420 */         if (jj_scan_token(9)) return true; 
/* 421 */         return false; }
/*     */     
/*     */     } 
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
/* 435 */   private final int[] jj_la1 = new int[10]; private static int[] jj_la1_0;
/*     */   
/*     */   static {
/* 438 */     jj_la1_init_0();
/*     */   }
/*     */   private static void jj_la1_init_0() {
/* 441 */     jj_la1_0 = new int[] { 122880, 122880, 57344, 123904, 123904, 122880, 122880, 61440, 57344, 61440 };
/*     */   }
/* 443 */   private final JJCalls[] jj_2_rtns = new JJCalls[3];
/*     */   private boolean jj_rescan = false;
/* 445 */   private int jj_gc = 0; private final LookaheadSuccess jj_ls; private List<int[]> jj_expentries; private int[] jj_expentry; private int jj_kind; private int[] jj_lasttokens;
/*     */   private int jj_endpos;
/*     */   
/*     */   public NginxConfigParser(InputStream stream) {
/* 449 */     this(stream, null);
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
/*     */   public void ReInit(InputStream stream) {
/* 464 */     ReInit(stream, null);
/*     */   }
/*     */   public void ReInit(InputStream stream, String encoding) {
/*     */     
/* 468 */     try { this.jj_input_stream.ReInit(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }
/* 469 */      this.token_source.ReInit(this.jj_input_stream);
/* 470 */     this.token = new Token();
/* 471 */     this.jj_ntk = -1;
/* 472 */     this.jj_gen = 0; int i;
/* 473 */     for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }
/* 474 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*     */   
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
/*     */   public void ReInit(Reader stream) {
/* 490 */     this.jj_input_stream.ReInit(stream, 1, 1);
/* 491 */     this.token_source.ReInit(this.jj_input_stream);
/* 492 */     this.token = new Token();
/* 493 */     this.jj_ntk = -1;
/* 494 */     this.jj_gen = 0; int i;
/* 495 */     for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }
/* 496 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*     */   
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
/*     */   public void ReInit(NginxConfigParserTokenManager tm) {
/* 511 */     this.token_source = tm;
/* 512 */     this.token = new Token();
/* 513 */     this.jj_ntk = -1;
/* 514 */     this.jj_gen = 0; int i;
/* 515 */     for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }
/* 516 */      for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }
/*     */   
/*     */   }
/*     */   private Token jj_consume_token(int kind) throws ParseException {
/*     */     Token oldToken;
/* 521 */     if ((oldToken = this.token).next != null) { this.token = this.token.next; }
/* 522 */     else { this.token = this.token.next = this.token_source.getNextToken(); }
/* 523 */      this.jj_ntk = -1;
/* 524 */     if (this.token.kind == kind) {
/* 525 */       this.jj_gen++;
/* 526 */       if (++this.jj_gc > 100) {
/* 527 */         this.jj_gc = 0;
/* 528 */         for (int i = 0; i < this.jj_2_rtns.length; i++) {
/* 529 */           JJCalls c = this.jj_2_rtns[i];
/* 530 */           while (c != null) {
/* 531 */             if (c.gen < this.jj_gen) c.first = null; 
/* 532 */             c = c.next;
/*     */           } 
/*     */         } 
/*     */       } 
/* 536 */       return this.token;
/*     */     } 
/* 538 */     this.token = oldToken;
/* 539 */     this.jj_kind = kind;
/* 540 */     throw generateParseException();
/*     */   }
/*     */   private static final class LookaheadSuccess extends Error {
/*     */     private LookaheadSuccess() {} }
/*     */   
/* 545 */   public NginxConfigParser(InputStream stream, String encoding) { this.jj_ls = new LookaheadSuccess();
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
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 594 */     this.jj_expentries = (List)new ArrayList<int>();
/*     */     
/* 596 */     this.jj_kind = -1;
/* 597 */     this.jj_lasttokens = new int[100]; try { this.jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch (UnsupportedEncodingException e) { throw new RuntimeException(e); }  this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  } public NginxConfigParser(Reader stream) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<int>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.jj_input_stream = new SimpleCharStream(stream, 1, 1); this.token_source = new NginxConfigParserTokenManager(this.jj_input_stream); this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  } public NginxConfigParser(NginxConfigParserTokenManager tm) { this.jj_ls = new LookaheadSuccess(); this.jj_expentries = (List)new ArrayList<int>(); this.jj_kind = -1; this.jj_lasttokens = new int[100]; this.token_source = tm; this.token = new Token(); this.jj_ntk = -1; this.jj_gen = 0; int i; for (i = 0; i < 10; ) { this.jj_la1[i] = -1; i++; }  for (i = 0; i < this.jj_2_rtns.length; ) { this.jj_2_rtns[i] = new JJCalls(); i++; }  }
/*     */   private boolean jj_scan_token(int kind) { if (this.jj_scanpos == this.jj_lastpos) { this.jj_la--; if (this.jj_scanpos.next == null) { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken(); } else { this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next; }  } else { this.jj_scanpos = this.jj_scanpos.next; }  if (this.jj_rescan) { int i = 0; Token tok = this.token; while (tok != null && tok != this.jj_scanpos) { i++; tok = tok.next; }  if (tok != null)
/*     */         jj_add_error_token(kind, i);  }  if (this.jj_scanpos.kind != kind)
/*     */       return true;  if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos)
/* 601 */       throw this.jj_ls;  return false; } private void jj_add_error_token(int kind, int pos) { if (pos >= 100)
/* 602 */       return;  if (pos == this.jj_endpos + 1)
/* 603 */     { this.jj_lasttokens[this.jj_endpos++] = kind; }
/* 604 */     else if (this.jj_endpos != 0)
/* 605 */     { this.jj_expentry = new int[this.jj_endpos];
/* 606 */       for (int i = 0; i < this.jj_endpos; i++)
/* 607 */         this.jj_expentry[i] = this.jj_lasttokens[i]; 
/*     */       Iterator<?> it;
/* 609 */       label31: for (it = this.jj_expentries.iterator(); it.hasNext(); ) {
/* 610 */         int[] oldentry = (int[])it.next();
/* 611 */         if (oldentry.length == this.jj_expentry.length) {
/* 612 */           for (int j = 0; j < this.jj_expentry.length; j++) {
/* 613 */             if (oldentry[j] != this.jj_expentry[j]) {
/*     */               continue label31;
/*     */             }
/*     */           } 
/* 617 */           this.jj_expentries.add(this.jj_expentry);
/*     */           break;
/*     */         } 
/*     */       } 
/* 621 */       if (pos != 0) this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;  }  }
/*     */   public final Token getNextToken() { if (this.token.next != null) { this.token = this.token.next; } else { this.token = this.token.next = this.token_source.getNextToken(); }  this.jj_ntk = -1; this.jj_gen++; return this.token; }
/*     */   public final Token getToken(int index) { Token t = this.token; for (int i = 0; i < index; i++) { if (t.next != null) { t = t.next; } else { t = t.next = this.token_source.getNextToken(); }
/*     */        }
/*     */      return t; }
/*     */   private int jj_ntk_f() { if ((this.jj_nt = this.token.next) == null)
/* 627 */       return this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind;  return this.jj_ntk = this.jj_nt.kind; } public ParseException generateParseException() { this.jj_expentries.clear();
/* 628 */     boolean[] la1tokens = new boolean[19];
/* 629 */     if (this.jj_kind >= 0) {
/* 630 */       la1tokens[this.jj_kind] = true;
/* 631 */       this.jj_kind = -1;
/*     */     }  int i;
/* 633 */     for (i = 0; i < 10; i++) {
/* 634 */       if (this.jj_la1[i] == this.jj_gen) {
/* 635 */         for (int k = 0; k < 32; k++) {
/* 636 */           if ((jj_la1_0[i] & 1 << k) != 0) {
/* 637 */             la1tokens[k] = true;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 642 */     for (i = 0; i < 19; i++) {
/* 643 */       if (la1tokens[i]) {
/* 644 */         this.jj_expentry = new int[1];
/* 645 */         this.jj_expentry[0] = i;
/* 646 */         this.jj_expentries.add(this.jj_expentry);
/*     */       } 
/*     */     } 
/* 649 */     this.jj_endpos = 0;
/* 650 */     jj_rescan_token();
/* 651 */     jj_add_error_token(0, 0);
/* 652 */     int[][] exptokseq = new int[this.jj_expentries.size()][];
/* 653 */     for (int j = 0; j < this.jj_expentries.size(); j++) {
/* 654 */       exptokseq[j] = this.jj_expentries.get(j);
/*     */     }
/* 656 */     return new ParseException(this.token, exptokseq, tokenImage); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void enable_tracing() {}
/*     */ 
/*     */   
/*     */   public final void disable_tracing() {}
/*     */ 
/*     */   
/*     */   private void jj_rescan_token() {
/* 668 */     this.jj_rescan = true;
/* 669 */     for (int i = 0; i < 3; i++) {
/*     */       try {
/* 671 */         JJCalls p = this.jj_2_rtns[i];
/*     */         do {
/* 673 */           if (p.gen > this.jj_gen) {
/* 674 */             this.jj_la = p.arg; this.jj_lastpos = this.jj_scanpos = p.first;
/* 675 */             switch (i) { case 0:
/* 676 */                 jj_3_1(); break;
/* 677 */               case 1: jj_3_2(); break;
/* 678 */               case 2: jj_3_3(); break; }
/*     */           
/*     */           } 
/* 681 */           p = p.next;
/* 682 */         } while (p != null);
/* 683 */       } catch (LookaheadSuccess ls) {}
/*     */     } 
/* 685 */     this.jj_rescan = false;
/*     */   }
/*     */   
/*     */   private void jj_save(int index, int xla) {
/* 689 */     JJCalls p = this.jj_2_rtns[index];
/* 690 */     while (p.gen > this.jj_gen) {
/* 691 */       if (p.next == null) { p = p.next = new JJCalls(); break; }
/* 692 */        p = p.next;
/*     */     } 
/* 694 */     p.gen = this.jj_gen + xla - this.jj_la; p.first = this.token; p.arg = xla;
/*     */   }
/*     */   
/*     */   static final class JJCalls {
/*     */     int gen;
/*     */     Token first;
/*     */     int arg;
/*     */     JJCalls next;
/*     */   } }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\javacc\NginxConfigParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */