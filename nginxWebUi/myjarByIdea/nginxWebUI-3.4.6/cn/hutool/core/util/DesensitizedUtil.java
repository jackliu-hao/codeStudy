package cn.hutool.core.util;

public class DesensitizedUtil {
   public static String desensitized(CharSequence str, DesensitizedType desensitizedType) {
      if (StrUtil.isBlank(str)) {
         return "";
      } else {
         String newStr = String.valueOf(str);
         switch (desensitizedType) {
            case USER_ID:
               newStr = String.valueOf(userId());
               break;
            case CHINESE_NAME:
               newStr = chineseName(String.valueOf(str));
               break;
            case ID_CARD:
               newStr = idCardNum(String.valueOf(str), 1, 2);
               break;
            case FIXED_PHONE:
               newStr = fixedPhone(String.valueOf(str));
               break;
            case MOBILE_PHONE:
               newStr = mobilePhone(String.valueOf(str));
               break;
            case ADDRESS:
               newStr = address(String.valueOf(str), 8);
               break;
            case EMAIL:
               newStr = email(String.valueOf(str));
               break;
            case PASSWORD:
               newStr = password(String.valueOf(str));
               break;
            case CAR_LICENSE:
               newStr = carLicense(String.valueOf(str));
               break;
            case BANK_CARD:
               newStr = bankCard(String.valueOf(str));
         }

         return newStr;
      }
   }

   public static Long userId() {
      return 0L;
   }

   public static String chineseName(String fullName) {
      return StrUtil.isBlank(fullName) ? "" : StrUtil.hide(fullName, 1, fullName.length());
   }

   public static String idCardNum(String idCardNum, int front, int end) {
      if (StrUtil.isBlank(idCardNum)) {
         return "";
      } else if (front + end > idCardNum.length()) {
         return "";
      } else {
         return front >= 0 && end >= 0 ? StrUtil.hide(idCardNum, front, idCardNum.length() - end) : "";
      }
   }

   public static String fixedPhone(String num) {
      return StrUtil.isBlank(num) ? "" : StrUtil.hide(num, 4, num.length() - 2);
   }

   public static String mobilePhone(String num) {
      return StrUtil.isBlank(num) ? "" : StrUtil.hide(num, 3, num.length() - 4);
   }

   public static String address(String address, int sensitiveSize) {
      if (StrUtil.isBlank(address)) {
         return "";
      } else {
         int length = address.length();
         return StrUtil.hide(address, length - sensitiveSize, length);
      }
   }

   public static String email(String email) {
      if (StrUtil.isBlank(email)) {
         return "";
      } else {
         int index = StrUtil.indexOf(email, '@');
         return index <= 1 ? email : StrUtil.hide(email, 1, index);
      }
   }

   public static String password(String password) {
      return StrUtil.isBlank(password) ? "" : StrUtil.repeat('*', password.length());
   }

   public static String carLicense(String carLicense) {
      if (StrUtil.isBlank(carLicense)) {
         return "";
      } else {
         if (carLicense.length() == 7) {
            carLicense = StrUtil.hide(carLicense, 3, 6);
         } else if (carLicense.length() == 8) {
            carLicense = StrUtil.hide(carLicense, 3, 7);
         }

         return carLicense;
      }
   }

   public static String bankCard(String bankCardNo) {
      if (StrUtil.isBlank(bankCardNo)) {
         return bankCardNo;
      } else {
         bankCardNo = StrUtil.trim(bankCardNo);
         if (bankCardNo.length() < 9) {
            return bankCardNo;
         } else {
            int length = bankCardNo.length();
            int midLength = length - 8;
            StringBuilder buf = new StringBuilder();
            buf.append(bankCardNo, 0, 4);

            for(int i = 0; i < midLength; ++i) {
               if (i % 4 == 0) {
                  buf.append(' ');
               }

               buf.append('*');
            }

            buf.append(' ').append(bankCardNo, length - 4, length);
            return buf.toString();
         }
      }
   }

   public static enum DesensitizedType {
      USER_ID,
      CHINESE_NAME,
      ID_CARD,
      FIXED_PHONE,
      MOBILE_PHONE,
      ADDRESS,
      EMAIL,
      PASSWORD,
      CAR_LICENSE,
      BANK_CARD;
   }
}
