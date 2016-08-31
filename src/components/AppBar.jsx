import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../action_creators';
import {Link} from 'react-router';
import paths from '../paths';

export const AppBar = React.createClass({
    // getScreen: function() {
        // console.log("get screen");
        // console.log(this.props);
        // console.log(this.props.screen);
        // return this.props.screen;
    // },
    isMainScreen: function () {
        return this.props.screen == paths.main;
    },

    openById: function () {
        this.props.openPage(paths.by_id);
    },

    render: function() {
        let btn;
        if (this.isMainScreen()) btn = <input type="button" onClick={this.openById} value="find by id!"
                                              disabled={this.props.is_loading ? "disabled" : ""} />;
        return <div>
            <h2>Toolbar</h2>
            <Link to="/">eQueue</Link>
            {btn}
            <hr/>
        </div>;
    }
});

function mapStateToProps(state, ownProps) {
    // console.log("map state");
    // console.log(ownProps);

    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        screen: ownProps.screen
    }
}

export const AppBarCon = connect(
    mapStateToProps,
    actionCreators
)(AppBar);