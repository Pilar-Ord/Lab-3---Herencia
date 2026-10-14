import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class Controlador {
    private final Vista vista = new Vista();
    private final Map<String, Paquete> envios = new LinkedHashMap<>();
    private final Map<String, Vehiculo> vehiculos = new LinkedHashMap<>();
    private final List<Despacho> despachos = new ArrayList<>();
    private final List<Entrega> entregas = new ArrayList<>();
    private final Registro registro = new Registro();
    private final Reporte reporte = new Reporte();

    Controlador() { cargarEjemplos(); }

    void iniciar() {
        System.out.println("Datos iniciales cargados sin ingresos registrados.");
        try {
            while (true) {
                vista.mostrarMenu();
                int opcion = vista.leerEntero("Opcion");
                if (opcion == 0) return;
                try { procesar(opcion); }
                catch (IllegalArgumentException | IllegalStateException ex) {
                    System.out.println("Operacion rechazada: " + ex.getMessage());
                }
            }
        } catch (Vista.FinDeEntrada ex) {
            System.out.println("Entrada terminada. Hasta luego.");
        }
    }

    private void procesar(int opcion) {
        switch (opcion) {
            case 1: case 2: {
                Paquete envio = leerEnvio();
                System.out.printf(Locale.US, "Cotizacion: Q%.2f, %d dia(s) habiles.%n",
                        envio.calcularCosto(), envio.estimarDias());
                if (opcion == 2 && vista.leerSiNo("Aceptar y registrar")) {
                    registrarEnvio(envio);
                    System.out.println("Paquete registrado y cobrado.");
                }
                break;
            }
            case 3: {
                String placa = vista.leerTexto("Placa");
                double capacidad = vista.leerDouble("Capacidad en kg");
                boolean refrigerado = vista.leerSiNo("Tiene refrigeracion");
                registrarVehiculo(placa, capacidad, refrigerado);
                System.out.println("Vehiculo registrado.");
                break;
            }
            case 4: {
                Despacho despacho = crearDespacho(buscarVehiculo(vista.leerTexto("Placa")));
                System.out.println("Despacho en preparacion: " + despacho.getVehiculo().getPlaca());
                break;
            }
            case 5: {
                Despacho despacho = buscarDespachoActivo(vista.leerTexto("Placa del despacho"));
                Paquete envio = buscarEnvio(vista.leerTexto("Codigo del envio"));
                asignarEnvio(despacho, envio);
                System.out.println("Paquete asignado.");
                break;
            }
            case 6: {
                Despacho despacho = buscarDespachoActivo(vista.leerTexto("Placa del despacho"));
                despacho.retirar(buscarEnvio(vista.leerTexto("Codigo del envio")));
                System.out.println("Paquete retirado; permanece en bodega.");
                break;
            }
            case 7: {
                Despacho despacho = buscarDespachoActivo(vista.leerTexto("Placa del despacho"));
                despacho.salir(registro);
                System.out.println("Despacho en ruta.");
                break;
            }
            case 8: {
                Despacho despacho = buscarDespachoActivo(vista.leerTexto("Placa del despacho"));
                Paquete envio = buscarEnvio(vista.leerTexto("Codigo del envio"));
                boolean entregado = vista.leerSiNo("Fue entregado");
                String firma = entregado ? vista.leerTexto("Firma del receptor") : "";
                Entrega entrega = new Entrega(envio, entregado, firma);
                despacho.registrarResultado(envio, entregado);
                entregas.add(entrega);
                System.out.println("Resultado registrado. Estado del envio: " + envio.getEstado()
                        + ". Estado del despacho: " + despacho.getEstado());
                break;
            }
            case 9: mostrarEnvios(); break;
            case 10: vehiculos.values().forEach(System.out::println); break;
            case 11: System.out.print(reporte.generarResumen(envios.values(), despachos, registro)); break;
            case 12: mostrarDespachos(); break;
            default: System.out.println("Opcion desconocida.");
        }
    }

    double cotizarEnvio(Paquete envio) { return envio.calcularCosto(); }

    void registrarEnvio(Paquete envio) {
        String clave = clave(envio.getCodigo());
        if (envios.containsKey(clave))
            throw new IllegalArgumentException("Codigo de envio repetido.");
        if (envio.getEstado() != EstadoEnvio.BODEGA
                || envio.getDespacho() != null || envio.getIntentos() != 0) {
            throw new IllegalArgumentException("Solo se pueden registrar envios nuevos.");
        }
        envios.put(clave, envio);
        registro.cobrarRegistro(envio);
    }

    void registrarVehiculo(String placa, double capacidad, boolean refrigerado) {
        Vehiculo vehiculo = new Vehiculo(placa, capacidad, refrigerado);
        String clave = clave(placa);
        if (vehiculos.containsKey(clave))
            throw new IllegalArgumentException("Placa repetida.");
        vehiculos.put(clave, vehiculo);
    }

    Despacho crearDespacho(Vehiculo vehiculo) {
        if (despachos.stream().anyMatch(d ->
                d.getVehiculo() == vehiculo && d.getEstado() != EstadoDespacho.CERRADO)) {
            throw new IllegalStateException("El vehiculo ya tiene un despacho activo.");
        }
        Despacho despacho = new Despacho(vehiculo);
        despachos.add(despacho);
        return despacho;
    }

    void asignarEnvio(Despacho despacho, Paquete envio) {
        despacho.asignar(envio);
    }

    private Paquete leerEnvio() {
        int categoria = vista.leerEntero(
                "Categoria (1 Sobre, 2 Paquete normal, 3 Paquete fragil, 4 Carga refrigerada)");
        if (categoria < 1 || categoria > 4)
            throw new IllegalArgumentException("Categoria desconocida.");

        String codigo = vista.leerTexto("Codigo");
        Cliente remitente = new Cliente(
                vista.leerTexto("Nombre del remitente"),
                vista.leerTexto("Direccion del remitente"),
                vista.leerTexto("Celular del remitente"));
        String destinatario = vista.leerTexto("Nombre del destinatario");
        String direccion = vista.leerTexto("Direccion del destinatario");
        String celular = vista.leerTexto("Celular del destinatario");
        double peso = vista.leerDouble("Peso real en kg");
        int tipoDestino = vista.leerEntero("Destino (1 Urbano, 2 Departamental)");
        if (tipoDestino != 1 && tipoDestino != 2)
            throw new IllegalArgumentException("Destino desconocido.");

        Destino destino = tipoDestino == 1 ? Destino.URBANO : Destino.DEPARTAMENTAL;
        switch (categoria) {
            case 1:
                return new Sobre(codigo, remitente, destinatario,
                        direccion, celular, peso, destino);
            case 2: case 3: {
                double largo = vista.leerDouble("Largo en cm");
                double ancho = vista.leerDouble("Ancho en cm");
                double alto = vista.leerDouble("Alto en cm");
                return categoria == 2
                        ? new PaqueteNormal(codigo, remitente, destinatario,
                                direccion, celular, peso, destino, largo, ancho, alto)
                        : new PaqueteFragil(codigo, remitente, destinatario,
                                direccion, celular, peso, destino, largo, ancho, alto,
                                vista.leerDouble("Valor declarado en Q"));
            }
            default:
                return new CargaRefrigerada(codigo, remitente, destinatario,
                        direccion, celular, peso, destino,
                        vista.leerDouble("Temperatura en C"));
        }
    }

    private void mostrarEnvios() {
        envios.values().forEach(envio -> System.out.printf(Locale.US,
                "%s | %s | %s | %.2f kg | %s | intentos: %d%n",
                envio.getCodigo(), envio.getCategoria(), envio.getEstado(),
                envio.getPesoReal(), envio.getDestino(), envio.getIntentos()));
    }

    private void mostrarDespachos() {
        for (Despacho despacho : despachos) {
            System.out.printf(Locale.US,
                    "%s | %s | %.2f kg | disponibles: %.2f kg%n",
                    despacho.getVehiculo().getPlaca(), despacho.getEstado(),
                    despacho.pesoCargado(), despacho.capacidadDisponible());
            for (Paquete envio : despacho.getEnvios()) {
                System.out.println("  " + envio.getCodigo() + " | " + envio.getEstado());
            }
        }
    }

    private Paquete buscarEnvio(String codigo) {
        Paquete envio = envios.get(clave(codigo));
        if (envio == null)
            throw new IllegalArgumentException("Paquete desconocido.");
        return envio;
    }

    private Vehiculo buscarVehiculo(String placa) {
        Vehiculo vehiculo = vehiculos.get(clave(placa));
        if (vehiculo == null)
            throw new IllegalArgumentException("Vehiculo desconocido.");
        return vehiculo;
    }

    private Despacho buscarDespachoActivo(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        return despachos.stream()
                .filter(d -> d.getVehiculo() == vehiculo
                        && d.getEstado() != EstadoDespacho.CERRADO)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No hay despacho activo para esa placa."));
    }

    private static String clave(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }

    private void cargarEjemplos() {
        registrarVehiculo("P-101", 10, false);
        registrarVehiculo("P-202", 20, true);
        registrarVehiculo("P-303", 50, false);

        Cliente ejemplo = new Cliente(
                "Remitente ejemplo", "Ciudad de Guatemala", "55550000");

        for (Paquete envio : new Paquete[] {
                new Sobre("S1", ejemplo, "Eva", "Direccion", "55551111",
                        0.2, Destino.URBANO),
                new Sobre("S2", ejemplo, "Eva", "Direccion", "55551111",
                        0.4, Destino.DEPARTAMENTAL),
                new PaqueteNormal("P1", ejemplo, "Eva", "Direccion", "55551111",
                        3.2, Destino.URBANO, 40, 30, 20),
                new PaqueteNormal("P2", ejemplo, "Eva", "Direccion", "55551111",
                        2, Destino.DEPARTAMENTAL, 20, 20, 20),
                new PaqueteFragil("F1", ejemplo, "Eva", "Direccion", "55551111",
                        1.5, Destino.URBANO, 20, 20, 20, 1200),
                new PaqueteFragil("F2", ejemplo, "Eva", "Direccion", "55551111",
                        2.5, Destino.DEPARTAMENTAL, 30, 20, 20, 500),
                new CargaRefrigerada("R1", ejemplo, "Eva", "Direccion", "55551111",
                        2, Destino.URBANO, 4),
                new CargaRefrigerada("R2", ejemplo, "Eva", "Direccion", "55551111",
                        4, Destino.URBANO, -10)
        }) {
            envios.put(clave(envio.getCodigo()), envio);
        }
    }

    Collection<Paquete> getEnvios() {
        return Collections.unmodifiableCollection(envios.values());
    }

    List<Despacho> getDespachos() {
        return Collections.unmodifiableList(despachos);
    }

    Registro getRegistro() {
        return registro;
    }

    Vehiculo getVehiculo(String placa) {
        return buscarVehiculo(placa);
    }
}