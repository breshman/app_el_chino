package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IReparacionDAO;
import com.elchino.mantenimiento.model.Reparacion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del patrón DAO para la tabla {@code reparacion} en PostgreSQL (CUS07, CUS09 y CUS12).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class ReparacionDAOImpl implements IReparacionDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link Reparacion}.
     */
    private final RowMapper<Reparacion> reparacionRowMapper = (rs, rowNum) -> {
        Reparacion r = new Reparacion();
        r.setIdReparacion(rs.getInt("id_reparacion"));
        r.setIdMantenimiento(rs.getInt("id_mantenimiento"));
        r.setIdTecnico(rs.getInt("id_tecnico"));
        r.setDiagnosticoTecnico(rs.getString("diagnostico_tecnico"));
        r.setAccionesRealizadas(rs.getString("acciones_realizadas"));
        r.setHorasHombre(rs.getBigDecimal("horas_hombre"));
        r.setCostoManoObra(rs.getBigDecimal("costo_mano_obra"));
        r.setResultado(rs.getString("resultado"));
        r.setObservacionesVerificacion(rs.getString("observaciones_verificacion"));
        Timestamp ts = rs.getTimestamp("fecha_reparacion");
        if (ts != null) {
            r.setFechaReparacion(ts.toLocalDateTime());
        }
        r.setUnidadDescripcion(rs.getString("unidad_desc"));
        r.setTecnicoNombre(rs.getString("tecnico_nombre"));
        return r;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public ReparacionDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Reparacion> listarTodas() {
        String sql = "SELECT r.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre " +
                     "FROM reparacion r " +
                     "JOIN mantenimiento m ON r.id_mantenimiento = m.id_mantenimiento " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON r.id_tecnico = ut.id_usuario " +
                     "ORDER BY r.fecha_reparacion DESC";
        return jdbcTemplate.query(sql, reparacionRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Reparacion> buscarPorId(Integer idReparacion) {
        String sql = "SELECT r.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre " +
                     "FROM reparacion r " +
                     "JOIN mantenimiento m ON r.id_mantenimiento = m.id_mantenimiento " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON r.id_tecnico = ut.id_usuario " +
                     "WHERE r.id_reparacion = ?";
        List<Reparacion> lista = jdbcTemplate.query(sql, reparacionRowMapper, idReparacion);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Reparacion> buscarPorIdMantenimiento(Integer idMantenimiento) {
        String sql = "SELECT r.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(ut.nombres || ' ' || ut.apellidos) AS tecnico_nombre " +
                     "FROM reparacion r " +
                     "JOIN mantenimiento m ON r.id_mantenimiento = m.id_mantenimiento " +
                     "JOIN unidad u ON m.id_unidad = u.id_unidad " +
                     "JOIN usuario ut ON r.id_tecnico = ut.id_usuario " +
                     "WHERE r.id_mantenimiento = ?";
        List<Reparacion> lista = jdbcTemplate.query(sql, reparacionRowMapper, idMantenimiento);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reparacion insertar(Reparacion reparacion) {
        String sql = "INSERT INTO reparacion (id_mantenimiento, id_tecnico, diagnostico_tecnico, acciones_realizadas, " +
                     "horas_hombre, costo_mano_obra, resultado, observaciones_verificacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_reparacion"});
            ps.setInt(1, reparacion.getIdMantenimiento());
            ps.setInt(2, reparacion.getIdTecnico());
            ps.setString(3, reparacion.getDiagnosticoTecnico());
            ps.setString(4, reparacion.getAccionesRealizadas());
            ps.setBigDecimal(5, reparacion.getHorasHombre());
            ps.setBigDecimal(6, reparacion.getCostoManoObra());
            ps.setString(7, reparacion.getResultado());
            ps.setString(8, reparacion.getObservacionesVerificacion());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_reparacion") != null) {
            reparacion.setIdReparacion(((Number) keys.get("id_reparacion")).intValue());
        } else if (keyHolder.getKey() != null) {
            reparacion.setIdReparacion(keyHolder.getKey().intValue());
        }
        return reparacion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarVerificacion(Integer idReparacion, String resultado, String observacionesVerificacion) {
        String sql = "UPDATE reparacion SET resultado = ?, observaciones_verificacion = ? WHERE id_reparacion = ?";
        return jdbcTemplate.update(sql, resultado, observacionesVerificacion, idReparacion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calcularCostoTotalMantenimiento() {
        // Suma el costo de mano de obra de reparaciones más el subtotal de repuestos consumidos
        String sql = "SELECT COALESCE((SELECT SUM(costo_mano_obra) FROM reparacion), 0) + " +
                     "COALESCE((SELECT SUM(subtotal) FROM detalle_repuesto), 0)";
        BigDecimal total = jdbcTemplate.queryForObject(sql, BigDecimal.class);
        return total != null ? total : BigDecimal.ZERO;
    }
}
