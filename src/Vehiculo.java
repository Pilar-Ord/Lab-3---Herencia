final class Vehiculo {
    private final String placa;
    private final double capacidad;
    private final boolean refrigerado;

    Vehiculo(String placa, double capacidad, boolean refrigerado) {
        this.placa = Paquete.texto(placa, "Placa");
        this.capacidad = Paquete.positivo(capacidad, "Capacidad");
        this.refrigerado = refrigerado;
    }

    String getPlaca() { return placa; }
    double getCapacidad() { return capacidad; }
    boolean esRefrigerado() { return refrigerado; }

    @Override public String toString() {
        return placa + " | capacidad: " + capacidad + " kg | refrigerado: " + (refrigerado ? "si" : "no");
    }
}