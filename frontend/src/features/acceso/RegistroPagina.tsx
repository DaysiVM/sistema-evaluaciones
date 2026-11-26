import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { EstadoCarga } from '../evaluacion/componentes/EstadoCarga';
import { MensajeError } from '../evaluacion/componentes/MensajeError';
import { usePeticion } from '../evaluacion/hooks/usePeticion';
import { Campo } from './Campo';
import { clienteAcceso } from './cliente';
import { ETIQUETAS_ROL, ROLES, type Rol } from './tipos';
import '../evaluacion/evaluacion.css';
import './acceso.css';

/** GEN-06-FE — Registro de cuenta (E13). */
export function RegistroPagina() {
  const { estado, ejecutar } = usePeticion(clienteAcceso.registrar);
  const [valores, setValores] = useState({
    nombres: '',
    apellidos: '',
    correo: '',
    contrasena: '',
  });
  const [rol, setRol] = useState<Rol>('SUJETO');
  const cargando = estado.fase === 'cargando';
  const cambiar = (campo: keyof typeof valores) => (e: { target: { value: string } }) =>
    setValores((v) => ({ ...v, [campo]: e.target.value }));
  const completo = Object.values(valores).every((v) => v.trim() !== '');

  const enviar = async (evento: FormEvent) => {
    evento.preventDefault();
    if (!cargando) await ejecutar({ ...valores, correo: valores.correo.trim(), rol });
  };

  return (
    <div className="ev-pagina acceso-pagina">
      <header className="ev-pagina-cabecera">
        <h1>Crear cuenta</h1>
        <p>Un rol distinto de Sujeto solo se acepta si quien registra puede asignarlo.</p>
      </header>
      {estado.fase === 'exito' ? (
        <div className="ev-confirmado" role="status">
          <p>
            Cuenta creada para <strong>{estado.datos.correo}</strong>.
          </p>
          <Link to="/acceso">Iniciar sesión</Link>
        </div>
      ) : (
        <form className="ev-form" onSubmit={(e) => void enviar(e)} noValidate>
          <Campo
            etiqueta="Nombres"
            autoComplete="given-name"
            value={valores.nombres}
            onChange={cambiar('nombres')}
            required
          />
          <Campo
            etiqueta="Apellidos"
            autoComplete="family-name"
            value={valores.apellidos}
            onChange={cambiar('apellidos')}
            required
          />
          <Campo
            etiqueta="Correo"
            type="email"
            autoComplete="username"
            value={valores.correo}
            onChange={cambiar('correo')}
            required
          />
          <Campo
            etiqueta="Contraseña"
            type="password"
            autoComplete="new-password"
            value={valores.contrasena}
            onChange={cambiar('contrasena')}
            required
          />
          <div className="ev-campo">
            <label htmlFor="registro-rol">Rol</label>
            <select
              id="registro-rol"
              className="ev-input"
              value={rol}
              onChange={(e) => setRol(e.target.value as Rol)}
            >
              {ROLES.map((r) => (
                <option key={r} value={r}>
                  {ETIQUETAS_ROL[r]}
                </option>
              ))}
            </select>
          </div>
          {estado.fase === 'error' && <MensajeError error={estado.error} />}
          {cargando && <EstadoCarga texto="Creando la cuenta…" />}
          <div className="ev-acciones">
            <button type="submit" className="ev-boton" disabled={cargando || !completo}>
              Crear cuenta
            </button>
          </div>
        </form>
      )}
      <p className="acceso-enlaces">
        <Link to="/acceso">Ya tengo cuenta</Link>
      </p>
    </div>
  );
}
