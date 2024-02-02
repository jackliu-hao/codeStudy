/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.File;
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
/*     */ public abstract class AbstractScanner
/*     */   implements Scanner
/*     */ {
/*  47 */   public static final String[] DEFAULTEXCLUDES = new String[] { "**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/RCS", "**/RCS/**", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/.svn", "**/.svn/**", "**/.arch-ids", "**/.arch-ids/**", "**/.bzr", "**/.bzr/**", "**/.MySCMServerInfo", "**/.DS_Store", "**/.metadata", "**/.metadata/**", "**/.hg", "**/.hg/**", "**/.git", "**/.git/**", "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**", "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail" };
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
/*     */   protected String[] includes;
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
/*     */   protected String[] excludes;
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
/*     */   protected boolean isCaseSensitive = true;
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
/*     */   public void setCaseSensitive(boolean isCaseSensitive) {
/* 136 */     this.isCaseSensitive = isCaseSensitive;
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
/*     */   protected static boolean matchPatternStart(String pattern, String str) {
/* 157 */     return SelectorUtils.matchPatternStart(pattern, str);
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
/*     */   protected static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
/* 181 */     return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
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
/*     */   protected static boolean matchPath(String pattern, String str) {
/* 197 */     return SelectorUtils.matchPath(pattern, str);
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
/*     */   protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
/* 216 */     return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
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
/*     */   public static boolean match(String pattern, String str) {
/* 235 */     return SelectorUtils.match(pattern, str);
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
/*     */   protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
/* 258 */     return SelectorUtils.match(pattern, str, isCaseSensitive);
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
/*     */   public void setIncludes(String[] includes) {
/* 277 */     if (includes == null) {
/*     */       
/* 279 */       this.includes = null;
/*     */     }
/*     */     else {
/*     */       
/* 283 */       this.includes = new String[includes.length];
/* 284 */       for (int i = 0; i < includes.length; i++)
/*     */       {
/* 286 */         this.includes[i] = normalizePattern(includes[i]);
/*     */       }
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
/*     */   public void setExcludes(String[] excludes) {
/* 305 */     if (excludes == null) {
/*     */       
/* 307 */       this.excludes = null;
/*     */     }
/*     */     else {
/*     */       
/* 311 */       this.excludes = new String[excludes.length];
/* 312 */       for (int i = 0; i < excludes.length; i++)
/*     */       {
/* 314 */         this.excludes[i] = normalizePattern(excludes[i]);
/*     */       }
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
/*     */   private String normalizePattern(String pattern) {
/* 327 */     pattern = pattern.trim();
/*     */     
/* 329 */     if (pattern.startsWith("%regex[")) {
/*     */       
/* 331 */       if (File.separatorChar == '\\')
/*     */       {
/* 333 */         pattern = StringUtils.replace(pattern, "/", "\\\\");
/*     */       }
/*     */       else
/*     */       {
/* 337 */         pattern = StringUtils.replace(pattern, "\\\\", "/");
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/* 342 */       pattern = pattern.replace((File.separatorChar == '/') ? 92 : 47, File.separatorChar);
/*     */       
/* 344 */       if (pattern.endsWith(File.separator))
/*     */       {
/* 346 */         pattern = pattern + "**";
/*     */       }
/*     */     } 
/*     */     
/* 350 */     return pattern;
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
/*     */   protected boolean isIncluded(String name) {
/* 363 */     for (int i = 0; i < this.includes.length; i++) {
/*     */       
/* 365 */       if (matchPath(this.includes[i], name, this.isCaseSensitive))
/*     */       {
/* 367 */         return true;
/*     */       }
/*     */     } 
/* 370 */     return false;
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
/*     */   protected boolean couldHoldIncluded(String name) {
/* 383 */     for (int i = 0; i < this.includes.length; i++) {
/*     */       
/* 385 */       if (matchPatternStart(this.includes[i], name, this.isCaseSensitive))
/*     */       {
/* 387 */         return true;
/*     */       }
/*     */     } 
/* 390 */     return false;
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
/*     */   protected boolean isExcluded(String name) {
/* 403 */     for (int i = 0; i < this.excludes.length; i++) {
/*     */       
/* 405 */       if (matchPath(this.excludes[i], name, this.isCaseSensitive))
/*     */       {
/* 407 */         return true;
/*     */       }
/*     */     } 
/* 410 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addDefaultExcludes() {
/* 418 */     int excludesLength = (this.excludes == null) ? 0 : this.excludes.length;
/*     */     
/* 420 */     String[] newExcludes = new String[excludesLength + DEFAULTEXCLUDES.length];
/* 421 */     if (excludesLength > 0)
/*     */     {
/* 423 */       System.arraycopy(this.excludes, 0, newExcludes, 0, excludesLength);
/*     */     }
/* 425 */     for (int i = 0; i < DEFAULTEXCLUDES.length; i++)
/*     */     {
/* 427 */       newExcludes[i + excludesLength] = DEFAULTEXCLUDES[i].replace('/', File.separatorChar);
/*     */     }
/* 429 */     this.excludes = newExcludes;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void setupDefaultFilters() {
/* 434 */     if (this.includes == null) {
/*     */ 
/*     */       
/* 437 */       this.includes = new String[1];
/* 438 */       this.includes[0] = "**";
/*     */     } 
/* 440 */     if (this.excludes == null)
/*     */     {
/* 442 */       this.excludes = new String[0];
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\AbstractScanner.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */