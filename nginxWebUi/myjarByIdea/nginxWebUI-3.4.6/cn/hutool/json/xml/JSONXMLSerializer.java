package cn.hutool.json.xml;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import java.util.Iterator;

public class JSONXMLSerializer {
   public static String toXml(Object object) throws JSONException {
      return toXml(object, (String)null);
   }

   public static String toXml(Object object, String tagName) throws JSONException {
      return toXml(object, tagName, "content");
   }

   public static String toXml(Object object, String tagName, String... contentKeys) throws JSONException {
      if (null == object) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();
         if (object instanceof JSONObject) {
            appendTag(sb, tagName, false);
            ((JSONObject)object).forEach((key, value) -> {
               if (ArrayUtil.isArray(value)) {
                  value = new JSONArray(value);
               }

               if (ArrayUtil.contains(contentKeys, key)) {
                  if (value instanceof JSONArray) {
                     int i = 0;

                     for(Iterator var5 = ((JSONArray)value).iterator(); var5.hasNext(); ++i) {
                        Object val = var5.next();
                        if (i > 0) {
                           sb.append('\n');
                        }

                        sb.append(EscapeUtil.escapeXml(val.toString()));
                     }
                  } else {
                     sb.append(EscapeUtil.escapeXml(value.toString()));
                  }
               } else if (StrUtil.isEmptyIfStr(value)) {
                  sb.append(wrapWithTag((String)null, key));
               } else if (value instanceof JSONArray) {
                  Iterator var7 = ((JSONArray)value).iterator();

                  while(var7.hasNext()) {
                     Object valx = var7.next();
                     if (valx instanceof JSONArray) {
                        sb.append(wrapWithTag(toXml(valx), key));
                     } else {
                        sb.append(toXml(valx, key));
                     }
                  }
               } else {
                  sb.append(toXml(value, key));
               }

            });
            appendTag(sb, tagName, true);
            return sb.toString();
         } else {
            if (ArrayUtil.isArray(object)) {
               object = new JSONArray(object);
            }

            if (object instanceof JSONArray) {
               Iterator var4 = ((JSONArray)object).iterator();

               while(var4.hasNext()) {
                  Object val = var4.next();
                  sb.append(toXml(val, tagName == null ? "array" : tagName));
               }

               return sb.toString();
            } else {
               return wrapWithTag(EscapeUtil.escapeXml(object.toString()), tagName);
            }
         }
      }
   }

   private static void appendTag(StringBuilder sb, String tagName, boolean isEndTag) {
      if (StrUtil.isNotBlank(tagName)) {
         sb.append('<');
         if (isEndTag) {
            sb.append('/');
         }

         sb.append(tagName).append('>');
      }

   }

   private static String wrapWithTag(String content, String tagName) {
      if (StrUtil.isBlank(tagName)) {
         return StrUtil.wrap(content, "\"");
      } else {
         return StrUtil.isEmpty(content) ? "<" + tagName + "/>" : "<" + tagName + ">" + content + "</" + tagName + ">";
      }
   }
}
