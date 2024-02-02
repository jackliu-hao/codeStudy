package cn.hutool.bloomfilter.filter;

import cn.hutool.core.util.HashUtil;

public class DefaultFilter extends FuncFilter {
   private static final long serialVersionUID = 1L;

   public DefaultFilter(long maxValue) {
      this(maxValue, DEFAULT_MACHINE_NUM);
   }

   public DefaultFilter(long maxValue, int machineNumber) {
      super(maxValue, machineNumber, HashUtil::javaDefaultHash);
   }
}
