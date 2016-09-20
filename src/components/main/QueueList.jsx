import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../../logic/all_actions';

export const QueueList = React.createClass({
    openQueue: function (qid) {
        return () => {
            this.props.loadQueue(qid);
        }
    },

    openCreate: function () {
        this.props.openCreate();
    },

    isQueuesPresent: function () {
        return (!!this.props.my_qids && !this.props.my_qids.isEmpty());
    },

    render: function() {
        console.log("hi from render");
        return <div>
            {this.isQueuesPresent() ?
                this.props.my_qids.toArray().map(qid => {
                    let queue = this.props.queues.get(qid);
                    if (!queue)
                        return false;
                    return (
                        <div key={queue.qid}>
                            <hr/>
                            <h4>{queue.name}</h4>
                            <p>id: {queue.qid}</p>
                            <input value="Открыть" onClick={this.openQueue(queue.qid)} type="button"
                                   disabled={this.props.is_loading ? "disabled" : ""}/>
                            <hr/>
                        </div>
                    );
                }) :
                false
                }
            <div>
                <input value="Создать" onClick={this.openCreate} type="button"
                       disabled={this.props.is_loading ? "disabled" : ""}/>
                <hr/>
            </div>
        </div>
    }
});

function mapStateToProps(state) {
    console.log();
    return {
        is_loading: state.getIn(['reducer', 'is_loading']),
        my_qids: state.getIn(["reducer", 'my_qids']),
        queues: state.getIn(["reducer", 'queues'])
    }
}

export const QueueListCon = connect(
    mapStateToProps,
    actionCreators
)(QueueList);