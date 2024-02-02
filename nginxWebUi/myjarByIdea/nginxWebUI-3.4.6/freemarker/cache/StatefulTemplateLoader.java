package freemarker.cache;

public interface StatefulTemplateLoader extends TemplateLoader {
   void resetState();
}
