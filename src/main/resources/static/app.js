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

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/citadel', (greeting) => {
        console.log(666, greeting)
        showGreeting(JSON.parse(greeting.body).effect);
    });
    stompClient.subscribe('/bgs/citadel', (greeting) => {
        console.log(777, greeting)
        showGreeting(JSON.parse(greeting.body).action);
    });
    stompClient.subscribe('/topic/citadel2', (greeting) => {
        console.log(88, greeting)
        showGreeting(JSON.parse(greeting.body).action);
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

function sendName() {
    stompClient.publish({
        destination: "/bgs/citadel",
        body: JSON.stringify({'name': $("#name").val()})
    });
}
function sendName2(){
    stompClient.publish({
        destination: "/topic/citadel",
        body: JSON.stringify({'effect': $("#name").val()})
    });
}

function sendName3(){
    stompClient.publish({
        destination: "/bgs/citadel2",
        body: JSON.stringify({'effect': $("#name").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
    $( "#send2" ).click(() => sendName2());
    $( "#send3" ).click(() => sendName3());
});

