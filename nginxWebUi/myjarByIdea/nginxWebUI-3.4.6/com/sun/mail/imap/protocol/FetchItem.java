package com.sun.mail.imap.protocol;

import com.sun.mail.iap.ParsingException;
import javax.mail.FetchProfile;

public abstract class FetchItem {
   private String name;
   private FetchProfile.Item fetchProfileItem;

   public FetchItem(String name, FetchProfile.Item fetchProfileItem) {
      this.name = name;
      this.fetchProfileItem = fetchProfileItem;
   }

   public String getName() {
      return this.name;
   }

   public FetchProfile.Item getFetchProfileItem() {
      return this.fetchProfileItem;
   }

   public abstract Object parseItem(FetchResponse var1) throws ParsingException;
}
