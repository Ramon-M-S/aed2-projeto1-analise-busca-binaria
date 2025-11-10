import os
import csv
import math
import time
import random

RUNS = 50

# -------- Função de busca binária --------
def busca_binaria(vetor, chave):
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

# -------- Leitura de vetor CSV --------
def ler_vetor(caminho):
    try:
        with open(caminho, newline='') as f:
            reader = csv.reader(f)
            linha = next(reader)
            return [int(x) for x in linha]
    except Exception:
        return None

# -------- Estatísticas --------
def media(valores):
    return sum(valores) / len(valores)

def desvio_padrao(valores, m):
    return math.sqrt(sum((v - m) ** 2 for v in valores) / len(valores))

# -------- Função principal --------
def main():
    tamanhos = [10000, 20000, 30000, 40000, 50000,
                60000, 70000, 80000, 90000, 100000]

    os.makedirs("resultados/estatisticas", exist_ok=True)
    os.makedirs("resultados/brutos", exist_ok=True)

    caminho_est = "resultados/estatisticas/resultados_casos_aleatorio_python.csv"
    caminho_bruto = "resultados/brutos/resultados_casos_aleatorio_python_run.csv"

    with open(caminho_est, "w", newline="") as f_est, \
         open(caminho_bruto, "w", newline="") as f_bruto:

        writer_est = csv.writer(f_est)
        writer_bruto = csv.writer(f_bruto)

        writer_est.writerow(["n", "media_ns", "desvio_ns"])
        writer_bruto.writerow(["n", "run", "tempo_ns"])

        for n in tamanhos:
            tempos = []

            for run in range(1, RUNS + 1):
                caminho = f"dados/n{n:06d}/run_{run:03d}.csv"
                vetor = ler_vetor(caminho)
                if vetor is None:
                    continue

                # Chave aleatória: metade das vezes existe, metade não
                if random.random() < 0.5:
                    chave = random.choice(vetor)
                else:
                    chave = vetor[-1] + random.randint(1, 1000)

                inicio = time.perf_counter_ns()
                busca_binaria(vetor, chave)
                fim = time.perf_counter_ns()

                tempo = fim - inicio
                tempos.append(tempo)
                writer_bruto.writerow([n, run, tempo])

            if tempos:
                m = media(tempos)
                dp = desvio_padrao(tempos, m)
                writer_est.writerow([n, m, dp])

if __name__ == "__main__":
    main()
