/*     */ package org.h2.schema;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.api.Aggregate;
/*     */ import org.h2.api.AggregateFunction;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class UserAggregate
/*     */   extends UserDefinedFunction
/*     */ {
/*     */   private Class<?> javaClass;
/*     */   
/*     */   public UserAggregate(Schema paramSchema, int paramInt, String paramString1, String paramString2, boolean paramBoolean) {
/*  31 */     super(paramSchema, paramInt, paramString1, 3);
/*  32 */     this.className = paramString2;
/*  33 */     if (!paramBoolean) {
/*  34 */       getInstance();
/*     */     }
/*     */   }
/*     */   
/*     */   public Aggregate getInstance() {
/*  39 */     if (this.javaClass == null) {
/*  40 */       this.javaClass = JdbcUtils.loadUserClass(this.className);
/*     */     }
/*     */     
/*     */     try {
/*  44 */       Aggregate aggregate2, aggregate1 = (Aggregate)this.javaClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */       
/*  46 */       if (aggregate1 instanceof Aggregate) {
/*  47 */         aggregate2 = aggregate1;
/*     */       } else {
/*  49 */         aggregate2 = new AggregateWrapper((AggregateFunction)aggregate1);
/*     */       } 
/*  51 */       return aggregate2;
/*  52 */     } catch (Exception exception) {
/*  53 */       throw DbException.convert(exception);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDropSQL() {
/*  59 */     StringBuilder stringBuilder = new StringBuilder("DROP AGGREGATE IF EXISTS ");
/*  60 */     return getSQL(stringBuilder, 0).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCreateSQL() {
/*  65 */     StringBuilder stringBuilder = new StringBuilder("CREATE FORCE AGGREGATE ");
/*  66 */     getSQL(stringBuilder, 0).append(" FOR ");
/*  67 */     return StringUtils.quoteStringSQL(stringBuilder, this.className).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/*  72 */     return 14;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void removeChildrenAndResources(SessionLocal paramSessionLocal) {
/*  77 */     this.database.removeMeta(paramSessionLocal, getId());
/*  78 */     this.className = null;
/*  79 */     this.javaClass = null;
/*  80 */     invalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class AggregateWrapper
/*     */     implements Aggregate
/*     */   {
/*     */     private final AggregateFunction aggregateFunction;
/*     */ 
/*     */     
/*     */     AggregateWrapper(AggregateFunction param1AggregateFunction) {
/*  91 */       this.aggregateFunction = param1AggregateFunction;
/*     */     }
/*     */ 
/*     */     
/*     */     public void init(Connection param1Connection) throws SQLException {
/*  96 */       this.aggregateFunction.init(param1Connection);
/*     */     }
/*     */ 
/*     */     
/*     */     public int getInternalType(int[] param1ArrayOfint) throws SQLException {
/* 101 */       int[] arrayOfInt = new int[param1ArrayOfint.length];
/* 102 */       for (byte b = 0; b < param1ArrayOfint.length; b++) {
/* 103 */         arrayOfInt[b] = DataType.convertTypeToSQLType(TypeInfo.getTypeInfo(param1ArrayOfint[b]));
/*     */       }
/* 105 */       return DataType.convertSQLTypeToValueType(this.aggregateFunction.getType(arrayOfInt));
/*     */     }
/*     */ 
/*     */     
/*     */     public void add(Object param1Object) throws SQLException {
/* 110 */       this.aggregateFunction.add(param1Object);
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getResult() throws SQLException {
/* 115 */       return this.aggregateFunction.getResult();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\schema\UserAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */