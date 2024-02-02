package cn.hutool.setting.profile;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Profile implements Serializable {
   private static final long serialVersionUID = -4189955219454008744L;
   public static final String DEFAULT_PROFILE = "default";
   private String profile;
   private Charset charset;
   private boolean useVar;
   private final Map<String, Setting> settingMap;

   public Profile() {
      this("default");
   }

   public Profile(String profile) {
      this(profile, Setting.DEFAULT_CHARSET, false);
   }

   public Profile(String profile, Charset charset, boolean useVar) {
      this.settingMap = new ConcurrentHashMap();
      this.profile = profile;
      this.charset = charset;
      this.useVar = useVar;
   }

   public Setting getSetting(String name) {
      String nameForProfile = this.fixNameForProfile(name);
      Setting setting = (Setting)this.settingMap.get(nameForProfile);
      if (null == setting) {
         setting = new Setting(nameForProfile, this.charset, this.useVar);
         this.settingMap.put(nameForProfile, setting);
      }

      return setting;
   }

   public Profile setProfile(String profile) {
      this.profile = profile;
      return this;
   }

   public Profile setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public Profile setUseVar(boolean useVar) {
      this.useVar = useVar;
      return this;
   }

   public Profile clear() {
      this.settingMap.clear();
      return this;
   }

   private String fixNameForProfile(String name) {
      Assert.notBlank(name, "Setting name must be not blank !");
      String actralProfile = StrUtil.nullToEmpty(this.profile);
      return !name.contains(".") ? StrUtil.format("{}/{}.setting", new Object[]{actralProfile, name}) : StrUtil.format("{}/{}", new Object[]{actralProfile, name});
   }
}
