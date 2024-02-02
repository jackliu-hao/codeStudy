package com.mysql.cj;

import com.mysql.cj.util.StringUtils;
import java.util.Iterator;
import java.util.LinkedList;

public class AppendingBatchVisitor implements BatchVisitor {
   LinkedList<byte[]> statementComponents = new LinkedList();

   public BatchVisitor append(byte[] values) {
      this.statementComponents.addLast(values);
      return this;
   }

   public BatchVisitor increment() {
      return this;
   }

   public BatchVisitor decrement() {
      this.statementComponents.removeLast();
      return this;
   }

   public BatchVisitor merge(byte[] front, byte[] back) {
      int mergedLength = front.length + back.length;
      byte[] merged = new byte[mergedLength];
      System.arraycopy(front, 0, merged, 0, front.length);
      System.arraycopy(back, 0, merged, front.length, back.length);
      this.statementComponents.addLast(merged);
      return this;
   }

   public BatchVisitor mergeWithLast(byte[] values) {
      return this.statementComponents.isEmpty() ? this.append(values) : this.merge((byte[])this.statementComponents.removeLast(), values);
   }

   public byte[][] getStaticSqlStrings() {
      byte[][] asBytes = new byte[this.statementComponents.size()][];
      this.statementComponents.toArray(asBytes);
      return asBytes;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      Iterator var2 = this.statementComponents.iterator();

      while(var2.hasNext()) {
         byte[] comp = (byte[])var2.next();
         sb.append(StringUtils.toString(comp));
      }

      return sb.toString();
   }
}
