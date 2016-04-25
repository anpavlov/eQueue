from tornado import websocket, web, ioloop
import json

# clients[<queue_id>] = []
clients = {}


class SocketHandler(websocket.WebSocketHandler):

    def __init__(self, application, request, **kwargs):
        super(SocketHandler, self).__init__(application, request, **kwargs)
        self.qids = []

    def check_origin(self, origin):
        return True

    def open(self):
        pass
        # if self not in clients:
        #     clients.append(self)

    def on_message(self, message):
        try:
            qid = int(message)
            if qid not in self.qids:
                self.qids.append(qid)
                if qid not in clients or not isinstance(clients[qid], list):
                    clients[qid] = []

                if self not in clients[qid]:
                    clients[qid].append(self)

        except ValueError:
            return

    def on_close(self):
        for qid in self.qids:
            if self in clients[qid]:
                clients[qid].remove(self)


class ApiHandler(web.RequestHandler):

    @web.asynchronous
    def post(self):
        self.finish()
        qid = self.get_argument("qid")
        qid = int(qid)
        in_front = self.get_argument("in_front")
        users_quantity = self.get_argument("users_quantity")
        wait_time = self.get_argument("wait_time")

        data = {"qid": qid, "in_front": in_front, "users_quantity": users_quantity, "wait_time": wait_time}
        j = json.dumps(data)
        if qid in clients and isinstance(clients[qid], list):
            for c in clients[qid]:
                c.write_message(j)

app = web.Application([
    (r'/ws/client/queue', SocketHandler),
    (r'/post/', ApiHandler),
])

if __name__ == '__main__':
    app.listen(8888)
    ioloop.IOLoop.instance().start()