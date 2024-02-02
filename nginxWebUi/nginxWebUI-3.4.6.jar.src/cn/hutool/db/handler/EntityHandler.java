/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import cn.hutool.db.Entity;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.ResultSetMetaData;
/*    */ import java.sql.SQLException;
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
/*    */ public class EntityHandler
/*    */   implements RsHandler<Entity>
/*    */ {
/*    */   private static final long serialVersionUID = -8742432871908355992L;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public static EntityHandler create() {
/* 26 */     return new EntityHandler();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntityHandler() {
/* 33 */     this(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntityHandler(boolean caseInsensitive) {
/* 42 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public Entity handle(ResultSet rs) throws SQLException {
/* 47 */     ResultSetMetaData meta = rs.getMetaData();
/* 48 */     int columnCount = meta.getColumnCount();
/*    */     
/* 50 */     return rs.next() ? HandleHelper.handleRow(columnCount, meta, rs, this.caseInsensitive) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\EntityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */