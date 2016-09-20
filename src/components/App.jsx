import React from 'react';
import {MyBarCon} /*{MyBarCon}*/ from './MyBar';

export default React.createClass({
    render: function() {
        return <div>
            <MyBarCon screen={this.props.location.pathname}/>
            {this.props.children}
        </div>
    }
});