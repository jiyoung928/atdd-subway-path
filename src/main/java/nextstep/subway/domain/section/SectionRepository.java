package subway.domain.section;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    Section findByUpStationIdAndDownStationId(Long upStationId, Long downStationId);

}
