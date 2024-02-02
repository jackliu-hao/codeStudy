/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringHandler
/*    */   implements RsHandler<String>
/*    */ {
/*    */   private static final long serialVersionUID = -5296733366845720383L;
/*    */   
/*    */   public static StringHandler create() {
/* 19 */     return new StringHandler();
/*    */   }
/*    */ 
/*    */   
/*    */   public String handle(ResultSet rs) throws SQLException {
/* 24 */     return rs.next() ? rs.getString(1) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\StringHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */