const webpack = require('webpack');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const autoprefixer = require('autoprefixer');
const path = require('path');

module.exports = {
    entry: [
        'webpack-dev-server/client?http://localhost:8080',
        'webpack/hot/dev-server',
        './src/index.jsx'
    ],
    module: {
        loaders: [
            {
                test: /(\.js|\.jsx)$/,
                exclude: /node_modules/,
                loader: 'react-hot!babel'
            },
            {
                test: /(\.scss|\.css)$/,
                loader: ExtractTextPlugin.extract('style', 'css?sourceMap&modules&importLoaders=1&localIdentName=[name]__[local]___[hash:base64:5]!postcss!sass')
            }
        ]
    },
    postcss: [autoprefixer],
    sassLoader: {
        data: '@import "theme/_config.scss";',
        includePaths: [path.resolve(__dirname, './src')]
    },
    resolve: {
        extensions: ['', '.scss', '.css', '.js', '.jsx'],
    },
    output: {
        path: __dirname + '/dist',
        publicPath: '/',
        filename: 'bundle.js'
    },
    devtool: "source-map",
    devServer: {
        contentBase: './dist',
        hot: true
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new ExtractTextPlugin('bundle.css', { allChunks: true }),
    ]
};
