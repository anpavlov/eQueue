import {List, Map} from 'immutable';
import actions from './actions';
import {push} from 'react-router-redux';
import fetch from 'isomorphic-fetch';
import {getCookie, setCookie} from './cookies';

export function setState(state) {
    return {
        type: actions.set_state,
        state
    }
}

export function setMyQueues(queue_list) {
    return {
        type: actions.set_my_queues,
        queues: queue_list
    }
}

export function openPage(path) {
    return function (dispatch) {
        dispatch(push(path));
    }
}

export function startLoadingQueue() {
    return {
        type: actions.start_loading_queue
    }
}

export function loadingCompleted() {
    return {
        type: actions.queue_loading_completed
    }
}

export function queueLoadingCompleted(qid, queue) {
    return {
        type: actions.queue_loading_completed,
        qid,
        queue
    }
}

export function loadQueue(qid) {
    return function (dispatch) {
        dispatch(startLoadingQueue());
        let handleGetQueueSuccess = function (data) {
            dispatch(queueLoadingCompleted(qid, data.body));
            dispatch(push('/client/queue/' + qid));
        };
        let handleGetQueueFailure = function (data) {
            dispatch(queueLoadingCompleted(qid, undefined));
            alert("Очередь не найдена");
        };
        sendRequest(getQueuePrepare(getCookie('token'), qid), handleGetQueueSuccess, handleGetQueueFailure);
    }
}

export function joinQueue(qid) {
    return function (dispatch) {
        dispatch(startLoadingQueue());
        let handleJoinQueueSuccess = function (data) {
            dispatch(loadingCompleted());
            dispatch(loadQueue(qid));
        };
        let handleJoinQueueFailure = function (data) {
            dispatch(loadingCompleted());
            alert("Не удалось присоединиться");
        };
        sendRequest(joinQueuePrepare(getCookie('token'), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function setToken(token) {
    return {
        type: actions.set_token,
        token
    }
}

function getMyQueuesPrepare(token) {
    let fd = new FormData();
    fd.set('token', token);
    return new Request(
        'http://p30280.lab1.stud.tech-mail.ru/api/queue/in-queue/',
        { method: 'POST', body: fd});
}

function getQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        'http://p30280.lab1.stud.tech-mail.ru/api/queue/info-user/',
        { method: 'POST', body: fd});
}

function joinQueuePrepare(token, qid) {
    let fd = new FormData();
    fd.set('token', token);
    fd.set('qid', qid);
    return new Request(
        'http://p30280.lab1.stud.tech-mail.ru/api/queue/join/',
        { method: 'POST', body: fd});
}

function createUserPrepare() {
    return new Request(
        'http://p30280.lab1.stud.tech-mail.ru/api/user/create/',
        { method: 'POST' });
}

export function init() {
    return function (dispatch) {
        let handleGetMyQueuesSuccess = function (data) {
            dispatch(setMyQueues(data.body.queues));
        };
        let handleCreateUserSuccess = function (data) {
            setCookie("token", data.body.token);
            dispatch(setToken(data.body.token));
            sendRequest(getMyQueuesPrepare(data.body.token), handleGetMyQueuesSuccess, handleCommonError);
        };
        let token = getCookie("token");
        if (!token) {
            sendRequest(createUserPrepare(), handleCreateUserSuccess, handleCommonError);
        } else {
            handleCreateUserSuccess({ body: { token } });
        }

    }
}

function handleCommonError(error) {
    alert(error);
}

function checkStatus(response) {
    if (response.status >= 200 && response.status < 300) {
        return response;
    } else {
        var error = new Error(response.statusText);
        error.response = response;
        throw error;
    }
}

function sendRequest(request, handleJson, handleError) {
    fetch(request)
        .then(checkStatus)
        .then(response => response.json(), handleError)
        .then(function (data) {
            if (data.code >= 200 && data.code < 300) {
                handleJson(data);
            } else {
                handleError('Json code is not OK');
            }
        }, handleError);
}