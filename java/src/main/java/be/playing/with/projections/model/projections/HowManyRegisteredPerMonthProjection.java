package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HowManyRegisteredPerMonthProjection implements Projection<Map<YearMonth, Integer>> {

  @Override
  public Map<YearMonth, Integer> project(List<Event> events) {
    Map<YearMonth, Integer> result = new TreeMap<>();
    for (Event event : events) {
      if (event.getType() == EventType.PLAYER_HAS_REGISTERED) {
        YearMonth month = YearMonth.from(event.getTimestamp());
        Integer count = result.computeIfAbsent(month, k -> 0);
        result.put(month, count + 1);
      }
    }
    return result;
  }

  @Override
  public String buildResultMessage(Map<YearMonth, Integer> projectResult) {
    return projectResult + " players registered per month";
  }
}
