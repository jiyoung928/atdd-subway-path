package nextstep.subway.service.section;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.SectionRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.line.LineResponse;
import nextstep.subway.dto.section.SectionRequest;
import nextstep.subway.service.line.LineService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class SectionService {
    private final SectionRepository sectionRepository;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final LineService lineService;

    public SectionService(SectionRepository sectionRepository, LineRepository lineRepository, StationRepository stationRepository, LineService lineService) {
        this.sectionRepository = sectionRepository;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.lineService = lineService;
    }

    @Transactional
    public void saveSection(Long lineId, SectionRequest sectionRequest) {

        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId())
                .orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId())
                .orElseThrow(IllegalArgumentException::new);

        line.addSection(new Section(line, upStation.getId(), downStation.getId(), sectionRequest.getDistance()));
    }

    @Transactional
    public LineResponse deleteSection(Long id, Long stationId) {

        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.removeLastStation(stationId);
        return LineResponse.createResponse(line, lineService.getLineStations(line));

    }

}
