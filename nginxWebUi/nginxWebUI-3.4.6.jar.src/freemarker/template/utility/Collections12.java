/*    */ package freemarker.template.utility;
/*    */ 
/*    */ import freemarker.template.EmptyMap;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ @Deprecated
/*    */ public class Collections12
/*    */ {
/* 35 */   public static final Map EMPTY_MAP = (Map)new EmptyMap();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Map singletonMap(Object key, Object value) {
/* 41 */     return Collections.singletonMap(key, value);
/*    */   }
/*    */   
/*    */   public static List singletonList(Object o) {
/* 45 */     return Collections.singletonList(o);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\templat\\utility\Collections12.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */