package cn.hutool.core.text;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class StrMatcher {
   List<String> patterns;

   public StrMatcher(String pattern) {
      this.patterns = parse(pattern);
   }

   public Map<String, String> match(String text) {
      HashMap<String, String> result = MapUtil.newHashMap(true);
      int from = 0;
      String key = null;
      Iterator var6 = this.patterns.iterator();

      while(var6.hasNext()) {
         String part = (String)var6.next();
         if (StrUtil.isWrap(part, "${", "}")) {
            key = StrUtil.sub(part, 2, part.length() - 1);
         } else {
            int to = text.indexOf(part, from);
            if (to < 0) {
               return MapUtil.empty();
            }

            if (null != key && to > from) {
               result.put(key, text.substring(from, to));
            }

            from = to + part.length();
            key = null;
         }
      }

      if (null != key && from < text.length()) {
         result.put(key, text.substring(from));
      }

      return result;
   }

   private static List<String> parse(String pattern) {
      List<String> patterns = new ArrayList();
      int length = pattern.length();
      char c = 0;
      boolean inVar = false;
      StringBuilder part = StrUtil.builder();

      for(int i = 0; i < length; ++i) {
         char pre = c;
         c = pattern.charAt(i);
         if (inVar) {
            part.append(c);
            if ('}' == c) {
               inVar = false;
               patterns.add(part.toString());
               part.setLength(0);
            }
         } else if ('{' == c && '$' == pre) {
            inVar = true;
            String preText = part.substring(0, part.length() - 1);
            if (StrUtil.isNotEmpty(preText)) {
               patterns.add(preText);
            }

            part.setLength(0);
            part.append(pre).append(c);
         } else {
            part.append(c);
         }
      }

      if (part.length() > 0) {
         patterns.add(part.toString());
      }

      return patterns;
   }
}
