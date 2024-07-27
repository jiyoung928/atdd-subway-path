package nextstep.subway.domain.section;

import nextstep.subway.domain.line.Line;

import javax.persistence.*;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private Long upStationId;

    private Long downStationId;

    private Long distance;

    private boolean first;

    protected Section() {}

    public Section(Line line, Long upStationId, Long downStationId, Long distance) {
        this.line = line;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Section(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public boolean isFirst(){
        return this.first;
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public void updateFirstSection(boolean first) {
        this.first = first;
    }



    public void updateForNewSection(Section newSection) {
        Long newDistance = this.distance - newSection.distance;
        this.upStationId = newSection.downStationId;
        this.distance = newDistance;
    }

}
