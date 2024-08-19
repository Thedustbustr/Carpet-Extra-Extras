package net.thedustbuster.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class TextBuilder {
  private final List<FormattedText> formattedTexts = new ArrayList<>();

  public TextBuilder addText(String text, List<ChatFormatting> formatting) {
    formattedTexts.add(new FormattedText(text, formatting));
    return this;
  }

  public Component build() {
    Component combinedComponent = Component.literal("");

    for (FormattedText formattedText : formattedTexts) {
      String text = formattedText.text();
      List<ChatFormatting> formatting = formattedText.formatting();
      Component textComponent = Component.literal(text);

      for (ChatFormatting format : formatting) { textComponent = textComponent.copy().withStyle(format); }

      combinedComponent = combinedComponent.copy().append(textComponent);
    }

    return combinedComponent;
  }

  // Internal class for holding text and formatting
  private record FormattedText(String text, List<ChatFormatting> formatting) {
    private FormattedText(String text, List<ChatFormatting> formatting) {
      this.text = text != null ? text : "";
      this.formatting = formatting != null ? formatting : List.of(ChatFormatting.WHITE);
    }
  }
}
