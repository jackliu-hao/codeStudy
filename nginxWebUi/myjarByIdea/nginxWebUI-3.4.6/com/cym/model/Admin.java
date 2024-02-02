package com.cym.model;

import com.cym.sqlhelper.bean.BaseModel;
import com.cym.sqlhelper.config.InitValue;
import com.cym.sqlhelper.config.Table;

@Table
public class Admin extends BaseModel {
   String name;
   String pass;
   String key;
   @InitValue("false")
   Boolean auth;
   @InitValue("false")
   Boolean api;
   String token;
   @InitValue("0")
   Integer type;

   public String getToken() {
      return this.token;
   }

   public void setToken(String token) {
      this.token = token;
   }

   public Boolean getApi() {
      return this.api;
   }

   public void setApi(Boolean api) {
      this.api = api;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public Boolean getAuth() {
      return this.auth;
   }

   public void setAuth(Boolean auth) {
      this.auth = auth;
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

   public Integer getType() {
      return this.type;
   }

   public void setType(Integer type) {
      this.type = type;
   }
}
