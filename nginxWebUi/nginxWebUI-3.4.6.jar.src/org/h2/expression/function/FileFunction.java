/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Paths;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBigint;
/*     */ import org.h2.value.ValueBlob;
/*     */ import org.h2.value.ValueClob;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public final class FileFunction
/*     */   extends Function1_2
/*     */ {
/*     */   public static final int FILE_READ = 0;
/*     */   public static final int FILE_WRITE = 1;
/*  44 */   private static final String[] NAMES = new String[] { "FILE_READ", "FILE_WRITE" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public FileFunction(Expression paramExpression1, Expression paramExpression2, int paramInt) {
/*  51 */     super(paramExpression1, paramExpression2);
/*  52 */     this.function = paramInt; } public Value getValue(SessionLocal paramSessionLocal) { ValueLob valueLob; ValueNull valueNull;
/*     */     ValueBigint valueBigint;
/*     */     String str;
/*     */     Value value2;
/*     */     Database database;
/*  57 */     paramSessionLocal.getUser().checkAdmin();
/*  58 */     Value value1 = this.left.getValue(paramSessionLocal);
/*  59 */     if (value1 == ValueNull.INSTANCE) {
/*  60 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/*  62 */     switch (this.function) {
/*     */       case 0:
/*  64 */         str = value1.getString();
/*  65 */         database = paramSessionLocal.getDatabase(); try {
/*     */           ValueClob valueClob;
/*  67 */           long l = FileUtils.size(str);
/*     */           
/*  69 */           try (InputStream null = FileUtils.newInputStream(str)) {
/*  70 */             if (this.right == null) {
/*  71 */               ValueBlob valueBlob = database.getLobStorage().createBlob(inputStream, l);
/*     */             } else {
/*  73 */               Value value = this.right.getValue(paramSessionLocal);
/*     */               
/*  75 */               InputStreamReader inputStreamReader = (value == ValueNull.INSTANCE) ? new InputStreamReader(inputStream) : new InputStreamReader(inputStream, value.getString());
/*  76 */               valueClob = database.getLobStorage().createClob(inputStreamReader, l);
/*     */             } 
/*     */           } 
/*  79 */           valueLob = paramSessionLocal.addTemporaryLob((ValueLob)valueClob);
/*  80 */         } catch (IOException iOException) {
/*  81 */           throw DbException.convertIOException(iOException, str);
/*     */         } 
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
/* 103 */         return (Value)valueLob;case 1: value2 = this.right.getValue(paramSessionLocal); if (value2 == ValueNull.INSTANCE) { valueNull = ValueNull.INSTANCE; } else { String str1 = value2.getString(); try(OutputStream null = Files.newOutputStream(Paths.get(str1, new String[0]), new java.nio.file.OpenOption[0]); InputStream null = valueNull.getInputStream()) { valueBigint = ValueBigint.get(IOUtils.copy(inputStream, outputStream)); } catch (IOException iOException) { throw DbException.convertIOException(iOException, str1); }  }  return (Value)valueBigint;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function); }
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 108 */     this.left = this.left.optimize(paramSessionLocal);
/* 109 */     if (this.right != null) {
/* 110 */       this.right = this.right.optimize(paramSessionLocal);
/*     */     }
/* 112 */     switch (this.function) {
/*     */       case 0:
/* 114 */         this
/* 115 */           .type = (this.right == null) ? TypeInfo.getTypeInfo(7, 2147483647L, 0, null) : TypeInfo.getTypeInfo(3, 2147483647L, 0, null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 123 */         return (Expression)this;case 1: this.type = TypeInfo.TYPE_BIGINT; return (Expression)this;
/*     */     } 
/*     */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 128 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/*     */       case 8:
/* 131 */         return false;
/*     */       case 5:
/* 133 */         if (this.function == 1)
/* 134 */           return false; 
/*     */         break;
/*     */     } 
/* 137 */     return super.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 142 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\FileFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */