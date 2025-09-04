package net.thedustbuster.util;

import net.thedustbuster.CarpetExtraExtrasServer;
import net.thedustbuster.util.func.Unit;

import static net.thedustbuster.util.func.Unit.Unit;

public final class Logger {
  public static Unit info(String str) {
    return Unit(() -> CarpetExtraExtrasServer.LOGGER.info(str));
  }

  public static Unit warn(String str) {
    return Unit(() -> CarpetExtraExtrasServer.LOGGER.warn(str));
  }

  public static Unit error(String str) {
    return Unit(() -> CarpetExtraExtrasServer.LOGGER.error(str));
  }

  public static Unit error(String str, Exception e) {
    return Unit(() -> CarpetExtraExtrasServer.LOGGER.error(String.format("%s Exception: %s", str, e.getMessage())));
  }
}
