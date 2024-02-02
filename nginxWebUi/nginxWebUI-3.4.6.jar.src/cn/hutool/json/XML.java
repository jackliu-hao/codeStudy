/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.json.xml.JSONXMLParser;
/*     */ import cn.hutool.json.xml.JSONXMLSerializer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XML
/*     */ {
/*  19 */   public static final Character AMP = Character.valueOf('&');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  24 */   public static final Character APOS = Character.valueOf('\'');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  29 */   public static final Character BANG = Character.valueOf('!');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  34 */   public static final Character EQ = Character.valueOf('=');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   public static final Character GT = Character.valueOf('>');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   public static final Character LT = Character.valueOf('<');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public static final Character QUEST = Character.valueOf('?');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  54 */   public static final Character QUOT = Character.valueOf('"');
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   public static final Character SLASH = Character.valueOf('/');
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JSONObject toJSONObject(String string) throws JSONException {
/*  71 */     return toJSONObject(string, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JSONObject toJSONObject(String string, boolean keepStrings) throws JSONException {
/*  86 */     return toJSONObject(new JSONObject(), string, keepStrings);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JSONObject toJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
/* 101 */     JSONXMLParser.parseJSONObject(jo, xmlStr, keepStrings);
/* 102 */     return jo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toXml(Object object) throws JSONException {
/* 113 */     return toXml(object, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toXml(Object object, String tagName) throws JSONException {
/* 125 */     return toXml(object, tagName, new String[] { "content" });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toXml(Object object, String tagName, String... contentKeys) throws JSONException {
/* 138 */     return JSONXMLSerializer.toXml(object, tagName, contentKeys);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\XML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */