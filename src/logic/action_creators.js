import {List, Map} from 'immutable';
import {push, replace} from 'react-router-redux';
import paths from '../utils/paths';
import {getCookie, setCookie, deleteCookie} from '../utils/cookies';
import * as api from '../utils/api';
import * as plain_actions from './plain_actions';

export function openPage(path) {
    return function (dispatch) {
        dispatch(push(path));
    }
}

export function loadQueue(qid) {
    return function (dispatch) {
        dispatch(plain_actions.startLoading());
        let handleGetQueueSuccess = function (data) {
            dispatch(plain_actions.queueLoadingCompleted(qid, data.body));
            dispatch(push('/queue/' + qid));
        };
        let handleGetQueueFailure = function (data) {
            dispatch(plain_actions.queueLoadingCompleted(qid, undefined));
            alert("Очередь не найдена");
        };
        api.sendRequest(api.getQueuePrepare(getCookie('token'), qid), handleGetQueueSuccess, handleGetQueueFailure);
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
        api.sendRequest(api.joinQueuePrepare(getCookie('token'), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function login(email, pass, cb) {
    return function (dispatch) {
        dispatch(plain_actions.startLoading());
        let handleRequestSuccess = function (data) {
            setCookie("token", data.body.token);
            dispatch(plain_actions.setToken(data.body.token));
            dispatch(plain_actions.loadingCompleted());
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
            setCookie("token", data.body.token);
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

export function logout() {
    return function (dispatch) {
        deleteCookie("token");
        dispatch(plain_actions.removeToken());
        dispatch(push(paths.login));
    }
}

export function replaceUrl(url) {
    return function (dispatch) {
        dispatch(replace(url));
    }
}

export function init() {
    return function (dispatch) {
        let token = getCookie("token");
        if (token) {
            dispatch(plain_actions.setToken(token));
        }

    }
}