/*    */ package cn.hutool.db.handler;
/*    */ 
/*    */ import cn.hutool.db.Entity;
/*    */ import cn.hutool.db.PageResult;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PageResultHandler
/*    */   implements RsHandler<PageResult<Entity>>
/*    */ {
/*    */   private static final long serialVersionUID = -1474161855834070108L;
/*    */   private final PageResult<Entity> pageResult;
/*    */   private final boolean caseInsensitive;
/*    */   
/*    */   public static PageResultHandler create(PageResult<Entity> pageResult) {
/* 31 */     return new PageResultHandler(pageResult);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PageResultHandler(PageResult<Entity> pageResult) {
/* 41 */     this(pageResult, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PageResultHandler(PageResult<Entity> pageResult, boolean caseInsensitive) {
/* 52 */     this.pageResult = pageResult;
/* 53 */     this.caseInsensitive = caseInsensitive;
/*    */   }
/*    */ 
/*    */   
/*    */   public PageResult<Entity> handle(ResultSet rs) throws SQLException {
/* 58 */     return HandleHelper.<PageResult<Entity>>handleRs(rs, this.pageResult, this.caseInsensitive);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\PageResultHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */