package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Look for e.g. players who join every game, answer very quickly, answer always correctly etc.
 */
public class BotsWhoAnswerQuicklyProjection implements Projection<Integer> {

  private static class QuestionStats {

    LocalDateTime questionAsked;
    Map<String, Duration> timeToAnswerByPlayerId = new HashMap<String, Duration>();
  }

  @Override
  public Integer project(List<Event> events) {
    Map<String, Map<String, QuestionStats>> gameIdToQuestionIdToStats = new HashMap<>();
    Map<String, List<Duration>> timesToAnswerByPlayerId = new HashMap<>();

    for (Event event : events) {

      if (event.getType() == EventType.QUESTION_WAS_ASKED) {
        String gameId = event.getPayload().get("game_id");
        String questionId = event.getPayload().get("question_id");
        QuestionStats stats = gameIdToQuestionIdToStats
            .computeIfAbsent(gameId, k -> new HashMap<>())
            .computeIfAbsent(questionId, k -> new QuestionStats());
        stats.questionAsked = event.getTimestamp();
      }

      if (event.getType() == EventType.ANSWER_WAS_GIVEN) {
        String gameId = event.getPayload().get("game_id");
        String questionId = event.getPayload().get("question_id");
        String playerId = event.getPayload().get("player_id");
        QuestionStats stats = gameIdToQuestionIdToStats
            .computeIfAbsent(gameId, k -> new HashMap<>())
            .computeIfAbsent(questionId, k -> new QuestionStats());
        Duration timeToAnswer = Duration.between(stats.questionAsked, event.getTimestamp());
        stats.timeToAnswerByPlayerId.put(playerId, timeToAnswer);
        timesToAnswerByPlayerId
            .computeIfAbsent(playerId, k -> new ArrayList<>())
            .add(timeToAnswer);
      }
    }

    for (Map.Entry<String, List<Duration>> e : timesToAnswerByPlayerId.entrySet()) {
//      System.out.println(e);
      String playerId = e.getKey();
      List<Duration> timesToAnswer = e.getValue();
      double[] seconds = timesToAnswer.stream()
          .mapToDouble(duration -> duration.toMillis() / 1000.0)
          .toArray();
      Statistics statistics = new Statistics(seconds);
      if (statistics.getVariance() < 5) {
        System.out.format("%1s\t%2.2f mean\t%3.2f median\t%4.2f variance\n",
            playerId,
            statistics.getMean(),
            statistics.median(),
            statistics.getVariance());
      }
    }

//    gameIdToQuestionIdToStats.values().stream()
//        .flatMap(questionIdToStats -> questionIdToStats.values().stream())
//        .flatMap(stats -> stats.timeToAnswerByPlayerId.entrySet().stream())
//        .forEach(e -> {
//          String playerId = e.getKey();
//          Duration timeToAnswer = e.getValue();
//
//        });
    return 0;
  }

  @Override
  public String buildResultMessage(Integer projectResult) {
    return projectResult + " possible bots";
  }
}
