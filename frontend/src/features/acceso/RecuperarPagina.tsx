import { useState, type FormEvent } from 'react';
import { Link } from 'react-router';
import { EstadoCarga } from '../evaluacion/componentes/EstadoCarga';
import { MensajeError } from '../evaluacion/componentes/MensajeError';
import { usePeticion } from '../evaluacion/hooks/usePeticion';
import { Campo } from './Campo';
import { clienteAcceso } from './cliente';
import { aErrorAcceso } from './errores';
import '../evaluacion/evaluacion.css';
import './acceso.css';

/** GEN-06-FE — Recuperar la contraseña: pedir el token (E16) y cambiarla con él (E17). */
export function RecuperarPagina() {
  const pedir = usePeticion(clienteAcceso.pedirRecuperacion, aErrorAcceso);
  const confirmar = usePeticion(clienteAcceso.confirmarRecuperacion, aErrorAcceso);
  const [correo, setCorreo] = useState('');
  const [token, setToken] = useState('');
  const [nueva, setNueva] = useState('');
  const pedido = pedir.estado.fase === 'exito';

  const enviarPedido = async (evento: FormEvent) => {
    evento.preventDefault();
    if (pedir.estado.fase !== 'cargando') await pedir.ejecutar({ correo: correo.trim() });
  };
  const enviarCambio = async (evento: FormEvent) => {
    evento.preventDefault();
    if (confirmar.estado.fase !== 'cargando')
      await confirmar.ejecutar({ token: token.trim(), nuevaContrasena: nueva });
  };

  if (confirmar.estado.fase === 'exito')
    return (
      <div className="ev-pagina acceso-pagina">
        <div className="ev-confirmado" role="status">
          <p>La contraseña se actualizó.</p>
          <Link to="/acceso">Iniciar sesión</Link>
        </div>
      </div>
    );

  return (
    <div className="ev-pagina acceso-pagina">
      <header className="ev-pagina-cabecera">
        <h1>Recuperar contraseña</h1>
        <p>Pida un token de un solo uso y úselo para elegir una contraseña nueva.</p>
      </header>

      <form className="ev-form" onSubmit={(e) => void enviarPedido(e)} noValidate>
        <Campo
          etiqueta="Correo"
          type="email"
          autoComplete="username"
          value={correo}
          onChange={(e) => setCorreo(e.target.value)}
          required
        />
        {pedir.estado.fase === 'error' && <MensajeError error={pedir.estado.error} />}
        {pedir.estado.fase === 'cargando' && <EstadoCarga texto="Enviando…" />}
        {pedido && (
          <p className="ev-confirmado" role="status">
            Si el correo tiene una cuenta activa, recibirá un token válido por 15 minutos.
          </p>
        )}
        <div className="ev-acciones">
          <button
            type="submit"
            className="ev-boton"
            disabled={pedir.estado.fase === 'cargando' || !correo.trim()}
          >
            {pedido ? 'Pedir otro token' : 'Pedir token'}
          </button>
        </div>
      </form>

      <form className="ev-form acceso-segundo" onSubmit={(e) => void enviarCambio(e)} noValidate>
        <Campo
          etiqueta="Token recibido"
          autoComplete="one-time-code"
          value={token}
          onChange={(e) => setToken(e.target.value)}
          required
        />
        <Campo
          etiqueta="Contraseña nueva"
          type="password"
          autoComplete="new-password"
          minLength={12}
          value={nueva}
          onChange={(e) => setNueva(e.target.value)}
          required
        />
        {confirmar.estado.fase === 'error' && <MensajeError error={confirmar.estado.error} />}
        {confirmar.estado.fase === 'cargando' && <EstadoCarga texto="Guardando…" />}
        <div className="ev-acciones">
          <button
            type="submit"
            className="ev-boton"
            disabled={
              confirmar.estado.fase === 'cargando' ||
              !token.trim() ||
              Array.from(nueva).length < 12 ||
              !nueva.trim()
            }
          >
            Cambiar contraseña
          </button>
        </div>
      </form>
      <p className="acceso-enlaces">
        <Link to="/acceso">Volver a iniciar sesión</Link>
      </p>
    </div>
  );
}
