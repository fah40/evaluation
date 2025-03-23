package site.easy.to.build.crm.service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.easy.to.build.crm.entity.TypeConfig;
import site.easy.to.build.crm.repository.TypeConfigRepository;

import java.util.List;

@Service
public class TypeConfigService {

    private final TypeConfigRepository typeConfigRepository;

    @Autowired
    public TypeConfigService(TypeConfigRepository typeConfigRepository) {
        this.typeConfigRepository = typeConfigRepository;
    }

    public List<TypeConfig> findAll() {
        return typeConfigRepository.findAll();
    }

    public TypeConfig findById(int id) {
        return typeConfigRepository.findById(id).orElse(null);
    }

    public TypeConfig save(TypeConfig typeConfig) {
        if (typeConfig.getType() == null || typeConfig.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        return typeConfigRepository.save(typeConfig);
    }

    public void delete(TypeConfig typeConfig) {
        typeConfigRepository.delete(typeConfig);
    }
}