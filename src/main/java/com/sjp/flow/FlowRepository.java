package com.sjp.flow;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowRepository extends JpaRepository<FlowEntity, Long>{
    boolean existsByExtensionName(String extensionName);
    Optional<FlowEntity> findByExtensionName(String extensionName);
}
