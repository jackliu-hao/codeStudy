/*    */ package org.noear.solon.core;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.noear.solon.ext.LinkedCaseInsensitiveMap;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NdMap
/*    */   extends LinkedCaseInsensitiveMap<Object>
/*    */ {
/*    */   public NdMap() {}
/*    */   
/*    */   public NdMap(Map map) {
/* 25 */     map.forEach((k, v) -> put(k.toString(), v));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\NdMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */