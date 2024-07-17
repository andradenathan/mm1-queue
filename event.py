class Event():
    ARRIVAL = "Arrival"
    DEPARTURE = "Departure"
    
    def __init__(self, type, time) -> None:
        self.type = type
        self.time = time