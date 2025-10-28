package com.github.andradenathan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Queue Simulation Integration Tests")
public class QueueSimulationTest {
    private QueueSimulation exponentialSimulation;
    private QueueSimulation constantSimulation;

    @BeforeEach
    void setUp() {
        exponentialSimulation = new QueueSimulation(false, 12345L);
        constantSimulation = new QueueSimulation(true, 12345L);
    }

    @Nested
    @DisplayName("Single Queue Simulation Tests")
    class SingleQueueTests {

        @Test
        @DisplayName("Deve simular uma fila com lambda = 0.5")
        void testSingleQueueSimulationLow() {
            double avgTime = exponentialSimulation.simulateSingleQueue(0.5);

            assertTrue(avgTime > 0, "Tempo médio deve ser positivo");
            assertTrue(avgTime < 100, "Tempo médio deve ser razoável");
        }

        @Test
        @DisplayName("Deve simular uma fila com lambda = 0.99")
        void testSingleQueueSimulationHigh() {
            double avgTime = exponentialSimulation.simulateSingleQueue(0.99);

            assertTrue(avgTime > 0, "Tempo médio deve ser positivo");
            assertTrue(avgTime > 10, "Com lambda alto, tempo deve ser grande");
        }

        @Test
        @DisplayName("Lambda maior deve produzir tempo médio maior")
        void testLambdaEffectOnAverageTime() {
            double timeLow = exponentialSimulation.simulateSingleQueue(0.5);
            double timeHigh = exponentialSimulation.simulateSingleQueue(0.9);

            assertTrue(timeHigh > timeLow,
                    "Lambda maior deve resultar em tempo médio maior");
        }

        @Test
        @DisplayName("Serviço constante deve ser mais rápido que exponencial")
        void testConstantFasterThanExponential() {
            double expTime = exponentialSimulation.simulateSingleQueue(0.8);
            double constTime = constantSimulation.simulateSingleQueue(0.8);

            assertTrue(constTime < expTime,
                    "Serviço constante deve ser mais rápido que exponencial");
        }

        @Test
        @DisplayName("Simulações com mesma seed devem produzir resultados iguais")
        void testReproducibility() {
            QueueSimulation sim1 = new QueueSimulation(false, 99999L);
            QueueSimulation sim2 = new QueueSimulation(false, 99999L);

            double result1 = sim1.simulateSingleQueue(0.7);
            double result2 = sim2.simulateSingleQueue(0.7);

            assertEquals(result1, result2, 0.001,
                    "Mesma seed deve produzir mesmos resultados");
        }
    }

    @Nested
    @DisplayName("Full Simulation Tests")
    class FullSimulationTests {
        @Test
        @DisplayName("Deve executar simulação completa com todos os lambdas")
        void testRunAllSimulations() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            assertNotNull(results);
            assertEquals(4, results.size(), "Deve ter 4 lambdas");

            assertTrue(results.containsKey(0.5));
            assertTrue(results.containsKey(0.8));
            assertTrue(results.containsKey(0.9));
            assertTrue(results.containsKey(0.99));
        }

        @Test
        @DisplayName("Resultados devem estar próximos da teoria")
        void testResultsCloseToTheory() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            for (Map.Entry<Double, SimulationResult> entry : results.entrySet()) {
                double lambda = entry.getKey();
                SimulationResult result = entry.getValue();

                double theoretical = 1.0 / (1.0 - lambda);

                assertEquals(theoretical, result.getTheoretical(), 0.001,
                        "Valor teórico deve ser 1/(1-lambda)");
            }
        }

        @Test
        @DisplayName("Tempos devem aumentar com lambda crescente")
        void testIncreasingTimesWithLambda() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            double time05 = results.get(0.5).getSimulation();
            double time08 = results.get(0.8).getSimulation();
            double time09 = results.get(0.9).getSimulation();
            double time099 = results.get(0.99).getSimulation();

            assertTrue(time05 < time08, "0.5 < 0.8");
            assertTrue(time08 < time09, "0.8 < 0.9");
            assertTrue(time09 < time099, "0.9 < 0.99");
        }

        @Test
        @DisplayName("Serviço constante deve ser consistentemente mais rápido")
        void testConstantAlwaysFaster() {
            Map<Double, SimulationResult> expResults =
                    exponentialSimulation.runAllSimulations();
            Map<Double, SimulationResult> constResults =
                    constantSimulation.runAllSimulations();

            for (double lambda : new double[]{0.5, 0.8, 0.9, 0.99}) {
                double expTime = expResults.get(lambda).getSimulation();
                double constTime = constResults.get(lambda).getSimulation();

                assertTrue(constTime < expTime,
                        String.format("Constante deve ser mais rápido para lambda=%.2f", lambda));
            }
        }
    }

    @Nested
    @DisplayName("SimulationResult Tests")
    class SimulationResultTests {

        @Test
        @DisplayName("Deve calcular erro corretamente")
        void testErrorCalculation() {
            SimulationResult result =
                    new SimulationResult(5.0, 4.0);

            // Erro = |5-4|/4 * 100 = 25%
            assertEquals(25.0, result.getError(), 0.01);
        }

        @Test
        @DisplayName("Erro deve ser zero quando valores são iguais")
        void testZeroError() {
            SimulationResult result =
                    new SimulationResult(10.0, 10.0);

            assertEquals(0.0, result.getError(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar valores corretos")
        void testGetters() {
            SimulationResult result =
                    new SimulationResult(3.5, 4.0);

            assertEquals(3.5, result.getSimulation(), 0.001);
            assertEquals(4.0, result.getTheoretical(), 0.001);
        }

        @Test
        @DisplayName("ToString deve conter valores formatados")
        void testToString() {
            SimulationResult result =
                    new SimulationResult(5.25, 5.00);

            String str = result.toString();

            assertTrue(str.contains("5.25") || str.contains("5,25"));
            assertTrue(str.contains("5.00") || str.contains("5,00"));
        }
    }

    @Nested
    @DisplayName("Statistical Validation Tests")
    class StatisticalTests {

        @Test
        @DisplayName("Lambda=0.5 deve ter W teórico = 2.0")
        void testTheoryLambda05() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            double theoretical = results.get(0.5).getTheoretical();
            assertEquals(2.0, theoretical, 0.001);
        }

        @Test
        @DisplayName("Lambda=0.8 deve ter W teórico = 5.0")
        void testTheoryLambda08() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            double theoretical = results.get(0.8).getTheoretical();
            assertEquals(5.0, theoretical, 0.001);
        }

        @Test
        @DisplayName("Lambda=0.9 deve ter W teórico = 10.0")
        void testTheoryLambda09() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            double theoretical = results.get(0.9).getTheoretical();
            assertEquals(10.0, theoretical, 0.001);
        }

        @Test
        @DisplayName("Lambda=0.99 deve ter W teórico ≈ 100.0")
        void testTheoryLambda099() {
            Map<Double, SimulationResult> results =
                    exponentialSimulation.runAllSimulations();

            double theoretical = results.get(0.99).getTheoretical();
            assertEquals(100.0, theoretical, 0.001);
        }

        @Test
        @DisplayName("Benefício do serviço constante deve aumentar com lambda")
        void testConstantBenefitIncreasesWithLambda() {
            Map<Double, SimulationResult> expResults =
                    exponentialSimulation.runAllSimulations();
            Map<Double, SimulationResult> constResults =
                    constantSimulation.runAllSimulations();

            double improvement05 = (expResults.get(0.5).getSimulation() -
                    constResults.get(0.5).getSimulation()) /
                    expResults.get(0.5).getSimulation();

            double improvement099 = (expResults.get(0.99).getSimulation() -
                    constResults.get(0.99).getSimulation()) /
                    expResults.get(0.99).getSimulation();

            assertTrue(improvement099 > improvement05,
                    "Benefício do serviço constante deve ser maior com lambda alto");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Robustness")
    class EdgeCaseTests {

        @Test
        @DisplayName("Deve lidar com lambda muito baixo")
        void testVeryLowLambda() {
            double avgTime = exponentialSimulation.simulateSingleQueue(0.1);

            assertTrue(avgTime > 0);
            assertTrue(avgTime < 2.0, "Lambda baixo deve ter tempo baixo");
        }

        @Test
        @DisplayName("Deve lidar com lambda próximo de 1")
        void testLambdaNearOne() {
            double avgTime = exponentialSimulation.simulateSingleQueue(0.999);

            assertTrue(avgTime > 0);
            // Lambda muito alto = sistema quase instável
            assertTrue(avgTime > 50, "Lambda ~1 deve ter tempo muito alto");
        }

        @Test
        @DisplayName("Resultados devem ser consistentes entre execuções")
        void testConsistencyAcrossRuns() {
            Map<Double, SimulationResult> run1 =
                    exponentialSimulation.runAllSimulations();

            QueueSimulation sim2 = new QueueSimulation(false, 12345L);
            Map<Double, SimulationResult> run2 =
                    sim2.runAllSimulations();

            for (double lambda : new double[]{0.5, 0.8, 0.9, 0.99}) {
                double time1 = run1.get(lambda).getSimulation();
                double time2 = run2.get(lambda).getSimulation();

                assertEquals(time1, time2, 0.001,
                        "Mesma seed deve produzir resultados idênticos");
            }
        }
    }
}
