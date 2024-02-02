package javax.mail.event;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;

public class TransportEvent extends MailEvent {
   public static final int MESSAGE_DELIVERED = 1;
   public static final int MESSAGE_NOT_DELIVERED = 2;
   public static final int MESSAGE_PARTIALLY_DELIVERED = 3;
   protected int type;
   protected transient Address[] validSent;
   protected transient Address[] validUnsent;
   protected transient Address[] invalid;
   protected transient Message msg;
   private static final long serialVersionUID = -4729852364684273073L;

   public TransportEvent(Transport transport, int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
      super(transport);
      this.type = type;
      this.validSent = validSent;
      this.validUnsent = validUnsent;
      this.invalid = invalid;
      this.msg = msg;
   }

   public int getType() {
      return this.type;
   }

   public Address[] getValidSentAddresses() {
      return this.validSent;
   }

   public Address[] getValidUnsentAddresses() {
      return this.validUnsent;
   }

   public Address[] getInvalidAddresses() {
      return this.invalid;
   }

   public Message getMessage() {
      return this.msg;
   }

   public void dispatch(Object listener) {
      if (this.type == 1) {
         ((TransportListener)listener).messageDelivered(this);
      } else if (this.type == 2) {
         ((TransportListener)listener).messageNotDelivered(this);
      } else {
         ((TransportListener)listener).messagePartiallyDelivered(this);
      }

   }
}
