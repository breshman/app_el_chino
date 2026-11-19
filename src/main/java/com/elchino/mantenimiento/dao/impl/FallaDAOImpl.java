package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IFallaDAO;
import com.elchino.mantenimiento.model.Falla;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del patrón DAO para la tabla {@code falla} en PostgreSQL (CUS05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class FallaDAOImpl implements IFallaDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link Falla}.
     */
    private final RowMapper<Falla> fallaRowMapper = (rs, rowNum) -> {
        Falla f = new Falla();
        f.setIdFalla(rs.getInt("id_falla"));
        f.setIdUnidad(rs.getInt("id_unidad"));
        f.setIdUsuarioReporta(rs.getInt("id_usuario_reporta"));
        int idInsp = rs.getInt("id_inspeccion");
        if (!rs.wasNull()) {
            f.setIdInspeccion(idInsp);
        }
        f.setTitulo(rs.getString("titulo"));
        f.setDescripcion(rs.getString("descripcion"));
        f.setSistemaAfectado(rs.getString("sistema_afectado"));
        f.setPrioridad(rs.getString("prioridad"));
        f.setEstado(rs.getString("estado"));
        Timestamp ts = rs.getTimestamp("fecha_reporte");
        if (ts != null) {
            f.setFechaReporte(ts.toLocalDateTime());
        }
        f.setUnidadDescripcion(rs.getString("unidad_desc"));
        f.setUsuarioReportaNombre(rs.getString("reporta_nombre"));
        return f;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public FallaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Falla> listarTodas() {
        String sql = "SELECT f.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS reporta_nombre " +
                     "FROM falla f " +
                     "JOIN unidad u ON f.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON f.id_usuario_reporta = us.id_usuario " +
                     "ORDER BY f.fecha_reporte DESC";
        return jdbcTemplate.query(sql, fallaRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Falla> listarActivas() {
        String sql = "SELECT f.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS reporta_nombre " +
                     "FROM falla f " +
                     "JOIN unidad u ON f.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON f.id_usuario_reporta = us.id_usuario " +
                     "WHERE f.estado IN ('REPORTADA', 'EN_ATENCION') " +
                     "ORDER BY CASE f.prioridad WHEN 'CRITICA' THEN 1 WHEN 'ALTA' THEN 2 WHEN 'MEDIA' THEN 3 ELSE 4 END, f.fecha_reporte DESC";
        return jdbcTemplate.query(sql, fallaRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Falla> buscarPorId(Integer idFalla) {
        String sql = "SELECT f.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS reporta_nombre " +
                     "FROM falla f " +
                     "JOIN unidad u ON f.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON f.id_usuario_reporta = us.id_usuario " +
                     "WHERE f.id_falla = ?";
        List<Falla> lista = jdbcTemplate.query(sql, fallaRowMapper, idFalla);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Falla insertar(Falla falla) {
        String sql = "INSERT INTO falla (id_unidad, id_usuario_reporta, id_inspeccion, titulo, descripcion, sistema_afectado, prioridad, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_falla"});
            ps.setInt(1, falla.getIdUnidad());
            ps.setInt(2, falla.getIdUsuarioReporta());
            if (falla.getIdInspeccion() != null) {
                ps.setInt(3, falla.getIdInspeccion());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, falla.getTitulo());
            ps.setString(5, falla.getDescripcion());
            ps.setString(6, falla.getSistemaAfectado());
            ps.setString(7, falla.getPrioridad());
            ps.setString(8, falla.getEstado() != null ? falla.getEstado() : "REPORTADA");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_falla") != null) {
            falla.setIdFalla(((Number) keys.get("id_falla")).intValue());
        } else if (keyHolder.getKey() != null) {
            falla.setIdFalla(keyHolder.getKey().intValue());
        }
        return falla;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarPrioridadYEstado(Integer idFalla, String prioridad, String estado) {
        String sql = "UPDATE falla SET prioridad = ?, estado = ? WHERE id_falla = ?";
        return jdbcTemplate.update(sql, prioridad, estado, idFalla);
    }
}
