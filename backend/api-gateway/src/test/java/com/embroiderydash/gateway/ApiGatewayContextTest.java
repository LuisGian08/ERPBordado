package com.embroiderydash.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties={"JWT_SECRET=test-secret-with-at-least-32-bytes-for-hmac"})
class ApiGatewayContextTest {
    @Autowired RouteDefinitionLocator routes;
    @Test void cargaLasCincoRutasDeServicios(){var ids=routes.getRouteDefinitions().map(r->r.getId()).collectList().block();assertTrue(ids!=null&&ids.containsAll(java.util.List.of("servicio-disenos","servicio-inventario","servicio-trabajos","servicio-pagos","servicio-auth")));}
}
