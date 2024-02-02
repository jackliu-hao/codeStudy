package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;

@Table
public class Bak extends BaseModel {
   String time;
   String content;

   public String getContent() {
      return this.content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getTime() {
      return this.time;
   }

   public void setTime(String time) {
      this.time = time;
   }
}
