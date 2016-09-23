import fetch from 'isomorphic-fetch';

const domain = "http://equeue.org";

export function getMyQueuesPrepare(token) {
    let fd = new FormData();
    fd.set('token', token);
    return new Request(
        domain + '/api/queue/in-queue/',
        { method: 'POST', body: fd});
}

export function getQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        domain + '/api/queue/info-user/',
        { method: 'POST', body: fd});
}

export function joinQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        domain + '/api/queue/join/',
        { method: 'POST', body: fd});
}

export function leaveQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        domain + '/api/queue/leave/',
        { method: 'POST', body: fd});
}

export function createUserPrepare() {
    return new Request(
        domain + '/api/user/create/',
        { method: 'POST' });
}

export function loginPrepare(email, pass) {
    let fd = new FormData();
    fd.set('email', email);
    fd.set('password', pass);
    return new Request(
        domain + '/api/user/login/',
        { method: 'POST', body: fd });
}

export function signupPrepare(email, pass) {
    let fd = new FormData();
    fd.set('email', email);
    fd.set('password', pass);
    return new Request(
        domain + '/api/user/create/',
        { method: 'POST', body: fd });
}

export function createQueuePrepare(name, desc, token) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('name', name);
    fd.set('description', desc);
    return new Request(
        domain + '/api/queue/create/',
        { method: 'POST', body: fd });
}

export function callNextPrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        domain + '/api/queue/call/',
        { method: 'POST', body: fd });
}

export function handleCommonError(error) {
    alert(error);
}

export function checkStatus(response) {
    if (response.status >= 200 && response.status < 300) {
        return response;
    } else {
        var error = new Error(response.statusText);
        error.response = response;
        throw error;
    }
}

export function sendRequest(request, handleJson, handleError = handleCommonError) {
    fetch(request)
        .then(checkStatus)
        .then(response => response.json(), handleError)
        .then(function (data) {
            if (data.code >= 200 && data.code < 300) {
                handleJson(data);
            } else {
                handleError(data);
            }
        }, handleError);
}
