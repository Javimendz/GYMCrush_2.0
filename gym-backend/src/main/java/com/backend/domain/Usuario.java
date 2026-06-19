//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    // @Column(name = "nombre", nullable = false)
    // private String nombre;

    // @Column(name = "apellidos", nullable = false)
    // private String apellidos;

    @Column(name = "email", unique = true, nullable = false, length = 150)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // @Column(name = "direccion", length = 255)
    // private String direccion;

    public Set<Role> getRoles() {
        return this.roles;
    }
    // @Column(name = "ciudad", length = 100)
    // private String ciudad;

    // @Column(name = "pais", length = 100)
    // private String pais;

    // @Column(name = "codigo_postal", length = 20)
    // private String codigoPostal;
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles = new HashSet<>();

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @Column(name = "token_expira_en", nullable = true)
    private LocalDateTime tokenExpiraEn;

    @Column(name = "fecha_registro", nullable = true)
    private LocalDateTime fechaRegistro;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // Cambiamos
                                                                                                              // a EAGER
                                                                                                              // par //
                                                                                                              // Login
    @OnDelete(action = OnDeleteAction.CASCADE)
    
    @ToString.Exclude // Para evitar bucle infinito con Lombok
    private Perfil perfil; // Relación 1:1 con Perfil

     @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<Notificacion> notificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<Reserva> reservas = new ArrayList<>(); // Un usuario realiza N reservas

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Salud> salud = new ArrayList<>();// Un usuario registra N datos de salud

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Nutricion> planes = new ArrayList<>(); // Un usuario recibe N planes

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>(); // Un usuario crea N tickets

    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL)
    private List<Chat> mensajesEnviados;

    @OneToMany(mappedBy = "receptor", cascade = CascadeType.ALL)
    private List<Chat> mensajesRecibidos;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Rutina> rutinas = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Visualizacion> visualizaciones = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsuarios().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsuarios().remove(this);
    }

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "plan_activo_id")
private PlanEntrenamiento planActivo;

@OneToMany(mappedBy = "entrenador")
private List<Horario> horarios;

}
