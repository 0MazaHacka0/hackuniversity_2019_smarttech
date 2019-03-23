from django.db import models


class Buses(models.Model):
    bus_id = models.UUIDField(unique=True, blank=False)
    gosnum = models.CharField(max_length=9)


class Clients(models.Model):
    client_id = models.UUIDField(unique=True, blank=False)
    disability_card = models.CharField(max_length=60)
    approved = models.BooleanField()


class Bindings(models.Model):
    bus_id = models.ForeignKey(Buses, on_delete=models.CASCADE)
    client_id = models.ForeignKey(Clients, on_delete=models.CASCADE)
    start_point = models.TextField()
    end_point = models.TextField()
