package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Table
public class UpstreamServer extends BaseModel {
   String upstreamId;
   String server;
   Integer port;
   Integer weight;
   Integer failTimeout;
   Integer maxFails;
   Integer maxConns;
   @InitValue("none")
   String status;
   @JsonIgnore
   @InitValue("-1")
   Integer monitorStatus;

   public Integer getMaxConns() {
      return this.maxConns;
   }

   public void setMaxConns(Integer maxConns) {
      this.maxConns = maxConns;
   }

   public Integer getMonitorStatus() {
      return this.monitorStatus;
   }

   public void setMonitorStatus(Integer monitorStatus) {
      this.monitorStatus = monitorStatus;
   }

   public Integer getFailTimeout() {
      return this.failTimeout;
   }

   public void setFailTimeout(Integer failTimeout) {
      this.failTimeout = failTimeout;
   }

   public Integer getMaxFails() {
      return this.maxFails;
   }

   public void setMaxFails(Integer maxFails) {
      this.maxFails = maxFails;
   }

   public String getStatus() {
      return this.status;
   }

   public void setStatus(String status) {
      this.status = status;
   }

   public Integer getPort() {
      return this.port;
   }

   public void setPort(Integer port) {
      this.port = port;
   }

   public String getUpstreamId() {
      return this.upstreamId;
   }

   public void setUpstreamId(String upstreamId) {
      this.upstreamId = upstreamId;
   }

   public String getServer() {
      return this.server;
   }

   public void setServer(String server) {
      this.server = server;
   }

   public Integer getWeight() {
      return this.weight;
   }

   public void setWeight(Integer weight) {
      this.weight = weight;
   }
}
