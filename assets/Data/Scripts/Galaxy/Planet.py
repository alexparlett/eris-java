from org.homonoia.eris.ecs import ScriptComponent

class Planet(ScriptComponent):
    def __init__(self):
        self.empire = None
        self.population = 0
        self.buildings = []
        pass

    def colonize(self,empire,population):
        pass

    def update(self, delta):
        pass
