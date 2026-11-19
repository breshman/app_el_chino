package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IInspeccionDAO;
import com.elchino.mantenimiento.model.Inspeccion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del patrón DAO para la tabla {@code inspeccion} (CUS04).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class InspeccionDAOImpl implements IInspeccionDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL con JOIN hacia unidad y usuario inspector.
     */
    private final RowMapper<Inspeccion> inspeccionRowMapper = (rs, rowNum) -> {
        Inspeccion i = new Inspeccion();
        i.setIdInspeccion(rs.getInt("id_inspeccion"));
        i.setIdUnidad(rs.getInt("id_unidad"));
        i.setIdInspector(rs.getInt("id_inspector"));
        i.setTipoInspeccion(rs.getString("tipo_inspeccion"));
        Timestamp ts = rs.getTimestamp("fecha_inspeccion");
        if (ts != null) {
            i.setFechaInspeccion(ts.toLocalDateTime());
        }
        i.setKilometrajeRegistrado(rs.getInt("kilometraje_registrado"));
        i.setObservaciones(rs.getString("observaciones"));
        i.setResultado(rs.getString("resultado"));
        i.setUnidadDescripcion(rs.getString("unidad_desc"));
        i.setInspectorNombre(rs.getString("inspector_nombre"));
        return i;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public InspeccionDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inspeccion> listarTodas() {
        String sql = "SELECT i.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS inspector_nombre " +
                     "FROM inspeccion i " +
                     "JOIN unidad u ON i.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON i.id_inspector = us.id_usuario " +
                     "ORDER BY i.fecha_inspeccion DESC";
        return jdbcTemplate.query(sql, inspeccionRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inspeccion> listarPorUnidad(Integer idUnidad) {
        String sql = "SELECT i.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS inspector_nombre " +
                     "FROM inspeccion i " +
                     "JOIN unidad u ON i.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON i.id_inspector = us.id_usuario " +
                     "WHERE i.id_unidad = ? " +
                     "ORDER BY i.fecha_inspeccion DESC";
        return jdbcTemplate.query(sql, inspeccionRowMapper, idUnidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Inspeccion> buscarPorId(Integer idInspeccion) {
        String sql = "SELECT i.*, (u.codigo_interno || ' (' || u.placa || ')') AS unidad_desc, " +
                     "(us.nombres || ' ' || us.apellidos) AS inspector_nombre " +
                     "FROM inspeccion i " +
                     "JOIN unidad u ON i.id_unidad = u.id_unidad " +
                     "JOIN usuario us ON i.id_inspector = us.id_usuario " +
                     "WHERE i.id_inspeccion = ?";
        List<Inspeccion> lista = jdbcTemplate.query(sql, inspeccionRowMapper, idInspeccion);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inspeccion insertar(Inspeccion inspeccion) {
        String sql = "INSERT INTO inspeccion (id_unidad, id_inspector, tipo_inspeccion, kilometraje_registrado, observaciones, resultado) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_inspeccion"});
            ps.setInt(1, inspeccion.getIdUnidad());
            ps.setInt(2, inspeccion.getIdInspector());
            ps.setString(3, inspeccion.getTipoInspeccion());
            ps.setInt(4, inspeccion.getKilometrajeRegistrado());
            ps.setString(5, inspeccion.getObservaciones());
            ps.setString(6, inspeccion.getResultado());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_inspeccion") != null) {
            inspeccion.setIdInspeccion(((Number) keys.get("id_inspeccion")).intValue());
        } else if (keyHolder.getKey() != null) {
            inspeccion.setIdInspeccion(keyHolder.getKey().intValue());
        }
        return inspeccion;
    }
}
