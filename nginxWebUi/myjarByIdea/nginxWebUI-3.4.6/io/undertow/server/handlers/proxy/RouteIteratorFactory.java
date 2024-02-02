package io.undertow.server.handlers.proxy;

import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RouteIteratorFactory {
   private final RouteParsingStrategy routeParsingStrategy;
   private final ParsingCompatibility parsingCompatibility;
   private final String rankedRouteDelimiter;

   public RouteIteratorFactory(RouteParsingStrategy routeParsingStrategy, ParsingCompatibility parsingCompatibility) {
      this(routeParsingStrategy, parsingCompatibility, (String)null);
   }

   public RouteIteratorFactory(RouteParsingStrategy routeParsingStrategy, ParsingCompatibility parsingCompatibility, String rankedRouteDelimiter) {
      if (routeParsingStrategy == RouteParsingStrategy.RANKED && rankedRouteDelimiter == null) {
         throw new IllegalArgumentException();
      } else {
         this.routeParsingStrategy = routeParsingStrategy;
         this.parsingCompatibility = parsingCompatibility;
         this.rankedRouteDelimiter = rankedRouteDelimiter;
      }
   }

   public Iterator<CharSequence> iterator(String sessionId) {
      return new RouteIterator(sessionId);
   }

   private class RouteIterator implements Iterator<CharSequence> {
      private final String sessionId;
      private boolean nextResolved;
      private int nextPos;
      private CharSequence next;

      RouteIterator(String sessionId) {
         this.sessionId = sessionId;
         if (RouteIteratorFactory.this.routeParsingStrategy == RouteParsingStrategy.NONE) {
            this.nextResolved = true;
            this.next = null;
         } else {
            int index = sessionId == null ? -1 : sessionId.indexOf(46);
            if (index == -1) {
               this.nextResolved = true;
               this.next = null;
            } else {
               this.nextPos = index + 1;
            }
         }

      }

      public boolean hasNext() {
         this.resolveNext();
         return this.next != null;
      }

      public CharSequence next() {
         this.resolveNext();
         if (this.next != null) {
            CharSequence result = this.next;
            this.nextResolved = RouteIteratorFactory.this.routeParsingStrategy != RouteParsingStrategy.RANKED;
            this.next = null;
            return result;
         } else {
            throw new NoSuchElementException();
         }
      }

      private void resolveNext() {
         if (!this.nextResolved) {
            int last;
            if (RouteIteratorFactory.this.routeParsingStrategy != RouteParsingStrategy.RANKED) {
               if (RouteIteratorFactory.this.parsingCompatibility == RouteIteratorFactory.ParsingCompatibility.MOD_JK) {
                  last = this.sessionId.indexOf(46, this.nextPos);
                  if (last == -1) {
                     last = this.sessionId.length();
                  }

                  this.next = CharBuffer.wrap(this.sessionId, this.nextPos, last);
               } else {
                  this.next = CharBuffer.wrap(this.sessionId, this.nextPos, this.sessionId.length());
               }
            } else if (this.nextPos >= this.sessionId.length()) {
               this.next = null;
            } else {
               last = this.sessionId.indexOf(RouteIteratorFactory.this.rankedRouteDelimiter, this.nextPos);
               this.next = CharBuffer.wrap(this.sessionId, this.nextPos, last != -1 ? last : this.sessionId.length());
               this.nextPos += this.next.length() + RouteIteratorFactory.this.rankedRouteDelimiter.length();
            }

            this.nextResolved = true;
         }

      }
   }

   public static enum ParsingCompatibility {
      MOD_JK,
      MOD_CLUSTER;
   }
}
