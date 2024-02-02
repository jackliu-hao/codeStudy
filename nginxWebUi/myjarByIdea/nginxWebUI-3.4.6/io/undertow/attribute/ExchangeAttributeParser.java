package io.undertow.attribute;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

public class ExchangeAttributeParser {
   private final List<ExchangeAttributeBuilder> builders;
   private final List<ExchangeAttributeWrapper> wrappers;

   ExchangeAttributeParser(ClassLoader classLoader, List<ExchangeAttributeWrapper> wrappers) {
      this.wrappers = wrappers;
      ServiceLoader<ExchangeAttributeBuilder> loader = ServiceLoader.load(ExchangeAttributeBuilder.class, classLoader);
      List<ExchangeAttributeBuilder> builders = new ArrayList();
      Iterator var5 = loader.iterator();

      while(var5.hasNext()) {
         ExchangeAttributeBuilder instance = (ExchangeAttributeBuilder)var5.next();
         builders.add(instance);
      }

      Collections.sort(builders, new Comparator<ExchangeAttributeBuilder>() {
         public int compare(ExchangeAttributeBuilder o1, ExchangeAttributeBuilder o2) {
            return Integer.compare(o2.priority(), o1.priority());
         }
      });
      this.builders = Collections.unmodifiableList(builders);
   }

   public ExchangeAttribute parse(String valueString) {
      List<ExchangeAttribute> attributes = new ArrayList();
      int pos = 0;
      int state = 0;

      for(int i = 0; i < valueString.length(); ++i) {
         char c = valueString.charAt(i);
         switch (state) {
            case 0:
               if (c != '%' && c != '$') {
                  break;
               }

               if (pos != i) {
                  attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos, i))));
                  pos = i;
               }

               if (c == '%') {
                  state = 1;
               } else {
                  state = 3;
               }
               break;
            case 1:
               if (c == '{') {
                  state = 2;
               } else if (c == '%') {
                  attributes.add(this.wrap(new ConstantExchangeAttribute("%")));
                  pos = i + 1;
                  state = 0;
               } else {
                  attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos, i + 1))));
                  pos = i + 1;
                  state = 0;
               }
               break;
            case 2:
               if (c == '}') {
                  attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos, i + 1))));
                  pos = i + 1;
                  state = 0;
               }
               break;
            case 3:
               if (c == '{') {
                  state = 4;
               } else if (c == '$') {
                  attributes.add(this.wrap(new ConstantExchangeAttribute("$")));
                  pos = i + 1;
                  state = 0;
               } else {
                  attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos, i + 1))));
                  pos = i + 1;
                  state = 0;
               }
               break;
            case 4:
               if (c == '}') {
                  attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos, i + 1))));
                  pos = i + 1;
                  state = 0;
               }
         }
      }

      switch (state) {
         case 0:
         case 1:
         case 3:
            if (pos != valueString.length()) {
               attributes.add(this.wrap(this.parseSingleToken(valueString.substring(pos))));
            }
         default:
            if (attributes.size() == 1) {
               return (ExchangeAttribute)attributes.get(0);
            }

            return new CompositeExchangeAttribute((ExchangeAttribute[])attributes.toArray(new ExchangeAttribute[attributes.size()]));
         case 2:
         case 4:
            throw UndertowMessages.MESSAGES.mismatchedBraces(valueString);
      }
   }

   public ExchangeAttribute parseSingleToken(String token) {
      Iterator var2 = this.builders.iterator();

      ExchangeAttribute res;
      do {
         if (!var2.hasNext()) {
            if (token.startsWith("%")) {
               UndertowLogger.ROOT_LOGGER.unknownVariable(token);
            }

            return new ConstantExchangeAttribute(token);
         }

         ExchangeAttributeBuilder builder = (ExchangeAttributeBuilder)var2.next();
         res = builder.build(token);
      } while(res == null);

      return res;
   }

   private ExchangeAttribute wrap(ExchangeAttribute attribute) {
      ExchangeAttribute res = attribute;

      ExchangeAttributeWrapper w;
      for(Iterator var3 = this.wrappers.iterator(); var3.hasNext(); res = w.wrap(res)) {
         w = (ExchangeAttributeWrapper)var3.next();
      }

      return res;
   }
}
