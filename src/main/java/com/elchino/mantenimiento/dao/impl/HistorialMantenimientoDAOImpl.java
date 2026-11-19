package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IHistorialMantenimientoDAO;
import com.elchino.mantenimiento.model.HistorialMantenimiento;
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

/**
 * Implementación del patrón DAO para la bitácora de auditoría {@code historial_mantenimiento}
 * en PostgreSQL (Implementa CUS10 y QA-05).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class HistorialMantenimientoDAOImpl implements IHistorialMantenimientoDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link HistorialMantenimiento}.
     */
    private final RowMapper<HistorialMantenimiento> historialRowMapper = (rs, rowNum) -> {
        HistorialMantenimiento h = new HistorialMantenimiento();
        h.setIdHistorial(rs.getInt("id_historial"));
        h.setIdUnidad(rs.getInt("id_unidad"));
        int idMant = rs.getInt("id_mantenimiento");
        if (!rs.wasNull()) {
            h.setIdMantenimiento(idMant);
        }
        h.setIdUsuario(rs.getInt("id_usuario"));
        h.setTipoEvento(rs.getString("tipo_evento"));
        h.setEstadoAnteriorUnidad(rs.getString("estado_anterior_unidad"));
        h.setEstadoNuevoUnidad(rs.getString("estado_nuevo_unidad"));
        h.setDescripcionEvento(rs.getString("descripcion_evento"));
        Timestamp ts = rs.getTimestamp("fecha_evento");
        if (ts != null) {
            h.setFechaEvento(ts.toLocalDateTime());
        }
        h.setUnidadDescripcion(rs.getString("unidad_desc"));
        h.setUsuarioNombre(rs.getString("usuario_nombre"));
        return h;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public HistorialMantenimientoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HistorialMantenimiento> listarTodo() {
        String sql = "SELECT h.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS usuario_nombre " +
                     "FROM historial_mantenimiento h " +
                     "JOIN unidad u ON h.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON h.id_usuario = us.id_usuario " +
                     "ORDER BY h.fecha_evento DESC";
        return jdbcTemplate.query(sql, historialRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<HistorialMantenimiento> listarPorUnidad(Integer idUnidad) {
        String sql = "SELECT h.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS usuario_nombre " +
                     "FROM historial_mantenimiento h " +
                     "JOIN unidad u ON h.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON h.id_usuario = us.id_usuario " +
                     "WHERE h.id_unidad = ? " +
                     "ORDER BY h.fecha_evento DESC";
        return jdbcTemplate.query(sql, historialRowMapper, idUnidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HistorialMantenimiento insertar(HistorialMantenimiento historial) {
        String sql = "INSERT INTO historial_mantenimiento (id_unidad, id_mantenimiento, id_usuario, tipo_evento, " +
                     "estado_anterior_unidad, estado_nuevo_unidad, descripcion_evento) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_historial"});
            ps.setInt(1, historial.getIdUnidad());
            if (historial.getIdMantenimiento() != null) {
                ps.setInt(2, historial.getIdMantenimiento());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, historial.getIdUsuario());
            ps.setString(4, historial.getTipoEvento());
            ps.setString(5, historial.getEstadoAnteriorUnidad());
            ps.setString(6, historial.getEstadoNuevoUnidad());
            ps.setString(7, historial.getDescripcionEvento());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_historial") != null) {
            historial.setIdHistorial(((Number) keys.get("id_historial")).intValue());
        } else if (keyHolder.getKey() != null) {
            historial.setIdHistorial(keyHolder.getKey().intValue());
        }
        return historial;
    }
}
