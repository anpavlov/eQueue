import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../logic/action_creators';

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

    joinQueue: function () {
        this.props.joinQueue(this.props.queue.qid);
    },

    render: function() {
        return (
            this.props.is_loading ?
                <div>loading ...</div> :
                !this.props.queue ?
                    <div>Queue not found!</div> :
                    <div>
                        <h2>{this.props.queue.name}</h2>
                        <p>Time left: {this.props.queue.wait_time}</p>
                        {(
                            this.props.queue.in_front == -1 ?
                                <input value="Присоединиться" onClick={this.joinQueue} type="button"
                                       disabled={this.props.is_loading ? "disabled" : ""}/> :
                                false
                        )}
                        <p>{JSON.stringify(this.props.queue)}</p>
                    </div>
        );
    }
});

function mapStateToProps(state, ownProps) {
    console.log('qp map cb');
    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        is_loading_sub: state.getIn(['reducer', 'loading_sub']),
        queue: state.getIn(['reducer', 'queues', +ownProps.params.qid]),
        qid: +ownProps.params.qid
    }
}

export const QueuePageCon = connect(
    mapStateToProps,
    actionCreators
)(QueuePage);