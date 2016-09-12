import React from 'react';
import {connect} from 'react-redux';
import actionCreators from '../logic/all_actions';
import {Link} from 'react-router';
import paths from '../utils/paths';

export const Login = React.createClass({
    getInitialState: function() {
        return {
            error: false
        }
    },

    handleSubmit: function(event) {
        event.preventDefault();

        this.setState({ error: false});

        const email = this.refs.email.value;
        const pass = this.refs.pass.value;

        this.props.login(email, pass, (loggedIn) => {
            if (!loggedIn)
                return this.setState({ error: true });

            const { location } = this.props;

            if (location.state && location.state.nextPathname) {
                this.props.replaceUrl(location.state.nextPathname);
            } else {
                this.props.replaceUrl('/admin/');
            }
        })
    },

    render: function() {
        return (
            <div>
                <form onSubmit={this.handleSubmit}>
                    <label><input ref="email" placeholder="Email"/></label>
                    <label><input ref="pass" placeholder="password" type="password"/></label><br />
                    <button type="submit">login</button>
                    {this.state.error && (
                        <p>Неправильные email или пароль</p>
                    )}
                </form>
                <Link to={paths.signup}>Signup</Link>
            </div>
        );
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

export const LoginCon = connect(
    mapStateToProps,
    actionCreators
)(Login);