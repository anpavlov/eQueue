var app = require('http').createServer(handler)
var io = require('socket.io')(app);
var fs = require('fs');

var sockets = {};

var groups = {};

app.listen(30213);

function handler (req, res) {
  fs.readFile(__dirname + '/index.html',
  function (err, data) {
    if (err) {
      res.writeHead(500);
      return res.end('Error loading index.html');
    }

    res.writeHead(200);
    res.end(data);
  });
}

io.on('connection', function (socket) {

  socket.queues = new Set();
  sockets[socket.id] = socket;
  console.log('user connected:' + socket.id);

  socket.on('apiserver', function (qid) {
      group = groups[qid] !== undefined ? groups[qid] : new Set();
      group.forEach( socket => sockets[socket].emit('refresh_info', qid));

      console.log('send data to ' + group.length + ' users from queue:' + qid)
  });

  socket.on('bindclient', function (qid) {
      socket.queues.add(qid);
      group =  groups[qid] !== undefined ? groups[qid] : new Set();
      group.add(this.id);
      groups[qid] = group;

      console.log('bind client "' + this.id  +  '" to queue: ' + qid + ', now: ' + group.size);
  });

  socket.on('disconnect', function() {
      socket.queues.forEach( qid => { 
          groups[qid].delete(socket.id);
          console.log('unbind client "' + socket.id  +  '" to queue: ' + qid + ', now: ' + groups[qid].size);
        }
      );

      delete sockets[socket.id]
      console.log('user disconnected:' + socket.id);
  });

});


// setInterval(function() {
//     console.log('Send Data:')
//     for (var socket_id in sockets) {
//         socket = sockets[socket_id];
//         socket.emit('news', { hello: 'world' });
//         console.log('send data to ' + socket.id)
//     }
// }, 10000);

