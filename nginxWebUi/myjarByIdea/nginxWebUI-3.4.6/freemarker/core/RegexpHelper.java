package freemarker.core;

import freemarker.cache.MruCacheStorage;
import freemarker.log.Logger;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.StringUtil;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

final class RegexpHelper {
   private static final Logger LOG = Logger.getLogger("freemarker.runtime");
   private static volatile boolean flagWarningsEnabled;
   private static final int MAX_FLAG_WARNINGS_LOGGED = 25;
   private static final Object flagWarningsCntSync;
   private static int flagWarningsCnt;
   private static final MruCacheStorage patternCache;
   static final long RE_FLAG_CASE_INSENSITIVE;
   static final long RE_FLAG_MULTILINE;
   static final long RE_FLAG_COMMENTS;
   static final long RE_FLAG_DOTALL;
   static final long RE_FLAG_REGEXP = 4294967296L;
   static final long RE_FLAG_FIRST_ONLY = 8589934592L;

   private static long intFlagToLong(int flag) {
      return (long)flag & 65535L;
   }

   private RegexpHelper() {
   }

   static Pattern getPattern(String patternString, int flags) throws TemplateModelException {
      PatternCacheKey patternKey = new PatternCacheKey(patternString, flags);
      Pattern result;
      synchronized(patternCache) {
         result = (Pattern)patternCache.get(patternKey);
      }

      if (result != null) {
         return result;
      } else {
         try {
            result = Pattern.compile(patternString, flags);
         } catch (PatternSyntaxException var8) {
            throw new _TemplateModelException(var8, new Object[]{"Malformed regular expression: ", new _DelayedGetMessage(var8)});
         }

         synchronized(patternCache) {
            patternCache.put(patternKey, result);
            return result;
         }
      }
   }

   static long parseFlagString(String flagString) {
      long flags = 0L;

      for(int i = 0; i < flagString.length(); ++i) {
         char c = flagString.charAt(i);
         switch (c) {
            case 'c':
               flags |= RE_FLAG_COMMENTS;
               break;
            case 'd':
            case 'e':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'l':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            default:
               if (flagWarningsEnabled) {
                  logFlagWarning("Unrecognized regular expression flag: " + StringUtil.jQuote(String.valueOf(c)) + ".");
               }
               break;
            case 'f':
               flags |= 8589934592L;
               break;
            case 'i':
               flags |= RE_FLAG_CASE_INSENSITIVE;
               break;
            case 'm':
               flags |= RE_FLAG_MULTILINE;
               break;
            case 'r':
               flags |= 4294967296L;
               break;
            case 's':
               flags |= RE_FLAG_DOTALL;
         }
      }

      return flags;
   }

   static void logFlagWarning(String message) {
      if (flagWarningsEnabled) {
         int cnt;
         synchronized(flagWarningsCntSync) {
            cnt = flagWarningsCnt;
            if (cnt >= 25) {
               flagWarningsEnabled = false;
               return;
            }

            ++flagWarningsCnt;
         }

         message = message + " This will be an error in some later FreeMarker version!";
         if (cnt + 1 == 25) {
            message = message + " [Will not log more regular expression flag problems until restart!]";
         }

         LOG.warn(message);
      }
   }

   static void checkNonRegexpFlags(String biName, long flags) throws _TemplateModelException {
      checkOnlyHasNonRegexpFlags(biName, flags, false);
   }

   static void checkOnlyHasNonRegexpFlags(String biName, long flags, boolean strict) throws _TemplateModelException {
      if (strict || flagWarningsEnabled) {
         String flag;
         if ((flags & RE_FLAG_MULTILINE) != 0L) {
            flag = "m";
         } else if ((flags & RE_FLAG_DOTALL) != 0L) {
            flag = "s";
         } else {
            if ((flags & RE_FLAG_COMMENTS) == 0L) {
               return;
            }

            flag = "c";
         }

         Object[] msg = new Object[]{"?", biName, " doesn't support the \"", flag, "\" flag without the \"r\" flag."};
         if (strict) {
            throw new _TemplateModelException(msg);
         } else {
            logFlagWarning((new _ErrorDescriptionBuilder(msg)).toString());
         }
      }
   }

   static {
      flagWarningsEnabled = LOG.isWarnEnabled();
      flagWarningsCntSync = new Object();
      patternCache = new MruCacheStorage(50, 150);
      RE_FLAG_CASE_INSENSITIVE = intFlagToLong(2);
      RE_FLAG_MULTILINE = intFlagToLong(8);
      RE_FLAG_COMMENTS = intFlagToLong(4);
      RE_FLAG_DOTALL = intFlagToLong(32);
   }

   private static class PatternCacheKey {
      private final String patternString;
      private final int flags;
      private final int hashCode;

      public PatternCacheKey(String patternString, int flags) {
         this.patternString = patternString;
         this.flags = flags;
         this.hashCode = patternString.hashCode() + 31 * flags;
      }

      public boolean equals(Object that) {
         if (!(that instanceof PatternCacheKey)) {
            return false;
         } else {
            PatternCacheKey thatPCK = (PatternCacheKey)that;
            return thatPCK.flags == this.flags && thatPCK.patternString.equals(this.patternString);
         }
      }

      public int hashCode() {
         return this.hashCode;
      }
   }
}
