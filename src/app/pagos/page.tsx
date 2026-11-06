'use client';

import { WorkCollections } from '@/components/WorkCollections';

export default function PagosPage() {
  return <div className="content">
    <div className="titleline"><div><div className="eyebrow">Cuentas por cobrar</div><h1>Pagos</h1><p className="subtitle">Los trabajos registrados aparecen aquí para consultar saldos y registrar abonos.</p></div></div>
    <WorkCollections />
  </div>;
}
