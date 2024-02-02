package com.cym.service;

import com.cym.model.Setting;
import com.cym.sqlhelper.utils.ConditionAndWrapper;
import com.cym.sqlhelper.utils.ConditionWrapper;
import com.cym.sqlhelper.utils.SqlHelper;
import org.noear.solon.annotation.Inject;
import org.noear.solon.aspect.annotation.Service;

@Service
public class SettingService {
   @Inject
   SqlHelper sqlHelper;

   public void set(String key, String value) {
      Setting setting = (Setting)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"key", key), Setting.class);
      if (setting == null) {
         setting = new Setting();
      }

      setting.setKey(key);
      setting.setValue(value);
      this.sqlHelper.insertOrUpdate(setting);
   }

   public String get(String key) {
      Setting setting = (Setting)this.sqlHelper.findOneByQuery((ConditionWrapper)(new ConditionAndWrapper()).eq((String)"key", key), Setting.class);
      return setting == null ? null : setting.getValue();
   }

   public void remove(String key) {
      this.sqlHelper.deleteByQuery((new ConditionAndWrapper()).eq((String)"key", key), Setting.class);
   }
}
