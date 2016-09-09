import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../../action_creators';
// import {Link} from 'react-router';
import paths from '../../paths';
import * as utils from '../../utils';

export const QueueList = React.createClass({
    openQueue: function (qid) {
        return () => {
            this.props.loadQueue(qid);
        }
    },

    render: function() {
        console.log("hi from render");
        return <div>
            {this.props.my_qids.toArray().map(qid => {
                let queue = this.props.queues.get(qid);
                return <div key={queue.qid}>
                    <hr/>
                    <h4>{queue.name}</h4>
                    <p>Осталось {queue.wait_time}</p>
                    <p>id: {queue.qid}</p>
                    <input value="Открыть" onClick={this.openQueue(queue.qid)} type="button"
                           disabled={this.props.is_loading ? "disabled" : ""}/>
                    <hr/>
                </div>;
            })}
        </div>
    }
});

function mapStateToProps(state) {
    console.log();
    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        my_qids: state.getIn(["reducer", 'my_qids']),
        queues: state.getIn(["reducer", 'queues'])
    }
}

export const QueueListCon = connect(
    mapStateToProps,
    actionCreators
)(QueueList);