/*     */ package org.apache.commons.codec.language;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
/*     */ import org.apache.commons.codec.EncoderException;
/*     */ import org.apache.commons.codec.Resources;
/*     */ import org.apache.commons.codec.StringEncoder;
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
/*     */ public class DaitchMokotoffSoundex
/*     */   implements StringEncoder
/*     */ {
/*     */   private static final String COMMENT = "//";
/*     */   private static final String DOUBLE_QUOTE = "\"";
/*     */   private static final String MULTILINE_COMMENT_END = "*/";
/*     */   private static final String MULTILINE_COMMENT_START = "/*";
/*     */   private static final String RESOURCE_FILE = "org/apache/commons/codec/language/dmrules.txt";
/*     */   private static final int MAX_LENGTH = 6;
/*     */   
/*     */   private static final class Branch
/*     */   {
/*  82 */     private final StringBuilder builder = new StringBuilder();
/*  83 */     private String lastReplacement = null;
/*  84 */     private String cachedString = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Branch createBranch() {
/*  93 */       Branch branch = new Branch();
/*  94 */       branch.builder.append(toString());
/*  95 */       branch.lastReplacement = this.lastReplacement;
/*  96 */       return branch;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object other) {
/* 101 */       if (this == other) {
/* 102 */         return true;
/*     */       }
/* 104 */       if (!(other instanceof Branch)) {
/* 105 */         return false;
/*     */       }
/*     */       
/* 108 */       return toString().equals(((Branch)other).toString());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void finish() {
/* 115 */       while (this.builder.length() < 6) {
/* 116 */         this.builder.append('0');
/* 117 */         this.cachedString = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 123 */       return toString().hashCode();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void processNextReplacement(String replacement, boolean forceAppend) {
/* 135 */       boolean append = (this.lastReplacement == null || !this.lastReplacement.endsWith(replacement) || forceAppend);
/*     */       
/* 137 */       if (append && this.builder.length() < 6) {
/* 138 */         this.builder.append(replacement);
/*     */         
/* 140 */         if (this.builder.length() > 6) {
/* 141 */           this.builder.delete(6, this.builder.length());
/*     */         }
/* 143 */         this.cachedString = null;
/*     */       } 
/*     */       
/* 146 */       this.lastReplacement = replacement;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 151 */       if (this.cachedString == null) {
/* 152 */         this.cachedString = this.builder.toString();
/*     */       }
/* 154 */       return this.cachedString;
/*     */     }
/*     */ 
/*     */     
/*     */     private Branch() {}
/*     */   }
/*     */   
/*     */   private static final class Rule
/*     */   {
/*     */     private final String pattern;
/*     */     private final String[] replacementAtStart;
/*     */     private final String[] replacementBeforeVowel;
/*     */     private final String[] replacementDefault;
/*     */     
/*     */     protected Rule(String pattern, String replacementAtStart, String replacementBeforeVowel, String replacementDefault) {
/* 169 */       this.pattern = pattern;
/* 170 */       this.replacementAtStart = replacementAtStart.split("\\|");
/* 171 */       this.replacementBeforeVowel = replacementBeforeVowel.split("\\|");
/* 172 */       this.replacementDefault = replacementDefault.split("\\|");
/*     */     }
/*     */     
/*     */     public int getPatternLength() {
/* 176 */       return this.pattern.length();
/*     */     }
/*     */     
/*     */     public String[] getReplacements(String context, boolean atStart) {
/* 180 */       if (atStart) {
/* 181 */         return this.replacementAtStart;
/*     */       }
/*     */       
/* 184 */       int nextIndex = getPatternLength();
/* 185 */       boolean nextCharIsVowel = (nextIndex < context.length()) ? isVowel(context.charAt(nextIndex)) : false;
/* 186 */       if (nextCharIsVowel) {
/* 187 */         return this.replacementBeforeVowel;
/*     */       }
/*     */       
/* 190 */       return this.replacementDefault;
/*     */     }
/*     */     
/*     */     private boolean isVowel(char ch) {
/* 194 */       return (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u');
/*     */     }
/*     */     
/*     */     public boolean matches(String context) {
/* 198 */       return context.startsWith(this.pattern);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 203 */       return String.format("%s=(%s,%s,%s)", new Object[] { this.pattern, Arrays.asList(this.replacementAtStart), 
/* 204 */             Arrays.asList(this.replacementBeforeVowel), Arrays.asList(this.replacementDefault) });
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 222 */   private static final Map<Character, List<Rule>> RULES = new HashMap<>();
/*     */ 
/*     */   
/* 225 */   private static final Map<Character, Character> FOLDINGS = new HashMap<>(); private final boolean folding;
/*     */   
/*     */   static {
/* 228 */     try (Scanner scanner = new Scanner(Resources.getInputStream("org/apache/commons/codec/language/dmrules.txt"), "UTF-8")) {
/* 229 */       parseRules(scanner, "org/apache/commons/codec/language/dmrules.txt", RULES, FOLDINGS);
/*     */     } 
/*     */ 
/*     */     
/* 233 */     for (Map.Entry<Character, List<Rule>> rule : RULES.entrySet()) {
/* 234 */       List<Rule> ruleList = rule.getValue();
/* 235 */       Collections.sort(ruleList, new Comparator<Rule>()
/*     */           {
/*     */             public int compare(DaitchMokotoffSoundex.Rule rule1, DaitchMokotoffSoundex.Rule rule2) {
/* 238 */               return rule2.getPatternLength() - rule1.getPatternLength();
/*     */             }
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void parseRules(Scanner scanner, String location, Map<Character, List<Rule>> ruleMapping, Map<Character, Character> asciiFoldings) {
/* 246 */     int currentLine = 0;
/* 247 */     boolean inMultilineComment = false;
/*     */     
/* 249 */     while (scanner.hasNextLine()) {
/* 250 */       currentLine++;
/* 251 */       String rawLine = scanner.nextLine();
/* 252 */       String line = rawLine;
/*     */       
/* 254 */       if (inMultilineComment) {
/* 255 */         if (line.endsWith("*/")) {
/* 256 */           inMultilineComment = false;
/*     */         }
/*     */         
/*     */         continue;
/*     */       } 
/* 261 */       if (line.startsWith("/*")) {
/* 262 */         inMultilineComment = true;
/*     */         continue;
/*     */       } 
/* 265 */       int cmtI = line.indexOf("//");
/* 266 */       if (cmtI >= 0) {
/* 267 */         line = line.substring(0, cmtI);
/*     */       }
/*     */ 
/*     */       
/* 271 */       line = line.trim();
/*     */       
/* 273 */       if (line.length() == 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 277 */       if (line.contains("=")) {
/*     */         
/* 279 */         String[] arrayOfString = line.split("=");
/* 280 */         if (arrayOfString.length != 2) {
/* 281 */           throw new IllegalArgumentException("Malformed folding statement split into " + arrayOfString.length + " parts: " + rawLine + " in " + location);
/*     */         }
/*     */         
/* 284 */         String leftCharacter = arrayOfString[0];
/* 285 */         String rightCharacter = arrayOfString[1];
/*     */         
/* 287 */         if (leftCharacter.length() != 1 || rightCharacter.length() != 1) {
/* 288 */           throw new IllegalArgumentException("Malformed folding statement - patterns are not single characters: " + rawLine + " in " + location);
/*     */         }
/*     */ 
/*     */         
/* 292 */         asciiFoldings.put(Character.valueOf(leftCharacter.charAt(0)), Character.valueOf(rightCharacter.charAt(0)));
/*     */         continue;
/*     */       } 
/* 295 */       String[] parts = line.split("\\s+");
/* 296 */       if (parts.length != 4) {
/* 297 */         throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
/*     */       }
/*     */       
/*     */       try {
/* 301 */         String pattern = stripQuotes(parts[0]);
/* 302 */         String replacement1 = stripQuotes(parts[1]);
/* 303 */         String replacement2 = stripQuotes(parts[2]);
/* 304 */         String replacement3 = stripQuotes(parts[3]);
/*     */         
/* 306 */         Rule r = new Rule(pattern, replacement1, replacement2, replacement3);
/* 307 */         char patternKey = r.pattern.charAt(0);
/* 308 */         List<Rule> rules = ruleMapping.get(Character.valueOf(patternKey));
/* 309 */         if (rules == null) {
/* 310 */           rules = new ArrayList<>();
/* 311 */           ruleMapping.put(Character.valueOf(patternKey), rules);
/*     */         } 
/* 313 */         rules.add(r);
/* 314 */       } catch (IllegalArgumentException e) {
/* 315 */         throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String stripQuotes(String str) {
/* 324 */     if (str.startsWith("\"")) {
/* 325 */       str = str.substring(1);
/*     */     }
/*     */     
/* 328 */     if (str.endsWith("\"")) {
/* 329 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 332 */     return str;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DaitchMokotoffSoundex() {
/* 342 */     this(true);
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
/*     */   public DaitchMokotoffSoundex(boolean folding) {
/* 356 */     this.folding = folding;
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
/*     */   private String cleanup(String input) {
/* 370 */     StringBuilder sb = new StringBuilder();
/* 371 */     for (char ch : input.toCharArray()) {
/* 372 */       if (!Character.isWhitespace(ch)) {
/*     */ 
/*     */ 
/*     */         
/* 376 */         ch = Character.toLowerCase(ch);
/* 377 */         if (this.folding && FOLDINGS.containsKey(Character.valueOf(ch))) {
/* 378 */           ch = ((Character)FOLDINGS.get(Character.valueOf(ch))).charValue();
/*     */         }
/* 380 */         sb.append(ch);
/*     */       } 
/* 382 */     }  return sb.toString();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object encode(Object obj) throws EncoderException {
/* 405 */     if (!(obj instanceof String)) {
/* 406 */       throw new EncoderException("Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
/*     */     }
/*     */     
/* 409 */     return encode((String)obj);
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
/*     */ 
/*     */   
/*     */   public String encode(String source) {
/* 425 */     if (source == null) {
/* 426 */       return null;
/*     */     }
/* 428 */     return soundex(source, false)[0];
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
/*     */   public String soundex(String source) {
/* 455 */     String[] branches = soundex(source, true);
/* 456 */     StringBuilder sb = new StringBuilder();
/* 457 */     int index = 0;
/* 458 */     for (String branch : branches) {
/* 459 */       sb.append(branch);
/* 460 */       if (++index < branches.length) {
/* 461 */         sb.append('|');
/*     */       }
/*     */     } 
/* 464 */     return sb.toString();
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
/*     */   private String[] soundex(String source, boolean branching) {
/* 478 */     if (source == null) {
/* 479 */       return null;
/*     */     }
/*     */     
/* 482 */     String input = cleanup(source);
/*     */     
/* 484 */     Set<Branch> currentBranches = new LinkedHashSet<>();
/* 485 */     currentBranches.add(new Branch());
/*     */     
/* 487 */     char lastChar = Character.MIN_VALUE;
/* 488 */     for (int index = 0; index < input.length(); index++) {
/* 489 */       char ch = input.charAt(index);
/*     */ 
/*     */       
/* 492 */       if (!Character.isWhitespace(ch)) {
/*     */ 
/*     */ 
/*     */         
/* 496 */         String inputContext = input.substring(index);
/* 497 */         List<Rule> rules = RULES.get(Character.valueOf(ch));
/* 498 */         if (rules != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 503 */           List<Branch> nextBranches = branching ? new ArrayList<>() : Collections.<Branch>emptyList();
/*     */           
/* 505 */           for (Rule rule : rules) {
/* 506 */             if (rule.matches(inputContext)) {
/* 507 */               if (branching) {
/* 508 */                 nextBranches.clear();
/*     */               }
/* 510 */               String[] replacements = rule.getReplacements(inputContext, (lastChar == '\000'));
/* 511 */               boolean branchingRequired = (replacements.length > 1 && branching);
/*     */               
/* 513 */               for (Branch branch : currentBranches) {
/* 514 */                 String[] arrayOfString; int j; byte b; for (arrayOfString = replacements, j = arrayOfString.length, b = 0; b < j; ) { String nextReplacement = arrayOfString[b];
/*     */                   
/* 516 */                   Branch nextBranch = branchingRequired ? branch.createBranch() : branch;
/*     */ 
/*     */                   
/* 519 */                   boolean force = ((lastChar == 'm' && ch == 'n') || (lastChar == 'n' && ch == 'm'));
/*     */                   
/* 521 */                   nextBranch.processNextReplacement(nextReplacement, force);
/*     */                   
/* 523 */                   if (branching) {
/* 524 */                     nextBranches.add(nextBranch);
/*     */                     
/*     */                     b++;
/*     */                   }  }
/*     */               
/*     */               } 
/*     */               
/* 531 */               if (branching) {
/* 532 */                 currentBranches.clear();
/* 533 */                 currentBranches.addAll(nextBranches);
/*     */               } 
/* 535 */               index += rule.getPatternLength() - 1;
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/* 540 */           lastChar = ch;
/*     */         } 
/*     */       } 
/* 543 */     }  String[] result = new String[currentBranches.size()];
/* 544 */     int i = 0;
/* 545 */     for (Branch branch : currentBranches) {
/* 546 */       branch.finish();
/* 547 */       result[i++] = branch.toString();
/*     */     } 
/*     */     
/* 550 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\DaitchMokotoffSoundex.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */