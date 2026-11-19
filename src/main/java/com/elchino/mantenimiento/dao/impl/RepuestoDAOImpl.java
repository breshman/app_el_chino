package com.elchino.mantenimiento.dao.impl;

import com.elchino.mantenimiento.dao.IRepuestoDAO;
import com.elchino.mantenimiento.model.Repuesto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del patrón DAO para la tabla {@code repuesto} en PostgreSQL.
 * Garantiza el descuento atómico de stock para integridad transaccional (CUS08 y QA-04).
 *
 * @author Desarrollador Backend Core (@BackendAgent)
 * @version 1.0.0
 */
@Repository
public class RepuestoDAOImpl implements IRepuestoDAO {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Mapeador de filas SQL a entidad {@link Repuesto}.
     */
    private final RowMapper<Repuesto> repuestoRowMapper = (rs, rowNum) -> {
        Repuesto r = new Repuesto();
        r.setIdRepuesto(rs.getInt("id_repuesto"));
        r.setCodigoSku(rs.getString("codigo_sku"));
        r.setNombre(rs.getString("nombre"));
        r.setCategoria(rs.getString("categoria"));
        r.setStock(rs.getInt("stock"));
        r.setStockMinimo(rs.getInt("stock_minimo"));
        r.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        r.setUnidadMedida(rs.getString("unidad_medida"));
        return r;
    };

    /**
     * Constructor con inyección de {@link JdbcTemplate}.
     *
     * @param jdbcTemplate plantilla JDBC.
     */
    public RepuestoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repuesto> listarTodos() {
        String sql = "SELECT * FROM repuesto ORDER BY categoria ASC, nombre ASC";
        return jdbcTemplate.query(sql, repuestoRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Repuesto> listarBajoStock() {
        String sql = "SELECT * FROM repuesto WHERE stock <= stock_minimo ORDER BY stock ASC";
        return jdbcTemplate.query(sql, repuestoRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Repuesto> buscarPorId(Integer idRepuesto) {
        String sql = "SELECT * FROM repuesto WHERE id_repuesto = ?";
        List<Repuesto> lista = jdbcTemplate.query(sql, repuestoRowMapper, idRepuesto);
        return lista.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Repuesto insertar(Repuesto repuesto) {
        String sql = "INSERT INTO repuesto (codigo_sku, nombre, categoria, stock, stock_minimo, precio_unitario, unidad_medida) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id_repuesto"});
            ps.setString(1, repuesto.getCodigoSku().toUpperCase());
            ps.setString(2, repuesto.getNombre());
            ps.setString(3, repuesto.getCategoria());
            ps.setInt(4, repuesto.getStock());
            ps.setInt(5, repuesto.getStockMinimo());
            ps.setBigDecimal(6, repuesto.getPrecioUnitario());
            ps.setString(7, repuesto.getUnidadMedida());
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null && keys.get("id_repuesto") != null) {
            repuesto.setIdRepuesto(((Number) keys.get("id_repuesto")).intValue());
        } else if (keyHolder.getKey() != null) {
            repuesto.setIdRepuesto(keyHolder.getKey().intValue());
        }
        return repuesto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int actualizar(Repuesto repuesto) {
        String sql = "UPDATE repuesto SET codigo_sku = ?, nombre = ?, categoria = ?, stock = ?, " +
                     "stock_minimo = ?, precio_unitario = ?, unidad_medida = ? WHERE id_repuesto = ?";
        return jdbcTemplate.update(sql,
                repuesto.getCodigoSku().toUpperCase(),
                repuesto.getNombre(),
                repuesto.getCategoria(),
                repuesto.getStock(),
                repuesto.getStockMinimo(),
                repuesto.getPrecioUnitario(),
                repuesto.getUnidadMedida(),
                repuesto.getIdRepuesto());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int descontarStock(Integer idRepuesto, Integer cantidad) {
        // Operación SQL atómica condicional: sólo descuenta si stock >= cantidad
        String sql = "UPDATE repuesto SET stock = stock - ? WHERE id_repuesto = ? AND stock >= ?";
        return jdbcTemplate.update(sql, cantidad, idRepuesto, cantidad);
    }
}
