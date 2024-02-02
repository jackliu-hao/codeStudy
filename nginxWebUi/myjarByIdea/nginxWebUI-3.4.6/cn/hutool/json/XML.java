package cn.hutool.json;

import cn.hutool.json.xml.JSONXMLParser;
import cn.hutool.json.xml.JSONXMLSerializer;

public class XML {
   public static final Character AMP = '&';
   public static final Character APOS = '\'';
   public static final Character BANG = '!';
   public static final Character EQ = '=';
   public static final Character GT = '>';
   public static final Character LT = '<';
   public static final Character QUEST = '?';
   public static final Character QUOT = '"';
   public static final Character SLASH = '/';

   public static JSONObject toJSONObject(String string) throws JSONException {
      return toJSONObject(string, false);
   }

   public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
      return toJSONObject(new JSONObject(), string, keepStrings);
   }

   public static JSONObject toJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
      JSONXMLParser.parseJSONObject(jo, xmlStr, keepStrings);
      return jo;
   }

   public static String toXml(Object object) throws JSONException {
      return toXml(object, (String)null);
   }

   public static String toXml(Object object, String tagName) throws JSONException {
      return toXml(object, tagName, "content");
   }

   public static String toXml(Object object, String tagName, String... contentKeys) throws JSONException {
      return JSONXMLSerializer.toXml(object, tagName, contentKeys);
   }
}
