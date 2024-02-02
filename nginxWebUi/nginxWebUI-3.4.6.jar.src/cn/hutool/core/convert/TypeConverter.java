package cn.hutool.core.convert;

import java.lang.reflect.Type;

@FunctionalInterface
public interface TypeConverter {
  Object convert(Type paramType, Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\TypeConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */