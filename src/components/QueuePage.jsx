import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../action_creators';

export const QueuePage = React.createClass({
    componentWillMount: function () {
        if (!this.props.queue) {
            this.props.startLoadingQueue();
            this.props.loadQueue(this.props.qid);
        }
    },

    componentWillReceiveProps: function (nextProps) {
        if (this.props.qid != nextProps.qid) {
            this.props.startLoadingQueue();
            this.props.loadQueue(nextProps.qid);
        }
    },

    render: function() {
        return this.props.is_loading ?
            <div>
                loading ...
            </div> : !this.props.queue ?
            <div>
                Queue not found!
            </div> :
            <div>
                <h2>{this.props.queue.name}</h2>
                <p>Time left: {this.props.queue.wait_time}</p>
            </div>;
    }
});

function mapStateToProps(state, ownProps) {
    console.log('qp map cb');
    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        queue: state.getIn(['reducer', 'queues', +ownProps.params.qid]),
        qid: +ownProps.params.qid
    }
}

export const QueuePageCon = connect(
    mapStateToProps,
    actionCreators
)(QueuePage);