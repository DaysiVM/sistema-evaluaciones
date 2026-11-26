package com.universidad.tests.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/** E19 (misma forma que E18). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SujetoResponse(
        Long id, String identificadorInstitucional, String nombres, String apellidos) {}
