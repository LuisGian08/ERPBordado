'use client';
import './auth.css';
import Link from 'next/link';
import {FormEvent,useState} from 'react';
import {useRouter} from 'next/navigation';
import {api} from '@/lib/api';
import {setSession,type SessionUser} from '@/lib/session';
type LoginResult={accessToken:string;tokenType:string;expiresIn:number;user:SessionUser};
export default function Login(){const router=useRouter();const[username,setUsername]=useState('');const[password,setPassword]=useState('');const[error,setError]=useState('');const[busy,setBusy]=useState(false);async function submit(e:FormEvent){e.preventDefault();setError('');setBusy(true);try{const result=await api<LoginResult>('/api/v1/auth/login',{method:'POST',body:JSON.stringify({username,password})});setSession({token:result.accessToken,user:result.user});router.replace('/')}catch(e){setError(e instanceof Error?e.message:'No se pudo iniciar sesión')}finally{setBusy(false)}}return <main className="loginpage"><section className="loginbox"><div className="brand loginbrand"><span className="brandmark">✳</span><span>EmbroideryDash<small>ERP · TALLER</small></span></div><div className="eyebrow">Acceso al taller</div><h1>Iniciar sesión</h1><p className="subtitle">Ingresa con el usuario que te proporcionó el administrador.</p>{error&&<div className="error" role="alert">{error}</div>}<form onSubmit={submit} className="loginform"><div className="field"><label htmlFor="username">Usuario</label><input id="username" autoComplete="username" required value={username} onChange={e=>setUsername(e.target.value)}/></div><div className="field"><label htmlFor="password">Contraseña</label><input id="password" type="password" autoComplete="current-password" required value={password} onChange={e=>setPassword(e.target.value)}/></div><button className="btn" disabled={busy}>{busy?'Verificando…':'Ingresar'}</button></form><p className="loginhint">¿Necesitas acceso? Solicita una cuenta al administrador del sistema.</p></section></main>}

