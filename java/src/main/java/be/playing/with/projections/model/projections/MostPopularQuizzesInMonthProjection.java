package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Popular = How many times a quiz is played
 */
public class MostPopularQuizzesInMonthProjection implements Projection<List<String>> {

  private final YearMonth month;

  public MostPopularQuizzesInMonthProjection(YearMonth month) {
    this.month = month;
  }

  @Override
  public List<String> project(List<Event> events) {
    Map<String, String> quizTitlesByQuizId = new HashMap<>();
    Map<String, Integer> timesPlayedByQuizId = new HashMap<>();

    for (Event event : events) {

      if (event.getType() == EventType.QUIZ_WAS_CREATED) {
        String quizId = event.getPayload().get("quiz_id");
        String quizTitle = event.getPayload().get("quiz_title");
        quizTitlesByQuizId.put(quizId, quizTitle);
      }

      if (event.getType() == EventType.GAME_WAS_OPENED && YearMonth.from(event.getTimestamp()).equals(month)) {
        String quizId = event.getPayload().get("quiz_id");
        Integer count = timesPlayedByQuizId.computeIfAbsent(quizId, k -> 0);
        timesPlayedByQuizId.put(quizId, count + 1);
      }
    }
    List<String> mostPopularTitles = timesPlayedByQuizId.entrySet().stream()
        .sorted(Comparator.comparingInt((ToIntFunction<Map.Entry<String, Integer>>) Map.Entry::getValue).reversed())
        .limit(10)
        .map(Map.Entry::getKey)
        .map(quizTitlesByQuizId::get)
        .collect(Collectors.toList());

    return mostPopularTitles;
  }

  @Override
  public String buildResultMessage(List<String> projectResult) {
    return projectResult + " were the most popular quizzes in " + month;
  }
}
