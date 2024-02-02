package javax.mail.internet;

import com.sun.mail.util.FolderClosedIOException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownServiceException;
import javax.activation.DataSource;
import javax.mail.FolderClosedException;
import javax.mail.MessageAware;
import javax.mail.MessageContext;
import javax.mail.MessagingException;

public class MimePartDataSource implements DataSource, MessageAware {
   protected MimePart part;
   private MessageContext context;

   public MimePartDataSource(MimePart part) {
      this.part = part;
   }

   public InputStream getInputStream() throws IOException {
      try {
         InputStream is;
         if (this.part instanceof MimeBodyPart) {
            is = ((MimeBodyPart)this.part).getContentStream();
         } else {
            if (!(this.part instanceof MimeMessage)) {
               throw new MessagingException("Unknown part");
            }

            is = ((MimeMessage)this.part).getContentStream();
         }

         String encoding = MimeBodyPart.restrictEncoding(this.part, this.part.getEncoding());
         return encoding != null ? MimeUtility.decode(is, encoding) : is;
      } catch (FolderClosedException var3) {
         throw new FolderClosedIOException(var3.getFolder(), var3.getMessage());
      } catch (MessagingException var4) {
         throw new IOException(var4.getMessage());
      }
   }

   public OutputStream getOutputStream() throws IOException {
      throw new UnknownServiceException("Writing not supported");
   }

   public String getContentType() {
      try {
         return this.part.getContentType();
      } catch (MessagingException var2) {
         return "application/octet-stream";
      }
   }

   public String getName() {
      try {
         if (this.part instanceof MimeBodyPart) {
            return ((MimeBodyPart)this.part).getFileName();
         }
      } catch (MessagingException var2) {
      }

      return "";
   }

   public synchronized MessageContext getMessageContext() {
      if (this.context == null) {
         this.context = new MessageContext(this.part);
      }

      return this.context;
   }
}
