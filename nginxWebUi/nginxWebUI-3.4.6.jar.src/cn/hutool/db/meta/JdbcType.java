/*    */ package cn.hutool.db.meta;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum JdbcType
/*    */ {
/* 13 */   ARRAY(2003),
/* 14 */   BIT(-7),
/* 15 */   TINYINT(-6),
/* 16 */   SMALLINT(5),
/* 17 */   INTEGER(4),
/* 18 */   BIGINT(-5),
/* 19 */   FLOAT(6),
/* 20 */   REAL(7),
/* 21 */   DOUBLE(8),
/* 22 */   NUMERIC(2),
/* 23 */   DECIMAL(3),
/* 24 */   CHAR(1),
/* 25 */   VARCHAR(12),
/* 26 */   LONGVARCHAR(-1),
/* 27 */   DATE(91),
/* 28 */   TIME(92),
/* 29 */   TIMESTAMP(93),
/* 30 */   BINARY(-2),
/* 31 */   VARBINARY(-3),
/* 32 */   LONGVARBINARY(-4),
/* 33 */   NULL(0),
/* 34 */   OTHER(1111),
/* 35 */   BLOB(2004),
/* 36 */   CLOB(2005),
/* 37 */   BOOLEAN(16),
/* 38 */   CURSOR(-10),
/* 39 */   UNDEFINED(-2147482648),
/* 40 */   NVARCHAR(-9),
/* 41 */   NCHAR(-15),
/* 42 */   NCLOB(2011),
/* 43 */   STRUCT(2002),
/* 44 */   JAVA_OBJECT(2000),
/* 45 */   DISTINCT(2001),
/* 46 */   REF(2006),
/* 47 */   DATALINK(70),
/* 48 */   ROWID(-8),
/* 49 */   LONGNVARCHAR(-16),
/* 50 */   SQLXML(2009),
/* 51 */   DATETIMEOFFSET(-155),
/* 52 */   TIME_WITH_TIMEZONE(2013),
/* 53 */   TIMESTAMP_WITH_TIMEZONE(2014);
/*    */ 
/*    */   
/*    */   public final int typeCode;
/*    */ 
/*    */   
/*    */   private static final Map<Integer, JdbcType> CODE_MAP;
/*    */ 
/*    */   
/*    */   JdbcType(int code) {
/* 63 */     this.typeCode = code;
/*    */   }
/*    */   static {
/* 66 */     CODE_MAP = new ConcurrentHashMap<>(100, 1.0F);
/*    */     
/* 68 */     for (JdbcType type : values())
/* 69 */       CODE_MAP.put(Integer.valueOf(type.typeCode), type); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\meta\JdbcType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */