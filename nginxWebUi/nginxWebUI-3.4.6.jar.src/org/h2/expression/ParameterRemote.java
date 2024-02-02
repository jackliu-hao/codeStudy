/*    */ package org.h2.expression;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.value.Transfer;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueLob;
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
/*    */ public class ParameterRemote
/*    */   implements ParameterInterface
/*    */ {
/*    */   private Value value;
/*    */   private final int index;
/* 25 */   private TypeInfo type = TypeInfo.TYPE_UNKNOWN;
/* 26 */   private int nullable = 2;
/*    */   
/*    */   public ParameterRemote(int paramInt) {
/* 29 */     this.index = paramInt;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Value paramValue, boolean paramBoolean) {
/* 34 */     if (paramBoolean && this.value instanceof ValueLob) {
/* 35 */       ((ValueLob)this.value).remove();
/*    */     }
/* 37 */     this.value = paramValue;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getParamValue() {
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkSet() {
/* 47 */     if (this.value == null) {
/* 48 */       throw DbException.get(90012, "#" + (this.index + 1));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isValueSet() {
/* 54 */     return (this.value != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 59 */     return (this.value == null) ? this.type : this.value.getType();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNullable() {
/* 64 */     return this.nullable;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void readMetaData(Transfer paramTransfer) throws IOException {
/* 74 */     this.type = paramTransfer.readTypeInfo();
/* 75 */     this.nullable = paramTransfer.readInt();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void writeMetaData(Transfer paramTransfer, ParameterInterface paramParameterInterface) throws IOException {
/* 86 */     paramTransfer.writeTypeInfo(paramParameterInterface.getType()).writeInt(paramParameterInterface.getNullable());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\ParameterRemote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */