package com.backend.domain;

import java.time.LocalDate;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.backend.domain.enums.EnumGenero;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa el perfil personal de un usuario.
 * <p>
 * Almacena información demográfica y de contacto del usuario,
 * incluyendo datos personales, dirección y preferencias.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "perfiles")
@Getter
@Setter
@Data   
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Perfil {

    /**
     * Identificador único del perfil.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del usuario.
     * <p>
     * Obligatorio y con longitud máxima de 80 caracteres.
     * </p>
     */
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    /**
     * Apellidos del usuario.
     * <p>
     * Opcional y con longitud máxima de 150 caracteres.
     * </p>
     */
    @Column(name = "apellidos", length = 150)
    private String apellidos;

    /**
     * Número de teléfono del usuario.
     * <p>
     * Obligatorio, único y con longitud máxima de 20 caracteres.
     * </p>
     */
    @Column(name = "telefono", length = 20, unique = true, nullable = false)
    private String telefono;

    /**
     * Número de identificación fiscal del usuario.
     * <p>
     * Obligatorio, único y con longitud máxima de 20 caracteres.
     * </p>
     */
    @Column(name = "dni", unique = true, length = 20, nullable = false)
    private String dni;

    /**
     * Fecha de nacimiento del usuario.
     * <p>
     * Obligatorio, para cálculo de edad y validaciones.
     * </p>
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Dirección postal del usuario.
     * <p>
     * Obligatorio, para envíos y correspondencia.
     * </p>
     */
    @Column(name = "direccion", nullable = false)
    private String direccion;

    /**
     * Ciudad de residencia del usuario.
     * <p>
     * Obligatorio, para localización geográfica.
     * </p>
     */
    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    /**
     * País de residencia del usuario.
     * <p>
     * Obligatorio, para fines demográficos.
     * </p>
     */
    @Column(name = "pais", nullable = false)
    private String pais;

    /**
     * Código postal del usuario.
     * <p>
     * Obligatorio, para envíos postales.
     * </p>
     */
    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;

    /**
     * Biografía o descripción personal del usuario.
     * <p>
     * Opcional, permite texto largo para descripciones.
     * </p>
     */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /**
     * Género del usuario.
     * <p>
     * Obligatorio, clasificación demográfica básica.
     * </p>
     */
    @Column(name = "genero", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private EnumGenero genero;

    /**
     * URL o ruta de la foto de perfil del usuario.
     * <p>
     * Opcional, con longitud máxima de 255 caracteres.
     * </p>
     */
    @Column(name = "foto", length = 255, nullable = true, columnDefinition = "TEXT")
    private String foto;

    public String getNombreCompleto() {
        if (apellidos == null || apellidos.isEmpty()) {
            return nombre;
        }
        return nombre + " " + apellidos;
    }

    /**
     * Usuario asociado a este perfil.
     * <p>
     * Relación uno-a-uno con carga eager, eliminación en cascada
     * y exclusión de toString/equals para evitar recursividad.
     * </p>
     */
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    /**
     * Compara dos perfiles por su identificador único.
     * <p>
     * Implementación personalizada para evitar comparaciones
     * complejas basadas en otros campos.
     * </p>
     *
     * @param o Objeto a comparar
     * @return true si ambos perfiles tienen el mismo ID
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil perfil)) return false;
        return getId() != null && getId().equals(perfil.getId());
    }

    /**
     * Genera código hash basado en la clase.
     * <p>
     * Implementación simple para consistencia con equals().
     * </p>
     *
     * @return Código hash de la clase
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}