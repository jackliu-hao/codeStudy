package cn.hutool.core.util;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.text.escape.Html4Escape;
import cn.hutool.core.text.escape.Html4Unescape;
import cn.hutool.core.text.escape.XmlEscape;
import cn.hutool.core.text.escape.XmlUnescape;

public class EscapeUtil {
   private static final String NOT_ESCAPE_CHARS = "*@-_+./";
   private static final Filter<Character> JS_ESCAPE_FILTER = (c) -> {
      return !Character.isDigit(c) && !Character.isLowerCase(c) && !Character.isUpperCase(c) && !StrUtil.contains("*@-_+./", c);
   };

   public static String escapeXml(CharSequence xml) {
      XmlEscape escape = new XmlEscape();
      return escape.replace(xml).toString();
   }

   public static String unescapeXml(CharSequence xml) {
      XmlUnescape unescape = new XmlUnescape();
      return unescape.replace(xml).toString();
   }

   public static String escapeHtml4(CharSequence html) {
      Html4Escape escape = new Html4Escape();
      return escape.replace(html).toString();
   }

   public static String unescapeHtml4(CharSequence html) {
      Html4Unescape unescape = new Html4Unescape();
      return unescape.replace(html).toString();
   }

   public static String escape(CharSequence content) {
      return escape(content, JS_ESCAPE_FILTER);
   }

   public static String escapeAll(CharSequence content) {
      return escape(content, (c) -> {
         return true;
      });
   }

   public static String escape(CharSequence content, Filter<Character> filter) {
      if (StrUtil.isEmpty(content)) {
         return StrUtil.str(content);
      } else {
         StringBuilder tmp = new StringBuilder(content.length() * 6);

         for(int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (!filter.accept(c)) {
               tmp.append(c);
            } else if (c < 256) {
               tmp.append("%");
               if (c < 16) {
                  tmp.append("0");
               }

               tmp.append(Integer.toString(c, 16));
            } else {
               tmp.append("%u");
               if (c <= 4095) {
                  tmp.append("0");
               }

               tmp.append(Integer.toString(c, 16));
            }
         }

         return tmp.toString();
      }
   }

   public static String unescape(String content) {
      if (StrUtil.isBlank(content)) {
         return content;
      } else {
         StringBuilder tmp = new StringBuilder(content.length());
         int lastPos = 0;

         while(lastPos < content.length()) {
            int pos = content.indexOf("%", lastPos);
            if (pos == lastPos) {
               char ch;
               if (content.charAt(pos + 1) == 'u') {
                  ch = (char)Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                  tmp.append(ch);
                  lastPos = pos + 6;
               } else {
                  ch = (char)Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                  tmp.append(ch);
                  lastPos = pos + 3;
               }
            } else if (pos == -1) {
               tmp.append(content.substring(lastPos));
               lastPos = content.length();
            } else {
               tmp.append(content, lastPos, pos);
               lastPos = pos;
            }
         }

         return tmp.toString();
      }
   }

   public static String safeUnescape(String content) {
      try {
         return unescape(content);
      } catch (Exception var2) {
         return content;
      }
   }
}
