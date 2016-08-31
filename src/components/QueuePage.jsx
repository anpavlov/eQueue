import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../action_creators';

export const QueuePage = React.createClass({
    render: function() {
        return this.props.is_loading ?
            <div>
                loading ...
            </div> :
            <div>
                <h2>{this.props.queue.name}</h2>
                <p>Time left: {this.props.queue.wait_time}</p>
            </div>;
    }
});

function mapStateToProps(state, ownProps) {
    console.log('map cb');
    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        queue: state.getIn(['reducer', 'queues', +ownProps.params.qid])
    }
}

export const QueuePageCon = connect(
    mapStateToProps,
    actionCreators
)(QueuePage);