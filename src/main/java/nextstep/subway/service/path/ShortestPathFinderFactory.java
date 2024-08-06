package nextstep.subway.service.path;

import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShortestPathFinderFactory {
    public static ShortestPathFinder createPathFinder(List<Section> allSections, List<Station> allStations) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        allStations.stream().map(Station::getId).forEach(graph::addVertex);
        allSections.forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStationId(), section.getDownStationId()),
                section.getDistance()
        ));
        return new ShortestPathFinder(graph);
    }
}
