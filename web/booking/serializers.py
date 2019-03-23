from rest_framework.serializers import ModelSerializer
from . import models


class BusesSerializer(ModelSerializer):
    class Meta:
        model = models.Buses
        fields = (
            'bus_id', 'gosnum'
        )


class ClientsSerializer(ModelSerializer):
    class Meta:
        model = models.Clients
        fields = (
            'client_id', 'disability_card', 'approved'
        )


class BindingsSerializer(ModelSerializer):
    class Meta:
        model = models.Bindings
        fields = (
            'bus_id', 'client_id', 'start_point', 'end_point'
        )
