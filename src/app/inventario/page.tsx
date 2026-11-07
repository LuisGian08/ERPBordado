'use client';

import { useCallback, useEffect, useState } from 'react';
import { api } from '@/lib/api';
import { AuthImage } from '@/components/AuthImage';
import { getSession } from '@/lib/session';
import type { Thread } from '@/lib/types';

type Form = { codigoColor: string; nombreColor: string; stockActualConos: number; precioCono: number };
type MovementKind = 'ENTRADA' | 'SALIDA';
const empty: Form = { codigoColor: '', nombreColor: '', stockActualConos: 0, precioCono: 0 };
const inventoryApi = (process.env.NEXT_PUBLIC_INVENTARIO_API_URL || process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8095').replace(/\/$/, '');

export default function Inventario() {
  const isAdmin = ['ADMIN', 'ADMIN_EMPRESA'].includes(getSession()?.user.role || '');
  const [threads, setThreads] = useState<Thread[]>([]);
  const [error, setError] = useState('');
  const [modal, setModal] = useState(false);
  const [busy, setBusy] = useState(false);
  const [form, setForm] = useState<Form>(empty);
  const [editing, setEditing] = useState<Thread | null>(null);
  const [movement, setMovement] = useState<Thread | null>(null);
  const [movementKind, setMovementKind] = useState<MovementKind>('ENTRADA');
  const [moveQty, setMoveQty] = useState('1');
  const [image, setImage] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState('');

  const load = useCallback(async () => {
    try { setThreads(await api<Thread[]>('/api/v1/inventario/hilos')); setError(''); }
    catch (e) { setError(e instanceof Error ? e.message : 'No se pudo cargar inventario'); }
  }, []);
  useEffect(() => { void load(); }, [load]);
  useEffect(() => {
    if (!image) { setImagePreview(''); return; }
    const url = URL.createObjectURL(image);
    setImagePreview(url);
    return () => URL.revokeObjectURL(url);
  }, [image]);

  const closeForm = () => { setModal(false); setEditing(null); setForm(empty); setImage(null); setImagePreview(''); };
  const save = async (e: React.FormEvent) => {
    e.preventDefault(); setBusy(true); setError('');
    try {
      const payload = {
        marca: editing?.marca || 'Taller', tipoMaterial: editing?.tipoMaterial || 'Hilo de bordado',
        codigoColor: form.codigoColor.trim(), nombreColor: form.nombreColor.trim(),
        referencia: editing?.referencia || form.codigoColor.trim(), stockActualConos: Number(form.stockActualConos),
        metrosPorCono: editing?.metrosPorCono || 5000, stockMinimoConos: editing?.stockMinimoConos || 5,
        precioCono: Number(form.precioCono),
      };
      const saved = await api<Thread>(editing ? `/api/v1/inventario/hilos/${editing.id}` : '/api/v1/inventario/hilos', {
        method: editing ? 'PUT' : 'POST', body: JSON.stringify(payload),
      });
      if (image) {
        const body = new FormData(); body.append('archivo', image);
        await api<void>(`/api/v1/inventario/hilos/${saved.id}/imagen`, { method: 'POST', body });
      }
      closeForm(); await load();
    } catch (ex) { setError(ex instanceof Error ? ex.message : 'No se pudo guardar el hilo'); }
    finally { setBusy(false); }
  };
  const edit = (thread: Thread) => {
    setEditing(thread);
    setForm({ codigoColor: thread.codigoColor, nombreColor: thread.nombreColor, stockActualConos: thread.stockActualConos, precioCono: thread.precioCono });
    setImage(null); setModal(true);
  };
  const openMovement = (thread: Thread, kind: MovementKind) => { setMovement(thread); setMovementKind(kind); setMoveQty('1'); setError(''); };
  const saveMovement = async () => {
    if (!movement) return;
    setBusy(true); setError('');
    try {
      await api('/api/v1/inventario/movimientos', { method: 'POST', body: JSON.stringify({
        hiloId: movement.id, tipoMovimiento: movementKind, cantidadConos: Number(moveQty),
        motivo: movementKind === 'ENTRADA' ? 'Compra de hilos' : 'Consumo de hilo', referenciaTrabajoId: null,
      }) });
      setMovement(null); await load();
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudo registrar el movimiento'); }
    finally { setBusy(false); }
  };

  return <div className="content">
    <div className="titleline"><div><div className="eyebrow">Almacén</div><h1>Inventario de hilos</h1><p className="subtitle">Registra colores, imágenes, compras y consumo de conos.</p></div>{isAdmin && <button className="btn" onClick={() => { setEditing(null); setForm(empty); setImage(null); setModal(true); }}>＋ Nuevo hilo</button>}</div>
    {error && <div className="error" role="alert">{error}<button className="iconbutton" style={{ float: 'right' }} onClick={() => setError('')}>×</button></div>}
    <div className="stats"><div className="card stat"><div className="stat-head">Referencias</div><div className="stat-value">{threads.length}</div></div><div className="card stat"><div className="stat-head">Conos disponibles</div><div className="stat-value">{threads.reduce((n, t) => n + t.stockActualConos, 0)}</div></div><div className="card stat"><div className="stat-head">Stock bajo</div><div className="stat-value">{threads.filter(t => t.alertaStockMinimo).length}</div></div></div>
    <section className="card panel"><div className="panelhead"><h2>Catálogo de hilos</h2><button className="btn secondary" onClick={() => void load()}>Actualizar</button></div>
      <div className="tablewrap"><table><thead><tr><th>Imagen</th><th>Color</th><th>Código</th><th>Stock actual</th><th>Compra por cono</th><th>Acciones</th></tr></thead><tbody>
        {threads.map(t => <tr key={t.id}><td>{t.tieneImagen ? <AuthImage src={`${inventoryApi}/api/v1/inventario/hilos/${t.id}/imagen`} alt={`Hilo ${t.nombreColor}`} className="threadthumb"/> : <span className="threadthumb thread-placeholder" title="No hay imagen guardada">{t.nombreColor.slice(0, 1).toUpperCase()}</span>}</td><td><b>{t.nombreColor}</b></td><td>{t.codigoColor}</td><td>{t.stockActualConos.toLocaleString('es-PE')}</td><td>S/ {Number(t.precioCono).toFixed(2)}</td><td><div className="designactions">{isAdmin && <button className="btn secondary" onClick={() => edit(t)}>Editar</button>}<button className="btn secondary" onClick={() => openMovement(t, 'ENTRADA')}>＋ Entrada</button><button className="btn secondary" disabled={t.stockActualConos < 1} onClick={() => openMovement(t, 'SALIDA')}>− Salida</button></div></td></tr>)}
      </tbody></table></div>
    </section>
    {isAdmin && modal && <div className="modalback"><section className="modal" role="dialog" aria-modal="true"><div className="modalhead"><h2>{editing ? 'Editar hilo' : 'Registrar hilo'}</h2><button className="iconbutton" onClick={closeForm} aria-label="Cerrar">×</button></div><form onSubmit={save}><div className="formgrid">
      <div className="field"><label>Código de color *</label><input required maxLength={50} value={form.codigoColor} onChange={e => setForm({ ...form, codigoColor: e.target.value })}/></div>
      <div className="field"><label>Nombre del color *</label><input required maxLength={100} value={form.nombreColor} onChange={e => setForm({ ...form, nombreColor: e.target.value })}/></div>
      <div className="field"><label>{editing ? 'Stock actual' : 'Valor inicial (conos)'} *</label><input required type="number" min="0" disabled={Boolean(editing)} value={form.stockActualConos} onChange={e => setForm({ ...form, stockActualConos: Number(e.target.value) })}/>{editing && <small>Registra una entrada o salida para cambiar el stock.</small>}</div>
      <div className="field"><label>Precio de compra por cono (S/) *</label><input required type="number" min="0" step="0.01" value={form.precioCono} onChange={e => setForm({ ...form, precioCono: Number(e.target.value) })}/></div>
      <div className="field span2"><label>Imagen del hilo (JPG o PNG, hasta 2 MB)</label><input type="file" accept="image/jpeg,image/png" onChange={e => setImage(e.target.files?.[0] || null)}/>{imagePreview && <img className="threadthumb large" src={imagePreview} alt="Vista previa de imagen del hilo"/>}{editing && !image && editing.tieneImagen && <small>La imagen actual se conserva. Selecciona otra para reemplazarla.</small>}</div>
    </div><div className="formfoot"><button type="button" className="btn secondary" onClick={closeForm}>Cancelar</button><button className="btn" disabled={busy}>{busy ? 'Guardando…' : editing ? 'Guardar cambios' : 'Registrar hilo'}</button></div></form></section></div>}
    {movement && <div className="modalback"><section className="modal" role="dialog" aria-modal="true"><div className="modalhead"><h2>{movementKind === 'ENTRADA' ? 'Entrada' : 'Salida'} de hilo · {movement.codigoColor}</h2><button className="iconbutton" onClick={() => setMovement(null)} aria-label="Cerrar">×</button></div><p>{movement.nombreColor} · Stock actual: {movement.stockActualConos} conos</p><div className="field"><label>{movementKind === 'ENTRADA' ? 'Cantidad de conos comprados' : 'Cantidad de conos usados'}</label><input type="number" min="1" max={movementKind === 'SALIDA' ? movement.stockActualConos : undefined} value={moveQty} onChange={e => setMoveQty(e.target.value)}/></div>{movementKind === 'ENTRADA' ? <p className="hint">Gasto registrado: S/ {(Number(moveQty || 0) * Number(movement.precioCono || 0)).toFixed(2)}</p> : <p className="hint">La salida reduce existencias; no se registra como gasto de compra.</p>}<div className="formfoot"><button className="btn secondary" onClick={() => setMovement(null)}>Cancelar</button><button className="btn" disabled={busy || Number(moveQty) < 1 || (movementKind === 'SALIDA' && Number(moveQty) > movement.stockActualConos)} onClick={() => void saveMovement()}>{busy ? 'Guardando…' : movementKind === 'ENTRADA' ? 'Registrar entrada y gasto' : 'Registrar salida'}</button></div></section></div>}
  </div>;
}
