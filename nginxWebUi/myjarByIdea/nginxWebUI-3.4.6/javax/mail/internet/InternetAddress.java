package javax.mail.internet;

import com.sun.mail.util.PropUtil;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.mail.Address;
import javax.mail.Session;

public class InternetAddress extends Address implements Cloneable {
   protected String address;
   protected String personal;
   protected String encodedPersonal;
   private static final long serialVersionUID = -7507595530758302903L;
   private static final boolean ignoreBogusGroupName = PropUtil.getBooleanSystemProperty("mail.mime.address.ignorebogusgroupname", true);
   private static final String rfc822phrase = "()<>@,;:\\\"\t .[]".replace(' ', '\u0000').replace('\t', '\u0000');
   private static final String specialsNoDotNoAt = "()<>,;:\\\"[]";
   private static final String specialsNoDot = "()<>,;:\\\"[]@";

   public InternetAddress() {
   }

   public InternetAddress(String address) throws AddressException {
      InternetAddress[] a = parse(address, true);
      if (a.length != 1) {
         throw new AddressException("Illegal address", address);
      } else {
         this.address = a[0].address;
         this.personal = a[0].personal;
         this.encodedPersonal = a[0].encodedPersonal;
      }
   }

   public InternetAddress(String address, boolean strict) throws AddressException {
      this(address);
      if (strict) {
         if (this.isGroup()) {
            this.getGroup(true);
         } else {
            checkAddress(this.address, true, true);
         }
      }

   }

   public InternetAddress(String address, String personal) throws UnsupportedEncodingException {
      this(address, personal, (String)null);
   }

   public InternetAddress(String address, String personal, String charset) throws UnsupportedEncodingException {
      this.address = address;
      this.setPersonal(personal, charset);
   }

   public Object clone() {
      InternetAddress a = null;

      try {
         a = (InternetAddress)super.clone();
      } catch (CloneNotSupportedException var3) {
      }

      return a;
   }

   public String getType() {
      return "rfc822";
   }

   public void setAddress(String address) {
      this.address = address;
   }

   public void setPersonal(String name, String charset) throws UnsupportedEncodingException {
      this.personal = name;
      if (name != null) {
         this.encodedPersonal = MimeUtility.encodeWord(name, charset, (String)null);
      } else {
         this.encodedPersonal = null;
      }

   }

   public void setPersonal(String name) throws UnsupportedEncodingException {
      this.personal = name;
      if (name != null) {
         this.encodedPersonal = MimeUtility.encodeWord(name);
      } else {
         this.encodedPersonal = null;
      }

   }

   public String getAddress() {
      return this.address;
   }

   public String getPersonal() {
      if (this.personal != null) {
         return this.personal;
      } else if (this.encodedPersonal != null) {
         try {
            this.personal = MimeUtility.decodeText(this.encodedPersonal);
            return this.personal;
         } catch (Exception var2) {
            return this.encodedPersonal;
         }
      } else {
         return null;
      }
   }

   public String toString() {
      if (this.encodedPersonal == null && this.personal != null) {
         try {
            this.encodedPersonal = MimeUtility.encodeWord(this.personal);
         } catch (UnsupportedEncodingException var2) {
         }
      }

      if (this.encodedPersonal != null) {
         return quotePhrase(this.encodedPersonal) + " <" + this.address + ">";
      } else {
         return !this.isGroup() && !this.isSimple() ? "<" + this.address + ">" : this.address;
      }
   }

   public String toUnicodeString() {
      String p = this.getPersonal();
      if (p != null) {
         return quotePhrase(p) + " <" + this.address + ">";
      } else {
         return !this.isGroup() && !this.isSimple() ? "<" + this.address + ">" : this.address;
      }
   }

   private static String quotePhrase(String phrase) {
      int len = phrase.length();
      boolean needQuoting = false;

      for(int i = 0; i < len; ++i) {
         char c = phrase.charAt(i);
         if (c == '"' || c == '\\') {
            StringBuffer sb = new StringBuffer(len + 3);
            sb.append('"');

            for(int j = 0; j < len; ++j) {
               char cc = phrase.charAt(j);
               if (cc == '"' || cc == '\\') {
                  sb.append('\\');
               }

               sb.append(cc);
            }

            sb.append('"');
            return sb.toString();
         }

         if (c < ' ' && c != '\r' && c != '\n' && c != '\t' || c >= 127 || rfc822phrase.indexOf(c) >= 0) {
            needQuoting = true;
         }
      }

      if (needQuoting) {
         StringBuffer sb = new StringBuffer(len + 2);
         sb.append('"').append(phrase).append('"');
         return sb.toString();
      } else {
         return phrase;
      }
   }

