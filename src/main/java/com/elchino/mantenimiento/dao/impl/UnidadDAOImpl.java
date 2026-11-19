package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IUnidadDAO;
import com.elchino.mantenimiento.model.Unidad;
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
 * Implementación del patrón DAO para la entidad {@link Unidad} sobre PostgreSQL (CUS02 y CUS03).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class UnidadDAOImpl implements IUnidadDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link Unidad}.
     */
    private final RowMapper<Unidad> unidadRowMapper = (rs, rowNum) -> {
        Unidad u = new Unidad();
        u.setIdUnidad(rs.getInt("id_unidad"));
        u.setCodigoInterno(rs.getString("codigo_interno"));
        u.setPlaca(rs.getString("placa"));
        u.setMarca(rs.getString("marca"));
        u.setModelo(rs.getString("modelo"));
        u.setAnioFabricacion(rs.getInt("anio_fabricacion"));
        u.setKilometrajeActual(rs.getInt("kilometraje_actual"));
        u.setEstado(rs.getString("estado"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            u.setFechaRegistro(ts.toLocalDateTime());
        }
        return u;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public UnidadDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Unidad> listarTodas() {
        String sql = "SELECT * FROM unidad ORDER BY codigo_interno ASC";
        return jdbcTemplate.query(sql, unidadRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Unidad> listarPorEstado(String estado) {
        String sql = "SELECT * FROM unidad WHERE estado = ? ORDER BY codigo_interno ASC";
        return jdbcTemplate.query(sql, unidadRowMapper, estado);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Unidad> buscarPorId(Integer idUnidad) {
        String sql = "SELECT * FROM unidad WHERE id_unidad = ?";
        List<Unidad> lista = jdbcTemplate.query(sql, unidadRowMapper, idUnidad);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Unidad> buscarPorCodigoOPlaca(String codigoInterno, String placa) {
        String sql = "SELECT * FROM unidad WHERE UPPER(codigo_interno) = UPPER(?) OR UPPER(placa) = UPPER(?)";
        List<Unidad> lista = jdbcTemplate.query(sql, unidadRowMapper, codigoInterno, placa);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Unidad insertar(Unidad unidad) {
        String sql = "INSERT INTO unidad (codigo_interno, placa, marca, modelo, anio_fabricacion, kilometraje_actual, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_unidad"});
            ps.setString(1, unidad.getCodigoInterno().toUpperCase());
            ps.setString(2, unidad.getPlaca().toUpperCase());
            ps.setString(3, unidad.getMarca());
            ps.setString(4, unidad.getModelo());
            ps.setInt(5, unidad.getAnioFabricacion());
            ps.setInt(6, unidad.getKilometrajeActual());
            ps.setString(7, unidad.getEstado() != null ? unidad.getEstado() : "OPERATIVA");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_unidad") != null) {
            unidad.setIdUnidad(((Number) keys.get("id_unidad")).intValue());
        } else if (keyHolder.getKey() != null) {
            unidad.setIdUnidad(keyHolder.getKey().intValue());
        }
        return unidad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizar(Unidad unidad) {
        String sql = "UPDATE unidad SET codigo_interno = ?, placa = ?, marca = ?, modelo = ?, " +
                     "anio_fabricacion = ?, kilometraje_actual = ?, estado = ? WHERE id_unidad = ?";
        return jdbcTemplate.update(sql,
                unidad.getCodigoInterno().toUpperCase(),
                unidad.getPlaca().toUpperCase(),
                unidad.getMarca(),
                unidad.getModelo(),
                unidad.getAnioFabricacion(),
                unidad.getKilometrajeActual(),
                unidad.getEstado(),
                unidad.getIdUnidad());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarEstado(Integer idUnidad, String nuevoEstado) {
        String sql = "UPDATE unidad SET estado = ? WHERE id_unidad = ?";
        return jdbcTemplate.update(sql, nuevoEstado, idUnidad);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizarKilometraje(Integer idUnidad, Integer nuevoKilometraje) {
        String sql = "UPDATE unidad SET kilometraje_actual = ? WHERE id_unidad = ? AND kilometraje_actual < ?";
        return jdbcTemplate.update(sql, nuevoKilometraje, idUnidad, nuevoKilometraje);
    }
}
