package cn.hutool.core.text.escape;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.text.replacer.StrReplacer;
import cn.hutool.core.util.CharUtil;

public class NumericEntityUnescaper extends StrReplacer {
   private static final long serialVersionUID = 1L;

   protected int replace(CharSequence str, int pos, StrBuilder out) {
      int len = str.length();
      if (str.charAt(pos) == '&' && pos < len - 2 && str.charAt(pos + 1) == '#') {
         int start = pos + 2;
         boolean isHex = false;
         char firstChar = str.charAt(start);
         if (firstChar == 'x' || firstChar == 'X') {
            ++start;
            isHex = true;
         }

         if (start == len) {
            return 0;
         }

         int end;
         for(end = start; end < len && CharUtil.isHexChar(str.charAt(end)); ++end) {
         }

         boolean isSemiNext = end != len && str.charAt(end) == ';';
         if (isSemiNext) {
            int entityValue;
            try {
               if (isHex) {
                  entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 16);
               } else {
                  entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 10);
               }
            } catch (NumberFormatException var12) {
               return 0;
            }

            out.append((char)entityValue);
            return 2 + end - start + (isHex ? 1 : 0) + 1;
         }
      }

      return 0;
   }
}
