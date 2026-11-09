import test from 'node:test';
import assert from 'node:assert/strict';
import { supabaseRequest, verificarUsuario } from '../src/supabase.js';

const baseEnv = {
  SUPABASE_URL: process.env.SUPABASE_URL,
  SUPABASE_PUBLISHABLE_KEY: process.env.SUPABASE_PUBLISHABLE_KEY,
};

function withFakeSupabase(handler) {
  const previousFetch = globalThis.fetch;
  process.env.SUPABASE_URL = 'https://ejemplo.supabase.co';
  process.env.SUPABASE_PUBLISHABLE_KEY = 'sb_publishable_prueba_sin_credenciales';
  globalThis.fetch = handler;
  return () => {
    globalThis.fetch = previousFetch;
    for (const [key, value] of Object.entries(baseEnv)) {
      if (value === undefined) delete process.env[key];
      else process.env[key] = value;
    }
  };
}

test('se reenvía el JWT del usuario; no se suplanta con clave administrativa', async () => {
  let captured;
  const restore = withFakeSupabase(async (url, options) => {
    captured = { url: String(url), options };
    return new Response(JSON.stringify({ id: 'usuario-prueba', email: 'demo@example.com' }), {
      status: 200, headers: { 'content-type': 'application/json' },
    });
  });
  try {
    const user = await verificarUsuario('jwt-del-usuario');
    assert.equal(user.id, 'usuario-prueba');
    assert.equal(captured.options.headers.apikey, 'sb_publishable_prueba_sin_credenciales');
    assert.equal(captured.options.headers.Authorization, 'Bearer jwt-del-usuario');
    assert.ok(captured.url.endsWith('/auth/v1/user'));
  } finally { restore(); }
});

test('solicitudes de registro no usan una sesión fingida', async () => {
  let captured;
  const restore = withFakeSupabase(async (_url, options) => {
    captured = options;
    return new Response(JSON.stringify({ user: { id: 'usuario-prueba' } }), { status: 200 });
  });
  try {
    await supabaseRequest('/auth/v1/signup', { method: 'POST', body: {
      email: 'demo@example.com', password: 'un-ejemplo-sin-secretos',
    } });
    assert.equal(captured.headers.Authorization, undefined);
    assert.equal(captured.method, 'POST');
  } finally { restore(); }
});

test('respuesta 401 de Supabase se propaga como error de autenticación', async () => {
  const restore = withFakeSupabase(async () =>
    new Response(JSON.stringify({ message: 'JWT inválido' }), { status: 401 }));
  try {
    await assert.rejects(
      () => verificarUsuario('jwt-no-valido'),
      (error) => error.status === 401,
    );
  } finally { restore(); }
});
