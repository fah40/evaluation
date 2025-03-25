package site.easy.to.build.crm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import site.easy.to.build.crm.entity.Configuration;
import site.easy.to.build.crm.entity.TypeConfig;

public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {

    public Configuration findByTypeConfig(TypeConfig typeConfig);

    Configuration findById(int id);
    List<Configuration> findByTypeConfigId(int typeConfigId);

    Configuration findByTypeConfigType(String type);
}