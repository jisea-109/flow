package com.sjp.flow;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<FlowEntity, Long>{
    boolean existsByExtensionName(String extensionName);
    FlowEntity findById(int flowId);
    List<FlowEntity> findByType(FlowExtensionType type);
    Optional<FlowEntity> findByExtensionName(String extensionName);
}
