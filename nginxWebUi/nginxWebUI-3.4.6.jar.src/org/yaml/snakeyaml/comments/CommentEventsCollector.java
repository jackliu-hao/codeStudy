/*     */ package org.yaml.snakeyaml.comments;
/*     */ 
/*     */ import java.util.AbstractQueue;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import org.yaml.snakeyaml.events.CommentEvent;
/*     */ import org.yaml.snakeyaml.events.Event;
/*     */ import org.yaml.snakeyaml.parser.Parser;
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
/*     */ public class CommentEventsCollector
/*     */ {
/*     */   private List<CommentLine> commentLineList;
/*     */   private Queue<Event> eventSource;
/*     */   private CommentType[] expectedCommentTypes;
/*     */   
/*     */   public CommentEventsCollector(final Parser parser, CommentType... expectedCommentTypes) {
/*  45 */     this.eventSource = new AbstractQueue<Event>()
/*     */       {
/*     */         public boolean offer(Event e)
/*     */         {
/*  49 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public Event poll() {
/*  54 */           return parser.getEvent();
/*     */         }
/*     */ 
/*     */         
/*     */         public Event peek() {
/*  59 */           return parser.peekEvent();
/*     */         }
/*     */ 
/*     */         
/*     */         public Iterator<Event> iterator() {
/*  64 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public int size() {
/*  69 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */     
/*  73 */     this.expectedCommentTypes = expectedCommentTypes;
/*  74 */     this.commentLineList = new ArrayList<>();
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
/*     */   public CommentEventsCollector(Queue<Event> eventSource, CommentType... expectedCommentTypes) {
/*  87 */     this.eventSource = eventSource;
/*  88 */     this.expectedCommentTypes = expectedCommentTypes;
/*  89 */     this.commentLineList = new ArrayList<>();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isEventExpected(Event event) {
/* 100 */     if (event == null || !event.is(Event.ID.Comment)) {
/* 101 */       return false;
/*     */     }
/* 103 */     CommentEvent commentEvent = (CommentEvent)event;
/* 104 */     for (CommentType type : this.expectedCommentTypes) {
/* 105 */       if (commentEvent.getCommentType() == type) {
/* 106 */         return true;
/*     */       }
/*     */     } 
/* 109 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommentEventsCollector collectEvents() {
/* 119 */     collectEvents(null);
/* 120 */     return this;
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
/*     */   public Event collectEvents(Event event) {
/* 133 */     if (event != null) {
/* 134 */       if (isEventExpected(event)) {
/* 135 */         this.commentLineList.add(new CommentLine((CommentEvent)event));
/*     */       } else {
/* 137 */         return event;
/*     */       } 
/*     */     }
/* 140 */     while (isEventExpected(this.eventSource.peek())) {
/* 141 */       this.commentLineList.add(new CommentLine((CommentEvent)this.eventSource.poll()));
/*     */     }
/* 143 */     return null;
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
/*     */   public Event collectEventsAndPoll(Event event) {
/* 156 */     Event nextEvent = collectEvents(event);
/* 157 */     return (nextEvent != null) ? nextEvent : this.eventSource.poll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<CommentLine> consume() {
/*     */     try {
/* 167 */       return this.commentLineList;
/*     */     } finally {
/* 169 */       this.commentLineList = new ArrayList<>();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 178 */     return this.commentLineList.isEmpty();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\comments\CommentEventsCollector.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */