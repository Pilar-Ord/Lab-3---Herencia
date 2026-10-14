import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Despacho {
    private final Vehiculo vehiculo;
    private final List<Paquete> envios = new ArrayList<>();
    private EstadoDespacho estado = EstadoDespacho.PREPARACION;

    Despacho(Vehiculo vehiculo) { this.vehiculo = java.util.Objects.requireNonNull(vehiculo); }

    void asignar(Paquete envio) {
        exigirPreparacion();
        if (envio.getEstado() != EstadoEnvio.BODEGA || envio.getDespacho() != null) {
            throw new IllegalStateException("El envio no esta disponible en bodega.");
        }
        if (!envio.compatibleCon(vehiculo)) throw new IllegalArgumentException("El vehiculo no es compatible con el envio.");
        if (envio.getPesoReal() > capacidadDisponible()) throw new IllegalArgumentException("Se excede la capacidad del vehiculo.");
        envio.asignarA(this);
        envios.add(envio);
    }

    void retirar(Paquete envio) {
        exigirPreparacion();
        if (!envios.contains(envio)) throw new IllegalArgumentException("El envio no pertenece al despacho.");
        envio.retirarDe(this);
        envios.remove(envio);
    }

    double pesoCargado() { return envios.stream().mapToDouble(Paquete::getPesoReal).sum(); }
    double capacidadDisponible() { return vehiculo.getCapacidad() - pesoCargado(); }

    void salir(Registro registro) {
        exigirPreparacion();
        if (envios.isEmpty()) throw new IllegalStateException("No puede salir un despacho vacio.");
        for (Paquete envio : envios) {
            envio.salirCon(this);
            if (envio.getIntentos() > 0) registro.cobrarReintento(envio);
        }
        estado = EstadoDespacho.EN_RUTA;
    }

    void registrarResultado(Paquete envio, boolean entregado) {
        if (estado != EstadoDespacho.EN_RUTA) throw new IllegalStateException("El despacho no esta en ruta.");
        if (!envios.contains(envio) || envio.getDespacho() != this || envio.getEstado() != EstadoEnvio.EN_RUTA) {
            throw new IllegalArgumentException("El envio no tiene un resultado pendiente en este despacho.");
        }
        envio.registrarResultado(entregado);
        if (envios.stream().noneMatch(e -> e.getDespacho() == this)) estado = EstadoDespacho.CERRADO;
    }

    private void exigirPreparacion() {
        if (estado != EstadoDespacho.PREPARACION) throw new IllegalStateException("El despacho no esta en preparacion.");
    }

    Vehiculo getVehiculo() { return vehiculo; }
    EstadoDespacho getEstado() { return estado; }
    List<Paquete> getEnvios() { return Collections.unmodifiableList(envios); }
}
