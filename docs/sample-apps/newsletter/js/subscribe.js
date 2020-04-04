console.log("subscribe");


window.onload = function () {
    const form = document.querySelector("form");
    form.onsubmit = submitHandler.bind(form);
    console.log("onsubmit registered");
}


function submitHandler(event) {
    event.preventDefault();


    // var xmlhttp = new XMLHttpRequest();   // new HttpRequest instance 
    // var theUrl = "/json-handler";
    // xmlhttp.open("POST", theUrl);
    // xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    // xmlhttp.send(JSON.stringify({ "email": "hello@user.com", "response": { "name": "Tester" } }));

    const form = document.querySelector("form");
    
    let email = form.querySelector("input[name='email']").value;
    
    const xhr = new XMLHttpRequest();
    
    xhr.onload = function () {
        if (xhr.status === 200) {
            alert('Success: ' + xhr.responseText);
        }
        else if (xhr.status !== 200) {
            alert('Request failed.  Returned status of ' + xhr.status + " - " + xhr.responseText);
        }
    };

    xhr.open('POST', form.getAttribute("action"));
    //xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.send(new FormData(form));

}