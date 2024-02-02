/*    */ package com.google.protobuf;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class ProtobufLists
/*    */ {
/*    */   public static <E> Internal.ProtobufList<E> emptyProtobufList() {
/* 46 */     return ProtobufArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
/* 50 */     int size = list.size();
/* 51 */     return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*    */   }
/*    */ 
/*    */   
/*    */   public static Internal.BooleanList emptyBooleanList() {
/* 56 */     return BooleanArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static Internal.BooleanList newBooleanList() {
/* 60 */     return new BooleanArrayList();
/*    */   }
/*    */   
/*    */   public static Internal.IntList emptyIntList() {
/* 64 */     return IntArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static Internal.IntList newIntList() {
/* 68 */     return new IntArrayList();
/*    */   }
/*    */   
/*    */   public static Internal.LongList emptyLongList() {
/* 72 */     return LongArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static Internal.LongList newLongList() {
/* 76 */     return new LongArrayList();
/*    */   }
/*    */   
/*    */   public static Internal.FloatList emptyFloatList() {
/* 80 */     return FloatArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static Internal.FloatList newFloatList() {
/* 84 */     return new FloatArrayList();
/*    */   }
/*    */   
/*    */   public static Internal.DoubleList emptyDoubleList() {
/* 88 */     return DoubleArrayList.emptyList();
/*    */   }
/*    */   
/*    */   public static Internal.DoubleList newDoubleList() {
/* 92 */     return new DoubleArrayList();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ProtobufLists.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */