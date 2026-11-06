export type SessionUser={id?:number;username:string;displayName:string;role:string;enabled?:boolean};
type Session={token:string;user:SessionUser};
let current:Session|null=null;
const listeners=new Set<()=>void>();
export function getSession(){return current}
export function setSession(session:Session){current=session;listeners.forEach(fn=>fn())}
export function clearSession(){current=null;listeners.forEach(fn=>fn())}
export function subscribeSession(fn:()=>void){listeners.add(fn);return()=>{listeners.delete(fn)}}
