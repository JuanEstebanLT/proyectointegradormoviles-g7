import { createClient } from '@supabase/supabase-js';
import { env } from './env.js';

// Cliente con service_role: omite RLS, por eso solo se usa en el servidor.
// Toda consulta debe filtrar explícitamente por usuario_id.
export const supabase = createClient(env.supabaseUrl, env.supabaseServiceRoleKey, {
  auth: { persistSession: false, autoRefreshToken: false }
});
