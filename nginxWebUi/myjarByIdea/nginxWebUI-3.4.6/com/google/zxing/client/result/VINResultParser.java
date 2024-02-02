package com.google.zxing.client.result;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import java.util.regex.Pattern;

public final class VINResultParser extends ResultParser {
   private static final Pattern IOQ = Pattern.compile("[IOQ]");
   private static final Pattern AZ09 = Pattern.compile("[A-Z0-9]{17}");

   public VINParsedResult parse(Result result) {
      if (result.getBarcodeFormat() != BarcodeFormat.CODE_39) {
         return null;
      } else {
         String rawText = result.getText();
         rawText = IOQ.matcher(rawText).replaceAll("").trim();
         if (!AZ09.matcher(rawText).matches()) {
            return null;
         } else {
            try {
               if (!checkChecksum(rawText)) {
                  return null;
               } else {
                  String wmi = rawText.substring(0, 3);
                  return new VINParsedResult(rawText, wmi, rawText.substring(3, 9), rawText.substring(9, 17), countryCode(wmi), rawText.substring(3, 8), modelYear(rawText.charAt(9)), rawText.charAt(10), rawText.substring(11));
               }
            } catch (IllegalArgumentException var4) {
               return null;
            }
         }
      }
   }

   private static boolean checkChecksum(CharSequence vin) {
      int sum = 0;

      for(int i = 0; i < vin.length(); ++i) {
         sum += vinPositionWeight(i + 1) * vinCharValue(vin.charAt(i));
      }

      char checkChar = vin.charAt(8);
      char expectedCheckChar = checkChar(sum % 11);
      return checkChar == expectedCheckChar;
   }

   private static int vinCharValue(char c) {
      if (c >= 'A' && c <= 'I') {
         return c - 65 + 1;
      } else if (c >= 'J' && c <= 'R') {
         return c - 74 + 1;
      } else if (c >= 'S' && c <= 'Z') {
         return c - 83 + 2;
      } else if (c >= '0' && c <= '9') {
         return c - 48;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static int vinPositionWeight(int position) {
      if (position > 0 && position <= 7) {
         return 9 - position;
      } else if (position == 8) {
         return 10;
      } else if (position == 9) {
         return 0;
      } else if (position >= 10 && position <= 17) {
         return 19 - position;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static char checkChar(int remainder) {
      if (remainder < 10) {
         return (char)(remainder + 48);
      } else if (remainder == 10) {
         return 'X';
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static int modelYear(char c) {
      if (c >= 'E' && c <= 'H') {
         return c - 69 + 1984;
      } else if (c >= 'J' && c <= 'N') {
         return c - 74 + 1988;
      } else if (c == 'P') {
         return 1993;
      } else if (c >= 'R' && c <= 'T') {
         return c - 82 + 1994;
      } else if (c >= 'V' && c <= 'Y') {
         return c - 86 + 1997;
      } else if (c >= '1' && c <= '9') {
         return c - 49 + 2001;
      } else if (c >= 'A' && c <= 'D') {
         return c - 65 + 2010;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static String countryCode(CharSequence wmi) {
      char c1 = wmi.charAt(0);
      char c2 = wmi.charAt(1);
      switch (c1) {
         case '1':
         case '4':
         case '5':
            return "US";
         case '2':
            return "CA";
         case '3':
            if (c2 >= 'A' && c2 <= 'W') {
               return "MX";
            }
         case '6':
         case '7':
         case '8':
         case ':':
         case ';':
         case '<':
         case '=':
         case '>':
         case '?':
         case '@':
         case 'A':
         case 'B':
         case 'C':
         case 'D':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'N':
         case 'O':
         case 'P':
         case 'Q':
         case 'R':
         case 'T':
         case 'U':
         case 'Y':
         default:
            break;
         case '9':
            if (c2 >= 'A' && c2 <= 'E' || c2 >= '3' && c2 <= '9') {
               return "BR";
            }
            break;
         case 'J':
            if (c2 >= 'A' && c2 <= 'T') {
               return "JP";
            }
            break;
         case 'K':
            if (c2 >= 'L' && c2 <= 'R') {
               return "KO";
            }
            break;
         case 'L':
            return "CN";
         case 'M':
            if (c2 >= 'A' && c2 <= 'E') {
               return "IN";
            }
            break;
         case 'S':
            if (c2 >= 'A' && c2 <= 'M') {
               return "UK";
            }

            if (c2 >= 'N' && c2 <= 'T') {
               return "DE";
            }
            break;
         case 'V':
            if (c2 >= 'F' && c2 <= 'R') {
               return "FR";
            }

            if (c2 >= 'S' && c2 <= 'W') {
               return "ES";
            }
            break;
         case 'W':
            return "DE";
         case 'X':
            if (c2 == '0' || c2 >= '3' && c2 <= '9') {
               return "RU";
            }
            break;
         case 'Z':
            if (c2 >= 'A' && c2 <= 'R') {
               return "IT";
            }
      }

      return null;
   }
}
