package com.cym.sqlhelper.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public class BaseModel implements Serializable {
   String id;
   @JsonIgnore
   Long createTime;
   @JsonIgnore
   Long updateTime;

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Long getCreateTime() {
      return this.createTime;
   }

   public void setCreateTime(Long createTime) {
      this.createTime = createTime;
   }

   public Long getUpdateTime() {
      return this.updateTime;
   }

   public void setUpdateTime(Long updateTime) {
      this.updateTime = updateTime;
   }
}
