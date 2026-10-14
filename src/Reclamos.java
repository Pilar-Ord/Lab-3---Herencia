final class Reclamos {
    boolean validarReclamo(String comentario) {
        return comentario != null && !comentario.isBlank();
    }
}