public final class Cliente {
    private final String nombreRemitente;
    private final String direccionRemitente;
    private final String numeroCelRemitente;

    public Cliente(String nombre, String direccion, String celular) {
        this.nombreRemitente = Paquete.texto(nombre, "Nombre del remitente");
        this.direccionRemitente = Paquete.texto(direccion, "Direccion del remitente");
        this.numeroCelRemitente = Paquete.texto(celular, "Celular del remitente");
    }

    public String getNombre() { return nombreRemitente; }
    public String getDireccion() { return direccionRemitente; }
    public String getCelular() { return numeroCelRemitente; }
}
