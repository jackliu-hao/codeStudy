package cn.hutool.jwt;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class Claims implements Serializable {
   private static final long serialVersionUID = 1L;
   private final JSONConfig CONFIG = JSONConfig.create().setDateFormat("#sss");
   private JSONObject claimJSON;

   protected void setClaim(String name, Object value) {
      this.init();
      Assert.notNull(name, "Name must be not null!");
      if (value == null) {
         this.claimJSON.remove(name);
      } else {
         this.claimJSON.set(name, value);
      }
   }

   protected void putAll(Map<String, ?> headerClaims) {
      if (MapUtil.isNotEmpty(headerClaims)) {
         Iterator var2 = headerClaims.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry<String, ?> entry = (Map.Entry)var2.next();
            this.setClaim((String)entry.getKey(), entry.getValue());
         }
      }

   }

   public Object getClaim(String name) {
      this.init();
      return this.claimJSON.getObj(name);
   }

   public JSONObject getClaimsJson() {
      this.init();
      return this.claimJSON;
   }

   public void parse(String tokenPart, Charset charset) {
      this.claimJSON = JSONUtil.parseObj(Base64.decodeStr(tokenPart, (Charset)charset), this.CONFIG);
   }

   public String toString() {
      this.init();
      return this.claimJSON.toString();
   }

   private void init() {
      if (null == this.claimJSON) {
         this.claimJSON = new JSONObject(this.CONFIG);
      }

   }
}
