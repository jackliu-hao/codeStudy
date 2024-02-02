/*      */ package com.mysql.cj.util;
/*      */ 
/*      */ import com.mysql.cj.Messages;
/*      */ import com.mysql.cj.ServerVersion;
/*      */ import com.mysql.cj.exceptions.ExceptionFactory;
/*      */ import com.mysql.cj.exceptions.WrongArgumentException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.nio.ByteBuffer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.UnsupportedCharsetException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.List;
/*      */ import java.util.Set;
/*      */ import java.util.UUID;
/*      */ import java.util.stream.Collectors;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class StringUtils
/*      */ {
/*      */   private static final int WILD_COMPARE_MATCH = 0;
/*      */   private static final int WILD_COMPARE_CONTINUE_WITH_WILD = 1;
/*      */   private static final int WILD_COMPARE_NO_MATCH = -1;
/*      */   static final char WILDCARD_MANY = '%';
/*      */   static final char WILDCARD_ONE = '_';
/*      */   static final char WILDCARD_ESCAPE = '\\';
/*      */   private static final String VALID_ID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@";
/*      */   
/*      */   public static String dumpAsHex(byte[] byteBuffer, int length) {
/*   75 */     length = Math.min(length, byteBuffer.length);
/*   76 */     StringBuilder fullOutBuilder = new StringBuilder(length * 4);
/*   77 */     StringBuilder asciiOutBuilder = new StringBuilder(16);
/*      */     int l;
/*   79 */     for (int p = 0; p < length; l = 0) {
/*   80 */       for (; l < 8 && p < length; p++, l++) {
/*   81 */         int asInt = byteBuffer[p] & 0xFF;
/*   82 */         if (asInt < 16) {
/*   83 */           fullOutBuilder.append("0");
/*      */         }
/*   85 */         fullOutBuilder.append(Integer.toHexString(asInt)).append(" ");
/*   86 */         asciiOutBuilder.append(" ").append((asInt >= 32 && asInt < 127) ? Character.valueOf((char)asInt) : ".");
/*      */       } 
/*   88 */       for (; l < 8; l++) {
/*   89 */         fullOutBuilder.append("   ");
/*      */       }
/*   91 */       fullOutBuilder.append("   ").append(asciiOutBuilder).append(System.lineSeparator());
/*   92 */       asciiOutBuilder.setLength(0);
/*      */     } 
/*   94 */     return fullOutBuilder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toHexString(byte[] byteBuffer, int length) {
/*  108 */     length = Math.min(length, byteBuffer.length);
/*  109 */     StringBuilder outputBuilder = new StringBuilder(length * 2);
/*  110 */     for (int i = 0; i < length; i++) {
/*  111 */       int asInt = byteBuffer[i] & 0xFF;
/*  112 */       if (asInt < 16) {
/*  113 */         outputBuilder.append("0");
/*      */       }
/*  115 */       outputBuilder.append(Integer.toHexString(asInt));
/*      */     } 
/*  117 */     return outputBuilder.toString();
/*      */   }
/*      */   
/*      */   private static boolean endsWith(byte[] dataFrom, String suffix) {
/*  121 */     for (int i = 1; i <= suffix.length(); i++) {
/*  122 */       int dfOffset = dataFrom.length - i;
/*  123 */       int suffixOffset = suffix.length() - i;
/*  124 */       if (dataFrom[dfOffset] != suffix.charAt(suffixOffset)) {
/*  125 */         return false;
/*      */       }
/*      */     } 
/*  128 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static char firstNonWsCharUc(String searchIn) {
/*  140 */     return firstNonWsCharUc(searchIn, 0);
/*      */   }
/*      */   
/*      */   public static char firstNonWsCharUc(String searchIn, int startAt) {
/*  144 */     if (searchIn == null) {
/*  145 */       return Character.MIN_VALUE;
/*      */     }
/*      */     
/*  148 */     int length = searchIn.length();
/*      */     
/*  150 */     for (int i = startAt; i < length; i++) {
/*  151 */       char c = searchIn.charAt(i);
/*      */       
/*  153 */       if (!Character.isWhitespace(c)) {
/*  154 */         return Character.toUpperCase(c);
/*      */       }
/*      */     } 
/*      */     
/*  158 */     return Character.MIN_VALUE;
/*      */   }
/*      */   
/*      */   public static char firstAlphaCharUc(String searchIn, int startAt) {
/*  162 */     if (searchIn == null) {
/*  163 */       return Character.MIN_VALUE;
/*      */     }
/*      */     
/*  166 */     int length = searchIn.length();
/*      */     
/*  168 */     for (int i = startAt; i < length; i++) {
/*  169 */       char c = searchIn.charAt(i);
/*      */       
/*  171 */       if (Character.isLetter(c)) {
/*  172 */         return Character.toUpperCase(c);
/*      */       }
/*      */     } 
/*      */     
/*  176 */     return Character.MIN_VALUE;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fixDecimalExponent(String dString) {
/*  189 */     int ePos = dString.indexOf('E');
/*      */     
/*  191 */     if (ePos == -1) {
/*  192 */       ePos = dString.indexOf('e');
/*      */     }
/*      */     
/*  195 */     if (ePos != -1 && 
/*  196 */       dString.length() > ePos + 1) {
/*  197 */       char maybeMinusChar = dString.charAt(ePos + 1);
/*      */       
/*  199 */       if (maybeMinusChar != '-' && maybeMinusChar != '+') {
/*  200 */         StringBuilder strBuilder = new StringBuilder(dString.length() + 1);
/*  201 */         strBuilder.append(dString.substring(0, ePos + 1));
/*  202 */         strBuilder.append('+');
/*  203 */         strBuilder.append(dString.substring(ePos + 1, dString.length()));
/*  204 */         dString = strBuilder.toString();
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  209 */     return dString;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytes(String s, String encoding) {
/*  222 */     if (s == null) {
/*  223 */       return new byte[0];
/*      */     }
/*  225 */     if (encoding == null) {
/*  226 */       return getBytes(s);
/*      */     }
/*      */     try {
/*  229 */       return s.getBytes(encoding);
/*  230 */     } catch (UnsupportedEncodingException uee) {
/*  231 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytesWrapped(String s, char beginWrap, char endWrap, String encoding) {
/*      */     byte[] b;
/*  252 */     if (encoding == null) {
/*  253 */       StringBuilder strBuilder = new StringBuilder(s.length() + 2);
/*  254 */       strBuilder.append(beginWrap);
/*  255 */       strBuilder.append(s);
/*  256 */       strBuilder.append(endWrap);
/*      */       
/*  258 */       b = getBytes(strBuilder.toString());
/*      */     } else {
/*  260 */       StringBuilder strBuilder = new StringBuilder(s.length() + 2);
/*  261 */       strBuilder.append(beginWrap);
/*  262 */       strBuilder.append(s);
/*  263 */       strBuilder.append(endWrap);
/*      */       
/*  265 */       s = strBuilder.toString();
/*  266 */       b = getBytes(s, encoding);
/*      */     } 
/*      */     
/*  269 */     return b;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(String searchIn, String searchFor) {
/*  282 */     return indexOfIgnoreCase(0, searchIn, searchFor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor) {
/*  297 */     if (searchIn == null || searchFor == null) {
/*  298 */       return -1;
/*      */     }
/*      */     
/*  301 */     int searchInLength = searchIn.length();
/*  302 */     int searchForLength = searchFor.length();
/*  303 */     int stopSearchingAt = searchInLength - searchForLength;
/*      */     
/*  305 */     if (startingPosition > stopSearchingAt || searchForLength == 0) {
/*  306 */       return -1;
/*      */     }
/*      */ 
/*      */     
/*  310 */     char firstCharOfSearchForUc = Character.toUpperCase(searchFor.charAt(0));
/*  311 */     char firstCharOfSearchForLc = Character.toLowerCase(searchFor.charAt(0));
/*      */     
/*  313 */     for (int i = startingPosition; i <= stopSearchingAt; i++) {
/*  314 */       if (isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc))
/*      */       {
/*  316 */         while (++i <= stopSearchingAt && isCharAtPosNotEqualIgnoreCase(searchIn, i, firstCharOfSearchForUc, firstCharOfSearchForLc));
/*      */       }
/*      */ 
/*      */       
/*  320 */       if (i <= stopSearchingAt && regionMatchesIgnoreCase(searchIn, i, searchFor)) {
/*  321 */         return i;
/*      */       }
/*      */     } 
/*      */     
/*  325 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String[] searchForSequence, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
/*  353 */     StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, "", searchMode);
/*  354 */     return strInspector.indexOfIgnoreCase(searchForSequence);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, Set<SearchMode> searchMode) {
/*  377 */     return indexOfIgnoreCase(startingPosition, searchIn, searchFor, openingMarkers, closingMarkers, "", searchMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfIgnoreCase(int startingPosition, String searchIn, String searchFor, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/*  404 */     StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
/*  405 */     return strInspector.indexOfIgnoreCase(searchFor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfNextAlphanumericChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/*  430 */     StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
/*  431 */     return strInspector.indexOfNextAlphanumericChar();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfNextNonWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/*  456 */     StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
/*  457 */     return strInspector.indexOfNextNonWsChar();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int indexOfNextWsChar(int startingPosition, String searchIn, String openingMarkers, String closingMarkers, String overridingMarkers, Set<SearchMode> searchMode) {
/*  482 */     StringInspector strInspector = new StringInspector(searchIn, startingPosition, openingMarkers, closingMarkers, overridingMarkers, searchMode);
/*  483 */     return strInspector.indexOfNextWsChar();
/*      */   }
/*      */   
/*      */   private static boolean isCharAtPosNotEqualIgnoreCase(String searchIn, int pos, char firstCharOfSearchForUc, char firstCharOfSearchForLc) {
/*  487 */     return (Character.toLowerCase(searchIn.charAt(pos)) != firstCharOfSearchForLc && Character.toUpperCase(searchIn.charAt(pos)) != firstCharOfSearchForUc);
/*      */   }
/*      */   
/*      */   protected static boolean isCharEqualIgnoreCase(char charToCompare, char compareToCharUC, char compareToCharLC) {
/*  491 */     return (Character.toLowerCase(charToCompare) == compareToCharLC || Character.toUpperCase(charToCompare) == compareToCharUC);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(String stringToSplit, String delimiter, boolean trim) {
/*  510 */     if (stringToSplit == null) {
/*  511 */       return new ArrayList<>();
/*      */     }
/*      */     
/*  514 */     if (delimiter == null) {
/*  515 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*  518 */     String[] tokens = stringToSplit.split(delimiter, -1);
/*  519 */     List<String> tokensList = Arrays.asList(tokens);
/*  520 */     if (trim) {
/*  521 */       tokensList = (List<String>)tokensList.stream().map(String::trim).collect(Collectors.toList());
/*      */     }
/*  523 */     return tokensList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim) {
/*  546 */     return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, boolean trim, Set<SearchMode> searchMode) {
/*  573 */     return split(stringToSplit, delimiter, openingMarkers, closingMarkers, "", trim, searchMode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim) {
/*  601 */     return split(stringToSplit, delimiter, openingMarkers, closingMarkers, overridingMarkers, trim, SearchMode.__MRK_COM_MYM_HNT_WS);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> split(String stringToSplit, String delimiter, String openingMarkers, String closingMarkers, String overridingMarkers, boolean trim, Set<SearchMode> searchMode) {
/*  632 */     StringInspector strInspector = new StringInspector(stringToSplit, openingMarkers, closingMarkers, overridingMarkers, searchMode);
/*  633 */     return strInspector.split(delimiter, trim);
/*      */   }
/*      */   
/*      */   private static boolean startsWith(byte[] dataFrom, String chars) {
/*  637 */     int charsLength = chars.length();
/*      */     
/*  639 */     if (dataFrom.length < charsLength) {
/*  640 */       return false;
/*      */     }
/*  642 */     for (int i = 0; i < charsLength; i++) {
/*  643 */       if (dataFrom[i] != chars.charAt(i)) {
/*  644 */         return false;
/*      */       }
/*      */     } 
/*  647 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean regionMatchesIgnoreCase(String searchIn, int startAt, String searchFor) {
/*  664 */     return searchIn.regionMatches(true, startAt, searchFor, 0, searchFor.length());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startsWithIgnoreCase(String searchIn, String searchFor) {
/*  678 */     return regionMatchesIgnoreCase(searchIn, 0, searchFor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startsWithIgnoreCaseAndNonAlphaNumeric(String searchIn, String searchFor) {
/*  692 */     if (searchIn == null) {
/*  693 */       return (searchFor == null);
/*      */     }
/*      */     
/*  696 */     int beginPos = 0;
/*  697 */     int inLength = searchIn.length();
/*      */     
/*  699 */     for (; beginPos < inLength; beginPos++) {
/*  700 */       char c = searchIn.charAt(beginPos);
/*  701 */       if (Character.isLetterOrDigit(c)) {
/*      */         break;
/*      */       }
/*      */     } 
/*      */     
/*  706 */     return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor) {
/*  720 */     return startsWithIgnoreCaseAndWs(searchIn, searchFor, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean startsWithIgnoreCaseAndWs(String searchIn, String searchFor, int beginPos) {
/*  737 */     if (searchIn == null) {
/*  738 */       return (searchFor == null);
/*      */     }
/*      */     
/*  741 */     for (; beginPos < searchIn.length() && 
/*  742 */       Character.isWhitespace(searchIn.charAt(beginPos)); beginPos++);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  747 */     return regionMatchesIgnoreCase(searchIn, beginPos, searchFor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int startsWithIgnoreCaseAndWs(String searchIn, String[] searchFor) {
/*  761 */     for (int i = 0; i < searchFor.length; i++) {
/*  762 */       if (startsWithIgnoreCaseAndWs(searchIn, searchFor[i], 0)) {
/*  763 */         return i;
/*      */       }
/*      */     } 
/*  766 */     return -1;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean endsWithIgnoreCase(String searchIn, String searchFor) {
/*  781 */     int len = searchFor.length();
/*  782 */     return searchIn.regionMatches(true, searchIn.length() - len, searchFor, 0, len);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] stripEnclosure(byte[] source, String prefix, String suffix) {
/*  795 */     if (source.length >= prefix.length() + suffix.length() && startsWith(source, prefix) && endsWith(source, suffix)) {
/*      */       
/*  797 */       int totalToStrip = prefix.length() + suffix.length();
/*  798 */       int enclosedLength = source.length - totalToStrip;
/*  799 */       byte[] enclosed = new byte[enclosedLength];
/*      */       
/*  801 */       int startPos = prefix.length();
/*  802 */       int numToCopy = enclosed.length;
/*  803 */       System.arraycopy(source, startPos, enclosed, 0, numToCopy);
/*      */       
/*  805 */       return enclosed;
/*      */     } 
/*  807 */     return source;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toAsciiString(byte[] buffer) {
/*  819 */     return toAsciiString(buffer, 0, buffer.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toAsciiString(byte[] buffer, int startPos, int length) {
/*  835 */     char[] charArray = new char[length];
/*  836 */     int readpoint = startPos;
/*      */     
/*  838 */     for (int i = 0; i < length; i++) {
/*  839 */       charArray[i] = (char)buffer[readpoint];
/*  840 */       readpoint++;
/*      */     } 
/*      */     
/*  843 */     return new String(charArray);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean wildCompareIgnoreCase(String searchIn, String searchFor) {
/*  856 */     return (wildCompareInternal(searchIn, searchFor) == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static int wildCompareInternal(String searchIn, String searchFor) {
/*  874 */     if (searchIn == null || searchFor == null) {
/*  875 */       return -1;
/*      */     }
/*      */     
/*  878 */     if (searchFor.equals("%")) {
/*  879 */       return 0;
/*      */     }
/*      */     
/*  882 */     int searchForPos = 0;
/*  883 */     int searchForEnd = searchFor.length();
/*      */     
/*  885 */     int searchInPos = 0;
/*  886 */     int searchInEnd = searchIn.length();
/*      */     
/*  888 */     int result = -1;
/*      */     
/*  890 */     while (searchForPos != searchForEnd) {
/*  891 */       while (searchFor.charAt(searchForPos) != '%' && searchFor.charAt(searchForPos) != '_') {
/*  892 */         if (searchFor.charAt(searchForPos) == '\\' && searchForPos + 1 != searchForEnd) {
/*  893 */           searchForPos++;
/*      */         }
/*      */         
/*  896 */         if (searchInPos == searchInEnd || 
/*  897 */           Character.toUpperCase(searchFor.charAt(searchForPos++)) != Character.toUpperCase(searchIn.charAt(searchInPos++))) {
/*  898 */           return 1;
/*      */         }
/*      */         
/*  901 */         if (searchForPos == searchForEnd) {
/*  902 */           return (searchInPos != searchInEnd) ? 1 : 0;
/*      */         }
/*      */         
/*  905 */         result = 1;
/*      */       } 
/*      */       
/*  908 */       if (searchFor.charAt(searchForPos) == '_') {
/*      */         do {
/*  910 */           if (searchInPos == searchInEnd) {
/*  911 */             return result;
/*      */           }
/*  913 */           searchInPos++;
/*  914 */         } while (++searchForPos < searchForEnd && searchFor.charAt(searchForPos) == '_');
/*      */         
/*  916 */         if (searchForPos == searchForEnd) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */       
/*  921 */       if (searchFor.charAt(searchForPos) == '%') {
/*  922 */         searchForPos++;
/*      */ 
/*      */         
/*  925 */         for (; searchForPos != searchForEnd; searchForPos++) {
/*  926 */           if (searchFor.charAt(searchForPos) != '%')
/*      */           {
/*      */ 
/*      */             
/*  930 */             if (searchFor.charAt(searchForPos) == '_') {
/*  931 */               if (searchInPos == searchInEnd) {
/*  932 */                 return -1;
/*      */               }
/*  934 */               searchInPos++;
/*      */             } else {
/*      */               break;
/*      */             } 
/*      */           }
/*      */         } 
/*      */         
/*  941 */         if (searchForPos == searchForEnd) {
/*  942 */           return 0;
/*      */         }
/*      */         
/*  945 */         if (searchInPos == searchInEnd) {
/*  946 */           return -1;
/*      */         }
/*      */         
/*      */         char cmp;
/*  950 */         if ((cmp = searchFor.charAt(searchForPos)) == '\\' && searchForPos + 1 != searchForEnd) {
/*  951 */           cmp = searchFor.charAt(++searchForPos);
/*      */         }
/*      */         
/*  954 */         searchForPos++;
/*      */         
/*      */         while (true) {
/*  957 */           if (searchInPos != searchInEnd && Character.toUpperCase(searchIn.charAt(searchInPos)) != Character.toUpperCase(cmp)) {
/*  958 */             searchInPos++;
/*      */             continue;
/*      */           } 
/*  961 */           if (searchInPos++ == searchInEnd) {
/*  962 */             return -1;
/*      */           }
/*      */           
/*  965 */           int tmp = wildCompareInternal(searchIn.substring(searchInPos), searchFor.substring(searchForPos));
/*  966 */           if (tmp <= 0) {
/*  967 */             return tmp;
/*      */           }
/*      */           
/*  970 */           if (searchInPos == searchInEnd)
/*      */             break; 
/*  972 */         }  return -1;
/*      */       } 
/*      */     } 
/*      */     
/*  976 */     return (searchInPos != searchInEnd) ? 1 : 0;
/*      */   }
/*      */   
/*      */   public static int lastIndexOf(byte[] s, char c) {
/*  980 */     if (s == null) {
/*  981 */       return -1;
/*      */     }
/*      */     
/*  984 */     for (int i = s.length - 1; i >= 0; i--) {
/*  985 */       if (s[i] == c) {
/*  986 */         return i;
/*      */       }
/*      */     } 
/*      */     
/*  990 */     return -1;
/*      */   }
/*      */   
/*      */   public static int indexOf(byte[] s, char c) {
/*  994 */     if (s == null) {
/*  995 */       return -1;
/*      */     }
/*      */     
/*  998 */     int length = s.length;
/*      */     
/* 1000 */     for (int i = 0; i < length; i++) {
/* 1001 */       if (s[i] == c) {
/* 1002 */         return i;
/*      */       }
/*      */     } 
/*      */     
/* 1006 */     return -1;
/*      */   }
/*      */   
/*      */   public static boolean isNullOrEmpty(String str) {
/* 1010 */     return (str == null || str.isEmpty());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean nullSafeEqual(String str1, String str2) {
/* 1024 */     return ((str1 == null && str2 == null) || (str1 != null && str1.equals(str2)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stripCommentsAndHints(String source, String openingMarkers, String closingMarkers, boolean allowBackslashEscapes) {
/* 1042 */     StringInspector strInspector = new StringInspector(source, openingMarkers, closingMarkers, "", allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS);
/*      */     
/* 1044 */     return strInspector.stripCommentsAndHints();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String sanitizeProcOrFuncName(String src) {
/* 1058 */     if (src == null || src.equals("%")) {
/* 1059 */       return null;
/*      */     }
/*      */     
/* 1062 */     return src;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<String> splitDBdotName(String source, String db, String quoteId, boolean isNoBslashEscSet) {
/*      */     String entityName;
/* 1081 */     if (source == null || source.equals("%")) {
/* 1082 */       return Collections.emptyList();
/*      */     }
/*      */     
/* 1085 */     int dotIndex = -1;
/* 1086 */     if (" ".equals(quoteId)) {
/* 1087 */       dotIndex = source.indexOf(".");
/*      */     } else {
/* 1089 */       dotIndex = indexOfIgnoreCase(0, source, ".", quoteId, quoteId, isNoBslashEscSet ? SearchMode.__MRK_WS : SearchMode.__BSE_MRK_WS);
/*      */     } 
/*      */     
/* 1092 */     String database = db;
/*      */     
/* 1094 */     if (dotIndex != -1) {
/* 1095 */       database = unQuoteIdentifier(source.substring(0, dotIndex), quoteId);
/* 1096 */       entityName = unQuoteIdentifier(source.substring(dotIndex + 1), quoteId);
/*      */     } else {
/* 1098 */       entityName = unQuoteIdentifier(source, quoteId);
/*      */     } 
/*      */     
/* 1101 */     return Arrays.asList(new String[] { database, entityName });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getFullyQualifiedName(String db, String entity, String quoteId, boolean isPedantic) {
/* 1118 */     StringBuilder fullyQualifiedName = new StringBuilder(quoteIdentifier((db == null) ? "" : db, quoteId, isPedantic));
/* 1119 */     fullyQualifiedName.append('.');
/* 1120 */     fullyQualifiedName.append(quoteIdentifier(entity, quoteId, isPedantic));
/* 1121 */     return fullyQualifiedName.toString();
/*      */   }
/*      */   
/*      */   public static boolean isEmptyOrWhitespaceOnly(String str) {
/* 1125 */     if (str == null || str.length() == 0) {
/* 1126 */       return true;
/*      */     }
/*      */     
/* 1129 */     int length = str.length();
/*      */     
/* 1131 */     for (int i = 0; i < length; i++) {
/* 1132 */       if (!Character.isWhitespace(str.charAt(i))) {
/* 1133 */         return false;
/*      */       }
/*      */     } 
/*      */     
/* 1137 */     return true;
/*      */   }
/*      */   
/*      */   public static String escapeQuote(String str, String quotChar) {
/* 1141 */     if (str == null) {
/* 1142 */       return null;
/*      */     }
/*      */     
/* 1145 */     str = toString(stripEnclosure(str.getBytes(), quotChar, quotChar));
/*      */     
/* 1147 */     int lastNdx = str.indexOf(quotChar);
/*      */ 
/*      */ 
/*      */     
/* 1151 */     String tmpSrc = str.substring(0, lastNdx);
/* 1152 */     tmpSrc = tmpSrc + quotChar + quotChar;
/*      */     
/* 1154 */     String tmpRest = str.substring(lastNdx + 1, str.length());
/*      */     
/* 1156 */     lastNdx = tmpRest.indexOf(quotChar);
/* 1157 */     while (lastNdx > -1) {
/*      */       
/* 1159 */       tmpSrc = tmpSrc + tmpRest.substring(0, lastNdx);
/* 1160 */       tmpSrc = tmpSrc + quotChar + quotChar;
/* 1161 */       tmpRest = tmpRest.substring(lastNdx + 1, tmpRest.length());
/*      */       
/* 1163 */       lastNdx = tmpRest.indexOf(quotChar);
/*      */     } 
/*      */     
/* 1166 */     tmpSrc = tmpSrc + tmpRest;
/* 1167 */     str = tmpSrc;
/*      */     
/* 1169 */     return str;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteIdentifier(String identifier, String quoteChar, boolean isPedantic) {
/* 1203 */     if (identifier == null) {
/* 1204 */       return null;
/*      */     }
/*      */     
/* 1207 */     identifier = identifier.trim();
/*      */     
/* 1209 */     int quoteCharLength = quoteChar.length();
/* 1210 */     if (quoteCharLength == 0) {
/* 1211 */       return identifier;
/*      */     }
/*      */ 
/*      */     
/* 1215 */     if (!isPedantic && identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
/*      */       
/* 1217 */       String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
/*      */ 
/*      */       
/* 1220 */       int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
/* 1221 */       while (quoteCharPos >= 0) {
/* 1222 */         int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
/* 1223 */         int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
/*      */         
/* 1225 */         if (quoteCharNextPosition == quoteCharNextExpectedPos) {
/* 1226 */           quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1233 */       if (quoteCharPos < 0) {
/* 1234 */         return identifier;
/*      */       }
/*      */     } 
/*      */     
/* 1238 */     return quoteChar + identifier.replaceAll(quoteChar, quoteChar + quoteChar) + quoteChar;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String quoteIdentifier(String identifier, boolean isPedantic) {
/* 1261 */     return quoteIdentifier(identifier, "`", isPedantic);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String unQuoteIdentifier(String identifier, String quoteChar) {
/* 1286 */     if (identifier == null) {
/* 1287 */       return null;
/*      */     }
/*      */     
/* 1290 */     identifier = identifier.trim();
/*      */     
/* 1292 */     int quoteCharLength = quoteChar.length();
/* 1293 */     if (quoteCharLength == 0) {
/* 1294 */       return identifier;
/*      */     }
/*      */ 
/*      */     
/* 1298 */     if (identifier.startsWith(quoteChar) && identifier.endsWith(quoteChar)) {
/*      */       
/* 1300 */       String identifierQuoteTrimmed = identifier.substring(quoteCharLength, identifier.length() - quoteCharLength);
/*      */ 
/*      */       
/* 1303 */       int quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar);
/* 1304 */       while (quoteCharPos >= 0) {
/* 1305 */         int quoteCharNextExpectedPos = quoteCharPos + quoteCharLength;
/* 1306 */         int quoteCharNextPosition = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextExpectedPos);
/*      */         
/* 1308 */         if (quoteCharNextPosition == quoteCharNextExpectedPos) {
/* 1309 */           quoteCharPos = identifierQuoteTrimmed.indexOf(quoteChar, quoteCharNextPosition + quoteCharLength);
/*      */           continue;
/*      */         } 
/* 1312 */         return identifier;
/*      */       } 
/*      */ 
/*      */       
/* 1316 */       return identifier.substring(quoteCharLength, identifier.length() - quoteCharLength).replaceAll(quoteChar + quoteChar, quoteChar);
/*      */     } 
/*      */     
/* 1319 */     return identifier;
/*      */   }
/*      */   
/*      */   public static int indexOfQuoteDoubleAware(String searchIn, String quoteChar, int startFrom) {
/* 1323 */     if (searchIn == null || quoteChar == null || quoteChar.length() == 0 || startFrom > searchIn.length()) {
/* 1324 */       return -1;
/*      */     }
/*      */     
/* 1327 */     int lastIndex = searchIn.length() - 1;
/*      */     
/* 1329 */     int beginPos = startFrom;
/* 1330 */     int pos = -1;
/*      */     
/* 1332 */     boolean next = true;
/* 1333 */     while (next) {
/* 1334 */       pos = searchIn.indexOf(quoteChar, beginPos);
/* 1335 */       if (pos == -1 || pos == lastIndex || !searchIn.startsWith(quoteChar, pos + 1)) {
/* 1336 */         next = false; continue;
/*      */       } 
/* 1338 */       beginPos = pos + 2;
/*      */     } 
/*      */ 
/*      */     
/* 1342 */     return pos;
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value, int offset, int length, String encoding) {
/* 1346 */     if (encoding == null || "null".equalsIgnoreCase(encoding)) {
/* 1347 */       return new String(value, offset, length);
/*      */     }
/*      */     try {
/* 1350 */       return new String(value, offset, length, encoding);
/* 1351 */     } catch (UnsupportedEncodingException uee) {
/* 1352 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value, String encoding) {
/* 1357 */     if (encoding == null) {
/* 1358 */       return new String(value);
/*      */     }
/*      */     try {
/* 1361 */       return new String(value, encoding);
/* 1362 */     } catch (UnsupportedEncodingException uee) {
/* 1363 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value, int offset, int length) {
/* 1368 */     return new String(value, offset, length);
/*      */   }
/*      */   
/*      */   public static String toString(byte[] value) {
/* 1372 */     return new String(value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytes(char[] value) {
/* 1383 */     return getBytes(value, 0, value.length);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytes(char[] c, String encoding) {
/* 1396 */     return getBytes(c, 0, c.length, encoding);
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(char[] value, int offset, int length) {
/* 1400 */     return getBytes(value, offset, length, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] getBytes(char[] value, int offset, int length, String encoding) {
/*      */     Charset cs;
/*      */     try {
/* 1419 */       if (encoding == null) {
/* 1420 */         cs = Charset.defaultCharset();
/*      */       } else {
/* 1422 */         cs = Charset.forName(encoding);
/*      */       } 
/* 1424 */     } catch (UnsupportedCharsetException ex) {
/* 1425 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), ex);
/*      */     } 
/* 1427 */     ByteBuffer buf = cs.encode(CharBuffer.wrap(value, offset, length));
/*      */ 
/*      */     
/* 1430 */     int encodedLen = buf.limit();
/* 1431 */     byte[] asBytes = new byte[encodedLen];
/* 1432 */     buf.get(asBytes, 0, encodedLen);
/*      */     
/* 1434 */     return asBytes;
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value) {
/* 1438 */     return value.getBytes();
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value, int offset, int length) {
/* 1442 */     return value.substring(offset, offset + length).getBytes();
/*      */   }
/*      */   
/*      */   public static byte[] getBytes(String value, int offset, int length, String encoding) {
/* 1446 */     if (encoding == null) {
/* 1447 */       return getBytes(value, offset, length);
/*      */     }
/*      */     
/*      */     try {
/* 1451 */       return value.substring(offset, offset + length).getBytes(encoding);
/* 1452 */     } catch (UnsupportedEncodingException uee) {
/* 1453 */       throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("StringUtils.0", new Object[] { encoding }), uee);
/*      */     } 
/*      */   }
/*      */   
/*      */   public static final boolean isValidIdChar(char c) {
/* 1458 */     return ("abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789$_#@".indexOf(c) != -1);
/*      */   }
/*      */   
/* 1461 */   private static final char[] HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*      */   
/*      */   public static void appendAsHex(StringBuilder builder, byte[] bytes) {
/* 1464 */     builder.append("0x");
/* 1465 */     for (byte b : bytes) {
/* 1466 */       builder.append(HEX_DIGITS[b >>> 4 & 0xF]).append(HEX_DIGITS[b & 0xF]);
/*      */     }
/*      */   }
/*      */   
/*      */   public static void appendAsHex(StringBuilder builder, int value) {
/* 1471 */     if (value == 0) {
/* 1472 */       builder.append("0x0");
/*      */       
/*      */       return;
/*      */     } 
/* 1476 */     int shift = 32;
/*      */     
/* 1478 */     boolean nonZeroFound = false;
/*      */     
/* 1480 */     builder.append("0x");
/*      */     do {
/* 1482 */       shift -= 4;
/* 1483 */       byte nibble = (byte)(value >>> shift & 0xF);
/* 1484 */       if (nonZeroFound) {
/* 1485 */         builder.append(HEX_DIGITS[nibble]);
/* 1486 */       } else if (nibble != 0) {
/* 1487 */         builder.append(HEX_DIGITS[nibble]);
/* 1488 */         nonZeroFound = true;
/*      */       } 
/* 1490 */     } while (shift != 0);
/*      */   }
/*      */   
/*      */   public static byte[] getBytesNullTerminated(String value, String encoding) {
/* 1494 */     Charset cs = Charset.forName(encoding);
/* 1495 */     ByteBuffer buf = cs.encode(value);
/* 1496 */     int encodedLen = buf.limit();
/* 1497 */     byte[] asBytes = new byte[encodedLen + 1];
/* 1498 */     buf.get(asBytes, 0, encodedLen);
/* 1499 */     asBytes[encodedLen] = 0;
/*      */     
/* 1501 */     return asBytes;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean canHandleAsServerPreparedStatementNoCache(String sql, ServerVersion serverVersion, boolean allowMultiQueries, boolean noBackslashEscapes, boolean useAnsiQuotes) {
/* 1508 */     if (startsWithIgnoreCaseAndNonAlphaNumeric(sql, "CALL")) {
/* 1509 */       return false;
/*      */     }
/*      */     
/* 1512 */     boolean canHandleAsStatement = true;
/*      */     
/* 1514 */     boolean allowBackslashEscapes = !noBackslashEscapes;
/* 1515 */     String quoteChar = useAnsiQuotes ? "\"" : "'";
/*      */     
/* 1517 */     if (allowMultiQueries) {
/* 1518 */       if (indexOfIgnoreCase(0, sql, ";", quoteChar, quoteChar, allowBackslashEscapes ? SearchMode.__BSE_MRK_COM_MYM_HNT_WS : SearchMode.__MRK_COM_MYM_HNT_WS) != -1)
/*      */       {
/* 1520 */         canHandleAsStatement = false;
/*      */       }
/* 1522 */     } else if (startsWithIgnoreCaseAndWs(sql, "XA ")) {
/* 1523 */       canHandleAsStatement = false;
/* 1524 */     } else if (startsWithIgnoreCaseAndWs(sql, "CREATE TABLE")) {
/* 1525 */       canHandleAsStatement = false;
/* 1526 */     } else if (startsWithIgnoreCaseAndWs(sql, "DO")) {
/* 1527 */       canHandleAsStatement = false;
/* 1528 */     } else if (startsWithIgnoreCaseAndWs(sql, "SET")) {
/* 1529 */       canHandleAsStatement = false;
/* 1530 */     } else if (startsWithIgnoreCaseAndWs(sql, "SHOW WARNINGS") && serverVersion.meetsMinimum(ServerVersion.parseVersion("5.7.2"))) {
/* 1531 */       canHandleAsStatement = false;
/* 1532 */     } else if (sql.startsWith("/* ping */")) {
/* 1533 */       canHandleAsStatement = false;
/*      */     } 
/*      */     
/* 1536 */     return canHandleAsStatement;
/*      */   }
/*      */   
/* 1539 */   static final char[] EMPTY_SPACE = new char[255];
/*      */   static {
/* 1541 */     for (int i = 0; i < EMPTY_SPACE.length; i++) {
/* 1542 */       EMPTY_SPACE[i] = ' ';
/*      */     }
/*      */   }
/*      */   
/*      */   public static String padString(String stringVal, int requiredLength) {
/* 1547 */     int currentLength = stringVal.length();
/* 1548 */     int difference = requiredLength - currentLength;
/*      */     
/* 1550 */     if (difference > 0) {
/* 1551 */       StringBuilder paddedBuf = new StringBuilder(requiredLength);
/* 1552 */       paddedBuf.append(stringVal);
/* 1553 */       paddedBuf.append(EMPTY_SPACE, 0, difference);
/* 1554 */       return paddedBuf.toString();
/*      */     } 
/*      */     
/* 1557 */     return stringVal;
/*      */   }
/*      */   
/*      */   public static int safeIntParse(String intAsString) {
/*      */     try {
/* 1562 */       return Integer.parseInt(intAsString);
/* 1563 */     } catch (NumberFormatException nfe) {
/* 1564 */       return 0;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isStrictlyNumeric(CharSequence cs) {
/* 1577 */     if (cs == null || cs.length() == 0) {
/* 1578 */       return false;
/*      */     }
/* 1580 */     for (int i = 0; i < cs.length(); i++) {
/* 1581 */       if (!Character.isDigit(cs.charAt(i))) {
/* 1582 */         return false;
/*      */       }
/*      */     } 
/* 1585 */     return true;
/*      */   }
/*      */   
/*      */   public static String safeTrim(String toTrim) {
/* 1589 */     return isNullOrEmpty(toTrim) ? toTrim : toTrim.trim();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String stringArrayToString(String[] elems, String prefix, String midDelimiter, String lastDelimiter, String suffix) {
/* 1610 */     StringBuilder valuesString = new StringBuilder();
/* 1611 */     if (elems.length > 1) {
/* 1612 */       valuesString.append(Arrays.<CharSequence>stream((CharSequence[])elems).limit((elems.length - 1)).collect(Collectors.joining(midDelimiter, prefix, lastDelimiter)));
/*      */     } else {
/* 1614 */       valuesString.append(prefix);
/*      */     } 
/* 1616 */     valuesString.append(elems[elems.length - 1]).append(suffix);
/*      */     
/* 1618 */     return valuesString.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasWildcards(String src) {
/* 1629 */     return (indexOfIgnoreCase(0, src, "%") > -1 || indexOfIgnoreCase(0, src, "_") > -1);
/*      */   }
/*      */   
/*      */   public static String getUniqueSavepointId() {
/* 1633 */     String uuid = UUID.randomUUID().toString();
/* 1634 */     return uuid.replaceAll("-", "_");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String joinWithSerialComma(List<?> elements) {
/* 1650 */     if (elements == null || elements.size() == 0) {
/* 1651 */       return "";
/*      */     }
/* 1653 */     if (elements.size() == 1) {
/* 1654 */       return elements.get(0).toString();
/*      */     }
/* 1656 */     if (elements.size() == 2) {
/* 1657 */       return (new StringBuilder()).append(elements.get(0)).append(" and ").append(elements.get(1)).toString();
/*      */     }
/* 1659 */     return (String)elements.subList(0, elements.size() - 1).stream().map(Object::toString).collect(Collectors.joining(", ", "", ", and ")) + elements
/* 1660 */       .get(elements.size() - 1).toString();
/*      */   }
/*      */   
/*      */   public static byte[] unquoteBytes(byte[] bytes) {
/* 1664 */     if (bytes[0] == 39 && bytes[bytes.length - 1] == 39) {
/*      */       
/* 1666 */       byte[] valNoQuotes = new byte[bytes.length - 2];
/* 1667 */       int j = 0;
/* 1668 */       int quoteCnt = 0;
/*      */       
/* 1670 */       for (int i = 1; i < bytes.length - 1; i++) {
/* 1671 */         if (bytes[i] == 39) {
/* 1672 */           quoteCnt++;
/*      */         } else {
/* 1674 */           quoteCnt = 0;
/*      */         } 
/*      */         
/* 1677 */         if (quoteCnt == 2) {
/* 1678 */           quoteCnt = 0;
/*      */         } else {
/* 1680 */           valNoQuotes[j++] = bytes[i];
/*      */         } 
/*      */       } 
/*      */       
/* 1684 */       byte[] res = new byte[j];
/* 1685 */       System.arraycopy(valNoQuotes, 0, res, 0, j);
/*      */       
/* 1687 */       return res;
/*      */     } 
/* 1689 */     return bytes;
/*      */   }
/*      */   
/*      */   public static byte[] quoteBytes(byte[] bytes) {
/* 1693 */     byte[] withQuotes = new byte[bytes.length * 2 + 2];
/* 1694 */     int j = 0;
/* 1695 */     withQuotes[j++] = 39;
/* 1696 */     for (int i = 0; i < bytes.length; i++) {
/* 1697 */       if (bytes[i] == 39) {
/* 1698 */         withQuotes[j++] = 39;
/*      */       }
/* 1700 */       withQuotes[j++] = bytes[i];
/*      */     } 
/* 1702 */     withQuotes[j++] = 39;
/*      */     
/* 1704 */     byte[] res = new byte[j];
/* 1705 */     System.arraycopy(withQuotes, 0, res, 0, j);
/* 1706 */     return res;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\c\\util\StringUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */