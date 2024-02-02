/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ public class PhoneticEngine
/*     */ {
/*     */   static final class PhonemeBuilder
/*     */   {
/*     */     private final Set<Rule.Phoneme> phonemes;
/*     */     
/*     */     public static PhonemeBuilder empty(Languages.LanguageSet languages) {
/*  72 */       return new PhonemeBuilder(new Rule.Phoneme("", languages));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private PhonemeBuilder(Rule.Phoneme phoneme) {
/*  78 */       this.phonemes = new LinkedHashSet<>();
/*  79 */       this.phonemes.add(phoneme);
/*     */     }
/*     */     
/*     */     private PhonemeBuilder(Set<Rule.Phoneme> phonemes) {
/*  83 */       this.phonemes = phonemes;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void append(CharSequence str) {
/*  92 */       for (Rule.Phoneme ph : this.phonemes) {
/*  93 */         ph.append(str);
/*     */       }
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
/*     */     
/*     */     public void apply(Rule.PhonemeExpr phonemeExpr, int maxPhonemes) {
/* 107 */       Set<Rule.Phoneme> newPhonemes = new LinkedHashSet<>(maxPhonemes);
/*     */       
/* 109 */       label18: for (Rule.Phoneme left : this.phonemes) {
/* 110 */         for (Rule.Phoneme right : phonemeExpr.getPhonemes()) {
/* 111 */           Languages.LanguageSet languages = left.getLanguages().restrictTo(right.getLanguages());
/* 112 */           if (!languages.isEmpty()) {
/* 113 */             Rule.Phoneme join = new Rule.Phoneme(left, right, languages);
/* 114 */             if (newPhonemes.size() < maxPhonemes) {
/* 115 */               newPhonemes.add(join);
/* 116 */               if (newPhonemes.size() >= maxPhonemes) {
/*     */                 break label18;
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 124 */       this.phonemes.clear();
/* 125 */       this.phonemes.addAll(newPhonemes);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<Rule.Phoneme> getPhonemes() {
/* 134 */       return this.phonemes;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String makeString() {
/* 145 */       StringBuilder sb = new StringBuilder();
/*     */       
/* 147 */       for (Rule.Phoneme ph : this.phonemes) {
/* 148 */         if (sb.length() > 0) {
/* 149 */           sb.append("|");
/*     */         }
/* 151 */         sb.append(ph.getPhonemeText());
/*     */       } 
/*     */       
/* 154 */       return sb.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class RulesApplication
/*     */   {
/*     */     private final Map<String, List<Rule>> finalRules;
/*     */ 
/*     */     
/*     */     private final CharSequence input;
/*     */ 
/*     */     
/*     */     private final PhoneticEngine.PhonemeBuilder phonemeBuilder;
/*     */ 
/*     */     
/*     */     private int i;
/*     */ 
/*     */     
/*     */     private final int maxPhonemes;
/*     */ 
/*     */     
/*     */     private boolean found;
/*     */ 
/*     */     
/*     */     public RulesApplication(Map<String, List<Rule>> finalRules, CharSequence input, PhoneticEngine.PhonemeBuilder phonemeBuilder, int i, int maxPhonemes) {
/* 181 */       Objects.requireNonNull(finalRules, "finalRules");
/* 182 */       this.finalRules = finalRules;
/* 183 */       this.phonemeBuilder = phonemeBuilder;
/* 184 */       this.input = input;
/* 185 */       this.i = i;
/* 186 */       this.maxPhonemes = maxPhonemes;
/*     */     }
/*     */     
/*     */     public int getI() {
/* 190 */       return this.i;
/*     */     }
/*     */     
/*     */     public PhoneticEngine.PhonemeBuilder getPhonemeBuilder() {
/* 194 */       return this.phonemeBuilder;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public RulesApplication invoke() {
/* 205 */       this.found = false;
/* 206 */       int patternLength = 1;
/* 207 */       List<Rule> rules = this.finalRules.get(this.input.subSequence(this.i, this.i + patternLength));
/* 208 */       if (rules != null) {
/* 209 */         for (Rule rule : rules) {
/* 210 */           String pattern = rule.getPattern();
/* 211 */           patternLength = pattern.length();
/* 212 */           if (rule.patternAndContextMatches(this.input, this.i)) {
/* 213 */             this.phonemeBuilder.apply(rule.getPhoneme(), this.maxPhonemes);
/* 214 */             this.found = true;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/* 220 */       if (!this.found) {
/* 221 */         patternLength = 1;
/*     */       }
/*     */       
/* 224 */       this.i += patternLength;
/* 225 */       return this;
/*     */     }
/*     */     
/*     */     public boolean isFound() {
/* 229 */       return this.found;
/*     */     }
/*     */   }
/*     */   private static final int DEFAULT_MAX_PHONEMES = 20;
/* 233 */   private static final Map<NameType, Set<String>> NAME_PREFIXES = new EnumMap<>(NameType.class);
/*     */   
/*     */   static {
/* 236 */     NAME_PREFIXES.put(NameType.ASHKENAZI, 
/* 237 */         Collections.unmodifiableSet(new HashSet<>(
/* 238 */             Arrays.asList(new String[] { "bar", "ben", "da", "de", "van", "von" }))));
/* 239 */     NAME_PREFIXES.put(NameType.SEPHARDIC, 
/* 240 */         Collections.unmodifiableSet(new HashSet<>(
/* 241 */             Arrays.asList(new String[] { 
/*     */                 "al", "el", "da", "dal", "de", "del", "dela", "de la", "della", "des", 
/* 243 */                 "di", "do", "dos", "du", "van", "von" })))); NAME_PREFIXES.put(NameType.GENERIC, 
/* 244 */         Collections.unmodifiableSet(new HashSet<>(
/* 245 */             Arrays.asList(new String[] { 
/*     */                 "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", 
/*     */                 "dos", "du", "van", "von" }))));
/*     */   }
/*     */   private final Lang lang;
/*     */   private final NameType nameType;
/*     */   private final RuleType ruleType;
/*     */   private final boolean concat;
/*     */   private final int maxPhonemes;
/*     */   
/*     */   private static String join(Iterable<String> strings, String sep) {
/* 256 */     StringBuilder sb = new StringBuilder();
/* 257 */     Iterator<String> si = strings.iterator();
/* 258 */     if (si.hasNext()) {
/* 259 */       sb.append(si.next());
/*     */     }
/* 261 */     while (si.hasNext()) {
/* 262 */       sb.append(sep).append(si.next());
/*     */     }
/*     */     
/* 265 */     return sb.toString();
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
/*     */   public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat) {
/* 291 */     this(nameType, ruleType, concat, 20);
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
/*     */   public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat, int maxPhonemes) {
/* 309 */     if (ruleType == RuleType.RULES) {
/* 310 */       throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
/*     */     }
/* 312 */     this.nameType = nameType;
/* 313 */     this.ruleType = ruleType;
/* 314 */     this.concat = concat;
/* 315 */     this.lang = Lang.instance(nameType);
/* 316 */     this.maxPhonemes = maxPhonemes;
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
/*     */   private PhonemeBuilder applyFinalRules(PhonemeBuilder phonemeBuilder, Map<String, List<Rule>> finalRules) {
/* 329 */     Objects.requireNonNull(finalRules, "finalRules");
/* 330 */     if (finalRules.isEmpty()) {
/* 331 */       return phonemeBuilder;
/*     */     }
/*     */     
/* 334 */     Map<Rule.Phoneme, Rule.Phoneme> phonemes = new TreeMap<>(Rule.Phoneme.COMPARATOR);
/*     */ 
/*     */     
/* 337 */     for (Rule.Phoneme phoneme : phonemeBuilder.getPhonemes()) {
/* 338 */       PhonemeBuilder subBuilder = PhonemeBuilder.empty(phoneme.getLanguages());
/* 339 */       String phonemeText = phoneme.getPhonemeText().toString();
/*     */       int i;
/* 341 */       for (i = 0; i < phonemeText.length(); ) {
/*     */         
/* 343 */         RulesApplication rulesApplication = (new RulesApplication(finalRules, phonemeText, subBuilder, i, this.maxPhonemes)).invoke();
/* 344 */         boolean found = rulesApplication.isFound();
/* 345 */         subBuilder = rulesApplication.getPhonemeBuilder();
/*     */         
/* 347 */         if (!found)
/*     */         {
/* 349 */           subBuilder.append(phonemeText.subSequence(i, i + 1));
/*     */         }
/*     */         
/* 352 */         i = rulesApplication.getI();
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 358 */       for (Rule.Phoneme newPhoneme : subBuilder.getPhonemes()) {
/* 359 */         if (phonemes.containsKey(newPhoneme)) {
/* 360 */           Rule.Phoneme oldPhoneme = phonemes.remove(newPhoneme);
/* 361 */           Rule.Phoneme mergedPhoneme = oldPhoneme.mergeWithLanguage(newPhoneme.getLanguages());
/* 362 */           phonemes.put(mergedPhoneme, mergedPhoneme); continue;
/*     */         } 
/* 364 */         phonemes.put(newPhoneme, newPhoneme);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 369 */     return new PhonemeBuilder(phonemes.keySet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String encode(String input) {
/* 380 */     Languages.LanguageSet languageSet = this.lang.guessLanguages(input);
/* 381 */     return encode(input, languageSet);
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
/*     */   public String encode(String input, Languages.LanguageSet languageSet) {
/* 395 */     Map<String, List<Rule>> rules = Rule.getInstanceMap(this.nameType, RuleType.RULES, languageSet);
/*     */     
/* 397 */     Map<String, List<Rule>> finalRules1 = Rule.getInstanceMap(this.nameType, this.ruleType, "common");
/*     */     
/* 399 */     Map<String, List<Rule>> finalRules2 = Rule.getInstanceMap(this.nameType, this.ruleType, languageSet);
/*     */ 
/*     */ 
/*     */     
/* 403 */     input = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();
/*     */     
/* 405 */     if (this.nameType == NameType.GENERIC) {
/* 406 */       if (input.length() >= 2 && input.substring(0, 2).equals("d'")) {
/* 407 */         String remainder = input.substring(2);
/* 408 */         String combined = "d" + remainder;
/* 409 */         return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
/*     */       } 
/* 411 */       for (String l : NAME_PREFIXES.get(this.nameType)) {
/*     */         
/* 413 */         if (input.startsWith(l + " ")) {
/*     */           
/* 415 */           String remainder = input.substring(l.length() + 1);
/* 416 */           String combined = l + remainder;
/* 417 */           return "(" + encode(remainder) + ")-(" + encode(combined) + ")";
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 422 */     List<String> words = Arrays.asList(input.split("\\s+"));
/* 423 */     List<String> words2 = new ArrayList<>();
/*     */ 
/*     */     
/* 426 */     switch (this.nameType) {
/*     */       case SEPHARDIC:
/* 428 */         for (String aWord : words) {
/* 429 */           String[] parts = aWord.split("'");
/* 430 */           String lastPart = parts[parts.length - 1];
/* 431 */           words2.add(lastPart);
/*     */         } 
/* 433 */         words2.removeAll(NAME_PREFIXES.get(this.nameType));
/*     */         break;
/*     */       case ASHKENAZI:
/* 436 */         words2.addAll(words);
/* 437 */         words2.removeAll(NAME_PREFIXES.get(this.nameType));
/*     */         break;
/*     */       case GENERIC:
/* 440 */         words2.addAll(words);
/*     */         break;
/*     */       default:
/* 443 */         throw new IllegalStateException("Unreachable case: " + this.nameType);
/*     */     } 
/*     */     
/* 446 */     if (this.concat) {
/*     */       
/* 448 */       input = join(words2, " ");
/* 449 */     } else if (words2.size() == 1) {
/*     */       
/* 451 */       input = words.iterator().next();
/*     */     } else {
/*     */       
/* 454 */       StringBuilder result = new StringBuilder();
/* 455 */       for (String word : words2) {
/* 456 */         result.append("-").append(encode(word));
/*     */       }
/*     */       
/* 459 */       return result.substring(1);
/*     */     } 
/*     */     
/* 462 */     PhonemeBuilder phonemeBuilder = PhonemeBuilder.empty(languageSet);
/*     */ 
/*     */     
/* 465 */     for (int i = 0; i < input.length(); ) {
/*     */       
/* 467 */       RulesApplication rulesApplication = (new RulesApplication(rules, input, phonemeBuilder, i, this.maxPhonemes)).invoke();
/* 468 */       i = rulesApplication.getI();
/* 469 */       phonemeBuilder = rulesApplication.getPhonemeBuilder();
/*     */     } 
/*     */ 
/*     */     
/* 473 */     phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules1);
/*     */     
/* 475 */     phonemeBuilder = applyFinalRules(phonemeBuilder, finalRules2);
/*     */     
/* 477 */     return phonemeBuilder.makeString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Lang getLang() {
/* 486 */     return this.lang;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public NameType getNameType() {
/* 495 */     return this.nameType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleType getRuleType() {
/* 504 */     return this.ruleType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConcat() {
/* 513 */     return this.concat;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxPhonemes() {
/* 523 */     return this.maxPhonemes;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\PhoneticEngine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */