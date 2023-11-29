const hostLANIp = '192.168.1.8'
const hostDomain = '3229nr8294.yicp.fun'
const localhost = "localhost"
let http = "https"
let host = hostDomain
let user = null
const stompClient = new StompJs.Client({
    brokerURL: `ws://${host}/bgs-websocket`,
    debug: function (str) {
        console.log(str);
    },
    reconnectDelay: -1,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});


// Fallback code
if (typeof WebSocket !== 'function') {
    console.log("use SockJs")
    // For SockJS you need to set a factory that creates a new SockJS instance
    // to be used for each (re)connect
    stompClient.webSocketFactory = function () {
        // Note that the URL is different from the WebSocket URL
        return new SockJS(`http://${host}/bgs-sockjs`);
    };
}

let _csrf;
let _jwt;

function extractMessage(data) {
    console.log(data)
    return `[${data.headers.destination}] ${JSON.parse(data.body).fromUser}: ${JSON.parse(data.body).content}`
}

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    console.log("stompClient", stompClient)
    subscribe()
};

function subscribe() {
    stompClient.subscribe('/public/lobby', (data) => {
        const msg = extractMessage(data);
        showGreeting(msg, msg.fromUser == user);
    });
    stompClient.subscribe('/user/private/*', (data) => {
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
    $("#chat").prop("disabled", !connected);
    $("#toUser").prop("disabled", !connected);
    $("#talkToLobby").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    if (!_csrf) {
        console.log("no _csrf")
    } else {
        stompClient.connectHeaders[_csrf.headerName] = _csrf.token
    }
    if (!_jwt) {
        console.log("no _jwt")
    } else {
        stompClient.connectHeaders['JWT'] = _jwt
    }
    console.log(_csrf, _jwt, stompClient)

    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

// function broadcast() {
//     stompClient.publish({
//         destination: "/bgs/common/broadcast",
//         body: JSON.stringify({'content': $("#input").val()})
//     });
// }
// function broadcast2() {
//     stompClient.publish({
//         destination: "/bgs/common/broadcast2",
//         body: JSON.stringify({'content': $("#input").val()})
//     });
// }
function question() {
    stompClient.publish({
        destination: "/bgs/common/question",
        body: JSON.stringify({'content': $("#input").val()})
    });
}

function talkToLobby() {
    stompClient.publish({
        destination: `/bgs/chat/lobby`,
        body: JSON.stringify({content: $("#input").val()})
    });
}

function chat() {
    console.log($("#toUser"))
    stompClient.publish({
        destination: `/bgs/chat/user/${$("#toUser").val()}`,
        body: JSON.stringify({'toUser': $("#toUser").val(), 'content': $("#input").val()}),
    });
    showGreeting(`[Send to ${$("#toUser").val()}]: ${$("#input").val()}`, true)
}

function tryConnect() {
    console.log(2)
    const ws = new WebSocket(
        `ws://${host}/bgs-websocket`
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


function showGreeting(message, myself = false) {
    if (myself) {
        $("#greetings").append("<tr><td style='background-color: #95ec69; text-align: right'>" + message + "</td></tr>");
    } else {
        $("#greetings").append("<tr><td style='background-color: #DDDDDD'>" + message + "</td></tr>");
    }
}

const getJWTWithAccount = (username) => {
    user = username
    // console.log("${_csrf.parameterName}")
    const Http = new XMLHttpRequest();
    const url = `http://${host}/learn/hello`;
    Http.open("GET", url);
    Http.setRequestHeader("Authorization", `Basic ${btoa(
        `${username}:password`
    )}`)
    Http.onreadystatechange = (e) => {
        if (e.currentTarget.readyState === 4) {
            console.log(Http.responseText)
            _jwt = Http.responseText.replace("Bearer ", "")
        }
    }
    Http.send()
}
$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#question").click(() => question());
    $("#chat").click(() => chat());
    $("#talkToLobby").click(() => talkToLobby())
    $("#updateIp").click(() => {
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        console.log($("meta[name='_csrf']"))
        console.log(header, token)
        console.log($("#ip").val())

        const Http = new XMLHttpRequest();
        const url = `http://${host}/VPN/update`;
        Http.open("POST", url);
        if (_csrf) Http.setRequestHeader(_csrf.headerName, _csrf.token)

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
    $("#getIp").click(() => {
        const Http = new XMLHttpRequest();
        const url = `http://${host}/VPN/subscription`;
        Http.open("GET", url);
        Http.send();
        Http.onreadystatechange = (e) => {
            if (e.currentTarget.readyState === 4) {
                console.log(JSON.parse(atob(atob(Http.responseText).substring(8))).add)
                $("#vpnIp").html(JSON.parse(atob(atob(Http.responseText).substring(8))).add)
            }
        }
    })
    // $("#getCsrfToken").click(() => {
    //     // console.log("${_csrf.parameterName}")
    //     const Http = new XMLHttpRequest();
    //     // const url=`http://${host}/csrf`;
    //     const url=`http://${hostLANIp}:8080/csrf`;
    //     Http.open("GET", url);
    //     Http.onreadystatechange = (e) => {
    //         if(e.currentTarget.readyState === 4) {
    //             console.log(JSON.parse(Http.responseText))
    //             _csrf = JSON.parse(Http.responseText)
    //         }
    //     }
    //     Http.send()
    // })
    // $("#getJWT").click(() => {
    //     // console.log("${_csrf.parameterName}")
    //     const Http = new XMLHttpRequest();
    //     const url=`http://${host}/learn/hello`;
    //     Http.open("GET", url);
    //     Http.onreadystatechange = (e) => {
    //         if(e.currentTarget.readyState === 4) {
    //             console.log(Http.responseText)
    //             _jwt = Http.responseText.replace("Bearer ","")
    //         }
    //     }
    //     Http.send()
    // })

    $("#getJWTWithWyc").click(() => getJWTWithAccount('wyc'))
    $("#getJWTWithLzz").click(() => getJWTWithAccount('lzz'))

    $("#subscribe").click(() => subscribe())
    $("#testRole").click(() => {
        const Http = new XMLHttpRequest();
        const url = `http://${host}/learn/testRole`;
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

