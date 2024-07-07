from heapq import heapify, heappop, heappush
from numpy import random

LAMBDAS = [0.5, 0.8, 0.9, 0.99]
SIMULATIONS_SIZE = {"N": 100, "T": 10.000}

def create_customers(lambd):
    interarrival_time = random.exponential(lambd)
    service_time = random.exponential(1.0)
    return (interarrival_time, service_time)

def create_queues():
    raise NotImplementedError("Queue creation not implemented yet")
    

def simulation():
    # 1. Create priority queue
    # 2. Such a queue stores the times of all pending events, such as the next time a customer will arive
    # 3. The event with the smallest time is at the front of the queue
    raise NotImplementedError("Simulation not implemented yet")
    

if __name__ == "__main__":
    queues = []
    for lambd in LAMBDAS:
        customer = create_customers(lambd)

        heappush(queues, customer)

    print(queues)