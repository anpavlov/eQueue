import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';

export const Signup = React.createClass({
    getInitialState: function() {
        return {
            email_error: false,
            pass_not_equal: false
        }
    },

    handleSubmit: function(event) {
        event.preventDefault();

        this.setState({ pass_not_equal: false, email_error: false});

        const email = this.refs.email.value;
        const pass1 = this.refs.pass1.value;
        const pass2 = this.refs.pass2.value;

        if (pass1 !== pass2)
            return this.setState({ pass_not_equal: true });

        this.props.signup(email, pass1, (error_code) => {
            if (error_code == 1)
                return this.setState({ email_error: true });
        })
    },

    render: function() {
        return <form onSubmit={this.handleSubmit}>
            <label><input ref="email" placeholder="Email"/></label>
            <label><input ref="pass1" placeholder="Password" type="password"/></label><br />
            <label><input ref="pass2" placeholder="Repeat password" type="password"/></label><br />
            <button type="submit">Signup</button>
            {this.state.pass_not_equal && (
                <p>Пароли не совпадают</p>
            )}
            {this.state.email_error && (
                <p>Email уже занят</p>
            )}
        </form>;
    }
});

function mapStateToProps(state, ownProps) {
    // console.log("map state");
    // console.log(state);
    return {
        location: ownProps.location,
        is_loading: state.getIn(['reducer', 'loading_queue'])
    }
}

export const SignupCon = connect(
    mapStateToProps,
    actionCreators
)(Signup);
