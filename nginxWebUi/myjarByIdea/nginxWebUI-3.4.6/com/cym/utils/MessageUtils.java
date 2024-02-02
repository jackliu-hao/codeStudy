package com.cym.utils;

import com.cym.service.SettingService;
import java.util.Properties;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

@Component
public class MessageUtils {
   @Inject
   PropertiesUtils propertiesUtils;
   Properties properties = null;
   Properties propertiesEN = null;
   @Inject
   SettingService settingService;

   @Init
   private void ini() {
      this.propertiesEN = this.propertiesUtils.getPropertis("messages_en_US.properties");
      this.properties = this.propertiesUtils.getPropertis("messages.properties");
   }

   public String get(String msgKey) {
      return this.settingService.get("lang") != null && this.settingService.get("lang").equals("en_US") ? this.propertiesEN.getProperty(msgKey) : this.properties.getProperty(msgKey);
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public Properties getPropertiesEN() {
      return this.propertiesEN;
   }

   public void setPropertiesEN(Properties propertiesEN) {
      this.propertiesEN = propertiesEN;
   }
}
