package javax.mail;

import java.util.Vector;
import javax.mail.event.MailEvent;

class EventQueue implements Runnable {
   private QueueElement head = null;
   private QueueElement tail = null;
   private Thread qThread = new Thread(this, "JavaMail-EventQueue");

   public EventQueue() {
      this.qThread.setDaemon(true);
      this.qThread.start();
   }

   public synchronized void enqueue(MailEvent event, Vector vector) {
      QueueElement newElt = new QueueElement(event, vector);
      if (this.head == null) {
         this.head = newElt;
         this.tail = newElt;
      } else {
         newElt.next = this.head;
         this.head.prev = newElt;
         this.head = newElt;
      }

      this.notifyAll();
   }

   private synchronized QueueElement dequeue() throws InterruptedException {
      while(this.tail == null) {
         this.wait();
      }

      QueueElement elt = this.tail;
      this.tail = elt.prev;
      if (this.tail == null) {
         this.head = null;
      } else {
         this.tail.next = null;
      }

      elt.prev = elt.next = null;
      return elt;
   }

   public void run() {
      label30:
      while(true) {
         try {
            QueueElement qe = this.dequeue();
            MailEvent e = qe.event;
            Vector v = qe.vector;
            int i = 0;

            while(true) {
               if (i >= v.size()) {
                  qe = null;
                  e = null;
                  v = null;
                  continue label30;
               }

               try {
                  e.dispatch(v.elementAt(i));
               } catch (Throwable var6) {
                  if (var6 instanceof InterruptedException) {
                     break;
                  }
               }

               ++i;
            }
         } catch (InterruptedException var7) {
         }

         return;
      }
   }

   void stop() {
      if (this.qThread != null) {
         this.qThread.interrupt();
         this.qThread = null;
      }

   }

   static class QueueElement {
      QueueElement next = null;
      QueueElement prev = null;
      MailEvent event = null;
      Vector vector = null;

      QueueElement(MailEvent event, Vector vector) {
         this.event = event;
         this.vector = vector;
      }
   }
}
