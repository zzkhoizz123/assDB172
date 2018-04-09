var express = require('express');
var bodyParser = require('body-parser');

var app = express();
app.use(bodyParser());

app.set('view engine', 'ejs');

app.use(express.static(__dirname + '/views'));


app.get('/', function (req, res) {
    res.render('Home/Home'); // it will automatically read in file views : index.ejs
});


app.post('/', function (req, res) {
    // console.log(req.body['userEmail']);
    // console.log(req.body['userPassword']);
    console.log(req.body);
 
    res.render('Home');

});

app.all('/*', (req, res) => {
    res.send('Not found');
});

app.listen(3000); // this is port 