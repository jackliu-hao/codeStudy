/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import cn.hutool.db.Entity;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EntityListHandler
/*    */   implements RsHandler<List<Entity>>
/*    */ {
/*    */   private static final long serialVersionUID = -2846240126316979895L;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public static EntityListHandler create() {
/* 26 */     return new EntityListHandler();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntityListHandler() {
/* 33 */     this(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public EntityListHandler(boolean caseInsensitive) {
/* 42 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Entity> handle(ResultSet rs) throws SQLException {
/* 47 */     return HandleHelper.<List<Entity>>handleRs(rs, new ArrayList<>(), this.caseInsensitive);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\EntityListHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */