import test from 'node:test';
import assert from 'node:assert/strict';
import { request } from 'node:http';
import { once } from 'node:events';
import { crearApp } from '../src/app.js';

const uid = '7f9c9b7c-8a2b-46e3-a547-1a7cbaa14b00';
const cid = '1af309eb-2927-4355-98ac-3acb9e65dcbb';
const categoria = { id: cid, usuario_id: uid, nombre: 'Vivienda', tipo: 'GASTO' };

function http(port, method, path, token, body) {
  return new Promise((resolve, reject) => {
    const payload = body ? JSON.stringify(body) : undefined;
    const req = request({ hostname: '127.0.0.1', port, path, method, headers: {
      ...(token ? { authorization: `Bearer ${token}` } : {}),
      ...(payload ? { 'content-type': 'application/json', 'content-length': Buffer.byteLength(payload) } : {}),
    } }, (res) => {
      let data = '';
      res.setEncoding('utf8');
      res.on('data', (chunk) => { data += chunk; });
      res.on('end', () => resolve({ status: res.statusCode, body: data ? JSON.parse(data) : null }));
    });
    req.on('error', reject);
    req.end(payload);
  });
}

test('API Express responde y exige autenticación con JWT; limita los datos al dueño', async () => {
  const originalFetch = globalThis.fetch;
  const oldUrl = process.env.SUPABASE_URL;
  const oldKey = process.env.SUPABASE_PUBLISHABLE_KEY;
  const oldOrigins = process.env.CORS_ORIGINS;
  process.env.SUPABASE_URL = 'https://ejemplo.supabase.co';
  process.env.SUPABASE_PUBLISHABLE_KEY = 'sb_publishable_prueba';
  process.env.CORS_ORIGINS = 'http://localhost:5173';
  const consultas = [];
  globalThis.fetch = async (raw, options) => {
    const target = new URL(raw);
    consultas.push({ path: target.pathname, search: target.search, token: options.headers.Authorization });
    if (target.pathname === '/auth/v1/user') {
      return new Response(JSON.stringify({ id: uid, email: 'demo@example.com' }), {
        status: options.headers.Authorization === 'Bearer token-qa' ? 200 : 401,
      });
    }
    if (target.pathname === '/rest/v1/categorias') {
      return new Response(JSON.stringify([categoria]), { status: 200 });
    }
    if (target.pathname === '/rest/v1/movimientos') {
      return new Response(JSON.stringify([]), { status: 200 });
    }
    return new Response(JSON.stringify({ message: 'Ruta mock no implementada' }), { status: 404 });
  };
  const server = crearApp().listen(0);
  try {
    await once(server, 'listening');
    const port = server.address().port;

    const health = await http(port, 'GET', '/api/v1/health');
    assert.equal(health.status, 200);
    assert.equal(health.body.status, 'ok');

    const sinToken = await http(port, 'GET', '/api/v1/categorias');
    assert.equal(sinToken.status, 401);

    const tokenFalso = await http(port, 'GET', '/api/v1/categorias', 'token-invalido');
    assert.equal(tokenFalso.status, 401);

    const lista = await http(port, 'GET', '/api/v1/categorias?limit=10', 'token-qa');
    assert.equal(lista.status, 200);
    assert.equal(lista.body[0].nombre, 'Vivienda');
    const consulta = consultas.find((x) => x.path === '/rest/v1/categorias');
    assert.equal(new URLSearchParams(consulta.search).get('usuario_id'), `eq.${uid}`);
    assert.equal(consulta.token, 'Bearer token-qa');

    const invalido = await http(port, 'POST', '/api/v1/movimientos', 'token-qa',
      { tipo: 'GASTO', monto: -100, categoria_id: cid });
    assert.equal(invalido.status, 400);
  } finally {
    await new Promise((resolve) => server.close(resolve));
    globalThis.fetch = originalFetch;
    for (const [key, old] of [
      ['SUPABASE_URL', oldUrl],
      ['SUPABASE_PUBLISHABLE_KEY', oldKey],
      ['CORS_ORIGINS', oldOrigins],
    ]) {
      if (old === undefined) delete process.env[key];
      else process.env[key] = old;
    }
  }
});
