package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class InterpolationFilterReader extends FilterReader {
   private String replaceData;
   private int replaceIndex;
   private int previousIndex;
   private Map variables;
   private String beginToken;
   private String endToken;
   private int beginTokenLength;
   private int endTokenLength;
   private static String DEFAULT_BEGIN_TOKEN = "${";
   private static String DEFAULT_END_TOKEN = "}";

   public InterpolationFilterReader(Reader in, Map variables, String beginToken, String endToken) {
      super(in);
      this.replaceData = null;
      this.replaceIndex = -1;
      this.previousIndex = -1;
      this.variables = new HashMap();
      this.variables = variables;
      this.beginToken = beginToken;
      this.endToken = endToken;
      this.beginTokenLength = beginToken.length();
      this.endTokenLength = endToken.length();
   }

   public InterpolationFilterReader(Reader in, Map variables) {
      this(in, variables, DEFAULT_BEGIN_TOKEN, DEFAULT_END_TOKEN);
   }

   public long skip(long n) throws IOException {
      if (n < 0L) {
         throw new IllegalArgumentException("skip value is negative");
      } else {
         for(long i = 0L; i < n; ++i) {
            if (this.read() == -1) {
               return i;
            }
         }

         return n;
      }
   }

   public int read(char[] cbuf, int off, int len) throws IOException {
      for(int i = 0; i < len; ++i) {
         int ch = this.read();
         if (ch == -1) {
            if (i == 0) {
               return -1;
            }

            return i;
         }

         cbuf[off + i] = (char)ch;
      }

      return len;
   }

   public int read() throws IOException {
      if (this.replaceIndex != -1 && this.replaceIndex < this.replaceData.length()) {
         int ch = this.replaceData.charAt(this.replaceIndex++);
         if (this.replaceIndex >= this.replaceData.length()) {
            this.replaceIndex = -1;
         }

         return ch;
      } else {
         int ch = true;
         int ch;
         if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
            ch = this.endToken.charAt(this.previousIndex++);
         } else {
            ch = this.in.read();
         }

         if (ch != this.beginToken.charAt(0)) {
            return ch;
         } else {
            StringBuffer key = new StringBuffer();
            int beginTokenMatchPos = 1;

            do {
               if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
                  ch = this.endToken.charAt(this.previousIndex++);
               } else {
                  ch = this.in.read();
               }

               if (ch == -1) {
                  break;
               }

               key.append((char)ch);
               if (beginTokenMatchPos < this.beginTokenLength && ch != this.beginToken.charAt(beginTokenMatchPos++)) {
                  ch = -1;
                  break;
               }
            } while(ch != this.endToken.charAt(0));

            if (ch != -1 && this.endTokenLength > 1) {
               int endTokenMatchPos = 1;

               do {
                  if (this.previousIndex != -1 && this.previousIndex < this.endTokenLength) {
                     ch = this.endToken.charAt(this.previousIndex++);
                  } else {
                     ch = this.in.read();
                  }

                  if (ch == -1) {
                     break;
                  }

                  key.append((char)ch);
                  if (ch != this.endToken.charAt(endTokenMatchPos++)) {
                     ch = -1;
                     break;
                  }
               } while(endTokenMatchPos < this.endTokenLength);
            }

            if (ch == -1) {
               this.replaceData = key.toString();
               this.replaceIndex = 0;
               return this.beginToken.charAt(0);
            } else {
               String variableKey = key.substring(this.beginTokenLength - 1, key.length() - this.endTokenLength);
               Object o = this.variables.get(variableKey);
               if (o != null) {
                  String value = o.toString();
                  if (value.length() != 0) {
                     this.replaceData = value;
                     this.replaceIndex = 0;
                  }

                  return this.read();
               } else {
                  this.previousIndex = 0;
                  this.replaceData = key.substring(0, key.length() - this.endTokenLength);
                  this.replaceIndex = 0;
                  return this.beginToken.charAt(0);
               }
            }
         }
      }
   }
}
