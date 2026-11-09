import { supabase } from '../config/supabase.js';
import { calcularResumen } from '../services/resumen.service.js';
import { reglas } from '../services/reglasSugerencias.js';
import { verificar } from '../utils/errores.js';
import { rangoMesActual } from '../utils/fechas.js';

// GET /sugerencias            → sugerencias que aplican al usuario según su mes actual (HU-08)
// GET /sugerencias?todas=true → catálogo completo de sugerencias activas
export async function listar(req, res) {
  const sugerencias = verificar(
    await supabase
      .from('sugerencia')
      .select('id, titulo, mensaje, regla_asociada, prioridad, sugerencia_categoria (categoria_id)')
      .eq('activa', true)
      .order('prioridad', { ascending: false })
  );

  const limpiar = ({ sugerencia_categoria: _, ...s }, categorias = []) => ({ ...s, categorias });

  if (req.query.todas === 'true') {
    return res.json(sugerencias.map((s) => limpiar(s)));
  }

  const [resumen, configuracion] = await Promise.all([
    calcularResumen(req.usuarioId, rangoMesActual()),
    supabase.from('configuracion').select('meta_ahorro_mensual').eq('usuario_id', req.usuarioId).maybeSingle().then(verificar)
  ]);

  const aplicables = [];
  for (const sugerencia of sugerencias) {
    const regla = reglas[sugerencia.regla_asociada];
    if (!regla) continue;

    let resultado = regla(resumen, configuracion);
    if (!resultado) continue;

    // Relación N:M: si la sugerencia está vinculada a categorías concretas, solo aplica a esas.
    const vinculadas = sugerencia.sugerencia_categoria.map((v) => v.categoria_id);
    if (vinculadas.length > 0) {
      const candidatas = Array.isArray(resultado) ? resultado : resumen.por_categoria.map((c) => c.categoria_id);
      resultado = candidatas.filter((id) => vinculadas.includes(id));
      if (resultado.length === 0) continue;
    }

    const ids = Array.isArray(resultado) ? resultado : [];
    const categorias = resumen.por_categoria
      .filter((c) => ids.includes(c.categoria_id))
      .map(({ categoria_id, nombre }) => ({ id: categoria_id, nombre }));
    aplicables.push(limpiar(sugerencia, categorias));
  }

  res.json(aplicables);
}
