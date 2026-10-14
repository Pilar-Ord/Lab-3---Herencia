import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class Registro {
    private double ingresosPorRegistro;
    private double ingresosPorReintentos;
    private final Map<String, Double> porCategoria = new LinkedHashMap<>();

    void cobrarRegistro(Paquete envio) {
        double importe = envio.calcularCosto();
        ingresosPorRegistro += importe;
        porCategoria.merge(envio.getCategoria(), importe, Double::sum);
    }

    void cobrarReintento(Paquete envio) {
        ingresosPorReintentos += 15;
        porCategoria.merge(envio.getCategoria(), 15.0, Double::sum);
    }

    double getIngresosPorRegistro() { return ingresosPorRegistro; }
    double getIngresosPorReintentos() { return ingresosPorReintentos; }
    double getIngresosTotales() { return ingresosPorRegistro + ingresosPorReintentos; }
    Map<String, Double> getPorCategoria() { return Collections.unmodifiableMap(porCategoria); }
}
