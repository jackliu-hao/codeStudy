/*    */ package org.h2.mvstore.db;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.Arrays;
/*    */ import org.h2.mvstore.WriteBuffer;
/*    */ import org.h2.mvstore.type.DataType;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
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
/*    */ public final class NullValueDataType
/*    */   implements DataType<Value>
/*    */ {
/* 25 */   public static final NullValueDataType INSTANCE = new NullValueDataType();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int compare(Value paramValue1, Value paramValue2) {
/* 32 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int binarySearch(Value paramValue, Object paramObject, int paramInt1, int paramInt2) {
/* 37 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getMemory(Value paramValue) {
/* 42 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isMemoryEstimationAllowed() {
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, Value paramValue) {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void write(WriteBuffer paramWriteBuffer, Object paramObject, int paramInt) {}
/*    */ 
/*    */   
/*    */   public Value read(ByteBuffer paramByteBuffer) {
/* 60 */     return (Value)ValueNull.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public void read(ByteBuffer paramByteBuffer, Object paramObject, int paramInt) {
/* 65 */     Arrays.fill((Object[])paramObject, 0, paramInt, ValueNull.INSTANCE);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value[] createStorage(int paramInt) {
/* 70 */     return new Value[paramInt];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\NullValueDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */