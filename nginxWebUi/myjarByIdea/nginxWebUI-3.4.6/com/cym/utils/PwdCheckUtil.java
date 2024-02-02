package com.cym.utils;

import cn.hutool.core.util.StrUtil;

public class PwdCheckUtil {
   public static String[] KEYBOARD_SLOPE_ARR = new String[]{"!qaz", "1qaz", "@wsx", "2wsx", "#edc", "3edc", "$rfv", "4rfv", "%tgb", "5tgb", "^yhn", "6yhn", "&ujm", "7ujm", "*ik,", "8ik,", "(ol.", "9ol.", ")p;/", "0p;/", "+[;.", "=[;.", "_pl,", "-pl,", ")okm", "0okm", "(ijn", "9ijn", "*uhb", "8uhb", "&ygv", "7ygv", "^tfc", "6tfc", "%rdx", "5rdx", "$esz", "4esz"};
   public static String[] KEYBOARD_HORIZONTAL_ARR = new String[]{"01234567890-=", "!@#$%^&*()_+", "qwertyuiop[]", "QWERTYUIOP{}", "asdfghjkl;'", "ASDFGHJKL:", "zxcvbnm,./", "ZXCVBNM<>?"};
   public static String DEFAULT_SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
   public static String SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

   public static void main(String[] args) {
   }

   public static boolean checkPasswordLength(String password, String minNum, String maxNum) {
      boolean flag = false;
      if (StrUtil.isBlank(maxNum)) {
         minNum = StrUtil.isBlank(minNum) ? "0" : minNum;
         if (password.length() >= Integer.parseInt(minNum)) {
            flag = true;
         }
      } else {
         minNum = StrUtil.isBlank(minNum) ? "0" : minNum;
         if (password.length() >= Integer.parseInt(minNum) && password.length() <= Integer.parseInt(maxNum)) {
            flag = true;
         }
      }

      return flag;
   }

   public static boolean checkContainDigit(String password) {
      char[] chPass = password.toCharArray();
      boolean flag = false;
      int num_count = 0;

      for(int i = 0; i < chPass.length; ++i) {
         if (Character.isDigit(chPass[i])) {
            ++num_count;
         }
      }

      if (num_count >= 1) {
         flag = true;
      }

      return flag;
   }

   public static boolean checkContainCase(String password) {
      char[] chPass = password.toCharArray();
      boolean flag = false;
      int char_count = 0;

      for(int i = 0; i < chPass.length; ++i) {
         if (Character.isLetter(chPass[i])) {
            ++char_count;
         }
      }

      if (char_count >= 1) {
         flag = true;
      }

      return flag;
   }

   public static boolean checkContainLowerCase(String password) {
      char[] chPass = password.toCharArray();
      boolean flag = false;
      int char_count = 0;

      for(int i = 0; i < chPass.length; ++i) {
         if (Character.isLowerCase(chPass[i])) {
            ++char_count;
         }
      }

      if (char_count >= 1) {
         flag = true;
      }

      return flag;
   }

   public static boolean checkContainUpperCase(String password) {
      char[] chPass = password.toCharArray();
      boolean flag = false;
      int char_count = 0;

      for(int i = 0; i < chPass.length; ++i) {
         if (Character.isUpperCase(chPass[i])) {
            ++char_count;
         }
      }

      if (char_count >= 1) {
         flag = true;
      }

      return flag;
   }

   public static boolean checkContainSpecialChar(String password) {
      char[] chPass = password.toCharArray();
      boolean flag = false;
      int special_count = 0;

      for(int i = 0; i < chPass.length; ++i) {
         if (SPECIAL_CHAR.indexOf(chPass[i]) != -1) {
            ++special_count;
         }
      }

      if (special_count >= 1) {
         flag = true;
      }

      return flag;
   }

   public static boolean checkLateralKeyboardSite(String password, int repetitions, boolean isLower) {
      String t_password = new String(password);
      t_password = t_password.toLowerCase();
      int n = t_password.length();
      boolean flag = false;
      int arrLen = KEYBOARD_HORIZONTAL_ARR.length;
      int limit_num = repetitions;

      for(int i = 0; i + limit_num <= n; ++i) {
         String str = t_password.substring(i, i + limit_num);
         String distinguishStr = password.substring(i, i + limit_num);

         for(int j = 0; j < arrLen; ++j) {
            String configStr = KEYBOARD_HORIZONTAL_ARR[j];
            String revOrderStr = (new StringBuffer(KEYBOARD_HORIZONTAL_ARR[j])).reverse().toString();
            if (isLower) {
               String UpperStr = KEYBOARD_HORIZONTAL_ARR[j].toUpperCase();
               if (configStr.indexOf(distinguishStr) == -1 && UpperStr.indexOf(distinguishStr) == -1) {
                  String revUpperStr = (new StringBuffer(UpperStr)).reverse().toString();
                  if (revOrderStr.indexOf(distinguishStr) == -1 && revUpperStr.indexOf(distinguishStr) == -1) {
                     continue;
                  }

                  flag = true;
                  return flag;
               }

               flag = true;
               return flag;
            } else {
               if (configStr.indexOf(str) != -1) {
                  flag = true;
                  return flag;
               }

               if (revOrderStr.indexOf(str) != -1) {
                  flag = true;
                  return flag;
               }
            }
         }
      }

      return flag;
   }

   public static boolean checkKeyboardSlantSite(String password, int repetitions, boolean isLower) {
      String t_password = new String(password);
      t_password = t_password.toLowerCase();
      int n = t_password.length();
      boolean flag = false;
      int arrLen = KEYBOARD_SLOPE_ARR.length;
      int limit_num = repetitions;

      for(int i = 0; i + limit_num <= n; ++i) {
         String str = t_password.substring(i, i + limit_num);
         String distinguishStr = password.substring(i, i + limit_num);

         for(int j = 0; j < arrLen; ++j) {
            String configStr = KEYBOARD_SLOPE_ARR[j];
            String revOrderStr = (new StringBuffer(KEYBOARD_SLOPE_ARR[j])).reverse().toString();
            if (isLower) {
               String UpperStr = KEYBOARD_SLOPE_ARR[j].toUpperCase();
               if (configStr.indexOf(distinguishStr) == -1 && UpperStr.indexOf(distinguishStr) == -1) {
                  String revUpperStr = (new StringBuffer(UpperStr)).reverse().toString();
                  if (revOrderStr.indexOf(distinguishStr) == -1 && revUpperStr.indexOf(distinguishStr) == -1) {
                     continue;
                  }

                  flag = true;
                  return flag;
               }

               flag = true;
               return flag;
            } else {
               if (configStr.indexOf(str) != -1) {
                  flag = true;
                  return flag;
               }

               if (revOrderStr.indexOf(str) != -1) {
                  flag = true;
                  return flag;
               }
            }
         }
      }

      return flag;
   }

   public static boolean checkSequentialChars(String password, int repetitions, boolean isLower) {
      String t_password = new String(password);
      boolean flag = false;
      int limit_num = repetitions;
      int normal_count = false;
      int reversed_count = false;
      if (!isLower) {
         t_password = t_password.toLowerCase();
      }

      int n = t_password.length();
      char[] pwdCharArr = t_password.toCharArray();

      for(int i = 0; i + limit_num <= n; ++i) {
         int normal_count = 0;
         int reversed_count = 0;

         for(int j = 0; j < limit_num - 1; ++j) {
            if (pwdCharArr[i + j + 1] - pwdCharArr[i + j] == 1) {
               ++normal_count;
               if (normal_count == limit_num - 1) {
                  return true;
               }
            }

            if (pwdCharArr[i + j] - pwdCharArr[i + j + 1] == 1) {
               ++reversed_count;
               if (reversed_count == limit_num - 1) {
                  return true;
               }
            }
         }
      }

      return flag;
   }

   public static boolean checkSequentialSameChars(String password, int repetitions) {
      String t_password = new String(password);
      int n = t_password.length();
      char[] pwdCharArr = t_password.toCharArray();
      boolean flag = false;
      int limit_num = repetitions;
      int count = false;

      for(int i = 0; i + limit_num <= n; ++i) {
         int count = 0;

         for(int j = 0; j < limit_num - 1; ++j) {
            if (pwdCharArr[i + j] == pwdCharArr[i + j + 1]) {
               ++count;
               if (count == limit_num - 1) {
                  return true;
               }
            }
         }
      }

      return flag;
   }
}
