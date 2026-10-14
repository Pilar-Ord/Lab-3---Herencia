import java.util.Locale;
import java.util.Scanner;

final class Vista {
    private final Scanner scanner = new Scanner(System.in);

    void mostrarMenu() {
        System.out.println("\n=== ENVIA GT ===");
        System.out.println("1. Cotizar envio");
        System.out.println("2. Registrar envio");
        System.out.println("3. Registrar vehiculo");
        System.out.println("4. Crear despacho");
        System.out.println("5. Asignar envio");
        System.out.println("6. Retirar envio");
        System.out.println("7. Dar salida a despacho");
        System.out.println("8. Registrar resultado de entrega");
        System.out.println("9. Consultar envios");
        System.out.println("10. Consultar vehiculos");
        System.out.println("11. Ver reporte");
        System.out.println("12. Consultar despachos");
        System.out.println("0. Salir");
    }

    String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje + ": ");
            if (!scanner.hasNextLine()) throw new FinDeEntrada();
            String valor = scanner.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("El dato no puede quedar vacio.");
        }
    }

    double leerDouble(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                double numero = Double.parseDouble(valor.replace(',', '.'));
                if (Double.isFinite(numero)) return numero;
            } catch (NumberFormatException ex) {
                // Se pide una nueva entrada sin cerrar la aplicacion.
            }
            System.out.println("Introduce un numero valido.");
        }
    }

    int leerEntero(String mensaje) {
        while (true) {
            try { return Integer.parseInt(leerTexto(mensaje)); }
            catch (NumberFormatException ex) { System.out.println("Introduce un numero entero."); }
        }
    }

    boolean leerSiNo(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje + " (s/n)").toLowerCase(Locale.ROOT);
            if (valor.equals("s") || valor.equals("si")) return true;
            if (valor.equals("n") || valor.equals("no")) return false;
            System.out.println("Responde s o n.");
        }
    }

    static final class FinDeEntrada extends RuntimeException { }
}
