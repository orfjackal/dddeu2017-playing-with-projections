package be.playing.with.projections;

import be.playing.with.projections.infra.FileEventStreamProvider;
import be.playing.with.projections.model.EventStreamProvider;
import be.playing.with.projections.model.projections.CountEventsProjection;
import be.playing.with.projections.model.projections.DistinctTypesProjection;

class Main {

    public static void main(String[] args) throws Exception {
        // Switch between File based access and Remote rest here!
        EventStreamProvider streamProvider = new FileEventStreamProvider();
//    EventStreamProvider streamProvider = new RestEventStreamProvider();

        {
            CountEventsProjection projection = new CountEventsProjection();
            System.out.println(projection.buildResultMessage(
                    projection.project(streamProvider.loadResponses(getStream(args)))));
        }
        {
            DistinctTypesProjection projection = new DistinctTypesProjection();
            System.out.println(projection.buildResultMessage(
                    projection.project(streamProvider.loadResponses(getStream(args)))));
        }
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
