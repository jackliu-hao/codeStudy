package cn.hutool.core.builder;

import java.io.Serializable;

public interface Builder<T> extends Serializable {
   T build();
}
