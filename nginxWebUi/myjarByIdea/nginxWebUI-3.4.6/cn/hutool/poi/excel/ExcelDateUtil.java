package cn.hutool.poi.excel;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.ExcelNumberFormat;

public class ExcelDateUtil {
   private static final int[] customFormats = new int[]{28, 30, 31, 32, 33, 55, 56, 57, 58};

   public static boolean isDateFormat(Cell cell) {
      return isDateFormat(cell, (ConditionalFormattingEvaluator)null);
   }

   public static boolean isDateFormat(Cell cell, ConditionalFormattingEvaluator cfEvaluator) {
      ExcelNumberFormat nf = ExcelNumberFormat.from(cell, cfEvaluator);
      return isDateFormat(nf);
   }

   public static boolean isDateFormat(ExcelNumberFormat numFmt) {
      return isDateFormat(numFmt.getIdx(), numFmt.getFormat());
   }

   public static boolean isDateFormat(int formatIndex, String formatString) {
      if (ArrayUtil.contains(customFormats, formatIndex)) {
         return true;
      } else {
         return StrUtil.isNotEmpty(formatString) && StrUtil.containsAny(formatString, new CharSequence[]{"周", "星期", "aa"}) ? true : DateUtil.isADateFormat(formatIndex, formatString);
      }
   }
}
