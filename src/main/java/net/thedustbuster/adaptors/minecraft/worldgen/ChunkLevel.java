package net.thedustbuster.adaptors.minecraft.worldgen;

public enum ChunkLevel {
  ENTITY_TICKING(Integer.MIN_VALUE, 31),
  LAZY(32, 32),
  BORDER(33, 33),
  UNLOADED(34, Integer.MAX_VALUE);

  private final int minValue;
  private final int maxValue;

  ChunkLevel(int minValue, int maxValue) {
    this.minValue = minValue;
    this.maxValue = maxValue;
  }

  public boolean contains(int value) {
    return value >= minValue && value <= maxValue;
  }

  public static ChunkLevel fromInt(int value) {
    for (ChunkLevel state : values()) {
      if (state.contains(value)) return state;
    }
    throw new IllegalArgumentException("No matching ChunkLevel for value: " + value);
  }
}