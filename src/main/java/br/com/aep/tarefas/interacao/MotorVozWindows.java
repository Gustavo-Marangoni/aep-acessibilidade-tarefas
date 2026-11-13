package br.com.aep.tarefas.interacao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Ponte entre o Java e o serviço de voz do Windows (System.Speech, que já vem
 * instalado no Windows 10/11). Usa o PowerShell para:
 * - reconhecer a fala do microfone (RF08 - comandos de voz);
 * - ler textos em voz alta (síntese de fala).
 *
 * Em outros sistemas operacionais o motor fica indisponível e o programa
 * continua funcionando pelo teclado.
 */
public class MotorVozWindows {

    private static final String SCRIPT_OUVIR = String.join("\n",
            "param([string]$ArquivoFrases = '', [int]$Segundos = 8, [switch]$Ditado)",
            "[Console]::OutputEncoding = [System.Text.Encoding]::UTF8",
            "Add-Type -AssemblyName System.Speech",
            "$todos = [System.Speech.Recognition.SpeechRecognitionEngine]::InstalledRecognizers()",
            "$info = $todos | Where-Object { $_.Culture.Name -eq 'pt-BR' } | Select-Object -First 1",
            "if (-not $info) { $info = $todos | Select-Object -First 1 }",
            "if (-not $info) { Write-Output '#SEM_RECONHECEDOR'; exit 2 }",
            "$motor = New-Object System.Speech.Recognition.SpeechRecognitionEngine($info)",
            "try { $motor.SetInputToDefaultAudioDevice() } catch { Write-Output '#SEM_MICROFONE'; exit 3 }",
            "if ($Ditado) {",
            "  $motor.LoadGrammar((New-Object System.Speech.Recognition.DictationGrammar))",
            "} else {",
            "  $frases = [string[]](Get-Content -LiteralPath $ArquivoFrases -Encoding UTF8 | Where-Object { $_ -ne '' })",
            "  $escolhas = New-Object System.Speech.Recognition.Choices",
            "  $escolhas.Add($frases)",
            "  $construtor = New-Object System.Speech.Recognition.GrammarBuilder",
            "  $construtor.Culture = $info.Culture",
            "  $construtor.Append($escolhas)",
            "  $motor.LoadGrammar((New-Object System.Speech.Recognition.Grammar($construtor)))",
            "}",
            "$resultado = $motor.Recognize([TimeSpan]::FromSeconds($Segundos))",
            "if ($resultado -and $resultado.Confidence -ge 0.3) { Write-Output $resultado.Text } else { Write-Output '' }",
            "$motor.Dispose()");

    private static final String SCRIPT_FALAR = String.join("\n",
            "param([string]$ArquivoTexto, [int]$Velocidade = 0)",
            "Add-Type -AssemblyName System.Speech",
            "$sintetizador = New-Object System.Speech.Synthesis.SpeechSynthesizer",
            "$voz = $sintetizador.GetInstalledVoices() | Where-Object { $_.Enabled -and $_.VoiceInfo.Culture.Name -eq 'pt-BR' } | Select-Object -First 1",
            "if ($voz) { $sintetizador.SelectVoice($voz.VoiceInfo.Name) }",
            "$sintetizador.Rate = [Math]::Max(-10, [Math]::Min(10, $Velocidade))",
            "$sintetizador.Speak((Get-Content -LiteralPath $ArquivoTexto -Encoding UTF8 -Raw))",
            "$sintetizador.Dispose()");

    private final boolean disponivel;
    private Path pastaTemporaria;
    private Path scriptOuvir;
    private Path scriptFalar;
    private String ultimoErro = "";

    public MotorVozWindows() {
        this.disponivel = System.getProperty("os.name", "").toLowerCase().contains("windows") && prepararScripts();
    }

