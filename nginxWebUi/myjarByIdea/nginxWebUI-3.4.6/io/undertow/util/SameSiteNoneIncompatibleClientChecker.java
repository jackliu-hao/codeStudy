package io.undertow.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SameSiteNoneIncompatibleClientChecker {
   private static final Pattern IOS_PATTERN = Pattern.compile("\\(iP.+; CPU .*OS (\\d+)[_\\d]*.*\\) AppleWebKit\\/");
   private static final Pattern MACOSX_PATTERN = Pattern.compile("\\(Macintosh;.*Mac OS X (\\d+)_(\\d+)[_\\d]*.*\\) AppleWebKit\\/");
   private static final Pattern SAFARI_PATTERN = Pattern.compile("Version\\/.* Safari\\/");
   private static final Pattern MAC_EMBEDDED_BROWSER_PATTERN = Pattern.compile("^Mozilla\\/[\\.\\d]+ \\(Macintosh;.*Mac OS X [_\\d]+\\) AppleWebKit\\/[\\.\\d]+ \\(KHTML, like Gecko\\)$");
   private static final Pattern CHROMIUM_PATTERN = Pattern.compile("Chrom(e|ium)");
   private static final Pattern CHROMIUM_VERSION_PATTERN = Pattern.compile("Chrom[^ \\/]+\\/(\\d+)[\\.\\d]* ");
   private static final Pattern UC_BROWSER_VERSION_PATTERN = Pattern.compile("UCBrowser\\/(\\d+)\\.(\\d+)\\.(\\d+)[\\.\\d]* ");

   public static boolean shouldSendSameSiteNone(String useragent) {
      return !isSameSiteNoneIncompatible(useragent);
   }

   public static boolean isSameSiteNoneIncompatible(String useragent) {
      if (useragent != null && !useragent.isEmpty()) {
         return hasWebKitSameSiteBug(useragent) || dropsUnrecognizedSameSiteCookies(useragent);
      } else {
         return false;
      }
   }

   private static boolean hasWebKitSameSiteBug(String useragent) {
      return isIosVersion(12, useragent) || isMacosxVersion(10, 14, useragent) && (isSafari(useragent) || isMacEmbeddedBrowser(useragent));
   }

   private static boolean dropsUnrecognizedSameSiteCookies(String useragent) {
      if (isUcBrowser(useragent)) {
         return !isUcBrowserVersionAtLeast(12, 13, 2, useragent);
      } else {
         return isChromiumBased(useragent) && isChromiumVersionAtLeast(51, useragent) && !isChromiumVersionAtLeast(67, useragent);
      }
   }

   private static boolean isIosVersion(int major, String useragent) {
      Matcher m = IOS_PATTERN.matcher(useragent);
      return m.find() ? String.valueOf(major).equals(m.group(1)) : false;
   }

   private static boolean isMacosxVersion(int major, int minor, String useragent) {
      Matcher m = MACOSX_PATTERN.matcher(useragent);
      if (!m.find()) {
         return false;
      } else {
         return String.valueOf(major).equals(m.group(1)) && String.valueOf(minor).equals(m.group(2));
      }
   }

   private static boolean isSafari(String useragent) {
      return SAFARI_PATTERN.matcher(useragent).find() && !isChromiumBased(useragent);
   }

   private static boolean isMacEmbeddedBrowser(String useragent) {
      return MAC_EMBEDDED_BROWSER_PATTERN.matcher(useragent).find();
   }

   private static boolean isChromiumBased(String useragent) {
      return CHROMIUM_PATTERN.matcher(useragent).find();
   }

   private static boolean isChromiumVersionAtLeast(int major, String useragent) {
      Matcher m = CHROMIUM_VERSION_PATTERN.matcher(useragent);
      if (m.find()) {
         int version = Integer.parseInt(m.group(1));
         return version >= major;
      } else {
         return false;
      }
   }

   static boolean isUcBrowser(String useragent) {
      return useragent.contains("UCBrowser/");
   }

   private static boolean isUcBrowserVersionAtLeast(int major, int minor, int build, String useragent) {
      Matcher m = UC_BROWSER_VERSION_PATTERN.matcher(useragent);
      if (m.find()) {
         int major_version = Integer.parseInt(m.group(1));
         int minor_version = Integer.parseInt(m.group(2));
         int build_version = Integer.parseInt(m.group(3));
         if (major_version != major) {
            return major_version > major;
         } else if (minor_version != minor) {
            return minor_version > minor;
         } else {
            return build_version >= build;
         }
      } else {
         return false;
      }
   }
}
