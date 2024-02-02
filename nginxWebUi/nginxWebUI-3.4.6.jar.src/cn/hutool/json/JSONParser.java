/*     */ package cn.hutool.json;
/*     */ 
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.lang.mutable.Mutable;
/*     */ import cn.hutool.core.lang.mutable.MutablePair;
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
/*     */ public class JSONParser
/*     */ {
/*     */   private final JSONTokener tokener;
/*     */   
/*     */   public static JSONParser of(JSONTokener tokener) {
/*  22 */     return new JSONParser(tokener);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONParser(JSONTokener tokener) {
/*  33 */     this.tokener = tokener;
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
/*     */   public void parseTo(JSONObject jsonObject, Filter<MutablePair<String, Object>> filter) {
/*  45 */     JSONTokener tokener = this.tokener;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  50 */     if (tokener.nextClean() != '{') {
/*  51 */       throw tokener.syntaxError("A JSONObject text must begin with '{'");
/*     */     }
/*     */     while (true) {
/*  54 */       char c = tokener.nextClean();
/*  55 */       switch (c) {
/*     */         case '\000':
/*  57 */           throw tokener.syntaxError("A JSONObject text must end with '}'");
/*     */         case '}':
/*     */           return;
/*     */       } 
/*  61 */       tokener.back();
/*  62 */       String key = tokener.nextValue().toString();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  67 */       c = tokener.nextClean();
/*  68 */       if (c != ':') {
/*  69 */         throw tokener.syntaxError("Expected a ':' after a key");
/*     */       }
/*     */       
/*  72 */       jsonObject.setOnce(key, tokener.nextValue(), filter);
/*     */ 
/*     */ 
/*     */       
/*  76 */       switch (tokener.nextClean()) {
/*     */         case ',':
/*     */         case ';':
/*  79 */           if (tokener.nextClean() == '}') {
/*     */             return;
/*     */           }
/*  82 */           tokener.back(); continue;
/*     */         case '}':
/*     */           return;
/*     */       }  break;
/*     */     } 
/*  87 */     throw tokener.syntaxError("Expected a ',' or '}'");
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
/*     */   public void parseTo(JSONArray jsonArray, Filter<Mutable<Object>> filter) {
/*  99 */     JSONTokener x = this.tokener;
/*     */     
/* 101 */     if (x.nextClean() != '[') {
/* 102 */       throw x.syntaxError("A JSONArray text must start with '['");
/*     */     }
/* 104 */     if (x.nextClean() != ']') {
/* 105 */       x.back();
/*     */       while (true) {
/* 107 */         if (x.nextClean() == ',') {
/* 108 */           x.back();
/* 109 */           jsonArray.addRaw(JSONNull.NULL, filter);
/*     */         } else {
/* 111 */           x.back();
/* 112 */           jsonArray.addRaw(x.nextValue(), filter);
/*     */         } 
/* 114 */         switch (x.nextClean()) {
/*     */           case ',':
/* 116 */             if (x.nextClean() == ']') {
/*     */               return;
/*     */             }
/* 119 */             x.back(); continue;
/*     */           case ']':
/*     */             return;
/*     */         }  break;
/*     */       } 
/* 124 */       throw x.syntaxError("Expected a ',' or ']'");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */