public final class CargaRefrigerada extends Paquete {
    private final double temperatura;

    public CargaRefrigerada(String codigo, Cliente remitente, String destinatario, String direccion,
                            String celular, double peso, Destino destino, double temperatura) {
        super(codigo, remitente, destinatario, direccion, celular, peso, destino);
        if (destino != Destino.URBANO) throw new IllegalArgumentException("La carga refrigerada solo admite destino urbano.");
        if (!Double.isFinite(temperatura) || temperatura < -25 || temperatura > 8) {
            throw new IllegalArgumentException("Temperatura permitida: -25 a 8 C.");
        }
        if (!Double.isFinite(25 + 12 * peso + 60)) throw new IllegalArgumentException("Peso fuera de rango.");
        this.temperatura = temperatura;
    }

    @Override public double calcularCosto() { return 25 + 12 * getPesoReal() + 60; }
    @Override public int estimarDias() { return 1; }
    @Override public int maximoIntentos() { return 1; }
    @Override public boolean compatibleCon(Vehiculo vehiculo) { return vehiculo.esRefrigerado(); }
    public double getTemperatura() { return temperatura; }
}