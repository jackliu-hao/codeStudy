package org.h2.server.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.h2.util.StringUtils;

public class PageParser {
   private static final int TAB_WIDTH = 4;
   private final String page;
   private int pos;
   private final Map<String, Object> settings;
   private final int len;
   private StringBuilder result;

   private PageParser(String var1, Map<String, Object> var2, int var3) {
      this.page = var1;
      this.pos = var3;
      this.len = var1.length();
      this.settings = var2;
      this.result = new StringBuilder(this.len);
   }

   public static String parse(String var0, Map<String, Object> var1) {
      PageParser var2 = new PageParser(var0, var1, 0);
      return var2.replaceTags();
   }

   private void setError(int var1) {
      String var2 = this.page.substring(0, var1) + "####BUG####" + this.page.substring(var1);
      var2 = escapeHtml(var2);
      this.result = new StringBuilder();
      this.result.append(var2);
   }

   private String parseBlockUntil(String var1) throws ParseException {
      PageParser var2 = new PageParser(this.page, this.settings, this.pos);
      var2.parseAll();
      if (!var2.readIf(var1)) {
         throw new ParseException(this.page, var2.pos);
      } else {
         this.pos = var2.pos;
         return var2.result.toString();
      }
   }

   private String replaceTags() {
      try {
         this.parseAll();
         if (this.pos != this.len) {
            this.setError(this.pos);
         }
      } catch (ParseException var2) {
         this.setError(this.pos);
      }

      return this.result.toString();
   }

   private void parseAll() throws ParseException {
      StringBuilder var1 = this.result;
      String var2 = this.page;

      int var3;
      for(var3 = this.pos; var3 < this.len; ++var3) {
         char var4 = var2.charAt(var3);
         String var6;
         String var14;
         switch (var4) {
            case '$':
               if (var2.length() > var3 + 1 && var2.charAt(var3 + 1) == '{') {
                  var3 += 2;
                  int var12 = var2.indexOf(125, var3);
                  if (var12 < 0) {
                     this.setError(var3);
                     return;
                  }

                  var6 = StringUtils.trimSubstring(var2, var3, var12);
                  var3 = var12;
                  var14 = (String)this.get(var6);
                  this.replaceTags(var14);
                  break;
               }

               var1.append(var4);
               break;
            case '<':
               if (var2.charAt(var3 + 3) == ':' && var2.charAt(var3 + 1) == '/') {
                  this.pos = var3;
                  return;
               }

               if (var2.charAt(var3 + 2) != ':') {
                  var1.append(var4);
                  break;
               }

               this.pos = var3;
               String var5;
               if (this.readIf("<c:forEach")) {
                  var5 = this.readParam("var");
                  var6 = this.readParam("items");
                  this.read(">");
                  int var7 = this.pos;
                  Object var8 = (List)this.get(var6);
                  if (var8 == null) {
                     this.result.append("?items?");
                     var8 = new ArrayList();
                  }

                  if (((List)var8).isEmpty()) {
                     this.parseBlockUntil("</c:forEach>");
                  }

                  Iterator var9 = ((List)var8).iterator();

                  while(var9.hasNext()) {
                     Object var10 = var9.next();
                     this.settings.put(var5, var10);
                     this.pos = var7;
                     String var11 = this.parseBlockUntil("</c:forEach>");
                     this.result.append(var11);
                  }
               } else {
                  if (!this.readIf("<c:if")) {
                     this.setError(var3);
                     return;
                  }

                  var5 = this.readParam("test");
                  int var13 = var5.indexOf("=='");
                  if (var13 < 0) {
                     this.setError(var3);
                     return;
                  }

                  var14 = var5.substring(var13 + 3, var5.length() - 1);
                  var5 = var5.substring(0, var13);
                  String var15 = (String)this.get(var5);
                  this.read(">");
                  String var16 = this.parseBlockUntil("</c:if>");
                  --this.pos;
                  if (var15.equals(var14)) {
                     this.result.append(var16);
                  }
               }

               var3 = this.pos;
               break;
            default:
               var1.append(var4);
         }
      }

      this.pos = var3;
   }

