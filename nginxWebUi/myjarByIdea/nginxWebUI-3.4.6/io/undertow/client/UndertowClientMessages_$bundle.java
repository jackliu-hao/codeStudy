package io.undertow.client;

import io.undertow.util.HttpString;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

public class UndertowClientMessages_$bundle implements UndertowClientMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final UndertowClientMessages_$bundle INSTANCE = new UndertowClientMessages_$bundle();
   private static final Locale LOCALE;

   protected UndertowClientMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String connectionClosed$str() {
      return "UT001000: Connection closed";
   }

   public final String connectionClosed() {
      return String.format(this.getLoggingLocale(), this.connectionClosed$str());
   }

   protected String requestAlreadyWritten$str() {
      return "UT001001: Request already written";
   }

   public final IllegalStateException requestAlreadyWritten() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.requestAlreadyWritten$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String failedToUpgradeChannel$str() {
      return "UT001020: Failed to upgrade channel due to response %s (%s)";
   }

   public final String failedToUpgradeChannel(int responseCode, String reason) {
      return String.format(this.getLoggingLocale(), this.failedToUpgradeChannel$str(), responseCode, reason);
   }

   protected String illegalContentLength$str() {
      return "UT001030: invalid content length %d";
   }

   public final IllegalArgumentException illegalContentLength(long length) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.illegalContentLength$str(), length));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unknownScheme$str() {
      return "UT001031: Unknown scheme in URI %s";
   }

   public final IllegalArgumentException unknownScheme(URI uri) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.unknownScheme$str(), uri));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unknownTransferEncoding$str() {
      return "UT001032: Unknown transfer encoding %s";
   }

   public final IOException unknownTransferEncoding(String transferEncodingString) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unknownTransferEncoding$str(), transferEncodingString));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidConnectionState$str() {
      return "UT001033: Invalid connection state";
   }

   public final IOException invalidConnectionState() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.invalidConnectionState$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unknownAjpMessageType$str() {
      return "UT001034: Unknown AJP packet type %s";
   }

   public final IOException unknownAjpMessageType(byte packetType) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unknownAjpMessageType$str(), packetType));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unknownMethod$str() {
      return "UT001035: Unknown method type for AJP request %s";
   }

   public final IOException unknownMethod(HttpString method) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.unknownMethod$str(), method));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String dataStillRemainingInChunk$str() {
      return "UT001036: Data still remaining in chunk %s";
   }

   public final IOException dataStillRemainingInChunk(long remaining) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.dataStillRemainingInChunk$str(), remaining));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String wrongMagicNumber$str() {
      return "UT001037: Wrong magic number, expected %s, actual %s";
   }

   public final IOException wrongMagicNumber(String expected, String actual) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.wrongMagicNumber$str(), expected, actual));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String receivedInvalidChunk$str() {
      return "UT001038: Received invalid AJP chunk %s with response already complete";
   }

   public final IOException receivedInvalidChunk(byte prefix) {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.receivedInvalidChunk$str(), prefix));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
