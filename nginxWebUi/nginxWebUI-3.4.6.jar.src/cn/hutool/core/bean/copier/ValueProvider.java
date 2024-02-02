package cn.hutool.core.bean.copier;

import java.lang.reflect.Type;

public interface ValueProvider<T> {
  Object value(T paramT, Type paramType);
  
  boolean containsKey(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\copier\ValueProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */