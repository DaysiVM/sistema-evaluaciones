package com.universidad.tests.application.service;

import com.universidad.tests.domain.model.VersionFormulario;
import org.springframework.stereotype.Service;

@Service
public class VersionFormularioRestService {
    private final VersionFormularioService versiones;

    public VersionFormularioRestService(VersionFormularioService versiones) {
        this.versiones = versiones;
    }

    public ResumenVersion crear(Long idTest, String codigo, String descripcion, String fuenteRef) {
        return resumen(
                versiones.crearBorradorDesdeUltimaPublicada(
                        idTest, codigo, descripcion, fuenteRef));
    }

    private ResumenVersion resumen(VersionFormulario version) {
        return new ResumenVersion(
                version.getIdVersionFormulario(), version.getIdTest(), version.getEstado().name());
    }

    public record ResumenVersion(Long id, Long testId, String estado) {}
}
