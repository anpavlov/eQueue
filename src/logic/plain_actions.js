import actions from './actions';

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

export function startLoading() {
    return {
        type: actions.start_loading
    }
}

export function loadingCompleted() {
    return {
        type: actions.loading_completed
    }
}

export function queueLoadingCompleted(qid, queue) {
    return {
        type: actions.loading_completed,
        qid,
        queue
    }
}

export function setToken(token) {
    return {
        type: actions.set_token,
        token
    }
}

export function removeToken() {
    return {
        type: actions.remove_token
    }
}