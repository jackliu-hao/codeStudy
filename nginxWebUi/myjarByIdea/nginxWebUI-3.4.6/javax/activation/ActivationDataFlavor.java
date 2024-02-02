package javax.activation;

import java.awt.datatransfer.DataFlavor;

public class ActivationDataFlavor extends DataFlavor {
   private String mimeType = null;
   private MimeType mimeObject = null;
   private String humanPresentableName = null;
   private Class representationClass = null;

   public ActivationDataFlavor(Class representationClass, String mimeType, String humanPresentableName) {
      super(mimeType, humanPresentableName);
      this.mimeType = mimeType;
      this.humanPresentableName = humanPresentableName;
      this.representationClass = representationClass;
   }

   public ActivationDataFlavor(Class representationClass, String humanPresentableName) {
      super(representationClass, humanPresentableName);
      this.mimeType = super.getMimeType();
      this.representationClass = representationClass;
      this.humanPresentableName = humanPresentableName;
   }

   public ActivationDataFlavor(String mimeType, String humanPresentableName) {
      super(mimeType, humanPresentableName);
      this.mimeType = mimeType;

      try {
         this.representationClass = Class.forName("java.io.InputStream");
      } catch (ClassNotFoundException var4) {
      }

      this.humanPresentableName = humanPresentableName;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public Class getRepresentationClass() {
      return this.representationClass;
   }

   public String getHumanPresentableName() {
      return this.humanPresentableName;
   }

   public void setHumanPresentableName(String humanPresentableName) {
      this.humanPresentableName = humanPresentableName;
   }

   public boolean equals(DataFlavor dataFlavor) {
      return this.isMimeTypeEqual(dataFlavor) && dataFlavor.getRepresentationClass() == this.representationClass;
   }

   public boolean isMimeTypeEqual(String mimeType) {
      MimeType mt = null;

      try {
         if (this.mimeObject == null) {
            this.mimeObject = new MimeType(this.mimeType);
         }

         mt = new MimeType(mimeType);
      } catch (MimeTypeParseException var4) {
      }

      return this.mimeObject.match(mt);
   }

   /** @deprecated */
   protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
      return parameterValue;
   }

   /** @deprecated */
   protected String normalizeMimeType(String mimeType) {
      return mimeType;
   }
}
