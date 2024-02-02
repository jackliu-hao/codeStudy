package cn.hutool.json;

import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.mutable.Mutable;
import cn.hutool.core.lang.mutable.MutablePair;

public class JSONParser {
   private final JSONTokener tokener;

   public static JSONParser of(JSONTokener tokener) {
      return new JSONParser(tokener);
   }

   public JSONParser(JSONTokener tokener) {
      this.tokener = tokener;
   }

   public void parseTo(JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
      JSONTokener tokener = this.tokener;
      if (tokener.nextClean() != '{') {
         throw tokener.syntaxError("A JSONObject text must begin with '{'");
      } else {
         while(true) {
            char c = tokener.nextClean();
            switch (c) {
               case '\u0000':
                  throw tokener.syntaxError("A JSONObject text must end with '}'");
               case '}':
                  return;
               default:
                  tokener.back();
                  String key = tokener.nextValue().toString();
                  c = tokener.nextClean();
                  if (c != ':') {
                     throw tokener.syntaxError("Expected a ':' after a key");
                  }

                  jsonObject.setOnce(key, tokener.nextValue(), filter);
                  switch (tokener.nextClean()) {
                     case ',':
                     case ';':
                        if (tokener.nextClean() == '}') {
                           return;
                        }

                        tokener.back();
                        break;
                     case '}':
                        return;
                     default:
                        throw tokener.syntaxError("Expected a ',' or '}'");
                  }
            }
         }
      }
   }

   public void parseTo(JSONArray jsonArray, Filter<Mutable<Object>> filter) {
      JSONTokener x = this.tokener;
      if (x.nextClean() != '[') {
         throw x.syntaxError("A JSONArray text must start with '['");
      } else if (x.nextClean() != ']') {
         x.back();

         while(true) {
            if (x.nextClean() == ',') {
               x.back();
               jsonArray.addRaw(JSONNull.NULL, filter);
            } else {
               x.back();
               jsonArray.addRaw(x.nextValue(), filter);
            }

            switch (x.nextClean()) {
               case ',':
                  if (x.nextClean() == ']') {
                     return;
                  }

                  x.back();
                  break;
               case ']':
                  return;
               default:
                  throw x.syntaxError("Expected a ',' or ']'");
            }
         }
      }
   }
}
