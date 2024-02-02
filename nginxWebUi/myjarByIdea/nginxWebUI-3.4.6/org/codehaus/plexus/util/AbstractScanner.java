package org.codehaus.plexus.util;

import java.io.File;

public abstract class AbstractScanner implements Scanner {
   public static final String[] DEFAULTEXCLUDES = new String[]{"**/*~", "**/#*#", "**/.#*", "**/%*%", "**/._*", "**/CVS", "**/CVS/**", "**/.cvsignore", "**/RCS", "**/RCS/**", "**/SCCS", "**/SCCS/**", "**/vssver.scc", "**/.svn", "**/.svn/**", "**/.arch-ids", "**/.arch-ids/**", "**/.bzr", "**/.bzr/**", "**/.MySCMServerInfo", "**/.DS_Store", "**/.metadata", "**/.metadata/**", "**/.hg", "**/.hg/**", "**/.git", "**/.git/**", "**/BitKeeper", "**/BitKeeper/**", "**/ChangeSet", "**/ChangeSet/**", "**/_darcs", "**/_darcs/**", "**/.darcsrepo", "**/.darcsrepo/**", "**/-darcs-backup*", "**/.darcs-temp-mail"};
   protected String[] includes;
   protected String[] excludes;
   protected boolean isCaseSensitive = true;

   public void setCaseSensitive(boolean isCaseSensitive) {
      this.isCaseSensitive = isCaseSensitive;
   }

   protected static boolean matchPatternStart(String pattern, String str) {
      return SelectorUtils.matchPatternStart(pattern, str);
   }

   protected static boolean matchPatternStart(String pattern, String str, boolean isCaseSensitive) {
      return SelectorUtils.matchPatternStart(pattern, str, isCaseSensitive);
   }

   protected static boolean matchPath(String pattern, String str) {
      return SelectorUtils.matchPath(pattern, str);
   }

   protected static boolean matchPath(String pattern, String str, boolean isCaseSensitive) {
      return SelectorUtils.matchPath(pattern, str, isCaseSensitive);
   }

   public static boolean match(String pattern, String str) {
      return SelectorUtils.match(pattern, str);
   }

   protected static boolean match(String pattern, String str, boolean isCaseSensitive) {
      return SelectorUtils.match(pattern, str, isCaseSensitive);
   }

   public void setIncludes(String[] includes) {
      if (includes == null) {
         this.includes = null;
      } else {
         this.includes = new String[includes.length];

         for(int i = 0; i < includes.length; ++i) {
            this.includes[i] = this.normalizePattern(includes[i]);
         }
      }

   }

   public void setExcludes(String[] excludes) {
      if (excludes == null) {
         this.excludes = null;
      } else {
         this.excludes = new String[excludes.length];

         for(int i = 0; i < excludes.length; ++i) {
            this.excludes[i] = this.normalizePattern(excludes[i]);
         }
      }

   }

   private String normalizePattern(String pattern) {
      pattern = pattern.trim();
      if (pattern.startsWith("%regex[")) {
         if (File.separatorChar == '\\') {
            pattern = StringUtils.replace(pattern, "/", "\\\\");
         } else {
            pattern = StringUtils.replace(pattern, "\\\\", "/");
         }
      } else {
         pattern = pattern.replace((char)(File.separatorChar == '/' ? '\\' : '/'), File.separatorChar);
         if (pattern.endsWith(File.separator)) {
            pattern = pattern + "**";
         }
      }

      return pattern;
   }

   protected boolean isIncluded(String name) {
      for(int i = 0; i < this.includes.length; ++i) {
         if (matchPath(this.includes[i], name, this.isCaseSensitive)) {
            return true;
         }
      }

      return false;
   }

   protected boolean couldHoldIncluded(String name) {
      for(int i = 0; i < this.includes.length; ++i) {
         if (matchPatternStart(this.includes[i], name, this.isCaseSensitive)) {
            return true;
         }
      }

      return false;
   }

   protected boolean isExcluded(String name) {
      for(int i = 0; i < this.excludes.length; ++i) {
         if (matchPath(this.excludes[i], name, this.isCaseSensitive)) {
            return true;
         }
      }

      return false;
   }

   public void addDefaultExcludes() {
      int excludesLength = this.excludes == null ? 0 : this.excludes.length;
      String[] newExcludes = new String[excludesLength + DEFAULTEXCLUDES.length];
      if (excludesLength > 0) {
         System.arraycopy(this.excludes, 0, newExcludes, 0, excludesLength);
      }

      for(int i = 0; i < DEFAULTEXCLUDES.length; ++i) {
         newExcludes[i + excludesLength] = DEFAULTEXCLUDES[i].replace('/', File.separatorChar);
      }

      this.excludes = newExcludes;
   }

   protected void setupDefaultFilters() {
      if (this.includes == null) {
         this.includes = new String[1];
         this.includes[0] = "**";
      }

      if (this.excludes == null) {
         this.excludes = new String[0];
      }

   }
}
