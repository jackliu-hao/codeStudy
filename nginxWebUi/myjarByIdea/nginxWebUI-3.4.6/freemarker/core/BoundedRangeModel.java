package freemarker.core;

final class BoundedRangeModel extends RangeModel {
   private final int step;
   private final int size;
   private final boolean rightAdaptive;
   private final boolean affectedByStringSlicingBug;

   BoundedRangeModel(int begin, int end, boolean inclusiveEnd, boolean rightAdaptive) {
      super(begin);
      this.step = begin <= end ? 1 : -1;
      this.size = Math.abs(end - begin) + (inclusiveEnd ? 1 : 0);
      this.rightAdaptive = rightAdaptive;
      this.affectedByStringSlicingBug = inclusiveEnd;
   }

   public int size() {
      return this.size;
   }

   int getStep() {
      return this.step;
   }

   boolean isRightUnbounded() {
      return false;
   }

   boolean isRightAdaptive() {
      return this.rightAdaptive;
   }

   boolean isAffectedByStringSlicingBug() {
      return this.affectedByStringSlicingBug;
   }
}
