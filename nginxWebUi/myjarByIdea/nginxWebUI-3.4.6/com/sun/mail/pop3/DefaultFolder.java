package com.sun.mail.pop3;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;

public class DefaultFolder extends Folder {
   DefaultFolder(POP3Store store) {
      super(store);
   }

   public String getName() {
      return "";
   }

   public String getFullName() {
      return "";
   }

   public Folder getParent() {
      return null;
   }

   public boolean exists() {
      return true;
   }

   public Folder[] list(String pattern) throws MessagingException {
      Folder[] f = new Folder[]{this.getInbox()};
      return f;
   }

   public char getSeparator() {
      return '/';
   }

   public int getType() {
      return 2;
   }

   public boolean create(int type) throws MessagingException {
      return false;
   }

   public boolean hasNewMessages() throws MessagingException {
      return false;
   }

   public Folder getFolder(String name) throws MessagingException {
      if (!name.equalsIgnoreCase("INBOX")) {
         throw new MessagingException("only INBOX supported");
      } else {
         return this.getInbox();
      }
   }

   protected Folder getInbox() throws MessagingException {
      return this.getStore().getFolder("INBOX");
   }

   public boolean delete(boolean recurse) throws MessagingException {
      throw new MethodNotSupportedException("delete");
   }

   public boolean renameTo(Folder f) throws MessagingException {
      throw new MethodNotSupportedException("renameTo");
   }

   public void open(int mode) throws MessagingException {
      throw new MethodNotSupportedException("open");
   }

   public void close(boolean expunge) throws MessagingException {
      throw new MethodNotSupportedException("close");
   }

   public boolean isOpen() {
      return false;
   }

   public Flags getPermanentFlags() {
      return new Flags();
   }

   public int getMessageCount() throws MessagingException {
      return 0;
   }

   public Message getMessage(int msgno) throws MessagingException {
      throw new MethodNotSupportedException("getMessage");
   }

   public void appendMessages(Message[] msgs) throws MessagingException {
      throw new MethodNotSupportedException("Append not supported");
   }

   public Message[] expunge() throws MessagingException {
      throw new MethodNotSupportedException("expunge");
   }
}
