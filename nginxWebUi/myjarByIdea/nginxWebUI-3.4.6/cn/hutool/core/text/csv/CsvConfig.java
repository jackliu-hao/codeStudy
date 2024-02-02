package cn.hutool.core.text.csv;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class CsvConfig<T extends CsvConfig<T>> implements Serializable {
   private static final long serialVersionUID = -8069578249066158459L;
   protected char fieldSeparator = ',';
   protected char textDelimiter = '"';
   protected Character commentCharacter = '#';
   protected Map<String, String> headerAlias = new LinkedHashMap();

   public T setFieldSeparator(char fieldSeparator) {
      this.fieldSeparator = fieldSeparator;
      return this;
   }

   public T setTextDelimiter(char textDelimiter) {
      this.textDelimiter = textDelimiter;
      return this;
   }

   public T disableComment() {
      return this.setCommentCharacter((Character)null);
   }

   public T setCommentCharacter(Character commentCharacter) {
      this.commentCharacter = commentCharacter;
      return this;
   }

   public T setHeaderAlias(Map<String, String> headerAlias) {
      this.headerAlias = headerAlias;
      return this;
   }

   public T addHeaderAlias(String header, String alias) {
      this.headerAlias.put(header, alias);
      return this;
   }

   public T removeHeaderAlias(String header) {
      this.headerAlias.remove(header);
      return this;
   }
}
