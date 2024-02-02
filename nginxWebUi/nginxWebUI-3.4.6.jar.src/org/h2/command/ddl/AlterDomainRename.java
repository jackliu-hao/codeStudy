/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.function.BiPredicate;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Domain;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.schema.SchemaObject;
/*    */ import org.h2.table.Column;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AlterDomainRename
/*    */   extends AlterDomain
/*    */ {
/*    */   private String newDomainName;
/*    */   
/*    */   public AlterDomainRename(SessionLocal paramSessionLocal, Schema paramSchema) {
/* 24 */     super(paramSessionLocal, paramSchema);
/*    */   }
/*    */   
/*    */   public void setNewDomainName(String paramString) {
/* 28 */     this.newDomainName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   long update(Schema paramSchema, Domain paramDomain) {
/* 33 */     Domain domain = paramSchema.findDomain(this.newDomainName);
/* 34 */     if (domain != null) {
/* 35 */       if (paramDomain != domain) {
/* 36 */         throw DbException.get(90119, this.newDomainName);
/*    */       }
/* 38 */       if (this.newDomainName.equals(paramDomain.getName())) {
/* 39 */         return 0L;
/*    */       }
/*    */     } 
/* 42 */     this.session.getDatabase().renameSchemaObject(this.session, (SchemaObject)paramDomain, this.newDomainName);
/* 43 */     forAllDependencies(this.session, paramDomain, (BiPredicate<Domain, Column>)null, (BiPredicate<Domain, Domain>)null, false);
/* 44 */     return 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getType() {
/* 49 */     return 96;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\AlterDomainRename.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */