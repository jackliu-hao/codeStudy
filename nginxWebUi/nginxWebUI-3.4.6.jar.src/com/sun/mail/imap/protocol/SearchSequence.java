/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import com.sun.mail.iap.Argument;
/*     */ import java.io.IOException;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import javax.mail.Flags;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.search.AddressTerm;
/*     */ import javax.mail.search.AndTerm;
/*     */ import javax.mail.search.BodyTerm;
/*     */ import javax.mail.search.DateTerm;
/*     */ import javax.mail.search.FlagTerm;
/*     */ import javax.mail.search.FromStringTerm;
/*     */ import javax.mail.search.FromTerm;
/*     */ import javax.mail.search.HeaderTerm;
/*     */ import javax.mail.search.MessageIDTerm;
/*     */ import javax.mail.search.NotTerm;
/*     */ import javax.mail.search.OrTerm;
/*     */ import javax.mail.search.RecipientStringTerm;
/*     */ import javax.mail.search.RecipientTerm;
/*     */ import javax.mail.search.SearchException;
/*     */ import javax.mail.search.SearchTerm;
/*     */ import javax.mail.search.SizeTerm;
/*     */ import javax.mail.search.StringTerm;
/*     */ import javax.mail.search.SubjectTerm;
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
/*     */ public class SearchSequence
/*     */ {
/*     */   public Argument generateSequence(SearchTerm term, String charset) throws SearchException, IOException {
/*  71 */     if (term instanceof AndTerm)
/*  72 */       return and((AndTerm)term, charset); 
/*  73 */     if (term instanceof OrTerm)
/*  74 */       return or((OrTerm)term, charset); 
/*  75 */     if (term instanceof NotTerm)
/*  76 */       return not((NotTerm)term, charset); 
/*  77 */     if (term instanceof HeaderTerm)
/*  78 */       return header((HeaderTerm)term, charset); 
/*  79 */     if (term instanceof FlagTerm)
/*  80 */       return flag((FlagTerm)term); 
/*  81 */     if (term instanceof FromTerm) {
/*  82 */       FromTerm fterm = (FromTerm)term;
/*  83 */       return from(fterm.getAddress().toString(), charset);
/*     */     } 
/*  85 */     if (term instanceof FromStringTerm) {
/*  86 */       FromStringTerm fterm = (FromStringTerm)term;
/*  87 */       return from(fterm.getPattern(), charset);
/*     */     } 
/*  89 */     if (term instanceof RecipientTerm) {
/*  90 */       RecipientTerm rterm = (RecipientTerm)term;
/*  91 */       return recipient(rterm.getRecipientType(), rterm.getAddress().toString(), charset);
/*     */     } 
/*     */ 
/*     */     
/*  95 */     if (term instanceof RecipientStringTerm) {
/*  96 */       RecipientStringTerm rterm = (RecipientStringTerm)term;
/*  97 */       return recipient(rterm.getRecipientType(), rterm.getPattern(), charset);
/*     */     } 
/*     */ 
/*     */     
/* 101 */     if (term instanceof SubjectTerm)
/* 102 */       return subject((SubjectTerm)term, charset); 
/* 103 */     if (term instanceof BodyTerm)
/* 104 */       return body((BodyTerm)term, charset); 
/* 105 */     if (term instanceof SizeTerm)
/* 106 */       return size((SizeTerm)term); 
/* 107 */     if (term instanceof javax.mail.search.SentDateTerm)
/* 108 */       return sentdate((DateTerm)term); 
/* 109 */     if (term instanceof javax.mail.search.ReceivedDateTerm)
/* 110 */       return receiveddate((DateTerm)term); 
/* 111 */     if (term instanceof MessageIDTerm) {
/* 112 */       return messageid((MessageIDTerm)term, charset);
/*     */     }
/* 114 */     throw new SearchException("Search too complex");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAscii(SearchTerm term) {
/* 122 */     if (term instanceof AndTerm || term instanceof OrTerm)
/*     */     { SearchTerm[] terms;
/* 124 */       if (term instanceof AndTerm) {
/* 125 */         terms = ((AndTerm)term).getTerms();
/*     */       } else {
/* 127 */         terms = ((OrTerm)term).getTerms();
/*     */       } 
/* 129 */       for (int i = 0; i < terms.length; i++)
/* 130 */       { if (!isAscii(terms[i]))
/* 131 */           return false;  }  }
/* 132 */     else { if (term instanceof NotTerm)
/* 133 */         return isAscii(((NotTerm)term).getTerm()); 
/* 134 */       if (term instanceof StringTerm)
/* 135 */         return isAscii(((StringTerm)term).getPattern()); 
/* 136 */       if (term instanceof AddressTerm) {
/* 137 */         return isAscii(((AddressTerm)term).getAddress().toString());
/*     */       } }
/*     */     
/* 140 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAscii(String s) {
/* 147 */     int l = s.length();
/*     */     
/* 149 */     for (int i = 0; i < l; i++) {
/* 150 */       if (s.charAt(i) > '')
/* 151 */         return false; 
/*     */     } 
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Argument and(AndTerm term, String charset) throws SearchException, IOException {
/* 159 */     SearchTerm[] terms = term.getTerms();
/*     */     
/* 161 */     Argument result = generateSequence(terms[0], charset);
/*     */     
/* 163 */     for (int i = 1; i < terms.length; i++)
/* 164 */       result.append(generateSequence(terms[i], charset)); 
/* 165 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument or(OrTerm term, String charset) throws SearchException, IOException {
/* 170 */     SearchTerm[] terms = term.getTerms();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 176 */     if (terms.length > 2) {
/* 177 */       OrTerm orTerm; SearchTerm t = terms[0];
/*     */ 
/*     */       
/* 180 */       for (int i = 1; i < terms.length; i++) {
/* 181 */         orTerm = new OrTerm(t, terms[i]);
/*     */       }
/* 183 */       term = orTerm;
/*     */       
/* 185 */       terms = term.getTerms();
/*     */     } 
/*     */ 
/*     */     
/* 189 */     Argument result = new Argument();
/*     */ 
/*     */     
/* 192 */     if (terms.length > 1) {
/* 193 */       result.writeAtom("OR");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 200 */     if (terms[0] instanceof AndTerm || terms[0] instanceof FlagTerm) {
/* 201 */       result.writeArgument(generateSequence(terms[0], charset));
/*     */     } else {
/* 203 */       result.append(generateSequence(terms[0], charset));
/*     */     } 
/*     */     
/* 206 */     if (terms.length > 1) {
/* 207 */       if (terms[1] instanceof AndTerm || terms[1] instanceof FlagTerm) {
/* 208 */         result.writeArgument(generateSequence(terms[1], charset));
/*     */       } else {
/* 210 */         result.append(generateSequence(terms[1], charset));
/*     */       } 
/*     */     }
/* 213 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument not(NotTerm term, String charset) throws SearchException, IOException {
/* 218 */     Argument result = new Argument();
/*     */ 
/*     */     
/* 221 */     result.writeAtom("NOT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 228 */     SearchTerm nterm = term.getTerm();
/* 229 */     if (nterm instanceof AndTerm || nterm instanceof FlagTerm) {
/* 230 */       result.writeArgument(generateSequence(nterm, charset));
/*     */     } else {
/* 232 */       result.append(generateSequence(nterm, charset));
/*     */     } 
/* 234 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument header(HeaderTerm term, String charset) throws SearchException, IOException {
/* 239 */     Argument result = new Argument();
/* 240 */     result.writeAtom("HEADER");
/* 241 */     result.writeString(term.getHeaderName());
/* 242 */     result.writeString(term.getPattern(), charset);
/* 243 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument messageid(MessageIDTerm term, String charset) throws SearchException, IOException {
/* 248 */     Argument result = new Argument();
/* 249 */     result.writeAtom("HEADER");
/* 250 */     result.writeString("Message-ID");
/*     */     
/* 252 */     result.writeString(term.getPattern(), charset);
/* 253 */     return result;
/*     */   }
/*     */   
/*     */   protected Argument flag(FlagTerm term) throws SearchException {
/* 257 */     boolean set = term.getTestSet();
/*     */     
/* 259 */     Argument result = new Argument();
/*     */     
/* 261 */     Flags flags = term.getFlags();
/* 262 */     Flags.Flag[] sf = flags.getSystemFlags();
/* 263 */     String[] uf = flags.getUserFlags();
/* 264 */     if (sf.length == 0 && uf.length == 0)
/* 265 */       throw new SearchException("Invalid FlagTerm"); 
/*     */     int i;
/* 267 */     for (i = 0; i < sf.length; i++) {
/* 268 */       if (sf[i] == Flags.Flag.DELETED) {
/* 269 */         result.writeAtom(set ? "DELETED" : "UNDELETED");
/* 270 */       } else if (sf[i] == Flags.Flag.ANSWERED) {
/* 271 */         result.writeAtom(set ? "ANSWERED" : "UNANSWERED");
/* 272 */       } else if (sf[i] == Flags.Flag.DRAFT) {
/* 273 */         result.writeAtom(set ? "DRAFT" : "UNDRAFT");
/* 274 */       } else if (sf[i] == Flags.Flag.FLAGGED) {
/* 275 */         result.writeAtom(set ? "FLAGGED" : "UNFLAGGED");
/* 276 */       } else if (sf[i] == Flags.Flag.RECENT) {
/* 277 */         result.writeAtom(set ? "RECENT" : "OLD");
/* 278 */       } else if (sf[i] == Flags.Flag.SEEN) {
/* 279 */         result.writeAtom(set ? "SEEN" : "UNSEEN");
/*     */       } 
/*     */     } 
/* 282 */     for (i = 0; i < uf.length; i++) {
/* 283 */       result.writeAtom(set ? "KEYWORD" : "UNKEYWORD");
/* 284 */       result.writeAtom(uf[i]);
/*     */     } 
/*     */     
/* 287 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument from(String address, String charset) throws SearchException, IOException {
/* 292 */     Argument result = new Argument();
/* 293 */     result.writeAtom("FROM");
/* 294 */     result.writeString(address, charset);
/* 295 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Argument recipient(Message.RecipientType type, String address, String charset) throws SearchException, IOException {
/* 301 */     Argument result = new Argument();
/*     */     
/* 303 */     if (type == Message.RecipientType.TO) {
/* 304 */       result.writeAtom("TO");
/* 305 */     } else if (type == Message.RecipientType.CC) {
/* 306 */       result.writeAtom("CC");
/* 307 */     } else if (type == Message.RecipientType.BCC) {
/* 308 */       result.writeAtom("BCC");
/*     */     } else {
/* 310 */       throw new SearchException("Illegal Recipient type");
/*     */     } 
/* 312 */     result.writeString(address, charset);
/* 313 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument subject(SubjectTerm term, String charset) throws SearchException, IOException {
/* 318 */     Argument result = new Argument();
/*     */     
/* 320 */     result.writeAtom("SUBJECT");
/* 321 */     result.writeString(term.getPattern(), charset);
/* 322 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument body(BodyTerm term, String charset) throws SearchException, IOException {
/* 327 */     Argument result = new Argument();
/*     */     
/* 329 */     result.writeAtom("BODY");
/* 330 */     result.writeString(term.getPattern(), charset);
/* 331 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument size(SizeTerm term) throws SearchException {
/* 336 */     Argument result = new Argument();
/*     */     
/* 338 */     switch (term.getComparison()) {
/*     */       case 5:
/* 340 */         result.writeAtom("LARGER");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 350 */         result.writeNumber(term.getNumber());
/* 351 */         return result;case 2: result.writeAtom("SMALLER"); result.writeNumber(term.getNumber()); return result;
/*     */     } 
/*     */     throw new SearchException("Cannot handle Comparison");
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
/* 365 */   private static String[] monthTable = new String[] { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 371 */   protected Calendar cal = new GregorianCalendar();
/*     */   
/*     */   protected String toIMAPDate(Date date) {
/* 374 */     StringBuffer s = new StringBuffer();
/*     */     
/* 376 */     this.cal.setTime(date);
/*     */     
/* 378 */     s.append(this.cal.get(5)).append("-");
/* 379 */     s.append(monthTable[this.cal.get(2)]).append('-');
/* 380 */     s.append(this.cal.get(1));
/*     */     
/* 382 */     return s.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   protected Argument sentdate(DateTerm term) throws SearchException {
/* 387 */     Argument result = new Argument();
/* 388 */     String date = toIMAPDate(term.getDate());
/*     */     
/* 390 */     switch (term.getComparison()) {
/*     */       case 5:
/* 392 */         result.writeAtom("SENTSINCE " + date);
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
/* 413 */         return result;case 3: result.writeAtom("SENTON " + date); return result;case 2: result.writeAtom("SENTBEFORE " + date); return result;case 6: result.writeAtom("OR SENTSINCE " + date + " SENTON " + date); return result;case 1: result.writeAtom("OR SENTBEFORE " + date + " SENTON " + date); return result;case 4: result.writeAtom("NOT SENTON " + date); return result;
/*     */     } 
/*     */     throw new SearchException("Cannot handle Date Comparison");
/*     */   }
/*     */   protected Argument receiveddate(DateTerm term) throws SearchException {
/* 418 */     Argument result = new Argument();
/* 419 */     String date = toIMAPDate(term.getDate());
/*     */     
/* 421 */     switch (term.getComparison()) {
/*     */       case 5:
/* 423 */         result.writeAtom("SINCE " + date);
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
/* 444 */         return result;case 3: result.writeAtom("ON " + date); return result;case 2: result.writeAtom("BEFORE " + date); return result;case 6: result.writeAtom("OR SINCE " + date + " ON " + date); return result;case 1: result.writeAtom("OR BEFORE " + date + " ON " + date); return result;case 4: result.writeAtom("NOT ON " + date); return result;
/*     */     } 
/*     */     throw new SearchException("Cannot handle Date Comparison");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\SearchSequence.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */