import {List, Map} from 'immutable';
import {push, replace} from 'react-router-redux';
import paths from '../utils/paths';
import {getCookie, setCookie, deleteCookie} from '../utils/cookies';
import * as api from '../utils/api';
import * as plain_actions from './plain_actions';

const cookie_token = "admintoken";

export function openPage(path) {
    return function (dispatch) {
        dispatch(push(path));
    }
}

export function loadQueue(qid) {
    return function (dispatch, getState) {
        let token = getState().getIn(['reducer', 'token']);
        dispatch(plain_actions.startLoading());
        let handleGetQueueSuccess = function (data) {
            dispatch(plain_actions.setQueue(qid, data.body));
            dispatch(plain_actions.loadingCompleted());
            dispatch(push('/admin/queue/' + qid));
        };
        let handleGetQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            alert("Очередь не найдена");
        };
        api.sendRequest(api.getQueuePrepare(getCookie(cookie_token), qid), handleGetQueueSuccess, handleGetQueueFailure);
    }
}

export function joinQueue(qid) {
    return function (dispatch) {
        dispatch(plain_actions.startLoading());
        let handleJoinQueueSuccess = function (data) {
            dispatch(plain_actions.loadingCompleted());
            dispatch(plain_actions.loadQueue(qid));
        };
        let handleJoinQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            alert("Не удалось присоединиться");
        };
        api.sendRequest(api.joinQueuePrepare(getCookie(cookie_token), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function callNext(qid) {
    return function (dispatch, getState) {
        let token = getState().getIn(['reducer', 'token']);
        dispatch(plain_actions.startLoading());
        let handleJoinQueueSuccess = function (data) {
            dispatch(loadQueue(qid));
        };
        let handleJoinQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            if (data.code == 400)
                alert("Очередь пуста");
            else
                alert("Неизвестная ошибка");
        };
        api.sendRequest(api.callNextPrepare(getCookie(cookie_token), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function login(email, pass, cb) {
    return function (dispatch) {
        dispatch(plain_actions.startLoading());
        let handleRequestSuccess = function (data) {
            setCookie(cookie_token, data.body.token);
            dispatch(plain_actions.setToken(data.body.token));
            dispatch(plain_actions.loadingCompleted());
            dispatch(pullMyQueues());
            cb(true);
        };
        let handleRequestFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            cb(false);
        };
        api.sendRequest(api.loginPrepare(email, pass), handleRequestSuccess, handleRequestFailure);
    }
}

export function signup(email, pass, cb) {
    return function (dispatch) {
        dispatch(plain_actions.startLoading());
        let handleRequestSuccess = function (data) {
            setCookie(cookie_token, data.body.token);
            dispatch(plain_actions.setToken(data.body.token));
            dispatch(plain_actions.loadingCompleted());
            cb(0);
            dispatch(push(paths.main));
        };
        let handleRequestFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            if (+data.code == 403)
                cb(1);
            else
                cb(-1);
        };
        api.sendRequest(api.signupPrepare(email, pass), handleRequestSuccess, handleRequestFailure);
    }
}

export function createQueue(email, pass) {
    return function (dispatch, getState) {
        let token = getState().getIn(['reducer', 'token']);
        dispatch(plain_actions.startLoading());
        let handleRequestSuccess = function (data) {
            let my_qids = getState().getIn(['reducer', 'my_qids']);
            my_qids = my_qids.push(+data.body.qid);
            dispatch(plain_actions.setMyQids(my_qids));
            dispatch(loadQueue(+data.body.qid));
        };
        let handleRequestFailure = function (data) {
            console.log("Problems here!");
            dispatch(plain_actions.loadingCompleted());
        };
        api.sendRequest(api.createQueuePrepare(email, pass, token), handleRequestSuccess, handleRequestFailure);
    }
}

export function logout() {
    return function (dispatch) {
        deleteCookie(cookie_token);
        dispatch(plain_actions.removeToken());
        dispatch(push(paths.login));
    }
}

export function openCreate() {
    return function (dispatch) {
        dispatch(push(paths.create));
    }
}

export function openMain() {
    return function (dispatch) {
        dispatch(push(paths.main));
    }
}

export function replaceUrl(url) {
    return function (dispatch) {
        dispatch(replace(url));
    }
}

export function pullMyQueues() {
    return function (dispatch, getState) {
        let token = getState().getIn(['reducer', 'token']);
        dispatch(plain_actions.startLoading());
        let handleRequestSuccess = function (data) {
            let my_qids = List(data.body.queues.map(q => q.qid));
            dispatch(plain_actions.setMyQids(my_qids));
            data.body.queues.forEach(q => dispatch(plain_actions.setQueue(q.qid, q)));
            dispatch(plain_actions.loadingCompleted());
        };
        let handleRequestFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
        };
        api.sendRequest(api.getMyQueuesPrepare(token), handleRequestSuccess, handleRequestFailure);
    }
}

export function init() {
    return function (dispatch) {
        let token = getCookie(cookie_token);
        if (token) {
            dispatch(plain_actions.setToken(token));
            dispatch(pullMyQueues());
        }

    }
}
