package oshi.hardware.common;

import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;

@Immutable
public abstract class AbstractSoundCard implements SoundCard {
   private String kernelVersion;
   private String name;
   private String codec;

   protected AbstractSoundCard(String kernelVersion, String name, String codec) {
      this.kernelVersion = kernelVersion;
      this.name = name;
      this.codec = codec;
   }

   public String getDriverVersion() {
      return this.kernelVersion;
   }

   public String getName() {
      return this.name;
   }

   public String getCodec() {
      return this.codec;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("SoundCard@");
      builder.append(Integer.toHexString(this.hashCode()));
      builder.append(" [name=");
      builder.append(this.name);
      builder.append(", kernelVersion=");
      builder.append(this.kernelVersion);
      builder.append(", codec=");
      builder.append(this.codec);
      builder.append(']');
      return builder.toString();
   }
}
