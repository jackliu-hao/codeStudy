/*     */ package com.sun.mail.iap;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.Vector;
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
/*     */ public class Response
/*     */ {
/*     */   protected int index;
/*     */   protected int pindex;
/*     */   protected int size;
/*  58 */   protected byte[] buffer = null;
/*  59 */   protected int type = 0;
/*  60 */   protected String tag = null;
/*     */   
/*     */   private static final int increment = 100;
/*     */   
/*     */   public static final int TAG_MASK = 3;
/*     */   
/*     */   public static final int CONTINUATION = 1;
/*     */   
/*     */   public static final int TAGGED = 2;
/*     */   
/*     */   public static final int UNTAGGED = 3;
/*     */   
/*     */   public static final int TYPE_MASK = 28;
/*     */   
/*     */   public static final int OK = 4;
/*     */   
/*     */   public static final int NO = 8;
/*     */   
/*     */   public static final int BAD = 12;
/*     */   public static final int BYE = 16;
/*     */   public static final int SYNTHETIC = 32;
/*     */   
/*     */   public Response(String s) {
/*  83 */     this.buffer = ASCIIUtility.getBytes(s);
/*  84 */     this.size = this.buffer.length;
/*  85 */     parse();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Response(Protocol p) throws IOException, ProtocolException {
/*  94 */     ByteArray ba = p.getResponseBuffer();
/*  95 */     ByteArray response = p.getInputStream().readResponse(ba);
/*  96 */     this.buffer = response.getBytes();
/*  97 */     this.size = response.getCount() - 2;
/*     */     
/*  99 */     parse();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Response(Response r) {
/* 106 */     this.index = r.index;
/* 107 */     this.size = r.size;
/* 108 */     this.buffer = r.buffer;
/* 109 */     this.type = r.type;
/* 110 */     this.tag = r.tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Response byeResponse(Exception ex) {
/* 118 */     String err = "* BYE JavaMail Exception: " + ex.toString();
/* 119 */     err = err.replace('\r', ' ').replace('\n', ' ');
/* 120 */     Response r = new Response(err);
/* 121 */     r.type |= 0x20;
/* 122 */     return r;
/*     */   }
/*     */   
/*     */   private void parse() {
/* 126 */     this.index = 0;
/*     */     
/* 128 */     if (this.size == 0)
/*     */       return; 
/* 130 */     if (this.buffer[this.index] == 43) {
/* 131 */       this.type |= 0x1;
/* 132 */       this.index++; return;
/*     */     } 
/* 134 */     if (this.buffer[this.index] == 42) {
/* 135 */       this.type |= 0x3;
/* 136 */       this.index++;
/*     */     } else {
/* 138 */       this.type |= 0x2;
/* 139 */       this.tag = readAtom();
/* 140 */       if (this.tag == null) {
/* 141 */         this.tag = "";
/*     */       }
/*     */     } 
/* 144 */     int mark = this.index;
/* 145 */     String s = readAtom();
/* 146 */     if (s == null)
/* 147 */       s = ""; 
/* 148 */     if (s.equalsIgnoreCase("OK")) {
/* 149 */       this.type |= 0x4;
/* 150 */     } else if (s.equalsIgnoreCase("NO")) {
/* 151 */       this.type |= 0x8;
/* 152 */     } else if (s.equalsIgnoreCase("BAD")) {
/* 153 */       this.type |= 0xC;
/* 154 */     } else if (s.equalsIgnoreCase("BYE")) {
/* 155 */       this.type |= 0x10;
/*     */     } else {
/* 157 */       this.index = mark;
/*     */     } 
/* 159 */     this.pindex = this.index;
/*     */   }
/*     */ 
/*     */   
/*     */   public void skipSpaces() {
/* 164 */     while (this.index < this.size && this.buffer[this.index] == 32) {
/* 165 */       this.index++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void skipToken() {
/* 172 */     while (this.index < this.size && this.buffer[this.index] != 32)
/* 173 */       this.index++; 
/*     */   }
/*     */   
/*     */   public void skip(int count) {
/* 177 */     this.index += count;
/*     */   }
/*     */   
/*     */   public byte peekByte() {
/* 181 */     if (this.index < this.size) {
/* 182 */       return this.buffer[this.index];
/*     */     }
/* 184 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte readByte() {
/* 192 */     if (this.index < this.size) {
/* 193 */       return this.buffer[this.index++];
/*     */     }
/* 195 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readAtom() {
/* 204 */     return readAtom(false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readAtom(char delim) {
/* 212 */     skipSpaces();
/*     */     
/* 214 */     if (this.index >= this.size) {
/* 215 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 222 */     int start = this.index;
/*     */     
/*     */     byte b;
/* 225 */     while (this.index < this.size && (b = this.buffer[this.index]) > 32 && b != 40 && b != 41 && b != 37 && b != 42 && b != 34 && b != 92 && b != Byte.MAX_VALUE && (delim == '\000' || b != delim))
/*     */     {
/* 227 */       this.index++;
/*     */     }
/* 229 */     return ASCIIUtility.toString(this.buffer, start, this.index);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readString(char delim) {
/* 238 */     skipSpaces();
/*     */     
/* 240 */     if (this.index >= this.size) {
/* 241 */       return null;
/*     */     }
/* 243 */     int start = this.index;
/* 244 */     while (this.index < this.size && this.buffer[this.index] != delim) {
/* 245 */       this.index++;
/*     */     }
/* 247 */     return ASCIIUtility.toString(this.buffer, start, this.index);
/*     */   }
/*     */   
/*     */   public String[] readStringList() {
/* 251 */     return readStringList(false);
/*     */   }
/*     */   
/*     */   public String[] readAtomStringList() {
/* 255 */     return readStringList(true);
/*     */   }
/*     */   
/*     */   private String[] readStringList(boolean atom) {
/* 259 */     skipSpaces();
/*     */     
/* 261 */     if (this.buffer[this.index] != 40)
/* 262 */       return null; 
/* 263 */     this.index++;
/*     */     
/* 265 */     Vector v = new Vector();
/*     */     do {
/* 267 */       v.addElement(atom ? readAtomString() : readString());
/* 268 */     } while (this.buffer[this.index++] != 41);
/*     */     
/* 270 */     int size = v.size();
/* 271 */     if (size > 0) {
/* 272 */       String[] s = new String[size];
/* 273 */       v.copyInto((Object[])s);
/* 274 */       return s;
/*     */     } 
/* 276 */     return null;
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
/*     */   public int readNumber() {
/* 288 */     skipSpaces();
/*     */     
/* 290 */     int start = this.index;
/* 291 */     while (this.index < this.size && Character.isDigit((char)this.buffer[this.index])) {
/* 292 */       this.index++;
/*     */     }
/* 294 */     if (this.index > start) {
/*     */       try {
/* 296 */         return ASCIIUtility.parseInt(this.buffer, start, this.index);
/* 297 */       } catch (NumberFormatException nex) {}
/*     */     }
/*     */     
/* 300 */     return -1;
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
/*     */   public long readLong() {
/* 312 */     skipSpaces();
/*     */     
/* 314 */     int start = this.index;
/* 315 */     while (this.index < this.size && Character.isDigit((char)this.buffer[this.index])) {
/* 316 */       this.index++;
/*     */     }
/* 318 */     if (this.index > start) {
/*     */       try {
/* 320 */         return ASCIIUtility.parseLong(this.buffer, start, this.index);
/* 321 */       } catch (NumberFormatException nex) {}
/*     */     }
/*     */     
/* 324 */     return -1L;
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
/*     */   public String readString() {
/* 336 */     return (String)parseString(false, true);
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
/*     */   public ByteArrayInputStream readBytes() {
/* 348 */     ByteArray ba = readByteArray();
/* 349 */     if (ba != null) {
/* 350 */       return ba.toByteArrayInputStream();
/*     */     }
/* 352 */     return null;
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
/*     */   public ByteArray readByteArray() {
/* 368 */     if (isContinuation()) {
/* 369 */       skipSpaces();
/* 370 */       return new ByteArray(this.buffer, this.index, this.size - this.index);
/*     */     } 
/* 372 */     return (ByteArray)parseString(false, false);
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
/*     */   public String readAtomString() {
/* 387 */     return (String)parseString(true, true);
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
/*     */   private Object parseString(boolean parseAtoms, boolean returnString) {
/* 399 */     skipSpaces();
/*     */     
/* 401 */     byte b = this.buffer[this.index];
/* 402 */     if (b == 34) {
/*     */       
/* 404 */       int start = ++this.index;
/* 405 */       int copyto = this.index;
/*     */       
/* 407 */       while (this.index < this.size && (b = this.buffer[this.index]) != 34) {
/* 408 */         if (b == 92)
/* 409 */           this.index++; 
/* 410 */         if (this.index != copyto)
/*     */         {
/*     */           
/* 413 */           this.buffer[copyto] = this.buffer[this.index];
/*     */         }
/* 415 */         copyto++;
/* 416 */         this.index++;
/*     */       } 
/* 418 */       if (this.index >= this.size)
/*     */       {
/*     */ 
/*     */         
/* 422 */         return null;
/*     */       }
/* 424 */       this.index++;
/*     */       
/* 426 */       if (returnString) {
/* 427 */         return ASCIIUtility.toString(this.buffer, start, copyto);
/*     */       }
/* 429 */       return new ByteArray(this.buffer, start, copyto - start);
/* 430 */     }  if (b == 123) {
/* 431 */       int start = ++this.index;
/*     */       
/* 433 */       while (this.buffer[this.index] != 125) {
/* 434 */         this.index++;
/*     */       }
/* 436 */       int count = 0;
/*     */       try {
/* 438 */         count = ASCIIUtility.parseInt(this.buffer, start, this.index);
/* 439 */       } catch (NumberFormatException nex) {
/*     */         
/* 441 */         return null;
/*     */       } 
/*     */       
/* 444 */       start = this.index + 3;
/* 445 */       this.index = start + count;
/*     */       
/* 447 */       if (returnString) {
/* 448 */         return ASCIIUtility.toString(this.buffer, start, start + count);
/*     */       }
/* 450 */       return new ByteArray(this.buffer, start, count);
/* 451 */     }  if (parseAtoms) {
/* 452 */       int start = this.index;
/*     */       
/* 454 */       String s = readAtom();
/* 455 */       if (returnString) {
/* 456 */         return s;
/*     */       }
/* 458 */       return new ByteArray(this.buffer, start, this.index);
/* 459 */     }  if (b == 78 || b == 110) {
/* 460 */       this.index += 3;
/* 461 */       return null;
/*     */     } 
/* 463 */     return null;
/*     */   }
/*     */   
/*     */   public int getType() {
/* 467 */     return this.type;
/*     */   }
/*     */   
/*     */   public boolean isContinuation() {
/* 471 */     return ((this.type & 0x3) == 1);
/*     */   }
/*     */   
/*     */   public boolean isTagged() {
/* 475 */     return ((this.type & 0x3) == 2);
/*     */   }
/*     */   
/*     */   public boolean isUnTagged() {
/* 479 */     return ((this.type & 0x3) == 3);
/*     */   }
/*     */   
/*     */   public boolean isOK() {
/* 483 */     return ((this.type & 0x1C) == 4);
/*     */   }
/*     */   
/*     */   public boolean isNO() {
/* 487 */     return ((this.type & 0x1C) == 8);
/*     */   }
/*     */   
/*     */   public boolean isBAD() {
/* 491 */     return ((this.type & 0x1C) == 12);
/*     */   }
/*     */   
/*     */   public boolean isBYE() {
/* 495 */     return ((this.type & 0x1C) == 16);
/*     */   }
/*     */   
/*     */   public boolean isSynthetic() {
/* 499 */     return ((this.type & 0x20) == 32);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getTag() {
/* 507 */     return this.tag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRest() {
/* 515 */     skipSpaces();
/* 516 */     return ASCIIUtility.toString(this.buffer, this.index, this.size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 523 */     this.index = this.pindex;
/*     */   }
/*     */   
/*     */   public String toString() {
/* 527 */     return ASCIIUtility.toString(this.buffer, 0, this.size);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\Response.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */