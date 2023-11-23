const hostLANIp = '192.168.1.6'
const localhost = "localhost"
let host = localhost

const stompClient = new StompJs.Client({
    brokerURL: `ws://${host}:8080/bgs-websocket`,
    debug: function (str) {
        console.log(str);
    },
    reconnectDelay: -1,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});

let _csrf;
let _jwt;

// Fallback code
if (typeof WebSocket !== 'function') {
    console.log("use SockJs")
    // For SockJS you need to set a factory that creates a new SockJS instance
    // to be used for each (re)connect
    stompClient.webSocketFactory = function () {
        // Note that the URL is different from the WebSocket URL
        return new SockJS(`http://${host}:8080/bgs-sockjs`);
    };
}

function extractMessage(data)  {
    console.log(data)
    return `[${data.headers.destination}] ${JSON.parse(data.body).fromUser}: ${JSON.parse(data.body).content}`
}

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    console.log("stompClient", stompClient)
    // stompClient.subscribe('/user/common/question', (data) => {
    //     showGreeting(extractMessage(data));
    // });
    // stompClient.subscribe('/common/broadcast', (data) => {
    //     showGreeting(extractMessage(data));
    // });
    // stompClient.subscribe('/user/common/chat', (data) => {
    //     showGreeting(extractMessage(data));
    // });
};

function subscribe() {
    stompClient.subscribe('/user/common/question', (data) => {
        showGreeting(extractMessage(data));
    });
    stompClient.subscribe('/common/broadcast', (data) => {
        showGreeting(extractMessage(data));
    });
    stompClient.subscribe('/user/common/chat', (data) => {
        showGreeting(extractMessage(data));
    });
}

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    if(!_csrf) {
        console.log("no _csrf")
    } else {
        stompClient.connectHeaders[_csrf.headerName]= _csrf.token
    }
    if(!_jwt) {
        console.log("no _jwt")
    } else {
        stompClient.connectHeaders['JWT']= _jwt
    }
    console.log(_csrf, _jwt, stompClient)
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function broadcast() {
    stompClient.publish({
        destination: "/bgs/common/broadcast",
        body: JSON.stringify({'content': $("#input").val()})
    });
}
function broadcast2() {
    stompClient.publish({
        destination: "/bgs/common/broadcast2",
        body: JSON.stringify({'content': $("#input").val()})
    });
}
function question() {
    stompClient.publish({
        destination: "/bgs/common/question",
        body: JSON.stringify({'content': $("#input").val()})
    });
}

function chat(){
    stompClient.publish({
        destination: "/bgs/common/chat",
        body: JSON.stringify({'toUser': $("#input2").val(),'content': $("#input").val()})
    });
}
function tryConnect() {
    console.log(2)
    const ws = new WebSocket(
        `ws://${host}:8080/bgs-websocket`
    );
    ws.onopen = () => {
        console.log('open')
        // connection opened
        // ws.send('something'); // send a message
    };

    ws.onmessage = e => {
        // a message was received
        console.log(e.data);
    };

    ws.onerror = e => {
        // an error occurred
        console.log('error', e);
    };

    ws.onclose = e => {
        // connection closed
        console.log(e.code, e.reason);
    };
}



function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}
const getJWTWithAccount = (username) => {
    // console.log("${_csrf.parameterName}")
    const Http = new XMLHttpRequest();
    const url=`http://${host}:8080/learn/hello`;
    Http.open("GET", url);
    Http.setRequestHeader("Authorization", `Basic ${btoa(
        `${username}:password`
    )}`)
    Http.onreadystatechange = (e) => {
        if(e.currentTarget.readyState === 4) {
            console.log(Http.responseText)
            _jwt = Http.responseText.replace("Bearer ","")
        }
    }
    Http.send()
}
$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#broadcast" ).click(() => broadcast());
    $( "#broadcast2" ).click(() => broadcast2());
    $( "#question" ).click(() => question());
    // $( "#chat" ).click(() => chat());
    $( "#chat" ).click(() => tryConnect());
    $( "#updateIp" ).click(() => {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        console.log($("meta[name='_csrf']"))
        console.log(header, token)
        console.log($("#ip").val())

        const Http = new XMLHttpRequest();
        const url=`http://${host}:8080/VPN/update`;
        Http.open("POST", url);
        if(_csrf) Http.setRequestHeader(_csrf.headerName, _csrf.token)

        // Http.setRequestHeader("Content-Type", "application/json")
        // Http.send(JSON.stringify({ip:2})) // 注意这个

        Http.setRequestHeader("Content-Type", "application/x-www-form-urlencoded")
        Http.send(`ip=${$("#ip").val()}`) // 注意这个

        // Http.setRequestHeader("Content-Type", "application/form-data")
        // Http.setRequestHeader(header, token)
        // Http.send(JSON.stringify({
        //     ip: $("#ip").val()
        // }));
        Http.onreadystatechange = (e) => {
            console.log(Http.responseText)
        }
    });
    $( "#getIp" ).click(() => {
        const Http = new XMLHttpRequest();
        const url=`http://${host}:8080/VPN/subscription`;
        Http.open("GET", url);
        Http.send();
        Http.onreadystatechange = (e) => {
            if(e.currentTarget.readyState === 4) {
                console.log(JSON.parse(atob(atob(Http.responseText).substring(8))).add)
                $("#vpnIp").html(JSON.parse(atob(atob(Http.responseText).substring(8))).add)
            }
        }
    })
    $("#getCsrfToken").click(() => {
        // console.log("${_csrf.parameterName}")
        const Http = new XMLHttpRequest();
        // const url=`http://${host}:8080/csrf`;
        const url=`http://${hostLANIp}:8080/csrf`;
        Http.open("GET", url);
        Http.onreadystatechange = (e) => {
            if(e.currentTarget.readyState === 4) {
                console.log(JSON.parse(Http.responseText))
                _csrf = JSON.parse(Http.responseText)
            }
        }
        Http.send()
    })
    $("#getJWT").click(() => {
        // console.log("${_csrf.parameterName}")
        const Http = new XMLHttpRequest();
        const url=`http://${host}:8080/learn/hello`;
        Http.open("GET", url);
        Http.onreadystatechange = (e) => {
            if(e.currentTarget.readyState === 4) {
                console.log(Http.responseText)
                _jwt = Http.responseText.replace("Bearer ","")
            }
        }
        Http.send()
    })

    $("#getJWTWithWyc").click(() => getJWTWithAccount('wyc'))
    $("#getJWTWithUser").click(() => getJWTWithAccount('user'))

    $("#subscribe").click(()=>subscribe())
    $("#testRole").click(()=>{
        const Http = new XMLHttpRequest();
        const url=`http://${host}:8080/learn/testRole`;
        Http.open("GET", url);

        Http.setRequestHeader("Authorization", `Basic ${btoa(
            'wyc:password'
        )}`)
        // Http.setRequestHeader("Authorization", `Bearer ${_jwt}`)

        Http.onreadystatechange = (e) => {
            if (e.currentTarget.readyState === 4) {
                console.log(e)
            }
        }
        Http.send()
    })
});

