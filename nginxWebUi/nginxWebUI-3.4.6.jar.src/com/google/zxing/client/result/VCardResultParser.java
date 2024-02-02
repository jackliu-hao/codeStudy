/*     */ package com.google.zxing.client.result;
/*     */ 
/*     */ import com.google.zxing.Result;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class VCardResultParser
/*     */   extends ResultParser
/*     */ {
/*  38 */   private static final Pattern BEGIN_VCARD = Pattern.compile("BEGIN:VCARD", 2);
/*  39 */   private static final Pattern VCARD_LIKE_DATE = Pattern.compile("\\d{4}-?\\d{2}-?\\d{2}");
/*  40 */   private static final Pattern CR_LF_SPACE_TAB = Pattern.compile("\r\n[ \t]");
/*  41 */   private static final Pattern NEWLINE_ESCAPE = Pattern.compile("\\\\[nN]");
/*  42 */   private static final Pattern VCARD_ESCAPES = Pattern.compile("\\\\([,;\\\\])");
/*  43 */   private static final Pattern EQUALS = Pattern.compile("=");
/*  44 */   private static final Pattern SEMICOLON = Pattern.compile(";");
/*  45 */   private static final Pattern UNESCAPED_SEMICOLONS = Pattern.compile("(?<!\\\\);+");
/*  46 */   private static final Pattern COMMA = Pattern.compile(",");
/*  47 */   private static final Pattern SEMICOLON_OR_COMMA = Pattern.compile("[;,]");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AddressBookParsedResult parse(Result result) {
/*  54 */     String rawText = getMassagedText(result);
/*     */     Matcher m;
/*  56 */     if (!(m = BEGIN_VCARD.matcher(rawText)).find() || m.start() != 0) {
/*  57 */       return null;
/*     */     }
/*     */     List<List<String>> names;
/*  60 */     if ((names = matchVCardPrefixedField("FN", rawText, true, false)) == null)
/*     */     {
/*     */       
/*  63 */       formatNames(names = matchVCardPrefixedField("N", rawText, true, false));
/*     */     }
/*     */     List<String> nicknameString;
/*  66 */     String[] nicknames = ((nicknameString = matchSingleVCardPrefixedField("NICKNAME", rawText, true, false)) == null) ? null : COMMA.split(nicknameString.get(0));
/*  67 */     List<List<String>> phoneNumbers = matchVCardPrefixedField("TEL", rawText, true, false);
/*  68 */     List<List<String>> emails = matchVCardPrefixedField("EMAIL", rawText, true, false);
/*  69 */     List<String> note = matchSingleVCardPrefixedField("NOTE", rawText, false, false);
/*  70 */     List<List<String>> addresses = matchVCardPrefixedField("ADR", rawText, true, true);
/*  71 */     List<String> org = matchSingleVCardPrefixedField("ORG", rawText, true, true);
/*     */     List<String> birthday;
/*  73 */     if ((birthday = matchSingleVCardPrefixedField("BDAY", rawText, true, false)) != null && !isLikeVCardDate(birthday.get(0))) {
/*  74 */       birthday = null;
/*     */     }
/*  76 */     List<String> title = matchSingleVCardPrefixedField("TITLE", rawText, true, false);
/*  77 */     List<List<String>> urls = matchVCardPrefixedField("URL", rawText, true, false);
/*  78 */     List<String> instantMessenger = matchSingleVCardPrefixedField("IMPP", rawText, true, false);
/*     */     List<String> geoString;
/*     */     String[] geo;
/*  81 */     if ((geo = (String[])(((geoString = matchSingleVCardPrefixedField("GEO", rawText, true, false)) == null) ? null : SEMICOLON_OR_COMMA.split(geoString.get(0)))) != null && geo.length != 2) {
/*  82 */       geo = null;
/*     */     }
/*  84 */     return new AddressBookParsedResult(toPrimaryValues(names), nicknames, null, 
/*     */ 
/*     */         
/*  87 */         toPrimaryValues(phoneNumbers), 
/*  88 */         toTypes(phoneNumbers), 
/*  89 */         toPrimaryValues(emails), 
/*  90 */         toTypes(emails), 
/*  91 */         toPrimaryValue(instantMessenger), 
/*  92 */         toPrimaryValue(note), 
/*  93 */         toPrimaryValues(addresses), 
/*  94 */         toTypes(addresses), 
/*  95 */         toPrimaryValue(org), 
/*  96 */         toPrimaryValue(birthday), 
/*  97 */         toPrimaryValue(title), 
/*  98 */         toPrimaryValues(urls), geo);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List<List<String>> matchVCardPrefixedField(CharSequence prefix, String rawText, boolean trim, boolean parseFieldDivider) {
/* 106 */     List<List<String>> matches = null;
/* 107 */     int i = 0;
/* 108 */     int max = rawText.length();
/*     */     
/* 110 */     while (i < max) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 115 */       Matcher matcher = Pattern.compile("(?:^|\n)" + prefix + "(?:;([^:]*))?:", 2).matcher(rawText);
/* 116 */       if (i > 0) {
/* 117 */         i--;
/*     */       }
/* 119 */       if (matcher.find(i)) {
/*     */ 
/*     */         
/* 122 */         i = matcher.end(0);
/*     */         
/* 124 */         String metadataString = matcher.group(1);
/* 125 */         List<String> metadata = null;
/* 126 */         boolean quotedPrintable = false;
/* 127 */         String quotedPrintableCharset = null;
/* 128 */         if (metadataString != null) {
/* 129 */           String[] arrayOfString; int j; byte b; for (j = (arrayOfString = SEMICOLON.split(metadataString)).length, b = 0; b < j; ) { String metadatum = arrayOfString[b];
/* 130 */             if (metadata == null) {
/* 131 */               metadata = new ArrayList<>(1);
/*     */             }
/* 133 */             metadata.add(metadatum);
/*     */             String[] metadatumTokens;
/* 135 */             if ((metadatumTokens = EQUALS.split(metadatum, 2)).length > 1) {
/* 136 */               String key = metadatumTokens[0];
/* 137 */               String value = metadatumTokens[1];
/* 138 */               if ("ENCODING".equalsIgnoreCase(key) && "QUOTED-PRINTABLE".equalsIgnoreCase(value)) {
/* 139 */                 quotedPrintable = true;
/* 140 */               } else if ("CHARSET".equalsIgnoreCase(key)) {
/* 141 */                 quotedPrintableCharset = value;
/*     */               } 
/*     */             } 
/*     */             b++; }
/*     */         
/*     */         } 
/* 147 */         int matchStart = i;
/*     */         
/* 149 */         while ((i = rawText.indexOf('\n', i)) >= 0) {
/* 150 */           if (i < rawText.length() - 1 && (rawText
/* 151 */             .charAt(i + 1) == ' ' || rawText
/* 152 */             .charAt(i + 1) == '\t')) {
/* 153 */             i += 2; continue;
/* 154 */           }  if (quotedPrintable && ((i > 0 && rawText
/* 155 */             .charAt(i - 1) == '=') || (i >= 2 && rawText
/* 156 */             .charAt(i - 2) == '='))) {
/* 157 */             i++;
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 163 */         if (i < 0) {
/*     */           
/* 165 */           i = max; continue;
/* 166 */         }  if (i > matchStart) {
/*     */           
/* 168 */           if (matches == null) {
/* 169 */             matches = new ArrayList<>(1);
/*     */           }
/* 171 */           if (i > 0 && rawText.charAt(i - 1) == '\r') {
/* 172 */             i--;
/*     */           }
/* 174 */           String element = rawText.substring(matchStart, i);
/* 175 */           if (trim) {
/* 176 */             element = element.trim();
/*     */           }
/* 178 */           if (quotedPrintable) {
/* 179 */             element = decodeQuotedPrintable(element, quotedPrintableCharset);
/* 180 */             if (parseFieldDivider) {
/* 181 */               element = UNESCAPED_SEMICOLONS.matcher(element).replaceAll("\n").trim();
/*     */             }
/*     */           } else {
/* 184 */             if (parseFieldDivider) {
/* 185 */               element = UNESCAPED_SEMICOLONS.matcher(element).replaceAll("\n").trim();
/*     */             }
/* 187 */             element = CR_LF_SPACE_TAB.matcher(element).replaceAll("");
/* 188 */             element = NEWLINE_ESCAPE.matcher(element).replaceAll("\n");
/* 189 */             element = VCARD_ESCAPES.matcher(element).replaceAll("$1");
/*     */           } 
/* 191 */           if (metadata == null) {
/*     */             List<String> match;
/* 193 */             (match = new ArrayList<>(1)).add(element);
/* 194 */             matches.add(match);
/*     */           } else {
/* 196 */             metadata.add(0, element);
/* 197 */             matches.add(metadata);
/*     */           } 
/* 199 */           i++; continue;
/*     */         } 
/* 201 */         i++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 206 */     return matches;
/*     */   }
/*     */   
/*     */   private static String decodeQuotedPrintable(CharSequence value, String charset) {
/* 210 */     int length = value.length();
/* 211 */     StringBuilder result = new StringBuilder(length);
/* 212 */     ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();
/* 213 */     for (int i = 0; i < length; i++) {
/*     */       char nextChar; char c;
/* 215 */       switch (c = value.charAt(i)) {
/*     */         case '\n':
/*     */         case '\r':
/*     */           break;
/*     */         case '=':
/* 220 */           if (i < length - 2 && (
/*     */             
/* 222 */             nextChar = value.charAt(i + 1)) != '\r' && nextChar != '\n') {
/* 223 */             char nextNextChar = value.charAt(i + 2);
/* 224 */             int firstDigit = parseHexDigit(nextChar);
/* 225 */             int secondDigit = parseHexDigit(nextNextChar);
/* 226 */             if (firstDigit >= 0 && secondDigit >= 0) {
/* 227 */               fragmentBuffer.write((firstDigit << 4) + secondDigit);
/*     */             }
/* 229 */             i += 2;
/*     */           } 
/*     */           break;
/*     */         
/*     */         default:
/* 234 */           maybeAppendFragment(fragmentBuffer, charset, result);
/* 235 */           result.append(c); break;
/*     */       } 
/*     */     } 
/* 238 */     maybeAppendFragment(fragmentBuffer, charset, result);
/* 239 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static void maybeAppendFragment(ByteArrayOutputStream fragmentBuffer, String charset, StringBuilder result) {
/* 245 */     if (fragmentBuffer.size() > 0) {
/* 246 */       String fragment; byte[] fragmentBytes = fragmentBuffer.toByteArray();
/*     */       
/* 248 */       if (charset == null) {
/* 249 */         fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
/*     */       } else {
/*     */         try {
/* 252 */           fragment = new String(fragmentBytes, charset);
/* 253 */         } catch (UnsupportedEncodingException unsupportedEncodingException) {
/* 254 */           fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
/*     */         } 
/*     */       } 
/* 257 */       fragmentBuffer.reset();
/* 258 */       result.append(fragment);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static List<String> matchSingleVCardPrefixedField(CharSequence prefix, String rawText, boolean trim, boolean parseFieldDivider) {
/*     */     List<List<String>> values;
/* 267 */     if ((values = matchVCardPrefixedField(prefix, rawText, trim, parseFieldDivider)) == null || values.isEmpty()) return null;  return values.get(0);
/*     */   }
/*     */   
/*     */   private static String toPrimaryValue(List<String> list) {
/* 271 */     return (list == null || list.isEmpty()) ? null : list.get(0);
/*     */   }
/*     */   
/*     */   private static String[] toPrimaryValues(Collection<List<String>> lists) {
/* 275 */     if (lists == null || lists.isEmpty()) {
/* 276 */       return null;
/*     */     }
/* 278 */     List<String> result = new ArrayList<>(lists.size());
/* 279 */     for (Iterator<List<String>> iterator = lists.iterator(); iterator.hasNext();) {
/*     */       
/* 281 */       if ((value = ((List<String>)iterator.next()).get(0)) != null && !value.isEmpty()) {
/* 282 */         result.add(value);
/*     */       }
/*     */     } 
/* 285 */     return result.<String>toArray(new String[lists.size()]);
/*     */   }
/*     */   
/*     */   private static String[] toTypes(Collection<List<String>> lists) {
/* 289 */     if (lists == null || lists.isEmpty()) {
/* 290 */       return null;
/*     */     }
/* 292 */     List<String> result = new ArrayList<>(lists.size());
/* 293 */     for (List<String> list : lists) {
/* 294 */       String type = null;
/* 295 */       for (int i = 1; i < list.size(); i++) {
/*     */         String metadatum;
/*     */         int equals;
/* 298 */         if ((equals = (metadatum = list.get(i)).indexOf('=')) < 0) {
/*     */           
/* 300 */           type = metadatum;
/*     */           break;
/*     */         } 
/* 303 */         if ("TYPE".equalsIgnoreCase(metadatum.substring(0, equals))) {
/* 304 */           type = metadatum.substring(equals + 1);
/*     */           break;
/*     */         } 
/*     */       } 
/* 308 */       result.add(type);
/*     */     } 
/* 310 */     return result.<String>toArray(new String[lists.size()]);
/*     */   }
/*     */   
/*     */   private static boolean isLikeVCardDate(CharSequence value) {
/* 314 */     return (value == null || VCARD_LIKE_DATE.matcher(value).matches());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void formatNames(Iterable<List<String>> names) {
/* 324 */     if (names != null) {
/* 325 */       for (Iterator<List<String>> iterator = names.iterator(); iterator.hasNext(); ) {
/* 326 */         List<String> list; String name = (list = iterator.next()).get(0);
/* 327 */         String[] components = new String[5];
/* 328 */         int start = 0;
/*     */         
/* 330 */         int componentIndex = 0; int end;
/* 331 */         while (componentIndex < 4 && (end = name.indexOf(';', start)) >= 0) {
/* 332 */           components[componentIndex] = name.substring(start, end);
/* 333 */           componentIndex++;
/* 334 */           start = end + 1;
/*     */         } 
/* 336 */         components[componentIndex] = name.substring(start);
/* 337 */         StringBuilder newName = new StringBuilder(100);
/* 338 */         maybeAppendComponent(components, 3, newName);
/* 339 */         maybeAppendComponent(components, 1, newName);
/* 340 */         maybeAppendComponent(components, 2, newName);
/* 341 */         maybeAppendComponent(components, 0, newName);
/* 342 */         maybeAppendComponent(components, 4, newName);
/* 343 */         list.set(0, newName.toString().trim());
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static void maybeAppendComponent(String[] components, int i, StringBuilder newName) {
/* 349 */     if (components[i] != null && !components[i].isEmpty()) {
/* 350 */       if (newName.length() > 0) {
/* 351 */         newName.append(' ');
/*     */       }
/* 353 */       newName.append(components[i]);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\VCardResultParser.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */