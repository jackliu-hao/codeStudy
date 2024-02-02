package org.h2.util.json;

public abstract class JSONTextSource {
   final JSONTarget<?> target;
   private final StringBuilder builder;

   JSONTextSource(JSONTarget<?> var1) {
      this.target = var1;
      this.builder = new StringBuilder();
   }

   final void parse() {
      boolean var1 = false;

      while(true) {
         while(true) {
            int var2;
            while((var2 = this.nextCharAfterWhitespace()) >= 0) {
               if (var2 != 125 && var2 != 93) {
                  if (var2 == 44) {
                     if (var1 || !this.target.isValueSeparatorExpected()) {
                        throw new IllegalArgumentException();
                     }

                     var1 = true;
                  } else {
                     if (var1 != this.target.isValueSeparatorExpected()) {
                        throw new IllegalArgumentException();
                     }

                     var1 = false;
                     switch (var2) {
                        case 34:
                           String var3 = this.readString();
                           if (this.target.isPropertyExpected()) {
                              if (this.nextCharAfterWhitespace() != 58) {
                                 throw new IllegalArgumentException();
                              }

                              this.target.member(var3);
                           } else {
                              this.target.valueString(var3);
                           }
                           break;
                        case 45:
                           this.parseNumber(false);
                           break;
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                           this.parseNumber(true);
                           break;
                        case 91:
                           this.target.startArray();
                           break;
                        case 102:
                           this.readKeyword1("false");
                           this.target.valueFalse();
                           break;
                        case 110:
                           this.readKeyword1("null");
                           this.target.valueNull();
                           break;
                        case 116:
                           this.readKeyword1("true");
                           this.target.valueTrue();
                           break;
                        case 123:
                           this.target.startObject();
                           break;
                        default:
                           throw new IllegalArgumentException();
                     }
                  }
               } else {
                  if (var1) {
                     throw new IllegalArgumentException();
                  }

                  if (var2 == 125) {
                     this.target.endObject();
                  } else {
                     this.target.endArray();
                  }
               }
            }

            return;
         }
      }
   }

   abstract int nextCharAfterWhitespace();

   abstract void readKeyword1(String var1);

   abstract void parseNumber(boolean var1);

   abstract int nextChar();

   abstract char readHex();

   private String readString() {
      this.builder.setLength(0);
      boolean var1 = false;

      while(true) {
         int var2 = this.nextChar();
         switch (var2) {
            case 34:
               if (var1) {
                  throw new IllegalArgumentException();
               } else {
                  return this.builder.toString();
               }
            case 92:
               var2 = this.nextChar();
               switch (var2) {
                  case 34:
                  case 47:
                  case 92:
                     this.appendNonSurrogate((char)var2, var1);
                     continue;
                  case 98:
                     this.appendNonSurrogate('\b', var1);
                     continue;
                  case 102:
                     this.appendNonSurrogate('\f', var1);
                     continue;
                  case 110:
                     this.appendNonSurrogate('\n', var1);
                     continue;
                  case 114:
                     this.appendNonSurrogate('\r', var1);
                     continue;
                  case 116:
                     this.appendNonSurrogate('\t', var1);
                     continue;
                  case 117:
                     var1 = this.appendChar(this.readHex(), var1);
                     continue;
                  default:
                     throw new IllegalArgumentException();
               }
            default:
               if (Character.isBmpCodePoint(var2)) {
                  var1 = this.appendChar((char)var2, var1);
               } else {
                  if (var1) {
                     throw new IllegalArgumentException();
                  }

                  this.builder.appendCodePoint(var2);
                  var1 = false;
               }
         }
      }
   }

   private void appendNonSurrogate(char var1, boolean var2) {
      if (var2) {
         throw new IllegalArgumentException();
      } else {
         this.builder.append(var1);
      }
   }

   private boolean appendChar(char var1, boolean var2) {
      if (var2 != Character.isLowSurrogate(var1)) {
         throw new IllegalArgumentException();
      } else {
         if (var2) {
            var2 = false;
         } else if (Character.isHighSurrogate(var1)) {
            var2 = true;
         }

         this.builder.append(var1);
         return var2;
      }
   }
}
