import os
import csv
import math
import time
import random

RUNS = 50

# -------- Função de busca binária --------
def busca_binaria_casos(vetor, chave):
    esquerda, direita = 0, len(vetor) - 1
    while esquerda <= direita:
        meio = (esquerda + direita) // 2
        if vetor[meio] == chave:
            return meio
        elif vetor[meio] < chave:
            esquerda = meio + 1
        else:
            direita = meio - 1
    return -1

# -------- Leitura do vetor CSV --------
def ler_vetor(caminho):
    try:
        with open(caminho, newline='') as f:
            reader = csv.reader(f)
            linha = next(reader)
            vetor = [int(x) for x in linha]
        return vetor
    except Exception as e:
        print(f"Erro ao abrir {caminho}: {e}")
        return None

# -------- Medição do tempo em nanossegundos --------
def medir_tempo_execucao_ns(vetor, chave):
    inicio = time.perf_counter_ns()
    busca_binaria_casos(vetor, chave)
    fim = time.perf_counter_ns()
    return fim - inicio

# -------- Cálculos estatísticos --------
def calcular_media(valores):
    return sum(valores) / len(valores)

def calcular_desvio_padrao(valores, media):
    soma = sum((v - media) ** 2 for v in valores)
    return math.sqrt(soma / len(valores))

# -------- Função principal --------
def main():
    tamanhos = [10000, 20000, 30000, 40000, 50000,
                60000, 70000, 80000, 90000, 100000]
    casos = ["MC", "CM", "PC"]

    # Cria os diretórios de saída, se não existirem
    os.makedirs("resultados/estatisticas", exist_ok=True)
    os.makedirs("resultados/brutos", exist_ok=True)

    arquivo_est = "resultados/estatisticas/resultados_casos_python.csv"
    arquivo_brutos = "resultados/brutos/resultados_python_run.csv"

    with open(arquivo_est, "w", newline='') as f_est, \
         open(arquivo_brutos, "w", newline='') as f_brutos:

        writer_est = csv.writer(f_est)
        writer_brutos = csv.writer(f_brutos)

        writer_est.writerow(["n", "caso", "media_ns", "desvio_ns"])
        writer_brutos.writerow(["n", "caso", "run", "tempo_ns"])

        for n in tamanhos:
            for tipo, caso in enumerate(casos):
                tempos = []

                for run in range(1, RUNS + 1):
                    caminho = f"dados/n{n:06d}/run_{run:03d}.csv"
                    vetor = ler_vetor(caminho)
                    if vetor is None:
                        continue

                    if tipo == 0:  # MC - melhor caso
                        chave = vetor[len(vetor) // 2]
                    elif tipo == 1:  # CM - caso médio
                        if random.random() < 0.5:
                            chave = random.choice(vetor)
                        else:
                            chave = vetor[-1] + random.randint(1, 500)
                    else:  # PC - pior caso
                        chave = vetor[-1] + 10000

                    tempo = medir_tempo_execucao_ns(vetor, chave)
                    tempos.append(tempo)
                    writer_brutos.writerow([n, caso, run, tempo])

                if tempos:
                    media = calcular_media(tempos)
                    desvio = calcular_desvio_padrao(tempos, media)
                    writer_est.writerow([n, caso, media, desvio])
                    print(f"n={n} ({caso}) concluído (média={media:.2f} ns, desvio={desvio:.2f} ns)")

if __name__ == "__main__":
    main()
