import './globals.css';
import type { Metadata } from 'next';
import { Shell } from '@/components/Shell';
export const metadata: Metadata = { title: 'EmbroideryDash ERP', description: 'Gestión de taller de bordado' };
export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) { return <html lang="es"><body><Shell>{children}</Shell></body></html>; }
