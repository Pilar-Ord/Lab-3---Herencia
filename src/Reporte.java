import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

final class Reporte {
    String generarResumen(Collection<Paquete> envios, Collection<Despacho> despachos, Registro registro) {
        Map<String, Integer> categorias = new LinkedHashMap<>();
        Map<EstadoEnvio, Integer> estados = new LinkedHashMap<>();
        for (Paquete envio : envios) {
            categorias.merge(envio.getCategoria(), 1, Integer::sum);
            estados.merge(envio.getEstado(), 1, Integer::sum);
        }
        StringBuilder resultado = new StringBuilder("\n=== REPORTE ===\nEnvios por categoria: \n");
        categorias.forEach((nombre, cantidad) -> resultado.append("  ").append(nombre).append(": ").append(cantidad).append('\n'));
        resultado.append("Envios por estado:\n");
        estados.forEach((estado, cantidad) -> resultado.append("  ").append(estado).append(": ").append(cantidad).append('\n'));
        resultado.append(String.format(java.util.Locale.US,
                "Ingresos por registro: Q%.2f\nIngresos por reintentos: Q%.2f\nTotal: Q%.2f\n",
                registro.getIngresosPorRegistro(), registro.getIngresosPorReintentos(), registro.getIngresosTotales()));
        resultado.append("Ingresos por categoria:\n");
        registro.getPorCategoria().forEach((nombre, monto) ->
                resultado.append(String.format(java.util.Locale.US, "  %s: Q%.2f%n", nombre, monto)));
        resultado.append("Despachos activos:\n");
        for (Despacho despacho : despachos) {
            if (despacho.getEstado() != EstadoDespacho.CERRADO) {
                resultado.append(String.format(java.util.Locale.US, "  %s | %s | %.2f kg cargados | %.2f kg disponibles%n",
                        despacho.getVehiculo().getPlaca(), despacho.getEstado(),
                        despacho.pesoCargado(), despacho.capacidadDisponible()));
            }
        }
        return resultado.toString();
    }
}