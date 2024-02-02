package io.undertow.servlet.api;

public interface ClassIntrospecter {
  <T> InstanceFactory<T> createInstanceFactory(Class<T> paramClass) throws NoSuchMethodException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ClassIntrospecter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */