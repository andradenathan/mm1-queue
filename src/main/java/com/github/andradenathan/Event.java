package com.github.andradenathan;

public class Event implements Comparable<Event> {
    public enum Type {
        ARRIVAL,
        DEPARTURE
    }

    private final Type type;
    private final double time;

    public Event(Type type, double time) {
        this.type = type;
        this.time = time;
    }

    public double getTime() {
        return time;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int compareTo(Event other) {
        return Double.compare(this.time, other.time);
    }

    @Override
    public String toString() {
        return String.format("Event{type=%s, time=%.4f}", type, time);
    }
}
