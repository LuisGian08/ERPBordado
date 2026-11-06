const fs = require('node:fs');
function edit(path, fn) { const before = fs.readFileSync(path, 'utf8'); fs.writeFileSync(path, fn(before)); }
edit('src/components/Shell.tsx', source => source
  .replace(/^const roleNames:.*$/m, "const roleNames:Record<string,string>={ADMIN:'Administrador maestro',ADMIN_EMPRESA:'Administrador de empresa',DISENADOR:'Diseñador',OPERADOR:'Operario'};")
  .replace(/^const rolePaths:.*$/m, "const rolePaths:Record<string,string[]>={ADMIN:links.map(([href])=>href),ADMIN_EMPRESA:links.filter(([href])=>href!=='/usuarios').map(([href])=>href),DISENADOR:['/disenos'],OPERADOR:['/disenos','/inventario']};"));
edit('backend/servicio-pagos/src/main/java/com/embroiderydash/pagos/config/SecurityConfig.java', source => source.replace('.requestMatchers(HttpMethod.POST,"/api/v1/pagos/*/anular").hasRole("ADMIN")', '.requestMatchers(HttpMethod.POST,"/api/v1/pagos/*/anular").hasAnyRole("ADMIN","ADMIN_EMPRESA")'));
