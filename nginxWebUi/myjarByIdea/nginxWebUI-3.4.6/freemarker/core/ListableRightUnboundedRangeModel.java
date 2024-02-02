package freemarker.core;

import freemarker.template.SimpleNumber;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import java.math.BigInteger;

final class ListableRightUnboundedRangeModel extends RightUnboundedRangeModel implements TemplateCollectionModel {
   ListableRightUnboundedRangeModel(int begin) {
      super(begin);
   }

   public int size() throws TemplateModelException {
      return Integer.MAX_VALUE;
   }

   public TemplateModelIterator iterator() throws TemplateModelException {
      return new TemplateModelIterator() {
         boolean needInc;
         int nextType = 1;
         int nextInt = ListableRightUnboundedRangeModel.this.getBegining();
         long nextLong;
         BigInteger nextBigInteger;

         public TemplateModel next() throws TemplateModelException {
            if (this.needInc) {
               switch (this.nextType) {
                  case 1:
                     if (this.nextInt < Integer.MAX_VALUE) {
                        ++this.nextInt;
                     } else {
                        this.nextType = 2;
                        this.nextLong = (long)this.nextInt + 1L;
                     }
                     break;
                  case 2:
                     if (this.nextLong < Long.MAX_VALUE) {
                        ++this.nextLong;
                     } else {
                        this.nextType = 3;
                        this.nextBigInteger = BigInteger.valueOf(this.nextLong);
                        this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
                     }
                     break;
                  default:
                     this.nextBigInteger = this.nextBigInteger.add(BigInteger.ONE);
               }
            }

            this.needInc = true;
            return this.nextType == 1 ? new SimpleNumber(this.nextInt) : (this.nextType == 2 ? new SimpleNumber(this.nextLong) : new SimpleNumber(this.nextBigInteger));
         }

         public boolean hasNext() throws TemplateModelException {
            return true;
         }
      };
   }
}
