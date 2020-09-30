<html lang="de">

<head>
    <link rel="stylesheet" type="text/css" data-app-integration="static" href="css/material-design-iconic-font.min.css">
    <link rel="stylesheet" type="text/css" data-app-integration="static" href="css/style.css">
    <title>Event Registration</title>
</head>

<?php
$label_default_h1 = "Registration";
$label_your_name = "Your name";
$label_date = "Date";
$label_email = "E-Mail";
$label_phone = "Phone";
$label_register = "Register";

switch ($_GET["lang"]) {
    case "de":
        $label_default_h1 = "Anmeldung";
        $label_your_name = "Name, Vorname";
        $label_date = "Datum";
        $label_email = "E-Mail";
        $label_phone = "Telefon";
        $label_register = "Anmelden";
        break;
}
?>

<body>
    <!-- manifestRefQuery: "html[data-app-integration-manifest]"-->
    <div>
        <h1>Event Registration Form</h1>
        <p>This is a registration form, that will be integrated into AEM</p>
        <hr/>
        <br/>
        <section class="sign-in" data-app-integration="html-snippet">
            <div class="container">
                <div class="signin-content">
                    <div class="signin-image">
                        <figure><img src="images/signin-image.webp" alt="sing up image"></figure>
                    </div>
                    <div class="signin-form">
                        <h2 class="form-title"><?php echo htmlspecialchars($_GET["event"] ?: $label_default_h1); ?></h2>
                        <form method="POST" class="register-form" id="login-form">
                            <div class="form-group">
                                <label for="your_name"><i class="zmdi zmdi-account material-icons-name"></i></label>
                                <input type="text" name="your_name" id="your_name" placeholder="<?php echo $label_your_name ?>">
                            </div>
                            <div class="form-group">
                                <label for="your_name"><i class="zmdi zmdi-calendar material-icons-name"></i></label>
                                <input type="text" name="your_name" id="your_name" placeholder="<?php echo $label_date ?>">
                            </div>
                            <div class="form-group">
                                <label for="your_name"><i class="zmdi zmdi-email material-icons-name"></i></label>
                                <input type="text" name="your_name" id="your_name" placeholder="<?php echo $label_email ?>">
                            </div>
                            <div class="form-group">
                                <label for="your_name"><i class="zmdi zmdi-phone material-icons-name"></i></label>
                                <input type="text" name="your_name" id="your_name" placeholder="<?php echo $label_phone ?>">
                            </div>
                            <div class="form-group form-button">
                                <input type="submit" name="signin" id="signin" class="form-submit" value="<?php echo $label_register ?>">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    </div>
    <script type="text/javascript" data-app-integration="static" src="js/registration.js"></script>
</body>

</html>