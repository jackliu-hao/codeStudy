package cn.hutool.bloomfilter.bitMap;

public interface BitMap {
  public static final int MACHINE32 = 32;
  
  public static final int MACHINE64 = 64;
  
  void add(long paramLong);
  
  boolean contains(long paramLong);
  
  void remove(long paramLong);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\bitMap\BitMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */