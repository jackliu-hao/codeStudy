package cn.hutool.http;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public enum GlobalHeaders {
   INSTANCE;

   final Map<String, List<String>> headers = new HashMap();

   private GlobalHeaders() {
      this.putDefault(false);
   }

   public GlobalHeaders putDefault(boolean isReset) {
      System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
      System.setProperty("jdk.tls.allowUnsafeServerCertChange", "true");
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
      if (isReset) {
         this.headers.clear();
      }

      this.header(Header.ACCEPT, "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", true);
      this.header(Header.ACCEPT_ENCODING, "gzip, deflate", true);
      this.header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8", true);
      this.header(Header.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 Hutool", true);
      return this;
   }

   public String header(String name) {
      List<String> values = this.headerList(name);
      return CollectionUtil.isEmpty(values) ? null : (String)values.get(0);
   }

   public List<String> headerList(String name) {
      return StrUtil.isBlank(name) ? null : (List)this.headers.get(name.trim());
   }

   public String header(Header name) {
      return null == name ? null : this.header(name.toString());
   }

   public synchronized GlobalHeaders header(String name, String value, boolean isOverride) {
      if (null != name && null != value) {
         List<String> values = (List)this.headers.get(name.trim());
         if (!isOverride && !CollectionUtil.isEmpty(values)) {
            values.add(value.trim());
         } else {
            ArrayList<String> valueList = new ArrayList();
            valueList.add(value);
            this.headers.put(name.trim(), valueList);
         }
      }

      return this;
   }

   public GlobalHeaders header(Header name, String value, boolean isOverride) {
      return this.header(name.toString(), value, isOverride);
   }

   public GlobalHeaders header(Header name, String value) {
      return this.header(name.toString(), value, true);
   }

   public GlobalHeaders header(String name, String value) {
      return this.header(name, value, true);
   }

   public GlobalHeaders header(Map<String, List<String>> headers) {
      if (MapUtil.isEmpty(headers)) {
         return this;
      } else {
         Iterator var3 = headers.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)var3.next();
            String name = (String)entry.getKey();
            Iterator var5 = ((List)entry.getValue()).iterator();

            while(var5.hasNext()) {
               String value = (String)var5.next();
               this.header(name, StrUtil.nullToEmpty(value), false);
            }
         }

         return this;
      }
   }

   public synchronized GlobalHeaders removeHeader(String name) {
      if (name != null) {
         this.headers.remove(name.trim());
      }

      return this;
   }

   public GlobalHeaders removeHeader(Header name) {
      return this.removeHeader(name.toString());
   }

   public Map<String, List<String>> headers() {
      return Collections.unmodifiableMap(this.headers);
   }

   public synchronized GlobalHeaders clearHeaders() {
      this.headers.clear();
      return this;
   }
}
