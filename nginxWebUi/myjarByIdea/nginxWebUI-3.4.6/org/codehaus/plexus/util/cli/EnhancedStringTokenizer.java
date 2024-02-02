package org.codehaus.plexus.util.cli;

import java.util.StringTokenizer;

public final class EnhancedStringTokenizer {
   private StringTokenizer cst;
   String cdelim;
   final boolean cdelimSingleChar;
   final char cdelimChar;
   boolean creturnDelims;
   String lastToken;
   boolean delimLast;

   public EnhancedStringTokenizer(String str) {
      this(str, " \t\n\r\f", false);
   }

   public EnhancedStringTokenizer(String str, String delim) {
      this(str, delim, false);
   }

   public EnhancedStringTokenizer(String str, String delim, boolean returnDelims) {
      this.cst = null;
      this.lastToken = null;
      this.delimLast = true;
      this.cst = new StringTokenizer(str, delim, true);
      this.cdelim = delim;
      this.creturnDelims = returnDelims;
      this.cdelimSingleChar = delim.length() == 1;
      this.cdelimChar = delim.charAt(0);
   }

   public boolean hasMoreTokens() {
      return this.cst.hasMoreTokens();
   }

   private String internalNextToken() {
      String token;
      if (this.lastToken != null) {
         token = this.lastToken;
         this.lastToken = null;
         return token;
      } else {
         token = this.cst.nextToken();
         if (this.isDelim(token)) {
            if (this.delimLast) {
               this.lastToken = token;
               return "";
            } else {
               this.delimLast = true;
               return token;
            }
         } else {
            this.delimLast = false;
            return token;
         }
      }
   }

   public String nextToken() {
      String token = this.internalNextToken();
      if (this.creturnDelims) {
         return token;
      } else if (this.isDelim(token)) {
         return this.hasMoreTokens() ? this.internalNextToken() : "";
      } else {
         return token;
      }
   }

   private boolean isDelim(String str) {
      if (str.length() == 1) {
         char ch = str.charAt(0);
         if (this.cdelimSingleChar) {
            if (this.cdelimChar == ch) {
               return true;
            }
         } else if (this.cdelim.indexOf(ch) >= 0) {
            return true;
         }
      }

      return false;
   }
}
