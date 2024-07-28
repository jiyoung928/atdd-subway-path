package nextstep.subway.domain.section;

import java.util.ArrayList;
import java.util.List;

public class SectionStationSorter {
    public List<Long> getSortedStationIds(final List<Section> sections) {
        List<Long> stationIds = new ArrayList<>();

        Long currentStationId = getFisrtUpStation(sections).getUpStationId();
        stationIds.add(currentStationId);
        for (Section section : sections) {
            if(!section.isFirst()){
                stationIds.add(section.getUpStationId());
            }
            if(section.isLast()){
                stationIds.add(section.getDownStationId());

            }
        }
        return stationIds;
    }

    public Section getFisrtUpStation(final List<Section> sections) {
        return sections.stream()
                .filter(Section::isFirst)
                .findAny()
                .orElse(null);
    }
}
