package org.wildfly.client.config;

import java.net.URI;
import javax.xml.stream.Location;

public final class XMLLocation implements Location, Comparable<XMLLocation> {
   public static final XMLLocation UNKNOWN = new XMLLocation((URI)null, -1, -1, -1);
   private final XMLLocation includedFrom;
   private final URI uri;
   private final int lineNumber;
   private final int columnNumber;
   private final int characterOffset;
   private final String publicId;
   private final String systemId;
   private int hashCode;

   public XMLLocation(XMLLocation includedFrom, URI uri, int lineNumber, int columnNumber, int characterOffset, String publicId, String systemId) {
      this.includedFrom = includedFrom;
      this.uri = uri;
      this.lineNumber = lineNumber;
      this.columnNumber = columnNumber;
      this.characterOffset = characterOffset;
      this.publicId = publicId;
      this.systemId = systemId;
   }

   public XMLLocation(URI uri, int lineNumber, int columnNumber, int characterOffset, String publicId, String systemId) {
      this((XMLLocation)null, uri, lineNumber, columnNumber, characterOffset, (String)null, (String)null);
   }

   public XMLLocation(XMLLocation includedFrom, URI uri, int lineNumber, int columnNumber, int characterOffset) {
      this(includedFrom, uri, lineNumber, columnNumber, characterOffset, (String)null, (String)null);
   }

   public XMLLocation(URI uri, int lineNumber, int columnNumber, int characterOffset) {
      this(uri, lineNumber, columnNumber, characterOffset, (String)null, (String)null);
   }

   public XMLLocation(URI uri) {
      this(uri, -1, -1, -1);
   }

   XMLLocation(XMLLocation includedFrom, URI uri, Location original) {
      this(includedFrom, uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
   }

   XMLLocation(URI uri, Location original) {
      this(uri, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
   }

   XMLLocation(Location original) {
      this(original instanceof XMLLocation ? ((XMLLocation)original).getUri() : null, original.getLineNumber(), original.getColumnNumber(), original.getCharacterOffset(), original.getPublicId(), original.getSystemId());
   }

   public URI getUri() {
      return this.uri;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public int getCharacterOffset() {
      return this.characterOffset;
   }

   public String getPublicId() {
      return this.publicId;
   }

   public String getSystemId() {
      return this.systemId;
   }

   public XMLLocation getIncludedFrom() {
      return this.includedFrom;
   }

   public static XMLLocation toXMLLocation(Location location) {
      return toXMLLocation((URI)null, location);
   }

   public static XMLLocation toXMLLocation(URI uri, Location location) {
      if (location instanceof XMLLocation) {
         return (XMLLocation)location;
      } else {
         return location == null ? UNKNOWN : new XMLLocation(uri, location);
      }
   }

   public static XMLLocation toXMLLocation(XMLLocation includedFrom, Location location) {
      return toXMLLocation(includedFrom, (URI)null, location);
   }

   public static XMLLocation toXMLLocation(XMLLocation includedFrom, URI uri, Location location) {
      if (location instanceof XMLLocation) {
         return (XMLLocation)location;
      } else {
         return location == null ? UNKNOWN : new XMLLocation(includedFrom, uri, location);
      }
   }

   public int hashCode() {
      int result = this.hashCode;
      if (result == 0) {
         if (this.includedFrom != null) {
            result = this.includedFrom.hashCode;
         }

         result = 31 * result + (this.uri != null ? this.uri.hashCode() : 0);
         result = 31 * result + this.lineNumber;
         result = 31 * result + this.columnNumber;
         result = 31 * result + this.characterOffset;
         result = 31 * result + (this.publicId != null ? this.publicId.hashCode() : 0);
         result = 31 * result + (this.systemId != null ? this.systemId.hashCode() : 0);
         if (result == 0) {
            result = -1;
         }

         this.hashCode = result;
      }

      return result;
   }

   public boolean equals(Object other) {
      return other instanceof XMLLocation && this.equals((XMLLocation)other);
   }

   private static boolean equals(Object a, Object b) {
      return a != b && a != null ? a.equals(b) : b == null;
   }

   public boolean equals(XMLLocation other) {
      return this == other || other != null && equals(this.includedFrom, other.includedFrom) && equals(this.uri, other.uri) && this.lineNumber == other.lineNumber && this.columnNumber == other.columnNumber && this.characterOffset == other.characterOffset && equals(this.publicId, other.publicId) && equals(this.systemId, other.systemId);
   }

   public String toString() {
      StringBuilder b = new StringBuilder();
      this.toString(b);
      return b.toString();
   }

   private void toString(StringBuilder b) {
      b.append("\n\tat ").append(this.uri == null ? "<input>" : this.uri);
      if (this.lineNumber > 0) {
         b.append(':').append(this.lineNumber);
         if (this.columnNumber > 0) {
            b.append(':').append(this.columnNumber);
         }
      }

      if (this.includedFrom != null) {
         this.includedFrom.toString(b);
      }

   }

   private int compareUri(URI a, URI b) {
      return a == null ? (b == null ? 0 : 1) : (b == null ? -1 : a.compareTo(b));
   }

   public int compareTo(XMLLocation o) {
      int c = this.compareUri(this.uri, o.uri);
      if (c == 0) {
         c = Integer.signum(this.lineNumber - o.lineNumber);
         if (c == 0) {
            c = Integer.signum(this.columnNumber - o.columnNumber);
            if (c == 0) {
               c = Integer.signum(this.characterOffset - o.characterOffset);
            }
         }
      }

      return c;
   }
}
