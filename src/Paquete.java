import java.util.Objects;

/** Clase padre de las cuatro categorias de paquetes. */
public abstract class Paquete {
    private final String codigo;
    private final Cliente remitente;
    private final String nombreDestinatario;
    private final String direccionDestinatario;
    private final String celularDestinatario;
    private final double pesoReal;
    private final Destino destino;
    private EstadoEnvio estado = EstadoEnvio.BODEGA;
    private int intentos;
    private Despacho despacho;

    protected Paquete(String codigo, Cliente remitente, String nombreDestinatario,
                      String direccionDestinatario, String celularDestinatario,
                      double pesoReal, Destino destino) {
        this.codigo = texto(codigo, "Codigo");
        this.remitente = Objects.requireNonNull(remitente, "Remitente requerido");
        this.nombreDestinatario = texto(nombreDestinatario, "Destinatario");
        this.direccionDestinatario = texto(direccionDestinatario, "Direccion del destinatario");
        this.celularDestinatario = texto(celularDestinatario, "Celular del destinatario");
        this.pesoReal = positivo(pesoReal, "Peso real");
        this.destino = Objects.requireNonNull(destino, "Destino requerido");
    }

    public abstract double calcularCosto();
    public abstract int estimarDias();
    public abstract int maximoIntentos();
    public boolean compatibleCon(Vehiculo vehiculo) { return true; }

    public void registrarResultado(boolean entregado) {
        if (estado != EstadoEnvio.EN_RUTA) throw new IllegalStateException("El paquete no esta en ruta.");
        intentos++;
        estado = entregado ? EstadoEnvio.ENTREGADO
                : intentos >= maximoIntentos() ? EstadoEnvio.DEVUELTO : EstadoEnvio.BODEGA;
        despacho = null;
    }

    void asignarA(Despacho nuevo) {
        if (estado != EstadoEnvio.BODEGA || despacho != null) {
            throw new IllegalStateException("El paquete debe estar en bodega y sin otro despacho.");
        }
        despacho = nuevo;
    }

    void retirarDe(Despacho actual) {
        if (despacho != actual || estado != EstadoEnvio.BODEGA) {
            throw new IllegalStateException("El paquete no esta asignado a este despacho en preparacion.");
        }
        despacho = null;
    }

    void salirCon(Despacho actual) {
        if (despacho != actual || estado != EstadoEnvio.BODEGA) throw new IllegalStateException("Asignacion invalida.");
        estado = EstadoEnvio.EN_RUTA;
    }

    protected final double pesoFacturable(double largo, double ancho, double alto) {
        return Math.ceil(Math.max(pesoReal, largo * ancho * alto / 5000));
    }

    protected final double costoPaquete(double largo, double ancho, double alto) {
        return (destino == Destino.URBANO ? 25 : 40) + 8 * pesoFacturable(largo, ancho, alto);
    }

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException(campo + " no puede estar vacio.");
        return valor.trim();
    }

    static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) throw new IllegalArgumentException(campo + " debe ser positivo.");
        return valor;
    }

    protected static void validarDimensiones(double largo, double ancho, double alto) {
        positivo(largo, "Largo");
        positivo(ancho, "Ancho");
        positivo(alto, "Alto");
        if (!Double.isFinite(largo * ancho * alto / 5000)) {
            throw new IllegalArgumentException("Dimensiones fuera de rango.");
        }
    }

    public String getCodigo() { return codigo; }
    public Cliente getRemitente() { return remitente; }
    public String getDestinatario() { return nombreDestinatario; }
    public String getDireccionDestinatario() { return direccionDestinatario; }
    public String getCelularDestinatario() { return celularDestinatario; }
    public double getPesoReal() { return pesoReal; }
    public Destino getDestino() { return destino; }
    public EstadoEnvio getEstado() { return estado; }
    public int getIntentos() { return intentos; }
    public Despacho getDespacho() { return despacho; }
    public String getCategoria() { return getClass().getSimpleName(); }
}
