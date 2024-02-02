package io.undertow.server.handlers.form;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FormParserFactory {
   private static final AttachmentKey<FormDataParser> ATTACHMENT_KEY = AttachmentKey.create(FormDataParser.class);
   private final ParserDefinition[] parserDefinitions;

   FormParserFactory(List<ParserDefinition> parserDefinitions) {
      this.parserDefinitions = (ParserDefinition[])parserDefinitions.toArray(new ParserDefinition[parserDefinitions.size()]);
   }

   public FormDataParser createParser(HttpServerExchange exchange) {
      FormDataParser existing = (FormDataParser)exchange.getAttachment(ATTACHMENT_KEY);
      if (existing != null) {
         return existing;
      } else {
         for(int i = 0; i < this.parserDefinitions.length; ++i) {
            FormDataParser parser = this.parserDefinitions[i].create(exchange);
            if (parser != null) {
               exchange.putAttachment(ATTACHMENT_KEY, parser);
               return parser;
            }
         }

         return null;
      }
   }

   public static Builder builder() {
      return builder(true);
   }

   public static Builder builder(boolean includeDefault) {
      Builder builder = new Builder();
      if (includeDefault) {
         builder.addParsers(new FormEncodedDataDefinition(), new MultiPartParserDefinition());
      }

      return builder;
   }

   public static class Builder {
      private List<ParserDefinition> parsers = new ArrayList();
      private String defaultCharset = null;

      public Builder addParser(ParserDefinition definition) {
         this.parsers.add(definition);
         return this;
      }

      public Builder addParsers(ParserDefinition... definition) {
         this.parsers.addAll(Arrays.asList(definition));
         return this;
      }

      public Builder addParsers(List<ParserDefinition> definition) {
         this.parsers.addAll(definition);
         return this;
      }

      public List<ParserDefinition> getParsers() {
         return this.parsers;
      }

      public void setParsers(List<ParserDefinition> parsers) {
         this.parsers = parsers;
      }

      public Builder withParsers(List<ParserDefinition> parsers) {
         this.setParsers(parsers);
         return this;
      }

      public String getDefaultCharset() {
         return this.defaultCharset;
      }

      public void setDefaultCharset(String defaultCharset) {
         this.defaultCharset = defaultCharset;
      }

      public Builder withDefaultCharset(String defaultCharset) {
         this.setDefaultCharset(defaultCharset);
         return this;
      }

      public FormParserFactory build() {
         if (this.defaultCharset != null) {
            Iterator var1 = this.parsers.iterator();

            while(var1.hasNext()) {
               ParserDefinition parser = (ParserDefinition)var1.next();
               parser.setDefaultEncoding(this.defaultCharset);
            }
         }

         return new FormParserFactory(this.parsers);
      }
   }

   public interface ParserDefinition<T> {
      FormDataParser create(HttpServerExchange var1);

      T setDefaultEncoding(String var1);
   }
}
