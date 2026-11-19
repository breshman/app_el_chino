package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IDetalleRepuestoDAO;
import com.elchino.mantenimiento.model.DetalleRepuesto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
 * Implementación del patrón DAO para la tabla {@code detalle_repuesto} (CUS08).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class DetalleRepuestoDAOImpl implements IDetalleRepuestoDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link DetalleRepuesto}.
     */
    private final RowMapper<DetalleRepuesto> detalleRowMapper = (rs, rowNum) -> {
        DetalleRepuesto d = new DetalleRepuesto();
        d.setIdDetalle(rs.getInt("id_detalle"));
        d.setIdReparacion(rs.getInt("id_reparacion"));
        d.setIdRepuesto(rs.getInt("id_repuesto"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        d.setSubtotal(rs.getBigDecimal("subtotal"));
        d.setRepuestoNombre(rs.getString("repuesto_nombre"));
        d.setRepuestoSku(rs.getString("codigo_sku"));
        return d;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public DetalleRepuestoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DetalleRepuesto> listarPorReparacion(Integer idReparacion) {
        String sql = "SELECT dr.*, r.nombre AS repuesto_nombre, r.codigo_sku " +
                     "FROM detalle_repuesto dr " +
                     "JOIN repuesto r ON dr.id_repuesto = r.id_repuesto " +
                     "WHERE dr.id_reparacion = ? " +
                     "ORDER BY dr.id_detalle ASC";
        return jdbcTemplate.query(sql, detalleRowMapper, idReparacion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DetalleRepuesto insertar(DetalleRepuesto detalle) {
        String sql = "INSERT INTO detalle_repuesto (id_reparacion, id_repuesto, cantidad, precio_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_detalle"});
            ps.setInt(1, detalle.getIdReparacion());
            ps.setInt(2, detalle.getIdRepuesto());
            ps.setInt(3, detalle.getCantidad());
            ps.setBigDecimal(4, detalle.getPrecioUnitario());
            ps.setBigDecimal(5, detalle.getSubtotal());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_detalle") != null) {
            detalle.setIdDetalle(((Number) keys.get("id_detalle")).intValue());
        } else if (keyHolder.getKey() != null) {
            detalle.setIdDetalle(keyHolder.getKey().intValue());
        }
        return detalle;
    }
}
