package freemarker.core;

final class LocalContextStack {
   private LocalContext[] buffer = new LocalContext[8];
   private int size;

   void push(LocalContext localContext) {
      int newSize = ++this.size;
      LocalContext[] buffer = this.buffer;
      if (buffer.length < newSize) {
         LocalContext[] newBuffer = new LocalContext[newSize * 2];

         for(int i = 0; i < buffer.length; ++i) {
            newBuffer[i] = buffer[i];
         }

         buffer = newBuffer;
         this.buffer = newBuffer;
      }

      buffer[newSize - 1] = localContext;
   }

   void pop() {
      this.buffer[--this.size] = null;
   }

   public LocalContext get(int index) {
      return this.buffer[index];
   }

   public int size() {
      return this.size;
   }
}
