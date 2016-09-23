import React from 'react';
import {MyBarCon} from './MyBar';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';
import CircularProgress from 'material-ui/CircularProgress';

const load_style  = {
    zIndex: "9999",
    position: "absolute",
    top: "0px",
    left: "0px",
    right: "0px",
    bottom: "0px",
    backgroundColor: "rgba(0,0,0,0.4)",
    textAlign: "center"
};

const muiTheme = getMuiTheme({
    palette: {
        primary1Color: "#291545",
    }
});

const bar_style = {
};

export const App = React.createClass({
    render: function() {
        return (
            <MuiThemeProvider muiTheme={muiTheme}>
                <div>
                    {this.props.is_loading ?
                        <div style={load_style}><CircularProgress style={bar_style}/></div> : false}
                    <div>
                        <MyBarCon screen={this.props.location.pathname}/>
                        {this.props.children}
                    </div>
                </div>
            </MuiThemeProvider>
        )
    }
});

function mapStateToProps(state, ownProps) {
    return {
        is_loading: state.getIn(['reducer', 'is_loading']),
        screen: ownProps.location.pathname
    }
}

export const AppCon = connect(
    mapStateToProps,
    actionCreators
)(App);