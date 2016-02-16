from django.contrib.auth.models import User
from django.contrib.sessions.backends.db import SessionStore
from web.models import Queue
import random, string

class DB(object):

    def user_signup(self):
        username = ''
        try:
            username = 'anonym'+ (str(User.objects.last().id+1))
        except:
            username = 'anonym0'
        user = User.objects.create_user(username)
        session = SessionStore()
        session['user_id'] = user.id
        session.save()
        
        data = {
            'code': 200,
            'body': {
                'auth_token': session.session_key
            }
        }

        return data

    def queue_create(self, token):
        s = SessionStore(session_key=token)
        user_id = s['user_id']
        user = User.objects.get(id=user_id)
        queue = Queue(
            author=user,
            alter_id=''.join(random.sample(string.ascii_lowercase,2) + random.sample(string.digits,3))
        )
        queue.save()
        
        data = {
            'code': 200,
            'body': {
                'alter_id': queue.alter_id
            }
        }

        return data

    def queue_update(self, token, alter_id, name = "", description = ""):
        s = SessionStore(session_key=token)
        user_id = s['user_id']
        queue = Queue.objects.get(alter_id=alter_id)

        if queue.author.id == user_id:
            if name:
                queue.name = name
            if description:
                queue.description = description
            queue.save()
        
        data = {
            'code': 200,
            'body': {
                'alter_id': queue.alter_id,
                'name': queue.name,
                'description': queue.description,
            }
        }

        return data
