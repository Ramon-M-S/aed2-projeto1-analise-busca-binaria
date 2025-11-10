package codigo.java;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BuscaBinaria {

    static final int RUNS = 50;

    // -------- Função de busca binária --------
    public static int buscaBinaria(int[] vetor, int chave) {
        int esquerda = 0, direita = vetor.length - 1;
        while (esquerda <= direita) {
            int meio = (esquerda + direita) / 2;
            if (vetor[meio] == chave)
                return meio;
            else if (vetor[meio] < chave)
                esquerda = meio + 1;
            else
                direita = meio - 1;
        }
        return -1;
    }

    // -------- Leitura do vetor CSV --------
    public static int[] lerVetor(String caminho, int n) {
        int[] vetor = new int[n];
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha = br.readLine();
            if (linha == null) return null;
            String[] valores = linha.split(",");
            for (int i = 0; i < n; i++) {
                vetor[i] = Integer.parseInt(valores[i].trim());
            }
        } catch (IOException e) {
            System.out.println("Erro ao abrir " + caminho);
            return null;
        }
        return vetor;
    }

    // -------- Geração da chave --------
    public static int gerarChave(int maxValor) {
        Random rand = new Random();
        if (rand.nextBoolean())
            return rand.nextInt(maxValor + 1); // valor possivelmente presente
        else
            return maxValor + rand.nextInt(1000); // valor inexistente
    }

    // -------- Medição do tempo em nanossegundos --------
    public static double medirTempoExecucaoNs(int[] vetor, int chave) {
        long inicio = System.nanoTime();
        buscaBinaria(vetor, chave);
        long fim = System.nanoTime();
        return (double) (fim - inicio);
    }

    // -------- Calcular média --------
    public static double calcularMedia(double[] tempos) {
        double soma = 0.0;
        for (double t : tempos)
            soma += t;
        return soma / tempos.length;
    }

    // -------- Calcular desvio padrão --------
    public static double calcularDesvioPadrao(double[] tempos, double media) {
        double soma = 0.0;
        for (double t : tempos) {
            double dif = t - media;
            soma += dif * dif;
        }
        return Math.sqrt(soma / tempos.length);
    }

    // -------- Função principal --------
    public static void main(String[] args) {
        int[] tamanhos = {10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000};

        // Diretórios e arquivos
        Path dirResultados = Paths.get("resultados");
        Path dirEstatisticas = dirResultados.resolve("estatisticas");
        Path dirBrutos = dirResultados.resolve("brutos");

        try {
            Files.createDirectories(dirEstatisticas);
            Files.createDirectories(dirBrutos);
        } catch (IOException e) {
            System.out.println("Erro ao criar diretórios");
            return;
        }

        File arquivoEst = dirEstatisticas.resolve("resultados_java.csv").toFile();
        File arquivoBrutos = dirBrutos.resolve("resultados_java_run.csv").toFile();

        try (PrintWriter saidaEst = new PrintWriter(new FileWriter(arquivoEst));
            PrintWriter saidaBrutos = new PrintWriter(new FileWriter(arquivoBrutos))) {

            saidaEst.println("n,media_ns,desvio_ns");
            saidaBrutos.println("n,run,tempo_ns");

            Random rand = new Random();

            for (int n : tamanhos) {
                double[] tempos = new double[RUNS];

                for (int run = 1; run <= RUNS; run++) {
                    String caminho = String.format("dados\\n%06d\\run_%03d.csv", n, run);
                    int[] vetor = lerVetor(caminho, n);
                    if (vetor == null) continue;

                    int chave = gerarChave(vetor[n - 1]);
                    double tempo = medirTempoExecucaoNs(vetor, chave);
                    tempos[run - 1] = tempo;

                    saidaBrutos.printf("%d,%d,%.2f%n", n, run, tempo);
                }

                double media = calcularMedia(tempos);
                double desvio = calcularDesvioPadrao(tempos, media);

                saidaEst.printf("%d,%.2f,%.2f%n", n, media, desvio);
                System.out.printf("n = %d concluído (média = %.2f ns, desvio = %.2f ns)%n", n, media, desvio);
            }

        

        } catch (IOException e) {
            System.out.println("Erro ao criar arquivos de saída.");
        }
    }
}
