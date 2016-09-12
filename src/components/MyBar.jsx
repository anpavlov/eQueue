import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../logic/action_creators';
import {Link} from 'react-router';
import paths from '../utils/paths';
import AppBar from 'react-toolbox/lib/app_bar';

export const MyBar = React.createClass({
    handleLogout: function () {
        this.props.logout();
    },

    render: function() {
        // let btn;
        // if (this.isMainScreen()) btn = <input type="button" onClick={this.openById} value="find by id!"
        //                                       disabled={this.props.is_loading ? "disabled" : ""} />;
        return (
            <AppBar >
                <Link to="/admin">eQueue</Link>
                {this.props.is_logged_in ?
                    <a href="#" onClick={this.handleLogout}>Logout</a> :
                    false
                }
            </AppBar>
        );
    }
});

function mapStateToProps(state, ownProps) {
    // console.log("map state");
    // console.log(ownProps);

    return {
        is_logged_in: !!state.getIn(['reducer', 'token'])
    }
}

export const MyBarCon = connect(
    mapStateToProps,
    actionCreators
)(MyBar);