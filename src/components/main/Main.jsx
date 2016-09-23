import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../../logic/all_actions';
import {EmptyListCon} from './EmptyList';
import {QueueListCon} from './QueueList';

export const Main = React.createClass({
    isQueuesPresent: function () {
        return (!!this.props.my_qids && !this.props.my_qids.isEmpty());
    },

    render: function() {
        // console.log("hi from render");
        return this.isQueuesPresent() ?
            <QueueListCon /> :
            <EmptyListCon/>
    }
});

function mapStateToProps(state) {
    console.log();
    return {
        my_qids: state.getIn(["reducer", 'my_qids'])
    }
}

export const MainCon = connect(
    mapStateToProps,
    actionCreators
)(Main);