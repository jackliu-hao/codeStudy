/*     */ package javax.mail;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.mail.event.MailEvent;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class EventQueue
/*     */   implements Runnable
/*     */ {
/*     */   static class QueueElement
/*     */   {
/*  59 */     QueueElement next = null;
/*  60 */     QueueElement prev = null;
/*  61 */     MailEvent event = null;
/*  62 */     Vector vector = null;
/*     */     
/*     */     QueueElement(MailEvent event, Vector vector) {
/*  65 */       this.event = event;
/*  66 */       this.vector = vector;
/*     */     }
/*     */   }
/*     */   
/*  70 */   private QueueElement head = null;
/*  71 */   private QueueElement tail = null;
/*     */   private Thread qThread;
/*     */   
/*     */   public EventQueue() {
/*  75 */     this.qThread = new Thread(this, "JavaMail-EventQueue");
/*  76 */     this.qThread.setDaemon(true);
/*  77 */     this.qThread.start();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void enqueue(MailEvent event, Vector vector) {
/*  84 */     QueueElement newElt = new QueueElement(event, vector);
/*     */     
/*  86 */     if (this.head == null) {
/*  87 */       this.head = newElt;
/*  88 */       this.tail = newElt;
/*     */     } else {
/*  90 */       newElt.next = this.head;
/*  91 */       this.head.prev = newElt;
/*  92 */       this.head = newElt;
/*     */     } 
/*  94 */     notifyAll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized QueueElement dequeue() throws InterruptedException {
/* 107 */     while (this.tail == null)
/* 108 */       wait(); 
/* 109 */     QueueElement elt = this.tail;
/* 110 */     this.tail = elt.prev;
/* 111 */     if (this.tail == null) {
/* 112 */       this.head = null;
/*     */     } else {
/* 114 */       this.tail.next = null;
/*     */     } 
/* 116 */     elt.prev = elt.next = null;
/* 117 */     return elt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*     */       while (true) {
/* 129 */         QueueElement qe = dequeue();
/* 130 */         MailEvent e = qe.event;
/* 131 */         Vector v = qe.vector;
/*     */         
/* 133 */         for (int i = 0; i < v.size(); i++) {
/*     */           try {
/* 135 */             e.dispatch(v.elementAt(i));
/* 136 */           } catch (Throwable t) {
/* 137 */             if (t instanceof InterruptedException) {
/*     */               return;
/*     */             }
/*     */           } 
/*     */         } 
/* 142 */         qe = null; e = null; v = null;
/*     */       } 
/* 144 */     } catch (InterruptedException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void stop() {
/* 153 */     if (this.qThread != null) {
/* 154 */       this.qThread.interrupt();
/* 155 */       this.qThread = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\EventQueue.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */