public final class PaqueteNormal extends Paquete {
    private final double largo;
    private final double ancho;
    private final double alto;

    public PaqueteNormal(String codigo, Cliente remitente, String destinatario, String direccion,
                         String celular, double peso, Destino destino,
                         double largo, double ancho, double alto) {
        super(codigo, remitente, destinatario, direccion, celular, peso, destino);
        validarDimensiones(largo, ancho, alto);
        this.largo = largo;
        this.ancho = ancho;
        this.alto = alto;
    }

    public double pesoFacturable() { return pesoFacturable(largo, ancho, alto); }
    @Override public double calcularCosto() { return costoPaquete(largo, ancho, alto); }
    @Override public int estimarDias() { return getDestino() == Destino.URBANO ? 2 : 4; }
    @Override public int maximoIntentos() { return 3; }
}