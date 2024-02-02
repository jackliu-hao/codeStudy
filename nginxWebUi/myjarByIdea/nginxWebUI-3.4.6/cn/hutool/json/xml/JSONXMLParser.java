package cn.hutool.json.xml;

import cn.hutool.json.InternalJSONUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import cn.hutool.json.XMLTokener;

public class JSONXMLParser {
   public static void parseJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
      XMLTokener x = new XMLTokener(xmlStr, jo.getConfig());

      while(x.more() && x.skipPast("<")) {
         parse(x, jo, (String)null, keepStrings);
      }

   }

   private static boolean parse(XMLTokener x, JSONObject context, String name, boolean keepStrings) throws JSONException {
      Object token = x.nextToken();
      String string;
      if (token == XML.BANG) {
         char c = x.next();
         if (c == '-') {
            if (x.next() == '-') {
               x.skipPast("-->");
               return false;
            }

            x.back();
         } else if (c == '[') {
            token = x.nextToken();
            if ("CDATA".equals(token) && x.next() == '[') {
               string = x.nextCDATA();
               if (string.length() > 0) {
                  context.accumulate("content", string);
               }

               return false;
            }

            throw x.syntaxError("Expected 'CDATA['");
         }

         int i = 1;

         do {
            token = x.nextMeta();
            if (token == null) {
               throw x.syntaxError("Missing '>' after '<!'.");
            }

            if (token == XML.LT) {
               ++i;
            } else if (token == XML.GT) {
               --i;
            }
         } while(i > 0);

         return false;
      } else if (token == XML.QUEST) {
         x.skipPast("?>");
         return false;
      } else if (token == XML.SLASH) {
         token = x.nextToken();
         if (name == null) {
            throw x.syntaxError("Mismatched close tag " + token);
         } else if (!token.equals(name)) {
            throw x.syntaxError("Mismatched " + name + " and " + token);
         } else if (x.nextToken() != XML.GT) {
            throw x.syntaxError("Misshaped close tag");
         } else {
            return true;
         }
      } else if (token instanceof Character) {
         throw x.syntaxError("Misshaped tag");
      } else {
         String tagName = (String)token;
         token = null;
         JSONObject jsonobject = new JSONObject();

         while(true) {
            if (token == null) {
               token = x.nextToken();
            }

            if (!(token instanceof String)) {
               if (token == XML.SLASH) {
                  if (x.nextToken() != XML.GT) {
                     throw x.syntaxError("Misshaped tag");
                  }

                  if (jsonobject.size() > 0) {
                     context.accumulate(tagName, jsonobject);
                  } else {
                     context.accumulate(tagName, "");
                  }

                  return false;
               }

               if (token != XML.GT) {
                  throw x.syntaxError("Misshaped tag");
               }

               while(true) {
                  token = x.nextContent();
                  if (token == null) {
                     if (tagName != null) {
                        throw x.syntaxError("Unclosed tag " + tagName);
                     }

                     return false;
                  }

                  if (token instanceof String) {
                     string = (String)token;
                     if (string.length() > 0) {
                        jsonobject.accumulate("content", keepStrings ? token : InternalJSONUtil.stringToValue(string));
                     }
                  } else if (token == XML.LT && parse(x, jsonobject, tagName, keepStrings)) {
                     if (jsonobject.size() == 0) {
                        context.accumulate(tagName, "");
                     } else if (jsonobject.size() == 1 && jsonobject.get("content") != null) {
                        context.accumulate(tagName, jsonobject.get("content"));
                     } else {
                        context.accumulate(tagName, jsonobject);
                     }

                     return false;
                  }
               }
            }

            string = (String)token;
            token = x.nextToken();
            if (token == XML.EQ) {
               token = x.nextToken();
               if (!(token instanceof String)) {
                  throw x.syntaxError("Missing value");
               }

               jsonobject.accumulate(string, keepStrings ? token : InternalJSONUtil.stringToValue((String)token));
               token = null;
            } else {
               jsonobject.accumulate(string, "");
            }
         }
      }
   }
}
