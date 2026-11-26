import os,json,secrets,subprocess,time,urllib.request,urllib.error,concurrent.futures,shutil
from pathlib import Path
import psycopg
root=Path(__file__).resolve().parents[2];os.chdir(root)
assert os.environ.get('BFA_BD_AISLADA')=='SI', 'Use solo una base PostgreSQL local desechable y confirme BFA_BD_AISLADA=SI'
assert os.environ.get('DB_HOST','127.0.0.1') in ['127.0.0.1','localhost'], 'Solo loopback'
assert os.environ.get('POSTGRES_PORT','55432')=='55432', 'Se reserva el puerto aislado 55432'
Path('backend/target').mkdir(exist_ok=True)
e={**os.environ,'DB_HOST':'127.0.0.1','POSTGRES_PORT':'55432','POSTGRES_USER':os.environ.get('POSTGRES_USER','evaluaciones'),'POSTGRES_PASSWORD':os.environ.get('POSTGRES_PASSWORD',''),'SERVER_PORT':'18080','SERVER_ADDRESS':'127.0.0.1','MOTOR_TEMPORIZADOR_HABILITADO':'false'}
log=open('backend/target/motor-api-local.log','w');app=subprocess.Popen(['java','-jar','backend/target/sistema-evaluaciones-backend-0.1.0-SNAPSHOT.jar'],env=e,stdout=log,stderr=log)
http=urllib.request.build_opener(urllib.request.ProxyHandler({}));base='http://127.0.0.1:18080'
def req(m,p,data=None,token=None):
 h={'Content-Type':'application/json'}
 if token:h['Authorization']='Bearer '+token
 r=urllib.request.Request(base+p,method=m,headers=h,data=json.dumps(data).encode() if data is not None else None)
 try:
  with http.open(r,timeout=20) as r:return r.status,json.loads(r.read() or 'null')
 except urllib.error.HTTPError as r:return r.code,json.loads(r.read() or 'null')
def expect(m,p,status,data=None,token=None):
 s,b=req(m,p,data,token)
 if s!=status:raise RuntimeError(f'{m} {p}: esperado {status}, observado {s}, codigo {(b or {}).get("codigo")}')
 return b
try:
 for i in range(50):
  try:
   if req('GET','/actuator/health')[0]==200:
    assert app.poll() is None, 'El proceso de API no esta activo'
    break
  except Exception:time.sleep(.4)
 else:raise RuntimeError('No arranco API local')
 with psycopg.connect(host='127.0.0.1',port=55432,user=e['POSTGRES_USER'],password=e['POSTGRES_PASSWORD'],dbname=os.environ.get('POSTGRES_DB','evaluaciones'),autocommit=True) as db:
  users={}
  for name in ['coord','sujeto','otro']:
   mail='motor-'+name+'-'+secrets.token_hex(5)+'@ejemplo.test';pwd=secrets.token_urlsafe(24)
   expect('POST','/api/v1/auth/registro',201,{'correo':mail,'contrasena':pwd,'nombres':'Persona ficticia','apellidos':'Prueba local','rol':'SUJETO'})
   uid=db.execute('select id_cuenta from cuenta where correo=%s',(mail,)).fetchone()[0]
   if name=='coord':db.execute("update cuenta_rol set rol='COORDINADOR' where id_cuenta=%s",(uid,))
   session=expect('POST','/api/v1/auth/sesion',200,{'correo':mail,'contrasena':pwd});users[name]=(uid,session['token'])
  test=db.execute("insert into test(codigo,nombre) values (%s,'Motor ficticio') returning id_test",('LOCAL_'+secrets.token_hex(6),)).fetchone()[0]
  sub=db.execute("insert into subprueba(id_test,codigo,nombre) values (%s,'SUB','Subprueba ficticia') returning id_subprueba",(test,)).fetchone()[0]
  ver=db.execute("insert into version_formulario(id_test,codigo_version,fuente_ref) values (%s,'LOCAL-1','FICTICIA') returning id_version_formulario",(test,)).fetchone()[0]
  config=db.execute("insert into configuracion_subprueba(id_version_formulario,id_subprueba,orden_aplicacion,tiempo_limite_segundos,tipo_respuesta,instrucciones_ref,opciones_esperadas) values (%s,%s,1,300,'SELECCION','FICTICIA',2) returning id_configuracion_subprueba",(ver,sub)).fetchone()[0]
  pregunta=expect('POST',f'/api/v1/versiones/{ver}/configuraciones/SUB/preguntas',201,{'numeroVisible':1,'ordenPresentacion':1,'enunciado':'Contenido ficticio para probar HTTP'},users['coord'][1])['id'];print('E27 HTTP real 201')
  opciones={'opciones':[{'letra':'A','texto':'Opcion ficticia A'},{'letra':'B','texto':'Opcion ficticia B'}]}
  expect('PUT',f'/api/v1/versiones/{ver}/preguntas/{pregunta}/opciones',200,opciones,users['coord'][1]);expect('PUT',f'/api/v1/versiones/{ver}/preguntas/{pregunta}/opciones',200,opciones,users['coord'][1]);print('E28 HTTP real 200 y reintento 200')
  expect('PUT',f'/api/v1/versiones/{ver}/preguntas/{pregunta}/opciones',403,opciones,users['sujeto'][1]);print('E28 sujeto sin permiso 403')
  db.execute("update version_formulario set estado='PUBLICADA',publicada_en=statement_timestamp(),evidencia_validacion='EVIDENCIA FICTICIA LOCAL' where id_version_formulario=%s",(ver,))
  expect('PUT',f'/api/v1/versiones/{ver}/preguntas/{pregunta}/opciones',409,opciones,users['coord'][1]);print('E28 version publicada 409')
  sujeto=db.execute("insert into sujeto(nombres,apellidos) values ('Sujeto','Ficticio') returning id_sujeto").fetchone()[0]
  periodo=db.execute("insert into periodo(codigo,nombre) values (%s,'Periodo ficticio') returning id_periodo",('LOCAL_'+secrets.token_hex(5),)).fetchone()[0]
  for n in ['coord','sujeto']:db.execute('insert into cuenta_sujeto_asignado(id_cuenta,id_sujeto) values (%s,%s)',(users[n][0],sujeto))
  vals={'base_url':base,'testId':test,'versionId':ver,'sujetoId':sujeto,'periodoId':periodo,**{k:v[1] for k,v in users.items()}}
  js="""const fs=require('fs'),newman=require(process.env.NEWMAN_MODULE || 'newman');const v=JSON.parse(fs.readFileSync(0,'utf8'));let evaluacionId;newman.run({collection:JSON.parse(fs.readFileSync('tests/api/GEN-motor-local.postman_collection.json')),environment:{values:Object.entries(v).map(([key,value])=>({key,value:String(value),enabled:true}))},reporters:[]},(e,s)=>{if(e){process.stdout.write(JSON.stringify({error:'ejecucion'}));process.exitCode=1;return;}process.stdout.write(JSON.stringify({solicitudes:s.run.stats.requests.total,aserciones:s.run.stats.assertions.total,fallos:s.run.failures.map(f=>({caso:f.source.name,tipo:f.error.name})),evaluacionId}));process.exitCode=s.run.failures.length?1:0;}).on('request',(err,a)=>{if(!err && a.item.name.startsWith('E3 crear') && a.response.code===201)evaluacionId=JSON.parse(a.response.stream.toString()).id;});"""
  r=subprocess.run([shutil.which('node'),'-e',js],input=json.dumps(vals),text=True,capture_output=True,env={**os.environ,'NO_PROXY':'127.0.0.1,localhost'});summary=json.loads(r.stdout);assert r.returncode==0,summary
  eid=int(summary['evaluacionId']);print('Newman motor:',summary['solicitudes'],'solicitudes,',summary['aserciones'],'aserciones,',len(summary['fallos']),'fallos')
  assert db.execute("select count(*) from evento_auditoria where accion='INICIAR_SUBPRUEBA' and id_evaluacion=%s",(eid,)).fetchone()[0]==1
  def vencer(eid):
   db.execute("update evaluacion set fecha_inicio=statement_timestamp()-interval '2 hours' where id_evaluacion=%s",(eid,))
   db.execute("update evaluacion_subprueba set estado='ACTIVA',fecha_inicio=statement_timestamp()-interval '1 hour',fecha_fin_limite=statement_timestamp()-interval '30 minutes',fecha_cierre=null where id_evaluacion=%s",(eid,))
  vencer(eid)
  cerrado=expect('POST',f'/api/v1/evaluaciones/{eid}/finalizar',200,token=users['sujeto'][1]);assert cerrado['estado']=='FINALIZADA' and cerrado['estadoResultado']=='PENDIENTE_VALIDACION';assert expect('POST',f'/api/v1/evaluaciones/{eid}/finalizar',200,token=users['sujeto'][1])==cerrado
  assert db.execute('select count(*) from resultado where id_evaluacion=%s',(eid,)).fetchone()[0]==1
  assert db.execute("select count(*) from evento_auditoria where accion='FINALIZAR_EVALUACION' and id_evaluacion=%s",(eid,)).fetchone()[0]==1
  print('E8 HTTP real 200, vencimiento cerrado, mismo resultado y un solo evento al repetir')
  body={'testId':test,'versionFormularioId':ver,'sujetoId':sujeto,'periodoId':periodo}
  rid=expect('POST','/api/v1/evaluaciones',201,body,users['coord'][1])['id'];vencer(rid)
  db.execute("create or replace function fallo_auditoria_local() returns trigger language plpgsql as $$ begin if NEW.accion='FINALIZAR_EVALUACION' then raise exception 'Fallo ficticio local de auditoria'; end if; return NEW; end $$")
  db.execute('create trigger fallo_auditoria_local before insert on evento_auditoria for each row execute function fallo_auditoria_local()')
  try:
   expect('POST',f'/api/v1/evaluaciones/{rid}/finalizar',500,token=users['sujeto'][1])
   assert db.execute('select estado from evaluacion where id_evaluacion=%s',(rid,)).fetchone()[0]=='EN_CURSO'
   assert db.execute('select count(*) from resultado where id_evaluacion=%s',(rid,)).fetchone()[0]==0
   assert db.execute("select estado from evaluacion_subprueba where id_evaluacion=%s",(rid,)).fetchone()[0]=='ACTIVA'
   print('E8 fallo de auditoria 500: rollback de evaluacion, resultado y cierre vencido OK')
  finally:db.execute('drop trigger fallo_auditoria_local on evento_auditoria');db.execute('drop function fallo_auditoria_local()')
  with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
   results=list(pool.map(lambda _:expect('POST',f'/api/v1/evaluaciones/{rid}/finalizar',200,token=users['sujeto'][1]),range(2)))
  assert results[0]==results[1]
  assert db.execute('select count(*) from resultado where id_evaluacion=%s',(rid,)).fetchone()[0]==1
  assert db.execute("select count(*) from evento_auditoria where accion='FINALIZAR_EVALUACION' and id_evaluacion=%s",(rid,)).fetchone()[0]==1
  print('E8 dos solicitudes concurrentes 200: mismo resultado, un solo registro y evento')
finally:
 app.terminate();app.wait(timeout=20);log.close()
