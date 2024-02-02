package cn.hutool.http.useragent;

import cn.hutool.core.util.StrUtil;
import java.util.Iterator;

public class UserAgentParser {
   public static UserAgent parse(String userAgentString) {
      if (StrUtil.isBlank(userAgentString)) {
         return null;
      } else {
         UserAgent userAgent = new UserAgent();
         Browser browser = parseBrowser(userAgentString);
         userAgent.setBrowser(browser);
         userAgent.setVersion(browser.getVersion(userAgentString));
         Engine engine = parseEngine(userAgentString);
         userAgent.setEngine(engine);
         userAgent.setEngineVersion(engine.getVersion(userAgentString));
         OS os = parseOS(userAgentString);
         userAgent.setOs(os);
         userAgent.setOsVersion(os.getVersion(userAgentString));
         Platform platform = parsePlatform(userAgentString);
         userAgent.setPlatform(platform);
         userAgent.setMobile(platform.isMobile() || browser.isMobile());
         return userAgent;
      }
   }

   private static Browser parseBrowser(String userAgentString) {
      Iterator var1 = Browser.browers.iterator();

      Browser browser;
      do {
         if (!var1.hasNext()) {
            return Browser.Unknown;
         }

         browser = (Browser)var1.next();
      } while(!browser.isMatch(userAgentString));

      return browser;
   }

   private static Engine parseEngine(String userAgentString) {
      Iterator var1 = Engine.engines.iterator();

      Engine engine;
      do {
         if (!var1.hasNext()) {
            return Engine.Unknown;
         }

         engine = (Engine)var1.next();
      } while(!engine.isMatch(userAgentString));

      return engine;
   }

   private static OS parseOS(String userAgentString) {
      Iterator var1 = OS.oses.iterator();

      OS os;
      do {
         if (!var1.hasNext()) {
            return OS.Unknown;
         }

         os = (OS)var1.next();
      } while(!os.isMatch(userAgentString));

      return os;
   }

   private static Platform parsePlatform(String userAgentString) {
      Iterator var1 = Platform.platforms.iterator();

      Platform platform;
      do {
         if (!var1.hasNext()) {
            return Platform.Unknown;
         }

         platform = (Platform)var1.next();
      } while(!platform.isMatch(userAgentString));

      return platform;
   }
}
