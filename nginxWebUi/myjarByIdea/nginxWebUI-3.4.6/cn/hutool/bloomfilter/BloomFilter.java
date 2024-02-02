package cn.hutool.bloomfilter;

import java.io.Serializable;

public interface BloomFilter extends Serializable {
   boolean contains(String var1);

   boolean add(String var1);
}
