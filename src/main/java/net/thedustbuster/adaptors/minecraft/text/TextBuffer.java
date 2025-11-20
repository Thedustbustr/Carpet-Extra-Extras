package net.thedustbuster.adaptors.minecraft.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.thedustbuster.libs.core.tuple.Pair;
import net.thedustbuster.libs.func.Attempt;
import net.thedustbuster.libs.func.option.Option;

import java.util.ArrayList;
import java.util.List;

public class TextBuffer {
  private final List<FormattedText> formattedTexts = new ArrayList<>();

  private List<FormattedText> getAllText() {
    return this.formattedTexts;
  }

  public static Option<ChatFormatting> getFormattingFromString(String str) {
    return Attempt.create(() -> ChatFormatting.valueOf(str.toUpperCase())).toOption();
  }

  public static String getTranslation(String key) {
    return Component.translatable(key).getString();
  }

  public static Component blank() {
    return Component.empty();
  }

  public TextBuffer merge(TextBuffer builder) {
    this.formattedTexts.addAll(builder.getAllText());
    return this;
  }

  public TextBuffer addAllText(List<Pair<String, List<ChatFormatting>>> lines) {
    lines.forEach(line -> this.addText(line._1(), line._2()));
    return this;
  }

  public TextBuffer addText(String text, List<ChatFormatting> formatting) {
    formattedTexts.add(new FormattedText(text, formatting));
    return this;
  }

  public TextBuffer addText(String text, ChatFormatting formatting) {
    formattedTexts.add(new FormattedText(text, List.of(formatting)));
    return this;
  }

  public TextBuffer addText(String text) {
    formattedTexts.add(new FormattedText(text, List.of(ChatFormatting.WHITE)));
    return this;
  }

  public Component buildWithNoItalics() {
    return build().copy().withStyle(__ -> __.withItalic(false));
  }

  public Component build() {
    Component finalComponentText = Component.empty();

    for (FormattedText formattedText : formattedTexts) {
      Component textComponent = Component.literal(formattedText.text());
      List<ChatFormatting> formatting = formattedText.formatting();

      for (ChatFormatting format : formatting) { textComponent = textComponent.copy().withStyle(format); }

      finalComponentText = finalComponentText.copy().append(textComponent);
    }

    return finalComponentText;
  }

  // Internal class for holding text and formatting
  private record FormattedText(String text, List<ChatFormatting> formatting) {
    private FormattedText(String text, List<ChatFormatting> formatting) {
      this.text = text != null ? text : "";
      this.formatting = formatting != null ? formatting : List.of(ChatFormatting.WHITE);
    }
  }
}
