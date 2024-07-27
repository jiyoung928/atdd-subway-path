package nextstep.subway.domain.section;

import nextstep.subway.common.exception.*;
import nextstep.subway.common.response.ErrorCode;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();


    public void add(Section section) {
        if(this.sections.isEmpty()){
            section.updateFirstSection(true);
            this.sections.add(section);
            return;
        }



        // 구간이 이미 노선에 포함되있는 경우
        Collection<Long> stationIds = getStationIds();
        if(getStationIds().contains(section.getDownStationId()) && getStationIds().contains(section.getUpStationId())) {
            throw new ExistStationException(ErrorCode.INVALID_STATION_ADD);
        }


        // 첫번째 역에 추가
        if(getFisrtUpStation().getId().equals(section.getDownStationId())){
            getFisrtUpStation().updateFirstSection(false);
            this.sections.add(section);
            return;
        }

        // 마지막 역에 추가
        if(getLastDownStationId().equals(section.getUpStationId())){
            this.sections.add(section);
            return;
        }


        Section origin = getOriginSection(section);
        origin.updateForNewSection(section);
        this.sections.add(section);

    }

    private Section getOriginSection(Section newSection) {
        Section section = null;

        for(Section originSection : this.sections) {
            if(originSection.getUpStationId().equals(newSection.getUpStationId())) {
                section = originSection;
            }
        }
        return section;
    }

    public Collection<Long> getStationIds() {
        List<Long> stationIds = sections.stream()
                .map(Section::getUpStationId)
                .collect(Collectors.toList());
        stationIds.add(getLastDownStationId());
        return stationIds;
    }
    public Long getLastDownStationId() {
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public Section getFisrtUpStation() {
        return sections.stream()
                .filter(Section::isFirst)
                .findAny()
                .orElse(null);
    }

    public void removeLastStation(Long stationId) {
        // 구간이 1개 이하인경우,
        if (this.sections.size() <= 1) {
            throw new InsufficientStationsException(ErrorCode.INSUFFICIENT_STATION_DELETE);
        }
        // 지하철 노선에 등록된 역(하행 종점역)이 아닌 경우
        if (!getLastDownStationId().equals(stationId)) {
            throw new NotLastStationException(ErrorCode.NOT_LAST_STATION_DELETE);
        }


        this.sections.remove(sections.size() - 1);

    }
}
