package nextstep.subway.unit.path;

import nextstep.subway.common.exception.NotExistStationException;
import nextstep.subway.common.exception.PathNotFoundException;
import nextstep.subway.common.exception.SameStationException;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.service.path.PathService;
import nextstep.subway.service.path.ShortestPathFinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.Same;


import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathServiceTest {

    private PathService pathService;
    private LineRepository lineRepository;
    private StationRepository stationRepository;
    private ShortestPathFinder shortestPathFinder;

    private Station 교대역;
    private Station 강남역;
    private Station 양재역;
    private Station 남부터미널역;

    private List<Station> 전체역;

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
        교대역 = new Station(1L, "교대역");
        강남역 = new Station(2L, "강남역");
        양재역 = new Station(3L, "양재역");
        남부터미널역 = new Station(4L, "남부터미널역");
        전체역 = List.of(교대역, 강남역, 양재역, 남부터미널역);

        이호선 = new Line("2호선", "green", new Section(교대역.getId(), 강남역.getId(), 10L));
        신분당선 = new Line("신분당선", "red", new Section(강남역.getId(), 양재역.getId(), 10L));
        삼호선 = new Line("3호선", "orange", new Section(교대역.getId(), 남부터미널역.getId(), 2L));
        삼호선.addSection(new Section(남부터미널역.getId(), 양재역.getId(), 3L));

        shortestPathFinder = new ShortestPathFinder();
        stationRepository = mock(StationRepository.class);
        lineRepository = mock(LineRepository.class);
        pathService = new PathService( lineRepository, stationRepository,shortestPathFinder);
    }

    @Test
    @DisplayName("최단 거리와 경로를 반환한다.")
    void findShortestPath() {
        when(stationRepository.findByIdIn(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(this::findStationById)
                            .collect(Collectors.toList());
                });

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

        when(stationRepository.existsById(교대역.getId())).thenReturn(true);
        when(stationRepository.existsById(양재역.getId())).thenReturn(true);

        PathResponse shortestPath = pathService.getShortestPath(교대역.getId(), 양재역.getId());
        assertAll(
                () -> assertThat(shortestPath.getDistance()).isEqualTo(5L),
                () -> assertThat(shortestPath.getStations().stream().map(StationResponse::getName).collect(Collectors.toList()))
                        .containsExactly(교대역.getName(), 남부터미널역.getName(), 양재역.getName())
        );
    }

    @Test
    @DisplayName("출발역과 도착역이 같은 경우 예외를 발생시킨다.")
    void findShortestPathSameStationException() {

        assertThrows(SameStationException.class,
                () -> pathService.getShortestPath(교대역.getId(), 교대역.getId()));
    }

    @Test
    @DisplayName("출발역과 도착역이 연결되있지 않은 경우 예외를 발생시킨다.")
    void findShortestPathNotConnectedException() {
        var 서울역 = new Station(5L, "서울역");
        var 수원역 = new Station(6L, "수원역");
        var 일호선 = new Line("1호선", "blue", new Section(서울역.getId(), 수원역.getId(), 10L));

        전체역 = List.of(교대역, 강남역, 양재역, 남부터미널역, 서울역, 수원역);

        when(stationRepository.findByIdIn(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(this::findStationById)
                            .collect(Collectors.toList());
                });

        when(stationRepository.existsById(서울역.getId())).thenReturn(true);
        when(stationRepository.existsById(강남역.getId())).thenReturn(true);

        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선, 일호선));

        assertThrows(PathNotFoundException.class,
                () -> pathService.getShortestPath(서울역.getId(), 강남역.getId()));
    }

    @Test
    @DisplayName("존재하지 않은 출발역이나 도착역을 조회 할 경우 예외를 발생시킨다.")
    void findShortestPathNotExistException() {
        var 임시역 = 5L;

        when(stationRepository.findByIdIn(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(this::findStationById)
                            .collect(Collectors.toList());
                });


        when(lineRepository.findAll())
                .thenReturn(List.of(이호선, 신분당선, 삼호선));

        assertThrows(NotExistStationException.class,
                () -> pathService.getShortestPath(교대역.getId(), 임시역));
    }
    private Station findStationById(final Long id) {
        return 전체역.stream().filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
