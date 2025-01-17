package be.playing.with.projections;

import be.playing.with.projections.infra.FileEventStreamProvider;
import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventStreamProvider;
import be.playing.with.projections.model.Projection;
import be.playing.with.projections.model.projections.ActivePlayersInMonthProjection;
import be.playing.with.projections.model.projections.BotsWhoAnswerQuicklyProjection;
import be.playing.with.projections.model.projections.CountEventsProjection;
import be.playing.with.projections.model.projections.DistinctTypesProjection;
import be.playing.with.projections.model.projections.HowManyRegisteredPerMonthProjection;
import be.playing.with.projections.model.projections.HowManyRegisteredProjection;
import be.playing.with.projections.model.projections.MostPopularQuizzesInMonthProjection;
import be.playing.with.projections.model.projections.MostPopularQuizzesProjection;
import be.playing.with.projections.model.projections.TrulyActivePlayersInMonthProjection;

import java.time.YearMonth;
import java.util.List;

import static java.time.Month.JULY;
import static java.time.Month.JUNE;

class Main {

  public static void main(String[] args) throws Exception {
    // Switch between File based access and Remote rest here!
    EventStreamProvider streamProvider = new FileEventStreamProvider();
//    EventStreamProvider streamProvider = new RestEventStreamProvider();
    List<Event> events = streamProvider.loadResponses(getStream(args));

    run(events, new CountEventsProjection());
    run(events, new DistinctTypesProjection());
    run(events, new HowManyRegisteredProjection());
    run(events, new HowManyRegisteredPerMonthProjection());
    run(events, new MostPopularQuizzesProjection());
    run(events, new MostPopularQuizzesInMonthProjection(YearMonth.of(2016, JULY)));
    run(events, new ActivePlayersInMonthProjection(YearMonth.of(2016, JUNE)));
    run(events, new TrulyActivePlayersInMonthProjection(YearMonth.of(2016, JUNE)));
    run(events, new BotsWhoAnswerQuicklyProjection());
  }

  private static <T> void run(List<Event> events, Projection<T> projection) {
    System.out.println();
    System.out.println("     " + projection.getClass().getSimpleName());
    T result = projection.project(events);
    String resultMessage = projection.buildResultMessage(result);
    System.out.println(resultMessage);
  }

  private static String getStream(String[] args) {
    if (args.length < 1) {
      return getDefaultStream();
    } else {
      return args[0];
    }
  }

  private static String getDefaultStream() {
    System.out.println("A stream id was expected. Defaulting to Zero.");
    return "../data/0.json";
  }
}
