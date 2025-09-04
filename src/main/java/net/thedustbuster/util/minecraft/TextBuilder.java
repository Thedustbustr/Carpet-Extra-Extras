package net.thedustbuster.util.minecraft;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TextBuilder {
  private final List<FormattedText> formattedTexts = new ArrayList<>();

  private Component buildText() {
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

  public TextBuilder addText(String text, List<ChatFormatting> formatting) {
    formattedTexts.add(new FormattedText(text, formatting));
    return this;
  }

  public TextBuilder addText(String text, ChatFormatting formatting) {
    formattedTexts.add(new FormattedText(text, List.of(formatting)));
    return this;
  }

  public Component build() {
    return buildText();
  }

  public Supplier<Component> buildAsSupplier() {
    return this::buildText;
  }

  // Internal class for holding text and formatting
  private record FormattedText(String text, List<ChatFormatting> formatting) {
    private FormattedText(String text, List<ChatFormatting> formatting) {
      this.text = text != null ? text : "";
      this.formatting = formatting != null ? formatting : List.of(ChatFormatting.WHITE);
    }
  }
}
