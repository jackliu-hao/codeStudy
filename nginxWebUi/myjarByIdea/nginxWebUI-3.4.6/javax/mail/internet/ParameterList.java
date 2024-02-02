package javax.mail.internet;

import com.sun.mail.util.ASCIIUtility;
import com.sun.mail.util.PropUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ParameterList {
   private Map list;
   private Set multisegmentNames;
   private Map slist;
   private String lastName;
   private static final boolean encodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.encodeparameters", false);
   private static final boolean decodeParameters = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters", false);
   private static final boolean decodeParametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.decodeparameters.strict", false);
   private static final boolean applehack = PropUtil.getBooleanSystemProperty("mail.mime.applefilenames", false);
   private static final boolean windowshack = PropUtil.getBooleanSystemProperty("mail.mime.windowsfilenames", false);
   private static final boolean parametersStrict = PropUtil.getBooleanSystemProperty("mail.mime.parameters.strict", true);
   private static final char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

   public ParameterList() {
      this.list = new LinkedHashMap();
      this.lastName = null;
      if (decodeParameters) {
         this.multisegmentNames = new HashSet();
         this.slist = new HashMap();
      }

   }

   public ParameterList(String s) throws ParseException {
      this();
      HeaderTokenizer h = new HeaderTokenizer(s, "()<>@,;:\\\"\t []/?=");

      while(true) {
         HeaderTokenizer.Token tk = h.next();
         int type = tk.getType();
         if (type != -4) {
            String value;
            if ((char)type != ';') {
               if (type != -1 || this.lastName == null || (!applehack || !this.lastName.equals("name") && !this.lastName.equals("filename")) && parametersStrict) {
                  throw new ParseException("Expected ';', got \"" + tk.getValue() + "\"");
               }

               String lastValue = (String)this.list.get(this.lastName);
               value = lastValue + " " + tk.getValue();
               this.list.put(this.lastName, value);
               continue;
            }

            tk = h.next();
            if (tk.getType() != -4) {
               if (tk.getType() != -1) {
                  throw new ParseException("Expected parameter name, got \"" + tk.getValue() + "\"");
               }

               String name = tk.getValue().toLowerCase(Locale.ENGLISH);
               tk = h.next();
               if ((char)tk.getType() != '=') {
                  throw new ParseException("Expected '=', got \"" + tk.getValue() + "\"");
               }

               if (windowshack && (name.equals("name") || name.equals("filename"))) {
                  tk = h.next(';', true);
               } else if (parametersStrict) {
                  tk = h.next();
               } else {
                  tk = h.next(';');
               }

               type = tk.getType();
               if (type != -1 && type != -2) {
                  throw new ParseException("Expected parameter value, got \"" + tk.getValue() + "\"");
               }

               value = tk.getValue();
               this.lastName = name;
               if (decodeParameters) {
                  this.putEncodedName(name, value);
               } else {
                  this.list.put(name, value);
               }
               continue;
            }
         }

         if (decodeParameters) {
            this.combineMultisegmentNames(false);
         }

         return;
      }
   }

   private void putEncodedName(String name, String value) throws ParseException {
      int star = name.indexOf(42);
      if (star < 0) {
         this.list.put(name, value);
      } else if (star == name.length() - 1) {
         name = name.substring(0, star);
         Value v = extractCharset(value);

         try {
            v.value = decodeBytes(v.value, v.charset);
         } catch (UnsupportedEncodingException var6) {
            if (decodeParametersStrict) {
               throw new ParseException(var6.toString());
            }
         }

         this.list.put(name, v);
      } else {
         String rname = name.substring(0, star);
         this.multisegmentNames.add(rname);
         this.list.put(rname, "");
         Object v;
         if (name.endsWith("*")) {
            if (name.endsWith("*0*")) {
               v = extractCharset(value);
            } else {
               v = new Value();
               ((Value)v).encodedValue = value;
               ((Value)v).value = value;
            }

            name = name.substring(0, name.length() - 1);
         } else {
            v = value;
         }

         this.slist.put(name, v);
      }

   }

   private void combineMultisegmentNames(boolean keepConsistentOnFailure) throws ParseException {
      boolean success = false;

      try {
         Iterator it = this.multisegmentNames.iterator();

         while(it.hasNext()) {
            String name = (String)it.next();
            new StringBuffer();
            MultiValue mv = new MultiValue();
            String charset = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int segment = 0;

            while(true) {
               String sname = name + "*" + segment;
               Object v = this.slist.get(sname);
               if (v == null) {
                  break;
               }

               mv.add(v);

               try {
                  if (v instanceof Value) {
                     Value vv = (Value)v;
                     if (segment == 0) {
                        charset = vv.charset;
                     } else if (charset == null) {
                        this.multisegmentNames.remove(name);
                        break;
                     }

                     decodeBytes(vv.value, (OutputStream)bos);
                  } else {
                     bos.write(ASCIIUtility.getBytes((String)v));
                  }
               } catch (IOException var26) {
               }

               this.slist.remove(sname);
               ++segment;
            }

            if (segment == 0) {
               this.list.remove(name);
            } else {
               try {
                  if (charset != null) {
                     mv.value = bos.toString(charset);
                  } else {
                     mv.value = bos.toString();
                  }
               } catch (UnsupportedEncodingException var25) {
                  if (decodeParametersStrict) {
                     throw new ParseException(var25.toString());
                  }

                  mv.value = bos.toString(0);
               }

               this.list.put(name, mv);
            }
         }

         success = true;
      } finally {
         if (keepConsistentOnFailure || success) {
            if (this.slist.size() > 0) {
               Iterator sit = this.slist.values().iterator();

               label218:
               while(true) {
                  Object v;
                  do {
                     if (!sit.hasNext()) {
                        this.list.putAll(this.slist);
                        break label218;
                     }

                     v = sit.next();
                  } while(!(v instanceof Value));

                  Value vv = (Value)v;

                  try {
                     vv.value = decodeBytes(vv.value, vv.charset);
                  } catch (UnsupportedEncodingException var24) {
                     if (decodeParametersStrict) {
                        throw new ParseException(var24.toString());
                     }
                  }
               }
            }

            this.multisegmentNames.clear();
            this.slist.clear();
         }

      }
   }

   public int size() {
      return this.list.size();
   }

   public String get(String name) {
      Object v = this.list.get(name.trim().toLowerCase(Locale.ENGLISH));
      String value;
      if (v instanceof MultiValue) {
         value = ((MultiValue)v).value;
      } else if (v instanceof Value) {
         value = ((Value)v).value;
      } else {
         value = (String)v;
      }

      return value;
   }

   public void set(String name, String value) {
      if (name == null && value != null && value.equals("DONE")) {
         if (decodeParameters && this.multisegmentNames.size() > 0) {
            try {
               this.combineMultisegmentNames(true);
            } catch (ParseException var4) {
            }
         }

      } else {
         name = name.trim().toLowerCase(Locale.ENGLISH);
         if (decodeParameters) {
            try {
               this.putEncodedName(name, value);
            } catch (ParseException var5) {
               this.list.put(name, value);
            }
         } else {
            this.list.put(name, value);
         }

      }
   }

   public void set(String name, String value, String charset) {
      if (encodeParameters) {
         Value ev = encodeValue(value, charset);
         if (ev != null) {
            this.list.put(name.trim().toLowerCase(Locale.ENGLISH), ev);
         } else {
            this.set(name, value);
         }
      } else {
         this.set(name, value);
      }

   }

   public void remove(String name) {
      this.list.remove(name.trim().toLowerCase(Locale.ENGLISH));
   }

   public Enumeration getNames() {
      return new ParamEnum(this.list.keySet().iterator());
   }

   public String toString() {
      return this.toString(0);
   }

   public String toString(int used) {
      ToStringBuffer sb = new ToStringBuffer(used);
      Iterator e = this.list.keySet().iterator();

      while(true) {
         while(e.hasNext()) {
            String name = (String)e.next();
            Object v = this.list.get(name);
            if (v instanceof MultiValue) {
               MultiValue vv = (MultiValue)v;
               String ns = name + "*";

               for(int i = 0; i < vv.size(); ++i) {
                  Object va = vv.get(i);
                  if (va instanceof Value) {
                     sb.addNV(ns + i + "*", ((Value)va).encodedValue);
                  } else {
                     sb.addNV(ns + i, (String)va);
                  }
               }
            } else if (v instanceof Value) {
               sb.addNV(name + "*", ((Value)v).encodedValue);
            } else {
               sb.addNV(name, (String)v);
            }
         }

         return sb.toString();
      }
   }

   private static String quote(String value) {
      return MimeUtility.quote(value, "()<>@,;:\\\"\t []/?=");
   }

   private static Value encodeValue(String value, String charset) {
      if (MimeUtility.checkAscii(value) == 1) {
         return null;
      } else {
         byte[] b;
         try {
            b = value.getBytes(MimeUtility.javaCharset(charset));
         } catch (UnsupportedEncodingException var6) {
            return null;
         }

         StringBuffer sb = new StringBuffer(b.length + charset.length() + 2);
         sb.append(charset).append("''");

         for(int i = 0; i < b.length; ++i) {
            char c = (char)(b[i] & 255);
            if (c > ' ' && c < 127 && c != '*' && c != '\'' && c != '%' && "()<>@,;:\\\"\t []/?=".indexOf(c) < 0) {
               sb.append(c);
            } else {
               sb.append('%').append(hex[c >> 4]).append(hex[c & 15]);
            }
         }

         Value v = new Value();
         v.charset = charset;
         v.value = value;
         v.encodedValue = sb.toString();
         return v;
      }
   }

   private static Value extractCharset(String value) throws ParseException {
      Value v = new Value();
      v.value = v.encodedValue = value;

      try {
         int i = value.indexOf(39);
         if (i <= 0) {
            if (decodeParametersStrict) {
               throw new ParseException("Missing charset in encoded value: " + value);
            }

            return v;
         }

         String charset = value.substring(0, i);
         int li = value.indexOf(39, i + 1);
         if (li < 0) {
            if (decodeParametersStrict) {
               throw new ParseException("Missing language in encoded value: " + value);
            }

            return v;
         }

         value.substring(i + 1, li);
         v.value = value.substring(li + 1);
         v.charset = charset;
      } catch (NumberFormatException var6) {
         if (decodeParametersStrict) {
            throw new ParseException(var6.toString());
         }
      } catch (StringIndexOutOfBoundsException var7) {
         if (decodeParametersStrict) {
            throw new ParseException(var7.toString());
         }
      }

      return v;
   }

   private static String decodeBytes(String value, String charset) throws ParseException, UnsupportedEncodingException {
      byte[] b = new byte[value.length()];
      int i = 0;

      int bi;
      for(bi = 0; i < value.length(); ++i) {
         char c = value.charAt(i);
         if (c == '%') {
            try {
               String hex = value.substring(i + 1, i + 3);
               c = (char)Integer.parseInt(hex, 16);
               i += 2;
            } catch (NumberFormatException var7) {
               if (decodeParametersStrict) {
                  throw new ParseException(var7.toString());
               }
            } catch (StringIndexOutOfBoundsException var8) {
               if (decodeParametersStrict) {
                  throw new ParseException(var8.toString());
               }
            }
         }

         b[bi++] = (byte)c;
      }

      charset = MimeUtility.javaCharset(charset);
      if (charset == null) {
         charset = MimeUtility.getDefaultJavaCharset();
      }

      return new String(b, 0, bi, charset);
   }

   private static void decodeBytes(String value, OutputStream os) throws ParseException, IOException {
      for(int i = 0; i < value.length(); ++i) {
         char c = value.charAt(i);
         if (c == '%') {
            try {
               String hex = value.substring(i + 1, i + 3);
               c = (char)Integer.parseInt(hex, 16);
               i += 2;
            } catch (NumberFormatException var5) {
               if (decodeParametersStrict) {
                  throw new ParseException(var5.toString());
               }
            } catch (StringIndexOutOfBoundsException var6) {
               if (decodeParametersStrict) {
                  throw new ParseException(var6.toString());
               }
            }
         }

         os.write((byte)c);
      }

   }

   private static class ToStringBuffer {
      private int used;
      private StringBuffer sb = new StringBuffer();

      public ToStringBuffer(int used) {
         this.used = used;
      }

      public void addNV(String name, String value) {
         value = ParameterList.quote(value);
         this.sb.append("; ");
         this.used += 2;
         int len = name.length() + value.length() + 1;
         if (this.used + len > 76) {
            this.sb.append("\r\n\t");
            this.used = 8;
         }

         this.sb.append(name).append('=');
         this.used += name.length() + 1;
         if (this.used + value.length() > 76) {
            String s = MimeUtility.fold(this.used, value);
            this.sb.append(s);
            int lastlf = s.lastIndexOf(10);
            if (lastlf >= 0) {
               this.used += s.length() - lastlf - 1;
            } else {
               this.used += s.length();
            }
         } else {
            this.sb.append(value);
            this.used += value.length();
         }

      }

      public String toString() {
         return this.sb.toString();
      }
   }

   private static class ParamEnum implements Enumeration {
      private Iterator it;

      ParamEnum(Iterator it) {
         this.it = it;
      }

      public boolean hasMoreElements() {
         return this.it.hasNext();
      }

      public Object nextElement() {
         return this.it.next();
      }
   }

   private static class MultiValue extends ArrayList {
      String value;

      private MultiValue() {
      }

      // $FF: synthetic method
      MultiValue(Object x0) {
         this();
      }
   }

   private static class Value {
      String value;
      String charset;
      String encodedValue;

      private Value() {
      }

      // $FF: synthetic method
      Value(Object x0) {
         this();
      }
   }
}
