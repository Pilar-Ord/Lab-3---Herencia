public final class PaqueteFragil extends Paquete {
    private final double largo;
    private final double ancho;
    private final double alto;
    private final double valorDeclarado;

    public PaqueteFragil(String codigo, Cliente remitente, String destinatario, String direccion,
                         String celular, double peso, Destino destino,
                         double largo, double ancho, double alto, double valorDeclarado) {
        super(codigo, remitente, destinatario, direccion, celular, peso, destino);
        validarDimensiones(largo, ancho, alto);
        this.largo = largo;
        this.ancho = ancho;
        this.alto = alto;
        this.valorDeclarado = positivo(valorDeclarado, "Valor declarado");
        if (!Double.isFinite(calcularCosto())) throw new IllegalArgumentException("Valor declarado fuera de rango.");
    }

    @Override public double calcularCosto() { return costoPaquete(largo, ancho, alto) + 15 + 0.03 * valorDeclarado; }
    @Override public int estimarDias() { return (getDestino() == Destino.URBANO ? 2 : 4) + 1; }
    @Override public int maximoIntentos() { return 2; }
}