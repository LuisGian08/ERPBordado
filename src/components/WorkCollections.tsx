'use client';

import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { api } from '@/lib/api';

type Work = {
  id: number;
  codigoTrabajo: string;
  clienteNombre: string;
  nombreDisenoHistorico: string;
  cantidadPrendas: number;
  precioVenta: number;
  estado: string;
  creadoEn: string;
};
type WorkPage = { content: Work[] };
type Summary = { trabajoId: number; precioVenta: number; totalPagado: number; saldoPendiente: number; estado: 'PENDIENTE' | 'PARCIAL' | 'PAGADO' | 'ANULADO' };
type Collection = { work: Work; summary: Summary | null };
const currency = (value: number) => `S/ ${Number(value || 0).toFixed(2)}`;

export function WorkCollections() {
  const [rows, setRows] = useState<Collection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selected, setSelected] = useState<Collection | null>(null);
  const [amount, setAmount] = useState('');
  const [method, setMethod] = useState('EFECTIVO');
  const [reference, setReference] = useState('');
  const [saving, setSaving] = useState(false);
  const [query, setQuery] = useState('');
  const paymentKey = useRef('');

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({ page: '0', size: '100', sort: 'creadoEn,desc' });
      if (query.trim()) params.set('cliente', query.trim());
      const result = await api<WorkPage>(`/api/v1/trabajos?${params.toString()}`);
      const collections = await Promise.all((result.content || []).map(async work => {
        if (work.estado === 'CANCELADO') return { work, summary: null };
        try { return { work, summary: await api<Summary>(`/api/v1/pagos/trabajo/${work.id}/resumen`) }; }
        catch { return { work, summary: null }; }
      }));
      setRows(collections);
      setError('');
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudieron cargar las cobranzas de trabajos'); }
    finally { setLoading(false); }
  }, [query]);
  useEffect(() => { const timer = window.setTimeout(() => void load(), 250); return () => window.clearTimeout(timer); }, [load]);
  const filteredRows = useMemo(() => rows, [rows]);

  const openPayment = (row: Collection) => {
    if (!row.summary || row.summary.estado === 'PAGADO' || row.work.estado === 'CANCELADO') return;
    setError('');
    paymentKey.current = '';
    setSelected(row); setAmount(String(row.summary.saldoPendiente)); setReference(''); setMethod('EFECTIVO');
  };
  const registerPayment = async () => {
    if (!selected || saving) return;
    setSaving(true); setError('');
    const key = paymentKey.current || crypto.randomUUID();
    paymentKey.current = key;
    try {
      await api('/api/v1/pagos', {
        method: 'POST',
        headers: { 'Idempotency-Key': key },
        body: JSON.stringify({ trabajoId: selected.work.id, monto: Number(amount), metodoPago: method, referenciaTransaccion: reference || null, notas: null }),
      });
      paymentKey.current = '';
      setSelected(null);
      await load();
    } catch (e) { setError(e instanceof Error ? e.message : 'No se pudo registrar el pago'); }
    finally { setSaving(false); }
  };
  const status = (row: Collection) => {
    if (row.work.estado === 'CANCELADO') return 'Cancelado';
    if (!row.summary) return 'No disponible';
    return row.summary.estado === 'PENDIENTE' ? 'Pendiente de pago' : row.summary.estado === 'PARCIAL' ? 'Pago parcial' : row.summary.estado === 'ANULADO' ? 'Anulado' : 'Pagado';
  };

  return <>
    <section className="card panel collections-panel">
      <div className="panelhead"><div><h2>Pagos de trabajos</h2><p className="muted">Consulta saldos, busca por cliente y registra abonos.</p></div><button className="btn secondary" onClick={() => void load()}>Actualizar</button></div>
      {error && <div className="error" role="alert">{error}</div>}
      <div className="tools"><div className="search"><input aria-label="Buscar pagos por cliente" placeholder="Buscar por cliente…" value={query} onChange={e => setQuery(e.target.value)} /></div><span className="muted">{filteredRows.length} trabajos</span></div>
      {loading ? <p className="emptyrow">Cargando trabajos…</p> : !filteredRows.length ? <p className="emptyrow">{rows.length ? 'No hay resultados para esa búsqueda.' : 'No hay trabajos para cobrar.'}</p> : <div className="tablewrap"><table><thead><tr><th>Trabajo</th><th>Cliente</th><th>Diseño</th><th>Total</th><th>Pagado</th><th>Saldo</th><th>Estado de pago</th><th/></tr></thead><tbody>{filteredRows.map(row => <tr key={row.work.id}>
        <td><b>{row.work.codigoTrabajo}</b></td><td>{row.work.clienteNombre}</td><td>{row.work.nombreDisenoHistorico}</td><td>{currency(row.work.precioVenta)}</td><td>{row.summary ? currency(row.summary.totalPagado) : "—"}</td><td>{row.summary ? currency(row.summary.saldoPendiente) : "—"}</td><td><span className={`badge ${row.work.estado === 'CANCELADO' || row.summary?.estado === 'ANULADO' ? 'warning' : row.summary?.estado === 'PAGADO' ? '' : 'gray'}`}>{status(row)}</span></td>
        <td><button className="btn secondary" disabled={!row.summary || row.summary.estado === 'PAGADO' || row.work.estado === 'CANCELADO'} onClick={() => openPayment(row)}>Registrar pago</button></td>
      </tr>)}</tbody></table></div>}
    </section>
    {selected && <div className="modalback"><section className="modal" role="dialog" aria-modal="true" aria-labelledby="work-payment-title"><div className="modalhead"><div><div className="eyebrow">Cobranza de trabajo</div><h2 id="work-payment-title">{selected.work.codigoTrabajo}</h2></div><button className="iconbutton" onClick={() => setSelected(null)} aria-label="Cerrar">×</button></div>{error && <div className="error" role="alert">{error}</div>}<p>{selected.work.clienteNombre} · {selected.work.nombreDisenoHistorico}</p><p className="muted">Saldo pendiente: <b>{currency(selected.summary?.saldoPendiente || 0)}</b></p><div className="formgrid"><div className="field"><label>Monto del abono</label><input type="number" min="0.01" max={selected.summary?.saldoPendiente} step="0.01" value={amount} onChange={e => { setAmount(e.target.value); paymentKey.current = ''; }} /></div><div className="field"><label>Método de pago</label><select className="select" value={method} onChange={e => { setMethod(e.target.value); paymentKey.current = ''; }}><option>EFECTIVO</option><option>TRANSFERENCIA</option><option>TARJETA</option><option>YAPE_PLIN</option></select></div><div className="field span2"><label>Referencia</label><input value={reference} onChange={e => { setReference(e.target.value); paymentKey.current = ''; }} maxLength={100} /></div></div><div className="formfoot"><button className="btn secondary" onClick={() => setSelected(null)}>Cancelar</button><button className="btn" disabled={saving || Number(amount) <= 0 || Number(amount) > Number(selected.summary?.saldoPendiente)} onClick={() => void registerPayment()}>{saving ? 'Registrando…' : 'Registrar abono'}</button></div></section></div>}
  </>;
}
