package cn.hutool.core.lang;

public class DefaultSegment<T extends Number> implements Segment<T> {
   protected T startIndex;
   protected T endIndex;

   public DefaultSegment(T startIndex, T endIndex) {
      this.startIndex = startIndex;
      this.endIndex = endIndex;
   }

   public T getStartIndex() {
      return this.startIndex;
   }

   public T getEndIndex() {
      return this.endIndex;
   }
}
