import React from 'react';
import ReactDOM from 'react-dom';
import {
    renderIntoDocument,
    scryRenderedDOMComponentsWithTag
} from 'react-addons-test-utils';
import First from '../../src/components/First';
import {expect} from 'chai';

describe('Voting', () => {

    it('render comp', () => {
        const component = renderIntoDocument(
            <First />
        );
        const h1s = scryRenderedDOMComponentsWithTag(component, 'h1');

        expect(h1s[0].textContent).to.equal('Hello World First!');
    });

});
