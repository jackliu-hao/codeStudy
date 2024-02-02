package com.mysql.cj;

public class ClientPreparedQuery extends AbstractPreparedQuery<ClientPreparedQueryBindings> {
   public ClientPreparedQuery(NativeSession sess) {
      super(sess);
   }

   protected long[] computeMaxParameterSetSizeAndBatchSize(int numBatchedArgs) {
      long sizeOfEntireBatch = 1L;
      long maxSizeOfParameterSet = 0L;
      int i;
      if (this.session.getServerSession().supportsQueryAttributes()) {
         sizeOfEntireBatch += 10L;
         sizeOfEntireBatch += (long)((this.queryAttributesBindings.getCount() + 7) / 8 + 1);

         for(i = 0; i < this.queryAttributesBindings.getCount(); ++i) {
            QueryAttributesBindValue queryAttribute = this.queryAttributesBindings.getAttributeValue(i);
            sizeOfEntireBatch += (long)(2 + queryAttribute.getName().length()) + queryAttribute.getBoundLength();
         }
      }

      for(i = 0; i < numBatchedArgs; ++i) {
         ClientPreparedQueryBindings qBindings = (ClientPreparedQueryBindings)this.batchedArgs.get(i);
         BindValue[] bindValues = qBindings.getBindValues();
         long sizeOfParameterSet = 0L;

         for(int j = 0; j < bindValues.length; ++j) {
            if (!bindValues[j].isNull()) {
               if (bindValues[j].isStream()) {
                  long streamLength = bindValues[j].getStreamLength();
                  if (streamLength != -1L) {
                     sizeOfParameterSet += streamLength * 2L;
                  } else {
                     int paramLength = ((ClientPreparedQueryBindValue[])qBindings.getBindValues())[j].getByteValue().length;
                     sizeOfParameterSet += (long)paramLength;
                  }
               } else {
                  sizeOfParameterSet += (long)((ClientPreparedQueryBindValue[])qBindings.getBindValues())[j].getByteValue().length;
               }
            } else {
               sizeOfParameterSet += 4L;
            }
         }

         if (this.parseInfo.getValuesClause() != null) {
            sizeOfParameterSet += (long)(this.parseInfo.getValuesClause().length() + 1);
         } else {
            sizeOfParameterSet += (long)(this.originalSql.length() + 1);
         }

         sizeOfEntireBatch += sizeOfParameterSet;
         if (sizeOfParameterSet > maxSizeOfParameterSet) {
            maxSizeOfParameterSet = sizeOfParameterSet;
         }
      }

      return new long[]{maxSizeOfParameterSet, sizeOfEntireBatch};
   }
}
