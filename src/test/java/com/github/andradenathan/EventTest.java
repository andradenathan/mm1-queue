package com.github.andradenathan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Event Tests")
public class EventTest {
    @Test
    @DisplayName("Deve criar evento de chegada com tempo correto")
    void testCreateArrivalEvent() {
        Event event = new Event(Event.Type.ARRIVAL, 5.0);

        assertEquals(Event.Type.ARRIVAL, event.getType());
        assertEquals(5.0, event.getTime(), 0.001);
    }

    @Test
    @DisplayName("Deve criar evento de saída com tempo correto")
    void testCreateDepartureEvent() {
        Event event = new Event(Event.Type.DEPARTURE, 10.5);

        assertEquals(Event.Type.DEPARTURE, event.getType());
        assertEquals(10.5, event.getTime(), 0.001);
    }

    @Test
    @DisplayName("Deve comparar eventos por tempo corretamente")
    void testCompareEventsByTime() {
        Event early = new Event(Event.Type.ARRIVAL, 1.0);
        Event late = new Event(Event.Type.DEPARTURE, 5.0);

        assertTrue(early.compareTo(late) < 0, "Evento anterior deve ser menor");
        assertTrue(late.compareTo(early) > 0, "Evento posterior deve ser maior");
    }

    @Test
    @DisplayName("Eventos com mesmo tempo devem ser iguais na comparação")
    void testCompareEventsWithSameTime() {
        Event event1 = new Event(Event.Type.ARRIVAL, 3.0);
        Event event2 = new Event(Event.Type.DEPARTURE, 3.0);

        assertEquals(0, event1.compareTo(event2));
    }

    @Test
    @DisplayName("Eventos devem ordenar corretamente em PriorityQueue")
    void testEventsInPriorityQueue() {
        java.util.PriorityQueue<Event> queue = new java.util.PriorityQueue<>();

        queue.add(new Event(Event.Type.ARRIVAL, 5.0));
        queue.add(new Event(Event.Type.DEPARTURE, 2.0));
        queue.add(new Event(Event.Type.ARRIVAL, 8.0));
        queue.add(new Event(Event.Type.DEPARTURE, 1.0));

        assertEquals(1.0, queue.poll().getTime(), 0.001);
        assertEquals(2.0, queue.poll().getTime(), 0.001);
        assertEquals(5.0, queue.poll().getTime(), 0.001);
        assertEquals(8.0, queue.poll().getTime(), 0.001);
    }

    @Test
    @DisplayName("Deve aceitar tempo zero")
    void testZeroTime() {
        Event event = new Event(Event.Type.ARRIVAL, 0.0);
        assertEquals(0.0, event.getTime(), 0.001);
    }

    @Test
    @DisplayName("Deve aceitar tempos muito grandes")
    void testLargeTime() {
        Event event = new Event(Event.Type.DEPARTURE, 999999.99);
        assertEquals(999999.99, event.getTime(), 0.001);
    }
}
