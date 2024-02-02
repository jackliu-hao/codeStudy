package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class Location extends BaseModel {
   String serverId;
   String path;
   @InitValue("0")
   Integer type;
   @JsonIgnore
   String locationParamJson;
   String value;
   @InitValue("http")
   String upstreamType;
   String upstreamId;
   String upstreamPath;
   String rootPath;
   String rootPage;
   String rootType;
   @InitValue("1")
   Integer header;
   @InitValue("0")
   Integer websocket;
   String descr;

   public String getDescr() {
      return this.descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public String getUpstreamType() {
      return this.upstreamType;
   }

   public void setUpstreamType(String upstreamType) {
      this.upstreamType = upstreamType;
   }

   public Integer getWebsocket() {
      return this.websocket;
   }

   public void setWebsocket(Integer websocket) {
      this.websocket = websocket;
   }

   public Integer getHeader() {
      return this.header;
   }

   public void setHeader(Integer header) {
      this.header = header;
   }

   public String getRootType() {
      return this.rootType;
   }

   public void setRootType(String rootType) {
      this.rootType = rootType;
   }

   public String getRootPath() {
      return this.rootPath;
   }

   public void setRootPath(String rootPath) {
      this.rootPath = rootPath;
   }

   public String getRootPage() {
      return this.rootPage;
   }

   public void setRootPage(String rootPage) {
      this.rootPage = rootPage;
   }

   public String getUpstreamPath() {
      return this.upstreamPath;
   }

   public void setUpstreamPath(String upstreamPath) {
      this.upstreamPath = upstreamPath;
   }

   public String getLocationParamJson() {
      return this.locationParamJson;
   }

   public void setLocationParamJson(String locationParamJson) {
      this.locationParamJson = locationParamJson;
   }

   public String getUpstreamId() {
      return this.upstreamId;
   }

   public void setUpstreamId(String upstreamId) {
      this.upstreamId = upstreamId;
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getServerId() {
      return this.serverId;
   }

   public void setServerId(String serverId) {
      this.serverId = serverId;
   }

   public Integer getType() {
      return this.type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
