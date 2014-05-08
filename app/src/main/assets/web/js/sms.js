$(document).ready(function() {
    var jsonUrl = "http://" + location.hostname + ":" + location.port + "/sms";
    $.ajax({
        url: jsonUrl
    })
    .done(function( data ) {
        updateSidebar(data);
    });
});

function updateSidebar(convList) {
    $("#sidebar > .spinner-container").css("display", "none");
    $("#sidebar > .content").html("");
    for (var i = 0; i < convList.length; i++) {
        var convObject = convList[i];
        var convView = createConvView(convObject);
        $("#sidebar > .content").append(convView);
    }

    selectConversation($("#sidebar > .content > .list-group-item:first"));
    
    $('.list-group-item').on('click', function(e) {
        selectConversation($(e.target));
    });
}

function selectConversation(newConv) {
        var previous = newConv.closest(".list-group").children(".active");
        $("img", previous).width("48");
        $(".selected-details", previous).css("display", "none");
        $(".unselected-details", selected).css("display", "block");
        previous.removeClass('active'); // previous list-item
        
        var selected = $(newConv).closest(".list-group-item");
        selected.addClass('active'); // activated list-item
        $("img", selected).width("64");
        $(".selected-details", selected).css("display", "block");
        $(".unselected-details", selected).css("display", "none");

        $("#conversation > .spinner-container").css("display", "block");
        var threadId = $(".threadId", selected).html();
        var jsonUrl = "http://" + location.hostname + ":" + location.port + "/sms/" + threadId;
        $.ajax({
            url: jsonUrl
        })
        .done(function( data ) {
            updateMessages(data);
        });
}

function updateMessages(messagesList) {
    $("#conversation > .spinner-container").css("display", "none");
    $("#conversation > .content").html("");
    for (var i = 0; i < messagesList.length; i++) {
        var messageObject = messagesList[i];
        var messageView = createMessageView(messageObject);
        $("#conversation > .content").append(messageView);
    }
}

function createMessageView(message) {
    var html = "<ul><li>" + message.date + " " + message.type + "</li><li>" + message.body + "</li><li>" + message.seen + "</li></ul>";
    return html;
}

function createConvView(conv) {
    var newDiv = '<a href="#" class="list-group-item" >';
    var icon = "";
    if (conv.imagePath != null) {
        icon = '<div class="contactIcon"><img src="' + conv.imagePath + '" width="48"/></div>'
    } else {
        icon = '<div class="contactIcon"><img src="../img/unknown-contact.png" width="48"/></div>'
    }
    var id = '<div class=threadId>' + conv.threadId + '</div>';
    var title = '<div class="contactText"><h4 class="list-group-item-heading">';
    title += conv.contact.displayName;
    title += '</h4>';
    var subtitle = '';
    subtitle += '<p class="list-group-item-text unselected-details">' + conv.snippet + '</p>';
    if (conv.contact.displayName != conv.contact.phoneNumber) {
        subtitle += '<p class="list-group-item-text selected-details">' + conv.contact.phoneNumber + '</p>';
    }
    subtitle += '<p class="list-group-item-text selected-details">' + conv.msgCount + ' messages</p></div>';
    newDiv += icon + id + title + subtitle + '</a>';
    return newDiv;
}