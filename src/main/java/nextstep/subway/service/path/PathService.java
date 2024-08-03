package nextstep.subway.service.path;

import nextstep.subway.common.exception.NotExistStationException;
import nextstep.subway.common.exception.PathNotFoundException;
import nextstep.subway.common.exception.SameStationException;
import nextstep.subway.common.response.ErrorCode;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.path.SectionEdge;
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

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final ShortestPathFinder shortestPathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, ShortestPathFinder shortestPathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.shortestPathFinder = shortestPathFinder;
    }


    public PathResponse getShortestPath(Long source, Long target) {

        validateStation(source, target);
        List<SectionEdge> edges = lineRepository.findAll().stream()
                .flatMap(Line::sectionStream)
                .map(SectionEdge::new)
                .collect(Collectors.toList());

        var shortestPath = shortestPathFinder.find(edges, source, target)
                .orElseThrow(() -> new PathNotFoundException(ErrorCode.NOT_FOUND_PATH));

        List<Long> stationIds = shortestPath.getVertexList();
        Map<Long, Station> stationMap = getStationMap(stationIds);
        return new PathResponse(
                stationIds.stream()
                        .map(stationMap::get)
                        .map(StationResponse::createResponse)
                        .collect(Collectors.toList()),
                (long) shortestPath.getWeight()
        );
    }

    private void validateStation(Long source, Long target) {
        if(source.equals(target)){
            throw new SameStationException(ErrorCode.SAME_STATION);
        }
        if(!stationRepository.existsById(source) || !stationRepository.existsById(target)){
            throw new NotExistStationException(ErrorCode.NOT_FOUND_STATION);
        }
    }

    private Map<Long, Station> getStationMap(final List<Long> stationsIds) {
        return stationRepository.findByIdIn(stationsIds)
                .stream()
                .collect(Collectors.toMap(Station::getId, (station -> station)));
    }
}
