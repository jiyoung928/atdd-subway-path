package nextstep.subway.unit.path;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.path.SectionEdge;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.service.path.ShortestPathFinder;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ShortestPathFinderTest {
    private final Long 교대역 = 1L;
    private final Long 강남역 = 2L;
    private final Long 양재역 = 3L;
    private final Long 남부터미널역 = 4L;
    private Line 이호선;
    private Line 신분당선;
    private Line 삼호선;


    /**
     * 교대역    --- *2호선* (10) ---    강남역
     * |                                 |
     * *3호선* (2)                   *신분당선* (10)
     * |                                 |
     * 남부터미널역  --- *3호선* (3) ---   양재
     */

    @BeforeEach
    void setUp() {
        이호선 = new Line("2호선", "green", new Section(교대역, 강남역, 10L));
        신분당선 = new Line("신분당선", "red", new Section(강남역, 양재역, 10L));
        삼호선 = new Line("3호선", "orange", new Section(교대역, 남부터미널역, 2L));
        삼호선.addSection(new Section(남부터미널역, 양재역, 3L));
    }


    @DisplayName("최단 거리를 반환한다")
    @Test
    void findShortestPath() {
        ShortestPathFinder pathFinder = new ShortestPathFinder();
        var lines = List.of(이호선, 신분당선, 삼호선);

        // 엣지 셋팅
        List<SectionEdge> edges = lines.stream()
                .flatMap(Line::sectionStream)
                .map(SectionEdge::new)
                .collect(Collectors.toList());

        Optional<GraphPath<Long, DefaultWeightedEdge>> pathResponse = pathFinder.find(edges, 교대역, 양재역);

        assertThat(pathResponse.get().getWeight()).isEqualTo(5);
    }

    @DisplayName("경로가 존재하지 않을 경우 빈값을 반환한다.")
    @Test
    void findNotFoundPath() {
        ShortestPathFinder pathFinder = new ShortestPathFinder();
        var 서울역 = new Station(5L, "서울역");
        var 수원역 = new Station(6L, "수원역");
        var 일호선 = new Line("1호선", "blue", new Section(서울역.getId(), 수원역.getId(), 10L));
        var lines = List.of(이호선, 신분당선, 삼호선, 일호선);

        // 엣지 셋팅
        List<SectionEdge> edges = lines.stream()
                .flatMap(Line::sectionStream)
                .map(SectionEdge::new)
                .collect(Collectors.toList());

        assertThat(pathFinder.find(edges, 서울역.getId(), 강남역).isEmpty()).isTrue();

    }

}