   private Object get(String var1) {
      int var2 = var1.indexOf(46);
      if (var2 >= 0) {
         String var3 = var1.substring(var2 + 1);
         var1 = var1.substring(0, var2);
         HashMap var4 = (HashMap)this.settings.get(var1);
         return var4 == null ? "?" + var1 + "?" : var4.get(var3);
      } else {
         return this.settings.get(var1);
      }
   }

   private void replaceTags(String var1) {
      if (var1 != null) {
         this.result.append(parse(var1, this.settings));
      }

   }

   private String readParam(String var1) throws ParseException {
      this.read(var1);
      this.read("=");
      this.read("\"");

      int var2;
      for(var2 = this.pos; this.page.charAt(this.pos) != '"'; ++this.pos) {
      }

      int var3 = this.pos;
      this.read("\"");
      String var4 = this.page.substring(var2, var3);
      return parse(var4, this.settings);
   }

   private void skipSpaces() {
      while(this.page.charAt(this.pos) == ' ') {
         ++this.pos;
      }

   }

   private void read(String var1) throws ParseException {
      if (!this.readIf(var1)) {
         throw new ParseException(var1, this.pos);
      }
   }

   private boolean readIf(String var1) {
      this.skipSpaces();
      if (this.page.regionMatches(this.pos, var1, 0, var1.length())) {
         this.pos += var1.length();
         this.skipSpaces();
         return true;
      } else {
         return false;
      }
   }

   static String escapeHtmlData(String var0) {
      return escapeHtml(var0, false);
   }

   public static String escapeHtml(String var0) {
      return escapeHtml(var0, true);
   }

   private static String escapeHtml(String var0, boolean var1) {
      if (var0 == null) {
         return null;
      } else {
         int var2 = var0.length();
         if (var1 && var2 == 0) {
            return "&nbsp;";
         } else {
            StringBuilder var3 = new StringBuilder(var2);
            boolean var4 = true;

            int var6;
            for(int var5 = 0; var5 < var2; var5 += Character.charCount(var6)) {
               var6 = var0.codePointAt(var5);
               if (var6 != 32 && var6 != 9) {
                  var4 = false;
                  switch (var6) {
                     case 10:
                        if (var1) {
                           var3.append("<br />");
                           var4 = true;
                        } else {
                           var3.append(var6);
                        }
                        break;
                     case 34:
                        var3.append("&quot;");
                        break;
                     case 36:
                        var3.append("&#36;");
                        break;
                     case 38:
                        var3.append("&amp;");
                        break;
                     case 39:
                        var3.append("&#39;");
                        break;
                     case 60:
                        var3.append("&lt;");
                        break;
                     case 62:
                        var3.append("&gt;");
                        break;
                     default:
                        if (var6 >= 128) {
                           var3.append("&#").append(var6).append(';');
                        } else {
                           var3.append((char)var6);
                        }
                  }
               } else {
                  for(int var7 = 0; var7 < (var6 == 32 ? 1 : 4); ++var7) {
                     if (var4 && var1) {
                        var3.append("&nbsp;");
                     } else {
                        var3.append(' ');
                        var4 = true;
                     }
                  }
               }
            }

            return var3.toString();
         }
      }
   }

   static String escapeJavaScript(String var0) {
      if (var0 == null) {
         return null;
      } else {
         int var1 = var0.length();
         if (var1 == 0) {
            return "";
         } else {
            StringBuilder var2 = new StringBuilder(var1);

            for(int var3 = 0; var3 < var1; ++var3) {
               char var4 = var0.charAt(var3);
               switch (var4) {
                  case '\t':
                     var2.append("\\t");
                     break;
                  case '\n':
                     var2.append("\\n");
                     break;
                  case '\r':
                     var2.append("\\r");
                     break;
                  case '"':
                     var2.append("\\\"");
                     break;
                  case '\'':
                     var2.append("\\'");
                     break;
                  case '\\':
                     var2.append("\\\\");
                     break;
                  default:
                     var2.append(var4);
               }
            }

            return var2.toString();
         }
      }
   }
}
