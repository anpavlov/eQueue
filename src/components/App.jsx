import React from 'react';
import {AppBarCon} /*{AppBarCon}*/ from './AppBar';

export default React.createClass({
    render: function() {
        return <div>
            <AppBarCon screen={this.props.location.pathname}/>
            {this.props.children}
        </div>
    }
});