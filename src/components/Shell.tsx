'use client';
import Link from 'next/link';
import {usePathname,useRouter} from 'next/navigation';
import {useEffect,useRef,useState} from 'react';
import {clearSession,getSession,subscribeSession,type SessionUser} from '@/lib/session';

const links=[['/','⌂','Dashboard'],['/disenos','▧','Diseños de bordado'],['/inventario','◉','Inventario de hilos'],['/trabajos','▤','Trabajos'],['/costos','$','Precio de diseño'],['/pagos','⇄','Pagos'],['/nomina','◷','Pago de operadores'],['/reportes','▥','Reportes'],['/usuarios','♙','Usuarios']] as const;
const roleNames:Record<string,string>={ADMIN:'Administrador maestro',ADMIN_EMPRESA:'Administrador de empresa',DISENADOR:'Diseñador',OPERADOR:'Operario'};
const rolePaths:Record<string,string[]>={ADMIN:links.map(([href])=>href),ADMIN_EMPRESA:links.filter(([href])=>href!=='/usuarios').map(([href])=>href),DISENADOR:['/disenos'],OPERADOR:['/disenos','/inventario']};

export function Shell({children}:{children:React.ReactNode}){
 const path=usePathname();const router=useRouter();const[open,setOpen]=useState(false);const[user,setUser]=useState<SessionUser|null>(null);const[userMenuOpen,setUserMenuOpen]=useState(false);const userMenuRef=useRef<HTMLDivElement>(null);
 const title=links.find(([href])=>href===path)?.[2]||'EmbroideryDash ERP';
 useEffect(()=>{const sync=()=>setUser(getSession()?.user||null);sync();return subscribeSession(sync)},[]);
 useEffect(()=>{setOpen(false);setUserMenuOpen(false)},[path]);
 useEffect(()=>{if(!userMenuOpen)return;const closeOnOutside=(event:PointerEvent)=>{if(!userMenuRef.current?.contains(event.target as Node))setUserMenuOpen(false)};const closeOnEscape=(event:KeyboardEvent)=>{if(event.key==='Escape')setUserMenuOpen(false)};document.addEventListener('pointerdown',closeOnOutside);document.addEventListener('keydown',closeOnEscape);return()=>{document.removeEventListener('pointerdown',closeOnOutside);document.removeEventListener('keydown',closeOnEscape)}},[userMenuOpen]);
 const allowed=!!user&&(rolePaths[user.role]||[]).some(route=>route==='/'?path==='/':path===route||path.startsWith(route+'/'));
 useEffect(()=>{if(path!=='/login'&&!user)router.replace('/login');else if(user&&!allowed)router.replace(rolePaths[user.role]?.[0]||'/login')},[path,user,allowed,router]);
 if(path==='/login')return <>{children}</>;
 if(!user||!allowed)return <div className="content">Verificando permisos…</div>;
 const visibleLinks=links.filter(([href])=>(rolePaths[user.role]||[]).includes(href));
 const initials=user.displayName.slice(0,2).toUpperCase();
 const logout=()=>{setUserMenuOpen(false);clearSession();router.replace('/login')};
 return <div className="shell">
  {open&&<button className="sidebar-scrim" aria-label="Cerrar navegación" onClick={()=>setOpen(false)}/>}
  <aside className={`sidebar ${open?'open':''}`}>
   <Link href="/" className="brand"><span className="brandmark">✳</span><span>EmbroideryDash<small>ERP · TALLER</small></span></Link>
   <div className="navlabel">Espacio de trabajo</div>
   <nav className="navlinks">{visibleLinks.map(([href,icon,label])=><Link onClick={()=>setOpen(false)} className={`navlink ${path===href?'active':''}`} href={href} key={href}><span className="navico">{icon}</span>{label}</Link>)}</nav>
   <div className="sidebottom"><div className="userbox"><span className="avatar">{initials}</span><span>{user.displayName}<small>{roleNames[user.role]||'Usuario'}</small></span></div></div>
  </aside>
  <main className="main">
   <header className="topbar">
    <div className="topbar-leading"><button className="iconbutton mobiletoggle" onClick={()=>setOpen(value=>!value)} aria-label={open?'Cerrar navegación':'Abrir navegación'} aria-expanded={open}>☰</button><span className="crumb">EmbroideryDash <span className="crumb-separator">/</span> {title}</span></div>
    <div className="topright"><div className="account-menu" ref={userMenuRef}>
     <button className={`account-trigger ${userMenuOpen?'selected':''}`} onClick={()=>setUserMenuOpen(value=>!value)} aria-haspopup="menu" aria-expanded={userMenuOpen}>
      <span className="account-copy"><strong>{user.displayName}</strong><small>{roleNames[user.role]||'Usuario'}</small></span><span className="avatar account-avatar">{initials}</span><span className="account-chevron" aria-hidden="true">⌄</span>
     </button>
     {userMenuOpen&&<div className="account-dropdown" role="menu"><div className="account-dropdown-head"><strong>{user.displayName}</strong><span>@{user.username}</span></div><button className="logout-option" role="menuitem" onClick={logout}><span aria-hidden="true">↪</span>Cerrar sesión</button></div>}
    </div></div>
   </header>
   {children}
  </main>
 </div>;
}
