package net.mightybyte.gigs;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GigsLogFormatter extends Formatter {

  @Override
  public String format(LogRecord record) {
    return record.getMessage();
  }

}
