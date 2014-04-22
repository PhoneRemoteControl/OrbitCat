$(document).ready(function() {
    var jsonUrl = "http://" + location.hostname + ":" + location.port + "/sms";
    $.ajax({
        url: jsonUrl
    })
    .done(function( data ) {
        var convList = JSON.parse(data);
        console.log(convList);
            
        updateSidebar(convList);
    });
});

function updateSidebar(convList) {
    $("#sidebar").html("");
    for (var i = 0; i < convList.length; i++) {
        var convObject = convList[i];
        var convView = createConvView(convObject);
        $("#sidebar").append(convView);
    }
    $("#sidebar > .list-group-item:first").addClass('active');
    
    $('.list-group-item').on('click',function(e){
        var previous = $(this).closest(".list-group").children(".active");
        previous.removeClass('active'); // previous list-item
        
        $(e.target).closest(".list-group-item").addClass('active'); // activated list-item
    });
}

function createConvView(conv) {
    var newDiv = '<a href="#" class="list-group-item" >';
    var title = '<h4 class="list-group-item-heading">';
    if (conv.contact.displayName != null) {
        title += conv.contact.displayName + ' (' + conv.contact.phoneNumber + ')';
    } else {
        title += conv.contact.phoneNumber;
    }
    title += '</h4>';
    var subtitle = '<p class="list-group-item-text">' + conv.msgCount + ' messages</p>';
    newDiv += title + subtitle + '</a>';
    return newDiv;
}