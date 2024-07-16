import numpy as np
from typing import List
from eventqueue import EventQueue

TIME = 0
SIMULATIONS_SIZE = {"N": 100, "T": 10000}

def generate_interarrival_time(lambd):
    return np.random.exponential(1/lambd)

def generate_service_time():
    return np.random.exponential(1.0)

def is_queue_empty(customer):
    return customer == 0

if __name__ == "__main__":
    for lambd in EventQueue.LAMBDAS:
        mean_queues = []
        for _ in range(SIMULATIONS_SIZE["N"]):
            queue: List[EventQueue] = []
            mean = 0
            total_customers = 0    
            TIME = 0
            last_event_time = 0

            queue.append(
                EventQueue(
                    EventQueue.ARRIVAL, 
                    generate_interarrival_time(lambd)))
            
            queue.append(
                EventQueue(
                    EventQueue.DEPARTURE, 
                    generate_service_time()))
            
            while TIME <= SIMULATIONS_SIZE["T"]:
                event = queue.pop(0)
                TIME = event.time
                mean += total_customers * (TIME - last_event_time)
                last_event_time = TIME

                if event.type == EventQueue.ARRIVAL:
                    total_customers += 1

                    queue.append(
                        EventQueue(
                            EventQueue.ARRIVAL, 
                            event.time + generate_interarrival_time(lambd)))
                else:
                    total_customers -= 1

                    if is_queue_empty(total_customers):
                        queue.append(
                            EventQueue(
                                EventQueue.DEPARTURE, 
                                queue[0].time + generate_service_time()))
                    else:
                        queue.append(
                            EventQueue(
                                EventQueue.DEPARTURE,
                                event.time + generate_service_time()))

                queue.sort(key = lambda event: event.time)
                
            mean_queues.append(mean/TIME * 1/lambd)
        print(round(np.mean(mean_queues), 2), round(1/(1-lambd), 2))