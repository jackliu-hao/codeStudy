/*     */ package org.h2.command.ddl;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.SchemaObject;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ public class CreateDomain
/*     */   extends SchemaOwnerCommand
/*     */ {
/*     */   private String typeName;
/*     */   private boolean ifNotExists;
/*     */   private TypeInfo dataType;
/*     */   private Domain parentDomain;
/*     */   private Expression defaultExpression;
/*     */   private Expression onUpdateExpression;
/*     */   private String comment;
/*     */   private ArrayList<AlterDomainAddConstraint> constraintCommands;
/*     */   
/*     */   public CreateDomain(SessionLocal paramSessionLocal, Schema paramSchema) {
/*  45 */     super(paramSessionLocal, paramSchema);
/*     */   }
/*     */   
/*     */   public void setTypeName(String paramString) {
/*  49 */     this.typeName = paramString;
/*     */   }
/*     */   
/*     */   public void setIfNotExists(boolean paramBoolean) {
/*  53 */     this.ifNotExists = paramBoolean;
/*     */   }
/*     */   
/*     */   public void setDataType(TypeInfo paramTypeInfo) {
/*  57 */     this.dataType = paramTypeInfo;
/*     */   }
/*     */   
/*     */   public void setParentDomain(Domain paramDomain) {
/*  61 */     this.parentDomain = paramDomain;
/*     */   }
/*     */   
/*     */   public void setDefaultExpression(Expression paramExpression) {
/*  65 */     this.defaultExpression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setOnUpdateExpression(Expression paramExpression) {
/*  69 */     this.onUpdateExpression = paramExpression;
/*     */   }
/*     */   
/*     */   public void setComment(String paramString) {
/*  73 */     this.comment = paramString;
/*     */   }
/*     */ 
/*     */   
/*     */   long update(Schema paramSchema) {
/*  78 */     if (paramSchema.findDomain(this.typeName) != null) {
/*  79 */       if (this.ifNotExists) {
/*  80 */         return 0L;
/*     */       }
/*  82 */       throw DbException.get(90119, this.typeName);
/*     */     } 
/*  84 */     if (this.typeName.indexOf(' ') < 0) {
/*  85 */       DataType dataType = DataType.getTypeByName(this.typeName, this.session.getDatabase().getMode());
/*  86 */       if (dataType != null) {
/*  87 */         if (this.session.getDatabase().equalsIdentifiers(this.typeName, Value.getTypeName(dataType.type))) {
/*  88 */           throw DbException.get(90119, this.typeName);
/*     */         }
/*  90 */         Table table = this.session.getDatabase().getFirstUserTable();
/*  91 */         if (table != null) {
/*  92 */           StringBuilder stringBuilder = (new StringBuilder(this.typeName)).append(" (");
/*  93 */           table.getSQL(stringBuilder, 3).append(')');
/*  94 */           throw DbException.get(90119, stringBuilder.toString());
/*     */         } 
/*     */       } 
/*     */     } 
/*  98 */     int i = getObjectId();
/*  99 */     Domain domain = new Domain(paramSchema, i, this.typeName);
/* 100 */     domain.setDataType((this.dataType != null) ? this.dataType : this.parentDomain.getDataType());
/* 101 */     domain.setDomain(this.parentDomain);
/* 102 */     domain.setDefaultExpression(this.session, this.defaultExpression);
/* 103 */     domain.setOnUpdateExpression(this.session, this.onUpdateExpression);
/* 104 */     domain.setComment(this.comment);
/* 105 */     paramSchema.getDatabase().addSchemaObject(this.session, (SchemaObject)domain);
/* 106 */     if (this.constraintCommands != null) {
/* 107 */       for (AlterDomainAddConstraint alterDomainAddConstraint : this.constraintCommands) {
/* 108 */         alterDomainAddConstraint.update();
/*     */       }
/*     */     }
/* 111 */     return 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 116 */     return 33;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addConstraintCommand(AlterDomainAddConstraint paramAlterDomainAddConstraint) {
/* 125 */     if (this.constraintCommands == null) {
/* 126 */       this.constraintCommands = Utils.newSmallArrayList();
/*     */     }
/* 128 */     this.constraintCommands.add(paramAlterDomainAddConstraint);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */