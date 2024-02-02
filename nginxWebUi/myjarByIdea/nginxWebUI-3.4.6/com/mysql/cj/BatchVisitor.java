package com.mysql.cj;

public interface BatchVisitor {
   BatchVisitor increment();

   BatchVisitor decrement();

   BatchVisitor append(byte[] var1);

   BatchVisitor merge(byte[] var1, byte[] var2);

   BatchVisitor mergeWithLast(byte[] var1);
}
