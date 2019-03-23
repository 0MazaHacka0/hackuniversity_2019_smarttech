from rest_framework import viewsets

from . import models
from . import serializers


class BusesViewSet(viewsets.ModelViewSet):
    queryset = models.Buses.objects.all()
    serializer_class = serializers.BusesSerializer


class ClientsViewSet(viewsets.ModelViewSet):
    queryset = models.Clients.objects.all()
    serializer_class = serializers.ClientsSerializer


class BindingsViewSet(viewsets.ModelViewSet):
    queryset = models.Bindings.objects.all()
    serializer_class = serializers.BindingsSerializer
