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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BeanListHandler<E>
/*    */   implements RsHandler<List<E>>
/*    */ {
/*    */   private static final long serialVersionUID = 4510569754766197707L;
/*    */   private final Class<E> elementBeanType;
/*    */   
/*    */   public static <E> BeanListHandler<E> create(Class<E> beanType) {
/* 28 */     return new BeanListHandler<>(beanType);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BeanListHandler(Class<E> beanType) {
/* 36 */     this.elementBeanType = beanType;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<E> handle(ResultSet rs) throws SQLException {
/* 41 */     return HandleHelper.<E, List<E>>handleRsToBeanList(rs, new ArrayList<>(), this.elementBeanType);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\BeanListHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */