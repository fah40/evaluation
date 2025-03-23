package site.easy.to.build.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.TypeConfig;

public interface TypeConfigRepository extends JpaRepository<TypeConfig, Integer> {
}