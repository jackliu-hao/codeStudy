package com.beust.jcommander;

public interface IStringConverterFactory {
   <T> Class<? extends IStringConverter<T>> getConverter(Class<T> var1);
}
