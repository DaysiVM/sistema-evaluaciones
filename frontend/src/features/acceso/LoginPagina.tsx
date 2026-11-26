import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router';
import { EstadoCarga } from '../evaluacion/componentes/EstadoCarga';
import { MensajeError } from '../evaluacion/componentes/MensajeError';
import { usePeticion } from '../evaluacion/hooks/usePeticion';
import { Campo } from './Campo';
import { useSesion } from './sesion';
import '../evaluacion/evaluacion.css';
import './acceso.css';

/** GEN-06-FE — Inicio de sesión (E14). */
export function LoginPagina() {
  const { iniciar } = useSesion();
  const navegar = useNavigate();
  const { estado, ejecutar } = usePeticion(iniciar);
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const cargando = estado.fase === 'cargando';

  const enviar = async (evento: FormEvent) => {
    evento.preventDefault();
    if (estado.fase === 'cargando') return;
    // Éxito = no null (iniciar no devuelve datos); entonces se vuelve al catálogo.
    if ((await ejecutar({ correo, contrasena })) !== null) void navegar('/', { replace: true });
  };

  return (
    <div className="ev-pagina acceso-pagina">
      <header className="ev-pagina-cabecera">
        <h1>Iniciar sesión</h1>
        <p>Ingrese con su correo institucional.</p>
      </header>
      <form className="ev-form" onSubmit={(e) => void enviar(e)} noValidate>
        <Campo
          etiqueta="Correo"
          type="email"
          autoComplete="username"
          value={correo}
          onChange={(e) => setCorreo(e.target.value)}
          required
        />
        <Campo
          etiqueta="Contraseña"
          type="password"
          autoComplete="current-password"
          value={contrasena}
          onChange={(e) => setContrasena(e.target.value)}
          required
        />
        {estado.fase === 'error' && <MensajeError error={estado.error} />}
        {cargando && <EstadoCarga texto="Verificando…" />}
        <div className="ev-acciones">
          <button type="submit" className="ev-boton" disabled={cargando || !correo || !contrasena}>
            Ingresar
          </button>
        </div>
      </form>
      <p className="acceso-enlaces">
        <Link to="/acceso/recuperar">Olvidé mi contraseña</Link>
        <Link to="/acceso/registro">Crear una cuenta</Link>
      </p>
    </div>
  );
}
