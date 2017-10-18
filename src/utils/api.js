import fetch from 'isomorphic-fetch';

const domain = "http://equeue.org";

export function getMyQueuesPrepare(token) {
    let fd = new FormData();
    fd.append('token', token);
    return new Request(
        domain + '/api/queue/my/',
        { method: 'POST', body: fd});
}

export function getQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.append('token', token);
    fd.append('qid', qid);
    return new Request(
        domain + '/api/queue/info-admin/',
        { method: 'POST', body: fd});
}

export function joinQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.append('token', token);
    fd.append('qid', qid);
    return new Request(
        domain + '/api/queue/join/',
        { method: 'POST', body: fd});
}

export function createUserPrepare() {
    return new Request(
        domain + '/api/user/create/',
        { method: 'POST' });
}

export function loginPrepare(email, pass) {
    let fd = new FormData();
    fd.append('email', email);
    fd.append('password', pass);
    return new Request(
        domain + '/api/user/login/',
        { method: 'POST', body: fd });
}

export function signupPrepare(email, pass) {
    let fd = new FormData();
    fd.append('email', email);
    fd.append('password', pass);
    return new Request(
        domain + '/api/user/create/',
        { method: 'POST', body: fd });
}

export function createQueuePrepare(name, desc, token) {
    let fd = new FormData();
    fd.append('token', token);
    fd.append('name', name);
    fd.append('description', desc);
    return new Request(
        domain + '/api/queue/create/',
        { method: 'POST', body: fd });
}

export function callNextPrepare(token, qid) {
    let fd = new FormData();
    fd.append('token', token);
    fd.append('qid', qid);
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