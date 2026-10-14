final class Entrega {
    private final Paquete envio;
    private final boolean recibido;
    private final String firma;

    Entrega(Paquete envio, boolean recibido, String firma) {
        this.envio = envio;
        this.recibido = recibido;
        this.firma = recibido ? Paquete.texto(firma, "Firma") : "";
    }

    Paquete getEnvio() { return envio; }
    boolean fueRecibido() { return recibido; }
    String getFirma() { return firma; }
}
