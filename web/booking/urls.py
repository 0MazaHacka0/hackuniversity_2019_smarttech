from rest_framework.routers import DefaultRouter

from . import views

router = DefaultRouter()
router.register('buses', views.BusesViewSet, basename='bus')
router.register('clients', views.ClientsViewSet, basename='client')
router.register('bindings', views.BindingsViewSet, basename='bindings')

urlpatterns = router.urls
