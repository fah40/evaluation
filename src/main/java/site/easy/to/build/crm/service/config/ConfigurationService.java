package site.easy.to.build.crm.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.Configuration;
import site.easy.to.build.crm.entity.TypeConfig;
import site.easy.to.build.crm.repository.ConfigurationRepository;

import java.util.List;

@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    public List<Configuration> findAll() {
        return configurationRepository.findAll();
    }

    public Configuration findById(int id) {
        return configurationRepository.findById(id).orElse(null);
    }

    public Configuration findByTypeConfig(TypeConfig typeconfig) {
        return configurationRepository.findByTypeConfig(typeconfig);
    }

    public int getPercentageByType(String type) {
        Configuration config = configurationRepository.findByTypeConfigType(type);
        if (config == null || config.getValue() == null) {
            return 80; // Valeur par défaut si aucune config n'est trouvée
        }
        try {
            return Integer.parseInt(config.getValue());
        } catch (NumberFormatException e) {
            return 80; // Valeur par défaut en cas d'erreur de parsing
        }
    }

    public Configuration save(Configuration configuration) {
        if (configuration.getTypeConfig() == null) {
            throw new IllegalArgumentException("TypeConfig cannot be null");
        }
        if (configuration.getValue() == null || configuration.getValue().trim().isEmpty()) {
            throw new IllegalArgumentException("Value cannot be empty");
        }
        return configurationRepository.save(configuration);
    }

    public void delete(Configuration configuration) {
        configurationRepository.delete(configuration);
    }
}