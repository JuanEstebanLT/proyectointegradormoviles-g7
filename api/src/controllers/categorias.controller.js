import { supabase } from '../config/supabase.js';
import { calcularResumen } from '../services/resumen.service.js';
import { HttpError, obtenerUno, verificar } from '../utils/errores.js';
import { rangoMesActual } from '../utils/fechas.js';

const COLUMNAS = 'id, usuario_id, nombre, tipo, color_hex, presupuesto_mensual, es_predeterminada, creado_en';

const categoriaDelUsuario = (req) =>
  supabase.from('categoria').select(COLUMNAS).eq('id', req.validado.params.id).eq('usuario_id', req.usuarioId);

/** Agrega a cada categoría su total, participación y uso de presupuesto en el período. */
async function conTotales(usuarioId, categorias, periodo) {
  const { por_categoria } = await calcularResumen(usuarioId, periodo);
  return categorias.map((categoria) => {
    const t = por_categoria.find((c) => c.categoria_id === categoria.id);
    return {
      ...categoria,
      total_periodo: t?.total ?? 0,
      cantidad_movimientos: t?.cantidad_movimientos ?? 0,
      participacion: t?.participacion ?? 0,
      uso_presupuesto: t?.uso_presupuesto ?? null
    };
  });
}

// GET /categorias?tipo=&desde=&hasta=   (por defecto, el mes actual)
export async function listar(req, res) {
  const { desde, hasta } = req.validado.query;
  const periodo = desde || hasta ? { desde, hasta } : rangoMesActual();

  let consulta = supabase.from('categoria').select(COLUMNAS).eq('usuario_id', req.usuarioId).order('nombre');
  if (req.validado.query.tipo) consulta = consulta.eq('tipo', req.validado.query.tipo);

  const categorias = verificar(await consulta);
  res.json(await conTotales(req.usuarioId, categorias, periodo));
}

// GET /categorias/:id   (incluye totales del mes actual)
export async function obtener(req, res) {
  const categoria = await obtenerUno(categoriaDelUsuario(req), 'Categoría');
  const [conDatos] = await conTotales(req.usuarioId, [categoria], rangoMesActual());
  res.json(conDatos);
}

// POST /categorias
export async function crear(req, res) {
  const { data, error } = await supabase
    .from('categoria')
    .insert({ ...req.validado.body, usuario_id: req.usuarioId })
    .select(COLUMNAS)
    .single();

  if (error?.code === '23505') throw new HttpError(409, 'Ya tienes una categoría con ese nombre');
  res.status(201).json(verificar({ data, error }));
}

// PUT /categorias/:id
export async function actualizar(req, res) {
  const { data, error } = await supabase
    .from('categoria')
    .update(req.validado.body)
    .eq('id', req.validado.params.id)
    .eq('usuario_id', req.usuarioId)
    .select(COLUMNAS)
    .maybeSingle();

  if (error?.code === '23505') throw new HttpError(409, 'Ya tienes una categoría con ese nombre');
  if (!verificar({ data, error })) throw new HttpError(404, 'Categoría no encontrada');
  res.json(data);
}

// DELETE /categorias/:id   (solo si no tiene movimientos asociados)
export async function eliminar(req, res) {
  const { data, error } = await supabase
    .from('categoria')
    .delete()
    .eq('id', req.validado.params.id)
    .eq('usuario_id', req.usuarioId)
    .select('id')
    .maybeSingle();

  if (error?.code === '23503') {
    throw new HttpError(409, 'No se puede eliminar: la categoría tiene movimientos asociados');
  }
  if (!verificar({ data, error })) throw new HttpError(404, 'Categoría no encontrada');
  res.status(204).end();
}
