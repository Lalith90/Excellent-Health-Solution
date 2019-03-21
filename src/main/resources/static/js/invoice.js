


function ajex(method, url) {
    let xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
           return this.responseText;
        }
    };
    xhttp.open(method,url, true);
    xhttp.send();
}