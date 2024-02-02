package org.h2.mvstore.rtree;

public interface Spatial {
  float min(int paramInt);
  
  void setMin(int paramInt, float paramFloat);
  
  float max(int paramInt);
  
  void setMax(int paramInt, float paramFloat);
  
  Spatial clone(long paramLong);
  
  long getId();
  
  boolean isNull();
  
  boolean equalsIgnoringId(Spatial paramSpatial);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\rtree\Spatial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */