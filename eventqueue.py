class EventQueue():
    ARRIVAL = "Arrival"
    DEPARTURE = "Departure"
    
    LAMBDAS = [0.5, 0.8, 0.9, 0.99]

    def __init__(self, type, time) -> None:
        self.type = type
        self.time = time
