package cn.hutool.core.text;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

public class TextSimilarity {
   public static double similar(String strA, String strB) {
      String newStrA;
      String newStrB;
      if (strA.length() < strB.length()) {
         newStrA = removeSign(strB);
         newStrB = removeSign(strA);
      } else {
         newStrA = removeSign(strA);
         newStrB = removeSign(strB);
      }

      int temp = Math.max(newStrA.length(), newStrB.length());
      if (0 == temp) {
         return 1.0;
      } else {
         int commonLength = longestCommonSubstringLength(newStrA, newStrB);
         return NumberUtil.div((float)commonLength, (float)temp);
      }
   }

   public static String similar(String strA, String strB, int scale) {
      return NumberUtil.formatPercent(similar(strA, strB), scale);
   }

   public static String longestCommonSubstring(String strA, String strB) {
      int[][] matrix = generateMatrix(strA, strB);
      int m = strA.length();
      int n = strB.length();
      char[] result = new char[matrix[m][n]];
      int currentIndex = result.length - 1;

      while(matrix[m][n] != 0) {
         if (matrix[m][n] == matrix[m][n - 1]) {
            --n;
         } else if (matrix[m][n] == matrix[m - 1][n]) {
            --m;
         } else {
            result[currentIndex] = strA.charAt(m - 1);
            --currentIndex;
            --n;
            --m;
         }
      }

      return new String(result);
   }

   private static String removeSign(String str) {
      int length = str.length();
      StringBuilder sb = StrUtil.builder(length);

      for(int i = 0; i < length; ++i) {
         char c = str.charAt(i);
         if (isValidChar(c)) {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   private static boolean isValidChar(char charValue) {
      return charValue >= 19968 && charValue <= '\u9fff' || charValue >= 'a' && charValue <= 'z' || charValue >= 'A' && charValue <= 'Z' || charValue >= '0' && charValue <= '9';
   }

   private static int longestCommonSubstringLength(String strA, String strB) {
      int m = strA.length();
      int n = strB.length();
      return generateMatrix(strA, strB)[m][n];
   }

   private static int[][] generateMatrix(String strA, String strB) {
      int m = strA.length();
      int n = strB.length();
      int[][] matrix = new int[m + 1][n + 1];

      for(int i = 1; i <= m; ++i) {
         for(int j = 1; j <= n; ++j) {
            if (strA.charAt(i - 1) == strB.charAt(j - 1)) {
               matrix[i][j] = matrix[i - 1][j - 1] + 1;
            } else {
               matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
            }
         }
      }

      return matrix;
   }
}
