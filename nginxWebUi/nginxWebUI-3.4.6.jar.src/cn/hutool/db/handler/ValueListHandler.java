/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ValueListHandler
/*    */   implements RsHandler<List<List<Object>>>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public static ValueListHandler create() {
/* 21 */     return new ValueListHandler();
/*    */   }
/*    */ 
/*    */   
/*    */   public List<List<Object>> handle(ResultSet rs) throws SQLException {
/* 26 */     ArrayList<List<Object>> result = new ArrayList<>();
/* 27 */     while (rs.next()) {
/* 28 */       result.add(HandleHelper.handleRowToList(rs));
/*    */     }
/* 30 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\ValueListHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */