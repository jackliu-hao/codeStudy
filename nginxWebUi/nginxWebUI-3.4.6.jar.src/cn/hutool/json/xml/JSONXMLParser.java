/*     */ package cn.hutool.json.xml;
/*     */ 
/*     */ import cn.hutool.json.InternalJSONUtil;
/*     */ import cn.hutool.json.JSONException;
/*     */ import cn.hutool.json.JSONObject;
/*     */ import cn.hutool.json.XML;
/*     */ import cn.hutool.json.XMLTokener;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JSONXMLParser
/*     */ {
/*     */   public static void parseJSONObject(JSONObject jo, String xmlStr, boolean keepStrings) throws JSONException {
/*  27 */     XMLTokener x = new XMLTokener(xmlStr, jo.getConfig());
/*  28 */     while (x.more() && x.skipPast("<")) {
/*  29 */       parse(x, jo, null, keepStrings);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean parse(XMLTokener x, JSONObject context, String name, boolean keepStrings) throws JSONException {
/*  50 */     Object token = x.nextToken();
/*     */     
/*  52 */     if (token == XML.BANG) {
/*  53 */       char c = x.next();
/*  54 */       if (c == '-') {
/*  55 */         if (x.next() == '-') {
/*  56 */           x.skipPast("-->");
/*  57 */           return false;
/*     */         } 
/*  59 */         x.back();
/*  60 */       } else if (c == '[') {
/*  61 */         token = x.nextToken();
/*  62 */         if ("CDATA".equals(token) && 
/*  63 */           x.next() == '[') {
/*  64 */           String string = x.nextCDATA();
/*  65 */           if (string.length() > 0) {
/*  66 */             context.accumulate("content", string);
/*     */           }
/*  68 */           return false;
/*     */         } 
/*     */         
/*  71 */         throw x.syntaxError("Expected 'CDATA['");
/*     */       } 
/*  73 */       int i = 1;
/*     */       while (true)
/*  75 */       { token = x.nextMeta();
/*  76 */         if (token == null)
/*  77 */           throw x.syntaxError("Missing '>' after '<!'."); 
/*  78 */         if (token == XML.LT) {
/*  79 */           i++;
/*  80 */         } else if (token == XML.GT) {
/*  81 */           i--;
/*     */         } 
/*  83 */         if (i <= 0)
/*  84 */           return false;  } 
/*  85 */     }  if (token == XML.QUEST) {
/*     */ 
/*     */       
/*  88 */       x.skipPast("?>");
/*  89 */       return false;
/*  90 */     }  if (token == XML.SLASH) {
/*     */ 
/*     */ 
/*     */       
/*  94 */       token = x.nextToken();
/*  95 */       if (name == null) {
/*  96 */         throw x.syntaxError("Mismatched close tag " + token);
/*     */       }
/*  98 */       if (!token.equals(name)) {
/*  99 */         throw x.syntaxError("Mismatched " + name + " and " + token);
/*     */       }
/* 101 */       if (x.nextToken() != XML.GT) {
/* 102 */         throw x.syntaxError("Misshaped close tag");
/*     */       }
/* 104 */       return true;
/*     */     } 
/* 106 */     if (token instanceof Character) {
/* 107 */       throw x.syntaxError("Misshaped tag");
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 112 */     String tagName = (String)token;
/* 113 */     token = null;
/* 114 */     JSONObject jsonobject = new JSONObject();
/*     */     while (true) {
/* 116 */       if (token == null) {
/* 117 */         token = x.nextToken();
/*     */       }
/*     */ 
/*     */       
/* 121 */       if (token instanceof String) {
/* 122 */         String string = (String)token;
/* 123 */         token = x.nextToken();
/* 124 */         if (token == XML.EQ) {
/* 125 */           token = x.nextToken();
/* 126 */           if (!(token instanceof String)) {
/* 127 */             throw x.syntaxError("Missing value");
/*     */           }
/* 129 */           jsonobject.accumulate(string, keepStrings ? token : InternalJSONUtil.stringToValue((String)token));
/* 130 */           token = null; continue;
/*     */         } 
/* 132 */         jsonobject.accumulate(string, ""); continue;
/*     */       }  break;
/*     */     } 
/* 135 */     if (token == XML.SLASH) {
/*     */       
/* 137 */       if (x.nextToken() != XML.GT) {
/* 138 */         throw x.syntaxError("Misshaped tag");
/*     */       }
/* 140 */       if (jsonobject.size() > 0) {
/* 141 */         context.accumulate(tagName, jsonobject);
/*     */       } else {
/* 143 */         context.accumulate(tagName, "");
/*     */       } 
/* 145 */       return false;
/*     */     } 
/* 147 */     if (token == XML.GT) {
/*     */       while (true) {
/*     */         
/* 150 */         token = x.nextContent();
/* 151 */         if (token == null) {
/* 152 */           if (tagName != null) {
/* 153 */             throw x.syntaxError("Unclosed tag " + tagName);
/*     */           }
/* 155 */           return false;
/* 156 */         }  if (token instanceof String) {
/* 157 */           String string = (String)token;
/* 158 */           if (string.length() > 0)
/* 159 */             jsonobject.accumulate("content", keepStrings ? token : InternalJSONUtil.stringToValue(string)); 
/*     */           continue;
/*     */         } 
/* 162 */         if (token == XML.LT)
/*     */         {
/* 164 */           if (parse(x, jsonobject, tagName, keepStrings)) {
/* 165 */             if (jsonobject.size() == 0) {
/* 166 */               context.accumulate(tagName, "");
/* 167 */             } else if (jsonobject.size() == 1 && jsonobject.get("content") != null) {
/* 168 */               context.accumulate(tagName, jsonobject.get("content"));
/*     */             } else {
/* 170 */               context.accumulate(tagName, jsonobject);
/*     */             } 
/* 172 */             return false;
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/* 177 */     throw x.syntaxError("Misshaped tag");
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\xml\JSONXMLParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */