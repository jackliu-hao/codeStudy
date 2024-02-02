package freemarker.core;

import freemarker.template.utility.CollectionUtils;

class TemplateElements {
   static final TemplateElements EMPTY = new TemplateElements((TemplateElement[])null, 0);
   private final TemplateElement[] buffer;
   private final int count;

   TemplateElements(TemplateElement[] buffer, int count) {
      this.buffer = buffer;
      this.count = count;
   }

   TemplateElement[] getBuffer() {
      return this.buffer;
   }

   int getCount() {
      return this.count;
   }

   TemplateElement getFirst() {
      return this.buffer != null ? this.buffer[0] : null;
   }

   TemplateElement getLast() {
      return this.buffer != null ? this.buffer[this.count - 1] : null;
   }

   TemplateElement asSingleElement() {
      if (this.count == 0) {
         return new TextBlock(CollectionUtils.EMPTY_CHAR_ARRAY, false);
      } else {
         TemplateElement first = this.buffer[0];
         if (this.count == 1) {
            return first;
         } else {
            MixedContent mixedContent = new MixedContent();
            mixedContent.setChildren(this);
            mixedContent.setLocation(first.getTemplate(), first, this.getLast());
            return mixedContent;
         }
      }
   }

   MixedContent asMixedContent() {
      MixedContent mixedContent = new MixedContent();
      if (this.count != 0) {
         TemplateElement first = this.buffer[0];
         mixedContent.setChildren(this);
         mixedContent.setLocation(first.getTemplate(), first, this.getLast());
      }

      return mixedContent;
   }
}
