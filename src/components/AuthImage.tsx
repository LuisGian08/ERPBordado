'use client';
import { useEffect,useState } from 'react';
import { getSession } from '@/lib/session';

const cache=new Map<string,{url:string;refs:number}>();
export function AuthImage({src,alt,className}:{src:string;alt:string;className?:string}) {
 const[url,setUrl]=useState<string|null>(cache.get(src)?.url||null);
 useEffect(()=>{let live=true;let acquired=false;const retain=(entry:{url:string;refs:number})=>{entry.refs++;acquired=true;setUrl(entry.url)};const release=()=>{const entry=cache.get(src);if(acquired&&entry){entry.refs--;if(entry.refs<=0){URL.revokeObjectURL(entry.url);cache.delete(src)}}};const existing=cache.get(src);if(existing){retain(existing);return()=>{live=false;release()}}setUrl(null);const token=getSession()?.token;if(!token)return()=>{live=false};fetch(src,{headers:{Authorization:`Bearer ${token}`},cache:'no-store'}).then(response=>{if(!response.ok)throw new Error('No se pudo cargar la imagen');return response.blob()}).then(blob=>{const entry={url:URL.createObjectURL(blob),refs:0};if(!live){URL.revokeObjectURL(entry.url);return}cache.set(src,entry);retain(entry)}).catch(()=>{if(live)setUrl(null)});return()=>{live=false;release()}},[src]);
 return url?<img src={url} alt={alt} className={className}/>:<span className={className} role="img" aria-label={alt}>Vista previa no disponible</span>;
}
