package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IMantenimientoDAO;
import com.elchino.mantenimiento.model.Mantenimiento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del patrón DAO para la tabla {@code mantenimiento}.
 * Incluye la consulta nativa de verificación de cruce de horarios de técnicos (CUS06 y CUS11).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class MantenimientoDAOImpl implements IMantenimientoDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link Mantenimiento}.
     */
    private final RowMapper<Mantenimiento> mantenimientoRowMapper = (rs, rowNum) -> {
        Mantenimiento m = new Mantenimiento();
        m.setIdMantenimiento(rs.getInt("id_mantenimiento"));
        m.setIdUnidad(rs.getInt("id_unidad"));
        int idFalla = rs.getInt("id_falla");
        if (!rs.wasNull()) {
            m.setIdFalla(idFalla);
        }
        m.setIdTecnico(rs.getInt("id_tecnico"));
        m.setIdResponsable(rs.getInt("id_responsable"));
        m.setTipoMantenimiento(rs.getString("tipo_mantenimiento"));
        Timestamp tsProg = rs.getTimestamp("fecha_programada");
        if (tsProg != null) {
            m.setFechaProgramada(tsProg.toLocalDateTime());
        }
        Timestamp tsIni = rs.getTimestamp("fecha_inicio");
        if (tsIni != null) {
            m.setFechaInicio(tsIni.toLocalDateTime());
        }
        Timestamp tsFin = rs.getTimestamp("fecha_fin");
        if (tsFin != null) {
            m.setFechaFin(tsFin.toLocalDateTime());
        }
        m.setDescripcionTrabajo(rs.getString("descripcion_trabajo"));
        m.setEstado(rs.getString("estado"));
        m.setUnidadDescripcion(rs.getString("unidad_desc"));
        m.setTecnicoNombre(rs.getString("tecnico_nombre"));
        m.setFallaTitulo(rs.getString("falla_titulo"));
        return m;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public MantenimientoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Mantenimiento> listarTodos() {
        String sql = "SELECT m.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre, " +
                     "f.titulo AS falla_titulo " +
                     "FROM mantenimiento m " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON m.id_tecnico = ut.id_usuario " +
                     "LEFT JOIN falla f ON m.id_falla = f.id_falla " +
                     "ORDER BY m.fecha_programada DESC";
        return jdbcTemplate.query(sql, mantenimientoRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Mantenimiento> listarPendientesOEnProceso() {
        String sql = "SELECT m.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre, " +
                     "f.titulo AS falla_titulo " +
                     "FROM mantenimiento m " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON m.id_tecnico = ut.id_usuario " +
                     "LEFT JOIN falla f ON m.id_falla = f.id_falla " +
                     "WHERE m.estado IN ('PROGRAMADO', 'EN_PROCESO') " +
                     "ORDER BY m.fecha_programada ASC";
        return jdbcTemplate.query(sql, mantenimientoRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Mantenimiento> buscarPorId(Integer idMantenimiento) {
        String sql = "SELECT m.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre, " +
                     "f.titulo AS falla_titulo " +
                     "FROM mantenimiento m " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON m.id_tecnico = ut.id_usuario " +
                     "LEFT JOIN falla f ON m.id_falla = f.id_falla " +
                     "WHERE m.id_mantenimiento = ?";
        List<Mantenimiento> lista = jdbcTemplate.query(sql, mantenimientoRowMapper, idMantenimiento);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeCruceHorarioTecnico(Integer idTecnico, LocalDateTime fechaProgramada) {
        // Verifica utilizando el índice idx_mantenimiento_tecnico_fecha si el técnico tiene otro
        // mantenimiento PROGRAMADO o EN_PROCESO en un rango de +/- 2 horas de la fecha/hora indicada
        String sql = "SELECT COUNT(*) FROM mantenimiento " +
                     "WHERE id_tecnico = ? " +
                     "AND estado IN ('PROGRAMADO', 'EN_PROCESO') " +
                     "AND fecha_programada BETWEEN ? AND ?";
        Timestamp inicioVentana = Timestamp.valueOf(fechaProgramada.minusHours(2));
        Timestamp finVentana = Timestamp.valueOf(fechaProgramada.plusHours(2));
        Integer conteo = jdbcTemplate.queryForObject(sql, Integer.class, idTecnico, inicioVentana, finVentana);
        return conteo != null && conteo > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mantenimiento insertar(Mantenimiento mantenimiento) {
        String sql = "INSERT INTO mantenimiento (id_unidad, id_falla, id_tecnico, id_responsable, tipo_mantenimiento, fecha_programada, descripcion_trabajo, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_mantenimiento"});
            ps.setInt(1, mantenimiento.getIdUnidad());
            if (mantenimiento.getIdFalla() != null) {
                ps.setInt(2, mantenimiento.getIdFalla());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, mantenimiento.getIdTecnico());
            ps.setInt(4, mantenimiento.getIdResponsable());
            ps.setString(5, mantenimiento.getTipoMantenimiento());
            ps.setTimestamp(6, Timestamp.valueOf(mantenimiento.getFechaProgramada()));
            ps.setString(7, mantenimiento.getDescripcionTrabajo());
            ps.setString(8, mantenimiento.getEstado() != null ? mantenimiento.getEstado() : "PROGRAMADO");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_mantenimiento") != null) {
            mantenimiento.setIdMantenimiento(((Number) keys.get("id_mantenimiento")).intValue());
        } else if (keyHolder.getKey() != null) {
            mantenimiento.setIdMantenimiento(keyHolder.getKey().intValue());
        }
        return mantenimiento;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarEstadoYFechas(Integer idMantenimiento, String nuevoEstado, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        String sql = "UPDATE mantenimiento SET estado = ?, " +
                     "fecha_inicio = COALESCE(?, fecha_inicio), " +
                     "fecha_fin = COALESCE(?, fecha_fin) " +
                     "WHERE id_mantenimiento = ?";
        Timestamp tsInicio = fechaInicio != null ? Timestamp.valueOf(fechaInicio) : null;
        Timestamp tsFin = fechaFin != null ? Timestamp.valueOf(fechaFin) : null;
        return jdbcTemplate.update(sql, nuevoEstado, tsInicio, tsFin, idMantenimiento);
    }
}
