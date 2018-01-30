const fs = require('fs');
const pify = require('pify');

const readFile = pify(fs.readFile);

exports.handler = function(event, context, callback) {
    readFile(`${__dirname}/not-found.html`, 'utf-8')
        .then((data) => {
            callback(null, data.toString());
        })
        .catch(err => {
            console.log(err);
        });
};