   private static String unquote(String s) {
      if (s.startsWith("\"") && s.endsWith("\"")) {
         s = s.substring(1, s.length() - 1);
         if (s.indexOf(92) >= 0) {
            StringBuffer sb = new StringBuffer(s.length());

            for(int i = 0; i < s.length(); ++i) {
               char c = s.charAt(i);
               if (c == '\\' && i < s.length() - 1) {
                  ++i;
                  c = s.charAt(i);
               }

               sb.append(c);
            }

            s = sb.toString();
         }
      }

      return s;
   }

   public boolean equals(Object a) {
      if (!(a instanceof InternetAddress)) {
         return false;
      } else {
         String s = ((InternetAddress)a).getAddress();
         if (s == this.address) {
            return true;
         } else {
            return this.address != null && this.address.equalsIgnoreCase(s);
         }
      }
   }

   public int hashCode() {
      return this.address == null ? 0 : this.address.toLowerCase(Locale.ENGLISH).hashCode();
   }

   public static String toString(Address[] addresses) {
      return toString(addresses, 0);
   }

   public static String toString(Address[] addresses, int used) {
      if (addresses != null && addresses.length != 0) {
         StringBuffer sb = new StringBuffer();

         for(int i = 0; i < addresses.length; ++i) {
            if (i != 0) {
               sb.append(", ");
               used += 2;
            }

            String s = addresses[i].toString();
            int len = lengthOfFirstSegment(s);
            if (used + len > 76) {
               sb.append("\r\n\t");
               used = 8;
            }

            sb.append(s);
            used = lengthOfLastSegment(s, used);
         }

         return sb.toString();
      } else {
         return null;
      }
   }

   private static int lengthOfFirstSegment(String s) {
      int pos;
      return (pos = s.indexOf("\r\n")) != -1 ? pos : s.length();
   }

   private static int lengthOfLastSegment(String s, int used) {
      int pos;
      return (pos = s.lastIndexOf("\r\n")) != -1 ? s.length() - pos - 2 : s.length() + used;
   }

   public static InternetAddress getLocalAddress(Session session) {
      try {
         return _getLocalAddress(session);
      } catch (SecurityException var2) {
      } catch (AddressException var3) {
      } catch (UnknownHostException var4) {
      }

      return null;
   }

   static InternetAddress _getLocalAddress(Session session) throws SecurityException, AddressException, UnknownHostException {
      String user = null;
      String host = null;
      String address = null;
      if (session == null) {
         user = System.getProperty("user.name");
         host = getLocalHostName();
      } else {
         address = session.getProperty("mail.from");
         if (address == null) {
            user = session.getProperty("mail.user");
            if (user == null || user.length() == 0) {
               user = session.getProperty("user.name");
            }

            if (user == null || user.length() == 0) {
               user = System.getProperty("user.name");
            }

            host = session.getProperty("mail.host");
            if (host == null || host.length() == 0) {
               host = getLocalHostName();
            }
         }
      }

      if (address == null && user != null && user.length() != 0 && host != null && host.length() != 0) {
         address = MimeUtility.quote(user.trim(), "()<>,;:\\\"[]@\t ") + "@" + host;
      }

      return address == null ? null : new InternetAddress(address);
   }

   private static String getLocalHostName() throws UnknownHostException {
      String host = null;
      InetAddress me = InetAddress.getLocalHost();
      if (me != null) {
         host = me.getHostName();
         if (host != null && host.length() > 0 && isInetAddressLiteral(host)) {
            host = '[' + host + ']';
         }
      }

      return host;
   }

   private static boolean isInetAddressLiteral(String addr) {
      boolean sawHex = false;
      boolean sawColon = false;

      for(int i = 0; i < addr.length(); ++i) {
         char c = addr.charAt(i);
         if ((c < '0' || c > '9') && c != '.') {
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
               sawHex = true;
            } else {
               if (c != ':') {
                  return false;
               }

               sawColon = true;
            }
         }
      }

