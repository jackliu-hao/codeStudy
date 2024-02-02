/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import cn.hutool.db.Entity;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.LinkedHashSet;
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
/*    */ public class EntitySetHandler
/*    */   implements RsHandler<LinkedHashSet<Entity>>
/*    */ {
/*    */   private static final long serialVersionUID = 8191723216703506736L;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public static EntitySetHandler create() {
/* 25 */     return new EntitySetHandler();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntitySetHandler() {
/* 32 */     this(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntitySetHandler(boolean caseInsensitive) {
/* 41 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public LinkedHashSet<Entity> handle(ResultSet rs) throws SQLException {
/* 46 */     return HandleHelper.<LinkedHashSet<Entity>>handleRs(rs, new LinkedHashSet<>(), this.caseInsensitive);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\EntitySetHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */