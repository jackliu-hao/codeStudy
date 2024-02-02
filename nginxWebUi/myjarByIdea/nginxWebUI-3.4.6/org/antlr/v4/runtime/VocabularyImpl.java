package org.antlr.v4.runtime;

import java.util.Arrays;

public class VocabularyImpl implements Vocabulary {
   private static final String[] EMPTY_NAMES = new String[0];
   public static final VocabularyImpl EMPTY_VOCABULARY;
   private final String[] literalNames;
   private final String[] symbolicNames;
   private final String[] displayNames;
   private final int maxTokenType;

   public VocabularyImpl(String[] literalNames, String[] symbolicNames) {
      this(literalNames, symbolicNames, (String[])null);
   }

   public VocabularyImpl(String[] literalNames, String[] symbolicNames, String[] displayNames) {
      this.literalNames = literalNames != null ? literalNames : EMPTY_NAMES;
      this.symbolicNames = symbolicNames != null ? symbolicNames : EMPTY_NAMES;
      this.displayNames = displayNames != null ? displayNames : EMPTY_NAMES;
      this.maxTokenType = Math.max(this.displayNames.length, Math.max(this.literalNames.length, this.symbolicNames.length)) - 1;
   }

   public static Vocabulary fromTokenNames(String[] tokenNames) {
      if (tokenNames != null && tokenNames.length != 0) {
         String[] literalNames = (String[])Arrays.copyOf(tokenNames, tokenNames.length);
         String[] symbolicNames = (String[])Arrays.copyOf(tokenNames, tokenNames.length);

         for(int i = 0; i < tokenNames.length; ++i) {
            String tokenName = tokenNames[i];
            if (tokenName != null) {
               if (!tokenName.isEmpty()) {
                  char firstChar = tokenName.charAt(0);
                  if (firstChar == '\'') {
                     symbolicNames[i] = null;
                     continue;
                  }

                  if (Character.isUpperCase(firstChar)) {
                     literalNames[i] = null;
                     continue;
                  }
               }

               literalNames[i] = null;
               symbolicNames[i] = null;
            }
         }

         return new VocabularyImpl(literalNames, symbolicNames, tokenNames);
      } else {
         return EMPTY_VOCABULARY;
      }
   }

   public int getMaxTokenType() {
      return this.maxTokenType;
   }

   public String getLiteralName(int tokenType) {
      return tokenType >= 0 && tokenType < this.literalNames.length ? this.literalNames[tokenType] : null;
   }

   public String getSymbolicName(int tokenType) {
      if (tokenType >= 0 && tokenType < this.symbolicNames.length) {
         return this.symbolicNames[tokenType];
      } else {
         return tokenType == -1 ? "EOF" : null;
      }
   }

   public String getDisplayName(int tokenType) {
      String literalName;
      if (tokenType >= 0 && tokenType < this.displayNames.length) {
         literalName = this.displayNames[tokenType];
         if (literalName != null) {
            return literalName;
         }
      }

      literalName = this.getLiteralName(tokenType);
      if (literalName != null) {
         return literalName;
      } else {
         String symbolicName = this.getSymbolicName(tokenType);
         return symbolicName != null ? symbolicName : Integer.toString(tokenType);
      }
   }

   static {
      EMPTY_VOCABULARY = new VocabularyImpl(EMPTY_NAMES, EMPTY_NAMES, EMPTY_NAMES);
   }
}
