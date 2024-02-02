/*     */ package com.sun.activation.registries;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class MailcapFile
/*     */ {
/*  41 */   private Map type_hash = new HashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   private Map fallback_hash = new HashMap();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private Map native_commands = new HashMap();
/*     */   
/*     */   private static boolean addReverse = false;
/*     */   
/*     */   static {
/*     */     try {
/*  58 */       addReverse = Boolean.getBoolean("javax.activation.addreverse");
/*  59 */     } catch (Throwable t) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailcapFile(String new_fname) throws IOException {
/*  70 */     if (LogSupport.isLoggable())
/*  71 */       LogSupport.log("new MailcapFile: file " + new_fname); 
/*  72 */     FileReader reader = null;
/*     */     try {
/*  74 */       reader = new FileReader(new_fname);
/*  75 */       parse(new BufferedReader(reader));
/*     */     } finally {
/*  77 */       if (reader != null) {
/*     */         try {
/*  79 */           reader.close();
/*  80 */         } catch (IOException ex) {}
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailcapFile(InputStream is) throws IOException {
/*  91 */     if (LogSupport.isLoggable())
/*  92 */       LogSupport.log("new MailcapFile: InputStream"); 
/*  93 */     parse(new BufferedReader(new InputStreamReader(is, "iso-8859-1")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MailcapFile() {
/* 100 */     if (LogSupport.isLoggable()) {
/* 101 */       LogSupport.log("new MailcapFile: default");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map getMailcapList(String mime_type) {
/* 113 */     Map search_result = null;
/* 114 */     Map wildcard_result = null;
/*     */ 
/*     */     
/* 117 */     search_result = (Map)this.type_hash.get(mime_type);
/*     */ 
/*     */     
/* 120 */     int separator = mime_type.indexOf('/');
/* 121 */     String subtype = mime_type.substring(separator + 1);
/* 122 */     if (!subtype.equals("*")) {
/* 123 */       String type = mime_type.substring(0, separator + 1) + "*";
/* 124 */       wildcard_result = (Map)this.type_hash.get(type);
/*     */       
/* 126 */       if (wildcard_result != null)
/* 127 */         if (search_result != null) {
/* 128 */           search_result = mergeResults(search_result, wildcard_result);
/*     */         } else {
/*     */           
/* 131 */           search_result = wildcard_result;
/*     */         }  
/*     */     } 
/* 134 */     return search_result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map getMailcapFallbackList(String mime_type) {
/* 146 */     Map search_result = null;
/* 147 */     Map wildcard_result = null;
/*     */ 
/*     */     
/* 150 */     search_result = (Map)this.fallback_hash.get(mime_type);
/*     */ 
/*     */     
/* 153 */     int separator = mime_type.indexOf('/');
/* 154 */     String subtype = mime_type.substring(separator + 1);
/* 155 */     if (!subtype.equals("*")) {
/* 156 */       String type = mime_type.substring(0, separator + 1) + "*";
/* 157 */       wildcard_result = (Map)this.fallback_hash.get(type);
/*     */       
/* 159 */       if (wildcard_result != null)
/* 160 */         if (search_result != null) {
/* 161 */           search_result = mergeResults(search_result, wildcard_result);
/*     */         } else {
/*     */           
/* 164 */           search_result = wildcard_result;
/*     */         }  
/*     */     } 
/* 167 */     return search_result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getMimeTypes() {
/* 174 */     Set types = new HashSet(this.type_hash.keySet());
/* 175 */     types.addAll(this.fallback_hash.keySet());
/* 176 */     types.addAll(this.native_commands.keySet());
/* 177 */     String[] mts = new String[types.size()];
/* 178 */     mts = (String[])types.toArray((Object[])mts);
/* 179 */     return mts;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getNativeCommands(String mime_type) {
/* 186 */     String[] cmds = null;
/* 187 */     List v = (List)this.native_commands.get(mime_type.toLowerCase());
/* 188 */     if (v != null) {
/* 189 */       cmds = new String[v.size()];
/* 190 */       cmds = (String[])v.toArray((Object[])cmds);
/*     */     } 
/* 192 */     return cmds;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map mergeResults(Map first, Map second) {
/* 202 */     Iterator verb_enum = second.keySet().iterator();
/* 203 */     Map clonedHash = new HashMap(first);
/*     */ 
/*     */     
/* 206 */     while (verb_enum.hasNext()) {
/* 207 */       String verb = verb_enum.next();
/* 208 */       List cmdVector = (List)clonedHash.get(verb);
/* 209 */       if (cmdVector == null) {
/* 210 */         clonedHash.put(verb, second.get(verb));
/*     */         continue;
/*     */       } 
/* 213 */       List oldV = (List)second.get(verb);
/* 214 */       cmdVector = new ArrayList(cmdVector);
/* 215 */       cmdVector.addAll(oldV);
/* 216 */       clonedHash.put(verb, cmdVector);
/*     */     } 
/*     */     
/* 219 */     return clonedHash;
/*     */   }
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
/*     */   public void appendToMailcap(String mail_cap) {
/* 233 */     if (LogSupport.isLoggable())
/* 234 */       LogSupport.log("appendToMailcap: " + mail_cap); 
/*     */     try {
/* 236 */       parse(new StringReader(mail_cap));
/* 237 */     } catch (IOException ex) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parse(Reader reader) throws IOException {
/* 246 */     BufferedReader buf_reader = new BufferedReader(reader);
/* 247 */     String line = null;
/* 248 */     String continued = null;
/*     */     
/* 250 */     while ((line = buf_reader.readLine()) != null) {
/*     */ 
/*     */       
/* 253 */       line = line.trim();
/*     */       
/*     */       try {
/* 256 */         if (line.charAt(0) == '#')
/*     */           continue; 
/* 258 */         if (line.charAt(line.length() - 1) == '\\') {
/* 259 */           if (continued != null) {
/* 260 */             continued = continued + line.substring(0, line.length() - 1); continue;
/*     */           } 
/* 262 */           continued = line.substring(0, line.length() - 1); continue;
/* 263 */         }  if (continued != null) {
/*     */           
/* 265 */           continued = continued + line;
/*     */           
/*     */           try {
/* 268 */             parseLine(continued);
/* 269 */           } catch (MailcapParseException e) {}
/*     */ 
/*     */           
/* 272 */           continued = null;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         try {
/* 277 */           parseLine(line);
/*     */         }
/* 279 */         catch (MailcapParseException e) {}
/*     */ 
/*     */       
/*     */       }
/* 283 */       catch (StringIndexOutOfBoundsException e) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void parseLine(String mailcapEntry) throws MailcapParseException, IOException {
/* 295 */     MailcapTokenizer tokenizer = new MailcapTokenizer(mailcapEntry);
/* 296 */     tokenizer.setIsAutoquoting(false);
/*     */     
/* 298 */     if (LogSupport.isLoggable()) {
/* 299 */       LogSupport.log("parse: " + mailcapEntry);
/*     */     }
/* 301 */     int currentToken = tokenizer.nextToken();
/* 302 */     if (currentToken != 2) {
/* 303 */       reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
/*     */     }
/*     */     
/* 306 */     String primaryType = tokenizer.getCurrentTokenValue().toLowerCase();
/* 307 */     String subType = "*";
/*     */ 
/*     */ 
/*     */     
/* 311 */     currentToken = tokenizer.nextToken();
/* 312 */     if (currentToken != 47 && currentToken != 59)
/*     */     {
/* 314 */       reportParseError(47, 59, currentToken, tokenizer.getCurrentTokenValue());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 320 */     if (currentToken == 47) {
/*     */       
/* 322 */       currentToken = tokenizer.nextToken();
/* 323 */       if (currentToken != 2) {
/* 324 */         reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
/*     */       }
/*     */       
/* 327 */       subType = tokenizer.getCurrentTokenValue().toLowerCase();
/*     */ 
/*     */       
/* 330 */       currentToken = tokenizer.nextToken();
/*     */     } 
/*     */     
/* 333 */     String mimeType = primaryType + "/" + subType;
/*     */     
/* 335 */     if (LogSupport.isLoggable()) {
/* 336 */       LogSupport.log("  Type: " + mimeType);
/*     */     }
/*     */     
/* 339 */     Map commands = new LinkedHashMap();
/*     */ 
/*     */     
/* 342 */     if (currentToken != 59) {
/* 343 */       reportParseError(59, currentToken, tokenizer.getCurrentTokenValue());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 349 */     tokenizer.setIsAutoquoting(true);
/* 350 */     currentToken = tokenizer.nextToken();
/* 351 */     tokenizer.setIsAutoquoting(false);
/* 352 */     if (currentToken != 2 && currentToken != 59)
/*     */     {
/* 354 */       reportParseError(2, 59, currentToken, tokenizer.getCurrentTokenValue());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 359 */     if (currentToken == 2) {
/*     */ 
/*     */       
/* 362 */       List v = (List)this.native_commands.get(mimeType);
/* 363 */       if (v == null) {
/* 364 */         v = new ArrayList();
/* 365 */         v.add(mailcapEntry);
/* 366 */         this.native_commands.put(mimeType, v);
/*     */       } else {
/*     */         
/* 369 */         v.add(mailcapEntry);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 374 */     if (currentToken != 59) {
/* 375 */       currentToken = tokenizer.nextToken();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 380 */     if (currentToken == 59) {
/* 381 */       boolean isFallback = false;
/*     */ 
/*     */ 
/*     */       
/*     */       do {
/* 386 */         currentToken = tokenizer.nextToken();
/* 387 */         if (currentToken != 2) {
/* 388 */           reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
/*     */         }
/*     */         
/* 391 */         String paramName = tokenizer.getCurrentTokenValue().toLowerCase();
/*     */ 
/*     */ 
/*     */         
/* 395 */         currentToken = tokenizer.nextToken();
/* 396 */         if (currentToken != 61 && currentToken != 59 && currentToken != 5)
/*     */         {
/*     */           
/* 399 */           reportParseError(61, 59, 5, currentToken, tokenizer.getCurrentTokenValue());
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 406 */         if (currentToken != 61) {
/*     */           continue;
/*     */         }
/*     */         
/* 410 */         tokenizer.setIsAutoquoting(true);
/* 411 */         currentToken = tokenizer.nextToken();
/* 412 */         tokenizer.setIsAutoquoting(false);
/* 413 */         if (currentToken != 2) {
/* 414 */           reportParseError(2, currentToken, tokenizer.getCurrentTokenValue());
/*     */         }
/*     */         
/* 417 */         String paramValue = tokenizer.getCurrentTokenValue();
/*     */ 
/*     */ 
/*     */         
/* 421 */         if (paramName.startsWith("x-java-")) {
/* 422 */           String commandName = paramName.substring(7);
/*     */ 
/*     */           
/* 425 */           if (commandName.equals("fallback-entry") && paramValue.equalsIgnoreCase("true")) {
/*     */             
/* 427 */             isFallback = true;
/*     */           }
/*     */           else {
/*     */             
/* 431 */             if (LogSupport.isLoggable()) {
/* 432 */               LogSupport.log("    Command: " + commandName + ", Class: " + paramValue);
/*     */             }
/* 434 */             List classes = (List)commands.get(commandName);
/* 435 */             if (classes == null) {
/* 436 */               classes = new ArrayList();
/* 437 */               commands.put(commandName, classes);
/*     */             } 
/* 439 */             if (addReverse) {
/* 440 */               classes.add(0, paramValue);
/*     */             } else {
/* 442 */               classes.add(paramValue);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/* 447 */         currentToken = tokenizer.nextToken();
/*     */       }
/* 449 */       while (currentToken == 59);
/*     */       
/* 451 */       Map masterHash = isFallback ? this.fallback_hash : this.type_hash;
/* 452 */       Map curcommands = (Map)masterHash.get(mimeType);
/*     */       
/* 454 */       if (curcommands == null) {
/* 455 */         masterHash.put(mimeType, commands);
/*     */       } else {
/* 457 */         if (LogSupport.isLoggable()) {
/* 458 */           LogSupport.log("Merging commands for type " + mimeType);
/*     */         }
/*     */         
/* 461 */         Iterator cn = curcommands.keySet().iterator();
/* 462 */         while (cn.hasNext()) {
/* 463 */           String cmdName = cn.next();
/* 464 */           List ccv = (List)curcommands.get(cmdName);
/* 465 */           List cv = (List)commands.get(cmdName);
/* 466 */           if (cv == null) {
/*     */             continue;
/*     */           }
/* 469 */           Iterator cvn = cv.iterator();
/* 470 */           while (cvn.hasNext()) {
/* 471 */             String clazz = cvn.next();
/* 472 */             if (!ccv.contains(clazz)) {
/* 473 */               if (addReverse) {
/* 474 */                 ccv.add(0, clazz); continue;
/*     */               } 
/* 476 */               ccv.add(clazz);
/*     */             } 
/*     */           } 
/*     */         } 
/* 480 */         cn = commands.keySet().iterator();
/* 481 */         while (cn.hasNext()) {
/* 482 */           String cmdName = cn.next();
/* 483 */           if (curcommands.containsKey(cmdName))
/*     */             continue; 
/* 485 */           List cv = (List)commands.get(cmdName);
/* 486 */           curcommands.put(cmdName, cv);
/*     */         } 
/*     */       } 
/* 489 */     } else if (currentToken != 5) {
/* 490 */       reportParseError(5, 59, currentToken, tokenizer.getCurrentTokenValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void reportParseError(int expectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
/* 498 */     throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " token.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void reportParseError(int expectedToken, int otherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
/* 507 */     throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + " or a " + MailcapTokenizer.nameForToken(otherExpectedToken) + " token.");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void reportParseError(int expectedToken, int otherExpectedToken, int anotherExpectedToken, int actualToken, String actualTokenValue) throws MailcapParseException {
/* 517 */     if (LogSupport.isLoggable()) {
/* 518 */       LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 524 */     throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(actualToken) + " token (" + actualTokenValue + ") while expecting a " + MailcapTokenizer.nameForToken(expectedToken) + ", a " + MailcapTokenizer.nameForToken(otherExpectedToken) + ", or a " + MailcapTokenizer.nameForToken(anotherExpectedToken) + " token.");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\activation\registries\MailcapFile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */