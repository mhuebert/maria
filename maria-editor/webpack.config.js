
// NPM deps:  css-loader postcss postcss-loader sass-loader style-loader stylus webpack

const path = require("path");
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const extractSass = new ExtractTextPlugin({
    filename: "maria.css"
});

module.exports = {
    entry: "./src/styles/maria.scss",
    output: {
        path: path.join(__dirname, "./resources/public/"),
        filename: "maria.css"
    },
    module: {
        rules: [{
            test: /\.scss$/,
            use: extractSass.extract(
                {
                    use: [{
                        loader: "css-loader"
                    },
                        {
                            loader: "postcss-loader"
                        }, {
                            loader: "sass-loader",
                            options: {
                                includePaths: ["./node_modules"]
                            }
                        }]
                })
        }]
    },
    plugins: [
        extractSass
    ],
};