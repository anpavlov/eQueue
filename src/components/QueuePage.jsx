import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';
import Paper from 'material-ui/Paper';
import RaisedButton from 'material-ui/RaisedButton';

const h2_style  = {
    marginTop: "0",
    paddingTop: "15px"
};

const paper_style  = {
    paddingLeft: "17px",
    paddingRight: "17px",
    paddingBottom: "17px",
    fontFamily: "Roboto, sans-serif"
};

export const QueuePage = React.createClass({
    componentWillMount: function () {
        if (!this.props.queue) {
            this.props.loadQueue(this.props.qid);
        }
    },

    componentWillReceiveProps: function (nextProps) {
        if (this.props.qid != nextProps.qid) {
            this.props.loadQueue(nextProps.qid);
        }
    },

    joinQueue: function () {
        this.props.joinQueue(this.props.queue.qid);
    },

    leaveQueue: function () {
        this.props.leaveQueue(this.props.queue.qid);
    },

    render: function() {
        return (
            <Paper style={paper_style}>
                {(!this.props.queue ?
                    <div style={h2_style}>Queue not found!</div> :
                    <div>
                        <h2 style={h2_style}>{this.props.queue.name}</h2>
                        <p><i>{this.props.queue.description}</i></p>
                        <p>Время ожидания: {this.props.queue.wait_time} мин.</p>
                        {(this.props.queue.in_front == -1 ?
                            <p>В очереди: {this.props.queue.users_quantity} человек</p> :
                            <p>Перед вами: {this.props.queue.in_front} человек</p>
                        )}
                        {(
                            this.props.queue.in_front == -1 ?
                                <RaisedButton primary={true} label="Присоединиться" fullWidth={true} onTouchTap={this.joinQueue}/> :
                                <RaisedButton primary={true} label="Выйти" fullWidth={true} onTouchTap={this.leaveQueue}/>
                        )}
                    </div>
                )}
            </Paper>
        );
    }
});

function mapStateToProps(state, ownProps) {
    console.log('qp map cb');
    return {
        is_loading: state.getIn(['reducer', 'is_loading']),
        queue: state.getIn(['reducer', 'queues', +ownProps.params.qid]),
        qid: +ownProps.params.qid
    }
}

export const QueuePageCon = connect(
    mapStateToProps,
    actionCreators
)(QueuePage);