import { supabase } from '../config/supabase.js';
import { HttpError, obtenerUno, verificar } from '../utils/errores.js';

const COLUMNAS =
  'id, usuario_id, categoria_id, tipo, monto, descripcion, medio_pago, fecha_movimiento, creado_en, actualizado_en, ' +
  'categoria:categoria_id (id, nombre, color_hex)';

/** Comprueba que la categoría sea del usuario y que su tipo coincida con el del movimiento. */
async function resolverTipo(usuarioId, categoriaId, tipoEnviado) {
  const categoria = verificar(
    await supabase.from('categoria').select('tipo').eq('id', categoriaId).eq('usuario_id', usuarioId).maybeSingle()
  );
  if (!categoria) throw new HttpError(400, 'La categoría indicada no existe');
  if (tipoEnviado && tipoEnviado !== categoria.tipo) {
    throw new HttpError(400, `El movimiento es de tipo ${tipoEnviado} pero la categoría es de tipo ${categoria.tipo}`);
  }
  return categoria.tipo;
}

// GET /movimientos?categoria_id=&tipo=&desde=&hasta=&pagina=&limite=
export async function listar(req, res) {
  const { categoria_id, tipo, desde, hasta, pagina, limite } = req.validado.query;

  let consulta = supabase
    .from('movimiento')
    .select(COLUMNAS, { count: 'exact' })
    .eq('usuario_id', req.usuarioId)
    .order('fecha_movimiento', { ascending: false })
    .order('id', { ascending: false })
    .range((pagina - 1) * limite, pagina * limite - 1);

  if (categoria_id) consulta = consulta.eq('categoria_id', categoria_id);
  if (tipo) consulta = consulta.eq('tipo', tipo);
  if (desde) consulta = consulta.gte('fecha_movimiento', desde);
  if (hasta) consulta = consulta.lte('fecha_movimiento', hasta);

  const { data, error, count } = await consulta;
  // Pedir una página más allá del final devuelve error de rango: se responde lista vacía.
  if (error?.code === 'PGRST103') return res.set('X-Total-Count', '0').json([]);
  verificar({ error });

  res.set('X-Total-Count', String(count ?? data.length)).json(data);
}

// GET /movimientos/:id
export async function obtener(req, res) {
  const movimiento = await obtenerUno(
    supabase.from('movimiento').select(COLUMNAS).eq('id', req.validado.params.id).eq('usuario_id', req.usuarioId),
    'Movimiento'
  );
  res.json(movimiento);
}

// POST /movimientos   (HU-02 ingresos, HU-03 gastos)
export async function crear(req, res) {
  const datos = req.validado.body;
  datos.tipo = await resolverTipo(req.usuarioId, datos.categoria_id, datos.tipo);

  const movimiento = verificar(
    await supabase
      .from('movimiento')
      .insert({ ...datos, usuario_id: req.usuarioId })
      .select(COLUMNAS)
      .single()
  );
  res.status(201).json(movimiento);
}

// PUT /movimientos/:id   (HU-09)
export async function actualizar(req, res) {
  const { id } = req.validado.params;
  const cambios = req.validado.body;

  const actual = await obtenerUno(
    supabase.from('movimiento').select('categoria_id').eq('id', id).eq('usuario_id', req.usuarioId),
    'Movimiento'
  );
  if (cambios.categoria_id || cambios.tipo) {
    cambios.tipo = await resolverTipo(req.usuarioId, cambios.categoria_id ?? actual.categoria_id, cambios.tipo);
  }

  const movimiento = verificar(
    await supabase
      .from('movimiento')
      .update(cambios)
      .eq('id', id)
      .eq('usuario_id', req.usuarioId)
      .select(COLUMNAS)
      .single()
  );
  res.json(movimiento);
}

// DELETE /movimientos/:id   (HU-09)
export async function eliminar(req, res) {
  await obtenerUno(
    supabase.from('movimiento').delete().eq('id', req.validado.params.id).eq('usuario_id', req.usuarioId).select('id'),
    'Movimiento'
  );
  res.status(204).end();
}
