package io.undertow.util;

import io.undertow.UndertowLogger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ByteRange {
   private final List<Range> ranges;

   public ByteRange(List<Range> ranges) {
      this.ranges = ranges;
   }

   public int getRanges() {
      return this.ranges.size();
   }

   public long getStart(int range) {
      return ((Range)this.ranges.get(range)).getStart();
   }

   public long getEnd(int range) {
      return ((Range)this.ranges.get(range)).getEnd();
   }

   public static ByteRange parse(String rangeHeader) {
      if (rangeHeader != null && rangeHeader.length() >= 7) {
         if (!rangeHeader.startsWith("bytes=")) {
            return null;
         } else {
            List<Range> ranges = new ArrayList();
            String[] parts = rangeHeader.substring(6).split(",");
            String[] var3 = parts;
            int var4 = parts.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String part = var3[var5];

               try {
                  int index = part.indexOf(45);
                  long start;
                  if (index == 0) {
                     start = Long.parseLong(part.substring(1));
                     if (start < 0L) {
                        UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
                        return null;
                     }

                     ranges.add(new Range(-1L, start));
                  } else {
                     if (index == -1) {
                        UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
                        return null;
                     }

                     start = Long.parseLong(part.substring(0, index));
                     if (start < 0L) {
                        UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
                        return null;
                     }

                     long end;
                     if (index + 1 < part.length()) {
                        end = Long.parseLong(part.substring(index + 1));
                     } else {
                        end = -1L;
                     }

                     ranges.add(new Range(start, end));
                  }
               } catch (NumberFormatException var12) {
                  UndertowLogger.REQUEST_LOGGER.debugf("Invalid range spec %s", rangeHeader);
                  return null;
               }
            }

            return ranges.isEmpty() ? null : new ByteRange(ranges);
         }
      } else {
         return null;
      }
   }

   public RangeResponseResult getResponseResult(long resourceContentLength, String ifRange, Date lastModified, String eTag) {
      if (this.ranges.isEmpty()) {
         return null;
      } else {
         long start = this.getStart(0);
         long end = this.getEnd(0);
         if (ifRange != null && !ifRange.isEmpty()) {
            if (ifRange.charAt(0) == '"') {
               if (eTag != null && !eTag.equals(ifRange)) {
                  return null;
               }
            } else {
               Date ifDate = DateUtils.parseDate(ifRange);
               if (ifDate != null && lastModified != null && ifDate.getTime() < lastModified.getTime()) {
                  return null;
               }
            }
         }

         long rangeLength;
         if (start == -1L) {
            if (end < 0L) {
               return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
            }

            start = Math.max(resourceContentLength - end, 0L);
            end = resourceContentLength - 1L;
            rangeLength = resourceContentLength - start;
         } else if (end == -1L) {
            long toWrite = resourceContentLength - start;
            if (toWrite <= 0L) {
               return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
            }

            rangeLength = toWrite;
            end = resourceContentLength - 1L;
         } else {
            end = Math.min(end, resourceContentLength - 1L);
            if (start >= resourceContentLength || start > end) {
               return new RangeResponseResult(0L, 0L, 0L, "bytes */" + resourceContentLength, 416);
            }

            rangeLength = end - start + 1L;
         }

         return new RangeResponseResult(start, end, rangeLength, "bytes " + start + "-" + end + "/" + resourceContentLength, 206);
      }
   }

   public static class Range {
      private final long start;
      private final long end;

      public Range(long start, long end) {
         this.start = start;
         this.end = end;
      }

      public long getStart() {
         return this.start;
      }

      public long getEnd() {
         return this.end;
      }
   }

   public static class RangeResponseResult {
      private final long start;
      private final long end;
      private final long contentLength;
      private final String contentRange;
      private final int statusCode;

      public RangeResponseResult(long start, long end, long contentLength, String contentRange, int statusCode) {
         this.start = start;
         this.end = end;
         this.contentLength = contentLength;
         this.contentRange = contentRange;
         this.statusCode = statusCode;
      }

      public long getStart() {
         return this.start;
      }

      public long getEnd() {
         return this.end;
      }

      public long getContentLength() {
         return this.contentLength;
      }

      public String getContentRange() {
         return this.contentRange;
      }

      public int getStatusCode() {
         return this.statusCode;
      }
   }
}
