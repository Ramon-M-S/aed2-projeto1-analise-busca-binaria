# Análise de Desempenho da Busca Binária  

**Implementações em C, Java e Python**  

Este documento apresenta uma análise comparativa de desempenho do algoritmo de **busca binária** implementado nas linguagens **C**, **Java** e **Python**. Todas as versões seguem a mesma metodologia experimental:
- Vetores de entrada idênticos (arquivos CSV ordenados).
- Tamanhos testados: **10.000 a 100.000 elementos** (incrementos de 10.000).
- **50 rodadas** por configuração (`RUNS = 50`).
- Dois modos de teste:
  1. **Casos específicos**: Melhor Caso (MC), Caso Médio (CM) e Pior Caso (PC).
  2. **Caso aleatório**: chave gerada estocasticamente (50% presente, 50% ausente).
- Saída em dois formatos:
  - **Resultados brutos**: todos os tempos individuais.
  - **Estatísticas**: média e desvio padrão por configuração.

---

## 🔹 Implementação em C

Desenvolvida com foco em **eficiência de baixo nível** e **precisão temporal**, aproveitando a API de desempenho do Windows.

### Características técnicas:

- **Medição de tempo**: `QueryPerformanceCounter` → resolução de nanossegundos.  
- **Alocação de memória**: `malloc`/`free`.
- **Leitura de CSV**: uso direto de `fscanf`.
- **Portabilidade**: limitada ao Windows (devido à API de tempo).
- **Arquitetura**: totalmente procedural, sem abstração adicional.

### Variações:
- `busca_binaria_casos.c`: avalia MC, CM e PC com chaves controladas.
- `busca_binaria.c`: simula cenário real com chave aleatória.

---

## 🔹 Implementação em Java

Projetada para **clareza**, **portabilidade** e uso das melhores práticas da linguagem.

### Características técnicas:
- **Medição de tempo**: `System.nanoTime()` → alta resolução, cross-platform.
- **Gerenciamento de recursos**: `try-with-resources` para leitura de arquivos.
- **Leitura de CSV**: `BufferedReader` + `split(",")`.
- **Estrutura**: orientada a métodos estáticos, com uso de `java.nio.file` para diretórios.
- **Aleatoriedade**: classe `Random`.

### Variações:
- `BuscaBinaria.java`: caso aleatório.
- `BuscaBinariaCasos.java`: casos específicos (MC, CM, PC).



---

## 🔹 Implementação em Python

Escrita com ênfase em **legibilidade**, **simplicidade** e uso de bibliotecas padrão.

### Características técnicas:
- **Medição de tempo**: `time.perf_counter_ns()` → resolução de nanossegundos, disponível a partir do Python 3.7.
- **Leitura de CSV**: módulo `csv` (robusto contra formatação irregular).
- **Gerenciamento de arquivos**: contexto `with` para fechamento automático.
- **Criação de diretórios**: `os.makedirs(..., exist_ok=True)`.
- **Aleatoriedade**: `random.choice()` e `random.random()`.

### Variações:
- `busca_binaria_aleatorio.py`: caso aleatório.
- `busca_binaria_casos.py`: casos específicos (MC, CM, PC).


---

## 📁 Estrutura de Saída Padrão (Todas as Linguagens)

resultados/  
├── brutos/  
│ ├── resultados_casos_especificos_C.csv  
│ ├── resultados_casos_aleatorio_C.csv  
│ ├── resultados_casos_especificos_java.csv  
│ ├── resultados_casos_aleatorio_java.csv  
│ ├── resultados_especificos_python_run.cs  
│ └── resultados_casos_aleatorio_python_run.csv  
└── estatisticas/  
├── resultados_casos_especificos_C.csv  
├── resultados_casos_aleatorio_C.csv  
├── resultados_casos_especificos_java.csv  
├── resultados_casos_aleatorio_java.csv  
├── resultados_casos_especificos__python.csv  
└── resultados_casos_aleatorio_python.csv  
