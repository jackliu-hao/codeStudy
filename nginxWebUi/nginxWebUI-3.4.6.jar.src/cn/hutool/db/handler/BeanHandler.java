/*    */ package cn.hutool.db.handler;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public class BeanHandler<E>
/*    */   implements RsHandler<E>
/*    */ {
/*    */   private static final long serialVersionUID = -5491214744966544475L;
/*    */   private final Class<E> elementBeanType;
/*    */   
/*    */   public static <E> BeanHandler<E> create(Class<E> beanType) {
/* 27 */     return new BeanHandler<>(beanType);
/*    */   }
/*    */   
/*    */   public BeanHandler(Class<E> beanType) {
/* 31 */     this.elementBeanType = beanType;
/*    */   }
/*    */ 
/*    */   
/*    */   public E handle(ResultSet rs) throws SQLException {
/* 36 */     ResultSetMetaData meta = rs.getMetaData();
/* 37 */     int columnCount = meta.getColumnCount();
/* 38 */     return rs.next() ? HandleHelper.<E>handleRow(columnCount, meta, rs, this.elementBeanType) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\handler\BeanHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */