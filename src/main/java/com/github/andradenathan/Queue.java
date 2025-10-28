package com.github.andradenathan;

import java.util.Random;

public class Queue {
    private final double lambda;
    private final boolean isServiceTimeConstant;
    private final Random random;

    private int customersInQueue;
    private double currentTime;
    private double lastEventTime;
    private double totalWeightedTime;
    private int customersCompleted;
    private double totalTimeInSystem;

    public Queue(double lambda, boolean isServiceTimeConstant, Random random) {
        this.lambda = lambda;
        this.isServiceTimeConstant = isServiceTimeConstant;
        this.random = random;
        this.customersInQueue = 0;
        this.currentTime = 0.0;
        this.lastEventTime = 0.0;
        this.totalWeightedTime = 0.0;
        this.customersCompleted = 0;
        this.totalTimeInSystem = 0.0;
    }

    public double generateInterarrivalTime() {
        return -Math.log(1 - random.nextDouble()) / lambda;
    }

    public double generateServiceTime() {
        if (isServiceTimeConstant) {
            return 1.0;
        }
        return -Math.log(1 - random.nextDouble());
    }

    public void updateStatistics(double newTime) {
        totalWeightedTime += customersInQueue * (newTime - lastEventTime);
        lastEventTime = newTime;
        currentTime = newTime;
    }

    public void processArrival() {
        customersInQueue++;
    }

    public void processDeparture() {
        customersInQueue--;
        customersCompleted++;
    }

    public boolean isEmpty() {
        return customersInQueue == 0;
    }

    public double getCurrentTime() {
        return currentTime;
    }

    public int getCustomersInQueue() {
        return customersInQueue;
    }

    public double getAverageTimeInSystem() {
        if (customersCompleted == 0) {
            return 0.0;
        }

        double averageCustomers = totalWeightedTime / currentTime;
        return averageCustomers / lambda;
    }

    @Override
    public String toString() {
        return String.format("Queue{lambda=%.2f, customers=%d, time=%.2f}",
                lambda, customersInQueue, currentTime);
    }
}
