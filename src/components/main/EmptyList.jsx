import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../../logic/all_actions';
import Paper from 'material-ui/Paper';
import RaisedButton from 'material-ui/RaisedButton';
// import {Link} from 'react-router';
import paths from '../../utils/paths';

const h2_style  = {
    marginTop: "0",
    paddingTop: "15px"
};

const paper_style  = {
    paddingLeft: "17px",
    paddingRight: "17px",
    paddingBottom: "17px",
    fontFamily: "Roboto, sans-serif",
    textAlign: "center",
    height: "100%"
};


export const EmptyList = React.createClass({
    render: function() {
        // console.log("hi from render");
        return <Paper style={paper_style}>
                    <h2 style={h2_style}>Вы не стоите ни в одной очереди</h2>
                    <RaisedButton primary={true} label="Найти по ID" fullWidth={true} onTouchTap={this.props.openById}/>
                </Paper>
    }
});

export const EmptyListCon = connect(
    undefined,
    actionCreators
)(EmptyList);