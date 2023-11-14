const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/bgs-websocket',
    debug: function (str) {
        console.log(str);
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
});

// Fallback code
if (typeof WebSocket !== 'function') {
    // For SockJS you need to set a factory that creates a new SockJS instance
    // to be used for each (re)connect
    stompClient.webSocketFactory = function () {
        // Note that the URL is different from the WebSocket URL
        return new SockJS('http://localhost:8080/bgs-sockjs');
    };
}

function extractMessage(data)  {
    console.log(data)
    return `[${data.headers.destination}] ${JSON.parse(data.body).fromUser}: ${JSON.parse(data.body).content}`
}

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);

    stompClient.subscribe('/user/common/question', (data) => {
        showGreeting(extractMessage(data));
    });
    stompClient.subscribe('/common/broadcast', (data) => {
        showGreeting(extractMessage(data));
    });
    stompClient.subscribe('/user/common/chat', (data) => {
        showGreeting(extractMessage(data));
    });
};

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



function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#broadcast" ).click(() => broadcast());
    $( "#broadcast2" ).click(() => broadcast2());
    $( "#question" ).click(() => question());
    $( "#chat" ).click(() => chat());
});

