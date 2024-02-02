package javax.mail.internet;

class MailDateParser {
   int index = 0;
   char[] orig = null;

   public MailDateParser(char[] orig, int index) {
      this.orig = orig;
      this.index = index;
   }

   public void skipUntilNumber() throws java.text.ParseException {
      try {
         while(true) {
            switch (this.orig[this.index]) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  return;
               default:
                  ++this.index;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var2) {
         throw new java.text.ParseException("No Number Found", this.index);
      }
   }

   public void skipWhiteSpace() {
      int len = this.orig.length;

      while(this.index < len) {
         switch (this.orig[this.index]) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
               ++this.index;
               break;
            default:
               return;
         }
      }

   }

   public int peekChar() throws java.text.ParseException {
      if (this.index < this.orig.length) {
         return this.orig[this.index];
      } else {
         throw new java.text.ParseException("No more characters", this.index);
      }
   }

   public void skipChar(char c) throws java.text.ParseException {
      if (this.index < this.orig.length) {
         if (this.orig[this.index] == c) {
            ++this.index;
         } else {
            throw new java.text.ParseException("Wrong char", this.index);
         }
      } else {
         throw new java.text.ParseException("No more characters", this.index);
      }
   }

   public boolean skipIfChar(char c) throws java.text.ParseException {
      if (this.index < this.orig.length) {
         if (this.orig[this.index] == c) {
            ++this.index;
            return true;
         } else {
            return false;
         }
      } else {
         throw new java.text.ParseException("No more characters", this.index);
      }
   }

   public int parseNumber() throws java.text.ParseException {
      int length = this.orig.length;
      boolean gotNum = false;

      int result;
      for(result = 0; this.index < length; ++this.index) {
         switch (this.orig[this.index]) {
            case '0':
               result *= 10;
               gotNum = true;
               break;
            case '1':
               result = result * 10 + 1;
               gotNum = true;
               break;
            case '2':
               result = result * 10 + 2;
               gotNum = true;
               break;
            case '3':
               result = result * 10 + 3;
               gotNum = true;
               break;
            case '4':
               result = result * 10 + 4;
               gotNum = true;
               break;
            case '5':
               result = result * 10 + 5;
               gotNum = true;
               break;
            case '6':
               result = result * 10 + 6;
               gotNum = true;
               break;
            case '7':
               result = result * 10 + 7;
               gotNum = true;
               break;
            case '8':
               result = result * 10 + 8;
               gotNum = true;
               break;
            case '9':
               result = result * 10 + 9;
               gotNum = true;
               break;
            default:
               if (gotNum) {
                  return result;
               }

               throw new java.text.ParseException("No Number found", this.index);
         }
      }

      if (gotNum) {
         return result;
      } else {
         throw new java.text.ParseException("No Number found", this.index);
      }
   }

