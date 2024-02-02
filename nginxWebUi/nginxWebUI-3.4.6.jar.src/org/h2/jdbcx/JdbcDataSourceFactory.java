/*    */ package org.h2.jdbcx;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ import javax.naming.Context;
/*    */ import javax.naming.Name;
/*    */ import javax.naming.Reference;
/*    */ import javax.naming.spi.ObjectFactory;
/*    */ import org.h2.engine.SysProperties;
/*    */ import org.h2.message.Trace;
/*    */ import org.h2.message.TraceSystem;
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
/*    */ 
/*    */ public final class JdbcDataSourceFactory
/*    */   implements ObjectFactory
/*    */ {
/* 31 */   private static final TraceSystem traceSystem = new TraceSystem(SysProperties.CLIENT_TRACE_DIRECTORY + "h2datasource" + ".trace.db");
/*    */   static {
/* 33 */     traceSystem.setLevelFile(SysProperties.DATASOURCE_TRACE_LEVEL);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   private final Trace trace = traceSystem.getTrace(14);
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
/*    */   public synchronized Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) {
/* 58 */     if (this.trace.isDebugEnabled()) {
/* 59 */       this.trace.debug("getObjectInstance obj={0} name={1} nameCtx={2} environment={3}", new Object[] { paramObject, paramName, paramContext, paramHashtable });
/*    */     }
/*    */     
/* 62 */     if (paramObject instanceof Reference) {
/* 63 */       Reference reference = (Reference)paramObject;
/* 64 */       if (reference.getClassName().equals(JdbcDataSource.class.getName())) {
/* 65 */         JdbcDataSource jdbcDataSource = new JdbcDataSource();
/* 66 */         jdbcDataSource.setURL((String)reference.get("url").getContent());
/* 67 */         jdbcDataSource.setUser((String)reference.get("user").getContent());
/* 68 */         jdbcDataSource.setPassword((String)reference.get("password").getContent());
/* 69 */         jdbcDataSource.setDescription((String)reference.get("description").getContent());
/* 70 */         String str = (String)reference.get("loginTimeout").getContent();
/* 71 */         jdbcDataSource.setLoginTimeout(Integer.parseInt(str));
/* 72 */         return jdbcDataSource;
/*    */       } 
/*    */     } 
/* 75 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static TraceSystem getTraceSystem() {
/* 83 */     return traceSystem;
/*    */   }
/*    */   
/*    */   Trace getTrace() {
/* 87 */     return this.trace;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbcx\JdbcDataSourceFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */