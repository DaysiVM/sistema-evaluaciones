package com.universidad.tests.controller;

import com.universidad.tests.application.usecase.BuscarSujetoUseCase;
import com.universidad.tests.application.usecase.ConsultarSujetoUseCase;
import com.universidad.tests.application.usecase.RegistrarSujetoUseCase;
import com.universidad.tests.application.usecase.vista.SujetoVista;
import com.universidad.tests.dto.request.SujetoRequest;
import com.universidad.tests.dto.response.SujetoResponse;
import com.universidad.tests.mapper.EvaluacionMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** E18 (registrar), E19 (consultar) y E23 (buscar) Sujetos (CU-GEN-20). */
@RestController
@Validated
@RequestMapping("/api/v1/sujetos")
public class SujetoController {

    private final RegistrarSujetoUseCase registrarSujeto;
    private final ConsultarSujetoUseCase consultarSujeto;
    private final BuscarSujetoUseCase buscarSujeto;
    private final EvaluacionMapper mapper;

    public SujetoController(
            RegistrarSujetoUseCase registrarSujeto,
            ConsultarSujetoUseCase consultarSujeto,
            BuscarSujetoUseCase buscarSujeto,
            EvaluacionMapper mapper) {
        this.registrarSujeto = registrarSujeto;
        this.consultarSujeto = consultarSujeto;
        this.buscarSujeto = buscarSujeto;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<SujetoResponse> registrar(@Valid @RequestBody SujetoRequest request) {
        SujetoVista creado =
                registrarSujeto.registrar(
                        request.identificadorInstitucional(),
                        request.nombres(),
                        request.apellidos(),
                        request.fechaNacimiento());
        SujetoResponse response = mapper.aResponse(creado);
        return ResponseEntity.created(URI.create("/api/v1/sujetos/" + response.id()))
                .body(response);
    }

    @GetMapping
    public List<SujetoResponse> buscar(
            @RequestParam(name = "identificadorInstitucional") @NotBlank
                    String identificadorInstitucional) {
        return buscarSujeto.buscarPorIdentificador(identificadorInstitucional).stream()
                .map(mapper::aResponse)
                .toList();
    }

    @GetMapping("/{sujetoId}")
    public SujetoResponse consultar(@PathVariable @Positive Long sujetoId) {
        return mapper.aResponse(consultarSujeto.consultar(sujetoId));
    }
}
