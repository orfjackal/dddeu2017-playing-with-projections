package be.playing.with.projections.model.projections;

import be.playing.with.projections.model.Event;
import be.playing.with.projections.model.EventType;
import be.playing.with.projections.model.Projection;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DistinctTypesProjection implements Projection<Set<EventType>> {

    @Override
    public Set<EventType> project(List<Event> events) {
        Set<EventType> types = new TreeSet<>();
        for (Event event : events) {
            types.add(event.getType());
        }
        return types;
    }

    @Override
    public String buildResultMessage(Set<EventType> projectResult) {
        return projectResult + " distinct types found in the stream";
    }
}

