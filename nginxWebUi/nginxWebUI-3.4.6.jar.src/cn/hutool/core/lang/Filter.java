package cn.hutool.core.lang;

@FunctionalInterface
public interface Filter<T> {
  boolean accept(T paramT);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */