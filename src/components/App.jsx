import React from 'react';
import {MyBarCon} from './MyBar';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';

export const App = React.createClass({
    render: function() {
        return (
            <MuiThemeProvider>
                <div>
                    {this.props.is_loading ?
                        <h1>Loading ...</h1> : false}
                    <div>
                        <MyBarCon/>
                        {this.props.children}
                    </div>
                </div>
            </MuiThemeProvider>
        )
    }
});

function mapStateToProps(state) {
    return {
        is_logged_in: state.getIn(['reducer', 'token']) != undefined,
        is_loading: state.getIn(['reducer', 'is_loading'])
    }
}

export const AppCon = connect(
    mapStateToProps,
    actionCreators
)(App);