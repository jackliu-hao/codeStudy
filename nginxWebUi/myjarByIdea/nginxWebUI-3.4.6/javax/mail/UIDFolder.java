package javax.mail;

public interface UIDFolder {
   long LASTUID = -1L;

   long getUIDValidity() throws MessagingException;

   Message getMessageByUID(long var1) throws MessagingException;

   Message[] getMessagesByUID(long var1, long var3) throws MessagingException;

   Message[] getMessagesByUID(long[] var1) throws MessagingException;

   long getUID(Message var1) throws MessagingException;

   public static class FetchProfileItem extends FetchProfile.Item {
      public static final FetchProfileItem UID = new FetchProfileItem("UID");

      protected FetchProfileItem(String name) {
         super(name);
      }
   }
}