      return !sawHex || sawColon;
   }

   public static InternetAddress[] parse(String addresslist) throws AddressException {
      return parse(addresslist, true);
   }

   public static InternetAddress[] parse(String addresslist, boolean strict) throws AddressException {
      return parse(addresslist, strict, false);
   }

   public static InternetAddress[] parseHeader(String addresslist, boolean strict) throws AddressException {
      return parse(addresslist, strict, true);
   }

   private static InternetAddress[] parse(String s, boolean strict, boolean parseHdr) throws AddressException {
      int start_personal = -1;
      int end_personal = -1;
      int length = s.length();
      boolean ignoreErrors = parseHdr && !strict;
      boolean in_group = false;
      boolean route_addr = false;
      boolean rfc822 = false;
      List v = new ArrayList();
      int end = -1;
      int start = -1;

      InternetAddress ma;
      String pers;
      for(int index = 0; index < length; ++index) {
         char c = s.charAt(index);
         String addressSpecials;
         switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               break;
            case '"':
               int qindex = index;
               rfc822 = true;
               if (start == -1) {
                  start = index;
               }

               ++index;

               label415:
               while(index < length) {
                  c = s.charAt(index);
                  switch (c) {
                     case '"':
                        break label415;
                     case '\\':
                        ++index;
                     default:
                        ++index;
                  }
               }

               if (index >= length) {
                  if (!ignoreErrors) {
                     throw new AddressException("Missing '\"'", s, index);
                  }

                  index = qindex + 1;
               }
               break;
            case '(':
               rfc822 = true;
               if (start >= 0 && end == -1) {
                  end = index;
               }

               int pindex = index++;

               int nesting;
               for(nesting = 1; index < length && nesting > 0; ++index) {
                  c = s.charAt(index);
                  switch (c) {
                     case '(':
                        ++nesting;
                        break;
                     case ')':
                        --nesting;
                        break;
                     case '\\':
                        ++index;
                  }
               }

               if (nesting > 0) {
                  if (!ignoreErrors) {
                     throw new AddressException("Missing ')'", s, index);
                  }

                  index = pindex + 1;
               } else {
                  --index;
                  if (start_personal == -1) {
                     start_personal = pindex + 1;
                  }

                  if (end_personal == -1) {
                     end_personal = index;
                  }
               }
               break;
            case ')':
               if (!ignoreErrors) {
                  throw new AddressException("Missing '('", s, index);
               }

               if (start == -1) {
                  start = index;
               }
               break;
            case ':':
               rfc822 = true;
               if (in_group && !ignoreErrors) {
                  throw new AddressException("Nested group", s, index);
               }

               if (start == -1) {
                  start = index;
               }

               if (parseHdr && !strict) {
                  if (index + 1 < length) {
                     addressSpecials = ")>[]:@\\,.";
                     char nc = s.charAt(index + 1);
                     if (addressSpecials.indexOf(nc) >= 0) {
                        if (nc != '@') {
                           break;
                        }

                        for(int i = index + 2; i < length; ++i) {
                           nc = s.charAt(i);
                           if (nc == ';' || addressSpecials.indexOf(nc) >= 0) {
                              break;
                           }
                        }

                        if (nc == ';') {
                           break;
                        }
                     }
                  }

                  addressSpecials = s.substring(start, index);
                  if (!ignoreBogusGroupName || !addressSpecials.equalsIgnoreCase("mailto") && !addressSpecials.equalsIgnoreCase("From") && !addressSpecials.equalsIgnoreCase("To") && !addressSpecials.equalsIgnoreCase("Cc") && !addressSpecials.equalsIgnoreCase("Subject") && !addressSpecials.equalsIgnoreCase("Re")) {
                     in_group = true;
                     break;
                  }

                  start = -1;
                  break;
               }

               in_group = true;
               break;
            case ';':
               if (start == -1) {
                  route_addr = false;
                  rfc822 = false;
                  end = -1;
                  start = -1;
                  break;
               } else if (in_group) {
                  in_group = false;
                  if (!parseHdr || strict || index + 1 >= length || s.charAt(index + 1) != '@') {
                     ma = new InternetAddress();
                     end = index + 1;
                     ma.setAddress(s.substring(start, end).trim());
                     v.add(ma);
                     route_addr = false;
                     rfc822 = false;
                     end = -1;
                     start = -1;
                     end_personal = -1;
                     start_personal = -1;
                  }
                  break;
               } else if (!ignoreErrors) {
                  throw new AddressException("Illegal semicolon, not in group", s, index);
               }
            case ',':
               if (start == -1) {
                  route_addr = false;
                  rfc822 = false;
                  end = -1;
                  start = -1;
               } else {
                  if (in_group) {
                     route_addr = false;
                     continue;
                  }

                  if (end == -1) {
                     end = index;
                  }

                  String addr = s.substring(start, end).trim();
                  String pers = null;
                  if (rfc822 && start_personal >= 0) {
                     pers = unquote(s.substring(start_personal, end_personal).trim());
                     if (pers.trim().length() == 0) {
                        pers = null;
                     }
                  }

                  if (parseHdr && !strict && pers != null && pers.indexOf(64) >= 0 && addr.indexOf(64) < 0 && addr.indexOf(33) < 0) {
                     addressSpecials = addr;
                     addr = pers;
                     pers = addressSpecials;
                  }

                  if (!rfc822 && !strict && !parseHdr) {
                     StringTokenizer st = new StringTokenizer(addr);

                     while(st.hasMoreTokens()) {
                        String a = st.nextToken();
                        checkAddress(a, false, false);
                        ma = new InternetAddress();
                        ma.setAddress(a);
                        v.add(ma);
                     }
                  } else {
                     if (!ignoreErrors) {
                        checkAddress(addr, route_addr, false);
                     }

                     ma = new InternetAddress();
                     ma.setAddress(addr);
                     if (pers != null) {
                        ma.encodedPersonal = pers;
                     }

                     v.add(ma);
                  }

                  route_addr = false;
                  rfc822 = false;
                  end = -1;
                  start = -1;
                  end_personal = -1;
                  start_personal = -1;
               }
               break;
            case '<':
               rfc822 = true;
               if (route_addr) {
                  if (!ignoreErrors) {
                     throw new AddressException("Extra route-addr", s, index);
                  }

                  if (start == -1) {
                     route_addr = false;
                     rfc822 = false;
                     end = -1;
                     start = -1;
                     break;
                  }

                  if (!in_group) {
                     if (end == -1) {
                        end = index;
                     }

                     pers = s.substring(start, end).trim();
                     ma = new InternetAddress();
                     ma.setAddress(pers);
                     if (start_personal >= 0) {
                        ma.encodedPersonal = unquote(s.substring(start_personal, end_personal).trim());
                     }

                     v.add(ma);
                     route_addr = false;
                     rfc822 = false;
                     end = -1;
                     start = -1;
                     end_personal = -1;
                     start_personal = -1;
                  }
               }

               int rindex = index;
               boolean inquote = false;
               ++index;

               label341:
               for(; index < length; ++index) {
                  c = s.charAt(index);
                  switch (c) {
                     case '"':
                        inquote = !inquote;
                        break;
                     case '>':
                        if (!inquote) {
                           break label341;
                        }
                        break;
                     case '\\':
                        ++index;
                  }
               }

               if (inquote) {
                  if (!ignoreErrors) {
                     throw new AddressException("Missing '\"'", s, index);
                  }

                  for(index = rindex + 1; index < length; ++index) {
                     c = s.charAt(index);
                     if (c == '\\') {
                        ++index;
                     } else if (c == '>') {
                        break;
                     }
                  }
               }

               if (index >= length) {
                  if (!ignoreErrors) {
                     throw new AddressException("Missing '>'", s, index);
                  }

                  index = rindex + 1;
                  if (start == -1) {
                     start = rindex;
                  }
               } else {
                  if (!in_group) {
                     start_personal = start;
                     if (start >= 0) {
                        end_personal = rindex;
                     }

                     start = rindex + 1;
                  }

                  route_addr = true;
                  end = index;
               }
               break;
            case '>':
               if (!ignoreErrors) {
                  throw new AddressException("Missing '<'", s, index);
               }

               if (start == -1) {
                  start = index;
               }
               break;
            case '[':
               rfc822 = true;
               int lindex = index++;

               label320:
               while(index < length) {
                  c = s.charAt(index);
                  switch (c) {
                     case '\\':
                        ++index;
                     default:
                        ++index;
                        break;
                     case ']':
                        break label320;
                  }
               }

               if (index >= length) {
                  if (!ignoreErrors) {
                     throw new AddressException("Missing ']'", s, index);
                  }

                  index = lindex + 1;
               }
               break;
            default:
               if (start == -1) {
                  start = index;
               }
         }
      }

      if (start >= 0) {
         if (end == -1) {
            end = length;
         }

         String addr = s.substring(start, end).trim();
         pers = null;
         if (rfc822 && start_personal >= 0) {
            pers = unquote(s.substring(start_personal, end_personal).trim());
            if (pers.trim().length() == 0) {
               pers = null;
            }
         }

         if (parseHdr && !strict && pers != null && pers.indexOf(64) >= 0 && addr.indexOf(64) < 0 && addr.indexOf(33) < 0) {
            String tmp = addr;
            addr = pers;
            pers = tmp;
         }

         if (!rfc822 && !strict && !parseHdr) {
            StringTokenizer st = new StringTokenizer(addr);

            while(st.hasMoreTokens()) {
               String a = st.nextToken();
               checkAddress(a, false, false);
               ma = new InternetAddress();
               ma.setAddress(a);
               v.add(ma);
            }
         } else {
            if (!ignoreErrors) {
               checkAddress(addr, route_addr, false);
            }

            ma = new InternetAddress();
            ma.setAddress(addr);
            if (pers != null) {
               ma.encodedPersonal = pers;
            }

            v.add(ma);
         }
      }

      InternetAddress[] a = new InternetAddress[v.size()];
      v.toArray(a);
      return a;
   }

   public void validate() throws AddressException {
      if (this.isGroup()) {
         this.getGroup(true);
      } else {
         checkAddress(this.getAddress(), true, true);
      }

   }

   private static void checkAddress(String addr, boolean routeAddr, boolean validate) throws AddressException {
      int start = 0;
      int len = addr.length();
      if (len == 0) {
         throw new AddressException("Empty address", addr);
      } else {
         int i;
         if (routeAddr && addr.charAt(0) == '@') {
            for(start = 0; (i = indexOfAny(addr, ",:", start)) >= 0; start = i + 1) {
               if (addr.charAt(start) != '@') {
                  throw new AddressException("Illegal route-addr", addr);
               }

               if (addr.charAt(i) == ':') {
                  start = i + 1;
                  break;
               }
            }
         }

         char c = '\uffff';
         char lastc = '\uffff';
         boolean inquote = false;

         for(i = start; i < len; ++i) {
            lastc = c;
            c = addr.charAt(i);
            if (c != '\\' && lastc != '\\') {
               if (c == '"') {
                  if (inquote) {
                     if (validate && i + 1 < len && addr.charAt(i + 1) != '@') {
                        throw new AddressException("Quote not at end of local address", addr);
                     }

                     inquote = false;
                  } else {
                     if (validate && i != 0) {
                        throw new AddressException("Quote not at start of local address", addr);
                     }

                     inquote = true;
                  }
               } else if (!inquote) {
                  if (c == '@') {
                     if (i == 0) {
                        throw new AddressException("Missing local name", addr);
                     }
                     break;
                  }

                  if (c <= ' ' || c >= 127) {
                     throw new AddressException("Local address contains control or whitespace", addr);
                  }

                  if ("()<>,;:\\\"[]@".indexOf(c) >= 0) {
                     throw new AddressException("Local address contains illegal character", addr);
                  }
               }
            }
         }

         if (inquote) {
            throw new AddressException("Unterminated quote", addr);
         } else if (c != '@') {
            if (validate) {
               throw new AddressException("Missing final '@domain'", addr);
            }
         } else {
            start = i + 1;
            if (start >= len) {
               throw new AddressException("Missing domain", addr);
            } else if (addr.charAt(start) == '.') {
               throw new AddressException("Domain starts with dot", addr);
            } else {
               for(i = start; i < len; ++i) {
                  c = addr.charAt(i);
                  if (c == '[') {
                     return;
                  }

                  if (c <= ' ' || c >= 127) {
                     throw new AddressException("Domain contains control or whitespace", addr);
                  }

                  if (!Character.isLetterOrDigit(c) && c != '-' && c != '.') {
                     throw new AddressException("Domain contains illegal character", addr);
                  }

                  if (c == '.' && lastc == '.') {
                     throw new AddressException("Domain contains dot-dot", addr);
                  }

                  lastc = c;
               }

               if (lastc == '.') {
                  throw new AddressException("Domain ends with dot", addr);
               }
            }
         }
      }
   }

   private boolean isSimple() {
      return this.address == null || indexOfAny(this.address, "()<>,;:\\\"[]") < 0;
   }

   public boolean isGroup() {
      return this.address != null && this.address.endsWith(";") && this.address.indexOf(58) > 0;
   }

   public InternetAddress[] getGroup(boolean strict) throws AddressException {
      String addr = this.getAddress();
      if (!addr.endsWith(";")) {
         return null;
      } else {
         int ix = addr.indexOf(58);
         if (ix < 0) {
            return null;
         } else {
            String list = addr.substring(ix + 1, addr.length() - 1);
            return parseHeader(list, strict);
         }
      }
   }

   private static int indexOfAny(String s, String any) {
      return indexOfAny(s, any, 0);
   }

   private static int indexOfAny(String s, String any, int start) {
      try {
         int len = s.length();

         for(int i = start; i < len; ++i) {
            if (any.indexOf(s.charAt(i)) >= 0) {
               return i;
            }
         }

         return -1;
      } catch (StringIndexOutOfBoundsException var5) {
         return -1;
      }
   }
}
