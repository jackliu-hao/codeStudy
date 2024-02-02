package cn.hutool.core.lang;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import java.lang.reflect.Type;

public interface Segment<T extends Number> {
   T getStartIndex();

   T getEndIndex();

   default T length() {
      T start = (Number)Assert.notNull(this.getStartIndex(), "Start index must be not null!");
      T end = (Number)Assert.notNull(this.getEndIndex(), "End index must be not null!");
      return (Number)Convert.convert((Type)start.getClass(), NumberUtil.sub(end, start).abs());
   }
}
