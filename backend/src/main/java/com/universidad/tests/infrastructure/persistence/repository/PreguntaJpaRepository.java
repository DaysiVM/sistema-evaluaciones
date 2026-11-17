package com.universidad.tests.infrastructure.persistence.repository;

import com.universidad.tests.infrastructure.persistence.entity.PreguntaJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaJpaEntity, Long> {

    List<PreguntaJpaEntity>
            findByConfiguracionSubprueba_IdConfiguracionSubpruebaOrderByOrdenPresentacionAsc(
                    Long idConfiguracionSubprueba);

    Optional<PreguntaJpaEntity>
            findByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
                    Long idConfiguracionSubprueba, String numeroVisible);

    boolean existsByConfiguracionSubprueba_IdConfiguracionSubpruebaAndNumeroVisible(
            Long idConfiguracionSubprueba, String numeroVisible);
}
