package com.embroiderydash.trabajos.domain.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 150)
    private String nombre;
    @Column(length = 100)
    private String contacto;
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @PrePersist void crearMarcaTiempo() { creadoEn = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
}
