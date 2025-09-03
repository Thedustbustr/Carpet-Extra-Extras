package net.thedustbuster.util.func.checked;

@FunctionalInterface
public interface CheckedFunction<T, S> {
  S apply(T t) throws Exception;
}