package cn.hutool.json;

import cn.hutool.core.bean.BeanUtil;

public class JSONSupport implements JSONString, JSONBeanParser<JSON> {
   public void parse(String jsonString) {
      this.parse((JSON)(new JSONObject(jsonString)));
   }

   public void parse(JSON json) {
      JSONSupport support = (JSONSupport)JSONConverter.jsonToBean(this.getClass(), json, false);
      BeanUtil.copyProperties(support, (Object)this, (String[])());
   }

   public JSONObject toJSON() {
      return new JSONObject(this);
   }

   public String toJSONString() {
      return this.toJSON().toString();
   }

   public String toPrettyString() {
      return this.toJSON().toStringPretty();
   }

   public String toString() {
      return this.toJSONString();
   }
}
