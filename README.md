# Fila M/M/1 - Modelagem e Avaliação de Desempenho

Um sistema de fila M/M/1 é caracterizado por chegadas de clientes seguindo um processo de Poisson, tempos de serviço exponencialmente distribuídos, e um único servidor. A simulação tem como objetivo avaliar o desempenho do sistema em termos de tamanho médio da fila e tempo médio de resposta para diferentes taxas de chegada (λ).

Este repositório implementa a solução do **Exercício 8.27** sobre simulação de filas com múltiplas implementações e uma suite completa de testes.

<img src="./docs/image.png" height="500"/>

---

## 📋 Sumário

- [Implementações](#implementações)
- [Teoria](#teoria)
- [Execução](#execução)
- [Resultados](#resultados)
- [Testes](#testes)

---

## 🚀 Implementações

### Java
Implementação em Java com testes para verificar todos os casos possíveis da fila construída.

**Arquivos principais:**
- `Event.java` - Eventos da simulação
- `Queue.java` - Fila M/M/1 individual
- `QueueSimulation.java` - Simulação completa

**Execução:**
```bash
mvn clean compile exec:java
```

---

## 📐 Teoria

### Fila M/M/1

Um sistema M/M/1 possui:
- **Chegadas**: Processo de Poisson com taxa λ (clientes/segundo)
- **Serviço**: Distribuição Exponencial com taxa μ (clientes/segundo)
- **Servidor**: Único servidor (1)
- **Capacidade**: Infinita
- **Disciplina**: FIFO (First In, First Out)

### Fórmulas Teóricas

#### Tempo Médio no Sistema
```
W = 1/(μ - λ)
```

Para μ = 1 (taxa de serviço de 1 cliente/segundo):
```
W = 1/(1 - λ)
```

#### Número Médio de Clientes
```
L = λW = λ/(1 - λ)
```

#### Lei de Little
```
L = λW
```
Relaciona o número médio de clientes (L) com o tempo médio no sistema (W).

### Valores Teóricos

| λ    | W (M/M/1) | W (M/D/1) | Melhoria |
|------|-----------|-----------|----------|
| 0.5  | 2.0s      | ~1.5s     | 25%      |
| 0.8  | 5.0s      | ~3.0s     | 40%      |
| 0.9  | 10.0s     | ~5.5s     | 45%      |
| 0.99 | 100.0s    | ~51.0s    | 49%      |

**M/M/1**: Serviço exponencial (variável)  
**M/D/1**: Serviço determinístico (constante)

---

## 📊 Resultados

```
Queue Simulation - Exercise 8.27
Configuration:
  - Number of queues: 100
  - Simulation time: 10000.0 seconds
  - Lambda values: [0.5, 0.8, 0.9, 0.99]

================================================================================
Service Time: Exponentially Distributed (mean = 1s)
================================================================================
Lambda     Simulation      Theoretical     Error %        
--------------------------------------------------------------------------------
0.50       2.01            2.00            0.50           
0.80       5.12            5.00            2.40           
0.90       10.45           10.00           4.50           
0.99       105.23          100.00          5.23           
================================================================================

================================================================================
Service Time: Constant (1 second)
================================================================================
Lambda     Simulation      Theoretical     Error %        
--------------------------------------------------------------------------------
0.50       1.52            2.00            24.00          
0.80       2.91            5.00            41.80          
0.90       5.51            10.00           44.90          
0.99       51.23           100.00          48.77          
================================================================================

================================================================================
ANALYSIS: Which service time distribution is faster?
================================================================================
λ=0.50: Exp=2.01s, Const=1.52s → CONSTANT (24.4% faster)
λ=0.80: Exp=5.12s, Const=2.91s → CONSTANT (43.2% faster)
λ=0.90: Exp=10.45s, Const=5.51s → CONSTANT (47.3% faster)
λ=0.99: Exp=105.23s, Const=51.23s → CONSTANT (51.3% faster)

CONCLUSION: CONSTANT service times result in faster completion.
Reason: Eliminating service time variability reduces waiting time.
================================================================================
```

### Análise dos Resultados

**Questão do Exercício**: "Os clientes completam mais rapidamente com tempos de serviço exponencialmente distribuídos ou constantes?"

**Resposta**: ✅ **Serviço CONSTANTE é significativamente mais rápido**

**Por quê?**
- Tempos constantes eliminam a **variabilidade**
- Variabilidade cria "gargalos" imprevisíveis
- Com λ = 0.99, a melhoria chega a **~50%**!
- A teoria M/D/1 confirma: W(M/D/1) < W(M/M/1)

---

## 🧪 Testes

A implementação inclui uma **suite completa de 50+ testes**.

### Arquivos de Teste

- **EventTest.java** (8 testes)
    - Criação de eventos
    - Comparação e ordenação
    - Edge cases

- **QueueTest.java** (17 testes)
    - Geração de números aleatórios
    - Processamento de filas
    - Validação estatística
    - Lei de Little

- **QueueSimulationTest.java** (25+ testes)
    - Simulações completas
    - Conformidade com teoria
    - Comparação exponencial vs constante
    - Edge cases (λ baixo e alto)

### Executar Testes

**Maven:**
```bash
mvn test
```

### Modificar Parâmetros

```java
// Em QueueSimulation.java
private static final double[] LAMBDAS = {0.5, 0.8, 0.9, 0.99};
private static final int QUEUE_QUANTITY = 100;
private static final double SIMULATION_TIME = 10000.0;
```

### Adicionar Novos Testes

```java
@Test
@DisplayName("Descrição do teste")
void testMeuNovoTeste() {
    // Arrange
    Queue queue = new Queue(0.5, false, new Random(123));
    
    // Act
    double result = queue.generateInterarrivalTime();
    
    // Assert
    assertTrue(result > 0, "Tempo deve ser positivo");
}
```

---
## 🎯 Funcionalidades

### Implementadas ✅

- ✅ Simulação M/M/1 (serviço exponencial)
- ✅ Simulação M/D/1 (serviço determinístico)
- ✅ Múltiplas taxas de chegada (λ)
- ✅ Cálculo de tempo médio no sistema
- ✅ Comparação com valores teóricos
- ✅ Validação estatística

### Possíveis Extensões 🚧

- 🔲 Simulação M/M/c (múltiplos servidores)
- 🔲 Filas com capacidade limitada
- 🔲 Diferentes disciplinas (LIFO, Priority)
- 🔲 Interface gráfica (visualização)
- 🔲 Análise de sensibilidade
- 🔲 Exportação de resultados (CSV, JSON)
- 🔲 Comparação com sistemas reais

---
## 📖 Referências

### Teoria de Filas
- Ross, S. M. (2013). *Simulation* (5th ed.). Academic Press.
- Kleinrock, L. (1975). *Queueing Systems, Volume 1: Theory*. Wiley.
- Gross, D., & Harris, C. M. (1998). *Fundamentals of Queueing Theory* (3rd ed.). Wiley.

### Simulação
- Banks, J., Carson, J. S., Nelson, B. L., & Nicol, D. M. (2009). *Discrete-Event System Simulation* (5th ed.). Pearson.
- Law, A. M. (2015). *Simulation Modeling and Analysis* (5th ed.). McGraw-Hill.

### Lei de Little
- Little, J. D. C. (1961). "A Proof for the Queuing Formula: L = λW". *Operations Research*, 9(3), 383-387.


## 🏆 Conclusões

Este projeto demonstra:

1. **Validação Empírica**: Simulações convergem para valores teóricos
2. **Importância da Variabilidade**: Serviço constante é ~40-50% mais rápido
3. **Lei de Little**: W = L/λ confirmada empiricamente
4. **Sistema Crítico**: λ → 1 causa crescimento exponencial de W
5. **Qualidade de Código**: Testes garantem correção da implementação

### Principais análises

📈 **Desempenho vs Utilização**
- Utilização alta (λ ≈ 1) → Tempos de resposta explodem
- Trade-off entre eficiência e qualidade de serviço

⚡ **Variabilidade é Inimiga**
- Reduzir variabilidade melhora drasticamente o desempenho

🎯 **Simulação Valida Teoria**
- Erros < 10% entre simulação e teoria
- Aumentar tempo/amostras reduz erro
