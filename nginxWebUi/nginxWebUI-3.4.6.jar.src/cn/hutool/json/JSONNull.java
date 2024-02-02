/*    */ package cn.hutool.json;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public class JSONNull
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 2633815155870764938L;
/* 21 */   public static final JSONNull NULL = new JSONNull();
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
/*    */   public boolean equals(Object object) {
/* 33 */     return (object == null || object == this);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return "null";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONNull.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */