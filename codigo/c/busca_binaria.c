#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <math.h>
#include <windows.h> // para QueryPerformanceCounter

#define RUNS 50

// -------- Função de busca binária --------
int busca_binaria(int *vetor, int n, int chave)
{
    int esquerda = 0, direita = n - 1;
    while (esquerda <= direita)
    {
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
int *ler_vetor(const char *caminho, int n)
{
    FILE *arquivo = fopen(caminho, "r");
    if (!arquivo)
    {
        printf("Erro ao abrir %s\n", caminho);
        return NULL;
    }

    int *vetor = (int *)malloc(n * sizeof(int));
    if (!vetor)
    {
        printf("Erro de alocação de memória.\n");
        fclose(arquivo);
        return NULL;
    }

    for (int i = 0; i < n; i++)
    {
        fscanf(arquivo, "%d,", &vetor[i]);
    }

    fclose(arquivo);
    return vetor;
}

// -------- Geração da chave --------
int gerar_chave(int max_valor)
{
    if (rand() % 2 == 0)
        return rand() % (max_valor + 1); // valor possivelmente presente
    else
        return max_valor + (rand() % 1000); // valor inexistente
}

// -------- Medição do tempo em nanossegundos --------
double medir_tempo_execucao_ns(int *vetor, int n, int chave)
{
    LARGE_INTEGER inicio, fim, freq;
    QueryPerformanceFrequency(&freq);
    QueryPerformanceCounter(&inicio);

    busca_binaria(vetor, n, chave);

    QueryPerformanceCounter(&fim);
    double tempo_ns = ((double)(fim.QuadPart - inicio.QuadPart) * 1e9) / freq.QuadPart;
    return tempo_ns;
}

// -------- Calcular média --------
double calcular_media(double *tempos, int tamanho)
{
    double soma = 0.0;
    for (int i = 0; i < tamanho; i++)
        soma += tempos[i];
    return soma / tamanho;
}

// -------- Calcular desvio padrão --------
double calcular_desvio_padrao(double *tempos, int tamanho, double media)
{
    double soma = 0.0;
    for (int i = 0; i < tamanho; i++)
    {
        double dif = tempos[i] - media;
        soma += dif * dif;
    }
    return sqrt(soma / tamanho);
}

// -------- Função principal --------
int main()
{
    srand((unsigned)time(NULL));
    int tamanhos[] = {10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000};
    int num_tamanhos = sizeof(tamanhos) / sizeof(tamanhos[0]);

    // Arquivos de saída
    const char *arquivo_estatisticas = "..\\..\\resultados\\estatisticas\\resultados_casos_C.csv";
    const char *arquivo_brutos = "..\\..\\resultados\\brutos\\resultados_casos_C_run.csv";

    // Cria os diretórios, se não existirem
    system("mkdir ..\\..\\resultados 2>nul");
    system("mkdir ..\\..\\resultados\\estatisticas 2>nul");
    system("mkdir ..\\..\\resultados\\brutos 2>nul");

    FILE *saida_est = fopen(arquivo_estatisticas, "w");
    FILE *saida_brutos = fopen(arquivo_brutos, "w");

    if (!saida_est || !saida_brutos)
    {
        printf("Erro ao criar arquivos de saída.\n");
        return 1;
    }

    fprintf(saida_est, "n,media_ns,desvio_ns\n");
    fprintf(saida_brutos, "n,run,tempo_ns\n");

    for (int i = 0; i < num_tamanhos; i++)
    {
        int n = tamanhos[i];
        double tempos[RUNS];

        for (int run = 1; run <= RUNS; run++)
        {
            char caminho[256];
            sprintf(caminho, "..\\..\\dados\\n%06d\\run_%03d.csv", n, run);

            int *vetor = ler_vetor(caminho, n);
            if (!vetor)
                continue;

            int chave = gerar_chave(vetor[n - 1]);
            double tempo = medir_tempo_execucao_ns(vetor, n, chave);
            tempos[run - 1] = tempo;

            fprintf(saida_brutos, "%d,%d,%.2f\n", n, run, tempo);
            free(vetor);
        }

        double media = calcular_media(tempos, RUNS);
        double desvio = calcular_desvio_padrao(tempos, RUNS, media);

        fprintf(saida_est, "%d,%.2f,%.2f\n", n, media, desvio);
        printf("n = %d concluído (média = %.2f ns, desvio = %.2f ns)\n", n, media, desvio);
    }

    fclose(saida_est);
    fclose(saida_brutos);

    printf("\nArquivos gerados:\n");
    printf(" - %s (médias e desvios)\n", arquivo_estatisticas);
    printf(" - %s (resultados brutos)\n", arquivo_brutos);
    return 0;
}
