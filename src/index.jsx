import React from 'react';
import ReactDOM from 'react-dom';
import {createStore, applyMiddleware, compose} from 'redux';
import { combineReducers } from 'redux-immutable';
import {Provider} from 'react-redux';
import {Router, Route, hashHistory, browserHistory, Redirect} from 'react-router';
import {syncHistoryWithStore, routerReducer, routerMiddleware} from 'react-router-redux';
import thunk from 'redux-thunk';
import io from 'socket.io-client';

import reducer from './logic/reducer';
import {AppCon} from './components/App';
import {MainCon} from './components/main/Main';
import {QueuePageCon} from './components/QueuePage';
import {ByIdCon} from './components/ById';
import {init, updateQueue} from './logic/action_creators';
import {setSocket} from './logic/plain_actions';
import paths from './utils/paths';

import injectTapEventPlugin from "react-tap-event-plugin";

injectTapEventPlugin();

// var initialState = Map();
// initialState = initialState.set('queues', Map());
const router_history = browserHistory;
const router_middleware = routerMiddleware(router_history);
const store = createStore(
    combineReducers({
        reducer,
        routing: routerReducer
    }),
    // initialState,
    compose(applyMiddleware(thunk),
            applyMiddleware(router_middleware),
            window.devToolsExtension ? window.devToolsExtension() : f => f)
);

const history = syncHistoryWithStore(router_history, store, {
    selectLocationState (state) {
        return state.get('routing');
    }
});

store.dispatch(init());

const socket = io('http://equeue.org/');
store.dispatch(setSocket(socket));
socket.on('refresh_info', function (qid) {
    qid = +qid;
    store.dispatch(updateQueue(qid));
});

const routes =
    <Route component={AppCon}>
        <Route path={paths.main} component={MainCon} />
        <Route path={paths.by_id} component={ByIdCon} />
        <Route path={paths.queue} component={QueuePageCon} />
        <Redirect from="/client" to={paths.main} />
    </Route>;

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>{routes}</Router>
    </Provider>,
    document.getElementById('app')
);
