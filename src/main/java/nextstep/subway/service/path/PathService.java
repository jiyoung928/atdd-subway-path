package nextstep.subway.service.path;

import nextstep.subway.common.exception.NotExistStationException;
import nextstep.subway.common.exception.PathNotFoundException;
import nextstep.subway.common.exception.SameStationException;
import nextstep.subway.common.response.ErrorCode;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.path.SectionEdge;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.SectionRepository;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.dto.path.PathResponse;
import nextstep.subway.dto.station.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public PathService(StationRepository stationRepository, SectionRepository sectionRepository) {
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public PathResponse getShortestPath(Long source, Long target) {

        validateStation(source, target);
        List<Section> sectionList = sectionRepository.findAll();
        List<Station> stationList = stationRepository.findAll();
        var shortestPath = ShortestPathFinderFactory.createPathFinder(sectionList, stationList);

        return shortestPath.find(source, target, stationList);
    }

    private void validateStation(Long source, Long target) {
        if(source.equals(target)){
            throw new SameStationException(ErrorCode.SAME_STATION);
        }
        if(!stationRepository.existsById(source) || !stationRepository.existsById(target)){
            throw new NotExistStationException(ErrorCode.NOT_FOUND_STATION);
        }
    }

}
