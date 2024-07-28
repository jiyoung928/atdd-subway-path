package nextstep.subway.dto.station;

import nextstep.subway.domain.station.Station;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StationResponse createResponse(Station line) {
        return new StationResponse(
                line.getId(),
                line.getName()
        );
    }
}
