package com.beust.jcommander;

public interface IStringConverterFactory {
  <T> Class<? extends IStringConverter<T>> getConverter(Class<T> paramClass);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\beust\jcommander\IStringConverterFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */