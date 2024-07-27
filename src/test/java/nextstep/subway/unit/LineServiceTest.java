package nextstep.subway.unit;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.service.line.LineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;


    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Long 강남역 = stationRepository.save(new Station("강남역")).getId();
        Long 양재역 = stationRepository.save(new Station("양재역")).getId();
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));

        // when
        // lineService.addSection 호출
        신분당선.addSection(new Section(강남역, 양재역, 10L));

        Collection<Long> test = 신분당선.getStationIds();
        // then
        // line.getSections 메서드를 통해 검증
        assertThat(신분당선.getStationIds()).containsAll(List.of(강남역, 양재역));

    }
}
