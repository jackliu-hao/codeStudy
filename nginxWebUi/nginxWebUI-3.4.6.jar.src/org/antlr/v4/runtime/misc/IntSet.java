package org.antlr.v4.runtime.misc;

import java.util.List;

public interface IntSet {
  void add(int paramInt);
  
  IntSet addAll(IntSet paramIntSet);
  
  IntSet and(IntSet paramIntSet);
  
  IntSet complement(IntSet paramIntSet);
  
  IntSet or(IntSet paramIntSet);
  
  IntSet subtract(IntSet paramIntSet);
  
  int size();
  
  boolean isNil();
  
  boolean equals(Object paramObject);
  
  int getSingleElement();
  
  boolean contains(int paramInt);
  
  void remove(int paramInt);
  
  List<Integer> toList();
  
  String toString();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\IntSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */