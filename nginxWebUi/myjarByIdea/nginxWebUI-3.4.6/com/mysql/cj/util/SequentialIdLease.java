package com.mysql.cj.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SequentialIdLease {
   private Set<Integer> sequentialIdsLease = new TreeSet();

   public int allocateSequentialId() {
      int nextSequentialId = 0;

      for(Iterator<Integer> it = this.sequentialIdsLease.iterator(); it.hasNext() && nextSequentialId + 1 == (Integer)it.next(); ++nextSequentialId) {
      }

      ++nextSequentialId;
      this.sequentialIdsLease.add(nextSequentialId);
      return nextSequentialId;
   }

   public void releaseSequentialId(int sequentialId) {
      this.sequentialIdsLease.remove(sequentialId);
   }
}
