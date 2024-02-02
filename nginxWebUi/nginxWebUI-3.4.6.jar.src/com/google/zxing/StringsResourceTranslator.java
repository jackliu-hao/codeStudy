/*     */ package com.google.zxing;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Writer;
/*     */ import java.net.URI;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StringsResourceTranslator
/*     */ {
/*  55 */   private static final String API_KEY = System.getProperty("translateAPI.key");
/*     */   static {
/*  57 */     if (API_KEY == null) {
/*  58 */       throw new IllegalArgumentException("translateAPI.key is not specified");
/*     */     }
/*     */   }
/*     */   
/*  62 */   private static final Pattern ENTRY_PATTERN = Pattern.compile("<string name=\"([^\"]+)\".*>([^<]+)</string>");
/*  63 */   private static final Pattern STRINGS_FILE_NAME_PATTERN = Pattern.compile("values-(.+)");
/*  64 */   private static final Pattern TRANSLATE_RESPONSE_PATTERN = Pattern.compile("translatedText\":\\s*\"([^\"]+)\"");
/*  65 */   private static final Pattern VALUES_DIR_PATTERN = Pattern.compile("values-[a-z]{2}(-[a-zA-Z]{2,3})?");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String APACHE_2_LICENSE = "<!--\n Copyright (C) 2015 ZXing authors\n\n Licensed under the Apache License, Version 2.0 (the \"License\");\n you may not use this file except in compliance with the License.\n You may obtain a copy of the License at\n\n      http://www.apache.org/licenses/LICENSE-2.0\n\n Unless required by applicable law or agreed to in writing, software\n distributed under the License is distributed on an \"AS IS\" BASIS,\n WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n See the License for the specific language governing permissions and\n limitations under the License.\n -->\n";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private static final Map<String, String> LANGUAGE_CODE_MASSAGINGS = new HashMap<>(3);
/*     */   static {
/*  86 */     LANGUAGE_CODE_MASSAGINGS.put("zh-rCN", "zh-cn");
/*  87 */     LANGUAGE_CODE_MASSAGINGS.put("zh-rTW", "zh-tw");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/*  93 */     Path resDir = Paths.get(args[0], new String[0]);
/*  94 */     Path valueDir = resDir.resolve("values");
/*  95 */     Path stringsFile = valueDir.resolve("strings.xml");
/*  96 */     Collection<String> forceRetranslation = Arrays.<String>asList(args).subList(1, args.length);
/*     */     
/*  98 */     DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>()
/*     */       {
/*     */         public boolean accept(Path entry) {
/* 101 */           return (Files.isDirectory(entry, new java.nio.file.LinkOption[0]) && !Files.isSymbolicLink(entry) && StringsResourceTranslator
/* 102 */             .VALUES_DIR_PATTERN.matcher(entry.getFileName().toString()).matches());
/*     */         }
/*     */       };
/* 105 */     try (DirectoryStream<Path> dirs = Files.newDirectoryStream(resDir, filter)) {
/* 106 */       for (Path dir : dirs) {
/* 107 */         translate(stringsFile, dir.resolve("strings.xml"), forceRetranslation);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void translate(Path englishFile, Path translatedFile, Collection<String> forceRetranslation) throws IOException {
/* 116 */     Map<String, String> english = readLines(englishFile);
/* 117 */     Map<String, String> translated = readLines(translatedFile);
/* 118 */     String parentName = translatedFile.getParent().getFileName().toString();
/*     */     
/* 120 */     Matcher stringsFileNameMatcher = STRINGS_FILE_NAME_PATTERN.matcher(parentName);
/* 121 */     if (!stringsFileNameMatcher.find()) {
/* 122 */       throw new IllegalArgumentException("Invalid parent dir: " + parentName);
/*     */     }
/* 124 */     String language = stringsFileNameMatcher.group(1);
/* 125 */     String massagedLanguage = LANGUAGE_CODE_MASSAGINGS.get(language);
/* 126 */     if (massagedLanguage != null) {
/* 127 */       language = massagedLanguage;
/*     */     }
/*     */     
/* 130 */     System.out.println("Translating " + language);
/*     */     
/* 132 */     Path resultTempFile = Files.createTempFile(null, null, (FileAttribute<?>[])new FileAttribute[0]);
/*     */     
/* 134 */     boolean anyChange = false;
/* 135 */     try (Writer out = Files.newBufferedWriter(resultTempFile, StandardCharsets.UTF_8, new java.nio.file.OpenOption[0])) {
/* 136 */       out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
/* 137 */       out.write("<!--\n Copyright (C) 2015 ZXing authors\n\n Licensed under the Apache License, Version 2.0 (the \"License\");\n you may not use this file except in compliance with the License.\n You may obtain a copy of the License at\n\n      http://www.apache.org/licenses/LICENSE-2.0\n\n Unless required by applicable law or agreed to in writing, software\n distributed under the License is distributed on an \"AS IS\" BASIS,\n WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n See the License for the specific language governing permissions and\n limitations under the License.\n -->\n");
/* 138 */       out.write("<resources>\n");
/*     */       
/* 140 */       for (Map.Entry<String, String> englishEntry : english.entrySet()) {
/* 141 */         String key = englishEntry.getKey();
/* 142 */         String value = englishEntry.getValue();
/* 143 */         out.write("  <string name=\"");
/* 144 */         out.write(key);
/* 145 */         out.write(34);
/* 146 */         if (value.contains("%s") || value.contains("%f"))
/*     */         {
/* 148 */           out.write(" formatted=\"false\"");
/*     */         }
/* 150 */         out.write(62);
/*     */         
/* 152 */         String translatedString = translated.get(key);
/* 153 */         if (translatedString == null || forceRetranslation.contains(key)) {
/* 154 */           anyChange = true;
/* 155 */           translatedString = translateString(value, language);
/*     */           
/* 157 */           translatedString = translatedString.replaceAll("'", "\\\\'");
/*     */         } 
/* 159 */         out.write(translatedString);
/*     */         
/* 161 */         out.write("</string>\n");
/*     */       } 
/*     */       
/* 164 */       out.write("</resources>\n");
/* 165 */       out.flush();
/*     */     } 
/*     */     
/* 168 */     if (anyChange) {
/* 169 */       System.out.println("  Writing translations");
/* 170 */       Files.move(resultTempFile, translatedFile, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */     } else {
/* 172 */       Files.delete(resultTempFile);
/*     */     } 
/*     */   }
/*     */   
/*     */   static String translateString(String english, String language) throws IOException {
/* 177 */     if ("en".equals(language)) {
/* 178 */       return english;
/*     */     }
/* 180 */     String massagedLanguage = LANGUAGE_CODE_MASSAGINGS.get(language);
/* 181 */     if (massagedLanguage != null) {
/* 182 */       language = massagedLanguage;
/*     */     }
/* 184 */     System.out.println("  Need translation for " + english);
/*     */     
/* 186 */     URI translateURI = URI.create("https://www.googleapis.com/language/translate/v2?key=" + API_KEY + "&q=" + 
/*     */         
/* 188 */         URLEncoder.encode(english, "UTF-8") + "&source=en&target=" + language);
/*     */     
/* 190 */     CharSequence translateResult = fetch(translateURI);
/* 191 */     Matcher m = TRANSLATE_RESPONSE_PATTERN.matcher(translateResult);
/* 192 */     if (!m.find()) {
/* 193 */       System.err.println("No translate result");
/* 194 */       System.err.println(translateResult);
/* 195 */       return english;
/*     */     } 
/* 197 */     String translation = m.group(1);
/*     */ 
/*     */     
/* 200 */     translation = translation.replaceAll("&(amp;)?quot;", "\"");
/* 201 */     translation = translation.replaceAll("&(amp;)?#39;", "'");
/*     */     
/* 203 */     System.out.println("  Got translation " + translation);
/* 204 */     return translation;
/*     */   }
/*     */   
/*     */   private static CharSequence fetch(URI translateURI) throws IOException {
/* 208 */     URLConnection connection = translateURI.toURL().openConnection();
/* 209 */     connection.connect();
/* 210 */     StringBuilder translateResult = new StringBuilder(200);
/* 211 */     try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
/* 212 */       char[] buffer = new char[8192];
/*     */       int charsRead;
/* 214 */       while ((charsRead = in.read(buffer)) > 0) {
/* 215 */         translateResult.append(buffer, 0, charsRead);
/*     */       }
/*     */     } 
/* 218 */     return translateResult;
/*     */   }
/*     */   
/*     */   private static Map<String, String> readLines(Path file) throws IOException {
/* 222 */     if (Files.exists(file, new java.nio.file.LinkOption[0])) {
/* 223 */       Map<String, String> entries = new TreeMap<>();
/* 224 */       for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
/* 225 */         Matcher m = ENTRY_PATTERN.matcher(line);
/* 226 */         if (m.find()) {
/* 227 */           entries.put(m.group(1), m.group(2));
/*     */         }
/*     */       } 
/* 230 */       return entries;
/*     */     } 
/* 232 */     return Collections.emptyMap();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\StringsResourceTranslator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */