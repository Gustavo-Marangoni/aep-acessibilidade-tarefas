package br.com.aep.tarefas.util;

import br.com.aep.tarefas.model.RegraNegocioException;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Funções de apoio para interpretar o que o usuário digitou ou falou:
 * remove acentos, converte números falados ("doze") e datas ("amanhã").
 */
public final class Texto {

    private static final DateTimeFormatter DATA_COMPLETA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final Map<String, Integer> UNIDADES = Map.ofEntries(
            Map.entry("zero", 0), Map.entry("um", 1), Map.entry("uma", 1), Map.entry("dois", 2),
            Map.entry("duas", 2), Map.entry("tres", 3), Map.entry("quatro", 4), Map.entry("cinco", 5),
            Map.entry("seis", 6), Map.entry("sete", 7), Map.entry("oito", 8), Map.entry("nove", 9),
            Map.entry("dez", 10), Map.entry("onze", 11), Map.entry("doze", 12), Map.entry("treze", 13),
            Map.entry("catorze", 14), Map.entry("quatorze", 14), Map.entry("quinze", 15),
            Map.entry("dezesseis", 16), Map.entry("dezessete", 17), Map.entry("dezoito", 18),
            Map.entry("dezenove", 19));

    private static final Map<String, Integer> DEZENAS = Map.of(
            "vinte", 20, "trinta", 30, "quarenta", 40, "cinquenta", 50,
            "sessenta", 60, "setenta", 70, "oitenta", 80, "noventa", 90);

    private static final String[] NOMES_UNIDADES = {
        "zero", "um", "dois", "tres", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez",
        "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"};
    private static final String[] NOMES_DEZENAS = {
        "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa"};

    private Texto() {
    }

    /** Minúsculas, sem acentos, sem pontuação e com espaços simples. */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase()
                .replaceAll("[^a-z0-9@./+_ -]", " ")
                .replaceAll("[.]+$", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    /** Converte "12", "doze" ou "vinte e um" em número (1 a 99, mais zero). */
    public static Optional<Integer> paraNumero(String texto) {
        String limpo = normalizar(texto).replaceFirst("^(numero|tarefa|usuario|opcao) ", "");
        if (limpo.matches("\\d{1,9}")) {
            return Optional.of(Integer.parseInt(limpo));
        }
        if (UNIDADES.containsKey(limpo)) {
            return Optional.of(UNIDADES.get(limpo));
        }
        String[] partes = limpo.split(" e ");
        if (partes.length <= 2 && DEZENAS.containsKey(partes[0])) {
            int valor = DEZENAS.get(partes[0]);
            if (partes.length == 2) {
                Integer unidade = UNIDADES.get(partes[1]);
                if (unidade == null || unidade < 1 || unidade > 9) {
                    return Optional.empty();
                }
                valor += unidade;
            }
            return Optional.of(valor);
        }
        return Optional.empty();
    }

    /** Escreve um número por extenso (sem acento), usado na gramática de voz. */
    public static String porExtenso(int numero) {
        if (numero < 20) {
            return NOMES_UNIDADES[numero];
        }
        String dezena = NOMES_DEZENAS[numero / 10];
        return numero % 10 == 0 ? dezena : dezena + " e " + NOMES_UNIDADES[numero % 10];
    }

    /** Frases de número que o reconhecedor de voz deve aceitar (0 a 99). */
    public static List<String> frasesDeNumero() {
        List<String> frases = new ArrayList<>();
        for (int i = 0; i <= 99; i++) {
            frases.add(porExtenso(i));
        }
        frases.add("uma");
        frases.add("duas");
        return frases;
    }

    public static boolean ehSim(String texto) {
        String limpo = normalizar(texto);
        return limpo.equals("s") || limpo.equals("sim") || limpo.equals("confirmar")
                || limpo.equals("confirmo") || limpo.equals("isso");
    }

    public static boolean ehNao(String texto) {
        String limpo = normalizar(texto);
        return limpo.equals("n") || limpo.equals("nao") || limpo.equals("cancelar") || limpo.equals("cancela");
    }

    public static boolean ehManter(String texto) {
        String limpo = normalizar(texto);
        return limpo.isEmpty() || limpo.equals("manter") || limpo.equals("mantem") || limpo.equals("pular");
    }

    /**
     * Interpreta uma data. Aceita: vazio ou "sem data" (nenhuma data), "hoje",
     * "amanhã", "depois de amanhã", "dd/mm" e "dd/mm/aaaa".
     */
    public static LocalDate paraData(String texto, LocalDate hoje) {
        String limpo = normalizar(texto);
        switch (limpo) {
            case "":
            case "sem data":
            case "nenhuma":
                return null;
            case "hoje":
                return hoje;
            case "amanha":
                return hoje.plusDays(1);
            case "depois de amanha":
                return hoje.plusDays(2);
            default:
                break;
        }
        String data = limpo.replace('-', '/').replace('.', '/');
        if (data.matches("\\d{1,2}/\\d{1,2}")) {
            data = data + "/" + hoje.getYear();
        }
        String[] partes = data.split("/");
        if (partes.length == 3) {
            data = String.format("%02d/%02d/%s", Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), partes[2]);
        }
        try {
            return LocalDate.parse(data, DATA_COMPLETA);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new RegraNegocioException("Data inválida. Use dd/mm/aaaa, \"hoje\", \"amanhã\" ou \"sem data\".");
        }
    }
}
