import React from 'react';
import ReactDOM from 'react-dom';
import {createStore, applyMiddleware, compose} from 'redux';
import { combineReducers } from 'redux-immutable';
import {Provider} from 'react-redux';
import {Router, Route, hashHistory, Redirect} from 'react-router';
import {syncHistoryWithStore, routerReducer, routerMiddleware} from 'react-router-redux';
import thunk from 'redux-thunk';

import reducer from './logic/reducer';
import {AppCon} from './components/App';
import {MainCon} from './components/main/Main';
import {LoginCon} from './components/Login';
import {SignupCon} from './components/Signup';
import {QueuePageCon} from './components/QueuePage';
import {init} from './logic/action_creators';
import paths from './utils/paths';

// var initialState = Map();
// initialState = initialState.set('queues', Map());
const router_history = hashHistory;
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

function requireAuth(nextState, replace) {
    if (!store.getState().getIn(['reducer', 'token'])) {
        replace({
            pathname: paths.login,
            state: { nextPathname: nextState.location.pathname }
        })
    }
}

function requireNotAuth(nextState, replace) {
    if (!!store.getState().getIn(['reducer', 'token'])) {
        replace({
            pathname: paths.main,
        })
    }
}

const routes =
    <Route component={AppCon}>
        <Route path={paths.login} component={LoginCon} onEnter={requireNotAuth}/>
        <Route path={paths.signup} component={SignupCon} onEnter={requireNotAuth}/>
        <Route path={paths.main} component={MainCon} onEnter={requireAuth}/>
        <Route path={paths.create} component={LoginCon} onEnter={requireAuth}/>
        <Route path={paths.queue} component={QueuePageCon} onEnter={requireAuth}/>
        <Redirect from="/admin" to={paths.main} />
    </Route>;

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>{routes}</Router>
    </Provider>,
    document.getElementById('app')
);
