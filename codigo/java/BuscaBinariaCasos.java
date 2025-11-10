package codigo.java;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class BuscaBinariaCasos {

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

    // -------- Medição do tempo em nanossegundos --------
    public static double medirTempoExecucaoNs(int[] vetor, int chave) {
        long inicio = System.nanoTime();
        buscaBinaria(vetor, chave);
        long fim = System.nanoTime();
        return (double) (fim - inicio);
    }

    // -------- Cálculos estatísticos --------
    public static double calcularMedia(double[] tempos) {
        double soma = 0.0;
        for (double t : tempos) soma += t;
        return soma / tempos.length;
    }

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
        String[] casos = {"MC", "CM", "PC"};
        Random rand = new Random();

        // Diretórios e arquivos
        Path dirResultados = Paths.get("resultados");
        Path dirEstatisticas = dirResultados.resolve("estatisticas");

        try {
            Files.createDirectories(dirEstatisticas);
        } catch (IOException e) {
            System.out.println("Erro ao criar diretórios");
            return;
        }

        File arquivoEst = dirEstatisticas.resolve("resultados_casos_java.csv").toFile();

        try (PrintWriter saidaEst = new PrintWriter(new FileWriter(arquivoEst))) {

            saidaEst.println("n,caso,media_ns,desvio_ns");
            //saidaBrutos.println("n,caso,run,tempo_ns");

            for (int n : tamanhos) {

                for (int tipo = 0; tipo < 3; tipo++) {
                    double[] tempos = new double[RUNS];

                    for (int run = 1; run <= RUNS; run++) {
                        String caminho = String.format("dados\\n%06d\\run_%03d.csv", n, run);
                        int[] vetor = lerVetor(caminho, n);
                        if (vetor == null) continue;

                        int chave;
                        if (tipo == 0) { // MC - melhor caso
                            chave = vetor[n / 2];
                        } else if (tipo == 1) { // CM - caso médio
                            chave = rand.nextBoolean() ? vetor[rand.nextInt(n)] : vetor[n - 1] + rand.nextInt(500);
                        } else { // PC - pior caso
                            chave = vetor[n - 1] + 10000; // garante inexistente
                        }

                        double tempo = medirTempoExecucaoNs(vetor, chave);
                        tempos[run - 1] = tempo;

                    }

                    double media = calcularMedia(tempos);
                    double desvio = calcularDesvioPadrao(tempos, media);

                    saidaEst.printf("%d,%s,%.2f,%.2f%n", n, casos[tipo], media, desvio);
                    System.out.printf("n=%d (%s) concluído (média=%.2f ns, desvio=%.2f ns)%n", n, casos[tipo], media, desvio);
                }
            }

        
        } catch (IOException e) {
            System.out.println("Erro ao criar arquivos de saída.");
        }
    }
}
