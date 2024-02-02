package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;

public class text_html extends text_plain {
   private static ActivationDataFlavor myDF;

   protected ActivationDataFlavor getDF() {
      return myDF;
   }

   static {
      myDF = new ActivationDataFlavor(String.class, "text/html", "HTML String");
   }
}
