import pprint
import numpy as np
from typing import List
from event import Event

LAMBDAS = [0.5, 0.8, 0.9, 0.99]
QUEUE_QUANTITY = 100
SIMULATION_TIME = 10000

def generate_interarrival_time(lambd):
    return np.random.exponential(1/lambd)

def compute_service_time(is_constant):
    return 1.0 if is_constant else np.random.exponential(1.0)

def is_queue_empty(customer):
    return customer == 0

def run(is_service_time_const):
    results = {}
    for lambd in LAMBDAS:
        mean_queues = []
        for _ in range(QUEUE_QUANTITY):
            events: List[Event] = []
            mean_per_events = 0
            customers_in_queue = 0
            current_time = 0
            last_event_time = 0
            
            # inicializa a fila com dois eventos (um de chegada, outro de saída) 
            events.append(Event(Event.ARRIVAL, 0))
            
            events.append(
                Event(
                    Event.DEPARTURE, 
                    compute_service_time(is_service_time_const)))
             
            while current_time <= SIMULATION_TIME:
                event = events.pop(0)
                current_time = event.time
                mean_per_events += customers_in_queue * (current_time - last_event_time)
                last_event_time = current_time

                if event.type == Event.ARRIVAL:
                    customers_in_queue += 1

                    events.append(
                        Event(
                            Event.ARRIVAL, 
                            event.time + generate_interarrival_time(lambd)))
                else:
                    customers_in_queue -= 1

                    if is_queue_empty(customers_in_queue):
                        events.append(
                            Event(
                                Event.DEPARTURE, 
                                events[0].time + compute_service_time(is_service_time_const)))
                    else:
                        events.append(
                            Event(
                                Event.DEPARTURE,
                                event.time + compute_service_time(is_service_time_const)))

                events.sort(key = lambda event: event.time)

            # p: pessoa, s: tempo (segundo)
            # essa fração de média está correta: (p*s/s) * 1/p/s = p * s/p = s 
            mean_queues.append(mean_per_events/current_time * 1/lambd)
            
        results[lambd] = {
            "simulation": round(np.mean(mean_queues), 2),
            "response_time": round(1/(1-lambd), 2)
        }
        
    return results

if __name__ == "__main__":
    results_service_time_exponentially_distributed = run(is_service_time_const=False)
    results_service_time_constant = run(is_service_time_const=True)
    pprint.pp(results_service_time_exponentially_distributed)
    print("-" * 5)
    pprint.pp(results_service_time_constant)    