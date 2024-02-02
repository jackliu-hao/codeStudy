package org.antlr.v4.runtime.misc;

public class IntegerStack extends IntegerList {
   public IntegerStack() {
   }

   public IntegerStack(int capacity) {
      super(capacity);
   }

   public IntegerStack(IntegerStack list) {
      super((IntegerList)list);
   }

   public final void push(int value) {
      this.add(value);
   }

   public final int pop() {
      return this.removeAt(this.size() - 1);
   }

   public final int peek() {
      return this.get(this.size() - 1);
   }
}
