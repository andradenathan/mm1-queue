package com.github.andradenathan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Queue Tests")
public class QueueTest {
    private Queue queueConstant;
    private Queue queueExponential;

    @BeforeEach
    void setUp() {
        new Random(12345);
        queueConstant = new Queue(0.5, true, new Random(12345));
        queueExponential = new Queue(0.5, false, new Random(12345));
    }

    @Test
    @DisplayName("Deve inicializar fila vazia")
    void testInitialState() {
        assertEquals(0, queueConstant.getCustomersInQueue());
        assertEquals(0.0, queueConstant.getCurrentTime(), 0.001);
        assertTrue(queueConstant.isEmpty());
    }

    @Test
    @DisplayName("Deve gerar tempo de serviço constante de 1 segundo")
    void testConstantServiceTime() {
        for (int i = 0; i < 100; i++) {
            double serviceTime = queueConstant.generateServiceTime();
            assertEquals(1.0, serviceTime, 0.001,
                    "Tempo de serviço constante deve ser sempre 1.0");
        }
    }

    @Test
    @DisplayName("Tempo de serviço exponencial deve variar")
    void testExponentialServiceTime() {
        double[] serviceTimes = new double[100];
        boolean hasVariation = false;

        for (int i = 0; i < 100; i++) {
            serviceTimes[i] = queueExponential.generateServiceTime();
            if (i > 0 && Math.abs(serviceTimes[i] - serviceTimes[i-1]) > 0.01) {
                hasVariation = true;
            }
        }

        assertTrue(hasVariation, "Tempo de serviço exponencial deve variar");
    }

    @Test
    @DisplayName("Média de tempos de serviço exponenciais deve ser próxima de 1")
    void testExponentialServiceTimeMean() {
        Queue queue = new Queue(0.5, false, new Random());
        double sum = 0;
        int samples = 10000;

        for (int i = 0; i < samples; i++) {
            sum += queue.generateServiceTime();
        }

        double mean = sum / samples;
        assertEquals(1.0, mean, 0.05,
                "Média de tempos exponenciais deve ser aproximadamente 1.0");
    }

    @Test
    @DisplayName("Deve gerar tempos entre chegadas com taxa lambda correta")
    void testInterarrivalTimeGeneration() {
        Queue queue = new Queue(0.5, false, new Random());
        double sum = 0;
        int samples = 10000;

        for (int i = 0; i < samples; i++) {
            sum += queue.generateInterarrivalTime();
        }

        double meanInterarrival = sum / samples;
        double expectedMean = 1.0 / 0.5;

        assertEquals(expectedMean, meanInterarrival, 0.1,
                "Média de tempos entre chegadas deve ser 1/lambda");
    }

    @Test
    @DisplayName("Deve processar chegada incrementando clientes")
    void testProcessArrival() {
        assertEquals(0, queueConstant.getCustomersInQueue());

        queueConstant.processArrival();
        assertEquals(1, queueConstant.getCustomersInQueue());

        queueConstant.processArrival();
        assertEquals(2, queueConstant.getCustomersInQueue());
    }

    @Test
    @DisplayName("Deve processar saída decrementando clientes")
    void testProcessDeparture() {
        queueConstant.processArrival();
        queueConstant.processArrival();
        assertEquals(2, queueConstant.getCustomersInQueue());

        queueConstant.processDeparture();
        assertEquals(1, queueConstant.getCustomersInQueue());

        queueConstant.processDeparture();
        assertEquals(0, queueConstant.getCustomersInQueue());
    }

    @Test
    @DisplayName("Fila deve estar vazia após saídas suficientes")
    void testQueueEmptyAfterDepartures() {
        queueConstant.processArrival();
        queueConstant.processArrival();
        assertFalse(queueConstant.isEmpty());

        queueConstant.processDeparture();
        queueConstant.processDeparture();
        assertTrue(queueConstant.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar estatísticas corretamente")
    void testUpdateStatistics() {
        queueConstant.processArrival();
        queueConstant.processArrival(); // 2 clientes

        queueConstant.updateStatistics(5.0); // 2 clientes por 5 segundos
        assertEquals(5.0, queueConstant.getCurrentTime(), 0.001);

        queueConstant.processDeparture(); // 1 cliente
        queueConstant.updateStatistics(10.0); // 1 cliente por mais 5 segundos
        assertEquals(10.0, queueConstant.getCurrentTime(), 0.001);
    }

    @Test
    @DisplayName("Tempo médio no sistema deve ser calculado usando Lei de Little")
    void testAverageTimeInSystem() {
        Queue queue = new Queue(0.5, true, new Random());

        queue.processArrival();
        queue.processArrival();
        queue.updateStatistics(5.0);

        queue.processDeparture();
        queue.updateStatistics(10.0);

        double avgTime = queue.getAverageTimeInSystem();
        assertEquals(3.0, avgTime, 0.001);
    }

    @Test
    @DisplayName("Diferentes lambdas devem gerar tempos entre chegadas diferentes")
    void testDifferentLambdas() {
        Queue fastQueue = new Queue(0.9, true, new Random(999));
        Queue slowQueue = new Queue(0.1, true, new Random(999));

        double fastInterarrival = 0;
        double slowInterarrival = 0;

        for (int i = 0; i < 1000; i++) {
            fastInterarrival += fastQueue.generateInterarrivalTime();
            slowInterarrival += slowQueue.generateInterarrivalTime();
        }

        assertTrue(fastInterarrival < slowInterarrival,
                "Lambda maior deve ter tempos entre chegadas menores");
    }

    @Test
    @DisplayName("Tempo médio deve ser zero se nenhum cliente completou")
    void testAverageTimeWithNoCompletions() {
        double avgTime = queueConstant.getAverageTimeInSystem();

        assertTrue(avgTime == 0.0 || Double.isNaN(avgTime));
    }
}
