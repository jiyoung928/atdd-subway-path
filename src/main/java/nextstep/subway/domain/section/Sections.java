package nextstep.subway.domain.section;

import nextstep.subway.common.exception.*;
import nextstep.subway.common.response.ErrorCode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Transient
    private final SectionStationSorter sectionStationSorter = new SectionStationSorter();

    public void add(Section section) {
        if(this.sections.isEmpty()){
            section.updateFirstSection(true);
            section.updateLastSection(true);
            this.sections.add(section);
            return;
        }

        // 구간이 이미 노선에 포함되있는 경우
        if(getStationIds().contains(section.getDownStationId()) && getStationIds().contains(section.getUpStationId())) {
            throw new ExistStationException(ErrorCode.INVALID_STATION_ADD);
        }
        // 추가되는 거리가 1보다 작은 경우
        if(section.getDistance() < 1){
            throw new SectionDistanceNotValidException(ErrorCode.INVALID_DISTANCE_ADD);

        }

        // 첫번째 역에 추가
        if(getFirstUpStation().getId().equals(section.getDownStationId())){
            getFirstUpStation().updateFirstSection(false);
            section.updateFirstSection(true);
            this.sections.add(section);
            return;
        }

        // 마지막 역에 추가
        if(getLastDownStationId().equals(section.getUpStationId())){
            getLastDownStation().updateLastSection(false);
            section.updateLastSection(true);
            this.sections.add(section);
            return;
        }


        Section origin = getOriginSection(section);
        Long originDownStationId = origin.getDownStationId();
        origin.updateForNewSection(section);
        section.updateNewSection(originDownStationId);
        if(origin.isLast()){
            origin.updateLastSection(false);
            section.updateLastSection(true);
        }
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

    public Section getLastDownStation() {
        return sections.get(sections.size() - 1);
    }

    public Long getLastDownStationId() {
        return sections.get(sections.size() - 1).getDownStationId();
    }

    public Section getFirstUpStation() {
        return sections.stream()
                .filter(Section::isFirst)
                .findAny()
                .orElse(null);
    }

    public Section findByUpStationId(Long upStationId) {
        return sections.stream()
                .filter(section -> section.getUpStationId().equals(upStationId))
                .findAny()
                .orElse(null);
    }
    public Section findByDownStationId(Long downStationId) {
        return sections.stream()
                .filter(section -> section.getDownStationId().equals(downStationId))
                .findAny()
                .orElse(null);
    }

    public void removeStation(Long stationId) {
        // 구간이 1개 이하인경우,
        if (this.sections.size() <= 1) {
            throw new InsufficientStationsException(ErrorCode.INSUFFICIENT_STATION_DELETE);
        }

        // 첫번째 역 삭제
        if(getFirstUpStation().getId().equals(stationId)){
            findByUpStationId(getFirstUpStation().getDownStationId()).updateFirstSection(true);
            if (sections.size() == 2){
                findByUpStationId(getFirstUpStation().getDownStationId()).updateLastSection(true);
            }
            this.sections.remove(getFirstUpStation());
            return;
        }

        // 마지막 역 삭제
        if(getLastDownStationId().equals(stationId)){
            findByDownStationId(getLastDownStation().getUpStationId()).updateLastSection(true);
            if (sections.size() == 2){
                findByDownStationId(getLastDownStation().getUpStationId()).updateFirstSection(true);
            }
            this.sections.add(getLastDownStation());
            return;
        }

        // 중간 역 삭제
        Section previousSection = findByDownStationId(stationId);
        Section nextSection = findByUpStationId(stationId);
        previousSection.updateForRemoveSection(nextSection);
        if( sections.size() == 2 ){
            previousSection.updateFirstSection(true);
            previousSection.updateLastSection(true);
        }
        this.sections.remove(nextSection);

    }

    public List<Long> getSortedStationIds() {
        return sectionStationSorter.getSortedStationIds(this.sections);
    }
}
