package freemarker.core;

public class _DelayedAOrAn extends _DelayedConversionToString {
   public _DelayedAOrAn(Object object) {
      super(object);
   }

   protected String doConversion(Object obj) {
      String s = obj.toString();
      return _MessageUtil.getAOrAn(s) + " " + s;
   }
}
