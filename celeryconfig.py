from celery.schedules import crontab

CELERYBEAT_SCHEDULE = {
    'every-minute': {
        'task': 'tasks.update_coefs',
        'schedule': crontab(minute='*/1'),
        # 'args': (1,2),
    },
}