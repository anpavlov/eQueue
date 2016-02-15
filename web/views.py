from django.shortcuts import render
from django.http import HttpResponse


def home(req):
    return HttpResponse("Hello, world!")
