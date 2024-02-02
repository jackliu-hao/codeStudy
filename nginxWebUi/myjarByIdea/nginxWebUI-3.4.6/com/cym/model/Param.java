package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Param extends BaseModel {
   String serverId;
   String locationId;
   String upstreamId;
   @JsonIgnore
   String templateId;
   String name;
   String value;
   @JsonIgnore
   String templateValue;
   @JsonIgnore
   String templateName;

   public String getTemplateValue() {
      return this.templateValue;
   }

   public void setTemplateValue(String templateValue) {
      this.templateValue = templateValue;
   }

   public String getTemplateName() {
      return this.templateName;
   }

   public void setTemplateName(String templateName) {
      this.templateName = templateName;
   }

   public String getTemplateId() {
      return this.templateId;
   }

   public void setTemplateId(String templateId) {
      this.templateId = templateId;
   }

   public String getUpstreamId() {
      return this.upstreamId;
   }

   public void setUpstreamId(String upstreamId) {
      this.upstreamId = upstreamId;
   }

   public String getServerId() {
      return this.serverId;
   }

   public void setServerId(String serverId) {
      this.serverId = serverId;
   }

   public String getLocationId() {
      return this.locationId;
   }

   public void setLocationId(String locationId) {
      this.locationId = locationId;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
