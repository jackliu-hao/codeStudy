/*     */ package javax.mail.internet;
/*     */ 
/*     */ import com.sun.mail.util.LineInputStream;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.mail.Header;
/*     */ import javax.mail.MessagingException;
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
/*     */ public class InternetHeaders
/*     */ {
/*  85 */   private static final boolean ignoreWhitespaceLines = PropUtil.getBooleanSystemProperty("mail.mime.ignorewhitespacelines", false);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List headers;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static final class InternetHeader
/*     */     extends Header
/*     */   {
/*     */     String line;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InternetHeader(String l) {
/* 118 */       super("", "");
/* 119 */       int i = l.indexOf(':');
/* 120 */       if (i < 0) {
/*     */         
/* 122 */         this.name = l.trim();
/*     */       } else {
/* 124 */         this.name = l.substring(0, i).trim();
/*     */       } 
/* 126 */       this.line = l;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public InternetHeader(String n, String v) {
/* 133 */       super(n, "");
/* 134 */       if (v != null) {
/* 135 */         this.line = n + ": " + v;
/*     */       } else {
/* 137 */         this.line = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 144 */       int i = this.line.indexOf(':');
/* 145 */       if (i < 0) {
/* 146 */         return this.line;
/*     */       }
/*     */       int j;
/* 149 */       for (j = i + 1; j < this.line.length(); j++) {
/* 150 */         char c = this.line.charAt(j);
/* 151 */         if (c != ' ' && c != '\t' && c != '\r' && c != '\n')
/*     */           break; 
/*     */       } 
/* 154 */       return this.line.substring(j);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class matchEnum
/*     */     implements Enumeration
/*     */   {
/*     */     private Iterator e;
/*     */ 
/*     */     
/*     */     private String[] names;
/*     */ 
/*     */     
/*     */     private boolean match;
/*     */ 
/*     */     
/*     */     private boolean want_line;
/*     */ 
/*     */     
/*     */     private InternetHeaders.InternetHeader next_header;
/*     */ 
/*     */     
/*     */     matchEnum(List v, String[] n, boolean m, boolean l) {
/* 179 */       this.e = v.iterator();
/* 180 */       this.names = n;
/* 181 */       this.match = m;
/* 182 */       this.want_line = l;
/* 183 */       this.next_header = null;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean hasMoreElements() {
/* 192 */       if (this.next_header == null)
/* 193 */         this.next_header = nextMatch(); 
/* 194 */       return (this.next_header != null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object nextElement() {
/* 201 */       if (this.next_header == null) {
/* 202 */         this.next_header = nextMatch();
/*     */       }
/* 204 */       if (this.next_header == null) {
/* 205 */         throw new NoSuchElementException("No more headers");
/*     */       }
/* 207 */       InternetHeaders.InternetHeader h = this.next_header;
/* 208 */       this.next_header = null;
/* 209 */       if (this.want_line) {
/* 210 */         return h.line;
/*     */       }
/* 212 */       return new Header(h.getName(), h.getValue());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private InternetHeaders.InternetHeader nextMatch() {
/* 221 */       label23: while (this.e.hasNext()) {
/* 222 */         InternetHeaders.InternetHeader h = this.e.next();
/*     */ 
/*     */         
/* 225 */         if (h.line == null) {
/*     */           continue;
/*     */         }
/*     */         
/* 229 */         if (this.names == null) {
/* 230 */           return this.match ? null : h;
/*     */         }
/*     */         
/* 233 */         for (int i = 0; i < this.names.length; i++) {
/* 234 */           if (this.names[i].equalsIgnoreCase(h.getName())) {
/* 235 */             if (this.match) {
/* 236 */               return h;
/*     */             }
/*     */ 
/*     */             
/*     */             continue label23;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 245 */         if (!this.match)
/* 246 */           return h; 
/*     */       } 
/* 248 */       return null;
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
/*     */   public InternetHeaders() {
/* 275 */     this.headers = new ArrayList(40);
/* 276 */     this.headers.add(new InternetHeader("Return-Path", null));
/* 277 */     this.headers.add(new InternetHeader("Received", null));
/* 278 */     this.headers.add(new InternetHeader("Resent-Date", null));
/* 279 */     this.headers.add(new InternetHeader("Resent-From", null));
/* 280 */     this.headers.add(new InternetHeader("Resent-Sender", null));
/* 281 */     this.headers.add(new InternetHeader("Resent-To", null));
/* 282 */     this.headers.add(new InternetHeader("Resent-Cc", null));
/* 283 */     this.headers.add(new InternetHeader("Resent-Bcc", null));
/* 284 */     this.headers.add(new InternetHeader("Resent-Message-Id", null));
/* 285 */     this.headers.add(new InternetHeader("Date", null));
/* 286 */     this.headers.add(new InternetHeader("From", null));
/* 287 */     this.headers.add(new InternetHeader("Sender", null));
/* 288 */     this.headers.add(new InternetHeader("Reply-To", null));
/* 289 */     this.headers.add(new InternetHeader("To", null));
/* 290 */     this.headers.add(new InternetHeader("Cc", null));
/* 291 */     this.headers.add(new InternetHeader("Bcc", null));
/* 292 */     this.headers.add(new InternetHeader("Message-Id", null));
/* 293 */     this.headers.add(new InternetHeader("In-Reply-To", null));
/* 294 */     this.headers.add(new InternetHeader("References", null));
/* 295 */     this.headers.add(new InternetHeader("Subject", null));
/* 296 */     this.headers.add(new InternetHeader("Comments", null));
/* 297 */     this.headers.add(new InternetHeader("Keywords", null));
/* 298 */     this.headers.add(new InternetHeader("Errors-To", null));
/* 299 */     this.headers.add(new InternetHeader("MIME-Version", null));
/* 300 */     this.headers.add(new InternetHeader("Content-Type", null));
/* 301 */     this.headers.add(new InternetHeader("Content-Transfer-Encoding", null));
/* 302 */     this.headers.add(new InternetHeader("Content-MD5", null));
/* 303 */     this.headers.add(new InternetHeader(":", null));
/* 304 */     this.headers.add(new InternetHeader("Content-Length", null));
/* 305 */     this.headers.add(new InternetHeader("Status", null));
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
/*     */   public InternetHeaders(InputStream is) throws MessagingException {
/* 323 */     this.headers = new ArrayList(40);
/* 324 */     load(is);
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
/*     */   public void load(InputStream is) throws MessagingException {
/* 344 */     LineInputStream lis = new LineInputStream(is);
/* 345 */     String prevline = null;
/*     */     
/* 347 */     StringBuffer lineBuffer = new StringBuffer();
/*     */     
/*     */     try {
/*     */       String line;
/*     */       do {
/* 352 */         line = lis.readLine();
/* 353 */         if (line != null && (line.startsWith(" ") || line.startsWith("\t"))) {
/*     */ 
/*     */           
/* 356 */           if (prevline != null) {
/* 357 */             lineBuffer.append(prevline);
/* 358 */             prevline = null;
/*     */           } 
/* 360 */           lineBuffer.append("\r\n");
/* 361 */           lineBuffer.append(line);
/*     */         } else {
/*     */           
/* 364 */           if (prevline != null) {
/* 365 */             addHeaderLine(prevline);
/* 366 */           } else if (lineBuffer.length() > 0) {
/*     */             
/* 368 */             addHeaderLine(lineBuffer.toString());
/* 369 */             lineBuffer.setLength(0);
/*     */           } 
/* 371 */           prevline = line;
/*     */         } 
/* 373 */       } while (line != null && !isEmpty(line));
/* 374 */     } catch (IOException ioex) {
/* 375 */       throw new MessagingException("Error in input stream", ioex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final boolean isEmpty(String line) {
/* 383 */     return (line.length() == 0 || (ignoreWhitespaceLines && line.trim().length() == 0));
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
/*     */   public String[] getHeader(String name) {
/* 396 */     Iterator e = this.headers.iterator();
/*     */     
/* 398 */     List v = new ArrayList();
/*     */     
/* 400 */     while (e.hasNext()) {
/* 401 */       InternetHeader h = e.next();
/* 402 */       if (name.equalsIgnoreCase(h.getName()) && h.line != null) {
/* 403 */         v.add(h.getValue());
/*     */       }
/*     */     } 
/* 406 */     if (v.size() == 0) {
/* 407 */       return null;
/*     */     }
/* 409 */     String[] r = new String[v.size()];
/* 410 */     r = v.<String>toArray(r);
/* 411 */     return r;
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
/*     */   public String getHeader(String name, String delimiter) {
/* 427 */     String[] s = getHeader(name);
/*     */     
/* 429 */     if (s == null) {
/* 430 */       return null;
/*     */     }
/* 432 */     if (s.length == 1 || delimiter == null) {
/* 433 */       return s[0];
/*     */     }
/* 435 */     StringBuffer r = new StringBuffer(s[0]);
/* 436 */     for (int i = 1; i < s.length; i++) {
/* 437 */       r.append(delimiter);
/* 438 */       r.append(s[i]);
/*     */     } 
/* 440 */     return r.toString();
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
/*     */   public void setHeader(String name, String value) {
/* 454 */     boolean found = false;
/*     */     
/* 456 */     for (int i = 0; i < this.headers.size(); i++) {
/* 457 */       InternetHeader h = this.headers.get(i);
/* 458 */       if (name.equalsIgnoreCase(h.getName())) {
/* 459 */         if (!found) {
/*     */           int j;
/* 461 */           if (h.line != null && (j = h.line.indexOf(':')) >= 0) {
/* 462 */             h.line = h.line.substring(0, j + 1) + " " + value;
/*     */           } else {
/*     */             
/* 465 */             h.line = name + ": " + value;
/*     */           } 
/* 467 */           found = true;
/*     */         } else {
/* 469 */           this.headers.remove(i);
/* 470 */           i--;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 475 */     if (!found) {
/* 476 */       addHeader(name, value);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeader(String name, String value) {
/* 496 */     int pos = this.headers.size();
/* 497 */     boolean addReverse = (name.equalsIgnoreCase("Received") || name.equalsIgnoreCase("Return-Path"));
/*     */ 
/*     */     
/* 500 */     if (addReverse)
/* 501 */       pos = 0; 
/* 502 */     for (int i = this.headers.size() - 1; i >= 0; i--) {
/* 503 */       InternetHeader h = this.headers.get(i);
/* 504 */       if (name.equalsIgnoreCase(h.getName())) {
/* 505 */         if (addReverse) {
/* 506 */           pos = i;
/*     */         } else {
/* 508 */           this.headers.add(i + 1, new InternetHeader(name, value));
/*     */           
/*     */           return;
/*     */         } 
/*     */       }
/* 513 */       if (!addReverse && h.getName().equals(":"))
/* 514 */         pos = i; 
/*     */     } 
/* 516 */     this.headers.add(pos, new InternetHeader(name, value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHeader(String name) {
/* 524 */     for (int i = 0; i < this.headers.size(); i++) {
/* 525 */       InternetHeader h = this.headers.get(i);
/* 526 */       if (name.equalsIgnoreCase(h.getName())) {
/* 527 */         h.line = null;
/*     */       }
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
/*     */   public Enumeration getAllHeaders() {
/* 541 */     return new matchEnum(this.headers, null, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaders(String[] names) {
/* 550 */     return new matchEnum(this.headers, names, true, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaders(String[] names) {
/* 559 */     return new matchEnum(this.headers, names, false, false);
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
/*     */   public void addHeaderLine(String line) {
/*     */     try {
/* 574 */       char c = line.charAt(0);
/* 575 */       if (c == ' ' || c == '\t')
/* 576 */       { InternetHeader h = this.headers.get(this.headers.size() - 1);
/*     */         
/* 578 */         h.line += "\r\n" + line; }
/*     */       else
/* 580 */       { this.headers.add(new InternetHeader(line)); } 
/* 581 */     } catch (StringIndexOutOfBoundsException e) {
/*     */       
/*     */       return;
/* 584 */     } catch (NoSuchElementException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getAllHeaderLines() {
/* 593 */     return getNonMatchingHeaderLines(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getMatchingHeaderLines(String[] names) {
/* 600 */     return new matchEnum(this.headers, names, true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNonMatchingHeaderLines(String[] names) {
/* 607 */     return new matchEnum(this.headers, names, false, true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\InternetHeaders.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */