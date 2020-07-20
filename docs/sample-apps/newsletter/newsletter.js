// async..await is not allowed in global scope, must use a wrapper
async function main() {
    "use strict";

    const express = require('express');
    const exphbs = require('express-handlebars');
    const path = require("path");
    const i18n = require("i18n-express");
    const nodemailer = require("nodemailer");

    const app = express();

    app.engine('handlebars', exphbs());
    app.set('view engine', 'handlebars');

    app.use(express.urlencoded({ extended: true }));
    app.use(i18n({
        translationsPath: path.join(__dirname, 'i18n'),
        siteLangs: ["en", "de"],
        textsVarName: 'translation'
    }));

    // internal data to store subscribers
    const subscriberList = [];

    const appConfig = {
        "contextRoot": "/ext/newsletter"
    };


    // define routing
    const router = express.Router();
    router.use('/js', express.static(path.join(__dirname, 'js')));
    router.use('/css', express.static(path.join(__dirname, 'css')));

    function getCommonConfig(request) {
        return {
            country: "GB",
            lang: request.query.clang || "en"
        };
    }


    router.get("/", (request, response) => {
        response.render("home", {
            "config": {
                "app": appConfig,
                "common": getCommonConfig(request)
            }
        });
    });

    router.get("/subscribe", (request, response, next) => {
        response.render("subscribe", {
            "config": {
                "app": appConfig,
                "common": getCommonConfig(request)
            },
            "newsletter": {
                "id": request.query.newsletterId,
                "name": request.query.newsletterName
            }
        });
    });


    router.post("/api/subscribe", (request, response) => {
        const email = request.body.email;
        if (email) {
            if (subscriberList.indexOf(email) < 0) {
                subscriberList.push(email);
                response.send(`Successful subscription of ${email} to our newsletter.`);
            } else {
                response.status(500).send(`E-mail address ${email} was already registered!`);
            }
        } else {
            response.status(400).send("No email address provided to subscribe!");
        }
    })


    async function sendSubscriptionConfirmationMail() {
        // Generate test SMTP service account from ethereal.email
        // Only needed if you don't have a real mail account for testing
        let testAccount = await nodemailer.createTestAccount();


        // create reusable transporter object using the default SMTP transport
        let transporter = nodemailer.createTransport({
            host: "smtp.ethereal.email",
            port: 587,
            secure: false, // true for 465, false for other ports
            auth: {
                user: testAccount.user, // generated ethereal user
                pass: testAccount.pass // generated ethereal password
            }
        });

        // send mail with defined transport object
        let info = await transporter.sendMail({
            from: '"Fred Foo 👻" <foo@example.com>', // sender address
            to: "bar@example.com, baz@example.com", // list of receivers
            subject: "Hello ✔", // Subject line
            text: "Hello world?", // plain text body
            html: "<b>Hello world?</b>" // html body
        });

        console.log("Message sent: %s", info.messageId);
        // Message sent: <b658f8ca-6296-ccf4-8306-87d57a0b4321@example.com>

        // Preview only available when sending through an Ethereal account
        console.log("Preview URL: %s", nodemailer.getTestMessageUrl(info));
        // Preview URL: https://ethereal.email/message/WaQKMgKddxQDoou...

    }


    app.use(appConfig.contextRoot, router);
    app.listen(5000);

    console.log("Listen on port 5000");
    console.log("http://localhost:5000" + appConfig.contextRoot);


}


main().catch(console.error);