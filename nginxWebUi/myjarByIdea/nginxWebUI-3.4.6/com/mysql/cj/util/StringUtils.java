package com.mysql.cj.util;

import com.mysql.cj.Messages;
import com.mysql.cj.ServerVersion;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class StringUtils {
   private static final int WILD_COMPARE_MATCH = 0;
   private static final int WILD_COMPARE_CONTINUE_WITH_WILD = 1;
   private static final int WILD_COMPARE_NO_MATCH = -1;
   static final char WILDCARD_MANY = '%';
   static final char WILDCARD_ONE = '_';
   static final char WILDCARD_ESCAPE = '\\';
   private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
   private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
   static final char[] EMPTY_SPACE = new char[255];

   public static String dumpAsHex(byte[] byteBuffer, int length) {
      length = Math.min(length, byteBuffer.length);
      StringBuilder fullOutBuilder = new StringBuilder(length * 4);
      StringBuilder asciiOutBuilder = new StringBuilder(16);
      int p = 0;

      for(int l = 0; p < length; l = 0) {
         while(l < 8 && p < length) {
            int asInt = byteBuffer[p] & 255;
            if (asInt < 16) {
               fullOutBuilder.append("0");
            }

            fullOutBuilder.append(Integer.toHexString(asInt)).append(" ");
            asciiOutBuilder.append(" ").append(asInt >= 32 && asInt < 127 ? (char)asInt : ".");
            ++p;
            ++l;
         }

         while(l < 8) {
            fullOutBuilder.append("   ");
            ++l;
         }

         fullOutBuilder.append("   ").append(asciiOutBuilder).append(System.lineSeparator());
         asciiOutBuilder.setLength(0);
      }

      return fullOutBuilder.toString();
   }

   public static String toHexString(byte[] byteBuffer, int length) {
      length = Math.min(length, byteBuffer.length);
      StringBuilder outputBuilder = new StringBuilder(length * 2);

      for(int i = 0; i < length; ++i) {
         int asInt = byteBuffer[i] & 255;
         if (asInt < 16) {
            outputBuilder.append("0");
         }

         outputBuilder.append(Integer.toHexString(asInt));
      }

      return outputBuilder.toString();
   }

   private static boolean endsWith(byte[] dataFrom, String suffix) {
      for(int i = 1; i <= suffix.length(); ++i) {
         int dfOffset = dataFrom.length - i;
         int suffixOffset = suffix.length() - i;
         if (dataFrom[dfOffset] != suffix.charAt(suffixOffset)) {
            return false;
         }
      }

      return true;
   }

   public static char firstNonWsCharUc(String searchIn) {
      return firstNonWsCharUc(searchIn, 0);
   }

   public static char firstNonWsCharUc(String searchIn, int startAt) {
      if (searchIn == null) {
         return '\u0000';
      } else {
         int length = searchIn.length();

         for(int i = startAt; i < length; ++i) {
            char c = searchIn.charAt(i);
            if (!Character.isWhitespace(c)) {
               return Character.toUpperCase(c);
            }
         }

         return '\u0000';
      }
   }

   public static char firstAlphaCharUc(String searchIn, int startAt) {
      if (searchIn == null) {
         return '\u0000';
      } else {
         int length = searchIn.length();

         for(int i = startAt; i < length; ++i) {
            char c = searchIn.charAt(i);
            if (Character.isLetter(c)) {
               return Character.toUpperCase(c);
            }
         }

         return '\u0000';
      }
   }

   public static String fixDecimalExponent(String dString) {
      int ePos = dString.indexOf(69);
      if (ePos == -1) {
         ePos = dString.indexOf(101);
      }

      if (ePos != -1 && dString.length() > ePos + 1) {
         char maybeMinusChar = dString.charAt(ePos + 1);
         if (maybeMinusChar != '-' && maybeMinusChar != '+') {
            StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
            strBuilder.append(dString.substring(0, ePos + 1));
            strBuilder.append('+');
            strBuilder.append(dString.substring(ePos + 1, dString.length()));
            dString = strBuilder.toString();
         }
      }

      return dString;
   }

   public static byte[] getBytes(String s, String encoding) {
      if (s == null) {
         return new byte[0];
      } else if (encoding == null) {
         return getBytes(s);
      } else {
         try {
            return s.getBytes(encoding);
         } catch (UnsupportedEncodingException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("StringUtils.0", new Object[]{encoding}), (Throwable)var3);
         }
      }
   }

   public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, String encoding) {
      byte[] b;
      StringBuilder strBuilder;
      if (encoding == null) {
         strBuilder = new StringBuilder(s.length() + 2);
         strBuilder.append(beginWrap);
         strBuilder.append(s);
         strBuilder.append(endWrap);
         b = getBytes(strBuilder.toString());
      } else {
         strBuilder = new StringBuilder(s.length() + 2);
         strBuilder.append(beginWrap);
         strBuilder.append(s);
         strBuilder.append(endWrap);
         s = strBuilder.toString();
         b = getBytes(s, encoding);
      }

      return b;
   }

   public static int indexOfIgnoreCase(String searchIn, String searchFor) {
      return indexOfIgnoreCase(0, searchIn, searchFor);
   }

   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor) {
      if (searchIn != null && searchFor != null) {
         int searchInLength = searchIn.length();
         int searchForLength = searchFor.length();
         int stopSearchingAt = searchInLength - searchForLength;
         if (startingPosition <= stopSearchingAt && searchForLength != 0) {
            char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
            char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));

            for(int i = startingPosition; i <= stopSearchingAt; ++i) {
               if (isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc)) {
                  do {
                     ++i;
                  } while(i <= stopSearchingAt && isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc));
               }

               if (i <= stopSearchingAt && regionMatchesIgnoreCase(searchIn, i, searchFor)) {
                  return i;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, "", searchMode);
      return strInspector.indexOfIgnoreCase(searchForSequence);
   }

   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
      return indexOfIgnoreCase(startingPosition, searchIn, searchFor, openingMarkers, closingMarkers, "", searchMode);
   }

   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
      return strInspector.indexOfIgnoreCase(searchFor);
   }

   public static int indexOfNextAlphanumericChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
      return strInspector.indexOfNextAlphanumericChar();
   }

   public static int indexOfNextNonWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
      return strInspector.indexOfNextNonWsChar();
   }

   public static int indexOfNextWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
      return strInspector.indexOfNextWsChar();
   }

   private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
      return Character.toLowerCase(searchIn.charAt(pos)) != firstCharOfSearchForLc && Character.toUpperCase(searchIn.charAt(pos)) != firstCharOfSearchForUc;
   }

   protected static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
      return Character.toLowerCase(charToCompare) == compareToCharLC || Character.toUpperCase(charToCompare) == compareToCharUC;
   }

   public static List<String> split(String stringToSplit, String delimiter, boolean trim) {
      if (stringToSplit == null) {
         return new ArrayList();
      } else if (delimiter == null) {
         throw new IllegalArgumentException();
      } else {
         String[] tokens = stringToSplit.split(delimiter, -1);
         List<String> tokensList = Arrays.asList(tokens);
         if (trim) {
            tokensList = (List)tokensList.stream().map(String::trim).collect(Collectors.toList());
         }

         return tokensList;
      }
   }

   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim) {
      return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim);
   }

   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim, Set<SearchMode> searchMode) {
      return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim, searchMode);
   }

   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim) {
      return split(stringToSplit, delimiter, openingMarkers, closingMarkers, overridingMarkers, trim, SearchMode.__MRK_COM_MYM_HNT_WS);
   }

   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim, Set<SearchMode> searchMode) {
      StringInspector strInspector = new StringInspector(stringToSplit, openingMarkers, closingMarkers, overridingMarkers, searchMode);
      return strInspector.split(delimiter, trim);
   }

   private static boolean startsWith(byte[] dataFrom, String chars) {
      int charsLength = chars.length();
      if (dataFrom.length < charsLength) {
         return false;
      } else {
         for(int i = 0; i < charsLength; ++i) {
            if (dataFrom[i] != chars.charAt(i)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean regionMatchesIgnoreCase(String searchIn, int startAt, String searchFor) {
      return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
   }

   public static boolean startsWithIgnoreCase(String searchIn, String searchFor) {
      return regionMatchesIgnoreCase(searchIn, 0, searchFor);
   }

   public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor) {
      if (searchIn == null) {
         return searchFor == null;
      } else {
         int beginPos = 0;

         for(int inLength = searchIn.length(); beginPos < inLength; ++beginPos) {
            char c = searchIn.charAt(beginPos);
            if (Character.isLetterOrDigit(c)) {
               break;
            }
         }

         return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
      }
   }

   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor) {
      return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
   }

   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
      if (searchIn == null) {
         return searchFor == null;
      } else {
         while(beginPos < searchIn.length() && Character.isWhitespace(searchIn.charAt(beginPos))) {
            ++beginPos;
         }

         return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
      }
   }

   public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor) {
      for(int i = 0; i < searchFor.length; ++i) {
         if (startsWithIgnoreCaseAndWs(searchIn, searchFor[i], 0)) {
            return i;
         }
      }

      return -1;
   }

   public static boolean endsWithIgnoreCase(String searchIn, String searchFor) {
      int len = searchFor.length();
      return searchIn.regionMatches(true, searchIn.length() - len, searchFor, 0, len);
   }

   public static byte[] stripEnclosure(byte[] source, String prefix, String suffix) {
      if (source.length >= prefix.length() + suffix.length() && startsWith(source, prefix) && endsWith(source, suffix)) {
         int totalToStrip = prefix.length() + suffix.length();
         int enclosedLength = source.length - totalToStrip;
         byte[] enclosed = new byte[enclosedLength];
         int startPos = prefix.length();
         int numToCopy = enclosed.length;
         System.arraycopy(source, startPos, enclosed, 0, numToCopy);
         return enclosed;
      } else {
         return source;
      }
   }

   public static String toAsciiString(byte[] buffer) {
      return toAsciiString(buffer, 0, buffer.length);
   }

   public static String toAsciiString(byte[] buffer, int startPos, int length) {
      char[] charArray = new char[length];
      int readpoint = startPos;

      for(int i = 0; i < length; ++i) {
         charArray[i] = (char)buffer[readpoint];
         ++readpoint;
      }

      return new String(charArray);
   }

   public static boolean wildCompareIgnoreCase(String searchIn, String searchFor) {
      return wildCompareInternal(searchIn, searchFor) == 0;
   }

   private static int wildCompareInternal(String searchIn, String searchFor) {
      if (searchIn != null && searchFor != null) {
         if (searchFor.equals("%")) {
            return 0;
         } else {
            int searchForPos = 0;
            int searchForEnd = searchFor.length();
            int searchInPos = 0;
            int searchInEnd = searchIn.length();
            int result = -1;

            while(searchForPos != searchForEnd) {
               while(true) {
                  if (searchFor.charAt(searchForPos) != '%' && searchFor.charAt(searchForPos) != '_') {
                     if (searchFor.charAt(searchForPos) == '\\' && searchForPos + 1 != searchForEnd) {
                        ++searchForPos;
                     }

                     if (searchInPos == searchInEnd || Character.toUpperCase(searchFor.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++))) {
                        return 1;
                     }

                     if (searchForPos == searchForEnd) {
                        return searchInPos != searchInEnd ? 1 : 0;
                     }

                     result = 1;
                  } else {
                     if (searchFor.charAt(searchForPos) == '_') {
                        do {
                           if (searchInPos == searchInEnd) {
                              return result;
                           }

                           ++searchInPos;
                           ++searchForPos;
                        } while(searchForPos < searchForEnd && searchFor.charAt(searchForPos) == '_');

                        if (searchForPos == searchForEnd) {
                           return searchInPos != searchInEnd ? 1 : 0;
                        }
                     }

                     if (searchFor.charAt(searchForPos) == '%') {
                        ++searchForPos;

                        for(; searchForPos != searchForEnd; ++searchForPos) {
                           if (searchFor.charAt(searchForPos) != '%') {
                              if (searchFor.charAt(searchForPos) != '_') {
                                 break;
                              }

                              if (searchInPos == searchInEnd) {
                                 return -1;
                              }

                              ++searchInPos;
                           }
                        }

                        if (searchForPos == searchForEnd) {
                           return 0;
                        }

                        if (searchInPos == searchInEnd) {
                           return -1;
                        }

                        char cmp;
                        if ((cmp = searchFor.charAt(searchForPos)) == '\\' && searchForPos + 1 != searchForEnd) {
                           ++searchForPos;
                           cmp = searchFor.charAt(searchForPos);
                        }

                        ++searchForPos;

                        while(true) {
                           while(searchInPos == searchInEnd || Character.toUpperCase(searchIn.charAt(searchInPos)) == Character.toUpperCase(cmp)) {
                              if (searchInPos++ == searchInEnd) {
                                 return -1;
                              }

                              int tmp = wildCompareInternal(searchIn.substring(searchInPos), searchFor.substring(searchForPos));
                              if (tmp <= 0) {
                                 return tmp;
                              }

                              if (searchInPos == searchInEnd) {
                                 return -1;
                              }
                           }

                           ++searchInPos;
                        }
                     }
                  }
               }
            }

            return searchInPos != searchInEnd ? 1 : 0;
         }
      } else {
         return -1;
      }
   }

   public static int lastIndexOf(byte[] s, char c) {
      if (s == null) {
         return -1;
      } else {
         for(int i = s.length - 1; i >= 0; --i) {
            if (s[i] == c) {
               return i;
            }
         }

         return -1;
      }
   }

   public static int indexOf(byte[] s, char c) {
      if (s == null) {
         return -1;
      } else {
         int length = s.length;

         for(int i = 0; i < length; ++i) {
            if (s[i] == c) {
               return i;
            }
         }

         return -1;
      }
   }

   public static boolean isNullOrEmpty(String str) {
      return str == null || str.isEmpty();
   }

   public static boolean nullSafeEqual(String str1, String str2) {
      return str1 == null && str2 == null || str1 != null && str1.equals(str2);
   }

   public static String stripCommentsAndHints(String source, String openingMarkers, String closingMarkers, boolean allowBackslashEscapes) {
      StringInspector strInspector = new StringInspector(source, openingMarkers, closingMarkers, "", allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS);
      return strInspector.stripCommentsAndHints();
   }

   public static String sanitizeProcOrFuncName(String src) {
      return src != null && !src.equals("%") ? src : null;
   }

   public static List<String> splitDBdotName(String source, String db, String quoteId, boolean isNoBslashEscSet) {
      if (source != null && !source.equals("%")) {
         int dotIndex = true;
         int dotIndex;
         if (" ".equals(quoteId)) {
            dotIndex = source.indexOf(".");
         } else {
            dotIndex = indexOfIgnoreCase(0, source, (String)".", quoteId, quoteId, isNoBslashEscSet ? SearchMode.__MRK_WS : SearchMode.__BSE_MRK_WS);
         }

         String database = db;
         String entityName;
         if (dotIndex != -1) {
            database = unQuoteIdentifier(source.substring(0, dotIndex), quoteId);
            entityName = unQuoteIdentifier(source.substring(dotIndex + 1), quoteId);
         } else {
            entityName = unQuoteIdentifier(source, quoteId);
         }

         return Arrays.asList(database, entityName);
      } else {
         return Collections.emptyList();
      }
   }

   public static String getFullyQualifiedName(String db, String entity, String quoteId, boolean isPedantic) {
      StringBuilder fullyQualifiedName = new StringBuilder(quoteIdentifier(db == null ? "" : db, quoteId, isPedantic));
      fullyQualifiedName.append('.');
      fullyQualifiedName.append(quoteIdentifier(entity, quoteId, isPedantic));
      return fullyQualifiedName.toString();
   }

   public static boolean isEmptyOrWhitespaceOnly(String str) {
      if (str != null && str.length() != 0) {
         int length = str.length();

         for(int i = 0; i < length; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static String escapeQuote(String str, String quotChar) {
      if (str == null) {
         return null;
      } else {
         str = toString(stripEnclosure(str.getBytes(), quotChar, quotChar));
         int lastNdx = str.indexOf(quotChar);
         String tmpSrc = str.substring(0, lastNdx);
         tmpSrc = tmpSrc + quotChar + quotChar;
         String tmpRest = str.substring(lastNdx + 1, str.length());

         for(lastNdx = tmpRest.indexOf(quotChar); lastNdx > -1; lastNdx = tmpRest.indexOf(quotChar)) {
            tmpSrc = tmpSrc + tmpRest.substring(0, lastNdx);
            tmpSrc = tmpSrc + quotChar + quotChar;
            tmpRest = tmpRest.substring(lastNdx + 1, tmpRest.length());
         }

         tmpSrc = tmpSrc + tmpRest;
         return tmpSrc;
      }
   }

   public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic) {
      if (identifier == null) {
         return null;
      } else {
         identifier = identifier.trim();
         int quoteCharLength = quoteChar.length();
         if (quoteCharLength == 0) {
            return identifier;
         } else {
            if (!isPedantic && identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
               String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);

               int quoteCharPos;
               int quoteCharNextPosition;
               for(quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar); quoteCharPos >= 0; quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength)) {
                  int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
                  quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
                  if (quoteCharNextPosition != quoteCharNextExpectedPos) {
                     break;
                  }
               }

               if (quoteCharPos < 0) {
                  return identifier;
               }
            }

            return quoteChar + identifier.replaceAll(quoteChar, quoteChar + quoteChar) + quoteChar;
         }
      }
   }

   public static String quoteIdentifier(String identifier, boolean isPedantic) {
      return quoteIdentifier(identifier, "`", isPedantic);
   }

   public static String unQuoteIdentifier(String identifier, String quoteChar) {
      if (identifier == null) {
         return null;
      } else {
         identifier = identifier.trim();
         int quoteCharLength = quoteChar.length();
         if (quoteCharLength == 0) {
            return identifier;
         } else if (identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
            String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);

            int quoteCharNextPosition;
            for(int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar); quoteCharPos >= 0; quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength)) {
               int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
               quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
               if (quoteCharNextPosition != quoteCharNextExpectedPos) {
                  return identifier;
               }
            }

            return identifier.substring(quoteCharLength, identifier.length() - quoteCharLength).replaceAll(quoteChar + quoteChar, quoteChar);
         } else {
            return identifier;
         }
      }
   }

   public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
      if (searchIn != null && quoteChar != null && quoteChar.length() != 0 && startFrom <= searchIn.length()) {
         int lastIndex = searchIn.length() - 1;
         int beginPos = startFrom;
         int pos = -1;
         boolean next = true;

         while(true) {
            while(next) {
               pos = searchIn.indexOf(quoteChar, beginPos);
               if (pos != -1 && pos != lastIndex && searchIn.startsWith(quoteChar, pos + 1)) {
                  beginPos = pos + 2;
               } else {
                  next = false;
               }
            }

            return pos;
         }
      } else {
         return -1;
      }
   }

   public static String toString(byte[] value, int offset, int length, String encoding) {
      if (encoding != null && !"null".equalsIgnoreCase(encoding)) {
         try {
            return new String(value, offset, length, encoding);
         } catch (UnsupportedEncodingException var5) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("StringUtils.0", new Object[]{encoding}), (Throwable)var5);
         }
      } else {
         return new String(value, offset, length);
      }
   }

   public static String toString(byte[] value, String encoding) {
      if (encoding == null) {
         return new String(value);
      } else {
         try {
            return new String(value, encoding);
         } catch (UnsupportedEncodingException var3) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("StringUtils.0", new Object[]{encoding}), (Throwable)var3);
         }
      }
   }

   public static String toString(byte[] value, int offset, int length) {
      return new String(value, offset, length);
   }

   public static String toString(byte[] value) {
      return new String(value);
   }

   public static byte[] getBytes(char[] value) {
      return getBytes((char[])value, 0, value.length);
   }

   public static byte[] getBytes(char[] c, String encoding) {
      return getBytes((char[])c, 0, c.length, encoding);
   }

   public static byte[] getBytes(char[] value, int offset, int length) {
      return getBytes((char[])value, offset, length, (String)null);
   }

   public static byte[] getBytes(char[] value, int offset, int length, String encoding) {
      Charset cs;
      try {
         if (encoding == null) {
            cs = Charset.defaultCharset();
         } else {
            cs = Charset.forName(encoding);
         }
      } catch (UnsupportedCharsetException var8) {
         throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("StringUtils.0", new Object[]{encoding}), (Throwable)var8);
      }

      ByteBuffer buf = cs.encode(CharBuffer.wrap(value, offset, length));
      int encodedLen = buf.limit();
      byte[] asBytes = new byte[encodedLen];
      buf.get(asBytes, 0, encodedLen);
      return asBytes;
   }

   public static byte[] getBytes(String value) {
      return value.getBytes();
   }

   public static byte[] getBytes(String value, int offset, int length) {
      return value.substring(offset, offset + length).getBytes();
   }

   public static byte[] getBytes(String value, int offset, int length, String encoding) {
      if (encoding == null) {
         return getBytes(value, offset, length);
      } else {
         try {
            return value.substring(offset, offset + length).getBytes(encoding);
         } catch (UnsupportedEncodingException var5) {
            throw (WrongArgumentException)ExceptionFactory.createException((Class)WrongArgumentException.class, (String)Messages.getString("StringUtils.0", new Object[]{encoding}), (Throwable)var5);
         }
      }
   }

   public static final boolean isValidIdChar(char c) {
      return "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@".indexOf(c) != -1;
   }

   public static void appendAsHex(StringBuilder builder, byte[] bytes) {
      builder.append("0x");
      byte[] var2 = bytes;
      int var3 = bytes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte b = var2[var4];
         builder.append(HEX_DIGITS[b >>> 4 & 15]).append(HEX_DIGITS[b & 15]);
      }

   }

   public static void appendAsHex(StringBuilder builder, int value) {
      if (value == 0) {
         builder.append("0x0");
      } else {
         int shift = 32;
         boolean nonZeroFound = false;
         builder.append("0x");

         do {
            shift -= 4;
            byte nibble = (byte)(value >>> shift & 15);
            if (nonZeroFound) {
               builder.append(HEX_DIGITS[nibble]);
            } else if (nibble != 0) {
               builder.append(HEX_DIGITS[nibble]);
               nonZeroFound = true;
            }
         } while(shift != 0);

      }
   }

   public static byte[] getBytesNullTerminated(String value, String encoding) {
      Charset cs = Charset.forName(encoding);
      ByteBuffer buf = cs.encode(value);
      int encodedLen = buf.limit();
      byte[] asBytes = new byte[encodedLen + 1];
      buf.get(asBytes, 0, encodedLen);
      asBytes[encodedLen] = 0;
      return asBytes;
   }

   public static boolean canHandleAsServerPreparedStatementNoCache(String sql, ServerVersion serverVersion, boolean allowMultiQueries, boolean noBackslashEscapes, boolean useAnsiQuotes) {
      if (startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
         return false;
      } else {
         boolean canHandleAsStatement = true;
         boolean allowBackslashEscapes = !noBackslashEscapes;
         String quoteChar = useAnsiQuotes ? "\"" : "'";
         if (allowMultiQueries) {
            if (indexOfIgnoreCase(0, sql, (String)";", quoteChar, quoteChar, allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS) != -1) {
               canHandleAsStatement = false;
            }
         } else if (startsWithIgnoreCaseAndWs(sql, "XA ")) {
            canHandleAsStatement = false;
         } else if (startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
            canHandleAsStatement = false;
         } else if (startsWithIgnoreCaseAndWs(sql, "DO")) {
            canHandleAsStatement = false;
         } else if (startsWithIgnoreCaseAndWs(sql, "SET")) {
            canHandleAsStatement = false;
         } else if (startsWithIgnoreCaseAndWs(sql, "SHOW WARNINGS") && serverVersion.meetsMinimum(ServerVersion.parseVersion("5.7.2"))) {
            canHandleAsStatement = false;
         } else if (sql.startsWith("/* ping */")) {
            canHandleAsStatement = false;
         }

         return canHandleAsStatement;
      }
   }

   public static String padString(String stringVal, int requiredLength) {
      int currentLength = stringVal.length();
      int difference = requiredLength - currentLength;
      if (difference > 0) {
         StringBuilder paddedBuf = new StringBuilder(requiredLength);
         paddedBuf.append(stringVal);
         paddedBuf.append(EMPTY_SPACE, 0, difference);
         return paddedBuf.toString();
      } else {
         return stringVal;
      }
   }

   public static int safeIntParse(String intAsString) {
      try {
         return Integer.parseInt(intAsString);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }

   public static boolean isStrictlyNumeric(CharSequence cs) {
      if (cs != null && cs.length() != 0) {
         for(int i = 0; i < cs.length(); ++i) {
            if (!Character.isDigit(cs.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static String safeTrim(String toTrim) {
      return isNullOrEmpty(toTrim) ? toTrim : toTrim.trim();
   }

   public static String stringArrayToString(String[] elems, String prefix, String midDelimiter, String lastDelimiter, String suffix) {
      StringBuilder valuesString = new StringBuilder();
      if (elems.length > 1) {
         valuesString.append((String)Arrays.stream(elems).limit((long)(elems.length - 1)).collect(Collectors.joining(midDelimiter, prefix, lastDelimiter)));
      } else {
         valuesString.append(prefix);
      }

      valuesString.append(elems[elems.length - 1]).append(suffix);
      return valuesString.toString();
   }

   public static boolean hasWildcards(String src) {
      return indexOfIgnoreCase(0, src, "%") > -1 || indexOfIgnoreCase(0, src, "_") > -1;
   }

   public static String getUniqueSavepointId() {
      String uuid = UUID.randomUUID().toString();
      return uuid.replaceAll("-", "_");
   }

   public static String joinWithSerialComma(List<?> elements) {
      if (elements != null && elements.size() != 0) {
         if (elements.size() == 1) {
            return elements.get(0).toString();
         } else {
            return elements.size() == 2 ? elements.get(0) + " and " + elements.get(1) : (String)elements.subList(0, elements.size() - 1).stream().map(Object::toString).collect(Collectors.joining(", ", "", ", and ")) + elements.get(elements.size() - 1).toString();
         }
      } else {
         return "";
      }
   }

   public static byte[] unquoteBytes(byte[] bytes) {
      if (bytes[0] == 39 && bytes[bytes.length - 1] == 39) {
         byte[] valNoQuotes = new byte[bytes.length - 2];
         int j = 0;
         int quoteCnt = 0;

         for(int i = 1; i < bytes.length - 1; ++i) {
            if (bytes[i] == 39) {
               ++quoteCnt;
            } else {
               quoteCnt = 0;
            }

            if (quoteCnt == 2) {
               quoteCnt = 0;
            } else {
               valNoQuotes[j++] = bytes[i];
            }
         }

         byte[] res = new byte[j];
         System.arraycopy(valNoQuotes, 0, res, 0, j);
         return res;
      } else {
         return bytes;
      }
   }

   public static byte[] quoteBytes(byte[] bytes) {
      byte[] withQuotes = new byte[bytes.length * 2 + 2];
      int j = 0;
      withQuotes[j++] = 39;

      for(int i = 0; i < bytes.length; ++i) {
         if (bytes[i] == 39) {
            withQuotes[j++] = 39;
         }

         withQuotes[j++] = bytes[i];
      }

      withQuotes[j++] = 39;
      byte[] res = new byte[j];
      System.arraycopy(withQuotes, 0, res, 0, j);
      return res;
   }

   static {
      for(int i = 0; i < EMPTY_SPACE.length; ++i) {
         EMPTY_SPACE[i] = ' ';
      }

   }
}
