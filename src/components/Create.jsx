import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';

export const Create = React.createClass({
    getInitialState: function() {
        return {
            empty_name: false
        }
    },

    handleSubmit: function(event) {
        event.preventDefault();

        this.setState({ empty_name: false });

        const name = this.refs.name.value;
        const desc = this.refs.desc.value;

        if (!name || name == "")
            return this.setState({ empty_name: true });

        this.props.createQueue(name, desc);
    },

    render: function() {
        return <form onSubmit={this.handleSubmit}>
            <label><input ref="name" placeholder="Name"/></label>
            <label><input ref="desc" placeholder="Description"/></label><br />
            <button type="Create">login</button>
            {this.state.pass_not_equal && (
                <p>Пароли не совпадают</p>
            )}
        </form>;
    }
});

function mapStateToProps(state, ownProps) {
    // console.log("map state");
    // console.log(state);
    return {}
}

export const CreateCon = connect(
    mapStateToProps,
    actionCreators
)(Create);