/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.ParsingException;
/*     */ import com.sun.mail.iap.Response;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import java.util.Vector;
/*     */ import javax.mail.internet.ParameterList;
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
/*     */ public class BODYSTRUCTURE
/*     */   implements Item
/*     */ {
/*  57 */   static final char[] name = new char[] { 'B', 'O', 'D', 'Y', 'S', 'T', 'R', 'U', 'C', 'T', 'U', 'R', 'E' };
/*     */   
/*     */   public int msgno;
/*     */   
/*     */   public String type;
/*     */   public String subtype;
/*     */   public String encoding;
/*  64 */   public int lines = -1;
/*  65 */   public int size = -1;
/*     */   
/*     */   public String disposition;
/*     */   
/*     */   public String id;
/*     */   public String description;
/*     */   public String md5;
/*     */   public String attachment;
/*     */   public ParameterList cParams;
/*     */   public ParameterList dParams;
/*     */   public String[] language;
/*     */   public BODYSTRUCTURE[] bodies;
/*     */   public ENVELOPE envelope;
/*  78 */   private static int SINGLE = 1;
/*  79 */   private static int MULTI = 2;
/*  80 */   private static int NESTED = 3;
/*     */   
/*     */   private int processedType;
/*     */   
/*  84 */   private static boolean parseDebug = PropUtil.getBooleanSystemProperty("mail.imap.parse.debug", false);
/*     */ 
/*     */ 
/*     */   
/*     */   public BODYSTRUCTURE(FetchResponse r) throws ParsingException {
/*  89 */     if (parseDebug)
/*  90 */       System.out.println("DEBUG IMAP: parsing BODYSTRUCTURE"); 
/*  91 */     this.msgno = r.getNumber();
/*  92 */     if (parseDebug) {
/*  93 */       System.out.println("DEBUG IMAP: msgno " + this.msgno);
/*     */     }
/*  95 */     r.skipSpaces();
/*     */     
/*  97 */     if (r.readByte() != 40) {
/*  98 */       throw new ParsingException("BODYSTRUCTURE parse error: missing ``('' at start");
/*     */     }
/*     */     
/* 101 */     if (r.peekByte() == 40) {
/* 102 */       if (parseDebug)
/* 103 */         System.out.println("DEBUG IMAP: parsing multipart"); 
/* 104 */       this.type = "multipart";
/* 105 */       this.processedType = MULTI;
/* 106 */       Vector v = new Vector(1);
/* 107 */       int i = 1;
/*     */       do {
/* 109 */         v.addElement(new BODYSTRUCTURE(r));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 116 */         r.skipSpaces();
/* 117 */       } while (r.peekByte() == 40);
/*     */ 
/*     */       
/* 120 */       this.bodies = new BODYSTRUCTURE[v.size()];
/* 121 */       v.copyInto((Object[])this.bodies);
/*     */       
/* 123 */       this.subtype = r.readString();
/* 124 */       if (parseDebug) {
/* 125 */         System.out.println("DEBUG IMAP: subtype " + this.subtype);
/*     */       }
/* 127 */       if (r.readByte() == 41) {
/* 128 */         if (parseDebug) {
/* 129 */           System.out.println("DEBUG IMAP: parse DONE");
/*     */         }
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 135 */       if (parseDebug) {
/* 136 */         System.out.println("DEBUG IMAP: parsing extension data");
/*     */       }
/* 138 */       this.cParams = parseParameters(r);
/* 139 */       if (r.readByte() == 41) {
/* 140 */         if (parseDebug) {
/* 141 */           System.out.println("DEBUG IMAP: body parameters DONE");
/*     */         }
/*     */         
/*     */         return;
/*     */       } 
/* 146 */       byte b = r.readByte();
/* 147 */       if (b == 40) {
/* 148 */         if (parseDebug)
/* 149 */           System.out.println("DEBUG IMAP: parse disposition"); 
/* 150 */         this.disposition = r.readString();
/* 151 */         if (parseDebug) {
/* 152 */           System.out.println("DEBUG IMAP: disposition " + this.disposition);
/*     */         }
/* 154 */         this.dParams = parseParameters(r);
/* 155 */         if (r.readByte() != 41) {
/* 156 */           throw new ParsingException("BODYSTRUCTURE parse error: missing ``)'' at end of disposition in multipart");
/*     */         }
/*     */         
/* 159 */         if (parseDebug)
/* 160 */           System.out.println("DEBUG IMAP: disposition DONE"); 
/* 161 */       } else if (b == 78 || b == 110) {
/* 162 */         if (parseDebug)
/* 163 */           System.out.println("DEBUG IMAP: disposition NIL"); 
/* 164 */         r.skip(2);
/*     */       } else {
/* 166 */         throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "bad multipart disposition, b " + b);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 174 */       if ((b = r.readByte()) == 41) {
/* 175 */         if (parseDebug) {
/* 176 */           System.out.println("DEBUG IMAP: no body-fld-lang");
/*     */         }
/*     */         return;
/*     */       } 
/* 180 */       if (b != 32) {
/* 181 */         throw new ParsingException("BODYSTRUCTURE parse error: missing space after disposition");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 186 */       if (r.peekByte() == 40) {
/* 187 */         this.language = r.readStringList();
/* 188 */         if (parseDebug) {
/* 189 */           System.out.println("DEBUG IMAP: language len " + this.language.length);
/*     */         }
/*     */       } else {
/* 192 */         String l = r.readString();
/* 193 */         if (l != null) {
/* 194 */           String[] la = { l };
/* 195 */           this.language = la;
/* 196 */           if (parseDebug) {
/* 197 */             System.out.println("DEBUG IMAP: language " + l);
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 205 */       while (r.readByte() == 32) {
/* 206 */         parseBodyExtension(r);
/*     */       }
/*     */     } else {
/* 209 */       if (parseDebug)
/* 210 */         System.out.println("DEBUG IMAP: single part"); 
/* 211 */       this.type = r.readString();
/* 212 */       if (parseDebug)
/* 213 */         System.out.println("DEBUG IMAP: type " + this.type); 
/* 214 */       this.processedType = SINGLE;
/* 215 */       this.subtype = r.readString();
/* 216 */       if (parseDebug) {
/* 217 */         System.out.println("DEBUG IMAP: subtype " + this.subtype);
/*     */       }
/*     */       
/* 220 */       if (this.type == null) {
/* 221 */         this.type = "application";
/* 222 */         this.subtype = "octet-stream";
/*     */       } 
/* 224 */       this.cParams = parseParameters(r);
/* 225 */       if (parseDebug)
/* 226 */         System.out.println("DEBUG IMAP: cParams " + this.cParams); 
/* 227 */       this.id = r.readString();
/* 228 */       if (parseDebug)
/* 229 */         System.out.println("DEBUG IMAP: id " + this.id); 
/* 230 */       this.description = r.readString();
/* 231 */       if (parseDebug) {
/* 232 */         System.out.println("DEBUG IMAP: description " + this.description);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 237 */       this.encoding = r.readAtomString();
/* 238 */       if (this.encoding != null && this.encoding.equalsIgnoreCase("NIL"))
/* 239 */         this.encoding = null; 
/* 240 */       if (parseDebug)
/* 241 */         System.out.println("DEBUG IMAP: encoding " + this.encoding); 
/* 242 */       this.size = r.readNumber();
/* 243 */       if (parseDebug)
/* 244 */         System.out.println("DEBUG IMAP: size " + this.size); 
/* 245 */       if (this.size < 0) {
/* 246 */         throw new ParsingException("BODYSTRUCTURE parse error: bad ``size'' element");
/*     */       }
/*     */ 
/*     */       
/* 250 */       if (this.type.equalsIgnoreCase("text")) {
/* 251 */         this.lines = r.readNumber();
/* 252 */         if (parseDebug)
/* 253 */           System.out.println("DEBUG IMAP: lines " + this.lines); 
/* 254 */         if (this.lines < 0) {
/* 255 */           throw new ParsingException("BODYSTRUCTURE parse error: bad ``lines'' element");
/*     */         }
/* 257 */       } else if (this.type.equalsIgnoreCase("message") && this.subtype.equalsIgnoreCase("rfc822")) {
/*     */ 
/*     */         
/* 260 */         this.processedType = NESTED;
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 265 */         r.skipSpaces();
/* 266 */         if (r.peekByte() == 40) {
/* 267 */           this.envelope = new ENVELOPE(r);
/* 268 */           if (parseDebug) {
/* 269 */             System.out.println("DEBUG IMAP: got envelope of nested message");
/*     */           }
/* 271 */           BODYSTRUCTURE[] bs = { new BODYSTRUCTURE(r) };
/* 272 */           this.bodies = bs;
/* 273 */           this.lines = r.readNumber();
/* 274 */           if (parseDebug)
/* 275 */             System.out.println("DEBUG IMAP: lines " + this.lines); 
/* 276 */           if (this.lines < 0) {
/* 277 */             throw new ParsingException("BODYSTRUCTURE parse error: bad ``lines'' element");
/*     */           }
/*     */         }
/* 280 */         else if (parseDebug) {
/* 281 */           System.out.println("DEBUG IMAP: missing envelope and body of nested message");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 286 */         r.skipSpaces();
/* 287 */         byte bn = r.peekByte();
/* 288 */         if (Character.isDigit((char)bn)) {
/* 289 */           throw new ParsingException("BODYSTRUCTURE parse error: server erroneously included ``lines'' element with type " + this.type + "/" + this.subtype);
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 295 */       if (r.peekByte() == 41) {
/* 296 */         r.readByte();
/* 297 */         if (parseDebug) {
/* 298 */           System.out.println("DEBUG IMAP: parse DONE");
/*     */         }
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 305 */       this.md5 = r.readString();
/* 306 */       if (r.readByte() == 41) {
/* 307 */         if (parseDebug) {
/* 308 */           System.out.println("DEBUG IMAP: no MD5 DONE");
/*     */         }
/*     */         
/*     */         return;
/*     */       } 
/* 313 */       byte b = r.readByte();
/* 314 */       if (b == 40) {
/* 315 */         this.disposition = r.readString();
/* 316 */         if (parseDebug) {
/* 317 */           System.out.println("DEBUG IMAP: disposition " + this.disposition);
/*     */         }
/* 319 */         this.dParams = parseParameters(r);
/* 320 */         if (parseDebug)
/* 321 */           System.out.println("DEBUG IMAP: dParams " + this.dParams); 
/* 322 */         if (r.readByte() != 41) {
/* 323 */           throw new ParsingException("BODYSTRUCTURE parse error: missing ``)'' at end of disposition");
/*     */         }
/*     */       }
/* 326 */       else if (b == 78 || b == 110) {
/* 327 */         if (parseDebug)
/* 328 */           System.out.println("DEBUG IMAP: disposition NIL"); 
/* 329 */         r.skip(2);
/*     */       } else {
/* 331 */         throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "bad single part disposition, b " + b);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 337 */       if (r.readByte() == 41) {
/* 338 */         if (parseDebug) {
/* 339 */           System.out.println("DEBUG IMAP: disposition DONE");
/*     */         }
/*     */         
/*     */         return;
/*     */       } 
/* 344 */       if (r.peekByte() == 40) {
/* 345 */         this.language = r.readStringList();
/* 346 */         if (parseDebug) {
/* 347 */           System.out.println("DEBUG IMAP: language len " + this.language.length);
/*     */         }
/*     */       } else {
/* 350 */         String l = r.readString();
/* 351 */         if (l != null) {
/* 352 */           String[] la = { l };
/* 353 */           this.language = la;
/* 354 */           if (parseDebug) {
/* 355 */             System.out.println("DEBUG IMAP: language " + l);
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 363 */       while (r.readByte() == 32)
/* 364 */         parseBodyExtension(r); 
/* 365 */       if (parseDebug)
/* 366 */         System.out.println("DEBUG IMAP: all DONE"); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isMulti() {
/* 371 */     return (this.processedType == MULTI);
/*     */   }
/*     */   
/*     */   public boolean isSingle() {
/* 375 */     return (this.processedType == SINGLE);
/*     */   }
/*     */   
/*     */   public boolean isNested() {
/* 379 */     return (this.processedType == NESTED);
/*     */   }
/*     */ 
/*     */   
/*     */   private ParameterList parseParameters(Response r) throws ParsingException {
/* 384 */     r.skipSpaces();
/*     */     
/* 386 */     ParameterList list = null;
/* 387 */     byte b = r.readByte();
/* 388 */     if (b == 40)
/* 389 */     { list = new ParameterList();
/*     */       while (true)
/* 391 */       { String name = r.readString();
/* 392 */         if (parseDebug)
/* 393 */           System.out.println("DEBUG IMAP: parameter name " + name); 
/* 394 */         if (name == null) {
/* 395 */           throw new ParsingException("BODYSTRUCTURE parse error: " + this.type + "/" + this.subtype + ": " + "null name in parameter list");
/*     */         }
/*     */ 
/*     */         
/* 399 */         String value = r.readString();
/* 400 */         if (parseDebug)
/* 401 */           System.out.println("DEBUG IMAP: parameter value " + value); 
/* 402 */         list.set(name, value);
/* 403 */         if (r.readByte() == 41)
/* 404 */         { list.set(null, "DONE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 412 */           return list; }  }  }  if (b == 78 || b == 110) { if (parseDebug) System.out.println("DEBUG IMAP: parameter list NIL");  r.skip(2); } else { throw new ParsingException("Parameter list parse error"); }  return list;
/*     */   }
/*     */   
/*     */   private void parseBodyExtension(Response r) throws ParsingException {
/* 416 */     r.skipSpaces();
/*     */     
/* 418 */     byte b = r.peekByte();
/* 419 */     if (b == 40) {
/* 420 */       r.skip(1);
/*     */       do {
/* 422 */         parseBodyExtension(r);
/* 423 */       } while (r.readByte() != 41);
/* 424 */     } else if (Character.isDigit((char)b)) {
/* 425 */       r.readNumber();
/*     */     } else {
/* 427 */       r.readString();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\BODYSTRUCTURE.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */