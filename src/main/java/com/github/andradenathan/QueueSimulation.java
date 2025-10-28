package com.github.andradenathan;

import java.util.*;

public class QueueSimulation {
    private static final double[] LAMBDAS = {0.5, 0.8, 0.9, 0.99};
    private static final int QUEUE_QUANTITY = 100;
    private static final double SIMULATION_TIME = 10000.0;

    private final boolean isServiceTimeConstant;
    private final Random random;

    public QueueSimulation(boolean isServiceTimeConstant, long seed) {
        this.isServiceTimeConstant = isServiceTimeConstant;
        this.random = new Random(seed);
    }

    public QueueSimulation(boolean isServiceTimeConstant) {
        this(isServiceTimeConstant, System.currentTimeMillis());
    }

    public double simulateSingleQueue(double lambda) {
        Queue queue = new Queue(lambda, isServiceTimeConstant, random);
        PriorityQueue<Event> eventQueue = new PriorityQueue<>();

        eventQueue.add(new Event(Event.Type.ARRIVAL, 0.0));

        eventQueue.add(new Event(Event.Type.DEPARTURE, queue.generateServiceTime()));

        while (queue.getCurrentTime() <= SIMULATION_TIME) {
            if (eventQueue.isEmpty()) {
                break;
            }

            Event event = eventQueue.poll();
            queue.updateStatistics(event.getTime());

            if (event.getType() == Event.Type.ARRIVAL) {
                queue.processArrival();


                double nextArrivalTime = event.getTime() + queue.generateInterarrivalTime();
                eventQueue.add(new Event(Event.Type.ARRIVAL, nextArrivalTime));

            } else {
                queue.processDeparture();

                if (!queue.isEmpty()) {
                    double nextDepartureTime = event.getTime() + queue.generateServiceTime();
                    eventQueue.add(new Event(Event.Type.DEPARTURE, nextDepartureTime));
                } else {
                    Event nextArrival = eventQueue.peek();
                    if (nextArrival != null && nextArrival.getType() == Event.Type.ARRIVAL) {
                        double nextDepartureTime = nextArrival.getTime() + queue.generateServiceTime();
                        eventQueue.add(new Event(Event.Type.DEPARTURE, nextDepartureTime));
                    }
                }
            }
        }

        return queue.getAverageTimeInSystem();
    }

    public Map<Double, SimulationResult> runAllSimulations() {
        Map<Double, SimulationResult> results = new LinkedHashMap<>();

        for (double lambda : LAMBDAS) {
            List<Double> averageTimes = new ArrayList<>();

            for (int i = 0; i < QUEUE_QUANTITY; i++) {
                double avgTime = simulateSingleQueue(lambda);
                averageTimes.add(avgTime);
            }

            double simulationMean = averageTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);

            double theoreticalResponseTime = 1.0 / (1.0 - lambda);

            results.put(lambda, new SimulationResult(simulationMean, theoreticalResponseTime));
        }

        return results;
    }

    public Map<Double, SimulationResult> getResults() {
        return runAllSimulations();
    }
}
