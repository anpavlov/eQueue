from tornado import websocket, web, ioloop
import json

# clients[<queue_id>] = []
clients = {}


class SocketHandler(websocket.WebSocketHandler):

    def __init__(self, application, request, **kwargs):
        super(SocketHandler, self).__init__(application, request, **kwargs)
        self.qid = -1

    def check_origin(self, origin):
        return True

    def open(self):
        pass
        # if self not in clients:
        #     clients.append(self)

    def on_message(self, message):
        if self.qid != -1:
            return
        try:
            qid = int(message)
            self.qid = qid
            if qid not in clients or not isinstance(clients[qid], list):
                clients[qid] = []

            if self not in clients[qid]:
                clients[qid].append(self)

        except ValueError:
            return

    def on_close(self):
        if self.qid != -1 and self in clients[self.qid]:
            clients[self.qid].remove(self)


class ApiHandler(web.RequestHandler):

    @web.asynchronous
    def get(self, *args):
        self.finish()
        # try:
        qid = self.get_argument("qid")
        qid = int(qid)
        action_type = self.get_argument("type")
        action = self.get_argument("action")
        # except web.MissingArgumentError:
        #     raise web.HTTPError(400)

        # if action_type != "all" "infront" "time"

        data = {"qid": qid, "type": action_type, "action": action}
        j = json.dumps(data)
        if qid in clients and isinstance(clients[qid], list):
            for c in clients[qid]:
                c.write_message(j)

app = web.Application([
    # (r'/', IndexHandler),
    (r'/ws/client/queue', SocketHandler),
    (r'/post/', ApiHandler),
    # (r'/(favicon.ico)', web.StaticFileHandler, {'path': '../'}),
    # (r'/(rest_api_example.png)', web.StaticFileHandler, {'path': './'}),
])

if __name__ == '__main__':
    app.listen(8888)
    ioloop.IOLoop.instance().start()
