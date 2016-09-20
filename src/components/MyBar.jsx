import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';
import {Link} from 'react-router';
import paths from '../utils/paths';
import AppBar from 'material-ui/AppBar';
import FlatButton from 'material-ui/FlatButton';

const styles = {
    title: {
        cursor: 'pointer',
    },
};

export const MyBar = React.createClass({
    handleLogout: function () {
        this.props.logout();
    },

    handleTouchTitle: function () {
        this.props.openMain();
    },

    render: function() {
        let app_bar;
        if (this.props.is_logged_in)
            app_bar = (
                <AppBar
                    showMenuIconButton={false}
                    title={<span style={styles.title}>eQueue</span>}
                    onTitleTouchTap={this.handleTouchTitle}
                    iconElementRight={<FlatButton label="Logout" onTouchTap={this.handleLogout}/>}
                />
            );
        else
            app_bar = (
                <AppBar
                    showMenuIconButton={false}
                    title={<span style={styles.title}>eQueue</span>}
                    onTitleTouchTap={this.handleTouchTitle}
                />
            );
        return app_bar;
    }
});

{/*<Link to="/admin">eQueue</Link>*/}
{/*{this.props.is_logged_in ?*/}
{/*<a href="#" onClick={this.handleLogout}>Logout</a> :*/}
{/*false*/}
{/*}*/}

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