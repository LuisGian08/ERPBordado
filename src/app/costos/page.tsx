'use client';

import { useCallback, useEffect, useState } from 'react';
import { api } from '@/lib/api';
import { AuthImage } from '@/components/AuthImage';
import { getSession } from '@/lib/session';
import type { CatalogContents } from '@/lib/types';

const gateway = (process.env.NEXT_PUBLIC_DISENOS_API_URL || process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8095').replace(/\/$/, '');

export default function Costos() {
  const [folder, setFolder] = useState<number | null>(null);
  const [contents, setContents] = useState<CatalogContents>({ carpetaActual: {}, ruta: [], subcarpetas: [], disenos: [] });
  const [error, setError] = useState('');
  const [editing, setEditing] = useState<number | null>(null);
  const [price, setPrice] = useState('');
  const [reason, setReason] = useState('');
  const [history, setHistory] = useState<Record<number, unknown[]>>({});
  const admin = ['ADMIN', 'ADMIN_EMPRESA'].includes(getSession()?.user.role || '');

  const load = useCallback(async (id = folder) => {
    try {
      const suffix = id === null ? '' : `?carpetaId=${id}`;
      setContents(await api<CatalogContents>(`/api/v1/carpetas/contenido${suffix}`));
      setError('');
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudieron cargar los precios'); }
  }, [folder]);
  useEffect(() => { void load(folder); }, [folder, load]);

  const save = async (id: number) => {
    try {
      await api(`/api/v1/disenos/${id}/precio`, { method: 'PUT', body: JSON.stringify({ precioUnitario: Number(price), motivo: reason || null }) });
      setEditing(null); await load();
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudo guardar el precio'); }
  };
  const showHistory = async (id: number) => {
    try { setHistory({ ...history, [id]: await api<unknown[]>(`/api/v1/disenos/${id}/precio/historial`) }); }
    catch (e) { setError(e instanceof Error ? e.message : 'No se pudo cargar el historial'); }
  };

  return <div className="content">
    <div className="titleline"><div><div className="eyebrow">Tarifario · PEN</div><h1>Precio de diseño</h1><p className="subtitle">Precio vigente por unidad de bordado y registro de cambios.</p></div></div>
    {error && <div className="error" role="alert">{error}</div>}
    <section className="card panel">
      <div className="breadcrumbs"><button className="btn secondary" onClick={() => setFolder(null)}>Raíz</button>{contents.ruta.map(item => <button className="btn secondary" key={item.id} onClick={() => setFolder(item.id)}>{item.nombre}</button>)}</div>
      <div className="foldergrid">{contents.subcarpetas.map(item => <button className="foldertile" key={item.id} onClick={() => setFolder(item.id)}>📁 {item.nombre}<small>{item.cantidadDisenos} diseños</small></button>)}</div>
      {!contents.disenos.length ? <p className="emptyrow">No hay diseños en esta carpeta.</p> : <div className="tablewrap"><table><thead><tr><th>Diseño</th><th>Puntadas</th><th>Medidas</th><th>Precio unitario</th><th>Historial</th></tr></thead><tbody>
        {contents.disenos.map(d => <tr key={d.id}><td><div className="pricedesign-name">{d.tieneMiniatura ? <AuthImage src={`${gateway}/api/v1/disenos/${d.id}/miniatura`} alt={`Miniatura de ${d.nombre}`} className="threadthumb"/> : <span className="threadthumb thread-placeholder" aria-label="Sin miniatura">{d.nombre.slice(0, 1).toUpperCase()}</span>}<b>{d.nombre}</b></div></td>
          <td>{d.puntadas.toLocaleString('es-PE')}</td><td>{d.anchoMm ?? '—'} × {d.altoMm ?? '—'} mm</td>
          <td>{editing === d.id ? <div className="inlineprice"><input aria-label="Precio unitario" type="number" min="0.01" step="0.01" value={price} onChange={e => setPrice(e.target.value)}/><input aria-label="Motivo del cambio" placeholder="Motivo opcional" value={reason} onChange={e => setReason(e.target.value)}/><button className="btn" onClick={() => void save(d.id)}>Guardar</button><button className="btn secondary" onClick={() => setEditing(null)}>Cancelar</button></div> : d.precioUnitario ? `S/ ${Number(d.precioUnitario).toFixed(2)} ${d.moneda}` : <span className="badge warning">Sin precio</span>}{admin && editing !== d.id && <button className="btn secondary" style={{ marginLeft: 8 }} onClick={() => { setPrice(d.precioUnitario ? String(d.precioUnitario) : ''); setReason(''); setEditing(d.id); }}>Editar</button>}</td>
          <td><button className="btn secondary" onClick={() => void showHistory(d.id)}>Ver cambios</button>{history[d.id] && <details><summary>{history[d.id].length} cambios</summary><pre className="historyjson">{JSON.stringify(history[d.id], null, 2)}</pre></details>}</td>
        </tr>)}
      </tbody></table></div>}
    </section>
  </div>;
}
