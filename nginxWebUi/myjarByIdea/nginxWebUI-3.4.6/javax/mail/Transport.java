package javax.mail;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

public abstract class Transport extends Service {
   private volatile Vector transportListeners = null;

   public Transport(Session session, URLName urlname) {
      super(session, urlname);
   }

   public static void send(Message msg) throws MessagingException {
      msg.saveChanges();
      send0(msg, msg.getAllRecipients());
   }

   public static void send(Message msg, Address[] addresses) throws MessagingException {
      msg.saveChanges();
      send0(msg, addresses);
   }

   private static void send0(Message msg, Address[] addresses) throws MessagingException {
      if (addresses != null && addresses.length != 0) {
         Hashtable protocols = new Hashtable();
         Vector invalid = new Vector();
         Vector validSent = new Vector();
         Vector validUnsent = new Vector();

         int dsize;
         for(dsize = 0; dsize < addresses.length; ++dsize) {
            Vector v;
            if (protocols.containsKey(addresses[dsize].getType())) {
               v = (Vector)protocols.get(addresses[dsize].getType());
               v.addElement(addresses[dsize]);
            } else {
               v = new Vector();
               v.addElement(addresses[dsize]);
               protocols.put(addresses[dsize].getType(), v);
            }
         }

         dsize = protocols.size();
         if (dsize == 0) {
            throw new SendFailedException("No recipient addresses");
         } else {
            Session s = msg.session != null ? msg.session : Session.getDefaultInstance(System.getProperties(), (Authenticator)null);
            Transport transport;
            if (dsize == 1) {
               transport = s.getTransport(addresses[0]);

               try {
                  transport.connect();
                  transport.sendMessage(msg, addresses);
               } finally {
                  transport.close();
               }

            } else {
               MessagingException chainedEx = null;
               boolean sendFailed = false;
               Enumeration e = protocols.elements();

               while(true) {
                  Address[] b;
                  while(e.hasMoreElements()) {
                     Vector v = (Vector)e.nextElement();
                     b = new Address[v.size()];
                     v.copyInto(b);
                     if ((transport = s.getTransport(b[0])) == null) {
                        for(int j = 0; j < b.length; ++j) {
                           invalid.addElement(b[j]);
                        }
                     } else {
                        try {
                           transport.connect();
                           transport.sendMessage(msg, b);
                        } catch (SendFailedException var28) {
                           sendFailed = true;
                           if (chainedEx == null) {
                              chainedEx = var28;
                           } else {
                              ((MessagingException)chainedEx).setNextException(var28);
                           }

                           Address[] a = var28.getInvalidAddresses();
                           int k;
                           if (a != null) {
                              for(k = 0; k < a.length; ++k) {
                                 invalid.addElement(a[k]);
                              }
                           }

                           a = var28.getValidSentAddresses();
                           if (a != null) {
                              for(k = 0; k < a.length; ++k) {
                                 validSent.addElement(a[k]);
                              }
                           }

                           Address[] c = var28.getValidUnsentAddresses();
                           if (c != null) {
                              for(int l = 0; l < c.length; ++l) {
                                 validUnsent.addElement(c[l]);
                              }
                           }
                        } catch (MessagingException var29) {
                           sendFailed = true;
                           if (chainedEx == null) {
                              chainedEx = var29;
                           } else {
                              ((MessagingException)chainedEx).setNextException(var29);
                           }
                        } finally {
                           transport.close();
                        }
                     }
                  }

                  if (!sendFailed && invalid.size() == 0 && validUnsent.size() == 0) {
                     return;
                  }

                  Address[] a = null;
                  b = null;
                  Address[] c = null;
                  if (validSent.size() > 0) {
                     a = new Address[validSent.size()];
                     validSent.copyInto(a);
                  }

                  if (validUnsent.size() > 0) {
                     b = new Address[validUnsent.size()];
                     validUnsent.copyInto(b);
                  }

                  if (invalid.size() > 0) {
                     c = new Address[invalid.size()];
                     invalid.copyInto(c);
                  }

                  throw new SendFailedException("Sending failed", (Exception)chainedEx, a, b, c);
               }
            }
         }
      } else {
         throw new SendFailedException("No recipient addresses");
      }
   }

   public abstract void sendMessage(Message var1, Address[] var2) throws MessagingException;

   public synchronized void addTransportListener(TransportListener l) {
      if (this.transportListeners == null) {
         this.transportListeners = new Vector();
      }

      this.transportListeners.addElement(l);
   }

   public synchronized void removeTransportListener(TransportListener l) {
      if (this.transportListeners != null) {
         this.transportListeners.removeElement(l);
      }

   }

   protected void notifyTransportListeners(int type, Address[] validSent, Address[] validUnsent, Address[] invalid, Message msg) {
      if (this.transportListeners != null) {
         TransportEvent e = new TransportEvent(this, type, validSent, validUnsent, invalid, msg);
         this.queueEvent(e, this.transportListeners);
      }
   }
}
