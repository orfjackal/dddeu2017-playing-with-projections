package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.util.List;

public class HowManyRegisteredProjection implements Projection<Integer> {

  @Override
  public Integer project(List<Event> events) {
    int count = 0;
    for (Event event : events) {
      if (event.getType() == EventType.PLAYER_HAS_REGISTERED) {
        count++;
      }
    }
    return count;
  }

  @Override
  public String buildResultMessage(Integer projectResult) {
    return projectResult + " players registered";
  }
}
