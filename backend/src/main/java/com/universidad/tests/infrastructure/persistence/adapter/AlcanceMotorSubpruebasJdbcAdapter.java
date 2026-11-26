package com.universidad.tests.infrastructure.persistence.adapter;

import com.universidad.tests.domain.port.AlcanceMotorSubpruebasPort;
import com.universidad.tests.domain.port.AlcanceSujetoPort;
import com.universidad.tests.domain.port.AutorizacionMotorSubpruebasPort.Permiso;
import org.springframework.stereotype.Repository;

/** E5-E7 usan la misma asignacion explicita; nunca otorgan alcance global por rol. */
@Repository
public class AlcanceMotorSubpruebasJdbcAdapter implements AlcanceMotorSubpruebasPort {
    private final AlcanceSujetoPort sujetos;

    public AlcanceMotorSubpruebasJdbcAdapter(AlcanceSujetoPort sujetos) {
        this.sujetos = sujetos;
    }

    public boolean permite(Long cuenta, String rol, Permiso permiso, Long evaluacion, Long sujeto) {
        return "SUJETO".equals(rol) && sujetos.estaAsignado(cuenta, sujeto);
    }
}
