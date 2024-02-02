package com.sun.mail.imap;

import com.sun.mail.imap.protocol.BODYSTRUCTURE;
import java.util.Vector;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;

public class IMAPMultipartDataSource extends MimePartDataSource implements MultipartDataSource {
   private Vector parts;

   protected IMAPMultipartDataSource(MimePart part, BODYSTRUCTURE[] bs, String sectionId, IMAPMessage msg) {
      super(part);
      this.parts = new Vector(bs.length);

      for(int i = 0; i < bs.length; ++i) {
         this.parts.addElement(new IMAPBodyPart(bs[i], sectionId == null ? Integer.toString(i + 1) : sectionId + "." + Integer.toString(i + 1), msg));
      }

   }

   public int getCount() {
      return this.parts.size();
   }

   public BodyPart getBodyPart(int index) throws MessagingException {
      return (BodyPart)this.parts.elementAt(index);
   }
}
