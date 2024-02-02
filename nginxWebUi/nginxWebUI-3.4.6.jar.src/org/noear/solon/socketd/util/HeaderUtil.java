/*    */ package org.noear.solon.socketd.util;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.noear.solon.Utils;
/*    */ import org.noear.solon.core.NvMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HeaderUtil
/*    */ {
/*    */   public static String encodeHeaderMap(Map<String, String> headers) {
/* 16 */     StringBuilder header = new StringBuilder();
/* 17 */     if (headers != null) {
/* 18 */       headers.forEach((k, v) -> header.append(k).append("=").append(v).append("&"));
/*    */ 
/*    */ 
/*    */       
/* 22 */       if (header.length() > 0) {
/* 23 */         header.setLength(header.length() - 1);
/*    */       }
/*    */     } 
/*    */     
/* 27 */     return header.toString();
/*    */   }
/*    */   
/*    */   public static Map<String, String> decodeHeaderMap(String header) {
/* 31 */     NvMap headerMap = new NvMap();
/*    */     
/* 33 */     if (Utils.isNotEmpty(header)) {
/* 34 */       String[] ss = header.split("&");
/* 35 */       for (String s : ss) {
/* 36 */         String[] kv = s.split("=");
/* 37 */         if (kv.length == 2) {
/* 38 */           headerMap.put(kv[0], kv[1]);
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 43 */     return (Map<String, String>)headerMap;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socket\\util\HeaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */