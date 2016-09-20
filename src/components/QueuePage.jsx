import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';

export const QueuePage = React.createClass({
    componentWillMount: function () {
        if (!this.props.queue) {
            this.props.startLoading();
            this.props.loadQueue(this.props.qid);
        }
    },

    componentWillReceiveProps: function (nextProps) {
        if (this.props.qid != nextProps.qid) {
            this.props.startLoading();
            this.props.loadQueue(nextProps.qid);
        }
    },

    callNext: function () {
        this.props.callNext(this.props.queue.qid);
    },

    render: function() {
        return (
            this.props.is_loading ?
                <div>loading ...</div> :
                !this.props.queue ?
                    <div>Queue not found!</div> :
                    <div>
                        <h2>{this.props.queue.name}</h2>
                        <p>{JSON.stringify(this.props.queue)}</p>
                        <input value="Вызвать следующего" onClick={this.callNext} type="button"
                               disabled={this.props.is_loading ? "disabled" : ""}/>
                    </div>
        );
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