package cn.hutool.http.useragent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import java.util.List;
import java.util.regex.Pattern;

public class Engine extends UserAgentInfo {
   private static final long serialVersionUID = 1L;
   public static final Engine Unknown = new Engine("Unknown", (String)null);
   public static final List<Engine> engines = CollUtil.newArrayList((Object[])(new Engine("Trident", "trident"), new Engine("Webkit", "webkit"), new Engine("Chrome", "chrome"), new Engine("Opera", "opera"), new Engine("Presto", "presto"), new Engine("Gecko", "gecko"), new Engine("KHTML", "khtml"), new Engine("Konqueror", "konqueror"), new Engine("MIDP", "MIDP")));
   private final Pattern versionPattern;

   public Engine(String name, String regex) {
      super(name, regex);
      this.versionPattern = Pattern.compile(name + "[/\\- ]([\\d\\w.\\-]+)", 2);
   }

   public String getVersion(String userAgentString) {
      return this.isUnknown() ? null : ReUtil.getGroup1((Pattern)this.versionPattern, userAgentString);
   }
}
