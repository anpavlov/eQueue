from django.conf.urls import include, url
from web import views

urlpatterns = [
    url(r'^$', views.home, name='home'),
]
