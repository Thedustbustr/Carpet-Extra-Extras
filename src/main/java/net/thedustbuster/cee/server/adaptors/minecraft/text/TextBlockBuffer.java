package net.thedustbuster.cee.server.adaptors.minecraft.text;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextBlockBuffer {
  private final List<Component> lines = new ArrayList<>();

  private List<Component> getLines() {
    return this.lines;
  }

  public TextBlockBuffer merge(TextBlockBuffer builder) {
    this.lines.addAll(builder.getLines());
    return this;
  }

  public TextBlockBuffer addLine(TextBuffer tb) {
    lines.add(tb.build());
    return this;
  }

  public TextBlockBuffer addLine(Component component) {
    lines.add(component);
    return this;
  }

  public TextBlockBuffer addLine(Function<TextBuffer, TextBuffer> fn) {
    lines.add(fn.apply(new TextBuffer()).build());
    return this;
  }

  public TextBlockBuffer addAllLines(List<Component> components) {
    components.forEach(this::addLine);
    return this;
  }

  public TextBlockBuffer addBlank() {
    lines.add(TextBuffer.blank());
    return this;
  }

  public List<Component> build() {
    return List.copyOf(lines);
  }

  public Component[] buildAsArray() {
    return build().toArray(new Component[0]);
  }

  public List<Component> buildWithNoItalics() {
    return lines.stream().map(c -> c.copy().withStyle(__ -> __.withItalic(false))).collect(Collectors.toUnmodifiableList());
  }
}