import test from 'node:test';
import assert from 'node:assert/strict';
import { calcularResumen, generarSugerencias } from '../src/finance.js';
import { dinero, uuid, fecha, tipo, texto, paginacion } from '../src/validation.js';

const perfil = { saldo_inicial: 1_000_000 };
const categorias = [
  { id: 'vivienda', nombre: 'Vivienda', tipo: 'GASTO' },
  { id: 'ingreso', nombre: 'Ingresos', tipo: 'INGRESO' },
];
const movimientos = [
  { tipo: 'INGRESO', monto: 500_000, categoria_id: 'ingreso', fecha_movimiento: '2026-11-01' },
  { tipo: 'GASTO', monto: 200_000, categoria_id: 'vivienda', fecha_movimiento: '2026-11-02' },
  { tipo: 'GASTO', monto: 100_000, categoria_id: 'vivienda', fecha_movimiento: '2026-10-30' },
];

test('saldo disponible incorpora saldo inicial e histórico', () => {
  const r = calcularResumen(perfil, movimientos, categorias);
  assert.equal(r.saldo_disponible, 1_200_000);
  assert.equal(r.ingresos, 500_000);
  assert.equal(r.gastos, 300_000);
  assert.equal(r.gastos_por_categoria[0].total, 300_000);
});

test('filtrar por fecha no modifica saldo disponible histórico', () => {
  const r = calcularResumen(perfil, movimientos, categorias, { desde: '2026-11-01', hasta: '2026-11-30' });
  assert.equal(r.saldo_disponible, 1_200_000);
  assert.equal(r.ingresos, 500_000);
  assert.equal(r.gastos, 200_000);
});

test('sin movimientos, solo se muestra saldo inicial', () => {
  const r = calcularResumen(perfil, [], categorias);
  assert.equal(r.saldo_disponible, 1_000_000);
  assert.equal(r.total_movimientos, 0);
});

test('reglas de ahorro producen sugerencias educativas', () => {
  const r = calcularResumen(perfil, movimientos, categorias);
  assert.ok(generarSugerencias(r).length > 0);
});

test('montos COP seguros y enteros', () => {
  assert.equal(dinero(2_500_000), 2_500_000);
  assert.equal(dinero(0, 'saldo', { permitirCero: true }), 0);
  for (const invalid of [-1, 0, 2.5, NaN, Infinity, '1000']) {
    assert.throws(() => dinero(invalid));
  }
});

test('validación de fechas reales y formatos', () => {
  assert.equal(fecha('2026-11-09'), '2026-11-09');
  assert.throws(() => fecha('2026-02-30'));
  assert.throws(() => fecha('09/11/2026'));
});

test('tipos, texto y paginación validan entrada', () => {
  assert.equal(tipo('GASTO'), 'GASTO');
  assert.throws(() => tipo('gasto'));
  assert.equal(texto(' Mercado ', 'descripción'), 'Mercado');
  assert.deepEqual(paginacion({ limit: '20', offset: '0' }), { limit: 20, offset: 0 });
  assert.throws(() => paginacion({ limit: '900' }));
});

test('UUID inválido se rechaza antes de consultar a PostgreSQL', () => {
  assert.equal(uuid('7f9c9b7c-8a2b-46e3-a547-1a7cbaa14b00'), '7f9c9b7c-8a2b-46e3-a547-1a7cbaa14b00');
  assert.throws(() => uuid('2'));
});
