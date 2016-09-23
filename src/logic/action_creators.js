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
    return function (dispatch, getState) {
        let token = getState().getIn(['reducer', 'token']);
        dispatch(plain_actions.startLoading());
        let handleGetQueueSuccess = function (data) {
            dispatch(plain_actions.setQueue(qid, data.body));
            dispatch(plain_actions.loadingCompleted());
            dispatch(push('/client/queue/' + qid));
        };
        let handleGetQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            alert("Очередь не найдена");
        };
        api.sendRequest(api.getQueuePrepare(getCookie('token'), qid), handleGetQueueSuccess, handleGetQueueFailure);
    }
}

export function joinQueue(qid) {
    return function (dispatch, getState) {
        dispatch(plain_actions.startLoading());
        let handleJoinQueueSuccess = function (data) {
            let my_qids = getState().getIn(['reducer', 'my_qids']);
            dispatch(plain_actions.setMyQids(my_qids.push(qid)));
            dispatch(loadQueue(qid));
            dispatch(plain_actions.loadingCompleted());
        };
        let handleJoinQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            alert("Не удалось присоединиться");
        };
        api.sendRequest(api.joinQueuePrepare(getCookie('token'), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function leaveQueue(qid) {
    return function (dispatch, getState) {
        dispatch(plain_actions.startLoading());
        let handleJoinQueueSuccess = function (data) {
            let my_qids = getState().getIn(['reducer', 'my_qids']);
            dispatch(plain_actions.setMyQids(my_qids.filter(l_qid => l_qid !== qid)));
            dispatch(loadQueue(qid));
            dispatch(plain_actions.loadingCompleted());
        };
        let handleJoinQueueFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
            alert("Не удалось выйти");
        };
        api.sendRequest(api.leaveQueuePrepare(getCookie('token'), qid), handleJoinQueueSuccess, handleJoinQueueFailure);
    }
}

export function logout() {
    return function (dispatch) {
        deleteCookie("token");
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

export function openById() {
    return function (dispatch) {
        dispatch(push(paths.by_id));
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
        let handleCreateUserSuccess = function (data) {
            setCookie("token", data.body.token);
            dispatch(plain_actions.setToken(data.body.token));
            dispatch(pullMyQueues());
            // api.sendRequest(api.getMyQueuesPrepare(data.body.token), handleGetMyQueuesSuccess);
        };
        let handleRequestFailure = function (data) {
            dispatch(plain_actions.loadingCompleted());
        };
        let token = getCookie("token");
        if (!token) {
            dispatch(plain_actions.startLoading());
            api.sendRequest(api.createUserPrepare(), handleCreateUserSuccess, handleRequestFailure);
        } else {
            handleCreateUserSuccess({ body: { token } });
        }
        //===========
        // let token = getCookie("token");
        // if (token) {
        //     dispatch(plain_actions.setToken(token));
        //     dispatch(pullMyQueues());
        // }

    }
}