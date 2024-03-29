package com.cym.ext;

import java.util.List;

public class DataGroup {
   List<KeyValue> pv;
   List<KeyValue> uv;
   List<KeyValue> status;
   List<KeyValue> browser;
   List<KeyValue> httpReferer;

   public List<KeyValue> getHttpReferer() {
      return this.httpReferer;
   }

   public void setHttpReferer(List<KeyValue> httpReferer) {
      this.httpReferer = httpReferer;
   }

   public List<KeyValue> getBrowser() {
      return this.browser;
   }

   public void setBrowser(List<KeyValue> browser) {
      this.browser = browser;
   }

   public List<KeyValue> getStatus() {
      return this.status;
   }

   public void setStatus(List<KeyValue> status) {
      this.status = status;
   }

   public List<KeyValue> getPv() {
      return this.pv;
   }

   public void setPv(List<KeyValue> pv) {
      this.pv = pv;
   }

   public List<KeyValue> getUv() {
      return this.uv;
   }

   public void setUv(List<KeyValue> uv) {
      this.uv = uv;
   }
}
