package kz.home.pzem.repository;

import kz.home.pzem.entity.PzemData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PzemDataRepository extends JpaRepository<PzemData, Long> {
}
