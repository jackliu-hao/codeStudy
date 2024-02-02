package cn.hutool.setting.profile;

import cn.hutool.core.lang.Singleton;
import cn.hutool.setting.Setting;

public class GlobalProfile {
   private GlobalProfile() {
   }

   public static Profile setProfile(String profile) {
      return (Profile)Singleton.get(Profile.class, profile);
   }

   public static Setting getSetting(String settingName) {
      return ((Profile)Singleton.get(Profile.class)).getSetting(settingName);
   }
}
