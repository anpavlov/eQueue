import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../../logic/all_actions';
import {Card, CardActions, CardHeader} from 'material-ui/Card';
import FlatButton from 'material-ui/FlatButton';
import Paper from 'material-ui/Paper';
import EnhancedButton from 'material-ui/internal/EnhancedButton';

const card_style = {
    margin: "5px"
};

const btn_style = {
    width: "100%"
};

const inbtn_style = {
    textAlign: "left",
    padding: "5px",
    paddingLeft: "15px"
};

const desc_style = {
    fontSize: "smaller",
    color: "rgba(0,0,0,0.54)"
};

export const QueueList = React.createClass({
    openQueue: function (qid) {
        return () => {
            this.props.loadQueue(qid);
        }
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
                        <Paper key={queue.qid} style={card_style}>
                            <EnhancedButton style={btn_style} onTouchTap={this.openQueue(queue.qid)}>
                                <div style={inbtn_style}>
                                    <p>{queue.name}</p>
                                    <p style={desc_style}>{queue.description}</p>
                                </div>
                            </EnhancedButton>
                        </Paper>
                    );
                }) :
                false
                }
        </div>
    }
});

{/*<div key={queue.qid}>*/}
    {/*<hr/>*/}
    {/*<h4>{queue.name}</h4>*/}
    {/*<p>id: {queue.qid}</p>*/}
    {/*<input value="Открыть" onClick={this.openQueue(queue.qid)} type="button"*/}
           {/*disabled={this.props.is_loading ? "disabled" : ""}/>*/}
    {/*<hr/>*/}
{/*</div>*/}

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