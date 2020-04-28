const express = require("express");
const fs = require("fs");
const app = express();
let array = [];
let proxycheck = {}
app.use(express.static("public"));
app.use(express.urlencoded());
app.get("/", (request, response) => {
  response.sendFile(__dirname + "/views/index.html");
});
app.get("/proxycheck", (request, response) => {
  console.log(request.headers)
  console.log(request.url)
  if(request.query.key != "yyyyyyyyyyy"){
    return response.status(401).send("Wrong token")
  }
  if(proxycheck[request.query.ip]){
    if(proxycheck[request.query.ip] === "yes"){
      let names = JSON.parse(fs.readFileSync(`/home/1b1t/1b1twhitelist/names.json`, "utf-8"));
      if(names.includes(request.query.player.toLowerCase())){
        return response.send({
          proxy: "no",
          cached: "yes"
        })
      }
    }
    return response.send({
      proxy: proxycheck[request.query.ip],
      cached: "yes"
    })
  }
  let names = JSON.parse(fs.readFileSync(`/home/1b1t/1b1twhitelist/names.json`, "utf-8"));
  if(names.includes(request.query.player.toLowerCase())){
    return response.send({
      proxy: "no",
      cached: "yes"
    })
  }
  var unirest = require('unirest');
  var req = unirest('GET', `https://proxycheck.io/v2/${request.query.ip}?vpn=1&asn=1&key=xxxxx-xxxxx-xxxxx-xxxxx`)
    .headers({
      'Cookie': '__cfduid=xxxxxxxxxxxxxxxxxxxxxxxxxxxxx; __cflb=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
    })
    .end(function (res) { 
      if (res.error)
      console.log(res.raw_body);
      response.send({
        proxy: `${JSON.parse(res.raw_body)[request.query.ip].proxy}`,
        cached: "no",
        cachedinfo: proxycheck
      })
      proxycheck[request.query.ip] = JSON.parse(res.raw_body)[request.query.ip].proxy
    });
})
var multer = require("multer");
var upload = multer();
app.post("/addname", upload.none(), (request, response) => {
  console.log(request.body);
  var unirest = require("unirest");
  var req = unirest(
    "POST",
    `https://www.google.com/recaptcha/api/siteverify?secret=realsecret&response=${
      request.body["g-recaptcha-response"]
    }`
  ).end(function(res) {
    if (res.error) console.log(res.raw_body);

    if (JSON.parse(res.raw_body).success === true) {
      var unirest = require('unirest');
      var req = unirest('GET', `https://api.mojang.com/users/profiles/minecraft/${request.body["username"].replace("(", "").replace(")", "")}`)
        .end(function (res) { 
          if (res.error){
            console.log(res.error)
            response.status(400).send("Invalid username")
            return
          } else{
            console.log("no error")
            if(!res.raw_body.includes("id")){
              response.status(400).send("Invalid username")
              return
            } else {
              let names = JSON.parse(fs.readFileSync(`/home/1b1t/1b1twhitelist/names.json`, "utf-8"));
              array = names;
              array.push(request.body["username"].replace("(", "").replace(")", ""));
              fs.writeFileSync(`/home/1b1t/1b1twhitelist/names.json`, JSON.stringify(array, null, 4), err => {
                if (err) throw err;
              });
              response.send(
                "Name added! Try relogging. If it still doesn't work contact the owner."
              );
            }
          }
        });
    } else {
      response.status(403).send("Google Recaptcha failed!");
    }
  });
});
app.get("/names", (request, response) => {
  let names = JSON.parse(fs.readFileSync(`/home/1b1t/1b1twhitelist/names.json`, "utf-8"));
  response.send(names); // don't think about going to /names the names are public in discord anyway so doesn't matter
});
const listener = app.listen(3002, () => {
  console.log("Your app is listening on port " + 3002);
});