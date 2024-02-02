/*    */ package cn.hutool.json;
/*    */ 
/*    */ import cn.hutool.core.bean.BeanUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONSupport
/*    */   implements JSONString, JSONBeanParser<JSON>
/*    */ {
/*    */   public void parse(String jsonString) {
/* 19 */     parse(new JSONObject(jsonString));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void parse(JSON json) {
/* 29 */     JSONSupport support = JSONConverter.<JSONSupport>jsonToBean(getClass(), json, false);
/* 30 */     BeanUtil.copyProperties(support, this, new String[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JSONObject toJSON() {
/* 37 */     return new JSONObject(this);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toJSONString() {
/* 42 */     return toJSON().toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toPrettyString() {
/* 51 */     return toJSON().toStringPretty();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return toJSONString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */