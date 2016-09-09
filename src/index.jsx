import React from 'react';
import ReactDOM from 'react-dom';
import {createStore, applyMiddleware, compose} from 'redux';
import { combineReducers } from 'redux-immutable';
import {Provider} from 'react-redux';
import {Map} from 'immutable';
import reducer from './reducer';
import {Router, Route, hashHistory} from 'react-router';
import {syncHistoryWithStore, routerReducer, routerMiddleware} from 'react-router-redux';
import thunk from 'redux-thunk';
// import { syncHistoryWithStore, routerReducer } from 'react-router-redux'
import App from './components/App';
import {MainCon} from './components/main/Main';
import {ByIdCon} from './components/ById';
import {QueuePageCon} from './components/QueuePage';
import {setMyQueues, init} from './action_creators';
import paths from './paths';

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
// store.dispatch(setMyQueues([{id: 2, name: "Adolf", time: "14 минут"}, {id: 6, name: "Pupes", time: "48 минут"}]));

// const history = syncHistoryWithStore(hashHistory, store, {
//     selectLocationState: state => state.get("routing")
// });
// store.dispatch(setState({ screen: 'main' }));

const routes =
    <Route component={App}>
        <Route path={paths.main} component={MainCon}/>
        <Route path={paths.by_id} component={ByIdCon}/>
        <Route path={paths.queue_page + ":qid"} component={QueuePageCon}/>
    </Route>;

ReactDOM.render(
    <Provider store={store}>
        <Router history={history}>{routes}</Router>
    </Provider>,
    document.getElementById('app')
);
