package com.github.andradenathan;

import java.util.Map;

public class SimulationResult {
    private final double simulation;
    private final double theoretical;

    public SimulationResult(double simulation, double theoretical) {
        this.simulation = simulation;
        this.theoretical = theoretical;
    }

    public double getSimulation() {
        return simulation;
    }

    public double getTheoretical() {
        return theoretical;
    }

    public double getError() {
        return Math.abs(simulation - theoretical) / theoretical * 100;
    }

    @Override
    public String toString() {
        return String.format("{simulation: %.2f, theoretical: %.2f, error: %.2f%%}",
                simulation, theoretical, getError());
    }

    public static void printResults(Map<Double, SimulationResult> results, String serviceType) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Service Time: " + serviceType);
        System.out.println("=".repeat(80));
        System.out.printf("%-10s %-15s %-15s %-15s%n",
                "Lambda", "Simulation", "Theoretical", "Error %");
        System.out.println("-".repeat(80));

        for (Map.Entry<Double, SimulationResult> entry : results.entrySet()) {
            double lambda = entry.getKey();
            SimulationResult result = entry.getValue();
            System.out.printf("%-10.2f %-15.2f %-15.2f %-15.2f%n",
                    lambda,
                    result.getSimulation(),
                    result.getTheoretical(),
                    result.getError());
        }
        System.out.println("=".repeat(80));
    }
}
