/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.function.BiPredicate;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.Table;
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
/*     */ public abstract class AlterDomain
/*     */   extends SchemaOwnerCommand
/*     */ {
/*     */   String domainName;
/*     */   boolean ifDomainExists;
/*     */   
/*     */   public static void forAllDependencies(SessionLocal paramSessionLocal, Domain paramDomain, BiPredicate<Domain, Column> paramBiPredicate, BiPredicate<Domain, Domain> paramBiPredicate1, boolean paramBoolean) {
/*  41 */     Database database = paramSessionLocal.getDatabase();
/*  42 */     for (Schema schema : database.getAllSchemasNoMeta()) {
/*  43 */       for (Domain domain : schema.getAllDomains()) {
/*  44 */         if (domain.getDomain() == paramDomain && (
/*  45 */           paramBiPredicate1 == null || paramBiPredicate1.test(paramDomain, domain))) {
/*  46 */           if (paramBoolean) {
/*  47 */             paramDomain.prepareExpressions(paramSessionLocal);
/*     */           }
/*  49 */           database.updateMeta(paramSessionLocal, (DbObject)domain);
/*     */         } 
/*     */       } 
/*     */       
/*  53 */       for (Table table : schema.getAllTablesAndViews(null)) {
/*  54 */         if (forTable(paramSessionLocal, paramDomain, paramBiPredicate, paramBoolean, table)) {
/*  55 */           database.updateMeta(paramSessionLocal, (DbObject)table);
/*     */         }
/*     */       } 
/*     */     } 
/*  59 */     for (Table table : paramSessionLocal.getLocalTempTables()) {
/*  60 */       forTable(paramSessionLocal, paramDomain, paramBiPredicate, paramBoolean, table);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean forTable(SessionLocal paramSessionLocal, Domain paramDomain, BiPredicate<Domain, Column> paramBiPredicate, boolean paramBoolean, Table paramTable) {
/*  66 */     boolean bool = false;
/*  67 */     for (Column column : paramTable.getColumns()) {
/*  68 */       if (column.getDomain() == paramDomain) {
/*  69 */         boolean bool1 = (paramBiPredicate == null || paramBiPredicate.test(paramDomain, column)) ? true : false;
/*  70 */         if (bool1) {
/*  71 */           if (paramBoolean) {
/*  72 */             column.prepareExpressions(paramSessionLocal);
/*     */           }
/*  74 */           bool = true;
/*     */         } 
/*     */       } 
/*     */     } 
/*  78 */     return bool;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   AlterDomain(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  86 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public final void setDomainName(String paramString) {
/*  90 */     this.domainName = paramString;
/*     */   }
/*     */   
/*     */   public final void setIfDomainExists(boolean paramBoolean) {
/*  94 */     this.ifDomainExists = paramBoolean;
/*     */   }
/*     */ 
/*     */   
/*     */   final long update(Schema paramSchema) {
/*  99 */     Domain domain = getSchema().findDomain(this.domainName);
/* 100 */     if (domain == null) {
/* 101 */       if (this.ifDomainExists) {
/* 102 */         return 0L;
/*     */       }
/* 104 */       throw DbException.get(90120, this.domainName);
/*     */     } 
/* 106 */     return update(paramSchema, domain);
/*     */   }
/*     */   
/*     */   abstract long update(Schema paramSchema, Domain paramDomain);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */