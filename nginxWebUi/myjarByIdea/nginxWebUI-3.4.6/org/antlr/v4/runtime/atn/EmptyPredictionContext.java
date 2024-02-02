package org.antlr.v4.runtime.atn;

public class EmptyPredictionContext extends SingletonPredictionContext {
   public EmptyPredictionContext() {
      super((PredictionContext)null, Integer.MAX_VALUE);
   }

   public boolean isEmpty() {
      return true;
   }

   public int size() {
      return 1;
   }

   public PredictionContext getParent(int index) {
      return null;
   }

   public int getReturnState(int index) {
      return this.returnState;
   }

   public boolean equals(Object o) {
      return this == o;
   }

   public String toString() {
      return "$";
   }
}
