import unittest
import network
import random
import string


class ApiTestCase(unittest.TestCase):

    def test_user_create(self):
        res = network.create_user({})
        token = res['body']['token'] or False
        self.assertTrue(token)

    def test_user_create_with_email_pass(self):
        data = {
            'email': gen_email(),
            'password': '123456'
        }
        res = network.create_user(data)
        token = res['body']['token'] or False
        self.assertTrue(token)

    def test_update_gcm(self):
        res = network.create_user({})
        gcm = network.update_gcm({'gcmid': 'eijfrene3536', 'token': res['body']['token']})
        self.assertTrue(gcm['body']['ok'] == 'ok')

    def test_login(self):
        email = gen_email()
        data = {
            'email': email,
            'password': '123456'
        }
        network.create_user(data)
        logged = network.login({'email': email, 'password': '123456'})
        new_token = logged['body']['token'] or False
        self.assertTrue(new_token)

    def test_logout(self):
        email = gen_email()
        data = {
            'email': email,
            'password': '123456'
        }
        network.create_user(data)
        logged = network.login({'email': email, 'password': '123456'})
        logged_out = network.logout({'token': logged['body']['token']})
        self.assertTrue(logged_out['code'] == 200)


def gen_email():
    return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(15)) + '@mail.ru'