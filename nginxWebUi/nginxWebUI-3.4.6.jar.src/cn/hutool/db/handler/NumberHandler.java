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
/*    */ public class NumberHandler
/*    */   implements RsHandler<Number>
/*    */ {
/*    */   private static final long serialVersionUID = 4081498054379705596L;
/*    */   
/*    */   public static NumberHandler create() {
/* 19 */     return new NumberHandler();
/*    */   }
/*    */ 
/*    */   
/*    */   public Number handle(ResultSet rs) throws SQLException {
/* 24 */     return (null != rs && rs.next()) ? rs.getBigDecimal(1) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\NumberHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */