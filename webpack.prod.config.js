const webpack = require('webpack');

module.exports = {
    entry: [
        // 'webpack-dev-server/client?http://localhost:8080',
        // 'webpack/hot/dev-server',
        './src/index.jsx'
    ],
    module: {
        loaders: [
            {
                test: /(\.js|\.jsx)$/,
                exclude: /node_modules/,
                loader: 'babel'
            }
        ]
    },
    resolve: {
        extensions: ['', '.js', '.jsx']
    },
    output: {
        path: __dirname + '/prod',
        publicPath: '/',
        filename: 'bundle.js'
    },
    //devtool: "eval-cheap-module-source-map",
    plugins: [
        // new webpack.HotModuleReplacementPlugin(),
        new webpack.DefinePlugin({
            "process.env": {
                NODE_ENV: JSON.stringify("production")
            }
        })
    ]
};
