package org.jboss.logging;

import java.util.ArrayDeque;

abstract class AbstractLoggerProvider {
   private final ThreadLocal<ArrayDeque<Entry>> ndcStack = new ThreadLocal();

   public void clearNdc() {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      if (stack != null) {
         stack.clear();
      }

   }

   public String getNdc() {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      return stack != null && !stack.isEmpty() ? ((Entry)stack.peek()).merged : null;
   }

   public int getNdcDepth() {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      return stack == null ? 0 : stack.size();
   }

   public String peekNdc() {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      return stack != null && !stack.isEmpty() ? ((Entry)stack.peek()).current : "";
   }

   public String popNdc() {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      return stack != null && !stack.isEmpty() ? ((Entry)stack.pop()).current : "";
   }

   public void pushNdc(String message) {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      if (stack == null) {
         stack = new ArrayDeque();
         this.ndcStack.set(stack);
      }

      stack.push(stack.isEmpty() ? new Entry(message) : new Entry((Entry)stack.peek(), message));
   }

   public void setNdcMaxDepth(int maxDepth) {
      ArrayDeque<Entry> stack = (ArrayDeque)this.ndcStack.get();
      if (stack != null) {
         while(stack.size() > maxDepth) {
            stack.pop();
         }
      }

   }

   private static class Entry {
      private String merged;
      private String current;

      Entry(String current) {
         this.merged = current;
         this.current = current;
      }

      Entry(Entry parent, String current) {
         this.merged = parent.merged + ' ' + current;
         this.current = current;
      }
   }
}
