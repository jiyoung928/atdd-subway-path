package nextstep.subway.service.path;

import nextstep.subway.domain.path.SectionEdge;
import nextstep.subway.domain.section.Section;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ShortestPathFinder {
    private final WeightedMultigraph<Long, DefaultWeightedEdge> graph;

    public ShortestPathFinder() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
    }

    public Optional<GraphPath<Long, DefaultWeightedEdge>> find(final List<SectionEdge> edges, Long source, Long target) {
        addEdges(edges);
        setWeight(edges);

        DijkstraShortestPath<Long, DefaultWeightedEdge> shortestPath = new DijkstraShortestPath<>(graph);

        return Optional.ofNullable(shortestPath.getPath(source, target));
    }
    private void addEdges(final List<SectionEdge> edges) {
        edges.stream()
                .flatMap(it -> Stream.of(it.getTargetVertex(), it.getSourceVertex()))
                .distinct()
                .forEach(graph::addVertex);
    }
    private void setWeight(final List<SectionEdge> edges) {
        edges.forEach(it -> {
            DefaultWeightedEdge edge = graph.addEdge(it.getSourceVertex(), it.getTargetVertex());
            graph.setEdgeWeight(edge, it.getWeight());
        });
    }

}
