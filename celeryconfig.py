from celery.schedules import crontab

CELERYBEAT_SCHEDULE = {
    'every-minute': {
        'task': 'tasks.update_coefs',
        'schedule': crontab(hour='*/22'),
        # 'args': (1,2),
    },
}