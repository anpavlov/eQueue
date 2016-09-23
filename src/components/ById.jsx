import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';
import Paper from 'material-ui/Paper';
import RaisedButton from 'material-ui/RaisedButton';
import TextField from 'material-ui/TextField';
// import {Link} from 'react-router';
// import * as utils from '../utils';

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

const error_style  = {
    color: "red"
};

export const ById = React.createClass({
    getInitialState: function() {
        return {
            nan: false,
            qid: undefined,
        }
    },

    handleChange : function (event) {
        this.setState({ qid: event.target.value })
    },

    openQueue: function () {
        this.setState({ nan: false });
        const qid = +this.state.qid;
        if (qid === parseInt(qid, 10) && qid >= 0)
            this.props.loadQueue(qid);
        else
            return this.setState({ nan: true });
    },

    render: function() {
        let txtProps = {
            onChange: this.handleChange,
            hintText: "ID очереди"
        };
        if (this.state.nan)
            txtProps.errorText = "ID должно быть положительным числом";

        return <Paper style={paper_style}>
            <h2 style={h2_style}>Поиск по ID очереди</h2>
            <TextField {...txtProps}/>
            <RaisedButton primary={true} label="Открыть" fullWidth={true} onTouchTap={this.openQueue}/>
        </Paper>;
    }
});

export const ByIdCon = connect(
    undefined,
    actionCreators
)(ById);