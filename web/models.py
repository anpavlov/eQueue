from django.contrib.auth.models import User
from django.db import models
from django.db.models.signals import class_prepared

class Queue(models.Model):
    author = models.ForeignKey(User)
    name = models.CharField(max_length=60)
    description = models.TextField()
    alias = models.CharField(max_length=30, blank=True)
    alter_id = models.CharField(max_length=10, unique=True)

