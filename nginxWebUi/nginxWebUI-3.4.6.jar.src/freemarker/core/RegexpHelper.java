/*     */ package freemarker.core;
/*     */ 
/*     */ import freemarker.cache.MruCacheStorage;
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.regex.PatternSyntaxException;
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
/*     */ final class RegexpHelper
/*     */ {
/*  35 */   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
/*     */   
/*  37 */   private static volatile boolean flagWarningsEnabled = LOG.isWarnEnabled();
/*     */   private static final int MAX_FLAG_WARNINGS_LOGGED = 25;
/*  39 */   private static final Object flagWarningsCntSync = new Object();
/*     */   
/*     */   private static int flagWarningsCnt;
/*  42 */   private static final MruCacheStorage patternCache = new MruCacheStorage(50, 150);
/*     */   
/*     */   private static long intFlagToLong(int flag) {
/*  45 */     return flag & 0xFFFFL;
/*     */   }
/*     */ 
/*     */   
/*  49 */   static final long RE_FLAG_CASE_INSENSITIVE = intFlagToLong(2);
/*     */   
/*  51 */   static final long RE_FLAG_MULTILINE = intFlagToLong(8);
/*     */   
/*  53 */   static final long RE_FLAG_COMMENTS = intFlagToLong(4);
/*     */   
/*  55 */   static final long RE_FLAG_DOTALL = intFlagToLong(32);
/*     */ 
/*     */   
/*     */   static final long RE_FLAG_REGEXP = 4294967296L;
/*     */ 
/*     */   
/*     */   static final long RE_FLAG_FIRST_ONLY = 8589934592L;
/*     */ 
/*     */ 
/*     */   
/*     */   static Pattern getPattern(String patternString, int flags) throws TemplateModelException {
/*     */     Pattern result;
/*  67 */     PatternCacheKey patternKey = new PatternCacheKey(patternString, flags);
/*     */ 
/*     */ 
/*     */     
/*  71 */     synchronized (patternCache) {
/*  72 */       result = (Pattern)patternCache.get(patternKey);
/*     */     } 
/*  74 */     if (result != null) {
/*  75 */       return result;
/*     */     }
/*     */     
/*     */     try {
/*  79 */       result = Pattern.compile(patternString, flags);
/*  80 */     } catch (PatternSyntaxException e) {
/*  81 */       throw new _TemplateModelException(e, new Object[] { "Malformed regular expression: ", new _DelayedGetMessage(e) });
/*     */     } 
/*     */     
/*  84 */     synchronized (patternCache) {
/*  85 */       patternCache.put(patternKey, result);
/*     */     } 
/*  87 */     return result;
/*     */   }
/*     */   
/*     */   private static class PatternCacheKey {
/*     */     private final String patternString;
/*     */     private final int flags;
/*     */     private final int hashCode;
/*     */     
/*     */     public PatternCacheKey(String patternString, int flags) {
/*  96 */       this.patternString = patternString;
/*  97 */       this.flags = flags;
/*  98 */       this.hashCode = patternString.hashCode() + 31 * flags;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object that) {
/* 103 */       if (that instanceof PatternCacheKey) {
/* 104 */         PatternCacheKey thatPCK = (PatternCacheKey)that;
/* 105 */         return (thatPCK.flags == this.flags && thatPCK.patternString
/* 106 */           .equals(this.patternString));
/*     */       } 
/* 108 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 114 */       return this.hashCode;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   static long parseFlagString(String flagString) {
/* 120 */     long flags = 0L;
/* 121 */     for (int i = 0; i < flagString.length(); i++) {
/* 122 */       char c = flagString.charAt(i);
/* 123 */       switch (c) {
/*     */         case 'i':
/* 125 */           flags |= RE_FLAG_CASE_INSENSITIVE;
/*     */           break;
/*     */         case 'm':
/* 128 */           flags |= RE_FLAG_MULTILINE;
/*     */           break;
/*     */         case 'c':
/* 131 */           flags |= RE_FLAG_COMMENTS;
/*     */           break;
/*     */         case 's':
/* 134 */           flags |= RE_FLAG_DOTALL;
/*     */           break;
/*     */         case 'r':
/* 137 */           flags |= 0x100000000L;
/*     */           break;
/*     */         case 'f':
/* 140 */           flags |= 0x200000000L;
/*     */           break;
/*     */         default:
/* 143 */           if (flagWarningsEnabled)
/* 144 */             logFlagWarning("Unrecognized regular expression flag: " + 
/*     */                 
/* 146 */                 StringUtil.jQuote(String.valueOf(c)) + "."); 
/*     */           break;
/*     */       } 
/*     */     } 
/* 150 */     return flags;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void logFlagWarning(String message) {
/*     */     int cnt;
/* 158 */     if (!flagWarningsEnabled) {
/*     */       return;
/*     */     }
/* 161 */     synchronized (flagWarningsCntSync) {
/* 162 */       cnt = flagWarningsCnt;
/* 163 */       if (cnt < 25) {
/* 164 */         flagWarningsCnt++;
/*     */       } else {
/* 166 */         flagWarningsEnabled = false;
/*     */         return;
/*     */       } 
/*     */     } 
/* 170 */     message = message + " This will be an error in some later FreeMarker version!";
/* 171 */     if (cnt + 1 == 25) {
/* 172 */       message = message + " [Will not log more regular expression flag problems until restart!]";
/*     */     }
/* 174 */     LOG.warn(message);
/*     */   }
/*     */   
/*     */   static void checkNonRegexpFlags(String biName, long flags) throws _TemplateModelException {
/* 178 */     checkOnlyHasNonRegexpFlags(biName, flags, false);
/*     */   }
/*     */   
/*     */   static void checkOnlyHasNonRegexpFlags(String biName, long flags, boolean strict) throws _TemplateModelException {
/*     */     String flag;
/* 183 */     if (!strict && !flagWarningsEnabled) {
/*     */       return;
/*     */     }
/* 186 */     if ((flags & RE_FLAG_MULTILINE) != 0L) {
/* 187 */       flag = "m";
/* 188 */     } else if ((flags & RE_FLAG_DOTALL) != 0L) {
/* 189 */       flag = "s";
/* 190 */     } else if ((flags & RE_FLAG_COMMENTS) != 0L) {
/* 191 */       flag = "c";
/*     */     } else {
/*     */       return;
/*     */     } 
/*     */     
/* 196 */     Object[] msg = { "?", biName, " doesn't support the \"", flag, "\" flag without the \"r\" flag." };
/*     */     
/* 198 */     if (strict) {
/* 199 */       throw new _TemplateModelException(msg);
/*     */     }
/*     */     
/* 202 */     logFlagWarning((new _ErrorDescriptionBuilder(msg)).toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\RegexpHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */