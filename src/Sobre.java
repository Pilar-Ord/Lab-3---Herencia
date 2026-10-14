public final class Sobre extends Paquete {
    public Sobre(String codigo, Cliente remitente, String destinatario, String direccion,
                 String celular, double peso, Destino destino) {
        super(codigo, remitente, destinatario, direccion, celular, peso, destino);
        if (peso > 0.5) throw new IllegalArgumentException("Un sobre no puede superar 0.5 kg.");
    }

    @Override public double calcularCosto() { return getDestino() == Destino.URBANO ? 20 : 35; }
    @Override public int estimarDias() { return getDestino() == Destino.URBANO ? 1 : 2; }
    @Override public int maximoIntentos() { return 3; }
}