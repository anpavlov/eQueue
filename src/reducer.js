import {Map, List, fromJS} from 'immutable';
import actions from './actions';
import {LOCATION_CHANGE} from 'react-router-redux';

function handleSetState(state, newState) {
    console.log("set state");
    console.log(state);
    console.log(newState);
    return state.merge(newState);
}

function handleSetMyQueues(state, queues) {
    let queue_map = state.get('queues');
    let qids = List(queues.map(q => q.id));
    for (let q of queues) {
        queue_map = queue_map.set(q.id, q);
    }
    return state.set('queues', queue_map)
                .set('my_qids', qids);
}

const initialState = fromJS({
    queues: Map(),
    locationBeforeTransitions: null
});

export default function (state = initialState, action) {
    switch (action.type) {
        case actions.set_state:
            return handleSetState(state, action.state);
        case actions.set_my_queues:
            return handleSetMyQueues(state, action.queues);
        case actions.start_loading_queue:
            return state.set('loading_queue', true);
        case actions.queue_loading_completed:
            return state.setIn(['queues', +action.qid], action.queue)
                        .set('loading_queue', false);
        case LOCATION_CHANGE:
            return state.merge({ locationBeforeTransitions: action.payload });
    }
    return state;
}