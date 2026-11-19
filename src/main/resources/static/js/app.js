/**
 * Utilidades JavaScript Vanilla para selector dinamico de repuestos en FrmReparacion
 * y consumo de endpoints REST documentados con Swagger.
 */
function agregarFilaRepuesto() {
    const contenedor = document.getElementById('contenedorRepuestos');
    const plantilla = document.getElementById('plantillaFilaRepuesto');
    if (!contenedor || !plantilla) return;
    const clon = plantilla.content.cloneNode(true);
    contenedor.appendChild(clon);
}

function eliminarFilaRepuesto(boton) {
    const fila = boton.closest('.fila-repuesto');
    if (fila) {
        fila.remove();
    }
}
