/*     */ package javax.mail.internet;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import com.sun.mail.util.PropUtil;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ParameterList
/*     */ {
/*  90 */   private Map list = new LinkedHashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Set multisegmentNames;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map slist;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 139 */   private String lastName = null;
/*     */   
/* 141 */   private static final boolean encodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.encodeparameters", false);
/*     */   
/* 143 */   private static final boolean decodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters", false);
/*     */   
/* 145 */   private static final boolean decodeParametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters.strict", false);
/*     */ 
/*     */   
/* 148 */   private static final boolean applehack = PropUtil.getBooleanSystemProperty("mail.mime.applefilenames", false);
/*     */   
/* 150 */   private static final boolean windowshack = PropUtil.getBooleanSystemProperty("mail.mime.windowsfilenames", false);
/*     */   
/* 152 */   private static final boolean parametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.parameters.strict", true);
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Value
/*     */   {
/*     */     String value;
/*     */ 
/*     */     
/*     */     String charset;
/*     */ 
/*     */     
/*     */     String encodedValue;
/*     */ 
/*     */ 
/*     */     
/*     */     private Value() {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class MultiValue
/*     */     extends ArrayList
/*     */   {
/*     */     String value;
/*     */ 
/*     */     
/*     */     private MultiValue() {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ParamEnum
/*     */     implements Enumeration
/*     */   {
/*     */     private Iterator it;
/*     */ 
/*     */     
/*     */     ParamEnum(Iterator it) {
/* 190 */       this.it = it;
/*     */     }
/*     */     
/*     */     public boolean hasMoreElements() {
/* 194 */       return this.it.hasNext();
/*     */     }
/*     */     
/*     */     public Object nextElement() {
/* 198 */       return this.it.next();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParameterList() {
/* 207 */     if (decodeParameters) {
/* 208 */       this.multisegmentNames = new HashSet();
/* 209 */       this.slist = new HashMap();
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
/*     */   public ParameterList(String s) throws ParseException {
/* 224 */     this();
/*     */     
/* 226 */     HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");
/*     */     while (true) {
/* 228 */       HeaderTokenizer.Token tk = h.next();
/* 229 */       int type = tk.getType();
/*     */ 
/*     */       
/* 232 */       if (type == -4) {
/*     */         break;
/*     */       }
/* 235 */       if ((char)type == ';') {
/*     */         
/* 237 */         tk = h.next();
/*     */         
/* 239 */         if (tk.getType() == -4) {
/*     */           break;
/*     */         }
/* 242 */         if (tk.getType() != -1) {
/* 243 */           throw new ParseException("Expected parameter name, got \"" + tk.getValue() + "\"");
/*     */         }
/* 245 */         String name = tk.getValue().toLowerCase(Locale.ENGLISH);
/*     */ 
/*     */         
/* 248 */         tk = h.next();
/* 249 */         if ((char)tk.getType() != '=') {
/* 250 */           throw new ParseException("Expected '=', got \"" + tk.getValue() + "\"");
/*     */         }
/*     */ 
/*     */         
/* 254 */         if (windowshack && (name.equals("name") || name.equals("filename"))) {
/*     */           
/* 256 */           tk = h.next(';', true);
/* 257 */         } else if (parametersStrict) {
/* 258 */           tk = h.next();
/*     */         } else {
/* 260 */           tk = h.next(';');
/* 261 */         }  type = tk.getType();
/*     */         
/* 263 */         if (type != -1 && type != -2)
/*     */         {
/* 265 */           throw new ParseException("Expected parameter value, got \"" + tk.getValue() + "\"");
/*     */         }
/*     */         
/* 268 */         String value = tk.getValue();
/* 269 */         this.lastName = name;
/* 270 */         if (decodeParameters) {
/* 271 */           putEncodedName(name, value); continue;
/*     */         } 
/* 273 */         this.list.put(name, value);
/*     */ 
/*     */ 
/*     */         
/*     */         continue;
/*     */       } 
/*     */ 
/*     */       
/* 281 */       if (type == -1 && this.lastName != null && ((applehack && (this.lastName.equals("name") || this.lastName.equals("filename"))) || !parametersStrict)) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 288 */         String lastValue = (String)this.list.get(this.lastName);
/* 289 */         String value = lastValue + " " + tk.getValue();
/* 290 */         this.list.put(this.lastName, value); continue;
/*     */       } 
/* 292 */       throw new ParseException("Expected ';', got \"" + tk.getValue() + "\"");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 298 */     if (decodeParameters)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 303 */       combineMultisegmentNames(false);
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
/*     */   private void putEncodedName(String name, String value) throws ParseException {
/* 319 */     int star = name.indexOf('*');
/* 320 */     if (star < 0) {
/*     */       
/* 322 */       this.list.put(name, value);
/* 323 */     } else if (star == name.length() - 1) {
/*     */       
/* 325 */       name = name.substring(0, star);
/* 326 */       Value v = extractCharset(value);
/*     */       try {
/* 328 */         v.value = decodeBytes(v.value, v.charset);
/* 329 */       } catch (UnsupportedEncodingException ex) {
/* 330 */         if (decodeParametersStrict)
/* 331 */           throw new ParseException(ex.toString()); 
/*     */       } 
/* 333 */       this.list.put(name, v);
/*     */     } else {
/*     */       Object v;
/* 336 */       String rname = name.substring(0, star);
/* 337 */       this.multisegmentNames.add(rname);
/* 338 */       this.list.put(rname, "");
/*     */ 
/*     */       
/* 341 */       if (name.endsWith("*")) {
/*     */         
/* 343 */         if (name.endsWith("*0*")) {
/* 344 */           v = extractCharset(value);
/*     */         } else {
/* 346 */           v = new Value();
/* 347 */           ((Value)v).encodedValue = value;
/* 348 */           ((Value)v).value = value;
/*     */         } 
/* 350 */         name = name.substring(0, name.length() - 1);
/*     */       } else {
/*     */         
/* 353 */         v = value;
/*     */       } 
/* 355 */       this.slist.put(name, v);
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
/*     */   private void combineMultisegmentNames(boolean keepConsistentOnFailure) throws ParseException {
/* 368 */     boolean success = false;
/*     */     try {
/* 370 */       Iterator it = this.multisegmentNames.iterator();
/* 371 */       while (it.hasNext()) {
/* 372 */         String name = it.next();
/* 373 */         StringBuffer sb = new StringBuffer();
/* 374 */         MultiValue mv = new MultiValue();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 379 */         String charset = null;
/* 380 */         ByteArrayOutputStream bos = new ByteArrayOutputStream();
/*     */         int segment;
/* 382 */         for (segment = 0;; segment++) {
/* 383 */           String sname = name + "*" + segment;
/* 384 */           Object v = this.slist.get(sname);
/* 385 */           if (v == null)
/*     */             break; 
/* 387 */           mv.add((E)v);
/*     */           try {
/* 389 */             if (v instanceof Value) {
/* 390 */               Value vv = (Value)v;
/* 391 */               if (segment == 0) {
/*     */ 
/*     */                 
/* 394 */                 charset = vv.charset;
/*     */               }
/* 396 */               else if (charset == null) {
/*     */                 
/* 398 */                 this.multisegmentNames.remove(name);
/*     */                 
/*     */                 break;
/*     */               } 
/* 402 */               decodeBytes(vv.value, bos);
/*     */             } else {
/* 404 */               bos.write(ASCIIUtility.getBytes((String)v));
/*     */             } 
/* 406 */           } catch (IOException ex) {}
/*     */ 
/*     */           
/* 409 */           this.slist.remove(sname);
/*     */         } 
/* 411 */         if (segment == 0) {
/*     */           
/* 413 */           this.list.remove(name); continue;
/*     */         } 
/*     */         try {
/* 416 */           if (charset != null)
/* 417 */           { mv.value = bos.toString(charset); }
/*     */           else
/* 419 */           { mv.value = bos.toString(); } 
/* 420 */         } catch (UnsupportedEncodingException uex) {
/* 421 */           if (decodeParametersStrict) {
/* 422 */             throw new ParseException(uex.toString());
/*     */           }
/* 424 */           mv.value = bos.toString(0);
/*     */         } 
/* 426 */         this.list.put(name, mv);
/*     */       } 
/*     */       
/* 429 */       success = true;
/*     */ 
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */       
/* 436 */       if (keepConsistentOnFailure || success) {
/*     */ 
/*     */         
/* 439 */         if (this.slist.size() > 0) {
/*     */           
/* 441 */           Iterator sit = this.slist.values().iterator();
/* 442 */           while (sit.hasNext()) {
/* 443 */             Object v = sit.next();
/* 444 */             if (v instanceof Value) {
/* 445 */               Value vv = (Value)v;
/*     */               try {
/* 447 */                 vv.value = decodeBytes(vv.value, vv.charset);
/*     */               }
/* 449 */               catch (UnsupportedEncodingException ex) {
/* 450 */                 if (decodeParametersStrict)
/* 451 */                   throw new ParseException(ex.toString()); 
/*     */               } 
/*     */             } 
/*     */           } 
/* 455 */           this.list.putAll(this.slist);
/*     */         } 
/*     */ 
/*     */         
/* 459 */         this.multisegmentNames.clear();
/* 460 */         this.slist.clear();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 471 */     return this.list.size();
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
/*     */   public String get(String name) {
/*     */     String value;
/* 485 */     Object v = this.list.get(name.trim().toLowerCase(Locale.ENGLISH));
/* 486 */     if (v instanceof MultiValue) {
/* 487 */       value = ((MultiValue)v).value;
/* 488 */     } else if (v instanceof Value) {
/* 489 */       value = ((Value)v).value;
/*     */     } else {
/* 491 */       value = (String)v;
/* 492 */     }  return value;
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
/*     */   public void set(String name, String value) {
/* 505 */     if (name == null && value != null && value.equals("DONE")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 512 */       if (decodeParameters && this.multisegmentNames.size() > 0) {
/*     */         try {
/* 514 */           combineMultisegmentNames(true);
/* 515 */         } catch (ParseException pex) {}
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 521 */     name = name.trim().toLowerCase(Locale.ENGLISH);
/* 522 */     if (decodeParameters) {
/*     */       try {
/* 524 */         putEncodedName(name, value);
/* 525 */       } catch (ParseException pex) {
/*     */         
/* 527 */         this.list.put(name, value);
/*     */       } 
/*     */     } else {
/* 530 */       this.list.put(name, value);
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
/*     */   public void set(String name, String value, String charset) {
/* 546 */     if (encodeParameters)
/* 547 */     { Value ev = encodeValue(value, charset);
/*     */       
/* 549 */       if (ev != null) {
/* 550 */         this.list.put(name.trim().toLowerCase(Locale.ENGLISH), ev);
/*     */       } else {
/* 552 */         set(name, value);
/*     */       }  }
/* 554 */     else { set(name, value); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(String name) {
/* 564 */     this.list.remove(name.trim().toLowerCase(Locale.ENGLISH));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration getNames() {
/* 574 */     return new ParamEnum(this.list.keySet().iterator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 584 */     return toString(0);
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
/*     */   public String toString(int used) {
/* 602 */     ToStringBuffer sb = new ToStringBuffer(used);
/* 603 */     Iterator e = this.list.keySet().iterator();
/*     */     
/* 605 */     while (e.hasNext()) {
/* 606 */       String name = e.next();
/* 607 */       Object v = this.list.get(name);
/* 608 */       if (v instanceof MultiValue) {
/* 609 */         MultiValue vv = (MultiValue)v;
/* 610 */         String ns = name + "*";
/* 611 */         for (int i = 0; i < vv.size(); i++) {
/* 612 */           Object va = vv.get(i);
/* 613 */           if (va instanceof Value)
/* 614 */           { sb.addNV(ns + i + "*", ((Value)va).encodedValue); }
/*     */           else
/* 616 */           { sb.addNV(ns + i, (String)va); } 
/*     */         }  continue;
/* 618 */       }  if (v instanceof Value) {
/* 619 */         sb.addNV(name + "*", ((Value)v).encodedValue); continue;
/*     */       } 
/* 621 */       sb.addNV(name, (String)v);
/*     */     } 
/* 623 */     return sb.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static class ToStringBuffer
/*     */   {
/*     */     private int used;
/*     */ 
/*     */     
/* 633 */     private StringBuffer sb = new StringBuffer();
/*     */     
/*     */     public ToStringBuffer(int used) {
/* 636 */       this.used = used;
/*     */     }
/*     */     
/*     */     public void addNV(String name, String value) {
/* 640 */       value = ParameterList.quote(value);
/* 641 */       this.sb.append("; ");
/* 642 */       this.used += 2;
/* 643 */       int len = name.length() + value.length() + 1;
/* 644 */       if (this.used + len > 76) {
/* 645 */         this.sb.append("\r\n\t");
/* 646 */         this.used = 8;
/*     */       } 
/* 648 */       this.sb.append(name).append('=');
/* 649 */       this.used += name.length() + 1;
/* 650 */       if (this.used + value.length() > 76)
/*     */       
/* 652 */       { String s = MimeUtility.fold(this.used, value);
/* 653 */         this.sb.append(s);
/* 654 */         int lastlf = s.lastIndexOf('\n');
/* 655 */         if (lastlf >= 0) {
/* 656 */           this.used += s.length() - lastlf - 1;
/*     */         } else {
/* 658 */           this.used += s.length();
/*     */         }  }
/* 660 */       else { this.sb.append(value);
/* 661 */         this.used += value.length(); }
/*     */     
/*     */     }
/*     */     
/*     */     public String toString() {
/* 666 */       return this.sb.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static String quote(String value) {
/* 672 */     return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
/*     */   }
/*     */   
/* 675 */   private static final char[] hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Value encodeValue(String value, String charset) {
/*     */     byte[] b;
/* 687 */     if (MimeUtility.checkAscii(value) == 1) {
/* 688 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 692 */       b = value.getBytes(MimeUtility.javaCharset(charset));
/* 693 */     } catch (UnsupportedEncodingException ex) {
/* 694 */       return null;
/*     */     } 
/* 696 */     StringBuffer sb = new StringBuffer(b.length + charset.length() + 2);
/* 697 */     sb.append(charset).append("''");
/* 698 */     for (int i = 0; i < b.length; i++) {
/* 699 */       char c = (char)(b[i] & 0xFF);
/*     */       
/* 701 */       if (c <= ' ' || c >= '' || c == '*' || c == '\'' || c == '%' || "()<>@,;:\\\"\t []/?=".indexOf(c) >= 0) {
/*     */         
/* 703 */         sb.append('%').append(hex[c >> 4]).append(hex[c & 0xF]);
/*     */       } else {
/* 705 */         sb.append(c);
/*     */       } 
/* 707 */     }  Value v = new Value();
/* 708 */     v.charset = charset;
/* 709 */     v.value = value;
/* 710 */     v.encodedValue = sb.toString();
/* 711 */     return v;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Value extractCharset(String value) throws ParseException {
/* 719 */     Value v = new Value();
/* 720 */     v.value = v.encodedValue = value;
/*     */     try {
/* 722 */       int i = value.indexOf('\'');
/* 723 */       if (i <= 0) {
/* 724 */         if (decodeParametersStrict) {
/* 725 */           throw new ParseException("Missing charset in encoded value: " + value);
/*     */         }
/* 727 */         return v;
/*     */       } 
/* 729 */       String charset = value.substring(0, i);
/* 730 */       int li = value.indexOf('\'', i + 1);
/* 731 */       if (li < 0) {
/* 732 */         if (decodeParametersStrict) {
/* 733 */           throw new ParseException("Missing language in encoded value: " + value);
/*     */         }
/* 735 */         return v;
/*     */       } 
/* 737 */       String lang = value.substring(i + 1, li);
/* 738 */       v.value = value.substring(li + 1);
/* 739 */       v.charset = charset;
/* 740 */     } catch (NumberFormatException nex) {
/* 741 */       if (decodeParametersStrict)
/* 742 */         throw new ParseException(nex.toString()); 
/* 743 */     } catch (StringIndexOutOfBoundsException ex) {
/* 744 */       if (decodeParametersStrict)
/* 745 */         throw new ParseException(ex.toString()); 
/*     */     } 
/* 747 */     return v;
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
/*     */   private static String decodeBytes(String value, String charset) throws ParseException, UnsupportedEncodingException {
/* 763 */     byte[] b = new byte[value.length()];
/*     */     int bi;
/* 765 */     for (int i = 0; i < value.length(); i++) {
/* 766 */       char c = value.charAt(i);
/* 767 */       if (c == '%')
/*     */         try {
/* 769 */           String hex = value.substring(i + 1, i + 3);
/* 770 */           c = (char)Integer.parseInt(hex, 16);
/* 771 */           i += 2;
/* 772 */         } catch (NumberFormatException ex) {
/* 773 */           if (decodeParametersStrict)
/* 774 */             throw new ParseException(ex.toString()); 
/* 775 */         } catch (StringIndexOutOfBoundsException ex) {
/* 776 */           if (decodeParametersStrict) {
/* 777 */             throw new ParseException(ex.toString());
/*     */           }
/*     */         }  
/* 780 */       b[bi++] = (byte)c;
/*     */     } 
/* 782 */     charset = MimeUtility.javaCharset(charset);
/* 783 */     if (charset == null)
/* 784 */       charset = MimeUtility.getDefaultJavaCharset(); 
/* 785 */     return new String(b, 0, bi, charset);
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
/*     */   private static void decodeBytes(String value, OutputStream os) throws ParseException, IOException {
/* 798 */     for (int i = 0; i < value.length(); i++) {
/* 799 */       char c = value.charAt(i);
/* 800 */       if (c == '%')
/*     */         try {
/* 802 */           String hex = value.substring(i + 1, i + 3);
/* 803 */           c = (char)Integer.parseInt(hex, 16);
/* 804 */           i += 2;
/* 805 */         } catch (NumberFormatException ex) {
/* 806 */           if (decodeParametersStrict)
/* 807 */             throw new ParseException(ex.toString()); 
/* 808 */         } catch (StringIndexOutOfBoundsException ex) {
/* 809 */           if (decodeParametersStrict) {
/* 810 */             throw new ParseException(ex.toString());
/*     */           }
/*     */         }  
/* 813 */       os.write((byte)c);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\ParameterList.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */