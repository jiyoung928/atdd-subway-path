package nextstep.subway.domain.section;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Section findByUpStationIdAndDownStationId(Long upStationId, Long downStationId);

}