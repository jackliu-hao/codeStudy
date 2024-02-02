package cn.hutool.bloomfilter;

import java.io.Serializable;

public interface BloomFilter extends Serializable {
  boolean contains(String paramString);
  
  boolean add(String paramString);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\bloomfilter\BloomFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */