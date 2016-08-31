import React from 'react';
// import {connect} from 'react-redux';
// import * as actionCreators from '../../action_creators';
// import {Link} from 'react-router';
import paths from '../../paths';
import {openPage} from '../../utils';

export default /*const EmptyList =*/ React.createClass({
    contextTypes: {
        router: React.PropTypes.object
    },

    render: function() {
        // console.log("hi from render");
        return <div>
                    No queues for u!<br/>
                    <input type="button"
                           onClick={() => openPage(this.context.router, paths.by_id)}
                           value="find by id!" />
                </div>
    }
});