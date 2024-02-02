package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;

@Table
public class Remote extends BaseModel {
   String protocol;
   String ip;
   Integer port;
   @InitValue("0")
   Integer status;
   String creditKey;
   String name;
   String pass;
   String version;
   String system;
   String descr;
   @InitValue("0")
   Integer monitor;
   String parentId;
   Integer type;
   Integer nginx;

   public Integer getMonitor() {
      return this.monitor;
   }

   public void setMonitor(Integer monitor) {
      this.monitor = monitor;
   }

   public Integer getNginx() {
      return this.nginx;
   }

   public void setNginx(Integer nginx) {
      this.nginx = nginx;
   }

   public Integer getType() {
      return this.type;
   }

   public void setType(Integer type) {
      this.type = type;
   }

   public String getParentId() {
      return this.parentId;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   public String getDescr() {
      return this.descr;
   }

   public void setDescr(String descr) {
      this.descr = descr;
   }

   public String getSystem() {
      return this.system;
   }

   public void setSystem(String system) {
      this.system = system;
   }

   public String getVersion() {
      return this.version;
   }

   public void setVersion(String version) {
      this.version = version;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public void setProtocol(String protocol) {
      this.protocol = protocol;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getPass() {
      return this.pass;
   }

   public void setPass(String pass) {
      this.pass = pass;
   }

   public Integer getStatus() {
      return this.status;
   }

   public void setStatus(Integer status) {
      this.status = status;
   }

   public Integer getPort() {
      return this.port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public String getIp() {
      return this.ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public String getCreditKey() {
      return this.creditKey;
   }

   public void setCreditKey(String creditKey) {
      this.creditKey = creditKey;
   }
}
