var express = require('express');
var bodyParser = require('body-parser');

var app = express();
app.use(bodyParser());

app.set('view engine', 'ejs');

app.use(express.static(__dirname + '/views'));

//app.get('/', function (req, res) {
//    res.sendFile(__dirname + '/index.html');
//});

app.get('/', function (req, res) {
    res.render('Home'); // it will automatically read in file views : index.ejs
});

app.get('/contact', function (req, res) {
    res.render('contact'); // it will automatically read in file views : contact.ejs
});

app.post('/', function (req, res) {
    // console.log(req.body['userEmail']);
    // console.log(req.body['userPassword']);
    console.log(req.body);
 
    res.render('Home');

});


app.get('/profile/:name', function (req, res) {

    var data = { age: 29, job: 'student', hobbies: ['eating', 'listening', 'fishing'] };
    res.render('profile', { person: req.params.name, data: data }); // this is the name of the file in view
});                                                     // then u can tranfer the object as data to file .ejs

app.all('/*', (req, res) => {
    res.send('Not found');
});

app.listen(3000); // this is port 