    private boolean prepararScripts() {
        try {
            pastaTemporaria = Files.createTempDirectory("aep-voz");
            scriptOuvir = pastaTemporaria.resolve("ouvir.ps1");
            scriptFalar = pastaTemporaria.resolve("falar.ps1");
            Files.writeString(scriptOuvir, SCRIPT_OUVIR, StandardCharsets.UTF_8);
            Files.writeString(scriptFalar, SCRIPT_FALAR, StandardCharsets.UTF_8);
            pastaTemporaria.toFile().deleteOnExit();
            scriptOuvir.toFile().deleteOnExit();
            scriptFalar.toFile().deleteOnExit();
            return true;
        } catch (IOException e) {
            ultimoErro = e.getMessage();
            return false;
        }
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public String getUltimoErro() {
        return ultimoErro;
    }

    /** Lê o texto em voz alta e só retorna quando terminar de falar. */
    public void falar(String texto, int velocidade) { // velocidade de 0 a 10 (5 = normal)
        if (!disponivel || texto == null || texto.isBlank()) {
            return;
        }
        Path arquivo = null;
        try {
            arquivo = Files.createTempFile(pastaTemporaria, "fala", ".txt");
            Files.writeString(arquivo, texto, StandardCharsets.UTF_8);
            executar(List.of("-File", scriptFalar.toString(), "-ArquivoTexto", arquivo.toString(),
                    "-Velocidade", String.valueOf((velocidade - 5) * 2)), 120);
        } catch (IOException e) {
            ultimoErro = e.getMessage();
        } finally {
            apagar(arquivo);
        }
    }

    /**
     * Escuta o microfone até reconhecer uma das frases (ou, com frases vazias,
     * em modo ditado livre).
     *
     * @return o texto reconhecido, vazio se nada foi entendido
     * @throws IllegalStateException se não houver reconhecedor ou microfone
     */
    public String ouvir(List<String> frases, int segundos) {
        if (!disponivel) {
            throw new IllegalStateException("Reconhecimento de voz disponível apenas no Windows.");
        }
        Path arquivo = null;
        try {
            List<String> argumentos = new ArrayList<>(List.of("-File", scriptOuvir.toString(),
                    "-Segundos", String.valueOf(segundos)));
            if (frases == null || frases.isEmpty()) {
                argumentos.add("-Ditado");
            } else {
                arquivo = Files.createTempFile(pastaTemporaria, "frases", ".txt");
                Files.write(arquivo, frases, StandardCharsets.UTF_8);
                argumentos.add("-ArquivoFrases");
                argumentos.add(arquivo.toString());
            }
            String saida = executar(argumentos, segundos + 20).trim();
            if (saida.contains("#SEM_RECONHECEDOR")) {
                throw new IllegalStateException("Nenhum reconhecedor de fala instalado no Windows.");
            }
            if (saida.contains("#SEM_MICROFONE")) {
                throw new IllegalStateException("Nenhum microfone encontrado.");
            }
            String[] linhas = saida.split("\\R");
            return linhas[linhas.length - 1].trim();
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao usar o reconhecimento de voz: " + e.getMessage(), e);
        } finally {
            apagar(arquivo);
        }
    }

    private String executar(List<String> argumentos, int limiteSegundos) throws IOException {
        List<String> comando = new ArrayList<>(List.of("powershell", "-NoProfile", "-NonInteractive",
                "-ExecutionPolicy", "Bypass"));
        comando.addAll(argumentos);
        Process processo = new ProcessBuilder(comando).redirectErrorStream(true).start();
        processo.getOutputStream().close();
        try (InputStream saida = processo.getInputStream()) {
            if (!processo.waitFor(limiteSegundos, TimeUnit.SECONDS)) {
                processo.destroyForcibly();
                return "";
            }
            return new String(saida.readAllBytes(), StandardCharsets.UTF_8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            processo.destroyForcibly();
            return "";
        }
    }

    private static void apagar(Path arquivo) {
        if (arquivo != null) {
            try {
                Files.deleteIfExists(arquivo);
            } catch (IOException ignorado) {
                // arquivo temporário; o sistema limpa depois
            }
        }
    }
}
