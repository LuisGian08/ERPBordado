'use client';
import Link from 'next/link';
import {usePathname,useRouter} from 'next/navigation';
import {useEffect,useState} from 'react';
import {clearSession,getSession,subscribeSession,type SessionUser} from '@/lib/session';

const links=[['/','⌂','Dashboard'],['/disenos','▧','Diseños de bordado'],['/inventario','◉','Inventario de hilos'],['/trabajos','▤','Trabajos'],['/costos','＄','Precio de diseño'],['/pagos','⇄','Pagos'],['/reportes','▥','Reportes'],['/usuarios','♙','Usuarios']] as const;
const roleNames:Record<string,string>={ADMIN:'Administrador maestro',ADMIN_EMPRESA:'Administrador de empresa',DISENADOR:'Diseñador',OPERADOR:'Operario'};
const rolePaths:Record<string,string[]>={ADMIN:links.map(([href])=>href),ADMIN_EMPRESA:links.filter(([href])=>href!=='/usuarios').map(([href])=>href),DISENADOR:['/disenos'],OPERADOR:['/disenos','/inventario']};

export function Shell({children}:{children:React.ReactNode}){
 const path=usePathname();const router=useRouter();const[open,setOpen]=useState(false);const[user,setUser]=useState<SessionUser|null>(null);
 const title=links.find(([href])=>href===path)?.[2]||'EmbroideryDash ERP';
 useEffect(()=>{const sync=()=>setUser(getSession()?.user||null);sync();return subscribeSession(sync)},[]);
 const allowed=!!user&&(rolePaths[user.role]||[]).some(route=>route==='/'?path==='/':path===route||path.startsWith(route+'/'));
 useEffect(()=>{if(path!=='/login'&&!user)router.replace('/login');else if(user&&!allowed)router.replace(rolePaths[user.role]?.[0]||'/login')},[path,user,allowed,router]);
 if(path==='/login')return <>{children}</>;
 if(!user||!allowed)return <div className="content">Verificando permisos…</div>;
 const visibleLinks=links.filter(([href])=>(rolePaths[user.role]||[]).includes(href));
 return <div className="shell"><aside className={`sidebar ${open?'open':''}`}><Link href="/" className="brand"><span className="brandmark">✳</span><span>EmbroideryDash<small>ERP · TALLER</small></span></Link><div className="navlabel">Espacio de trabajo</div><nav className="navlinks">{visibleLinks.map(([href,icon,label])=><Link onClick={()=>setOpen(false)} className={`navlink ${path===href?'active':''}`} href={href} key={href}><span className="navico">{icon}</span>{label}</Link>)}</nav><div className="sidebottom"><div className="userbox"><span className="avatar">{user.displayName.slice(0,2).toUpperCase()}</span><span>{user.displayName}<small>{roleNames[user.role]||'Usuario'}</small></span></div></div></aside><main className="main"><header className="topbar"><div style={{display:'flex',alignItems:'center',gap:12}}><button className="iconbutton mobiletoggle" onClick={()=>setOpen(!open)} aria-label="Abrir navegación">☰</button><span className="crumb">EmbroideryDash <span style={{color:'#c4cecb'}}> / </span> {title}</span></div><div className="topright"><span className="muted" style={{fontSize:12}}>{user.username}</span><button className="btn secondary" onClick={()=>{clearSession();router.replace('/login')}} style={{padding:'8px 11px',fontSize:11}}>Cerrar sesión</button><span className="avatar" style={{width:30,height:30,fontSize:11}}>{user.displayName.slice(0,2).toUpperCase()}</span></div></header>{children}</main></div>
}
