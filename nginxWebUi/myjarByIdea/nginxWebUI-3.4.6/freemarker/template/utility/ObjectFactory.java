package freemarker.template.utility;

public interface ObjectFactory<T> {
   T createObject() throws Exception;
}