   public int parseMonth() throws java.text.ParseException {
      try {
         char curr;
         switch (this.orig[this.index++]) {
            case 'A':
            case 'a':
               curr = this.orig[this.index++];
               if (curr != 'P' && curr != 'p') {
                  if (curr == 'U' || curr == 'u') {
                     curr = this.orig[this.index++];
                     if (curr == 'G' || curr == 'g') {
                        return 7;
                     }
                  }
               } else {
                  curr = this.orig[this.index++];
                  if (curr == 'R' || curr == 'r') {
                     return 3;
                  }
               }
            case 'B':
            case 'C':
            case 'E':
            case 'G':
            case 'H':
            case 'I':
            case 'K':
            case 'L':
            case 'P':
            case 'Q':
            case 'R':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            case 'b':
            case 'c':
            case 'e':
            case 'g':
            case 'h':
            case 'i':
            case 'k':
            case 'l':
            case 'p':
            case 'q':
            case 'r':
            default:
               break;
            case 'D':
            case 'd':
               curr = this.orig[this.index++];
               if (curr != 'E' && curr != 'e') {
                  break;
               }

               curr = this.orig[this.index++];
               if (curr != 'C' && curr != 'c') {
                  break;
               }

               return 11;
            case 'F':
            case 'f':
               curr = this.orig[this.index++];
               if (curr != 'E' && curr != 'e') {
                  break;
               }

               curr = this.orig[this.index++];
               if (curr != 'B' && curr != 'b') {
                  break;
               }

               return 1;
            case 'J':
            case 'j':
               switch (this.orig[this.index++]) {
                  case 'A':
                  case 'a':
                     curr = this.orig[this.index++];
                     if (curr != 'N' && curr != 'n') {
                        throw new java.text.ParseException("Bad Month", this.index);
                     }

                     return 0;
                  case 'U':
                  case 'u':
                     curr = this.orig[this.index++];
                     if (curr != 'N' && curr != 'n') {
                        if (curr != 'L' && curr != 'l') {
                           throw new java.text.ParseException("Bad Month", this.index);
                        }

                        return 6;
                     }

                     return 5;
                  default:
                     throw new java.text.ParseException("Bad Month", this.index);
               }
            case 'M':
            case 'm':
               curr = this.orig[this.index++];
               if (curr != 'A' && curr != 'a') {
                  break;
               }

               curr = this.orig[this.index++];
               if (curr != 'R' && curr != 'r') {
                  if (curr != 'Y' && curr != 'y') {
                     break;
                  }

                  return 4;
               }

               return 2;
            case 'N':
            case 'n':
               curr = this.orig[this.index++];
               if (curr != 'O' && curr != 'o') {
                  break;
               }

               curr = this.orig[this.index++];
               if (curr != 'V' && curr != 'v') {
                  break;
               }

               return 10;
            case 'O':
            case 'o':
               curr = this.orig[this.index++];
               if (curr != 'C' && curr != 'c') {
                  break;
               }

               curr = this.orig[this.index++];
               if (curr != 'T' && curr != 't') {
                  break;
               }

               return 9;
            case 'S':
            case 's':
               curr = this.orig[this.index++];
               if (curr == 'E' || curr == 'e') {
                  curr = this.orig[this.index++];
                  if (curr == 'P' || curr == 'p') {
                     return 8;
                  }
               }
         }
      } catch (ArrayIndexOutOfBoundsException var3) {
      }

      throw new java.text.ParseException("Bad Month", this.index);
   }

   public int parseTimeZone() throws java.text.ParseException {
      if (this.index >= this.orig.length) {
         throw new java.text.ParseException("No more characters", this.index);
      } else {
         char test = this.orig[this.index];
         return test != '+' && test != '-' ? this.parseAlphaTimeZone() : this.parseNumericTimeZone();
      }
   }

   public int parseNumericTimeZone() throws java.text.ParseException {
      boolean switchSign = false;
      char first = this.orig[this.index++];
      if (first == '+') {
         switchSign = true;
      } else if (first != '-') {
         throw new java.text.ParseException("Bad Numeric TimeZone", this.index);
      }

      int oindex = this.index;
      int tz = this.parseNumber();
      if (tz >= 2400) {
         throw new java.text.ParseException("Numeric TimeZone out of range", oindex);
      } else {
         int offset = tz / 100 * 60 + tz % 100;
         return switchSign ? -offset : offset;
      }
   }

   public int parseAlphaTimeZone() throws java.text.ParseException {
      int result = false;
      boolean foundCommon = false;

      char curr;
      int result;
      try {
         switch (this.orig[this.index++]) {
            case 'C':
            case 'c':
               result = 360;
               foundCommon = true;
               break;
            case 'E':
            case 'e':
               result = 300;
               foundCommon = true;
               break;
            case 'G':
            case 'g':
               curr = this.orig[this.index++];
               if (curr == 'M' || curr == 'm') {
                  curr = this.orig[this.index++];
                  if (curr == 'T' || curr == 't') {
                     result = 0;
                     break;
                  }
               }

               throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
            case 'M':
            case 'm':
               result = 420;
               foundCommon = true;
               break;
            case 'P':
            case 'p':
               result = 480;
               foundCommon = true;
               break;
            case 'U':
            case 'u':
               curr = this.orig[this.index++];
               if (curr != 'T' && curr != 't') {
                  throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
               }

               result = 0;
               break;
            default:
               throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
         }
      } catch (ArrayIndexOutOfBoundsException var5) {
         throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
      }

      if (foundCommon) {
         curr = this.orig[this.index++];
         if (curr != 'S' && curr != 's') {
            if (curr == 'D' || curr == 'd') {
               curr = this.orig[this.index++];
               if (curr != 'T' && curr == 't') {
                  throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
               }

               result -= 60;
            }
         } else {
            curr = this.orig[this.index++];
            if (curr != 'T' && curr != 't') {
               throw new java.text.ParseException("Bad Alpha TimeZone", this.index);
            }
         }
      }

      return result;
   }

   int getIndex() {
      return this.index;
   }
}
