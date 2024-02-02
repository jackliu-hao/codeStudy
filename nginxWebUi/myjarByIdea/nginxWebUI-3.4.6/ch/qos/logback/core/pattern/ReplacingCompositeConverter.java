package ch.qos.logback.core.pattern;

import java.util.List;
import java.util.regex.Pattern;

public class ReplacingCompositeConverter<E> extends CompositeConverter<E> {
   Pattern pattern;
   String regex;
   String replacement;

   public void start() {
      List<String> optionList = this.getOptionList();
      if (optionList == null) {
         this.addError("at least two options are expected whereas you have declared none");
      } else {
         int numOpts = optionList.size();
         if (numOpts < 2) {
            this.addError("at least two options are expected whereas you have declared only " + numOpts + "as [" + optionList + "]");
         } else {
            this.regex = (String)optionList.get(0);
            this.pattern = Pattern.compile(this.regex);
            this.replacement = (String)optionList.get(1);
            super.start();
         }
      }
   }

   protected String transform(E event, String in) {
      return !this.started ? in : this.pattern.matcher(in).replaceAll(this.replacement);
   }
}
