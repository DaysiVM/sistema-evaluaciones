package com.universidad.tests.domain.port;

public interface AutorizacionContenidoPort {

    // Verificar permiso para crear versiones
    void exigirPermisoCrearVersion(Long idTest);

    // Verificar permiso para modificar contenido
    void exigirPermisoEditarContenido(Long idVersionFormulario);

    // Verificar permiso para publicar versiones
    void exigirPermisoPublicar(Long idVersionFormulario);

    // Verificar permiso para retirar versiones
    void exigirPermisoRetirar(Long idVersionFormulario);

    // Verificar permiso para consultar contenido restringido
    void exigirPermisoConsultarContenido(Long idVersionFormulario);
}
