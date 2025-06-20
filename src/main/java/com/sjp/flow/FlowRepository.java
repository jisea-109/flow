package com.sjp.flow;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<FlowEntity, Long>{
    boolean existsByExtensionName(String extensionName); // 확장자가 있는지 확인
    List<FlowEntity> findByType(FlowExtensionType type); // 고정 또는 커스텀 확장자 찾기
    List<FlowEntity> findByCheckedTrue(); // 커스텀 확장자 + 선택된 고정 확장자 찾기
    Optional<FlowEntity> findByExtensionName(String extensionName); // 확장자 찾아서 가져오기
}
