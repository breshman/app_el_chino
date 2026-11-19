package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IUsuarioDAO;
import com.elchino.mantenimiento.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación concreta del patrón DAO para la tabla {@code usuario}
 * utilizando {@link JdbcTemplate} y consultas SQL nativas parametrizadas (CUS01 / RNF01).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class UsuarioDAOImpl implements IUsuarioDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a objetos de dominio {@link Usuario}.
     */
    private final RowMapper<Usuario> usuarioRowMapper = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombres(rs.getString("nombres"));
        u.setApellidos(rs.getString("apellidos"));
        u.setEmail(rs.getString("email"));
        u.setRol(rs.getString("rol"));
        u.setEstado(rs.getString("estado"));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        Timestamp tsBloqueo = rs.getTimestamp("bloqueado_hasta");
        if (tsBloqueo != null) {
            u.setBloqueadoHasta(tsBloqueo.toLocalDateTime());
        }
        Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
        if (tsCreacion != null) {
            u.setFechaCreacion(tsCreacion.toLocalDateTime());
        }
        return u;
    };

    /**
     * Constructor con inyección de dependencias de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC configurada con PostgreSQL.
     */
    public UsuarioDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT * FROM usuario WHERE username = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, usuarioRowMapper, username);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Usuario> buscarPorId(Integer idUsuario) {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";
        List<Usuario> lista = jdbcTemplate.query(sql, usuarioRowMapper, idUsuario);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuario ORDER BY apellidos ASC, nombres ASC";
        return jdbcTemplate.query(sql, usuarioRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Usuario> listarActivosPorRol(String rol) {
        String sql = "SELECT * FROM usuario WHERE rol = ? AND estado = 'ACTIVO' ORDER BY apellidos ASC";
        return jdbcTemplate.query(sql, usuarioRowMapper, rol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Usuario insertar(Usuario usuario) {
        // Inserción parametrizada recuperando la clave primaria generada en PostgreSQL
        String sql = "INSERT INTO usuario (username, password_hash, nombres, apellidos, email, rol, estado, intentos_fallidos) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_usuario"});
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombres());
            ps.setString(4, usuario.getApellidos());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getRol());
            ps.setString(7, usuario.getEstado() != null ? usuario.getEstado() : "ACTIVO");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_usuario") != null) {
            usuario.setIdUsuario(((Number) keys.get("id_usuario")).intValue());
        } else if (keyHolder.getKey() != null) {
            usuario.setIdUsuario(keyHolder.getKey().intValue());
        }
        return usuario;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarEstado(Integer idUsuario, String estado) {
        String sql = "UPDATE usuario SET estado = ? WHERE id_usuario = ?";
        return jdbcTemplate.update(sql, estado, idUsuario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarIntentosYBloqueo(String username, int intentosFallidos, LocalDateTime bloqueadoHasta) {
        String sql = "UPDATE usuario SET intentos_fallidos = ?, bloqueado_hasta = ? WHERE username = ?";
        Timestamp tsBloqueo = bloqueadoHasta != null ? Timestamp.valueOf(bloqueadoHasta) : null;
        return jdbcTemplate.update(sql, intentosFallidos, tsBloqueo, username);
    }
}
