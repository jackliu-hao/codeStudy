/*      */ package javax.mail.internet;
/*      */ 
/*      */ import com.sun.mail.util.PropUtil;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.net.InetAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.StringTokenizer;
/*      */ import javax.mail.Address;
/*      */ import javax.mail.Session;
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
/*      */ public class InternetAddress
/*      */   extends Address
/*      */   implements Cloneable
/*      */ {
/*      */   protected String address;
/*      */   protected String personal;
/*      */   protected String encodedPersonal;
/*      */   private static final long serialVersionUID = -7507595530758302903L;
/*   84 */   private static final boolean ignoreBogusGroupName = PropUtil.getBooleanSystemProperty("mail.mime.address.ignorebogusgroupname", true);
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
/*      */   public InternetAddress() {}
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
/*      */   public InternetAddress(String address) throws AddressException {
/*  111 */     InternetAddress[] a = parse(address, true);
/*      */     
/*  113 */     if (a.length != 1) {
/*  114 */       throw new AddressException("Illegal address", address);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  122 */     this.address = (a[0]).address;
/*  123 */     this.personal = (a[0]).personal;
/*  124 */     this.encodedPersonal = (a[0]).encodedPersonal;
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
/*      */   public InternetAddress(String address, boolean strict) throws AddressException {
/*  139 */     this(address);
/*  140 */     if (strict) {
/*  141 */       if (isGroup()) {
/*  142 */         getGroup(true);
/*      */       } else {
/*  144 */         checkAddress(this.address, true, true);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InternetAddress(String address, String personal) throws UnsupportedEncodingException {
/*  157 */     this(address, personal, null);
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
/*      */   public InternetAddress(String address, String personal, String charset) throws UnsupportedEncodingException {
/*  170 */     this.address = address;
/*  171 */     setPersonal(personal, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object clone() {
/*  179 */     InternetAddress a = null;
/*      */     try {
/*  181 */       a = (InternetAddress)super.clone();
/*  182 */     } catch (CloneNotSupportedException e) {}
/*  183 */     return a;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getType() {
/*  191 */     return "rfc822";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setAddress(String address) {
/*  200 */     this.address = address;
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
/*      */   public void setPersonal(String name, String charset) throws UnsupportedEncodingException {
/*  218 */     this.personal = name;
/*  219 */     if (name != null) {
/*  220 */       this.encodedPersonal = MimeUtility.encodeWord(name, charset, null);
/*      */     } else {
/*  222 */       this.encodedPersonal = null;
/*      */     } 
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
/*      */   public void setPersonal(String name) throws UnsupportedEncodingException {
/*  238 */     this.personal = name;
/*  239 */     if (name != null) {
/*  240 */       this.encodedPersonal = MimeUtility.encodeWord(name);
/*      */     } else {
/*  242 */       this.encodedPersonal = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getAddress() {
/*  250 */     return this.address;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPersonal() {
/*  261 */     if (this.personal != null) {
/*  262 */       return this.personal;
/*      */     }
/*  264 */     if (this.encodedPersonal != null) {
/*      */       try {
/*  266 */         this.personal = MimeUtility.decodeText(this.encodedPersonal);
/*  267 */         return this.personal;
/*  268 */       } catch (Exception ex) {
/*      */ 
/*      */ 
/*      */         
/*  272 */         return this.encodedPersonal;
/*      */       } 
/*      */     }
/*      */     
/*  276 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  287 */     if (this.encodedPersonal == null && this.personal != null) {
/*      */       try {
/*  289 */         this.encodedPersonal = MimeUtility.encodeWord(this.personal);
/*  290 */       } catch (UnsupportedEncodingException ex) {}
/*      */     }
/*  292 */     if (this.encodedPersonal != null)
/*  293 */       return quotePhrase(this.encodedPersonal) + " <" + this.address + ">"; 
/*  294 */     if (isGroup() || isSimple()) {
/*  295 */       return this.address;
/*      */     }
/*  297 */     return "<" + this.address + ">";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toUnicodeString() {
/*  308 */     String p = getPersonal();
/*  309 */     if (p != null)
/*  310 */       return quotePhrase(p) + " <" + this.address + ">"; 
/*  311 */     if (isGroup() || isSimple()) {
/*  312 */       return this.address;
/*      */     }
/*  314 */     return "<" + this.address + ">";
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
/*  333 */   private static final String rfc822phrase = "()<>@,;:\\\"\t .[]".replace(' ', false).replace('\t', false); private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
/*      */   private static final String specialsNoDot = "()<>,;:\\\"[]@";
/*      */   
/*      */   private static String quotePhrase(String phrase) {
/*  337 */     int len = phrase.length();
/*  338 */     boolean needQuoting = false;
/*      */     
/*  340 */     for (int i = 0; i < len; i++) {
/*  341 */       char c = phrase.charAt(i);
/*  342 */       if (c == '"' || c == '\\') {
/*      */         
/*  344 */         StringBuffer sb = new StringBuffer(len + 3);
/*  345 */         sb.append('"');
/*  346 */         for (int j = 0; j < len; j++) {
/*  347 */           char cc = phrase.charAt(j);
/*  348 */           if (cc == '"' || cc == '\\')
/*      */           {
/*  350 */             sb.append('\\'); } 
/*  351 */           sb.append(cc);
/*      */         } 
/*  353 */         sb.append('"');
/*  354 */         return sb.toString();
/*  355 */       }  if ((c < ' ' && c != '\r' && c != '\n' && c != '\t') || c >= '' || rfc822phrase.indexOf(c) >= 0)
/*      */       {
/*      */         
/*  358 */         needQuoting = true;
/*      */       }
/*      */     } 
/*  361 */     if (needQuoting) {
/*  362 */       StringBuffer sb = new StringBuffer(len + 2);
/*  363 */       sb.append('"').append(phrase).append('"');
/*  364 */       return sb.toString();
/*      */     } 
/*  366 */     return phrase;
/*      */   }
/*      */   
/*      */   private static String unquote(String s) {
/*  370 */     if (s.startsWith("\"") && s.endsWith("\"")) {
/*  371 */       s = s.substring(1, s.length() - 1);
/*      */       
/*  373 */       if (s.indexOf('\\') >= 0) {
/*  374 */         StringBuffer sb = new StringBuffer(s.length());
/*  375 */         for (int i = 0; i < s.length(); i++) {
/*  376 */           char c = s.charAt(i);
/*  377 */           if (c == '\\' && i < s.length() - 1)
/*  378 */             c = s.charAt(++i); 
/*  379 */           sb.append(c);
/*      */         } 
/*  381 */         s = sb.toString();
/*      */       } 
/*      */     } 
/*  384 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean equals(Object a) {
/*  391 */     if (!(a instanceof InternetAddress)) {
/*  392 */       return false;
/*      */     }
/*  394 */     String s = ((InternetAddress)a).getAddress();
/*  395 */     if (s == this.address)
/*  396 */       return true; 
/*  397 */     if (this.address != null && this.address.equalsIgnoreCase(s)) {
/*  398 */       return true;
/*      */     }
/*  400 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int hashCode() {
/*  407 */     if (this.address == null) {
/*  408 */       return 0;
/*      */     }
/*  410 */     return this.address.toLowerCase(Locale.ENGLISH).hashCode();
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
/*      */   public static String toString(Address[] addresses) {
/*  426 */     return toString(addresses, 0);
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
/*      */ 
/*      */   
/*      */   public static String toString(Address[] addresses, int used) {
/*  450 */     if (addresses == null || addresses.length == 0) {
/*  451 */       return null;
/*      */     }
/*  453 */     StringBuffer sb = new StringBuffer();
/*      */     
/*  455 */     for (int i = 0; i < addresses.length; i++) {
/*  456 */       if (i != 0) {
/*  457 */         sb.append(", ");
/*  458 */         used += 2;
/*      */       } 
/*      */       
/*  461 */       String s = addresses[i].toString();
/*  462 */       int len = lengthOfFirstSegment(s);
/*  463 */       if (used + len > 76) {
/*  464 */         sb.append("\r\n\t");
/*  465 */         used = 8;
/*      */       } 
/*  467 */       sb.append(s);
/*  468 */       used = lengthOfLastSegment(s, used);
/*      */     } 
/*      */     
/*  471 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int lengthOfFirstSegment(String s) {
/*      */     int pos;
/*  479 */     if ((pos = s.indexOf("\r\n")) != -1) {
/*  480 */       return pos;
/*      */     }
/*  482 */     return s.length();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int lengthOfLastSegment(String s, int used) {
/*      */     int pos;
/*  492 */     if ((pos = s.lastIndexOf("\r\n")) != -1) {
/*  493 */       return s.length() - pos - 2;
/*      */     }
/*  495 */     return s.length() + used;
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
/*      */   public static InternetAddress getLocalAddress(Session session) {
/*      */     
/*  513 */     try { return _getLocalAddress(session); }
/*  514 */     catch (SecurityException sex) {  }
/*  515 */     catch (AddressException ex) {  }
/*  516 */     catch (UnknownHostException ex) {}
/*  517 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static InternetAddress _getLocalAddress(Session session) throws SecurityException, AddressException, UnknownHostException {
/*  528 */     String user = null, host = null, address = null;
/*  529 */     if (session == null) {
/*  530 */       user = System.getProperty("user.name");
/*  531 */       host = getLocalHostName();
/*      */     } else {
/*  533 */       address = session.getProperty("mail.from");
/*  534 */       if (address == null) {
/*  535 */         user = session.getProperty("mail.user");
/*  536 */         if (user == null || user.length() == 0)
/*  537 */           user = session.getProperty("user.name"); 
/*  538 */         if (user == null || user.length() == 0)
/*  539 */           user = System.getProperty("user.name"); 
/*  540 */         host = session.getProperty("mail.host");
/*  541 */         if (host == null || host.length() == 0) {
/*  542 */           host = getLocalHostName();
/*      */         }
/*      */       } 
/*      */     } 
/*  546 */     if (address == null && user != null && user.length() != 0 && host != null && host.length() != 0)
/*      */     {
/*  548 */       address = MimeUtility.quote(user.trim(), "()<>,;:\\\"[]@\t ") + "@" + host;
/*      */     }
/*      */     
/*  551 */     if (address == null) {
/*  552 */       return null;
/*      */     }
/*  554 */     return new InternetAddress(address);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String getLocalHostName() throws UnknownHostException {
/*  562 */     String host = null;
/*  563 */     InetAddress me = InetAddress.getLocalHost();
/*  564 */     if (me != null) {
/*  565 */       host = me.getHostName();
/*  566 */       if (host != null && host.length() > 0 && isInetAddressLiteral(host))
/*  567 */         host = '[' + host + ']'; 
/*      */     } 
/*  569 */     return host;
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
/*      */   private static boolean isInetAddressLiteral(String addr) {
/*  583 */     boolean sawHex = false, sawColon = false;
/*  584 */     for (int i = 0; i < addr.length(); i++) {
/*  585 */       char c = addr.charAt(i);
/*  586 */       if (c < '0' || c > '9')
/*      */       {
/*  588 */         if (c != '.')
/*      */         {
/*  590 */           if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
/*  591 */             sawHex = true;
/*  592 */           } else if (c == ':') {
/*  593 */             sawColon = true;
/*      */           } else {
/*  595 */             return false;
/*      */           }  }  } 
/*  597 */     }  return (!sawHex || sawColon);
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
/*      */   public static InternetAddress[] parse(String addresslist) throws AddressException {
/*  610 */     return parse(addresslist, true);
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
/*      */   
/*      */   public static InternetAddress[] parse(String addresslist, boolean strict) throws AddressException {
/*  633 */     return parse(addresslist, strict, false);
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
/*      */ 
/*      */ 
/*      */   
/*      */   public static InternetAddress[] parseHeader(String addresslist, boolean strict) throws AddressException {
/*  658 */     return parse(addresslist, strict, true);
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
/*      */   private static InternetAddress[] parse(String s, boolean strict, boolean parseHdr) throws AddressException {
/*  672 */     int start_personal = -1, end_personal = -1;
/*  673 */     int length = s.length();
/*  674 */     boolean ignoreErrors = (parseHdr && !strict);
/*  675 */     boolean in_group = false;
/*  676 */     boolean route_addr = false;
/*  677 */     boolean rfc822 = false;
/*      */     
/*  679 */     List v = new ArrayList();
/*      */     
/*      */     int start, end, index;
/*  682 */     for (start = end = -1, index = 0; index < length; index++) {
/*  683 */       int nesting, pindex, rindex; boolean inquote; int qindex, lindex; String addr, pers; char c = s.charAt(index);
/*      */       
/*  685 */       switch (c) {
/*      */ 
/*      */         
/*      */         case '(':
/*  689 */           rfc822 = true;
/*  690 */           if (start >= 0 && end == -1)
/*  691 */             end = index; 
/*  692 */           pindex = index;
/*  693 */           index++; for (nesting = 1; index < length && nesting > 0; 
/*  694 */             index++) {
/*  695 */             c = s.charAt(index);
/*  696 */             switch (c) {
/*      */               case '\\':
/*  698 */                 index++;
/*      */                 break;
/*      */               case '(':
/*  701 */                 nesting++;
/*      */                 break;
/*      */               case ')':
/*  704 */                 nesting--;
/*      */                 break;
/*      */             } 
/*      */ 
/*      */           
/*      */           } 
/*  710 */           if (nesting > 0) {
/*  711 */             if (!ignoreErrors) {
/*  712 */               throw new AddressException("Missing ')'", s, index);
/*      */             }
/*      */             
/*  715 */             index = pindex + 1;
/*      */             break;
/*      */           } 
/*  718 */           index--;
/*  719 */           if (start_personal == -1)
/*  720 */             start_personal = pindex + 1; 
/*  721 */           if (end_personal == -1) {
/*  722 */             end_personal = index;
/*      */           }
/*      */           break;
/*      */         case ')':
/*  726 */           if (!ignoreErrors) {
/*  727 */             throw new AddressException("Missing '('", s, index);
/*      */           }
/*      */           
/*  730 */           if (start == -1) {
/*  731 */             start = index;
/*      */           }
/*      */           break;
/*      */         case '<':
/*  735 */           rfc822 = true;
/*  736 */           if (route_addr) {
/*  737 */             if (!ignoreErrors) {
/*  738 */               throw new AddressException("Extra route-addr", s, index);
/*      */             }
/*      */ 
/*      */             
/*  742 */             if (start == -1) {
/*  743 */               route_addr = false;
/*  744 */               rfc822 = false;
/*  745 */               start = end = -1;
/*      */               break;
/*      */             } 
/*  748 */             if (!in_group) {
/*      */               
/*  750 */               if (end == -1)
/*  751 */                 end = index; 
/*  752 */               String str = s.substring(start, end).trim();
/*      */               
/*  754 */               InternetAddress ma = new InternetAddress();
/*  755 */               ma.setAddress(str);
/*  756 */               if (start_personal >= 0) {
/*  757 */                 ma.encodedPersonal = unquote(s.substring(start_personal, end_personal).trim());
/*      */               }
/*      */ 
/*      */               
/*  761 */               v.add(ma);
/*      */               
/*  763 */               route_addr = false;
/*  764 */               rfc822 = false;
/*  765 */               start = end = -1;
/*  766 */               start_personal = end_personal = -1;
/*      */             } 
/*      */           } 
/*      */ 
/*      */           
/*  771 */           rindex = index;
/*  772 */           inquote = false;
/*      */           
/*  774 */           for (; ++index < length; index++) {
/*  775 */             c = s.charAt(index);
/*  776 */             switch (c) {
/*      */               case '\\':
/*  778 */                 index++;
/*      */                 break;
/*      */               case '"':
/*  781 */                 inquote = !inquote;
/*      */                 break;
/*      */               case '>':
/*  784 */                 if (inquote) {
/*      */                   break;
/*      */                 }
/*      */                 break;
/*      */             } 
/*      */ 
/*      */ 
/*      */           
/*      */           } 
/*  793 */           if (inquote) {
/*  794 */             if (!ignoreErrors) {
/*  795 */               throw new AddressException("Missing '\"'", s, index);
/*      */             }
/*      */ 
/*      */             
/*  799 */             for (index = rindex + 1; index < length; index++) {
/*  800 */               c = s.charAt(index);
/*  801 */               if (c == '\\') {
/*  802 */                 index++;
/*  803 */               } else if (c == '>') {
/*      */                 break;
/*      */               } 
/*      */             } 
/*      */           } 
/*      */           
/*  809 */           if (index >= length) {
/*  810 */             if (!ignoreErrors) {
/*  811 */               throw new AddressException("Missing '>'", s, index);
/*      */             }
/*      */             
/*  814 */             index = rindex + 1;
/*  815 */             if (start == -1) {
/*  816 */               start = rindex;
/*      */             }
/*      */             break;
/*      */           } 
/*  820 */           if (!in_group) {
/*  821 */             start_personal = start;
/*  822 */             if (start_personal >= 0)
/*  823 */               end_personal = rindex; 
/*  824 */             start = rindex + 1;
/*      */           } 
/*  826 */           route_addr = true;
/*  827 */           end = index;
/*      */           break;
/*      */         
/*      */         case '>':
/*  831 */           if (!ignoreErrors) {
/*  832 */             throw new AddressException("Missing '<'", s, index);
/*      */           }
/*      */           
/*  835 */           if (start == -1) {
/*  836 */             start = index;
/*      */           }
/*      */           break;
/*      */         case '"':
/*  840 */           qindex = index;
/*  841 */           rfc822 = true;
/*  842 */           if (start == -1) {
/*  843 */             start = index;
/*      */           }
/*  845 */           for (; ++index < length; index++) {
/*  846 */             c = s.charAt(index);
/*  847 */             switch (c) {
/*      */               case '\\':
/*  849 */                 index++;
/*      */                 break;
/*      */               
/*      */               case '"':
/*      */                 break;
/*      */             } 
/*      */           
/*      */           } 
/*  857 */           if (index >= length) {
/*  858 */             if (!ignoreErrors) {
/*  859 */               throw new AddressException("Missing '\"'", s, index);
/*      */             }
/*      */             
/*  862 */             index = qindex + 1;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case '[':
/*  867 */           rfc822 = true;
/*  868 */           lindex = index;
/*      */           
/*  870 */           for (; ++index < length; index++) {
/*  871 */             c = s.charAt(index);
/*  872 */             switch (c) {
/*      */               case '\\':
/*  874 */                 index++;
/*      */                 break;
/*      */               
/*      */               case ']':
/*      */                 break;
/*      */             } 
/*      */           
/*      */           } 
/*  882 */           if (index >= length) {
/*  883 */             if (!ignoreErrors) {
/*  884 */               throw new AddressException("Missing ']'", s, index);
/*      */             }
/*      */             
/*  887 */             index = lindex + 1;
/*      */           } 
/*      */           break;
/*      */         
/*      */         case ';':
/*  892 */           if (start == -1) {
/*  893 */             route_addr = false;
/*  894 */             rfc822 = false;
/*  895 */             start = end = -1;
/*      */             break;
/*      */           } 
/*  898 */           if (in_group) {
/*  899 */             in_group = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  906 */             if (parseHdr && !strict && index + 1 < length && s.charAt(index + 1) == '@') {
/*      */               break;
/*      */             }
/*  909 */             InternetAddress ma = new InternetAddress();
/*  910 */             end = index + 1;
/*  911 */             ma.setAddress(s.substring(start, end).trim());
/*  912 */             v.add(ma);
/*      */             
/*  914 */             route_addr = false;
/*  915 */             rfc822 = false;
/*  916 */             start = end = -1;
/*  917 */             start_personal = end_personal = -1;
/*      */             break;
/*      */           } 
/*  920 */           if (!ignoreErrors) {
/*  921 */             throw new AddressException("Illegal semicolon, not in group", s, index);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         case ',':
/*  928 */           if (start == -1) {
/*  929 */             route_addr = false;
/*  930 */             rfc822 = false;
/*  931 */             start = end = -1;
/*      */             break;
/*      */           } 
/*  934 */           if (in_group) {
/*  935 */             route_addr = false;
/*      */             
/*      */             break;
/*      */           } 
/*  939 */           if (end == -1) {
/*  940 */             end = index;
/*      */           }
/*  942 */           addr = s.substring(start, end).trim();
/*  943 */           pers = null;
/*  944 */           if (rfc822 && start_personal >= 0) {
/*  945 */             pers = unquote(s.substring(start_personal, end_personal).trim());
/*      */             
/*  947 */             if (pers.trim().length() == 0) {
/*  948 */               pers = null;
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  956 */           if (parseHdr && !strict && pers != null && pers.indexOf('@') >= 0 && addr.indexOf('@') < 0 && addr.indexOf('!') < 0) {
/*      */ 
/*      */             
/*  959 */             String tmp = addr;
/*  960 */             addr = pers;
/*  961 */             pers = tmp;
/*      */           } 
/*  963 */           if (rfc822 || strict || parseHdr) {
/*  964 */             if (!ignoreErrors)
/*  965 */               checkAddress(addr, route_addr, false); 
/*  966 */             InternetAddress ma = new InternetAddress();
/*  967 */             ma.setAddress(addr);
/*  968 */             if (pers != null)
/*  969 */               ma.encodedPersonal = pers; 
/*  970 */             v.add(ma);
/*      */           } else {
/*      */             
/*  973 */             StringTokenizer st = new StringTokenizer(addr);
/*  974 */             while (st.hasMoreTokens()) {
/*  975 */               String str = st.nextToken();
/*  976 */               checkAddress(str, false, false);
/*  977 */               InternetAddress ma = new InternetAddress();
/*  978 */               ma.setAddress(str);
/*  979 */               v.add(ma);
/*      */             } 
/*      */           } 
/*      */           
/*  983 */           route_addr = false;
/*  984 */           rfc822 = false;
/*  985 */           start = end = -1;
/*  986 */           start_personal = end_personal = -1;
/*      */           break;
/*      */         
/*      */         case ':':
/*  990 */           rfc822 = true;
/*  991 */           if (in_group && 
/*  992 */             !ignoreErrors)
/*  993 */             throw new AddressException("Nested group", s, index); 
/*  994 */           if (start == -1)
/*  995 */             start = index; 
/*  996 */           if (parseHdr && !strict) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1002 */             if (index + 1 < length) {
/* 1003 */               String addressSpecials = ")>[]:@\\,.";
/* 1004 */               char nc = s.charAt(index + 1);
/* 1005 */               if (addressSpecials.indexOf(nc) >= 0) {
/* 1006 */                 if (nc != '@') {
/*      */                   break;
/*      */                 }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */                 
/* 1016 */                 for (int i = index + 2; i < length; i++) {
/* 1017 */                   nc = s.charAt(i);
/* 1018 */                   if (nc == ';')
/*      */                     break; 
/* 1020 */                   if (addressSpecials.indexOf(nc) >= 0)
/*      */                     break; 
/*      */                 } 
/* 1023 */                 if (nc == ';') {
/*      */                   break;
/*      */                 }
/*      */               } 
/*      */             } 
/*      */ 
/*      */             
/* 1030 */             String gname = s.substring(start, index);
/* 1031 */             if (ignoreBogusGroupName && (gname.equalsIgnoreCase("mailto") || gname.equalsIgnoreCase("From") || gname.equalsIgnoreCase("To") || gname.equalsIgnoreCase("Cc") || gname.equalsIgnoreCase("Subject") || gname.equalsIgnoreCase("Re"))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1038 */               start = -1; break;
/*      */             } 
/* 1040 */             in_group = true; break;
/*      */           } 
/* 1042 */           in_group = true;
/*      */           break;
/*      */ 
/*      */         
/*      */         case '\t':
/*      */         case '\n':
/*      */         case '\r':
/*      */         case ' ':
/*      */           break;
/*      */         
/*      */         default:
/* 1053 */           if (start == -1) {
/* 1054 */             start = index;
/*      */           }
/*      */           break;
/*      */       } 
/*      */     } 
/* 1059 */     if (start >= 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1065 */       if (end == -1) {
/* 1066 */         end = length;
/*      */       }
/* 1068 */       String addr = s.substring(start, end).trim();
/* 1069 */       String pers = null;
/* 1070 */       if (rfc822 && start_personal >= 0) {
/* 1071 */         pers = unquote(s.substring(start_personal, end_personal).trim());
/*      */         
/* 1073 */         if (pers.trim().length() == 0) {
/* 1074 */           pers = null;
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1082 */       if (parseHdr && !strict && pers != null && pers.indexOf('@') >= 0 && addr.indexOf('@') < 0 && addr.indexOf('!') < 0) {
/*      */ 
/*      */         
/* 1085 */         String tmp = addr;
/* 1086 */         addr = pers;
/* 1087 */         pers = tmp;
/*      */       } 
/* 1089 */       if (rfc822 || strict || parseHdr) {
/* 1090 */         if (!ignoreErrors)
/* 1091 */           checkAddress(addr, route_addr, false); 
/* 1092 */         InternetAddress ma = new InternetAddress();
/* 1093 */         ma.setAddress(addr);
/* 1094 */         if (pers != null)
/* 1095 */           ma.encodedPersonal = pers; 
/* 1096 */         v.add(ma);
/*      */       } else {
/*      */         
/* 1099 */         StringTokenizer st = new StringTokenizer(addr);
/* 1100 */         while (st.hasMoreTokens()) {
/* 1101 */           String str = st.nextToken();
/* 1102 */           checkAddress(str, false, false);
/* 1103 */           InternetAddress ma = new InternetAddress();
/* 1104 */           ma.setAddress(str);
/* 1105 */           v.add(ma);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1110 */     InternetAddress[] a = new InternetAddress[v.size()];
/* 1111 */     v.toArray(a);
/* 1112 */     return a;
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
/*      */   public void validate() throws AddressException {
/* 1126 */     if (isGroup()) {
/* 1127 */       getGroup(true);
/*      */     } else {
/* 1129 */       checkAddress(getAddress(), true, true);
/*      */     } 
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
/*      */   private static void checkAddress(String addr, boolean routeAddr, boolean validate) throws AddressException {
/* 1145 */     int start = 0;
/*      */     
/* 1147 */     int len = addr.length();
/* 1148 */     if (len == 0) {
/* 1149 */       throw new AddressException("Empty address", addr);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1155 */     if (routeAddr && addr.charAt(0) == '@') {
/*      */       int j;
/*      */ 
/*      */ 
/*      */       
/* 1160 */       for (start = 0; (j = indexOfAny(addr, ",:", start)) >= 0; 
/* 1161 */         start = j + 1) {
/* 1162 */         if (addr.charAt(start) != '@')
/* 1163 */           throw new AddressException("Illegal route-addr", addr); 
/* 1164 */         if (addr.charAt(j) == ':') {
/*      */           
/* 1166 */           start = j + 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1180 */     char c = Character.MAX_VALUE;
/* 1181 */     char lastc = Character.MAX_VALUE;
/* 1182 */     boolean inquote = false; int i;
/* 1183 */     for (i = start; i < len; i++) {
/* 1184 */       lastc = c;
/* 1185 */       c = addr.charAt(i);
/*      */ 
/*      */       
/* 1188 */       if (c != '\\' && lastc != '\\')
/*      */       {
/* 1190 */         if (c == '"') {
/* 1191 */           if (inquote) {
/*      */             
/* 1193 */             if (validate && i + 1 < len && addr.charAt(i + 1) != '@') {
/* 1194 */               throw new AddressException("Quote not at end of local address", addr);
/*      */             }
/* 1196 */             inquote = false;
/*      */           } else {
/* 1198 */             if (validate && i != 0) {
/* 1199 */               throw new AddressException("Quote not at start of local address", addr);
/*      */             }
/* 1201 */             inquote = true;
/*      */           }
/*      */         
/*      */         }
/* 1205 */         else if (!inquote) {
/*      */           
/* 1207 */           if (c == '@') {
/* 1208 */             if (i == 0)
/* 1209 */               throw new AddressException("Missing local name", addr); 
/*      */             break;
/*      */           } 
/* 1212 */           if (c <= ' ' || c >= '') {
/* 1213 */             throw new AddressException("Local address contains control or whitespace", addr);
/*      */           }
/* 1215 */           if ("()<>,;:\\\"[]@".indexOf(c) >= 0)
/* 1216 */             throw new AddressException("Local address contains illegal character", addr); 
/*      */         }  } 
/*      */     } 
/* 1219 */     if (inquote) {
/* 1220 */       throw new AddressException("Unterminated quote", addr);
/*      */     }
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
/* 1234 */     if (c != '@') {
/* 1235 */       if (validate) {
/* 1236 */         throw new AddressException("Missing final '@domain'", addr);
/*      */       }
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1242 */     start = i + 1;
/* 1243 */     if (start >= len) {
/* 1244 */       throw new AddressException("Missing domain", addr);
/*      */     }
/* 1246 */     if (addr.charAt(start) == '.')
/* 1247 */       throw new AddressException("Domain starts with dot", addr); 
/* 1248 */     for (i = start; i < len; i++) {
/* 1249 */       c = addr.charAt(i);
/* 1250 */       if (c == '[')
/*      */         return; 
/* 1252 */       if (c <= ' ' || c >= '') {
/* 1253 */         throw new AddressException("Domain contains control or whitespace", addr);
/*      */       }
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
/* 1268 */       if (!Character.isLetterOrDigit(c) && c != '-' && c != '.') {
/* 1269 */         throw new AddressException("Domain contains illegal character", addr);
/*      */       }
/* 1271 */       if (c == '.' && lastc == '.') {
/* 1272 */         throw new AddressException("Domain contains dot-dot", addr);
/*      */       }
/* 1274 */       lastc = c;
/*      */     } 
/* 1276 */     if (lastc == '.') {
/* 1277 */       throw new AddressException("Domain ends with dot", addr);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean isSimple() {
/* 1285 */     return (this.address == null || indexOfAny(this.address, "()<>,;:\\\"[]") < 0);
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
/*      */   public boolean isGroup() {
/* 1299 */     return (this.address != null && this.address.endsWith(";") && this.address.indexOf(':') > 0);
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
/*      */   public InternetAddress[] getGroup(boolean strict) throws AddressException {
/* 1315 */     String addr = getAddress();
/*      */     
/* 1317 */     if (!addr.endsWith(";"))
/* 1318 */       return null; 
/* 1319 */     int ix = addr.indexOf(':');
/* 1320 */     if (ix < 0) {
/* 1321 */       return null;
/*      */     }
/* 1323 */     String list = addr.substring(ix + 1, addr.length() - 1);
/*      */     
/* 1325 */     return parseHeader(list, strict);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int indexOfAny(String s, String any) {
/* 1335 */     return indexOfAny(s, any, 0);
/*      */   }
/*      */   
/*      */   private static int indexOfAny(String s, String any, int start) {
/*      */     try {
/* 1340 */       int len = s.length();
/* 1341 */       for (int i = start; i < len; i++) {
/* 1342 */         if (any.indexOf(s.charAt(i)) >= 0)
/* 1343 */           return i; 
/*      */       } 
/* 1345 */       return -1;
/* 1346 */     } catch (StringIndexOutOfBoundsException e) {
/* 1347 */       return -1;
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\internet\InternetAddress.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */