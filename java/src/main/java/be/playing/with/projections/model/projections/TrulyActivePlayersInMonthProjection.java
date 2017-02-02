package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Active = has finished at least five games in one month, and answered at least one question
 */
public class TrulyActivePlayersInMonthProjection implements Projection<Integer> {

  private final YearMonth month;

  public TrulyActivePlayersInMonthProjection(YearMonth month) {
    this.month = month;
  }

  @Override
  public Integer project(List<Event> events) {
    Map<String, Set<String>> playerIdsByGameId = new HashMap<>();
    Map<String, AtomicInteger> gameCountByPlayerId = new HashMap<>();

    for (Event event : events) {

      if (event.getType() == EventType.ANSWER_WAS_GIVEN) {
        String playerId = event.getPayload().get("player_id");
        String gameId = event.getPayload().get("game_id");
        playerIdsByGameId.computeIfAbsent(gameId, k -> new HashSet<>())
            .add(playerId);
      }

      if (event.getType() == EventType.GAME_WAS_FINISHED && YearMonth.from(event.getTimestamp()).equals(month)) {
        String gameId = event.getPayload().get("game_id");
        Set<String> playerIds = playerIdsByGameId.get(gameId);
        for (String playerId : playerIds) {
          gameCountByPlayerId.computeIfAbsent(playerId, k -> new AtomicInteger(0))
              .incrementAndGet();
        }
      }
    }

    int activePlayers = (int) gameCountByPlayerId.entrySet().stream()
        .filter(e -> e.getValue().get() >= 5)
        .count();
    return activePlayers;
  }

  @Override
  public String buildResultMessage(Integer projectResult) {
    return projectResult + " truly active players in " + month;
  }
}
