import React from 'react';
import {connect} from 'react-redux';
import * as actionCreators from '../action_creators';
import {Link} from 'react-router';
import * as utils from '../utils';

export const ById = React.createClass({
    handleQidChange: function (e) {
        this.setState({search_qid: e.target.value});
    },

    openQueue: function () {
        this.props.loadQueue(this.state.search_qid);
        // this.props.openPage("/queue/" + this.state.search_qid);
    },

    render: function() {
        return <div>
            <h2>Search by id</h2>
            <input type="text" placeholder="Queue ID" onChange={this.handleQidChange}/>
            <button disabled={this.props.is_loading ? "disabled" : ""} onClick={this.openQueue}>Search</button>
            <hr/>
        </div>;
    }
});

function mapStateToProps(state) {
    // console.log("map state");
    // console.log(state);
    return {
        is_loading: state.getIn(['reducer', 'loading_queue']),
        screen: state.get('screen')
    }
}

export const ByIdCon = connect(
    mapStateToProps,
    actionCreators
)(ById);