/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.constraint.ConstraintActionType;
/*     */ import org.h2.constraint.ConstraintDomain;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.ColumnTemplate;
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
/*     */ public class DropDomain
/*     */   extends AlterDomain
/*     */ {
/*     */   private ConstraintActionType dropAction;
/*     */   
/*     */   public DropDomain(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  31 */     super(paramSessionLocal, paramSchema);
/*  32 */     this.dropAction = (paramSessionLocal.getDatabase().getSettings()).dropRestrict ? ConstraintActionType.RESTRICT : ConstraintActionType.CASCADE;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setDropAction(ConstraintActionType paramConstraintActionType) {
/*  37 */     this.dropAction = paramConstraintActionType;
/*     */   }
/*     */ 
/*     */   
/*     */   long update(Schema paramSchema, Domain paramDomain) {
/*  42 */     forAllDependencies(this.session, paramDomain, this::copyColumn, this::copyDomain, true);
/*  43 */     this.session.getDatabase().removeSchemaObject(this.session, (SchemaObject)paramDomain);
/*  44 */     return 0L;
/*     */   }
/*     */   
/*     */   private boolean copyColumn(Domain paramDomain, Column paramColumn) {
/*  48 */     Table table = paramColumn.getTable();
/*  49 */     if (this.dropAction == ConstraintActionType.RESTRICT) {
/*  50 */       throw DbException.get(90107, new String[] { this.domainName, table.getCreateSQL() });
/*     */     }
/*  52 */     String str = paramColumn.getName();
/*  53 */     ArrayList arrayList = paramDomain.getConstraints();
/*  54 */     if (arrayList != null && !arrayList.isEmpty()) {
/*  55 */       for (ConstraintDomain constraintDomain : arrayList) {
/*  56 */         Expression expression = constraintDomain.getCheckConstraint(this.session, str);
/*  57 */         AlterTableAddConstraint alterTableAddConstraint = new AlterTableAddConstraint(this.session, table.getSchema(), 3, false);
/*     */         
/*  59 */         alterTableAddConstraint.setTableName(table.getName());
/*  60 */         alterTableAddConstraint.setCheckExpression(expression);
/*  61 */         alterTableAddConstraint.update();
/*     */       } 
/*     */     }
/*  64 */     copyExpressions(this.session, paramDomain, (ColumnTemplate)paramColumn);
/*  65 */     return true;
/*     */   }
/*     */   
/*     */   private boolean copyDomain(Domain paramDomain1, Domain paramDomain2) {
/*  69 */     if (this.dropAction == ConstraintActionType.RESTRICT) {
/*  70 */       throw DbException.get(90107, new String[] { this.domainName, paramDomain2.getTraceSQL() });
/*     */     }
/*  72 */     ArrayList arrayList = paramDomain1.getConstraints();
/*  73 */     if (arrayList != null && !arrayList.isEmpty()) {
/*  74 */       for (ConstraintDomain constraintDomain : arrayList) {
/*  75 */         Expression expression = constraintDomain.getCheckConstraint(this.session, null);
/*  76 */         AlterDomainAddConstraint alterDomainAddConstraint = new AlterDomainAddConstraint(this.session, paramDomain2.getSchema(), false);
/*     */         
/*  78 */         alterDomainAddConstraint.setDomainName(paramDomain2.getName());
/*  79 */         alterDomainAddConstraint.setCheckExpression(expression);
/*  80 */         alterDomainAddConstraint.update();
/*     */       } 
/*     */     }
/*  83 */     copyExpressions(this.session, paramDomain1, (ColumnTemplate)paramDomain2);
/*  84 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean copyExpressions(SessionLocal paramSessionLocal, Domain paramDomain, ColumnTemplate paramColumnTemplate) {
/*  88 */     paramColumnTemplate.setDomain(paramDomain.getDomain());
/*  89 */     Expression expression = paramDomain.getDefaultExpression();
/*  90 */     boolean bool = false;
/*  91 */     if (expression != null && paramColumnTemplate.getDefaultExpression() == null) {
/*  92 */       paramColumnTemplate.setDefaultExpression(paramSessionLocal, expression);
/*  93 */       bool = true;
/*     */     } 
/*  95 */     expression = paramDomain.getOnUpdateExpression();
/*  96 */     if (expression != null && paramColumnTemplate.getOnUpdateExpression() == null) {
/*  97 */       paramColumnTemplate.setOnUpdateExpression(paramSessionLocal, expression);
/*  98 */       bool = true;
/*     */     } 
/* 100 */     return bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 105 */     return 47;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\DropDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */